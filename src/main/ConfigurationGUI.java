package main;

// ConfigurationGUI.java (versión actualizada)
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import model.campus.Campus;
import model.campus.Edificio;
import model.campus.Ruta;

public class ConfigurationGUI {
    private JDialog configDialog;
    private DefaultTableModel buildingsModel;
    private DefaultTableModel routesModel;
    private Campus campus;
    private CampusModeSelectionDialog.CampusConfigurationMode campusMode;

    // Componentes específicos por modo
    private JComboBox<String> mainChargeCenterCombo;
    private JCheckBox allBuildingsChargeCheck;
    private JPanel modeSpecificPanel;

    public ConfigurationGUI(JFrame parent, CampusModeSelectionDialog.CampusConfigurationMode mode) {
        this.campusMode = mode;
        this.campus = new Campus(); // Inicializar campus vacío
        initializeGUI(parent);
    }

    private void initializeGUI(JFrame parent) {
        configDialog = new JDialog(parent, getWindowTitle(), true);
        configDialog.setLayout(new BorderLayout(10, 10));
        configDialog.setSize(1000, 700);
        configDialog.setLocationRelativeTo(parent);

        createHeader();
        createMainContent();
        createFooter();

        configDialog.setVisible(true);
    }

    private String getWindowTitle() {
        switch (campusMode) {
            case CENTRO_CARGA_CENTRALIZADO:
                return "Configuración - Centro de Carga Centralizado";
            case CENTROS_CARGA_DISTRIBUIDOS:
                return "Configuración - Centros de Carga Distribuidos";
            default:
                return "Configuración del Campus";
        }
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(getHeaderColor());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(getModeTitle(), SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel modeLabel = new JLabel(getModeDescription(), SwingConstants.CENTER);
        modeLabel.setForeground(Color.WHITE);
        modeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(getHeaderColor());
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(modeLabel, BorderLayout.SOUTH);

        JButton backButton = new JButton("← Cambiar Modo");
        backButton.addActionListener(e -> {
            configDialog.dispose();
            new CampusModeSelectionDialog(null);
        });

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(backButton, BorderLayout.EAST);

        configDialog.add(headerPanel, BorderLayout.NORTH);
    }

    private Color getHeaderColor() {
        switch (campusMode) {
            case CENTRO_CARGA_CENTRALIZADO:
                return new Color(65, 105, 225); // Azul
            case CENTROS_CARGA_DISTRIBUIDOS:
                return new Color(34, 139, 34); // Verde
            default:
                return new Color(70, 130, 180);
        }
    }

    private String getModeTitle() {
        switch (campusMode) {
            case CENTRO_CARGA_CENTRALIZADO:
                return "MODO: CENTRO DE CARGA CENTRALIZADO";
            case CENTROS_CARGA_DISTRIBUIDOS:
                return "MODO: CENTROS DE CARGA DISTRIBUIDOS";
            default:
                return "CONFIGURACIÓN DEL CAMPUS";
        }
    }

    private String getModeDescription() {
        switch (campusMode) {
            case CENTRO_CARGA_CENTRALIZADO:
                return "Un único centro principal de carga para todos los vehículos autónomos";
            case CENTROS_CARGA_DISTRIBUIDOS:
                return "Múltiples edificios con centros de carga integrados y capacidad variable";
            default:
                return "Sistema de entregas automatizadas";
        }
    }

    private void createMainContent() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Configuración del Modo", createModeConfigurationPanel());
        tabbedPane.addTab("Gestión de Edificios", createBuildingsPanel());
        //tabbedPane.addTab("Configuración de Rutas", createRoutesPanel());
        //tabbedPane.addTab("Resumen del Sistema", createSummaryPanel());

        configDialog.add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createModeConfigurationPanel() {
        JPanel modePanel = new JPanel(new BorderLayout(10, 10));
        modePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de información del modo
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información del Modo Seleccionado"));

        JTextArea infoArea = new JTextArea(6, 50);
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setBackground(Color.WHITE);
        infoArea.setFont(new Font("Arial", Font.PLAIN, 12));
        infoArea.setText(getModeDetailedDescription());

        JScrollPane infoScroll = new JScrollPane(infoArea);

        // Panel de configuración específica del modo
        modeSpecificPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        modeSpecificPanel.setBorder(BorderFactory.createTitledBorder("Configuración Específica del Modo"));

        setupModeSpecificConfiguration();

        modePanel.add(infoScroll, BorderLayout.NORTH);
        modePanel.add(modeSpecificPanel, BorderLayout.CENTER);

        return modePanel;
    }

    private String getModeDetailedDescription() {
        switch (campusMode) {
            case CENTRO_CARGA_CENTRALIZADO:
                return "CONFIGURACIÓN CENTRALIZADA:\n\n" +
                        "• Un único edificio funcionará como centro principal de carga y almacenamiento\n" +
                        "• Este centro tendrá capacidad para todos los vehículos autónomos del sistema\n" +
                        "• Los demás edificios serán puntos de entrega sin capacidad de carga\n" +
                        "• Los vehículos regresan al centro principal para recarga y mantenimiento\n" +
                        "• Ideal para campus con distribución geográfica compacta";

            case CENTROS_CARGA_DISTRIBUIDOS:
                return "CONFIGURACIÓN DISTRIBUIDA:\n\n" +
                        "• Múltiples edificios contarán con centros de carga integrados\n" +
                        "• Cada edificio tiene capacidad variable para vehículos autónomos\n" +
                        "• Los vehículos pueden cargarse en cualquier centro disponible\n" +
                        "• Mayor redundancia y disponibilidad del sistema\n" +
                        "• Ideal para campus extensos o con múltiples zonas alejadas";
            default:
                return "";
        }
    }

    private void actualizarComboBoxCentroPrincipal() {
        if (mainChargeCenterCombo != null) {
            mainChargeCenterCombo.removeAllItems();

            // Agregar TODOS los edificios del campus
            for (Edificio edificio : campus.getEdificios()) {
                mainChargeCenterCombo.addItem(edificio.getId());
            }

            // Seleccionar el centro principal actual si existe
            if (campus.getCentroPrincipal() != null) {
                mainChargeCenterCombo.setSelectedItem(campus.getCentroPrincipal().getId());
            }

            // Actualizar tooltip con la cantidad
            mainChargeCenterCombo.setToolTipText("Edificios disponibles: " + campus.getEdificios().size());
        }
    }

    private void setupModeSpecificConfiguration() {
        modeSpecificPanel.removeAll();

        switch (campusMode) {
            case CENTRO_CARGA_CENTRALIZADO:
                setupCentralizedMode();
                break;
            case CENTROS_CARGA_DISTRIBUIDOS:
                setupDistributedMode();
                break;
        }

        modeSpecificPanel.revalidate();
        modeSpecificPanel.repaint();
    }

    private void setupCentralizedMode() {
        JPanel centralizedPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel mainCenterLabel = new JLabel("Centro Principal de Carga:");
        mainChargeCenterCombo = new JComboBox<>();

        // NUEVO: Botón para actualizar la lista
        JButton refreshComboBtn = new JButton("Actualizar Lista");

        JButton setMainCenterBtn = new JButton("Establecer como Centro Principal");

        JLabel capacityLabel = new JLabel("Capacidad del Centro Principal:");
        JSpinner totalCapacitySpinner = new JSpinner(new SpinnerNumberModel(20, 5, 100, 5));

        JLabel infoLabel = new JLabel("El centro principal almacenará y cargará TODOS los vehículos");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));

        centralizedPanel.add(mainCenterLabel);
        centralizedPanel.add(mainChargeCenterCombo);
        centralizedPanel.add(new JLabel()); // Espacio vacío
        centralizedPanel.add(refreshComboBtn);
        centralizedPanel.add(capacityLabel);
        centralizedPanel.add(totalCapacitySpinner);
        centralizedPanel.add(setMainCenterBtn);
        centralizedPanel.add(infoLabel);

        actualizarComboBoxCentroPrincipal();

        // Listener para actualizar combo box
        refreshComboBtn.addActionListener(e -> {
            actualizarComboBoxCentroPrincipal();
            JOptionPane.showMessageDialog(configDialog,
                    "Lista de edificios actualizada",
                    "Actualización", JOptionPane.INFORMATION_MESSAGE);
        });

        setMainCenterBtn.addActionListener(e -> {
            String selectedId = (String) mainChargeCenterCombo.getSelectedItem();
            if (selectedId != null) {
                configureAsMainChargeCenter(selectedId, (Integer) totalCapacitySpinner.getValue());

                //Actualizar tabla después de configurar centro principal
                actualizarTablaCompleta();

                JOptionPane.showMessageDialog(configDialog,
                        "Centro principal establecido en: " + selectedId +
                                "\nCapacidad: " + totalCapacitySpinner.getValue() + " vehículos",
                        "Centro Principal Configurado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(configDialog,
                        "No hay edificios disponibles para configurar como centro principal",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        modeSpecificPanel.add(centralizedPanel);
    }

    private void setupDistributedMode() {
        JPanel distributedPanel = new JPanel(new GridLayout(5, 1, 10, 10));

        JLabel modeLabel = new JLabel("Modo: Todos los edificios tendrán centro de carga integrado");
        modeLabel.setFont(new Font("Arial", Font.BOLD, 12));

        allBuildingsChargeCheck = new JCheckBox("Configurar automáticamente todos los edificios como centros de carga", true);

        JLabel capacityLabel = new JLabel("Capacidad base por edificio:");
        JSpinner baseCapacitySpinner = new JSpinner(new SpinnerNumberModel(5, 2, 20, 1));

        JButton applyToAllBtn = new JButton("Aplicar configuración a todos los edificios");

        // NUEVO: Botón para forzar actualización de tabla
        JButton forceRefreshBtn = new JButton("Actualizar Tabla Después de Cambios");

        distributedPanel.add(modeLabel);
        distributedPanel.add(allBuildingsChargeCheck);
        distributedPanel.add(capacityLabel);
        distributedPanel.add(baseCapacitySpinner);
        distributedPanel.add(applyToAllBtn);
        distributedPanel.add(forceRefreshBtn);

        applyToAllBtn.addActionListener(e -> {
            int baseCapacity = (Integer) baseCapacitySpinner.getValue();
            boolean allChargeCenters = allBuildingsChargeCheck.isSelected();
            configureAllBuildingsAsChargeCenters(allChargeCenters, baseCapacity);

            JOptionPane.showMessageDialog(configDialog,
                    "Configuración aplicada a todos los edificios:\n" +
                            "• Centros de carga: " + (allChargeCenters ? "SÍ" : "NO") + "\n" +
                            "• Capacidad base: " + baseCapacity + " vehículos\n\n" +
                            "Use el botón 'Actualizar Tabla' para ver los cambios",
                    "Configuración Aplicada", JOptionPane.INFORMATION_MESSAGE);
        });

        // NUEVO: Listener para forzar actualización
        forceRefreshBtn.addActionListener(e -> {
            actualizarTablaCompleta();
            JOptionPane.showMessageDialog(configDialog,
                    "Tabla actualizada con la configuración actual de todos los edificios",
                    "Tabla Actualizada", JOptionPane.INFORMATION_MESSAGE);
        });

        modeSpecificPanel.add(distributedPanel);
    }

    private void actualizarTablaCompleta() {
        buildingsModel.setRowCount(0); // Limpiar tabla

        for (Edificio edificio : campus.getEdificios()) {
            String id = edificio.getId();
            String nombre = edificio.getNombre();
            int capacidad = edificio.getCapacidadVehiculos();
            boolean esCentroCarga = edificio.isTieneCentroCarga();

            agregarEdificioATabla(id, nombre, capacidad, esCentroCarga);
        }

    }


    private JPanel createBuildingsPanel() {
        JPanel buildingsPanel = new JPanel(new BorderLayout(10, 10));
        buildingsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel superior con controles
        JPanel topPanel = new JPanel(new BorderLayout());

        // Controles para agregar edificios
        JPanel buildingControls = new JPanel(new GridLayout(getBuildingControlsRows(), 2, 10, 10));
        buildingControls.setBorder(BorderFactory.createTitledBorder("Agregar Nuevo Edificio"));

        JTextField buildingIdField = new JTextField();
        JTextField buildingNameField = new JTextField();
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));

        JCheckBox chargeCenterCheck = new JCheckBox("Centro de Carga");
        JButton addBuildingBtn = new JButton("Agregar Edificio");
        JButton autoGenerateBtn = new JButton("Generar Edificios Automáticamente");

        // Configurar según el modo
        if (campusMode == CampusModeSelectionDialog.CampusConfigurationMode.CENTRO_CARGA_CENTRALIZADO) {
            chargeCenterCheck.setEnabled(false);
            chargeCenterCheck.setToolTipText("En modo centralizado, solo el centro principal tiene carga");
        }

        buildingControls.add(new JLabel("ID del Edificio*:"));
        buildingControls.add(buildingIdField);
        buildingControls.add(new JLabel("Nombre del Edificio*:"));
        buildingControls.add(buildingNameField);
        buildingControls.add(new JLabel("Capacidad de Vehículos:"));
        buildingControls.add(capacitySpinner);
        buildingControls.add(addBuildingBtn);
        buildingControls.add(autoGenerateBtn);


        JPanel tableControls = new JPanel(new FlowLayout());
        JButton refreshTableBtn = new JButton("Actualizar Tabla");
        JButton deleteBuildingBtn = new JButton("Eliminar Edificio Seleccionado");
        JButton clearAllBtn = new JButton("Eliminar Todos los Edificios");

        deleteBuildingBtn.setBackground(new Color(220, 80, 80));
        deleteBuildingBtn.setForeground(Color.WHITE);
        clearAllBtn.setBackground(new Color(180, 60, 60));
        clearAllBtn.setForeground(Color.WHITE);

        tableControls.add(refreshTableBtn);
        tableControls.add(deleteBuildingBtn);
        tableControls.add(clearAllBtn);

        topPanel.add(buildingControls, BorderLayout.NORTH);
        topPanel.add(tableControls, BorderLayout.SOUTH);

        // Tabla de edificios
        String[] columns = getBuildingTableColumns();
        buildingsModel = new DefaultTableModel(columns, 0);
        JTable buildingsTable = new JTable(buildingsModel);
        JScrollPane buildingsScroll = new JScrollPane(buildingsTable);

        addBuildingBtn.addActionListener(e -> {
            String id = buildingIdField.getText().trim();
            String nombre = buildingNameField.getText().trim();
            int capacidad = (Integer) capacitySpinner.getValue();
            boolean esCentroCarga = chargeCenterCheck.isSelected();

            if (id.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(configDialog,
                        "ID y Nombre son campos obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (campus.existeEdificio(id)) {
                JOptionPane.showMessageDialog(configDialog,
                        "Ya existe un edificio con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // En modo centralizado, forzar que no sea centro de carga
            if (campusMode == CampusModeSelectionDialog.CampusConfigurationMode.CENTRO_CARGA_CENTRALIZADO) {
                esCentroCarga = false;
            }

            try {
                boolean exito = campus.agregarEdificio(id, nombre, capacidad, esCentroCarga);

                if (exito) {
                    // Agregar a la tabla
                    agregarEdificioATabla(id, nombre, capacidad, esCentroCarga);

                    // Actualizar combo box de centro principal
                    actualizarComboBoxCentroPrincipal();

                    // Limpiar formulario
                    buildingIdField.setText("");
                    buildingNameField.setText("");
                    capacitySpinner.setValue(5);
                    chargeCenterCheck.setSelected(false);

                    JOptionPane.showMessageDialog(configDialog,
                            "Edificio '" + nombre + "' agregado exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(configDialog,
                        "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshTableBtn.addActionListener(e -> {
            actualizarTablaCompleta();
            JOptionPane.showMessageDialog(configDialog,
                    "Tabla actualizada correctamente",
                    "Actualización", JOptionPane.INFORMATION_MESSAGE);
        });

        // NUEVO: Listener para ELIMINAR EDIFICIO SELECCIONADO
        deleteBuildingBtn.addActionListener(e -> {
            eliminarEdificioSeleccionado(buildingsTable);
        });

        // NUEVO: Listener para ELIMINAR TODOS LOS EDIFICIOS
        clearAllBtn.addActionListener(e -> {
            eliminarTodosLosEdificios();
        });

        // Listener para generar edificios automáticamente
        autoGenerateBtn.addActionListener(e -> {
            generarEdificiosAutomaticos();
            // NUEVO: Actualizar combo box después de generar
            actualizarComboBoxCentroPrincipal();
        });

        buildingsPanel.add(topPanel, BorderLayout.NORTH);
        buildingsPanel.add(buildingsScroll, BorderLayout.CENTER);

        return buildingsPanel;
    }

    private void agregarEdificioATabla(String id, String nombre, int capacidad, boolean esCentroCarga) {
        Object[] fila;

        if (campusMode == CampusModeSelectionDialog.CampusConfigurationMode.CENTRO_CARGA_CENTRALIZADO) {
            boolean esCentroPrincipal = campus.getCentroPrincipal() != null &&
                    campus.getCentroPrincipal().getId().equals(id);

            fila = new Object[]{
                    id,
                    nombre,
                    capacidad,
                    esCentroPrincipal ? "Sí" : "No",
                    esCentroPrincipal ? "Centro Principal" : "Punto de Entrega"
            };
        } else {
            fila = new Object[]{
                    id,
                    nombre,
                    capacidad,
                    esCentroCarga ? "Sí" : "No",
                    "0"
            };
        }

        buildingsModel.addRow(fila);
    }

    private void eliminarTodosLosEdificios() {
        if (campus.getEdificios().isEmpty()) {
            JOptionPane.showMessageDialog(configDialog,
                    "No hay edificios para eliminar",
                    "Tabla Vacía", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int totalEdificios = campus.getEdificios().size();
        int totalRutas = campus.getRutas().size();

        // Confirmación MUY EXPLÍCITA para esta acción destructiva
        JPanel confirmPanel = new JPanel(new BorderLayout());
        JLabel warningLabel = new JLabel(
                "<html><b style='color:red; font-size:14px;'>ADVERTENCIA: ACCIÓN IRREVERSIBLE</b><br><br>" +
                        "Está a punto de eliminar <b>TODOS</b> los edificios del campus:<br><br>" +
                        "• Edificios a eliminar: <b>" + totalEdificios + "</b><br>" +
                        "• Rutas a eliminar: <b>" + totalRutas + "</b><br><br>" +
                        "¿Está absolutamente seguro?</html>");

        JCheckBox confirmCheck = new JCheckBox("Sí, entiendo que esta acción no se puede deshacer");

        confirmPanel.add(warningLabel, BorderLayout.NORTH);
        confirmPanel.add(confirmCheck, BorderLayout.SOUTH);

        int confirm = JOptionPane.showConfirmDialog(configDialog,
                confirmPanel,
                "ELIMINAR TODOS LOS EDIFICIOS",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION && confirmCheck.isSelected()) {
            try {
                // Limpiar todo el campus
                campus.limpiarCampus();

                // Limpiar la tabla
                buildingsModel.setRowCount(0);

                // Actualizar combo box
                actualizarComboBoxCentroPrincipal();

                JOptionPane.showMessageDialog(configDialog,
                        "Se eliminaron todos los edificios exitosamente:\n\n" +
                                "• Edificios eliminados: " + totalEdificios + "\n" +
                                "• Rutas eliminadas: " + totalRutas + "\n\n" +
                                "El campus ha sido reiniciado completamente.",
                        "Eliminación Completa", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(configDialog,
                        "Error al eliminar todos los edificios: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (confirm == JOptionPane.YES_OPTION && !confirmCheck.isSelected()) {
            JOptionPane.showMessageDialog(configDialog,
                    "Debe marcar la casilla de confirmación para proceder",
                    "Confirmación Requerida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarEdificioSeleccionado(JTable buildingsTable) {
        int selectedRow = buildingsTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(configDialog,
                    "Por favor, seleccione un edificio de la tabla para eliminar",
                    "Ningún Edificio Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener información del edificio seleccionado
        String id = (String) buildingsModel.getValueAt(selectedRow, 0);
        String nombre = (String) buildingsModel.getValueAt(selectedRow, 1);

        // Confirmación de eliminación
        int confirm = JOptionPane.showConfirmDialog(configDialog,
                "¿Está seguro de eliminar el siguiente edificio?\n\n" +
                        "ID: " + id + "\n" +
                        "Nombre: " + nombre + "\n\n" +
                        "Esta acción también eliminará todas las rutas conectadas a este edificio.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Verificar si es el centro principal
                boolean eraCentroPrincipal = campus.getCentroPrincipal() != null &&
                        campus.getCentroPrincipal().getId().equals(id);

                // Eliminar del campus
                boolean exito = campus.eliminarEdificio(id);

                if (exito) {
                    // Eliminar de la tabla
                    buildingsModel.removeRow(selectedRow);

                    // Actualizar combo box
                    actualizarComboBoxCentroPrincipal();

                    // Mensaje informativo
                    String mensaje = "Edificio '" + nombre + "' eliminado exitosamente";
                    if (eraCentroPrincipal) {
                        mensaje += "\nEste edificio era el centro principal. Debe asignar un nuevo centro principal.";
                    }

                    JOptionPane.showMessageDialog(configDialog,
                            mensaje,
                            "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);

                } else {
                    JOptionPane.showMessageDialog(configDialog,
                            "Error al eliminar el edificio",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(configDialog,
                        "Error al eliminar el edificio: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void generarEdificiosAutomaticos() {
        String[] edificiosBase = {
                "A,Edificio de Ciencias",
                "B,Edificio de Ingeniería",
                "C,Centro Principal",
                "D,Edificio de Artes",
                "E,Biblioteca Central",
                "F,Edificio Administrativo"
        };

        int edificiosCreados = 0;

        for (String edificioInfo : edificiosBase) {
            String[] partes = edificioInfo.split(",");
            String id = partes[0];
            String nombre = partes[1];
            int capacidad = 5 + (int)(Math.random() * 10); // Capacidad entre 5-15

            boolean esCentroCarga = false;
            if (campusMode == CampusModeSelectionDialog.CampusConfigurationMode.CENTRO_CARGA_CENTRALIZADO) {
                esCentroCarga = false;
            } else {
                esCentroCarga = true;
            }

            if (!campus.existeEdificio(id)) {
                campus.agregarEdificio(id, nombre, capacidad, esCentroCarga);
                agregarEdificioATabla(id, nombre, capacidad, esCentroCarga);
                edificiosCreados++;
            }
        }

        actualizarComboBoxCentroPrincipal();

        JOptionPane.showMessageDialog(configDialog,
                "Se generaron " + edificiosCreados + " edificios automáticamente\n" +
                        "Lista actualizada en el selector de centro principal",
                "Generación Automática", JOptionPane.INFORMATION_MESSAGE);
    }

    private void configureAllBuildingsAsChargeCenters(boolean allChargeCenters, int baseCapacity) {
        List<Edificio> listaE = campus.getEdificios();
        for (Edificio edificio : listaE) {
            edificio.setTieneCentroCarga(allChargeCenters);
            edificio.setCapacidadVehiculos(baseCapacity);
        }
        actualizarTablaCompleta();
    }

    private void configureAsMainChargeCenter(String buildingId, int capacity) {
        Edificio edificio = campus.getEdificio(buildingId);
        if (edificio != null) {
            edificio.setTieneCentroCarga(true);
            edificio.setCapacidadVehiculos(capacity);
            campus.setCentroPrincipal(edificio);

            // Actualizar la tabla para reflejar el cambio
            actualizarTablaEdificios();

            JOptionPane.showMessageDialog(configDialog,
                    "Centro principal configurado: " + buildingId, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // NUEVO: Método para actualizar toda la tabla de edificios
    private void actualizarTablaEdificios() {
        buildingsModel.setRowCount(0); // Limpiar tabla

        for (Edificio edificio : campus.getEdificios()) {
            agregarEdificioATabla(
                    edificio.getId(),
                    edificio.getNombre(),
                    edificio.getCapacidadVehiculos(),
                    edificio.isTieneCentroCarga()
            );
        }
    }

    private int getBuildingControlsRows() {
        return campusMode == CampusModeSelectionDialog.CampusConfigurationMode.CENTROS_CARGA_DISTRIBUIDOS ? 5 : 4;
    }

    private String[] getBuildingTableColumns() {
        if (campusMode == CampusModeSelectionDialog.CampusConfigurationMode.CENTRO_CARGA_CENTRALIZADO) {
            return new String[]{"ID", "Nombre", "Capacidad", "Es Centro Principal", "Tipo"};
        } else {
            return new String[]{"ID", "Nombre", "Capacidad", "Centro de Carga", "Vehículos Actuales"};
        }
    }

    // Los métodos createRoutesPanel(), createSummaryPanel() y demás se mantienen similares
    // pero adaptados para cada modo...


    private String getResumenConfiguracion() {
        int totalEdificios = campus.getEdificios().size();
        int centrosCarga = campus.getCentrosCarga().size();
        int capacidadTotal = campus.getEdificios().stream()
                .mapToInt(Edificio::getCapacidadVehiculos)
                .sum();

        String centroPrincipalInfo = "Distribuido entre todos los edificios";
        if (campus.getCentroPrincipal() != null) {
            centroPrincipalInfo = campus.getCentroPrincipal().getNombre() +
                    " (" + campus.getCentroPrincipal().getId() + ")";
        }

        return "RESUMEN DE CONFIGURACIÓN:\n\n" +
                "• Modo: " + getModeTitle() + "\n" +
                "• Total edificios: " + totalEdificios + "\n" +
                "• Centros de carga: " + centrosCarga + "\n" +
                "• Capacidad total: " + capacidadTotal + " vehículos\n" +
                "• Centro principal: " + centroPrincipalInfo;
    }

    private void createFooter() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        footerPanel.setBackground(Color.LIGHT_GRAY);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveConfigBtn = new JButton("Guardar Configuración");
        JButton completeConfigBtn = new JButton("Completar Configuración");
        JButton changeModeBtn = new JButton("Cambiar Modo");

        buttonPanel.add(saveConfigBtn);
        buttonPanel.add(completeConfigBtn);
        buttonPanel.add(changeModeBtn);

        changeModeBtn.addActionListener(e -> {
            configDialog.dispose();
            new CampusModeSelectionDialog(null);
        });

        completeConfigBtn.addActionListener(e -> {
            if (validateConfiguration()) {
                int confirm = JOptionPane.showConfirmDialog(configDialog,
                        getResumenConfiguracion() +
                                "\n\n¿Desea completar la configuración con estos datos?",
                        "Confirmar Configuración", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Guardar el campus en una variable estática o pasarlo de alguna manera
                    DeliverySystemGUI.campusConfigurado = this.campus; // Ejemplo

                    configDialog.dispose();

                    JOptionPane.showMessageDialog(null,
                            "Configuración completada exitosamente!\n\n" +
                                        "Puedes continuar con el sistema principal.",
                            "Configuración Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        footerPanel.add(buttonPanel, BorderLayout.EAST);

        configDialog.add(footerPanel, BorderLayout.SOUTH);
    }

    private boolean validateConfiguration() {
        // Validaciones específicas por modo
        switch (campusMode) {
            case CENTRO_CARGA_CENTRALIZADO:
                return validateCentralizedConfiguration();
            case CENTROS_CARGA_DISTRIBUIDOS:
                return validateDistributedConfiguration();
            default:
                return false;
        }
    }

    private boolean validateCentralizedConfiguration() {
        // Validar que hay un centro principal definido
        // y al menos 2 edificios totales
        return true; // Implementar validación real
    }

    private boolean validateDistributedConfiguration() {
        // Validar que hay al menos 2 edificios con centros de carga
        // y capacidades definidas
        return true; // Implementar validación real
    }
}