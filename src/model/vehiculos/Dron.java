package model.vehiculos;

import model.campus.Edificio;

public class Dron extends Vehiculo {

    public Dron(String id, Edificio ubicacionInicial) {
        super(id, 8.0, 1.0, 3.0, 0.15, ubicacionInicial);
    }

    @Override
    public String getTipo() {
        return "DRONE";
    }

    @Override
    public double calcularVelocidadPromedio() {
        return 30.0; // km/h
    }

    @Override
    public void realizarEntrega(Edificio destino) {
        registrarEvento("DRONE iniciando entrega aérea");
        registrarEvento("Velocidad promedio: " + calcularVelocidadPromedio() + " km/h");
        registrarEvento("Capacidad de carga: " + getCapacidadCarga() + " kg");
        registrarEvento("Modo: Aéreo - Sobrevolando con la carga");

        // Simular movimiento específico de drone
        simularMovimiento(destino);

        registrarEvento("Entrega aérea de pedido completada con éxito");
    }

    @Override
    public double estimateEnergyCost(double distancia) {
        // Drones consumen más energía en despegue/aterrizaje
        double consumoBase = super.estimateEnergyCost(distancia);
        return consumoBase + 0.1; // +0.1 kWh extra por maniobras
    }
}