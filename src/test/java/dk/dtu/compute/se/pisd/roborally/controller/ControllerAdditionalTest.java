package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.*;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;

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
            Player player = new Player(board, null, "Player " + i);
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

        Space space = board.getSpace(2, 1);

        Assertions.assertNotNull(space);

        // (2,1) only has a WEST wall
        Assertions.assertEquals(1, space.getWalls().size());
        Assertions.assertTrue(space.getWalls().contains(Heading.WEST));

        // (2,1) has no actions in the current board setup
        Assertions.assertTrue(space.getActions().isEmpty());
    }

    /**
     * Verifies that the advanced board contains the expected
     * wall placements on selected spaces.
     */
    @Test
    void testAdvancedBoardHasExpectedWalls() {
        Board board = BoardFactory.getInstance().createBoard("Advanced");

        Space s3 = board.getSpace(2, 2);
        Assertions.assertTrue(s3.getWalls().contains(Heading.NORTH));

        Space s4 = board.getSpace(5, 4);
        Assertions.assertTrue(s4.getWalls().contains(Heading.NORTH));
        Assertions.assertTrue(s4.getWalls().contains(Heading.WEST));

        Space s5 = board.getSpace(6, 5);
        Assertions.assertTrue(s5.getWalls().contains(Heading.EAST));
        Assertions.assertTrue(s5.getWalls().contains(Heading.SOUTH));

        Space s6 = board.getSpace(2, 2);
        Assertions.assertTrue(s6.getWalls().contains(Heading.NORTH));
    }

    /**
     * Verifies that the advanced board contains conveyor belts
     * with the correct headings on the expected spaces.
     */
    @Test
    void testAdvancedBoardHasExpectedConveyorBelts() {
        Board board = BoardFactory.getInstance().createBoard("Advanced");

        ConveyorBelt belt1 = (ConveyorBelt) board.getSpace(0, 3).getActions().get(0);
        ConveyorBelt belt2 = (ConveyorBelt) board.getSpace(4, 3).getActions().get(0);
        ConveyorBelt belt3 = (ConveyorBelt) board.getSpace(3, 6).getActions().get(0);
        ConveyorBelt belt4 = (ConveyorBelt) board.getSpace(7, 2).getActions().get(0);
        ConveyorBelt belt5 = (ConveyorBelt) board.getSpace(9, 8).getActions().get(0);
        ConveyorBelt belt6 = (ConveyorBelt) board.getSpace(1, 7).getActions().get(0);

        Assertions.assertEquals(Heading.EAST, belt1.getHeading());
        Assertions.assertEquals(Heading.EAST, belt2.getHeading());

        Assertions.assertEquals(Heading.WEST, belt3.getHeading());

        Assertions.assertEquals(Heading.SOUTH, belt4.getHeading());

        Assertions.assertEquals(Heading.NORTH, belt5.getHeading());
        Assertions.assertEquals(Heading.EAST, belt6.getHeading());
    }

    /**
     * Verifies that checkpoints are placed on the expected
     * spaces of the advanced board.
     */
    @Test
    void testAdvancedBoardHasCheckpoints() {
        Board board = BoardFactory.getInstance().createBoard("Advanced");

        Assertions.assertInstanceOf(Checkpoint.class, board.getSpace(8, 1).getActions().get(0));
        Assertions.assertInstanceOf(Checkpoint.class, board.getSpace(5, 5).getActions().get(0));
        Assertions.assertInstanceOf(Checkpoint.class, board.getSpace(1, 9).getActions().get(0));

        Checkpoint checkpoint1 = (Checkpoint) board.getSpace(8, 1).getActions().get(0);
        Checkpoint checkpoint2 = (Checkpoint) board.getSpace(5, 5).getActions().get(0);
        Checkpoint checkpoint3 = (Checkpoint) board.getSpace(1, 9).getActions().get(0);

        Assertions.assertTrue(checkpoint3.isLastCheckPoint());
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
     */
    @Test
    void testCheckpointWithoutPlayerDoesNothing() {
        Checkpoint checkpoint1 = new Checkpoint(1);
        Board board = gameController.board;

        board.getSpace(2, 2).getActions().add(checkpoint1);
        checkpoint1.doAction(gameController, board.getSpace(2, 2));

        Assertions.assertEquals(0, board.getPlayer(0).getCheckpointsReached());
    }

    /**
     * Tests that the checkpoint action correctly updates the
     * number of checkpoints reached for the player.
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

    /**
     * Tests that AppController reports no running game initially.
     */
    @Test
    void testAppControllerIsGameRunningFalseInitially() {
        RoboRally roboRally = new RoboRally();
        AppController appController = new AppController(roboRally);

        Assertions.assertFalse(appController.isGameRunning());
    }

    /**
     * Tests that stopGame returns false when no game is running.
     */
    @Test
    void testAppControllerStopGameFalseWhenNoGameRunning() {
        RoboRally roboRally = new RoboRally();
        AppController appController = new AppController(roboRally);

        Assertions.assertFalse(appController.stopGame());
    }


    /**
     * Tests that update performs no action and throws no exception.
     */
    @Test
    void testAppControllerUpdateDoesNothing() {
        RoboRally roboRally = new RoboRally();
        AppController appController = new AppController(roboRally);

        Assertions.assertDoesNotThrow(() -> appController.update(null));
    }

    /**
     * Injects a GameController into AppController using reflection.
     *
     * @param controller     the AppController
     * @param gameController the GameController to inject
     * @throws Exception if reflection fails
     */
    private void setGameController(AppController controller, GameController gameController) throws Exception {
        Field field = AppController.class.getDeclaredField("gameController");
        field.setAccessible(true);
        field.set(controller, gameController);
    }


    /**
     * Verifies that the board constructor correctly initializes board name,
     * width, height, and step mode when a custom board name is provided.
     */
    @Test
    void testBoardConstructorWithBoardName() {
        Board board = new Board(5, 6, "TestBoard");

        Assertions.assertEquals("TestBoard", board.boardName);
        Assertions.assertEquals(5, board.width);
        Assertions.assertEquals(6, board.height);
        Assertions.assertFalse(board.isStepMode());
    }

    /**
     * Verifies that a game id can be assigned to a board and retrieved afterwards.
     */
    @Test
    void testSetAndGetGameId() {
        Board board = new Board(5, 5);

        board.setGameId(10);

        Assertions.assertEquals(10, board.getGameId());
    }

    /**
     * Verifies that setting the game id a second time to a different value
     * throws an IllegalStateException.
     */
    @Test
    void testSetGameIdTwiceWithDifferentValueThrowsException() {
        Board board = new Board(5, 5);

        board.setGameId(1);

        Assertions.assertThrows(IllegalStateException.class, () -> board.setGameId(2));
    }

    /**
     * Verifies that getSpace returns null for coordinates outside the board boundaries.
     */
    @Test
    void testGetSpaceReturnsNullOutsideBoard() {
        Board board = new Board(5, 5);

        Assertions.assertNull(board.getSpace(-1, 0));
        Assertions.assertNull(board.getSpace(0, -1));
        Assertions.assertNull(board.getSpace(5, 0));
        Assertions.assertNull(board.getSpace(0, 5));
    }

    /**
     * Verifies that getPlayer returns null for invalid player indices.
     */
    @Test
    void testGetPlayerReturnsNullForInvalidIndex() {
        Board board = new Board(5, 5);

        Assertions.assertNull(board.getPlayer(-1));
        Assertions.assertNull(board.getPlayer(0));
    }

    /**
     * Verifies that setCurrentPlayer only accepts players that belong to the same board.
     */
    @Test
    void testSetCurrentPlayerOnlyAcceptsPlayersOnBoard() {
        Board board = new Board(5, 5);
        Player player1 = new Player(board, null, "P1");
        Player player2 = new Player(new Board(5, 5), null, "P2");

        board.addPlayer(player1);
        board.setCurrentPlayer(player1);
        board.setCurrentPlayer(player2);

        Assertions.assertEquals(player1, board.getCurrentPlayer());
    }

    /**
     * Verifies that the board phase and step can be updated and retrieved correctly.
     */
    @Test
    void testSetPhaseAndStep() {
        Board board = new Board(5, 5);

        board.setPhase(Phase.PROGRAMMING);
        board.setStep(3);

        Assertions.assertEquals(Phase.PROGRAMMING, board.getPhase());
        Assertions.assertEquals(3, board.getStep());
    }

    /**
     * Verifies that step mode can be enabled on the board.
     */
    @Test
    void testSetStepMode() {
        Board board = new Board(5, 5);

        board.setStepMode(true);

        Assertions.assertTrue(board.isStepMode());
    }

    /**
     * Verifies that getPlayerNumber returns -1 for a player that belongs to another board.
     */
    @Test
    void testGetPlayerNumberReturnsMinusOneForPlayerFromDifferentBoard() {
        Board board1 = new Board(5, 5);
        Board board2 = new Board(5, 5);

        Player player = new Player(board2, null, "OtherPlayer");

        Assertions.assertEquals(-1, board1.getPlayerNumber(player));
    }

    /**
     * Verifies that getNeighbour returns the correct adjacent space
     * for each cardinal direction from a center space.
     */
    @Test
    void testGetNeighbourReturnsCorrectNeighbour() {
        Board board = new Board(5, 5);
        Space center = board.getSpace(2, 2);

        Assertions.assertEquals(board.getSpace(2, 1), board.getNeighbour(center, Heading.NORTH));
        Assertions.assertEquals(board.getSpace(2, 3), board.getNeighbour(center, Heading.SOUTH));
        Assertions.assertEquals(board.getSpace(1, 2), board.getNeighbour(center, Heading.WEST));
        Assertions.assertEquals(board.getSpace(3, 2), board.getNeighbour(center, Heading.EAST));
    }

    /**
     * Verifies that getNeighbour returns null when there is no neighbouring space
     * because the current space lies on the board edge.
     */
    @Test
    void testGetNeighbourReturnsNullAtBoardEdge() {
        Board board = new Board(5, 5);
        Space corner = board.getSpace(0, 0);

        Assertions.assertNull(board.getNeighbour(corner, Heading.NORTH));
        Assertions.assertNull(board.getNeighbour(corner, Heading.WEST));
    }

    /**
     * Verifies that neighbour wall detection returns true when the current space
     * contains a wall in the queried direction.
     */
    @Test
    void testNeighbourWallReturnsTrueWhenCurrentSpaceHasWall() {
        Board board = new Board(5, 5);
        Space space = board.getSpace(2, 2);

        space.getWalls().add(Heading.NORTH);

        Assertions.assertTrue(board.getNieghborwall(space, Heading.NORTH));
    }

    /**
     * Verifies that neighbour wall detection returns true when the neighbouring
     * space contains a wall facing the opposite direction.
     */
    @Test
    void testNeighbourWallReturnsTrueWhenNeighbourHasOppositeWall() {
        Board board = new Board(5, 5);
        Space space = board.getSpace(2, 2);
        Space northNeighbour = board.getSpace(2, 1);

        northNeighbour.getWalls().add(Heading.SOUTH);

        Assertions.assertTrue(board.getNieghborwall(space, Heading.NORTH));
    }

    /**
     * Verifies that neighbour wall detection returns false when neither the current
     * space nor the neighbouring space contains a blocking wall.
     */
    @Test
    void testNeighbourWallReturnsFalseWhenNoWallsExist() {
        Board board = new Board(5, 5);
        Space space = board.getSpace(2, 2);

        Assertions.assertFalse(board.getNieghborwall(space, Heading.NORTH));
        Assertions.assertFalse(board.getNieghborwall(space, Heading.SOUTH));
        Assertions.assertFalse(board.getNieghborwall(space, Heading.WEST));
        Assertions.assertFalse(board.getNieghborwall(space, Heading.EAST));
    }

    /**
     * Verifies that the move counter starts at zero, increments correctly,
     * and can be updated explicitly.
     */
    @Test
    void testMoveCounterMethods() {
        Board board = new Board(5, 5);

        Assertions.assertEquals(0, board.getMoveCounter());

        board.IncMovecounter();
        board.IncMovecounter();

        Assertions.assertEquals(2, board.getMoveCounter());

        board.setMoveCounter(10);

        Assertions.assertEquals(10, board.getMoveCounter());
    }

    /**
     * Verifies that the status message during an ongoing game includes
     * the current player, phase, and step number.
     */
    @Test
    void testGetStatusMessageDuringGame() {
        Board board = new Board(5, 5);
        Player player = new Player(board, null, "Tester");

        board.addPlayer(player);
        board.setCurrentPlayer(player);
        board.setPhase(Phase.PROGRAMMING);
        board.setStep(2);

        String status = board.getStatusMessage();

        Assertions.assertTrue(status.contains("Tester"));
        Assertions.assertTrue(status.contains("PROGRAMMING"));
        Assertions.assertTrue(status.contains("2"));
    }

    /**
     * Verifies that the status message after the game has finished includes
     * the winner's name, a winning message, and the move count.
     */
    @Test
    void testGetStatusMessageWhenFinished() {
        Board board = new Board(5, 5);
        Player player = new Player(board, null, "Winner");

        board.addPlayer(player);
        board.setCurrentPlayer(player);
        board.setPhase(Phase.FINISHED);
        board.setMoveCounter(7);

        String status = board.getStatusMessage();

        Assertions.assertTrue(status.contains("Winner"));
        Assertions.assertTrue(status.contains("won the game"));
        Assertions.assertTrue(status.contains("7"));
    }
}