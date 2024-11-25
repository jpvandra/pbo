import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class KonsumenFrame extends JFrame {
    private JTextField txtId, txtNama;
    private JButton btnTambah, btnEdit, btnHapus, btnRefresh;
    private JTable tableKonsumen;
    private DefaultTableModel tableModel;

    private Connection conn;

    public KonsumenFrame() {
        setTitle("CRUD Data Konsumen");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel lblId = new JLabel("ID Konsumen:");
        lblId.setBounds(20, 20, 100, 30);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(120, 20, 150, 30);
        txtId.setEditable(false);
        add(txtId);

        JLabel lblNama = new JLabel("Nama Konsumen:");
        lblNama.setBounds(20, 60, 100, 30);
        add(lblNama);

        txtNama = new JTextField();
        txtNama.setBounds(120, 60, 150, 30);
        add(txtNama);

        btnTambah = new JButton("Tambah");
        btnTambah.setBounds(20, 100, 100, 30);
        add(btnTambah);

        btnEdit = new JButton("Edit");
        btnEdit.setBounds(130, 100, 100, 30);
        add(btnEdit);

        btnHapus = new JButton("Hapus");
        btnHapus.setBounds(240, 100, 100, 30);
        add(btnHapus);

        btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(350, 100, 100, 30);
        add(btnRefresh);

        tableModel = new DefaultTableModel(new String[]{"ID", "Nama Konsumen"}, 0);
        tableKonsumen = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableKonsumen);
        scrollPane.setBounds(20, 150, 540, 200);
        add(scrollPane);

        connectDatabase();

        loadData();

        btnTambah.addActionListener(e -> tambahKonsumen());
        btnEdit.addActionListener(e -> editKonsumen());
        btnHapus.addActionListener(e -> hapusKonsumen());
        btnRefresh.addActionListener(e -> loadData());
        tableKonsumen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableKonsumen.getSelectedRow();
                if (selectedRow != -1) {
                    txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    txtNama.setText(tableModel.getValueAt(selectedRow, 1).toString());
                }
            }
        });
    }

    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/toko", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Koneksi database gagal");
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM data_konsumen");
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("idkonsumen"),
                        rs.getString("nama_konsumen")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void tambahKonsumen() {
        String nama = txtNama.getText();
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO data_konsumen (nama_konsumen) VALUES (?)")) {
            stmt.setString(1, nama);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Konsumen berhasil ditambahkan");
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void editKonsumen() {
        int id = Integer.parseInt(txtId.getText());
        String nama = txtNama.getText();
        try (PreparedStatement stmt = conn.prepareStatement("UPDATE data_konsumen SET nama_konsumen = ? WHERE idkonsumen = ?")) {
            stmt.setString(1, nama);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Konsumen berhasil diubah");
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hapusKonsumen() {
        int id = Integer.parseInt(txtId.getText());
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM data_konsumen WHERE idkonsumen = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Konsumen berhasil dihapus");
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KonsumenFrame().setVisible(true));
    }
}

