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

        // ── Conveyor corridor: row 2, cols 0–3 → EAST ──
        for (int col = 0; col < 4; col++) {
            ConveyorBelt belt = new ConveyorBelt();
            belt.setHeading(Heading.EAST);
            board.getSpace(col, 2).getActions().add(belt);
        }

        // ── Conveyor corridor: row 5, cols 4–7 → WEST ──
        for (int col = 4; col < 8; col++) {
            ConveyorBelt belt = new ConveyorBelt();
            belt.setHeading(Heading.WEST);
            board.getSpace(col, 5).getActions().add(belt);
        }

        // ── Conveyor corridor: col 2, rows 3–5 → NORTH ──
        for (int row = 3; row <= 5; row++) {
            ConveyorBelt belt = new ConveyorBelt();
            belt.setHeading(Heading.NORTH);
            board.getSpace(2, row).getActions().add(belt);
        }

        // ── Fast conveyor: col 5, rows 2–4 → SOUTH ──
        for (int row = 2; row <= 4; row++) {
            ConveyorBelt belt = new ConveyorBelt();
            belt.setHeading(Heading.SOUTH);
            board.getSpace(5, row).getActions().add(belt);
        }

        // ── Walls that enclose the east belt corridor (row 2) ──
        board.getSpace(0, 2).getWalls().add(Heading.NORTH); // entry north lip
        board.getSpace(3, 2).getWalls().add(Heading.SOUTH); // exit south lip

        // ── Walls guarding the north belt corridor (col 2) ──
        board.getSpace(2, 3).getWalls().add(Heading.EAST);  // right guard
        board.getSpace(3, 3).getWalls().add(Heading.NORTH); // top of blocked cell

        // ── Walls guarding the west belt corridor (row 5) ──
        board.getSpace(4, 5).getWalls().add(Heading.WEST);  // entry guard
        board.getSpace(5, 5).getWalls().add(Heading.SOUTH); // original wall
        board.getSpace(7, 5).getWalls().add(Heading.NORTH); // far end lip

        // ── Walls for (1,1) ──
        board.getSpace(1, 1).getWalls().add(Heading.SOUTH);
        board.getSpace(1, 0).getWalls().add(Heading.NORTH);
        board.getSpace(2, 1).getWalls().add(Heading.WEST);

        // ── Extra scatter walls ──
        board.getSpace(1, 3).getWalls().add(Heading.SOUTH);
        board.getSpace(6, 1).getWalls().add(Heading.EAST);
        board.getSpace(3, 4).getWalls().add(Heading.NORTH);
        board.getSpace(4, 6).getWalls().add(Heading.SOUTH);
        board.getSpace(6, 4).getWalls().add(Heading.SOUTH);

        // ── Checkpoints ──
        board.getSpace(3, 1).getActions().add(new Checkpoint(1));
        board.getSpace(6, 3).getActions().add(new Checkpoint(2));
        board.getSpace(1, 6).getActions().add(new Checkpoint(3));

        return board;
    }


    /**
     * Creates a more complex 10x10 board named "Advanced".
     * This board contains more advanced features such as additional walls
     * conveyor belts facing different directions to increase difficulty.
     * and three different checkpoints for the players
     *
     * @return an advanced Board configuration
     * @author Mikkel Hjelm
     */
    private Board createAdvancedBoard() {
        Board board = new Board(10, 10, "Advanced");

        Space s1 = board.getSpace(2, 2);
        s1.getWalls().add(Heading.NORTH);
        s1.getWalls().add(Heading.WEST);

        Space s2 = board.getSpace(5, 3);
        s2.getWalls().add(Heading.NORTH);
        s2.getWalls().add(Heading.WEST);

        Space s3 = board.getSpace(8,6);
        s3.getWalls().add(Heading.NORTH);
        s3.getWalls().add(Heading.SOUTH);

        Space s4 = board.getSpace(3,9);
        s4.getWalls().add(Heading.EAST);
        s4.getWalls().add(Heading.WEST);

        Space s5 = board.getSpace(3,9);
        s5.getWalls().add(Heading.EAST);
        s5.getWalls().add(Heading.SOUTH);

        ConveyorBelt belt1 = new ConveyorBelt();
        belt1.setHeading(Heading.EAST);
        board.getSpace(1, 1).getActions().add(belt1);

        ConveyorBelt belt2 = new ConveyorBelt();
        belt2.setHeading(Heading.SOUTH);
        board.getSpace(4, 1).getActions().add(belt2);

        ConveyorBelt belt3 = new ConveyorBelt();
        belt3.setHeading(Heading.WEST);
        board.getSpace(7, 4).getActions().add(belt3);

        ConveyorBelt belt4 = new ConveyorBelt();
        belt4.setHeading(Heading.SOUTH);
        board.getSpace(8, 8).getActions().add(belt4);

        ConveyorBelt belt5 = new ConveyorBelt();
        belt5.setHeading(Heading.EAST);
        board.getSpace(3, 6).getActions().add(belt5);

        Checkpoint checkpoint1 = new Checkpoint(1);
        board.getSpace(4, 4).getActions().add(checkpoint1);

        Checkpoint checkpoint2 = new Checkpoint(2);
        board.getSpace(6, 6).getActions().add(checkpoint2);

        Checkpoint checkpoint3 = new Checkpoint(3);
        checkpoint3.setLastCheckPoint(true);
        board.getSpace(5, 9).getActions().add(checkpoint3);

        Checkpoint checkpoint4 = new Checkpoint(4);
        checkpoint4.setLastCheckPoint(true);
        board.getSpace(8, 7).getActions().add(checkpoint4);

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
