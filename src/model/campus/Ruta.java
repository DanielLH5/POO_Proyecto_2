package model.campus;

import java.io.Serializable;

public class Ruta implements Serializable {
    private final String id;
    private final Edificio origen;
    private final Edificio destino;
    private final double distancia; // en metros
    private final double tiempoEstimado; // en minutos

    public Ruta(String id, Edificio origen, Edificio destino, double distancia, double tiempoEstimado) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Los edificios no pueden ser nulos");
        }
        if (distancia <= 0) {
            throw new IllegalArgumentException("La distancia debe ser positiva");
        }
        if (tiempoEstimado <= 0) {
            throw new IllegalArgumentException("El tiempo estimado debe ser positivo");
        }
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede ser nulo o vacío");
        }

        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;
        this.tiempoEstimado = tiempoEstimado;
    }

    // Constructor alternativo que genera ID automáticamente
    public Ruta(Edificio origen, Edificio destino, double distancia, double tiempoEstimado) {
        this(generarIdAutomatico(origen, destino), origen, destino, distancia, tiempoEstimado);
    }

    // Getters
    public String getId() {
        return id;
    }

    public Edificio getOrigen() {
        return origen;
    }

    public Edificio getDestino() {
        return destino;
    }

    public double getDistancia() {
        return distancia;
    }

    public double getTiempoEstimado() {
        return tiempoEstimado;
    }

    // Método para generar ID automático basado en los edificios
    private static String generarIdAutomatico(Edificio origen, Edificio destino) {
        return origen.getId() + "_" + destino.getId();
    }

    // Metodo importante: Verificar si conecta dos edificios
    public boolean conecta(Edificio edificioA, Edificio edificioB) {
        return (origen.equals(edificioA) && destino.equals(edificioB)) ||
                (origen.equals(edificioB) && destino.equals(edificioA));
    }

    // Verificar si un edificio es parte de esta ruta
    public boolean contieneEdificio(Edificio edificio) {
        return origen.equals(edificio) || destino.equals(edificio);
    }

    // Obtener el otro extremo de la ruta
    public Edificio getOtroExtremo(Edificio edificio) {
        if (origen.equals(edificio)) {
            return destino;
        } else if (destino.equals(edificio)) {
            return origen;
        }
        return null; // El edificio no está en esta ruta
    }

    // Representación textual de la ruta
    @Override
    public String toString() {
        return String.format("Ruta %s: %s ↔ %s (%.1fm, %.1fmin)",
                id, origen.getId(), destino.getId(), distancia, tiempoEstimado);
    }

    // Para comparar rutas (dos rutas son iguales si conectan los mismos edificios)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Ruta otraRuta = (Ruta) obj;

        // Las rutas son iguales si conectan los mismos edificios (sin importar dirección)
        return (origen.equals(otraRuta.origen) && destino.equals(otraRuta.destino)) ||
                (origen.equals(otraRuta.destino) && destino.equals(otraRuta.origen));
    }

    @Override
    public int hashCode() {
        // Mismo hash para A↔B y B↔A
        return origen.hashCode() + destino.hashCode();
    }
}