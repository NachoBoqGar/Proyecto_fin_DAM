package controllers;

import accesodatos.CitasDAO;
import accesodatos.ClientesDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import modelos.Clientes;

import java.io.IOException;
import java.text.ParseException;


public class BuscarClienteCitaController {
    @FXML
    public TextField dniBuscar;
    @FXML
    public TextField nombreBuscar;
    @FXML
    public TextField apellido1Buscar;
    @FXML
    public TextField apellido2Buscar;
    @FXML
    public Button botonCancelarBuscarCLiente;
    @FXML
    public TableView tablaResultados;
    @FXML
    public TextField buscarPorColumna;
    @FXML
    public Button limpiarDatosCLiente;
    @FXML
    public Button buscarCliente;
    @FXML
    public Button botonSeleccionarCliente;

    private AltaCitaController altaCitaController;
    private CitasDAO citasDAO = new CitasDAO();

    private ClientesDAO clientesDAO = new ClientesDAO();
    FilteredList<Clientes> datosFiltrados;

    public void initialize (){
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
        //para que as columnas coupen todo o ancho da tabla
        tablaResultados.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //cargar columnas
        tablaResultados.getColumns().addAll(idColumn,dniColumn,nombreColumn,apellido1Column,apellido2Column,correoColumn,telefonoColumn,direccionColumn);

        //evento doble clik en unha fila da tabla de resultados da búsqueda. Abre unha nova pestaña onde se mosta os datos do cliente seleccionado
        tablaResultados.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {

                try {
                    cargarDatosClienteSeleccionado();
                } catch (IOException | ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //listener para buscar por columna a medida que se vai introducindo tecto no textfield
        buscarPorColumna.textProperty().addListener((observable, oldValue, newValue) -> {
            datosFiltrados.setPredicate(cliente -> {
                // mostrar tabla completa si non hai texto a buscar
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                //vamos comprobamondo columna e devolvese true si coincide
                String textoBusqueda = newValue.toLowerCase();
                if (cliente.getNombre() != null && cliente.getNombre().toLowerCase().contains(textoBusqueda)) {
                    return true;
                } else if (cliente.getApellido1() != null && cliente.getApellido1().toLowerCase().contains(textoBusqueda)) {
                    return true;
                } else if (cliente.getApellido2() != null && cliente.getApellido2().toLowerCase().contains(textoBusqueda)) {
                    return true;
                } else if (cliente.getDni() != null && cliente.getDni().toLowerCase().contains(textoBusqueda)) {
                    return true;
                } else if (cliente.getDireccion() != null && cliente.getDireccion().toLowerCase().contains(textoBusqueda)) {
                    return true;
                } else if (cliente.getTelefono() != null && cliente.getTelefono().toLowerCase().contains(textoBusqueda)) {
                    return true;
                } else if (cliente.getCorreo() != null && cliente.getCorreo().toLowerCase().contains(textoBusqueda)) {
                    return true;
                }

                return false;
            });
        });

    }

    //inicializamos a variable vistaCitaController coa instancia que pasamos desde o formulario de altacita.fxml
    public void setAltaCitaController(AltaCitaController controller) {
        this.altaCitaController = controller;
    }

    //cerramos ventana sin ejecutar nada
    public void cancelarBuscarCliente(ActionEvent actionEvent) {
        Scene scene = botonCancelarBuscarCLiente.getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.close();
    }

    //accion do boton seleccionar cliente
    public void seleccionarCliente() throws IOException, ParseException {
        cargarDatosClienteSeleccionado();
    }

    //borramos todos os datos dos distintos compoñentes
    public void limpiarDatosCliente(ActionEvent actionEvent) {
        dniBuscar.setText("");
        nombreBuscar.setText("");
        apellido1Buscar.setText("");
        apellido2Buscar.setText("");
        tablaResultados.setItems(null);
    }

    //accion do boton de buscar. Realzimos consulta en funcion dos datos introducidos
    public void buscarCliente(ActionEvent actionEvent) {

        String consulta = "SELECT c FROM Clientes c WHERE 1=1";
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
        //creamos unha ObservableList<Clientes> para mostrar datos, creamos unha filtered list utilizada para filtrado por columna. Incialmente mostranse todos os resultados
        ObservableList<Clientes> datosTabla = FXCollections.observableArrayList(clientesDAO.listadoClientes(consulta));
        datosFiltrados = new FilteredList<>(datosTabla, p -> true);
        tablaResultados.setItems(datosFiltrados);
    }

    //cargamos os datos do cliente seleccionado no formulario altacita. SI xa ten unha cita creada pra esa fecha e hora non o permitimos
    private void cargarDatosClienteSeleccionado() throws IOException, ParseException {

        Clientes clienteSeleccionado = (Clientes) tablaResultados.getSelectionModel().getSelectedItem();

        if(!citasDAO.citaUsuarioFecha(clienteSeleccionado.getIdcliente(),altaCitaController.fechaFormateadaCita())) {
            altaCitaController.idClienteCita.setText(String.valueOf(clienteSeleccionado.getIdcliente()));
            altaCitaController.nombreCliente.setText(clienteSeleccionado.getNombre());
            altaCitaController.apellidosCliente.setText(clienteSeleccionado.getApellido1() + " " + clienteSeleccionado.getApellido2());
            altaCitaController.correoUsuario = clienteSeleccionado.getCorreo();
            altaCitaController.labelSeleccionarCliente.setVisible(false);
            Scene scene = botonCancelarBuscarCLiente.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.close();
        }else{

            Alert errorUsuarioExiste = new Alert(Alert.AlertType.ERROR);
            errorUsuarioExiste.setTitle("Error");
            errorUsuarioExiste.setHeaderText(null);
            errorUsuarioExiste.setContentText("\nEl cliente ya tiene programada una cita para esa fecha y hora");
            errorUsuarioExiste.showAndWait();
        }

    }


    public void entrarBotonLimpiar(MouseEvent mouseEvent) {
        limpiarDatosCLiente.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirBotonLimpiar(MouseEvent mouseEvent) {
        limpiarDatosCLiente.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarBotonBuscar(MouseEvent mouseEvent) {
        buscarCliente.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirBotonBuscar(MouseEvent mouseEvent) {
        buscarCliente.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarBotonSeleccionar(MouseEvent mouseEvent) {
        botonSeleccionarCliente.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirBotonSeleccionar(MouseEvent mouseEvent) {
        botonSeleccionarCliente.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void cancelarBotonEntrar(MouseEvent mouseEvent) {
        botonCancelarBuscarCLiente.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void cancelarBotonSalir(MouseEvent mouseEvent) {
        botonCancelarBuscarCLiente.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }


}
