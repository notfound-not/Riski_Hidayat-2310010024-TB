package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Koneksi {
    private static Connection conn;
    
    public static Connection getKoneksi() {
        if (conn == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/db_tiket_pesawat";
                String user = "root";
                String password = ""; // sesuaikan dengan password MySQL Anda
                
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(url, user, password);
                System.out.println("Koneksi Database Berhasil!");
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Driver tidak ditemukan: " + e.getMessage());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Koneksi Gagal: " + e.getMessage());
            }
        }
        return conn;
    }
}