package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        //Image icon = new Image(System.getProperty("user.dir") + "\\src\\main\\resources\\Images\\EngineLogo.PNG");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GraphDrawingFramework.fxml"));
        Parent root = loader.load();

        GUIController guiController = loader.getController();
        Scene gameScene = new Scene(root);
        guiController.init();

        stage.setTitle("GraphDrawingFramework");
        //stage.getIcons().add(icon);
        stage.setScene(gameScene);
        stage.setResizable(false);
        stage.requestFocus();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}