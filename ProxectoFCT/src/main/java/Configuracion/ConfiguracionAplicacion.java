package Configuracion;

import sesion.UsuarioSesion;

import java.time.LocalTime;



public class ConfiguracionAplicacion {
    static UsuarioSesion usuarioSesion;

    public LocalTime HORA_INICIO_MAÑANA = LocalTime.of(9, 0);
    public  LocalTime HORA_FIN_MAÑANA = LocalTime.of(14, 0);
    public  LocalTime HORA_INICIO_TARDE = LocalTime.of(16, 0);
    public LocalTime HORA_FIN_TARDE = LocalTime.of(21, 0);
    public int DURACION_CITA = 30;
    public int MAX_CITAS_HORA = 2;




    public LocalTime getHoraInicioManana() {
        return HORA_INICIO_MAÑANA;
    }

    public  LocalTime getHoraFinManana() {
        return HORA_FIN_MAÑANA;
    }

    public  LocalTime getHoraInicioTarde() {
        return HORA_INICIO_TARDE;
    }

    public  LocalTime getHoraFinTarde() {
        return HORA_FIN_TARDE;
    }

    public  int getDuracionCita() {
        return DURACION_CITA;
    }
    public  int getMaxCitasHora() {
        return MAX_CITAS_HORA;
    }

    public  String firmaUsuario(){

        usuarioSesion = UsuarioSesion.getInstanciaUsuario();
        String firma = "\n-------------------------------------------------\n"+""+usuarioSesion.getNombre();
        return  firma;
    }


}
