package ec.edu.udla.fitsyncpro.UI;

import ec.edu.udla.fitsyncpro.controllers.GestorEvolucionFisica;
import ec.edu.udla.fitsyncpro.controllers.GestorRutinas;
import ec.edu.udla.fitsyncpro.controllers.GestorSocios;
import ec.edu.udla.fitsyncpro.models.Ejercicio;
import ec.edu.udla.fitsyncpro.models.Socio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelAsignacionRutinas {
    // --- Contenedor Principal ---
    private JPanel panelPrincipal;

    // --- Controles Superiores (Asignación General) ---
    private JComboBox cmbSocio;
    private JComboBox cmbDia;
    private JButton btnAsignarSocio;
    private JButton btnAsignarSemana;
    private JLabel lblDisponible; // Donde dice "Disponible: Pecho y Espalda..."

    // --- Tabla Central (Plan Semanal) ---
    private JTable tablaPlanSemanal;

    // --- Controles Inferiores (Personalización) ---
    private JList listaRutinaPersonalizada; // La lista que está en el recuadro del medio
    private JComboBox cmbCatalogo;
    private JButton btnAgregarEjercicio;
    private JButton btnQuitarEjercicio;

    // --- Botones Footer ---
    private JButton btnQuitarAsignacion;
    private JButton btnRefrescar;


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
        btnAsignarSocio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Socio s = (Socio) cmbSocio.getSelectedItem();
                String dia = (String) cmbDia.getSelectedItem();
                if (s != null && dia != null) {
                    // Aquí llamamos al gestor para copiar la rutina maestra al socio
                    boolean exito = gestorRutinas.asignarRutinaASocio(s.getIdUsuario(), dia);
                    if (exito) {
                        JOptionPane.showMessageDialog(panelPrincipal, "Rutina asignada exitosamente para el " + dia);
                        refrescarPlanSemanal();
                    } else {
                        JOptionPane.showMessageDialog(panelPrincipal, "No hay rutina maestra disponible para ese día.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        btnAsignarSemana.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Socio s = (Socio) cmbSocio.getSelectedItem();
                if (s != null) {
                    int ok = JOptionPane.showConfirmDialog(panelPrincipal, "¿Asignar todas las rutinas de la semana a " + s.getNombre() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (ok == JOptionPane.YES_OPTION) {
                        gestorRutinas.asignarSemanaCompleta(s.getIdUsuario());
                        refrescarPlanSemanal();
                        JOptionPane.showMessageDialog(panelPrincipal, "Semana planificada exitosamente.");
                    }
                }
            }
        });
    }

    private void configurarModelos() {
        // 1. Configurar Tabla Semanal
        modeloTabla = new DefaultTableModel(new String[]{"Día", "Objetivo", "Nivel", "Ejercicios", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evita que editen el texto dando doble clic
            }
        };
        tablaPlanSemanal.setModel(modeloTabla);
        tablaPlanSemanal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 2. Configurar Lista de Personalización
        modeloListaPersonalizada = new DefaultListModel<>();
        listaRutinaPersonalizada.setModel(modeloListaPersonalizada);

        // 3. Llenar los días de la semana
        String[] dias = {"LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO"};
        for (String dia : dias) {
            cmbDia.addItem(dia);
        }
    }

    private void configurarListeners() {
        // --- EVENTOS DE SELECCIÓN ---

        // Cuando cambia el socio seleccionado, actualizamos su plan semanal
        cmbSocio.addActionListener(e -> refrescarPlanSemanal());

        // Cuando cambia el día en el combo superior, vemos qué rutina maestra hay disponible
        cmbDia.addActionListener(e -> refrescarInfoDisponible());

        // Cuando el entrenador hace clic en un día de la tabla, mostramos los ejercicios de ese día abajo
        tablaPlanSemanal.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refrescarDetalleDia();
            }
        });














}
