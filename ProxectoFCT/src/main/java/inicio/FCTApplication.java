package inicio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.SQLException;

public class FCTApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException, SQLException, ClassNotFoundException {

        //cargar ventana de login
        FXMLLoader fxmlLoader = new FXMLLoader(FCTApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Inicio de Sesión");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}