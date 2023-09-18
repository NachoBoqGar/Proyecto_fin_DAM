package modelos;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "citas")
public class Citas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idcita;
    @Column
    @NotEmpty
    @Size(max = 300)
    private String comentario;
    @Column
    @NotEmpty
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    //Joins para relacionar claves foraneas coas súas taboas
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idcliente",  nullable=false)
    private Clientes clientes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idempleado",  nullable=false)
    private Empleados empleados;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idestado",  nullable=false)
    private Estados estados;

    public Citas() {
    }

    public Citas(String comentario, Date fecha, Clientes clientes, Empleados empleados, Estados estados) {

        this.comentario = comentario;
        this.fecha = fecha;
        this.clientes = clientes;
        this.empleados = empleados;
        this.estados = estados;
    }

    public int getIdcita() {
        return idcita;
    }

    public void setIdcita(int idcita) {
        this.idcita = idcita;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Clientes getClientes() {
        return clientes;
    }

    public void setClientes(Clientes clientes) {
        this.clientes = clientes;
    }

    public Empleados getEmpleados() {
        return empleados;
    }

    public void setEmpleados(Empleados empleados) {
        this.empleados = empleados;
    }

    public Estados getEstado() {
        return estados;
    }

    public void setEstados(Estados estados) {
        this.estados = estados;
    }

    @Override
    public String toString() {
        return "Citas{" +
                "idcita=" + idcita +
                ", comentario='" + comentario + '\'' +
                ", fecha=" + fecha +
                '}';
    }
}
