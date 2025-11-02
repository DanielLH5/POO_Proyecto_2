package model.sistema;

import model.vehiculos.Vehiculo;
import model.pedidos.Pedido;
import model.campus.Campus;
import model.campus.Edificio;

public class SimuladorEntrega {
    private Campus campus;
    private GestorEnergia gestorEnergia;

    public SimuladorEntrega(Campus campus, GestorEnergia gestorEnergia) {
        this.campus = campus;
        this.gestorEnergia = gestorEnergia;
    }

    // METODO PRINCIPAL SIMPLIFICADO
    public void simularEntrega(Pedido pedido, Vehiculo vehiculo) {
        System.out.println("Simulando entrega: " + pedido.getId());

        // 1. Verificar si puede hacer el viaje
        if (!gestorEnergia.puedeHacerViaje(vehiculo, pedido.getOrigen(), pedido.getDestino())) {
            System.out.println("No hay suficiente batería para la entrega");
            return;
        }

        // 2. Mover al origen (si es necesario)
        if (!vehiculo.getUbicacionActual().equals(pedido.getOrigen())) {
            System.out.println("Moviendo al origen: " + pedido.getOrigen().getId());
            simularMovimientoSimple(vehiculo, pedido.getOrigen());
        }

        // 3. Mover al destino
        System.out.println("Moviendo al destino: " + pedido.getDestino().getId());
        simularMovimientoSimple(vehiculo, pedido.getDestino());

        // 4. Completar entrega
        System.out.println("Entrega completada en: " + pedido.getDestino().getId());
        vehiculo.cambiarEstado("DISPONIBLE");
    }

    // MOVIMIENTO SIMPLIFICADO
    private void simularMovimientoSimple(Vehiculo vehiculo, Edificio destino) {
        Edificio origen = vehiculo.getUbicacionActual();

        // Consumir energía del tramo
        gestorEnergia.consumirEnTramo(vehiculo, origen, destino);

        // Actualizar ubicación
        vehiculo.actualizarUbicacion(destino);

        System.out.println(origen.getId() + " → " + destino.getId() +
                " | Batería: " + Math.round(vehiculo.getEstadoBateria()) + "%");
    }
}