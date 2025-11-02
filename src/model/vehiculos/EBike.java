package model.vehiculos;

import model.campus.Edificio;

public class EBike extends Vehiculo {

    public EBike(String id, Edificio ubicacionInicial) {
        super(id,
                5.0,    // capacidadBateria (kWh) - batería más pequeña
                0.5,    // nivelMinimoBateria (kWh)
                10.0,   // capacidadCarga (kg) - carga media
                0.08,   // consumoEnergia (kWh/km) - muy eficiente
                ubicacionInicial);
    }

    @Override
    public String getTipo() {
        return "E-BIKE";
    }

    @Override
    public double calcularVelocidadPromedio() {
        return 18.0; // km/h - velocidad intermedia
    }

    // E-bikes pueden usar ciclovías
    public boolean puedeUsarCiclovias() {
        return true;
    }

    @Override
    public String toString() {
        return "E-BIKE " + id + " (" + Math.round(getEstadoBateria()) + "%) - " + getEstado();
    }
}