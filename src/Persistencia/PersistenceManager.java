package Persistencia;

import model.campus.Campus;
import model.vehiculos.Vehiculo;
import model.pedidos.Pedido;
import model.pedidos.ColaPedidos;
import java.io.*;
import java.util.List;

public class PersistenceManager {
    private static final String CAMPUS_FILE = "campus_data.dat";
    private static final String VEHICLES_FILE = "vehicles_data.dat";
    private static final String PEDIDOS_FILE = "pedidos_data.dat";
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

    public static void guardarPedidos(List<Pedido> pedidos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PEDIDOS_FILE))) {
            oos.writeObject(pedidos);
            System.out.println(pedidos.size() + " pedidos guardados exitosamente");
        } catch (IOException e) {
            System.err.println("Error guardando pedidos: " + e.getMessage());
            throw new RuntimeException("Error en persistencia de pedidos", e);
        }
    }

    // CARGAR PEDIDOS
    @SuppressWarnings("unchecked")
    public static List<Pedido> cargarPedidos() {
        File file = new File(PEDIDOS_FILE);
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PEDIDOS_FILE))) {
            List<Pedido> pedidos = (List<Pedido>) ois.readObject();
            System.out.println(pedidos.size() + " pedidos cargados exitosamente");
            return pedidos;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error cargando pedidos: " + e.getMessage());
            return null;
        }
    }

    // GUARDAR COLA DE PEDIDOS PENDIENTES
    public static void guardarColaPedidos(ColaPedidos colaPedidos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("cola_pedidos.dat"))) {
            oos.writeObject(colaPedidos);
            System.out.println("Cola de pedidos guardada exitosamente");
        } catch (IOException e) {
            System.err.println("Error guardando cola de pedidos: " + e.getMessage());
        }
    }

    // CARGAR COLA DE PEDIDOS PENDIENTES
    public static ColaPedidos cargarColaPedidos() {
        File file = new File("cola_pedidos.dat");
        if (!file.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("cola_pedidos.dat"))) {
            ColaPedidos cola = (ColaPedidos) ois.readObject();
            System.out.println("Cola de pedidos cargada exitosamente");
            return cola;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error cargando cola de pedidos: " + e.getMessage());
            return null;
        }
    }

    // Verificar si hay datos guardados (actualizado)
    public static boolean existenDatosGuardados() {
        return new File(CAMPUS_FILE).exists() ||
                new File(VEHICLES_FILE).exists() ||
                new File(PEDIDOS_FILE).exists();
    }

    // Limpiar todos los datos (actualizado)
    public static void limpiarDatos() {
        new File(CAMPUS_FILE).delete();
        new File(VEHICLES_FILE).delete();
        new File(PEDIDOS_FILE).delete();
        new File("cola_pedidos.dat").delete();
        new File(CONFIG_FILE).delete();
        System.out.println("Todos los datos persistentes eliminados");
    }
}
