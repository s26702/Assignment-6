// PlayersView.java

package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Phase;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class PlayersView extends BorderPane implements ViewObserver {

    private Board board;

    private TabPane tabPane;
    private PlayerView[] playerViews;

    private HBox buttonPanel;
    private Button finishButton;
    private Button executeButton;
    private Button stepButton;

    public PlayersView(GameController gameController) {
        board = gameController.board;

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        playerViews = new PlayerView[board.getPlayersNumber()];
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            playerViews[i] = new PlayerView(gameController, board.getPlayer(i));
            tabPane.getTabs().add(playerViews[i]);
        }

        finishButton = new Button("Finish Programming");
        finishButton.setOnAction(e -> gameController.finishProgrammingPhase());

        executeButton = new Button("Execute Program");
        executeButton.setOnAction(e -> gameController.executePrograms());

        stepButton = new Button("Execute Current Register");
        stepButton.setOnAction(e -> gameController.executeStep());

        buttonPanel = new HBox(finishButton, executeButton, stepButton);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setSpacing(10.0);

        this.setCenter(tabPane);
        this.setBottom(buttonPanel);

        board.attach(this);
        update(board);
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == board) {
            Player current = board.getCurrentPlayer();
            tabPane.getSelectionModel().select(board.getPlayerNumber(current));

            switch (board.getPhase()) {
                case INITIALISATION:
                    finishButton.setDisable(true);
                    executeButton.setDisable(false);
                    stepButton.setDisable(true);
                    break;

                case PROGRAMMING:
                    finishButton.setDisable(false);
                    executeButton.setDisable(true);
                    stepButton.setDisable(true);
                    break;

                case ACTIVATION:
                    finishButton.setDisable(true);
                    executeButton.setDisable(false);
                    stepButton.setDisable(false);
                    break;

                default:
                    finishButton.setDisable(true);
                    executeButton.setDisable(true);
                    stepButton.setDisable(true);
            }

            if (board.getPhase() == Phase.PLAYER_INTERACTION) {
                finishButton.setDisable(true);
                executeButton.setDisable(true);
                stepButton.setDisable(true);
            }
        }
    }
}