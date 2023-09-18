package modelos;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Clientes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idcliente;
    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Este campo no puede estar vacío")
    @Size(max = 20)
    private String dni;
    @Column(nullable = false)
    @NotEmpty(message = "Este campo no puede estar vacío")
    @Size(max = 45)
    private String nombre;
    @Column(nullable = false)
    @NotEmpty(message = "Este campo no puede estar vacío")
    @Size(max = 45)
    private String apellido1;
    @Column(nullable = false)
    @NotEmpty(message = "Este campo no puede estar vacío")
    @Size(max = 45)
    private String apellido2;
    @Column(nullable = false)
    @Email
    @Size(max = 45)
    private String correo;
    @Column
    @Size(max = 45)
    private String telefono;
    @Column
    @Size(max = 150)
    private String direccion;
    @Column
    @NotEmpty
    private Date fecha_alta;
    @Column
    @Size(max = 1)
    private int activo ;

    //Joins para relacionar claves foraneas coas súas taboas
    @OneToMany(mappedBy = "clientes", fetch = FetchType.EAGER)
    private List<Citas> citas;

    public Clientes() {
    }

    public Clientes( String dni, String nombre, String apellido1, String apellido2, String correo, String telefono, String direccion, Date fecha_alta, int activo) {

        this.dni = dni;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fecha_alta = fecha_alta;
        this.activo = activo;

    }

    public int getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(int idcliente) {
        this.idcliente = idcliente;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Date getFecha_alta() {
        return fecha_alta;
    }

    public void setFecha_alta(Date fecha_alta) {
        this.fecha_alta = fecha_alta;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    public List<Citas> getCitas() {
        return citas;
    }

    public void setCitas(List<Citas> citas) {
        this.citas = citas;
    }

    @Override
    public String toString() {
        return "Clientes{" +
                "idcliente=" + idcliente +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido1='" + apellido1 + '\'' +
                ", apellido2='" + apellido2 + '\'' +
                ", correo='" + correo + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", fecha_alta=" + fecha_alta +
                ", activo=" + activo +
                '}';
    }
}
