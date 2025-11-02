package model.pedidos;

import model.campus.Edificio;
import model.vehiculos.Vehiculo;

public class Pedido {
    private final String id;
    private final Edificio origen;
    private final Edificio destino;
    private final double peso; // en kg
    private EstadoPedido estado;
    private Vehiculo vehiculoAsignado;

    // CONSTRUCTOR
    public Pedido(String id, Edificio origen, Edificio destino, double peso) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Origen y destino no pueden ser nulos");
        }
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso debe ser positivo");
        }

        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
        this.estado = EstadoPedido.RECIBIDO;
        this.vehiculoAsignado = null;
    }

    // GETTERS
    public String getId() { return id; }
    public Edificio getOrigen() { return origen; }
    public Edificio getDestino() { return destino; }
    public double getPeso() { return peso; }
    public EstadoPedido getEstado() { return estado; }
    public Vehiculo getVehiculoAsignado() { return vehiculoAsignado; }

    // MÉTODOS PARA CAMBIAR ESTADO
    public void asignarVehiculo(Vehiculo vehiculo) {
        this.vehiculoAsignado = vehiculo;
        this.estado = EstadoPedido.PREPARACION;
    }

    public void iniciarEntrega() {
        if (vehiculoAsignado == null) {
            throw new IllegalStateException("No se puede iniciar entrega sin vehículo asignado");
        }
        this.estado = EstadoPedido.EN_CAMINO;
    }

    public void marcarEntregado() {
        if (vehiculoAsignado == null) {
            throw new IllegalStateException("No se puede marcar como entregado sin vehículo asignado");
        }
        this.estado = EstadoPedido.ENTREGADO;
    }

    public void cancelar() {
        this.estado = EstadoPedido.CANCELADO;
        this.vehiculoAsignado = null;
    }

    // MÉTODOS DE VERIFICACIÓN
    public boolean estaPendiente() {
        return estado == EstadoPedido.RECIBIDO;
    }

    public boolean estaEnProceso() {
        return estado == EstadoPedido.PREPARACION || estado == EstadoPedido.EN_CAMINO;
    }

    public boolean estaCompletado() {
        return estado == EstadoPedido.ENTREGADO || estado == EstadoPedido.CANCELADO;
    }

    // TO STRING
    @Override
    public String toString() {
        String vehiculoInfo = vehiculoAsignado != null ? vehiculoAsignado.getId() : "Sin asignar";
        return "Pedido " + id + " - " + origen.getId() + " → " + destino.getId() +
                " (" + peso + "kg) - " + estado + " - Vehículo: " + vehiculoInfo;
    }

    // EQUALS Y HASHCODE (basados en ID)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pedido pedido = (Pedido) obj;
        return id.equals(pedido.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}