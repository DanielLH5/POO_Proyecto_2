package model.campus;

import java.util.*;

public class Campus {
    private List<Edificio> edificios;    // ✅ Lista en lugar de Map
    private List<Ruta> rutas;
    private Edificio centroPrincipal;

    public Campus() {
        this.edificios = new ArrayList<>();
        this.rutas = new ArrayList<>();
        inicializarCampusEjemplo();
    }

    // Esto lo podemos usar como prueba, lo ideal sería hacerlo random. Pero por ahora así podría funcionar.
    private void inicializarCampusEjemplo() {
        // Crear edificios
        agregarEdificio(new Edificio("A", "Edificio A", 10, true));
        agregarEdificio(new Edificio("B", "Edificio B", 8, false));
        agregarEdificio(new Edificio("C", "Centro Principal", 20, true));
        agregarEdificio(new Edificio("D", "Edificio D", 6, true));

        this.centroPrincipal = getEdificio("C"); // Buscar por ID

        // Crear rutas
        agregarRuta(new Ruta(getEdificio("A"), getEdificio("B"), 90));
        agregarRuta(new Ruta(getEdificio("B"), getEdificio("C"), 100));
        agregarRuta(new Ruta(getEdificio("C"), getEdificio("D"), 120));
        agregarRuta(new Ruta(getEdificio("A"), getEdificio("C"), 80));
        agregarRuta(new Ruta(getEdificio("A"), getEdificio("D"), 200));
    }

    // Metodo con listas (más simples)
    public void agregarEdificio(Edificio edificio) {
        if (!edificios.contains(edificio)) {
            edificios.add(edificio);
        }
    }

    // Buscar edificio por ID (recorriendo la lista)
    public Edificio getEdificio(String id) {
        for (Edificio edificio : edificios) {
            if (edificio.getId().equals(id)) {
                return edificio;
            }
        }
        return null; // No encontrado
    }

    public boolean existeEdificio(String id) {
        return getEdificio(id) != null;
    }

    // Metodos para rutas
    public void agregarRuta(Ruta ruta) {
        rutas.add(ruta);
    }

    // Calcular distancia entre dos edificios
    public double getDistancia(Edificio origen, Edificio destino) {
        if (origen.equals(destino)) return 0;

        for (Ruta ruta : rutas) {
            if (ruta.conecta(origen, destino)) {
                return ruta.getDistancia();
            }
        }
        return -1; // No hay ruta directa
    }

    // Obtener edificios vecinos
    public List<Edificio> getEdificiosVecinos(Edificio edificio) {
        List<Edificio> vecinos = new ArrayList<>();
        for (Ruta ruta : rutas) {
            if (ruta.getOrigen().equals(edificio)) {
                vecinos.add(ruta.getDestino());
            } else if (ruta.getDestino().equals(edificio)) {
                vecinos.add(ruta.getOrigen());
            }
        }
        return vecinos;
    }

    // Metodo para encontrar ruta entre dos edificios
    public Ruta getRuta(Edificio origen, Edificio destino) {
        for (Ruta ruta : rutas) {
            if (ruta.conecta(origen, destino)) {
                return ruta;
            }
        }
        return null;
    }

    // Metodos para centros de carga
    public List<Edificio> getCentrosCarga() {
        List<Edificio> centros = new ArrayList<>();
        for (Edificio edificio : edificios) {
            if (edificio.isTieneCentroCarga()) {
                centros.add(edificio);
            }
        }
        return centros;
    }

    public Edificio getCentroCargaMasCercano(Edificio ubicacionActual) {
        List<Edificio> centros = getCentrosCarga();
        Edificio masCercano = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Edificio centro : centros) {
            double distancia = getDistancia(ubicacionActual, centro);
            if (distancia > 0 && distancia < menorDistancia) {
                menorDistancia = distancia;
                masCercano = centro;
            }
        }
        return masCercano;
    }

    // Getters actualizados para Listas
    public List<Edificio> getEdificios() {
        return new ArrayList<>(edificios); // Copia de la lista
    }

    public List<Ruta> getRutas() {
        return new ArrayList<>(rutas);
    }

    public Edificio getCentroPrincipal() {
        return centroPrincipal;
    }

    public void setCentroPrincipal(Edificio centroPrincipal) {
        this.centroPrincipal = centroPrincipal;
    }

    @Override
    public String toString() {
        return "Campus con " + edificios.size() + " edificios y " + rutas.size() + " rutas";
    }
}