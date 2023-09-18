
package modelos;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "estados")
public class Estados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idestado;
    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Este campo no puede estar vacío")
    @Size(max = 45)
    private String nombre;


    //Joins para relacionar claves foraneas coas súas taboas
    @OneToMany(mappedBy = "estados")
    private List<Citas> citas;

    public Estados() {

    }

    public Estados(int id) {
        this.idestado = id;
    }

    public Estados(int id, String nombre) {

            this.idestado = id;
            this.nombre = nombre;

    }

    public int getIdestado() {
        return idestado;
    }

    public void setIdestado(int idestado) {
        this.idestado = idestado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }



    public List<Citas> getCitas() {
        return citas;
    }

    public void setCitas(List<Citas> citas) {
        this.citas = citas;
    }

    @Override
    public String toString() {
        return "Estados{" +
                "idestado=" + idestado +
                ", nombre='" + nombre +
                '}';
    }
}
