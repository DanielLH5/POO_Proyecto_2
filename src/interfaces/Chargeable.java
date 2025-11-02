package interfaces;

public interface Chargeable {

    // Metodo para cargar la batería
    void cargarBateria(double cantidad);

    // Metodo para obtener el estado actual de la batería (porcentaje)
    double getEstadoBateria();

    // Metodo para verificar si necesita carga
    default boolean necesitaCarga() {
        return getEstadoBateria() < 20.0; // 20% mínimo
    }

    // Metodo para calcular tiempo de carga estimado
    default double calcularTiempoCargaCompleta() {
        return (100.0 - getEstadoBateria()) * 0.5; // Ejemplo: 0.5 min por %
    }
}