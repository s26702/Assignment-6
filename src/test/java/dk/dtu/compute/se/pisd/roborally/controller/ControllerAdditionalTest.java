package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Board board = BoardFactory.getInstance().createBoard("SomethingElse");

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
        Assertions.assertTrue(space.getWalls().contains(Heading.SOUTH));
        Assertions.assertTrue(space.getWalls().contains(Heading.WEST));
        Assertions.assertEquals(2, space.getActions().size());

        Assertions.assertInstanceOf(ConveyorBelt.class, space.getActions().get(0));
        Assertions.assertInstanceOf(ConveyorBelt.class, space.getActions().get(1));

        ConveyorBelt belt1 = (ConveyorBelt) space.getActions().get(0);
        ConveyorBelt belt2 = (ConveyorBelt) space.getActions().get(1);

        Assertions.assertEquals(Heading.WEST, belt1.getHeading());
        Assertions.assertEquals(Heading.NORTH, belt2.getHeading());
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
}
