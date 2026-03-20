package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

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

    /**
     * Creates a new board of given name of a board, which indicates
     * which type of board should be created. For now the name is ignored.
     *
     * @param name the given name board
     * @return the new board corresponding to that name
     */
    public Board createBoard(String name) {
        // TODO A6b: Implement this method properly as described in Assignment 6b.
        //     Dependent on the provided name, create a board accordingly and
        //     return it. In case the name is null, some default board should
        //     be returned (defensive programming).

            if (name == null || name.equals("Default")) {
                return createDefaultBoard();
            } else if (name.equals("Advanced")) {
                return createAdvancedBoard();
            } else {
                return createDefaultBoard();
            }
        }

        private Board createDefaultBoard() {
            Board board = new Board(8, 8, "Default");

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
    private Board createAdvancedBoard() {
        Board board = new Board(10, 10, "Advanced");

        board.getSpace(2, 2).getWalls().add(Heading.NORTH);
        board.getSpace(2, 2).getWalls().add(Heading.WEST);

        ConveyorBelt belt = new ConveyorBelt();
        belt.setHeading(Heading.EAST);
        board.getSpace(1, 1).getActions().add(belt);

        return board;
    }
}

    // TODO A6b: add a method that returns a list (of type List<String>)
    //     of all available board names. The corresponding method
    //     createBoard(String name) must return a board for any of the
    //     names in this list. Make sure that the new method that you create
    //     here has a proper JavaDoc documentation.
    //






}
