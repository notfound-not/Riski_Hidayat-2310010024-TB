/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import config.Koneksi;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ASUS
 */
public class FormRutePenerbangan extends javax.swing.JFrame {

    /**
     * Creates new form FormRutePenerbangan
     */
    public FormRutePenerbangan() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Data Rute Penerbangan");
        loadMaskapai();
        tampilData();
        kosongkanForm();
        txt_id.setEnabled(false);
    }
    
    // Method untuk load data maskapai ke combobox
    private void loadMaskapai() {
        try {
            cmb_maskapai.removeAllItems();
            String sql = "SELECT id_maskapai, nama_maskapai FROM maskapai ORDER BY nama_maskapai";
            Connection conn = Koneksi.getKoneksi();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                cmb_maskapai.addItem(rs.getString("id_maskapai") + " - " + rs.getString("nama_maskapai"));
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error load maskapai: " + e.getMessage());
        }
    }
    
    // Method untuk menampilkan data ke tabel
    private void tampilData() {
        DefaultTableModel model = new DefaultTableModel(){
        @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Semua cell tidak bisa diedit
            }};
        model.addColumn("ID");
        model.addColumn("Maskapai");
        model.addColumn("No. Penerbangan");
        model.addColumn("Asal");
        model.addColumn("Tujuan");
        model.addColumn("Waktu Berangkat");
        model.addColumn("Waktu Tiba");
        model.addColumn("Harga");
        model.addColumn("Kapasitas");
        
        try {
            String sql = "SELECT r.*, m.nama_maskapai FROM rute_penerbangan r " +
                        "INNER JOIN maskapai m ON r.id_maskapai = m.id_maskapai " +
                        "ORDER BY r.id_rute";
            Connection conn = Koneksi.getKoneksi();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_rute"),
                    rs.getString("nama_maskapai"),
                    rs.getString("nomor_penerbangan"),
                    rs.getString("kota_asal"),
                    rs.getString("kota_tujuan"),
                    rs.getString("waktu_keberangkatan"),
                    rs.getString("waktu_kedatangan"),
                    "Rp " + String.format("%,.0f", rs.getDouble("harga_tiket")),
                    rs.getString("kapasitas_kursi")
                });
            }
            tbl_rute.setModel(model);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error menampilkan data: " + e.getMessage());
        }
    }
    
    // Method untuk mengosongkan form
    private void kosongkanForm() {
        txt_id.setText("");
        txt_nomor_penerbangan.setText("");
        txt_kota_asal.setText("");
        txt_kota_tujuan.setText("");
        txt_waktu_kedatangan.setText("");
        txt_waktu_kedatangan.setText("");
        txt_harga.setText("");
        txt_kapasitas.setText("");
        if (cmb_maskapai.getItemCount() > 0) {
            cmb_maskapai.setSelectedIndex(0);
        }
        btn_tambah.setEnabled(true);
        btn_update.setEnabled(false);
        btn_hapus.setEnabled(false);
    }

    // Method untuk mendapatkan ID Maskapai dari ComboBox
    private String getIdMaskapai() {
        String selected = (String) cmb_maskapai.getSelectedItem();
        if (selected != null) {
            return selected.split(" - ")[0];
        }
        return "";
    }
    
    // Method untuk tambah data
    private void tambahData() {
        // Validasi input
        if (txt_nomor_penerbangan.getText().trim().isEmpty() || 
            txt_kota_asal.getText().trim().isEmpty() || 
            txt_kota_tujuan.getText().trim().isEmpty() ||
            txt_waktu_penerbangan.getText().trim().isEmpty() ||
            txt_waktu_kedatangan.getText().trim().isEmpty() ||
            txt_harga.getText().trim().isEmpty() ||
            txt_kapasitas.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
            return;
        }
        
        try {
            String sql = "INSERT INTO rute_penerbangan (id_maskapai, nomor_penerbangan, kota_asal, kota_tujuan, " +
                        "waktu_keberangkatan, waktu_kedatangan, harga_tiket, kapasitas_kursi) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);
            
            pst.setString(1, getIdMaskapai());
            pst.setString(2, txt_nomor_penerbangan.getText().trim());
            pst.setString(3, txt_kota_asal.getText().trim());
            pst.setString(4, txt_kota_tujuan.getText().trim());
            pst.setString(5, txt_waktu_penerbangan.getText().trim());
            pst.setString(6, txt_waktu_kedatangan.getText().trim());
            pst.setDouble(7, Double.parseDouble(txt_harga.getText().trim()));
            pst.setInt(8, Integer.parseInt(txt_kapasitas.getText().trim()));
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!");
            tampilData();
            kosongkanForm();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga dan Kapasitas harus berupa angka!");
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
            String sql = "UPDATE rute_penerbangan SET id_maskapai=?, nomor_penerbangan=?, kota_asal=?, " +
                        "kota_tujuan=?, waktu_keberangkatan=?, waktu_kedatangan=?, harga_tiket=?, " +
                        "kapasitas_kursi=? WHERE id_rute=?";
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);
            
            pst.setString(1, getIdMaskapai());
            pst.setString(2, txt_nomor_penerbangan.getText().trim());
            pst.setString(3, txt_kota_asal.getText().trim());
            pst.setString(4, txt_kota_tujuan.getText().trim());
            pst.setString(5, txt_waktu_penerbangan.getText().trim());
            pst.setString(6, txt_waktu_kedatangan.getText().trim());
            pst.setDouble(7, Double.parseDouble(txt_harga.getText().trim()));
            pst.setInt(8, Integer.parseInt(txt_kapasitas.getText().trim()));
            pst.setString(9, txt_id.getText());
            
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
            "Yakin ingin menghapus data ini?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM rute_penerbangan WHERE id_rute=?";
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txt_id = new javax.swing.JTextField();
        txt_nomor_penerbangan = new javax.swing.JTextField();
        txt_kota_asal = new javax.swing.JTextField();
        txt_kota_tujuan = new javax.swing.JTextField();
        txt_waktu_penerbangan = new javax.swing.JTextField();
        txt_waktu_kedatangan = new javax.swing.JTextField();
        txt_harga = new javax.swing.JTextField();
        txt_kapasitas = new javax.swing.JTextField();
        cmb_maskapai = new javax.swing.JComboBox<>();
        btn_tambah = new javax.swing.JButton();
        btn_update = new javax.swing.JButton();
        btn_hapus = new javax.swing.JButton();
        btn_batal = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_rute = new javax.swing.JTable();
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
        btn_kembali = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_harga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_hargaActionPerformed(evt);
            }
        });

        cmb_maskapai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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

        tbl_rute.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_rute.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_ruteMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_rute);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("FORM RUTE PENERBANGAN");

        jLabel2.setText("ID :");

        jLabel3.setText("Nomot Penerbangan :");

        jLabel4.setText("Kota Asal :");

        jLabel5.setText("Kota Tujuan :");

        jLabel6.setText("Waktu Penerbangan :");

        jLabel7.setText("Waktu Kedatangan :");

        jLabel8.setText("Harga :");

        jLabel9.setText("Kapasitas :");

        jLabel10.setText("Maskapai");

        btn_kembali.setText("Kembali");
        btn_kembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_kembaliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(97, 97, 97)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(154, 154, 154)
                                        .addComponent(jLabel1))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txt_harga)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel4)
                                            .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txt_kota_asal)
                                            .addComponent(jLabel6)
                                            .addComponent(txt_waktu_penerbangan)
                                            .addComponent(jLabel8)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel10)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cmb_maskapai, 0, 156, Short.MAX_VALUE)))
                                        .addGap(28, 28, 28)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel9)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(txt_nomor_penerbangan, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                                                .addComponent(jLabel3)
                                                .addComponent(jLabel5)
                                                .addComponent(txt_kota_tujuan)
                                                .addComponent(jLabel7)
                                                .addComponent(txt_waktu_kedatangan)
                                                .addComponent(txt_kapasitas))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btn_tambah, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btn_update, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btn_hapus, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btn_batal, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(200, 200, 200)
                        .addComponent(btn_kembali, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_nomor_penerbangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_kota_asal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_kota_tujuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_waktu_penerbangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_waktu_kedatangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_harga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_kapasitas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmb_maskapai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_tambah)
                            .addComponent(btn_update)
                            .addComponent(btn_hapus)
                            .addComponent(btn_batal))))
                .addGap(33, 33, 33)
                .addComponent(btn_kembali)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    private void tbl_ruteMouseClicked(java.awt.event.MouseEvent evt) {                                      
        int row = tbl_rute.getSelectedRow();
        if (row != -1) {
            txt_id.setText(tbl_rute.getValueAt(row, 0).toString());
            
            // Set combobox maskapai
            String namaMaskapai = tbl_rute.getValueAt(row, 1).toString();
            for (int i = 0; i < cmb_maskapai.getItemCount(); i++) {
                if (cmb_maskapai.getItemAt(i).contains(namaMaskapai)) {
                    cmb_maskapai.setSelectedIndex(i);
                    break;
                }
            }
            
            txt_nomor_penerbangan.setText(tbl_rute.getValueAt(row, 2).toString());
            txt_kota_asal.setText(tbl_rute.getValueAt(row, 3).toString());
            txt_kota_tujuan.setText(tbl_rute.getValueAt(row, 4).toString());
            txt_waktu_penerbangan.setText(tbl_rute.getValueAt(row, 5).toString());
            txt_waktu_kedatangan.setText(tbl_rute.getValueAt(row, 6).toString());
            
            // Bersihkan format Rp dan koma
            String harga = tbl_rute.getValueAt(row, 7).toString().replace("Rp ", "").replace(",", "").replace(".", "");
            txt_harga.setText(harga);
            
            txt_kapasitas.setText(tbl_rute.getValueAt(row, 8).toString());
            
            btn_tambah.setEnabled(false);
            btn_update.setEnabled(true);
            btn_hapus.setEnabled(true);
        }
    }                                     

    private void btn_tambahActionPerformed(java.awt.event.ActionEvent evt) {                                           
        tambahData();
    }                                          

    private void btn_updateActionPerformed(java.awt.event.ActionEvent evt) {                                           
        updateData();
    }                                          

    private void btn_hapusActionPerformed(java.awt.event.ActionEvent evt) {                                          
        deleteData();
    }                                         

    private void btn_batalActionPerformed(java.awt.event.ActionEvent evt) {                                          
        kosongkanForm();
    }                                         

    private void txt_hargaActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void btn_kembaliActionPerformed(java.awt.event.ActionEvent evt) {                                            
        this.dispose();
    }                                           

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
            java.util.logging.Logger.getLogger(FormRutePenerbangan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormRutePenerbangan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormRutePenerbangan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormRutePenerbangan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormRutePenerbangan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton btn_batal;
    private javax.swing.JButton btn_hapus;
    private javax.swing.JButton btn_kembali;
    private javax.swing.JButton btn_tambah;
    private javax.swing.JButton btn_update;
    private javax.swing.JComboBox<String> cmb_maskapai;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JTable tbl_rute;
    private javax.swing.JTextField txt_harga;
    private javax.swing.JTextField txt_id;
    private javax.swing.JTextField txt_kapasitas;
    private javax.swing.JTextField txt_kota_asal;
    private javax.swing.JTextField txt_kota_tujuan;
    private javax.swing.JTextField txt_nomor_penerbangan;
    private javax.swing.JTextField txt_waktu_kedatangan;
    private javax.swing.JTextField txt_waktu_penerbangan;
    // End of variables declaration                   
}
