package model.vehiculos;

import model.campus.Edificio;

public class EBike extends Vehiculo {

    public EBike(String id, Edificio ubicacionInicial) {
        super(id, 5.0, 0.5, 10.0, 0.08, ubicacionInicial); // Mayor capacidad
    }

    @Override
    public String getTipo() {
        return "EBike";
    }

    @Override
    public double calcularVelocidadPromedio() {
        return 18.0;
    }

    @Override
    public void realizarEntrega(Edificio destino) {
        registrarEvento("EBike iniciando entrega de pedido");
        registrarEvento("Velocidad promedio: " + calcularVelocidadPromedio() + " km/h");
        registrarEvento("Capacidad de carga: " + getCapacidadCarga() + " kg");
        registrarEvento("Modo: ECO - Manteniendo Consumo");

        simularMovimiento(destino);

        registrarEvento("Entrega de pedido completada con éxito");
    }

    @Override
    public double estimateEnergyCost(double distancia) {
        // AGV son muy eficientes en rutas predefinidas
        return distancia * getConsumoEnergia() * 0.9;
    }
}