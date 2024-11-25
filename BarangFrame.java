import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BarangFrame extends JFrame {
    private JTextField txtIdBarang, txtNamaBarang, txtHarga;
    private JButton btnAdd, btnUpdate, btnDelete, btnView;

    public BarangFrame() {
        setTitle("CRUD Data Barang");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        // Atur GridBagConstraints untuk mengatur posisi elemen
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Baris pertama: Label dan TextField ID Barang
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("ID Barang:"), gbc);

        gbc.gridx = 1;
        txtIdBarang = new JTextField(15);
        add(txtIdBarang, gbc);

        // Baris kedua: Label dan TextField Nama Barang
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Nama Barang:"), gbc);

        gbc.gridx = 1;
        txtNamaBarang = new JTextField(15);
        add(txtNamaBarang, gbc);

        // Baris ketiga: Label dan TextField Harga Barang
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Harga Barang:"), gbc);

        gbc.gridx = 1;
        txtHarga = new JTextField(15);
        add(txtHarga, gbc);

        // Baris keempat: Tombol CRUD
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5)); // Panel untuk tombol
        btnAdd = new JButton("Tambah");
        btnUpdate = new JButton("Ubah");
        btnDelete = new JButton("Hapus");
        btnView = new JButton("Lihat");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnView);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // Tambahkan ActionListener untuk tombol
        btnAdd.addActionListener(e -> addBarang());
        btnUpdate.addActionListener(e -> updateBarang());
        btnDelete.addActionListener(e -> deleteBarang());
        btnView.addActionListener(e -> viewBarang());

        setVisible(true);
    }

    private void addBarang() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO data_barang (nama, harga) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtNamaBarang.getText());
            stmt.setDouble(2, Double.parseDouble(txtHarga.getText()));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Barang berhasil ditambahkan!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateBarang() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE data_barang SET nama = ?, harga = ? WHERE id_barang = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtNamaBarang.getText());
            stmt.setDouble(2, Double.parseDouble(txtHarga.getText()));
            stmt.setInt(3, Integer.parseInt(txtIdBarang.getText()));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Barang berhasil diperbarui!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteBarang() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM data_barang WHERE id_barang = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(txtIdBarang.getText()));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Barang berhasil dihapus!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void viewBarang() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM data_barang";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            StringBuilder result = new StringBuilder("ID Barang\tNama Barang\tHarga\n");
            while (rs.next()) {
                result.append(rs.getInt("id_barang")).append("\t\t")
                      .append(rs.getString("nama")).append("\t\t")
                      .append(rs.getDouble("harga")).append("\n");
            }
            JOptionPane.showMessageDialog(this, result.toString());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new BarangFrame();
    }
}
