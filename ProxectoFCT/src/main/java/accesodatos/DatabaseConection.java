package accesodatos;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DatabaseConection {

    public static EntityManagerFactory entityManagerFactory;

    //metodo  para abrir conexión coa BBDD
    public static EntityManager abrirConexion() {

        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("persistencia_aplicacion");
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            return entityManager;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //método  para cerrar conexión coa BBDD
    public static void cerrarConexion(EntityManager em) {

        em.close();
        entityManagerFactory.close();
    }




}
