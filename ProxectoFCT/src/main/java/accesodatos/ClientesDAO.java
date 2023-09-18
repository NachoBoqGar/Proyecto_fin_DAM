package accesodatos;

import modelos.Clientes;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class ClientesDAO {

    //variable para abrir conexión a BBDD
    DatabaseConection dbc = new DatabaseConection();

    //alta de novo cliente
    public void altaNuevoCliente (Clientes cliente){

        EntityManager em = dbc.abrirConexion();

        try {

            EntityTransaction transacion = em.getTransaction();
            transacion.begin();
            em.persist(cliente);
            transacion.commit();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            dbc.cerrarConexion(em);
        }

    }

    public void actualizarCliente (Clientes clienteActualizado){

        EntityManager em = dbc.abrirConexion();


        try {

            em.getTransaction().begin();
            //buscamos o cliente a actualizar polo seu id e asignamoslle os valores actualizados
            Clientes cliente = em.find(Clientes.class, clienteActualizado.getIdcliente());
            cliente.setDni(clienteActualizado.getDni());
            cliente.setNombre(clienteActualizado.getNombre());
            cliente.setApellido1(clienteActualizado.getApellido1());
            cliente.setApellido2(clienteActualizado.getApellido2());
            cliente.setCorreo(clienteActualizado.getCorreo());
            cliente.setTelefono(clienteActualizado.getTelefono());
            cliente.setDireccion(clienteActualizado.getDireccion());

            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
        }


    }

    //consultar clientes
    public List<Clientes> listadoClientes (String consulta) {

        EntityManager em = dbc.abrirConexion();
        List<Clientes> listaClientes = null;

        //ejecutamos consulta e gardamos o resultado en unha lista
        try {
            TypedQuery<Clientes> query = em.createQuery(consulta, Clientes.class);
            listaClientes = query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
            return listaClientes;
        }

    }

    public boolean existeCliente(String dni){

        EntityManager em = dbc.abrirConexion();
        boolean existeCliente = false;
        //buscamos si existe un usuario con ese DNI
        try {
            TypedQuery<Clientes> consulta = em.createQuery("SELECT c FROM Clientes c WHERE c.dni = :dni", Clientes.class);
            consulta.setParameter("dni",dni);
            List<Clientes> clientes = consulta.getResultList();
            //comprobamos si hai resultados, si hay é que existe un cliente
            if (!clientes.isEmpty()) {
                existeCliente =  true;
            }

        } catch (Exception ex) {
            System.out.println("non hai cliente");
            ex.printStackTrace();

        } finally {
            dbc.cerrarConexion(em);
            return existeCliente;

        }
    }

    //Metodo para dar de baja un cliente
    public void cambioEstadoCLiente(int id, int activo){

        EntityManager em = dbc.abrirConexion();

        try {
            em.getTransaction().begin();
            Query consulta = em.createQuery("UPDATE Clientes c SET c.activo = :activo WHERE c.idcliente = :id");
            consulta.setParameter("id", id);
            consulta.setParameter("activo", activo);
            consulta.executeUpdate();
            em.getTransaction().commit();

        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            dbc.cerrarConexion(em);
        }

    }

    //obter cliente por id
    public Clientes clientePorID(int id) {

        EntityManager em = dbc.abrirConexion();

        //buscamos si existe un usuario con ese DNI
        Clientes cliente = null;
        try {
            TypedQuery<Clientes> consulta = em.createQuery("SELECT c FROM Clientes c WHERE c.idcliente = :id", Clientes.class);
            consulta.setParameter("id", id);
            cliente = consulta.getSingleResult();

        } catch (Exception ex) {
            System.out.println("non hai cliente");
            ex.printStackTrace();

        } finally {
            dbc.cerrarConexion(em);
            return cliente;
        }
    }
}
