package main;

// OrdersGUI.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PedidosGUI {
    private JDialog ordersDialog;
    private DefaultTableModel ordersModel;

    public PedidosGUI(JFrame parent) {
        initializeGUI(parent);
    }

    private void initializeGUI(JFrame parent) {
        ordersDialog = new JDialog(parent, "Gestión de Pedidos", true);
        ordersDialog.setLayout(new BorderLayout(10, 10));
        ordersDialog.setSize(1000, 700);
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

        // Panel de creación manual
        JPanel manualPanel = new JPanel(new GridLayout(1, 6, 5, 5));

        JComboBox<String> originCombo = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        JComboBox<String> destCombo = new JComboBox<>(new String[]{"A", "B", "C", "D"});
        JSpinner weightSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 100.0, 0.1));
        JButton createOrderBtn = new JButton("Crear Pedido");
        JButton clearFormBtn = new JButton("Limpiar");

        manualPanel.add(new JLabel("Origen:"));
        manualPanel.add(originCombo);
        manualPanel.add(new JLabel("Destino:"));
        manualPanel.add(destCombo);
        manualPanel.add(new JLabel("Peso (kg):"));
        manualPanel.add(weightSpinner);
        manualPanel.add(createOrderBtn);
        manualPanel.add(clearFormBtn);

        // Panel de creación automática
        JPanel autoPanel = new JPanel(new FlowLayout());
        JSpinner autoCountSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));
        JButton autoGenerateBtn = new JButton("Generar Pedidos Automáticamente");
        JButton bulkLoadBtn = new JButton("Cargar Pedidos desde Archivo");

        autoPanel.add(new JLabel("Cantidad:"));
        autoPanel.add(autoCountSpinner);
        autoPanel.add(autoGenerateBtn);
        autoPanel.add(bulkLoadBtn);

        creationPanel.add(manualPanel);
        creationPanel.add(autoPanel);

        // Listeners
        createOrderBtn.addActionListener(e -> {
            // CONECTAR: Lógica para crear pedido
            String origin = (String) originCombo.getSelectedItem();
            String destination = (String) destCombo.getSelectedItem();
            double weight = (Double) weightSpinner.getValue();

            String orderId = "PED-" + System.currentTimeMillis();
            ordersModel.addRow(new Object[]{orderId, origin, destination, weight + "kg", "PENDIENTE", "N/A"});
        });

        return creationPanel;
    }

    private JPanel createOrdersTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Pedidos Registrados"));

        // Tabla de pedidos
        String[] columns = {"ID Pedido", "Origen", "Destino", "Peso", "Estado", "Vehículo Asignado"};
        ordersModel = new DefaultTableModel(columns, 0);
        JTable ordersTable = new JTable(ordersModel);
        JScrollPane tableScroll = new JScrollPane(ordersTable);

        // Controles de pedidos
        JPanel controlsPanel = new JPanel(new FlowLayout());
        JButton processOrdersBtn = new JButton("Procesar Pedidos Pendientes");
        JButton assignVehicleBtn = new JButton("Asignar Vehículo");
        JButton cancelOrderBtn = new JButton("Cancelar Pedido");
        JButton viewDetailsBtn = new JButton("Ver Detalles");

        controlsPanel.add(processOrdersBtn);
        controlsPanel.add(assignVehicleBtn);
        controlsPanel.add(cancelOrderBtn);
        controlsPanel.add(viewDetailsBtn);

        tablePanel.add(tableScroll, BorderLayout.CENTER);
        tablePanel.add(controlsPanel, BorderLayout.SOUTH);

        // Listeners
        processOrdersBtn.addActionListener(e -> {
            // CONECTAR: Lógica para procesar pedidos
            JOptionPane.showMessageDialog(ordersDialog, "Procesando pedidos pendientes...");
        });

        return tablePanel;
    }

    private void createFooter() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel statsLabel = new JLabel(" Total pedidos: 0 | Pendientes: 0 | En proceso: 0 | Completados: 0 ");

        JButton refreshBtn = new JButton("Actualizar");
        JButton exportBtn = new JButton("Exportar Reporte");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshBtn);
        buttonPanel.add(exportBtn);

        footerPanel.add(statsLabel, BorderLayout.WEST);
        footerPanel.add(buttonPanel, BorderLayout.EAST);

        ordersDialog.add(footerPanel, BorderLayout.SOUTH);
    }
}