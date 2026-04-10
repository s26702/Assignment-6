package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dk.dtu.compute.se.pisd.roborally.model.Phase;

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


    /**
     * Tests that moving forward moves the player one space in the current heading
     *
     * @author Mikkel Hjelm
     */
    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current, current.getHeading());

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(),
                "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(),
                "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(),
                "Space (0,0) should be empty!");
    }


    // TODO and there should be more tests added for the different assignments eventually


    /**
     * uTurn should rotate the player 180 degrees without moving.
     * @author Lucas Spielberg-Winther
     */
    @Test
    void uTurn() {
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

    /**
     * Tests that the current player cannot move to an occupied space.
     * @author Lucas Spielberg-Winther
     */
    @Test
    void moveCurrentPlayerToOccupiedSpace() {
        Board board = gameController.board;

        Player player1 = board.getCurrentPlayer();
        Player player2 = board.getPlayer(1);

        // Try to move player1 to player2's space
        gameController.moveCurrentPlayerToSpace(board.getSpace(1,1));

        Assertions.assertEquals(player1, board.getSpace(0, 0).getPlayer(),
                "Player1 should still be at (0,0) since (1,1) is occupied!");

        Assertions.assertEquals(player2, board.getSpace(1, 1).getPlayer(),
                "Player2 should still be at (1,1)!");

        Assertions.assertEquals(player1, board.getCurrentPlayer(),
                "Current player should remain player1 after a failed move!");
    }

    /**
     * moveBack should move the player one step in the opposite direction without changing direction.
     * @author Lucas Spielberg-Winther
     */
    @Test
    void moveBack() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer(); // at (0,0), heading SOUTH

        // Move to (0,3) first so there is room to move back
        current.setSpace(board.getSpace(0, 3));
        Heading originalHeading = current.getHeading(); // SOUTH

        gameController.moveBack(current, current.getHeading());

        Assertions.assertEquals(current, board.getSpace(0, 2).getPlayer(),
                "Player should be one step back (northward) at (0,2)!");
        Assertions.assertEquals(originalHeading, current.getHeading(),
                "Heading should be restored to original after moveBack!");
    }

    /**
     * Tests that fast-forward pushes another player two steps when possible.
     * @author Mikkel Hjelm
     */
    @Test
    void testFastForwardPushesOtherPlayer() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Player other = board.getPlayer(1);

        current.setSpace(board.getSpace(2, 2));
        current.setHeading(Heading.EAST);

        other.setSpace(board.getSpace(3, 2));

        gameController.fastForward(current, current.getHeading());

        Assertions.assertEquals(current, board.getSpace(4, 2).getPlayer(),
                "Current player should end two spaces forward after fast forward.");
        Assertions.assertEquals(other, board.getSpace(5, 2).getPlayer(),
                "Other player should be pushed forward during fast forward.");
    }

    /**
     * Tests that moving backwards pushes another player if the target
     * space is occupied and the chain can move.
     * @author Mikkel Hjelm
     */
    @Test
    void testMoveBackPushesOtherPlayer() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Player other = board.getPlayer(1);

        current.setSpace(board.getSpace(3, 3));
        current.setHeading(Heading.NORTH);

        other.setSpace(board.getSpace(3, 2));

        gameController.moveBack(current, current.getHeading());

        Assertions.assertEquals(current, board.getSpace(3, 2).getPlayer(),
                "Current player should move backwards into the occupied space.");
        Assertions.assertEquals(other, board.getSpace(3, 1).getPlayer(),
                "Other player should be pushed one step backwards.");
        Assertions.assertEquals(Heading.NORTH, current.getHeading(),
                "Heading should remain unchanged after moveBack.");
    }

    /**
     * Tests that moving backwards does not move any player when the push
     * chain is blocked by the edge of the board.
     *
     * @author Mikkel Hjelm
     */
    @Test
    void testMoveBackBlockedByPushChain() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Player other = board.getPlayer(1);

        current.setSpace(board.getSpace(3, 1));
        current.setHeading(Heading.NORTH);

        other.setSpace(board.getSpace(3, 0));

        gameController.moveBack(current, current.getHeading());

        Assertions.assertEquals(current, board.getSpace(3, 1).getPlayer(),
                "Current player should remain in place when backwards push is blocked.");
        Assertions.assertEquals(other, board.getSpace(3, 0).getPlayer(),
                "Other player should remain in place when the push chain is blocked.");
        Assertions.assertEquals(Heading.NORTH, current.getHeading(),
                "Heading should remain unchanged after failed moveBack.");
    }

    /**
     * Tests that a player collecting checkpoints in order increments their checkpoint count.
     * @author Lucas Spielberg-Winther
     */
    @Test
    void testCheckpointInOrder() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        Checkpoint checkpoint1 = new Checkpoint(1);
        board.getSpace(2, 2).getActions().add(checkpoint1);

        current.setSpace(board.getSpace(2, 2));
        checkpoint1.doAction(gameController, board.getSpace(2, 2));

        Assertions.assertEquals(1, current.getCheckpointsReached(),
                "Player should have 1 checkpoint after reaching checkpoint 1!");
    }

    /**
     * Tests that a player cannot skip checkpoints — collecting checkpoint 2
     * before checkpoint 1 should not increment the count.
     * @author Lucas Spielberg-Winther
     */
    @Test
    void testCheckpointOutOfOrder() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        Checkpoint checkpoint2 = new Checkpoint(2);
        board.getSpace(2, 2).getActions().add(checkpoint2);

        current.setSpace(board.getSpace(2, 2));
        checkpoint2.doAction(gameController, board.getSpace(2, 2));

        Assertions.assertEquals(0, current.getCheckpointsReached(),
                "Player should not collect checkpoint 2 before checkpoint 1!");
    }

    /**
     * Tests that reaching the last checkpoint triggers the FINISHED phase.
     * @author Lucas Spielberg-Winther
     */
    @Test
    void testLastCheckpointWinsGame() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        Checkpoint checkpoint1 = new Checkpoint(1);
        board.getSpace(2, 2).getActions().add(checkpoint1);

        Checkpoint checkpoint2 = new Checkpoint(2);
        checkpoint2.setLastCheckPoint(true);
        board.getSpace(3, 3).getActions().add(checkpoint2);

        current.setSpace(board.getSpace(2, 2));
        checkpoint1.doAction(gameController, board.getSpace(2, 2));

        current.setSpace(board.getSpace(3, 3));
        checkpoint2.doAction(gameController, board.getSpace(3, 3));

        Assertions.assertEquals(Phase.FINISHED, board.getPhase(),
                "Game should be in FINISHED phase after collecting all checkpoints!");
        Assertions.assertEquals(current, board.getCurrentPlayer(),
                "Winner should be the current player!");
    }

    /**
     * Tests that the conveyor belt moves a player one space in the belt's direction.
     * @author Lucas Spielberg-Winther
     */
    @Test
    void testConveyorBeltMovesPlayer() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        ConveyorBelt belt = new ConveyorBelt();
        belt.setHeading(Heading.EAST);
        board.getSpace(2, 2).getActions().add(belt);

        current.setSpace(board.getSpace(2, 2));
        belt.doAction(gameController, board.getSpace(2, 2));

        Assertions.assertEquals(current, board.getSpace(3, 2).getPlayer(),
                "Player should be moved one space east by the conveyor belt!");
    }

    /**
     * Tests that a robot pushing another robot moves both correctly.
     * @author Lucas Spielberg-Winther
     */
    @Test
    void testPushChain() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Player other = board.getPlayer(1);

        current.setSpace(board.getSpace(0, 2));
        current.setHeading(Heading.SOUTH);
        other.setSpace(board.getSpace(0, 3));

        gameController.moveForward(current, current.getHeading());

        Assertions.assertEquals(current, board.getSpace(0, 3).getPlayer(),
                "Current player should have moved to (0,3)!");
        Assertions.assertEquals(other, board.getSpace(0, 4).getPlayer(),
                "Other player should have been pushed to (0,4)!");
    }

    /**
     * Tests that a robot cannot push another robot off the board edge.
     * @author Lucas Spielberg-Winther
     */
    @Test
    void testPushChainBlockedByEdge() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Player other = board.getPlayer(1);

        current.setSpace(board.getSpace(0, 6));
        current.setHeading(Heading.SOUTH);
        other.setSpace(board.getSpace(0, 7));

        gameController.moveForward(current, current.getHeading());

        Assertions.assertEquals(current, board.getSpace(0, 6).getPlayer(),
                "Current player should not move when push chain is blocked by edge!");
        Assertions.assertEquals(other, board.getSpace(0, 7).getPlayer(),
                "Other player should not be pushed off the board!");
    }

    /**
     *  Tests that a checkpoint does not increment progress
     *  when no player is standing on the space.
     *  Ensures that checkpoint actions only affects players
     *  currently occupying the checkpoint space.
     * @author Mikkel Hjelm
     */
    @Test
    void testCheckpointWithoutPlayerDoesNothing() {
        Board board = gameController.board;
        Checkpoint checkpoint1 = new Checkpoint(1);

        board.getSpace(2, 2).getActions().add(checkpoint1);
        checkpoint1.doAction(gameController, board.getSpace(2, 2));

        Assertions.assertEquals(0, board.getPlayer(0).getCheckpointsReached());
    }
}