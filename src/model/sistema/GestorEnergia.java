package model.sistema;

import model.vehiculos.Vehiculo;
import interfaces.Chargeable;
import Persistencia.PersistenceManager;
import java.util.List;
import java.util.ArrayList;

public class GestorEnergia {
    private List<Vehiculo> flota;
    private double consumoTotal;
    private PoliticaRecarga politicaRecarga;

    public GestorEnergia(List<Vehiculo> flota) {
        this.flota = flota != null ? flota : new ArrayList<>();
        this.consumoTotal = 0.0;
        this.politicaRecarga = new PoliticaRecarga();
    }

    // MÉTODO MEJORADO PARA RECARGAR VEHÍCULO CON PERSISTENCIA
    public boolean recargarVehiculo(String vehiculoId, double cantidad) {
        Vehiculo vehiculo = buscarVehiculoPorId(vehiculoId);
        if (vehiculo != null && vehiculo instanceof Chargeable) {
            Chargeable chargeable = (Chargeable) vehiculo;
            chargeable.cargarBateria(cantidad);

            // GUARDAR EN PERSISTENCIA INMEDIATAMENTE
            PersistenceManager.guardarFlota(flota);
            System.out.println("✓ Vehículo " + vehiculoId + " recargado y guardado en persistencia");

            return true;
        }
        return false;
    }

    public void recargarTodosCompletamente() {
        for (Vehiculo vehiculo : flota) {
            if (vehiculo instanceof Chargeable) {
                Chargeable chargeable = (Chargeable) vehiculo;
                double cargaNecesaria = 100.0 - chargeable.getEstadoBateria();
                if (cargaNecesaria > 0) {
                    chargeable.cargarBateria(cargaNecesaria);
                }
            }
        }

        // GUARDAR EN PERSISTENCIA DESPUÉS DE RECARGAR TODOS
        PersistenceManager.guardarFlota(flota);
        System.out.println("✓ Toda la flota recargada y guardada en persistencia");
    }

    public int recargarVehiculosBateriaBaja() {
        int recargados = 0;
        for (Vehiculo vehiculo : flota) {
            if (vehiculo instanceof Chargeable) {
                Chargeable chargeable = (Chargeable) vehiculo;
                if (chargeable.necesitaCarga()) {
                    double cargaNecesaria = 100.0 - chargeable.getEstadoBateria();
                    chargeable.cargarBateria(cargaNecesaria);
                    recargados++;
                }
            }
        }

        if (recargados > 0) {
            PersistenceManager.guardarFlota(flota);
            System.out.println("✓ " + recargados + " vehículos recargados y guardados en persistencia");
        }

        return recargados;
    }

    public void registrarConsumo(Vehiculo vehiculo, double consumo) {
        this.consumoTotal += consumo;

        // GUARDAR EN PERSISTENCIA DESPUÉS DE REGISTRAR CONSUMO
        PersistenceManager.guardarFlota(flota);
        System.out.println("✓ Consumo registrado y vehículos guardados en persistencia");
    }

    // Estimar consumo energético para una ruta
    public double estimarConsumoRuta(Vehiculo vehiculo, double distancia) {
        if (vehiculo == null) return 0.0;

        // Usar el método estimateEnergyCost del vehículo si está disponible
        try {
            return vehiculo.estimateEnergyCost(distancia);
        } catch (Exception e) {
            // Estimación por defecto basada en el tipo de vehículo
            String tipo = vehiculo.getTipo().toLowerCase();
            if (tipo.contains("dron")) {
                return distancia * 0.8; // kWh por km para drones
            } else if (tipo.contains("rover")) {
                return distancia * 1.2; // kWh por km para rovers
            } else {
                return distancia * 1.0; // kWh por km por defecto
            }
        }
    }

    // Verificar si un vehículo tiene carga suficiente para una ruta
    public boolean tieneCargaSuficiente(Vehiculo vehiculo, double distancia) {
        if (vehiculo instanceof Chargeable) {
            Chargeable chargeable = (Chargeable) vehiculo;
            double consumoEstimado = estimarConsumoRuta(vehiculo, distancia);
            double bateriaActual = chargeable.getEstadoBateria();

            // Considerar que necesitamos al menos 10% de batería después del viaje
            return (bateriaActual - (consumoEstimado / 10.0)) >= 10.0;
        }
        return false;
    }

    // Obtener vehículos con batería baja
    public List<Vehiculo> getVehiculosBateriaBaja() {
        List<Vehiculo> bajaBateria = new ArrayList<>();
        for (Vehiculo vehiculo : flota) {
            if (vehiculo instanceof Chargeable) {
                Chargeable chargeable = (Chargeable) vehiculo;
                if (chargeable.necesitaCarga()) {
                    bajaBateria.add(vehiculo);
                }
            }
        }
        return bajaBateria;
    }

    // Obtener estadísticas de energía
    public EstadisticasEnergia getEstadisticas() {
        double bateriaPromedio = 0.0;
        int vehiculosBajaBateria = 0;
        double consumoTotal = 0.0;

        for (Vehiculo vehiculo : flota) {
            if (vehiculo instanceof Chargeable) {
                Chargeable chargeable = (Chargeable) vehiculo;
                bateriaPromedio += chargeable.getEstadoBateria();

                if (chargeable.necesitaCarga()) {
                    vehiculosBajaBateria++;
                }
            }
        }

        if (!flota.isEmpty()) {
            bateriaPromedio /= flota.size();
        }

        return new EstadisticasEnergia(bateriaPromedio, vehiculosBajaBateria, this.consumoTotal);
    }

    public PoliticaRecarga getPoliticaRecarga() {
        return this.politicaRecarga;
    }

    // Buscar vehículo por ID
    private Vehiculo buscarVehiculoPorId(String id) {
        return flota.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Clase interna para estadísticas
    public static class EstadisticasEnergia {
        public final double bateriaPromedio;
        public final int vehiculosBajaBateria;
        public final double consumoTotal;

        public EstadisticasEnergia(double bateriaPromedio, int vehiculosBajaBateria, double consumoTotal) {
            this.bateriaPromedio = bateriaPromedio;
            this.vehiculosBajaBateria = vehiculosBajaBateria;
            this.consumoTotal = consumoTotal;
        }
    }

    // Clase para política de recarga
    public static class PoliticaRecarga {
        private double nivelRecargaAutomatica = 20.0; // 20%
        private boolean recargaAutomatica = true;

        public boolean debeRecargar(double nivelBateria) {
            return recargaAutomatica && nivelBateria <= nivelRecargaAutomatica;
        }

        // Getters y setters
        public double getNivelRecargaAutomatica() { return nivelRecargaAutomatica; }
        public void setNivelRecargaAutomatica(double nivel) { this.nivelRecargaAutomatica = nivel; }
        public boolean isRecargaAutomatica() { return recargaAutomatica; }
        public void setRecargaAutomatica(boolean recargaAutomatica) { this.recargaAutomatica = recargaAutomatica; }
    }
}