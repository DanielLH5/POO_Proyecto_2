package util;

public class GeneradorIDs {
    private static int contadorVehiculos = 0;
    private static int contadorPedidos = 0;

    public static String generarIdVehiculo(String tipo) {
        contadorVehiculos++;
        return tipo + "_" + contadorVehiculos; // DRON_1, ROVER_1, EBIKE_1
    }

    public static String generarIdPedido() {
        contadorPedidos++;
        return "PEDIDO_" + contadorPedidos;    // PEDIDO_1, PEDIDO_2, PEDIDO_3
    }

    public static void reiniciarContadores() {
        contadorVehiculos = 0;
        contadorPedidos = 0;
    }
}