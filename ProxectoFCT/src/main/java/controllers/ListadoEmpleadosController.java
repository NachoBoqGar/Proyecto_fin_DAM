package controllers;

import Exportar.ExportarDatos;
import accesodatos.EmpleadosDAO;
import com.itextpdf.text.DocumentException;
import inicio.FCTApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import modelos.Citas;
import modelos.Empleados;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class ListadoEmpleadosController {
    @FXML
    public Button botonCancelarListadoUsuarios;
    @FXML
    public TextField dniBuscar;
    @FXML
    public TextField nombreBuscar;
    @FXML
    public TextField apellido1Buscar;
    @FXML
    public TextField apellido2Buscar;
    @FXML
    public TextField telefonoBuscar;
    @FXML
    public TextField emailBuscar;
    @FXML
    public TextField direccionBuscar;
    @FXML
    public TextField idBuscar;
    @FXML
    public DatePicker fechaAltaBuscar;
    @FXML
    public Label etiquetaError;
    @FXML
    public Button botonLimpiarCampos;
    @FXML
    public ComboBox rolBuscar;
    @FXML
    public TableView tablaResultadosEmpleados;
    @FXML
    public TabPane tabPaneListadoEmpleados;
    @FXML
    public Button botonExportar;
    @FXML
    public CheckBox registrosBaja;
    @FXML
    public Button botonBuscarEmpleados;
    @FXML
    public Button botonNuevoEmpleado;


    EmpleadosDAO empleadosDAO = new EmpleadosDAO();
    ExportarDatos exportarDatos = new ExportarDatos();
    private MenuInicioController menuInicioController;
    public void setMenuInicioController(MenuInicioController controller) {
        menuInicioController = controller;
    }

    public void initialize(){

        //icono boton exportar
        Image iconoExportar = new Image(getClass().getResourceAsStream("/images/export.png"));
        botonExportar.setGraphic(new ImageView(iconoExportar));
        //inicializamos combobox de roles
        rolBuscar.getItems().addAll("ADMIN","USUARIO");

        //inicializamos tabla de datos
        //crear columnas
        TableColumn<Empleados, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idempleado"));
        TableColumn<Empleados, String> dniColumn = new TableColumn<>("DNI");
        dniColumn.setCellValueFactory(new PropertyValueFactory<>("dni"));
        TableColumn<Empleados,  String> nombreColumn = new TableColumn<>("Nombre");
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        TableColumn<Empleados,  String> apellido1Column = new TableColumn<>("Primer Apellido");
        apellido1Column.setCellValueFactory(new PropertyValueFactory<>("apellido1"));
        TableColumn<Empleados,  String> apellido2Column = new TableColumn<>("Segundo Apellido");
        apellido2Column.setCellValueFactory(new PropertyValueFactory<>("apellido2"));
        TableColumn<Empleados,  String> correoColumn = new TableColumn<>("Correo");
        correoColumn.setCellValueFactory(new PropertyValueFactory<>("correo"));
        TableColumn<Empleados,  String> telefonoColumn = new TableColumn<>("Teléfono");
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        TableColumn<Empleados, String> direccionColumn = new TableColumn<>("Dirección");
        direccionColumn.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        TableColumn<Empleados, Date> fechaAltaColumn = new TableColumn<>("Fecha de Alta");
        //cambiar formato da fecha a dd/MM/yyyy
        fechaAltaColumn.setCellFactory(column -> {
            return new TableCell<Empleados, Date>() {
                @Override
                protected void updateItem(Date fecha, boolean empty) {
                    super.updateItem(fecha, empty);
                    if (fecha == null || empty) {
                        setText(null);
                    } else {
                        // Convertimos la fecha a LocalDateTime
                        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(fecha.getTime()), ZoneId.systemDefault());
                        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        String fechaFormateada = localDateTime.format(formatoFecha);

                        // Establecemos la fecha en la celda correspondiente
                        setText(fechaFormateada);
                    }
                }
            };
        });
        fechaAltaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha_alta"));
        TableColumn<Empleados, String> roleColumn = new TableColumn<>("Rol");
        //metodo para cambiar o que se mostra, a celda teña o valor 1=ADMIN 2=USUARIO
        roleColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Empleados, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Empleados, String> role) {
                if (role.getValue().getRoles().getIdrol() == 1) {
                    return new SimpleStringProperty("ADMIN");
                } else {
                    return new SimpleStringProperty("USUARIO");
                }
            }
        });
        TableColumn<Empleados, String> estadoColumn = new TableColumn<>("Estado");
        //metodo para cambiar o que se mostra, a celda teña o valor 1=ACTIVO 0=BAJA
        estadoColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Empleados, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Empleados, String> activo) {
                if (activo.getValue().getActivo() == 1) {
                    return new SimpleStringProperty("ACTIVO");
                } else {
                    return new SimpleStringProperty("BAJA");
                }
            }
        });
        //para que as columnas coupen todo o ancho da tabla
        tablaResultadosEmpleados.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //cargar columnas
        tablaResultadosEmpleados.getColumns().addAll(idColumn,dniColumn,nombreColumn,apellido1Column,apellido2Column,correoColumn,telefonoColumn,direccionColumn,fechaAltaColumn,roleColumn,estadoColumn);


        //evento doble clik en unha fila da tabla de resultados da búsqueda. Abre unha nova pestaña onde se mosta os datos do cliente seleccionado
        tablaResultadosEmpleados.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                abrirNuevaPestañaEmpleado();
            }
        });
    }

    //Accion boton cancelar.Cerrase a ventana
    public void cancelarVistaEmpleados(ActionEvent actionEvent) {

        Scene scene = botonCancelarListadoUsuarios.getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.close();
        //pasamos a false a variable de ventana de listado de empleados para poder abrir unha nova
        menuInicioController.cerrarVentanaListadoEmpleados();
    }
    public void LimpiarCampos(ActionEvent actionEvent) {
        dniBuscar.setText("");
        nombreBuscar.setText("");
        apellido1Buscar.setText("");
        apellido2Buscar.setText("");
        telefonoBuscar.setText("");
        emailBuscar.setText("");
        direccionBuscar.setText("");
        idBuscar.setText("");
        fechaAltaBuscar.setValue(null);
        rolBuscar.setValue(null);
        tablaResultadosEmpleados.setItems(null);
    }

    public void BuscarRegistros(ActionEvent actionEvent) {

        boolean idCorrecto = true;
        formatoSinErrores();
        //creamos consulta en funcion dos campos que estean cubertos
        String consulta = "";
        if(registrosBaja.isSelected()) {
            consulta = "SELECT e FROM Empleados e WHERE 1=1";
        }else{
            consulta = "SELECT e FROM Empleados e WHERE 1=1 AND activo=1";
        }
        if (!dniBuscar.getText().trim().isEmpty()) {
            consulta += " AND dni LIKE '%" + dniBuscar.getText().trim() + "%'";
        }
        if (!nombreBuscar.getText().trim().isEmpty()) {
            consulta += " AND nombre LIKE '%" + nombreBuscar.getText().trim() + "%'";
        }
        if (!apellido1Buscar.getText().trim().isEmpty()) {
            consulta += " AND apellido1 LIKE '%" + apellido1Buscar.getText().trim() + "%'";
        }
        if (!apellido2Buscar.getText().trim().isEmpty()) {
            consulta += " AND apellido2 LIKE '%" + apellido2Buscar.getText().trim() + "%'";
        }
        if (!telefonoBuscar.getText().trim().isEmpty()) {
            consulta += " AND telefono LIKE '%" + telefonoBuscar.getText().trim() + "%'";
        }
        if (!emailBuscar.getText().trim().isEmpty()) {
            consulta += " AND email LIKE '%" + emailBuscar.getText().trim() + "%'";
        }
        if (!direccionBuscar.getText().trim().isEmpty()) {
            consulta += " AND direccion LIKE '%" + direccionBuscar.getText().trim() + "%'";
        }
        if (!rolBuscar.getSelectionModel().isEmpty()) {
            int idrol = rolBuscar.getSelectionModel().getSelectedItem().toString().equals("ADMIN") ? 1:2;
            consulta += " AND idrol=" + idrol ;
        }
        if (!(fechaAltaBuscar.getValue() ==null)) {
            consulta += " AND fecha_alta='" + fechaAltaBuscar.getValue()+"'" ;
        }
        if (!idBuscar.getText().trim().isEmpty()) {

            try{
                consulta += " AND idempleado = " + Integer.parseInt(idBuscar.getText().trim());
            }catch (NumberFormatException ex){

                idBuscar.getStyleClass().add("campo-incorrecto");
                etiquetaError.setVisible(true);
                etiquetaError.getStyleClass().add("mensage-advertencia");
                idCorrecto = false;
                tablaResultadosEmpleados.setItems(null);
            }
        }

        System.out.println(consulta);


        if(idCorrecto == true){
            //realizamos consulta e obtemos os resultados en unha lista que mostraremos na tabla resultados
            List<Empleados> resultadoBusqueda = empleadosDAO.listadoEmpleados(consulta);
            tablaResultadosEmpleados.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        }

    }

    private void formatoSinErrores(){
        etiquetaError.setVisible(false);
        etiquetaError.getStyleClass().remove("mensage-advertencia");
        idBuscar.getStyleClass().remove("campo-incorrecto");
    }

    private void abrirNuevaPestañaEmpleado() {
        try {
            Empleados empleadoSeleccionado = (Empleados) tablaResultadosEmpleados.getSelectionModel().getSelectedItem();
            if(empleadoSeleccionado != null) {
                //cargamos formulario e obtemos controllador para cargar os datos no formulario que vamos a abrir
                FXMLLoader loader = new FXMLLoader(FCTApplication.class.getResource("vistadatosusuario.fxml"));
                Parent root = loader.load();
                VistaDatosUsuarioController controller = loader.getController();
                controller.ListadoEmpeladosController(this);

                controller.datosId.setText(String.valueOf(empleadoSeleccionado.getIdempleado()));
                SimpleDateFormat formatoFechaAlta = new SimpleDateFormat("dd-MM-yyyy");
                String fechaString = formatoFechaAlta.format(empleadoSeleccionado.getFecha_alta());
                controller.datosFechaAlta.setText(fechaString);
                controller.datosDni.setText(empleadoSeleccionado.getDni());
                controller.datosNombre.setText(empleadoSeleccionado.getNombre());
                controller.datosApellido1.setText(empleadoSeleccionado.getApellido1());
                controller.datosApellido2.setText(empleadoSeleccionado.getApellido2());
                controller.datosTelefono.setText(empleadoSeleccionado.getTelefono());
                controller.datosEmail.setText(empleadoSeleccionado.getCorreo());
                controller.datosDireccion.setText(empleadoSeleccionado.getDireccion());
                controller.datosUsuario.setText(empleadoSeleccionado.getDni());
                controller.datosPass.setText(empleadoSeleccionado.getPass());
                int estado = empleadoSeleccionado.getActivo();
                controller.datosEstado.setText(estado == 1 ? "ACTIVO" : "BAJA");

                controller.datosRol.getItems().addAll("ADMIN", "USUARIO");
                if (empleadoSeleccionado.getRoles().getIdrol() == 1) {
                    controller.datosRol.setValue("ADMIN");
                } else {
                    controller.datosRol.setValue("USUARIO");
                }

                //creaamos nova pestaña
                Tab pestana = new Tab(empleadoSeleccionado.getApellido1() + " " + empleadoSeleccionado.getApellido2() + ", " + empleadoSeleccionado.getNombre(), root);
                tabPaneListadoEmpleados.getTabs().add(pestana);
                tabPaneListadoEmpleados.getSelectionModel().select(pestana);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    //Abrir formulario para dar de alta un novo empleado
    public void crearNuevoEmpleado(ActionEvent actionEvent) throws IOException {

        //cargamos formulario
        FXMLLoader loader = new FXMLLoader(FCTApplication.class.getResource("altaempleado.fxml"));
        Parent root = loader.load();

        //abrimos formulario para crear o novo empleado. Apertura en modo modal
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Alta de nuevo empleado");
        stage.initModality(Modality.APPLICATION_MODAL);
        //cargamos folla de estilos
        stage.getScene().getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());
        stage.showAndWait();
    }

    public void exportarDatos(ActionEvent actionEvent) throws IOException, DocumentException {

        Stage stage = (Stage) botonExportar.getScene().getWindow();
        exportarDatos.exportarFormatoExcel(tablaResultadosEmpleados, stage);
    }

    //metodos parar cambiar estilo de botons cando se entra ou sae de eles co rato

    public void entrarBuscar(MouseEvent mouseEvent) {
        botonBuscarEmpleados.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirBUscar(MouseEvent mouseEvent) {
        botonBuscarEmpleados.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarLimpiar(MouseEvent mouseEvent) {
        botonLimpiarCampos.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirLimpiar(MouseEvent mouseEvent) {
        botonLimpiarCampos.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarExportar(MouseEvent mouseEvent) {
        botonExportar.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirExportar(MouseEvent mouseEvent) {
        botonExportar.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarCancelar(MouseEvent mouseEvent) {
        botonCancelarListadoUsuarios.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirCancelar(MouseEvent mouseEvent) {
        botonCancelarListadoUsuarios.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarNuevoEmpleado(MouseEvent mouseEvent) {
        botonNuevoEmpleado.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirNuevoEmpleado(MouseEvent mouseEvent) {
        botonNuevoEmpleado.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }
}
