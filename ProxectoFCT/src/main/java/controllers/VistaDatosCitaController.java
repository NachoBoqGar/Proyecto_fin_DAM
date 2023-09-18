package controllers;

import accesodatos.CitasDAO;
import accesodatos.ClientesDAO;
import inicio.FCTApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import mail.EnvioCorreo;
import modelos.Citas;
import modelos.Clientes;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class VistaDatosCitaController {


    @FXML
    public TextField idCliente;
    @FXML
    public TextField nombreCliente;
    @FXML
    public TextField apellidosCliente;
    @FXML
    public TextField idEmpleado;
    @FXML
    public TextField nombreEmpleado;
    @FXML
    public TextField apellidosEmpleado;
    @FXML
    public TextField idCita;
    @FXML
    public TextField fechaCita;
    @FXML
    public TextField estadoCita;
    @FXML
    public TextArea comentarioCita;
    @FXML
    public Button botonGuardarCambiosCita;
    @FXML
    public Button botonCancelarCita;
    @FXML
    public Button botonNoPresentado;
    @FXML
    public Button botonFinalizarCita;
    @FXML
    public GridPane gridPaneEmcabezado;


    //variable para detectar cambios no campo comentario
    private boolean comentarioModificado = false;
    //variable para o envio de correos
    private EnvioCorreo envioCorreo = new EnvioCorreo();
    //variables para consultas a tabla citas e clienetes
    CitasDAO citasDAO = new CitasDAO();
    ClientesDAO clientesDAO = new ClientesDAO();
    //variable para obter a instancia do formulario ListadoCitas
    private ListaCitasController listaCitasController;

    public void initialize(){

        //para ejecutar unha vez finalziado initialize(). Para comprobar o estado da cita e si está finalizada deshabilitar a edicion
        Platform.runLater(this::citaCerrada);

    }

    //incializamos a variable coa instancia da vista de listado de clientes
    public void ListaCitasController(ListaCitasController listaCitasController) {
        this.listaCitasController = listaCitasController;
    }

    public void cerrarVentana(ActionEvent actionEvent) {
        if(comentarioModificado){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(null);
            alert.setTitle("Guardar Cambios");
            alert.setContentText("¿Deseas realmente salir sin guardar los cambios?");
            Optional<ButtonType> action = alert.showAndWait();
            if (action.get() == ButtonType.OK) {


            }
        }

    }

    //si a cita ten un estado diferente a PENDIENTE impedimos que se modifiquen datos
    private void citaCerrada() {
        if(!estadoCita.getText().equals("PENDIENTE")){
            botonGuardarCambiosCita.setDisable(true);
            botonCancelarCita.setDisable(true);
            botonNoPresentado.setDisable(true);
            botonFinalizarCita.setDisable(true);
            comentarioCita.setDisable(true);
            if(estadoCita.getText().equals("FINALIZADA")){
                gridPaneEmcabezado.setStyle("-fx-background-color:   #CEFBBA");
            } else if (estadoCita.getText().equals("NO PRESENTADO")) {
                gridPaneEmcabezado.setStyle("-fx-background-color:   #FAFFC5");
            }else{
                gridPaneEmcabezado.setStyle("-fx-background-color:  #F1AAAA");
            }
        }
    }

    public void actualizarCita(ActionEvent actionEvent)  {

        if(comentarioModificado){
            String comentarioCitaModificado = comentarioCita.getText();
            int idCitaModificada = Integer.parseInt(idCita.getText());
            Citas citaModificada = new Citas();
            citaModificada.setIdcita(idCitaModificada);
            citaModificada.setComentario(comentarioCitaModificado);
            citasDAO.actualizarComentarioCita(citaModificada);
            botonGuardarCambiosCita.setDisable(true);
            comentarioModificado= true;
            //actualziamos a lista de citas do formulario listacitas si está aberto
            actualizarLista();
        }
        notificacionModificacionCita("Cita actualizada","La cita se ha actualizado correctamente");

    }


    //METODOS PARA ACTUALZIAR ESTADOS DAS CITAS

    //Cancelar cita
    public void cancelarCita(ActionEvent actionEvent) {
        //pasamos por parametros os datos da alerta de cita finalizada
        alertaCierreCita(2,"¿Deseas realmente cancelar la cita?","CANCELADA","Cita Cancelada","La cita ha sido cancelada correctamente");
        gridPaneEmcabezado.setStyle("-fx-background-color:  #F1AAAA");
        //actualziamos a lista de citas do formulario listacitas si está aberto
        actualizarLista();

    }
    //Pasar a No Presenrado a cita
    public void estadoNoPresentado(ActionEvent actionEvent) throws ParseException {
        if(!fechaActualPosteriorFechaCita(fechaCita.getText())) {
        //pasamos por parametros os datos da alerta de cita finalizada
         alertaCierreCita(3,"¿Deseas cambiar el estado de la cita a No Presentado?","NO PRESENTADO","Estado No Presentado","El estado de la cita se ha cambiado a No Presentado");
            gridPaneEmcabezado.setStyle("-fx-background-color:   #FAFFC5");
        }else{
            alertaCambioNoPermitido("La fecha de la cita es posterior a la fecha actual");
        }
        //actualziamos a lista de citas do formulario listacitas si está aberto
        actualizarLista();
    }

    //Pasar a Finalizada a cita
    public void estadoFinalizada(ActionEvent actionEvent) throws ParseException {
        if(!fechaActualPosteriorFechaCita(fechaCita.getText())) {
            //pasamos por parametros os datos da alerta de cita finalizada
            alertaCierreCita(4, "¿Deseas finalizar la cita?", "FINALIZADA", "Estado Finalizada", "El estado de la cita se ha cambiado a Finalizada");
            gridPaneEmcabezado.setStyle("-fx-background-color:   #CEFBBA");
        }else{
            alertaCambioNoPermitido("La fecha de la cita es posterior a la fecha actual");
        }
        //actualziamos a lista de citas do formulario listacitas si está aberto
        actualizarLista();
    }

    //Metodo para detectar si se modifica o campo comentario
    public void comentarioModificado(KeyEvent keyEvent) {
        comentarioModificado= true;
        botonGuardarCambiosCita.setDisable(false);
    }

    //mensage de alerta para confirmar o cierre das citas
    public void alertaCierreCita(int idestado,String preguntaAlerta, String textoEstado,String tituloCierre, String textoCierre){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Confirmación");
        alert.setContentText( preguntaAlerta);
        Optional<ButtonType> action = alert.showAndWait();

        if (action.get() == ButtonType.OK) {
            //pasamos por parametro o id da cita a modificar e establecemos o idestado a 2 (cancelada)
            citasDAO.cambiarEstadoCita(idestado,Integer.parseInt(idCita.getText()));
            estadoCita.setText(textoEstado);

            notificacionModificacionCita(tituloCierre,textoCierre);
            if(idestado == 2) {
                //envio notificacion de cancelación da cita
                Clientes cliente = clientesDAO.clientePorID(Integer.parseInt(idCliente.getText()));
                envioCorreo.Correo(cliente.getCorreo(), "Cita cancelada", "La cita que tenía programada con nosotros para la siguiente fecha y hora " + fechaCita.getText() + " ha sido cancelada");
            }
        }

    }

    //metodo para notificar os distintos cambios nas citas
    public  void notificacionModificacionCita(String titulo, String texto){

        //desactivamos edicción
        citaCerrada();
        //notificación flotante
        Notifications.create()
                .title(titulo)
                .text(texto)
                .hideAfter(Duration.seconds(2))
                .position(Pos.CENTER)
                .darkStyle()
                //.graphic(new ImageView(new Image()))
                .show();
    }

    //metodo para comprobar si a data da cita e posterior ou anterior a actual
    public boolean fechaActualPosteriorFechaCita(String fechaCita) throws ParseException {
        //obtemos fecha e hora actual
        Calendar fechaHoraActual = Calendar.getInstance();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date fecha = formatoFecha.parse(fechaCita);
        //comparamos fecha e hora da cita coa fecha e hora actual
        if (fechaHoraActual.getTime().compareTo(fecha) < 0) {
           return true;
        } else {
            return false;
        }

    }

    //Metodo para verificar que non se realice un cambio non permitido. Como finalizar ou establecer como no presentado unha cita antes da fecha para a que esta programada
    public void alertaCambioNoPermitido(String textoAlerta){

        Alert errorFechaCitaPosteriorFechaActual = new Alert(Alert.AlertType.ERROR);
        errorFechaCitaPosteriorFechaActual.setTitle("Cambio de estado no permitido");
        errorFechaCitaPosteriorFechaActual.setHeaderText(null);
        errorFechaCitaPosteriorFechaActual.setContentText(textoAlerta);
        errorFechaCitaPosteriorFechaActual.showAndWait();
    }

    //método para actualziar a lista de citas do formulario listacitas si está aberto
    public void actualizarLista(){
        //actualziamos a lista de citas do formulario listacitas si está aberto
        if(listaCitasController != null) {
            //actualizamos a tabla da vista principal
            Button botonBuscar = listaCitasController.botonBuscarCitas;
            botonBuscar.fire();
        }
    }



    //eventos dos botons para cambiar formato candoentra/sae o rato deles

    public void cancelarCitaEntrar(MouseEvent mouseEvent) {
        botonCancelarCita.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void cancelarCitaSalir(MouseEvent mouseEvent) {
        botonCancelarCita.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void guardarEntrar(MouseEvent mouseEvent) {
        botonGuardarCambiosCita.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void guardarSalir(MouseEvent mouseEvent) {
        botonGuardarCambiosCita.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void nopresentadoEntrar(MouseEvent mouseEvent) {
        botonNoPresentado.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void noPresentadoSalir(MouseEvent mouseEvent) {
        botonNoPresentado.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void finalizadadaEntrar(MouseEvent mouseEvent) {
        botonFinalizarCita.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void finalizadaSalir(MouseEvent mouseEvent) {
        botonFinalizarCita.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }
}
