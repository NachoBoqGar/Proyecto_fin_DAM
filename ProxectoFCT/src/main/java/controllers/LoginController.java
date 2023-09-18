package controllers;



import inicio.FCTApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import accesodatos.EmpleadosDAO;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import sesion.UsuarioSesion;

import java.io.IOException;

public class LoginController {

    public GridPane gridPaneLogin;
    //variables
    EmpleadosDAO emp = new EmpleadosDAO();
    @FXML
    public Label loginError;
    @FXML
    public TextField usuario;
    @FXML
    public PasswordField pass;
    @FXML
    public Button boton_login;
    @FXML
    public HBox hboxLogin;


    public void initialize() {

        loginError.setVisible(false);
        pass.setFocusTraversable(false);
        gridPaneLogin.setStyle("-fx-background-color: white;");
        boton_login.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");

    }


    public void pulsarIngresar(ActionEvent actionEvent) throws IOException {

        //comprobamos si o usuario e pass coinciden,si son correctos accedemos ao menú principal
        if(emp.validarEmpleado(usuario.getText(),pass.getText())){

            //creamos o usuario que inicia sesion e gardamos os seus datos
            UsuarioSesion usuarioConectado = UsuarioSesion.getInstanciaUsuario();
            usuarioConectado.setId(emp.getIdEmpleado(usuario.getText()));
            usuarioConectado.setNombre(emp.getNombreCompletoEmpleado(usuario.getText()));
            usuarioConectado.setRol(emp.getRolEmpleado(usuario.getText()));


            //abrimos menú principal
            FXMLLoader fxmlLoader = new FXMLLoader(FCTApplication.class.getResource("menuinicio.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Menú Principal");
            stage.setScene(scene);
            stage.show();
            //cerramos ventana de login ao iniciar sesión
            Stage stage_login = (Stage) boton_login.getScene().getWindow();
            stage_login.close();
        }else{
            loginError.setVisible(true);
        }

    }

    public void ratonEntrarEntrar(MouseEvent mouseEvent) {
        boton_login.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void ratonSalirEntrar(MouseEvent mouseEvent) {
        boton_login.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }
}
