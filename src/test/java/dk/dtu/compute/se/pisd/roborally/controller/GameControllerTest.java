package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }


    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }


    // TODO and there should be more tests added for the different assignments eventually


    /**
     * uturn should rotate the player 180 degrees without moving.
     */
    @Test
    void uturn() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer(); // at (0,0), heading SOUTH

        Heading originalHeading = current.getHeading();
        gameController.uturn(current);

        Assertions.assertEquals(originalHeading.next().next(), current.getHeading(),
                "Heading should be reversed (180°) after U-turn!");
        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(),
                "Player should not have moved after U-turn!");
    }

    /**
     * Test that turning rights updates the heading correctly.
     * @author Mikkel Hjelm
     */
    @Test
    void testTurnRight() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.setHeading(Heading.NORTH);
        gameController.turnRight(current);

        Assertions.assertEquals(Heading.EAST, current.getHeading(),
                "Player should be heading EAST after turning right from NORTH");
        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(),
                "Player should not have moved after turning right!");
    }

    /**
     * Test that turning left updates the heading correctly.
     * @author Christoffer Sørensen
     */

    @Test
    void testTurnLeft() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.setHeading(Heading.NORTH);
        gameController.turnLeft(current);

        Assertions.assertEquals(Heading.WEST, current.getHeading(),
                "Player should be heading WEST after turning left from NORTH");
        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(),
                "Player should not have moved after turning left!");
    }

}