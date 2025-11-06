package main;
import model.campus.Campus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeliverySystemGUI {
    private JFrame mainFrame;

    public static Campus campusConfigurado;

    public DeliverySystemGUI() {
        initializeGUI();
    }

    private void initializeGUI() {
        mainFrame = new JFrame("Sistema de Entregas Automatizadas - Menú Principal");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setSize(600, 500);
        mainFrame.setLocationRelativeTo(null);

        createHeader();
        createMenuButtons();
        createStatusBar();

        mainFrame.setVisible(true);
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(600, 80));
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("SISTEMA DE ENTREGAS AUTOMATIZADAS", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel subtitleLabel = new JLabel("Campus Universitario - POO IIS2025", SwingConstants.CENTER);
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        mainFrame.add(headerPanel, BorderLayout.NORTH);
    }

    private void createMenuButtons() {
        JPanel menuPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        menuPanel.setBackground(Color.WHITE);

        // Botón 1: Configuración del Campus y Flota
        JButton configButton = createMenuButton("CONFIGURACIÓN", "Configurar Campus y Flota",
                new Color(65, 105, 225));
        configButton.addActionListener(e -> abrirVentanaConfiguracion());

        // Botón 2: Gestión de Pedidos
        JButton ordersButton = createMenuButton("GESTIÓN DE PEDIDOS", "Generar y Registrar Pedidos",
                new Color(34, 139, 34));
        ordersButton.addActionListener(e -> openOrdersWindow());

        // Botón 3: Monitoreo de Vehículos
        JButton vehiclesButton = createMenuButton("MONITOREO VEHÍCULOS", "Asignación y Seguimiento",
                new Color(255, 140, 0));
        vehiclesButton.addActionListener(e -> openVehiclesWindow());

        // Botón 4: Gestión Energética
        JButton energyButton = createMenuButton("GESTIÓN ENERGÉTICA", "Batería y Consumo",
                new Color(220, 20, 60));
        energyButton.addActionListener(e -> openEnergyWindow());

        // Botón 5: Estadísticas
        JButton statsButton = createMenuButton("ESTADÍSTICAS", "Rendimiento y Métricas",
                new Color(147, 112, 219));
        statsButton.addActionListener(e -> openStatisticsWindow());

        // Botón 6: Log del Sistema
        JButton logButton = createMenuButton("LOG DEL SISTEMA", "Eventos y Notificaciones",
                new Color(47, 79, 79));
        logButton.addActionListener(e -> openLogWindow());

        menuPanel.add(configButton);
        menuPanel.add(ordersButton);
        menuPanel.add(vehiclesButton);
        menuPanel.add(energyButton);
        menuPanel.add(statsButton);
        menuPanel.add(logButton);

        mainFrame.add(menuPanel, BorderLayout.CENTER);
    }

    class RoundedBorder implements javax.swing.border.Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }


    private JButton createMenuButton(String title, String subtitle, Color color) {
        JButton button = new JButton("<html><center><b>" + title + "</b><br><small>" + subtitle + "</small></center></html>");
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorderPainted(false);

        button.setBorder(new RoundedBorder(15));

        return button;
    }

    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setBackground(Color.LIGHT_GRAY);

        JLabel statusLabel = new JLabel(" Sistema listo - Seleccione una opción del menú ");
        JLabel versionLabel = new JLabel("v1.0 - POO IIS2025 ");

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(versionLabel, BorderLayout.EAST);

        mainFrame.add(statusBar, BorderLayout.SOUTH);
    }

    // Métodos para abrir las ventanas específicas
    private void abrirVentanaConfiguracion() {
        new CampusModeSelectionDialog(mainFrame);
    }

    private void openOrdersWindow() {
        new PedidosGUI(mainFrame);
    }

    private void openVehiclesWindow() {
        //new VehiclesGUI(mainFrame);
    }

    private void openEnergyWindow() {
        //new EnergyGUI(mainFrame);
    }

    private void openStatisticsWindow() {
        //new StatisticsGUI(mainFrame);
    }

    private void openLogWindow() {
        //new LogGUI(mainFrame);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
            new DeliverySystemGUI();
        });
    }
}