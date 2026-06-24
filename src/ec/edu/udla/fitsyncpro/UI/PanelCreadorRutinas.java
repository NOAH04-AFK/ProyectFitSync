package ec.edu.udla.fitsyncpro.UI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent; // IMPORTANTE: Agregado para arreglar el error
import javax.swing.event.ListSelectionListener; // IMPORTANTE: Agregado para arreglar el error
import ec.edu.udla.fitsyncpro.controllers.GestorRutinas;
import ec.edu.udla.fitsyncpro.models.Ejercicio;
import ec.edu.udla.fitsyncpro.models.Equipo;
import ec.edu.udla.fitsyncpro.models.Rutina;
import ec.edu.udla.fitsyncpro.models.VideoTutorial;
import ec.edu.udla.fitsyncpro.utils.EstadoEquipo;
import ec.edu.udla.fitsyncpro.utils.GrupoMuscular;
import ec.edu.udla.fitsyncpro.utils.NivelEntrenamiento;

import java.util.ArrayList; // IMPORTANTE: Para manejar las listas sin error
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelCreadorRutinas {
    private JPanel panel;
    private JTabbedPane tabbedPane1;
    private JComboBox cmbGrupoMuscular;
    private JList listaCatalogo;
    private JList listaRutinaSocio;
    private JButton btnAgregar;
    private JButton btnDeshacer;
    private JButton btnGuardarRutina;
    private JTextField txtNombreEjercicio;
    private JTextField txtSeries;
    private JTextField txtReps;
    private JComboBox comboGrupoNuevo;
    private JButton btnGuardarNuevo;
    private JComboBox comboDiaGuardado;
    private JComboBox comboDiaVer;
    private JList listaRutinaDia;
    private JTextField txtObjetivo;
    private JComboBox cmbNivel;
    private JLabel lblInfoRutina;
    private JTextArea txtVideoUrl;
    private JButton btnInactivarRutina;
    private JList listaEquipos;
    private JTextField txtNombreEquipo;
    private JComboBox cmbGrupoEquipo;
    private JButton btnRegistrar;
    private JButton marcarComoDAÑADOButton;
    private JButton btnMarcarOperativo;
    private JButton btnInactivarEquipo;

    private GestorRutinas gestor;
    private DefaultListModel<Ejercicio> modeloCatalogo;
    private DefaultListModel<Ejercicio> modeloRutina;
    private DefaultListModel<Ejercicio> modeloRutinaDia;
    private DefaultListModel<Equipo> modeloEquipos;

    public PanelCreadorRutinas() {

        gestor = new GestorRutinas();
        modeloCatalogo = new DefaultListModel<>();
        modeloRutina = new DefaultListModel<>();
        modeloRutinaDia = new DefaultListModel<>();
        modeloEquipos = new DefaultListModel<>();

        listaEquipos.setModel(modeloEquipos);
        listaRutinaDia.setModel(modeloRutinaDia);
        listaCatalogo.setModel(modeloCatalogo);
        listaRutinaSocio.setModel(modeloRutina);

        btnDeshacer.setEnabled(false);

        
        cmbNivel.removeAllItems();
        for (NivelEntrenamiento n : NivelEntrenamiento.values()) {
            cmbNivel.addItem(n.name());
        }


        cmbGrupoEquipo.removeAllItems();
        for (GrupoMuscular gm : GrupoMuscular.values()) {
            cmbGrupoEquipo.addItem(gm.name());
        }

        actualizarListaEquipos();


        String[] diasSemana = {"LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO"};

        comboDiaVer.removeAllItems();
        for (String dia : diasSemana) {
            comboDiaVer.addItem(dia);
        }

        comboDiaGuardado.removeAllItems();
        for (String dia : diasSemana) {
            comboDiaGuardado.addItem(dia);
        }

        cmbGrupoMuscular.removeAllItems();
        cmbGrupoMuscular.addItem("TODOS");
        for (GrupoMuscular gm : GrupoMuscular.values()) {
            cmbGrupoMuscular.addItem(gm.name());
        }

        comboGrupoNuevo.removeAllItems();
        for (GrupoMuscular gm : GrupoMuscular.values()) {
            comboGrupoNuevo.addItem(gm.name());
        }


        actualizarCatalogoVisual(null);



        comboDiaVer.addActionListener(e -> actualizarVistaSemana());

        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Ejercicio seleccionado = (Ejercicio) listaCatalogo.getSelectedValue();
                if (seleccionado != null) {


                    if (!gestor.isGrupoDisponible(seleccionado.getGrupoMuscular())) {
                        JOptionPane.showMessageDialog(null,
                                "No puedes agregar este ejercicio. Las máquinas de " + seleccionado.getGrupoMuscular() + " están DAÑADAS.",
                                "Máquina Fuera de Servicio", JOptionPane.ERROR_MESSAGE);
                        return;
                    }


                    gestor.agregarEjercicio(seleccionado);
                    modeloRutina.addElement(seleccionado);
                    btnDeshacer.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Seleccione un ejercicio del catálogo.");
                }
            }
        });

        btnDeshacer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Ejercicio deshecho = gestor.deshacerUltimoEjercicio();
                if (deshecho != null) {
                    modeloRutina.remove(modeloRutina.getSize()-1);
                    if (gestor.getRutinaActual().isEmpty()) {
                        btnDeshacer.setEnabled(false);
                    }
                }
            }
        });

        btnGuardarRutina.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gestor.getRutinaActual().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "La rutina está vacía.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String objetivo = txtObjetivo.getText().trim();
                if (objetivo.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ingrese un objetivo para la rutina.");
                    return;
                }
                String dia = comboDiaGuardado.getSelectedItem().toString();
                NivelEntrenamiento nivel = NivelEntrenamiento.valueOf(cmbNivel.getSelectedItem().toString());
                gestor.guardarRutinaEnDia(dia, objetivo, nivel);
                modeloRutina.clear();
                btnDeshacer.setEnabled(false);
                txtObjetivo.setText("");
                JOptionPane.showMessageDialog(null, "¡Rutina guardada para " + dia + "!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarVistaSemana();
            }
        });
        btnGuardarNuevo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nombre = txtNombreEjercicio.getText();
                    int series = Integer.parseInt(txtSeries.getText());
                    int reps = Integer.parseInt(txtReps.getText());

                    String seleccionGrupo = comboGrupoNuevo.getSelectedItem().toString();
                    GrupoMuscular grupoSeleccionado = GrupoMuscular.valueOf(seleccionGrupo);

                    if (nombre.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El nombre no puede estar vacío.");
                        return;
                    }

                    Ejercicio nuevoEj = new Ejercicio("EJ-", nombre, grupoSeleccionado, series, reps);
                    gestor.obtenerCatalogo(null).add(nuevoEj);

                    String filtroActual = cmbGrupoMuscular.getSelectedItem().toString();
                    if (filtroActual.equals("TODOS")) {
                        actualizarCatalogoVisual(null);
                    } else {
                        actualizarCatalogoVisual(GrupoMuscular.valueOf(filtroActual));
                    }

                    txtNombreEjercicio.setText("");
                    txtSeries.setText("");
                    txtReps.setText("");

                    JOptionPane.showMessageDialog(null, "¡Ejercicio creado y añadido al catálogo!");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Ingrese números válidos en Series y Repeticiones.");
                }
            }
        });
        cmbGrupoMuscular.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String seleccion = cmbGrupoMuscular.getSelectedItem().toString();
                if (seleccion.equals("TODOS")) {
                    actualizarCatalogoVisual(null);
                } else {
                    actualizarCatalogoVisual(GrupoMuscular.valueOf(seleccion));
                }
            }
        });
        listaRutinaDia.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Ejercicio sel = (Ejercicio) listaRutinaDia.getSelectedValue();
                if (sel == null) return;
                VideoTutorial video = gestor.obtenerVideoPorEjercicio(sel.getIdEjercicio());
                if (video != null) {
                    txtVideoUrl.setText(video.getDescripcion() + "\n" + video.getUrlYoutube());
                } else {
                    txtVideoUrl.setText("No hay video disponible.");
                }
            }
        });
        btnInactivarRutina.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dia = comboDiaVer.getSelectedItem().toString();
                int ok = JOptionPane.showConfirmDialog(null,
                        "¿Inactivar la rutina del " + dia + "? (Se conserva el historial)",
                        "Confirmar", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    if (gestor.inactivarRutina(dia)) {
                        JOptionPane.showMessageDialog(null, "Rutina inactivada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        actualizarVistaSemana();
                    } else {
                        JOptionPane.showMessageDialog(null, "No hay rutina activa ese día.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = txtNombreEquipo.getText().trim();
                if (nombre.isEmpty()) { JOptionPane.showMessageDialog(null, "Ingrese el nombre."); return; }
                GrupoMuscular grupo = GrupoMuscular.valueOf(cmbGrupoEquipo.getSelectedItem().toString());
                gestor.registrarEquipo(new Equipo("EQ-" + System.currentTimeMillis(), nombre, grupo));
                actualizarListaEquipos();
                txtNombreEquipo.setText("");
                JOptionPane.showMessageDialog(null, "¡Equipo registrado!");
            }
        });
        marcarComoDAÑADOButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Equipo sel = (Equipo) listaEquipos.getSelectedValue();
                if (sel == null) { JOptionPane.showMessageDialog(null, "Seleccione un equipo."); return; }
                gestor.actualizarEstadoEquipo(sel.getIdEquipo(), EstadoEquipo.DAÑADO);
                actualizarListaEquipos();
                JOptionPane.showMessageDialog(null, "Equipo marcado como DAÑADO.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnMarcarOperativo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Equipo sel = (Equipo) listaEquipos.getSelectedValue();
                if (sel == null) { JOptionPane.showMessageDialog(null, "Seleccione un equipo."); return; }
                gestor.actualizarEstadoEquipo(sel.getIdEquipo(), EstadoEquipo.OPERATIVO);
                actualizarListaEquipos();
                JOptionPane.showMessageDialog(null, "Equipo restaurado a OPERATIVO.");
            }
        });
        btnInactivarEquipo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Equipo sel = (Equipo) listaEquipos.getSelectedValue();
                if (sel == null) { JOptionPane.showMessageDialog(null, "Seleccione un equipo."); return; }
                int ok = JOptionPane.showConfirmDialog(null, "¿Inactivar \"" + sel.getNombreEquipo() + "\"?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    gestor.inactivarEquipo(sel.getIdEquipo());
                    actualizarListaEquipos();
                }
            }
        });
    }

    private void actualizarVistaSemana() {
        modeloRutinaDia.clear();
        txtVideoUrl.setText("");
        String dia = comboDiaVer.getSelectedItem().toString();
        Rutina rutina = gestor.obtenerRutinaPorDia(dia);
        if (rutina != null && rutina.isActiva()) {
            lblInfoRutina.setText("Rutina: " + rutina.getObjetivo() + " | Nivel: " + rutina.getNivel().name());
            for (Ejercicio ej : rutina.getEjercicios()) modeloRutinaDia.addElement(ej);
        } else if (rutina != null) {
            lblInfoRutina.setText("Rutina INACTIVA para este día");
        } else {
            lblInfoRutina.setText("Sin rutina asignada");
            modeloRutinaDia.addElement(new Ejercicio("LIBRE", "Día Libre — Descanso", GrupoMuscular.CARDIO, 0, 0));
        }
    }
    private void actualizarListaEquipos() {
        modeloEquipos.clear();
        for (Equipo eq : gestor.obtenerEquipos()) modeloEquipos.addElement(eq);
    }
    private void actualizarCatalogoVisual(GrupoMuscular filtro) {
        modeloCatalogo.clear();
        ArrayList<Ejercicio> lista = gestor.obtenerCatalogo(filtro);
        for (Ejercicio ej : lista) {
            modeloCatalogo.addElement(ej);
        }
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("PanelCreadorRutinas");
        frame.setContentPane(new PanelCreadorRutinas().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}