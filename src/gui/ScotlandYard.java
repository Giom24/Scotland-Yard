package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import logic.GameLogic;

/**
 * Main ScotlandYard
 *
 * @author Guillaume Fournier-Mayer (tinf 101922)
 */
public class ScotlandYard extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Thread.currentThread().setUncaughtExceptionHandler((Thread th, Throwable ex) -> {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Unerwarteter Fehler");
            alert.setContentText("Entschuldigung, das hätte nicht passieren dürfen!");
            alert.showAndWait();
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        BorderPane root = loader.load();
        Scene scene = new Scene(root, GameLogic.Config.WIDTH, GameLogic.Config.HEIGHT);
        stage.setMinWidth(GameLogic.Config.WIDTH);
        stage.setMinHeight(GameLogic.Config.HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Scotland Yard");
        stage.show();
    }

    /**
     * Einstiegspunkt
     *
     * @param args
     */
    public static void main(String[] args) {

        launch(args);
    }

}
