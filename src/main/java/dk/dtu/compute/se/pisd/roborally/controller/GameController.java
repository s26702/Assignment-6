/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

/**
 *
 * This controller represents the main game logic of RoboRally.
 * It manages the current state of the game and executes the rules
 * for player movement, command cards, phases, and board actions.
 *
 * The GameController coordinates the interaction between the board
 * and the players during the execution of a game.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        Player p = board.getCurrentPlayer();
        int Pnum = board.getPlayerNumber(p);
        int size = board.getPlayersNumber();
        for (int i = 0; i < size; i++) {
            Player other = board.getPlayer(i);
            if (other.getSpace().equals(space)) {
                if (other.equals(p)) {
                    System.out.println("Space already occupied by that player");
                } else {
                    System.out.println("Space occupied by another player");
                }
                return;
            }
        }
        p.setSpace(space);
        board.IncMovecounter();
        Pnum++;
        board.setCurrentPlayer(board.getPlayer(Pnum % size));
    }

    // XXX A6c
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX A6c
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX A6c
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX A6c
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX A6c
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX A6c
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX A6c
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX A6c
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    // TODO A6e: implement the execution af an interactive card to
    //     this method (e.g. by switching to the PLAYER_INTERACTION phase
    //     at the right point)
    private void executeNextStep() {
        if (board.getPhase() == Phase.FINISHED) {
            return;
        }
       Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (command.isInteractive()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return; // pause — wait for player to choose
                    }
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    executeFieldActions();
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * Executes the chosen option of an interactive command card and resumes
     * the normal execution flow. This method is called when a player selects
     * their desired action during the {@link Phase#PLAYER_INTERACTION} phase.
     * After executing the chosen command, the game advances to the next player
     * or executes field actions if all players have completed the current
     * register. If the game is not in step mode, the execution loop resumes
     * automatically.
     *
     * @param option the {@link Command} option chosen by the current player
     */
    public void executeCommandOptionAndContinue(@NotNull Command option) {
        Player currentPlayer = board.getCurrentPlayer();
        board.setPhase(Phase.ACTIVATION); // resume normal flow
        executeCommand(currentPlayer, option);

        // Now advance to next player (same logic as in executeNextStep)
        int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
        if (nextPlayerNumber < board.getPlayersNumber()) {
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        } else {
            executeFieldActions();
            int step = board.getStep() + 1;
            if (step < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(step);
                board.setStep(step);
                board.setCurrentPlayer(board.getPlayer(0));
            } else {
                startProgrammingPhase();
            }
        }

        if (!board.isStepMode()) {
            continuePrograms();
        }
    }

    /**
     * Executes all field actions for all players after one register
     * has been completed
     * @author Mikkel Hjelm
     */
    private void executeFieldActions() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            Space space = player.getSpace();

            if (space != null) {
                for (FieldAction action : space.getActions()) {
                    action.doAction(this, space);
                }
            }
        }
    }

    // XXX A6c
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    if (canMove(player, player.getHeading())) {
                        moveForward(player, player.getHeading());
                    }
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    for (int i = 0; i < 2; i++) {
                        if (canMove(player, player.getHeading())) {
                            moveForward(player, player.getHeading());
                        } else {
                            break;
                        }
                    }
                    break;
                case BACK:
                    this.moveBack(player, player.getHeading());
                    break;
                case UTURN:
                    this.uturn(player);
                    break;

                default:
                    // DO NOTHING (for now)//
            }
        }
    }

    /**
     * Checks whether the given player can move one step in the specified heading.
     * If the target space is occupied by another player, the method recursively
     * checks wheter that player can also be moved in the same direction.
     * @param player the player that attempts to move
     * @param heading the direction of the movement
     * @return true if the player can move.
     * @author Mikkel Hjelm
     */
    private boolean canMove(@NotNull Player player, Heading heading) {
        if(player.getSpace() == null || heading == null) {
            return false;
        }

        Space next = board.getNeighbour(player.getSpace(), heading);

        if (next == null || board.getNieghborwall(player.getSpace(), heading)) {
            return false;
        }

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player other = board.getPlayer(i);
            if (other.equals(player)) {
                continue;
            }
            if (next.equals(other.getSpace())) {
                return canMove(other, heading);
            }
        }
        return true;
    }

    /**
     * Moves a player one space forward in the given heading.
     * If another player occupies the target space, that player is moved first
     * in the same direction, creating a push chain.
     * @param player the player to move
     * @param heading the direction of movement
     * @author Mikkel Hjelm
     */
    public void moveForward(Player player, Heading heading) {
        if(player == null || player.getSpace() == null || heading == null) {
            return;
        }
        Space next = board.getNeighbour(player.getSpace(), heading);
        if(next == null || board.getNieghborwall(player.getSpace(), heading)) {
            return;
        }

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player other = board.getPlayer(i);
            if (other == null ||other == player) {
                continue;
            }

            if (next.equals(other.getSpace())) {
                if (!canMove(other, heading)) return;
                moveForward(other, heading);
            }
        }
        player.setSpace(next);
    }


    public void fastForward(@NotNull Player player, Heading heading) {
        for (int i = 0; i < 2; i++) {
            if (canMove(player, heading)) {
                moveForward(player, heading);
            } else {
                break;
            }
        }
    }

    public void turnRight(@NotNull Player player) {
        player.setHeading(player.getHeading().next());
    }

    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());
    }

    public void moveBack(@NotNull Player player, Heading heading) {
        Heading NewHeading = heading.next().next();
        if(!canMove(player, NewHeading)) return;

        uturn(player);
        moveForward(player, player.getHeading());
        uturn(player);
    }

    public void uturn(@NotNull Player player) {
        player.setHeading(player.getHeading().next());
        player.setHeading(player.getHeading().next());

    }

    public void finishGame(@NotNull Player player) {
        board.setCurrentPlayer(player);
        board.setPhase(Phase.FINISHED);
    }

}
