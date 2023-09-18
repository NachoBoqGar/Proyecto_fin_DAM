package controllers;

import accesodatos.CitasDAO;
import accesodatos.EmpleadosDAO;
import inicio.FCTApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import mail.EnvioCorreo;
import modelos.Citas;
import modelos.Clientes;
import modelos.Empleados;
import modelos.Estados;
import org.controlsfx.control.Notifications;
import sesion.UsuarioSesion;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;


public class AltaCitaController {

    @FXML
    public TextField fechaCita;
    @FXML
    public ImageView buscarCliente;
    @FXML
    public TextField estadoCita;
    @FXML
    public TextField nombreCliente;
    @FXML
    public TextField apellidosCliente;
    @FXML
    public TextField idClienteCita;
    @FXML
    public TextArea comentarioCita;
    @FXML
    public ComboBox empleadoCita;
    @FXML
    public Button botonCancelarCita;
    @FXML
    public Label labelSeleccionarCliente;
    public Button botonCrearCita;


    EnvioCorreo enviarCorreo = new EnvioCorreo();
    CitasDAO citasDAO = new CitasDAO();
    EmpleadosDAO empleadosDAO = new EmpleadosDAO();
    //variable para ver rol do usuario conectado
    UsuarioSesion usuarioSesion;

    String correoUsuario=null;
    public void initialize (){

        //establecemos o estado como pendiente
        estadoCita.setText("PENDIENTE");
        //para ejecutar unha vez finalziado initialize(). Para poder cargar os elementos do combobox tras ser inicializado
        Platform.runLater(this::rellenarComboboxEmpleados);

    }

    public void buscarCliente(MouseEvent mouseEvent) throws IOException {


        //cargamos formulario
        FXMLLoader fxmlLoader = new FXMLLoader(FCTApplication.class.getResource("buscarclientecita.fxml"));
        Parent root = fxmlLoader.load();
        //pasamos o controller actual a altacita.fxml para desde ali poder cargar os datos do cliente que se selecciona
        BuscarClienteCitaController controller = fxmlLoader.getController();
        controller.setAltaCitaController(this);
        //abrimos formulario para crear a nova cita. Apertura en modo modal
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setMinWidth(1003);
        stage.setMinHeight(879);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();


    }

    //cerrase ventana tras pulsar o boton cancelar
    public void cancelarCita(ActionEvent actionEvent) {

        Scene scene = botonCancelarCita.getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.close();

    }

    //crease unha nova cita. Obligatorio seleccionar un cliente
    public void crearCita(ActionEvent actionEvent) throws ParseException {

        if(!idClienteCita.getText().equals("")) {
            //obtemos empleado seleccionado e quedamonos co ID
            String empleadoSeleccionado = empleadoCita.getSelectionModel().getSelectedItem().toString();
            String[] parts = empleadoSeleccionado.split(" - ");
            String idEmpleadoSeleccionado = parts[0];
            //creamos todas as variables para crear a nova cita
            int idEmpleado = Integer.parseInt(idEmpleadoSeleccionado);
            Empleados empleado = new Empleados();
            empleado.setIdempleado(idEmpleado);
            int idCliente = Integer.parseInt(idClienteCita.getText());
            Clientes cliente = new Clientes();
            cliente.setIdcliente(idCliente);
            Estados estado = new Estados(1);
            String comentario = comentarioCita.getText().trim();
            java.sql.Date fechaSqlCita = fechaFormateadaCita();

            //creamos objeto cita e ejecutamos consulta para gardalo na BBDD
            Citas nuevaCita = new Citas(comentario, fechaSqlCita, cliente, empleado, estado);
            citasDAO.crearCita(nuevaCita);

            //cerramos ventana de creación de cita
            Scene scene = botonCancelarCita.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.close();

            //envio notificacion alta da cita
            enviarCorreo.Correo(correoUsuario,"Cita programada","Tiene una cita programada con nosotros para la siguiente fecha y hora "+fechaCita.getText());

            //mostramos mensage de cita creada e cerramos a ventana
            Notifications.create()
                    .title("Nueva Cita")
                    .text("La cita se ha creado correctamente")
                    .hideAfter(Duration.seconds(8))
                    .position(Pos.CENTER)
                    .darkStyle()
                    //.graphic(new ImageView(new Image("images/DSCF2148.JPG")))
                    .show();


        }else{
            labelSeleccionarCliente.setVisible(true);
            labelSeleccionarCliente.getStyleClass().add("mensage-advertencia");

        }

    }

    //metodo para rellenar o combobox cos empleados dispoñibles para a cita. Si solo hai permisos de USUARIO solo se pode establecer citas asi mismo
    public void rellenarComboboxEmpleados() {
        try {
            usuarioSesion = UsuarioSesion.getInstanciaUsuario();
            //si o usuario ten rol USUARIO ssolo pode dar de alta citas para si mismo
            //en caso contrario buscamos empleados que non teñan citas asinadas para ese día e esa hora
            if (usuarioSesion.getRol().equals("USUARIO")) {
                empleadoCita.getItems().add(usuarioSesion.getId() + " - " + usuarioSesion.getNombre());
                empleadoCita.getSelectionModel().selectFirst();
            } else {
                SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date fecha = formatoFecha.parse(fechaCita.getText());
                java.sql.Date fechaSqlCita = new java.sql.Date(fecha.getTime());
                List<String> empleadosLibresParaCita = empleadosDAO.empleadosLibresParaCita(fechaSqlCita);

                for (String empleado : empleadosLibresParaCita) {
                    empleadoCita.getItems().add(empleado);
                    empleadoCita.getSelectionModel().selectFirst();
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public Date fechaFormateadaCita() throws ParseException {

        String fechaString = fechaCita.getText().trim();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date fecha = formatoFecha.parse(fechaString);
        java.sql.Date fechaSqlCita = new java.sql.Date(fecha.getTime());

        return fechaSqlCita;

    }

    //EVENTOS

    //Evento para resaltar/quitar resaltado o icono de buscar cliente cando se poña o rato encima ou se quite e dos diferentes botóns

    public void resaltarBuscarCliente() {

        buscarCliente.setStyle("-fx-opacity: 0.50;");
    }
    public void quitarResaltadoBuscarCliente() {
        buscarCliente.setStyle(null);
    }

    public void entrarCrearCIta(MouseEvent mouseEvent) {
        botonCrearCita.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirCrearCita(MouseEvent mouseEvent) {
        botonCrearCita.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarCancelarCIta(MouseEvent mouseEvent) {
        botonCancelarCita.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirCancelarCIta(MouseEvent mouseEvent) {
        botonCancelarCita.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }
}
