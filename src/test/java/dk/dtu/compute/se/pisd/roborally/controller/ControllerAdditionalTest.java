package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

/**
 * Additional unit tests for controller-related supporting classes.
 *
 * This test class focuses on increasing line coverage for classes that are not
 * part of the main GameController logic, primarily {@link BoardFactory},
 * {@link Checkpoint}, and {@link ConveyorBelt}.
 * The tests verify correct board creation, singleton behavior, board contents,
 * and available board names.
 *
 * @author Mikkel Hjelm
 */
class ControllerAdditionalTest {

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
     * Verifies that BoardFactory follows the singleton pattern
     * and always returns the same instance.
     */
    @Test
    void testGetInstanceReturnsSameInstance() {
        BoardFactory factory1 = BoardFactory.getInstance();
        BoardFactory factory2 = BoardFactory.getInstance();

        Assertions.assertNotNull(factory1);
        Assertions.assertSame(factory1, factory2);
    }

    /**
     * Verifies that passing null to createBoard returns
     * the default simple board configuration.
     */
    @Test
    void testCreateBoardNullReturnsSimpleBoard() {
        Board board = BoardFactory.getInstance().createBoard(null);

        Assertions.assertNotNull(board);
        Assertions.assertEquals("Simple", board.boardName);
        Assertions.assertEquals(8, board.width);
        Assertions.assertEquals(8, board.height);
    }

    /**
     * Verifies that an unknown board name falls back
     * to the default simple board.
     */
    @Test
    void testCreateBoardUnknownReturnsSimpleBoard() {
        Board board = BoardFactory.getInstance().createBoard("Blah Blah");

        Assertions.assertNotNull(board);
        Assertions.assertEquals("Simple", board.boardName);
        Assertions.assertEquals(8, board.width);
        Assertions.assertEquals(8, board.height);
    }

    /**
     * Verifies that requesting the simple board returns
     * An 8x8 board with the correct name.
     */
    @Test
    void testCreateBoardSimpleReturnsSimpleBoard() {
        Board board = BoardFactory.getInstance().createBoard("Simple");

        Assertions.assertNotNull(board);
        Assertions.assertEquals("Simple", board.boardName);
        Assertions.assertEquals(8, board.width);
        Assertions.assertEquals(8, board.height);
    }

    /**
     * Verifies that requesting the advanced board returns
     * a 10x10 board with the correct name.
     */
    @Test
    void testCreateBoardAdvancedReturnsAdvancedBoard() {
        Board board = BoardFactory.getInstance().createBoard("Advanced");

        Assertions.assertNotNull(board);
        Assertions.assertEquals("Advanced", board.boardName);
        Assertions.assertEquals(10, board.width);
        Assertions.assertEquals(10, board.height);
    }

    /**
     * Verifies that the simple board contains the expected
     * walls and conveyor belts at position (1,1).
     */
    @Test
    void testSimpleBoardHasExpectedWallsAndActionsAt11() {
        Board board = BoardFactory.getInstance().createBoard("Simple");

        Space space = board.getSpace(1, 1);

        Assertions.assertNotNull(space);

        // (1,1) only has a SOUTH wall
        Assertions.assertEquals(1, space.getWalls().size());
        Assertions.assertTrue(space.getWalls().contains(Heading.SOUTH));

        // (1,1) has no actions in the current board setup
        Assertions.assertTrue(space.getActions().isEmpty());
    }

    /**
     * Verifies that the advanced board contains the expected
     * wall placements on selected spaces.
     */
    @Test
    void testAdvancedBoardHasExpectedWalls() {
        Board board = BoardFactory.getInstance().createBoard("Advanced");

        Space s1 = board.getSpace(2, 2);
        Assertions.assertTrue(s1.getWalls().contains(Heading.NORTH));
        Assertions.assertTrue(s1.getWalls().contains(Heading.WEST));

        Space s2 = board.getSpace(5, 3);
        Assertions. assertTrue(s2.getWalls().contains(Heading.NORTH));
        Assertions.assertTrue(s2.getWalls().contains(Heading.WEST));

        Space s3 = board.getSpace(8, 6);
        Assertions.assertTrue(s3.getWalls().contains(Heading.NORTH));
        Assertions.assertTrue(s3.getWalls().contains(Heading.SOUTH));

        Space s4 = board.getSpace(3, 9);
        Assertions.assertTrue(s4.getWalls().contains(Heading.EAST));
        Assertions.assertTrue(s4.getWalls().contains(Heading.WEST));
        Assertions.assertTrue(s4.getWalls().contains(Heading.SOUTH));
    }

    /**
     * Verifies that the advanced board contains conveyor belts
     * with the correct headings on the expected spaces.
     */
    @Test
    void testAdvancedBoardHasExpectedConveyorBelts() {
        Board board = BoardFactory.getInstance().createBoard("Advanced");

        ConveyorBelt belt1 = (ConveyorBelt) board.getSpace(1, 1).getActions().get(0);
        ConveyorBelt belt2 = (ConveyorBelt) board.getSpace(4, 1).getActions().get(0);
        ConveyorBelt belt3 = (ConveyorBelt) board.getSpace(7, 4).getActions().get(0);
        ConveyorBelt belt4 = (ConveyorBelt) board.getSpace(8, 8).getActions().get(0);
        ConveyorBelt belt5 = (ConveyorBelt) board.getSpace(3, 6).getActions().get(0);

        Assertions.assertEquals(Heading.EAST, belt1.getHeading());
        Assertions.assertEquals(Heading.SOUTH, belt2.getHeading());
        Assertions.assertEquals(Heading.WEST, belt3.getHeading());
        Assertions.assertEquals(Heading.SOUTH, belt4.getHeading());
        Assertions.assertEquals(Heading.EAST, belt5.getHeading());
    }

    /**
     * Verifies that checkpoints are placed on the expected
     * spaces of the advanced board.
     */
    @Test
    void testAdvancedBoardHasCheckpointsAtExpectedPositions() {
        Board board = BoardFactory.getInstance().createBoard("Advanced");

        Assertions.assertInstanceOf(Checkpoint.class, board.getSpace(4, 4).getActions().get(0));
        Assertions.assertInstanceOf(Checkpoint.class, board.getSpace(6, 6).getActions().get(0));
        Assertions.assertInstanceOf(Checkpoint.class, board.getSpace(5, 9).getActions().get(0));
        Assertions.assertInstanceOf(Checkpoint.class, board.getSpace(8, 7).getActions().get(0));
    }

    /**
     * Verifies that the board factory exposes the expected
     * available board names.
     */
    @Test
    void testGetBoardNamesReturnsExpectedNames() {
        List<String> names = BoardFactory.getInstance().getBoardNames();

        Assertions.assertEquals(2, names.size());
        Assertions.assertTrue(names.contains("Simple"));
        Assertions.assertTrue(names.contains("Advanced"));
    }

    /**
     * Verifies that the returned board names list cannot be modified.
     */
    @Test
    void testGetBoardNamesReturnsUnmodifiableList() {
        List<String> names = BoardFactory.getInstance().getBoardNames();

        Assertions.assertThrows(UnsupportedOperationException.class, () -> names.add("TestBoard"));
    }
    /**
     * Ensures that checkpoint actions only affect players
     * currently occupying the checkpoint space.
     * @author Mikkel Hjelm
     */
    @Test
    void testCheckpointWithoutPlayerDoesNothing() {
        Checkpoint checkpoint1 = new Checkpoint(1);
        Board  board = gameController.board;

        board.getSpace(2, 2).getActions().add(checkpoint1);
        checkpoint1.doAction(gameController, board.getSpace(2, 2));

        Assertions.assertEquals(0, board.getPlayer(0).getCheckpointsReached());
    }

    /**
     * Tests that the checkpoint action correctly updates the
     * number of checkpoints reached for the player.
     * @author Mikkel Hjelm
     */
    @Test
    void testCheckpointIncrementsWhenPlayerIsOnSpace() {
        Checkpoint checkpoint = new Checkpoint(1);
        Board board = gameController.board;
        Player player = board.getPlayer(0);

        player.setSpace(board.getSpace(2, 2));
        board.getSpace(2, 2).getActions().add(checkpoint);

        checkpoint.doAction(gameController, board.getSpace(2, 2));

        Assertions.assertEquals(1, player.getCheckpointsReached());
    }


    /**
     * Tests that the checkpoint constructor throws an exception
     * when the checkpoint number is 0 or less.
     */
    @Test
    void testCheckpointConstructorThrowsExceptionForZero() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Checkpoint(0));
    }


    /**
     * Tests that the checkpoint number can be updated
     * and retrieved correctly using the setter and getter.
     */
    @Test
    void testCheckpointNumberGetterAndSetter() {
        Checkpoint checkpoint = new Checkpoint(1);

        checkpoint.setNumber(3);

        Assertions.assertEquals(3, checkpoint.getNumber());
    }

    /**
     * Tests that the last checkpoint
     * can be updated and retrieved correctly
     */
    @Test
    void testLastCheckpointGetterAndSetter() {
        Checkpoint checkpoint = new Checkpoint(1);

        checkpoint.setLastCheckPoint(true);

        Assertions.assertTrue(checkpoint.isLastCheckPoint());
    }
}
