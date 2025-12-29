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
public class LaporanPendapatan extends javax.swing.JFrame {

    /**
     * Creates new form LaporanPendapatan
     */
    public LaporanPendapatan() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Laporan Pendapatan per Maskapai");
        
        // Set tanggal default (bulan ini)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        txt_tanggal_awal.setText(sdf.format(cal.getTime()));
        txt_tanggal_akhir.setText(sdf.format(new java.util.Date()));
        
        // Tampilkan data tapi sembunyikan info total
        lbl_info.setText("Klik tombol 'Tampilkan' untuk melihat total pendapatan");
        tampilkanDataSaja();
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
        model.addColumn("Kode Maskapai");
        model.addColumn("Nama Maskapai");
        model.addColumn("Jumlah Penerbangan");
        model.addColumn("Total Tiket Terjual");
        model.addColumn("Total Pendapatan");
        
        try {
            String sql = "SELECT m.kode_maskapai, m.nama_maskapai, " +
                        "COUNT(DISTINCT r.id_rute) as jumlah_penerbangan, " +
                        "COALESCE(SUM(p.jumlah_tiket), 0) as total_tiket, " +
                        "COALESCE(SUM(p.total_bayar), 0) as total_pendapatan " +
                        "FROM maskapai m " +
                        "LEFT JOIN rute_penerbangan r ON m.id_maskapai = r.id_maskapai " +
                        "LEFT JOIN pemesanan_tiket p ON r.id_rute = p.id_rute " +
                        "AND p.tanggal_pemesanan BETWEEN ? AND ? " +
                        "GROUP BY m.id_maskapai, m.kode_maskapai, m.nama_maskapai " +
                        "ORDER BY total_pendapatan DESC";
            
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txt_tanggal_awal.getText().trim());
            pst.setString(2, txt_tanggal_akhir.getText().trim());
            ResultSet rs = pst.executeQuery();
            
            int no = 1;
            double grandTotal = 0;
            int totalTiket = 0;
            
            while (rs.next()) {
                double pendapatan = rs.getDouble("total_pendapatan");
                int tiket = rs.getInt("total_tiket");
                grandTotal += pendapatan;
                totalTiket += tiket;
                
                model.addRow(new Object[]{
                    no++,
                    rs.getString("kode_maskapai"),
                    rs.getString("nama_maskapai"),
                    rs.getString("jumlah_penerbangan"),
                    tiket,
                    "Rp " + String.format("%,.0f", pendapatan)
                });
            }
            
            // Tambahkan baris total
            model.addRow(new Object[]{
                "", "", "TOTAL:", "", 
                totalTiket,
                "Rp " + String.format("%,.0f", grandTotal)
            });
            
            tbl_laporan.setModel(model);
            
            // JANGAN update lbl_info di sini (biarkan kosong)
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error menampilkan data: " + e.getMessage());
        }
    }
    
    

// Method untuk menampilkan laporan pendapatan per maskapai
    private void tampilkanLaporan() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Semua cell tidak bisa diedit
            }
        };
        model.addColumn("No");
        model.addColumn("Kode Maskapai");
        model.addColumn("Nama Maskapai");
        model.addColumn("Jumlah Penerbangan");
        model.addColumn("Total Tiket Terjual");
        model.addColumn("Total Pendapatan");
        
        
        
        try {
            String sql = "SELECT m.kode_maskapai, m.nama_maskapai, " +
                        "COUNT(DISTINCT r.id_rute) as jumlah_penerbangan, " +
                        "SUM(p.jumlah_tiket) as total_tiket, " +
                        "SUM(p.total_bayar) as total_pendapatan " +
                        "FROM maskapai m " +
                        "LEFT JOIN rute_penerbangan r ON m.id_maskapai = r.id_maskapai " +
                        "LEFT JOIN pemesanan_tiket p ON r.id_rute = p.id_rute " +
                        "AND p.tanggal_pemesanan BETWEEN ? AND ? " +
                        "GROUP BY m.id_maskapai, m.kode_maskapai, m.nama_maskapai " +
                        "ORDER BY total_pendapatan DESC";
            
            Connection conn = Koneksi.getKoneksi();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txt_tanggal_awal.getText().trim());
            pst.setString(2, txt_tanggal_akhir.getText().trim());
            ResultSet rs = pst.executeQuery();
            
            int no = 1;
            double grandTotal = 0;
            int totalTiket = 0;
            
            while (rs.next()) {
                double pendapatan = rs.getDouble("total_pendapatan");
                int tiket = rs.getInt("total_tiket");
                grandTotal += pendapatan;
                totalTiket += tiket;
                
                model.addRow(new Object[]{
                    no++,
                    rs.getString("kode_maskapai"),
                    rs.getString("nama_maskapai"),
                    rs.getString("jumlah_penerbangan"),
                    tiket,
                    "Rp " + String.format("%,.0f", pendapatan)
                });
            }
            
            // Tambahkan baris total
            model.addRow(new Object[]{
                "", "", "TOTAL:", "", 
                totalTiket,
                "Rp " + String.format("%,.0f", grandTotal)
            });
            
            tbl_laporan.setModel(model);
            
            // Tampilkan info
            lbl_info.setText("Total Maskapai: " + (no - 1) + " | Total Tiket Terjual: " + totalTiket + 
                           " | Total Pendapatan: Rp " + String.format("%,.0f", grandTotal));
            
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
        // ║  JFILECHOOSER - PILIH LOKASI SAVE                   ║
        // ╚═══════════════════════════════════════════════════════╝
        
        // Buat JFileChooser untuk memilih lokasi save
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan PDF");
        
        // Set nama file default
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String defaultFileName = "Laporan_Pendapatan_Maskapai_" + sdf.format(new java.util.Date()) + ".pdf";
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
            // ║  GENERATE PDF - LAPORAN PENDAPATAN                  ║
            // ╚═══════════════════════════════════════════════════════╝
            
            // Buat dokumen PDF
            Document document = new Document(PageSize.A4); // Portrait
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            
            // Font
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.DARK_GRAY);
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
            Font fontContent = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
            Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);
            
            // Judul
            Paragraph title = new Paragraph("LAPORAN PENDAPATAN PER MASKAPAI", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);
            
            // Info periode
            Paragraph periode = new Paragraph(
                "Periode: " + txt_tanggal_awal.getText() + " s/d " + txt_tanggal_akhir.getText(), 
                fontSubtitle
            );
            periode.setAlignment(Element.ALIGN_CENTER);
            document.add(periode);
            
            Paragraph tanggalCetak = new Paragraph(
                "Tanggal Cetak: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date()), 
                fontSubtitle
            );
            tanggalCetak.setAlignment(Element.ALIGN_CENTER);
            tanggalCetak.setSpacingAfter(20);
            document.add(tanggalCetak);
            
            // Buat tabel
            PdfPTable pdfTable = new PdfPTable(6); // 6 kolom
            pdfTable.setWidthPercentage(100);
            pdfTable.setSpacingBefore(10f);
            pdfTable.setSpacingAfter(10f);
            
            // Set lebar kolom
            float[] columnWidths = {5f, 12f, 25f, 15f, 15f, 20f};
            pdfTable.setWidths(columnWidths);
            
            // Header tabel dengan background hijau
            String[] headers = {"No", "Kode", "Nama Maskapai", "Jml Penerbangan", 
                              "Tiket Terjual", "Total Pendapatan"};
            
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
                cell.setBackgroundColor(new BaseColor(46, 204, 113)); // Hijau
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(8);
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
                    cell.setPadding(6);
                    
                    // Alignment
                    if (j == 0 || j == 3 || j == 4) { // No, Jumlah Penerbangan, Tiket (center)
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else if (j == 5) { // Total Pendapatan (right)
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    } else {
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    }
                    
                    // Alternating row colors
                    if (i % 2 == 0) {
                        cell.setBackgroundColor(new BaseColor(248, 249, 250));
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
                cell.setPadding(8);
                cell.setBackgroundColor(new BaseColor(255, 243, 205)); // Kuning soft
                
                if (j == 2) { // TOTAL (center)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                } else if (j == 4) { // Total tiket (center)
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                } else if (j == 5) { // Total pendapatan (right)
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                } else {
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                }
                
                pdfTable.addCell(cell);
            }
            
            document.add(pdfTable);
            
            // Summary Box
            document.add(new Paragraph(" ")); // Spacing
            
            PdfPTable summaryTable = new PdfPTable(1);
            summaryTable.setWidthPercentage(100);
            
            PdfPCell summaryCell = new PdfPCell(new Phrase(lbl_info.getText(), fontTotal));
            summaryCell.setBackgroundColor(new BaseColor(236, 240, 241));
            summaryCell.setPadding(12);
            summaryCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            summaryTable.addCell(summaryCell);
            
            document.add(summaryTable);
            
            // Footer
            Paragraph footer = new Paragraph(
                "\n\nDicetak oleh Sistem Informasi Penjualan Tiket Pesawat - NPM: 2310010024", 
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY)
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            
            document.close();
            
            // ╔═══════════════════════════════════════════════════════╗
            // ║  NOTIFIKASI SUKSES DENGAN LOKASI FILE               ║
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_laporan = new javax.swing.JTable();
        lbl_info = new javax.swing.JLabel();
        btn_tampilkan = new javax.swing.JButton();
        btn_export = new javax.swing.JButton();
        btn_detail = new javax.swing.JButton();
        btn_kembali = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btn_exportPDF = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        tbl_laporan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_laporanMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_laporan);

        lbl_info.setText("jLabel1");

        btn_tampilkan.setText("Tampilkan");
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

        btn_detail.setText("Detail");
        btn_detail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_detailActionPerformed(evt);
            }
        });

        btn_kembali.setText("Kembali");
        btn_kembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_kembaliActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("LAPORAN PENDAPATAN");

        jLabel2.setText("Tanggal Awal : ");

        jLabel3.setText("Tanggal Akhir :");

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(128, 128, 128))
            .addGroup(layout.createSequentialGroup()
                .addGap(186, 186, 186)
                .addComponent(btn_kembali)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txt_tanggal_awal, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(txt_tanggal_akhir)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(btn_tampilkan)
                        .addGap(31, 31, 31)
                        .addComponent(btn_detail)
                        .addGap(48, 48, 48)
                        .addComponent(btn_export)
                        .addGap(18, 18, 18)
                        .addComponent(btn_exportPDF))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(169, 169, 169)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 511, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(lbl_info)))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txt_tanggal_awal, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_tanggal_akhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btn_export)
                                .addComponent(btn_exportPDF))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btn_tampilkan)
                                .addComponent(btn_detail)))))
                .addGap(27, 27, 27)
                .addComponent(lbl_info)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_kembali)
                .addContainerGap(20, Short.MAX_VALUE))
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

    private void btn_detailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_detailActionPerformed
        tampilkanDetail();
    }//GEN-LAST:event_btn_detailActionPerformed

    private void tbl_laporanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_laporanMouseClicked
        if (evt.getClickCount() == 2) { // Double click
            tampilkanDetail();
        }
    }//GEN-LAST:event_tbl_laporanMouseClicked

    private void btn_kembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_kembaliActionPerformed
       this.dispose();
    }//GEN-LAST:event_btn_kembaliActionPerformed

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
            java.util.logging.Logger.getLogger(LaporanPendapatan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LaporanPendapatan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LaporanPendapatan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LaporanPendapatan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LaporanPendapatan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_detail;
    private javax.swing.JButton btn_export;
    private javax.swing.JButton btn_exportPDF;
    private javax.swing.JButton btn_kembali;
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
