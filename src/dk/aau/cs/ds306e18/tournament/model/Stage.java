package dk.aau.cs.ds306e18.tournament.model;

import javafx.scene.Node;

import java.util.List;

public interface Stage {

    /** Starts the stage with the given list of teams. The teams are seeded after the order in the list. */
    void start(List<Team> seededTeams);

    String getName();
    StageStatus getStatus();

    /** Returns a list of all the matches in this stage. */
    List<Match> getAllMatches();

    /** Returns a list of all the matches that are ready to be played, but haven't played yet. */
    List<Match> getUpcomingMatches();

    /** Returns a list of all planned matches, that can't be played yet. */
    List<Match> getPendingMatches();

    /** Returns a list of all the matches that have been played. */
    List<Match> getCompletedMatches();

    /** Returns a list of the teams that performed best this stage. They are sorted after performance, with best team first. */
    List<Team> getTopTeams(int count, TieBreaker tieBreaker);

    /** Returns a Node of the stage. This node contains a reference to it self and other functionality to display the stage.*/
    Node getJavaFxNode();
}
