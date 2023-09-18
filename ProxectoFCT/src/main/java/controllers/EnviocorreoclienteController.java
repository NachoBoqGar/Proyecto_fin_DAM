package controllers;

import Configuracion.ConfiguracionAplicacion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import mail.EnvioCorreo;
import org.controlsfx.control.Notifications;
import sesion.UsuarioSesion;

import java.util.Optional;

public class EnviocorreoclienteController {
    @FXML
    public Label etiquetaDestinatario;
    @FXML
    public TextField textoAsunto;
    @FXML
    public Button botonEnviar;
    @FXML
    public Button botonCancelarEnvio;
    public TextField direccionCorreo;
    public TextField textoCorreo;
    EnvioCorreo envioCorreo = new EnvioCorreo();
    ConfiguracionAplicacion configuracionAplicacion = new ConfiguracionAplicacion();

    static UsuarioSesion usuarioSesion;
    public void enviarCorreo(ActionEvent actionEvent) {

        if(textoAsunto.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(null);
            alert.setTitle("Correo sin asunto");
            alert.setContentText("¿Deseas enviar el correo sin asunto?");
            Optional<ButtonType> action = alert.showAndWait();

            if (action.get() == ButtonType.OK) {

                String asunto = textoAsunto.getText();
                String cuerpo = textoCorreo.getText();

                envioCorreo.Correo(direccionCorreo.getText(),asunto,cuerpo+configuracionAplicacion.firmaUsuario());

                Notifications.create()
                        .title("Correo enviado")
                        .text("Se ha enviado el correo al cliente")
                        .hideAfter(Duration.seconds(2))
                        .position(Pos.CENTER)
                        .darkStyle()
                        //.graphic(new ImageView(new Image()))
                        .show();

                Scene scene = botonCancelarEnvio.getScene();
                Stage stage = (Stage) scene.getWindow();
                stage.close();
            }
        }else{
            String asunto = textoAsunto.getText();
            String cuerpo = textoCorreo.getText();
            envioCorreo.Correo(direccionCorreo.getText(),asunto,cuerpo+configuracionAplicacion.firmaUsuario());

            Notifications.create()
                    .title("Correo enviado")
                    .text("Se ha enviado el correo al cliente")
                    .hideAfter(Duration.seconds(2))
                    .position(Pos.CENTER)
                    .darkStyle()
                    //.graphic(new ImageView(new Image()))
                    .show();

            Scene scene = botonCancelarEnvio.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.close();

        }

    }



    public void cancelarEnvioCorreo(ActionEvent actionEvent) {
        
        Scene scene = botonCancelarEnvio.getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.close();
    }




    public void entrarCancelarEnvio(MouseEvent mouseEvent) {
       botonCancelarEnvio.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirCancelarEnvio(MouseEvent mouseEvent) {
        botonCancelarEnvio.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }


    public void entarEnviarCorreo(MouseEvent mouseEvent) {
        botonEnviar.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirEnviarCorreo(MouseEvent mouseEvent) {
        botonEnviar.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }



}
