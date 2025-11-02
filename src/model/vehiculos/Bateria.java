package model.vehiculos;

public class Bateria {
    private double capacidadTotal;
    private double nivelActual;
    private final double nivelMinimo;

    public Bateria(double capacidadTotal, double nivelMinimo) {
        this.capacidadTotal = capacidadTotal;
        this.nivelMinimo = nivelMinimo;
        this.nivelActual = capacidadTotal; // Empieza llena
    }

    // Getters
    public double getCapacidadTotal() {
        return capacidadTotal;
    }

    public double getNivelActual() {
        return nivelActual;
    }

    public double getNivelMinimo() {
        return nivelMinimo;
    }

    public double getPorcentaje() {
        return (nivelActual / capacidadTotal) * 100.0;
    }

    public boolean necesitaCarga() {
        return getPorcentaje() <= 20.0;
    }

    public boolean estaCritica() {
        return nivelActual <= nivelMinimo;
    }

    // Consumir energía
    public boolean consumir(double cantidad) {
        if (nivelActual >= cantidad) {
            nivelActual -= cantidad;
            return true;
        }
        return false;
    }

    // Cargar energía
    public void cargar(double cantidad) {
        nivelActual = Math.min(capacidadTotal, nivelActual + cantidad);
    }

    public void cargarCompleto() {
        nivelActual = capacidadTotal;
    }

    @Override
    public String toString() {
        return "Batería: " + Math.round(nivelActual) + "/" + Math.round(capacidadTotal) +
                " (" + Math.round(getPorcentaje()) + "%)";
        // Resultado: "Batería: 8/10 (80%)"
    }
}