package gui;

import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;

import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import javafx.stage.FileChooser;

import logic.GameLogic;
import logic.board.Position;

/**
 * The Gui Controller
 *
 * @author Guillaume Fournier-Mayer (tinf 101922)
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private BorderPane root;
    @FXML
    private ImageView img_board;
    @FXML
    private AnchorPane anchor_board;
    @FXML
    private ImageView img_bus;
    @FXML
    private AnchorPane anchor_bus;
    @FXML
    private ImageView img_cab;
    @FXML
    private AnchorPane anchor_cab;
    @FXML
    private ImageView img_underground;
    @FXML
    private AnchorPane anchor_underground;
    @FXML
    private ImageView img_black;
    @FXML
    private AnchorPane anchor_black;
    @FXML
    private Label label_cab;
    @FXML
    private Label label_bus;
    @FXML
    private Label label_tube;
    @FXML
    private Label label_boat;
    @FXML
    private Label label_playername;
    @FXML
    private AnchorPane anchor_playername;
    @FXML
    private RadioMenuItem menu_god_mode;
    @FXML
    private GridPane grid_logbook;

    private Group players;
    private GameLogic logic;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.initBoard();
        this.initPlayerStats();
        this.clearLogbook(grid_logbook);

    }

    /**
     * Inits the game board.
     */
    private void initBoard() {
        this.players = new Group();
        this.anchor_board.getChildren().add(players);
        img_board.setPreserveRatio(false);
        img_board.setImage(new Image("gui/img/Spielplan.png"));
        img_board.fitWidthProperty().bind(anchor_board.widthProperty());
        img_board.fitHeightProperty().bind(anchor_board.heightProperty());
    }

    /**
     * Inits the player stats.
     */
    private void initPlayerStats() {
        // CAB
        img_cab.setPreserveRatio(false);
        img_cab.setImage(new Image("gui/img/Taxi.png"));
        img_cab.fitWidthProperty().bind(anchor_cab.widthProperty());
        img_cab.fitHeightProperty().bind(anchor_cab.heightProperty());

        // Bus
        img_bus.setPreserveRatio(false);
        img_bus.setImage(new Image("gui/img/Bus.png"));
        img_bus.fitWidthProperty().bind(anchor_bus.widthProperty());
        img_bus.fitHeightProperty().bind(anchor_bus.heightProperty());

        // Underground
        img_underground.setPreserveRatio(false);
        img_underground.setImage(new Image("gui/img/Underground.png"));
        img_underground.fitWidthProperty().bind(anchor_underground.widthProperty());
        img_underground.fitHeightProperty().bind(anchor_underground.heightProperty());

        // Black
        img_black.setPreserveRatio(false);
        img_black.setImage(new Image("gui/img/BlackTicket.png"));
        img_black.fitWidthProperty().bind(anchor_black.widthProperty());
        img_black.fitHeightProperty().bind(anchor_black.heightProperty());

        this.label_playername.setText("-");

    }

    // HANDLER #################################################################
    /**
     * Handles a click on the board. Is used to chose a station.
     *
     * @param event The event
     */
    @FXML
    private void handleMouseClickOnBoard(MouseEvent event) {
        if (logic != null) {
            double xNorm = event.getX() / this.anchor_board.getWidth();
            double yNorm = event.getY() / this.anchor_board.getHeight();
            this.logic.handleHumanPlaying(new Position(xNorm, yNorm));
        }
    }

    /**
     * Handles a clock on the "New Game" button. Opens a dialog if a game is currently running.
     * Creates a new Game.
     *
     * @param event The event
     */
    @FXML
    private void handleMenuNew(ActionEvent event) {
        if (this.logic != null) {
            Optional<ButtonType> result =
                    this.showConfirmation("A Game is currently running!", String.format(
                            "Creating a new Game will delete all current progress.\nProceed anyway?"));
            if (result.get() == ButtonType.OK) {
                this.newGame();
            }
        } else {
            this.newGame();
        }
    }

    /**
     * Handles a clock on the "Exit" button. Opens a dialog if a game is currently running. Exits
     * the Game.
     *
     * @param event The event
     */
    @FXML
    private void handleMenuClose(ActionEvent event) {
        if (this.logic != null) {
            Optional<ButtonType> result =
                    this.showConfirmation("A Game is currently running!", String.format(
                            "Exiting now will delete all current progress.\nProceed anyway?"));
            if (result.get() == ButtonType.OK) {
                Platform.exit();
            }
        } else {
            Platform.exit();
        }

    }

    /**
     * Handles a click on the "Save" button. Opens a filechooser dialog and saves the game into the
     * chosen file.
     *
     * @param event The event
     */
    @FXML
    private void handleMenuSave(ActionEvent event) {
        if (this.logic == null) {
            this.showInfo("No Game to save.", "You have to Start a new Game before saving.");
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Game");
            fileChooser.getExtensionFilters()
                    .add((new FileChooser.ExtensionFilter("Scotland Yard Savegame", "*.sy")));
            File fileToSave = fileChooser.showSaveDialog(root.getScene().getWindow());
            if (fileToSave != null) {
                try {
                    FileWriter writer = new FileWriter(fileToSave);
                    this.logic.save(writer);
                    this.showInfo("Game has been saved successfully", "");

                } catch (IOException ex) {
                    this.showError("Error while saving Game", ex.getMessage());
                }
            }
        }
    }

    /**
     * Handles a clock on the "Load" button. Opens a dialog if a game is currently running. Opens a
     * filechooser dialog. Loads the game from the chosen file.
     *
     * @param event The event
     */
    @FXML
    private void handleMenuLoad(ActionEvent event) {

        if (this.logic != null) {
            Optional<ButtonType> result =
                    this.showConfirmation("A Game is currently running!", String.format(
                            "Loading a new Game will delete all current progress.\nProceed anyway?"));
            if (result.get() == ButtonType.OK) {
                this.loadGame();
            }
        } else {
            this.loadGame();
        }
    }

    /**
     * Handles a click on the "Godmode" button. Toggles the godmode.
     *
     * @param event The event
     */
    @FXML
    private void handleGodMode(ActionEvent event) {
        if (this.logic != null) {
            this.logic.forceRedraw();
        }
    }

    // #########################################################################
    /**
     * Opens a dialog to get the initial game settings and starts the game
     */
    private void newGame() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("New Game");
        dialog.setHeaderText("Game Settings");

        ButtonType startButton = new ButtonType("Start", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(startButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ChoiceBox choice = new ChoiceBox(FXCollections.observableArrayList("3", "4", "5"));
        choice.setValue("3");

        CheckBox misterXAi = new CheckBox();
        CheckBox detectivesAi = new CheckBox();
        detectivesAi.setSelected(true);

        grid.add(new Label("Should MisterX be controlled by AI:"), 0, 0);
        grid.add(misterXAi, 1, 0);
        grid.add(new Label("How may Detectives:"), 0, 1);
        grid.add(choice, 1, 1);
        grid.add(new Label("Detectives AI?:"), 0, 2);
        grid.add(detectivesAi, 1, 2);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            return dialogButton.equals(startButton);
        });

        if (dialog.showAndWait().get()) {
            int numDetectives = Integer.parseInt(choice.getValue().toString());
            InputStream networkStream = getClass().getResourceAsStream("/logic/board/network.json");
            if (networkStream == null) {
                showError("Error while Starting Game", "Network could not be found");
            } else {
                if (this.logic != null) {
                    this.reset();
                }
                Reader reader = new InputStreamReader(networkStream);
                JavaFXGui gui = new JavaFXGui(this.anchor_board, this.grid_logbook,
                        this.menu_god_mode, this.players, this.label_playername, this.label_cab,
                        this.label_bus, this.label_tube, this.label_boat);
                try {

                    this.logic = new GameLogic(reader, gui, misterXAi.isSelected(), numDetectives,
                            detectivesAi.isSelected());
                    this.logic.start();
                } catch (JsonSyntaxException | IllegalArgumentException ex) {
                    this.showError("Error while Starting Game", ex.getMessage());
                    this.reset();
                }

            }

        }
    }

    /**
     * Opens a filechooser and tries to load a game from a Json file.
     */
    private void loadGame() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Game");
        fileChooser.getExtensionFilters()
                .add((new FileChooser.ExtensionFilter("Scotland Yard Savegame", "*.sy")));
        File fileToLoad = fileChooser.showOpenDialog(root.getScene().getWindow());

        if (fileToLoad != null) {
            InputStream networkStream = getClass().getResourceAsStream("/logic/board/network.json");
            if (networkStream == null) {
                showError("Error while starting game", "Network could not be found");
            } else {
                // If game ist already launched -> Reset
                if (this.logic != null) {
                    this.reset();
                }
                try {
                    Reader saveReader = new FileReader(fileToLoad);
                    Reader networkReader = new InputStreamReader(networkStream);
                    // Create GUI
                    JavaFXGui gui = new JavaFXGui(this.anchor_board, this.grid_logbook,
                            this.menu_god_mode, this.players, this.label_playername, this.label_cab,
                            this.label_bus, this.label_tube, this.label_boat);
                    // Create logic
                    this.logic = new GameLogic(saveReader, networkReader, gui);
                    // Start the game
                    this.logic.start();
                } catch (JsonSyntaxException | IOException | IllegalArgumentException ex) {
                    this.showError("Error while loading Game", ex.getMessage());
                    this.reset();
                }
            }
        }
    }

    /**
     * Resets the whole game. The graphical interface will be ready for a new game afterwards.
     */
    private void reset() {
        if (this.logic != null) {
            this.logic.stop();
        }
        this.menu_god_mode.setSelected(false);
        this.clearPlayers();
        this.clearLogbook(grid_logbook);

    }

    /**
     * Removes all players from current game board.
     */
    private void clearPlayers() {
        this.players.getChildren().clear();
    }

    /**
     * Removes all tickets from the logbook.
     *
     * @param logbook The logbook
     */
    private void clearLogbook(GridPane logbook) {
        logbook.getChildren().stream().filter(node -> node instanceof AnchorPane)
                .map(node -> (AnchorPane) node).forEach(pane -> {
                    Iterator<Node> iter = pane.getChildren().iterator();
                    while (iter.hasNext()) {
                        Node node = iter.next();
                        if (node instanceof ImageView) {
                            iter.remove();
                        }
                    }
                });
    }

    // DIALOGS #################################################################
    /**
     * Shows an Error dialog.
     *
     * @param header The header of the dialog
     * @param content The content of the dialog
     */
    private void showError(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("An Error Occured");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    /**
     * Shows an info dialog.
     *
     * @param header The header of the dialog
     * @param content The content of the dialog
     */
    private void showInfo(String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    /**
     * Shows an confirmation dialog.
     *
     * @param header The header of the dialog
     * @param content The content of the dialog
     */
    private Optional<ButtonType> showConfirmation(String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        return alert.showAndWait();
    }

}
