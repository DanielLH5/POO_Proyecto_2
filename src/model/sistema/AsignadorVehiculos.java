package model.sistema;

import model.vehiculos.Vehiculo;
import model.pedidos.Pedido;
import model.campus.Campus;
import exceptions.NoVehicleAvailableException;
import java.util.List;

public class AsignadorVehiculos {
    private Campus campus;

    public AsignadorVehiculos(Campus campus) {
        this.campus = campus;
    }

    // METODO PRINCIPAL: Encontrar el mejor vehículo para un pedido
    public Vehiculo encontrarMejorVehiculo(Pedido pedido, List<Vehiculo> flota)
            throws NoVehicleAvailableException {

        Vehiculo mejorVehiculo = null;
        double mejorPuntaje = -1;

        for (Vehiculo vehiculo : flota) {
            if (!tieneSuficienteBateriaParaViaje(vehiculo, pedido)) {
                continue; // Saltar vehículos sin batería suficiente
            }

            double puntaje = calcularPuntaje(vehiculo, pedido);

            if (puntaje > mejorPuntaje) {
                mejorPuntaje = puntaje;
                mejorVehiculo = vehiculo;
            }
        }

        if (mejorVehiculo == null) {
            throw new NoVehicleAvailableException(
                    "No hay vehículos disponibles para el pedido " + pedido.getId(),
                    "capacidad y batería",
                    pedido.getId(),
                    pedido.getPeso()
            );
        }

        return mejorVehiculo;
    }

    // Calcular qué tan adecuado es un vehículo para un pedido
    private double calcularPuntaje(Vehiculo vehiculo, Pedido pedido) {
        // Si no está disponible, puntaje 0
        if (!vehiculo.estaDisponible()) {
            return 0;
        }

        // Si no puede transportar el peso, puntaje 0
        if (!vehiculo.puedeTransportar(pedido.getPeso())) {
            return 0;
        }

        double puntaje = 0;

        // 1. Puntaje por batería (40% del total)
        double porcentajeBateria = vehiculo.getEstadoBateria();
        double puntajeBateria = (porcentajeBateria / 100.0) * 40;
        puntaje += puntajeBateria;

        // 2. Puntaje por cercanía al origen (30% del total)
        double distanciaAlOrigen = campus.getDistancia(
                vehiculo.getUbicacionActual(),
                pedido.getOrigen()
        );
        double puntajeCercania = (1.0 / (distanciaAlOrigen + 1)) * 30;
        puntaje += puntajeCercania;

        // 3. Puntaje por eficiencia energética (20% del total)
        double consumo = vehiculo.getConsumoEnergia();
        double puntajeEficiencia = (1.0 / consumo) * 20;
        puntaje += puntajeEficiencia;

        // 4. Puntaje por tipo de vehículo (10% del total)
        double puntajeTipo = calcularPuntajePorTipo(vehiculo, pedido);
        puntaje += puntajeTipo;

        return puntaje;
    }

    // Puntaje adicional según el tipo de vehículo y pedido
    private double calcularPuntajePorTipo(Vehiculo vehiculo, Pedido pedido) {
        String tipoVehiculo = vehiculo.getTipo();
        double peso = pedido.getPeso();

        switch (tipoVehiculo) {
            case "DRON":
                // Drones son mejores para paquetes ligeros y distancias medias
                return (peso <= 2.0) ? 10 : 5;

            case "ROVER":
                // Rovers son mejores para paquetes pesados
                return (peso >= 5.0) ? 10 : 5;

            case "E-BIKE":
                // E-Bikes son balanceadas
                return 7;

            default:
                return 5;
        }
    }

    // Metodo simplificado (alternativa más simple)
    public Vehiculo encontrarVehiculoDisponible(Pedido pedido, List<Vehiculo> flota)
            throws NoVehicleAvailableException {

        for (Vehiculo vehiculo : flota) {
            if (esAdecuadoParaPedido(vehiculo, pedido)) {
                return vehiculo;
            }
        }

        throw new NoVehicleAvailableException(pedido.getId());
    }

    // Verificar si un vehículo es adecuado para un pedido
    private boolean esAdecuadoParaPedido(Vehiculo vehiculo, Pedido pedido) {
        return vehiculo.estaDisponible() &&
                vehiculo.puedeTransportar(pedido.getPeso()) &&
                tieneSuficienteBateriaParaViaje(vehiculo, pedido);
    }

    // Verificar si tiene batería para el viaje completo
    private boolean tieneSuficienteBateriaParaViaje(Vehiculo vehiculo, Pedido pedido) {
        double distanciaOrigen = campus.getDistancia(
                vehiculo.getUbicacionActual(),
                pedido.getOrigen()
        );
        double distanciaEntrega = campus.getDistancia(
                pedido.getOrigen(),
                pedido.getDestino()
        );
        double distanciaTotal = distanciaOrigen + distanciaEntrega;

        double energiaRequerida = vehiculo.estimateEnergyCost(distanciaTotal);
        return vehiculo.getBateria().getNivelActual() >= energiaRequerida;
    }
}