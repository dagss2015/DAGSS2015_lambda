/*
 Proyecto Java EE, DAGSS-2014
 */
package es.uvigo.esei.dagss.dominio.daos;

import es.uvigo.esei.dagss.dominio.entidades.Medicamento;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

@Stateless
@LocalBean
public class MedicamentoDAO extends GenericoDAO<Medicamento> {

    public List<Medicamento> buscarPorNombre(String nombre) {
        Query q = em.createQuery("SELECT m FROM Medicamento AS m "
                + "  WHERE m.nombre LIKE :nombre");
        q.setParameter("nombre", "%"+nombre+"%");

        return q.getResultList();
    }

    public List<Medicamento> buscarPorFabricante(String fabricante) {
        Query q = em.createQuery("SELECT m FROM Medicamento AS m "
                + "  WHERE m.fabricante like :fabricante");
        q.setParameter("fabricante", "%"+fabricante+"%");

        return q.getResultList();
    }

    public List<Medicamento> buscarPorPpioActivo(String ppioActivo) {
         Query q = em.createQuery("SELECT m FROM Medicamento AS m "
                + "  WHERE m.principioActivo like :ppioactivo");
        q.setParameter("ppioactivo", "%"+ppioActivo+"%");

        return q.getResultList();
    }

    public Medicamento buscarMedicamentoPorReceta(Long idReceta){
        Query q = em.createQuery("SELECT m FROM Medicamento AS m,Prescripcion AS p,Receta AS r "
                + "  WHERE m.id = p.medicamento.id AND p.id = r.prescripcion.id AND r.id = :idreceta");
        q.setParameter("idreceta", idReceta);
        return filtrarResultadoUnico(q);
    }
}
