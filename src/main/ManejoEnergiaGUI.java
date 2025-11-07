package main;

// EnergyGUI.java
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import model.sistema.GestorEnergia;
import model.vehiculos.Vehiculo;
import Persistencia.PersistenceManager;
import java.util.List;


public class ManejoEnergiaGUI {
    private JDialog energyDialog;
    private DefaultTableModel energyModel;
    private GestorEnergia gestorEnergia;
    private List<Vehiculo> flota;
    private JTable energyTable;

    public ManejoEnergiaGUI(JFrame parent) {
        // Cargar flota desde persistencia
        this.flota = PersistenceManager.cargarFlota();
        this.gestorEnergia = new GestorEnergia(flota);
        initializeGUI(parent);
        actualizarTablaEnergia();
        actualizarEstadisticas();
    }

    private void initializeGUI(JFrame parent) {
        energyDialog = new JDialog(parent, "Gestión Energética", true);
        energyDialog.setLayout(new BorderLayout(10, 10));
        energyDialog.setSize(900, 700);
        energyDialog.setLocationRelativeTo(parent);

        createHeader();
        createMainContent();
        createFooter();

        energyDialog.setVisible(true);
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(220, 20, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("GESTIÓN ENERGÉTICA");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JButton backButton = new JButton("Volver al Menú");
        backButton.addActionListener(e -> energyDialog.dispose());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);

        energyDialog.add(headerPanel, BorderLayout.NORTH);
    }

    private void createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(createBatteryTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createEnergyControlsPanel(), BorderLayout.SOUTH);

        energyDialog.add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createBatteryTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Estado de Batería de la Flota"));

        // Tabla de energía
        String[] columns = {"Vehículo", "Tipo", "Batería Actual", "Batería Máxima", "Estado", "Tiempo hasta Recarga"};
        energyModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        energyTable = new JTable(energyModel);

        energyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        energyTable.setRowSelectionAllowed(true);
        energyTable.setColumnSelectionAllowed(false);

        energyTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 2) {
                    String batteryStr = value.toString().replace("%", "");
                    try {
                        double battery = Double.parseDouble(batteryStr);
                        if (battery < 20) {
                            c.setBackground(Color.RED);
                            c.setForeground(Color.WHITE);
                        } else if (battery < 50) {
                            c.setBackground(Color.ORANGE);
                            c.setForeground(Color.BLACK);
                        } else {
                            c.setBackground(Color.GREEN);
                            c.setForeground(Color.BLACK);
                        }
                    } catch (NumberFormatException e) {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                } else {
                    c.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                    c.setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
                }

                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(energyTable);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createEnergyControlsPanel() {
        JPanel controlsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Controles de Gestión Energética"));

        JButton chargeSelectedBtn = new JButton("Recargar Seleccionado");
        JButton chargeAllBtn = new JButton("Recargar Todos");
        JButton setChargePolicyBtn = new JButton("Política de Recarga");
        JButton energyReportBtn = new JButton("Reporte de Consumo");
        JButton estimateConsumptionBtn = new JButton("Estimar Consumo");
        JButton lowBatteryAlertBtn = new JButton("Alertas Batería Baja");
        JButton optimizeRoutesBtn = new JButton("Optimizar Rutas");

        controlsPanel.add(chargeSelectedBtn);
        controlsPanel.add(chargeAllBtn);
        controlsPanel.add(setChargePolicyBtn);
        controlsPanel.add(energyReportBtn);
        controlsPanel.add(estimateConsumptionBtn);
        controlsPanel.add(lowBatteryAlertBtn);
        controlsPanel.add(optimizeRoutesBtn);

        // Listeners actualizados con funcionalidad real
        chargeSelectedBtn.addActionListener(e -> recargarSeleccionado());
        chargeAllBtn.addActionListener(e -> recargarTodos());
        setChargePolicyBtn.addActionListener(e -> configurarPoliticaRecarga());
        energyReportBtn.addActionListener(e -> generarReporteConsumo());
        estimateConsumptionBtn.addActionListener(e -> estimarConsumo());
        lowBatteryAlertBtn.addActionListener(e -> mostrarAlertasBateriaBaja());
        optimizeRoutesBtn.addActionListener(e -> optimizarRutasEnergeticamente());
        return controlsPanel;
    }

    private void actualizarDesdePersistencia() {
        this.flota = PersistenceManager.cargarFlota();
        this.gestorEnergia = new GestorEnergia(flota);
    }

    private void recargarSeleccionado() {
        int selectedRow = energyTable.getSelectedRow();
        if (selectedRow >= 0) {
            String vehiculoId = (String) energyModel.getValueAt(selectedRow, 0);

            int confirmacion = JOptionPane.showConfirmDialog(
                    energyDialog,
                    "¿Recargar completamente el vehículo " + vehiculoId + " al 100%?",
                    "Confirmar Recarga",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacion == JOptionPane.YES_OPTION) {
                boolean exito = gestorEnergia.recargarVehiculo(vehiculoId, 100.0);
                if (exito) {
                    // ACTUALIZAR DESDE PERSISTENCIA PARA OBTENER LOS DATOS MÁS RECIENTES
                    actualizarDesdePersistencia();
                    actualizarTablaEnergia();
                    actualizarEstadisticas();
                    JOptionPane.showMessageDialog(energyDialog,
                            "✓ Vehículo " + vehiculoId + " recargado al 100%\nLos cambios se han guardado en persistencia.",
                            "Recarga Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(energyDialog,
                            "Error al recargar el vehículo",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(energyDialog,
                    "Seleccione un vehículo de la tabla",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void recargarTodos() {
        int confirmacion = JOptionPane.showConfirmDialog(
                energyDialog,
                "¿Recargar completamente TODA la flota al 100%?\nEsta acción puede tomar unos segundos.",
                "Confirmar Recarga Masiva",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            gestorEnergia.recargarTodosCompletamente();

            // ACTUALIZAR DESDE PERSISTENCIA PARA OBTENER LOS DATOS MÁS RECIENTES
            actualizarDesdePersistencia();
            actualizarTablaEnergia();
            actualizarEstadisticas();

            JOptionPane.showMessageDialog(energyDialog,
                    "Toda la flota ha sido recargada al 100%\nLos cambios se han guardado en persistencia.",
                    "Recarga Masiva Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ACTUALIZAR EL BOTÓN DE REFRESH EN EL FOOTER
    private void createFooter() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel statsLabel = new JLabel();
        statsLabel.setName("statsLabel");

        JButton refreshBtn = new JButton("Actualizar desde Persistencia");
        JButton autoMonitorBtn = new JButton("Monitoreo Automático");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(refreshBtn);
        buttonPanel.add(autoMonitorBtn);

        footerPanel.add(statsLabel, BorderLayout.WEST);
        footerPanel.add(buttonPanel, BorderLayout.EAST);

        energyDialog.add(footerPanel, BorderLayout.SOUTH);

        // LISTENER MEJORADO PARA REFRESH
        refreshBtn.addActionListener(e -> {
            actualizarDesdePersistencia();
            actualizarTablaEnergia();
            actualizarEstadisticas();
            JOptionPane.showMessageDialog(energyDialog,
                    "✓ Datos actualizados desde persistencia\nSe cargaron los últimos cambios guardados.",
                    "Actualización Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        autoMonitorBtn.addActionListener(e -> activarMonitoreoAutomatico());
    }

    private void actualizarTablaEnergia() {
        energyModel.setRowCount(0);

        for (Vehiculo vehiculo : flota) {
            String tipo = vehiculo.getTipo();
            double bateriaActual = vehiculo.getEstadoBateria();
            double tiempoRecarga = vehiculo.calcularTiempoCargaCompleta();

            energyModel.addRow(new Object[]{
                    vehiculo.getId(),
                    tipo,
                    String.format("%.1f%%", bateriaActual),
                    "100%", // Asumiendo capacidad máxima fija
                    vehiculo.getEstado(),
                    String.format("%.1f min", tiempoRecarga)
            });
        }
    }

    private void actualizarEstadisticas() {
        JPanel footerPanel = (JPanel) energyDialog.getContentPane().getComponent(2);
        JLabel statsLabel = null;

        for (Component comp : footerPanel.getComponents()) {
            if (comp instanceof JLabel && "statsLabel".equals(comp.getName())) {
                statsLabel = (JLabel) comp;
                break;
            }
        }

        if (statsLabel != null) {
            GestorEnergia.EstadisticasEnergia stats = gestorEnergia.getEstadisticas();
            statsLabel.setText(String.format(
                    " Batería promedio: %.1f%% | Vehículos con baja batería: %d | Consumo total: %.1f kWh ",
                    stats.bateriaPromedio, stats.vehiculosBajaBateria, stats.consumoTotal
            ));
        }
    }

    private void configurarPoliticaRecarga() {
        // Diálogo para configurar política de recarga
        JSpinner nivelSpinner = new JSpinner(new SpinnerNumberModel(20.0, 5.0, 50.0, 5.0));
        JCheckBox recargaAutoCheck = new JCheckBox("Recarga automática", true);

        Object[] message = {
                "Nivel mínimo para recarga automática (%):", nivelSpinner,
                recargaAutoCheck
        };

        int option = JOptionPane.showConfirmDialog(
                energyDialog,
                message,
                "Configurar Política de Recarga",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            double nivel = (Double) nivelSpinner.getValue();
            boolean auto = recargaAutoCheck.isSelected();

            gestorEnergia.getPoliticaRecarga().setNivelRecargaAutomatica(nivel);
            gestorEnergia.getPoliticaRecarga().setRecargaAutomatica(auto);

            JOptionPane.showMessageDialog(energyDialog,
                    "Política actualizada: Recarga automática al " + nivel + "%");
        }
    }

    private void generarReporteConsumo() {
        GestorEnergia.EstadisticasEnergia stats = gestorEnergia.getEstadisticas();

        String reporte = String.format(
                "=== REPORTE DE CONSUMO ENERGÉTICO ===\n\n" +
                        "Batería promedio de la flota: %.1f%%\n" +
                        "Vehículos con batería baja: %d\n" +
                        "Consumo total acumulado: %.1f kWh\n\n" +
                        "=== VEHÍCULOS QUE NECESITAN RECARGA ===\n",
                stats.bateriaPromedio, stats.vehiculosBajaBateria, stats.consumoTotal
        );

        List<Vehiculo> bajaBateria = gestorEnergia.getVehiculosBateriaBaja();
        if (bajaBateria.isEmpty()) {
            reporte += "No hay vehículos con batería baja";
        } else {
            for (Vehiculo vehiculo : bajaBateria) {
                reporte += String.format("- %s (%s): %.1f%%\n",
                        vehiculo.getId(), vehiculo.getTipo(), vehiculo.getEstadoBateria());
            }
        }

        JOptionPane.showMessageDialog(energyDialog, reporte, "Reporte de Consumo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void estimarConsumo() {
        // Diálogo para estimar consumo de una ruta específica
        JTextField distanciaField = new JTextField(10);
        JComboBox<String> vehiculoCombo = new JComboBox<>();

        for (Vehiculo vehiculo : flota) {
            vehiculoCombo.addItem(vehiculo.getId() + " (" + vehiculo.getTipo() + ")");
        }

        Object[] message = {
                "Vehículo:", vehiculoCombo,
                "Distancia (km):", distanciaField
        };

        int option = JOptionPane.showConfirmDialog(
                energyDialog,
                message,
                "Estimar Consumo de Ruta",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            try {
                String vehiculoSeleccionado = (String) vehiculoCombo.getSelectedItem();
                String vehiculoId = vehiculoSeleccionado.split(" ")[0]; // Extraer ID
                double distancia = Double.parseDouble(distanciaField.getText());

                Vehiculo vehiculo = flota.stream()
                        .filter(v -> v.getId().equals(vehiculoId))
                        .findFirst()
                        .orElse(null);

                if (vehiculo != null) {
                    double consumoEstimado = gestorEnergia.estimarConsumoRuta(vehiculo, distancia);
                    boolean suficienteBateria = gestorEnergia.tieneCargaSuficiente(vehiculo, distancia);

                    String resultado = String.format(
                            "=== ESTIMACIÓN DE CONSUMO ===\n\n" +
                                    "Vehículo: %s\n" +
                                    "Distancia: %.1f km\n" +
                                    "Consumo estimado: %.1f kWh\n" +
                                    "Batería actual: %.1f%%\n" +
                                    "Suficiente batería: %s\n" +
                                    "Autonomía restante: %.1f km",
                            vehiculoId, distancia, consumoEstimado, vehiculo.getEstadoBateria(),
                            suficienteBateria ? "SÍ" : "NO",
                            (vehiculo.getEstadoBateria() / 100.0) * (vehiculo.getEstadoBateria() / consumoEstimado) * distancia
                    );

                    JOptionPane.showMessageDialog(energyDialog, resultado, "Resultado de Estimación", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(energyDialog, "Distancia inválida", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarAlertasBateriaBaja() {
        List<Vehiculo> bajaBateria = gestorEnergia.getVehiculosBateriaBaja();

        if (bajaBateria.isEmpty()) {
            JOptionPane.showMessageDialog(energyDialog, "No hay vehículos con batería baja", "Alertas", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder alertas = new StringBuilder("=== ALERTAS DE BATERÍA BAJA ===\n\n");
            for (Vehiculo vehiculo : bajaBateria) {
                alertas.append(String.format("%s (%s): %.1f%%\n",
                        vehiculo.getId(), vehiculo.getTipo(), vehiculo.getEstadoBateria()));
            }
            alertas.append("\nSe recomienda recargar estos vehículos.");

            JOptionPane.showMessageDialog(energyDialog, alertas.toString(), "Alertas de Batería Baja", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void optimizarRutasEnergeticamente() {
        // Lógica simple de optimización - priorizar vehículos con más batería
        List<Vehiculo> vehiculosOrdenados = flota.stream()
                .sorted((v1, v2) -> Double.compare(v2.getEstadoBateria(), v1.getEstadoBateria()))
                .toList();

        StringBuilder optimizacion = new StringBuilder("=== OPTIMIZACIÓN DE RUTAS ===\n\n");
        optimizacion.append("Orden recomendado para asignación de pedidos:\n\n");

        int posicion = 1;
        for (Vehiculo vehiculo : vehiculosOrdenados) {
            optimizacion.append(String.format("%d. %s (%s) - %.1f%% batería\n",
                    posicion++, vehiculo.getId(), vehiculo.getTipo(), vehiculo.getEstadoBateria()));
        }

        optimizacion.append("\nPriorizar vehículos con mayor batería para rutas largas.");

        JOptionPane.showMessageDialog(energyDialog, optimizacion.toString(), "Optimización de Rutas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void configuracionEnergia() {
        JOptionPane.showMessageDialog(energyDialog,
                "Configuración de energía - Funcionalidad en desarrollo",
                "Configuración", JOptionPane.INFORMATION_MESSAGE);
    }

    private void activarMonitoreoAutomatico() {
        // Simular monitoreo automático
        Timer timer = new Timer(5000, e -> { // Actualizar cada 5 segundos
            actualizarTablaEnergia();
            actualizarEstadisticas();

            // Verificar alertas críticas
            List<Vehiculo> criticos = flota.stream()
                    .filter(v -> v.getEstadoBateria() < 10)
                    .toList();

            if (!criticos.isEmpty()) {
                JOptionPane.showMessageDialog(energyDialog,
                        "¡ALERTA CRÍTICA! Vehículos con batería muy baja (<10%)",
                        "Alerta Crítica", JOptionPane.WARNING_MESSAGE);
            }
        });

        timer.start();
        JOptionPane.showMessageDialog(energyDialog,
                "Monitoreo automático activado. Se actualizará cada 5 segundos.",
                "Monitoreo", JOptionPane.INFORMATION_MESSAGE);
    }
}