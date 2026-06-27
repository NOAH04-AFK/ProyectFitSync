package ec.edu.udla.fitsyncpro.UI;

import ec.edu.udla.fitsyncpro.controllers.GestorPerformance;
import ec.edu.udla.fitsyncpro.controllers.GestorSocios;
import ec.edu.udla.fitsyncpro.estructuras.NodoAVL;
import ec.edu.udla.fitsyncpro.models.Recompensa;
import ec.edu.udla.fitsyncpro.models.RegistroPerformance;
import ec.edu.udla.fitsyncpro.models.Socio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Módulo 4 – Monitoreo de Performance y Gamificación (interfaz gráfica).
 *
 * Muestra el ranking de constancia obtenido del recorrido in-order inverso
 * del Árbol AVL, la bitácora histórica, los logros y las alertas de retención.
 */
public class PanelPerformance {

    private final GestorPerformance gestor;
    private final GestorSocios gestorSocios;
    private final JPanel panel;

    private JComboBox<Socio> cmbSocio;
    private JTextField txtEjercicio, txtKg;
    private DefaultTableModel modeloRanking;
    private JTable tablaRanking;
    private JLabel lblArbol;
    private JTextArea txtProgreso;
    private JTextArea txtRecompensas;
    private DefaultListModel<RegistroPerformance> modeloBitacora;

    public PanelPerformance(GestorPerformance gestorCompartido, GestorSocios gestorSocios) {
        this.gestor       = gestorCompartido;
        this.gestorSocios = gestorSocios;
        this.panel        = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        construirNorte();
        construirCentro();
        construirSur();

        refrescarTodo();
    }

    // ── Registro de actividad (CREATE) ────────────────────────────────────────
    /** Arma la zona superior (seleccion de socio y registro de asistencia/carga). */
    private void construirNorte() {
        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        norte.setBorder(BorderFactory.createTitledBorder("Registrar actividad del socio"));

        cmbSocio = new JComboBox<>();
        JButton btnAsistencia = new JButton("Registrar asistencia de hoy (+10 pts)");
        txtEjercicio = new JTextField(12);
        txtKg        = new JTextField(5);
        JButton btnCarga = new JButton("Registrar carga");

        norte.add(new JLabel("Socio:"));
        norte.add(cmbSocio);
        norte.add(btnAsistencia);
        norte.add(new JLabel("   Ejercicio:"));
        norte.add(txtEjercicio);
        norte.add(new JLabel("Kg:"));
        norte.add(txtKg);
        norte.add(btnCarga);
        panel.add(norte, BorderLayout.NORTH);

        btnAsistencia.addActionListener(e -> {
            Socio s = (Socio) cmbSocio.getSelectedItem();
            if (s == null) return;
            String msj = gestor.registrarAsistenciaHoy(s.getIdUsuario());
            refrescarTodo();
            JOptionPane.showMessageDialog(panel, msj);
        });

        btnCarga.addActionListener(e -> {
            Socio s = (Socio) cmbSocio.getSelectedItem();
            if (s == null) return;
            try {
                double kg = Double.parseDouble(txtKg.getText().trim());
                String msj = gestor.registrarCargaHoy(s.getIdUsuario(), txtEjercicio.getText(), kg);
                txtEjercicio.setText("");
                txtKg.setText("");
                refrescarTodo();
                JOptionPane.showMessageDialog(panel, msj);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Ingrese un número válido en Kg.");
            }
        });

        cmbSocio.addActionListener(e -> refrescarDetalleSocio());
    }

    // ── Ranking AVL + detalle del socio ──────────────────────────────────────
    /** Arma la zona central (ranking del Arbol AVL, bitacora, logros). */
    private void construirCentro() {
        // Izquierda: ranking (recorrido in-order inverso del AVL)
        modeloRanking = new DefaultTableModel(new String[]{"Pos", "ID", "Socio", "Puntos"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaRanking = new JTable(modeloRanking);
        lblArbol = new JLabel(" ");
        JPanel izq = new JPanel(new BorderLayout(4, 4));
        izq.setBorder(BorderFactory.createTitledBorder("Ranking de constancia (Árbol AVL, in-order descendente)"));
        izq.add(new JScrollPane(tablaRanking), BorderLayout.CENTER);
        izq.add(lblArbol, BorderLayout.SOUTH);

        // Derecha: progreso, logros y bitácora del socio seleccionado
        txtProgreso = new JTextArea(8, 28);
        txtProgreso.setEditable(false);
        txtRecompensas = new JTextArea(8, 28);
        txtRecompensas.setEditable(false);
        modeloBitacora = new DefaultListModel<>();

        JTabbedPane tabsDetalle = new JTabbedPane();
        tabsDetalle.addTab("Progreso", new JScrollPane(txtProgreso));
        tabsDetalle.addTab("Recompensas", new JScrollPane(txtRecompensas));
        tabsDetalle.addTab("Bitácora (ArrayList)", new JScrollPane(new JList<>(modeloBitacora)));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, izq, tabsDetalle);
        split.setResizeWeight(0.55);
        panel.add(split, BorderLayout.CENTER);
    }

    // ── Acciones (READ alertas / DELETE lógico del ranking) ──────────────────
    /** Arma la zona inferior (alertas de inactividad y manejo del ranking). */
    private void construirSur() {
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAlertas    = new JButton("Ver alertas de inactividad");
        JButton btnRemover    = new JButton("Remover del ranking (baja lógica)");
        JButton btnReintegrar = new JButton("Reincorporar al ranking");
        JButton btnRefrescar  = new JButton("Refrescar");
        sur.add(btnAlertas);
        sur.add(btnRemover);
        sur.add(btnReintegrar);
        sur.add(btnRefrescar);
        panel.add(sur, BorderLayout.SOUTH);

        btnAlertas.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            for (String alerta : gestor.obtenerAlertasInactividad()) {
                sb.append("⚠ ").append(alerta).append("\n");
            }
            JOptionPane.showMessageDialog(panel,
                    sb.length() == 0 ? "Sin alertas: todos los socios están activos." : sb.toString(),
                    "Protocolo de retención", JOptionPane.INFORMATION_MESSAGE);
        });

        btnRemover.addActionListener(e -> {
            Socio s = (Socio) cmbSocio.getSelectedItem();
            if (s == null) return;
            if (gestor.removerDelRanking(s.getIdUsuario())) {
                refrescarTodo();
                JOptionPane.showMessageDialog(panel,
                        s.getNombre() + " fue removido de la tabla activa (su historial se conserva).");
            } else {
                JOptionPane.showMessageDialog(panel, "El socio no está en la tabla activa.");
            }
        });

        btnReintegrar.addActionListener(e -> {
            Socio s = (Socio) cmbSocio.getSelectedItem();
            if (s == null) return;
            gestor.incorporarSocioNuevo(s.getIdUsuario());   // por si es un socio nuevo
            if (gestor.reincorporarAlRanking(s.getIdUsuario())) {
                refrescarTodo();
            } else {
                JOptionPane.showMessageDialog(panel, "El socio ya está en la tabla activa.");
            }
        });

        btnRefrescar.addActionListener(e -> {
            refrescarCombo();
            refrescarTodo();
        });
    }

    // ── Refrescos ─────────────────────────────────────────────────────────────
    private void refrescarCombo() {
        Socio sel = (Socio) cmbSocio.getSelectedItem();
        cmbSocio.removeAllItems();
        for (Socio s : gestorSocios.obtenerSociosActivos()) {
            gestor.incorporarSocioNuevo(s.getIdUsuario());   // socios nuevos entran al árbol
            cmbSocio.addItem(s);
        }
        if (sel != null) cmbSocio.setSelectedItem(sel);
    }

    /** Recorre el Arbol AVL en orden inverso (mayor a menor puntaje) y llena la tabla del ranking. */
    private void refrescarRanking() {
        modeloRanking.setRowCount(0);
        int pos = 1;
        for (NodoAVL nodo : gestor.obtenerRanking()) {
            modeloRanking.addRow(new Object[]{
                    pos++, nodo.getSocio().getIdUsuario(),
                    nodo.getSocio().getNombre(), nodo.getPuntaje()
            });
        }
        lblArbol.setText(gestor.infoArbol());
    }

    /** Muestra el progreso, la bitacora y los logros del socio seleccionado. */
    private void refrescarDetalleSocio() {
        Socio s = (Socio) cmbSocio.getSelectedItem();
        txtProgreso.setText("");
        txtRecompensas.setText("");
        modeloBitacora.clear();
        if (s == null) return;
        txtProgreso.setText(gestor.evaluarProgreso(s.getIdUsuario()));
        txtRecompensas.setText(construirTextoRecompensas(s));
        for (RegistroPerformance r : gestor.obtenerBitacora(s.getIdUsuario())) modeloBitacora.addElement(r);
    }

    /** Texto con TODAS las recompensas que el socio puede ganar, con sus puntos y su estado. */
    private String construirTextoRecompensas(Socio s) {
        int pts = gestor.getPuntaje(s.getIdUsuario());
        java.util.List<Recompensa> obtenidas = gestor.obtenerLogros(s.getIdUsuario());
        StringBuilder sb = new StringBuilder();
        sb.append("Puntos de ").append(s.getNombre()).append(": ").append(pts).append(" pts\n");
        sb.append("Cómo sumar: asistencia +").append(GestorPerformance.PTS_ASISTENCIA)
          .append(" · carga +").append(GestorPerformance.PTS_CARGA)
          .append(" · récord de carga +").append(GestorPerformance.PTS_SOBRECARGA).append(" extra\n\n");
        sb.append("RECOMPENSAS QUE PUEDES GANAR\n");
        sb.append("──────────────────────────────\n");
        for (Recompensa r : gestor.getRecompensasConfiguradas()) {
            boolean lograda = obtenidas.contains(r);
            sb.append(lograda ? "✔ " : "•  ").append(r.getDescripcion()).append("\n");
            sb.append("     Requiere ").append(r.getPuntosRequeridos()).append(" pts  →  ");
            if (lograda)                              sb.append("OBTENIDA");
            else if (pts >= r.getPuntosRequeridos())  sb.append("¡lista para canjear!");
            else                                      sb.append("te faltan ").append(r.getPuntosRequeridos() - pts).append(" pts");
            sb.append("\n\n");
        }
        return sb.toString();
    }

    /** Refresca todas las vistas (lo invoca la VentanaPrincipal al cambiar de pestaña) */
    /** Recarga todo (incorpora socios nuevos al arbol y refresca el ranking). */
    public void refrescarTodo() {
        refrescarCombo();
        refrescarRanking();
        refrescarDetalleSocio();
    }

    public JPanel getPanel() {
        return panel;
    }
}
