package controllers;

import accesodatos.ClientesDAO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import modelos.Citas;
import modelos.Clientes;
import org.controlsfx.control.Notifications;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AltaClienteController {
    @FXML
    public Button botonCancelarCliente;
    @FXML
    public TextField datosDNI;
    @FXML
    public TextField datosNombre;
    @FXML
    public TextField datosApellido1;
    @FXML
    public TextField datosApellido2;
    @FXML
    public TextField datosTelefono;
    @FXML
    public TextField datosEmail;
    @FXML
    public TextField datosDireccion;
    @FXML
    public TextField datosFechaAlta;
    @FXML
    public TextField datosEstado;
    @FXML
    public GridPane gridPaneDatosPersonales;
    @FXML
    public TableView tablaHistoricoCitas;
    @FXML
    public Label etiquetaCampoObligatorio;
    @FXML
    public Label formatoCorreoIncorrecto;
    public Button botonGuardarCliente;

    //variable para gardar a instancia de MenuInicioController
    //necesitamos que a instancia en esta clase sexa a mesma que a que temos en MenuInicioController para que funcione correctamente o metodo cerrarVentanaAlta()
    private MenuInicioController menuInicioController;
    private  ClientesDAO altaCliente = new ClientesDAO();

    public void initialize() {
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

        //inicializamos tabla de datos
        //crear columnas
        TableColumn<Citas, Integer> idColumn = new TableColumn<>("ID");
        TableColumn<Citas, Date> fechaColumn = new TableColumn<>("Fecha");
        TableColumn<Citas, String> comentarioColumn = new TableColumn<>("Comentario");
        TableColumn<Citas, String> estadoColumn = new TableColumn<>("Estado");

        //para que as columnas coupen todo o ancho da tabla
        tablaHistoricoCitas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //cargar columnas
        tablaHistoricoCitas.getColumns().addAll(idColumn,fechaColumn, comentarioColumn,estadoColumn);
    }

    //Método para no inicializar a variable menuInicioController coa mesma instancia que temos aberta na ventana de inicio
    public void setMenuInicioController(MenuInicioController controller) {
        menuInicioController = controller;
    }

    //pulsar boton cancelar
    public void cancelarNuevoCliente(ActionEvent actionEvent) {

        Scene scene = botonCancelarCliente.getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.close();

        //pasamos a false a variable de ventana de alta aberta para poder abrir unha nova
        menuInicioController.cerrarVentanaAlta();

    }

    public void crearNuevoCliente(ActionEvent actionEvent) throws ParseException {

        //obtemos os valores dos distintos campos
        //realizamos cambio de formato da fecha e convertimola a tipo Date para usalo na consulta
        java.sql.Date fechaSql = fechaFormateada ();
        int activo = 1;
        String dni = datosDNI.getText().trim();
        String nombre = datosNombre.getText().trim();
        String apellido1 = datosApellido1.getText().trim();
        String apellido2 = datosApellido2.getText().trim();
        String telefono = datosTelefono.getText().trim();
        String direccion = datosDireccion.getText().trim();
        String email = datosEmail.getText().trim();


        //verificamos que todos os campos obligatorios estan cubertos e en caso de haber mail que teña o formato correcto
        if(todosCamposCubiertosCorrectamente() && validarCorreo()) {
            //verificamos si existe o cliente na base de datos comprobando o DNI (campo único)
            if(!altaCliente.existeCliente(datosDNI.getText().trim())){
                //creamos cliente cos datos introducimos e realizamos insert na base de datos
                Clientes cliente = new Clientes(dni, nombre, apellido1, apellido2, email, telefono, direccion, fechaSql, activo);
                altaCliente.altaNuevoCliente(cliente);
                //limpamos os campos tras a alta
                limparCampos();

                Notifications.create()
                        .title("Alta de Cliente")
                        .text("El cliente se ha dado de alta correctamente")
                        .hideAfter(Duration.seconds(2))
                        .position(Pos.CENTER)
                        .darkStyle()
                        //.graphic(new ImageView(new Image("images/DSCF2148.JPG")))
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

    //Metodo para comprobar que todos os campos obligatorios estan cubertos, devolve True si é correcto
    public boolean todosCamposCubiertosCorrectamente(){

        boolean todosCamposCubiertosCorrecto = true;

        //comprobamos si os campos estan cubertos, en caso contrario resaltamos o textfield correspondente
        if(datosDNI.getText().trim().equals("")){
            todosCamposCubiertosCorrecto = false;
            datosDNI.getStyleClass().add("campo-incorrecto");
        }else{
            datosDNI.getStyleClass().removeAll("campo-incorrecto");
        }

        if(datosNombre.getText().trim().equals("")){
            todosCamposCubiertosCorrecto = false;
            datosNombre.getStyleClass().add("campo-incorrecto");
        }else{
            datosNombre.getStyleClass().removeAll("campo-incorrecto");
        }

        if(datosApellido1.getText().trim().equals("")){
            todosCamposCubiertosCorrecto = false;
            datosApellido1.getStyleClass().add("campo-incorrecto");
        }else{
            datosApellido1.getStyleClass().removeAll("campo-incorrecto");
        }

        if(datosApellido2.getText().trim().equals("")){
            todosCamposCubiertosCorrecto = false;
            datosApellido2.getStyleClass().add("campo-incorrecto");
        }else{
            datosApellido2.getStyleClass().removeAll("campo-incorrecto");
        }

        //si algun campo esta sin cubrir mostramos advertencia, si estan todos cubertos quitamola
        if(todosCamposCubiertosCorrecto){
            etiquetaCampoObligatorio.setVisible(false);
        }else{
            etiquetaCampoObligatorio.setVisible(true);
        }


        return todosCamposCubiertosCorrecto;


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
                formatoCorreoIncorrecto.getStyleClass().add("mensage-advertencia");
            }
        }
        return  formatoCorrecto;
    }

    public java.sql.Date fechaFormateada () throws ParseException {

        String fechaAltaString = datosFechaAlta.getText();
        SimpleDateFormat formatoLeido = new SimpleDateFormat("dd-MM-yyyy");
        Date fecha = formatoLeido.parse(fechaAltaString);
        SimpleDateFormat formatoFechaAlta = new SimpleDateFormat("yyyy-MM-dd");
        String fechaSqlString = formatoFechaAlta.format(fecha);
        java.sql.Date fechaSql = java.sql.Date.valueOf(fechaSqlString);


        return  fechaSql;
    }

    //metodo para borrar os datos de todos os campos
    public void limparCampos(){
        datosDNI.setText("");
        datosNombre.setText("");
        datosApellido1.setText("");
        datosApellido2.setText("");
        datosEmail.setText("");
        datosTelefono.setText("");
        datosDireccion.setText("");
    }


    //enventos dos botons cambio formato entrar/salir de el
    public void guardarBotonEntrar(MouseEvent mouseEvent) {
        botonGuardarCliente.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }
    public void guardarBotonSalir(MouseEvent mouseEvent) {
        botonGuardarCliente.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void cancelarBotonEntrar(MouseEvent mouseEvent) {
        botonCancelarCliente.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");

    }

    public void cancelarBotonSalir(MouseEvent mouseEvent) {
        botonCancelarCliente.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }




}
