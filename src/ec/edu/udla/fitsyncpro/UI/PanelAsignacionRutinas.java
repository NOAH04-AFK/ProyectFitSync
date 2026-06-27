package ec.edu.udla.fitsyncpro.UI;

import ec.edu.udla.fitsyncpro.controllers.GestorEvolucionFisica;
import ec.edu.udla.fitsyncpro.controllers.GestorRutinas;
import ec.edu.udla.fitsyncpro.controllers.GestorSocios;
import ec.edu.udla.fitsyncpro.models.Ejercicio;
import ec.edu.udla.fitsyncpro.models.PlanificacionDiaria;
import ec.edu.udla.fitsyncpro.models.Rutina;
import ec.edu.udla.fitsyncpro.models.Socio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Módulo 2 – Asignación de Planes (vista del entrenador, versión .form).
 * Vincula las rutinas maestras al plan de cada socio (PlanificacionDiaria),
 * permite personalizar el día seleccionado y aplica el filtro preventivo
 * de salud del Módulo 3 antes de asignar.
 */
public class PanelAsignacionRutinas {
    // --- Contenedor Principal ---
    private JPanel panelPrincipal;

    // --- Controles Superiores (Asignación General) ---
    private JComboBox cmbSocio;
    private JComboBox cmbDia;
    private JButton btnAsignarSocio;
    private JButton btnAsignarSemana;
    private JLabel lblDisponible;

    // --- Tabla Central (Plan Semanal) ---
    private JTable tablaPlanSemanal;

    // --- Controles Inferiores (Personalización) ---
    private JList listaRutinaPersonalizada;
    private JComboBox cmbCatalogo;
    private JButton btnAgregarEjercicio;
    private JButton btnQuitarEjercicio;

    // --- Botones Footer ---
    private JButton btnQuitarAsignacion;
    private JButton btnRefrescar;

    public static final String[] DIAS_SEMANA =
            {"LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO"};

    private final GestorRutinas gestorRutinas;
    private final GestorSocios gestorSocios;
    private final GestorEvolucionFisica gestorEvolucion;

    private DefaultTableModel modeloTabla;
    private DefaultListModel<Ejercicio> modeloListaPersonalizada;

    public PanelAsignacionRutinas(GestorRutinas gestorRutinas, GestorSocios gestorSocios, GestorEvolucionFisica gestorEvolucion) {
        this.gestorRutinas = gestorRutinas;
        this.gestorSocios = gestorSocios;
        this.gestorEvolucion = gestorEvolucion;

        configurarModelos();
        configurarListeners();
        refrescarTodo();
    }

    private void configurarModelos() {
        modeloTabla = new DefaultTableModel(new String[]{"Día", "Objetivo", "Nivel", "Ejercicios", "Estado"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaPlanSemanal.setModel(modeloTabla);
        tablaPlanSemanal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        modeloListaPersonalizada = new DefaultListModel<>();
        listaRutinaPersonalizada.setModel(modeloListaPersonalizada);
        listaRutinaPersonalizada.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    private void configurarListeners() {
        cmbSocio.addActionListener(e -> refrescarPlanSemanal());
        cmbDia.addActionListener(e -> refrescarInfoDisponible());
        tablaPlanSemanal.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) refrescarDetalleDia();
        });

        // ── Asignar la rutina del día al socio ──
        btnAsignarSocio.addActionListener(e -> {
            Socio s = (Socio) cmbSocio.getSelectedItem();
            if (s == null) { aviso("Seleccione un socio."); return; }
            String dia = (String) cmbDia.getSelectedItem();

            if (gestorRutinas.obtenerRutinaDeSocio(s.getIdUsuario(), dia) != null) {
                int ok = JOptionPane.showConfirmDialog(panelPrincipal,
                        "El socio ya tiene una rutina para el " + dia + " (con posibles cambios personalizados).\n"
                                + "Reasignar la reemplazará por una copia limpia de la plantilla.\n\n¿Continuar?",
                        "Reemplazar rutina existente", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (ok != JOptionPane.YES_OPTION) return;
            }
            if (!pasaFiltroDeSalud(s)) return;   // filtro preventivo del Módulo 3

            if (gestorRutinas.asignarRutinaASocio(s.getIdUsuario(), dia)) {
                refrescarPlanSemanal();
                seleccionarDiaEnTabla(dia);
                Rutina r = gestorRutinas.obtenerRutinaDeSocio(s.getIdUsuario(), dia);
                JOptionPane.showMessageDialog(panelPrincipal,
                        "Rutina \"" + r.getObjetivo() + "\" asignada a " + s.getNombre() + " para el " + dia + ".");
            } else {
                JOptionPane.showMessageDialog(panelPrincipal,
                        "No hay rutina ACTIVA guardada para el " + dia + ".\nCréela primero en el Creador de Rutinas.",
                        "Sin rutina disponible", JOptionPane.WARNING_MESSAGE);
            }
        });

        // ── Asignar la semana completa ──
        btnAsignarSemana.addActionListener(e -> {
            Socio s = (Socio) cmbSocio.getSelectedItem();
            if (s == null) { aviso("Seleccione un socio."); return; }
            int conf = JOptionPane.showConfirmDialog(panelPrincipal,
                    "¿Asignar todas las rutinas de la semana a " + s.getNombre() + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf != JOptionPane.YES_OPTION) return;
            if (!pasaFiltroDeSalud(s)) return;

            int n = gestorRutinas.asignarSemanaCompleta(s.getIdUsuario());
            refrescarPlanSemanal();
            JOptionPane.showMessageDialog(panelPrincipal, (n == 0)
                    ? "No hay rutinas activas guardadas en la semana."
                    : "Se vincularon " + n + " rutina(s) al plan de " + s.getNombre() + ".");
        });

        // ── Personalización: agregar / quitar ejercicio del día seleccionado ──
        btnAgregarEjercicio.addActionListener(e -> {
            Socio s = (Socio) cmbSocio.getSelectedItem();
            String dia = diaSeleccionadoEnTabla();
            Ejercicio ej = (Ejercicio) cmbCatalogo.getSelectedItem();
            if (s == null || dia == null) { aviso("Seleccione un socio y un día (una fila de la tabla)."); return; }
            if (ej == null) { aviso("Seleccione un ejercicio del catálogo."); return; }
            if (gestorRutinas.agregarEjercicioARutinaDeSocio(s.getIdUsuario(), dia, ej)) {
                refrescarPlanSemanal();
                seleccionarDiaEnTabla(dia);
            } else {
                aviso("El " + dia + " no tiene rutina asignada a este socio. Asígnela primero arriba.");
            }
        });

        btnQuitarEjercicio.addActionListener(e -> {
            Socio s = (Socio) cmbSocio.getSelectedItem();
            String dia = diaSeleccionadoEnTabla();
            int idx = listaRutinaPersonalizada.getSelectedIndex();
            if (s == null || dia == null) { aviso("Seleccione un día del plan."); return; }
            if (idx == -1) { aviso("Seleccione el ejercicio a quitar de la lista."); return; }
            if (gestorRutinas.quitarEjercicioDeRutinaDeSocio(s.getIdUsuario(), dia, idx)) {
                refrescarPlanSemanal();
                seleccionarDiaEnTabla(dia);
            }
        });

        // ── Footer: quitar asignación / refrescar ──
        btnQuitarAsignacion.addActionListener(e -> {
            Socio s = (Socio) cmbSocio.getSelectedItem();
            String dia = diaSeleccionadoEnTabla();
            if (s == null || dia == null) { aviso("Seleccione un socio y una fila de su plan."); return; }
            if (gestorRutinas.desasignarRutinaDeSocio(s.getIdUsuario(), dia)) {
                refrescarPlanSemanal();
            } else {
                aviso("Ese día no tenía rutina asignada.");
            }
        });

        btnRefrescar.addActionListener(e -> refrescarTodo());
    }

    /** Filtro preventivo de salud (Módulo 3): confirma si el socio tiene contraindicaciones. */
    private boolean pasaFiltroDeSalud(Socio s) {
        String alerta = gestorEvolucion.verificarContraindicacion(s.getIdUsuario());
        if (alerta == null) return true;
        int ok = JOptionPane.showConfirmDialog(panelPrincipal,
                alerta + "\n\n¿Confirmar la asignación de todos modos?",
                "Filtro preventivo de salud — Módulo 3", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return ok == JOptionPane.YES_OPTION;
    }

    // ── Refrescos ─────────────────────────────────────────────────────────────
    private void refrescarInfoDisponible() {
        String dia = (String) cmbDia.getSelectedItem();
        if (dia == null) return;
        Rutina r = gestorRutinas.obtenerRutinaPorDia(dia);
        if (r != null && r.isActiva()) {
            lblDisponible.setText("Disponible: " + r.getObjetivo() + " (" + r.getNivel().name()
                    + ", " + r.getEjercicios().size() + " ejercicios)");
        } else {
            lblDisponible.setText("Sin rutina activa para el " + dia);
        }
    }

    private void refrescarPlanSemanal() {
        modeloTabla.setRowCount(0);
        Socio s = (Socio) cmbSocio.getSelectedItem();
        if (s == null) return;
        PlanificacionDiaria plan = gestorRutinas.obtenerPlanDeSocio(s.getIdUsuario());
        for (String dia : DIAS_SEMANA) {
            Rutina r = (plan != null) ? plan.obtenerRutina(dia) : null;
            if (r == null) {
                modeloTabla.addRow(new Object[]{dia, "—", "—", "—", "Sin asignar"});
            } else {
                modeloTabla.addRow(new Object[]{dia, r.getObjetivo(), r.getNivel().name(),
                        r.getEjercicios().size(), r.isActiva() ? "ACTIVA" : "INACTIVA"});
            }
        }
    }

    private void refrescarDetalleDia() {
        if (modeloListaPersonalizada == null) return;
        modeloListaPersonalizada.clear();
        Socio s = (Socio) cmbSocio.getSelectedItem();
        String dia = diaSeleccionadoEnTabla();
        if (s == null || dia == null) return;
        Rutina r = gestorRutinas.obtenerRutinaDeSocio(s.getIdUsuario(), dia);
        if (r == null) return;
        for (Ejercicio ej : r.getEjercicios()) modeloListaPersonalizada.addElement(ej);
    }

    private void refrescarCombo() {
        Socio sel = (Socio) cmbSocio.getSelectedItem();
        cmbSocio.removeAllItems();
        for (Socio s : gestorSocios.obtenerSociosActivos()) cmbSocio.addItem(s);
        if (sel != null) cmbSocio.setSelectedItem(sel);
    }

    private void refrescarCatalogo() {
        if (cmbCatalogo == null) return;
        Ejercicio sel = (Ejercicio) cmbCatalogo.getSelectedItem();
        cmbCatalogo.removeAllItems();
        for (Ejercicio ej : gestorRutinas.obtenerCatalogo(null)) cmbCatalogo.addItem(ej);
        if (sel != null) cmbCatalogo.setSelectedItem(sel);
    }

    /** Refresca todas las vistas (lo invoca la VentanaPrincipal al cambiar de pestaña). */
    public void refrescarTodo() {
        refrescarCombo();
        refrescarCatalogo();
        refrescarInfoDisponible();
        refrescarPlanSemanal();
    }

    // ── Utilitarios ───────────────────────────────────────────────────────────
    private String diaSeleccionadoEnTabla() {
        int fila = tablaPlanSemanal.getSelectedRow();
        return fila == -1 ? null : modeloTabla.getValueAt(fila, 0).toString();
    }

    private void seleccionarDiaEnTabla(String dia) {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if (modeloTabla.getValueAt(i, 0).toString().equals(dia)) {
                tablaPlanSemanal.setRowSelectionInterval(i, i);
                return;
            }
        }
    }

    private void aviso(String msj) {
        JOptionPane.showMessageDialog(panelPrincipal, msj, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public JPanel getPanel() {
        return panelPrincipal;
    }
}