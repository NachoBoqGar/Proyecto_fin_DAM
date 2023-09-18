package controllers;

import Exportar.ExportarDatos;
import accesodatos.CitasDAO;
import accesodatos.EmpleadosDAO;
import accesodatos.EstadosDAO;
import com.itextpdf.text.DocumentException;
import inicio.FCTApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import modelos.Citas;
import modelos.Clientes;
import modelos.Empleados;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListaCitasController {
    @FXML
    public DatePicker fechaDesde;
    @FXML
    public DatePicker fechaHasta;

    @FXML
    public Label fechaCitasListadas;
    @FXML
    public CheckBox checkPendiente;
    @FXML
    public CheckBox checkCancelada;
    @FXML
    public CheckBox checkNoPresentado;
    @FXML
    public CheckBox checkFinalizada;
    @FXML
    public Button botonCancelarListadoCitas;
    @FXML
    public Button botonBuscarCitas;
    @FXML
    public Button botonLimpiarCampos;
    @FXML
    public ListView listaEmpleados;
    @FXML
    public TableView tablaResultados;
    @FXML
    public TabPane tabPaneListadoCitas;
    @FXML
    public Button botonExportarCitas;
    ExportarDatos exportarDatos = new ExportarDatos();

    //listas para gardar os estados  e os empleados seleccionados
    List<Integer> listaEstadosSeleccionados =  new ArrayList<>();
    List<Integer> listaEmpleadosSeleccionados = new ArrayList<>();

    //variables para realizar consultas
    EmpleadosDAO empleadosDAO = new EmpleadosDAO();
    CitasDAO citasDAO = new CitasDAO();

    EstadosDAO estadosDAO = new EstadosDAO();

    //variable para obter a instancia do menñu inicial e cambiar a variableventanaCitasAberta no menú inicial cando se peche esta ventana
    private MenuInicioController menuInicioController;

    public void initialize(){


        //icono boton exportar
        Image iconoExportar = new Image(getClass().getResourceAsStream("/images/export.png"));
        botonExportarCitas.setGraphic(new ImageView(iconoExportar));
        //inicializamos lista de empleados ee stablecemos selección multiple
        List<Empleados> empleadosSeleccionados = empleadosDAO.listadoEmpleados("SELECT e FROM Empleados e");
        for (Empleados empleado : empleadosSeleccionados) {

            listaEmpleados.getItems().add(empleado.getIdempleado()+" - "+empleado.getNombre()+" "+empleado.getApellido1()+" "+empleado.getApellido2());
        }
        listaEmpleados.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //inicializamos tabla de datos
        //crear columnas
        TableColumn<Citas, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idcita"));
        TableColumn<Citas, Date> fechaColumn = new TableColumn<>("Fecha");
        //cambiar formato da fecha a dd/MM/yyyy HH:mm:ss
        fechaColumn.setCellFactory(column -> { return new TableCell<Citas, Date>() {
                @Override
                protected void updateItem(Date fecha, boolean empty) {
                    super.updateItem(fecha, empty);
                    if (fecha == null || empty) {
                        setText(null);
                    } else {
                        //creamos Localdate a partir da fecha e cambiamos formato
                        LocalDateTime localDateTime = LocalDateTime.ofInstant(fecha.toInstant(), ZoneId.systemDefault());
                        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        String fechaFormateada = localDateTime.format(formatoFecha);

                        //establecemos fecha na celda correspodiente
                        setText(fechaFormateada);
                    }
                }
            };
        });
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        TableColumn<Citas, String> comentarioColumn = new TableColumn<>("Comentario");
        comentarioColumn.setCellValueFactory(new PropertyValueFactory<>("comentario"));
        TableColumn<Citas, String> clienteColumn = new TableColumn<>("Cliente");
        clienteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Citas, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Citas, String> cita) {
                //creamos cliente co id da cita para mostrar os seus datos
                Clientes cliente = cita.getValue().getClientes(); ;
                String datosCliente = cliente.getNombre()+" "+cliente.getApellido1()+" "+cliente.getApellido2();
                return new SimpleStringProperty(datosCliente);
            }
        });
        TableColumn<Citas, String> empleadoColumn = new TableColumn<>("Empleado");
        empleadoColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Citas, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Citas, String> cita) {

                Empleados empleado = cita.getValue().getEmpleados(); ;
                String datosEmpleado = empleado.getNombre()+" "+empleado.getApellido1()+" "+empleado.getApellido2();
                return new SimpleStringProperty(datosEmpleado);
            }
        });
        TableColumn<Citas, String> estadoColumn = new TableColumn<>("Estado");
        estadoColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Citas, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Citas, String> cita) {

                if (cita.getValue().getEstado().getIdestado() == 1) {
                    return new SimpleStringProperty("PENDIENTE");
                } else if (cita.getValue().getEstado().getIdestado() == 2) {
                    return new SimpleStringProperty("CANCELADA");
                } else if (cita.getValue().getEstado().getIdestado() == 3) {
                    return new SimpleStringProperty("NO PRESENTADO");
                }else{
                    return new SimpleStringProperty("FINALIZADA");
                }
            }
        });


        //para que as columnas ocupen ancho total da tabla
        tablaResultados.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //cargar columnas
        tablaResultados.getColumns().addAll(idColumn,fechaColumn,comentarioColumn,clienteColumn,empleadoColumn,estadoColumn);

        //evento doble clik en unha fila da tabla de resultados da búsqueda. Abre unha nova pestaña onde se mostra os datos da cita seleccionada
        tablaResultados.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                try {
                    abrirNuevaPestanaCita();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // eventos para evitar que fechadesde sexa posterior a fecha hasta e que fechahasta sexa anterior a fechadesde
        fechaDesde.setOnAction(event -> {
            if (fechaDesde.getValue() != null && fechaHasta.getValue() != null && fechaDesde.getValue().isAfter(fechaHasta.getValue())) {
                fechaHasta.setValue(fechaDesde.getValue());
            }
        });

        fechaHasta.setOnAction(event -> {
            if (fechaHasta.getValue() != null && fechaDesde.getValue() !=null && fechaHasta.getValue().isBefore(fechaDesde.getValue())) {
                fechaDesde.setValue(fechaHasta.getValue());
            }
        });



    }

    public void setMenuInicioController(MenuInicioController controller) {
        menuInicioController = controller;
    }

    //Accion boton cancelar.Cerrase a ventana
    public void cerrarVentanaListadoCitas(ActionEvent actionEvent) {

        Scene scene = botonCancelarListadoCitas.getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.close();
        //pasamos a false a variable de ventana de listado de citas aberta para poder abrir unha nova
        menuInicioController.cerrarVentanaListadoCitas();

    }

    public void limpiarCampos(ActionEvent actionEvent) {
        fechaDesde.setValue(null);
        fechaHasta.setValue(null);
        listaEmpleados.getSelectionModel().clearSelection();
        checkCancelada.setSelected(false);
        checkFinalizada.setSelected(false);
        checkPendiente.setSelected(false);
        checkNoPresentado.setSelected(false);
        listaEstadosSeleccionados.clear();
        listaEmpleadosSeleccionados.clear();
        tablaResultados.setItems(null);
    }

    public void buscarCitas(ActionEvent actionEvent) throws ParseException {
        //reinicamos as listas para gardar os id
        listaEstadosSeleccionados.clear();
        listaEmpleadosSeleccionados.clear();

        Date fechaDesdeSeleccionada = null;
        if(fechaDesde.getValue()!=null) fechaDesdeSeleccionada = fechaFormateada(fechaDesde.getValue().toString());
        Date fechaHastaSeleccionada = null;
        if(fechaHasta.getValue()!=null) fechaHastaSeleccionada = fechaFormateada(fechaHasta.getValue().toString());

        //gardamos na lista os estados seleccionados
        if (checkPendiente.isSelected()) {
            listaEstadosSeleccionados.add(1);
        }
        if (checkCancelada.isSelected()) {
            listaEstadosSeleccionados.add(2);
        }
        if (checkNoPresentado.isSelected()) {
            listaEstadosSeleccionados.add(3);
        }
        if (checkFinalizada.isSelected()) {
            listaEstadosSeleccionados.add(4);
        }

        //obtemos os ID dos empleados seleccionados
        List<String> empleados = listaEmpleados.getSelectionModel().getSelectedItems();
        for (String empleadoSeleccionado : empleados) {

            String[] parts = empleadoSeleccionado.split(" - ");
            String idEmpleado = parts[0];
            listaEmpleadosSeleccionados.add(Integer.parseInt(idEmpleado));
        }


        List<Citas> citas = citasDAO.consultarCitas(fechaDesdeSeleccionada,fechaHastaSeleccionada, listaEstadosSeleccionados,listaEmpleadosSeleccionados);
        tablaResultados.setItems(FXCollections.observableArrayList(citas));

    }

    //abrirr pestaña cita seleccionada con doble click
    private void abrirNuevaPestanaCita() throws IOException {
        try{
            Citas cita = (Citas) tablaResultados.getSelectionModel().getSelectedItem();
            if(cita != null) {
                //cargamos formulario e obtemos controllador para cargar os datos no formulario que vamos a abrir
                FXMLLoader loader = new FXMLLoader(FCTApplication.class.getResource("vistadatoscita.fxml"));
                Parent root = loader.load();
                VistaDatosCitaController controller = loader.getController();
                controller.ListaCitasController(this);

                controller.idCita.setText(String.valueOf(cita.getIdcita()));
                LocalDateTime localDateTime = LocalDateTime.ofInstant(cita.getFecha().toInstant(), ZoneId.systemDefault());
                DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String fechaFormateada = localDateTime.format(formatoFecha);
                controller.fechaCita.setText(fechaFormateada);
                controller.estadoCita.setText(cita.getEstado().getNombre().toUpperCase());
                controller.idCliente.setText(String.valueOf(cita.getClientes().getIdcliente()));
                controller.nombreCliente.setText(cita.getClientes().getNombre());
                controller.apellidosCliente.setText(cita.getClientes().getApellido1() + " " + cita.getClientes().getApellido2());
                controller.idEmpleado.setText(String.valueOf(cita.getEmpleados().getIdempleado()));
                controller.nombreEmpleado.setText(cita.getEmpleados().getNombre());
                controller.apellidosEmpleado.setText(cita.getEmpleados().getApellido1() + " " + cita.getEmpleados().getApellido2());
                controller.comentarioCita.setText(cita.getComentario());

                // Crear nueva pestaña
                Tab pestana = new Tab();
                pestana.setContent(root);


                Button cerrarPestana = new Button("X");
                HBox tituloPestana = new HBox();
                tituloPestana.setAlignment(Pos.CENTER);
                tituloPestana.getChildren().addAll(new Label(fechaFormateada + "   " + cita.getClientes().getNombre() + " " + cita.getClientes().getApellido1() + " " + cita.getClientes().getApellido2() + "  "), cerrarPestana);
                pestana.setGraphic(tituloPestana);

                //accion do boton cerrar pestaña
                cerrarPestana.setOnAction(event -> {
                    tabPaneListadoCitas.getTabs().remove(pestana);
                });

                tabPaneListadoCitas.getTabs().add(pestana);
                tabPaneListadoCitas.getSelectionModel().select(pestana);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    //cambiar feccha a formato yyyy-mm-dd
    public java.sql.Date fechaFormateada(String fecha) throws ParseException {

        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date fechaUtil = formatoFecha.parse(fecha);
        java.sql.Date fechaSqlCita = new java.sql.Date(fechaUtil.getTime());
        return fechaSqlCita;
    }


    //exportar datos da taboa
    public void exportarCitas(ActionEvent actionEvent) throws DocumentException, IOException {
        Stage stage = (Stage) botonExportarCitas.getScene().getWindow();
        exportarDatos.exportarFormatoExcel(tablaResultados, stage);
    }

    public void entrarExportar(MouseEvent mouseEvent) {
        botonExportarCitas.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirExportar(MouseEvent mouseEvent) {
        botonExportarCitas.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarLimpiar(MouseEvent mouseEvent) {
        botonLimpiarCampos.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirLimpiar(MouseEvent mouseEvent) {
        botonLimpiarCampos.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarBuscar(MouseEvent mouseEvent) {
        botonBuscarCitas.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirBuscar(MouseEvent mouseEvent) {
        botonBuscarCitas.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarCamcelar(MouseEvent mouseEvent) {
        botonCancelarListadoCitas.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirCancelar(MouseEvent mouseEvent) {
        botonCancelarListadoCitas.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }
}
