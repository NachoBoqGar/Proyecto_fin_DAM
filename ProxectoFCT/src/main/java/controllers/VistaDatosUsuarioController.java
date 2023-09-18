package controllers;

import accesodatos.EmpleadosDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import modelos.Empleados;
import modelos.Roles;
import org.controlsfx.control.Notifications;

import java.util.Optional;

public class VistaDatosUsuarioController {
    @FXML
    public TextField datosDni;
    @FXML
    public TextField datosNombre;
    @FXML
    public TextField datosApellido1;
    @FXML
    public TextField datosApellido2;
    @FXML
    public TextField datosId;
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
    public TextField datosUsuario;
    @FXML
    public TextField datosPass;
    @FXML
    public ComboBox datosRol;
    @FXML
    public Button botonCancelarUsuario;
    @FXML
    public Button botonGuardar;
    @FXML
    public Label etiquetaCampoObligatorio;
    @FXML
    public Button botonRestaurar;
    @FXML
    public Button botonBajaEmpleado;
    @FXML
    public GridPane gridPaneDatos;

    EmpleadosDAO empleadosDAO = new EmpleadosDAO();

    //variables para detectar cambiios nos distintos campos
    private boolean dniModificado = false;
    private boolean nombreModificado = false;
    private boolean apellido1Modificado = false;
    private boolean apellido2Modificado = false;
    private boolean telefonoModificado = false;
    private boolean emailModificado = false;
    private boolean direccionModificado = false;
    private boolean passModificado = false;
    private boolean rolModificado = false;
    private ListadoEmpleadosController listadoEmpleadosController;

    public void initialize(){
        //establecemos formato mensaxe advertencia campos obligatorios
        etiquetaCampoObligatorio.getStyleClass().add("mensage-advertencia");

        //so permitir números no campo telefono
        datosTelefono.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String telefono = event.getCharacter();
            if (!"0123456789".contains(telefono)) {
                event.consume();
            }
        });

        //desactivamos campos e habilitamos restarurar si o cliente está dado de baixa
        Platform.runLater(this::desactivarCamposEmpleadoBaixa);

    }

    //incializamos a variable coa instancia da vista de listado de empleados
    public void ListadoEmpeladosController(ListadoEmpleadosController controller) {
        this.listadoEmpleadosController = controller;
    }

    //cerrar a pestaña do usuario
    public void cerrarPestanaUsuario(ActionEvent actionEvent) {

        // Referencia a pestaña actual. *HBox e o elemento no que esta contenido o boton cancelar
        HBox parent = (HBox) botonCancelarUsuario.getParent();
        TabPane tabPane = (TabPane) parent.getScene().getWindow().getScene().getRoot();
        Tab pestanaActual = tabPane.getSelectionModel().getSelectedItem();

        // Cerrar a pestaña actual
        tabPane.getTabs().remove(pestanaActual);

    }

    public void guardarCambiosUsuario(ActionEvent actionEvent) {

        if (nombreModificado || apellido1Modificado || apellido2Modificado || dniModificado || telefonoModificado || emailModificado || direccionModificado || passModificado || rolModificado) {
            //obtemos os datos dos distintos campos
            String id = datosId.getText();
            String rol = datosRol.getSelectionModel().getSelectedItem().toString();
            String dni = datosDni.getText();
            String nombre = datosNombre.getText();
            String apellido1 = datosApellido1.getText();
            String apellido2 = datosApellido2.getText();
            String pass = datosPass.getText();
            String telefono = datosTelefono.getText();
            String direccion = datosDireccion.getText();
            String correo = datosEmail.getText();
            //comprobamos que todos os campos obligatorios estan cubertos e q o novo dni non existe. si está ok atualizase o usuario
            if(todosCamposObligatoriosCubiertos()) {
                if(!empleadosDAO.existeEmpleado(datosDni.getText().trim()) || dniModificado==false){
                    //creamos cliente onde gardamos os datos actualizados
                    Empleados empleadoActualizado = new Empleados();
                    empleadoActualizado.setIdempleado(Integer.parseInt(id));
                    empleadoActualizado.setDni(dni);
                    empleadoActualizado.setNombre(nombre);
                    empleadoActualizado.setApellido1(apellido1);
                    empleadoActualizado.setApellido2(apellido2);
                    empleadoActualizado.setTelefono(telefono);
                    empleadoActualizado.setDireccion(direccion);
                   // empleadoActualizado.setCorreo(correo);
                    empleadoActualizado.setPass(pass);
                    System.out.println(empleadoActualizado.getDireccion());
                    Roles novoRol = new Roles ();
                    if (rol.equals("ADMIN")) {
                        novoRol.setIdrol(1);
                        empleadoActualizado.setRoles(novoRol);
                    } else {
                        novoRol.setIdrol(2);
                        empleadoActualizado.setRoles(novoRol);
                    }
                    //realizamos actualización
                    empleadosDAO.actualizarEmpleado(empleadoActualizado);
                    //restablecemos a false as variables que nos indican si un valor se modifica e deshabilitamos o boton de gardar
                    dniModificado = false;
                    nombreModificado = false;
                    apellido1Modificado = false;
                    apellido2Modificado = false;
                    telefonoModificado = false;
                    emailModificado = false;
                    direccionModificado = false;
                    passModificado = false;
                    rolModificado = false;
                    botonGuardar.setDisable(true);

                    Notifications.create()
                            .title("Usuario actualizado")
                            .text("El usuario se ha actualizado correctamente")
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

    //Metodo para comprobar que todos os campos obligatorios estan cubertos, devolve True si é correcto
    public boolean todosCamposObligatoriosCubiertos(){

        boolean todosCamposCubiertos = true;

        //comprobamos si os campos estan cubertos, en caso contrario resaltamos o textfield correspondente
        if(datosDni.getText().trim().equals("")){
            todosCamposCubiertos = false;
            datosDni.getStyleClass().add("campo-incorrecto");
        }else{
            datosDni.getStyleClass().removeAll("campo-incorrecto");
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
        if(datosPass.getText().trim().equals("")){
            todosCamposCubiertos = false;
            datosPass.getStyleClass().add("campo-incorrecto");
        }else{
            datosPass.getStyleClass().removeAll("campo-incorrecto");
        }

        //si algun campo esta sin cubrir mostramos advertencia, si estan todos cubertos quitamola
        if(todosCamposCubiertos){
            etiquetaCampoObligatorio.setVisible(false);
        }else{
            etiquetaCampoObligatorio.setVisible(true);
        }

        return todosCamposCubiertos;


    }

    //método para dar de baixa un empleado
    public void darBajaEmpleado(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Confirmación");
        alert.setContentText("¿Deseas realmente dar de baja el empleado?");
        Optional<ButtonType> action = alert.showAndWait();


        if (action.get() == ButtonType.OK) {
            empleadosDAO.cambioEstadoEmpleado(Integer.parseInt(datosId.getText()),0);
            datosEstado.setText("BAJA");
            desactivarCamposEmpleadoBaixa();
            gridPaneDatos.setStyle("-fx-background-color:  #F78383");
            //actualizamos a tabla da vista principal
            Button botonBuscar = listadoEmpleadosController.botonBuscarEmpleados;
            botonBuscar.fire();

            Notifications.create()
                    .title("Baja Empleado")
                    .text("El empleado se ha dado de baja correctamente")
                    .hideAfter(Duration.seconds(2))
                    .position(Pos.CENTER)
                    .darkStyle()
                    //.graphic(new ImageView(new Image()))
                    .show();
        }
    }

    //metodo para restaurar un empleado dado de baixa
    public void restaurarEmpleado(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Confirmación");
        alert.setContentText("¿Deseas restaurar el empleado dado de baja?");
        Optional<ButtonType> action = alert.showAndWait();
        gridPaneDatos.setStyle("-fx-background-color:   #C7F5D1");

        if (action.get() == ButtonType.OK) {
            empleadosDAO.cambioEstadoEmpleado(Integer.parseInt(datosId.getText()),1);
            datosEstado.setText("ALTA");
            activarCamposEmpleadoRestaurado();

            //actualizamos a tabla da vista principal
            Button botonBuscar = listadoEmpleadosController.botonBuscarEmpleados;
            botonBuscar.fire();

            Notifications.create()
                    .title("Restaurar Empleado")
                    .text("El empleado se ha restaurado correctamente")
                    .hideAfter(Duration.seconds(2))
                    .position(Pos.CENTER)
                    .darkStyle()
                    //.graphic(new ImageView(new Image()))
                    .show();
        }
    }

    //método para evitar modificacións en usuarios dados de baixa. Habilitamos posibilidade de restauralo
    public void desactivarCamposEmpleadoBaixa(){
        if(datosEstado.getText().equals("BAJA")) {
            datosDni.setDisable(true);
            datosPass.setDisable(true);
            datosNombre.setDisable(true);
            datosRol.setDisable(true);
            datosApellido1.setDisable(true);
            datosApellido2.setDisable(true);
            datosFechaAlta.setDisable(true);
            datosEstado.setDisable(true);
            datosTelefono.setDisable(true);
            datosEmail.setDisable(true);
            datosDireccion.setDisable(true);
            datosId.setDisable(true);
            botonBajaEmpleado.setDisable(true);
            botonRestaurar.setDisable(false);
        }
    }

    //habilitar campos tras restaurar un empleado
    public void activarCamposEmpleadoRestaurado(){

        if(datosEstado.getText().equals("ALTA")) {
            datosDni.setDisable(false);
            datosPass.setDisable(false);
            datosNombre.setDisable(false);
            datosApellido1.setDisable(false);
            datosApellido2.setDisable(false);
            datosTelefono.setDisable(false);
            datosRol.setDisable(false);
            datosEmail.setDisable(false);
            datosDireccion.setDisable(false);
            botonBajaEmpleado.setDisable(false);
            botonRestaurar.setDisable(true);
        }

    }



    //métodos para detectar modificacións nos distintos campos
    public void dniModificado(KeyEvent keyEvent) {
        dniModificado = true;
        botonGuardar.setDisable(false);
    }

    public void nombreModificado(KeyEvent keyEvent) {
        nombreModificado = true;
        botonGuardar.setDisable(false);
    }

    public void apellido1Modificado(KeyEvent keyEvent) {
        apellido1Modificado =  true;
        botonGuardar.setDisable(false);
    }

    public void apellido2Modificado(KeyEvent keyEvent) {
        apellido2Modificado =  true;
        botonGuardar.setDisable(false);
    }

    public void telefonoModificado(KeyEvent keyEvent) {
        telefonoModificado =  true;
        botonGuardar.setDisable(false);
    }

    public void emailModificado(KeyEvent keyEvent) {
        emailModificado = true;
        botonGuardar.setDisable(false);
    }

    public void direccionModificado(KeyEvent keyEvent) {
        direccionModificado = true;
        botonGuardar.setDisable(false);
    }


    public void passModificado(KeyEvent keyEvent) {
        passModificado = true;
        botonGuardar.setDisable(false);
    }

    public void rolModificado(ActionEvent actionEvent) {
        rolModificado = true;
        botonGuardar.setDisable(false);
    }


    //eventos dos botons para cambiar formato candoentra/sae o rato deles

    public void entrarRestaurar(MouseEvent mouseEvent) {
        botonRestaurar.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirRestaurar(MouseEvent mouseEvent) {
        botonRestaurar.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarGuardar(MouseEvent mouseEvent) {
        botonGuardar.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirGuardar(MouseEvent mouseEvent) {
        botonGuardar.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarBaja(MouseEvent mouseEvent) {
        botonBajaEmpleado.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirBaja(MouseEvent mouseEvent) {
        botonBajaEmpleado.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarCancelar(MouseEvent mouseEvent) {
        botonCancelarUsuario.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirCancelar(MouseEvent mouseEvent) {
        botonCancelarUsuario.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }
}
