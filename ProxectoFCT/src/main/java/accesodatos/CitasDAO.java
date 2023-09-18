package accesodatos;

import modelos.Citas;
import modelos.Estados;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CitasDAO {
    DatabaseConection dbc = new DatabaseConection();


    //método para crear unha nova cita
    public void crearCita(Citas cita){

        EntityManager em = dbc.abrirConexion();

        try {

            EntityTransaction transacion = em.getTransaction();
            transacion.begin();
            em.persist(cita);
            transacion.commit();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            dbc.cerrarConexion(em);
        }


    }

    //metodo para obter a lista de citas de uun cliente
    public List<Citas> getCitasCliente(int id){

        EntityManager em = dbc.abrirConexion();
        List<Citas> listaCitas = null;
        //ejecutamos consulta e gardamos o resultado en unha lista
        try {
            TypedQuery<Citas> consulta = em.createQuery("SELECT c FROM Citas c WHERE c.clientes.idcliente = :idcliente", Citas.class);
            consulta.setParameter("idcliente", id);
            listaCitas = consulta.getResultList();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
            return listaCitas;
        }
    }

    //metodo para obter a lista de citas en unha fecha determinada
    public List<Citas> listaCitasFecha(java.sql.Date fecha){

        EntityManager em = dbc.abrirConexion();
        List<Citas> listaCitas = null;

        //ejecutamos consulta e gardamos o resultado en unha lista
        try {
            TypedQuery<Citas> consulta = em.createQuery("SELECT c FROM Citas c WHERE DATE(c.fecha) = :fecha", Citas.class);
            consulta.setParameter("fecha", fecha);
            listaCitas = consulta.getResultList();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
            return listaCitas;
        }
    }

    //metodo para obter a cita coa data máis antiga
    public List<Citas> citaMaisAntiga(){

        EntityManager em = dbc.abrirConexion();
        List<Citas> listaCitas = null;

        //ejecutamos consulta e gardamos o resultado en unha lista
        try {
            TypedQuery<Citas> consulta = em.createQuery("SELECT c FROM Citas c ORDER BY fecha asc", Citas.class);
            consulta.setMaxResults(1);
            listaCitas = consulta.getResultList();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
            return listaCitas;
        }
    }

    //metodo para obter a cita coa data máis a futuro
    public List<Citas> citaMaisFuturo(){

        EntityManager em = dbc.abrirConexion();
        List<Citas> listaCitas = null;

        //ejecutamos consulta e gardamos o resultado en unha lista
        try {
            TypedQuery<Citas> consulta = em.createQuery("SELECT c FROM Citas c ORDER BY fecha desc", Citas.class);
            consulta.setMaxResults(1);
            listaCitas = consulta.getResultList();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
            return listaCitas;
        }
    }

    //metodo que nos devovle true si existe unha cita a unha determinada fecha e hora para un empleado en concreto
    public boolean existeCitaEmpleadoFecha(int id, Date fecha){

        EntityManager em = dbc.abrirConexion();
        List<Citas> citas = null;

        try {
            TypedQuery<Citas> consulta = em.createQuery("SELECT c FROM Citas c WHERE c.empleados.idempleado = :idempleado AND c.fecha = :fecha", Citas.class);
            consulta.setParameter("idempleado", id);
            consulta.setParameter("fecha", fecha);
            citas = consulta.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
            return citas.isEmpty() ? true : false;
        }
    }

    //metodo que nos devolve si o numero de citas a en unha determinada fecha e hora e igual ou superior o maximo de citas permitidas para ese horario
    public boolean maxCitasPorHora(int maxCitas, Date fecha) {

        EntityManager em = dbc.abrirConexion();
        List<Citas> citas = null;
        int numCitas = 0;

        try {
            TypedQuery<Citas> consulta = em.createQuery("SELECT c FROM Citas c WHERE c.fecha = :fecha", Citas.class);
            consulta.setParameter("fecha", fecha);
            citas = consulta.getResultList();
            numCitas = citas.size();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
            return numCitas < maxCitas ? true : false;
        }
    }

    //consulta para comprobar si un cliente ten unha cita para unha fecha en concreto
    public boolean citaUsuarioFecha (int id, Date fecha){

        EntityManager em = dbc.abrirConexion();
        List<Citas> citas = null;
        int numCitas =0;
        //ejecutamos consulta e gardamos o resultado en unha lista
        try {
            TypedQuery<Citas> consulta = em.createQuery("SELECT c FROM Citas c WHERE c.clientes.idcliente = :idcliente AND c.fecha = :fecha", Citas.class);
            consulta.setParameter("idcliente", id);
            consulta.setParameter("fecha", fecha);
            citas = consulta.getResultList();
            numCitas = citas.size();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
            return numCitas > 0 ? true : false;
        }
    }


    //consulta de citas para o formulario listado de citas
    public List<Citas> consultarCitas(java.util.Date fechaDesdeSeleccionada,java.util.Date fechaHastaSeleccionada,List<Integer> listaEstadosSeleccionados,List<Integer> listaEmpleadosSeleccionados){
        //lista para gardar resultado da consulta
        List<Citas> citas = null;

        EntityManager em = dbc.abrirConexion();

        //executamos consulta e gardamos o resultado en unha lista
        try {
            String consultaString = "SELECT * FROM citas c WHERE (:fechaDesde IS NULL OR DATE(c.fecha) >= :fechaDesde) " +
                    "AND (:fechaHasta IS NULL OR DATE(c.fecha) <= :fechaHasta) " +
                    "AND (coalesce(:estados, '') = '' OR c.idestado IN (:estados))"+
                    "AND (coalesce(:empleados, '') = '' OR c.idempleado IN (:empleados))";

            Query consulta = em.createNativeQuery(consultaString, Citas.class);
            consulta.setParameter("fechaDesde", fechaDesdeSeleccionada, TemporalType.DATE);
            consulta.setParameter("fechaHasta", fechaHastaSeleccionada, TemporalType.DATE);
            consulta.setParameter("estados", listaEstadosSeleccionados != null ? listaEstadosSeleccionados : null);
            consulta.setParameter("empleados", listaEmpleadosSeleccionados != null ? listaEmpleadosSeleccionados : null);
            citas = consulta.getResultList();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
            return citas;
        }

    }


    //consulta para actualizar os datos de comentario de unha cita
    public void actualizarComentarioCita(Citas citaModificada) {

        EntityManager em = dbc.abrirConexion();

        try {
            em.getTransaction().begin();
            //buscamos a cita a actualizar polo seu id e asignamoslle os valores actualizados
            Citas cita = em.find(Citas.class, citaModificada.getIdcita());
            cita.setComentario(citaModificada.getComentario());
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
        }
    }


    //consulta para modificar o idestado dunha cita que se recibe por parametro
    public void cambiarEstadoCita(int idestado, int idcita) {
        EntityManager em = dbc.abrirConexion();

        try {
            em.getTransaction().begin();
            //buscamos a cita a actualizar polo seu id e asignamoslle os valores actualizados
            Citas cita = em.find(Citas.class, idcita);
            Estados estado = new Estados(idestado);
            cita.setEstados(estado);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
        }

    }

    //devolve o numero de citas de cada estado
    public List<Citas> listaCitasPorEstado(java.util.Date fechaDesdeSeleccionada, java.util.Date fechaHastaSeleccionada, int estado) {

        EntityManager em = dbc.abrirConexion();
        //creamos lista para gardar os datos
        List<Citas> resultados = null;

        //executamos consulta e gardamos o resultado en unha lista



        try {
            String consultaString = "SELECT * FROM Citas c WHERE (:fechaDesde IS NULL OR DATE(c.fecha) >= :fechaDesde) " +
                    "AND (:fechaHasta IS NULL OR DATE(c.fecha) <= :fechaHasta) " +
                    "AND c.idestado = :estado";

            Query consulta = em.createNativeQuery(consultaString, Citas.class);
            consulta.setParameter("fechaDesde", fechaDesdeSeleccionada, TemporalType.DATE);
            consulta.setParameter("fechaHasta", fechaHastaSeleccionada, TemporalType.DATE);
            consulta.setParameter("estado", estado);
            resultados = consulta.getResultList();


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
            return resultados;
        }

    }

    //devolve o número de citas de un empleado
    public List<Citas> listaCitasPorEmpleado(java.util.Date fechaDesdeSeleccionada, java.util.Date fechaHastaSeleccionada, int idempleado) {

        EntityManager em = dbc.abrirConexion();
        //creamos lista para gardar os datos
        List<Citas> resultados = null;

        //executamos consulta e gardamos o resultado en unha lista
        try {
            String consultaString = "SELECT * FROM Citas c WHERE (:fechaDesde IS NULL OR DATE(c.fecha) >= :fechaDesde) " +
                    "AND (:fechaHasta IS NULL OR DATE(c.fecha) <= :fechaHasta) " +
                    "AND c.idempleado = :idempleado";

            Query consulta = em.createNativeQuery(consultaString, Citas.class);
            consulta.setParameter("fechaDesde", fechaDesdeSeleccionada, TemporalType.DATE);
            consulta.setParameter("fechaHasta", fechaHastaSeleccionada, TemporalType.DATE);
            consulta.setParameter("idempleado", idempleado);
            resultados = consulta.getResultList();


        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
            return resultados;
        }

    }

    //metodo para cancelar as citas pendientes de un usuario
    public void cancelarCitasCliente(int idcliente){

        EntityManager em = dbc.abrirConexion();
        List<Citas> citas = null;
        int numCitas =0;
        //ejecutamos consulta e gardamos o resultado en unha lista
        try {
            em.getTransaction().begin();
            TypedQuery<Citas> consulta = em.createQuery("SELECT c FROM Citas c WHERE c.clientes.idcliente = :idcliente AND c.estados.idestado = 1", Citas.class);
            consulta.setParameter("idcliente", idcliente);
            citas = consulta.getResultList();
            if (!citas.isEmpty()) {

                for (Citas cita : citas) {
                    Estados estado = new Estados(2);
                    cita.setEstados(estado);

                }


            }
            em.getTransaction().commit();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);

        }

    }

}
