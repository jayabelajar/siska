
package form;

import javax.swing.JFrame;
import java.awt.CardLayout;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import java.awt.Font;
import java.awt.*;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PiePlot;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;



public class Menu_Utama extends javax.swing.JFrame {
    
    private String namaAdminAktif;
    private JLabel labelWelcome;
    private JLabel labelClock;  

    public void setNamaAdminAktif(String nama) {
    this.namaAdminAktif = nama;
    if (labelWelcome != null) {
        labelWelcome.setText("SELAMAT DATANG " + namaAdminAktif + " DI SISTEM INFORMASI ABSENSI & KARTU IZIN SISWA");
    }
}

    private CardLayout cardLayout; // cardlayout
        
    /**
     * Creates new form Menu_Utama
     */
    
    public Menu_Utama() {
         
        initComponents();
                 
        
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); // Memaksimalkan jendela
        cardLayout = (CardLayout) mainpanel.getLayout(); // Inisialisasi CardLayout
        
        // Di dalam method initComponents(), pada bagian mainpanel.add()
        mainpanel.add(panelberanda, "panelberanda");
        mainpanel.add(panelabsensi, "panelabsensi");
        mainpanel.add(panelkartuizin, "panelkartuizin");
        mainpanel.add(paneldatasiswa, "paneldatasiswa");
        mainpanel.add(paneldataadmin, "paneldataadmin");
        
       // Membuat label Welcome dan Clock
        labelWelcome = new JLabel();
        labelWelcome.setFont(new Font("Tw Cen MT", Font.BOLD, 24));
        labelWelcome.setHorizontalAlignment(SwingConstants.CENTER);

        labelClock = new JLabel();
        labelClock.setFont(new Font("Tw Cen MT", Font.BOLD, 18));
        labelClock.setHorizontalAlignment(SwingConstants.RIGHT);

        // Setting layout untuk panelwelcome
        panelwelcome.setLayout(new BorderLayout());
        panelwelcome.add(labelWelcome, BorderLayout.WEST);
        panelwelcome.add(labelClock, BorderLayout.EAST);

        // Set teks Welcome
        labelWelcome.setText("SELAMAT DATANG " + namaAdminAktif + " DI SISTEM INFORMASI ABSENSI & KARTU IZIN SISWA");

        // Buat jam berjalan
        Timer timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy | HH:mm:ss", new Locale("id", "ID"));
                labelClock.setText(now.format(formatter));
            }
        });
        timer.start();

        // Tampilkan diagram PieChart
        tampilkanDiagram();
        
        filtertanggal.getDateEditor().addPropertyChangeListener(evt -> {
    if ("date".equals(evt.getPropertyName())) {
        filterAbsensiByTanggalDanKelas();
    }
});

        
    }
    


    
    private void tampilkanDiagram() {
        paneldiagram.setLayout(new GridLayout(1, 2)); // 2 kolom untuk 2 chart
        paneldiagram.removeAll(); // Clear panel

        paneldiagram.add(createStatistikPerizinanChart());
        paneldiagram.add(createStatistikSiswaChart());

        paneldiagram.revalidate();
        paneldiagram.repaint();
    }

    // Membuat PieChart Statistik Perizinan
    private ChartPanel createStatistikPerizinanChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        try {
            Connection conn = Koneksi.getConnection();
            String sql = "SELECT Status, COUNT(*) AS jumlah FROM absensi GROUP BY Status";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String status = rs.getString("Status");
                int jumlah = rs.getInt("jumlah");
                dataset.setValue(status + ": " + jumlah + " siswa", jumlah); // Tambahkan jumlah langsung di label
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createPieChart(
            "Statistik Perizinan", dataset, true, true, false
        );

        PiePlot piePlot = (PiePlot) chart.getPlot();
        piePlot.setLabelFont(new Font("Tw Cen MT", Font.BOLD, 16)); // Untuk label pada chart (angka persentase)

        chart.getLegend().setItemFont(new Font("Tw Cen MT", Font.BOLD, 18)); // Untuk font di legend

        return new ChartPanel(chart);
    }


    // Membuat PieChart Statistik Jenis Kelamin Siswa
    private ChartPanel createStatistikSiswaChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        try {
            Connection conn = Koneksi.getConnection();
            String sql = "SELECT jenis_kelamin, COUNT(*) AS jumlah FROM siswa GROUP BY jenis_kelamin";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String jk = rs.getString("jenis_kelamin").equals("L") ? "Laki-Laki" : "Perempuan";
                int jumlah = rs.getInt("jumlah");
                dataset.setValue(jk + ": " + jumlah + " siswa", jumlah);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createPieChart(
            "Statistik Siswa Berdasarkan Jenis Kelamin", dataset, true, true, false
        );

        PiePlot piePlot = (PiePlot) chart.getPlot();
        piePlot.setLabelFont(new Font("Tw Cen MT", Font.BOLD, 16));

        chart.getLegend().setItemFont(new Font("Tw Cen MT", Font.BOLD, 18)); // Ganti legend font

        return new ChartPanel(chart);
    }

    private void filterDataByKelasSiswa(String kelas) {
        try {
            Connection conn = Koneksi.getConnection();
            String query;

            if (kelas.equals("Kelas")) {
                query = "SELECT NIS, Nama, Kelas, jenis_kelamin FROM siswa"; // Ambil semua data
            } else {
                query = "SELECT NIS, Nama, Kelas, jenis_kelamin FROM siswa WHERE Kelas = ?";
            }

            PreparedStatement pst = conn.prepareStatement(query);
            if (!kelas.equals("Kelas")) {
                pst.setString(1, kelas);
            }

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
            model.setRowCount(0); // Bersihkan tabel

            while (rs.next()) {
                String jenisKelamin = rs.getString("jenis_kelamin");
                // Ubah "L" → "Laki-Laki", "P" → "Perempuan"
                String jenisKelaminLabel = jenisKelamin.equals("L") ? "Laki-Laki" : "Perempuan";

                Object[] row = {
                    rs.getInt("NIS"),
                    rs.getString("Nama"),
                    rs.getString("Kelas"),
                    jenisKelaminLabel // Tambahkan jenis kelamin
                };
                model.addRow(row);
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Data tidak ditemukan untuk kelas " + kelas + ".");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private void filterAbsensiByTanggalDanKelas() {
    try {
        java.util.Date selectedDate = filtertanggal.getDate();
        String kelas = filterkelas.getSelectedItem().toString();

        if (selectedDate == null || kelas.equals("KELAS")) {
            JOptionPane.showMessageDialog(null, "Pilih tanggal dan kelas terlebih dahulu.");
            return;
        }

        String tanggal = new java.sql.Date(selectedDate.getTime()).toString();

        Connection conn = Koneksi.getConnection();
        String sql = "SELECT a.NIS, s.Nama, s.Kelas, a.Status, a.Waktu_Masuk, a.Waktu_Pulang, a.Tanggal " +
                     "FROM absensi a JOIN siswa s ON a.NIS = s.NIS " +
                     "WHERE a.Tanggal = ? AND s.Kelas = ? " +
                     "GROUP BY a.NIS";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, tanggal);
        pst.setString(2, kelas);

        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("NIS"),
                rs.getString("Nama"),
                rs.getString("Kelas"),
                rs.getString("Status"),
                rs.getTime("Waktu_Masuk"),
                rs.getTime("Waktu_Pulang"),
                rs.getDate("Tanggal")
            });
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Tidak ada data untuk tanggal dan kelas yang dipilih.");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Gagal memfilter absensi: " + e.getMessage());
    }
}

    
    // pencarian
    private void searchByNISOrName(String searchTerm) {
    if (searchTerm != null && !searchTerm.trim().isEmpty()) {
        try {
                Connection conn = Koneksi.getConnection();
                String query = "SELECT a.NIS, s.Nama, s.Kelas, a.Status, a.Waktu_Masuk, a.Waktu_Pulang " +
                               "FROM absensi a JOIN siswa s ON a.NIS = s.NIS " +
                               "WHERE a.NIS = ? OR s.Nama LIKE ?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, searchTerm);
                pst.setString(2, "%" + searchTerm + "%"); // Mencari berdasarkan nama

                ResultSet rs = pst.executeQuery();

                // Mengatur model tabel untuk menampilkan hasil pencarian
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                model.setRowCount(0); // Clear existing data

                // Menambahkan hasil pencarian ke model tabel
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("NIS"),
                        rs.getString("Nama"),
                        rs.getString("Kelas"),
                        rs.getString("Status"),
                        rs.getTime("Waktu_Masuk"),
                        rs.getTime("Waktu_Pulang")
                    };
                    model.addRow(row); // Menambahkan data ke model JTable
                }
            
                // Jika tidak ada hasil
                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(null, "Data tidak ditemukan.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Masukkan NIS atau Nama Siswa yang valid.");
        }
    }
    
    // filter combobox kelas
    private void filterDataByKelas(String kelas) {
    try {
            Connection conn = Koneksi.getConnection();
            String query;
            if (kelas.equals("Semua")) {
                query = "SELECT a.NIS, s.Nama, s.Kelas, a.Status, a.Waktu_Masuk, a.Waktu_Pulang " +
                        "FROM absensi a JOIN siswa s ON a.NIS = s.NIS";
            } else {
                query = "SELECT a.NIS, s.Nama, s.Kelas, a.Status, a.Waktu_Masuk, a.Waktu_Pulang " +
                        "FROM absensi a JOIN siswa s ON a.NIS = s.NIS WHERE s.Kelas = ?";
            }
            PreparedStatement pst = conn.prepareStatement(query);
            if (!kelas.equals("Semua")) {
                pst.setString(1, kelas);
            }
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0); // Clear existing data

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("NIS"),
                    rs.getString("Nama"),
                    rs.getString("Kelas"),
                    rs.getString("Status"),
                    rs.getTime("Waktu_Masuk"),
                    rs.getTime("Waktu_Pulang")
                };
                model.addRow(row); // Menambahkan data ke model JTable
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
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

        panelheader = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        panelsidebar = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        panelmenu = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        mainpanel = new javax.swing.JPanel();
        panelberanda = new javax.swing.JPanel();
        panelwelcome = new javax.swing.JPanel();
        paneldiagram = new javax.swing.JPanel();
        panelabsensi = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        filterkelas = new javax.swing.JComboBox<>();
        jTextField1 = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        filtertanggal = new com.toedter.calendar.JDateChooser();
        rekapbulan = new com.toedter.calendar.JMonthChooser();
        rekapsemester = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jButton29 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        filterkelas_rekapsemester = new javax.swing.JComboBox<>();
        filterkelas_rekapbulanan = new javax.swing.JComboBox<>();
        panelkartuizin = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jTextField3 = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        paneldatasiswa = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jComboBox2 = new javax.swing.JComboBox<>();
        jTextField2 = new javax.swing.JTextField();
        jButton8 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        paneldataadmin = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        panelfooter = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelheader.setBackground(new java.awt.Color(0, 153, 102));

        jLabel3.setFont(new java.awt.Font("Tw Cen MT", 1, 23)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("SISTEM INFORMASI ABSENSI DAN KARTU IZIN SISWA");

        jLabel4.setFont(new java.awt.Font("Tw Cen MT", 1, 23)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("MTS. MAMBAUL FALAH GRESIK");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/mafal-4.png"))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Tw Cen MT", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Jl. KH. Burhan Al-Mansur Tambilung Sukaoneng Tambak, Bawean, Sukaoneng, Gresik");

        javax.swing.GroupLayout panelheaderLayout = new javax.swing.GroupLayout(panelheader);
        panelheader.setLayout(panelheaderLayout);
        panelheaderLayout.setHorizontalGroup(
            panelheaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelheaderLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel5)
                .addGap(29, 29, 29)
                .addGroup(panelheaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelheaderLayout.setVerticalGroup(
            panelheaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelheaderLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(panelheaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(panelheaderLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        panelsidebar.setBackground(new java.awt.Color(0, 153, 102));

        jButton1.setFont(new java.awt.Font("Tw Cen MT", 1, 15)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/beranda.png"))); // NOI18N
        jButton1.setText("BERANDA");
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton1.setInheritsPopupMenu(true);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Tw Cen MT", 1, 15)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/absensi.png"))); // NOI18N
        jButton2.setText("DATA ABSENSI");
        jButton2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Tw Cen MT", 1, 15)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/kartuizin.png"))); // NOI18N
        jButton3.setText("KARTU IZIN");
        jButton3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Tw Cen MT", 1, 15)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/siswa.png"))); // NOI18N
        jButton4.setText("DATA SISWA");
        jButton4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Tw Cen MT", 1, 15)); // NOI18N
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/admin.png"))); // NOI18N
        jButton5.setText("DATA ADMIN");
        jButton5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        panelmenu.setBackground(new java.awt.Color(255, 255, 255));
        panelmenu.setForeground(new java.awt.Color(0, 153, 102));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel2.setText("MENU UTAMA");

        javax.swing.GroupLayout panelmenuLayout = new javax.swing.GroupLayout(panelmenu);
        panelmenu.setLayout(panelmenuLayout);
        panelmenuLayout.setHorizontalGroup(
            panelmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelmenuLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelmenuLayout.setVerticalGroup(
            panelmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelmenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton6.setFont(new java.awt.Font("Tw Cen MT", 1, 15)); // NOI18N
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/keluar.png"))); // NOI18N
        jButton6.setText("KELUAR");
        jButton6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelsidebarLayout = new javax.swing.GroupLayout(panelsidebar);
        panelsidebar.setLayout(panelsidebarLayout);
        panelsidebarLayout.setHorizontalGroup(
            panelsidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelmenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelsidebarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelsidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelsidebarLayout.setVerticalGroup(
            panelsidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelsidebarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelmenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainpanel.setBackground(new java.awt.Color(255, 255, 255));
        mainpanel.setLayout(new java.awt.CardLayout());

        panelberanda.setBackground(new java.awt.Color(255, 255, 255));

        panelwelcome.setBackground(new java.awt.Color(255, 255, 255));
        panelwelcome.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelwelcomeLayout = new javax.swing.GroupLayout(panelwelcome);
        panelwelcome.setLayout(panelwelcomeLayout);
        panelwelcomeLayout.setHorizontalGroup(
            panelwelcomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelwelcomeLayout.setVerticalGroup(
            panelwelcomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 88, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout paneldiagramLayout = new javax.swing.GroupLayout(paneldiagram);
        paneldiagram.setLayout(paneldiagramLayout);
        paneldiagramLayout.setHorizontalGroup(
            paneldiagramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 635, Short.MAX_VALUE)
        );
        paneldiagramLayout.setVerticalGroup(
            paneldiagramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 346, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelberandaLayout = new javax.swing.GroupLayout(panelberanda);
        panelberanda.setLayout(panelberandaLayout);
        panelberandaLayout.setHorizontalGroup(
            panelberandaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelberandaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelberandaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelwelcome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paneldiagram, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelberandaLayout.setVerticalGroup(
            panelberandaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelberandaLayout.createSequentialGroup()
                .addComponent(panelwelcome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paneldiagram, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainpanel.add(panelberanda, "card2");

        panelabsensi.setBackground(new java.awt.Color(255, 255, 255));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("DATA ABSENSI HARI INI");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "NIS", "NAMA SISWA", "KELAS", "STATUS", "WAKTU MASUK", "WAKTU PULANG", "TANGGAL"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        filterkelas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "KELAS", "7", "8", "9" }));
        filterkelas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterkelasActionPerformed(evt);
            }
        });

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/search.png"))); // NOI18N
        jButton7.setText("CARI");
        jButton7.setHideActionText(true);
        jButton7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/lens.png"))); // NOI18N
        jButton10.setText("TAMPIL");
        jButton10.setHideActionText(true);
        jButton10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButton10.setInheritsPopupMenu(true);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/add.png"))); // NOI18N
        jButton11.setText("TAMBAH");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/edit.png"))); // NOI18N
        jButton12.setText("UBAH");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/trash.png"))); // NOI18N
        jButton13.setText("HAPUS");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/print.png"))); // NOI18N
        jButton14.setText("CETAK");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/update.png"))); // NOI18N
        jButton28.setText("PERBARUI");
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        rekapsemester.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SEMESTER", "SEMESTER 1", "SEMESTER 2" }));
        rekapsemester.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rekapsemesterActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("REKAP ABSENSI");

        jButton29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/print.png"))); // NOI18N
        jButton29.setText("CETAK");
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jButton30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/print.png"))); // NOI18N
        jButton30.setText("CETAK");
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        filterkelas_rekapsemester.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "KELAS", "7", "8", "9" }));

        filterkelas_rekapbulanan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "KELAS", "7", "8", "9" }));
        filterkelas_rekapbulanan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterkelas_rekapbulananActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelabsensiLayout = new javax.swing.GroupLayout(panelabsensi);
        panelabsensi.setLayout(panelabsensiLayout);
        panelabsensiLayout.setHorizontalGroup(
            panelabsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelabsensiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelabsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelabsensiLayout.createSequentialGroup()
                        .addGroup(panelabsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelabsensiLayout.createSequentialGroup()
                                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton11))
                            .addGroup(panelabsensiLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelabsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(rekapsemester, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(rekapbulan, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(panelabsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(filterkelas_rekapsemester, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(filterkelas_rekapbulanan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelabsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton29)
                            .addComponent(jButton30)
                            .addGroup(panelabsensiLayout.createSequentialGroup()
                                .addComponent(jButton12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton14)))
                        .addGap(125, 125, 125))
                    .addGroup(panelabsensiLayout.createSequentialGroup()
                        .addGroup(panelabsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(panelabsensiLayout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(filtertanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(filterkelas, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton7)))
                        .addContainerGap())))
        );
        panelabsensiLayout.setVerticalGroup(
            panelabsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelabsensiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelabsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filtertanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelabsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(filterkelas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton7)
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelabsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton10)
                    .addComponent(jButton11)
                    .addComponent(jButton12)
                    .addComponent(jButton13)
                    .addComponent(jButton14)
                    .addComponent(jButton28))
                .addGap(30, 30, 30)
                .addGroup(panelabsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(rekapbulan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelabsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton30)
                        .addComponent(filterkelas_rekapbulanan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelabsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelabsensiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rekapsemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(filterkelas_rekapsemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton29))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        mainpanel.add(panelabsensi, "card2");

        panelkartuizin.setBackground(new java.awt.Color(255, 255, 255));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("KARTU IZIN");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "NIS", "NAMA SISWA", "KELAS", "STATUS", "JAM KELUAR", "JAM KEMBALI", "KETERANGAN"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/search.png"))); // NOI18N
        jButton9.setText("CARI");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/lens.png"))); // NOI18N
        jButton15.setText("TAMPILKAN");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/add.png"))); // NOI18N
        jButton16.setText("TAMBAH");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/edit.png"))); // NOI18N
        jButton17.setText("UBAH");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/trash.png"))); // NOI18N
        jButton18.setText("HAPUS");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/print.png"))); // NOI18N
        jButton19.setText("CETAK");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelkartuizinLayout = new javax.swing.GroupLayout(panelkartuizin);
        panelkartuizin.setLayout(panelkartuizinLayout);
        panelkartuizinLayout.setHorizontalGroup(
            panelkartuizinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelkartuizinLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelkartuizinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                    .addGroup(panelkartuizinLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9))
                    .addGroup(panelkartuizinLayout.createSequentialGroup()
                        .addComponent(jButton15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton19)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelkartuizinLayout.setVerticalGroup(
            panelkartuizinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelkartuizinLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelkartuizinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelkartuizinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton15)
                    .addComponent(jButton16)
                    .addComponent(jButton17)
                    .addComponent(jButton18)
                    .addComponent(jButton19))
                .addContainerGap(72, Short.MAX_VALUE))
        );

        mainpanel.add(panelkartuizin, "card2");

        paneldatasiswa.setBackground(new java.awt.Color(255, 255, 255));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("DATA SISWA");

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "NIS", "NAMA SISWA", "KELAS", "JENIS KELAMIN"
            }
        ));
        jScrollPane3.setViewportView(jTable3);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "KELAS", "7", "8", "9" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/search.png"))); // NOI18N
        jButton8.setText("CARI");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/lens.png"))); // NOI18N
        jButton20.setText("TAMPILKAN");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/add.png"))); // NOI18N
        jButton21.setText("TAMBAH");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jButton22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/edit.png"))); // NOI18N
        jButton22.setText("UBAH");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/trash.png"))); // NOI18N
        jButton23.setText("HAPUS");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout paneldatasiswaLayout = new javax.swing.GroupLayout(paneldatasiswa);
        paneldatasiswa.setLayout(paneldatasiswaLayout);
        paneldatasiswaLayout.setHorizontalGroup(
            paneldatasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneldatasiswaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneldatasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                    .addGroup(paneldatasiswaLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8))
                    .addGroup(paneldatasiswaLayout.createSequentialGroup()
                        .addComponent(jButton20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton23)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        paneldatasiswaLayout.setVerticalGroup(
            paneldatasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneldatasiswaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneldatasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(paneldatasiswaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton20)
                    .addComponent(jButton21)
                    .addComponent(jButton22)
                    .addComponent(jButton23))
                .addContainerGap(70, Short.MAX_VALUE))
        );

        mainpanel.add(paneldatasiswa, "card2");

        paneldataadmin.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setText("DATA ADMIN");

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "NAMA ADMIN", "USERNAME", "PASSWORD", "RFID"
            }
        ));
        jScrollPane4.setViewportView(jTable4);

        jButton24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/lens.png"))); // NOI18N
        jButton24.setText("TAMPILKAN");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jButton25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/add.png"))); // NOI18N
        jButton25.setText("TAMBAH");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jButton26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/edit.png"))); // NOI18N
        jButton26.setText("EDIT");
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        jButton27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/trash.png"))); // NOI18N
        jButton27.setText("HAPUS");
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout paneldataadminLayout = new javax.swing.GroupLayout(paneldataadmin);
        paneldataadmin.setLayout(paneldataadminLayout);
        paneldataadminLayout.setHorizontalGroup(
            paneldataadminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneldataadminLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paneldataadminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 635, Short.MAX_VALUE)
                    .addGroup(paneldataadminLayout.createSequentialGroup()
                        .addGroup(paneldataadminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addGroup(paneldataadminLayout.createSequentialGroup()
                                .addComponent(jButton24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton27)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        paneldataadminLayout.setVerticalGroup(
            paneldataadminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneldataadminLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(paneldataadminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton24)
                    .addComponent(jButton25)
                    .addComponent(jButton26)
                    .addComponent(jButton27))
                .addContainerGap(72, Short.MAX_VALUE))
        );

        mainpanel.add(paneldataadmin, "card2");

        panelfooter.setBackground(new java.awt.Color(0, 153, 102));

        jLabel1.setFont(new java.awt.Font("Tw Cen MT", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Created by Team SISKA TIF SDA");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Copyright © 2025 Politeknik Negeri Jember. All rights reserved.");

        javax.swing.GroupLayout panelfooterLayout = new javax.swing.GroupLayout(panelfooter);
        panelfooter.setLayout(panelfooterLayout);
        panelfooterLayout.setHorizontalGroup(
            panelfooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelfooterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addContainerGap())
        );
        panelfooterLayout.setVerticalGroup(
            panelfooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelfooterLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelheader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelsidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelfooter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelheader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelsidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(mainpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelfooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        cardLayout.show(mainpanel, "panelberanda");
        tampilkanDiagram();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        cardLayout.show(mainpanel, "panelabsensi");
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        cardLayout.show(mainpanel, "panelkartuizin");
        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        cardLayout.show(mainpanel, "paneldatasiswa");
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        cardLayout.show(mainpanel, "paneldataadmin");
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        int result = JOptionPane.showConfirmDialog(
            this,
            "Apakah Anda ingin keluar?",
            "Konfirmasi Keluar",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(
                this,
                "Selamat tinggal, " + namaAdminAktif + "!",
                "Keluar",
                JOptionPane.INFORMATION_MESSAGE
            );

            new Masuk().setVisible(true); // Kembali ke form login
            this.dispose(); // Menutup Menu_Utama
        }

    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        String keyword = jTextField1.getText();
    if (keyword.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Masukkan NIS atau Nama siswa.");
        return;
    }

    try {
        Connection conn = Koneksi.getConnection();
        String query = "SELECT a.NIS, s.Nama, s.Kelas, a.Status, a.Waktu_Masuk, a.Waktu_Pulang, a.Tanggal " +
                       "FROM absensi a JOIN siswa s ON a.NIS = s.NIS " +
                       "WHERE a.NIS LIKE ? OR s.Nama LIKE ?";
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, "%" + keyword + "%");
        pst.setString(2, "%" + keyword + "%");
        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("NIS"),
                rs.getString("Nama"),
                rs.getString("Kelas"),
                rs.getString("Status"),
                rs.getTime("Waktu_Masuk"),
                rs.getTime("Waktu_Pulang"),
                rs.getDate("Tanggal")
            });
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saat mencari: " + e.getMessage());
    }

    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        // TODO add your handling code here:
        int selectedRow = jTable3.getSelectedRow();
        if (selectedRow != -1) {
            int nis = (int) jTable3.getValueAt(selectedRow, 0);
            String nama = (String) jTable3.getValueAt(selectedRow, 1);
            String kelas = (String) jTable3.getValueAt(selectedRow, 2);
            String jenisKelamin = (String) jTable3.getValueAt(selectedRow, 3);

            JTextField namaField = new JTextField(nama);
            String[] kelasOptions = {"7", "8", "9"};
            JComboBox<String> kelasComboBox = new JComboBox<>(kelasOptions);
            kelasComboBox.setSelectedItem(kelas);

            String[] jkOptions = {"L", "P"};
            JComboBox<String> jkComboBox = new JComboBox<>(jkOptions);
            jkComboBox.setSelectedItem(jenisKelamin);

            Object[] message = {
                "NIS: " + nis,
                "Nama:", namaField,
                "Kelas:", kelasComboBox,
                "Jenis Kelamin:", jkComboBox
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Ubah Data Siswa", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    Connection conn = Koneksi.getConnection();
                    String query = "UPDATE siswa SET Nama=?, Kelas=?, jenis_kelamin=? WHERE NIS=?";
                    PreparedStatement pst = conn.prepareStatement(query);
                    pst.setString(1, namaField.getText());
                    pst.setString(2, (String) kelasComboBox.getSelectedItem());
                    pst.setString(3, (String) jkComboBox.getSelectedItem());
                    pst.setInt(4, nis);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Data berhasil diubah!");
                    jButton20ActionPerformed(evt); // Refresh data
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silakan pilih data yang ingin diubah.");
        }


    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
    try {
        Connection conn = Koneksi.getConnection();
        String query = "SELECT a.NIS, s.Nama, s.Kelas, a.Status, a.Waktu_Masuk, a.Waktu_Pulang, a.Tanggal " +
                       "FROM absensi a JOIN siswa s ON a.NIS = s.NIS " +
                       "WHERE a.Tanggal = CURDATE() ORDER BY a.Waktu_Masuk ASC";

        PreparedStatement pst = conn.prepareStatement(query);
        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Bersihkan tabel

        while (rs.next()) {
            Object[] row = {
                rs.getInt("NIS"),
                rs.getString("Nama"),
                rs.getString("Kelas"),
                rs.getString("Status"),
                rs.getTime("Waktu_Masuk"),
                rs.getTime("Waktu_Pulang"),
                rs.getDate("Tanggal")
            };
            model.addRow(row);
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Belum ada data absensi untuk hari ini.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error saat mengambil data: " + e.getMessage());
    }


    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
       
    JTextField nisField = new JTextField();
    JComboBox<String> statusField = new JComboBox<>(new String[]{"Hadir", "Sakit", "Izin", "Alpa"});
    JComboBox<String> waktuMasukCombo = new JComboBox<>(new String[]{"07:00:00", "08:00:00", "09:00:00"});
    JComboBox<String> waktuPulangCombo = new JComboBox<>(new String[]{"13:00:00", "14:00:00", "15:00:00"});
    com.toedter.calendar.JDateChooser tanggalChooser = new com.toedter.calendar.JDateChooser();

    Object[] message = {
        "NIS:", nisField,
        "Status:", statusField,
        "Waktu Masuk:", waktuMasukCombo,
        "Waktu Pulang:", waktuPulangCombo,
        "Tanggal:", tanggalChooser
    };

    int option = JOptionPane.showConfirmDialog(null, message, "Tambah Absensi", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
        try {
            java.util.Date selectedDate = tanggalChooser.getDate();
            java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

            Connection conn = Koneksi.getConnection();
            String query = "INSERT INTO absensi (NIS, Status, Waktu_Masuk, Waktu_Pulang, Tanggal) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, Integer.parseInt(nisField.getText()));
            pst.setString(2, statusField.getSelectedItem().toString());
            pst.setTime(3, Time.valueOf(waktuMasukCombo.getSelectedItem().toString()));
            pst.setTime(4, Time.valueOf(waktuPulangCombo.getSelectedItem().toString()));
            pst.setDate(5, sqlDate);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan!");
            jButton10ActionPerformed(evt);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menambahkan data!");
        }
    }


    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
    int row = jTable1.getSelectedRow();
    if (row != -1) {
        int nis = (int) jTable1.getValueAt(row, 0);
        String status = (String) jTable1.getValueAt(row, 3);
        Time masuk = (Time) jTable1.getValueAt(row, 4);
        Time pulang = (Time) jTable1.getValueAt(row, 5);
        java.sql.Date tanggal = (java.sql.Date) jTable1.getValueAt(row, 6);

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Hadir", "Sakit", "Izin", "Alpa"});
        statusBox.setSelectedItem(status);
        JComboBox<String> waktuMasukCombo = new JComboBox<>(new String[]{"07:00:00", "08:00:00", "09:00:00"});
        waktuMasukCombo.setSelectedItem(masuk.toString());
        JComboBox<String> waktuPulangCombo = new JComboBox<>(new String[]{"13:00:00", "14:00:00", "15:00:00"});
        waktuPulangCombo.setSelectedItem(pulang.toString());
        com.toedter.calendar.JDateChooser tanggalChooser = new com.toedter.calendar.JDateChooser();
        tanggalChooser.setDate(tanggal);

        Object[] input = {
            "Status:", statusBox,
            "Waktu Masuk:", waktuMasukCombo,
            "Waktu Pulang:", waktuPulangCombo,
            "Tanggal:", tanggalChooser
        };

        int confirm = JOptionPane.showConfirmDialog(null, input, "Ubah Data Absensi", JOptionPane.OK_CANCEL_OPTION);
        if (confirm == JOptionPane.OK_OPTION) {
            try {
                Connection conn = Koneksi.getConnection();
                String update = "UPDATE absensi SET Status=?, Waktu_Masuk=?, Waktu_Pulang=?, Tanggal=? WHERE NIS=? AND Tanggal=?";
                PreparedStatement pst = conn.prepareStatement(update);
                pst.setString(1, statusBox.getSelectedItem().toString());
                pst.setTime(2, Time.valueOf(waktuMasukCombo.getSelectedItem().toString()));
                pst.setTime(3, Time.valueOf(waktuPulangCombo.getSelectedItem().toString()));
                pst.setDate(4, new java.sql.Date(tanggalChooser.getDate().getTime()));
                pst.setInt(5, nis);
                pst.setDate(6, tanggal);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Data berhasil diubah!");
                jButton10ActionPerformed(evt);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Gagal mengubah data!");
            }
        }
    } else {
        JOptionPane.showMessageDialog(null, "Pilih baris yang ingin diubah!");
    }


    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
    int row = jTable1.getSelectedRow();
    if (row != -1) {
        int nis = (int) jTable1.getValueAt(row, 0);
        java.sql.Date tanggal = (java.sql.Date) jTable1.getValueAt(row, 6);

        int confirm = JOptionPane.showConfirmDialog(null, "Hapus data absensi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = Koneksi.getConnection();
                String delete = "DELETE FROM absensi WHERE NIS = ? AND Tanggal = ?";
                PreparedStatement pst = conn.prepareStatement(delete);
                pst.setInt(1, nis);
                pst.setDate(2, tanggal);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
                jButton10ActionPerformed(evt);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Gagal menghapus data!");
            }
        }
    } else {
        JOptionPane.showMessageDialog(null, "Pilih baris yang ingin dihapus!");
    }

    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here: 
    try {
        String kelas = filterkelas.getSelectedItem().toString();
        java.util.Date selectedDate = filtertanggal.getDate();

        if (kelas.equals("KELAS")) {
            JOptionPane.showMessageDialog(null, "Pilih kelas terlebih dahulu.");
            return;
        }

        Connection conn = Koneksi.getConnection();

        String sql = "SELECT a.*, s.Nama, s.Kelas FROM absensi a " +
                     "JOIN siswa s ON a.NIS = s.NIS " +
                     "WHERE s.Kelas = ?";

        if (selectedDate != null) {
            sql += " AND a.Tanggal = ?";
        }

        sql += " GROUP BY a.NIS"; // Hindari duplikat NIS

        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, kelas);

        if (selectedDate != null) {
            java.sql.Date tanggal = new java.sql.Date(selectedDate.getTime());
            pst.setDate(2, tanggal);
        }

        ResultSet rs = pst.executeQuery();
        JRResultSetDataSource jasperReports = new JRResultSetDataSource(rs);

        Map<String, Object> param = new HashMap<>();
        param.put("kelas", kelas);
        if (selectedDate != null) {
            param.put("tanggal", new java.sql.Date(selectedDate.getTime()));
        }

        JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/form/absensi.jasper"), param, jasperReports);
        JasperViewer.viewReport(print, false);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Gagal mencetak laporan: " + e.getMessage());
    }


    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        // TODO add your handling code here:
        try {
            Connection conn = Koneksi.getConnection();
            String query = "SELECT k.NIS, s.Nama, s.Kelas, k.Status_Izin, k.Jam_Keluar, k.Jam_Kembali, k.Keterangan " +
                           "FROM kartu_izin k JOIN siswa s ON k.NIS = s.NIS"; // Mengambil data dari tabel kartu_izin dan siswa
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0); // Clear existing data

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("NIS"),
                    rs.getString("Nama"),
                    rs.getString("Kelas"),
                    rs.getString("Status_Izin"),
                    rs.getTime("Jam_Keluar"),
                    rs.getTime("Jam_Kembali"),
                    rs.getString("Keterangan") // Menambahkan kolom Keterangan
                };
                model.addRow(row); // Menambahkan data ke model JTable
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }


    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        // TODO add your handling code here:
        JTextField nisField = new JTextField();
        JComboBox<String> statusField = new JComboBox<>(new String[]{"Sakit", "Izin Pribadi", "Dispensasi", "Lainnya"});

        // Jam Keluar dan Jam Kembali menggunakan JSpinner
        SpinnerDateModel modelJamKeluar = new SpinnerDateModel();
        JSpinner jamKeluarSpinner = new JSpinner(modelJamKeluar);
        jamKeluarSpinner.setValue(new java.util.Date()); // Set jam sekarang

        SpinnerDateModel modelJamKembali = new SpinnerDateModel();
        JSpinner jamKembaliSpinner = new JSpinner(modelJamKembali);
        java.util.Date jamKembaliDefault = new java.util.Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000); // 2 jam setelahnya
        jamKembaliSpinner.setValue(jamKembaliDefault);

        // Format untuk jam
        JSpinner.DateEditor timeEditorKeluar = new JSpinner.DateEditor(jamKeluarSpinner, "HH:mm:ss");
        jamKeluarSpinner.setEditor(timeEditorKeluar);

        JSpinner.DateEditor timeEditorKembali = new JSpinner.DateEditor(jamKembaliSpinner, "HH:mm:ss");
        jamKembaliSpinner.setEditor(timeEditorKembali);

        JTextArea keteranganField = new JTextArea(5, 20); // Membuat form keterangan lebih besar
        keteranganField.setLineWrap(true); // Aktifkan line wrap

        Object[] message = {
            "NIS:", nisField,
            "Status Izin:", statusField,
            "Jam Keluar:", jamKeluarSpinner,
            "Jam Kembali:", jamKembaliSpinner,
            "Keterangan:", keteranganField // Menambahkan Keterangan ke popup
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Input Data Kartu Izin", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Connection conn = Koneksi.getConnection();
                String query = "INSERT INTO kartu_izin (NIS, Status_Izin, Jam_Keluar, Jam_Kembali, Keterangan) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setInt(1, Integer.parseInt(nisField.getText()));
                pst.setString(2, statusField.getSelectedItem().toString());
                pst.setTime(3, new Time(((java.util.Date) jamKeluarSpinner.getValue()).getTime()));
                pst.setTime(4, new Time(((java.util.Date) jamKembaliSpinner.getValue()).getTime()));
                pst.setString(5, keteranganField.getText()); // Menyimpan Keterangan

                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan!");
                jButton15ActionPerformed(evt); // Refresh JTable setelah menambah data
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, "Format waktu tidak valid. Gunakan format HH:mm:ss.");
            }
        }


    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        // TODO add your handling code here:
        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow != -1) {
            int nis = (int) jTable2.getValueAt(selectedRow, 0);
            String statusIzin = (String) jTable2.getValueAt(selectedRow, 3);
            Time jamKeluar = (Time) jTable2.getValueAt(selectedRow, 4);
            Time jamKembali = (Time) jTable2.getValueAt(selectedRow, 5);
            String keterangan = (String) jTable2.getValueAt(selectedRow, 6); // Mengambil Keterangan

            JTextField nisField = new JTextField(String.valueOf(nis));
            nisField.setEditable(false);
            JComboBox<String> statusField = new JComboBox<>(new String[]{"Sakit", "Izin Pribadi", "Dispensasi", "Lainnya"});
            statusField.setSelectedItem(statusIzin);

            // Jam Keluar dan Jam Kembali menggunakan JSpinner
            SpinnerDateModel modelJamKeluar = new SpinnerDateModel();
            JSpinner jamKeluarSpinner = new JSpinner(modelJamKeluar);
            jamKeluarSpinner.setValue(jamKeluar); // Set jam keluar yang ada

            SpinnerDateModel modelJamKembali = new SpinnerDateModel();
            JSpinner jamKembaliSpinner = new JSpinner(modelJamKembali);
            jamKembaliSpinner.setValue(jamKembali); // Set jam kembali yang ada

            // Format untuk jam
            JSpinner.DateEditor timeEditorKeluar = new JSpinner.DateEditor(jamKeluarSpinner, "HH:mm:ss");
            jamKeluarSpinner.setEditor(timeEditorKeluar);

            JSpinner.DateEditor timeEditorKembali = new JSpinner.DateEditor(jamKembaliSpinner, "HH:mm:ss");
            jamKembaliSpinner.setEditor(timeEditorKembali);

            JTextArea keteranganField = new JTextArea(5, 20); // Membuat form keterangan lebih besar
            keteranganField.setLineWrap(true); // Aktifkan line wrap
            keteranganField.setText(keterangan); // Mengisi keterangan yang ada

            Object[] message = {
                "NIS:", nisField,
                "Status Izin:", statusField,
                "Jam Keluar:", jamKeluarSpinner,
                "Jam Kembali:", jamKembaliSpinner,
                "Keterangan:", keteranganField // Menambahkan Keterangan ke popup
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Ubah Data Kartu Izin", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    Connection conn = Koneksi.getConnection();
                    String query = "UPDATE kartu_izin SET Status_Izin=?, Jam_Keluar=?, Jam_Kembali=?, Keterangan=? WHERE NIS=?";
                    PreparedStatement pst = conn.prepareStatement(query);
                    pst.setString(1, statusField.getSelectedItem().toString());
                    pst.setTime(2, new Time(((java.util.Date) jamKeluarSpinner.getValue()).getTime()));
                    pst.setTime(3, new Time(((java.util.Date) jamKembaliSpinner.getValue()).getTime()));
                    pst.setString(4, keteranganField.getText()); // Menyimpan Keterangan
                    pst.setInt(5, nis);

                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Data berhasil diubah!");
                    jButton15ActionPerformed(evt); // Refresh JTable setelah mengubah data
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(null, "Format waktu tidak valid. Gunakan format HH:mm:ss.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silakan pilih data yang ingin diubah.");
        }

    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        // TODO add your handling code here:
        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow != -1) {
            int nis = (int) jTable2.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    Connection conn = Koneksi.getConnection();
                    String query = "DELETE FROM kartu_izin WHERE NIS=?";
                    PreparedStatement pst = conn.prepareStatement(query);
                    pst.setInt(1, nis);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
                    jButton15ActionPerformed(evt); // Refresh JTable setelah menghapus data
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silakan pilih data yang ingin dihapus.");
        }


    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        // TODO add your handling code here:
        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow != -1) {
            try {
                Connection conn = Koneksi.getConnection();
                int nis = (int) jTable2.getValueAt(selectedRow, 0); // Ambil NIS dari baris yang dipilih
                String query = "SELECT k.NIS, s.Nama, s.Kelas, k.Status_Izin, k.Jam_Keluar, k.Jam_Kembali, k.Keterangan " +
                               "FROM kartu_izin k JOIN siswa s ON k.NIS = s.NIS WHERE k.NIS = ?";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setInt(1, nis);

                ResultSet rs = pst.executeQuery();

                // Menggunakan JRResultSetDataSource
                JRResultSetDataSource resultSetDataSource = new JRResultSetDataSource(rs);
                String path = new File("src/form/kartuizin.jasper").getAbsolutePath();
                JasperPrint jp = JasperFillManager.fillReport(path, null, resultSetDataSource);
                JasperViewer.viewReport(jp, false);

            } catch (JRException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error saat mencetak laporan: " + e.getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error saat mengakses database: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silakan pilih data yang ingin dicetak.");
        }


    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        // Mendapatkan input dari jTextField3
        String searchTerm = jTextField3.getText().trim();

        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Masukkan NIS atau Nama Siswa yang valid.");
            return;
        }

        try {
            Connection conn = Koneksi.getConnection();
            String query = "SELECT k.NIS, s.Nama, s.Kelas, k.Status_Izin, k.Jam_Keluar, k.Jam_Kembali, k.Keterangan " +
                           "FROM kartu_izin k JOIN siswa s ON k.NIS = s.NIS " +
                           "WHERE k.NIS = ? OR s.Nama LIKE ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, searchTerm);
            pst.setString(2, "%" + searchTerm + "%"); // Mencari berdasarkan nama

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0); // Clear existing data

            // Menambahkan hasil pencarian ke model tabel
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("NIS"),
                    rs.getString("Nama"),
                    rs.getString("Kelas"),
                    rs.getString("Status_Izin"),
                    rs.getTime("Jam_Keluar"),
                    rs.getTime("Jam_Kembali"),
                    rs.getString("Keterangan")
                };
                model.addRow(row); // Menambahkan data ke model JTable
            }

            // Jika tidak ada hasil
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Data tidak ditemukan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }

    }//GEN-LAST:event_jButton9ActionPerformed

    private void filterkelasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterkelasActionPerformed
        filterAbsensiByTanggalDanKelas();
        
    }//GEN-LAST:event_filterkelasActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
         // Mendapatkan kelas yang dipilih dari JComboBox
        String selectedKelas = (String) jComboBox2.getSelectedItem();
        System.out.println("Kelas yang dipilih: " + selectedKelas); // Debugging

        // Memanggil fungsi untuk memfilter data berdasarkan kelas yang dipilih
        filterDataByKelasSiswa(selectedKelas);
    
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        // Mendapatkan input dari jTextField2
        String searchTerm = jTextField2.getText().trim();

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
        try {
            Connection conn = Koneksi.getConnection();
            String query = "SELECT NIS, Nama, Kelas, jenis_kelamin FROM siswa WHERE NIS = ? OR Nama LIKE ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, searchTerm);
            pst.setString(2, "%" + searchTerm + "%"); // Cari berdasarkan nama

            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
            model.setRowCount(0); // Bersihkan data tabel

            while (rs.next()) {
                String jenisKelamin = rs.getString("jenis_kelamin");
                String jenisKelaminLabel = jenisKelamin.equals("L") ? "Laki-Laki" : "Perempuan";

                Object[] row = {
                    rs.getInt("NIS"),
                    rs.getString("Nama"),
                    rs.getString("Kelas"),
                    jenisKelaminLabel
                };
                model.addRow(row);
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Data tidak ditemukan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    } else {
        JOptionPane.showMessageDialog(null, "Masukkan NIS atau Nama Siswa yang valid.");
    }

    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        // TODO add your handling code here:
        try {
        Connection conn = Koneksi.getConnection();
        String query = "SELECT NIS, Nama, Kelas, jenis_kelamin FROM siswa";
        PreparedStatement pst = conn.prepareStatement(query);
        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        model.setRowCount(0); // Menghapus data lama di tabel

        while (rs.next()) {
            Object[] row = {
                rs.getInt("NIS"),
                rs.getString("Nama"),
                rs.getString("Kelas"),
                rs.getString("jenis_kelamin")
            };
            model.addRow(row); // Tambahkan ke JTable
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }

    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        // TODO add your handling code here:
        JTextField nisField = new JTextField();
        JTextField namaField = new JTextField();
        String[] kelasOptions = {"7", "8", "9"};
        JComboBox<String> kelasComboBox = new JComboBox<>(kelasOptions);
        String[] jkOptions = {"L", "P"};
        JComboBox<String> jkComboBox = new JComboBox<>(jkOptions);

        Object[] message = {
            "NIS:", nisField,
            "Nama:", namaField,
            "Kelas:", kelasComboBox,
            "Jenis Kelamin:", jkComboBox
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Tambah Data Siswa", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Connection conn = Koneksi.getConnection();
                String query = "INSERT INTO siswa (NIS, Nama, Kelas, jenis_kelamin) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setInt(1, Integer.parseInt(nisField.getText()));
                pst.setString(2, namaField.getText());
                pst.setString(3, (String) kelasComboBox.getSelectedItem());
                pst.setString(4, (String) jkComboBox.getSelectedItem());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan!");
                jButton20ActionPerformed(evt); // Refresh tabel siswa
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "NIS harus berupa angka!");
            }
        }

    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        // TODO add your handling code here:
        int selectedRow = jTable3.getSelectedRow();
        if (selectedRow != -1) {
        int nis = (int) jTable3.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(null, "Apakah yakin ingin menghapus data NIS " + nis + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                    Connection conn = Koneksi.getConnection();
                    String query = "DELETE FROM siswa WHERE NIS=?";
                    PreparedStatement pst = conn.prepareStatement(query);
                    pst.setInt(1, nis);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
                    jButton20ActionPerformed(evt); // Refresh tabel
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silakan pilih data yang ingin dihapus.");
        }

    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        // TODO add your handling code here:
        try {
            Connection conn = Koneksi.getConnection();
            String query = "SELECT nama, username, password, rfid FROM admin";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            DefaultTableModel model = (DefaultTableModel) jTable4.getModel();
            model.setRowCount(0); // Kosongkan tabel sebelum mengisi ulang

            while (rs.next()) {
                Object[] row = {
                    rs.getString("nama"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("rfid") // Tambahkan ini untuk menampilkan RFID
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }

    }//GEN-LAST:event_jButton24ActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        // TODO add your handling code here:
        JTextField namaField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField rfidField = new JTextField();

        Object[] message = {
            "Nama Admin:", namaField,
            "Username:", usernameField,
            "Password:", passwordField,
            "RFID:", rfidField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Tambah Admin", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Connection conn = Koneksi.getConnection();
                String query = "INSERT INTO admin (nama, username, password, rfid) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, namaField.getText());
                pst.setString(2, usernameField.getText());
                pst.setString(3, new String(passwordField.getPassword()));
                pst.setString(4, rfidField.getText());
                pst.executeUpdate();

                JOptionPane.showMessageDialog(null, "Data admin berhasil ditambahkan!");
                jButton24ActionPerformed(evt); // Refresh tabel
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        }


    }//GEN-LAST:event_jButton25ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        // TODO add your handling code here:
        int selectedRow = jTable4.getSelectedRow();
        if (selectedRow != -1) {
            String nama = (String) jTable4.getValueAt(selectedRow, 0);
            String username = (String) jTable4.getValueAt(selectedRow, 1);
            String password = (String) jTable4.getValueAt(selectedRow, 2);
            String rfid = (String) jTable4.getValueAt(selectedRow, 3);

            JTextField namaField = new JTextField(nama);
            JTextField usernameField = new JTextField(username);
            JPasswordField passwordField = new JPasswordField(password);
            JTextField rfidField = new JTextField(rfid);

            Object[] message = {
                "Nama Admin:", namaField,
                "Username:", usernameField,
                "Password:", passwordField,
                "RFID:", rfidField
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Ubah Admin", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    Connection conn = Koneksi.getConnection();
                    String query = "UPDATE admin SET nama=?, password=?, rfid=? WHERE username=?";
                    PreparedStatement pst = conn.prepareStatement(query);
                    pst.setString(1, namaField.getText());
                    pst.setString(2, new String(passwordField.getPassword()));
                    pst.setString(3, rfidField.getText());
                    pst.setString(4, usernameField.getText());
                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Data admin berhasil diubah!");
                    jButton24ActionPerformed(evt); // Refresh tabel
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silakan pilih data admin yang ingin diubah.");
        }


    }//GEN-LAST:event_jButton26ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        // TODO add your handling code here:
        int selectedRow = jTable4.getSelectedRow();
        if (selectedRow != -1) {
            String nama = (String) jTable4.getValueAt(selectedRow, 0);
            int option = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                try {
                    Connection conn = Koneksi.getConnection();
                    String query = "DELETE FROM admin WHERE Nama=?";
                    PreparedStatement pst = conn.prepareStatement(query);
                    pst.setString(1, nama);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
                    jButton24ActionPerformed(evt); // Refresh JTable setelah menghapus data
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silakan pilih data yang ingin dihapus.");
        }

    }//GEN-LAST:event_jButton27ActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        // TODO add your handling code here:
    try {
        Connection conn = Koneksi.getConnection();
        String today = java.time.LocalDate.now().toString();

        // Cek apakah data hari ini sudah ada
        String checkQuery = "SELECT COUNT(*) FROM absensi WHERE Tanggal = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
        checkStmt.setString(1, today);
        ResultSet rs = checkStmt.executeQuery();
        rs.next();

        if (rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(null, "Data absensi hari ini sudah ada!");
            return;
        }

        // Salin semua siswa dengan status default "Hadir"
        String insertQuery = "INSERT INTO absensi (NIS, Status, Tanggal, Waktu_Masuk, Waktu_Pulang) " +
                             "SELECT NIS, 'Hadir', ?, '07:00:00', '15:00:00' FROM siswa";
        PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
        insertStmt.setString(1, today);
        insertStmt.executeUpdate();

        JOptionPane.showMessageDialog(null, "Absensi hari ini berhasil ditambahkan!");
        jButton10ActionPerformed(evt);
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }

    }//GEN-LAST:event_jButton28ActionPerformed

    private void rekapsemesterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rekapsemesterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rekapsemesterActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        // TODO add your handling code here:
         
    }//GEN-LAST:event_jButton29ActionPerformed

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        // TODO add your handling code here:
    try {
        String kelas = filterkelas_rekapbulanan.getSelectedItem().toString();
        if (kelas.equals("KELAS")) {
            JOptionPane.showMessageDialog(null, "Pilih kelas terlebih dahulu.");
            return;
        }

        LocalDate now = LocalDate.now();
        int bulan = now.getMonthValue();
        int tahun = now.getYear();

        String sql = "SELECT a.NIS, s.Nama, s.Kelas, " +
                     "SUM(a.Status = 'Hadir') AS Hadir, " +
                     "SUM(a.Status = 'Izin') AS Izin, " +
                     "SUM(a.Status = 'Sakit') AS Sakit, " +
                     "SUM(a.Status = 'Alpa') AS Alpa, " +
                     "COUNT(*) AS Total " +
                     "FROM absensi a " +
                     "JOIN siswa s ON a.NIS = s.NIS " +
                     "WHERE MONTH(a.Tanggal) = ? AND YEAR(a.Tanggal) = ? AND s.Kelas = ? " +
                     "GROUP BY a.NIS, s.Nama, s.Kelas";

        Connection conn = Koneksi.getConnection();
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setInt(1, bulan);
        pst.setInt(2, tahun);
        pst.setString(3, kelas);

        ResultSet rs = pst.executeQuery();
        JRResultSetDataSource dataSource = new JRResultSetDataSource(rs);

        Map<String, Object> param = new HashMap<>();
        param.put("kelas", kelas);
        param.put("bulan", bulan);
        param.put("tahun", tahun);

        InputStream reportStream = getClass().getResourceAsStream("/form/rekapbulanan.jasper");
        if (reportStream == null) {
            JOptionPane.showMessageDialog(null, "File laporan '/form/rekapbulanan.jasper' tidak ditemukan!");
            return;
        }

        JasperPrint print = JasperFillManager.fillReport(reportStream, param, dataSource);
        JasperViewer.viewReport(print, false);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Gagal mencetak rekap bulanan: " + e.getMessage());
    }


    }//GEN-LAST:event_jButton30ActionPerformed

    private void filterkelas_rekapbulananActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterkelas_rekapbulananActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterkelas_rekapbulananActionPerformed

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
            java.util.logging.Logger.getLogger(Menu_Utama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Menu_Utama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Menu_Utama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Menu_Utama.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Menu_Utama().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> filterkelas;
    private javax.swing.JComboBox<String> filterkelas_rekapbulanan;
    private javax.swing.JComboBox<String> filterkelas_rekapsemester;
    private com.toedter.calendar.JDateChooser filtertanggal;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JPanel mainpanel;
    private javax.swing.JPanel panelabsensi;
    private javax.swing.JPanel panelberanda;
    private javax.swing.JPanel paneldataadmin;
    private javax.swing.JPanel paneldatasiswa;
    private javax.swing.JPanel paneldiagram;
    private javax.swing.JPanel panelfooter;
    private javax.swing.JPanel panelheader;
    private javax.swing.JPanel panelkartuizin;
    private javax.swing.JPanel panelmenu;
    private javax.swing.JPanel panelsidebar;
    private javax.swing.JPanel panelwelcome;
    private com.toedter.calendar.JMonthChooser rekapbulan;
    private javax.swing.JComboBox<String> rekapsemester;
    // End of variables declaration//GEN-END:variables
}
