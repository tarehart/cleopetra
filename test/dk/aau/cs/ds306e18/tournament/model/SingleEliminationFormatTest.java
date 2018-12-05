package dk.aau.cs.ds306e18.tournament.model;

import dk.aau.cs.ds306e18.tournament.TestUtilities;
import dk.aau.cs.ds306e18.tournament.model.format.SingleEliminationFormat;
import dk.aau.cs.ds306e18.tournament.model.match.Match;
import dk.aau.cs.ds306e18.tournament.model.tiebreaker.TieBreakerBySeed;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SingleEliminationFormatTest {

    //there should be a correct amount of matches
    @Test
    public void amountOfMatchesTest01(){
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateTeams(8,1));
        assertEquals(7, bracket.getAllMatches().size());
    }

    //there should be a correct amount of matches
    @Test
    public void amountOfMatchesTest02() {
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateTeams(16,1));
        assertEquals(15, bracket.getAllMatches().size());
    }

    //final match should depend on all matches
    @Test
    public void dependsOnFinalMatch01() {
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateTeams(8,1));
        List<Match> matches = bracket.getAllMatches();
        for(int n = 1; n < matches.size(); n++) {
            assertTrue(matches.get(0).dependsOn(matches.get(n)));
        }
    }

    //final match should depend on all matches
    @Test
    public void dependsOnFinalMatch02() {
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateTeams(16,1));
        List<Match> matches = bracket.getAllMatches();
        for(int n = 1; n < matches.size(); n++) {
            assertTrue(matches.get(0).dependsOn(matches.get(n)));
        }
    }

    //final match should depend on all matches
    @Test
    public void dependsOnFinalMatch03() {
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateSeededTeams(10,1));
        List<Match> matches = bracket.getAllMatches();
        for(int n = 1; n < matches.size(); n++) {
            assertTrue(matches.get(0).dependsOn(matches.get(n)));
        }
    }

    //The teams should be seeded correctly in first round
    @Test
    public void seedTest01(){
        ArrayList<Team> teamList = TestUtilities.generateSeededTeams(8,1);
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(teamList);
        assertEquals(1, bracket.getAllMatches().get(bracket.getAllMatches().size()-1).getBlueTeam().getInitialSeedValue());
        assertEquals(8, bracket.getAllMatches().get(bracket.getAllMatches().size()-1).getOrangeTeam().getInitialSeedValue());
        assertEquals(4, bracket.getAllMatches().get(bracket.getAllMatches().size()-2).getBlueTeam().getInitialSeedValue());
        assertEquals(5, bracket.getAllMatches().get(bracket.getAllMatches().size()-2).getOrangeTeam().getInitialSeedValue());
        assertEquals(2, bracket.getAllMatches().get(bracket.getAllMatches().size()-3).getBlueTeam().getInitialSeedValue());
        assertEquals(7, bracket.getAllMatches().get(bracket.getAllMatches().size()-3).getOrangeTeam().getInitialSeedValue());
        assertEquals(3, bracket.getAllMatches().get(bracket.getAllMatches().size()-4).getBlueTeam().getInitialSeedValue());
        assertEquals(6, bracket.getAllMatches().get(bracket.getAllMatches().size()-4).getOrangeTeam().getInitialSeedValue());
    }

    //first match should be null, and best seeded team should be placed in next round
    @Test
    public void seedTest02(){
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateSeededTeams(7,1));
        assertNull(bracket.getUpperBracketMatchesArray()[6]);
        assertEquals(1,bracket.getUpperBracketMatchesArray()[2].getBlueTeam().getInitialSeedValue());
    }

    //match 3 should be null and snd seed should be placed in next round
    @Test
    public void seedTest03(){
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateSeededTeams(6,1));
        assertNull(bracket.getUpperBracketMatchesArray()[4]);
        assertEquals(2,bracket.getUpperBracketMatchesArray()[1].getBlueTeam().getInitialSeedValue());
    }

    //There should only be one match in first around, this should be the worst seeded teams.
    //The winner of this match should meet seed 1
    @Test
    public void seedTest04(){
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateSeededTeams(5,1));
        assertNull(bracket.getUpperBracketMatchesArray()[6]);
        assertNull(bracket.getUpperBracketMatchesArray()[4]);
        assertNull(bracket.getUpperBracketMatchesArray()[3]);
        assertEquals(4,bracket.getUpperBracketMatchesArray()[5].getBlueTeam().getInitialSeedValue());
        assertEquals(5,bracket.getUpperBracketMatchesArray()[5].getOrangeTeam().getInitialSeedValue());
    }

    //Should return the correct amount of playable matches
    @Test
    public void upcomingMatchesTest01(){
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateTeams(8,1));
        assertEquals(4, bracket.getUpcomingMatches().size());
        bracket.getUpcomingMatches().get(0).setHasBeenPlayed(true);
        assertEquals(3, bracket.getUpcomingMatches().size());
    }

    //Should return the correct amount of playable matches
    @Test
    public void upcomingMatchesTest02(){
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateTeams(6,1));
        assertEquals(2, bracket.getUpcomingMatches().size());
    }

    //Should return the correct amount of not-playable upcoming matches
    @Test
    public void pendingMatchesTest01(){
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateTeams(8,1));
        assertEquals(3, bracket.getPendingMatches().size());
        bracket.getAllMatches().get(bracket.getAllMatches().size()-1).setScores(1, 2, true);
        bracket.getAllMatches().get(bracket.getAllMatches().size()-2).setScores(1, 2, true);
        assertEquals(2, bracket.getPendingMatches().size());
    }

    //Should return the correct amount of played matches
    @Test
    public void completedMatchesTest01(){
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateTeams(8,1));
        assertEquals(0, bracket.getCompletedMatches().size());
        bracket.getAllMatches().get(bracket.getAllMatches().size()-1).setHasBeenPlayed(true);
        bracket.getAllMatches().get(bracket.getAllMatches().size()-2).setHasBeenPlayed(true);
        assertEquals(2, bracket.getCompletedMatches().size());
    }

    //Gets top 4 teams
    @Test
    public void getTopTeamsTest01(){
        SingleEliminationFormat bracket = generateBracketsAndWins(8);
        List <Team> teamList = new ArrayList<>(bracket.getTopTeams(4, new TieBreakerBySeed()));
        int seedValue = 1;
        for(int i = 0; i < 4; i++) {
            assertEquals(seedValue, teamList.get(i).getInitialSeedValue());
            seedValue++;
        }
    }

    //Gets top 6 teams
    @Test
    public void getTopTeamTest02() {
        SingleEliminationFormat bracket = generateBracketsAndWins(10);
        List<Team> teamList = new ArrayList<Team>(bracket.getTopTeams(6, new TieBreakerBySeed()));
        int seedValue = 1;
        for(int i = 0; i < 6; i++) {
            assertEquals(seedValue, teamList.get(i).getInitialSeedValue());
            seedValue++;
        }
    }

    //StageStatus test  pending-running-concluded
    @Test
    public void stageStatusTest01() {
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        assertEquals(StageStatus.PENDING, bracket.getStatus());
        bracket.start(TestUtilities.generateTeams(4, 1));
        assertEquals(StageStatus.RUNNING, bracket.getStatus());
        bracket =  generateBracketsAndWins(4);
        assertEquals(StageStatus.CONCLUDED, bracket.getStatus());
    }

    //StageStatus should still be running if some matches has been played
    @Test
    public void stageStatusTest02() {
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateTeams(8, 1));
        List<Match> arrayList = bracket.getAllMatches();
        arrayList.get(arrayList.size()-1).setBlueScore(1);
        arrayList.get(arrayList.size()-1).setHasBeenPlayed(true);
        arrayList.get(arrayList.size()-2).setBlueScore(1);
        arrayList.get(arrayList.size()-2).setHasBeenPlayed(true);
        assertEquals(StageStatus.RUNNING, bracket.getStatus());
    }

    //Should throw exception if the match is not playable
    @Test(expected = IllegalStateException.class)
    public void unPlayableMatch() {
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateTeams(8, 1));
        bracket.getAllMatches().get(0).setBlueScore(1);
    }

    //Should throw exception if the match is not playable
    @Test(expected = IllegalStateException.class)
    public void unPlayableMatch02() {
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateTeams(8, 1));
        bracket.getAllMatches().get(2).setHasBeenPlayed(true);
    }

    /** Generates a bracket and sets wins according to the best seed
     * @param amountOfTeams the amount of teams */
    private SingleEliminationFormat generateBracketsAndWins(int amountOfTeams) {
        SingleEliminationFormat bracket = new SingleEliminationFormat();
        bracket.start(TestUtilities.generateSeededTeams(amountOfTeams,1));

        for(int matchIndex = bracket.getAllMatches().size()-1 ; matchIndex >= 0; matchIndex--){
            if(bracket.getAllMatches().get(matchIndex).getBlueTeam().getInitialSeedValue() < bracket.getAllMatches().get(matchIndex).getOrangeTeam().getInitialSeedValue()) {
                bracket.getAllMatches().get(matchIndex).setBlueScore(1);
                bracket.getAllMatches().get(matchIndex).setHasBeenPlayed(true);
            }
            else{
                bracket.getAllMatches().get(matchIndex).setOrangeScore(1);
                bracket.getAllMatches().get(matchIndex).setHasBeenPlayed(true);
            }
        }
        return bracket;
    }
}