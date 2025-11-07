package main;

import model.pedidos.GestionPedidos;
import model.pedidos.Pedido;
import model.pedidos.EstadoPedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PedidosGUI {
    private JDialog ordersDialog;
    private DefaultTableModel ordersModel;
    private GestionPedidos gestionPedidos;
    private JComboBox<String> originCombo;
    private JComboBox<String> destCombo;

    public PedidosGUI(JFrame parent) {
        this.gestionPedidos = new GestionPedidos();
        initializeGUI(parent);
        cargarPedidosExistentes();
    }

    private void cargarPedidosExistentes() {
        actualizarTablaPedidos();
        actualizarEstadisticas();
    }

    private void initializeGUI(JFrame parent) {
        ordersDialog = new JDialog(parent, "Gestión de Pedidos", true);
        ordersDialog.setLayout(new BorderLayout(10, 10));
        ordersDialog.setSize(1100, 750);
        ordersDialog.setLocationRelativeTo(parent);

        createHeader();
        createMainContent();
        createFooter();

        ordersDialog.setVisible(true);
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 139, 34));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("GESTIÓN DE PEDIDOS");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JButton backButton = new JButton("← Volver al Menú");
        backButton.addActionListener(e -> ordersDialog.dispose());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);

        ordersDialog.add(headerPanel, BorderLayout.NORTH);
    }

    private void createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(createOrderCreationPanel(), BorderLayout.NORTH);
        mainPanel.add(createOrdersTablePanel(), BorderLayout.CENTER);

        ordersDialog.add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createOrderCreationPanel() {
        JPanel creationPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        creationPanel.setBorder(BorderFactory.createTitledBorder("Creación de Nuevos Pedidos"));

        // Obtener edificios disponibles
        List<String> edificios = gestionPedidos.getEdificiosDisponibles();
        String[] idsEdificios = edificios.toArray(new String[0]);

        // Panel de creación manual
        JPanel manualPanel = new JPanel(new GridLayout(1, 8, 5, 5));
        originCombo = new JComboBox<>(idsEdificios);
        destCombo = new JComboBox<>();
        JSpinner weightSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 100.0, 0.1));
        JButton createOrderBtn = new JButton("Crear Pedido");

        manualPanel.add(new JLabel("Origen:"));
        manualPanel.add(originCombo);
        manualPanel.add(new JLabel("Destino:"));
        manualPanel.add(destCombo);
        manualPanel.add(new JLabel("Peso (kg):"));
        manualPanel.add(weightSpinner);
        manualPanel.add(createOrderBtn);

        // Panel de creación automática
        JPanel autoPanel = new JPanel(new FlowLayout());
        JSpinner autoCountSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));
        JButton autoGenerateBtn = new JButton("Generar Pedidos Automáticamente");

        autoPanel.add(new JLabel("Cantidad:"));
        autoPanel.add(autoCountSpinner);
        autoPanel.add(autoGenerateBtn);

        creationPanel.add(manualPanel);
        creationPanel.add(autoPanel);

        // Listener para cuando cambie el origen
        originCombo.addActionListener(e -> actualizarDestinosDisponibles());

        // Inicializar destinos
        actualizarDestinosDisponibles();

        // Listeners
        createOrderBtn.addActionListener(e -> {
            try {
                String origenId = (String) originCombo.getSelectedItem();
                String destinoId = (String) destCombo.getSelectedItem();
                double weight = (Double) weightSpinner.getValue();

                if (destinoId == null || destinoId.isEmpty()) {
                    JOptionPane.showMessageDialog(ordersDialog,
                            "Seleccione un destino válido", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Pedido nuevoPedido = gestionPedidos.crearPedidoManual(origenId, destinoId, weight);
                agregarPedidoATabla(nuevoPedido);

                JOptionPane.showMessageDialog(ordersDialog, "Pedido creado exitosamente: " + nuevoPedido.getId());
                actualizarEstadisticas();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ordersDialog,
                        "Error al crear pedido: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        autoGenerateBtn.addActionListener(e -> {
            try {
                int cantidad = (Integer) autoCountSpinner.getValue();
                List<Pedido> pedidosGenerados = gestionPedidos.generarPedidosAutomaticos(cantidad);

                for (Pedido pedido : pedidosGenerados) {
                    agregarPedidoATabla(pedido);
                }

                JOptionPane.showMessageDialog(ordersDialog,
                        "Se generaron " + cantidad + " pedidos automáticamente");
                actualizarEstadisticas();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ordersDialog,
                        "Error al generar pedidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return creationPanel;
    }

    private JPanel createOrdersTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Pedidos Registrados"));

        // Tabla de pedidos
        String[] columns = {"ID Pedido", "Origen", "Destino", "Peso", "Estado", "Vehículo Asignado"};
        ordersModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable ordersTable = new JTable(ordersModel);
        JScrollPane tableScroll = new JScrollPane(ordersTable);

        // Panel de controles superior (procesar y eliminar)
        JPanel topControlsPanel = new JPanel(new FlowLayout());
        JButton processOrdersBtn = new JButton("Procesar Pedidos Pendientes");
        JButton deleteAllBtn = new JButton("Eliminar Todos los Pedidos");

        topControlsPanel.add(processOrdersBtn);
        topControlsPanel.add(deleteAllBtn);

        // Panel de controles inferior (acciones individuales)
        JPanel bottomControlsPanel = new JPanel(new FlowLayout());
        JButton assignVehicleBtn = new JButton("Asignar Vehículo");
        JButton changeStatusBtn = new JButton("Cambiar Estado");
        JButton cancelOrderBtn = new JButton("Cancelar Pedido");
        JButton deleteOrderBtn = new JButton("Eliminar Pedido");
        JButton viewDetailsBtn = new JButton("Ver Detalles");

        bottomControlsPanel.add(assignVehicleBtn);
        bottomControlsPanel.add(changeStatusBtn);
        bottomControlsPanel.add(cancelOrderBtn);
        bottomControlsPanel.add(deleteOrderBtn);
        bottomControlsPanel.add(viewDetailsBtn);

        // Panel principal de controles
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.add(topControlsPanel, BorderLayout.NORTH);
        controlsPanel.add(bottomControlsPanel, BorderLayout.SOUTH);

        tablePanel.add(tableScroll, BorderLayout.CENTER);
        tablePanel.add(controlsPanel, BorderLayout.SOUTH);

        // Listeners
        processOrdersBtn.addActionListener(e -> {
            try {
                gestionPedidos.procesarPedidosPendientes();
                actualizarTablaPedidos();
                actualizarEstadisticas();
                JOptionPane.showMessageDialog(ordersDialog, "Procesamiento de pedidos completado");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ordersDialog,
                        "Error al procesar pedidos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteAllBtn.addActionListener(e -> {
            int confirmacion = JOptionPane.showConfirmDialog(
                    ordersDialog,
                    "¿Está seguro de que desea eliminar TODOS los pedidos?\nEsta acción no se puede deshacer.",
                    "Confirmar Eliminación Total",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                gestionPedidos.eliminarTodosLosPedidos();
                actualizarTablaPedidos();
                actualizarEstadisticas();
                JOptionPane.showMessageDialog(ordersDialog, "Todos los pedidos han sido eliminados");
            }
        });

        assignVehicleBtn.addActionListener(e -> {
            int selectedRow = ordersTable.getSelectedRow();
            if (selectedRow >= 0) {
                asignarVehiculoManualmente(selectedRow);
            } else {
                JOptionPane.showMessageDialog(ordersDialog,
                        "Seleccione un pedido de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        changeStatusBtn.addActionListener(e -> {
            int selectedRow = ordersTable.getSelectedRow();
            if (selectedRow >= 0) {
                cambiarEstadoPedido(selectedRow);
            } else {
                JOptionPane.showMessageDialog(ordersDialog,
                        "Seleccione un pedido de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelOrderBtn.addActionListener(e -> {
            int selectedRow = ordersTable.getSelectedRow();
            if (selectedRow >= 0) {
                cancelarPedidoSeleccionado(selectedRow);
            } else {
                JOptionPane.showMessageDialog(ordersDialog,
                        "Seleccione un pedido de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteOrderBtn.addActionListener(e -> {
            int selectedRow = ordersTable.getSelectedRow();
            if (selectedRow >= 0) {
                eliminarPedidoSeleccionado(selectedRow);
            } else {
                JOptionPane.showMessageDialog(ordersDialog,
                        "Seleccione un pedido de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        viewDetailsBtn.addActionListener(e -> {
            int selectedRow = ordersTable.getSelectedRow();
            if (selectedRow >= 0) {
                verDetallesPedido(selectedRow);
            } else {
                JOptionPane.showMessageDialog(ordersDialog,
                        "Seleccione un pedido de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        return tablePanel;
    }

    private void createFooter() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel statsLabel = new JLabel(" Total pedidos: 0 | Pendientes: 0 | En proceso: 0 | Completados: 0 ");
        statsLabel.setName("statsLabel");

        JButton refreshBtn = new JButton("Actualizar");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshBtn);

        footerPanel.add(statsLabel, BorderLayout.WEST);
        footerPanel.add(buttonPanel, BorderLayout.EAST);

        ordersDialog.add(footerPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> {
            actualizarTablaPedidos();
            actualizarEstadisticas();
            JOptionPane.showMessageDialog(ordersDialog, "Datos actualizados");
        });

    }

    // MÉTODOS AUXILIARES MEJORADOS
    private void actualizarDestinosDisponibles() {
        String origenId = (String) originCombo.getSelectedItem();
        if (origenId != null) {
            List<String> destinosConectados = gestionPedidos.getEdificiosConectados(origenId);
            destCombo.removeAllItems();
            for (String destino : destinosConectados) {
                destCombo.addItem(destino);
            }

            if (destinosConectados.isEmpty()) {
                destCombo.addItem("No hay conexiones disponibles");
            }
        }
    }

    private void agregarPedidoATabla(Pedido pedido) {
        String vehiculoInfo = pedido.getVehiculoAsignado() != null ?
                pedido.getVehiculoAsignado().getId() : "N/A";

        ordersModel.addRow(new Object[]{
                pedido.getId(),
                pedido.getOrigen().getId(),
                pedido.getDestino().getId(),
                String.format("%.1fkg", pedido.getPeso()),
                pedido.getEstado().toString(),
                vehiculoInfo
        });
    }

    private void actualizarTablaPedidos() {
        ordersModel.setRowCount(0);
        for (Pedido pedido : gestionPedidos.getTodosLosPedidos()) {
            agregarPedidoATabla(pedido);
        }
    }

    private void actualizarEstadisticas() {
        JPanel footerPanel = (JPanel) ordersDialog.getContentPane().getComponent(2);
        JLabel statsLabel = null;

        for (Component comp : footerPanel.getComponents()) {
            if (comp instanceof JLabel && "statsLabel".equals(comp.getName())) {
                statsLabel = (JLabel) comp;
                break;
            }
        }

        if (statsLabel != null) {
            statsLabel.setText(String.format(
                    " Total pedidos: %d | Pendientes: %d | En proceso: %d | Completados: %d ",
                    gestionPedidos.getTotalPedidos(),
                    gestionPedidos.getPedidosPendientes(),
                    gestionPedidos.getPedidosEnProceso(),
                    gestionPedidos.getPedidosCompletados()
            ));
        }
    }

    private void asignarVehiculoManualmente(int rowIndex) {
        String pedidoId = (String) ordersModel.getValueAt(rowIndex, 0);
        String origenId = (String) ordersModel.getValueAt(rowIndex, 1);

        List<String> vehiculosDisponibles = gestionPedidos.getVehiculosPorEdificio(origenId);

        if (vehiculosDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(ordersDialog,
                    "No hay vehículos disponibles en el edificio " + origenId, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String vehiculoSeleccionado = (String) JOptionPane.showInputDialog(
                ordersDialog,
                "Seleccione un vehículo del edificio " + origenId + " para el pedido " + pedidoId + ":",
                "Asignar Vehículo",
                JOptionPane.QUESTION_MESSAGE,
                null,
                vehiculosDisponibles.toArray(),
                vehiculosDisponibles.get(0)
        );

        if (vehiculoSeleccionado != null) {
            try {
                boolean exito = gestionPedidos.asignarVehiculoAPedido(pedidoId, vehiculoSeleccionado);
                if (exito) {
                    actualizarTablaPedidos();
                    actualizarEstadisticas();
                    JOptionPane.showMessageDialog(ordersDialog, "Vehículo asignado exitosamente");
                } else {
                    JOptionPane.showMessageDialog(ordersDialog,
                            "No se pudo asignar el vehículo", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ordersDialog,
                        "Error al asignar vehículo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cambiarEstadoPedido(int rowIndex) {
        String pedidoId = (String) ordersModel.getValueAt(rowIndex, 0);
        String estadoActual = (String) ordersModel.getValueAt(rowIndex, 4);

        List<EstadoPedido> estadosDisponibles = gestionPedidos.getEstadosDisponibles(pedidoId);

        if (estadosDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(ordersDialog,
                    "No hay estados disponibles para cambiar", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        EstadoPedido nuevoEstado = (EstadoPedido) JOptionPane.showInputDialog(
                ordersDialog,
                "Estado actual: " + estadoActual + "\nSeleccione nuevo estado para el pedido " + pedidoId + ":",
                "Cambiar Estado",
                JOptionPane.QUESTION_MESSAGE,
                null,
                estadosDisponibles.toArray(),
                estadosDisponibles.get(0)
        );

        if (nuevoEstado != null) {
            try {
                boolean exito = gestionPedidos.cambiarEstadoPedido(pedidoId, nuevoEstado);
                if (exito) {
                    actualizarTablaPedidos();
                    actualizarEstadisticas();
                    JOptionPane.showMessageDialog(ordersDialog, "Estado cambiado exitosamente a: " + nuevoEstado);
                } else {
                    JOptionPane.showMessageDialog(ordersDialog,
                            "No se pudo cambiar el estado", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ordersDialog,
                        "Error al cambiar estado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelarPedidoSeleccionado(int rowIndex) {
        String pedidoId = (String) ordersModel.getValueAt(rowIndex, 0);

        int confirmacion = JOptionPane.showConfirmDialog(
                ordersDialog,
                "¿Está seguro de que desea cancelar el pedido " + pedidoId + "?",
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = gestionPedidos.cancelarPedido(pedidoId);
                if (exito) {
                    actualizarTablaPedidos();
                    actualizarEstadisticas();
                    JOptionPane.showMessageDialog(ordersDialog, "Pedido cancelado exitosamente");
                } else {
                    JOptionPane.showMessageDialog(ordersDialog,
                            "No se pudo cancelar el pedido", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ordersDialog,
                        "Error al cancelar pedido: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarPedidoSeleccionado(int rowIndex) {
        String pedidoId = (String) ordersModel.getValueAt(rowIndex, 0);

        int confirmacion = JOptionPane.showConfirmDialog(
                ordersDialog,
                "¿Está seguro de que desea ELIMINAR permanentemente el pedido " + pedidoId + "?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean exito = gestionPedidos.eliminarPedido(pedidoId);
                if (exito) {
                    ordersModel.removeRow(rowIndex);
                    actualizarEstadisticas();
                    JOptionPane.showMessageDialog(ordersDialog, "Pedido eliminado exitosamente");
                } else {
                    JOptionPane.showMessageDialog(ordersDialog,
                            "No se pudo eliminar el pedido", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ordersDialog,
                        "Error al eliminar pedido: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void verDetallesPedido(int rowIndex) {
        String pedidoId = (String) ordersModel.getValueAt(rowIndex, 0);
        Pedido pedido = gestionPedidos.getTodosLosPedidos().stream()
                .filter(p -> p.getId().equals(pedidoId))
                .findFirst()
                .orElse(null);

        if (pedido != null) {
            String detalles = String.format(
                    "=== DETALLES DEL PEDIDO ===\n\n" +
                            "ID: %s\n" +
                            "Origen: %s\n" +
                            "Destino: %s\n" +
                            "Peso: %.1f kg\n" +
                            "Estado: %s\n" +
                            "Vehículo: %s\n\n" +
                            "=== INFORMACIÓN ADICIONAL ===\n" +
                            "Creado: %s\n" +
                            "En cola: %s",
                    pedido.getId(),
                    pedido.getOrigen().getId(),
                    pedido.getDestino().getId(),
                    pedido.getPeso(),
                    pedido.getEstado().toString(),
                    pedido.getVehiculoAsignado() != null ?
                            pedido.getVehiculoAsignado().getId() + " (" + pedido.getVehiculoAsignado().getClass().getSimpleName() + ")" :
                            "No asignado",
                    "Fecha de creación", // Aquí podrías agregar timestamp si lo tienes
                    gestionPedidos.getColaPedidos().getTodosPedidos().contains(pedido) ? "Sí" : "No"
            );

            JOptionPane.showMessageDialog(ordersDialog, detalles, "Detalles del Pedido - " + pedidoId,
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}