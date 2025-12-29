/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import config.Koneksi;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author ASUS
 */
public class FormPemesananTiket extends javax.swing.JFrame {

    /**
     * Creates new form FormPemesananTiket
     */
    public FormPemesananTiket() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Transaksi Pemesanan Tiket");
        loadRute();
        loadStatus();
        tampilData();
        kosongkanForm();
        txt_id.setEnabled(false);
        txt_total.setEnabled(false);
        txt_harga_satuan.setEnabled(false);
        
        // Set tanggal default hari ini
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        txt_tanggal.setText(sdf.format(new java.util.Date()));
    }
    
    // Method untuk load data rute ke combobox
    private void loadRute() {
        try {
            cmb_rute.removeAllItems();
            String sql = "SELECT r.id_rute, r.nomor_penerbangan, r.kota_asal, r.kota_tujuan, " +
                        "r.harga_tiket, m.nama_maskapai " +
                        "FROM rute_penerbangan r " +
                        "INNER JOIN maskapai m ON r.id_maskapai = m.id_maskapai " +
                        "ORDER BY r.nomor_penerbangan";
            Connection conn = Koneksi.getKoneksi();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String item = rs.getString("id_rute") + " - " + 
                             rs.getString("nomor_penerbangan") + " | " +
                             rs.getString("kota_asal") + " → " +
                             rs.getString("kota_tujuan") + " (" +
                             rs.getString("nama_maskapai") + ") - Rp " +
                             String.format("%,.0f", rs.getDouble("harga_tiket"));
                cmb_rute.addItem(item);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load rute: " + e.getMessage());
        }
    }
    
    // Method untuk load status pembayaran
    private void loadStatus() {
        cmb_status.removeAllItems();
        cmb_status.addItem("Belum Lunas");
        cmb_status.addItem("Lunas");
    }
    
    // Method untuk menampilkan data ke tabel
    private void tampilData() {
        DefaultTableModel model = new DefaultTableModel(){
        @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Semua cell tidak bisa diedit
            }};
        model.addColumn("ID");
        model.addColumn("Kode Booking");
        model.addColumn("Tanggal");
        model.addColumn("Penerbangan");
        model.addColumn("Penumpang");
        model.addColumn("No. Identitas");
        model.addColumn("telepon");
        model.addColumn("email");
        model.addColumn("Jumlah");
        model.addColumn("Total Bayar");
        model.addColumn("Status");
        
        try {
            String sql = "SELECT p.*, r.nomor_penerbangan, r.kota_asal, r.kota_tujuan " +
                        "FROM pemesanan_tiket p " +
                        "INNER JOIN rute_penerbangan r ON p.id_rute = r.id_rute " +
                        "ORDER BY p.id_pemesanan DESC";
            Connection conn = Koneksi.getKoneksi();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String penerbangan = rs.getString("nomor_penerbangan") + " (" +
                                   rs.getString("kota_asal") + " → " +
                                   rs.getString("kota_tujuan") + ")";
                
                model.addRow(new Object[]{
                    rs.getString("id_pemesanan"),
                    rs.getString("kode_booking"),
                    rs.getString("tanggal_pemesanan"),
                    penerbangan,
                    rs.getString("nama_penumpang"),
                    rs.getString("no_identitas"),
                    rs.getString("telepon"),
                    rs.getString("email"),
                    rs.getString("jumlah_tiket"),
                    "Rp " + String.format("%,.0f", rs.getDouble("total_bayar")),
                    rs.getString("status_pembayaran")
                });
            }
            tbl_pemesanan.setModel(model);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error menampilkan data: " + e.getMessage());
        }
    }
    
    // Method untuk mengosongkan form
    private void kosongkanForm() {
        txt_id.setText("");
        txt_kode_booking.setText("");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        txt_tanggal.setText(sdf.format(new java.util.Date()));
        txt_nama_penumpang.setText("");
        txt_no_identitas.setText("");
        txt_telepon.setText("");
        txt_email.setText("");
        txt_jumlah_tiket.setText("");
        txt_harga_satuan.setText("");
        txt_total.setText("");
        if (cmb_rute.getItemCount() > 0) {
            cmb_rute.setSelectedIndex(0);
        }
        cmb_status.setSelectedIndex(0);
        btn_tambah.setEnabled(true);
        btn_update.setEnabled(false);
        btn_hapus.setEnabled(false);
        generateKodeBooking();
    }
    
    // Method untuk generate kode booking otomatis
    private void generateKodeBooking() {
        try {
            String sql = "SELECT MAX(SUBSTRING(kode_booking, 3)) as max_kode FROM pemesanan_tiket";
            Connection conn = Koneksi.getKoneksi();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                String maxKode = rs.getString("max_kode");
                if (maxKode != null) {
                    int nomorUrut = Integer.parseInt(maxKode) + 1;
                    txt_kode_booking.setText(String.format("BK%03d", nomorUrut));
                } else {
                    txt_kode_booking.setText("BK001");
                }
            }
            
        } catch (SQLException e) {
            txt_kode_booking.setText("BK001");
        }
    }
    
    // Method untuk mendapatkan ID Rute dari ComboBox
    private String getIdRute() {
        String selected = (String) cmb_rute.getSelectedItem();
        if (selected != null) {
            return selected.split(" - ")[0];
        }
        return "";
    }
    
    // Method untuk mendapatkan harga tiket dari rute yang dipilih
    private double getHargaTiket() {
        try {
            String sql = "SELECT harga_tiket FROM rute_penerbangan WHERE id_rute=?";
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, getIdRute());
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("harga_tiket");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error get harga: " + e.getMessage());
        }
        return 0;
    }
    
    // Method untuk menghitung total
    private void hitungTotal() {
        try {
            double harga = getHargaTiket();
            txt_harga_satuan.setText(String.format("%.0f", harga));
            
            if (!txt_jumlah_tiket.getText().trim().isEmpty()) {
                int jumlah = Integer.parseInt(txt_jumlah_tiket.getText().trim());
                double total = harga * jumlah;
                txt_total.setText(String.format("%.0f", total));
            }
        } catch (NumberFormatException e) {
            txt_total.setText("0");
        }
    }
    
    // Method untuk tambah data
    private void tambahData() {
        // Validasi input
        if (txt_kode_booking.getText().trim().isEmpty() || 
            txt_nama_penumpang.getText().trim().isEmpty() || 
            txt_no_identitas.getText().trim().isEmpty() ||
            txt_jumlah_tiket.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Field wajib harus diisi!");
            return;
        }
        
        try {
            String sql = "INSERT INTO pemesanan_tiket (kode_booking, tanggal_pemesanan, id_rute, " +
                        "nama_penumpang, no_identitas, telepon, email, jumlah_tiket, total_bayar, " +
                        "status_pembayaran) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);
            
            pst.setString(1, txt_kode_booking.getText().trim());
            pst.setString(2, txt_tanggal.getText().trim());
            pst.setString(3, getIdRute());
            pst.setString(4, txt_nama_penumpang.getText().trim());
            pst.setString(5, txt_no_identitas.getText().trim());
            pst.setString(6, txt_telepon.getText().trim());
            pst.setString(7, txt_email.getText().trim());
            pst.setInt(8, Integer.parseInt(txt_jumlah_tiket.getText().trim()));
            pst.setDouble(9, Double.parseDouble(txt_total.getText().trim()));
            pst.setString(10, (String) cmb_status.getSelectedItem());
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Pemesanan berhasil!\nKode Booking: " + txt_kode_booking.getText());
            tampilData();
            kosongkanForm();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah tiket dan total harus berupa angka!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error menambah data: " + e.getMessage());
        }
    }
    
    // Method untuk update data
    private void updateData() {
        if (txt_id.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan diupdate!");
            return;
        }
        
        try {
            String sql = "UPDATE pemesanan_tiket SET tanggal_pemesanan=?, id_rute=?, " +
                        "nama_penumpang=?, no_identitas=?, telepon=?, email=?, jumlah_tiket=?, " +
                        "total_bayar=?, status_pembayaran=? WHERE id_pemesanan=?";
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);
            
            pst.setString(1, txt_tanggal.getText().trim());
            pst.setString(2, getIdRute());
            pst.setString(3, txt_nama_penumpang.getText().trim());
            pst.setString(4, txt_no_identitas.getText().trim());
            pst.setString(5, txt_telepon.getText().trim());
            pst.setString(6, txt_email.getText().trim());
            pst.setInt(7, Integer.parseInt(txt_jumlah_tiket.getText().trim()));
            pst.setDouble(8, Double.parseDouble(txt_total.getText().trim()));
            pst.setString(9, (String) cmb_status.getSelectedItem());
            pst.setString(10, txt_id.getText());
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            tampilData();
            kosongkanForm();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error update data: " + e.getMessage());
        }
    }
    
    // Method untuk delete data
    private void deleteData() {
        if (txt_id.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Yakin ingin menghapus pemesanan ini?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM pemesanan_tiket WHERE id_pemesanan=?";
                Connection conn = Koneksi.getKoneksi();
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, txt_id.getText());
                
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                tampilData();
                kosongkanForm();
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error hapus data: " + e.getMessage());
            }
        }
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txt_id = new javax.swing.JTextField();
        txt_kode_booking = new javax.swing.JTextField();
        txt_tanggal = new javax.swing.JTextField();
        txt_nama_penumpang = new javax.swing.JTextField();
        txt_no_identitas = new javax.swing.JTextField();
        txt_telepon = new javax.swing.JTextField();
        txt_email = new javax.swing.JTextField();
        txt_jumlah_tiket = new javax.swing.JTextField();
        txt_harga_satuan = new javax.swing.JTextField();
        txt_total = new javax.swing.JTextField();
        cmb_rute = new javax.swing.JComboBox<>();
        cmb_status = new javax.swing.JComboBox<>();
        btn_tambah = new javax.swing.JButton();
        btn_update = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        btn_batal = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_pemesanan = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btn_kembali = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_idActionPerformed(evt);
            }
        });

        txt_email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_emailActionPerformed(evt);
            }
        });

        txt_total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_totalActionPerformed(evt);
            }
        });

        cmb_rute.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_rute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_ruteActionPerformed(evt);
            }
        });

        cmb_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmb_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_statusActionPerformed(evt);
            }
        });

        btn_tambah.setText("Tambah");
        btn_tambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tambahActionPerformed(evt);
            }
        });

        btn_update.setText("Update");
        btn_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updateActionPerformed(evt);
            }
        });

        btn_hapus.setText("Hapus");
        btn_hapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_hapusActionPerformed(evt);
            }
        });

        btn_batal.setText("Batal");
        btn_batal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_batalActionPerformed(evt);
            }
        });

        tbl_pemesanan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_pemesanan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_pemesananMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_pemesanan);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("FORM PEMESANAN TIKET");

        jLabel2.setText("ID : ");

        jLabel3.setText("Kode Booking :");

        jLabel4.setText("Tanggal :");

        jLabel5.setText("Nama Penumpang :");

        jLabel6.setText("No Identitas :");

        jLabel7.setText("Telepon :");

        jLabel8.setText("Email :");

        jLabel9.setText("Jumlah Tiket :");

        jLabel10.setText("Harga Satuan :");

        jLabel11.setText("Total :");

        btn_kembali.setText("Kembali");
        btn_kembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_kembaliActionPerformed(evt);
            }
        });

        jLabel12.setText("Rute :");

        jLabel13.setText("Status :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(171, 171, 171)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txt_no_identitas)
                                    .addComponent(txt_tanggal)
                                    .addComponent(txt_id)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel8)
                                    .addComponent(txt_harga_satuan)
                                    .addComponent(txt_email)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cmb_rute, 0, 175, Short.MAX_VALUE)))
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(txt_kode_booking, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel5)
                                            .addComponent(txt_nama_penumpang, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jLabel7)
                                                .addComponent(txt_telepon, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                                                .addComponent(jLabel9)
                                                .addComponent(txt_jumlah_tiket))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel13)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cmb_status, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(jLabel11)
                                            .addComponent(txt_total, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                            .addComponent(jLabel6)
                            .addComponent(jLabel4)
                            .addComponent(jLabel10)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btn_update, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(155, 155, 155)
                                .addComponent(btn_batal))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(197, 197, 197)
                        .addComponent(btn_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_kode_booking, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_nama_penumpang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_no_identitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_telepon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_jumlah_tiket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(cmb_rute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13)
                            .addComponent(cmb_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_harga_satuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_tambah)
                            .addComponent(btn_batal)
                            .addComponent(btn_update)
                            .addComponent(btn_hapus))
                        .addGap(18, 18, 18)
                        .addComponent(btn_kembali)))
                .addGap(126, 126, 126))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tambahActionPerformed
        tambahData();
    }//GEN-LAST:event_btn_tambahActionPerformed

    private void btn_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_updateActionPerformed
        updateData();
    }//GEN-LAST:event_btn_updateActionPerformed

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_hapusActionPerformed
        deleteData();
    }//GEN-LAST:event_btn_hapusActionPerformed

    private void btn_batalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_batalActionPerformed
        kosongkanForm();
    }//GEN-LAST:event_btn_batalActionPerformed

    private void cmb_ruteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_ruteActionPerformed
        hitungTotal();
    }//GEN-LAST:event_cmb_ruteActionPerformed

    private void tbl_pemesananMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_pemesananMouseClicked
    int row = tbl_pemesanan.getSelectedRow();
    if (row != -1) {
        // Ambil ID pemesanan dari tabel
        String idPemesanan = tbl_pemesanan.getValueAt(row, 0).toString();
        
        try {
            // Query untuk mengambil SEMUA data dari database
            String sql = "SELECT p.*, r.nomor_penerbangan, r.kota_asal, r.kota_tujuan, m.nama_maskapai " +
                        "FROM pemesanan_tiket p " +
                        "INNER JOIN rute_penerbangan r ON p.id_rute = r.id_rute " +
                        "INNER JOIN maskapai m ON r.id_maskapai = m.id_maskapai " +
                        "WHERE p.id_pemesanan = ?";
            
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, idPemesanan);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                // Isi semua field
                txt_id.setText(rs.getString("id_pemesanan"));
                txt_kode_booking.setText(rs.getString("kode_booking"));
                txt_tanggal.setText(rs.getString("tanggal_pemesanan"));
                txt_nama_penumpang.setText(rs.getString("nama_penumpang"));
                txt_no_identitas.setText(rs.getString("no_identitas"));
                txt_telepon.setText(rs.getString("telepon")); // ← DITAMBAHKAN
                txt_email.setText(rs.getString("email"));     // ← DITAMBAHKAN
                txt_jumlah_tiket.setText(rs.getString("jumlah_tiket"));
                
                // Format harga satuan dan total
                double hargaSatuan = rs.getDouble("total_bayar") / rs.getInt("jumlah_tiket");
                txt_harga_satuan.setText(String.format("%.0f", hargaSatuan));
                txt_total.setText(String.format("%.0f", rs.getDouble("total_bayar")));
                
                // Set combo box rute
                String ruteInfo = rs.getString("id_rute") + " - " + 
                                 rs.getString("nomor_penerbangan") + " | " +
                                 rs.getString("kota_asal") + " → " +
                                 rs.getString("kota_tujuan") + " (" +
                                 rs.getString("nama_maskapai") + ") - Rp " +
                                 String.format("%,.0f", hargaSatuan);
                
                // Cari dan set item di combobox
                for (int i = 0; i < cmb_rute.getItemCount(); i++) {
                    String item = (String) cmb_rute.getItemAt(i);
                    if (item.startsWith(rs.getString("id_rute") + " - ")) {
                        cmb_rute.setSelectedIndex(i);
                        break;
                    }
                }
                
                // Set status
                cmb_status.setSelectedItem(rs.getString("status_pembayaran"));
                
                // Update status button
                btn_tambah.setEnabled(false);
                btn_update.setEnabled(true);
                btn_hapus.setEnabled(true);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error mengambil data: " + e.getMessage());
        }
    }
    }//GEN-LAST:event_tbl_pemesananMouseClicked

    private void txt_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_idActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_idActionPerformed

    private void txt_emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_emailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_emailActionPerformed

    private void txt_totalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_totalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_totalActionPerformed

    private void btn_kembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_kembaliActionPerformed
         this.dispose();
    }//GEN-LAST:event_btn_kembaliActionPerformed

    private void cmb_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_statusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_statusActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormPemesananTiket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormPemesananTiket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormPemesananTiket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormPemesananTiket.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormPemesananTiket().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_batal;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_kembali;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_update;
    private javax.swing.JComboBox<String> cmb_rute;
    private javax.swing.JComboBox<String> cmb_status;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbl_pemesanan;
    private javax.swing.JTextField txt_email;
    private javax.swing.JTextField txt_harga_satuan;
    private javax.swing.JTextField txt_id;
    private javax.swing.JTextField txt_jumlah_tiket;
    private javax.swing.JTextField txt_kode_booking;
    private javax.swing.JTextField txt_nama_penumpang;
    private javax.swing.JTextField txt_no_identitas;
    private javax.swing.JTextField txt_tanggal;
    private javax.swing.JTextField txt_telepon;
    private javax.swing.JTextField txt_total;
    // End of variables declaration//GEN-END:variables
}
