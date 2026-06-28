package ec.edu.udla.fitsyncpro.UI;

import ec.edu.udla.fitsyncpro.controllers.GestorSocios;
import ec.edu.udla.fitsyncpro.models.Entrenador;
import ec.edu.udla.fitsyncpro.models.Socio;
import ec.edu.udla.fitsyncpro.utils.TipoMembresia;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Módulo 1 – Gestión de Socios y Entrenadores (interfaz gráfica, versión .form).
 * Pestaña Socios: CRUD + búsqueda.   Pestaña Entrenadores: CRUD + asignación 16:1.
 */
public class PanelGestionSocios {
    // --- Contenedor Principal ---
    private JPanel panelPrincipal;
    private JTabbedPane tabbedPane1;

    // --- Pestaña Socios ---
    private JTextField txtNombre;
    private JTextField txtEdad;
    private JTextField txtTelefono;
    private JComboBox cmbMembresia;
    private JButton btnRegistrar;
    private JTable tablaSocios;
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnTodos;
    private JButton btnActualizar;
    private JButton btnInactivar;

    // --- Pestaña Entrenadores ---
    private JTextField txtNombreT;
    private JTextField txtEdadT;
    private JTextField txtTelefonoT;
    private JTextField txtEspecialidad;
    private JComboBox cmbTurno;
    private JButton btnRegistrarT;
    private JList listaEntrenadores;
    private JList listaAlumnos;
    private JComboBox cmbSocioAsignar;
    private JButton btnAsignar;
    private JButton btnQuitarAlumno;
    private JButton btnEditarT;
    private JButton btnInactivarT;

    // --- Lógica ---
    private static final String[] TURNOS = {"Matutino", "Vespertino", "Nocturno"};
    private final GestorSocios gestor;
    private DefaultTableModel modeloSocios;
    private DefaultListModel<Entrenador> modeloEntrenadores;
    private DefaultListModel<Socio> modeloAlumnos;

    public PanelGestionSocios() {
        this(new GestorSocios());
    }

    public PanelGestionSocios(GestorSocios gestorCompartido) {
        this.gestor = gestorCompartido;

        modeloSocios = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Edad", "Teléfono", "Membresía", "Entrenador", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaSocios.setModel(modeloSocios);

        modeloEntrenadores = new DefaultListModel<>();
        modeloAlumnos = new DefaultListModel<>();
        listaEntrenadores.setModel(modeloEntrenadores);
        listaAlumnos.setModel(modeloAlumnos);

        for (TipoMembresia m : TipoMembresia.values()) cmbMembresia.addItem(m);
        for (String t : TURNOS) cmbTurno.addItem(t);

        // ════════ MEJORAS VISUALES PARA COMBINAR CON FLATLAF ════════
        tablaSocios.setRowHeight(28);
        tablaSocios.setShowVerticalLines(false);

        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTodos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnInactivar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrarT.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAsignar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuitarAlumno.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEditarT.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnInactivarT.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // ════════════════════════════════════════════════════════════

        createListener();
        refrescarTodo();
    }

    private void createListener() {

        // ════════════ EVENTO DE CLIC EN LA TABLA SOCIOS ════════════
        tablaSocios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaSocios.getSelectedRow();
                if (fila != -1) {
                    txtNombre.setText(modeloSocios.getValueAt(fila, 1).toString());
                    txtEdad.setText(modeloSocios.getValueAt(fila, 2).toString());
                    txtTelefono.setText(modeloSocios.getValueAt(fila, 3).toString());
                    String memStr = modeloSocios.getValueAt(fila, 4).toString();
                    cmbMembresia.setSelectedItem(TipoMembresia.valueOf(memStr));
                }
            }
        });

        // ════════════ SOCIOS ════════════
        btnRegistrar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                try {
                    String nombre = txtNombre.getText().trim();
                    if (nombre.isEmpty()) { aviso("Ingrese el nombre."); return; }
                    int edad = Integer.parseInt(txtEdad.getText().trim());
                    Socio nuevo = new Socio(gestor.generarIdSocio(), nombre, edad,
                            txtTelefono.getText().trim(), (TipoMembresia) cmbMembresia.getSelectedItem());
                    gestor.registrarSocio(nuevo);
                    txtNombre.setText(""); txtEdad.setText(""); txtTelefono.setText("");
                    refrescarTablaSocios(gestor.obtenerTodosLosSocios());
                    cargarCmbSocioAsignar();
                    JOptionPane.showMessageDialog(panelPrincipal, "Socio registrado: " + nuevo.getIdUsuario());
                } catch (NumberFormatException ex) {
                    aviso("La edad debe ser un número.");
                }
            }
        });

        btnBuscar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                String filtro = txtBuscar.getText().trim();
                if (filtro.isEmpty()) { refrescarTablaSocios(gestor.obtenerTodosLosSocios()); return; }
                refrescarTablaSocios(gestor.buscarSociosPorNombre(filtro));
            }
        });

        btnTodos.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                txtBuscar.setText("");
                refrescarTablaSocios(gestor.obtenerTodosLosSocios());
            }
        });

        btnActualizar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                String id = idSocioSeleccionado();
                if (id == null) return;

                try {
                    String nuevoNombre = txtNombre.getText().trim();
                    int nuevaEdad = Integer.parseInt(txtEdad.getText().trim());
                    String nuevoTel = txtTelefono.getText().trim();
                    TipoMembresia nuevaMem = (TipoMembresia) cmbMembresia.getSelectedItem();

                    if (nuevoNombre.isEmpty()) {
                        aviso("El nombre no puede estar vacío.");
                        return;
                    }

                    Socio s = gestor.buscarSocioPorId(id);
                    if (s != null) {
                        s.setNombre(nuevoNombre);
                        s.setEdad(nuevaEdad);
                        s.setTelefono(nuevoTel);
                        s.setTipoMembresia(nuevaMem);

                        refrescarTablaSocios(gestor.obtenerTodosLosSocios());
                        cargarCmbSocioAsignar();
                        refrescarEntrenadores();

                        txtNombre.setText("");
                        txtEdad.setText("");
                        txtTelefono.setText("");
                        tablaSocios.clearSelection();

                        JOptionPane.showMessageDialog(panelPrincipal, "Socio actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    aviso("Asegúrese de ingresar un número válido en la Edad.");
                }
            }
        });

        btnInactivar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                String id = idSocioSeleccionado();
                if (id == null) return;
                int ok = JOptionPane.showConfirmDialog(panelPrincipal,
                        "¿Inactivar al socio " + id + "? (baja lógica, conserva su historial)",
                        "Confirmar", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    gestor.inactivarSocio(id);
                    refrescarTablaSocios(gestor.obtenerTodosLosSocios());
                    cargarCmbSocioAsignar();
                    refrescarEntrenadores();
                }
            }
        });

        // ════════════ ENTRENADORES ════════════
        btnRegistrarT.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                try {
                    String nombre = txtNombreT.getText().trim();
                    String esp = txtEspecialidad.getText().trim();
                    if (nombre.isEmpty() || esp.isEmpty()) { aviso("Complete nombre y especialidad."); return; }
                    int edad = Integer.parseInt(txtEdadT.getText().trim());
                    Entrenador nuevo = new Entrenador(gestor.generarIdEntrenador(), nombre, edad,
                            txtTelefonoT.getText().trim(), esp, (String) cmbTurno.getSelectedItem());
                    gestor.registrarEntrenador(nuevo);
                    txtNombreT.setText(""); txtEdadT.setText(""); txtTelefonoT.setText(""); txtEspecialidad.setText("");
                    refrescarEntrenadores();
                    JOptionPane.showMessageDialog(panelPrincipal, "Entrenador registrado: " + nuevo.getIdUsuario());
                } catch (NumberFormatException ex) {
                    aviso("La edad debe ser un número.");
                }
            }
        });

        listaEntrenadores.addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) refrescarAlumnos(); });

        btnAsignar.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Entrenador t = (Entrenador) listaEntrenadores.getSelectedValue();
                Socio s = (Socio) cmbSocioAsignar.getSelectedItem();
                if (t == null || s == null) { aviso("Seleccione un entrenador y un socio."); return; }
                if (gestor.buscarEntrenadorPorId(t.getIdUsuario()) == null) {
                    aviso("El entrenador seleccionado ya no está disponible."); return;
                }
                if (gestor.asignarSocioAEntrenador(s.getIdUsuario(), t.getIdUsuario())) {
                    refrescarEntrenadores();
                    refrescarAlumnos();
                    refrescarTablaSocios(gestor.obtenerTodosLosSocios());
                } else {
                    JOptionPane.showMessageDialog(panelPrincipal,
                            "No se pudo asignar: el entrenador está saturado (límite 16:1) o datos inválidos.",
                            "Límite de carga", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnQuitarAlumno.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Entrenador t = (Entrenador) listaEntrenadores.getSelectedValue();
                Socio alumno = (Socio) listaAlumnos.getSelectedValue();
                if (t == null || alumno == null) { aviso("Seleccione un entrenador y un alumno."); return; }
                t.removerAlumno(alumno.getIdUsuario());
                refrescarEntrenadores();
                refrescarAlumnos();
                refrescarTablaSocios(gestor.obtenerTodosLosSocios());
            }
        });

        btnEditarT.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Entrenador t = (Entrenador) listaEntrenadores.getSelectedValue();
                if (t == null) { aviso("Seleccione un entrenador."); return; }
                JTextField nuevaEsp = new JTextField(t.getEspecialidad());
                JComboBox<String> nuevoTurno = new JComboBox<>(TURNOS);
                nuevoTurno.setSelectedItem(t.getTurno());
                Object[] form = {"Especialidad:", nuevaEsp, "Turno:", nuevoTurno};
                int ok = JOptionPane.showConfirmDialog(panelPrincipal, form,
                        "Actualizar " + t.getNombre(), JOptionPane.OK_CANCEL_OPTION);
                if (ok == JOptionPane.OK_OPTION) {
                    gestor.actualizarEntrenador(t.getIdUsuario(), nuevaEsp.getText().trim(),
                            (String) nuevoTurno.getSelectedItem());
                    refrescarEntrenadores();
                }
            }
        });

        btnInactivarT.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                Entrenador t = (Entrenador) listaEntrenadores.getSelectedValue();
                if (t == null) { aviso("Seleccione un entrenador."); return; }
                int ok = JOptionPane.showConfirmDialog(panelPrincipal,
                        "¿Inactivar a " + t.getNombre() + "? Sus alumnos quedarán sin asignación.",
                        "Confirmar", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    gestor.inactivarEntrenador(t.getIdUsuario());
                    refrescarEntrenadores();
                    refrescarAlumnos();
                    refrescarTablaSocios(gestor.obtenerTodosLosSocios());
                }
            }
        });
    }

    // ── Refrescos ─────────────────────────────────────────────────────────────
    public void refrescarTodo() {
        refrescarTablaSocios(gestor.obtenerTodosLosSocios());
        refrescarEntrenadores();
        cargarCmbSocioAsignar();
    }

    private void refrescarTablaSocios(java.util.List<Socio> socios) {
        modeloSocios.setRowCount(0);
        for (Socio s : socios) {
            Entrenador t = gestor.entrenadorDeSocio(s.getIdUsuario());
            modeloSocios.addRow(new Object[]{
                    s.getIdUsuario(), s.getNombre(), s.getEdad(), s.getTelefono(),
                    s.getTipoMembresia().name(), (t != null) ? t.getNombre() : "—",
                    s.isActivo() ? "ACTIVO" : "INACTIVO"
            });
        }
    }

    private void refrescarEntrenadores() {
        Entrenador sel = (Entrenador) listaEntrenadores.getSelectedValue();
        modeloEntrenadores.clear();
        for (Entrenador t : gestor.obtenerEntrenadoresActivos()) modeloEntrenadores.addElement(t);
        if (sel != null) listaEntrenadores.setSelectedValue(sel, true);
        refrescarAlumnos();
    }

    private void refrescarAlumnos() {
        modeloAlumnos.clear();
        Entrenador t = (Entrenador) listaEntrenadores.getSelectedValue();
        if (t != null) for (Socio s : t.getAlumnosACargo()) modeloAlumnos.addElement(s);
    }

    private void cargarCmbSocioAsignar() {
        Socio sel = (Socio) cmbSocioAsignar.getSelectedItem();
        cmbSocioAsignar.removeAllItems();
        for (Socio s : gestor.obtenerSociosActivos()) cmbSocioAsignar.addItem(s);
        if (sel != null) cmbSocioAsignar.setSelectedItem(sel);
    }

    // ── Utilitarios ───────────────────────────────────────────────────────────
    private String idSocioSeleccionado() {
        int fila = tablaSocios.getSelectedRow();
        if (fila == -1) { aviso("Seleccione un socio de la tabla."); return null; }
        return modeloSocios.getValueAt(fila, 0).toString();
    }

    private void aviso(String msj) {
        JOptionPane.showMessageDialog(panelPrincipal, msj, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public JPanel getPanel() {
        return panelPrincipal;
    }
}