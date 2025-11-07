package model.pedidos;

import java.io.Serializable;

public enum EstadoPedido implements Serializable {
    RECIBIDO,      // Pedido recibido, esperando asignación
    PREPARACION,   // Vehículo asignado, preparando entrega
    EN_CAMINO,     // Vehículo en movimiento hacia destino
    ENTREGADO,     // Pedido entregado exitosamente
    CANCELADO      // Pedido cancelado (sin vehículo disponible, etc.)
}