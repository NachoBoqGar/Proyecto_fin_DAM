package controllers;

import Configuracion.ConfiguracionAplicacion;
import accesodatos.CitasDAO;
import inicio.FCTApplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.scene.control.CalendarPicker;
import sesion.UsuarioSesion;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalTime;




public class SeleccionarFechaCitaController {





    @FXML
    public GridPane gridPaneCitas;
    @FXML
    public CalendarPicker fechaCita;
    @FXML
    public Label etiquetaFechaSeleccionada;
    @FXML
    public Button botonCancelarSeleccionarFecha;
    ConfiguracionAplicacion configuracionAplicacion = new ConfiguracionAplicacion();
    //variable para acceder aos datos do usuario conectado
    UsuarioSesion usuarioSesion;
    //variable para consultas
    CitasDAO citasDAO = new CitasDAO();
    //variable para gardar os botons creados e eliminar o estilo
    List<Button> listaBotons = new ArrayList<>();
    //variable para gardar a instancia de MenuInicioController
    //necesitamos que a instancia en esta clase sexa a mesma que a que temos en MenuInicioController para que funcione correctamente o metodo cerrarVentanaCrearCita
    private MenuInicioController menuInicioController;

    public void initialize() throws ParseException {
        //inicializamos coa fecha de hoxe
        //creamos formato e actualizamos a etiqueta coa fecha seleccionada
        SimpleDateFormat formatoEtiquetaFecha = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("es"));
        fechaCita.calendarProperty().set(Calendar.getInstance());
        Calendar fechaActual = fechaCita.getCalendar();
        String fechaActualCadena = formatoEtiquetaFecha.format(fechaActual.getTime());
        etiquetaFechaSeleccionada.setText(fechaActualCadena);

        //deschabilitar fechas anterirores a hoxe
        /*
        fechaCita.setCalendarRangeCallback((range) -> {
            fechaCita.disabledCalendars().clear();
            Calendar minDate = Calendar.getInstance();
            minDate.set(Calendar.YEAR, 2000);
            minDate.set(Calendar.MONTH, Calendar.JANUARY);
            minDate.set(Calendar.DAY_OF_MONTH, 1);
            Calendar maxDate = Calendar.getInstance();

            for (Calendar c = (Calendar) minDate.clone(); c.before(maxDate); c.add(Calendar.DATE, 1)) {
                fechaCita.disabledCalendars().add((Calendar) c.clone());
            }

            return null;
        });*/

        //deshabilitamos fechas anteriores a hoxe e fin de semana
        fechaCita.setCalendarRangeCallback((range) -> {
            fechaCita.disabledCalendars().clear();
            Calendar minDate = Calendar.getInstance();
            minDate.set(Calendar.YEAR, 2000);
            minDate.set(Calendar.MONTH, Calendar.JANUARY);
            minDate.set(Calendar.DAY_OF_MONTH, 1);
            Calendar maxDate = Calendar.getInstance();
            maxDate.add(Calendar.YEAR, 100); //

            for (Calendar c = (Calendar) minDate.clone(); c.before(maxDate); c.add(Calendar.DATE, 1)) {
                if (c.before(Calendar.getInstance())) {
                    fechaCita.disabledCalendars().add((Calendar) c.clone());
                }
                else {
                    int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                        fechaCita.disabledCalendars().add((Calendar) c.clone());
                    }
                }
            }
            return null;
        });



        //listener para dectectar cada vez que se selecciona unha nova fecha
        fechaCita.calendarProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {
                //borramos os botons antiguos para non se superpoñan os estilos
                borrarBotons();

                Calendar fechaSeleccionadaCalendar = fechaCita.getCalendar();
                String fechaSeleccionada = formatoEtiquetaFecha.format(fechaSeleccionadaCalendar.getTime());
                etiquetaFechaSeleccionada.setText(fechaSeleccionada);
                System.out.println( fechaSeleccionadaCalendar.get(Calendar.DAY_OF_WEEK));
                try {
                    generarBotons();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        generarBotons();

    }
    public void setMenuInicioController(MenuInicioController controller) {
        menuInicioController = controller;
    }



    public void cancelarSeleccionarFecha(ActionEvent actionEvent) {

        Scene scene = botonCancelarSeleccionarFecha.getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.close();

        //pasamos a false a variable de ventana de alta aberta para poder abrir unha nova
        menuInicioController.cerrarVentanaCrearCita();

    }

    public void generarBotons() throws ParseException {
        //variable para limitar o numero de btoton en cada fila
        int botonsPorFila = 5;
        // creamos contenedores para agregar os botons do horario de mañá
        LocalTime horaInicioCitasManana = configuracionAplicacion.getHoraInicioManana();
        HBox hBoxManana = new HBox();
        hBoxManana.setAlignment(Pos.CENTER);
        hBoxManana.setSpacing(10);
        VBox vBoxManana = new VBox(hBoxManana);
        vBoxManana.setSpacing(10);
        vBoxManana.setAlignment(Pos.CENTER);

        //variable para ir contando o número de botons creado
        int contadorBotonsManana = 0;
        //bucle para crear os botons. O primeiro boton empeza ca hora inicio de mañá e van creandose botons en funcion da duración da cita ata a hora de fin
        while (horaInicioCitasManana.isBefore(configuracionAplicacion.getHoraFinManana())) {
            Button boton = new Button(horaInicioCitasManana.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            //vamos gardando os botóns para cando se cambie de fecha recorrer a lista borrando o estilo e evitar que se superpoña
            listaBotons.add(boton);
            //aplicase estilo enfunción de si hai citas dispoñibles ou non e agregase o boton ao hbox
            formatoBoton(boton);
            hBoxManana.getChildren().add(boton);
            //sumamos a duracion da cita a hora de inicio
            horaInicioCitasManana = horaInicioCitasManana.plusMinutes(configuracionAplicacion.getDuracionCita());
            contadorBotonsManana++;

            //si se chega o limite de botons da fila crease un novo hbox dentro do vbox para seguir metendo botons
            if (contadorBotonsManana == botonsPorFila) {
                hBoxManana = new HBox();
                hBoxManana.setSpacing(10);
                hBoxManana.setAlignment(Pos.CENTER);
                vBoxManana.getChildren().add(hBoxManana);
                contadorBotonsManana = 0;
            }

            //evento para abrir formulario para crear una nova cita
            boton.setOnAction(event -> {
                try {
                    //cargamos formulario
                    FXMLLoader loader = new FXMLLoader(FCTApplication.class.getResource("altacita.fxml"));
                    Parent root = loader.load();
                    AltaCitaController controller = loader.getController();

                    //recollemos datos de hora da cita e fecha
                    controller.fechaCita.setText(fechaCompleta(boton));

                    //abrimos formulario para crear a nova cita. Apertura en modo modal
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setResizable(false);
                    stage.setTitle("Crear Cita");
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();

                    //actualizar a lista de botons tras pechar a ventana
                    borrarBotons();
                    generarBotons();

                    //actualizar a lista de botons tras pechar a ventana
                    stage.setOnHidden(e -> {
                        try {

                            borrarBotons();
                            generarBotons();
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });


        }

        // creamos contenedores para agregar os botóns do horario de tarde
        LocalTime horaInicioCitasTarde = configuracionAplicacion.getHoraInicioTarde();
        HBox hBoxTarde = new HBox();
        hBoxTarde.setSpacing(10);
        hBoxTarde.setAlignment(Pos.CENTER);
        VBox vBoxTarde = new VBox(hBoxTarde);
        vBoxTarde.setSpacing(10);
        vBoxTarde.setAlignment(Pos.CENTER);
        int contadorBotonsTarde = 0;
        while (horaInicioCitasTarde.isBefore(configuracionAplicacion.getHoraFinTarde())) {
            Button boton = new Button(horaInicioCitasTarde.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            listaBotons.add(boton);
            //aplicase estilo enfunción de si hai citas dispoñibles ou non
            formatoBoton(boton);
            hBoxTarde.getChildren().add(boton);
            //sumamos a duracion da cita a hora de inicio
            horaInicioCitasTarde = horaInicioCitasTarde.plusMinutes(configuracionAplicacion.getDuracionCita());
            contadorBotonsTarde++;

            //evento para abrir formulario para crear una nova cita
            boton.setOnAction(event -> {
                try {
                    //cargamos formulario
                    FXMLLoader loader = new FXMLLoader(FCTApplication.class.getResource("altacita.fxml"));
                    Parent root = loader.load();
                    AltaCitaController controller = loader.getController();


                    //recollemos datos de hora da cita e fecha
                    controller.fechaCita.setText(fechaCompleta(boton));

                    //abrimos formulario para crear a noca cita. Apertura en modo modal
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();

                    //actualizar a lista de botons tras pechar a ventana
                    borrarBotons();
                    generarBotons();


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            });

            if (contadorBotonsTarde == botonsPorFila) {
                hBoxTarde = new HBox();
                hBoxTarde.setSpacing(10);
                hBoxTarde.setAlignment(Pos.CENTER);
                vBoxTarde.getChildren().add(hBoxTarde);
                contadorBotonsTarde = 0;
            }
        }

        // añadimos cada contenedor na fila correspondente do vBox
        gridPaneCitas.add(vBoxManana, 0, 1);
        gridPaneCitas.add(vBoxTarde, 0, 3);
    }


    //metodo para dar formato os botons en funcion de si hai citas dispoñibles
    public void formatoBoton(Button boton) throws ParseException {

        usuarioSesion = UsuarioSesion.getInstanciaUsuario();

        boton.setPrefSize(200, 150);


        if (usuarioSesion.getRol().equals("USUARIO")) {


            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date fecha = formatoFecha.parse(fechaCompleta(boton));
            java.sql.Date fechaSqlCita = new java.sql.Date(fecha.getTime());

            if (citasDAO.existeCitaEmpleadoFecha(usuarioSesion.getId(), fechaSqlCita)) {
                boton.getStyleClass().addAll("citas-disponibles","citas-disponibles-seleccionado");

            }else{
                boton.getStyleClass().add("citas-completas");
                boton.setDisable(true);
            }
        }else{

            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date fecha = formatoFecha.parse(fechaCompleta(boton));
            java.sql.Date fechaSqlCita = new java.sql.Date(fecha.getTime());

            if (citasDAO.maxCitasPorHora(configuracionAplicacion.getMaxCitasHora(), fechaSqlCita)) {
                boton.getStyleClass().addAll("citas-disponibles","citas-disponibles-seleccionado");

            }else{
                boton.getStyleClass().add("citas-completas");
                boton.setDisable(true);
            }

        }
    }

    //metodo para obter a fecha + hora da cita
    public String fechaCompleta(Button boton){

        SimpleDateFormat fechaCitaSeleccionada = new SimpleDateFormat("yyyy-MM-dd", new Locale("es"));
        Calendar fechaSeleccionadaCalendar = fechaCita.getCalendar();
        String fechaSeleccionada = fechaCitaSeleccionada.format(fechaSeleccionadaCalendar.getTime());
        String horaCita = boton.getText();

        return fechaSeleccionada + " " + horaCita;
    }

    public void borrarBotons(){

        //eliminamos os estilos dos boton para que non se superpoñan o cambiar de día
        for (Button boton : listaBotons) {
            boton.getStyleClass().removeAll("citas-disponibles","citas-disponibles-seleccionado","citas-completas");
        }
        listaBotons.clear();

    }

    //eventos dos botons para cambiar formato candoentra/sae o rato deles
    public void cancelarEntrar(MouseEvent mouseEvent) {
        botonCancelarSeleccionarFecha.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void cancelarSalir(MouseEvent mouseEvent) {
        botonCancelarSeleccionarFecha.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }
}
