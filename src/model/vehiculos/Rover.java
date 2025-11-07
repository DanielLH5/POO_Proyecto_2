package model.vehiculos;

import model.campus.Edificio;

public class Rover extends Vehiculo {

    public Rover(String id, Edificio ubicacionInicial) {
        super(id, 5.0, 0.2, 20.0, 0.3, ubicacionInicial); // Mayor capacidad que drone
    }

    @Override
    public String getTipo() {
        return "ROVER";
    }

    @Override
    public double calcularVelocidadPromedio() {
        return 15.0; // km/h - más lento que drone pero más capacidad
    }

    @Override
    public void realizarEntrega(Edificio destino) {
        registrarEvento("ROVER iniciando entrega terrestre");
        registrarEvento("Velocidad promedio: " + calcularVelocidadPromedio() + " km/h");
        registrarEvento("Capacidad de carga: " + getCapacidadCarga() + " kg");
        registrarEvento("Modo: Terrestre - Superficie estable");

        // Simular movimiento específico de rover
        simularMovimiento(destino);

        registrarEvento("Entrega de pedido completada con éxito");
    }

    @Override
    public double estimateEnergyCost(double distancia) {
        // Rovers son más eficientes en terreno plano
        return distancia * getConsumoEnergia();
    }
}
