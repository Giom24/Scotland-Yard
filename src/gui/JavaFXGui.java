package gui;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import logic.GUIConnector;
import logic.Ticket;
import logic.board.Station;
import logic.player.Player;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import logic.GameLogic;
import logic.GameLogic.Config;

/**
 * API between Logic and GUI
 *
 * @author Guillaume Fournier-Mayer (tinf 101922)
 */
public class JavaFXGui implements GUIConnector {

    private final Group players;
    private final AnchorPane board;
    private final Label labelCab;
    private final Label labelBus;
    private final Label labelTube;
    private final Label labelBoat;
    private final Label playerName;
    private final GridPane logbook;
    private final RadioMenuItem godMode;

    private static final String TICKET_CAB_NAME = "Cab";
    private static final String TICKET_BUS_NAME = "Bus";
    private static final String TICKET_TUBE_NAME = "Tube";
    private static final String TICKET_BLACK_NAME = "Black";

    public JavaFXGui(AnchorPane board, GridPane logbook, RadioMenuItem godMode, Group players,
            Label playerName, Label labelCab, Label labelBus, Label labelTube, Label labelBoat) {
        this.board = board;
        this.logbook = logbook;
        this.players = players;
        this.playerName = playerName;
        this.labelCab = labelCab;
        this.labelBus = labelBus;
        this.labelTube = labelTube;
        this.labelBoat = labelBoat;
        this.godMode = godMode;
    }

    @Override
    public void drawPlayers(List<Player> players, Player turn, Station lastSeen) {

        this.players.getChildren().clear();
        players.forEach(player -> {

            Station station = player.getCurrentStation();
            double x = station.getPosition().getX();
            double y = station.getPosition().getY();
            Circle circle = new Circle(x, y, Config.PLAYER_SIZE);

            if (player.isMisterX()) {
                ImagePattern imagePattern = new ImagePattern(new Image("gui/img/mrx.png"));
                circle.setFill(imagePattern);
                if (!player.isAi() && turn.isMisterX() || godMode.isSelected()) {
                    circle.setCenterX(y);
                    circle.setCenterX(y);
                    circle.centerXProperty().bind(this.board.widthProperty().multiply(x));
                    circle.centerYProperty().bind(this.board.heightProperty().multiply(y));
                    this.players.getChildren().add(circle);
                } else if (lastSeen != null) {
                    x = lastSeen.getPosition().getX();
                    y = lastSeen.getPosition().getY();
                    circle.setCenterX(y);
                    circle.setCenterX(y);
                    circle.centerXProperty().bind(this.board.widthProperty().multiply(x));
                    circle.centerYProperty().bind(this.board.heightProperty().multiply(y));
                    this.players.getChildren().add(circle);
                }

            } else {
                switch (player.getId()) {
                    case 1: {
                        circle.setFill(new ImagePattern(new Image("gui/img/blue.png")));
                        break;
                    }
                    case 2: {
                        circle.setFill(new ImagePattern(new Image("gui/img/yellow.png")));
                        break;
                    }
                    case 3: {
                        circle.setFill(new ImagePattern(new Image("gui/img/red.png")));
                        break;
                    }
                    case 4: {
                        circle.setFill(new ImagePattern(new Image("gui/img/green.png")));
                        break;
                    }
                    case 5: {
                        circle.setFill(new ImagePattern(new Image("gui/img/black.png")));
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException();
                    }
                }
                circle.centerXProperty().bind(this.board.widthProperty().multiply(x));
                circle.centerYProperty().bind(this.board.heightProperty().multiply(y));
                this.players.getChildren().add(circle);
            }

        });
    }

    @Override
    public void showPlayersStates(Player player) {

        if (player.isMisterX()) {
            this.playerName.setText("MisterX");
        } else {
            this.playerName.setText("Detective " + player.getId());
        }

        for (Ticket ticket : Ticket.values()) {
            int num = player.getTicketNum(ticket);
            switch (ticket) {
                case CAB: {
                    this.labelCab.setText(String.valueOf(num));
                    break;
                }
                case BUS: {
                    this.labelBus.setText(String.valueOf(num));
                    break;
                }
                case TUBE: {
                    this.labelTube.setText(String.valueOf(num));
                    break;
                }
                case BLACK: {
                    this.labelBoat.setText(String.valueOf(num));
                    break;
                }
            }
        }
    }

    @Override
    public Ticket showChooseTicketDialog(Station station, Set<Ticket> tickets) {

        Dialog<Ticket> dialog = new Dialog<>();
        // dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        dialog.setTitle("Choose a Ticket");
        dialog.setHeaderText(String.format(
                "The chosen Station \"%d\" is reachable by %d modes of Transport.\nChoose one please.",
                station.getIdentifier(), tickets.size()));

        ButtonType loginButtonType = new ButtonType("Choose", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        List<String> ticketNames = this.getTicketNames(tickets);
        ChoiceBox choice = new ChoiceBox(FXCollections.observableArrayList(ticketNames));
        choice.setValue(ticketNames.stream().findFirst().get());
        grid.add(choice, 1, 1);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            return this.getTicketFromString(choice.getValue().toString());
        });

        return dialog.showAndWait().get();
    }

    @Override
    public void showNotEnoughTicketsForMoveDialog(Station station) {
        this.showDialog(AlertType.INFORMATION, "Not enough Tickets left", "Invalid move!",
                String.format(
                        "You have not enough tickets left to reach station \"%d\".\nChoose another one.",
                        station.getIdentifier()));
    }

    @Override
    public void showStationNotReachableInMove(Station station) {
        this.showDialog(AlertType.INFORMATION, "Station not reachable", "Invalid move!",
                String.format("You can not reach station \"%d\".\nChoose another one.",
                        station.getIdentifier()));
    }

    @Override
    public void showError(String title, String header, String context) {
        this.showDialog(AlertType.ERROR, title, header, context);
    }

    @Override
    public void showWinner(GameLogic.WinState state) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setTitle("Station not reachable");

        String reason = "";

        switch (state) {
            case MISTERX_SURROUNDED: {
                reason = "Detectives have won the Game! MisterX is surrounded.";
                break;
            }

            case MISTERX_CATCHED: {
                reason = "Detectives have won the Game! MisterX has been catched.";
                break;
            }
            case DETECTIVES_BLOCKED: {
                reason = "MisterX has won the Game! All Detectives are blocked.";
                break;
            }
            case MISTERX_WIN: {
                reason = "MisterX has won the Game! Last round reached.";
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }

        }

        alert.setContentText(reason);
        Platform.runLater(alert::showAndWait);
    }

    @Override
    public void setLogbookEntry(int round, Ticket ticket) {
        round--;
        int column = round / this.logbook.getRowConstraints().size();
        int row = round % this.logbook.getRowConstraints().size();

        this.logbook.getChildren().forEach(node -> {
            int currentRow = this.convertGridIndex(GridPane.getRowIndex(node));
            int currentColumn = this.convertGridIndex(GridPane.getColumnIndex(node));
            if (currentRow == row && currentColumn == column && node instanceof AnchorPane) {
                AnchorPane pane = (AnchorPane) node;
                ImageView ticketImage = new ImageView(this.getImageNameFromTicket(ticket));
                ticketImage.setPreserveRatio(false);
                ticketImage.fitWidthProperty().bind(pane.widthProperty());
                ticketImage.fitHeightProperty().bind(pane.heightProperty());
                pane.getChildren().add(ticketImage);
            }
        });
    }

    private String getImageNameFromTicket(Ticket ticket) {
        switch (ticket) {
            case CAB: {
                return "gui/img/Taxi.png";
            }
            case BUS: {
                return "gui/img/Bus.png";
            }
            case TUBE: {
                return "gui/img/Underground.png";
            }
            case BLACK: {
                return "gui/img/BlackTicket.png";
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    private int convertGridIndex(Integer index) {
        return index == null ? 0 : index;
    }

    private List<String> getTicketNames(Set<Ticket> tickets) {
        return tickets.stream().map(ticket -> {
            switch (ticket) {
                case CAB: {
                    return TICKET_CAB_NAME;
                }
                case BUS: {
                    return TICKET_BUS_NAME;
                }
                case TUBE: {
                    return TICKET_TUBE_NAME;
                }
                case BLACK: {
                    return TICKET_BLACK_NAME;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }).collect(Collectors.toList());
    }

    private Ticket getTicketFromString(String ticketName) {
        switch (ticketName) {
            case TICKET_CAB_NAME: {
                return Ticket.CAB;
            }
            case TICKET_BUS_NAME: {
                return Ticket.BUS;
            }
            case TICKET_TUBE_NAME: {
                return Ticket.TUBE;
            }
            case TICKET_BLACK_NAME: {
                return Ticket.BLACK;
            }

            default: {
                throw new IllegalArgumentException();
            }

        }
    }

    public void showDialog(AlertType type, String title, String header, String context) {
        Alert alert = new Alert(type);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        alert.showAndWait();
    }

    @Override
    public void ShowLogError() {
        this.showError("An Error Occured", "Can't write to Log", "");
    }

}
