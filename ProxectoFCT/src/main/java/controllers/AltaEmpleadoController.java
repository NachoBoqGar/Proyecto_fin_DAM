package controllers;

import accesodatos.EmpleadosDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelos.Clientes;
import modelos.Empleados;
import modelos.Roles;
import org.controlsfx.control.Notifications;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AltaEmpleadoController {


    @FXML
    public TextField datosDni;
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
    public ComboBox datosRol;
    @FXML
    public TextField datosTelefono;
    @FXML
    public TextField datosEmail;
    @FXML
    public TextField datosDireccion;
    @FXML
    public TextField datosPass;
    @FXML
    public Label etiquetaCampoObligatorio;
    @FXML
    public Button botonCancelarAltaEmpleado;
    @FXML
    public Label formatoCorreoIncorrecto;
    public Button botonGuardarEmpleado;


    EmpleadosDAO empleadosDAO = new EmpleadosDAO();

    public void initialize(){
        //inicializamos combobox de roles
        datosRol.getItems().addAll("ADMIN","USUARIO");
        datosRol.setValue("USUARIO");


        //inicializamos o campo fecha alta a data actual e o campo estado a Activo
        //establecemos o formato da mesaxe de advertencia si faltan campos obligatorios
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        datosFechaAlta.setText(LocalDate.now().format(formatoFecha));
        datosEstado.setText("ACTIVO");
        //formato etiqueta campos obligatorios
        etiquetaCampoObligatorio.getStyleClass().add("mensage-advertencia");

        //Creamos evento para solo permitir números no campo teléfono
        datosTelefono.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String telefono = event.getCharacter();
            if (!"0123456789".contains(telefono)) {
                event.consume();
            }
        });
    }

    //cerrar ventana de alta de empleado
    public void cerrarAltaEmpleado(ActionEvent actionEvent) {

        Scene scene = botonCancelarAltaEmpleado.getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.close();
    }

    //dar de alta novo empleado
    public void altaNuevoEmpleado(ActionEvent actionEvent) throws ParseException {

        //obtemos os valores dos distintos campos
        //realizamos cambio de formato da fecha e convertimola a tipo Date para usalo na consulta
        java.sql.Date fechaSql = fechaFormateada ();
        int activo = 1;
        String dni = datosDni.getText().trim();
        String nombre = datosNombre.getText().trim();
        String apellido1 = datosApellido1.getText().trim();
        String apellido2 = datosApellido2.getText().trim();
        String telefono = datosTelefono.getText().trim();
        String email = datosEmail.getText().trim();
        String direccion = datosDireccion.getText().trim();
        String pass = datosPass.getText().trim();
        Roles rol = new Roles();
        if (datosRol.getSelectionModel().getSelectedItem().toString().equals("ADMIN")) {
            rol.setIdrol(1);
        } else {
            rol.setIdrol(2);
        }
        //verificamos que todos os campos obligatorios estan cubertos
        if(todosCamposObligatoriosCubiertos() && validarCorreo()) {
            System.out.println(dni);
            //verificamos si existe o empleado na base de datos comprobando o DNI (campo único)
            if(!empleadosDAO.existeEmpleado(datosDni.getText().trim())){
                //creamos cliente cos datos introducimos e realizamos insert na base de datos
                Empleados empleado = new Empleados(dni, nombre, apellido1, apellido2, email,fechaSql,pass, direccion,telefono, activo,rol);
                empleadosDAO.altaNuevoEmpleado(empleado);

                //notificacion emergente de operacion correcta
                Notifications.create()
                        .title("Alta de Empleado")
                        .text("El empleado se ha dado de alta correctamente")
                        .hideAfter(Duration.seconds(8))
                        .position(Pos.CENTER)
                        .darkStyle()
                        //.graphic(new ImageView(new Image("images/DSCF2148.JPG")))
                        .show();

                //cerramos ventana de creación de empleado
                Scene scene = botonCancelarAltaEmpleado.getScene();
                Stage stage = (Stage) scene.getWindow();
                stage.close();


            }else{
                Alert errorUsuarioExiste = new Alert(Alert.AlertType.ERROR);
                errorUsuarioExiste.setTitle("Error");
                errorUsuarioExiste.setHeaderText(null);
                errorUsuarioExiste.setContentText("\nYa hay un empleado creado con ese DNI");
                errorUsuarioExiste.showAndWait();
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

    //metodo para comprobar o formato do correo
    public boolean validarCorreo(){

        boolean formatoCorrecto = true;
        System.out.println("1111111111111111111");
        Pattern patron = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        Matcher matcher = patron.matcher(datosEmail.getText().trim());
        if(datosEmail.getText().trim() != "") {
            if (matcher.matches()) {
                datosEmail.getStyleClass().removeAll("campo-incorrecto");
                formatoCorreoIncorrecto.setVisible(false);
                System.out.println("holaaaaaaa");
            } else {
                System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzz");
                formatoCorrecto = false;
                datosEmail.getStyleClass().add("campo-incorrecto");
                formatoCorreoIncorrecto.setVisible(true);
                formatoCorreoIncorrecto.getStyleClass().add("mensage-advertencia");
            }
        }
        return  formatoCorrecto;
    }

    //metodo para cambiar de formato a fecha
    public java.sql.Date fechaFormateada () throws ParseException {

        String fechaAltaString = datosFechaAlta.getText();
        SimpleDateFormat formatoLeido = new SimpleDateFormat("dd-MM-yyyy");
        Date fecha = formatoLeido.parse(fechaAltaString);
        SimpleDateFormat formatoFechaAlta = new SimpleDateFormat("yyyy-MM-dd");
        String fechaSqlString = formatoFechaAlta.format(fecha);
        java.sql.Date fechaSql = java.sql.Date.valueOf(fechaSqlString);

        return  fechaSql;
    }

    //aplicar estilos cando se entra sale dos botóns cancelar e gardar
    public void cancelarEntrarAltaEmpleado(MouseEvent mouseEvent) {
        botonCancelarAltaEmpleado.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirCancelarAltaEmpleado(MouseEvent mouseEvent) {
        botonCancelarAltaEmpleado.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarGardarEMpleado(MouseEvent mouseEvent) {
        botonGuardarEmpleado.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirGardarEmpleado(MouseEvent mouseEvent) {
        botonGuardarEmpleado.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }
}



