package Persistencia;

import model.campus.Campus;
import model.vehiculos.Vehiculo;
import java.io.*;
import java.util.List;

public class PersistenceManager {
    private static final String CAMPUS_FILE = "campus_data.dat";
    private static final String VEHICLES_FILE = "vehicles_data.dat";
    private static final String CONFIG_FILE = "system_config.dat";

    // Guardar campus completo
    public static void guardarCampus(Campus campus) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CAMPUS_FILE))) {
            oos.writeObject(campus);
            System.out.println("Campus guardado exitosamente");
        } catch (IOException e) {
            System.err.println("Error guardando campus: " + e.getMessage());
            throw new RuntimeException("Error en persistencia", e);
        }
    }

    // Cargar campus
    public static Campus cargarCampus() {
        File file = new File(CAMPUS_FILE);
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CAMPUS_FILE))) {
            Campus campus = (Campus) ois.readObject();
            System.out.println("Campus cargado exitosamente");
            return campus;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error cargando campus: " + e.getMessage());
            return null;
        }
    }

    // Guardar flota de vehículos
    public static void guardarFlota(List<Vehiculo> flota) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(VEHICLES_FILE))) {
            oos.writeObject(flota);
            System.out.println("Flota de " + flota.size() + " vehículos guardada");
        } catch (IOException e) {
            System.err.println("Error guardando flota: " + e.getMessage());
            throw new RuntimeException("Error en persistencia", e);
        }
    }

    // Cargar flota de vehículos
    @SuppressWarnings("unchecked")
    public static List<Vehiculo> cargarFlota() {
        File file = new File(VEHICLES_FILE);
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(VEHICLES_FILE))) {
            List<Vehiculo> flota = (List<Vehiculo>) ois.readObject();
            System.out.println("Flota de " + flota.size() + " vehículos cargada");
            return flota;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error cargando flota: " + e.getMessage());
            return null;
        }
    }

    // Guardar configuración del sistema
    public static void guardarConfiguracion(String modo, String usuario, java.time.LocalDateTime fecha) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONFIG_FILE))) {
            writer.println("MODO=" + modo);
            writer.println("USUARIO=" + usuario);
            writer.println("FECHA_CONFIG=" + fecha.toString());
            writer.println("VERSION=1.0");
            System.out.println("Configuración del sistema guardada");
        } catch (IOException e) {
            System.err.println("Error guardando configuración: " + e.getMessage());
        }
    }

    // Verificar si hay datos guardados
    public static boolean existenDatosGuardados() {
        return new File(CAMPUS_FILE).exists() || new File(VEHICLES_FILE).exists();
    }

    // Limpiar todos los datos (para testing/reset)
    public static void limpiarDatos() {
        new File(CAMPUS_FILE).delete();
        new File(VEHICLES_FILE).delete();
        new File(CONFIG_FILE).delete();
        System.out.println("Todos los datos persistentes eliminados");
    }
}
