package ec.edu.udla.fitsyncpro.UI;

import ec.edu.udla.fitsyncpro.controllers.GestorEvolucionFisica;
import ec.edu.udla.fitsyncpro.models.RegistroFisico;
import ec.edu.udla.fitsyncpro.models.Socio;
import ec.edu.udla.fitsyncpro.utils.TipoMembresia;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PanelCondicionFisica {
    public JPanel panel;
    private JTabbedPane tabbedPane;
    private JComboBox<Socio> cmbSocio;
    private JLabel lblAlerta;
    private JTextField txtPeso;
    private JTextField txtEstatura;
    private JTextField txtGrasa;
    private JTextField txtCintura;
    private JTextField txtCadera;
    private JLabel lblIMC;
    private JLabel lblClasificacion;
    private JButton btnCalcularIMC;
    private JTextArea txtObservaciones;
    private JTextArea txtLesiones;
    private JButton btnGuardarEval;
    private JLabel lblResumenUltimo;
    private JTable tablaHistorial;
    private JButton btnInactivar;
    private JTextField txtNombreSocio;
    private JTextField txtEdadSocio;
    private JTextField txtTelefonoSocio;
    private JComboBox<TipoMembresia> cmbMembresia;
    private JButton btnRegistrarSocio;
    private JComboBox<Socio> cmbSocioHistorial;
    private JButton btnRefrescarHistorial;
    private JButton btnEditar;

    private GestorEvolucionFisica gestor;
    private DefaultTableModel modeloTabla;
    private String idRegistroEnEdicion = null;
    private String idSocioEnEdicion = null;

    public PanelCondicionFisica() {
        gestor = new GestorEvolucionFisica();


        String[] columnas = {"ID Registro", "Fecha", "Peso (kg)", "Estatura (m)",
                "% Grasa", "Cintura", "Cadera", "IMC", "Clasificación", "Estado"};


        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };


        tablaHistorial.setModel(modeloTabla);


        DefaultComboBoxModel<TipoMembresia> modeloMembresia = new DefaultComboBoxModel<>();
        for (TipoMembresia tipo : TipoMembresia.values()) {
            modeloMembresia.addElement(tipo);
        }
        cmbMembresia.setModel(modeloMembresia);


        cargarComboSocios(cmbSocio);
        cargarComboSocios(cmbSocioHistorial);

        btnCalcularIMC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double peso = Double.parseDouble(txtPeso.getText().trim());
                    double estatura = Double.parseDouble(txtEstatura.getText().trim());
                    if (estatura <= 0) throw new NumberFormatException();

                    double imc = Math.round((peso / (estatura * estatura)) * 100.0) / 100.0;
                    lblIMC.setText(String.valueOf(imc));

                    String cls;
                    java.awt.Color color;
                    if (imc < 18.5) { cls = "Bajo peso"; color = new java.awt.Color(30, 100, 180); }
                    else if (imc < 25.0) { cls = "Normal"; color = new java.awt.Color(0, 130, 50); }
                    else if (imc < 30.0) { cls = "Sobrepeso"; color = new java.awt.Color(200, 130, 0); }
                    else { cls = "Obesidad"; color = new java.awt.Color(180, 30, 30); }

                    lblClasificacion.setText("→ " + cls);
                    lblClasificacion.setForeground(color);
                    lblIMC.setForeground(color);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Ingrese valores numéricos válidos en Peso y Estatura.", "Error de entrada", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnGuardarEval.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Socio socio = (Socio) cmbSocio.getSelectedItem();
                if (socio == null) {
                    JOptionPane.showMessageDialog(panel, "Seleccione un socio para guardar la evaluación.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    double peso = Double.parseDouble(txtPeso.getText().trim());
                    double estatura = Double.parseDouble(txtEstatura.getText().trim());
                    double grasa = txtGrasa.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtGrasa.getText().trim());
                    double cintura = txtCintura.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtCintura.getText().trim());
                    double cadera = txtCadera.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtCadera.getText().trim());
                    String obs = txtObservaciones.getText().trim();
                    String les = txtLesiones.getText().trim();

                    if (peso <= 0 || estatura <= 0) {
                        JOptionPane.showMessageDialog(panel, "El peso y la estatura deben ser mayores a cero.", "Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (idRegistroEnEdicion == null) {
                        RegistroFisico nuevo = gestor.crearRegistro(socio.getIdUsuario(), peso, estatura, grasa, cintura, cadera, obs, les);
                        if (nuevo != null) {
                            JOptionPane.showMessageDialog(panel, "Evaluación guardada exitosamente.\nIMC: " + nuevo.getImc(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(panel, "No se pudo guardar. El socio podría estar inactivo.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        // MODO ACTUALIZACIÓN
                        boolean ok = gestor.actualizarRegistro(idSocioEnEdicion, idRegistroEnEdicion,
                                peso, estatura, grasa, cintura, cadera, obs, les);
                        if (ok) {
                            JOptionPane.showMessageDialog(panel, "Registro actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            // Resetear el modo edición
                            idRegistroEnEdicion = null;
                            btnGuardarEval.setText("Guardar Evaluación");
                        } else {
                            JOptionPane.showMessageDialog(panel, "Error al actualizar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    // 3. Limpieza de interfaz y refresco de datos
                    txtPeso.setText(""); txtEstatura.setText(""); txtGrasa.setText("");
                    txtCintura.setText(""); txtCadera.setText("");
                    txtObservaciones.setText(""); txtLesiones.setText("");
                    lblIMC.setText("--");
                    lblClasificacion.setText("");

                    // Refresca la tabla en la pestaña Historial automáticamente
                    btnRefrescarHistorial.doClick();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Por favor, ingrese valores numéricos válidos en los campos.", "Error de entrada", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnRefrescarHistorial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Socio socio = (Socio) cmbSocioHistorial.getSelectedItem();
                if (socio == null) return;

                modeloTabla.setRowCount(0); // Limpia la tabla antes de rellenar
                ArrayList<RegistroFisico> historial = gestor.obtenerHistorial(socio.getIdUsuario());

                for (RegistroFisico r : historial) {
                    modeloTabla.addRow(new Object[]{
                            r.getIdRegistro(),
                            r.getFechaEvaluacion().toString(),
                            r.getPeso(),
                            r.getEstatura(),
                            r.getPorcentajeGrasa() + "%",
                            r.getCircunferenciaCintura() + " cm",
                            r.getCircunferenciaCadera() + " cm",
                            r.getImc(),
                            r.clasificacionIMC(),
                            r.isActivo() ? "Activo" : "Inactivo"
                    });
                }

                RegistroFisico ultimo = gestor.obtenerUltimoRegistro(socio.getIdUsuario());
                if (ultimo != null) {
                    lblResumenUltimo.setText("Último registro — Peso: " + ultimo.getPeso() + " kg | IMC: " + ultimo.getImc() + " (" + ultimo.clasificacionIMC() + ") | Grasa: " + ultimo.getPorcentajeGrasa() + "%");
                } else {
                    lblResumenUltimo.setText("Sin registros aún.");
                }
            }
        });

        btnInactivar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int fila = tablaHistorial.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(panel, "Seleccione un registro en la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String idReg = modeloTabla.getValueAt(fila, 0).toString();
                String estadoActual = modeloTabla.getValueAt(fila, 9).toString();

                if (!estadoActual.equals("Activo")) {
                    JOptionPane.showMessageDialog(panel, "Ese registro ya está inactivo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Socio socio = (Socio) cmbSocioHistorial.getSelectedItem();
                int confirm = JOptionPane.showConfirmDialog(panel, "¿Inactivar el registro " + idReg + "?", "Confirmar", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean ok = gestor.inactivarRegistro(socio.getIdUsuario(), idReg);
                    if (ok) {
                        JOptionPane.showMessageDialog(panel, "Registro inactivado correctamente.");
                        btnRefrescarHistorial.doClick(); // Simula el clic para recargar la tabla automáticamente
                    }
                }
            }
        });

        btnRegistrarSocio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nombre = txtNombreSocio.getText().trim();
                    String telef = txtTelefonoSocio.getText().trim();
                    TipoMembresia mem = (TipoMembresia) cmbMembresia.getSelectedItem();

                    if (nombre.isEmpty() || telef.isEmpty() || txtEdadSocio.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(panel, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int edad = Integer.parseInt(txtEdadSocio.getText().trim());
                    String idNuevo = gestor.generarIdSocio();

                    Socio nuevo = new Socio(idNuevo, nombre, edad, telef, mem);
                    boolean ok = gestor.registrarSocio(nuevo);

                    if (ok) {
                        txtNombreSocio.setText("");
                        txtEdadSocio.setText("");
                        txtTelefonoSocio.setText("");

                        cargarComboSocios(cmbSocio);
                        cargarComboSocios(cmbSocioHistorial);

                        JOptionPane.showMessageDialog(panel,
                                "Socio registrado correctamente con ID: " + idNuevo,
                                "Registro exitoso",
                                JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(panel, "No se pudo registrar. El ID ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "La edad debe ser un número entero válido.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int fila = tablaHistorial.getSelectedRow();
                if (fila < 0) {
                    JOptionPane.showMessageDialog(panel, "Selecciona una fila primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                idRegistroEnEdicion = modeloTabla.getValueAt(fila, 0).toString();

                // ← GUARDAR EL SOCIO DEL HISTORIAL
                Socio socioEditando = (Socio) cmbSocioHistorial.getSelectedItem();
                idSocioEnEdicion = socioEditando.getIdUsuario();

                // ← SINCRONIZAR cmbSocio con el mismo socio
                for (int i = 0; i < cmbSocio.getItemCount(); i++) {
                    if (cmbSocio.getItemAt(i).getIdUsuario().equals(idSocioEnEdicion)) {
                        cmbSocio.setSelectedIndex(i);
                        break;
                    }
                }

                txtPeso.setText(modeloTabla.getValueAt(fila, 2).toString());
                txtEstatura.setText(modeloTabla.getValueAt(fila, 3).toString());
                txtGrasa.setText(modeloTabla.getValueAt(fila, 4).toString().replace("%", "").trim());
                txtCintura.setText(modeloTabla.getValueAt(fila, 5).toString().replace(" cm", "").trim());
                txtCadera.setText(modeloTabla.getValueAt(fila, 6).toString().replace(" cm", "").trim());

                btnGuardarEval.setText("Actualizar Registro");
                tabbedPane.setSelectedIndex(0);
            }
        });
    }

    // Método auxiliar para no repetir código al llenar los combos
    private void cargarComboSocios(JComboBox<Socio> combo) {
        if (combo != null && gestor != null) {
            combo.removeAllItems();
            for (Socio s : gestor.obtenerSociosActivos()) {
                combo.addItem(s);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("FitSync Pro - Panel de Condición Física");
        frame.setContentPane(new PanelCondicionFisica().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(850, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}