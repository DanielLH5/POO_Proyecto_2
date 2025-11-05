package model.campus;

import java.util.*;

public class Campus {
<<<<<<< HEAD
    private List<Edificio> edificios;    // ✅ Lista en lugar de Map
=======
<<<<<<< Updated upstream
}
=======
    private List<Edificio> edificios;
>>>>>>> Aportes_Elian
    private List<Ruta> rutas;
    private Edificio centroPrincipal;

    public Campus() {
        this.edificios = new ArrayList<>();
        this.rutas = new ArrayList<>();
<<<<<<< HEAD
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
=======
        this.centroPrincipal = null;
    }

    public Campus(boolean configuracionPorDefecto) {
        this();
        if (configuracionPorDefecto) {
            inicializarCampusEjemplo();
        }
    }

    public boolean agregarEdificio(Edificio edificio) {
        if (edificio == null || existeEdificio(edificio.getId())) {
            return false;
        }
        edificios.add(edificio);
        return true;
    }

    public boolean agregarEdificio(String id, String nombre, int capacidad, boolean centroCarga) {
        if (existeEdificio(id)) {
            return false;
        }
        Edificio nuevoEdificio = new Edificio(id, nombre, capacidad, centroCarga);
        return edificios.add(nuevoEdificio);
    }

    public boolean agregarEdificio(String id, String nombre, int capacidad, boolean centroCarga, boolean esCentroPrincipal) {
        if (existeEdificio(id)) {
            return false;
        }
        Edificio nuevoEdificio = new Edificio(id, nombre, capacidad, centroCarga, esCentroPrincipal);
        if (esCentroPrincipal) {
            setCentroPrincipal(nuevoEdificio);
        }
        return edificios.add(nuevoEdificio);
    }

    public Edificio getEdificio(String id) {
        for (Edificio edificio : edificios) {
            if (edificio.getId().equalsIgnoreCase(id)) {
                return edificio;
            }
        }
        return null;
>>>>>>> Aportes_Elian
    }

    public boolean existeEdificio(String id) {
        return getEdificio(id) != null;
    }

<<<<<<< HEAD
    // Metodos para rutas
    public void agregarRuta(Ruta ruta) {
        rutas.add(ruta);
    }

    // Calcular distancia entre dos edificios
    public double getDistancia(Edificio origen, Edificio destino) {
        if (origen.equals(destino)) return 0;
=======
    public List<Edificio> getEdificiosPorTipo(boolean soloCentrosCarga) {
        List<Edificio> resultado = new ArrayList<>();
        for (Edificio edificio : edificios) {
            if (!soloCentrosCarga || edificio.isTieneCentroCarga()) {
                resultado.add(edificio);
            }
        }
        return resultado;
    }

    public boolean agregarRuta(Ruta ruta) {
        if (ruta == null || existeRuta(ruta.getOrigen(), ruta.getDestino())) {
            return false;
        }
        rutas.add(ruta);
        return true;
    }

    public boolean agregarRuta(String idOrigen, String idDestino, double distancia) {
        Edificio origen = getEdificio(idOrigen);
        Edificio destino = getEdificio(idDestino);

        if (origen == null || destino == null || origen.equals(destino)) {
            return false;
        }

        if (existeRuta(origen, destino)) {
            return false;
        }

        Ruta nuevaRuta = new Ruta(origen, destino, distancia);
        return rutas.add(nuevaRuta);
    }

    public boolean existeRuta(Edificio origen, Edificio destino) {
        return getRuta(origen, destino) != null;
    }

    public boolean existeRuta(String idOrigen, String idDestino) {
        Edificio origen = getEdificio(idOrigen);
        Edificio destino = getEdificio(idDestino);
        return origen != null && destino != null && existeRuta(origen, destino);
    }

    public double getDistancia(Edificio origen, Edificio destino) {
        if (origen == null || destino == null) {
            return -1;
        }
        if (origen.equals(destino)) {
            return 0;
        }
>>>>>>> Aportes_Elian

        for (Ruta ruta : rutas) {
            if (ruta.conecta(origen, destino)) {
                return ruta.getDistancia();
            }
        }
        return -1; // No hay ruta directa
    }

<<<<<<< HEAD
    // Obtener edificios vecinos
    public List<Edificio> getEdificiosVecinos(Edificio edificio) {
        List<Edificio> vecinos = new ArrayList<>();
=======
    public double getDistancia(String idOrigen, String idDestino) {
        Edificio origen = getEdificio(idOrigen);
        Edificio destino = getEdificio(idDestino);
        return getDistancia(origen, destino);
    }

    public List<Edificio> getEdificiosVecinos(Edificio edificio) {
        List<Edificio> vecinos = new ArrayList<>();
        if (edificio == null) {
            return vecinos;
        }

>>>>>>> Aportes_Elian
        for (Ruta ruta : rutas) {
            if (ruta.getOrigen().equals(edificio)) {
                vecinos.add(ruta.getDestino());
            } else if (ruta.getDestino().equals(edificio)) {
                vecinos.add(ruta.getOrigen());
            }
        }
        return vecinos;
    }

<<<<<<< HEAD
    // Metodo para encontrar ruta entre dos edificios
    public Ruta getRuta(Edificio origen, Edificio destino) {
=======
    public List<Edificio> getEdificiosVecinos(String idEdificio) {
        Edificio edificio = getEdificio(idEdificio);
        return getEdificiosVecinos(edificio);
    }

    public Ruta getRuta(Edificio origen, Edificio destino) {
        if (origen == null || destino == null) {
            return null;
        }

>>>>>>> Aportes_Elian
        for (Ruta ruta : rutas) {
            if (ruta.conecta(origen, destino)) {
                return ruta;
            }
        }
        return null;
    }

<<<<<<< HEAD
    // Metodos para centros de carga
=======
    public Ruta getRuta(String idOrigen, String idDestino) {
        Edificio origen = getEdificio(idOrigen);
        Edificio destino = getEdificio(idDestino);
        return getRuta(origen, destino);
    }

>>>>>>> Aportes_Elian
    public List<Edificio> getCentrosCarga() {
        List<Edificio> centros = new ArrayList<>();
        for (Edificio edificio : edificios) {
            if (edificio.isTieneCentroCarga()) {
                centros.add(edificio);
            }
        }
        return centros;
    }

<<<<<<< HEAD
    public Edificio getCentroCargaMasCercano(Edificio ubicacionActual) {
=======
    public List<Edificio> getCentrosPrincipales() {
        List<Edificio> centros = new ArrayList<>();
        for (Edificio edificio : edificios) {
            if (edificio.isEsCentroPrincipal()) {
                centros.add(edificio);
            }
        }
        return centros;
    }

    public Edificio getCentroCargaMasCercano(Edificio ubicacionActual) {
        if (ubicacionActual == null) {
            return null;
        }

>>>>>>> Aportes_Elian
        List<Edificio> centros = getCentrosCarga();
        Edificio masCercano = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Edificio centro : centros) {
<<<<<<< HEAD
=======
            if (centro.equals(ubicacionActual)) {
                continue; // Saltar el mismo edificio
            }

>>>>>>> Aportes_Elian
            double distancia = getDistancia(ubicacionActual, centro);
            if (distancia > 0 && distancia < menorDistancia) {
                menorDistancia = distancia;
                masCercano = centro;
            }
        }
        return masCercano;
    }

<<<<<<< HEAD
    // Getters actualizados para Listas
    public List<Edificio> getEdificios() {
        return new ArrayList<>(edificios); // Copia de la lista
=======
    public Edificio getCentroCargaMasCercano(String idUbicacionActual) {
        Edificio ubicacion = getEdificio(idUbicacionActual);
        return getCentroCargaMasCercano(ubicacion);
    }

    public boolean configurarCentroPrincipal(String idEdificio) {
        Edificio edificio = getEdificio(idEdificio);
        if (edificio != null && edificio.puedeSerCentroPrincipal()) {
            // Quitar centro principal anterior si existe
            if (centroPrincipal != null) {
                centroPrincipal.setEsCentroPrincipal(false);
            }
            // Establecer nuevo centro principal
            centroPrincipal = edificio;
            edificio.setEsCentroPrincipal(true);
            edificio.setTieneCentroCarga(true); // Asegurar que tenga centro de carga
            return true;
        }
        return false;
    }

    public void configurarModoCentralizado(String idCentroPrincipal, int capacidadCentro) {
        // Primero, quitar centros de carga de todos los edificios
        for (Edificio edificio : edificios) {
            edificio.setTieneCentroCarga(false);
            edificio.setEsCentroPrincipal(false);
        }

        // Configurar el centro principal
        Edificio centro = getEdificio(idCentroPrincipal);
        if (centro != null) {
            centro.setTieneCentroCarga(true);
            centro.setCapacidadVehiculos(capacidadCentro);
            centro.setEsCentroPrincipal(true);
            this.centroPrincipal = centro;
        }
    }

    public void configurarModoDistribuido(int capacidadBase) {
        for (Edificio edificio : edificios) {
            edificio.setTieneCentroCarga(true);
            edificio.setCapacidadVehiculos(capacidadBase);
            edificio.setEsCentroPrincipal(false);
        }
        if (centroPrincipal != null) {
            centroPrincipal.setEsCentroPrincipal(true);
        }
    }

    public void configurarTodosComoCentrosCarga(int capacidadBase) {
        for (Edificio edificio : edificios) {
            edificio.setTieneCentroCarga(true);
            if (capacidadBase > 0) {
                edificio.setCapacidadVehiculos(capacidadBase);
            }
        }
    }

    public boolean eliminarEdificio(String id) {
        Edificio edificio = getEdificio(id);
        if (edificio != null) {
            // Si es el centro principal, limpiar la referencia
            if (edificio.equals(centroPrincipal)) {
                centroPrincipal = null;
            }
            // Eliminar rutas asociadas
            rutas.removeIf(ruta -> ruta.getOrigen().equals(edificio) || ruta.getDestino().equals(edificio));
            // Eliminar edificio
            return edificios.remove(edificio);
        }
        return false;
    }

    public boolean eliminarRuta(Edificio origen, Edificio destino) {
        if (origen == null || destino == null) {
            return false;
        }
        return rutas.removeIf(ruta -> ruta.conecta(origen, destino));
    }

    // NUEVO: Eliminar ruta por IDs
    public boolean eliminarRuta(String idOrigen, String idDestino) {
        Edificio origen = getEdificio(idOrigen);
        Edificio destino = getEdificio(idDestino);
        return eliminarRuta(origen, destino);
    }

    public void limpiarCampus() {
        edificios.clear();
        rutas.clear();
        centroPrincipal = null;
    }

    // NUEVO: Inicializar campus de ejemplo
    private void inicializarCampusEjemplo() {
        // Crear edificios
        agregarEdificio("A", "Edificio de Ciencias", 10, true);
        agregarEdificio("B", "Edificio de Ingeniería", 8, false);
        agregarEdificio("C", "Centro Principal", 20, true, true);
        agregarEdificio("D", "Edificio de Artes", 6, true);

        // Crear rutas
        agregarRuta("A", "B", 90.0);
        agregarRuta("B", "C", 100.0);
        agregarRuta("C", "D", 120.0);
        agregarRuta("A", "C", 80.0);
        agregarRuta("A", "D", 200.0);
    }

    // NUEVO: Métodos de validación
    public boolean esConfiguracionValida() {
        return edificios.size() >= 2 &&
                !rutas.isEmpty() &&
                getCentrosCarga().size() >= 1;
    }

    public boolean esModoCentralizadoValido() {
        return centroPrincipal != null &&
                centroPrincipal.isEsCentroPrincipal() &&
                centroPrincipal.getCapacidadVehiculos() >= 10;
    }

    public boolean esModoDistribuidoValido() {
        return getCentrosCarga().size() >= 2;
    }

    public String getEstadisticas() {
        int totalEdificios = edificios.size();
        int centrosCarga = getCentrosCarga().size();
        int capacidadTotal = edificios.stream().mapToInt(Edificio::getCapacidadVehiculos).sum();
        int vehiculosEstacionados = edificios.stream().mapToInt(Edificio::getVehiculosEstacionados).sum();

        return String.format(
                "Estadísticas del Campus:\n" +
                        "• Total edificios: %d\n" +
                        "• Centros de carga: %d\n" +
                        "• Rutas configuradas: %d\n" +
                        "• Capacidad total: %d vehículos\n" +
                        "• Vehículos estacionados: %d\n" +
                        "• Centro principal: %s",
                totalEdificios, centrosCarga, rutas.size(), capacidadTotal,
                vehiculosEstacionados,
                centroPrincipal != null ? centroPrincipal.getNombre() + " (" + centroPrincipal.getId() + ")" : "No asignado"
        );
    }

    public Map<String, Object> getResumen() {
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("totalEdificios", edificios.size());
        resumen.put("totalRutas", rutas.size());
        resumen.put("centrosCarga", getCentrosCarga().size());
        resumen.put("centroPrincipal", centroPrincipal != null ? centroPrincipal.getId() : "N/A");
        resumen.put("configuracionValida", esConfiguracionValida());
        return resumen;
    }

    public int TotalEdificios() {
        return edificios.size();
    }

    public int TotalRutas() {
        return rutas.size();
    }

    public List<Edificio> getEdificios() {
        return new ArrayList<>(edificios);
>>>>>>> Aportes_Elian
    }

    public List<Ruta> getRutas() {
        return new ArrayList<>(rutas);
    }

    public Edificio getCentroPrincipal() {
        return centroPrincipal;
    }

    public void setCentroPrincipal(Edificio centroPrincipal) {
<<<<<<< HEAD
        this.centroPrincipal = centroPrincipal;
=======
        if (this.centroPrincipal != null) {
            this.centroPrincipal.setEsCentroPrincipal(false);
        }
        this.centroPrincipal = centroPrincipal;
        if (centroPrincipal != null) {
            centroPrincipal.setEsCentroPrincipal(true);
            centroPrincipal.setTieneCentroCarga(true);
        }
    }

    public boolean setCentroPrincipal(String idEdificio) {
        Edificio edificio = getEdificio(idEdificio);
        if (edificio != null) {
            setCentroPrincipal(edificio);
            return true;
        }
        return false;
>>>>>>> Aportes_Elian
    }

    @Override
    public String toString() {
<<<<<<< HEAD
        return "Campus con " + edificios.size() + " edificios y " + rutas.size() + " rutas";
    }
}}
=======
        return String.format("Campus [Edificios: %d, Rutas: %d, Centro Principal: %s]",
                edificios.size(), rutas.size(),
                centroPrincipal != null ? centroPrincipal.getId() : "Ninguno");
    }
}
>>>>>>> Stashed changes
>>>>>>> Aportes_Elian
