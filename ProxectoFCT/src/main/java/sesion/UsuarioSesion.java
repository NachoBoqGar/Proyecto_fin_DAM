package sesion;

//clase para gardar os datos do usuario que inicia sesion
//praton diseño Singleton para que solo haxa unha instancia do usuario que inicia sesion
public class UsuarioSesion {

    private static UsuarioSesion instanciaUsuario=null;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private int id;
    private String rol;

    private UsuarioSesion(){};

    public static UsuarioSesion getInstanciaUsuario() {
        if (instanciaUsuario == null) {
            instanciaUsuario = new UsuarioSesion();
        }
        return instanciaUsuario;
    }
    public int getId() {

        return id;
    }

    public String getApellido1() {
        return apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
