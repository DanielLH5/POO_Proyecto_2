package model.campus;

public class Ruta {
    private final Edificio origen;
    private final Edificio destino;
    private final double distancia; // en metros

    public Ruta(Edificio origen, Edificio destino, double distancia) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Los edificios no pueden ser nulos");
        }
        if (distancia <= 0) {
            throw new IllegalArgumentException("La distancia debe ser positiva");
        }

        this.origen = origen;
        this.destino = destino;
        this.distancia = distancia;
    }

    // Getters
    public Edificio getOrigen() {
        return origen;
    }

    public Edificio getDestino() {
        return destino;
    }

    public double getDistancia() {
        return distancia;
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
        return origen.getId() + " ↔ " + destino.getId() + " (" + distancia + "m)";
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