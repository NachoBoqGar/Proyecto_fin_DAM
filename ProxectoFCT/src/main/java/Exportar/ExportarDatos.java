package Exportar;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;


import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.text.pdf.PdfWriter;


import java.io.FileOutputStream;
import java.io.FileNotFoundException;


import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class ExportarDatos {

    public void exportarFormatoExcel(TableView tabla, Stage stage) throws IOException, DocumentException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar como");
        fileChooser.getExtensionFilters().addAll( new FileChooser.ExtensionFilter("Archivos de Excel", "*.xlsx"), new FileChooser.ExtensionFilter("Archivos de OpenDocument", "*.ods"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            //creamos combobox para elexir formatos
            ChoiceBox<String> formatBox = new ChoiceBox<>();
            formatBox.getItems().addAll(".xlsx", ".ods");
            formatBox.setValue(".xlsx");
            Dialog<String> dialog = new Dialog<>();
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.getDialogPane().setContent(formatBox);
            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    return formatBox.getValue();
                }
                return null;
            });

            //executamos exportación
            exportar(tabla, file.getAbsolutePath());


        }
    }

    private void exportar(TableView tabla, String ruta) throws IOException {

        //creamos o ficheiro onde gardaremos os datos
        File ficheiro = new File(ruta);
        FileOutputStream fos = new FileOutputStream(ficheiro);
        XSSFWorkbook libro = new XSSFWorkbook();
        XSSFSheet folla = libro.createSheet("Hoja1");

        //creamos a primeira fila cos nomes de cada columna
        XSSFRow cabecera = folla.createRow(0);
        for (int j = 0; j < tabla.getColumns().size(); j++) {
            XSSFCell celda = cabecera.createCell(j);
            TableColumn<?, ?> col = (TableColumn<?, ?>) tabla.getColumns().get(j);
            String headerText = col.getText();
            celda.setCellValue(headerText);
        }

        //recorremos a tabla creando as filas e gardadndo os datos en cada celda
        for (int i = 0; i < tabla.getItems().size(); i++) {
            XSSFRow fila = folla.createRow(i + 1);
            for (int j = 0; j < tabla.getColumns().size(); j++) {
                XSSFCell celda = fila.createCell(j);
                TableColumn col = (TableColumn) tabla.getColumns().get(j);
                Object cellData = col.getCellData(tabla.getItems().get(i));
                if (cellData != null) {
                    if (cellData instanceof String) {
                        celda.setCellValue((String) cellData);
                    } else if (cellData instanceof Number) {
                        celda.setCellValue(((Number) cellData).doubleValue());
                    } else if (cellData instanceof LocalDate) {
                        celda.setCellValue((LocalDate) cellData);
                    } else if (cellData instanceof LocalDateTime) {
                        celda.setCellValue((LocalDateTime) cellData);
                    } else if (cellData instanceof Boolean) {
                        celda.setCellValue((Boolean) cellData);
                    } else {
                        celda.setCellValue(cellData.toString());
                    }
                } else {
                    celda.setCellValue("");
                }
            }
        }

        //gardamos fecheiro si non houbo erros e mostramos mensaxe de exportación correcta
        try {
            libro.write(fos);
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Exportar datos");
            alerta.setHeaderText(null);
            alerta.setContentText("Los datos se han exportado correctamente.");
            alerta.showAndWait();

        } catch (IOException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error al exportar datos");
            alerta.setHeaderText(null);
            alerta.setContentText("Ha ocurrido un error al exportar los datos.");
            alerta.showAndWait();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {

                }
            }
        }
    }




}
