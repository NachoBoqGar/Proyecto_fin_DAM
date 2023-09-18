package accesodatos;

import modelos.Empleados;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.Date;
import java.util.List;

public class EmpleadosDAO {
    //variable para abrir conexión a BBDD
    DatabaseConection dbc = new DatabaseConection();

    //metodo para validar usuario e permitir o login
    public boolean validarEmpleado( String dni, String pass){

        EntityManager em = dbc.abrirConexion();

        Empleados empleado;
        try {
            //buscamos o empleado polo dni e gardamolo nun obxecto de tipo empleado
            TypedQuery<Empleados> query = em.createQuery("SELECT emp FROM Empleados emp WHERE emp.dni = :dni", Empleados.class);
            query.setParameter("dni", dni);
            empleado = query.getSingleResult();

            //si non obtemos nada da consulta o empleado non existe
            //si a pass introducida non coincide ca pass do usuario denegamos login
            if (empleado == null) {
                return false;
            }
            if (!empleado.getPass().equals(pass)) {
                return false;
            }

            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        finally {
            dbc.cerrarConexion(em);

        }

    }

    //metodo para obter nome dado un dni
    public String getNombreCompletoEmpleado(String dni){
        EntityManager em = dbc.abrirConexion();


        try {
            //buscamos o empleado polo dni e gardamolo nun obxecto de tipo empleado
            TypedQuery<String> query = em.createQuery("SELECT CONCAT(emp.nombre, ' ', emp.apellido1, ' ', emp.apellido2) FROM Empleados emp WHERE emp.dni = :dni", String.class);
            query.setParameter("dni", dni);
            String nombre = query.getSingleResult();

            return nombre;

        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
        finally {
            dbc.cerrarConexion(em);

        }
    }

    //metodo para obter o rol de un empleado
    public String getRolEmpleado(String dni){
        EntityManager em = dbc.abrirConexion();

        try {
            //buscamos o empleado polo dni e gardamolo nun obxecto de tipo empleado
            //emp.roles.nombre -> accedemos directamente a nome a través da relación que temos establecida
            TypedQuery<String> query = em.createQuery("SELECT emp.roles.nombre FROM Empleados emp WHERE emp.dni = :dni" , String.class);
            query.setParameter("dni", dni);
            String rol = query.getSingleResult();

            return rol;

        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
        finally {
            dbc.cerrarConexion(em);

        }
    }

    //metodo para obter o id de un empleado polo seu DNI
    public int getIdEmpleado(String dni){
        EntityManager em = dbc.abrirConexion();

        try {
            TypedQuery<Integer> query = em.createQuery("SELECT idempleado FROM Empleados emp WHERE emp.dni = :dni" , Integer.class);
            query.setParameter("dni", dni);
            int id = query.getSingleResult();

            return id;

        }catch (Exception ex){
            ex.printStackTrace();
            return -1;
        }
        finally {
            dbc.cerrarConexion(em);

        }
    }

  //metodo para buscar empleados que non teñen asignadas citas para unha hora e dia
    public  List<String> empleadosLibresParaCita(Date fechaCita){

        List<String> empleadosLibres = null;

        EntityManager em = dbc.abrirConexion();


        //ejecutamos consulta e gardamos o resultado en unha lista
        try {
            TypedQuery<String> consulta = em.createQuery("SELECT CONCAT(e.idempleado, ' - ', e.nombre, ' ', e.apellido1, ' ', e.apellido2) FROM Empleados e WHERE NOT EXISTS (SELECT c.idcita FROM Citas c  WHERE c.empleados.idempleado = e AND c.fecha = :fechaCita) AND e.activo=1", String.class);
            consulta.setParameter("fechaCita", fechaCita);
            empleadosLibres = consulta.getResultList();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
            return empleadosLibres;
        }


    }

    //metodo que devolve a lista de empleados en funcion da consulta que se pasa como parametro
        public List<Empleados> listadoEmpleados(String consulta) {

        EntityManager em = dbc.abrirConexion();
        List<Empleados> listaEmpleados = null;

        //ejecutamos consulta e gardamos o resultado en unha lista
        try {
            TypedQuery<Empleados> query = em.createQuery(consulta, Empleados.class);
            listaEmpleados = query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
            return listaEmpleados;
        }
    }

    public void actualizarEmpleado(Empleados empleadoActualizado) {

        EntityManager em = dbc.abrirConexion();


        try {

            em.getTransaction().begin();
            //buscamos o cliente a actualizar polo seu id e asignamoslle os valores actualizados
            Empleados empleado = em.find(Empleados.class, empleadoActualizado.getIdempleado());
            empleado.setDni(empleadoActualizado.getDni());
            empleado.setNombre(empleadoActualizado.getNombre());
            empleado.setApellido1(empleadoActualizado.getApellido1());
            empleado.setApellido2(empleadoActualizado.getApellido2());
            empleado.setCorreo(empleadoActualizado.getCorreo());
            empleado.setTelefono(empleadoActualizado.getTelefono());
            empleado.setDireccion(empleadoActualizado.getDireccion());
            empleado.setPass(empleadoActualizado.getPass());
            empleado.setRoles(empleadoActualizado.getRoles());

            em.getTransaction().commit();

        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
        }
    }

    //comprobamos si hay un empleado co dni indicado
    public boolean existeEmpleado(String dni) {

        EntityManager em = dbc.abrirConexion();

        boolean existeEmpleado = false;
        //buscamos si existe un usuario con ese DNI
        try {
            TypedQuery<Empleados> consulta = em.createQuery("SELECT e FROM Empleados e WHERE e.dni = :dni", Empleados.class);
            consulta.setParameter("dni",dni);
            List<Empleados> clientes = consulta.getResultList();
            //comprobamos si hai resultados, si hay é que existe un cliente
            if (!clientes.isEmpty()) {
                existeEmpleado =  true;
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            dbc.cerrarConexion(em);
            return existeEmpleado;

        }
    }


    public void cambioEstadoEmpleado(int id, int activo) {

        EntityManager em = dbc.abrirConexion();

        try {
            em.getTransaction().begin();
            Query consulta = em.createQuery("UPDATE Empleados e SET e.activo = :activo WHERE e.idempleado = :idempleado");
            consulta.setParameter("idempleado", id);
            consulta.setParameter("activo", activo);
            consulta.executeUpdate();
            em.getTransaction().commit();

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            dbc.cerrarConexion(em);
        }
    }

    //alta de un nuevo cliente
    public void altaNuevoEmpleado(Empleados empleado) {

        EntityManager em = dbc.abrirConexion();

        try {

            EntityTransaction transacion = em.getTransaction();
            transacion.begin();
            em.persist(empleado);
            transacion.commit();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            dbc.cerrarConexion(em);
        }
    }

    //obter empleado polpo seu id
    public Empleados empleadoPorID(int id) {

        EntityManager em = dbc.abrirConexion();
        Empleados empleado = null;

        //ejecutamos consulta e gardamos o resultado en unha lista
        try {
            TypedQuery<Empleados> consulta = em.createQuery("SELECT e FROM Empleados e WHERE e.idempleado = :id", Empleados.class);
            consulta.setParameter("id",id);
            empleado = consulta.getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
            return empleado;
        }
    }
}
