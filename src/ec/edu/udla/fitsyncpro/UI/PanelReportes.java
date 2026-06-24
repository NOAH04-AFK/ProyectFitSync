package ec.edu.udla.fitsyncpro.UI;

import javax.swing.*;

public class PanelReportes {
    // --- Contenedor Principal ---
    private JPanel panelPrincipal;
    private JTabbedPane tabbedPane1;

    // --- Tab 1: Reporte Operativo ---
    private JButton btnGenerar;
    private JTextArea txtReporte;
    private JList listaHistorial;

    // --- Tab 2: Retención ---
    private JButton btnCalcular;
    private JTextArea txtRetencion;

    // --- Tab 3: Plantillas ---
    private JList listaPlantillas;
    private JCheckBox chkSoloActivas;
    private JButton btnNueva;
    private JButton btnInactivar;

    // --- Tab 4: Seguridad (RBAC) ---
    private JList listaCuentas;
    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private JComboBox cmbNivel;
    private JButton btnActualizar;

}
