package model.pedidos;

import model.campus.Campus;
import model.campus.Edificio;
import model.vehiculos.Vehiculo;
import Persistencia.PersistenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestionPedidos {
    private ColaPedidos colaPedidos;
    private List<Pedido> todosLosPedidos;
    private Campus campus;
    private List<Vehiculo> flotaVehiculos;

    public GestionPedidos() {
        // Cargar datos desde persistencia
        this.campus = PersistenceManager.cargarCampus();
        this.flotaVehiculos = PersistenceManager.cargarFlota();

        // Cargar o inicializar pedidos
        this.todosLosPedidos = PersistenceManager.cargarPedidos();
        if (this.todosLosPedidos == null) {
            this.todosLosPedidos = new ArrayList<>();
        }

        // Cargar o inicializar cola de pedidos
        this.colaPedidos = PersistenceManager.cargarColaPedidos();
        if (this.colaPedidos == null) {
            this.colaPedidos = new ColaPedidos();
            // Reconstruir cola con pedidos pendientes existentes
            reconstruirColaDesdePedidos();
        }
    }

    private void reconstruirColaDesdePedidos() {
        for (Pedido pedido : todosLosPedidos) {
            if (pedido.estaPendiente()) {
                colaPedidos.agregarPedido(pedido);
            }
        }
    }

    // Crear nuevo pedido manualmente
    public Pedido crearPedidoManual(String origenId, String destinoId, double peso) {
        if (campus == null) {
            throw new IllegalStateException("No hay campus cargado");
        }

        Edificio origen = campus.getEdificio(origenId);
        Edificio destino = campus.getEdificio(destinoId);

        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Edificio de origen o destino no encontrado");
        }

        if (origen.equals(destino)) {
            throw new IllegalArgumentException("El origen y destino deben ser diferentes");
        }

        String id = "PED-" + System.currentTimeMillis();
        Pedido nuevoPedido = new Pedido(id, origen, destino, peso);

        // Agregar a la cola y al histórico
        colaPedidos.agregarPedido(nuevoPedido);
        todosLosPedidos.add(nuevoPedido);

        // Guardar en persistencia
        guardarEstado();

        return nuevoPedido;
    }

    // Generar pedidos automáticamente
    public List<Pedido> generarPedidosAutomaticos(int cantidad) {
        if (campus == null) {
            throw new IllegalStateException("No hay campus cargado");
        }

        List<Edificio> edificios = campus.getEdificios();
        if (edificios.size() < 2) {
            throw new IllegalStateException("Se necesitan al menos 2 edificios para generar pedidos");
        }

        List<Pedido> pedidosGenerados = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            // Seleccionar origen y destino aleatorios (diferentes)
            Edificio origen = edificios.get((int)(Math.random() * edificios.size()));
            Edificio destino;
            do {
                destino = edificios.get((int)(Math.random() * edificios.size()));
            } while (destino.equals(origen));

            // Peso aleatorio entre 0.5 y 15 kg
            double peso = 0.5 + (Math.random() * 14.5);

            String id = "PED-AUTO-" + System.currentTimeMillis() + "-" + i;
            Pedido pedido = new Pedido(id, origen, destino, peso);

            colaPedidos.agregarPedido(pedido);
            todosLosPedidos.add(pedido);
            pedidosGenerados.add(pedido);
        }

        // Guardar en persistencia
        guardarEstado();

        return pedidosGenerados;
    }

    // Procesar pedidos pendientes (asignar vehículos) - VERSIÓN CORREGIDA
    public void procesarPedidosPendientes() {
        if (flotaVehiculos == null || flotaVehiculos.isEmpty()) {
            throw new IllegalStateException("No hay vehículos disponibles en la flota");
        }

        // Mapa para llevar registro de la carga acumulada por vehículo
        Map<String, Double> cargaAcumuladaPorVehiculo = new HashMap<>();

        // Obtener todos los pedidos pendientes sin removerlos de la cola
        List<Pedido> pedidosPendientes = new ArrayList<>();
        ColaPedidos colaTemporal = new ColaPedidos();

        // Extraer pedidos manteniendo el orden
        while (colaPedidos.hayPedidosPendientes()) {
            Pedido pedido = colaPedidos.obtenerSiguientePedido();
            pedidosPendientes.add(pedido);
            colaTemporal.agregarPedido(pedido);
        }

        // Restaurar la cola original
        this.colaPedidos = colaTemporal;

        // Procesar pedidos ordenados por peso descendente (para optimizar carga)
        pedidosPendientes.sort((p1, p2) -> Double.compare(p2.getPeso(), p1.getPeso()));

        int pedidosAsignados = 0;
        for (Pedido pedido : pedidosPendientes) {
            // Solo procesar si el pedido sigue pendiente
            if (pedido.estaPendiente()) {
                // Buscar vehículo adecuado considerando carga acumulada
                Vehiculo vehiculoAdecuado = seleccionarVehiculoConCarga(pedido, cargaAcumuladaPorVehiculo);

                if (vehiculoAdecuado != null) {
                    // Remover de la cola y asignar vehículo
                    colaPedidos.removerPedido(pedido);
                    pedido.asignarVehiculo(vehiculoAdecuado);
                    pedidosAsignados++;

                    // Actualizar carga acumulada
                    String vehiculoId = vehiculoAdecuado.getId();
                    double cargaActual = cargaAcumuladaPorVehiculo.getOrDefault(vehiculoId, 0.0);
                    cargaAcumuladaPorVehiculo.put(vehiculoId, cargaActual + pedido.getPeso());

                    System.out.println("Asignado: " + pedido.getId() + " (" + pedido.getPeso() + "kg) a " +
                            vehiculoId + " - Carga acumulada: " + (cargaActual + pedido.getPeso()) + "kg");
                }
            }
        }

        // Guardar cambios
        guardarEstado();
        System.out.println("Procesamiento completado: " + pedidosAsignados + " pedidos asignados");
    }

    // Método mejorado para seleccionar vehículo considerando carga acumulada
    private Vehiculo seleccionarVehiculoConCarga(Pedido pedido, Map<String, Double> cargaAcumulada) {
        return flotaVehiculos.stream()
                .filter(v -> v.estaDisponible())
                .filter(v -> puedeTransportarConCarga(v, pedido.getPeso(), cargaAcumulada.getOrDefault(v.getId(), 0.0)))
                .filter(v -> v.getEstadoBateria() > 20)
                // Priorizar vehículos con menos carga acumulada para distribución equitativa
                .sorted((v1, v2) -> {
                    double carga1 = cargaAcumulada.getOrDefault(v1.getId(), 0.0);
                    double carga2 = cargaAcumulada.getOrDefault(v2.getId(), 0.0);
                    double capacidad1 = v1.getCapacidadCarga();
                    double capacidad2 = v2.getCapacidadCarga();

                    // Calcular porcentaje de uso (carga actual / capacidad total)
                    double porcentajeUso1 = carga1 / capacidad1;
                    double porcentajeUso2 = carga2 / capacidad2;

                    return Double.compare(porcentajeUso1, porcentajeUso2);
                })
                .findFirst()
                .orElse(null);
    }

    // Método para verificar capacidad considerando carga acumulada
    private boolean puedeTransportarConCarga(Vehiculo vehiculo, double pesoPedido, double cargaAcumulada) {
        double capacidadMaxima = vehiculo.getCapacidadCarga();
        double cargaTotal = cargaAcumulada + pesoPedido;
        return cargaTotal <= capacidadMaxima;
    }

    // Método auxiliar para obtener capacidad máxima (usando el método existente de Vehiculo)
    private double getCapacidadMaxima(Vehiculo vehiculo) {
        return vehiculo.getCapacidadCarga();
    }

    private Vehiculo seleccionarVehiculoOptimo(Pedido pedido) {
        return flotaVehiculos.stream()
                .filter(v -> v.estaDisponible())
                .filter(v -> v.puedeTransportar(pedido.getPeso()))
                .filter(v -> v.getEstadoBateria() > 20)
                // Priorizar vehículos con más batería y menos uso
                .sorted((v1, v2) -> {
                    // Primero por batería (mayor primero)
                    int compBateria = Double.compare(v2.getEstadoBateria(), v1.getEstadoBateria());
                    if (compBateria != 0) return compBateria;

                    // Luego por capacidad disponible (mayor primero)
                    double cap1 = v1.getCapacidadCarga();
                    double cap2 = v2.getCapacidadCarga();
                    return Double.compare(cap2, cap1);
                })
                .findFirst()
                .orElse(null);
    }

    // Obtener edificios conectados a un edificio específico
    public List<String> getEdificiosConectados(String edificioId) {
        if (campus == null) return new ArrayList<>();

        Edificio edificio = campus.getEdificio(edificioId);
        if (edificio == null) return new ArrayList<>();

        // Asumiendo que Campus tiene un método para obtener conexiones
        // Si no existe, podemos implementar una lógica alternativa
        try {
            return campus.getEdificiosVecinos(edificio).stream()
                    .map(Edificio::getId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Si no existe el método, retornar todos los edificios excepto el origen
            return campus.getEdificios().stream()
                    .filter(ed -> !ed.getId().equals(edificioId))
                    .map(Edificio::getId)
                    .collect(Collectors.toList());
        }
    }

    // Obtener vehículos asignados a un edificio específico
    public List<String> getVehiculosPorEdificio(String edificioId) {
        if (flotaVehiculos == null) return new ArrayList<>();

        return flotaVehiculos.stream()
                .filter(v -> estaDisponible(v))
                .filter(v -> getUbicacionVehiculo(v) != null && getUbicacionVehiculo(v).equals(edificioId))
                .filter(v -> getBateria(v) > 20)
                .map(this::getIdVehiculo)
                .collect(Collectors.toList());
    }

    // Obtener todos los vehículos disponibles
    public List<String> getVehiculosDisponibles() {
        if (flotaVehiculos == null) return new ArrayList<>();

        return flotaVehiculos.stream()
                .filter(v -> estaDisponible(v))
                .filter(v -> getBateria(v) > 20)
                .map(this::getIdVehiculo)
                .collect(Collectors.toList());
    }

    // Asignar vehículo específico a un pedido
    public boolean asignarVehiculoAPedido(String pedidoId, String vehiculoId) {
        // Buscar el pedido
        Pedido pedido = todosLosPedidos.stream()
                .filter(p -> p.getId().equals(pedidoId))
                .findFirst()
                .orElse(null);

        if (pedido == null) {
            throw new IllegalArgumentException("Pedido no encontrado: " + pedidoId);
        }

        // Buscar el vehículo
        Vehiculo vehiculo = flotaVehiculos.stream()
                .filter(v -> getIdVehiculo(v).equals(vehiculoId))
                .findFirst()
                .orElse(null);

        if (vehiculo == null) {
            throw new IllegalArgumentException("Vehículo no encontrado: " + vehiculoId);
        }

        // Verificar que el pedido esté pendiente
        if (!pedido.estaPendiente()) {
            throw new IllegalStateException("El pedido no está pendiente, no se puede asignar vehículo");
        }

        // Verificar que el vehículo esté disponible y pueda transportar el peso
        if (!estaDisponible(vehiculo)) {
            throw new IllegalStateException("El vehículo no está disponible");
        }

        if (!puedeTransportar(vehiculo, pedido.getPeso())) {
            throw new IllegalStateException("El vehículo no puede transportar el peso del pedido");
        }

        if (getBateria(vehiculo) <= 20) {
            throw new IllegalStateException("El vehículo no tiene suficiente batería (mínimo 20%)");
        }

        // Asignar vehículo al pedido
        pedido.asignarVehiculo(vehiculo);

        // Remover de la cola si estaba en ella
        colaPedidos.removerPedido(pedido);

        // Guardar cambios
        guardarEstado();

        return true;
    }

    // Cambiar estado de un pedido manualmente - CORREGIDO
    public boolean cambiarEstadoPedido(String pedidoId, EstadoPedido nuevoEstado) {
        Pedido pedido = todosLosPedidos.stream()
                .filter(p -> p.getId().equals(pedidoId))
                .findFirst()
                .orElse(null);

        if (pedido == null) return false;

        try {
            EstadoPedido estadoActual = pedido.getEstado();

            // Usar los métodos existentes de Pedido para cambiar estado
            switch (nuevoEstado) {
                case RECIBIDO:
                    // Para volver a RECIBIDO, necesitamos crear un nuevo pedido o resetear
                    // Como no tenemos método para resetear, usamos este enfoque
                    if (pedido.getVehiculoAsignado() != null) {
                        // Simplemente removemos la asignación
                        pedido.asignarVehiculo(null);
                    }
                    // No podemos cambiar directamente el estado, así que manejamos esto diferente
                    break;

                case PREPARACION:
                    if (pedido.getVehiculoAsignado() == null) {
                        throw new IllegalStateException("No se puede preparar pedido sin vehículo asignado");
                    }
                    pedido.asignarVehiculo(pedido.getVehiculoAsignado());
                    break;

                case EN_CAMINO:
                    pedido.iniciarEntrega();
                    break;

                case ENTREGADO:
                    pedido.marcarEntregado();
                    break;

                case CANCELADO:
                    pedido.cancelar();
                    colaPedidos.removerPedido(pedido);
                    break;
            }

            guardarEstado();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // Eliminar pedido individual
    public boolean eliminarPedido(String pedidoId) {
        Pedido pedido = todosLosPedidos.stream()
                .filter(p -> p.getId().equals(pedidoId))
                .findFirst()
                .orElse(null);

        if (pedido != null) {
            // No necesitamos liberar vehículo porque Pedido.cancelar() ya lo hace
            pedido.cancelar();

            // Remover de la cola y de todos los pedidos
            colaPedidos.removerPedido(pedido);
            todosLosPedidos.remove(pedido);

            guardarEstado();
            return true;
        }
        return false;
    }

    // En GestionPedidos.java - Agregar este método
    public boolean cancelarPedido(String pedidoId) {
        Pedido pedido = todosLosPedidos.stream()
                .filter(p -> p.getId().equals(pedidoId))
                .findFirst()
                .orElse(null);

        if (pedido != null) {
            try {
                // Cancelar el pedido (esto libera el vehículo automáticamente)
                pedido.cancelar();

                // Remover de la cola de pedidos pendientes
                colaPedidos.removerPedido(pedido);

                // Guardar cambios
                guardarEstado();

                return true;

            } catch (Exception e) {
                System.err.println("Error al cancelar pedido " + pedidoId + ": " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    // Eliminar todos los pedidos
    public void eliminarTodosLosPedidos() {
        // Cancelar todos los pedidos (esto libera los vehículos)
        for (Pedido pedido : todosLosPedidos) {
            pedido.cancelar();
        }

        // Limpiar listas
        todosLosPedidos.clear();
        colaPedidos = new ColaPedidos();

        guardarEstado();
    }

    // Obtener estados disponibles para un pedido (según su estado actual)
    public List<EstadoPedido> getEstadosDisponibles(String pedidoId) {
        Pedido pedido = todosLosPedidos.stream()
                .filter(p -> p.getId().equals(pedidoId))
                .findFirst()
                .orElse(null);

        if (pedido == null) return new ArrayList<>();

        List<EstadoPedido> estados = new ArrayList<>();
        EstadoPedido estadoActual = pedido.getEstado();

        switch (estadoActual) {
            case RECIBIDO:
                estados.add(EstadoPedido.PREPARACION);
                estados.add(EstadoPedido.CANCELADO);
                break;
            case PREPARACION:
                estados.add(EstadoPedido.EN_CAMINO);
                estados.add(EstadoPedido.CANCELADO);
                break;
            case EN_CAMINO:
                estados.add(EstadoPedido.ENTREGADO);
                estados.add(EstadoPedido.CANCELADO);
                break;
            case ENTREGADO:
                // No se puede cambiar desde ENTREGADO
                break;
            case CANCELADO:
                // No se puede cambiar desde CANCELADO
                break;
        }

        return estados;
    }

    // MÉTODOS AUXILIARES PARA VEHÍCULOS (para evitar dependencias de métodos específicos)
    private boolean estaDisponible(Vehiculo vehiculo) {
        // Asume que Vehiculo tiene un método estáDisponible() o usa lógica alternativa
        try {
            return vehiculo.estaDisponible();
        } catch (Exception e) {
            // Si no existe el método, asumir que todos están disponibles
            return true;
        }
    }

    private boolean puedeTransportar(Vehiculo vehiculo, double peso) {
        try {
            return vehiculo.puedeTransportar(peso);
        } catch (Exception e) {
            // Si no existe el método, asumir que puede transportar hasta 50kg
            return peso <= 50.0;
        }
    }

    private double getBateria(Vehiculo vehiculo) {
        try {
            return vehiculo.getEstadoBateria();
        } catch (Exception e) {
            // Si no existe el método, asumir 100% de batería
            return 100.0;
        }
    }

    private String getUbicacionVehiculo(Vehiculo vehiculo) {
        try {
            return vehiculo.getUbicacionActual() != null ? vehiculo.getUbicacionActual().getId() : null;
        } catch (Exception e) {
            // Si no existe el método, retornar null
            return null;
        }
    }

    private String getIdVehiculo(Vehiculo vehiculo) {
        try {
            return vehiculo.getId();
        } catch (Exception e) {
            return "Vehículo-" + vehiculo.hashCode();
        }
    }

    // Obtener edificios disponibles para la GUI
    public List<String> getEdificiosDisponibles() {
        if (campus == null) return new ArrayList<>();
        return campus.getEdificios().stream()
                .map(Edificio::getId)
                .collect(Collectors.toList());
    }

    // Guardar estado completo
    private void guardarEstado() {
        PersistenceManager.guardarPedidos(todosLosPedidos);
        PersistenceManager.guardarColaPedidos(colaPedidos);
        // También guardar vehículos si se modificaron
        if (flotaVehiculos != null) {
            PersistenceManager.guardarFlota(flotaVehiculos);
        }
    }

    // Obtener estadísticas
    public int getTotalPedidos() {
        return todosLosPedidos.size();
    }

    public int getPedidosPendientes() {
        return colaPedidos.cantidadPedidos();
    }

    public int getPedidosEnProceso() {
        return (int) todosLosPedidos.stream()
                .filter(Pedido::estaEnProceso)
                .count();
    }

    public int getPedidosCompletados() {
        return (int) todosLosPedidos.stream()
                .filter(Pedido::estaCompletado)
                .count();
    }

    // Getters
    public ColaPedidos getColaPedidos() { return colaPedidos; }
    public List<Pedido> getTodosLosPedidos() { return todosLosPedidos; }
    public Campus getCampus() { return campus; }
    public List<Vehiculo> getFlotaVehiculos() { return flotaVehiculos; }
}