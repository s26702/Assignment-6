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

            if (name == null || name.equals("Simple")) {
                return createDefaultBoard();
            } else if (name.equals("Advanced")) {
                return createAdvancedBoard();
            } else {
                return createDefaultBoard();
            }
        }

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
        board.getSpace(3, 3).getActions().add(checkpoint1);

        Checkpoint checkpoint2 = new Checkpoint(2);
        board.getSpace(6, 6).getActions().add(checkpoint2);

        Checkpoint checkpoint3 = new Checkpoint(3);
        board.getSpace(9, 9).getActions().add(checkpoint3);

        return board;
    }


    // TODO A6b: add a method that returns a list (of type List<String>)
    //     of all available board names. The corresponding method
    //     createBoard(String name) must return a board for any of the
    //     names in this list. Make sure that the new method that you create
    //     here has a proper JavaDoc documentation.
    //

    public java.util.List<String> getBoardNames() {
        java.util.List<String> names = new java.util.ArrayList<>();
        names.add("Simple");
        names.add("Advanced");
        return names;
    }



}
