package main;

import Persistencia.PersistenceManager;
import model.sistema.GestorSimulador;
import model.vehiculos.Vehiculo;
import model.pedidos.Pedido;
import model.campus.Campus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class VehiclesGUI {
    private JDialog vehiclesDialog;
    private DefaultTableModel vehiclesModel;
    private GestorSimulador gestorSimulador;
    private List<Vehiculo> flota;
    private Campus campus;
    private JTextArea simulationArea; // Referencia directa

    public VehiclesGUI(JFrame parent) {
        this.flota = obtenerFlota();
        this.campus = obtenerCampus();

        if (this.flota != null && this.campus != null) {
            this.gestorSimulador = new GestorSimulador(flota, campus);
            initializeGUI(parent);
            cargarDatosVehiculos();
        } else {
            JOptionPane.showMessageDialog(parent,
                    "No se pudieron cargar los datos necesarios para el módulo de vehículos.",
                    "Error de inicialización", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Vehiculo> obtenerFlota() {
        List<Vehiculo> flota = PersistenceManager.cargarFlota();
        if (flota == null) {
            JOptionPane.showMessageDialog(null,
                    "No se encontró flota de vehículos. Se creará una lista vacía.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            return new ArrayList<>();
        }
        return flota;
    }

    private Campus obtenerCampus() {
        Campus campus = PersistenceManager.cargarCampus();
        if (campus == null) {
            JOptionPane.showMessageDialog(null,
                    "No se encontró configuración del campus. Configure el campus primero.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            throw new IllegalStateException("Campus no configurado");
        }
        return campus;
    }

    private void cargarDatosVehiculos() {
        actualizarTablaVehiculos();
        actualizarEstadisticas();
    }

    private void initializeGUI(JFrame parent) {
        vehiclesDialog = new JDialog(parent, "Monitoreo de Vehículos", true);
        vehiclesDialog.setLayout(new BorderLayout(10, 10));
        vehiclesDialog.setSize(1100, 750);
        vehiclesDialog.setLocationRelativeTo(parent);

        createHeader();
        createMainContent();
        createFooter();

        vehiclesDialog.setVisible(true);
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 140, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("MONITOREO DE VEHÍCULOS");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JButton backButton = new JButton("Volver al Menú");
        backButton.addActionListener(e -> vehiclesDialog.dispose());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);

        vehiclesDialog.add(headerPanel, BorderLayout.NORTH);
    }

    private void createMainContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        splitPane.setLeftComponent(createVehiclesTablePanel());
        splitPane.setRightComponent(createSimulationPanel());

        vehiclesDialog.add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createVehiclesTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Estado de la Flota"));

        // Tabla de vehículos
        String[] columns = {"ID", "Tipo", "Ubicación", "Estado", "Batería", "Pedido Actual"};
        vehiclesModel = new DefaultTableModel(columns, 0);
        JTable vehiclesTable = new JTable(vehiclesModel);
        JScrollPane tableScroll = new JScrollPane(vehiclesTable);

        // Controles de vehículos
        JPanel controlsPanel = new JPanel(new GridLayout(2, 4, 5, 5));

        JButton simulateMoveBtn = new JButton("Simular Movimiento");
        JButton forceChargeBtn = new JButton("Recargar");
        JButton viewEventsBtn = new JButton("Eventos");

        controlsPanel.add(simulateMoveBtn);
        controlsPanel.add(forceChargeBtn);
        controlsPanel.add(viewEventsBtn);

        tablePanel.add(tableScroll, BorderLayout.CENTER);
        tablePanel.add(controlsPanel, BorderLayout.SOUTH);

        // LISTENERS
        simulateMoveBtn.addActionListener(e -> {
            redirectSystemOutput(simulationArea);
            gestorSimulador.simularMovimientoFlota();
            actualizarTablaVehiculos();
            actualizarEstadisticas();
        });

        viewEventsBtn.addActionListener(e -> {
            int selectedRow = vehiclesTable.getSelectedRow();
            if (selectedRow >= 0) {
                String vehiculoId = (String) vehiclesModel.getValueAt(selectedRow, 0);
                mostrarHistorialVehiculo(vehiculoId);
            } else {
                JOptionPane.showMessageDialog(vehiclesDialog,
                        "Seleccione un vehículo de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        forceChargeBtn.addActionListener(e -> {
            int selectedRow = vehiclesTable.getSelectedRow();
            if (selectedRow >= 0) {
                String vehiculoId = (String) vehiclesModel.getValueAt(selectedRow, 0);
                forzarRecarga(vehiculoId);
            } else {
                JOptionPane.showMessageDialog(vehiclesDialog,
                        "Seleccione un vehículo de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        return tablePanel;
    }

    private JPanel createSimulationPanel() {
        JPanel simulationPanel = new JPanel(new BorderLayout(10, 10));
        simulationPanel.setBorder(BorderFactory.createTitledBorder("Panel de Simulación"));

        // Área de texto para mostrar la simulación
        simulationArea = new JTextArea();
        simulationArea.setEditable(false);
        simulationArea.setBackground(Color.BLACK);
        simulationArea.setForeground(Color.GREEN);
        simulationArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane simulationScroll = new JScrollPane(simulationArea);

        // Controles de simulación
        JPanel simControlsPanel = new JPanel(new GridLayout(1, 3, 5, 5));

        JButton startSimBtn = new JButton("Estado Flota");
        JButton clearLogBtn = new JButton("Limpiar Log");

        simControlsPanel.add(startSimBtn);
        simControlsPanel.add(clearLogBtn);

        simulationPanel.add(simulationScroll, BorderLayout.CENTER);
        simulationPanel.add(simControlsPanel, BorderLayout.SOUTH);

        // Listeners para simulación
        startSimBtn.addActionListener(e -> {
            redirectSystemOutput(simulationArea);
            gestorSimulador.mostrarEstadoFlota();
        });

        clearLogBtn.addActionListener(e -> {
            simulationArea.setText("");
        });

        return simulationPanel;
    }

    private void createFooter() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel statusLabel = new JLabel(" Vehículos activos: 0 | En entrega: 0 | Disponibles: 0 | En recarga: 0 ");
        statusLabel.setName("statusLabel");

        JButton autoRefreshBtn = new JButton("Auto-actualizar");

        footerPanel.add(statusLabel, BorderLayout.WEST);
        footerPanel.add(autoRefreshBtn, BorderLayout.EAST);

        vehiclesDialog.add(footerPanel, BorderLayout.SOUTH);

        autoRefreshBtn.addActionListener(e -> {
            actualizarTablaVehiculos();
            actualizarEstadisticas();
        });
    }

    // MÉTODOS AUXILIARES
    private void actualizarTablaVehiculos() {
        vehiclesModel.setRowCount(0);

        for (Vehiculo vehiculo : flota) {
            String pedidoInfo = vehiculo.getPedidoActual() != null ?
                    vehiculo.getPedidoActual().getId() : "Ninguno";

            vehiclesModel.addRow(new Object[]{
                    vehiculo.getId(),
                    vehiculo.getTipo(),
                    vehiculo.getUbicacionActual() != null ? vehiculo.getUbicacionActual().getId() : "N/A",
                    vehiculo.getEstado(),
                    String.format("%.1f%%", vehiculo.getEstadoBateria()),
                    pedidoInfo
            });
        }
    }

    private void actualizarEstadisticas() {
        JPanel footerPanel = (JPanel) vehiclesDialog.getContentPane().getComponent(2);
        JLabel statusLabel = null;

        for (Component comp : footerPanel.getComponents()) {
            if (comp instanceof JLabel && "statusLabel".equals(comp.getName())) {
                statusLabel = (JLabel) comp;
                break;
            }
        }

        if (statusLabel != null) {
            long disponibles = flota.stream().filter(Vehiculo::estaDisponible).count();
            long enPreparacion = flota.stream().filter(v -> "PREPARACION".equals(v.getEstado())).count();
            long enEntrega = flota.stream().filter(v -> "EN_ENTREGA".equals(v.getEstado())).count();
            long enCarga = flota.stream().filter(v -> "EN_CARGA".equals(v.getEstado())).count();
            long activos = flota.size() - flota.stream().filter(v -> "FUERA_SERVICIO".equals(v.getEstado())).count();

            statusLabel.setText(String.format(
                    " Activos: %d | Prep: %d | Entrega: %d | Disp: %d | Carga: %d ",
                    activos, enPreparacion, enEntrega, disponibles, enCarga
            ));
        }
    }

    private void mostrarHistorialVehiculo(String vehiculoId) {
        StringBuilder historial = new StringBuilder();
        historial.append("HISTORIAL - ").append(vehiculoId).append("\n\n");

        Vehiculo vehiculo = flota.stream()
                .filter(v -> v.getId().equals(vehiculoId))
                .findFirst()
                .orElse(null);

        if (vehiculo != null) {
            for (String evento : vehiculo.getHistorialEventos()) {
                historial.append(evento).append("\n");
            }
        } else {
            historial.append("Vehículo no encontrado");
        }

        JTextArea textArea = new JTextArea(historial.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(vehiclesDialog, scrollPane,
                "Historial - " + vehiculoId, JOptionPane.INFORMATION_MESSAGE);
    }

    private void forzarRecarga(String vehiculoId) {
        Vehiculo vehiculo = flota.stream()
                .filter(v -> v.getId().equals(vehiculoId))
                .findFirst()
                .orElse(null);

        if (vehiculo != null) {
            vehiculo.cargarBateria(vehiculo.getBateria().getCapacidadTotal() * 0.5);
            actualizarTablaVehiculos();
            JOptionPane.showMessageDialog(vehiclesDialog,
                    "Vehículo " + vehiculoId + " recargado al " +
                            String.format("%.1f%%", vehiculo.getEstadoBateria()));
        }
    }

    private void actualizarDatosDesdePersistencia() {
        this.flota = PersistenceManager.cargarFlota();
        this.campus = PersistenceManager.cargarCampus();
        if (this.flota != null && this.campus != null) {
            this.gestorSimulador = new GestorSimulador(flota, campus);
        }
    }

    private void redirectSystemOutput(JTextArea textArea) {
        if (textArea != null) {
            PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
            System.setOut(printStream);
            System.setErr(printStream);
        }
    }

    // Clase auxiliar para redirigir output
    private static class CustomOutputStream extends java.io.OutputStream {
        private JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) {
            textArea.append(String.valueOf((char) b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }
}