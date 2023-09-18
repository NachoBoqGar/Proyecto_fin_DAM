package modelos;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "roles")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private int idrol;
    @Column(nullable = false, name = "nombre")
    private String nombre;

    @OneToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<Empleados> empleados;

    public Roles(int idrol, String nombre) {
        this.idrol = idrol;
        this.nombre = nombre;

    }

    public Roles() {

    }

    public int getIdrol() {
        return idrol;
    }

    public void setIdrol(int idrol) {
        this.idrol = idrol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }



    @Override
    public String toString() {
        return "Roles{" +
                "id=" + idrol +
                ", nombre='" + nombre + '\'';
    }






}
