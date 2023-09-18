package controllers;

import Configuracion.ConfiguracionAplicacion;
import Exportar.ExportarDatos;
import accesodatos.CitasDAO;
import accesodatos.ClientesDAO;
import com.itextpdf.text.DocumentException;
import inicio.FCTApplication;
import javafx.application.Platform;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import modelos.Citas;
import modelos.Clientes;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VistaDatosClienteController {
    @FXML
    public TextField datosDNI;
    @FXML
    public TextField datosNombre;
    @FXML
    public TextField datosApellido1;
    @FXML
    public TextField datosApellido2;
    @FXML
    public TextField datosFechaAlta;
    @FXML
    public TextField datosEstado;
    @FXML
    public TextField datosTelefono;
    @FXML
    public TextField datosEmail;
    @FXML
    public TextField datosDireccion;
    @FXML
    public TextField datosID;
    @FXML
    public Button botonCancelarVistaCliente;
    @FXML
    public TableView tablaHistoricoCitas;
    @FXML
    public Button botonGuardarCliente;
    @FXML
    public Label etiquetaCampoObligatorio;
    @FXML
    public Button botonRestaurar;
    @FXML
    public Button botonBajaCliente;
    @FXML
    public Label formatoCorreoIncorrecto;
    @FXML
    public Button botonExportarCitasCliente;
    @FXML
    public Button enviarCorreo;
    @FXML
    public GridPane gridPaneEncabezado;

    //variable para acceder a base de datos e realziar as consultas
    private ClientesDAO clientesDAO = new ClientesDAO();
    //variables para detectar cambiios nos distintos campos
    private boolean dniModificado = false;
    private boolean nombreModificado = false;
    private boolean apellido1Modificado = false;
    private boolean apellido2Modificado = false;
    private boolean telefonoModificado = false;
    private boolean emailModificado = false;
    private boolean direccionModificado = false;

    //variables para realziar consultas
    CitasDAO citasDAO = new CitasDAO();
    ExportarDatos exportarDatos = new ExportarDatos();
    ConfiguracionAplicacion configuracionAplicacion = new ConfiguracionAplicacion();
    private ListadoClientesController listadoClientesController;
    public void initialize(){
        //icono boton enviar correo
        Image iconoCorreo = new Image(getClass().getResourceAsStream("/images/email.png"));
        enviarCorreo.setGraphic(new ImageView(iconoCorreo));

        //icono boton exportar
        Image iconoExportar = new Image(getClass().getResourceAsStream("/images/export.png"));
        botonExportarCitasCliente.setGraphic(new ImageView(iconoExportar));

        //desactivamos campos e habilitamos restarurar si o cliente está dado de baixa
        Platform.runLater(this::desactivarCamposUsuarioBaixa);

        //establecemos formato mensaxe advertencia campos obligatorios
        etiquetaCampoObligatorio.getStyleClass().add("mensage-advertencia");
        //Creamos evento para solo permitir números no campo teléfono
        datosTelefono.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String telefono = event.getCharacter();
            if (!"0123456789".contains(telefono)) {
                event.consume();
            }
        });

        //inicializamos tabla de datos para o historico de citas
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
                        //creamos Localdate a partir da fecha
                        LocalDateTime localDateTime = LocalDateTime.ofInstant(fecha.toInstant(), ZoneId.systemDefault());


                        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        String fechaFormateada = localDateTime.format(formatoFecha);

                        //establecemos fecha na celda correspodiente
                        setText(fechaFormateada);
                    }
                }
            };
        });
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        TableColumn<Citas, String> comentarioColumn = new TableColumn<>("Comentario");
        comentarioColumn.setCellValueFactory(new PropertyValueFactory<>("comentario"));

        TableColumn<Citas, String> estadoColumn = new TableColumn<>("Estado");
        //metodo para cambiar o que se mostra, a celda teña o valor 1=cancelada 2=pendiente 3=No presentado 4= Finalizada
        estadoColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Citas, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Citas, String> estado) {
                if (estado.getValue().getEstado().getIdestado() == 1) {
                    return new SimpleStringProperty("PENDIENTE");
                } else if (estado.getValue().getEstado().getIdestado() == 2){
                    return new SimpleStringProperty("CANCELADA");
                } else if (estado.getValue().getEstado().getIdestado() == 3) {
                    return new SimpleStringProperty("NO PRESENTADA");
                }else{
                    return new SimpleStringProperty("FINALIZADA");
                }
            }
        });

        //para que as columnas coupen todo o ancho da tabla
        tablaHistoricoCitas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //cargar columnas
        tablaHistoricoCitas.getColumns().addAll(idColumn,fechaColumn, comentarioColumn,estadoColumn);

        //evento doble clik en unha fila da tabla de resultados da búsqueda. Abre unha ventana onde se mostran os datos da cita
        tablaHistoricoCitas.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                // doble clic detectado
                try {
                    abrirCita();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


    }

    //incializamos a variable coa instancia da vista de listado de clientes
    public void ListadoClientesController(ListadoClientesController listadoClientesController) {
        this.listadoClientesController = listadoClientesController;
    }

    //salir do formulario
    public void cancelarVistaCliente(ActionEvent actionEvent) {

        // Referencia a pestaña actual. HBox e o elemento no que esta contenido o boton cancelar
        HBox parent = (HBox) botonCancelarVistaCliente.getParent();
        TabPane tabPane = (TabPane) parent.getScene().getWindow().getScene().getRoot();
        Tab pestanaActual = tabPane.getSelectionModel().getSelectedItem();

        // Cerrar a pestaña actual
        tabPane.getTabs().remove(pestanaActual);
    }

    //método para dar de baixa un cliente
    public void bajaCliente(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Confirmación");
        alert.setContentText("¿Deseas realmente dar de baja el cliente?");
        Optional<ButtonType> action = alert.showAndWait();

        if (action.get() == ButtonType.OK) {
            clientesDAO.cambioEstadoCLiente(Integer.parseInt(datosID.getText()),0);
            citasDAO.cancelarCitasCliente(Integer.parseInt(datosID.getText()));
            datosEstado.setText("BAJA");
            tablaHistoricoCitas.setItems(FXCollections.observableArrayList(citasDAO.getCitasCliente(Integer.parseInt(datosID.getText()))));
            desactivarCamposUsuarioBaixa();

            //actualizamos a tabla da vista principal
            Button botonBuscar = listadoClientesController.botonBuscar;
            botonBuscar.fire();

            Notifications.create()
                    .title("Baja Cliente")
                    .text("El cliente se ha dado de baja correctamente")
                    .hideAfter(Duration.seconds(2))
                    .position(Pos.CENTER)
                    .darkStyle()
                    //.graphic(new ImageView(new Image()))
                    .show();
        }
    }

    public void guardarCambiosCliente(ActionEvent actionEvent) {

        if (nombreModificado || apellido1Modificado || apellido2Modificado || dniModificado || telefonoModificado || emailModificado || direccionModificado) {
            //obtemos os datos dos textfield
            String id = datosID.getText();
            String dni = datosDNI.getText();
            String nombre = datosNombre.getText();
            String apellido1 = datosApellido1.getText();
            String apellido2 = datosApellido2.getText();
            String telefono = datosTelefono.getText();
            String direccion = datosDireccion.getText();
            String correo = datosEmail.getText();

            //comprobamos que todos os campos obligatorios estan cubertos e q o novo dni non existe. si está todo ok actualizase o usuario
            if(todosCamposObligatoriosCubiertos() && validarCorreo()) {
                if(!clientesDAO.existeCliente(datosDNI.getText().trim()) || dniModificado==false){
                    //creamos cliente onde gardamos os datos actualizados
                    Clientes clienteActualizado = new Clientes();
                    clienteActualizado.setIdcliente(Integer.parseInt(id));
                    clienteActualizado.setDni(dni);
                    clienteActualizado.setNombre(nombre);
                    clienteActualizado.setApellido1(apellido1);
                    clienteActualizado.setApellido2(apellido2);
                    clienteActualizado.setTelefono(telefono);
                    clienteActualizado.setDireccion(direccion);
                    clienteActualizado.setCorreo(correo);
                    //realizamos actualización
                    clientesDAO.actualizarCliente(clienteActualizado);
                    //restablecemos a false as variables que nos indican si un valor se modifica e deshabilitamos o boton de gardar
                    dniModificado = false;
                    nombreModificado = false;
                    apellido1Modificado = false;
                    apellido2Modificado = false;
                    telefonoModificado = false;
                    emailModificado = false;
                    direccionModificado = false;
                    botonGuardarCliente.setDisable(true);
                    if(correo.equals("")){
                        enviarCorreo.setDisable(true);
                    }else{
                        enviarCorreo.setDisable(false);
                    }

                    //actualizamos a tabla da vista principal
                    Button botonBuscar = listadoClientesController.botonBuscar;
                    botonBuscar.fire();

                    Notifications.create()
                            .title("Cliente actualizado")
                            .text("El cliente se ha actualizado correctamente")
                            .hideAfter(Duration.seconds(2))
                            .position(Pos.CENTER)
                            .darkStyle()
                            //.graphic(new ImageView(new Image()))
                            .show();

                }else{
                    Alert errorUsuarioExiste = new Alert(Alert.AlertType.ERROR);
                    errorUsuarioExiste.setTitle("Error");
                    errorUsuarioExiste.setHeaderText(null);
                    errorUsuarioExiste.setContentText("\nYa hay un usuario creado con ese DNI");
                    errorUsuarioExiste.showAndWait();
                }
            }
        }

    }

    //metodo para restaurar un cliente dado de baixa
    public void restaurarCliente(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Confirmación");
        alert.setContentText("¿Deseas restaurar el cliente dado de baja?");
        Optional<ButtonType> action = alert.showAndWait();

        if (action.get() == ButtonType.OK) {
            clientesDAO.cambioEstadoCLiente(Integer.parseInt(datosID.getText()),1);
            datosEstado.setText("ALTA");
            activarCamposClienteRestaurado();

            //actualizamos a tabla da vista principal
            Button botonBuscar = listadoClientesController.botonBuscar;
            botonBuscar.fire();

            Notifications.create()
                    .title("Restaurar Cliente")
                    .text("El cliente se ha restaurado correctamente")
                    .hideAfter(Duration.seconds(2))
                    .position(Pos.CENTER)
                    .darkStyle()
                    //.graphic(new ImageView(new Image()))
                    .show();
        }
    }

    //Metodo para comprobar que todos os campos obligatorios estan cubertos, devolve True si é correcto
    public boolean todosCamposObligatoriosCubiertos(){

        boolean todosCamposCubiertos = true;

        //comprobamos si os campos estan cubertos, en caso contrario resaltamos o textfield correspondente
        if(datosDNI.getText().trim().equals("")){
            todosCamposCubiertos = false;
            datosDNI.getStyleClass().add("campo-incorrecto");
        }else{
            datosDNI.getStyleClass().removeAll("campo-incorrecto");
        }

        if(datosNombre.getText().trim().equals("")){
            todosCamposCubiertos = false;
            datosNombre.getStyleClass().add("campo-incorrecto");
        }else{
            datosNombre.getStyleClass().removeAll("campo-incorrecto");
        }

        if(datosApellido1.getText().trim().equals("")){
            todosCamposCubiertos = false;
            datosApellido1.getStyleClass().add("campo-incorrecto");
        }else{
            datosApellido1.getStyleClass().removeAll("campo-incorrecto");
        }

        if(datosApellido2.getText().trim().equals("")){
            todosCamposCubiertos = false;
            datosApellido2.getStyleClass().add("campo-incorrecto");
        }else{
            datosApellido2.getStyleClass().removeAll("campo-incorrecto");
        }

        //si algun campo esta sin cubrir mostramos advertencia, si estan todos cubertos quitamola
        if(todosCamposCubiertos){
            etiquetaCampoObligatorio.setVisible(false);
        }else{
            etiquetaCampoObligatorio.setVisible(true);
        }

        return todosCamposCubiertos;


    }

    public boolean validarCorreo(){

        boolean formatoCorrecto = true;

        Pattern patron = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        Matcher matcher = patron.matcher(datosEmail.getText().trim());
        if(datosEmail.getText().trim() != "") {
            if (matcher.matches()) {
                datosEmail.getStyleClass().removeAll("campo-incorrecto");
                formatoCorreoIncorrecto.setVisible(false);
            } else {
                formatoCorrecto = false;
                datosEmail.getStyleClass().add("campo-incorrecto");
                formatoCorreoIncorrecto.setVisible(true);
            }
        }
        return  formatoCorrecto;
    }

    private void abrirCita() throws IOException {
        Citas cita = (Citas) tablaHistoricoCitas.getSelectionModel().getSelectedItem();
        if(cita != null) {
            //cargamos formulario e obtemos controllador para cargar os datos no formulario que vamos a abrir
            FXMLLoader loader = new FXMLLoader(FCTApplication.class.getResource("vistadatoscita.fxml"));
            Parent root = loader.load();
            VistaDatosCitaController controller = loader.getController();

            controller.idCita.setText(String.valueOf(cita.getIdcita()));
            LocalDateTime localDateTime = LocalDateTime.ofInstant(cita.getFecha().toInstant(), ZoneId.systemDefault());
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fechaFormateada = localDateTime.format(formatoFecha);
            controller.fechaCita.setText(fechaFormateada);
            controller.estadoCita.setText(cita.getEstado().getNombre().toUpperCase());
            controller.idCliente.setText(String.valueOf(cita.getClientes().getIdcliente()));
            controller.nombreCliente.setText(cita.getClientes().getNombre());
            controller.apellidosCliente.setText(cita.getClientes().getApellido1() + " " + cita.getClientes().getApellido2());
            controller.idEmpleado.setText(String.valueOf(cita.getEmpleados().getIdempleado()));
            controller.nombreEmpleado.setText(cita.getClientes().getNombre());
            controller.apellidosEmpleado.setText(cita.getClientes().getApellido1() + " " + cita.getClientes().getApellido2());
            controller.comentarioCita.setText(cita.getComentario());

            //abrimos formulario para ver a cita seleccionada
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setMinWidth(1003);
            stage.setMinHeight(879);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }

        tablaHistoricoCitas.setItems(FXCollections.observableArrayList(citasDAO.getCitasCliente(Integer.parseInt(datosID.getText()))));
    }

    //método  para enviar correos o usuario
    public void enviarCorreoUsuario(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader(FCTApplication.class.getResource("enviocorreocliente.fxml"));
        Parent root = loader.load();
        EnviocorreoclienteController controller = loader.getController();


        //recollemos datos de hora da cita e fecha
        controller.etiquetaDestinatario.setText("Enviar correo a "+datosNombre.getText()+" "+datosApellido1.getText()+" "+datosApellido2.getText());
        controller.direccionCorreo.setText(datosEmail.getText());
        //abrimos formulario para crear a noca cita. Apertura en modo modal
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

    }

    //método para evitar modificacións en clientes dados de baixa. Habilitamos posibilidade de restauralo
    public void desactivarCamposUsuarioBaixa(){
        if(datosEstado.getText().equals("BAJA")) {
            datosDNI.setDisable(true);
            datosNombre.setDisable(true);
            datosApellido1.setDisable(true);
            datosApellido2.setDisable(true);
            datosFechaAlta.setDisable(true);
            datosEstado.setDisable(true);
            datosTelefono.setDisable(true);
            datosEmail.setDisable(true);
            datosDireccion.setDisable(true);
            datosID.setDisable(true);
            botonBajaCliente.setDisable(true);
            botonRestaurar.setDisable(false);
            gridPaneEncabezado.setStyle("-fx-background-color:  #F78383");
            enviarCorreo.setDisable(true);
        }
    }

    //habilitar campos tras restaurar un cliente
    public void activarCamposClienteRestaurado(){

        if(datosEstado.getText().equals("ALTA")) {
            datosDNI.setDisable(false);
            datosNombre.setDisable(false);
            datosApellido1.setDisable(false);
            datosApellido2.setDisable(false);
            datosTelefono.setDisable(false);
            datosEmail.setDisable(false);
            datosDireccion.setDisable(false);
            botonBajaCliente.setDisable(false);
            enviarCorreo.setDisable(false);
            botonRestaurar.setDisable(true);
            gridPaneEncabezado.setStyle("-fx-background-color:   #C7F5D1");
        }

    }

    public void dniModificado(KeyEvent keyEvent) {
        dniModificado = true;
        botonGuardarCliente.setDisable(false);
    }

    public void nombreModificado(KeyEvent keyEvent) {
        nombreModificado = true;
        botonGuardarCliente.setDisable(false);
    }

    public void apellido1Modificado(KeyEvent keyEvent) {
        apellido1Modificado =  true;
        botonGuardarCliente.setDisable(false);
    }

    public void apellido2Modificado(KeyEvent keyEvent) {
        apellido2Modificado =  true;
        botonGuardarCliente.setDisable(false);
    }

    public void telefonoModificado(KeyEvent keyEvent) {
        telefonoModificado =  true;
        botonGuardarCliente.setDisable(false);
    }

    public void emailModificado(KeyEvent keyEvent) {
        emailModificado = true;
        botonGuardarCliente.setDisable(false);
    }

    public void direccionModificado(KeyEvent keyEvent) {
        direccionModificado = true;
        botonGuardarCliente.setDisable(false);
    }

    //método para exportar datos da taboa de resultado
    public void exportarCitas(ActionEvent actionEvent) throws DocumentException, IOException {
        Stage stage = (Stage) botonExportarCitasCliente.getScene().getWindow();
        exportarDatos.exportarFormatoExcel(tablaHistoricoCitas, stage);
    }



    //eventos dos botons para cambiar formato candoentra/sae o rato deles
    public void restaurarEntrar(MouseEvent mouseEvent) {
        botonRestaurar.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void restaurarSalir(MouseEvent mouseEvent) {
        botonRestaurar.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void gardarEntrar(MouseEvent mouseEvent) {
        botonGuardarCliente.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void gardarSalir(MouseEvent mouseEvent) {
        botonGuardarCliente.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void exportarEntrar(MouseEvent mouseEvent) {
        botonExportarCitasCliente.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void exportarSalir(MouseEvent mouseEvent) {
        botonExportarCitasCliente.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");

    }

    public void bajaEntrar(MouseEvent mouseEvent) {
        botonBajaCliente.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void bajaSAlir(MouseEvent mouseEvent) {
        botonBajaCliente.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void cancelarEntrar(MouseEvent mouseEvent) {
        botonCancelarVistaCliente.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void cancelarSalir(MouseEvent mouseEvent) {
        botonCancelarVistaCliente.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void salirBotonCorreo(MouseEvent mouseEvent) {
        enviarCorreo.setStyle("-fx-background-color:  #ADD8E6;-fx-border-width: 1px; -fx-border-color: black;");
    }


    public void entarBotonCorreo(MouseEvent mouseEvent) {
        enviarCorreo.setStyle("-fx-background-color:  #ADD8E6;-fx-border-width: 1px; -fx-border-color: white;");
    }



}


