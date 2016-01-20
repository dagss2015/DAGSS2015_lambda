/*
 Proyecto Java EE, DAGSS-2013
 */

package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

@Stateless
@LocalBean
public class PrescripcionDAO extends GenericoDAO<Prescripcion>{
    
    // Completar aqui
    
    public List<Prescripcion> buscarPorTratamiento(Long idTratamiento) {
        Query q = em.createQuery("SELECT p FROM Prescripcion AS p "
                + "  WHERE p.tratamiento.id = :idtratamiento");
        q.setParameter("idtratamiento", idTratamiento);
        return q.getResultList();
    }
}
