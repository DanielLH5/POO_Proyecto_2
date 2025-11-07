package model.pedidos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ColaPedidos implements Serializable {
    private List<Pedido> pedidos;

    public ColaPedidos() {
        this.pedidos = new ArrayList<>();
    }

    // Agregar pedido al FINAL de la cola
    public void agregarPedido(Pedido pedido) {
        if (pedido != null && pedido.estaPendiente()) {
            pedidos.add(pedido);
        }
    }

    // Obtener y remover el PRIMER pedido de la cola
    public Pedido obtenerSiguientePedido() {
        if (pedidos.isEmpty()) {
            return null;
        }
        return pedidos.remove(0); // Remueve el primero
    }

    // Ver el primer pedido SIN removerlo
    public Pedido verSiguientePedido() {
        if (pedidos.isEmpty()) {
            return null;
        }
        return pedidos.get(0);
    }

    // Verificar si hay pedidos pendientes
    public boolean hayPedidosPendientes() {
        return !pedidos.isEmpty();
    }

    // Cantidad de pedidos en cola
    public int cantidadPedidos() {
        return pedidos.size();
    }

    // Obtener todos los pedidos (para mostrar en GUI)
    public List<Pedido> getTodosPedidos() {
        return new ArrayList<>(pedidos);
    }

    // Remover un pedido específico (si se cancela)
    public boolean removerPedido(Pedido pedido) {
        return pedidos.remove(pedido);
    }

    @Override
    public String toString() {
        return "Cola de Pedidos: " + pedidos.size() + " pedidos pendientes";
    }
}