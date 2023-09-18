package modelos;


import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
@Table(name = "empleados")
public class Empleados implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idempleado;
    @Column(name ="dni",nullable = false, unique = true)
    @NotEmpty
    @Size(max = 20)
    private String dni;
    @Column(nullable = false)
    @NotEmpty
    @Size(max = 45)
    private String nombre;
    @Column(nullable = false)
    @NotEmpty
    @Size(max = 45)
    private String apellido1;
    @Column(nullable = false)
    @NotEmpty
    @Size(max = 45)
    private String apellido2;
    @Column()
    @Email
    @Size(max = 45)
    private String correo;
    @Column
    @NotEmpty
    private Date fecha_alta;
    @Column(nullable = false)
    @NotEmpty(message = "Este campo no puede estar vacío")
    @Size(min = 5, max = 20)
    private String pass;
    @Column
    @Size(max = 20)
    private String telefono;
    @Column
    @Size(max = 150)
    private String direccion;
    @Column
    @Size(max = 1)
    private int activo ;

    //Joins para relacionar claves foraneas coas súas taboas
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrol", referencedColumnName = "id_rol", nullable=false)
    private Roles roles;

    @OneToMany(mappedBy = "empleados")
    private List<Citas> citas;


    public Empleados() {
    }

    public Empleados( String dni, String nombre, String apellido1, String apellido2, String correo, Date fecha_alta, String pass,String direccion, String telefono, int activo, Roles roles) {

        this.dni = dni;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.correo = correo;
        this.fecha_alta = fecha_alta;
        this.pass = pass;
        this.direccion = direccion;
        this.telefono = telefono;
        this.activo = activo;
        this.roles = roles;

    }

    public int getIdempleado() {
        return idempleado;
    }

    public void setIdempleado(int idempleado) {
        this.idempleado = idempleado;
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

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public List<Citas> getCitas() {
        return citas;
    }

    public void setCitas(List<Citas> citas) {
        this.citas = citas;
    }

    @Override
    public String toString() {
        return "Empleados{" +
                "idempleado=" + idempleado +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido1='" + apellido1 + '\'' +
                ", apellido2='" + apellido2 + '\'' +
                ", correo='" + correo + '\'' +
                ", fecha_alta=" + fecha_alta +
                ", pass='" + pass + '\'' +
                ", telefono='" + telefono + '\'' +
                ", activo=" + activo +
                ", roles=" + roles +
                ", citas=" + citas +
                '}';
    }


}
