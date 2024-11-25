import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class TransaksiFrame extends JFrame {
    private JTextField txtIdTransaksi, txtQuantity, txtTotalHarga;
    private JComboBox<String> cbKonsumen, cbBarang;
    private JButton btnTambah, btnEdit, btnHapus, btnRefresh;
    private JTable tableTransaksi;
    private DefaultTableModel tableModel;

    private Connection conn;

    public TransaksiFrame() {
        setTitle("CRUD Data Transaksi");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel lblIdTransaksi = new JLabel("ID Transaksi:");
        lblIdTransaksi.setBounds(20, 20, 100, 30);
        add(lblIdTransaksi);

        txtIdTransaksi = new JTextField();
        txtIdTransaksi.setBounds(130, 20, 150, 30);
        txtIdTransaksi.setEditable(false);
        add(txtIdTransaksi);

        JLabel lblKonsumen = new JLabel("Konsumen:");
        lblKonsumen.setBounds(20, 60, 100, 30);
        add(lblKonsumen);

        cbKonsumen = new JComboBox<>();
        cbKonsumen.setBounds(130, 60, 200, 30);
        add(cbKonsumen);

        JLabel lblBarang = new JLabel("Barang:");
        lblBarang.setBounds(20, 100, 100, 30);
        add(lblBarang);

        cbBarang = new JComboBox<>();
        cbBarang.setBounds(130, 100, 200, 30);
        add(cbBarang);

        JLabel lblQuantity = new JLabel("Quantity:");
        lblQuantity.setBounds(20, 140, 100, 30);
        add(lblQuantity);

        txtQuantity = new JTextField();
        txtQuantity.setBounds(130, 140, 150, 30);
        add(txtQuantity);

        JLabel lblTotalHarga = new JLabel("Total Harga:");
        lblTotalHarga.setBounds(20, 180, 100, 30);
        add(lblTotalHarga);

        txtTotalHarga = new JTextField();
        txtTotalHarga.setBounds(130, 180, 150, 30);
        txtTotalHarga.setEditable(false);
        add(txtTotalHarga);

        btnTambah = new JButton("Tambah");
        btnTambah.setBounds(20, 230, 100, 30);
        add(btnTambah);

        btnEdit = new JButton("Edit");
        btnEdit.setBounds(130, 230, 100, 30);
        add(btnEdit);

        btnHapus = new JButton("Hapus");
        btnHapus.setBounds(240, 230, 100, 30);
        add(btnHapus);

        btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(350, 230, 100, 30);
        add(btnRefresh);

        tableModel = new DefaultTableModel(new String[]{"ID Transaksi", "Konsumen", "Barang", "Quantity", "Total Harga"}, 0);
        tableTransaksi = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableTransaksi);
        scrollPane.setBounds(20, 280, 740, 150);
        add(scrollPane);

        // Koneksi database
        connectDatabase();

        // Memuat data awal
        loadComboBoxes();
        loadData();

        // Listener
        btnTambah.addActionListener(e -> tambahTransaksi());
        btnEdit.addActionListener(e -> editTransaksi());
        btnHapus.addActionListener(e -> hapusTransaksi());
        btnRefresh.addActionListener(e -> {
            loadComboBoxes();
            loadData();
        });

        tableTransaksi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tableTransaksi.getSelectedRow();
                if (selectedRow != -1) {
                    txtIdTransaksi.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    cbKonsumen.setSelectedItem(tableModel.getValueAt(selectedRow, 1).toString());
                    cbBarang.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());
                    txtQuantity.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    txtTotalHarga.setText(tableModel.getValueAt(selectedRow, 4).toString());
                }
            }
        });

        txtQuantity.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                hitungTotalHarga();
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

    private void loadComboBoxes() {
        cbKonsumen.removeAllItems();
        cbBarang.removeAllItems();

        try (Statement stmt = conn.createStatement()) {
            ResultSet rsKonsumen = stmt.executeQuery("SELECT * FROM data_konsumen");
            while (rsKonsumen.next()) {
                cbKonsumen.addItem(rsKonsumen.getInt("idkonsumen") + " - " + rsKonsumen.getString("nama_konsumen"));
            }

            ResultSet rsBarang = stmt.executeQuery("SELECT * FROM data_barang");
            while (rsBarang.next()) {
                cbBarang.addItem(rsBarang.getInt("idbarang") + " - " + rsBarang.getString("nama_barang"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT t.idtransaksi, k.nama_konsumen, b.nama_barang, t.quantity, t.total_harga " +
                                             "FROM data_transaksi t " +
                                             "JOIN data_konsumen k ON t.idkonsumen = k.idkonsumen " +
                                             "JOIN data_barang b ON t.idbarang = b.idbarang");
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("idtransaksi"),
                        rs.getString("nama_konsumen"),
                        rs.getString("nama_barang"),
                        rs.getInt("quantity"),
                        rs.getDouble("total_harga")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hitungTotalHarga() {
        try {
            String selectedBarang = (String) cbBarang.getSelectedItem();
            if (selectedBarang == null || txtQuantity.getText().isEmpty()) return;

            int idBarang = Integer.parseInt(selectedBarang.split(" - ")[0]);
            int quantity = Integer.parseInt(txtQuantity.getText());

            try (PreparedStatement stmt = conn.prepareStatement("SELECT harga_barang FROM data_barang WHERE idbarang = ?")) {
                stmt.setInt(1, idBarang);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    double harga = rs.getDouble("harga_barang");
                    txtTotalHarga.setText(String.valueOf(harga * quantity));
                }
            }
        } catch (Exception e) {
            txtTotalHarga.setText("");
        }
    }

    private void tambahTransaksi() {
        try {
            String selectedKonsumen = (String) cbKonsumen.getSelectedItem();
            String selectedBarang = (String) cbBarang.getSelectedItem();

            if (selectedKonsumen == null || selectedBarang == null || txtQuantity.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua data harus diisi!");
                return;
            }

            int idKonsumen = Integer.parseInt(selectedKonsumen.split(" - ")[0]);
            int idBarang = Integer.parseInt(selectedBarang.split(" - ")[0]);
            int quantity = Integer.parseInt(txtQuantity.getText());
            double totalHarga = Double.parseDouble(txtTotalHarga.getText());

            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO data_transaksi (idkonsumen, idbarang, quantity, total_harga) VALUES (?, ?, ?, ?)");
            stmt.setInt(1, idKonsumen);
            stmt.setInt(2, idBarang);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, totalHarga);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Transaksi berhasil ditambahkan!");
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void editTransaksi() {
        try {
            int idTransaksi = Integer.parseInt(txtIdTransaksi.getText());
            String selectedKonsumen = (String) cbKonsumen.getSelectedItem();
            String selectedBarang = (String) cbBarang.getSelectedItem();

            if (selectedKonsumen == null || selectedBarang == null || txtQuantity.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua data harus diisi!");
                return;
            }

            int idKonsumen = Integer.parseInt(selectedKonsumen.split(" - ")[0]);
            int idBarang = Integer.parseInt(selectedBarang.split(" - ")[0]);
            int quantity = Integer.parseInt(txtQuantity.getText());
            double totalHarga = Double.parseDouble(txtTotalHarga.getText());

            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE data_transaksi SET idkonsumen = ?, idbarang = ?, quantity = ?, total_harga = ? WHERE idtransaksi = ?");
            stmt.setInt(1, idKonsumen);
            stmt.setInt(2, idBarang);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, totalHarga);
            stmt.setInt(5, idTransaksi);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Transaksi berhasil diperbarui!");
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hapusTransaksi() {
        try {
            int idTransaksi = Integer.parseInt(txtIdTransaksi.getText());

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM data_transaksi WHERE idtransaksi = ?");
            stmt.setInt(1, idTransaksi);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Transaksi berhasil dihapus!");
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TransaksiFrame().setVisible(true));
    }
}

