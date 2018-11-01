package dk.aau.cs.ds306e18.tournament.model;

import dk.aau.cs.ds306e18.tournament.model.format.RoundRobinFormat;
import org.junit.Test;

import static dk.aau.cs.ds306e18.tournament.TestUtilities.*;
import static org.junit.Assert.*;


public class RoundRobinFormatTest {


    @Test
    public void testRoundRobinBracket(){

        int numberOfTeams = 4;
        int teamSize = 1;

        RoundRobinFormat bracket = new RoundRobinFormat();
        bracket.start(generateTeams(numberOfTeams, teamSize));
    }


    @Test
    public void testrunCheck(){

        int numberOfTeams = 20;
        int teamSize = 1;

        RoundRobinFormat bracket = new RoundRobinFormat();
        bracket.start(generateTeams(numberOfTeams, teamSize));

        assertEquals((bracket.runCheck(3)), 13);
        assertEquals((bracket.runCheck(1)), 11);

        for (int i = 1; i <= numberOfTeams; i++) {
            assertTrue(bracket.runCheck(i) < numberOfTeams);
            assertTrue(bracket.runCheck(i) > 0);
            if (i >= (numberOfTeams/2)) {
                assertTrue(bracket.runCheck(i) < i);
            } else assertTrue(bracket.runCheck(i) > i);
        }
    }
}