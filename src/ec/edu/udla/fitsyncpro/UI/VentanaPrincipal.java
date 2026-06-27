package ec.edu.udla.fitsyncpro.UI;

import ec.edu.udla.fitsyncpro.controllers.GestorEvolucionFisica;
import ec.edu.udla.fitsyncpro.controllers.GestorPerformance;
import ec.edu.udla.fitsyncpro.controllers.GestorReportes;
import ec.edu.udla.fitsyncpro.controllers.GestorRutinas;
import ec.edu.udla.fitsyncpro.controllers.GestorSocios;
import ec.edu.udla.fitsyncpro.models.Administrador;
import ec.edu.udla.fitsyncpro.utils.TipoUsuario;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal unificada de FitSync Pro.
 *
 * Integra los módulos en pestañas y aplica control de acceso por TipoUsuario (RBAC):
 *  ─ ADMINISTRADOR : todos los módulos (requiere credenciales del Módulo 5)
 *  ─ ENTRENADOR    : socios, rutinas, asignación de planes y condición física
 *  ─ USUARIO       : su plan de entrenamiento (solo lectura)
 *
 * Todos los paneles comparten los MISMOS gestores, de modo que un socio
 * registrado en el Módulo 1 aparece en los módulos 2, 3, 4 y 5.
 */

public class VentanaPrincipal extends JFrame {

    // ── Gestores compartidos ──────────────────────────────────────────────────
    private final GestorEvolucionFisica gestorEvolucion;
    private final GestorSocios          gestorSocios;
    private final GestorRutinas         gestorRutinas;
    private final GestorPerformance     gestorPerformance;
    private final GestorReportes        gestorReportes;

    // ── Paneles de los módulos ────────────────────────────────────────────────
    private final PanelGestionSocios     panelSocios;
    private final PanelCreadorRutinas    panelRutinas;
    private final PanelAsignacionRutinas panelAsignacion;
    private final PanelMiPlan            panelMiPlan;
    private final PanelCondicionFisica   panelCondicion;
    private final PanelPerformance       panelPerformance;
    private final PanelReportes          panelReportes;

    private final JTabbedPane tabs;
    private TipoUsuario rolActual;

    public VentanaPrincipal() {
        super("FitSync Pro — Sistema de Gestión de Gimnasio");

        // El directorio de socios del Módulo 3 se comparte con los demás módulos
        gestorEvolucion   = new GestorEvolucionFisica();
        gestorSocios      = new GestorSocios(gestorEvolucion.getDirectorio());
        gestorRutinas     = new GestorRutinas();
        gestorPerformance = new GestorPerformance(gestorSocios);
        gestorReportes    = new GestorReportes(gestorSocios, gestorPerformance, gestorRutinas);

        panelSocios      = new PanelGestionSocios(gestorSocios);
        panelRutinas     = new PanelCreadorRutinas(gestorRutinas);
        panelAsignacion  = new PanelAsignacionRutinas(gestorRutinas, gestorSocios, gestorEvolucion);
        panelMiPlan      = new PanelMiPlan(gestorRutinas, gestorSocios);
        panelCondicion   = new PanelCondicionFisica(gestorEvolucion);
        panelPerformance = new PanelPerformance(gestorPerformance, gestorSocios);
        panelReportes    = new PanelReportes(gestorReportes);

        tabs = new JTabbedPane();
        tabs.addChangeListener(e -> refrescarPestanaActual());
        setContentPane(tabs);

        // Menú de sesión
        JMenuBar barra = new JMenuBar();
        JMenu menuSesion = new JMenu("Sesión");
        JMenuItem itemCambiar = new JMenuItem("Cambiar de usuario");
        JMenuItem itemSalir   = new JMenuItem("Salir");
        itemCambiar.addActionListener(e -> {
            setVisible(false);
            if (iniciarSesion()) setVisible(true);
            else System.exit(0);
        });
        itemSalir.addActionListener(e -> System.exit(0));
        JMenuItem itemSesionActual = new JMenuItem("Sesión actual");
        itemSesionActual.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Rol con sesión activa: " + (getRolActual() != null ? getRolActual().name() : "ninguno"),
                        "Sesión actual", JOptionPane.INFORMATION_MESSAGE));
        menuSesion.add(itemCambiar);
        menuSesion.add(itemSesionActual);
        menuSesion.add(itemSalir);
        barra.add(menuSesion);
        setJMenuBar(barra);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setLocationRelativeTo(null);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LOGIN – Control de acceso por TipoUsuario
    // ════════════════════════════════════════════════════════════════════════

    /** Muestra el diálogo de acceso. Devuelve false si el usuario cancela. */
    public boolean iniciarSesion() {
        JComboBox<TipoUsuario> cmbRol = new JComboBox<>(TipoUsuario.values());
        Object[] formulario = {
                "Seleccione su tipo de usuario:", cmbRol,
                " ", new JLabel("(El perfil ADMINISTRADOR solicita credenciales)")
        };
        int ok = JOptionPane.showConfirmDialog(null, formulario,
                "FitSync Pro — Inicio de sesión", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return false;

        TipoUsuario rol = (TipoUsuario) cmbRol.getSelectedItem();

        // El administrador valida sus credenciales contra el RBAC del Módulo 5
        if (rol == TipoUsuario.ADMINISTRADOR) {
            Administrador cuenta = pedirCredenciales();
            if (cuenta == null) return iniciarSesion();   // reintenta desde el inicio
            setTitle("FitSync Pro — " + cuenta.getNombre() + " [" + cuenta.getNivelAcceso() + "]");
        } else {
            setTitle("FitSync Pro — Sesión: " + rol.name());
        }

        rolActual = rol;
        construirPestanas(rol);
        return true;
    }

    private Administrador pedirCredenciales() {
        while (true) {
            JTextField txtUsuario   = new JTextField();
            JPasswordField txtClave = new JPasswordField();
            Object[] formulario = {"Usuario:", txtUsuario, "Clave:", txtClave};
            int ok = JOptionPane.showConfirmDialog(null, formulario,
                    "Credenciales de administrador", JOptionPane.OK_CANCEL_OPTION);
            if (ok != JOptionPane.OK_OPTION) return null;

            Administrador cuenta = gestorReportes.validarAcceso(
                    txtUsuario.getText().trim(), new String(txtClave.getPassword()));
            if (cuenta != null) return cuenta;

            JOptionPane.showMessageDialog(null, "Credenciales incorrectas.",
                    "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Arma las pestañas visibles según el rol (control de acceso). */
    private void construirPestanas(TipoUsuario rol) {
        tabs.removeAll();

        if (rol == TipoUsuario.ADMINISTRADOR || rol == TipoUsuario.ENTRENADOR) {
            tabs.addTab("Módulo 1 · Socios y Entrenadores", panelSocios.getPanel());
            tabs.addTab("Módulo 2 · Creador de Rutinas", panelRutinas.getPanel());
            tabs.addTab("Módulo 2 · Asignación de Planes", panelAsignacion.getPanel());
            tabs.addTab("Módulo 3 · Condición Física", panelCondicion.panel);
        }
        // RBAC: la Gestión de Equipos (infraestructura) es exclusiva del administrador
        panelRutinas.serGestionEquipoValidos(rol == TipoUsuario.ADMINISTRADOR);

        if (rol == TipoUsuario.USUARIO) {
            tabs.addTab("Mi Plan de Entrenamiento", panelMiPlan.getPanel());
        }
        // Módulo 4 (Performance/Ranking) y Módulo 5 (Reportes) son exclusivos del administrador
        if (rol == TipoUsuario.ADMINISTRADOR) {
            tabs.addTab("Módulo 4 · Performance y Ranking", panelPerformance.getPanel());
            tabs.addTab("Módulo 5 · Administración y Reportes", panelReportes.getPanel());
        }
    }

    /** Al cambiar de pestaña se refrescan los datos compartidos entre módulos. */
    private void refrescarPestanaActual() {
        Component sel = tabs.getSelectedComponent();
        if (sel == null) return;
        if (sel == panelSocios.getPanel())      panelSocios.refrescarTodo();
        if (sel == panelAsignacion.getPanel())  panelAsignacion.refrescarTodo();
        if (sel == panelMiPlan.getPanel())      panelMiPlan.refrescarTodo();
        if (sel == panelPerformance.getPanel()) panelPerformance.refrescarTodo();
    }

    public TipoUsuario getRolActual() {
        return rolActual;
    }

    // ── Punto de entrada de la aplicación ─────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal v = new VentanaPrincipal();
            if (v.iniciarSesion()) v.setVisible(true);
            else System.exit(0);
        });
    }
}
