/*
 Proyecto Java EE, DAGSS-2013
 */

package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Tratamiento;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

@Stateless
@LocalBean
public class TratamientoDAO extends GenericoDAO<Tratamiento>{
    
    // Completar aqui
    
    public List<Tratamiento> buscarPorPaciente(Long idPaciente) {
        Query q = em.createQuery("SELECT t FROM Tratamiento AS t "
                + "  WHERE t.paciente.id = :idpaciente AND CURRENT_TIMESTAMP > t.fechaInicio AND CURRENT_TIMESTAMP < t.fechaFin");
        q.setParameter("idpaciente", idPaciente);
        return q.getResultList();
    }
}
