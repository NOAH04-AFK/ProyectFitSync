package ec.edu.udla.fitsyncpro.UI;

import ec.edu.udla.fitsyncpro.controllers.GestorReportes;
import ec.edu.udla.fitsyncpro.models.Administrador;
import ec.edu.udla.fitsyncpro.models.PlantillaReporte;
import ec.edu.udla.fitsyncpro.models.ReporteOperativo;

import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Módulo 5 – Administración y Reportes de Gestión (interfaz gráfica, versión .form).
 * Pestañas: Reporte operativo · Retención · Plantillas · Seguridad (RBAC).
 */
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

    // --- Lógica ---
    private final GestorReportes gestor;
    private DefaultListModel<ReporteOperativo> modeloHistorial;
    private DefaultListModel<PlantillaReporte> modeloPlantillas;
    private DefaultListModel<Administrador> modeloCuentas;

    public PanelReportes(GestorReportes gestorCompartido) {
        this.gestor = gestorCompartido;

        // Áreas de reporte en monoespaciado (alinea columnas del texto)
        txtReporte.setEditable(false);
        txtReporte.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtRetencion.setEditable(false);
        txtRetencion.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        modeloHistorial = new DefaultListModel<>();
        modeloPlantillas = new DefaultListModel<>();
        modeloCuentas = new DefaultListModel<>();
        listaHistorial.setModel(modeloHistorial);
        listaPlantillas.setModel(modeloPlantillas);
        listaCuentas.setModel(modeloCuentas);

        chkSoloActivas.setSelected(true);
        cmbNivel.addItem("TOTAL");
        cmbNivel.addItem("CONSULTA");

        createListener();
        refrescarPlantillas();
        refrescarCuentas();
    }

    private void createListener() {

        // ---- Tab 1: Reporte Operativo ----
        btnGenerar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                ReporteOperativo r = gestor.generarReporteOperativo();
                txtReporte.setText(r.getContenido());
                txtReporte.setCaretPosition(0);
                refrescarHistorial();
            }
        });

        listaHistorial.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            ReporteOperativo r = (ReporteOperativo) listaHistorial.getSelectedValue();
            if (r != null) { txtReporte.setText(r.getContenido()); txtReporte.setCaretPosition(0); }
        });

        // ---- Tab 2: Retención ----
        btnCalcular.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                txtRetencion.setText(gestor.obtenerIndicadoresRetencion());
                txtRetencion.setCaretPosition(0);
            }
        });

        // ---- Tab 3: Plantillas ----
        chkSoloActivas.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { refrescarPlantillas(); }
        });

        btnNueva.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                JTextField nombre = new JTextField();
                JTextField desc = new JTextField();
                JComboBox<String> frec = new JComboBox<>(new String[]{"Diaria", "Semanal", "Mensual"});
                Object[] form = {"Nombre:", nombre, "Descripción:", desc, "Frecuencia:", frec};
                int ok = JOptionPane.showConfirmDialog(panelPrincipal, form,
                        "Nueva plantilla de reporte", JOptionPane.OK_CANCEL_OPTION);
                if (ok == JOptionPane.OK_OPTION && !nombre.getText().trim().isEmpty()) {
                    gestor.registrarPlantilla(new PlantillaReporte(
                            gestor.generarIdPlantilla(), nombre.getText().trim(),
                            desc.getText().trim(), (String) frec.getSelectedItem()));
                    refrescarPlantillas();
                }
            }
        });

        btnInactivar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                PlantillaReporte p = (PlantillaReporte) listaPlantillas.getSelectedValue();
                if (p == null) { aviso("Seleccione una plantilla."); return; }
                if (gestor.inactivarPlantilla(p.getIdPlantilla())) refrescarPlantillas();
                else aviso("La plantilla ya estaba inactiva.");
            }
        });

        // ---- Tab 4: Seguridad (RBAC) ----
        listaCuentas.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            Administrador a = (Administrador) listaCuentas.getSelectedValue();
            if (a != null) {
                txtUsuario.setText(a.getUsuarioLogin());
                cmbNivel.setSelectedItem(a.getNivelAcceso());
                txtClave.setText("");
            }
        });

        btnActualizar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                String usuario = txtUsuario.getText().trim();
                if (usuario.isEmpty()) { aviso("Seleccione una cuenta o escriba el usuario."); return; }
                String clave = new String(txtClave.getPassword());
                if (gestor.actualizarCredenciales(usuario, clave, (String) cmbNivel.getSelectedItem())) {
                    txtClave.setText("");
                    refrescarCuentas();
                    JOptionPane.showMessageDialog(panelPrincipal, "Credenciales actualizadas para \"" + usuario + "\".");
                } else {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "No existe una cuenta activa con ese usuario.", "RBAC", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    // ── Refrescos ─────────────────────────────────────────────────────────────
    /** Recarga la lista de reportes operativos generados. */
    private void refrescarHistorial() {
        modeloHistorial.clear();
        for (ReporteOperativo r : gestor.obtenerHistorialReportes()) modeloHistorial.addElement(r);
    }

    /** Recarga la lista de plantillas (respeta el filtro solo activas). */
    private void refrescarPlantillas() {
        modeloPlantillas.clear();
        for (PlantillaReporte p : gestor.obtenerPlantillas(chkSoloActivas.isSelected())) modeloPlantillas.addElement(p);
    }

    /** Recarga la lista de cuentas administrativas (RBAC). */
    private void refrescarCuentas() {
        modeloCuentas.clear();
        for (Administrador a : gestor.obtenerCuentas()) modeloCuentas.addElement(a);
    }

    private void aviso(String msj) {
        JOptionPane.showMessageDialog(panelPrincipal, msj, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public JPanel getPanel() {
        return panelPrincipal;
    }
}