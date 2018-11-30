package dk.aau.cs.ds306e18.tournament.rlbot;

import dk.aau.cs.ds306e18.tournament.model.Bot;
import dk.aau.cs.ds306e18.tournament.model.match.Match;
import dk.aau.cs.ds306e18.tournament.rlbot.RLBotStalker;
import dk.aau.cs.ds306e18.tournament.utility.Alerts;
import dk.aau.cs.ds306e18.tournament.utility.ConfigFileEditor;
import rlbot.flat.GameTickPacket;
import rlbot.flat.PlayerInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MatchRunner {

    // both %s will be replaced with the directory of the rlbot.cfg
    private static final String COMMAND_FORMAT = "cmd.exe /c start cmd.exe /c \"cd %s & python \"%s\\run.py\"\"";

    private static final int BLUE_TEAM_INDEX = 0;
    private static final int ORANGE_TEAM_INDEX = 1;

    private static RLBotStalker stalker;

    /** Starts the given match in Rocket League. */
    public static boolean startMatch(RLBotSettings settings, Match match) {
        try {
            checkMatch(settings, match);
        } catch (IllegalStateException e) {
            Alerts.errorNotification("Error occurred while starting match", e.getMessage());
            return false;
        }

        try {
            // Set up rlbot config file
            ConfigFileEditor.readConfig(settings.getConfigPath());
            ConfigFileEditor.configureMatch(match);
            ConfigFileEditor.writeConfig(settings.getConfigPath());

            Thread.sleep(100);

            // We assume that the runner is in the same folder as the rlbot.cfg
            Path pathToDirectory = Paths.get(settings.getConfigPath()).getParent();
            String command = String.format(COMMAND_FORMAT, pathToDirectory, pathToDirectory);
            System.out.println("Running command: " + command);
            Runtime.getRuntime().exec(command);
            initStalker();
            System.out.println("Started RLBot framework");
            return true;

        } catch (Exception err) {
            err.printStackTrace();
        }

        return false;
    }

    /** Returns true if the given match can be started. */
    public static boolean canStartMatch(RLBotSettings settings, Match match) {
        try {
            checkMatch(settings, match);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /** Check the requirements for starting the match. Throws an IllegalStateException if anything is wrong.
     * Does nothing if everything is okay. */
    private static void checkMatch(RLBotSettings settings, Match match) {
        // These methods throws IllegalStageException if anything is wrong
        checkRLBotSettings(settings);
        checkBotConfigsInMatch(match);
    }

    /** Checks rlbot settings. Throws an IllegalStateException if anything is wrong. Does nothing if everything is okay. */
    private static void checkRLBotSettings(RLBotSettings settings) {
        // Check if rlbot.cfg set
        String configPath = settings.getConfigPath();
        if (configPath == null || configPath.isEmpty())
            throw new IllegalStateException("The RLBot config file (rlbot.cfg) is not set in RLBot Settings.");

        // Check if rlbot.cfg exists
        File cfg = new File(configPath);
        if (!cfg.exists() || !cfg.isFile() || cfg.isDirectory())
            throw new IllegalStateException("Could not find RLBot config file (\"" + configPath + "\")");

        // Check if run.py exists in same directory
        File runner = new File(cfg.getParent(), "run.py");
        if (!runner.exists()) {
            throw new IllegalStateException("Could not find run.py next to the RLBot config file (\"" + runner.getAbsolutePath() + "\")");
        }
    }

    /** Throws an IllegalStateException if anything is wrong with the bots' config files for this match.
     * Does nothing if all the bots on both teams have a valid config file in a given match. */
    private static void checkBotConfigsInMatch(Match match) {
        // Check if there are two teams
        if (match.getBlueTeam() == null) throw new IllegalStateException("There is no blue team for this match.");
        if (match.getOrangeTeam() == null) throw new IllegalStateException("There is no orange team for this match.");

        ArrayList<Bot> bots = new ArrayList<>();
        bots.addAll(match.getBlueTeam().getBots());
        bots.addAll(match.getOrangeTeam().getBots());

        for (Bot bot : bots) {
            String path = bot.getConfigPath();

            // Check if bot cfg is set
            if (path == null || path.isEmpty())
                throw new IllegalStateException("The bot '" + bot.getName() + "' has no config file.");

            // Check if bot cfg exists
            File file = new File(path);
            if (!file.exists() || !file.isFile() || file.isDirectory())
                throw new IllegalStateException("The config file of the bot named '" + bot.getName()
                        + "' is not found (path: '" + path + ")'");
        }
    }

    /** Initializes the RLBotStalker which fetches packets from the RLBot_Core_Interface.dll. */
    public static void initStalker() {
        // Create a RLBotStalker
        if (stalker == null) {
            try {
                stalker = new RLBotStalker();
            } catch (Exception e) {
                System.err.println("Could not initialize RLBotStalker : " + e.getMessage());
                stalker = null;
            }
        }

        // Start stalker if it is not running
        if (stalker != null && !stalker.isRunning()) {
            stalker.start();
        }
    }

    /** Fetch the score of blue team from Rocket League. */
    public static int fetchBlueScore() throws IOException {
        return fetchScoreOfTeam(BLUE_TEAM_INDEX);
    }

    /** Fetch the score of orange team from Rocket League. */
    public static int fetchOrangeScore() throws IOException {
        return fetchScoreOfTeam(ORANGE_TEAM_INDEX);
    }

    /** Fetch the score of a team from Rocket League. */
    private static int fetchScoreOfTeam(int teamIndex) throws IOException {

        if (stalker == null) {
            // Stalker was not started
            initStalker();
            throw new IOException("Started RLBotStalker. Please wait a few seconds.");
        } else {

            GameTickPacket lastPacket = stalker.getLastPacket();
            if (lastPacket == null) {
                throw new IOException("Could not fetch a packet from Rocket League.");
            }

            // Iterate over all players to find the score of the team
            int score = 0;
            for (int i = 0; i < lastPacket.playersLength(); i++) {
                PlayerInfo bot = lastPacket.players(i);
                if (bot.team() == teamIndex) {
                    score += bot.scoreInfo().goals();
                }
            }
            return score;
        }
    }
}
