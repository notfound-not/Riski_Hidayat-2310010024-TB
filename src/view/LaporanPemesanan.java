/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import config.Koneksi;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ASUS
 */
public class LaporanPemesanan extends javax.swing.JFrame {

    /**
     * Creates new form LaporanPemesanan
     */
    public LaporanPemesanan() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Laporan Pemesanan Tiket");
        
        // Set tanggal default (bulan ini)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        txt_tanggal_awal.setText(sdf.format(cal.getTime()));
        txt_tanggal_akhir.setText(sdf.format(new java.util.Date()));
        
        // Tampilkan data tapi sembunyikan info total
        lbl_info.setText("Klik tombol 'Tampilkan' untuk melihat total transaksi");
        tampilkanDataSaja();
        
        //tampilkanLaporan();
    }
    
    // Method untuk menampilkan data tanpa update label info
    private void tampilkanDataSaja() {
        // Override DefaultTableModel agar tabel tidak bisa diedit
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Semua cell tidak bisa diedit
            }
        };
        
        model.addColumn("No");
        model.addColumn("Kode Booking");
        model.addColumn("Tanggal");
        model.addColumn("Maskapai");
        model.addColumn("No. Penerbangan");
        model.addColumn("Rute");
        model.addColumn("Penumpang");
        model.addColumn("Jumlah Tiket");
        model.addColumn("Total Bayar");
        model.addColumn("Status");
        
        try {
            String sql = "SELECT p.*, r.nomor_penerbangan, r.kota_asal, r.kota_tujuan, m.nama_maskapai " +
                        "FROM pemesanan_tiket p " +
                        "INNER JOIN rute_penerbangan r ON p.id_rute = r.id_rute " +
                        "INNER JOIN maskapai m ON r.id_maskapai = m.id_maskapai " +
                        "WHERE p.tanggal_pemesanan BETWEEN ? AND ? " +
                        "ORDER BY p.tanggal_pemesanan DESC";
            
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txt_tanggal_awal.getText().trim());
            pst.setString(2, txt_tanggal_akhir.getText().trim());
            ResultSet rs = pst.executeQuery();
            
            int no = 1;
            double grandTotal = 0;
            
            while (rs.next()) {
                String rute = rs.getString("kota_asal") + " → " + rs.getString("kota_tujuan");
                double total = rs.getDouble("total_bayar");
                grandTotal += total;
                
                model.addRow(new Object[]{
                    no++,
                    rs.getString("kode_booking"),
                    rs.getString("tanggal_pemesanan"),
                    rs.getString("nama_maskapai"),
                    rs.getString("nomor_penerbangan"),
                    rute,
                    rs.getString("nama_penumpang"),
                    rs.getString("jumlah_tiket"),
                    "Rp " + String.format("%,.0f", total),
                    rs.getString("status_pembayaran")
                });
            }
            
            // Tambahkan baris total
            model.addRow(new Object[]{
                "", "", "", "", "", "", "TOTAL:", "", 
                "Rp " + String.format("%,.0f", grandTotal), ""
            });
            
            tbl_laporan.setModel(model);
            
            // JANGAN update lbl_info di sini (biarkan kosong)
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error menampilkan data: " + e.getMessage());
        }
    }

    
    // Method untuk menampilkan laporan
    private void tampilkanLaporan() {
        DefaultTableModel model = new DefaultTableModel(){
        @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Semua cell tidak bisa diedit
            }};
        model.addColumn("No");
        model.addColumn("Kode Booking");
        model.addColumn("Tanggal");
        model.addColumn("Maskapai");
        model.addColumn("No. Penerbangan");
        model.addColumn("Rute");
        model.addColumn("Penumpang");
        model.addColumn("Jumlah Tiket");
        model.addColumn("Total Bayar");
        model.addColumn("Status");
        
        try {
            String sql = "SELECT p.*, r.nomor_penerbangan, r.kota_asal, r.kota_tujuan, m.nama_maskapai " +
                        "FROM pemesanan_tiket p " +
                        "INNER JOIN rute_penerbangan r ON p.id_rute = r.id_rute " +
                        "INNER JOIN maskapai m ON r.id_maskapai = m.id_maskapai " +
                        "WHERE p.tanggal_pemesanan BETWEEN ? AND ? " +
                        "ORDER BY p.tanggal_pemesanan DESC";
            
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txt_tanggal_awal.getText().trim());
            pst.setString(2, txt_tanggal_akhir.getText().trim());
            ResultSet rs = pst.executeQuery();
            
            int no = 1;
            double grandTotal = 0;
            
            while (rs.next()) {
                String rute = rs.getString("kota_asal") + " → " + rs.getString("kota_tujuan");
                double total = rs.getDouble("total_bayar");
                grandTotal += total;
                
                model.addRow(new Object[]{
                    no++,
                    rs.getString("kode_booking"),
                    rs.getString("tanggal_pemesanan"),
                    rs.getString("nama_maskapai"),
                    rs.getString("nomor_penerbangan"),
                    rute,
                    rs.getString("nama_penumpang"),
                    rs.getString("jumlah_tiket"),
                    "Rp " + String.format("%,.0f", total),
                    rs.getString("status_pembayaran")
                });
            }
            
            // Tambahkan baris total
            model.addRow(new Object[]{
                "", "", "", "", "", "", "TOTAL:", "", 
                "Rp " + String.format("%,.0f", grandTotal), ""
            });
            
            tbl_laporan.setModel(model);
            
            // Tampilkan info
            lbl_info.setText("Total Transaksi: " + (no - 1) + " | Total Pendapatan: Rp " + 
                           String.format("%,.0f", grandTotal));
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error menampilkan laporan: " + e.getMessage());
        }
    }
    
    // Method untuk export ke CSV
    private void exportToCSV() {
        try {
            // Buat JFileChooser untuk memilih lokasi save
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogTitle("Simpan Laporan CSV");
            
            // Set nama file default
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String defaultFileName = "Laporan_Pendapatan_Maskapai_" + sdf.format(new java.util.Date()) + ".csv";
            fileChooser.setSelectedFile(new java.io.File(defaultFileName));
            
            // Filter hanya file CSV
            javax.swing.filechooser.FileNameExtensionFilter filter = 
                new javax.swing.filechooser.FileNameExtensionFilter("CSV Files (*.csv)", "csv");
            fileChooser.setFileFilter(filter);
            
            // Tampilkan dialog save
            int userSelection = fileChooser.showSaveDialog(this);
            
            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                
                // Pastikan extensi .csv
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".csv")) {
                    filePath += ".csv";
                }
                
                // Tulis file CSV
                FileWriter writer = new FileWriter(filePath);
                
                // Header
                writer.append("LAPORAN PENDAPATAN PER MASKAPAI\n");
                writer.append("Periode: " + txt_tanggal_awal.getText() + " s/d " + txt_tanggal_akhir.getText() + "\n");
                writer.append("Tanggal Cetak: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date()) + "\n\n");
                
                // Column headers
                writer.append("No,Kode Maskapai,Nama Maskapai,Jumlah Penerbangan,Total Tiket Terjual,Total Pendapatan\n");
                
                // Data
                DefaultTableModel model = (DefaultTableModel) tbl_laporan.getModel();
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        writer.append(model.getValueAt(i, j).toString());
                        if (j < model.getColumnCount() - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
                
                writer.flush();
                writer.close();
                
                JOptionPane.showMessageDialog(this, 
                    "Laporan berhasil diekspor!\n" + 
                    "Lokasi: " + filePath,
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
                
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error export: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Method untuk export ke PDF
private void exportToPDF() {
    try {
        // ╔═══════════════════════════════════════════════════════╗
        // ║  TAMBAHKAN JFILECHOOSER DI SINI (BAGIAN BARU)        ║
        // ╚═══════════════════════════════════════════════════════╝
        
        // Buat JFileChooser untuk memilih lokasi save
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan PDF");
        
        // Set nama file default
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String defaultFileName = "Laporan_Pemesanan_" + sdf.format(new java.util.Date()) + ".pdf";
        fileChooser.setSelectedFile(new java.io.File(defaultFileName));
        
        // Filter hanya file PDF
        javax.swing.filechooser.FileNameExtensionFilter filter = 
            new javax.swing.filechooser.FileNameExtensionFilter("PDF Files (*.pdf)", "pdf");
        fileChooser.setFileFilter(filter);
        
        // Tampilkan dialog save
        int userSelection = fileChooser.showSaveDialog(this);
        
        // Cek apakah user klik Save atau Cancel
        if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            // Pastikan extensi .pdf
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            
            // ╔═══════════════════════════════════════════════════════╗
            // ║  KODE GENERATE PDF (SAMA SEPERTI SEBELUMNYA)        ║
            // ╚═══════════════════════════════════════════════════════╝
            
            // Buat dokumen PDF
            Document document = new Document(PageSize.A4.rotate()); // Landscape
            PdfWriter.getInstance(document, new FileOutputStream(filePath)); // ← Pakai filePath dari dialog
            document.open();
            
            // Font
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
            Font fontContent = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
            Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
            
            // Judul
            Paragraph title = new Paragraph("LAPORAN PEMESANAN TIKET PESAWAT", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);
            
            // Info periode
            Paragraph periode = new Paragraph(
                "Periode: " + txt_tanggal_awal.getText() + " s/d " + txt_tanggal_akhir.getText(), 
                fontContent
            );
            periode.setAlignment(Element.ALIGN_CENTER);
            document.add(periode);
            
            Paragraph tanggalCetak = new Paragraph(
                "Tanggal Cetak: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date()), 
                fontContent
            );
            tanggalCetak.setAlignment(Element.ALIGN_CENTER);
            tanggalCetak.setSpacingAfter(15);
            document.add(tanggalCetak);
            
            // Buat tabel
            PdfPTable pdfTable = new PdfPTable(10); // 10 kolom
            pdfTable.setWidthPercentage(100);
            pdfTable.setSpacingBefore(10f);
            pdfTable.setSpacingAfter(10f);
            
            // Set lebar kolom
            float[] columnWidths = {3f, 8f, 7f, 10f, 8f, 12f, 12f, 5f, 10f, 8f};
            pdfTable.setWidths(columnWidths);
            
            // Header tabel dengan background biru
            String[] headers = {"No", "Kode Booking", "Tanggal", "Maskapai", "No. Penerbangan", 
                              "Rute", "Penumpang", "Jml", "Total Bayar", "Status"};
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
                cell.setBackgroundColor(new BaseColor(52, 152, 219)); // Biru
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(5);
                pdfTable.addCell(cell);
            }
            
            // Data dari tabel
            DefaultTableModel model = (DefaultTableModel) tbl_laporan.getModel();
            int rowCount = model.getRowCount();
            
            for (int i = 0; i < rowCount - 1; i++) { // -1 untuk skip baris total
                for (int j = 0; j < model.getColumnCount(); j++) {
                    PdfPCell cell = new PdfPCell(new Phrase(
                        model.getValueAt(i, j).toString(), 
                        fontContent
                    ));
                    cell.setPadding(4);
                    
                    // Alignment
                    if (j == 0 || j == 7) { // No dan Jumlah (center)
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else if (j == 8) { // Total Bayar (right)
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    } else {
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    }
                    
                    // Alternating row colors
                    if (i % 2 == 0) {
                        cell.setBackgroundColor(new BaseColor(245, 245, 245));
                    }
                    
                    pdfTable.addCell(cell);
                }
            }
            
            // Baris Total
            for (int j = 0; j < model.getColumnCount(); j++) {
                PdfPCell cell = new PdfPCell(new Phrase(
                    model.getValueAt(rowCount - 1, j).toString(), 
                    fontTotal
                ));
                cell.setPadding(5);
                cell.setBackgroundColor(new BaseColor(255, 235, 156)); // Kuning muda
                
                if (j == 8) {
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                } else if (j == 6) {
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                } else {
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                }
                
                pdfTable.addCell(cell);
            }
            
            document.add(pdfTable);
            
            // Footer info
            Paragraph footer = new Paragraph(lbl_info.getText(), fontContent);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(15);
            document.add(footer);
            
            document.close();
            
            // ╔═══════════════════════════════════════════════════════╗
            // ║  NOTIFIKASI DENGAN LOKASI FILE (BAGIAN BARU)        ║
            // ╚═══════════════════════════════════════════════════════╝
            JOptionPane.showMessageDialog(this, 
                "Laporan berhasil diekspor ke PDF!\n" +
                "Lokasi: " + filePath, 
                "Sukses", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        // Jika user klik Cancel, tidak ada yang terjadi (normal)
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Error export PDF: " + e.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
    
    // Method untuk menampilkan detail per maskapai
    private void tampilkanDetail() {
        int row = tbl_laporan.getSelectedRow();
        if (row != -1 && row < tbl_laporan.getRowCount() - 1) {
            String kodeMaskapai = tbl_laporan.getValueAt(row, 1).toString();
            String namaMaskapai = tbl_laporan.getValueAt(row, 2).toString();
            
            // Buat dialog untuk menampilkan detail
            DefaultTableModel detailModel = new DefaultTableModel();
            detailModel.addColumn("Tanggal");
            detailModel.addColumn("Kode Booking");
            detailModel.addColumn("Rute");
            detailModel.addColumn("Penumpang");
            detailModel.addColumn("Jumlah");
            detailModel.addColumn("Total");
            
            try {
                String sql = "SELECT p.tanggal_pemesanan, p.kode_booking, " +
                            "CONCAT(r.kota_asal, ' → ', r.kota_tujuan) as rute, " +
                            "p.nama_penumpang, p.jumlah_tiket, p.total_bayar " +
                            "FROM pemesanan_tiket p " +
                            "INNER JOIN rute_penerbangan r ON p.id_rute = r.id_rute " +
                            "INNER JOIN maskapai m ON r.id_maskapai = m.id_maskapai " +
                            "WHERE m.kode_maskapai = ? " +
                            "AND p.tanggal_pemesanan BETWEEN ? AND ? " +
                            "ORDER BY p.tanggal_pemesanan DESC";
                
                Connection conn = Koneksi.getKoneksi();
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, kodeMaskapai);
                pst.setString(2, txt_tanggal_awal.getText().trim());
                pst.setString(3, txt_tanggal_akhir.getText().trim());
                ResultSet rs = pst.executeQuery();
                
                while (rs.next()) {
                    detailModel.addRow(new Object[]{
                        rs.getString("tanggal_pemesanan"),
                        rs.getString("kode_booking"),
                        rs.getString("rute"),
                        rs.getString("nama_penumpang"),
                        rs.getString("jumlah_tiket"),
                        "Rp " + String.format("%,.0f", rs.getDouble("total_bayar"))
                    });
                }
                
                // Tampilkan dalam dialog (perlu membuat JDialog terpisah)
                JOptionPane.showMessageDialog(this, 
                    "Detail transaksi untuk " + namaMaskapai + "\nTotal: " + detailModel.getRowCount() + " transaksi");
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
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
        txt_tanggal_awal = new javax.swing.JTextField();
        txt_tanggal_akhir = new javax.swing.JTextField();
        btn_tampilkan = new javax.swing.JButton();
        btn_export = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_laporan = new javax.swing.JTable();
        lbl_info = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btn_keluar = new javax.swing.JButton();
        btn_exportPDF = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_tampilkan.setText("Tampilan");
        btn_tampilkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tampilkanActionPerformed(evt);
            }
        });

        btn_export.setText("Export");
        btn_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_exportActionPerformed(evt);
            }
        });

        tbl_laporan.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tbl_laporan);

        lbl_info.setText("jLabel1");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("LAPORAN PEMESANAN ");

        jLabel2.setText("Tanggal Awal :");

        jLabel3.setText("Tanggal Akhir :");

        btn_keluar.setText("Kembali");
        btn_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_keluarActionPerformed(evt);
            }
        });

        btn_exportPDF.setText("PDF");
        btn_exportPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_exportPDFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(161, 161, 161)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txt_tanggal_awal))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_tanggal_akhir)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(lbl_info))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(btn_tampilkan)
                                                .addGap(53, 53, 53)
                                                .addComponent(btn_export)
                                                .addGap(42, 42, 42)
                                                .addComponent(btn_exportPDF)))
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 27, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(142, 142, 142))
            .addGroup(layout.createSequentialGroup()
                .addGap(192, 192, 192)
                .addComponent(btn_keluar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txt_tanggal_awal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txt_tanggal_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btn_export)
                        .addComponent(btn_exportPDF))
                    .addComponent(btn_tampilkan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl_info)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_keluar)
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_tampilkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tampilkanActionPerformed
        if (txt_tanggal_awal.getText().trim().isEmpty() || txt_tanggal_akhir.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tanggal harus diisi!");
            return;
        }
        tampilkanLaporan();
    }//GEN-LAST:event_btn_tampilkanActionPerformed

    private void btn_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_exportActionPerformed
        exportToCSV();
    }//GEN-LAST:event_btn_exportActionPerformed

    private void btn_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_keluarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btn_keluarActionPerformed

    private void btn_exportPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_exportPDFActionPerformed
        exportToPDF();
    }//GEN-LAST:event_btn_exportPDFActionPerformed

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
            java.util.logging.Logger.getLogger(LaporanPemesanan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LaporanPemesanan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LaporanPemesanan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LaporanPemesanan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LaporanPemesanan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_export;
    private javax.swing.JButton btn_exportPDF;
    private javax.swing.JButton btn_keluar;
    private javax.swing.JButton btn_tampilkan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_info;
    private javax.swing.JTable tbl_laporan;
    private javax.swing.JTextField txt_tanggal_akhir;
    private javax.swing.JTextField txt_tanggal_awal;
    // End of variables declaration//GEN-END:variables
}
