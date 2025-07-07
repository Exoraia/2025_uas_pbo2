/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mavenproject4;

/**
 *
 * @author ASUS
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Mavenproject4 extends JFrame {

    private JTable visitTable;
    private DefaultTableModel tableModel;
    
    private JTextField nameField;
    private JTextField nimField;
    private JComboBox<String> studyProgramBox;
    private JComboBox<String> purposeBox;
    private JButton addButton;
    private JButton clearButton;
    private JButton editButton;
    private JButton deleteButton;
    
    private boolean actionColumnsAdded = false;

    public Mavenproject4() {
        setTitle("Library Visit Log");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        nameField = new JTextField();
        nimField = new JTextField();
        studyProgramBox = new JComboBox<>(new String[] {"Sistem dan Teknologi Informasi", "Bisnis Digital", "Kewirausahaan"});
        purposeBox = new JComboBox<>(new String[] {"Membaca", "Meminjam/Mengembalikan Buku", "Research", "Belajar"});
        addButton = new JButton("Add");
        clearButton = new JButton("Clear");

        inputPanel.setBorder(BorderFactory.createTitledBorder("Visit Entry Form"));
        inputPanel.add(new JLabel("NIM:"));
        inputPanel.add(nimField);
        inputPanel.add(new JLabel("Name Mahasiswa:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Program Studi:"));
        inputPanel.add(studyProgramBox);
        inputPanel.add(new JLabel("Tujuan Kunjungan:"));
        inputPanel.add(purposeBox);
        inputPanel.add(addButton);
        inputPanel.add(clearButton);

        add(inputPanel, BorderLayout.NORTH);

        String[] columns = {"Waktu Kunjungan", "NIM", "Nama", "Program Studi", "Tujuan Kunjungan"};
        tableModel = new DefaultTableModel(columns, 0);
        visitTable = new JTable(tableModel);
        add(new JScrollPane(visitTable), BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(visitTable);
        add(scrollPane, BorderLayout.CENTER);

        
        setVisible(true);
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("control G"), "showActions");

        getRootPane().getActionMap().put("showActions", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!actionColumnsAdded) {
                    addActionColumns();
                    actionColumnsAdded = true;
                }
            }
        });
    }
    
    private void addActionColumns() {
        JPanel ActionPanel = new JPanel(new GridLayout(1, 2));

        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");

        ActionPanel.add(editButton);
        ActionPanel.add(deleteButton);

        tableModel.addColumn("Action");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt("Action", i, tableModel.getColumnCount() - 2);
            add(ActionPanel, BorderLayout.NORTH);
        }

        visitTable.getColumn("Action").setCellRenderer(new ButtonRenderer());

        visitTable.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox()));
    }


    private void tambahProduk() {
        try {
            String query = String.format(
                "mutation { addLog(name: \"%s\", nim: \"%s\", studyProgram: \"%s\"), purpose: \\\"%s\\\" { id name nim studyProgram purpose } }",
                nameField.getText(),
                nimField.getText(),
                studyProgramBox.getSelectedItem(),
                purposeBox.getSelectedItem()
            );
            String jsonRequest = new Gson().toJson(new GraphQLQuery(query));
            String response = sendGraphQLRequest(jsonRequest);
            System.out.println("Product added!\n\n" + response);

            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            JsonElement dataElement = jsonObject.get("data");

            if (dataElement == null || dataElement.isJsonNull()) {
                System.out.println("No data received after adding product.");
                return;
            }

            JsonObject addedProduct = dataElement.getAsJsonObject().getAsJsonObject("addProduct");

            if (addedProduct != null) {
                tableModel.setRowCount(0);

                int id = addedProduct.get("id").getAsInt();
                String name = addedProduct.get("name").getAsString();
                String nim = addedProduct.get("nim").getAsString();
                String studyProgram = addedProduct.get("studyProgram").getAsString();
                String purpose = addedProduct.get("purpose").getAsString();
                tableModel.addRow(new Object[]{id, name, nim, studyProgram, purpose});
            }


        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        clearInputFields();
    }


    private void ambilSemuaProduk() {
        try {
            String query = "query { allProducts { id name price category } }";
            String jsonRequest = new Gson().toJson(new GraphQLQuery(query));
            String response = sendGraphQLRequest(jsonRequest);

            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            JsonElement dataElement = jsonObject.get("data");

            if (dataElement == null || dataElement.isJsonNull()) {
                System.out.println("No data received from GraphQL.");
                tableModel.setRowCount(0);
                return;
            }

            JsonArray products = dataElement.getAsJsonObject().getAsJsonArray("allProducts");

            tableModel.setRowCount(0);

            if (products != null) {
                for (JsonElement element : products) {
                    JsonObject product = element.getAsJsonObject();
                    Object id = null;
                    if (product.has("id")) {
                        try {
                            id = product.get("id").getAsLong();
                        } catch (NumberFormatException e) {
                        }
                    }
                    String name = product.has("name") ? product.get("name").getAsString() : "";
                    String nim = product.has("nim") ? product.get("nim").getAsString() : "";
                    String studyProgram = product.has("studyProgram") ? product.get("studyProgram").getAsString() : "";
                    String purpose = product.has("purpose") ? product.get("purpose").getAsString() : "";
                    tableModel.addRow(new Object[]{id, name, nim, studyProgram, purpose});
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void editProduk() {
        int selectedRow = visitTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang ingin anda edit.", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object idObject = tableModel.getValueAt(selectedRow, 0);
        Long id = null;
        if (idObject instanceof Integer) {
            id = ((Integer) idObject).longValue();
        } else if (idObject instanceof Long) {
            id = (Long) idObject;
        } else {
            JOptionPane.showMessageDialog(this, "Error: ID bukan angka valid..", "Data Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = nameField.getText();
        String nim = nimField.getText();
        String studyProgram = (String) studyProgramBox.getSelectedItem();
        String purpose = (String) purposeBox.getSelectedItem();

        try {
            if (name.isEmpty() || nim.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Isi semua field!..", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            String query = String.format(
                "mutation { updateLog(name: \"%s\", nim: \"%s\", studyProgram: \"%s\"), purpose: \"%s\" { id name nim studyPogram purpose } }",
                id, name, nim, studyProgram, purpose
            );
            String jsonRequest = new Gson().toJson(new GraphQLQuery(query));
            String response = sendGraphQLRequest(jsonRequest);
            System.out.println("Product diedit!\n\n" + response);
            ambilSemuaProduk();
            clearInputFields();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error edit produk: " + e.getMessage(), "GraphQL Error", JOptionPane.ERROR_MESSAGE);
        }
        ambilSemuaProduk();
    }

    private void hapusProduk() {
        int selectedRow = visitTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk yang ingin dihapus..");
            return;
        }

        Object idObject = tableModel.getValueAt(selectedRow, 0);
        Long idToDelete = null;
        if (idObject instanceof Integer) {
            idToDelete = ((Integer) idObject).longValue();
        } else if (idObject instanceof Long) {
            idToDelete = (Long) idObject;
        } else {
            JOptionPane.showMessageDialog(this, "Error: ID bukan angka valid..");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus produk ber-ID: " + idToDelete + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String query = String.format(
                    "mutation { deleteLog(id: %d) }",
                    idToDelete
                );
                String jsonRequest = new Gson().toJson(new GraphQLQuery(query));
                String response = sendGraphQLRequest(jsonRequest);
                System.out.println("Produk dihapus!\n\n" + response);
                ambilSemuaProduk();
                clearInputFields();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error hapus produk: " + e.getMessage(), "GraphQL Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        ambilSemuaProduk();
    }

    private void clearInputFields() {
        nameField.setText("");
        nimField.setText("");
        studyProgramBox.setSelectedIndex(0);
        purposeBox.setSelectedIndex(0);
    }

    private String sendGraphQLRequest(String json) throws Exception {
        URL url = new URL("http://localhost:4567/graphql");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Mavenproject4::new);
    }

    class GraphQLQuery {
        String query;
        GraphQLQuery(String query) {
            this.query = query;
        }
    }
}
