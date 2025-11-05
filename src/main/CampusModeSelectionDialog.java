package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CampusModeSelectionDialog {
    private JDialog modeDialog;
    private CampusConfigurationMode selectedMode;
    private boolean configurationCompleted = false;

    public enum CampusConfigurationMode {
        CENTRO_CARGA_CENTRALIZADO,
        CENTROS_CARGA_DISTRIBUIDOS
    }

    public CampusModeSelectionDialog(JFrame parent) {
        initializeGUI(parent);
    }

    private void initializeGUI(JFrame parent) {
        modeDialog = new JDialog(parent, "Selección de Modo de Campus", true);
        modeDialog.setLayout(new BorderLayout(20, 20));
        modeDialog.setSize(600, 500);
        modeDialog.setLocationRelativeTo(parent);
        modeDialog.setResizable(false);

        createHeader();
        createModeSelectionPanel();
        createFooter();

        modeDialog.setVisible(true);
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("CONFIGURACIÓN DEL CAMPUS UNIVERSITARIO", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel subtitleLabel = new JLabel("Seleccione el modo de configuración del sistema de entregas", SwingConstants.CENTER);
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        modeDialog.add(headerPanel, BorderLayout.NORTH);
    }

    private void createModeSelectionPanel() {
        JPanel selectionPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        selectionPanel.setBackground(Color.WHITE);

        // Opción 1: Centro de Carga Centralizado
        JPanel centralizedPanel = createModePanel(
                "CENTRO DE CARGA CENTRALIZADO",
                "• Un único edificio como centro principal de carga\n" +
                        "• Capacidad para todos los vehículos del sistema\n" +
                        "• Los demás edificios son puntos de entrega\n" +
                        "• Ideal para campus pequeños o medianos\n" +
                        "• Gestión centralizada de la flota",
                new Color(65, 105, 225),
                "Seleccionar este modo"
        );

        // Opción 2: Centros de Carga Distribuidos
        JPanel distributedPanel = createModePanel(
                "CENTROS DE CARGA DISTRIBUIDOS",
                "• Múltiples edificios con centros de carga\n" +
                        "• Capacidad variable por edificio\n" +
                        "• Distribución descentralizada de vehículos\n" +
                        "• Ideal para campus grandes\n" +
                        "• Mayor redundancia y disponibilidad",
                new Color(34, 139, 34),
                "Seleccionar este modo"
        );

        // Listeners para los paneles
        centralizedPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedMode = CampusConfigurationMode.CENTRO_CARGA_CENTRALIZADO;
                openConfigurationWindow();
            }
        });

        distributedPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedMode = CampusConfigurationMode.CENTROS_CARGA_DISTRIBUIDOS;
                openConfigurationWindow();
            }
        });

        selectionPanel.add(centralizedPanel);
        selectionPanel.add(distributedPanel);

        modeDialog.add(selectionPanel, BorderLayout.CENTER);
    }

    private JPanel createModePanel(String title, String description, Color color, String buttonText) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Título
        JLabel titleLabel = new JLabel("<html><center><b>" + title + "</b></center></html>", SwingConstants.CENTER);
        titleLabel.setForeground(color);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Descripción
        JTextArea descArea = new JTextArea(description);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(Color.WHITE);
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Botón de selección
        JButton selectButton = new JButton(buttonText);
        selectButton.setBackground(color);
        selectButton.setForeground(Color.WHITE);
        selectButton.setFocusPainted(false);
        selectButton.setFont(new Font("Arial", Font.BOLD, 12));
        selectButton.setContentAreaFilled(true);
        selectButton.setOpaque(true);
        selectButton.setBorderPainted(false);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(descArea, BorderLayout.CENTER);
        panel.add(selectButton, BorderLayout.SOUTH);

        // Listener para el botón
        selectButton.addActionListener(e -> {
            if (title.contains("CENTRALIZADO")) {
                selectedMode = CampusConfigurationMode.CENTRO_CARGA_CENTRALIZADO;
            } else {
                selectedMode = CampusConfigurationMode.CENTROS_CARGA_DISTRIBUIDOS;
            }
            openConfigurationWindow();
        });

        return panel;
    }

    private void createFooter() {
        JPanel footerPanel = new JPanel(new FlowLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        footerPanel.setBackground(Color.WHITE);

        JLabel infoLabel = new JLabel("Seleccione un modo de configuración para continuar");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerPanel.add(infoLabel);

        modeDialog.add(footerPanel, BorderLayout.SOUTH);
    }

    private void openConfigurationWindow() {
        modeDialog.dispose();
        new ConfigurationGUI(null, selectedMode); // Pasar el modo seleccionado
    }

    public CampusConfigurationMode getSelectedMode() {
        return selectedMode;
    }

    public boolean isConfigurationCompleted() {
        return configurationCompleted;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CampusModeSelectionDialog(null);
        });
    }
}