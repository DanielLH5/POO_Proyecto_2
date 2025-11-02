package model.sistema;

import model.vehiculos.*;
import model.pedidos.*;
import model.campus.*;
import exceptions.*;
import java.util.*;

public class SistemaEntrega {
    // COMPONENTES DEL SISTEMA
    private Campus campus;
    private List<Vehiculo> flota;
    private ColaPedidos colaPedidos;
    private AsignadorVehiculos asignador;
    private GestorEnergia gestorEnergia;
    private SimuladorEntrega simulador;

    // ESTADÍSTICAS
    private int pedidosCompletados;
    private int pedidosCancelados;
    private double energiaTotalConsumida;

    public SistemaEntrega() {
        this.campus = new Campus();
        this.flota = new ArrayList<>();
        this.colaPedidos = new ColaPedidos();
        this.asignador = new AsignadorVehiculos(campus);
        this.gestorEnergia = new GestorEnergia(campus);
        this.simulador = new SimuladorEntrega(campus, gestorEnergia);

        this.pedidosCompletados = 0;
        this.pedidosCancelados = 0;
        this.energiaTotalConsumida = 0;

        inicializarFlota();
    }

    // INICIALIZAR FLOTA CON VEHÍCULOS
    private void inicializarFlota() {
        Edificio centroPrincipal = campus.getCentroPrincipal();

        // Crear algunos vehículos de ejemplo
        flota.add(new Dron("D001", centroPrincipal));
        flota.add(new Dron("D002", centroPrincipal));
        flota.add(new Rover("R001", centroPrincipal));
        flota.add(new Rover("R002", centroPrincipal));
        flota.add(new EBike("B001", centroPrincipal));
        flota.add(new EBike("B002", centroPrincipal));
    }

    // MÉTODOS PARA GESTIÓN DE PEDIDOS
    public void crearPedido(String idPedido, String idOrigen, String idDestino, double peso) {
        try {
            Edificio origen = campus.getEdificio(idOrigen);
            Edificio destino = campus.getEdificio(idDestino);

            Pedido pedido = new Pedido(idPedido, origen, destino, peso);
            colaPedidos.agregarPedido(pedido);

            System.out.println("Pedido " + idPedido + " creado: " +
                    origen.getId() + " → " + destino.getId() + " (" + peso + "kg)");

        } catch (Exception e) {
            System.out.println("Error creando pedido: " + e.getMessage());
        }
    }

    // PROCESAR SIGUIENTE PEDIDO EN COLA
    public void procesarSiguientePedido() {
        if (!colaPedidos.hayPedidosPendientes()) {
            System.out.println("No hay pedidos pendientes");
            return;
        }

        Pedido pedido = colaPedidos.obtenerSiguientePedido();
        System.out.println("Procesando pedido: " + pedido.getId());

        try {
            // Buscar vehículo adecuado
            Vehiculo vehiculo = asignador.encontrarMejorVehiculo(pedido, flota);

            // Asignar vehículo al pedido
            pedido.asignarVehiculo(vehiculo);
            vehiculo.cambiarEstado("EN_ENTREGA");

            System.out.println("Pedido " + pedido.getId() + " asignado a " +
                    vehiculo.getTipo() + " " + vehiculo.getId());

            // Iniciar simulación de entrega
            simulador.simularEntrega(pedido, vehiculo);

            // Actualizar estadísticas
            pedidosCompletados++;

        } catch (NoVehicleAvailableException e) {
            System.out.println(e.getMessage());
            pedido.cancelar();
            pedidosCancelados++;
        }
    }

    // GESTIÓN DE ENERGÍA DE LA FLOTA
    public void gestionarEnergiaFlota() {
        System.out.println("Gestionando energía de la flota...");

        for (Vehiculo vehiculo : flota) {
            if (gestorEnergia.necesitaCarga(vehiculo)) {
                Edificio centroCarga = gestorEnergia.getCentroCargaCercano(vehiculo.getUbicacionActual());

                System.out.println("⚡ " + vehiculo.getId() + " necesita carga. Enviando a " +
                        centroCarga.getId());

                // Mover a centro de carga y cargar
                vehiculo.actualizarUbicacion(centroCarga);
                gestorEnergia.cargarCompletamente(vehiculo);
                vehiculo.cambiarEstado("DISPONIBLE");

                System.out.println(vehiculo.getId() + " cargado al 100%");
            }
        }
    }

    // MÉTODOS DE CONSULTA
    public List<Vehiculo> getFlota() {
        return new ArrayList<>(flota);
    }

    public List<Pedido> getPedidosPendientes() {
        return colaPedidos.getTodosPedidos();
    }

    public Campus getCampus() {
        return campus;
    }

    // ESTADÍSTICAS DEL SISTEMA
    public void mostrarEstadisticas() {
        System.out.println("\nESTADÍSTICAS DEL SISTEMA");
        System.out.println("Pedidos completados: " + pedidosCompletados);
        System.out.println("Pedidos cancelados: " + pedidosCancelados);
        System.out.println("Vehículos en flota: " + flota.size());
        System.out.println("Pedidos en cola: " + colaPedidos.cantidadPedidos());

        // Estadísticas por tipo de vehículo
        Map<String, Integer> vehiculosPorTipo = new HashMap<>();
        for (Vehiculo v : flota) {
            String tipo = v.getTipo();
            vehiculosPorTipo.put(tipo, vehiculosPorTipo.getOrDefault(tipo, 0) + 1);
        }

        System.out.println("Distribución de flota: " + vehiculosPorTipo);
        System.out.println("Energía total consumida: " + String.format("%.2f", energiaTotalConsumida) + " kWh");
    }

    // ESTADO ACTUAL DEL SISTEMA
    public void mostrarEstadoActual() {
        System.out.println("\nESTADO ACTUAL DEL SISTEMA");

        System.out.println("--- FLOTA ---");
        for (Vehiculo vehiculo : flota) {
            System.out.println("  " + vehiculo);
        }

        System.out.println("--- PEDIDOS PENDIENTES ---");
        for (Pedido pedido : colaPedidos.getTodosPedidos()) {
            System.out.println("  " + pedido);
        }

        System.out.println("--- CAMPUS ---");
        System.out.println("  " + campus.getEdificios().size() + " edificios");
        System.out.println("  " + campus.getRutas().size() + " rutas");
    }

    // SIMULACIÓN COMPLETA DEL SISTEMA
    public void ejecutarCicloCompleto() {
        System.out.println("\nEJECUTANDO CICLO COMPLETO");

        // 1. Gestionar energía
        gestionarEnergiaFlota();

        // 2. Procesar pedidos pendientes
        int pedidosProcesados = 0;
        while (colaPedidos.hayPedidosPendientes() && pedidosProcesados < 3) {
            procesarSiguientePedido();
            pedidosProcesados++;
        }

        // 3. Mostrar estado
        mostrarEstadoActual();
    }

    // METODO MAIN PARA PRUEBAS
    public static void main(String[] args) {
        SistemaEntrega sistema = new SistemaEntrega();

        // Crear algunos pedidos de ejemplo
        sistema.crearPedido("P001", "A", "B", 1.5);
        sistema.crearPedido("P002", "C", "D", 8.0);
        sistema.crearPedido("P003", "B", "A", 0.5);

        // Ejecutar ciclos de simulación
        for (int i = 1; i <= 3; i++) {
            System.out.println("\n=== CICLO " + i + " ===");
            sistema.ejecutarCicloCompleto();
        }

        // Mostrar estadísticas finales
        sistema.mostrarEstadisticas();
    }
}