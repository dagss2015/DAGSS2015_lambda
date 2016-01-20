/*
 Proyecto Java EE, DAGSS-2014
 */
package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Medico;
import es.uvigo.esei.dagss.dominio.entidades.Paciente;
import es.uvigo.esei.dagss.dominio.entidades.Cita;
import es.uvigo.esei.dagss.dominio.entidades.EstadoCita;
import java.util.Calendar;
import java.util.Date;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

@Stateless
@LocalBean
public class MedicoDAO extends GenericoDAO<Medico> {

    public Medico buscarPorDNI(String dni) {
        Query q = em.createQuery("SELECT m FROM Medico AS m "
                + "  WHERE m.dni = :dni");
        q.setParameter("dni", dni);

        return filtrarResultadoUnico(q);
    }

    public Medico buscarPorNumeroColegiado(String numeroColegiado) {
        Query q = em.createQuery("SELECT m FROM Medico AS m "
                + "  WHERE m.numeroColegiado = :numeroColegiado");
        q.setParameter("numeroColegiado", numeroColegiado);
        
        return filtrarResultadoUnico(q);
    }

    public List<Paciente> buscarPorNombre(String patron) {
        Query q = em.createQuery("SELECT m FROM Medico AS m "
                + "  WHERE (m.nombre LIKE :patron) OR "
                + "        (m.apellidos LIKE :patron)");
        q.setParameter("patron","%"+patron+"%");        
        return q.getResultList();
    }
    

    // Completar aqui

    public List<Cita> buscarCitasMedicoPorDia(Medico m) {
        
        Query q = em.createQuery("SELECT c FROM Cita AS c "
                + "  WHERE (c.fecha = :fecha ) AND "
                + "        (c.medico.id = :id) AND (c.estado = :estado)");
        q.setParameter("id",m.getId());
        Date d = Calendar.getInstance().getTime();
        q.setParameter("fecha",d);
        q.setParameter("estado", EstadoCita.PLANIFICADA);
        return q.getResultList();
    }
    
    public Medico buscarMedicoPorReceta(Long idReceta){
        Query q = em.createQuery("SELECT m FROM Medico AS m,Prescripcion AS p,Receta AS r,Tratamiento AS t "
                + "  WHERE m.id = t.medico.id AND p.id = r.prescripcion.id AND t.id = p.tratamiento.id AND r.id = :idreceta");
        q.setParameter("idreceta", idReceta);
        return filtrarResultadoUnico(q);
    }
}
