package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

/**
 * Unit tests for the {@link GameController} class.
 *
 * This test class verifies the core game logic of RoboRally, including
 * player movement, command execution, phase transitions, board interactions,
 * and edge cases related to game progression.
 *
 * The purpose of these tests is to ensure correct behavior of the main
 * controller logic and achieve high line coverage of the GameController.
 *
 * @author Lucas Spielberg-Winther, Mikkel Hjelm and Niklas Hansen
 */

class GameControllerTest {

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

    // --- moveCurrentPlayerToSpace ---

    @Test
    void testMoveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Space target = board.getSpace(5, 0);

        current.setSpace(board.getSpace(0, 0));
        gameController.moveCurrentPlayerToSpace(target);

        Assertions.assertEquals(current, target.getPlayer(),
                "Current player should move to the target space.");
    }

    @Test
    void testMoveCurrentPlayerToSpaceOccupied() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Player other = board.getPlayer(1);
        Space target = board.getSpace(2, 2);

        current.setSpace(board.getSpace(0, 0));
        other.setSpace(target);

        gameController.moveCurrentPlayerToSpace(target);

        Assertions.assertNotEquals(current, target.getPlayer(),
                "Current player should not move to an occupied space.");
        Assertions.assertEquals(other, target.getPlayer(),
                "Other player should remain in the occupied space.");
    }

    @Test
    void moveCurrentPlayerToOccupiedSpace() {
        Board board = gameController.board;

        Player player1 = board.getCurrentPlayer();
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(1, 1));

        Assertions.assertEquals(player1, board.getSpace(0, 0).getPlayer(),
                "Player1 should still be at (0,0) since (1,1) is occupied!");
        Assertions.assertEquals(player2, board.getSpace(1, 1).getPlayer(),
                "Player2 should still be at (1,1)!");
        Assertions.assertEquals(player1, board.getCurrentPlayer(),
                "Current player should remain player1 after a failed move!");
    }

    @Test
    void testMoveCurrentPlayerToSpaceSamePlayer() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Space space = board.getSpace(1, 1);
        current.setSpace(space);

        gameController.moveCurrentPlayerToSpace(space);

        Assertions.assertEquals(current, space.getPlayer(),
                "Player should remain in their own space.");
    }

    @Test
    void testMoveCurrentPlayerAdvancesCurrentPlayer() {
        Board board = gameController.board;
        Player first = board.getPlayer(0);
        Player second = board.getPlayer(1);
        board.setCurrentPlayer(first);
        first.setSpace(board.getSpace(0, 0));

        gameController.moveCurrentPlayerToSpace(board.getSpace(5, 0));

        Assertions.assertEquals(second, board.getCurrentPlayer(),
                "Current player should advance to next player after move.");
    }

    // --- startProgrammingPhase ---

    @Test
    void testStartProgrammingPhase() {
        Board board = gameController.board;
        gameController.startProgrammingPhase();

        Assertions.assertEquals(Phase.PROGRAMMING, board.getPhase(),
                "Phase should be PROGRAMMING after startProgrammingPhase.");
        Assertions.assertEquals(board.getPlayer(0), board.getCurrentPlayer(),
                "Current player should be player 0 after startProgrammingPhase.");
        Assertions.assertEquals(0, board.getStep(),
                "Step should be 0 after startProgrammingPhase.");
    }

    // --- finishProgrammingPhase ---

    @Test
    void testFinishProgrammingPhase() {
        Board board = gameController.board;
        gameController.startProgrammingPhase();
        gameController.finishProgrammingPhase();

        Assertions.assertEquals(Phase.ACTIVATION, board.getPhase(),
                "Phase should be ACTIVATION after finishProgrammingPhase.");
        Assertions.assertEquals(board.getPlayer(0), board.getCurrentPlayer(),
                "Current player should be player 0 after finishProgrammingPhase.");
    }

    // --- executePrograms ---

    @Test
    void testExecuteProgramsRunsAllSteps() {
        Board board = gameController.board;
        gameController.finishProgrammingPhase();

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                board.getPlayer(i).getProgramField(j).setCard(new CommandCard(Command.RIGHT));
            }
        }

        gameController.executePrograms();

        Assertions.assertEquals(Phase.PROGRAMMING, board.getPhase(),
                "Phase should return to PROGRAMMING after all registers are executed.");
        Assertions.assertFalse(board.isStepMode(),
                "Step mode should be false after executePrograms.");
    }

    @Test
    void testExecuteProgramsWhenFinished() {
        Board board = gameController.board;
        gameController.finishGame(board.getPlayer(0));

        Assertions.assertEquals(Phase.FINISHED, board.getPhase(),
                "Phase should remain FINISHED.");
    }

    // --- executeStep / executeNextStep ---

    @Test
    void testExecuteStep() {
        Board board = gameController.board;
        gameController.finishProgrammingPhase();

        Player first = board.getPlayer(0);
        first.getProgramField(0).setCard(new CommandCard(Command.RIGHT));

        gameController.executeStep();

        Assertions.assertTrue(board.isStepMode(),
                "Step mode should be true after executeStep.");
        Assertions.assertEquals(board.getPlayer(1), board.getCurrentPlayer(),
                "Current player should advance to player 1 after one step.");
    }

    @Test
    void testExecuteNextStepPausesOnInteractiveCard() {
        Board board = gameController.board;
        gameController.finishProgrammingPhase();

        Player first = board.getPlayer(0);
        first.getProgramField(0).setCard(new CommandCard(Command.LEFT_OR_RIGHT));

        gameController.executeStep();

        Assertions.assertEquals(Phase.PLAYER_INTERACTION, board.getPhase(),
                "Phase should be PLAYER_INTERACTION when an interactive card is encountered.");
    }

    @Test
    void testExecuteStepWithNullCard() {
        Board board = gameController.board;
        gameController.finishProgrammingPhase();

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            board.getPlayer(i).getProgramField(0).setCard(null);
        }

        gameController.executeStep();

        Assertions.assertEquals(board.getPlayer(1), board.getCurrentPlayer(),
                "Should advance to next player even with null card.");
    }

    // --- executeCommandOptionAndContinue ---

    @Test
    void testExecuteCommandOptionAndContinueForward() {
        Board board = gameController.board;
        gameController.finishProgrammingPhase();

        Player first = board.getPlayer(0);
        first.setSpace(board.getSpace(0, 0));
        first.setHeading(Heading.EAST);
        first.getProgramField(0).setCard(new CommandCard(Command.LEFT_OR_RIGHT));

        gameController.executeStep();
        Assertions.assertEquals(Phase.PLAYER_INTERACTION, board.getPhase());

        gameController.executeCommandOptionAndContinue(Command.RIGHT);

        Assertions.assertEquals(Heading.SOUTH, first.getHeading(),
                "Player should have turned right after choosing RIGHT option.");
    }

    @Test
    void testExecuteCommandOptionAndContinueAdvancesPlayer() {
        Board board = gameController.board;
        gameController.finishProgrammingPhase();

        Player first = board.getPlayer(0);
        first.getProgramField(0).setCard(new CommandCard(Command.LEFT_OR_RIGHT));

        gameController.executeStep();
        gameController.executeCommandOptionAndContinue(Command.LEFT);

        Assertions.assertEquals(board.getPlayer(1), board.getCurrentPlayer(),
                "Current player should advance after executeCommandOptionAndContinue.");
    }

    @Test
    void testExecuteCommandOptionAndContinueLastPlayer() {
        Board board = gameController.board;
        gameController.finishProgrammingPhase();

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            board.getPlayer(i).getProgramField(0).setCard(new CommandCard(Command.LEFT_OR_RIGHT));
        }

        for (int i = 0; i < board.getPlayersNumber() - 1; i++) {
            gameController.executeStep();
            gameController.executeCommandOptionAndContinue(Command.RIGHT);
        }

        gameController.executeStep();
        int stepBefore = board.getStep();
        gameController.executeCommandOptionAndContinue(Command.RIGHT);

        Assertions.assertTrue(board.getStep() > stepBefore || board.getPhase() == Phase.PROGRAMMING,
                "Step should advance or loop back to programming after last player resolves.");
    }

    @Test
    void testExecuteCommandOptionAndContinueInStepMode() {
        Board board = gameController.board;
        gameController.finishProgrammingPhase();
        board.setStepMode(true);

        Player first = board.getPlayer(0);
        first.getProgramField(0).setCard(new CommandCard(Command.LEFT_OR_RIGHT));

        gameController.executeStep();
        gameController.executeCommandOptionAndContinue(Command.LEFT);

        Assertions.assertTrue(board.isStepMode(),
                "Step mode should remain true after executeCommandOptionAndContinue in step mode.");
        Assertions.assertEquals(board.getPlayer(1), board.getCurrentPlayer(),
                "Should advance only one player in step mode.");
    }

    // --- executeFieldActions (via conveyor belt) ---

    @Test
    void testExecuteFieldActionsViaConveyorBelt() {
        Board board = gameController.board;
        gameController.finishProgrammingPhase();

        Player first = board.getPlayer(0);
        first.setSpace(board.getSpace(2, 0));
        first.setHeading(Heading.EAST);

        ConveyorBelt belt = new ConveyorBelt();
        belt.setHeading(Heading.EAST);
        board.getSpace(2, 0).getActions().add(belt);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            board.getPlayer(i).getProgramField(0).setCard(new CommandCard(Command.RIGHT));
        }

        gameController.executeStep();
        for (int i = 1; i < board.getPlayersNumber(); i++) {
            gameController.executeStep();
        }

        Assertions.assertEquals(first, board.getSpace(3, 0).getPlayer(),
                "Conveyor belt should have moved player east after field actions.");
    }

    // --- executeCommand (via cards) ---

    @Test
    void testExecuteCommandForwardCard() {
        Board board = gameController.board;
        gameController.finishProgrammingPhase();

        Player first = board.getPlayer(0);
        first.setSpace(board.getSpace(0, 0));
        first.setHeading(Heading.EAST);
        first.getProgramField(0).setCard(new CommandCard(Command.FORWARD));

        gameController.executeStep();

        Assertions.assertEquals(first, board.getSpace(1, 0).getPlayer(),
                "Player should move east after FORWARD card.");
    }

    @Test
    void testExecuteCommandFastForwardCard() {
        Board board = gameController.board;
        gameController.finishProgrammingPhase();

        Player first = board.getPlayer(0);
        first.setSpace(board.getSpace(0, 0));
        first.setHeading(Heading.EAST);
        first.getProgramField(0).setCard(new CommandCard(Command.FAST_FORWARD));

        gameController.executeStep();

        Assertions.assertEquals(first, board.getSpace(2, 0).getPlayer(),
                "Player should move two spaces east after FAST_FORWARD card.");
    }

    @Test
    void testExecuteCommandBackCard() {
        Board board = gameController.board;
        gameController.finishProgrammingPhase();

        Player first = board.getPlayer(0);
        first.setSpace(board.getSpace(3, 3));
        first.setHeading(Heading.EAST);
        first.getProgramField(0).setCard(new CommandCard(Command.BACK));

        gameController.executeStep();

        Assertions.assertEquals(first, board.getSpace(2, 3).getPlayer(),
                "Player should move west after BACK card.");
        Assertions.assertEquals(Heading.EAST, first.getHeading(),
                "Heading should remain EAST after BACK card.");
    }

    // --- canMove ---

    @Test
    void testCanMoveNullSpace() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(null);

        boolean result = gameController.canMove(current, Heading.NORTH);

        Assertions.assertFalse(result,
                "canMove should return false when player has no space.");
    }

    @Test
    void testCanMoveBlockedByEdge() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(0, 0));

        boolean result = gameController.canMove(current, Heading.NORTH);

        Assertions.assertFalse(result,
                "canMove should return false at board edge.");
    }

    @Test
    void testCanMoveBlockedByWall() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(2, 2));
        board.getSpace(2, 2).getWalls().add(Heading.NORTH);

        boolean result = gameController.canMove(current, Heading.NORTH);

        Assertions.assertFalse(result,
                "canMove should return false when wall blocks movement.");
    }

    // --- moveForward ---

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current, current.getHeading());

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(),
                "Player " + current.getName() + " should be at Space (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(),
                "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(),
                "Space (0,0) should be empty!");
    }

    @Test
    void testMoveForwardNullPlayer() {
        Assertions.assertDoesNotThrow(() -> gameController.moveForward(null, Heading.NORTH),
                "moveForward should handle null player gracefully.");
    }

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

    @Test
    void testPushChainBlockedByEdge() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Player other = board.getPlayer(2);

        current.setSpace(board.getSpace(0, board.height - 2));
        current.setHeading(Heading.SOUTH);
        other.setSpace(board.getSpace(0, board.height - 1));

        gameController.moveForward(current, Heading.SOUTH);

        Assertions.assertEquals(current, board.getSpace(0, board.height - 2).getPlayer(),
                "Current player should not move when push chain is blocked by edge!");
        Assertions.assertEquals(other, board.getSpace(0, board.height - 1).getPlayer(),
                "Other player should not be pushed off the board!");
    }

    // --- fastForward ---

    @Test
    void testFastForwardMovesTwoSpaces() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(0, 2));
        current.setHeading(Heading.EAST);

        gameController.fastForward(current, Heading.EAST);

        Assertions.assertEquals(current, board.getSpace(2, 2).getPlayer(),
                "Player should move two spaces east with fast forward.");
    }

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

    @Test
    void testFastForwardBlockedAfterOne() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Player other = board.getPlayer(1);

        other.setSpace(board.getSpace(board.width - 1, 2));
        current.setSpace(board.getSpace(board.width - 2, 2));
        current.setHeading(Heading.EAST);

        gameController.fastForward(current, Heading.EAST);

        Assertions.assertEquals(current, board.getSpace(board.width - 2, 2).getPlayer(),
                "Player should not move when fast forward is fully blocked.");
    }

    // --- turnRight ---

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

    // --- turnLeft ---

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

    // --- moveBack ---

    @Test
    void moveBack() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.setSpace(board.getSpace(0, 3));
        Heading originalHeading = current.getHeading();

        gameController.moveBack(current, current.getHeading());

        Assertions.assertEquals(current, board.getSpace(0, 2).getPlayer(),
                "Player should be one step back (northward) at (0,2)!");
        Assertions.assertEquals(originalHeading, current.getHeading(),
                "Heading should be restored to original after moveBack!");
    }

    @Test
    void testMoveBackRestoresHeading() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(4, 4));
        current.setHeading(Heading.EAST);

        gameController.moveBack(current, Heading.EAST);

        Assertions.assertEquals(Heading.EAST, current.getHeading(),
                "Heading should be restored to EAST after moveBack.");
        Assertions.assertEquals(current, board.getSpace(3, 4).getPlayer(),
                "Player should have moved one space west.");
    }

    @Test
    void testMoveBackPushesOtherPlayer() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Player other = board.getPlayer(1);

        current.setSpace(board.getSpace(3, 3));
        current.setHeading(Heading.SOUTH);
        other.setSpace(board.getSpace(3, 2));

        gameController.moveBack(current, current.getHeading());

        Assertions.assertEquals(current, board.getSpace(3, 2).getPlayer(),
                "Current player should move backwards into the occupied space.");
        Assertions.assertEquals(other, board.getSpace(3, 1).getPlayer(),
                "Other player should be pushed one step backwards.");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(),
                "Heading should remain unchanged after moveBack.");
    }

    @Test
    void testMoveBackBlockedByPushChain() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Player other = board.getPlayer(1);

        current.setSpace(board.getSpace(3, 1));
        current.setHeading(Heading.SOUTH);
        other.setSpace(board.getSpace(3, 0));

        gameController.moveBack(current, current.getHeading());

        Assertions.assertEquals(current, board.getSpace(3, 1).getPlayer(),
                "Current player should remain in place when backwards push is blocked.");
        Assertions.assertEquals(other, board.getSpace(3, 0).getPlayer(),
                "Other player should remain in place when the push chain is blocked.");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(),
                "Heading should remain unchanged after failed moveBack.");
    }

    // --- uturn ---

    @Test
    void uTurn() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        Heading originalHeading = current.getHeading();
        gameController.uturn(current);

        Assertions.assertEquals(originalHeading.next().next(), current.getHeading(),
                "Heading should be reversed (180°) after U-turn!");
        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(),
                "Player should not have moved after U-turn!");
    }

    // --- finishGame ---

    @Test
    void testFinishGame() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.finishGame(current);

        Assertions.assertEquals(Phase.FINISHED, board.getPhase(),
                "Phase should be FINISHED after finishGame.");
        Assertions.assertEquals(current, board.getCurrentPlayer(),
                "Winner should be set as current player.");
    }

    // --- Checkpoint ---

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

    // --- ConveyorBelt ---

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
}