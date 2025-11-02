package persistence;

import java.io.*;

public class GestorArchivos {

    // METODO PRINCIPAL: Guardar cualquier objeto
    public static boolean guardar(Object objeto, String archivo) {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(archivo))) {
            salida.writeObject(objeto);
            System.out.println("Guardado: " + archivo);
            return true;
        } catch (IOException e) {
            System.out.println("Error guardando: " + e.getMessage());
            return false;
        }
    }

    // METODO PRINCIPAL: Cargar cualquier objeto
    public static Object cargar(String archivo) {
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo))) {
            Object objeto = entrada.readObject();
            System.out.println("Cargado: " + archivo);
            return objeto;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error cargando: " + e.getMessage());
            return null;
        }
    }

    // VERIFICAR SI EXISTE ARCHIVO
    public static boolean existe(String archivo) {
        return new File(archivo).exists();
    }

    // GUARDAR TEXTO (para logs o configuraciones)
    public static void guardarTexto(String texto, String archivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
            writer.print(texto);
            System.out.println("Texto guardado: " + archivo);
        } catch (IOException e) {
            System.out.println("Error guardando texto: " + e.getMessage());
        }
    }
}