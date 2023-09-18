package controllers;

import Exportar.ExportarDatos;
import accesodatos.CitasDAO;
import accesodatos.ClientesDAO;
import com.itextpdf.text.DocumentException;
import inicio.FCTApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
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
import javafx.stage.Stage;
import javafx.util.Callback;
import modelos.Clientes;
import modelos.Empleados;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class ListadoClientesController {

    @FXML
    public Button botonCancelarListadoClientes;
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
    public Label etiquetaError;
    @FXML
    public TableView tablaResultados;
    @FXML
    public TabPane tabPaneListadoClientes;
    @FXML
    public Button botonExportarClientes;
    @FXML
    public CheckBox registrosBaja;
    @FXML
    public Button botonBuscar;
    @FXML
    public Button botonLimpiarCampos;


    private ClientesDAO clientesDAO = new ClientesDAO();
    private CitasDAO citasDAO = new CitasDAO();
    private ExportarDatos exportarDatos = new ExportarDatos();
    FilteredList<Clientes> datosFiltrados;

    //variable para gardar a instancia de MenuInicioController
    //necesitamos que a instancia en esta clase sexa a mesma que a que temos en MenuInicioController para que funcione correctamente o metodo cerrarVentanaListadoCLientes()
    private MenuInicioController menuInicioController;

    public void initialize() {

        //icono boton exportar
        Image iconoCorreo = new Image(getClass().getResourceAsStream("/images/export.png"));
        botonExportarClientes.setGraphic(new ImageView(iconoCorreo));
        //inicializamos tabla de datos
        //crear columnas
        TableColumn<Clientes, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idcliente"));
        TableColumn<Clientes, String> dniColumn = new TableColumn<>("DNI");
        dniColumn.setCellValueFactory(new PropertyValueFactory<>("dni"));
        TableColumn<Clientes,  String> nombreColumn = new TableColumn<>("Nombre");
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        TableColumn<Clientes,  String> apellido1Column = new TableColumn<>("Primer Apellido");
        apellido1Column.setCellValueFactory(new PropertyValueFactory<>("apellido1"));
        TableColumn<Clientes,  String> apellido2Column = new TableColumn<>("Segundo Apellido");
        apellido2Column.setCellValueFactory(new PropertyValueFactory<>("apellido2"));
        TableColumn<Clientes,  String> correoColumn = new TableColumn<>("Correo");
        correoColumn.setCellValueFactory(new PropertyValueFactory<>("correo"));
        TableColumn<Clientes,  String> telefonoColumn = new TableColumn<>("Teléfono");
        telefonoColumn.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        TableColumn<Clientes, String> direccionColumn = new TableColumn<>("Dirección");
        direccionColumn.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        TableColumn<Clientes, Date> fechaAltaColumn = new TableColumn<>("Fecha de Alta");
        //cambiar formato da fecha a dd/MM/yyyy
        fechaAltaColumn.setCellFactory(column -> {
            return new TableCell<Clientes, Date>() {
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
        TableColumn<Clientes, String> estadoColumn = new TableColumn<>("Estado");
        //metodo para cambiar o que se mostra, a celda teña o valor 1=activo 0=baja
        estadoColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Clientes, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Clientes, String> estado) {
                if (estado.getValue().getActivo() == 1) {
                    return new SimpleStringProperty("ACTIVO");
                } else {
                    return new SimpleStringProperty("BAJA");
                }
            }
        });
        //para que as columnas ocupen todo o ancho
        tablaResultados.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //cargar columnas
        tablaResultados.getColumns().addAll(idColumn,dniColumn,nombreColumn,apellido1Column,apellido2Column,correoColumn,telefonoColumn,direccionColumn,fechaAltaColumn,estadoColumn);

        //evento doble clik en unha fila da tabla de resultados da búsqueda. Abre unha nova pestaña onde se mosta os datos do cliente seleccionado
        tablaResultados.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                abrirNuevaPestañaCliente();
            }
        });


    }

    public void setMenuInicioController(MenuInicioController controller) {
        menuInicioController = controller;
    }

    //pulsar boton cancelar
    public void cancelarListadoClientes(ActionEvent actionEvent) {

        Scene scene = botonCancelarListadoClientes.getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.close();

        //pasamos a false a variable de ventana de alta aberta para poder abrir unha nova
        menuInicioController.cerrarVentanaListadoClientes();
    }

    public void BuscarRegistros(ActionEvent actionEvent) {

        boolean idCorrecto = true;
        //
        formatoSinErrores();

        //creamos consulta en funcion dos campos que estean cubertos
        String consulta = "";
        if(registrosBaja.isSelected()) {
            consulta = "SELECT c FROM Clientes c WHERE 1=1";
        }else{
            consulta = "SELECT c FROM Clientes c WHERE 1=1 AND activo=1";
        }
        if (!dniBuscar.getText().trim().isEmpty()) {
            consulta += " AND dni LIKE '" + dniBuscar.getText().trim() + "'";
        }
        if (!nombreBuscar.getText().trim().isEmpty()) {
            consulta += " AND nombre LIKE '" + nombreBuscar.getText().trim()+"'";
        }
        if (!apellido1Buscar.getText().trim().isEmpty()) {
            consulta += " AND apellido1 LIKE '" + apellido1Buscar.getText().trim() + "'";
        }
        if (!apellido2Buscar.getText().trim().isEmpty()) {
            consulta += " AND apellido2 LIKE '" + apellido2Buscar.getText().trim() + "'";
        }
        if (!telefonoBuscar.getText().trim().isEmpty()) {
            consulta += " AND telefono LIKE '" + telefonoBuscar.getText().trim() + "'";
        }
        if (!emailBuscar.getText().trim().isEmpty()) {
            consulta += " AND correo LIKE '" + emailBuscar.getText().trim() + "'";
        }
        if (!direccionBuscar.getText().trim().isEmpty()) {
            consulta += " AND direccion LIKE '" + direccionBuscar.getText().trim() + "'";
        }
        if (!idBuscar.getText().trim().isEmpty()) {

            try{
                consulta += " AND idcliente = " + Integer.parseInt(idBuscar.getText().trim());
            }catch (NumberFormatException ex){

                idBuscar.getStyleClass().add("campo-incorrecto");
                etiquetaError.setVisible(true);
                etiquetaError.getStyleClass().add("mensage-advertencia");
                idCorrecto = false;
                tablaResultados.setItems(null);
            }
        }


        if(idCorrecto == true){
            //realizamos consulta e obtemos os resultados en unha lista que mostraremos na tabla resultados
            List<Clientes> resultadoBusqueda = clientesDAO.listadoClientes(consulta);
            tablaResultados.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        }
    }

    //Metodo  para borrar toso os datos dos campos cando se pulsa o boton Limpar
    public void LimpiarCampos(ActionEvent actionEvent) {
        dniBuscar.setText("");
        nombreBuscar.setText("");
        apellido1Buscar.setText("");
        apellido2Buscar.setText("");
        telefonoBuscar.setText("");
        emailBuscar.setText("");
        direccionBuscar.setText("");
        idBuscar.setText("");
        tablaResultados.setItems(null);
        formatoSinErrores();
    }

    //metodo para eliminar advertencia e estilos de formato incorrecto
    private void formatoSinErrores(){
        etiquetaError.setVisible(false);
        etiquetaError.getStyleClass().remove("mensage-advertencia");
        idBuscar.getStyleClass().remove("campo-incorrecto");
    }

    private void abrirNuevaPestañaCliente() {


        try {
            Clientes clienteSeleccionado = (Clientes) tablaResultados.getSelectionModel().getSelectedItem();
            if(clienteSeleccionado != null) {
                //cargamos formulario e obtemos controllador para cargar os datos no formulario que vamos a abrir
                FXMLLoader loader = new FXMLLoader(FCTApplication.class.getResource("vistadatoscliente.fxml"));
                Parent root = loader.load();
                VistaDatosClienteController controller = loader.getController();
                controller.ListadoClientesController(this);


                controller.datosID.setText(String.valueOf(clienteSeleccionado.getIdcliente()));
                SimpleDateFormat formatoFechaAlta = new SimpleDateFormat("dd-MM-yyyy");
                String fechaString = formatoFechaAlta.format(clienteSeleccionado.getFecha_alta());
                controller.datosFechaAlta.setText(fechaString);
                controller.datosDNI.setText(clienteSeleccionado.getDni());
                controller.datosNombre.setText(clienteSeleccionado.getNombre());
                controller.datosApellido1.setText(clienteSeleccionado.getApellido1());
                controller.datosApellido2.setText(clienteSeleccionado.getApellido2());
                controller.datosTelefono.setText(clienteSeleccionado.getTelefono());
                controller.datosEmail.setText(clienteSeleccionado.getCorreo());
                controller.datosDireccion.setText(clienteSeleccionado.getDireccion());
                if(clienteSeleccionado.getCorreo().equals("")){
                    controller.enviarCorreo.setDisable(true);
                }
                int estado = clienteSeleccionado.getActivo();
                controller.datosEstado.setText(estado == 1 ? "ACTIVO" : "BAJA");
                controller.tablaHistoricoCitas.setItems(FXCollections.observableArrayList(citasDAO.getCitasCliente(clienteSeleccionado.getIdcliente())));


                // Crear nueva pestaña
                Tab pestana = new Tab(clienteSeleccionado.getApellido1() + " " + clienteSeleccionado.getApellido2() + ", " + clienteSeleccionado.getNombre() + "   ");
                pestana.setContent(root);
                pestana.setClosable(true);

                tabPaneListadoClientes.getTabs().add(pestana);
                tabPaneListadoClientes.getSelectionModel().select(pestana);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    //exportar datos da taboa
    public void exportarClientes(ActionEvent actionEvent) throws IOException, DocumentException {
        Stage stage = (Stage) botonExportarClientes.getScene().getWindow();
        exportarDatos.exportarFormatoExcel(tablaResultados, stage);
    }

    //eventos dos botons para cambiar formato candoentra/sae o rato deles
    public void cancelarEntrar(MouseEvent mouseEvent) {
        botonCancelarListadoClientes.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void cancelarSalir(MouseEvent mouseEvent) {
        botonCancelarListadoClientes.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void buscarEntrar(MouseEvent mouseEvent) {
       botonBuscar.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void buscarSalir(MouseEvent mouseEvent) {
        botonBuscar.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void limpiarEntrar(MouseEvent mouseEvent) {
        botonLimpiarCampos.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void limpiarSalir(MouseEvent mouseEvent) {
        botonLimpiarCampos.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void exportarEntrar(MouseEvent mouseEvent) {
        botonExportarClientes.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void exportarSalir(MouseEvent mouseEvent) {
        botonExportarClientes.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }
}
