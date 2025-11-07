package model.vehiculos;

import Persistencia.PersistenceManager;
import interfaces.Chargeable;
import interfaces.Trackable;
import model.campus.Edificio;
import model.pedidos.Pedido;

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
    protected Pedido pedidoActual; // Pedido que está transportando actualmente
    protected List<String> historialEventos; // Registro de eventos del viaje

    // CONSTRUCTOR PROTEGIDO
    protected Vehiculo(String id, double capacidadBateria, double nivelMinimoBateria,
                       double capacidadCarga, double consumoEnergia, Edificio ubicacionInicial) {
        this.id = id;
        this.bateria = new Bateria(capacidadBateria, nivelMinimoBateria);
        this.capacidadCarga = capacidadCarga;
        this.consumoEnergia = consumoEnergia;
        this.ubicacionActual = ubicacionInicial;
        this.historialUbicaciones = new ArrayList<>();
        this.historialEventos = new ArrayList<>();
        this.estado = "DISPONIBLE";
        this.pedidoActual = null;

        // Registrar ubicación inicial en el historial
        if (ubicacionInicial != null) {
            this.historialUbicaciones.add(ubicacionInicial);
        }

        // Registrar evento inicial
        registrarEvento("Vehículo creado en " + ubicacionInicial.getId());
    }

    // MÉTODOS ABSTRACTOS (implementación polimórfica)
    public abstract String getTipo();
    public abstract double calcularVelocidadPromedio();
    public abstract void realizarEntrega(Edificio destino); // MÉTODO POLIMÓRFICO PRINCIPAL

    // MÉTODO PARA SIMULAR MOVIMIENTO ENTRE EDIFICIOS
    public void simularMovimiento(Edificio destino) {
        if (destino == null || destino.equals(ubicacionActual)) {
            registrarEvento("Movimiento cancelado - destino inválido o igual a origen");
            return;
        }

        if (pedidoActual == null) {
            registrarEvento("Movimiento cancelado - no hay pedido asignado");
            return;
        }

        // Calcular distancia (simulada)
        double distancia = calcularDistancia(ubicacionActual, destino);

        if (!tieneSuficienteBateria(distancia)) {
            registrarEvento("Movimiento cancelado - batería insuficiente para " + distancia + " km");
            return;
        }

        // Cambiar estado a EN_CAMINO
        cambiarEstado("EN_ENTREGA");
        registrarEvento("Iniciando entrega desde " + ubicacionActual.getId() + " hacia " + destino.getId());

        // Simular consumo de energía
        if (realizarViaje(distancia)) {
            // Actualizar ubicación
            actualizarUbicacion(destino);
            registrarEvento("Llegada a " + destino.getId() + " - Entrega completada");

            // Completar entrega
            completarEntrega();
        } else {
            registrarEvento("Error en el viaje - no se pudo completar la entrega");
        }
    }

    // MÉTODOS AUXILIARES PARA VEHÍCULOS (actualizados para usar métodos directos)
    private boolean estaDisponible(Vehiculo vehiculo) {
        return vehiculo.estaDisponible();
    }

    private boolean puedeTransportar(Vehiculo vehiculo, double peso) {
        return vehiculo.puedeTransportar(peso);
    }

    private double getBateria(Vehiculo vehiculo) {
        return vehiculo.getEstadoBateria();
    }

    private String getUbicacionVehiculo(Vehiculo vehiculo) {
        return vehiculo.getUbicacionActual() != null ? vehiculo.getUbicacionActual().getId() : null;
    }

    private String getIdVehiculo(Vehiculo vehiculo) {
        return vehiculo.getId();
    }

    // MÉTODO PARA CALCULAR DISTANCIA (SIMULADA)
    protected double calcularDistancia(Edificio origen, Edificio destino) {
        // En una implementación real, esto calcularía la distancia real
        // Por ahora, simulamos una distancia base
        return 1.5; // km
    }

    // MÉTODO PARA COMPLETAR ENTREGA
    protected void completarEntrega() {
        if (pedidoActual != null) {
            pedidoActual.marcarEntregado();
            registrarEvento("Pedido " + pedidoActual.getId() + " marcado como ENTREGADO");

            // Guardar el estado del pedido en persistencia
            List<Pedido> pedidos = PersistenceManager.cargarPedidos();
            if (pedidos != null) {
                pedidos.stream()
                        .filter(p -> p.getId().equals(pedidoActual.getId()))
                        .findFirst()
                        .ifPresent(p -> p.marcarEntregado());
                PersistenceManager.guardarPedidos(pedidos);
            }

            pedidoActual = null;
        }
        cambiarEstado("DISPONIBLE");

        // Guardar cambios del vehículo en persistencia
        PersistenceManager.guardarFlota(List.of(this));
    }

    // En Vehiculo.java - mejorar el método asignarPedido
    public void asignarPedido(Pedido pedido) {
        if (pedido != null && estaDisponible() && !pedido.estaCompletado()) {
            this.pedidoActual = pedido;
            cambiarEstado("PREPARACION");
            registrarEvento("Pedido " + pedido.getId() + " asignado - Origen: " +
                    pedido.getOrigen().getId() + " -> Destino: " + pedido.getDestino().getId());

            // Actualizar ubicación al origen del pedido si es diferente
            if (pedido.getOrigen() != null && !pedido.getOrigen().equals(this.ubicacionActual)) {
                actualizarUbicacion(pedido.getOrigen());
            }

            // Guardar cambios en persistencia inmediatamente
            PersistenceManager.guardarFlota(List.of(this));
        } else {
            String razon = pedido == null ? "pedido nulo" :
                    pedido.estaCompletado() ? "pedido ya completado" : "vehículo no disponible";
            registrarEvento("No se pudo asignar pedido - " + razon);
        }
    }

    // LIBERAR PEDIDO (para cancelaciones)
    public void liberarPedido() {
        if (pedidoActual != null) {
            registrarEvento("Pedido " + pedidoActual.getId() + " liberado");
            pedidoActual = null;
        }
        cambiarEstado("DISPONIBLE");
    }

    // REGISTRAR EVENTO EN EL HISTORIAL
    protected void registrarEvento(String evento) {
        String eventoConTimestamp = String.format("[%s] %s",
                java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")),
                evento);
        historialEventos.add(eventoConTimestamp);
        System.out.println(eventoConTimestamp); // Para ver en consola
    }

    // METODO CONCRETO QUE PUEDEN SOBREESCRIBIR
    public double estimateEnergyCost(double distancia) {
        return distancia * consumoEnergia;
    }

    // IMPLEMENTACIÓN DE CHARGEABLE
    @Override
    public void cargarBateria(double cantidad) {
        bateria.cargar(cantidad);
        registrarEvento("Batería cargada: +" + cantidad + " kWh - Nivel: " + getEstadoBateria() + "%");
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
        boolean exito = bateria.consumir(energiaRequerida);
        if (exito) {
            registrarEvento("Viaje realizado: " + distancia + " km - Energía consumida: " + energiaRequerida + " kWh");
        }
        return exito;
    }

    public void cambiarEstado(String nuevoEstado) {
        String estadoAnterior = this.estado;
        this.estado = nuevoEstado;
        registrarEvento("Estado cambiado: " + estadoAnterior + " -> " + nuevoEstado);
    }

    // GETTERS Y SETTERS
    public String getId() { return id; }
    public Bateria getBateria() { return bateria; }
    public String getEstado() { return estado; }
    public double getCapacidadCarga() { return capacidadCarga; }
    public double getConsumoEnergia() { return consumoEnergia; }
    public Pedido getPedidoActual() { return pedidoActual; }
    public List<String> getHistorialEventos() { return new ArrayList<>(historialEventos); }

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

    // EQUALS Y HASHCODE
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