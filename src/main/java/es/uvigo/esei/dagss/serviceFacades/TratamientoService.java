/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.esei.dagss.serviceFacades;

import es.uvigo.esei.dagss.dominio.daos.RecetaDAO;
import es.uvigo.esei.dagss.dominio.daos.TratamientoDAO;
import es.uvigo.esei.dagss.dominio.entidades.EstadoReceta;
import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import es.uvigo.esei.dagss.dominio.entidades.Receta;
import es.uvigo.esei.dagss.dominio.entidades.Tratamiento;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author cesar
 */
@Stateless
public class TratamientoService {
    @EJB
    TratamientoDAO tratamientoDAO;
    @EJB
    RecetaDAO recetaDAO;
    public void guardarTratamiento(Tratamiento t){
        long milis = t.getFechaFin().getTime() - t.getFechaInicio().getTime();
        int dias = (int) Math.floor(milis / (1000 * 60 * 60 * 24));
        tratamientoDAO.crear(t);
        Calendar fechaInicio = Calendar.getInstance();
        Calendar fechaFin = Calendar.getInstance();
        for(Prescripcion p: t.getPrescipciones()){
            fechaInicio.setTime(t.getFechaInicio());
            int dosisPorDia = p.getDosis();
            int unidadesPorCaja = p.getMedicamento().getNumeroDosis();
            int totalDosisTratamiento = dosisPorDia * dias;
            int numRecetas = (int)Math.ceil((float)totalDosisTratamiento/(float)unidadesPorCaja);
            int periodoReceta = (int)Math.floor(unidadesPorCaja/dosisPorDia);
            for(int i=0;i<numRecetas;i++){
                fechaFin.setTime(fechaInicio.getTime());
                fechaFin.add(Calendar.DAY_OF_YEAR, periodoReceta);
                recetaDAO.crear(new Receta(p, 1, fechaInicio.getTime(), fechaFin.getTime(), EstadoReceta.GENERADA));
                fechaInicio.setTime(fechaFin.getTime());
            }
        }        
    }
}
