package main;

// VehiclesGUI.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VehiclesGUI {
    private JDialog vehiclesDialog;
    private DefaultTableModel vehiclesModel;

    public VehiclesGUI(JFrame parent) {
        initializeGUI(parent);
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

        JLabel titleLabel = new JLabel("🚗 MONITOREO DE VEHÍCULOS");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JButton backButton = new JButton("← Volver al Menú");
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
        splitPane.setRightComponent(createMapPanel());

        vehiclesDialog.add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createVehiclesTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Estado de la Flota"));

        // Tabla de vehículos
        String[] columns = {"ID", "Tipo", "Ubicación", "Estado", "Batería", "Pedido Actual", "Tiempo Estimado"};
        vehiclesModel = new DefaultTableModel(columns, 0);
        JTable vehiclesTable = new JTable(vehiclesModel);
        JScrollPane tableScroll = new JScrollPane(vehiclesTable);

        // Controles de vehículos
        JPanel controlsPanel = new JPanel(new GridLayout(2, 3, 5, 5));

        JButton updateLocationsBtn = new JButton("📍 Actualizar Ubicaciones");
        JButton simulateMoveBtn = new JButton("🎬 Simular Movimiento");
        JButton forceChargeBtn = new JButton("🔋 Forzar Recarga");
        JButton changeStatusBtn = new JButton("🔄 Cambiar Estado");
        JButton viewRouteBtn = new JButton("🗺️ Ver Ruta");
        JButton emergencyStopBtn = new JButton("🛑 Parada Emergencia");

        controlsPanel.add(updateLocationsBtn);
        controlsPanel.add(simulateMoveBtn);
        controlsPanel.add(forceChargeBtn);
        controlsPanel.add(changeStatusBtn);
        controlsPanel.add(viewRouteBtn);
        controlsPanel.add(emergencyStopBtn);

        tablePanel.add(tableScroll, BorderLayout.CENTER);
        tablePanel.add(controlsPanel, BorderLayout.SOUTH);

        // Listeners
        simulateMoveBtn.addActionListener(e -> {
            // CONECTAR: Lógica para simular movimiento
            JOptionPane.showMessageDialog(vehiclesDialog, "Simulando movimiento de vehículos...");
        });

        return tablePanel;
    }

    private JPanel createMapPanel() {
        JPanel mapPanel = new JPanel(new BorderLayout(10, 10));
        mapPanel.setBorder(BorderFactory.createTitledBorder("Mapa del Campus - Ubicación en Tiempo Real"));

        // Mapa visual
        JPanel mapVisualization = new JPanel();
        mapVisualization.setBackground(Color.WHITE);
        mapVisualization.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        mapVisualization.setPreferredSize(new Dimension(400, 400));

        // Información del vehículo seleccionado
        JPanel vehicleInfoPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        vehicleInfoPanel.setBorder(BorderFactory.createTitledBorder("Información del Vehículo Seleccionado"));

        vehicleInfoPanel.add(new JLabel("ID:"));
        vehicleInfoPanel.add(new JLabel("---"));
        vehicleInfoPanel.add(new JLabel("Tipo:"));
        vehicleInfoPanel.add(new JLabel("---"));
        vehicleInfoPanel.add(new JLabel("Ubicación:"));
        vehicleInfoPanel.add(new JLabel("---"));
        vehicleInfoPanel.add(new JLabel("Batería:"));
        vehicleInfoPanel.add(new JLabel("---"));
        vehicleInfoPanel.add(new JLabel("Estado:"));
        vehicleInfoPanel.add(new JLabel("---"));

        mapPanel.add(mapVisualization, BorderLayout.CENTER);
        mapPanel.add(vehicleInfoPanel, BorderLayout.SOUTH);

        return mapPanel;
    }

    private void createFooter() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel statusLabel = new JLabel(" Vehículos activos: 0 | En entrega: 0 | Disponibles: 0 | En recarga: 0 ");

        JButton autoRefreshBtn = new JButton("🔄 Auto-actualizar (10s)");
        JButton exportRouteBtn = new JButton("💾 Exportar Rutas");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(autoRefreshBtn);
        buttonPanel.add(exportRouteBtn);

        footerPanel.add(statusLabel, BorderLayout.WEST);
        footerPanel.add(buttonPanel, BorderLayout.EAST);

        vehiclesDialog.add(footerPanel, BorderLayout.SOUTH);
    }
}