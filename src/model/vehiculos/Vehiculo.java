package model.vehiculos;

import interfaces.Chargeable;
import interfaces.Trackable;
import model.campus.Edificio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Vehiculo implements Chargeable, Trackable, Serializable {
    // ATRIBUTOS COMUNES A TODOS LOS VEHÍCULOS
    protected String id;
    protected Bateria bateria;
    protected Edificio ubicacionActual;
    protected List<Edificio> historialUbicaciones;
    protected String estado; // DISPONIBLE, EN_ENTREGA, EN_CARGA, FUERA_SERVICIO
    protected double capacidadCarga; // kg
    protected double consumoEnergia; // kWh por km

    // CONSTRUCTOR PROTEGIDO (para que solo lo usen las subclases)
    protected Vehiculo(String id, double capacidadBateria, double nivelMinimoBateria,
                       double capacidadCarga, double consumoEnergia, Edificio ubicacionInicial) {
        this.id = id;
        this.bateria = new Bateria(capacidadBateria, nivelMinimoBateria);
        this.capacidadCarga = capacidadCarga;
        this.consumoEnergia = consumoEnergia;
        this.ubicacionActual = ubicacionInicial;
        this.historialUbicaciones = new ArrayList<>();
        this.estado = "DISPONIBLE";

        // Registrar ubicación inicial en el historial
        if (ubicacionInicial != null) {
            this.historialUbicaciones.add(ubicacionInicial);
        }
    }

    // MÉTODOS ABSTRACTOS (cada subclase los implementa diferente)
    public abstract String getTipo();
    public abstract double calcularVelocidadPromedio();

    // METODO CONCRETO QUE PUEDEN SOBREESCRIBIR
    public double estimateEnergyCost(double distancia) {
        return distancia * consumoEnergia;
    }

    // IMPLEMENTACIÓN DE CHARGEABLE
    @Override
    public void cargarBateria(double cantidad) {
        bateria.cargar(cantidad);
    }

    @Override
    public double getEstadoBateria() {
        return bateria.getPorcentaje();
    }

    // IMPLEMENTACIÓN DE TRACKABLE
    @Override
    public Edificio getUbicacionActual() {
        return ubicacionActual;
    }

    @Override
    public void actualizarUbicacion(Edificio nuevaUbicacion) {
        if (nuevaUbicacion != null) {
            this.historialUbicaciones.add(this.ubicacionActual);
            this.ubicacionActual = nuevaUbicacion;
        }
    }

    @Override
    public List<Edificio> getHistorialUbicaciones() {
        return new ArrayList<>(historialUbicaciones);
    }

    // MÉTODOS DE NEGOCIO
    public boolean puedeTransportar(double peso) {
        return peso <= capacidadCarga && estado.equals("DISPONIBLE");
    }

    public boolean tieneSuficienteBateria(double distancia) {
        double energiaRequerida = estimateEnergyCost(distancia);
        return bateria.getNivelActual() >= energiaRequerida;
    }

    public boolean realizarViaje(double distancia) {
        double energiaRequerida = estimateEnergyCost(distancia);
        return bateria.consumir(energiaRequerida);
    }

    public void cambiarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
    }

    // GETTERS Y SETTERS
    public String getId() { return id; }
    public Bateria getBateria() { return bateria; }
    public String getEstado() { return estado; }
    public double getCapacidadCarga() { return capacidadCarga; }
    public double getConsumoEnergia() { return consumoEnergia; }

    public void setUbicacionActual(Edificio ubicacion) {
        this.ubicacionActual = ubicacion;
    }

    // MÉTODOS DE VERIFICACIÓN
    public boolean estaDisponible() {
        return "DISPONIBLE".equals(estado) && !bateria.necesitaCarga();
    }

    public boolean necesitaRecarga() {
        return bateria.necesitaCarga();
    }

    // TO STRING
    @Override
    public String toString() {
        return String.format("%s %s - Batería: %.1f%% - Ubicación: %s - Estado: %s",
                getTipo(), id, getEstadoBateria(),
                ubicacionActual != null ? ubicacionActual.getId() : "N/A",
                estado);
    }

    // EQUALS Y HASHCODE (basados en ID)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vehiculo vehiculo = (Vehiculo) obj;
        return id.equals(vehiculo.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}