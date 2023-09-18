package accesodatos;


import modelos.Estados;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class EstadosDAO {

    DatabaseConection dbc = new DatabaseConection();

    public List<String> nombreEstados () {

        EntityManager em = dbc.abrirConexion();
        List<String> nombresEstados = null;

        //ejecutamos consulta e gardamos o resultado en unha lista
        try {
            TypedQuery<String> query = em.createQuery("SELECT e.nombre FROM Estados e", String.class);
            nombresEstados = query.getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            dbc.cerrarConexion(em);
            return nombresEstados;
        }

    }
}
