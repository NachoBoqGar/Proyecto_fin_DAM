package controllers;

import accesodatos.EmpleadosDAO;
import inicio.FCTApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import modelos.Empleados;
import sesion.UsuarioSesion;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class MenuInicioController {
    @FXML
    public Label datosUsuario;
    @FXML
    public TitledPane menuAdministrar;
    @FXML
    public Button botonUsuario;
    @FXML
    public Button botonEstadisticas;
    @FXML
    public Button listadoCitas;
    @FXML
    public Button crearCita;
    @FXML
    public Button listadoClientes;
    @FXML
    public Button altaCliente;
    @FXML
    public Button botonUsuarioUnico;


    private boolean ventanaAltaClienteAberta= false;
    private Stage ventanaAltaCliente;
    private boolean ventanaListadoClienteAberta= false;
    private Stage ventanaListadoClientes;
    private boolean ventanaCrearCitaAberta= false;
    private Stage ventanaCrearCita;
    private boolean ventanaListadoEmpleadosAberta = false;
    private Stage ventanaListadoEmpleados;
    private boolean ventanaListadoCitasAberta= false;
    private Stage ventanaListadoCitas;
    private boolean ventanaEstadisticasAberta= false;
    private Stage ventanaEstadisticas;
    UsuarioSesion usuarioSesion;
    //variable para realizar consultas
    EmpleadosDAO empleadosDAO = new EmpleadosDAO();

    public void initialize() {
        usuarioSesion = UsuarioSesion.getInstanciaUsuario();
        datosUsuario.setText("Bienvenido, "+usuarioSesion.getNombre()+" ["+usuarioSesion.getRol()+"]" );

        if(usuarioSesion.getRol().equals("ADMIN")){
            botonUsuario.setVisible(true);
            botonUsuarioUnico.setVisible(false);
        }else{
            botonUsuario.setVisible(false);
            botonUsuarioUnico.setVisible(true);
        }


    }



    public void abrirAltaCliente(ActionEvent mouseEvent) throws IOException {

        //si esta a ventana aberta pasamola a primeiro plano en vez de abrir unha nova
        if (ventanaAltaClienteAberta == false) {
            FXMLLoader fxmlLoader = new FXMLLoader(FCTApplication.class.getResource("altacliente.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Alta de nuevo Cliente");
            stage.setScene(scene);
            //cargamos folla de estilos
            scene.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
            //pasamos esta instancia de MenuInicioCOntroller a AltaCLienteController para desde ali pasar a false a variable ventanaAltaClienteAberta
            //si non pasamos esta misma instancia non funcionaria
            AltaClienteController controller = fxmlLoader.getController();
            controller.setMenuInicioController(this);

            ventanaAltaClienteAberta = true;
            ventanaAltaCliente = stage;
            //manejo evento cerrar ventana
            stage.setOnCloseRequest(e -> cerrarVentanaAlta ());
            stage.show();

        } else {
            ventanaAltaCliente.setIconified(false);
            ventanaAltaCliente.toFront();
        }
    }
    //Metodo para poñer a false a variable ventanaAltaClienteAberta cando se pecha o formulario de alta de cliente
    public void cerrarVentanaAlta (){

        ventanaAltaClienteAberta= false;

    }

    public void abrirlistadoClientes(ActionEvent actionEvent) throws IOException {
        //si esta a ventana aberta pasamola a primeiro plano en vez de abrir unha nova
        if (ventanaListadoClienteAberta == false) {
            FXMLLoader fxmlLoader = new FXMLLoader(FCTApplication.class.getResource("listadoclientes.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Lista de Clientes");
            stage.setScene(scene);
            //cargamos folla de estilos
            scene.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
            //pasamos esta instancia de MenuInicioCOntroller a AltaCLienteController para desde ali pasar a false a variable ventanaAltaClienteAberta
            //si non pasamos esta misma instancia nonfuncionaria
            ListadoClientesController controller = fxmlLoader.getController();
            controller.setMenuInicioController(this);

            ventanaListadoClienteAberta = true;
            ventanaListadoClientes = stage;
            //manejo evento cerrar ventana
            stage.setOnCloseRequest(e -> cerrarVentanaListadoClientes ());
            stage.show();

        } else {
            ventanaListadoClientes.setIconified(false);
            ventanaListadoClientes.toFront();
        }
    }
    //Metodo para poñer a false a variable ventanaListadoClientesAberta cando se pecha o formulario de listado de clientes
    public void cerrarVentanaListadoClientes (){

        ventanaListadoClienteAberta= false;
    }

    public void abrirNuevaCita(ActionEvent actionEvent) throws IOException {
        //si esta a ventana aberta pasamola a primeiro plano en vez de abrir unha nova
        if (ventanaCrearCitaAberta == false) {
            FXMLLoader fxmlLoader = new FXMLLoader(FCTApplication.class.getResource("seleccionarfechacita.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Crear nueva cita");
            stage.setScene(scene);
            //cargamos folla de estilos
            scene.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
            //pasamos esta instancia de MenuInicioController a AltaCLienteController para desde ali pasar a false a variable ventanaAltaClienteAberta
            //si non pasamos esta misma instancia non funcionaria
            SeleccionarFechaCitaController controller = fxmlLoader.getController();
            controller.setMenuInicioController(this);

            ventanaCrearCitaAberta = true;
            ventanaCrearCita = stage;
            //manejo evento cerrar ventana
            stage.setOnCloseRequest(e ->  cerrarVentanaCrearCita ());
            stage.show();

        } else {
            //si xa hai unha ventana aberta traemola a primeiro plano
            ventanaCrearCita.setIconified(false);
            ventanaCrearCita.toFront();
        }
    }

    //Metodo para poñer a false a variable ventanaCrearCitaAberta cando se pecha o formulario de listado de clientes
    public void cerrarVentanaCrearCita (){

        ventanaCrearCitaAberta= false;
    }

    public void abrirListadoEmpleados(ActionEvent actionEvent) throws IOException {
        //si esta a ventana aberta pasamola a primeiro plano en vez de abrir unha nova
        if (ventanaListadoEmpleadosAberta == false) {
            FXMLLoader fxmlLoader = new FXMLLoader(FCTApplication.class.getResource("listadoempleados.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Lista de Empleados");
            stage.setScene(scene);
            //cargamos folla de estilos
            scene.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
            //pasamos esta instancia de MenuInicioCOntroller a AltaCLienteController para desde ali pasar a false a variable ventanaAltaClienteAberta
            //si non pasamos esta misma instancia nonfuncionaria
            ListadoEmpleadosController controller = fxmlLoader.getController();
            controller.setMenuInicioController(this);

            ventanaListadoEmpleadosAberta = true;
            ventanaListadoEmpleados = stage;
            //manejo evento cerrar ventana
            stage.setOnCloseRequest(e -> cerrarVentanaListadoEmpleados ());
            stage.show();

        } else {
            ventanaListadoEmpleados.setIconified(false);
            ventanaListadoEmpleados.toFront();
        }
    }
    //Metodo para poñer a false a variable ventanaListadoEmpleadosAberta cando se pecha o formulario de administrar usuarios
    public void cerrarVentanaListadoEmpleados (){
        ventanaListadoEmpleadosAberta = false;
    }

    //abrir ventana listado de citas
    public void listadoCitas(ActionEvent actionEvent) throws IOException {
        //si esta a ventana aberta pasamola a primeiro plano en vez de abrir unha nova
        if (ventanaListadoCitasAberta == false) {
            FXMLLoader fxmlLoader = new FXMLLoader(FCTApplication.class.getResource("listacitas.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Lista de Citas");
            stage.setScene(scene);
            //cargamos folla de estilos
            scene.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
            //pasamos esta instancia de MenuInicioController a AltaCLienteController para desde ali pasar a false a variable ventanaAltaClienteAberta
            //si non pasamos esta misma instancia nonfuncionaria
            ListaCitasController controller = fxmlLoader.getController();
            controller.setMenuInicioController(this);

            ventanaListadoCitasAberta = true;
            ventanaListadoCitas = stage;
            //manejo evento cerrar ventana
            stage.setOnCloseRequest(e -> cerrarVentanaListadoCitas ());
            stage.show();

        } else {
            ventanaListadoCitas.setIconified(false);
            ventanaListadoCitas.toFront();
        }
    }

    //Metodo para poñer a false a variable ventanaListadoCitasAberta cando se pecha o formulario de listado de citas
    public void cerrarVentanaListadoCitas (){
        ventanaListadoCitasAberta = false;
    }

    public void abrirEstadisticas(ActionEvent actionEvent) throws IOException {
        //si esta a ventana aberta pasamola a primeiro plano en vez de abrir unha nova
        if (ventanaEstadisticasAberta == false) {
            FXMLLoader fxmlLoader = new FXMLLoader(FCTApplication.class.getResource("estadisticas.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Estadísticas");
            stage.setScene(scene);
            //cargamos folla de estilos
            scene.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
            //pasamos esta instancia de MenuInicioController a AltaCLienteController para desde ali pasar a false a variable ventanaAltaClienteAberta
            //si non pasamos esta misma instancia nonfuncionaria
            EstadisticasController controller = fxmlLoader.getController();
            controller.setMenuInicioController(this);

            ventanaEstadisticasAberta = true;
            ventanaEstadisticas = stage;
            //manejo evento cerrar ventana
            stage.setOnCloseRequest(e -> cerrarVentanaEstadisticas ());
            stage.show();

        } else {
            ventanaEstadisticas.setIconified(false);
            ventanaEstadisticas.toFront();
        }
    }
    public void cerrarVentanaEstadisticas (){ ventanaEstadisticasAberta = false;
    }

    //abrir ventana de usuario con permisos usuario
    public void abrirUsuarioUnico(ActionEvent actionEvent) throws IOException {

        //cargamos formulario e obtemos controllador para cargar os datos no formulario que vamos a abrir
        FXMLLoader loader = new FXMLLoader(FCTApplication.class.getResource("vistadatosusuario.fxml"));
        Parent root = loader.load();
        VistaDatosUsuarioController controller = loader.getController();

        Empleados empleado = empleadosDAO.empleadoPorID(usuarioSesion.getId());
        controller.datosId.setText(String.valueOf(empleado.getIdempleado()));
        SimpleDateFormat formatoFechaAlta = new SimpleDateFormat("dd-MM-yyyy");
        String fechaString = formatoFechaAlta.format(empleado.getFecha_alta());
        controller.datosFechaAlta.setText(fechaString);
        controller.datosDni.setText(empleado.getDni());
        controller.datosNombre.setText(empleado.getNombre());
        controller.datosApellido1.setText(empleado.getApellido1());
        controller.datosApellido2.setText(empleado.getApellido2());
        controller.datosTelefono.setText(empleado.getTelefono());
        controller.datosEmail.setText(empleado.getCorreo());
        controller.datosDireccion.setText(empleado.getDireccion());
        controller.datosUsuario.setText(empleado.getDni());
        controller.datosPass.setText(empleado.getPass());

        controller.datosEstado.setText(empleado.getActivo() == 1 ? "ACTIVO" : "BAJA");

        controller.datosRol.getItems().addAll("ADMIN","USUARIO");
        if (empleado.getRoles().getIdrol() == 1) {
            controller.datosRol.setValue("ADMIN");
        } else {
            controller.datosRol.setValue("USUARIO");
        }
        controller.datosRol.setDisable(true);
        controller.botonBajaEmpleado.setVisible(false);
        controller.botonRestaurar.setVisible(false);
        controller.botonCancelarUsuario.setVisible(false);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setMinWidth(1003);
        stage.setMinHeight(879);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }



    //eventos para detectarcando se entra e sale dos botons
    public void entraAdministrar(MouseEvent mouseEvent) {
        botonUsuario.setStyle("-fx-background-color:  #1e90ff; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirAdministrar(MouseEvent mouseEvent) {
        botonUsuario.setStyle("-fx-background-color:  #1e90ff; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarEstadisticas(MouseEvent mouseEvent) {
        botonEstadisticas.setStyle("-fx-background-color:  #1e90ff; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirEstadistica(MouseEvent mouseEvent) {
        botonEstadisticas.setStyle("-fx-background-color:  #1e90ff; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarCrearCIta(MouseEvent mouseEvent) {
        crearCita.setStyle("-fx-background-color:  #1e90ff; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirCrearCita(MouseEvent mouseEvent) {
        crearCita.setStyle("-fx-background-color:  #1e90ff; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarListaCliente(MouseEvent mouseEvent) {
        listadoClientes.setStyle("-fx-background-color:  #1e90ff; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirLIstaCliente(MouseEvent mouseEvent) {
        listadoClientes.setStyle("-fx-background-color:  #1e90ff; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entarAltaCliente(MouseEvent mouseEvent) {
        altaCliente.setStyle("-fx-background-color:  #1e90ff; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirAltaCliente(MouseEvent mouseEvent) {
        altaCliente.setStyle("-fx-background-color:  #1e90ff; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }


    public void entrarListaCitas(MouseEvent mouseEvent) {
         listadoCitas.setStyle("-fx-background-color:  #1e90ff; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirListaCitas(MouseEvent mouseEvent) {
        listadoCitas.setStyle("-fx-background-color:  #1e90ff; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }
    public void usuarioUnicoEntra(MouseEvent mouseEvent) {
        botonUsuarioUnico.setStyle("-fx-background-color:  #1e90ff; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void usuarioUnicoSale(MouseEvent mouseEvent) {
        botonUsuarioUnico.setStyle("-fx-background-color:  #1e90ff; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }






}
