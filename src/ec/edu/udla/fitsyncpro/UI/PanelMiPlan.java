package ec.edu.udla.fitsyncpro.UI;

import ec.edu.udla.fitsyncpro.controllers.GestorRutinas;
import ec.edu.udla.fitsyncpro.controllers.GestorSocios;
import ec.edu.udla.fitsyncpro.models.Ejercicio;
import ec.edu.udla.fitsyncpro.models.Rutina;
import ec.edu.udla.fitsyncpro.models.Socio;
import ec.edu.udla.fitsyncpro.models.VideoTutorial;

import javax.swing.*;
import java.awt.Dimension;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Módulo 2 – "Mi Plan" (vista de consulta del socio, rol USUARIO).
 * Solo lectura: el socio ve la rutina que su entrenador le asignó por día,
 * con el videotutorial para ejecutarla de forma autónoma.
 */
public class PanelMiPlan {
    // --- Contenedor Principal ---
    private JPanel panelPrincipal;

    // --- Componentes Superiores ---
    private JComboBox cmbSocio;
    private JComboBox cmbDia;
    private JLabel lblResumen;

    // --- Listas y Textos ---
    private JList listaEjercicios;
    private JTextArea txtVideo;

    // --- Lógica ---
    private final GestorRutinas gestorRutinas;
    private final GestorSocios gestorSocios;
    private DefaultListModel<Ejercicio> modeloEjercicios;

    // Mismos días (con acento) que usa PanelAsignacionRutinas al asignar
    private static final String[] DIAS = {"LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO"};

    public PanelMiPlan(GestorRutinas gestorRutinas, GestorSocios gestorSocios) {
        this.gestorRutinas = gestorRutinas;
        this.gestorSocios = gestorSocios;

        // Tamaño visible de los combos (en FlowLayout salían diminutos)
        cmbDia.setPreferredSize(new Dimension(150, 28));
        cmbSocio.setPreferredSize(new Dimension(230, 28));

        modeloEjercicios = new DefaultListModel<>();
        listaEjercicios.setModel(modeloEjercicios);

        txtVideo.setEditable(false);
        txtVideo.setLineWrap(true);
        txtVideo.setWrapStyleWord(true);

        createListener();

        cmbDia.setSelectedItem(diaDeHoy());   // por defecto, el día actual
        refrescarTodo();
    }

    /** Configura los eventos: al cambiar de socio o de dia se refresca la rutina mostrada. */
    private void createListener() {
        cmbSocio.addActionListener(e -> refrescarRutina());
        cmbDia.addActionListener(e -> refrescarRutina());
        listaEjercicios.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            Ejercicio sel = (Ejercicio) listaEjercicios.getSelectedValue();
            if (sel == null) { txtVideo.setText(""); return; }
            VideoTutorial video = gestorRutinas.obtenerVideoPorEjercicio(sel.getIdEjercicio());
            txtVideo.setText(video != null
                    ? video.getDescripcion() + "\n" + video.getUrlYoutube()
                    : "No hay video disponible para este ejercicio.");
        });
    }

    /** Día actual en formato del sistema (LUNES, MARTES, ...). */
    private static String diaDeHoy() {
        Locale es = new Locale("es");
        return LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, es).toUpperCase(es);
    }

    // ── Refrescos ─────────────────────────────────────────────────────────────
    /** Muestra en pantalla la rutina que el entrenador asigno al socio para el dia elegido (solo lectura). */
    private void refrescarRutina() {
        modeloEjercicios.clear();
        txtVideo.setText("");
        Socio s = (Socio) cmbSocio.getSelectedItem();
        String dia = (String) cmbDia.getSelectedItem();
        if (s == null || dia == null) return;

        Rutina r = gestorRutinas.obtenerRutinaDeSocio(s.getIdUsuario(), dia);
        if (r == null) {
            lblResumen.setText("Sin plan asignado para el " + dia + " — consulta a tu entrenador.");
            return;
        }
        lblResumen.setText("Objetivo: " + r.getObjetivo() + " | Nivel: " + r.getNivel().name());
        for (Ejercicio ej : r.getEjercicios()) modeloEjercicios.addElement(ej);
    }

    /** Llena el combo con los socios activos. */
    private void refrescarCombo() {
        Socio sel = (Socio) cmbSocio.getSelectedItem();
        cmbSocio.removeAllItems();
        for (Socio s : gestorSocios.obtenerSociosActivos()) cmbSocio.addItem(s);
        if (sel != null) cmbSocio.setSelectedItem(sel);
    }

    /** Refresca todas las vistas (lo invoca la VentanaPrincipal al cambiar de pestaña). */
    /** Recarga todo el panel. Lo llama VentanaPrincipal cada vez que el socio entra a su pestaña. */
    public void refrescarTodo() {
        refrescarCombo();
        refrescarRutina();
    }

    public JPanel getPanel() {
        return panelPrincipal;
    }
}