package model.sistema;

import model.campus.Edificio;
import model.vehiculos.Vehiculo;
import model.campus.Campus;
import model.pedidos.Pedido;
import Persistencia.PersistenceManager;

import java.util.List;

public class GestorSimulador {
    private List<Vehiculo> flota;
    private Campus campus;
    private List<Pedido> pedidos;
    private GestorEnergia gestorEnergia; // ← NUEVA REFERENCIA

    public GestorSimulador(List<Vehiculo> flota, Campus campus) {
        this.flota = flota;
        this.campus = campus;
        this.pedidos = PersistenceManager.cargarPedidos();
        this.gestorEnergia = new GestorEnergia(flota); // ← INICIALIZAR
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

    // SIMULAR MOVIMIENTO DE TODOS LOS VEHÍCULOS ACTIVOS - CON GESTIÓN ENERGÉTICA INTEGRADA
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
                System.out.println("Ruta: " + pedido.getOrigen().getId() + " → " + pedido.getDestino().getId());
                System.out.println("Batería inicial: " + vehiculo.getEstadoBateria() + "%");

                // CALCULAR DISTANCIA Y CONSUMO
                double distancia = calcularDistancia(vehiculo.getUbicacionActual(), pedido.getDestino());
                double consumoEstimado = gestorEnergia.estimarConsumoRuta(vehiculo, distancia);

                System.out.println("Distancia: " + distancia + " km");
                System.out.println("Consumo estimado: " + consumoEstimado + " kWh");

                // VERIFICAR BATERÍA SUFICIENTE
                if (!gestorEnergia.tieneCargaSuficiente(vehiculo, distancia)) {
                    System.out.println(vehiculo.getId() + " no tiene suficiente batería para esta ruta");
                    System.out.println("Batería actual: " + vehiculo.getEstadoBateria() + "%");
                    vehiculo.cambiarEstado("EN_CARGA");
                    continue;
                }

                // REALIZAR VIAJE CON CONSUMO ENERGÉTICO
                boolean viajeExitoso = vehiculo.realizarViaje(distancia);

                if (viajeExitoso) {
                    // REGISTRAR CONSUMO EN EL GESTOR DE ENERGÍA
                    gestorEnergia.registrarConsumo(vehiculo, consumoEstimado);

                    System.out.println("Viaje exitoso - Consumo real: " + consumoEstimado + " kWh");
                    System.out.println("Batería restante: " + vehiculo.getEstadoBateria() + "%");

                    // VERIFICAR SI NECESITA RECARGA DESPUÉS DEL VIAJE
                    if (vehiculo.getEstadoBateria() < gestorEnergia.getPoliticaRecarga().getNivelRecargaAutomatica()) {
                        System.out.println(vehiculo.getId() + " necesita recarga (" +
                                vehiculo.getEstadoBateria() + "%)");
                        vehiculo.cambiarEstado("EN_CARGA");
                    }

                    // COMPLETAR ENTREGA
                    vehiculo.realizarEntrega(pedido.getDestino());
                    System.out.println("Entrega completada para " + pedido.getId());
                } else {
                    System.out.println("Error en el viaje - no se pudo completar la entrega");
                }
            }
        }

        // GUARDAR CAMBIOS EN PERSISTENCIA
        guardarEstadoActualizado();

        // MOSTRAR REPORTE ENERGÉTICO
        mostrarReporteEnergetico();
    }

    // MÉTODO PARA CALCULAR DISTANCIA ENTRE EDIFICIOS
    private double calcularDistancia(Edificio origen, Edificio destino) {
        if (origen == null || destino == null) return 1.5; // Distancia por defecto

        // En un sistema real, aquí calcularías la distancia real usando coordenadas
        // Por ahora, simulamos distancias basadas en los IDs de los edificios
        try {
            // Simular distancia diferente según los edificios
            int hashOrigen = Math.abs(origen.getId().hashCode()) % 10;
            int hashDestino = Math.abs(destino.getId().hashCode()) % 10;
            double distancia = 0.5 + (Math.abs(hashOrigen - hashDestino) * 0.3);
            return Math.max(0.5, Math.min(3.0, distancia)); // Entre 0.5 y 3.0 km
        } catch (Exception e) {
            return 1.5; // Distancia por defecto
        }
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

    // MOSTRAR REPORTE ENERGÉTICO DESPUÉS DE LA SIMULACIÓN
    private void mostrarReporteEnergetico() {
        System.out.println("\n=== REPORTE ENERGÉTICO DE LA SIMULACIÓN ===");

        GestorEnergia.EstadisticasEnergia stats = gestorEnergia.getEstadisticas();
        System.out.println("Batería promedio de la flota: " + String.format("%.1f", stats.bateriaPromedio) + "%");
        System.out.println("Vehículos con batería baja: " + stats.vehiculosBajaBateria);
        System.out.println("Consumo total acumulado: " + String.format("%.1f", stats.consumoTotal) + " kWh");

        // Mostrar vehículos que necesitan recarga
        List<Vehiculo> necesitaRecarga = gestorEnergia.getVehiculosBateriaBaja();
        if (!necesitaRecarga.isEmpty()) {
            System.out.println("\nVEHÍCULOS QUE NECESITAN RECARGA:");
            for (Vehiculo vehiculo : necesitaRecarga) {
                System.out.println("  - " + vehiculo.getId() + " (" + vehiculo.getTipo() +
                        "): " + String.format("%.1f", vehiculo.getEstadoBateria()) + "%");
            }
        }

        System.out.println("============================================\n");
    }

    // OBTENER ESTADO ACTUAL DE LA FLOTA - ACTUALIZADO CON INFO ENERGÉTICA
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

        // Mostrar información energética
        GestorEnergia.EstadisticasEnergia stats = gestorEnergia.getEstadisticas();
        System.out.println("Batería promedio: " + String.format("%.1f", stats.bateriaPromedio) + "%");
        System.out.println("Consumo total: " + String.format("%.1f", stats.consumoTotal) + " kWh");

        // Mostrar vehículos con pedidos asignados
        List<Vehiculo> conPedidos = flota.stream()
                .filter(v -> v.getPedidoActual() != null)
                .toList();

        System.out.println("\nVEHÍCULOS CON PEDIDOS ASIGNADOS (" + conPedidos.size() + "):");
        for (Vehiculo vehiculo : conPedidos) {
            Pedido pedido = vehiculo.getPedidoActual();
            System.out.println("  " + vehiculo.getId() + " - " + pedido.getId() +
                    " (" + pedido.getOrigen().getId() + " → " + pedido.getDestino().getId() +
                    ") - Estado: " + pedido.getEstado() +
                    " - Batería: " + String.format("%.1f", vehiculo.getEstadoBateria()) + "%");
        }
    }

    // RECARGAR VEHÍCULOS AUTOMÁTICAMENTE SEGÚN POLÍTICA
    public void ejecutarRecargaAutomatica() {
        System.out.println("EJECUTANDO RECARGA AUTOMÁTICA...");
        int vehiculosRecargados = gestorEnergia.recargarVehiculosBateriaBaja();
        System.out.println(vehiculosRecargados + " vehículos recargados automáticamente");

        // Actualizar persistencia
        PersistenceManager.guardarFlota(flota);
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

    // GETTER para GestorEnergia (para que la GUI pueda acceder)
    public GestorEnergia getGestorEnergia() {
        return gestorEnergia;
    }
}