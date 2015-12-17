/*
 Proyecto Java EE, DAGSS-2013
 */
package es.uvigo.esei.dagss.controladores.medico;

import es.uvigo.esei.dagss.controladores.autenticacion.AutenticacionControlador;
import es.uvigo.esei.dagss.dominio.daos.CitaDAO;
import es.uvigo.esei.dagss.dominio.daos.MedicamentoDAO;
import es.uvigo.esei.dagss.dominio.daos.MedicoDAO;
import es.uvigo.esei.dagss.dominio.entidades.Cita;
import es.uvigo.esei.dagss.dominio.entidades.EstadoCita;
import es.uvigo.esei.dagss.dominio.entidades.Medicamento;
import es.uvigo.esei.dagss.dominio.entidades.Medico;
import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import es.uvigo.esei.dagss.dominio.entidades.TipoUsuario;
import es.uvigo.esei.dagss.dominio.entidades.Tratamiento;
import es.uvigo.esei.dagss.serviceFacades.TratamientoService;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
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

@Named(value = "medicoControlador")
@SessionScoped
public class MedicoControlador implements Serializable {

    private Medico medicoActual;
    private String dni;
    private String numeroColegiado;
    private String password;
    private Cita citaActual;
    private String textoBuscar;
    private int dosisAPrescribir;
    private String indicaciones;
    private Tratamiento tratamiento = new Tratamiento();
    private List<Prescripcion> prescripcionesDisponibles = new LinkedList<>();
    private EstadoCita estadoCita;


    @Inject
    private AutenticacionControlador autenticacionControlador;
    
    @EJB
    private CitaDAO citaDAO;

    @EJB
    private MedicoDAO medicoDAO;
    
    @EJB
    private MedicamentoDAO medicamentoDAO;
        
    @EJB
    private TratamientoService tratamientoService;

    /**
     * Creates a new instance of AdministradorControlador
     */
    public MedicoControlador() {
    }
    
    public Cita getCitaActual() {
        return citaActual;
    }

    public void setCitaActual(Cita citaActual) {
        this.citaActual = citaActual;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNumeroColegiado() {
        return numeroColegiado;
    }

    public void setNumeroColegiado(String numeroColegiado) {
        this.numeroColegiado = numeroColegiado;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Medico getMedicoActual() {
        return medicoActual;
    }

    public void setMedicoActual(Medico medicoActual) {
        this.medicoActual = medicoActual;
    }

    private boolean parametrosAccesoInvalidos() {
        return (((dni == null) && (numeroColegiado == null)) || (password == null));
    }

    public String getTextoBuscar() {
        return textoBuscar;
    }

    public void setTextoBuscar(String textoBuscar) {
        this.textoBuscar = textoBuscar;
    }

    public List<Prescripcion> getPrescripcionesDisponibles() {
        return this.prescripcionesDisponibles;
    }

    public void setPrescripcionesDisponibles(List<Prescripcion> prescripcionesDisponibles) {
        this.prescripcionesDisponibles = prescripcionesDisponibles;
    }

    public EstadoCita getEstadoCita() {
        return estadoCita;
    }

    public void setEstadoCita(EstadoCita estadoCita) {
        this.estadoCita = estadoCita;
    }

    private Medico recuperarDatosMedico() {
        Medico medico = null;
        if (dni != null) {
            medico = medicoDAO.buscarPorDNI(dni);
        }
        if ((medico == null) && (numeroColegiado != null)) {
            medico = medicoDAO.buscarPorNumeroColegiado(numeroColegiado);
        }
        return medico;
    }

    public String doLogin() {
        String destino = null;
        if (parametrosAccesoInvalidos()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No se ha indicado un DNI ó un número de colegiado o una contraseña", ""));
        } else {
            Medico medico = recuperarDatosMedico();
            if (medico == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No existe ningún médico con los datos indicados", ""));
            } else {
                if (autenticacionControlador.autenticarUsuario(medico.getId(), password,
                        TipoUsuario.MEDICO.getEtiqueta().toLowerCase())) {
                    medicoActual = medico;
                    destino = "privado/index";
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Credenciales de acceso incorrectas", ""));
                }
            }
        }
        return destino;
    }

    //Acciones
    public String doShowCita(Cita cita) {
        citaActual = cita;
        return "detallesCita";
    }
    
    public List<Cita> getCitas(){
        return medicoDAO.buscarCitasMedicoPorDia(medicoActual);
    }
    
    public void buscarMedicamentoPorNombre(){
        this.prescripcionesDisponibles = medToPres(medicamentoDAO.buscarPorNombre(textoBuscar));
    }
    
    public void buscarMedicamentoPorFabricante(){
        this.prescripcionesDisponibles = medToPres(medicamentoDAO.buscarPorFabricante(textoBuscar));
    }
    
    public void buscarMedicamentoPorPpioActivo(){
        this.prescripcionesDisponibles = medToPres(medicamentoDAO.buscarPorPpioActivo(textoBuscar));
    }
    
    public void prescribir(Prescripcion prescripcion){
       this.tratamiento.getPrescipciones().add(prescripcion);
    }

    public int getDosisAPrescribir() {
        return dosisAPrescribir;
    }

    public void setDosisAPrescribir(int dosisAPrescribir) {
        this.dosisAPrescribir = dosisAPrescribir;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }
    
    public void eliminar(Prescripcion prescripcion){
        this.tratamiento.getPrescipciones().remove(prescripcion);
    }
    
    private List<Prescripcion> medToPres(List<Medicamento> med){
        List<Prescripcion> ret = new LinkedList<>();
        for(Medicamento m: med){
            ret.add(new Prescripcion(null, null, m, null));
        }
        return ret;
    }
    
    public EstadoCita[] getEstadosCita(){
        return EstadoCita.values();
    }
    
    public String finalizarCita(){
        if(this.citaActual.getEstado().equals(EstadoCita.COMPLETADA)){
            this.tratamientoService.guardarTratamiento(this.tratamiento);
        }
        this.citaDAO.actualizar(citaActual);
        return "agenda";
    }
}
