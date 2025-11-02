package interfaces;

import model.campus.Edificio;

public interface Trackable {

    // Obtener la ubicación actual
    Edificio getUbicacionActual();

    // Actualizar la ubicación (para simulación)
    void actualizarUbicacion(Edificio nuevaUbicacion);

    // Obtener el historial de ubicaciones
    java.util.List<Edificio> getHistorialUbicaciones();

    // Verificar si está en movimiento
    default boolean estaEnMovimiento() {
        return getHistorialUbicaciones().size() > 1;
    }

    // Calcular distancia recorrida
    default double calcularDistanciaRecorrida() {
        // Lógica para sumar distancias entre ubicaciones del historial
        return 0.0; // Implementar según necesidad
    }
}