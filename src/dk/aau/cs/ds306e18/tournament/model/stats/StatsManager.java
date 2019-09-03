package dk.aau.cs.ds306e18.tournament.model.stats;

import dk.aau.cs.ds306e18.tournament.model.Team;
import dk.aau.cs.ds306e18.tournament.model.format.Format;
import dk.aau.cs.ds306e18.tournament.model.match.Match;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for track all stats for a Team for individual Stages and globally across all Stages.
 */
public class StatsManager {

    private Team team;
    private StatsTracker globalStatsTracker;
    private Map<Format, StatsTracker> formatStats = new HashMap<>();

    /**
     * Create a StatsManager for the given team.
     */
    public StatsManager(Team team) {
        this.team = team;
        globalStatsTracker = new StatsTracker(team);
    }

    /**
     * Returns the team's stats across all Stages.
     */
    public Stats getGlobalStats() {
        return globalStatsTracker.getStats();
    }

    /**
     * Returns the team's stats for the given format.
     */
    public Stats getStats(Format format) {
        return getTracker(format).getStats();
    }

    /**
     * Start tracking stats from the given match.
     */
    public void trackMatch(Format format, Match match) {
        getTracker(format).trackMatch(match);
        recalculateGlobalStats();
    }

    /**
     * Stop tracking stats from the given match.
     */
    public void untrackMatch(Format format, Match match) {
        getTracker(format).untrackMatch(match);
        recalculateGlobalStats();
    }

    /**
     * Start tracking stats from the given matches.
     */
    public void trackMatches(Format format, Collection<? extends Match> matches) {
        getTracker(format).trackMatches(matches);
        recalculateGlobalStats();
    }

    /**
     * Stop tracking stats from the given matches.
     */
    public void untrackMatches(Format format, Collection<? extends Match> matches) {
        getTracker(format).untrackMatches(matches);
        recalculateGlobalStats();
    }

    private StatsTracker getTracker(Format format) {
        return formatStats.computeIfAbsent(format, k -> new StatsTracker(team));
    }

    /**
     * Recalculate the global stats. StatsChangeListeners for the global stats are notified.
     */
    private void recalculateGlobalStats() {
        // Since all matches comes from a unique format, we just add the stats for each format to get the global stats.
        int wins = 0;
        int loses = 0;
        int goals = 0;
        int goalsConceded = 0;
        for (Format format : formatStats.keySet()) {
            Stats stats = getStats(format);
            wins += stats.getWins();
            loses += stats.getLoses();
            goals += stats.getGoals();
            goalsConceded += stats.getGoalsConceded();
        }
        globalStatsTracker.getStats().set(wins, loses, goals, goalsConceded);
    }
}
