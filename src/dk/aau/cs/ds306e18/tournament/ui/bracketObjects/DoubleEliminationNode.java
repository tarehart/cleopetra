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

import java.util.ArrayList;

/** Used to display a Double Elimination stage. */
public class DoubleEliminationNode extends VBox implements ModelCoupledUI {

    private final Insets MARGINS = new Insets(0, 0, 8, 0);
    private final int CELL_SIZE = 50;

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

        getChildren().add(new Label("UPPER BRACKET"));
        getChildren().add(getUpperBracket(doubleElimination.getUpperBracketMatchesArray(), doubleElimination.getRounds()));
        getChildren().add(new Label("LOWER BRACKET"));
        getChildren().add(getLowerBracket(doubleElimination.getFinalMatch(), null, null));
    }



    private GridPane getUpperBracket(Match[] matchArray, int rounds) {
        //Match[] matchArray = singleElimination.getMatchesAsArray();
        //int rounds = singleElimination.getRounds();

        GridPane content = new GridPane();

        int m = 0; // match index
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


    private HBox getLowerBracket(Match finalMatch, ArrayList<Match> upperBracket, ArrayList<Match> lowerBracket) {


        /* Concept: Starting point is finalMatch. Then get the two matches: blueFromMatch and orangeFromMatch, end check
        * is these upper or lower. The matches found that is in lower is the matches in the next round. Then repeat the
        * process from the newly found matches that is in lower bracket. */

        ArrayList<MatchIndex> miArray = getNextLevelMatches(finalMatch, 0);

        //Get highest index
        int highestIndex = 0;
        for (MatchIndex matchIndex : miArray) {
            if(highestIndex < matchIndex.getLevel())
                highestIndex = matchIndex.getLevel();
        }

        //Create the visuals
        HBox content = new HBox();
        for(int i = highestIndex; i > 0; i--) {

            VBox round = new VBox();

            //Find all matchIndex that has the same level as i
            for (MatchIndex matchIndex : miArray) {
                if(matchIndex.getLevel() == i){
                    MatchVisualController mvc = boc.loadVisualMatch(matchIndex.getMatch());
                    mvcs.add(mvc);
                    round.getChildren().add(mvc.getRoot());
                }
            }

            content.getChildren().add(round);
        }

        return content;
    }

    /** index 0 = final match
     * @param match
     * @param level
     * @return
     */
    private ArrayList<MatchIndex> getNextLevelMatches(Match match, int level) {

        Match matchChild1 = match.getBlueFromMatch();
        Match matchChild2 = match.getOrangeFromMatch();

        ArrayList<MatchIndex> miArray = new ArrayList<>();

        //UpperBracketMatchCheck
        if(matchChild1 != null && !isMatchInLowerBracket(matchChild1))
            matchChild1 = null;

        if(matchChild2 != null && !isMatchInLowerBracket(matchChild2))
            matchChild2 = null;

        //Recursive call if child is not null
        if(matchChild1 != null){
            miArray.addAll(getNextLevelMatches(matchChild1, level + 1));
        }

        if(matchChild2 != null){
            miArray.addAll(getNextLevelMatches(matchChild2, level + 1));
        }

        //Return the array with this match
        miArray.add(new MatchIndex(match, level));
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

    private GridPane getLowerBracket(Match[] matchArray, int rounds) {
        //Match[] matchArray = singleElimination.getMatchesAsArray();
        //int rounds = singleElimination.getRounds();

        //TODO DEN BLIVER TEGNET FRA HØJRE MOD VENSTRE, SÅ ÆNDRE NOGET MED R OG ROUNDS.

        System.out.println("Rounds: " + rounds);

        GridPane content = new GridPane();

        int m = 0; // match index
        int matchesInPriveousLower = 0;
        for (int r = 0; r < rounds; r++) {


            //int matchesInPriveousLower = 0;
            if(r != rounds - 1){ //todo r might need to be replaced with rounds.
                matchesInPriveousLower = pow2(r+1);
            }
            int matchesInRoundUpper = pow2(r);
            int matchesInRound = (int)Math.ceil((matchesInRoundUpper + (matchesInPriveousLower)) / 2d);


            System.out.println("Round: " + r + " - Matches in round: " + matchesInRound + " - matchesInPrivLower: " + matchesInPriveousLower + " - MatchesInUpper: " + matchesInRoundUpper);

            //int matchesInRound = (int)Math.ceil((pow2(r) / 2d));

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

            matchesInPriveousLower = matchesInRound;
        }

        return content;
    }

    /* Does work, but expect bracket gets drawn from right to left.
    private GridPane getLowerBracket(Match[] matchArray, int rounds) {
        //Match[] matchArray = singleElimination.getMatchesAsArray();
        //int rounds = singleElimination.getRounds();

        //TODO DEN BLIVER TEGNET FRA HØJRE MOD VENSTRE, SÅ ÆNDRE NOGET MED R OG ROUNDS.

        GridPane content = new GridPane();

        int m = 0; // match index
        int matchesInPriveousLower = 0;
        for (int r = 0; r < rounds; r++) {

            //int matchesInPriveousLower = 0;
            if(r != 0){ //todo r might need to be replaced with rounds.
                matchesInPriveousLower = pow2(r-1);
            }
            int matchesInRoundUpper = pow2(r);
            int matchesInRound = (int)Math.ceil((matchesInRoundUpper + (matchesInPriveousLower)) / 2d);


            System.out.println("Round: " + r + " - Matches in round: " + matchesInRound);

            //int matchesInRound = (int)Math.ceil((pow2(r) / 2d));

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

            matchesInPriveousLower = matchesInRound;
        }

        return content;
    }
    */

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

    private class MatchIndex {

        private Match match;
        private int level;

        public MatchIndex(Match match, int level) {
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
