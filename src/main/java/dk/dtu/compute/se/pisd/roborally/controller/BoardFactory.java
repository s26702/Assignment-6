package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * A factory for creating boards. The factory itself is implemented as a singleton.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
// XXX A3: might be used for creating a first slightly more interesting board.
public class BoardFactory {

    /**
     * The single instance of this class, which is lazily instantiated on demand.
     */
    static private BoardFactory instance = null;

    /**
     * Constructor for BoardFactory. It is private in order to make the factory a singleton.
     */
    private BoardFactory() {
    }

    /**
     * Returns the single instance of this factory. The instance is lazily
     * instantiated when requested for the first time.
     *
     * @return the single instance of the BoardFactory
     */
    public static BoardFactory getInstance() {
        if (instance == null) {
            instance = new BoardFactory();
        }
        return instance;
    }


    /** The default board name used as a fallback when no valid name is provided. */
    private static final String DEFAULT_NAME = "<none>";

    /** The name identifier for the simple 8x8 board. */
    private static final String SIMPLE_BOARD_NAME = "Simple";

    /** The name identifier for the advanced 10x10 board. */
    private static final String ADVANCED_BOARD_NAME = "Advanced";


    /**
     * * This method implements a factory pattern to return different board
     *   configurations. If the name is unknown or null, a default "Simple"
     *   board is returned to ensure the game can always start (defensive programming).
     * @param name the name of the board to be created
     * @return the new board corresponding to the given name
     */
    public Board createBoard(String name) {
        if (name == null || name.equals(DEFAULT_NAME)) {
            return createDefaultBoard();
        } else if (name.equals(SIMPLE_BOARD_NAME)) {
            return createDefaultBoard();
        } else if (name.equals(ADVANCED_BOARD_NAME)) {
            return createAdvancedBoard();
        } else {
            return createDefaultBoard();
        }
    }


    /**
     * Creates a basic 8x8 board named "Simple" with a few walls and conveyor belts.
     *
     * @return a simple Board configuration
     */
    private Board createDefaultBoard() {
        Board board = new Board(8, 8, "Simple");

        // add some walls, actions and checkpoints to some spaces
        Space space = board.getSpace(0,0);
        space.getWalls().add(Heading.SOUTH);
        ConveyorBelt action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(1,0);
        space.getWalls().add(Heading.NORTH);
        action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(1,1);
        space.getWalls().add(Heading.WEST);
        action  = new ConveyorBelt();
        action.setHeading(Heading.NORTH);
        space.getActions().add(action);

        space = board.getSpace(5,5);
        space.getWalls().add(Heading.SOUTH);
        action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(6,5);
        action  = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        return board;
    }


    /**
     * Creates a more complex 10x10 board named "Advanced".
     *
     * This board contains more advanced features such as additional walls
     * conveyor belts facing different directions to increase difficulty.
     * and three different checkpoints for the players
     *
     * @return an advanced Board configuration
     */
    private Board createAdvancedBoard() {
        Board board = new Board(10, 10, "Advanced");

        Space s1 = board.getSpace(2, 2);
        s1.getWalls().add(Heading.NORTH);
        s1.getWalls().add(Heading.WEST);

        ConveyorBelt belt1 = new ConveyorBelt();
        belt1.setHeading(Heading.EAST);
        board.getSpace(1, 1).getActions().add(belt1);

        ConveyorBelt belt2 = new ConveyorBelt();
        belt2.setHeading(Heading.SOUTH);
        board.getSpace(4, 1).getActions().add(belt2);

        Checkpoint checkpoint1 = new Checkpoint(1);
        board.getSpace(4, 4).getActions().add(checkpoint1);

        Checkpoint checkpoint2 = new Checkpoint(2);
        board.getSpace(6, 6).getActions().add(checkpoint2);

        Checkpoint checkpoint3 = new Checkpoint(3);
        checkpoint3.setLastCheckPoint(true);
        board.getSpace(9, 9).getActions().add(checkpoint3);

        return board;
    }


    /**
     * Returns a list of all available board names that this factory can create.
     * This method is used by the AppController to populate the board selection
     * dialog, allowing the user to choose between different game arenas.
     *
     * @return a List of Strings containing all available board names
     * @author Christoffer Sørensen
     */
    public List<String> getBoardNames() {
        List<String> underlyingList = new ArrayList<>();
        underlyingList.add(SIMPLE_BOARD_NAME);
        underlyingList.add(ADVANCED_BOARD_NAME);
        return Collections.unmodifiableList(underlyingList);
    }



}
