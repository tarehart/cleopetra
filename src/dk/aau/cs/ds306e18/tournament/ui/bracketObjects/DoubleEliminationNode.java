package dk.aau.cs.ds306e18.tournament.ui.bracketObjects;

import dk.aau.cs.ds306e18.tournament.model.format.DoubleEliminationFormat;
import dk.aau.cs.ds306e18.tournament.model.match.Match;
import dk.aau.cs.ds306e18.tournament.ui.BracketOverviewTabController;
import dk.aau.cs.ds306e18.tournament.ui.MatchVisualController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;

/** Used to display a Double Elimination stage. */
public class DoubleEliminationNode extends VBox implements ModelCoupledUI {

    private final Insets MARGINS = new Insets(0, 0, 8, 0);
    private final Font bracketLabelFont = new Font("Calibri", 28);

    private final DoubleEliminationFormat doubleElimination;
    private final BracketOverviewTabController boc;

    private ArrayList<MatchVisualController> mvcs = new ArrayList<>();

    /** Used to display the a Double Elimination stage. */
    public DoubleEliminationNode(DoubleEliminationFormat doubleElimination, BracketOverviewTabController boc) {
        this.doubleElimination = doubleElimination;
        this.boc = boc;
        update();
    }

    /** Updates all UI elements for the Double Elimination stage. */
    private void update() {
        removeElements();

        Label upperBracketLabel = new Label("UPPER BRACKET");
        upperBracketLabel.setFont(bracketLabelFont);
        getChildren().add(upperBracketLabel);
        getChildren().add(getUpperBracket(doubleElimination.getUpperBracketMatchesArray(), doubleElimination.getRounds()));

        Label lowerBracketLabel = new Label("LOWER BRACKET");
        lowerBracketLabel.setFont(bracketLabelFont);
        getChildren().add(lowerBracketLabel);
        getChildren().add(getLowerBracket(doubleElimination.getFinalMatch()));
    }


    /** Create a GridPane containing all given matches.
     * This is used to visualise the upper bracket.
     * Code: all but final match ripped from Nico/SingleEliminationNode.
     * @param matchArray an array of matches to be inserted in the GridPane.
     * @param rounds the number of rounds in the stage.
     * @return a GridPane containing all matches in the given array. */
    private GridPane getUpperBracket(Match[] matchArray, int rounds) {

        GridPane content = new GridPane();

        int m = 0; // match index
        int CELL_SIZE = 50;
        for (int r = 0; r < rounds; r++) {
            int matchesInRound = pow2(r);
            int column = rounds - 1 - r;
            int cellSpan = pow2(column);

            // Add matches for round r
            for (int i = 0; i < matchesInRound; i++) {
                Match match = matchArray[m];
                m++;
                VBox box = new VBox();

                // Some matches can be null
                if (match != null) {
                    MatchVisualController mvc = boc.loadVisualMatch(match);
                    mvcs.add(mvc);
                    box.getChildren().add(mvc.getRoot());
                }

                box.setAlignment(Pos.CENTER);
                box.setMinHeight(CELL_SIZE * cellSpan);

                content.add(box, column, (matchesInRound - 1 - i) * cellSpan);
                content.setRowSpan(box, cellSpan);
                content.setMargin(box, MARGINS);
                content.setValignment(box, VPos.CENTER);
            }
        }

        //Add final match
        Match finalMatch = doubleElimination.getFinalMatch();
        int column = rounds;
        int cellSpan = pow2(column);
        VBox box = new VBox();

        MatchVisualController mvc = boc.loadVisualMatch(finalMatch);
        mvcs.add(mvc);
        box.getChildren().add(mvc.getRoot());

        box.setAlignment(Pos.CENTER);
        box.setMinHeight(CELL_SIZE * cellSpan);
        content.add(box, column,0);
        content.setRowSpan(box, cellSpan);
        content.setMargin(box, MARGINS);
        content.setValignment(box, VPos.CENTER);

        return content;
    }

    /** Creates a HBox that has a visual representation of the lower bracket.
     * @param finalMatch the final match of the bracket.
     * @return a HBox containing a visual representation of the lower bracket. */
    private HBox getLowerBracket(Match finalMatch) {

        //Get all matches that is in the lower bracket and their level in the tree/bracket
        ArrayList<MatchWithLevel> miArray = getNextLevelMatches(finalMatch, 0);

        //Get highest index
        int highestIndex = 0;
        for (MatchWithLevel matchWithLevel : miArray) {
            if(highestIndex < matchWithLevel.getLevel())
                highestIndex = matchWithLevel.getLevel();
        }

        //Create the visuals
        HBox content = new HBox();
        for(int i = highestIndex; i > 0; i--) {

            VBox round = new VBox();

            //Find all matchIndex that has the same level as i
            for (MatchWithLevel matchWithLevel : miArray) {
                if(matchWithLevel.getLevel() == i){
                    MatchVisualController mvc = boc.loadVisualMatch(matchWithLevel.getMatch());
                    mvcs.add(mvc);
                    round.getChildren().add(mvc.getRoot());
                }
            }

            content.getChildren().add(round);
        }

        return content;
    }

    /** Recursive method that takes a match and returns an arrayList of all
     * its children and the levels of each child.
     * @param match a match to use as parent.
     * @param level the level of the given match in the lower bracket tree.
     * @return an arrayList of all child matches of the given match. */
    private ArrayList<MatchWithLevel> getNextLevelMatches(Match match, int level) {

        Match matchChild1 = match.getBlueFromMatch();
        Match matchChild2 = match.getOrangeFromMatch();
        ArrayList<MatchWithLevel> miArray = new ArrayList<>();

        //UpperBracketMatchCheck
        if(matchChild1 != null && !isMatchInLowerBracket(matchChild1)) matchChild1 = null;
        if(matchChild2 != null && !isMatchInLowerBracket(matchChild2)) matchChild2 = null;

        //Recursive call if child is not null
        if(matchChild1 != null)
            miArray.addAll(getNextLevelMatches(matchChild1, level + 1));

        if(matchChild2 != null)
            miArray.addAll(getNextLevelMatches(matchChild2, level + 1));

        //Return the array with this match
        miArray.add(new MatchWithLevel(match, level));
        return miArray;
    }

    /** @return true if the given match is in the lower bracket.*/
    private boolean isMatchInLowerBracket(Match match){
        for (Match lowerBracketMatch : doubleElimination.getLowerBracketMatches()) {
            if(lowerBracketMatch == match)
                return true;
        }

        return false;
    }

    /** Returns 2 to the power of n. */
    private int pow2(int n) {
        int res = 1;
        for (int i = 0; i < n; i++) {
            res *= 2;
        }
        return res;
    }

    @Override
    public void decoupleFromModel() {
        removeElements();
    }

    /** Completely remove all UI elements. */
    public void removeElements() {
        getChildren().clear();
        for (MatchVisualController mvc : mvcs) {
            mvc.decoupleFromModel();
        }
        mvcs.clear();
    }

    /** Used when creating the lower bracket where
     * the need for a match and its level is present. */
    private class MatchWithLevel {

        private Match match;
        private int level;

        public MatchWithLevel(Match match, int level) {
            this.match = match;
            this.level = level;
        }

        public Match getMatch() {
            return match;
        }

        public int getLevel() {
            return level;
        }
    }
}
