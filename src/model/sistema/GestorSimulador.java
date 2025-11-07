package model.sistema;

import model.vehiculos.Vehiculo;
import model.campus.Campus;
import model.pedidos.Pedido;
import Persistencia.PersistenceManager;

import java.util.List;

public class GestorSimulador {
    private List<Vehiculo> flota;
    private Campus campus;
    private List<Pedido> pedidos;

    public GestorSimulador(List<Vehiculo> flota, Campus campus) {
        this.flota = flota;
        this.campus = campus;
        this.pedidos = PersistenceManager.cargarPedidos();
        sincronizarPedidosConVehiculos();
    }

    // SINCRONIZAR PEDIDOS ASIGNADOS CON VEHÍCULOS - CORREGIDO
    private void sincronizarPedidosConVehiculos() {
        if (pedidos == null) return;

        System.out.println("Sincronizando " + pedidos.size() + " pedidos con vehículos...");

        for (Pedido pedido : pedidos) {
            // Solo sincronizar pedidos que NO estén completados
            if (!pedido.estaCompletado() && pedido.getVehiculoAsignado() != null) {
                String vehiculoId = pedido.getVehiculoAsignado().getId();

                // Buscar el vehículo en la flota
                Vehiculo vehiculo = flota.stream()
                        .filter(v -> v.getId().equals(vehiculoId))
                        .findFirst()
                        .orElse(null);

                if (vehiculo != null) {
                    // Solo asignar si el vehículo no tiene ya este pedido específico
                    if (vehiculo.getPedidoActual() == null ||
                            !pedido.getId().equals(vehiculo.getPedidoActual().getId())) {

                        vehiculo.asignarPedido(pedido);
                        System.out.println("Pedido " + pedido.getId() + " asignado a " + vehiculoId);
                    }
                } else {
                    System.out.println("Vehículo " + vehiculoId + " no encontrado en flota");
                }
            }
        }

        System.out.println("Sincronización completada");
    }

    // SIMULAR MOVIMIENTO DE TODOS LOS VEHÍCULOS ACTIVOS - CORREGIDO
    public void simularMovimientoFlota() {
        // Recargar pedidos actualizados antes de simular
        this.pedidos = PersistenceManager.cargarPedidos();
        sincronizarPedidosConVehiculos();

        List<Vehiculo> vehiculosActivos = flota.stream()
                .filter(v -> v.getPedidoActual() != null &&
                        ("PREPARACION".equals(v.getEstado()) || "EN_ENTREGA".equals(v.getEstado())))
                .toList();

        System.out.println("SIMULANDO MOVIMIENTO DE " + vehiculosActivos.size() + " VEHÍCULOS ACTIVOS");

        if (vehiculosActivos.isEmpty()) {
            System.out.println("No hay vehículos con pedidos pendientes para simular");
            return;
        }

        for (Vehiculo vehiculo : vehiculosActivos) {
            Pedido pedido = vehiculo.getPedidoActual();
            if (pedido != null && !pedido.estaCompletado()) {
                System.out.println("\n--- " + vehiculo.getId() + " en movimiento ---");
                System.out.println("Pedido: " + pedido.getId());
                System.out.println("Ruta: " + pedido.getOrigen().getId() + " -- " + pedido.getDestino().getId());

                // Usar el método polimórfico de entrega
                vehiculo.realizarEntrega(pedido.getDestino());

                System.out.println("Movimiento completado para " + vehiculo.getId());
            }
        }

        // GUARDAR CAMBIOS EN PERSISTENCIA
        guardarEstadoActualizado();
    }

    // GUARDAR ESTADO ACTUALIZADO EN PERSISTENCIA
    private void guardarEstadoActualizado() {
        // Guardar vehículos con sus estados actualizados
        PersistenceManager.guardarFlota(flota);

        // Actualizar pedidos con el estado actual de los vehículos
        if (pedidos != null) {
            for (Pedido pedido : pedidos) {
                if (pedido.getVehiculoAsignado() != null) {
                    // Buscar el vehículo actualizado en la flota
                    Vehiculo vehiculoActualizado = flota.stream()
                            .filter(v -> v.getId().equals(pedido.getVehiculoAsignado().getId()))
                            .findFirst()
                            .orElse(null);

                    if (vehiculoActualizado != null) {
                        // Si el vehículo completó el pedido, actualizar el estado del pedido
                        if (vehiculoActualizado.getPedidoActual() == null &&
                                pedido.getEstado() != model.pedidos.EstadoPedido.ENTREGADO) {
                            // El vehículo ya no tiene el pedido, significa que se completó
                            pedido.marcarEntregado();
                        }
                    }
                }
            }
            // Guardar pedidos actualizados
            PersistenceManager.guardarPedidos(pedidos);
        }

        System.out.println("Estado guardado en persistencia");
    }

    // OBTENER ESTADO ACTUAL DE LA FLOTA
    public void mostrarEstadoFlota() {
        System.out.println("ESTADO ACTUAL DE LA FLOTA:");
        System.out.println("Total vehículos: " + flota.size());

        long disponibles = flota.stream().filter(Vehiculo::estaDisponible).count();
        long enPreparacion = flota.stream().filter(v -> "PREPARACION".equals(v.getEstado())).count();
        long enEntrega = flota.stream().filter(v -> "EN_ENTREGA".equals(v.getEstado())).count();
        long enCarga = flota.stream().filter(v -> "EN_CARGA".equals(v.getEstado())).count();

        System.out.println("Disponibles: " + disponibles);
        System.out.println("En preparación: " + enPreparacion);
        System.out.println("En entrega: " + enEntrega);
        System.out.println("En carga: " + enCarga);
        System.out.println("Fuera de servicio: " + (flota.size() - disponibles - enPreparacion - enEntrega - enCarga));

        // Mostrar vehículos con pedidos asignados
        List<Vehiculo> conPedidos = flota.stream()
                .filter(v -> v.getPedidoActual() != null)
                .toList();

        System.out.println("\nVEHÍCULOS CON PEDIDOS ASIGNADOS (" + conPedidos.size() + "):");
        for (Vehiculo vehiculo : conPedidos) {
            Pedido pedido = vehiculo.getPedidoActual();
            System.out.println("  " + vehiculo.getId() + " - " + pedido.getId() +
                    " (" + pedido.getOrigen().getId() + " → " + pedido.getDestino().getId() +
                    ") - Estado: " + pedido.getEstado());
        }
    }

    // FORZAR SINCRONIZACIÓN MANUAL - CORREGIDO
    public void forzarSincronizacion() {
        this.pedidos = PersistenceManager.cargarPedidos();
        sincronizarPedidosConVehiculos();
        System.out.println("Sincronización forzada completada");
    }

    // ACTUALIZAR DESDE PERSISTENCIA MANTENIENDO CAMBIOS LOCALES
    public void actualizarDesdePersistencia() {
        System.out.println("Actualizando desde persistencia...");

        // Cargar flota actualizada
        List<Vehiculo> flotaActualizada = PersistenceManager.cargarFlota();
        if (flotaActualizada != null) {
            // Mantener los pedidos actuales de los vehículos que están en memoria
            for (Vehiculo vehiculoLocal : this.flota) {
                if (vehiculoLocal.getPedidoActual() != null) {
                    // Buscar el vehículo equivalente en la flota actualizada
                    Vehiculo vehiculoActualizado = flotaActualizada.stream()
                            .filter(v -> v.getId().equals(vehiculoLocal.getId()))
                            .findFirst()
                            .orElse(null);

                    if (vehiculoActualizado != null) {
                        // Preservar el pedido actual del vehículo local
                        vehiculoActualizado.asignarPedido(vehiculoLocal.getPedidoActual());
                    }
                }
            }
            this.flota = flotaActualizada;
        }

        // Cargar pedidos actualizados
        this.pedidos = PersistenceManager.cargarPedidos();

        System.out.println("Actualización desde persistencia completada");
    }
}