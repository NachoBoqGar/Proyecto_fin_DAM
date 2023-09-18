package controllers;

import accesodatos.CitasDAO;
import accesodatos.ClientesDAO;
import accesodatos.EmpleadosDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import modelos.Citas;
import modelos.Clientes;
import modelos.Empleados;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EstadisticasController {
    @FXML
    public Button botonCancelarEstadisticas;
    @FXML
    public Label numTotalEmpleados;
    @FXML
    public Label etiquetaTotalClientes;
    @FXML
    public Label numCitasProgramadas;

    public Label numCitasAtendidas;
    @FXML
    public Label numCitasTotales;
    @FXML
    public Label numTotalClientes;
    @FXML
    public PieChart pieChartCitasPorEstado;
    @FXML
    public Button botonEstadisticasCitas;
    @FXML
    public DatePicker fechaDesde;
    @FXML
    public DatePicker fechaHasta;
    @FXML
    public PieChart pieChartCitasPorEmpleado;
    @FXML
    public ComboBox comboboxEmpleados;
    @FXML
    public LineChart lineChartNumCitasDia;
    public GridPane gridPaneEstadisticas;


    EmpleadosDAO empleadosDAO = new EmpleadosDAO();
    ClientesDAO clientesDAO = new ClientesDAO();
    CitasDAO citasDAO = new CitasDAO();
    private MenuInicioController menuInicioController;

    public void initialize(){
        //inicializamos datos xenerales da aplicacion
        numTotalEmpleados.setText(String.valueOf(numEmpleados()));
        numTotalClientes.setText(String.valueOf(numClientes()));
        numCitasProgramadas.setText(String.valueOf(numCitasProgramadas()));
        numCitasAtendidas.setText(String.valueOf(numCitasAtendidas()));
        numCitasTotales.setText(String.valueOf(numCitasTotales()));

        rellenarComboBoxEmpleados();

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

    public void cancelarEstadisticas(ActionEvent actionEvent) {

        Scene scene = botonCancelarEstadisticas.getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.close();

        //pasamos a false a variable ventanaEstadisticasAberta para poder abrir unha nova
        menuInicioController.cerrarVentanaEstadisticas();
    }

    //metodo para obter o número total de empleados
    public int numEmpleados(){

        List<Empleados> resultadoBusqueda = empleadosDAO.listadoEmpleados("SELECT e FROM Empleados e WHERE 1=1 AND activo = 1");
        int numTotalEmpleados = resultadoBusqueda.size();
        return  numTotalEmpleados;
    }
    //método para obter o número total de clientes activos
    public int numClientes(){

        List<Clientes> resultadoBusqueda = clientesDAO.listadoClientes("SELECT c FROM Clientes c WHERE 1=1 AND activo = 1");
        int numTotalClientes = resultadoBusqueda.size();
        return  numTotalClientes;
    }
    //método para ver o número total de citas atendidas
    public int numCitasProgramadas(){

        List<Integer> estado = new ArrayList<>();
        estado.add(1);
        List<Citas> resultadoBusqueda = citasDAO.consultarCitas(null,null,estado,null);
        int numTotalCitasProgramadas = resultadoBusqueda.size();
        return  numTotalCitasProgramadas;
    }
    public int numCitasTotales(){
        List<Integer> estado = new ArrayList<>();
        estado.add(1);
        estado.add(2);
        estado.add(3);
        estado.add(4);
        List<Citas> resultadoBusqueda = citasDAO.consultarCitas(null,null,estado,null);
        int numTotalCitas= resultadoBusqueda.size();
        return  numTotalCitas;
    }

    //metodo  para ver o número de citas finalizadas
    public int numCitasAtendidas(){

        List<Integer> estado = new ArrayList<>();
        estado.add(4);
        List<Citas> resultadoBusqueda = citasDAO.consultarCitas(null,null,estado,null);
        int numTotalCitasProgramadas = resultadoBusqueda.size();
        return  numTotalCitasProgramadas;
    }


    //acción do boton para ver as estadisticas de citas
    public void verEstadisticasCitas(ActionEvent actionEvent) throws ParseException {
        numCitasDiferenteEstado();
        numCitasDiferenteEmpleado();
        numCitasPorDía();
    }

    //método para obter o numero de citas de cada estado e crear o piechart
    private void numCitasDiferenteEstado() throws ParseException {

        //lista para garadar o número de citas de cada estado do total de citas
        List<Integer> numCitasDiferentesEstados = new ArrayList<>();
        Date fechaDesdeSeleccionada = null;
        if(fechaDesde.getValue()!=null) fechaDesdeSeleccionada = fechaFormateada(fechaDesde.getValue().toString());
        Date fechaHastaSeleccionada = null;
        if(fechaHasta.getValue()!=null) fechaHastaSeleccionada = fechaFormateada(fechaHasta.getValue().toString());
        for(int i=1;i<5;i++) {
            List<Citas> citas = citasDAO.listaCitasPorEstado(fechaDesdeSeleccionada, fechaHastaSeleccionada, i);
            numCitasDiferentesEstados.add(citas.size());
        }

        // Crear dataset para cargar datos no piechart
        ObservableList<PieChart.Data> datosPieChart = FXCollections.observableArrayList();
        if (numCitasDiferentesEstados.get(0) > 0) {
                datosPieChart.add(new PieChart.Data("Pendiente (" + numCitasDiferentesEstados.get(0).toString() + ")", numCitasDiferentesEstados.get(0)));
        }
        if (numCitasDiferentesEstados.get(1) > 0) {
                datosPieChart.add(new PieChart.Data("Cancelada (" + numCitasDiferentesEstados.get(1).toString() + ")", numCitasDiferentesEstados.get(1)));
        }
        if (numCitasDiferentesEstados.get(2) > 0) {
                datosPieChart.add(new PieChart.Data("No Presentado (" + numCitasDiferentesEstados.get(2).toString() + ")", numCitasDiferentesEstados.get(2)));
        }
        if (numCitasDiferentesEstados.get(3) > 0) {
                datosPieChart.add(new PieChart.Data("Finalizada (" + numCitasDiferentesEstados.get(3).toString() + ")", numCitasDiferentesEstados.get(3)));
        }

        pieChartCitasPorEstado.setTitle("Número de citas por estado");
        pieChartCitasPorEstado.setData(datosPieChart);

    }

    //método para obter o numero de cada empleado e crear o piechart
    private void numCitasDiferenteEmpleado() throws ParseException {

        //lista para gardar todos os empleados
        List<Empleados> empleados = empleadosDAO.listadoEmpleados("SELECT e FROM Empleados e WHERE 1=1");
        // Crear dataset para cargar datos no piechart
        ObservableList<PieChart.Data> datosPieChart = FXCollections.observableArrayList();

        Date fechaDesdeSeleccionada = null;
        if(fechaDesde.getValue()!=null) fechaDesdeSeleccionada = fechaFormateada(fechaDesde.getValue().toString());
        Date fechaHastaSeleccionada = null;
        if(fechaHasta.getValue()!=null) fechaHastaSeleccionada = fechaFormateada(fechaHasta.getValue().toString());
        //bucle no que para cada empleado buscamos o número de citas que ten
        for(Empleados empleado : empleados) {
            //buscamos o numero de citas que ten cada un dos empleados
            List<Citas> citasEmpleado = citasDAO.listaCitasPorEmpleado(fechaDesdeSeleccionada, fechaHastaSeleccionada, empleado.getIdempleado());
            if (citasEmpleado.size() > 0) {
                datosPieChart.add(new PieChart.Data(empleado.getNombre()+" "+empleado.getApellido1()+" ("+citasEmpleado.size()+")", citasEmpleado.size()));
            }
        }

        pieChartCitasPorEmpleado.setTitle("Número de citas por empleado");
        pieChartCitasPorEmpleado.setData(datosPieChart);
    }

    //método par obter as consultas creadas totales por día e crear o linechart. Creamos barchart e actualizamos piechar citas pos estado si hay un cliente seleccionado
    private void numCitasPorDía() throws ParseException {

        //buscamos un line chart e si existe eliminamolo para non super un novo o actual. Facemos o mesmo co BarChart
        ObservableList<Node> children = gridPaneEstadisticas.getChildren();
        for (Node child : children) {
            if (child instanceof LineChart ) {
                gridPaneEstadisticas.getChildren().remove(child);
                break;
            }

        }
        for (Node child : children) {
            if (child instanceof BarChart ) {
                gridPaneEstadisticas.getChildren().remove(child);
                break;
            }

        }

        String tituloGrafica = "Número de citas desde el ";
        Date fechaDesdeSeleccionada = null;
        Date fechaHastaSeleccionada = null;
        List<Citas> citaMaisAntiga = citasDAO.citaMaisAntiga();
        List<Citas> citaMaisFuturo = citasDAO.citaMaisFuturo();


        if(fechaDesde.getValue()!=null){
            fechaDesdeSeleccionada = fechaFormateada(fechaDesde.getValue().toString());
            tituloGrafica = tituloGrafica +fechaDesdeSeleccionada+" hasta ";
        }else{
            fechaDesdeSeleccionada = fechaFormateada(citaMaisAntiga.get(0).getFecha().toString());
            tituloGrafica = tituloGrafica +"inicio de la actividad hasta ";
        }
        if(fechaHasta.getValue()!=null) {
            fechaHastaSeleccionada = fechaFormateada(fechaHasta.getValue().toString());
            tituloGrafica=tituloGrafica+"el "+fechaDesdeSeleccionada;
        }else{
            fechaHastaSeleccionada = fechaFormateada(citaMaisFuturo.get(0).getFecha().toString());
            tituloGrafica=tituloGrafica+"ultima cita programada";
        }

        //creamos os eixes X e Y
        CategoryAxis xAxis = new  CategoryAxis();
        xAxis.setLabel("Fecha");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Número de citas");


        //creamos linechart e establecemos o titulo en funcion das datas seleccionadas
        LineChart<String,Number> lineChart =  new LineChart<String,Number>(xAxis,yAxis);
        lineChart.setTitle(tituloGrafica);
        //creamos a serie de datos
        XYChart.Series series = new XYChart.Series();
        series.setName("Citas creadas");

        //agregamos os valores a serie
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaDesdeSeleccionada);
        //variable para contar os días

        while (calendar.getTime().before(fechaHastaSeleccionada)) {
            // empezamos na fecha desde e vamos recorrendo hasta chegar a fecha hasta
            java.sql.Date fechaActual = new java.sql.Date(calendar.getTimeInMillis());
            List<Citas> citas = citasDAO.listaCitasFecha(fechaActual);
            series.getData().add(new XYChart.Data<>(fechaActual.toString(), citas.size()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        }

        //cargamos datos no linechart e creamolo na celda correspondente
        lineChart.getData().add(series);
        gridPaneEstadisticas.getChildren().add(lineChart);
        GridPane.setConstraints(lineChart, 1, 5);
        GridPane.setColumnSpan(lineChart,3);


        //CREAMOS BARCHAR SI HAY UN EMPLEADO SELECCIONADO
        if(comboboxEmpleados.getSelectionModel().getSelectedItem() != null ){
            List<Integer> idEmpleadoSeleccionadoList = empleadoSeleccionadoComboBox();

            //creamos os eixes X e Y
            CategoryAxis xAxisBar = new  CategoryAxis();
            xAxisBar.setLabel("Número de citas asignadas cada por día");
            NumberAxis yAxisBar = new NumberAxis();
            yAxisBar.setLabel("Número de citas");

            //creamos barchart e establecemos o titulo en funcion das datas seleccionadas
            BarChart<String,Number> barChart =  new BarChart<String,Number>(xAxisBar,yAxisBar);
            barChart.setTitle(tituloGrafica);
            barChart.setBarGap(0);
            barChart.setCategoryGap(5);

            //creamos a serie de datos
            XYChart.Series seriesBar = new XYChart.Series();
            series.setName("Citas asignadas");

            //agregamos os valores a serie
            Calendar calendarBar = Calendar.getInstance();
            calendar.setTime(fechaDesdeSeleccionada);

            while (calendar.getTime().before(fechaHastaSeleccionada)) {
                // empezamos na fecha desde e vamos recorrendo hasta chegar a fecha hasta
                java.sql.Date fechaActual = new java.sql.Date(calendar.getTimeInMillis());
                List<Citas> citas = citasDAO.consultarCitas(fechaActual,fechaActual,null,idEmpleadoSeleccionadoList);
                seriesBar.getData().add(new XYChart.Data<>(fechaActual.toString(), citas.size()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            //cargamos datos no linechart e creamolo na celda correspondente
            barChart.getData().add(seriesBar);
            gridPaneEstadisticas.getChildren().add(barChart);
            GridPane.setConstraints(barChart, 3, 3);

            ///////////////////////////////////////////////////////////////
            List<Integer>  numCitasDiferentesEstados = new ArrayList<>();
            for(int i=1;i<5;i++) {
                List<Integer> idEstados = new ArrayList<>();
                idEstados.add(i);
                List<Citas> citas = citasDAO.consultarCitas(fechaDesdeSeleccionada, fechaHastaSeleccionada, idEstados,idEmpleadoSeleccionadoList);
                numCitasDiferentesEstados.add(citas.size());
                idEstados.clear();
            }

            // Crear dataset para cargar datos no piechart
            ObservableList<PieChart.Data> datosPieChart = FXCollections.observableArrayList();
            if (numCitasDiferentesEstados.get(0) > 0) {
                datosPieChart.add(new PieChart.Data("Pendiente (" + numCitasDiferentesEstados.get(0).toString() + ")", numCitasDiferentesEstados.get(0)));
            }
            if (numCitasDiferentesEstados.get(1) > 0) {
                datosPieChart.add(new PieChart.Data("Cancelada (" + numCitasDiferentesEstados.get(1).toString() + ")", numCitasDiferentesEstados.get(1)));
            }
            if (numCitasDiferentesEstados.get(2) > 0) {
                datosPieChart.add(new PieChart.Data("No Presentado (" + numCitasDiferentesEstados.get(2).toString() + ")", numCitasDiferentesEstados.get(2)));
            }
            if (numCitasDiferentesEstados.get(3) > 0) {
                datosPieChart.add(new PieChart.Data("Finalizada (" + numCitasDiferentesEstados.get(3).toString() + ")", numCitasDiferentesEstados.get(3)));
            }

            String empleadoSeleccionado = comboboxEmpleados.getSelectionModel().getSelectedItem().toString();
            String[] parts = empleadoSeleccionado.split(" - ");
            String nombreEmpleado = parts[1];
            pieChartCitasPorEstado.setTitle("Número de citas por estado de "+nombreEmpleado);
            pieChartCitasPorEstado.setData(datosPieChart);

        }
        
    }

    public List<Integer> empleadoSeleccionadoComboBox (){

        String empleadoSeleccionado = comboboxEmpleados.getSelectionModel().getSelectedItem().toString();
        String[] parts = empleadoSeleccionado.split(" - ");
        String idEmpleadoSeleccionado = parts[0];
        List<Integer> idEmpleadoSeleccionadoList = new ArrayList<>();
        idEmpleadoSeleccionadoList.add(Integer.valueOf(idEmpleadoSeleccionado));

        return idEmpleadoSeleccionadoList;
    }

    public void  rellenarComboBoxEmpleados(){
        List<Empleados> empleados = empleadosDAO.listadoEmpleados("SELECT e FROM Empleados e WHERE 1=1");
        comboboxEmpleados.getItems().add(0, null);
        for(Empleados empleado : empleados){
            comboboxEmpleados.getItems().add(empleado.getIdempleado()+" - "+empleado.getNombre()+" "+empleado.getApellido1()+" "+empleado.getApellido2());
        }

    }
    public java.sql.Date fechaFormateada(String fecha) throws ParseException {

        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date fechaUtil = formatoFecha.parse(fecha);
        java.sql.Date fechaSqlCita = new java.sql.Date(fechaUtil.getTime());
        return fechaSqlCita;
    }

    public void entrarActualizar(MouseEvent mouseEvent) {
        botonEstadisticasCitas.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirActualizar(MouseEvent mouseEvent) {
        botonEstadisticasCitas.setStyle("-fx-background-color:  #ADD8E6; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
    }

    public void entrarCancelar(MouseEvent mouseEvent) {
        botonCancelarEstadisticas.setStyle("-fx-background-color:  #708090; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: white;");
    }

    public void salirCancelar(MouseEvent mouseEvent) {
        botonCancelarEstadisticas.setStyle("-fx-background-color:  #708090; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
    }
}
