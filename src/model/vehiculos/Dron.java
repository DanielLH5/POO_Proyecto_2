package model.vehiculos;

import model.campus.Edificio;

public class Dron extends Vehiculo {

    public Dron(String id, Edificio ubicacionInicial) {
        super(id,
                8.0,    // capacidadBateria (kWh)
                1.0,    // nivelMinimoBateria (kWh)
                3.0,    // capacidadCarga (kg) - drones cargan poco
                0.15,   // consumoEnergia (kWh/km) - bajo consumo
                ubicacionInicial);
    }

    @Override
    public String getTipo() {
        return "DRON";
    }

    @Override
    public double calcularVelocidadPromedio() {
        return 25.0; // km/h - más rápido por aire
    }

    // Drones pueden tener comportamiento especial para entregas aéreas
    public String realizarEntregaAerea() {
        return "Entregando por aire con dron " + id;
    }

    @Override
    public String toString() {
        return "DRON " + id + " (" + Math.round(getEstadoBateria()) + "%) - " + getEstado();
    }
}