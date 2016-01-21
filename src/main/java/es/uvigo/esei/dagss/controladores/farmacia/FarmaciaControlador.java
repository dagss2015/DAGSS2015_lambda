/*
 Proyecto Java EE, DAGSS-2013
 */
package es.uvigo.esei.dagss.controladores.farmacia;

import es.uvigo.esei.dagss.controladores.autenticacion.AutenticacionControlador;
import es.uvigo.esei.dagss.dominio.daos.FarmaciaDAO;
import es.uvigo.esei.dagss.dominio.daos.MedicamentoDAO;
import es.uvigo.esei.dagss.dominio.daos.MedicoDAO;
import es.uvigo.esei.dagss.dominio.daos.PacienteDAO;
import es.uvigo.esei.dagss.dominio.daos.PrescripcionDAO;
import es.uvigo.esei.dagss.dominio.daos.RecetaDAO;
import es.uvigo.esei.dagss.dominio.daos.TratamientoDAO;
import es.uvigo.esei.dagss.dominio.entidades.EstadoReceta;
import es.uvigo.esei.dagss.dominio.entidades.Farmacia;
import es.uvigo.esei.dagss.dominio.entidades.Medicamento;
import es.uvigo.esei.dagss.dominio.entidades.Medico;
import es.uvigo.esei.dagss.dominio.entidades.Paciente;
import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import es.uvigo.esei.dagss.dominio.entidades.Receta;
import es.uvigo.esei.dagss.dominio.entidades.TipoUsuario;
import es.uvigo.esei.dagss.dominio.entidades.Tratamiento;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author ribadas
 */
@Named(value = "farmaciaControlador")
@SessionScoped
public class FarmaciaControlador implements Serializable {

    private Farmacia farmaciaActual;
    private String nif;
    private String password;
    private String textoBuscar;
    private List<Receta> listaRecetas;
    private Paciente pacienteActual;
    private List<Tratamiento> listaTratamientos;
    private List<Prescripcion> listaPrescripciones;

    @Inject
    private AutenticacionControlador autenticacionControlador;

    @EJB
    private FarmaciaDAO farmaciaDAO;
    
    @EJB
    private PacienteDAO pacienteDAO;
    
    @EJB
    private TratamientoDAO tratamientoDAO;
    
    @EJB
    private PrescripcionDAO prescripcionDAO;
    
    @EJB
    private RecetaDAO recetaDAO;
    
    @EJB
    private MedicamentoDAO medicamentoDAO;
    
    @EJB
    private MedicoDAO medicoDAO;

    /**
     * Creates a new instance of AdministradorControlador
     */
    public FarmaciaControlador() {
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Farmacia getFarmaciaActual() {
        return farmaciaActual;
    }

    public void setFarmaciaActual(Farmacia farmaciaActual) {
        this.farmaciaActual = farmaciaActual;
    }

    private boolean parametrosAccesoInvalidos() {
        return ((nif == null) || (password == null));
    }

    public String doLogin() {
        String destino = null;
        if (parametrosAccesoInvalidos()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No se ha indicado un nif o una contraseña", ""));
        } else {
            Farmacia farmacia = farmaciaDAO.buscarPorNIF(nif);
            if (farmacia == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No existe una farmacia con el NIF " + nif, ""));
            } else {
                if (autenticacionControlador.autenticarUsuario(farmacia.getId(), password,
                        TipoUsuario.FARMACIA.getEtiqueta().toLowerCase())) {
                    farmaciaActual = farmacia;
                    destino = "privado/index";
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Credenciales de acceso incorrectas", ""));
                }

            }
        }
        return destino;
    }

    public String getTextoBuscar() {
        return textoBuscar;
    }

    public void setTextoBuscar(String textoBuscar) {
        this.textoBuscar = textoBuscar;
    }

    public List<Receta> getListaRecetas() {
        return listaRecetas;
    }

    public void setListaRecetas(List<Receta> listaRecetas) {
        this.listaRecetas = listaRecetas;
    }

    public Paciente getPacienteActual() {
        return pacienteActual;
    }

    public void setPacienteActual(Paciente pacienteActual) {
        this.pacienteActual = pacienteActual;
    }

    public List<Tratamiento> getListaTratamientos() {
        return listaTratamientos;
    }

    public void setListaTratamientos(List<Tratamiento> listaTratamientos) {
        this.listaTratamientos = listaTratamientos;
    }

    public List<Prescripcion> getListaPrescripciones() {
        return listaPrescripciones;
    }

    public void setListaPrescripciones(List<Prescripcion> listaPrescripciones) {
        this.listaPrescripciones = listaPrescripciones;
    }
    
    public void buscarClienteDNI(){
        pacienteActual = pacienteDAO.buscarPorDNI(textoBuscar);
        buscarTratamientos();
        buscarPrescripciones();
        buscarRecetas();
    }
    
    public void buscarTratamientos(){
        listaTratamientos = tratamientoDAO.buscarPorPaciente(pacienteActual.getId());
    }
    
    public void buscarPrescripciones(){
        for(Tratamiento t: listaTratamientos){
            listaPrescripciones = new LinkedList<>(prescripcionDAO.buscarPorTratamiento(t.getId()));
        }
    }
    
    public void buscarRecetas(){
        for(Prescripcion p: listaPrescripciones){
            listaRecetas = new LinkedList<>(recetaDAO.buscarPorPrescripcion(p.getId()));
        }
    }
    
    public String recuperarMedicamentoReceta(Long idReceta){
        Medicamento m = medicamentoDAO.buscarMedicamentoPorReceta(idReceta);
        return m.getNombre();
    }
    
    public String recuperarMedicoReceta(Long idReceta){
        Medico m = medicoDAO.buscarMedicoPorReceta(idReceta);
        return m.getNombre();
    }
    
    public boolean servirMedicamento(Receta receta){
        if(receta==null) return false;
            if(receta.getEstado().equals(EstadoReceta.GENERADA) && 
                (receta.getInicioValidez().before(Calendar.getInstance().getTime()) ||
                 receta.getInicioValidez().equals(Calendar.getInstance().getTime())) && 
                (receta.getFinValidez().after(Calendar.getInstance().getTime()) ||
                 receta.getFinValidez().equals(Calendar.getInstance().getTime()))) 
                    return true;
        return false;
    }
    
    public void suministrarReceta(Receta receta){
        receta.setEstado(EstadoReceta.SERVIDA);
        receta.setFarmacia(farmaciaActual);
        recetaDAO.actualizar(receta);
    }
}
