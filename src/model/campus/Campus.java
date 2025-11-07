package model.campus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Campus implements Serializable {
    private List<Edificio> edificios;
    private List<Ruta> rutas;
    private Edificio centroPrincipal;

    public Campus() {
        this.edificios = new ArrayList<>();
        this.rutas = new ArrayList<>();
        this.centroPrincipal = null;
    }

    public boolean agregarEdificio(Edificio edificio) {
        if (edificio == null || existeEdificio(edificio.getId())) {
            return false;
        }
        edificios.add(edificio);
        return true;
    }

    public boolean eliminarRuta(String rutaId) {
        if (rutaId == null || rutaId.trim().isEmpty()) {
            System.err.println("Error: El ID de la ruta no puede ser nulo o vacío");
            return false;
        }

        boolean eliminada = rutas.removeIf(ruta -> {
            boolean coincide = ruta.getId().equals(rutaId);
            if (coincide) {
                System.out.println("Ruta eliminada: " + ruta);
            }
            return coincide;
        });

        if (!eliminada) {
            System.err.println("Error: No se encontró ninguna ruta con ID: " + rutaId);
        }

        return eliminada;
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
    }

    public boolean existeEdificio(String id) {
        return getEdificio(id) != null;
    }

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

    public boolean agregarRuta(String origenId, String destinoId, double distancia, double tiempoEstimado) {
        try {
            Edificio origen = getEdificio(origenId);
            Edificio destino = getEdificio(destinoId);

            if (origen == null || destino == null) {
                throw new IllegalArgumentException("Uno o ambos edificios no existen");
            }

            if (origen.equals(destino)) {
                throw new IllegalArgumentException("No se puede crear una ruta entre el mismo edificio");
            }

            // Verificar si ya existe una ruta entre estos edificios
            if (existeRuta(origenId, destinoId)) {
                throw new IllegalArgumentException("Ya existe una ruta entre estos edificios");
            }

            Ruta nuevaRuta = new Ruta(origen, destino, distancia, tiempoEstimado);
            return rutas.add(nuevaRuta);

        } catch (Exception e) {
            System.err.println("Error al agregar ruta: " + e.getMessage());
            return false;
        }
    }

    public boolean existeRuta(String origenId, String destinoId) {
        return rutas.stream().anyMatch(ruta ->
                (ruta.getOrigen().getId().equals(origenId) && ruta.getDestino().getId().equals(destinoId)) ||
                        (ruta.getOrigen().getId().equals(destinoId) && ruta.getDestino().getId().equals(origenId))
        );
    }

    public boolean existeRuta(Edificio origen, Edificio destino) {
        return getRuta(origen, destino) != null;
    }

    public double getDistancia(Edificio origen, Edificio destino) {
        if (origen == null || destino == null) {
            return -1;
        }
        if (origen.equals(destino)) {
            return 0;
        }

        for (Ruta ruta : rutas) {
            if (ruta.conecta(origen, destino)) {
                return ruta.getDistancia();
            }
        }
        return -1; // No hay ruta directa
    }

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

        for (Ruta ruta : rutas) {
            if (ruta.getOrigen().equals(edificio)) {
                vecinos.add(ruta.getDestino());
            } else if (ruta.getDestino().equals(edificio)) {
                vecinos.add(ruta.getOrigen());
            }
        }
        return vecinos;
    }

    public List<Edificio> getEdificiosVecinos(String idEdificio) {
        Edificio edificio = getEdificio(idEdificio);
        return getEdificiosVecinos(edificio);
    }

    public Ruta getRuta(Edificio origen, Edificio destino) {
        if (origen == null || destino == null) {
            return null;
        }

        for (Ruta ruta : rutas) {
            if (ruta.conecta(origen, destino)) {
                return ruta;
            }
        }
        return null;
    }

    public Ruta getRuta(String idOrigen, String idDestino) {
        Edificio origen = getEdificio(idOrigen);
        Edificio destino = getEdificio(idDestino);
        return getRuta(origen, destino);
    }

    public List<Edificio> getCentrosCarga() {
        List<Edificio> centros = new ArrayList<>();
        for (Edificio edificio : edificios) {
            if (edificio.isTieneCentroCarga()) {
                centros.add(edificio);
            }
        }
        return centros;
    }

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

        List<Edificio> centros = getCentrosCarga();
        Edificio masCercano = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Edificio centro : centros) {
            if (centro.equals(ubicacionActual)) {
                continue; // Saltar el mismo edificio
            }

            double distancia = getDistancia(ubicacionActual, centro);
            if (distancia > 0 && distancia < menorDistancia) {
                menorDistancia = distancia;
                masCercano = centro;
            }
        }
        return masCercano;
    }

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
    }

    public List<Ruta> getRutas() {
        return new ArrayList<>(rutas);
    }

    public Edificio getCentroPrincipal() {
        return centroPrincipal;
    }

    public void setCentroPrincipal(Edificio centroPrincipal) {
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
    }

    @Override
    public String toString() {
        return String.format("Campus [Edificios: %d, Rutas: %d, Centro Principal: %s]",
                edificios.size(), rutas.size(),
                centroPrincipal != null ? centroPrincipal.getId() : "Ninguno");
    }
}

