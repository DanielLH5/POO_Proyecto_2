package model.sistema;

import model.vehiculos.Vehiculo;
import model.campus.Campus;
import model.campus.Edificio;

public class GestorEnergia {
    private Campus campus;

    public GestorEnergia(Campus campus) {
        this.campus = campus;
    }

    // 1. Calcular consumo para un viaje
    public double calcularConsumo(Vehiculo vehiculo, Edificio origen, Edificio destino) {
        double distancia = campus.getDistancia(origen, destino);
        return vehiculo.estimateEnergyCost(distancia);
    }

    // 2. Verificar si puede hacer un viaje
    public boolean puedeHacerViaje(Vehiculo vehiculo, Edificio origen, Edificio destino) {
        double consumo = calcularConsumo(vehiculo, origen, destino);
        return vehiculo.getBateria().getNivelActual() >= consumo;
    }

    // 3. Enviar a cargar si es necesario
    public boolean necesitaCarga(Vehiculo vehiculo) {
        return vehiculo.getEstadoBateria() <= 20.0;
    }

    // 4. Encontrar centro de carga cercano
    public Edificio getCentroCargaCercano(Edificio ubicacion) {
        return campus.getCentroCargaMasCercano(ubicacion);
    }

    // 5. Cargar vehículo completamente
    public void cargarCompletamente(Vehiculo vehiculo) {
        vehiculo.getBateria().cargarCompleto();
    }

    // 6. Simular consumo en un tramo
    public boolean consumirEnTramo(Vehiculo vehiculo, Edificio desde, Edificio hasta) {
        double consumo = calcularConsumo(vehiculo, desde, hasta);
        return vehiculo.getBateria().consumir(consumo);
    }
}