package model.vehiculos;

import model.campus.Edificio;

public class Rover extends Vehiculo {

    public Rover(String id, Edificio ubicacionInicial) {
        super(id,
                15.0,   // capacidadBateria (kWh) - más batería
                2.0,    // nivelMinimoBateria (kWh)
                20.0,   // capacidadCarga (kg) - carga más pesada
                0.3,    // consumoEnergia (kWh/km) - más consumo
                ubicacionInicial);
    }

    @Override
    public String getTipo() {
        return "ROVER";
    }

    @Override
    public double calcularVelocidadPromedio() {
        return 12.0; // km/h - más lento por tierra
    }

    // Rovers pueden manejar terrenos difíciles
    public boolean puedeManejarTerrenoAccidentado() {
        return true;
    }

    @Override
    public String toString() {
        return "ROVER " + id + " (" + Math.round(getEstadoBateria()) + "%) - " + getEstado();
    }
}