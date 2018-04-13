package SkybaseManager2017;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.xml.stream.XMLInputFactory;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Toolkit;

/**
 * Klasa główna aplikacji, dziedzicząca po klasie {@link javax.swing.JFrame}.
 *
 * @see javax.swing.JFrame
 * @author Sebastian Zabrzyski
 */




public class SkybaseManager2017 extends javax.swing.JFrame {

    static Connection connection;
    static Statement statement = null;
    private int zaimportowanych = 0;
    private int do_zaimportowania = 0;
    private String ostatnie_zapytanie = "";
    private ArrayList<String> import_bledy = new ArrayList<String>();

    static private String path = System.getProperty("user.dir");

    private static String baza_login = "oracle";
    private static String baza_haslo = "oracle";
    private static String baza_nazwa = "oracle";
   // private static String baza_adres = path + "/database/LOTY.FDB";
    private static String xml_adres = path + "/xml/Dane_lotow.xml";

    /**
     * Metoda służąca do wyświetlania szczegółowych komunikatów o błędach
     * zgłaszanych podczas działania aplikacji.
     *
     * @param message Komunikat o błędzie.
     * @param szczegolowy Szczegółowy komunikat o błędzie (treść wyjątku).
     * @author Sebastian Zabrzyski
     */
    public void blad(String message, String szczegolowy) {

        if (szczegolowy.contains("Column unknown") || szczegolowy.contains("Token unknown") || szczegolowy.contains("violates CHECK constraint")) {
            szczegolowy = "Naruszenie więzów integralności encji";
        }
        if (szczegolowy.contains("violation of FOREIGN KEY constraint")) {
            szczegolowy = "Naruszenie więzów integralności referencyjnej";
        }
        if (szczegolowy.contains("violation of PRIMARY or UNIQUE KEY")) {
            szczegolowy = "Naruszenie więzów integralności klucza głównego lub unikalnego";
        }
        if (szczegolowy.contains("to complete network request to host")) {
            szczegolowy = "Nie można ustanowić połączenia z serwerem bazy danych";
        }
        if (szczegolowy.contains("connection rejected by remote interface")) {
            szczegolowy = "Połączenie odrzucone przez interfejs zdalny";
        }
        if (szczegolowy.contains("on-disk structure")) {
            szczegolowy = "Nieobsługiwana wersja bazy danych";
        }

        final JFrame frame1 = new JFrame();
        Object[] options = {"Szczegóły błędu",
            "OK"};

        int odp = JOptionPane.showOptionDialog(frame1,
                message,
                "Wystąpił błąd!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]);

        if (odp == JOptionPane.YES_OPTION) {

            blad(szczegolowy);

        }

    }

    /**
     * Metoda służąca do wyświetlania ogólnych komunikatów o błędach zgłaszanych
     * podczas działania aplikacji.
     *
     * @param message Komunikat o błędzie.
     * @author Sebastian Zabrzyski
     */
    public void blad(String message) {

        final JFrame frame1 = new JFrame();
        JOptionPane.showMessageDialog(frame1,
                message, "Wystąpił błąd!", JOptionPane.ERROR_MESSAGE);

    }

    /**
     * Metoda służąca do ustanowienia połączenia z bazą użytkownika przy
     * wykorzystaniu interfejsu JDBC. Po nawiązaniu połączenia następuje
     * odblokowanie przycisków służące do komunikacji z bazą danych. W razie
     * wystąpienia wyjątku SQLException, wyświetlony zostaje komunikat z
     * informacją o jego treści.
     *
     * @throws SQLException Wyjątek zgłaszany w przypadku problemu z dostępem do
     * bazy danych.
     * @author Sebastian Zabrzyski
     */
    public void polacz_z_baza() throws SQLException {

      //  File baza = new File(baza_adres);
      //  if (!baza.exists()) {
      //      String tresc = "Nie odnaleziono pliku z bazą danych!";
     //       jToggleButton1.setSelected(false);
     //       blad(tresc);
//
       // } else {

            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");

                try {

                    connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:" + baza_nazwa,baza_login,baza_haslo);
                    statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_READ_ONLY);
                    String wynik = "Połączono z bazą danych!";
                    jTextArea2.append(wynik + "\n");

                    jToggleButton1.setText("Rozłącz z bazą");
                    jButton6.setEnabled(true);
                    jButton3.setEnabled(true);
                    jToggleButton1.setSelected(true);
                    jMenuItem1.setEnabled(true);
                    jMenuItem3.setText("Rozłącz...");
                    jComboBox1.setEnabled(true);
                    
                    jButton13.setEnabled(true);
jButton14.setEnabled(true);
jButton15.setEnabled(true);
jButton16.setEnabled(true);

                } catch (SQLException ex) {
                    jToggleButton1.setSelected(false);
                    String wynik = "Wystąpił błąd podczas łączenia z bazą danych!";
                    String szczegolowy = ex.getMessage();
                    blad(wynik, szczegolowy);

                }

            } catch (ClassNotFoundException ex) {
                String wynik = "Nie odnaleziono sterownika JDBC!";

                jToggleButton1.setSelected(false);

                blad(wynik);

            }

       // }

    }

    /**
     * Metoda służąca do zakończenia połączenia z bazą użytkownika. Po
     * zakończeniu połączenia z bazą metoda wyłącza przyciski GUI służące do
     * komunikacji z bazą danych. W razie wystąpienia wyjątku SQLException,
     * zostaje wyświetlony komunikat z informacją o treści wyjątku.
     *
     * @throws SQLException Wyjątek zgłaszany w przypadku problemu z dostępem do
     * bazy danych.
     * @author Sebastian Zabrzyski
     */
    public void rozlacz_z_baza() throws SQLException {

        try {
            statement.close();

            String wynik = "Rozłączono z bazą danych!";
            jTextArea2.append(wynik + "\n");

            jToggleButton1.setText("Połącz z bazą");
            jButton6.setEnabled(false);
            jButton3.setEnabled(false);
            jToggleButton1.setSelected(false);
            jMenuItem1.setEnabled(false);
            jMenuItem3.setText("Połącz...");
            jComboBox1.setSelectedIndex(0);
            jComboBox1.setEnabled(false);
            jButton9.setEnabled(false);
            jButton12.setEnabled(false);
            jButton10.setEnabled(false);
            jButton11.setEnabled(false);
            
            jButton13.setEnabled(false);
jButton14.setEnabled(false);
jButton15.setEnabled(false);
jButton16.setEnabled(false);
            
            jTable1.setModel(new javax.swing.table.DefaultTableModel(0, 0));

        } catch (SQLException ex) {

            String wynik = "Wystąpił błąd przy zamykaniu połączenia z bazą!";

            String szczegolowy = ex.getMessage();
            blad(wynik, szczegolowy);

        }

    }

    /**
     * Metoda wywołuje metodę parsowania pliku XML.W razie wystąpienia
     * jakiegokolwiek wyjątku podczas parsowania, wyświetlony zostaje komunikat
     * z informacją o błędzie.
     *
     * @throws Exception Wyjątek zgłaszany w przypadku problemu z parsowaniem
     * pliku XML.
     * @author Sebastian Zabrzyski
     */
    public void parsowanie_call() throws Exception {

        File xml = new File(xml_adres);
        if (!xml.exists()) {
            String tresc = "Nie odnaleziono pliku XML";
            blad(tresc);

        } else {
            try {
                parsowanieXML(xml_adres);
            } catch (Exception e) {

                String wynik = "Wystąpił błąd podczas importu danych z pliku XML";

                String szczegolowy = e.getMessage();
                blad(wynik, szczegolowy);

            }

        }

    }

    //Zapytania
    /**
     * Metoda służąca do komunikacji z bazą danych przy użyciu zapytań SQL
     * zdefiniowanych przez użytkownika. Jeżeli zapytanie nie zostanie
     * rozpoznane przez interfejs JDBC lub nie zostanie prawidłowo wykonane,
     * wyświetlony zostaje komunikat z informacją o błędzie.
     *
     * @param query Zapytanie SQL pobrane z pola tekstowego.
     * @param rodzaj Wartość zmiennej informuje metodę czy zapytania zostały
     * wprowadzone ręcznie przez użytkownika czy też wygenerowane przez
     * aplikację.
     *
     * @throws SQLException Wyjątek zgłaszany w przypadku problemu z dostępem do
     * bazy danych.
     * @author Adam Szczur, Sebastian Zabrzyski
     */
    public void zapytanie(String query, int rodzaj) throws SQLException {

        StringTokenizer st = new StringTokenizer(query, " ");
        String firstWord = "";
        try {
            firstWord = st.nextToken().toUpperCase();
        } catch (java.util.NoSuchElementException e) {

        }

        if (firstWord.equals("SELECT")) {
            // wykonywanie zapytan SELECT do bazy
            try {
                ResultSet resultSet = statement.executeQuery(query);
                ResultSetMetaData rsmd = resultSet.getMetaData();
                int liczbaKol = rsmd.getColumnCount();

                int liczba_rekordow = 0;
                while (resultSet.next()) {
                    liczba_rekordow++;
                }

                if (rodzaj != 2) {

                    jComboBox1.setSelectedIndex(0);
                    jButton9.setEnabled(false);
                    jButton12.setEnabled(false);
                    jButton10.setEnabled(false);

                }
                jTable1.setModel(new javax.swing.table.DefaultTableModel(liczba_rekordow, liczbaKol));

                for (int i = 0; i < liczbaKol; i++) {
                    String nazwa_kolumny = rsmd.getColumnName(i + 1);
                    jTable1.getColumnModel().getColumn(i).setHeaderValue(nazwa_kolumny);

                }

                int licznik_rekordow = 0;

                resultSet.beforeFirst();

                while (resultSet.next()) {
                    int licznik_wierszy = 0;
                    String linia = new String();
                    for (int i = 1; i <= liczbaKol; i++) {

                        String wartosc = resultSet.getString(i);
                        jTable1.setValueAt(wartosc, licznik_rekordow, licznik_wierszy);
                        licznik_wierszy++;
                    }

                    licznik_rekordow++;
                }

                ostatnie_zapytanie = query;
                jButton11.setEnabled(true);

            } catch (SQLException ex) {

                String wynik = "Wystąpił problem podczas wykonywania zapytania do bazy!";
                String szczegolowy = ex.getMessage();
                blad(wynik, szczegolowy);

            }
        } else if (firstWord.equals("INSERT") || firstWord.equals("DELETE") || firstWord.equals("UPDATE")) {
            // wykonywanie zapytan INSERT/DELETE/UPDATE do bazy
            try {
                int wynikUpdate = statement.executeUpdate(query);
                if (wynikUpdate > 0) {
                    String operacja = "";
                    if (firstWord.equals("INSERT")) {
                        operacja = "dodanych";
                    }
                    if (firstWord.equals("DELETE")) {
                        operacja = "usuniętych";
                    }
                    if (firstWord.equals("UPDATE")) {
                        operacja = "zmodyfikowanych";
                    }

                    if (rodzaj == 0 || rodzaj == 2) {

                        String wynik = "Liczba " + operacja + " rekordów: " + wynikUpdate;
                        jTextArea2.append(wynik + "\n");
                    } else {
                        zaimportowanych++;

                    }
                } else {

                    String wynik = "Nie zmodyfikowano zadnego rekordu";
                    jTextArea2.append(wynik + "\n");

                }
            } catch (SQLException ex) {
                String wynik = "Wystąpił błąd podczas wykonywania zapytania do bazy!";

                if (rodzaj == 0 || rodzaj == 2) {
                    String szczegolowy = ex.getMessage();
                    blad(wynik, szczegolowy);
                } else {

                    import_bledy.add(ex.getMessage());

                }

            }
        } else {

            String wynik = "Nie rozpoznano zapytania SQL";

            blad(wynik);

        }

    }
//Zapytania

//XML
    /**
     * Metoda służąca do parsowania pliku XML i wykonywania zapytań SQL
     * wygenerowanych na podstawie otrzymanyh danych. Po zakończeniu wykonywania
     * zapytań wyświetlony zostaje komunikat z informacją o ilości dodanych
     * rekordów oraz ewentualnych błędach.
     *
     * @param filePath Ścieżka do pliku XML.
     * @throws FileNotFoundException Wyjątek zgłaszany jeżeli aplikacja nie
     * odnajdzie pliku XML w podanej ścieżce.
     * @throws XMLStreamException Wyjątek zgłaszany w przypadku problemu z
     * parsowaniem pliku XML.
     * @author Adam Szczur, Sebastian Zabrzyski
     */
    public void parsowanieXML(String filePath) throws FileNotFoundException, XMLStreamException {

        String zapytania = "";
        String akceptowane = "PasazerowiePracownicyKierunkiLotyPasazerowie_lotPracownicy_lot";
        XMLInputFactory iFactory = XMLInputFactory.newInstance();
        InputStream xmlFile = new FileInputStream(new File(filePath));
        XMLStreamReader parser = iFactory.createXMLStreamReader(xmlFile);

        ArrayList<String> tags = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();

        while (parser.hasNext()) {

            switch (parser.next()) {
// START ELEMENT //
                case XMLStreamConstants.START_ELEMENT:
                    // tagi 'glowne' //
                    String parsowane = parser.getLocalName();
                    if (parsowane.equals("Pasazerowie")) {
                        tags.add("tabela");
                        values.add("Pasazerowie");
                    }
                    if (parsowane.equals("Pracownicy")) {
                        tags.add("tabela");
                        values.add("Pracownicy");
                    }
                    if (parsowane.equals("Kierunki")) {
                        tags.add("tabela");
                        values.add("Kierunki");
                    }
                    if (parsowane.equals("Samoloty")) {
                        tags.add("tabela");
                        values.add("Samoloty");
                    }
                    
                    if (parsowane.equals("Loty")) {
                        tags.add("tabela");
                        values.add("Loty");
                    }

                    if (parsowane.equals("Pasazerowie_lot")) {
                        tags.add("tabela");
                        values.add("Pasazerowie_lot");
                    }

                    if (parsowane.equals("Pracownicy_lot")) {
                        tags.add("tabela");
                        values.add("Pracownicy_lot");
                    }

                    // 'podtagi' //
                    if (parsowane.equals("ID_Pasazera")) {
                        tags.add("ID_Pasazera");
                        values.add(parser.getElementText());
                    }
                    if (parsowane.equals("Imie")) {
                        tags.add("Imie");
                        values.add("'" + parser.getElementText() + "'");
                    }
                    if (parsowane.equals("Nazwisko")) {
                        tags.add("Nazwisko");
                        values.add("'" + parser.getElementText() + "'");
                    }

                    if (parsowane.equals("Pesel")) {
                        tags.add("Pesel");
                        values.add(parser.getElementText());
                    }
                    if (parsowane.equals("ID_Pracownika")) {
                        tags.add("ID_Pracownika");
                        values.add(parser.getElementText());
                    }
                    if (parsowane.equals("Rola")) {
                        tags.add("Rola");
                        values.add("'" + parser.getElementText() + "'");
                    }
                    if (parsowane.equals("ID_Kierunku")) {
                        tags.add("ID_Kierunku");
                        values.add(parser.getElementText());
                    }
                                        if (parsowane.equals("ID_Samolotu")) {
                        tags.add("ID_Samolotu");
                        values.add(parser.getElementText());
                    }
                    if (parsowane.equals("Model")) {
                        tags.add("Model");
                        values.add("'" + parser.getElementText() + "'");
                    }
                                        if (parsowane.equals("Rok_produkcji")) {
                        tags.add("Rok_produkcji");
                        values.add("'" + parser.getElementText() + "'");
                    }
                                                            if (parsowane.equals("Liczba_pasazerow")) {
                        tags.add("Liczba_pasazerow");
                        values.add(parser.getElementText());
                    }
                    if (parsowane.equals("Skad")) {
                        tags.add("Skad");
                        values.add("'" + parser.getElementText() + "'");
                    }
                    if (parsowane.equals("Dokad")) {
                        tags.add("Dokad");
                        values.add("'" + parser.getElementText() + "'");
                    }
                    if (parsowane.equals("ID_Lotu")) {
                        tags.add("ID_Lotu");
                        values.add(parser.getElementText());
                    }
                    if (parsowane.equals("Data_lotu")) {
                        tags.add("Data_lotu");
                        values.add("'" + parser.getElementText() + "'");
                    }
                    break;
// END ELEMENT //
                case XMLStreamConstants.END_ELEMENT:

                    if (akceptowane.contains(parser.getLocalName())) {

                        String zapytanie = "INSERT INTO ";
                        if (values.get(0).equals("Pasazerowie_lot") || values.get(0).equals("Pracownicy_lot")) {
                            zapytanie += values.get(0);
                            zapytanie += " (" + tags.get(1) + ", " + tags.get(2) + ") VALUES (";
                            zapytanie += values.get(1) + ", " + values.get(2) + ");";
                        }

                        if (values.get(0).equals("Kierunki")) {
                            zapytanie += values.get(0);
                            zapytanie += " (" + tags.get(1) + ", " + tags.get(2) + ", " + tags.get(3) + ") VALUES (";
                            zapytanie += values.get(1) + ", " + values.get(2) + ", " + values.get(3) + ");";
                        }
                        if (values.get(0).equals("Pasazerowie") || values.get(0).equals("Samoloty") || values.get(0).equals("Loty")) {
                            zapytanie += values.get(0);
                            zapytanie += " (" + tags.get(1) + ", " + tags.get(2) + ", " + tags.get(3) + ", " + tags.get(4) + ") VALUES (";
                            zapytanie += values.get(1) + ", " + values.get(2) + ", " + values.get(3) + ", " + values.get(4) + ");";
                        }
                        if (values.get(0).equals("Pracownicy")) {
                            zapytanie += values.get(0);
                            zapytanie += " (" + tags.get(1) + ", " + tags.get(2) + ", " + tags.get(3) + ", " + tags.get(4) + ", " + tags.get(5) + ") VALUES (";
                            zapytanie += values.get(1) + ", " + values.get(2) + ", " + values.get(3) + ", " + values.get(4) + ", " + values.get(5) + ");";
                        }

                        zapytania = zapytania + zapytanie;

                        tags.clear();
                        values.clear();
                        System.out.println();
                    }
                    break;
// END DOCUMENT //
                case XMLStreamConstants.END_DOCUMENT:

                    String zapytanie_text = zapytania;
                    zapytanie_text = zapytanie_text.replace("\n", "").replace("\r", "");

                    String[] separated = zapytanie_text.split(";");
                    for (String nowe_zapytanie : separated) {
                        do_zaimportowania++;
                        try {
                            zapytanie(nowe_zapytanie, 1);
                        } catch (Exception e) {

                        }

                    }

                    int liczba_bledow = do_zaimportowania - zaimportowanych;

                    if (liczba_bledow > 0) {

                        String wynik = "Import danych został zakończony z błędami.\nLiczba rekordów dodanych do bazy: " + zaimportowanych + " z " + do_zaimportowania;

                        final JFrame frame1 = new JFrame();
                        Object[] options = {"Szczegóły błędów",
                            "OK"};

                        int odp = JOptionPane.showOptionDialog(frame1,
                                wynik,
                                "Importowanie danych z pliku XML",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE,
                                null,
                                options,
                                options[0]);

                        wynik = "Wystąpił błąd podczas wykonywania zapytania do bazy!";

                        if (odp == JOptionPane.YES_OPTION) {

                            for (String error : import_bledy) {

                                blad(wynik, error);

                            }

                        }

                    } else {
                        String wynik = "Import danych został zakończony pomyślnie.\nLiczba rekordów dodanych do bazy: " + zaimportowanych;
                        final JFrame frame1 = new JFrame();
                        JOptionPane.showMessageDialog(frame1,
                                wynik, "Importowanie danych z pliku XML", JOptionPane.INFORMATION_MESSAGE);
                    }

                    import_bledy.clear();
                    zaimportowanych = 0;
                    do_zaimportowania = 0;

                    break;
            }

        }
    }

//XML
    /**
     * Konstruktor wywołujący inicjalizację komponentów interfejsu graficznego
     * aplikacji.
     *
     * @author Sebastian Zabrzyski
     */
    public SkybaseManager2017() {

        initComponents();

    }

    @SuppressWarnings("unchecked")

    /**
     * Metoda inicjalizująca komponenty interfejsu graficznego aplikacji.
     *
     * @author NetBeans IDE GUI Builder & Sebastian Zabrzyski
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jLabel4 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox<>();
        jComboBox6 = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();

        jDialog1.setLocation(new java.awt.Point(0, 0));
        jDialog1.setMinimumSize(new java.awt.Dimension(326, 236));
        jDialog1.setModal(true);
        jDialog1.setPreferredSize(new java.awt.Dimension(326, 236));
        jDialog1.setResizable(false);
        jDialog1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                jDialog1ComponentHidden(evt);
            }
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jDialog1ComponentMoved(evt);
            }
        });
        jDialog1.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                jDialog1WindowClosed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setText("Z tabeli");

        jComboBox3.setMaximumRowCount(6);
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        jComboBox4.setMaximumRowCount(6);
        jComboBox4.setEnabled(false);
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("policz wystąpienia");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel6.setText("gdzie");

        jComboBox5.setMaximumRowCount(6);
        jComboBox5.setEnabled(false);
        jComboBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox5ActionPerformed(evt);
            }
        });

        jComboBox6.setMaximumRowCount(6);
        jComboBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox6ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel7.setText("jest");

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton17.setText("Wykonaj");
        jButton17.setToolTipText("");
        jButton17.setActionCommand("OK");
        jButton17.setEnabled(false);
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton18.setText("Anuluj");
        jButton18.setToolTipText("");
        jButton18.setActionCommand("OK");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel4)
                        .addGap(7, 7, 7)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel5)
                        .addGap(7, 7, 7)
                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel6)
                        .addGap(7, 7, 7)
                        .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jDialog1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel7)
                        .addGap(7, 7, 7)
                        .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jButton17.getAccessibleContext().setAccessibleName("OK");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Skybase Manager 2017");
        setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        setLocation(new java.awt.Point(0, 0));
        setMinimumSize(new java.awt.Dimension(1114, 672));
        setPreferredSize(new java.awt.Dimension(1114, 672));
        setResizable(false);

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton3.setText("Wykonaj zapytanie");
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton4.setText("Wyczyść");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton6.setText("Importuj dane XML");
        jButton6.setEnabled(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jTextArea1.setBackground(new java.awt.Color(249, 249, 249));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Treść zapytania SQL:");

        jToggleButton1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jToggleButton1.setLabel("Połącz z bazą");
        jToggleButton1.setMaximumSize(new java.awt.Dimension(149, 36));
        jToggleButton1.setMinimumSize(new java.awt.Dimension(149, 36));
        jToggleButton1.setPreferredSize(new java.awt.Dimension(149, 36));
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jTextArea2.setBackground(new java.awt.Color(249, 249, 249));
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);
        jTextArea2.setEditable(false);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Wynik zapytania:");

        jButton8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton8.setText("Wyczyść");
        jButton8.setPreferredSize(new java.awt.Dimension(150, 36));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTable1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable1.setEditingColumn(0);
        jTable1.setEditingRow(0);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(jTable1);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Pokaż tabelę:");

        jComboBox1.setMaximumRowCount(6);
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Pasazerowie", "Pracownicy", "Kierunki", "Loty", "Samoloty", "Pasazerowie_lot", "Pracownicy_lot" }));
        jComboBox1.setEnabled(false);
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton9.setText("Usuń rekord");
        jButton9.setEnabled(false);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton10.setText("Dodaj rekord");
        jButton10.setToolTipText("");
        jButton10.setEnabled(false);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton11.setText("Odśwież");
        jButton11.setEnabled(false);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton12.setText("Modyfikuj rekord");
        jButton12.setEnabled(false);
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton13.setText("MIN");
        jButton13.setEnabled(false);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton14.setText("COUNT");
        jButton14.setEnabled(false);
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton15.setText("MAX");
        jButton15.setEnabled(false);
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton16.setText("AVG");
        jButton16.setEnabled(false);
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jMenuBar1.setBorder(new javax.swing.border.MatteBorder(null));
        jMenuBar1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenuBar1.setMinimumSize(new java.awt.Dimension(56, 40));
        jMenuBar1.setPreferredSize(new java.awt.Dimension(130, 34));

        jMenu1.setBackground(new java.awt.Color(0, 153, 255));
        jMenu1.setBorder(null);
        jMenu1.setText("Program  ");
        jMenu1.setActionCommand("Program");
        jMenu1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenu1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jMenu1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jMenu1.setMargin(new java.awt.Insets(5, 11, 0, 0));
        jMenu1.setMinimumSize(new java.awt.Dimension(24, 19));
        jMenu1.setPreferredSize(new java.awt.Dimension(78, 19));

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Zakończ");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenu2.setLabel("    Baza danych");
        jMenu2.setMargin(new java.awt.Insets(5, -3, 0, 0));
        jMenu2.setPreferredSize(new java.awt.Dimension(94, 21));

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setText("Połącz...");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Importuj dane XML...");
        jMenuItem1.setEnabled(false);
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("    Pomoc");
        jMenu3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jMenu3.setMargin(new java.awt.Insets(5, -3, 0, 0));
        jMenu3.setPreferredSize(new java.awt.Dimension(64, 21));

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setText("O programie...");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem6);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(11, 11, 11)
                                .addComponent(jButton6))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(11, 11, 11)
                                        .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(11, 11, 11)
                                        .addComponent(jButton16)
                                        .addGap(11, 11, 11)
                                        .addComponent(jButton13)
                                        .addGap(11, 11, 11)
                                        .addComponent(jButton15)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(6, 6, 6)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 629, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(11, 11, 11)
                                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(11, 11, 11)
                                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(11, 11, 11)
                                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(11, 11, 11))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(8, 8, 8)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Po kliknięciu przycisku metoda pobiera wartość z pola tekstowego
     * interpretując ją jako polecenie SQL i przekazuje je do metody
     * {@link #zapytanie(java.lang.String, int) } parametr polecenia SQL wpisane
     * przez użytkownika. Jeżeli pobrany tekst nie zawiera żadnego polecenia
     * SQL, wyświetlony zostaje komunikat z informacją o błędzie.
     *
     * @param evt Obsługa zdarzenia kliknięcia przycisku.
     * @author Sebastian Zabrzyski
     */
    public void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        String zapytanie_text = jTextArea1.getText();
        zapytanie_text = zapytanie_text.replace("\n", "").replace("\r", "");
        int licznik_sred = zapytanie_text.length() - zapytanie_text.replace(";", "").length();

        if (licznik_sred > 0) {
            String[] separated = zapytanie_text.split(";");

            if (separated.length > 0) {

                for (String nowe_zapytanie : separated) {
                    try {
                        zapytanie(nowe_zapytanie, 0);
                    } catch (Exception e) {

                    }
                }
            } else {
                String wynik = "Nie rozpoznano zapytania SQL";

                blad(wynik);

            }

        } else {
            String wynik = "Zapytania SQL muszą kończyć się średnikiem";

            blad(wynik);

        }


    }//GEN-LAST:event_jButton3ActionPerformed
    /**
     * Przycisk służący do wyczyszczenia pola zapytań SQL.
     *
     * @param evt Obsługa zdarzenia kliknięcia przycisku.
     * @author Sebastian Zabrzyski
     */
    public void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jTextArea1.setText("");
    }//GEN-LAST:event_jButton4ActionPerformed
    /**
     * Przycisk służący do wywoływania parsowania pliku XML.
     *
     * @param evt Obsługa zdarzenia kliknięcia przycisku.
     * @author Sebastian Zabrzyski
     */
    public void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        try {
            parsowanie_call();
        } catch (Exception e) {

        }
    }//GEN-LAST:event_jButton6ActionPerformed
    /**
     * Jeżeli przycisk nie jest wciśnięty, następuje wywołanie metody
     * {@link #polacz_z_baza()} Po nawiązaniu połączenia z bazą przycisk zmienia
     * swój stan na wciśnięty a jego wartość zmienia się z "Połącz" na
     * "Rozłącz", przez co po jego ponownym kliknięciu następuje wywołanie
     * metody {@link #rozlacz_z_baza()}.
     *
     * @param evt Obsługa zdarzenia kliknięcia przycisku.
     * @author Sebastian Zabrzyski
     */
    public void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed

        try {

            if (jToggleButton1.isSelected()) {
                polacz_z_baza();

            } else {
                rozlacz_z_baza();

            }
        } catch (Exception e) {

        }


    }//GEN-LAST:event_jToggleButton1ActionPerformed
    /**
     * Po naciśnięciu przycisku następuje wywołanie metody
     * {@link #polacz_z_baza()} Po nawiązaniu połączenia wartość przycisku
     * zmienia się z "Połącz" na "Rozłącz", przez co po jego ponownym kliknięciu
     * następuje wywołanie metody {@link #rozlacz_z_baza()}.
     *
     * @param evt Obsługa zdarzenia kliknięcia przycisku.
     * @author Sebastian Zabrzyski
     */
    public void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed

        try {
            if (jMenuItem3.getText().equals("Połącz...")) {
                polacz_z_baza();
            } else {
                rozlacz_z_baza();

            }

        } catch (Exception e) {

        }


    }//GEN-LAST:event_jMenuItem3ActionPerformed
    /**
     * Przycisk wywołuje metodę do parsowania pliku XML
     * {@link #parsowanie_call}.
     *
     * @param evt Obsługa zdarzenia kliknięcia przycisku.
     * @author Sebastian Zabrzyski
     */
    public void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        try {
            parsowanie_call();

        } catch (Exception e) {

        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed
    /**
     * Po kliknięciu przycisku następuje zakończenie pracy aplikacji.
     *
     * @param evt Obsługa zdarzenia kliknięcia przycisku.
     * @author Sebastian Zabrzyski
     */
    public void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem4ActionPerformed
    /**
     * Po kliknięciu przycisku następuje wyczyszczenie wyników zapytań SQL i
     * zablokowanie przycisków służących do operacji na rekordach tabeli.
     *
     * @param evt Obsługa zdarzenia kliknięcia przycisku.
     * @author Sebastian Zabrzyski
     */
    public void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        jTextArea2.setText("");
        jComboBox1.setSelectedIndex(0);
        jButton9.setEnabled(false);
        jButton12.setEnabled(false);
        jButton10.setEnabled(false);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(0, 0));
    }//GEN-LAST:event_jButton8ActionPerformed

    /**
     * Po kliknięciu przycisku zostaje wyświetlony komunikat z informacjami
     * dotyczącymi aplikacji.
     *
     * @param evt Obsługa zdarzenia kliknięcia przycisku.
     * @author Sebastian Zabrzyski
     */
    public void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        final JFrame frame2 = new JFrame();
        JOptionPane.showMessageDialog(frame2,
                "Skybase Manager 2017\nWersja: 1.0\n\nAutor: Sebastian Zabrzyski", "O programie...", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    /**
     * Po wybraniu wartości z rozwijanej listy następuje przesłanie zapytania
     * SQL za pomocą metody {@link #zapytanie(java.lang.String, int) } w celu
     * wyświetlenia zawartości bazy w tabeli.
     *
     * @param evt Obsługa zdarzenia kliknięcia przycisku.
     * @author Sebastian Zabrzyski
     */
    public void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        String tabela = String.valueOf(jComboBox1.getSelectedItem());
        if (!tabela.equals(" ")) {

            String query = "SELECT * FROM " + tabela;
            try {
                zapytanie(query, 2);
            } catch (Exception e) {

            }
            jButton9.setEnabled(true);
            jButton12.setEnabled(true);
            jButton10.setEnabled(true);
        } else {

            jButton9.setEnabled(false);
            jButton12.setEnabled(false);
            jButton10.setEnabled(false);
            jButton11.setEnabled(false);
            jTable1.setModel(new javax.swing.table.DefaultTableModel(0, 0));
        }

    }//GEN-LAST:event_jComboBox1ActionPerformed
    /**
     * Po kliknięciu przycisku następuje usunięcie wybranego rekordu tabeli z
     * bazy danych.
     *
     * @param evt Obsługa zdarzenia kliknięcia przycisku.
     * @author Sebastian Zabrzyski
     */
    public void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed

        int row = jTable1.getSelectedRow();
        if (row >= 0) {

            String tabela = String.valueOf(jComboBox1.getSelectedItem());
            try {
                switch (tabela) {

                    case "Pasazerowie":
                        String c1 = "ID_Pasazera";
                        String w1 = jTable1.getValueAt(row, 0).toString();
                        String query = "DELETE FROM " + tabela + " WHERE " + c1 + " = " + w1 + "";
                        zapytanie(query, 0);
                        break;

                    case "Pracownicy":

                        c1 = "ID_Pracownika";
                        w1 = jTable1.getValueAt(row, 0).toString();
                        query = "DELETE FROM " + tabela + " WHERE " + c1 + " = " + w1 + "";
                        zapytanie(query, 0);

                        break;

                    case "Loty":

                        c1 = "ID_Lotu";
                        w1 = jTable1.getValueAt(row, 0).toString();
                        query = "DELETE FROM " + tabela + " WHERE " + c1 + " = " + w1 + "";
                        zapytanie(query, 0);

                        break;

                    case "Kierunki":

                        c1 = "ID_Kierunku";
                        w1 = jTable1.getValueAt(row, 0).toString();
                        query = "DELETE FROM " + tabela + " WHERE " + c1 + " = " + w1 + "";
                        zapytanie(query, 0);

                        break;
                        
                                         case "Samoloty":

                        c1 = "ID_Samolotu";
                        w1 = jTable1.getValueAt(row, 0).toString();
                        query = "DELETE FROM " + tabela + " WHERE " + c1 + " = " + w1 + "";
                        zapytanie(query, 0);

                        break;

                    case "Pracownicy_lot":

                        c1 = "ID_Pracownika";
                        String c2 = "ID_Lotu";
                        w1 = jTable1.getValueAt(row, 0).toString();
                        String w2 = jTable1.getValueAt(row, 1).toString();
                        query = "DELETE FROM " + tabela + " WHERE " + c1 + " = " + w1 + " AND " + c2 + " = " + w2 + "";
                        zapytanie(query, 0);

                        break;

                    case "Pasazerowie_lot":

                        c1 = "ID_Pasazera";
                        c2 = "ID_Lotu";
                        w1 = jTable1.getValueAt(row, 0).toString();
                        w2 = jTable1.getValueAt(row, 1).toString();
                        query = "DELETE FROM " + tabela + " WHERE " + c1 + " = " + w1 + " AND " + c2 + " = " + w2 + "";
                        zapytanie(query, 0);

                        break;

                    default:

                        break;

                }
            } catch (Exception e) {

            }
        } else {

            String tresc = "Nie zaznaczono żadnego rekordu";
            blad(tresc);

        }


    }//GEN-LAST:event_jButton9ActionPerformed
    /**
     * Po kliknięciu przycisku następuje dodanie rekordu do wybranej tabeli w
     * baie danych.
     *
     * @param evt Obsługa zdarzenia kliknięcia przycisku.
     * @author Sebastian Zabrzyski
     */
    public void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed

        String tabela = String.valueOf(jComboBox1.getSelectedItem());
        final JFrame frame1 = new JFrame();
        try {
            switch (tabela) {

                case "Pasazerowie":

                    String p_id = (String) JOptionPane.showInputDialog(
                            frame1,
                            "Podaj numer ID pasażera:"
                            + "",
                            "Dodawanie pasażera",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            "");

                    if (p_id != null && p_id.length() > 0) {

                        String p_imie = (String) JOptionPane.showInputDialog(
                                frame1,
                                "Podaj imię:\n"
                                + "(max 50 znaków)",
                                "Dodawanie pasażera",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                "");

                        if (p_imie != null && p_imie.length() > 0) {

                            String p_nazwisko = (String) JOptionPane.showInputDialog(
                                    frame1,
                                    "Podaj nazwisko:\n"
                                    + "(max 50 znaków)",
                                    "Dodawanie pasażera",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    "");

                            if (p_nazwisko != null && p_nazwisko.length() > 0) {

                                String p_pesel = (String) JOptionPane.showInputDialog(
                                        frame1,
                                        "Podaj numer PESEL:\n"
                                        + "(dokładnie 11 znaków)",
                                        "Dodawanie pasażera",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        null,
                                        "");

                                if (p_pesel != null && p_pesel.length() > 0) {

                                    String query = "INSERT INTO Pasazerowie VALUES (" + p_id + ", '" + p_imie + "','" + p_nazwisko + "'," + p_pesel + ")";
                                    zapytanie(query, 2);
                                }

                            }

                        }

                    }

                    break;

                case "Pracownicy":

                    String z_id = (String) JOptionPane.showInputDialog(
                            frame1,
                            "Podaj numer ID pracownika:"
                            + "",
                            "Dodawanie pracownika",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            "");

                    if (z_id != null && z_id.length() > 0) {

                        String z_imie = (String) JOptionPane.showInputDialog(
                                frame1,
                                "Podaj imię:\n"
                                + "(max 50 znaków)",
                                "Dodawanie pracownika",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                "");

                        if (z_imie != null && z_imie.length() > 0) {

                            String z_nazwisko = (String) JOptionPane.showInputDialog(
                                    frame1,
                                    "Podaj nazwisko:\n"
                                    + "(max 50 znaków)",
                                    "Dodawanie pracownika",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    "");

                            if (z_nazwisko != null && z_nazwisko.length() > 0) {

                                String z_pesel = (String) JOptionPane.showInputDialog(
                                        frame1,
                                        "Podaj numer PESEL:\n"
                                        + "(dokładnie 11 znaków)",
                                        "Dodawanie pracownika",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        null,
                                        "");

                                if (z_pesel != null && z_pesel.length() > 0) {

                                    String z_r = (String) JOptionPane.showInputDialog(
                                            frame1,
                                            "Podaj rolę pracownika:"
                                            + "",
                                            "Dodawanie pracownika",
                                            JOptionPane.PLAIN_MESSAGE,
                                            null,
                                            null,
                                            "");

                                    if (z_r != null && z_r.length() > 0) {

                                        String query = "INSERT INTO Pracownicy VALUES (" + z_id + ", '" + z_imie + "','" + z_nazwisko + "'," + z_pesel + ",'" + z_r + "')";
                                        zapytanie(query, 2);
                                    }

                                }

                            }

                        }

                    }

                    break;

                case "Loty":

                    String l_id = (String) JOptionPane.showInputDialog(
                            frame1,
                            "Podaj numer ID lotu:"
                            + "",
                            "Dodawanie lotu",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            "");

                    if (l_id != null && l_id.length() > 0) {

                        String l_kid = (String) JOptionPane.showInputDialog(
                                frame1,
                                "Podaj nr ID kierunku:"
                                + "",
                                "Dodawanie lotu",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                "");

                        if (l_kid != null && l_kid.length() > 0) {

                            String l_s = (String) JOptionPane.showInputDialog(
                                    frame1,
                                    "Podaj nr ID samolotu:"
                                    + "",
                                    "Dodawanie lotu",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    "");

                            if (l_s != null && l_s.length() > 0) {

                                              String l_data = (String) JOptionPane.showInputDialog(
                                    frame1,
                                    "Podaj datę lotu:\n"
                                    + "(format YYYY-MM-DD)",
                                    "Dodawanie lotu",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    "");

                            if (l_data != null && l_data.length() > 0) {

                                String query = "INSERT INTO Loty VALUES (" + l_id + ", " + l_kid + ", " + l_s + ",'" + l_data + "')";
                                zapytanie(query, 2);

                            }

                            }

                        }

                    }

                    break;

                case "Kierunki":

                    String k_id = (String) JOptionPane.showInputDialog(
                            frame1,
                            "Podaj numer ID kierunku:"
                            + "",
                            "Dodawanie kierunku",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            "");

                    if (k_id != null && k_id.length() > 0) {

                        String k_z = (String) JOptionPane.showInputDialog(
                                frame1,
                                "Lot z miejscowości:\n"
                                + "(max 50 znaków)",
                                "Dodawanie kierunku",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                "");

                        if (k_z != null && k_z.length() > 0) {

                            String k_do = (String) JOptionPane.showInputDialog(
                                    frame1,
                                    "Lot do miejscowości:\n"
                                    + "(max 50 znaków)",
                                    "Dodawanie kierunku",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    "");

                            if (k_do != null && k_do.length() > 0) {

                                String query = "INSERT INTO Kierunki VALUES (" + k_id + ", '" + k_z + "','" + k_do + "')";
                                zapytanie(query, 2);

                            }

                        }

                    }

                    break;
                    
                    
                    
                      case "Samoloty":

                    String s_id = (String) JOptionPane.showInputDialog(
                            frame1,
                            "Podaj numer ID samolotu:"
                            + "",
                            "Dodawanie samolotu",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            "");

                    if (s_id != null && s_id.length() > 0) {

                        String s_m = (String) JOptionPane.showInputDialog(
                                frame1,
                                "Podaj nazwę modelu:"
                                + "",
                                "Dodawanie samolotu",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                "");

                        if (s_m != null && s_m.length() > 0) {

                            String s_rp = (String) JOptionPane.showInputDialog(
                                    frame1,
                                    "Podaj rok produkcji:"
                                    + "",
                                    "Dodawanie samolotu",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    "");

                            if (s_rp != null && s_rp.length() > 0) {

                                      String s_lp = (String) JOptionPane.showInputDialog(
                                    frame1,
                                    "Podaj liczbę pasażerów:"
                                    + "",
                                    "Dodawanie samolotu",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    "");

                            if (s_lp != null && s_lp.length() > 0) {

                                String query = "INSERT INTO Samoloty VALUES (" + s_id + ", '" + s_m + "'," + s_rp + "," + s_lp +  ")";
                                zapytanie(query, 2);

                            }

                        }

                        }

                    }

                    break;
                    
                    
                    
                    

                case "Pracownicy_lot":

                    String zl_pid = (String) JOptionPane.showInputDialog(
                            frame1,
                            "Podaj numer ID pracownika"
                            + "",
                            "Dodawanie lotu pracownika",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            "");

                    if (zl_pid != null && zl_pid.length() > 0) {

                        String zl_lid = (String) JOptionPane.showInputDialog(
                                frame1,
                                "Podaj nr ID lotu:"
                                + "",
                                "Dodawanie lotu pracownika",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                "");

                        if (zl_lid != null && zl_lid.length() > 0) {

                            String query = "INSERT INTO Pracownicy_lot VALUES (" + zl_pid + ", " + zl_lid + ")";
                            zapytanie(query, 2);

                        }

                    }

                    break;

                case "Pasazerowie_lot":

                    String pl_pid = (String) JOptionPane.showInputDialog(
                            frame1,
                            "Podaj numer ID pasażera"
                            + "",
                            "Dodawanie lotu pasażera",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            "");

                    if (pl_pid != null && pl_pid.length() > 0) {

                        String pl_lid = (String) JOptionPane.showInputDialog(
                                frame1,
                                "Podaj nr ID lotu:"
                                + "",
                                "Dodawanie lotu pasażera",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                "");

                        if (pl_lid != null && pl_lid.length() > 0) {

                            String query = "INSERT INTO Pasazerowie_lot VALUES (" + pl_pid + ", " + pl_lid + ")";
                            zapytanie(query, 2);

                        }

                    }

                    break;

                default:

                    break;

            }
        } catch (Exception e) {

        }

    }//GEN-LAST:event_jButton10ActionPerformed
    /**
     * Po kliknięciu przycisku następuje odświeżenie zawartości wyświetlonej
     * tabeli poprzez ponowne wywołanie ostatniego zapytania "SELECT".
     *
     * @param evt Obsługa zdarzenia kliknięcia przycisku.
     * @author Sebastian Zabrzyski
     */
    public void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        try {
            zapytanie(ostatnie_zapytanie, 2);
        } catch (Exception e) {

        }
    }//GEN-LAST:event_jButton11ActionPerformed
    /**
     * Po kliknięciu przycisku następuje modyfikacja pól wybranego rekordu
     * tabeli w bazie danych.
     *
     * @param evt Obsługa zdarzenia kliknięcia przycisku.
     * @author Sebastian Zabrzyski
     */
    public void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed

        int row = jTable1.getSelectedRow();
        if (row >= 0) {

            String tabela = String.valueOf(jComboBox1.getSelectedItem());
            final JFrame frame1 = new JFrame();
            try {
                switch (tabela) {

                    case "Pasazerowie":

                        String p_id = (String) JOptionPane.showInputDialog(
                                frame1,
                                "Podaj numer ID pasażera:"
                                + "",
                                "Modyfikacja pasażera",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                jTable1.getValueAt(row, 0).toString());

                        if (p_id != null && p_id.length() > 0) {

                            String p_imie = (String) JOptionPane.showInputDialog(
                                    frame1,
                                    "Podaj imię:\n"
                                    + "(max 50 znaków)",
                                    "Modyfikacja pasażera",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    jTable1.getValueAt(row, 1).toString());

                            if (p_imie != null && p_imie.length() > 0) {

                                String p_nazwisko = (String) JOptionPane.showInputDialog(
                                        frame1,
                                        "Podaj nazwisko:\n"
                                        + "(max 50 znaków)",
                                        "Modyfikacja pasażera",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        null,
                                        jTable1.getValueAt(row, 2).toString());

                                if (p_nazwisko != null && p_nazwisko.length() > 0) {

                                    String p_pesel = (String) JOptionPane.showInputDialog(
                                            frame1,
                                            "Podaj numer PESEL:\n"
                                            + "(dokładnie 11 znaków)",
                                            "Modyfikacja pasażera",
                                            JOptionPane.PLAIN_MESSAGE,
                                            null,
                                            null,
                                            jTable1.getValueAt(row, 3).toString());

                                    if (p_pesel != null && p_pesel.length() > 0) {

                                        String query = "UPDATE Pasazerowie SET ID_Pasazera = " + p_id + ", Imie = '" + p_imie + "', Nazwisko = '" + p_nazwisko + "', Pesel = " + p_pesel + " WHERE ID_Pasazera = " + jTable1.getValueAt(row, 0).toString() + "";
                                        zapytanie(query, 2);
                                    }

                                }

                            }

                        }

                        break;

                    case "Pracownicy":

                        String z_id = (String) JOptionPane.showInputDialog(
                                frame1,
                                "Podaj numer ID pracownika:"
                                + "",
                                "Modyfikacja pracownika",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                jTable1.getValueAt(row, 0).toString());

                        if (z_id != null && z_id.length() > 0) {

                            String z_imie = (String) JOptionPane.showInputDialog(
                                    frame1,
                                    "Podaj imię:\n"
                                    + "(max 50 znaków)",
                                    "Modyfikacja pracownika",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    jTable1.getValueAt(row, 1).toString());

                            if (z_imie != null && z_imie.length() > 0) {

                                String z_nazwisko = (String) JOptionPane.showInputDialog(
                                        frame1,
                                        "Podaj nazwisko:\n"
                                        + "(max 50 znaków)",
                                        "Modyfikacja pracownika",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        null,
                                        jTable1.getValueAt(row, 2).toString());

                                if (z_nazwisko != null && z_nazwisko.length() > 0) {

                                    String z_pesel = (String) JOptionPane.showInputDialog(
                                            frame1,
                                            "Podaj numer PESEL:\n"
                                            + "(dokładnie 11 znaków)",
                                            "Modyfikacja pracownika",
                                            JOptionPane.PLAIN_MESSAGE,
                                            null,
                                            null,
                                            jTable1.getValueAt(row, 3).toString());

                                    if (z_pesel != null && z_pesel.length() > 0) {

                                        String z_r = (String) JOptionPane.showInputDialog(
                                                frame1,
                                                "Podaj rolę pracownika:"
                                                + "",
                                                "Modyfikacja pracownika",
                                                JOptionPane.PLAIN_MESSAGE,
                                                null,
                                                null,
                                                jTable1.getValueAt(row, 4).toString());

                                        if (z_r != null && z_r.length() > 0) {

                                            String query = "UPDATE Pracownicy SET ID_Pracownika = " + z_id + ", Imie = '" + z_imie + "', Nazwisko = '" + z_nazwisko + "', Pesel = " + z_pesel + ", Rola = '" + z_r + "' WHERE ID_Pracownika = " + jTable1.getValueAt(row, 0).toString() + "";
                                            zapytanie(query, 2);
                                        }

                                    }

                                }

                            }

                        }

                        break;

                    case "Loty":

                        String l_id = (String) JOptionPane.showInputDialog(
                                frame1,
                                "Podaj numer ID lotu:"
                                + "",
                                "Modyfikacja lotu",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                jTable1.getValueAt(row, 0).toString());

                        if (l_id != null && l_id.length() > 0) {

                            String l_kid = (String) JOptionPane.showInputDialog(
                                    frame1,
                                    "Podaj nr ID kierunku:"
                                    + "",
                                    "Modyfikacja lotu",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    jTable1.getValueAt(row, 1).toString());

                            if (l_kid != null && l_kid.length() > 0) {

                 String l_s = (String) JOptionPane.showInputDialog(
                                        frame1,
                                        "Podaj nr ID samolotu:"
                                        + "",
                                        "Modyfikacja lotu",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        null,
                                        jTable1.getValueAt(row, 2).toString());

                                if (l_s != null && l_s.length() > 0) {
                                    
                String l_data = (String) JOptionPane.showInputDialog(
                                        frame1,
                                        "Podaj datę lotu:\n"
                                        + "(format YYYY-MM-DD)",
                                        "Modyfikacja lotu",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        null,
                                        jTable1.getValueAt(row, 3).toString());

                                if (l_data != null && l_data.length() > 0) {

                                    String query = "UPDATE Loty SET ID_Lotu = " + l_id + ", ID_Kierunku = " + l_kid + ", ID_Samolotu = " + l_s + ", Data_lotu = '" + l_data + "' WHERE ID_Lotu= " + jTable1.getValueAt(row, 0).toString() + "";
                                    zapytanie(query, 2);

                                }
                           
                                }

                            }

                        }

                        break;
                        
                        
                         case "Samoloty":

                        String s_id = (String) JOptionPane.showInputDialog(
                                frame1,
                                "Podaj numer ID samolotu:"
                                + "",
                                "Modyfikacja samolotu",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                jTable1.getValueAt(row, 0).toString());

                        if (s_id != null && s_id.length() > 0) {

                            String s_m= (String) JOptionPane.showInputDialog(
                                    frame1,
                                    "Podaj nazwę modelu:"
                                    + "",
                                    "Modyfikacja samolotu",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    jTable1.getValueAt(row, 1).toString());

                            if (s_m != null && s_m.length() > 0) {

                 String s_rp = (String) JOptionPane.showInputDialog(
                                        frame1,
                                        "Podaj rok produkcji"
                                        + "",
                                        "Modyfikacja samolotu",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        null,
                                        jTable1.getValueAt(row, 2).toString());

                                if (s_rp != null && s_rp.length() > 0) {
                                    
                String s_lp = (String) JOptionPane.showInputDialog(
                                        frame1,
                                        "Podaj liczbę pasażerów"
                                        + "",
                                        "Modyfikacja samolotu",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        null,
                                        jTable1.getValueAt(row, 3).toString());

                                if (s_lp != null && s_lp.length() > 0) {

                                    String query = "UPDATE Samoloty SET ID_Samolotu = " + s_id + ", Model = " + s_m + ", Rok_produkcji = " + s_rp + ", Liczba_pasazerow = '" + s_lp + "' WHERE ID_Samolotu= " + jTable1.getValueAt(row, 0).toString() + "";
                                    zapytanie(query, 2);

                                }
                           
                                }

                            }

                        }

                        break;    

                    case "Kierunki":

                        String k_id = (String) JOptionPane.showInputDialog(
                                frame1,
                                "Podaj numer ID kierunku:"
                                + "",
                                "Modyfikacja kierunku",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                jTable1.getValueAt(row, 0).toString());

                        if (k_id != null && k_id.length() > 0) {

                            String k_z = (String) JOptionPane.showInputDialog(
                                    frame1,
                                    "Lot z miejscowości:\n"
                                    + "(max 50 znaków)",
                                    "Modyfikacja kierunku",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    jTable1.getValueAt(row, 1).toString());

                            if (k_z != null && k_z.length() > 0) {

                                String k_do = (String) JOptionPane.showInputDialog(
                                        frame1,
                                        "Lot do miejscowości:\n"
                                        + "(max 50 znaków)",
                                        "Modyfikacja kierunku",
                                        JOptionPane.PLAIN_MESSAGE,
                                        null,
                                        null,
                                        jTable1.getValueAt(row, 2).toString());

                                if (k_do != null && k_do.length() > 0) {

                                    String query = "UPDATE Kierunki SET ID_Kierunku = " + k_id + ", Skad = '" + k_z + "', Dokad = '" + k_do + "' WHERE ID_Kierunku = " + jTable1.getValueAt(row, 0).toString() + "";
                                    zapytanie(query, 2);

                                }

                            }

                        }

                        break;

                    case "Pracownicy_lot":

                        String zl_pid = (String) JOptionPane.showInputDialog(
                                frame1,
                                "Podaj numer ID pracownika"
                                + "",
                                "Modyfikacja lotu pracownika",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                jTable1.getValueAt(row, 0).toString());

                        if (zl_pid != null && zl_pid.length() > 0) {

                            String zl_lid = (String) JOptionPane.showInputDialog(
                                    frame1,
                                    "Podaj nr ID lotu:"
                                    + "",
                                    "Modyfikacja lotu pracownika",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    jTable1.getValueAt(row, 1).toString());

                            if (zl_lid != null && zl_lid.length() > 0) {

                                String query = "UPDATE Pracownicy_lot SET ID_Pracownika = " + zl_pid + ", ID_Lotu = " + zl_lid + " WHERE ID_Pracownika = " + jTable1.getValueAt(row, 0).toString() + " AND ID_Lotu = " + jTable1.getValueAt(row, 1).toString() + "";
                                zapytanie(query, 2);

                            }

                        }

                        break;

                    case "Pasazerowie_lot":

                        String pl_pid = (String) JOptionPane.showInputDialog(
                                frame1,
                                "Podaj numer ID pasażera"
                                + "",
                                "Modyfikacja lotu pasażera",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                jTable1.getValueAt(row, 0).toString());

                        if (pl_pid != null && pl_pid.length() > 0) {

                            String pl_lid = (String) JOptionPane.showInputDialog(
                                    frame1,
                                    "Podaj nr ID lotu:"
                                    + "",
                                    "Modyfikacja lotu pasażera",
                                    JOptionPane.PLAIN_MESSAGE,
                                    null,
                                    null,
                                    jTable1.getValueAt(row, 1).toString());

                            if (pl_lid != null && pl_lid.length() > 0) {

                                String query = "UPDATE Pasazerowie_lot SET ID_Pasazera = " + pl_pid + ", ID_Lotu = " + pl_lid + " WHERE ID_Pasazera = " + jTable1.getValueAt(row, 0).toString() + " AND ID_Lotu = " + jTable1.getValueAt(row, 1).toString() + "";
                                zapytanie(query, 2);

                            }

                        }

                        break;

                    default:

                        break;

                }
            } catch (Exception e) {

            }

        } else {
            String tresc = "Nie zaznaczono żadnego rekordu";
            blad(tresc);

        }


    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
 jComboBox3.removeAllItems();
jComboBox3.addItem("");
jComboBox3.addItem("Pasazerowie");
jComboBox3.addItem("Pracownicy");
jComboBox3.addItem("Kierunki");
jComboBox3.addItem("Loty");
jComboBox3.addItem("Samoloty");
jComboBox3.addItem("Pasazerowie_lot");
jComboBox3.addItem("Zaloga_lot");

jComboBox6.removeAllItems();
jComboBox6.addItem("");
jComboBox6.addItem("mniejsze od");
jComboBox6.addItem("mniejsze lub równe");
jComboBox6.addItem("większe od");
jComboBox6.addItem("większe lub równe");
jComboBox6.addItem("równe");

jLabel5.setText("policz minimum");
jButton13.setEnabled(false);
jButton14.setEnabled(false);
jButton15.setEnabled(false);
jButton16.setEnabled(false);
this.setEnabled(false);
jDialog1.setTitle("Policz minimum");
jDialog1.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
        jDialog1.setVisible(true);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
jComboBox3.removeAllItems();
jComboBox3.addItem("");
jComboBox3.addItem("Pasazerowie");
jComboBox3.addItem("Pracownicy");
jComboBox3.addItem("Kierunki");
jComboBox3.addItem("Loty");
jComboBox3.addItem("Samoloty");
jComboBox3.addItem("Pasazerowie_lot");
jComboBox3.addItem("Zaloga_lot");

jComboBox6.removeAllItems();
jComboBox6.addItem("");
jComboBox6.addItem("mniejsze od");
jComboBox6.addItem("mniejsze lub równe");
jComboBox6.addItem("większe od");
jComboBox6.addItem("większe lub równe");
jComboBox6.addItem("równe");

jLabel5.setText("policz wystąpienia");
jButton13.setEnabled(false);
jButton14.setEnabled(false);
jButton15.setEnabled(false);
jButton16.setEnabled(false);
this.setEnabled(false);

jDialog1.setTitle("Policz wystąpienia");
jDialog1.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
        jDialog1.setVisible(true);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed


jComboBox3.removeAllItems();
jComboBox3.addItem("");
jComboBox3.addItem("Pasazerowie");
jComboBox3.addItem("Pracownicy");
jComboBox3.addItem("Kierunki");
jComboBox3.addItem("Loty");
jComboBox3.addItem("Samoloty");
jComboBox3.addItem("Pasazerowie_lot");
jComboBox3.addItem("Zaloga_lot");

jComboBox6.removeAllItems();
jComboBox6.addItem("");
jComboBox6.addItem("mniejsze od");
jComboBox6.addItem("mniejsze lub równe");
jComboBox6.addItem("większe od");
jComboBox6.addItem("większe lub równe");
jComboBox6.addItem("równe");

jLabel5.setText("policz maximum");
jButton13.setEnabled(false);
jButton14.setEnabled(false);
jButton15.setEnabled(false);
jButton16.setEnabled(false);

this.setEnabled(false);
jDialog1.setTitle("Policz maximum");
jDialog1.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
        jDialog1.setVisible(true);
      
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
jComboBox3.removeAllItems();
jComboBox3.addItem("");
jComboBox3.addItem("Pasazerowie");
jComboBox3.addItem("Pracownicy");
jComboBox3.addItem("Kierunki");
jComboBox3.addItem("Loty");
jComboBox3.addItem("Samoloty");
jComboBox3.addItem("Pasazerowie_lot");
jComboBox3.addItem("Zaloga_lot");

jComboBox6.removeAllItems();
jComboBox6.addItem("");
jComboBox6.addItem("mniejsze od");
jComboBox6.addItem("mniejsze lub równe");
jComboBox6.addItem("większe od");
jComboBox6.addItem("większe lub równe");
jComboBox6.addItem("równe");

jLabel5.setText("policz średnią");
jButton13.setEnabled(false);
jButton14.setEnabled(false);
jButton15.setEnabled(false);
jButton16.setEnabled(false);
this.setEnabled(false);
jDialog1.setTitle("Policz średnią");
jDialog1.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
        jDialog1.setVisible(true);
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jDialog1WindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_jDialog1WindowClosed

    }//GEN-LAST:event_jDialog1WindowClosed

    private void jDialog1ComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jDialog1ComponentMoved

    }//GEN-LAST:event_jDialog1ComponentMoved

    private void jDialog1ComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jDialog1ComponentHidden
        jDialog1.setVisible(false);
        jButton13.setEnabled(true);
        jButton14.setEnabled(true);
        jButton15.setEnabled(true);
        jButton16.setEnabled(true);
        this.setEnabled(true);
        this.toFront();
    }//GEN-LAST:event_jDialog1ComponentHidden

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        jDialog1.setVisible(false);
        jButton13.setEnabled(true);
        jButton14.setEnabled(true);
        jButton15.setEnabled(true);
        jButton16.setEnabled(true);
        this.setEnabled(true);
        this.toFront();
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed

        String q2 = "";
        String q1 = "";

        String s_from = String.valueOf(jComboBox3.getSelectedItem());
        String s_select = String.valueOf(jComboBox4.getSelectedItem());
        String s_where = String.valueOf(jComboBox5.getSelectedItem());
        String s_is = String.valueOf(jComboBox6.getSelectedItem());
        String s_istext = jTextField1.getText();
        String funkcja = "";
        if(jLabel5.getText().equals("policz wystąpienia")) {
            funkcja = "COUNT";

        }
        if(jLabel5.getText().equals("policz średnią")) {
            funkcja = "AVG";

        }
        if(jLabel5.getText().equals("policz minimum")) {
            funkcja = "MIN";

        }
        if(jLabel5.getText().equals("policz maximum")) {
            funkcja = "MAX";

        }

        q1 = "SELECT " + funkcja + "(" + s_select + ") FROM " + s_from;

        if (s_istext.length() > 0) {

            switch (s_is) {
                case "mniejsze od":
                s_is = "<";
                break;

                case "mniejsze lub równe":
                s_is = "<=";
                break;

                case "większe od":
                s_is = ">";
                break;

                case "większe lub równe":
                s_is = ">=";
                break;

                case "równe":
                s_is = "=";
                break;

                default:
                jComboBox6.setEnabled(true);
                jTextField1.setEnabled(true);
                break;
            }

            s_istext = "'" + s_istext + "'";

            q2 = " WHERE " + s_where + " " + s_is + " " + s_istext;

        } else {

            q2 = "";
        }

        String q = q1 + q2;

        try {
            zapytanie(q, 0);
        } catch (Exception e) {

        }
        jButton13.setEnabled(true);
        jButton14.setEnabled(true);
        jButton15.setEnabled(true);
        jButton16.setEnabled(true);
        this.setEnabled(true);
        this.toFront();
        jDialog1.setVisible(false);

    }//GEN-LAST:event_jButton17ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox6ActionPerformed
        String tabela3 = String.valueOf(jComboBox6.getSelectedItem());
        switch (tabela3) {
            case "":

            jTextField1.setEnabled(false);
            jTextField1.setText("");
            break;
            default:

            jTextField1.setEnabled(true);
            break;
        }
    }//GEN-LAST:event_jComboBox6ActionPerformed

    private void jComboBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox5ActionPerformed
        String tabela2 = String.valueOf(jComboBox5.getSelectedItem());
        switch (tabela2) {
            case "":
            jComboBox6.setEnabled(false);
            jComboBox6.setSelectedItem("");
            jTextField1.setEnabled(false);
            jTextField1.setText("");
            break;
            default:
            jComboBox6.setEnabled(true);

            break;
        }
    }//GEN-LAST:event_jComboBox5ActionPerformed

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        String tabela4 = String.valueOf(jComboBox4.getSelectedItem());
        switch (tabela4) {
            case "":
            jButton17.setEnabled(false);
            jComboBox5.setEnabled(false);
            jComboBox6.setEnabled(false);
            jComboBox6.setSelectedItem("");
            jComboBox5.setSelectedItem("");
            jTextField1.setEnabled(false);
            jTextField1.setText("");
            break;
            default:
            jButton17.setEnabled(true);
            jComboBox5.setEnabled(true);

            break;
        }
    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed

        String tabela1 = String.valueOf(jComboBox3.getSelectedItem());
        switch (tabela1) {
            case "Pasazerowie":

            if(jLabel5.getText().equals("policz wystąpienia")) {

                jComboBox4.removeAllItems();
                jComboBox4.addItem("");
                jComboBox4.addItem("ID_Pasazera");
                jComboBox4.addItem("Imie");
                jComboBox4.addItem("Nazwisko");
                jComboBox4.addItem("Pesel");

            } else {

                jComboBox4.removeAllItems();
                jComboBox4.addItem("");
                jComboBox4.addItem("ID_Pasazera");
                jComboBox4.addItem("Pesel");

            }

            jComboBox5.removeAllItems();

            jComboBox5.addItem("");
            jComboBox5.addItem("ID_Pasazera");
            jComboBox5.addItem("Imie");
            jComboBox5.addItem("Nazwisko");
            jComboBox5.addItem("Pesel");

            jComboBox4.setEnabled(true);

            break;
            case "Pracownicy":

            if(jLabel5.getText().equals("policz wystąpienia")) {

                jComboBox4.removeAllItems();
                jComboBox4.addItem("");
                jComboBox4.addItem("ID_Pracownika");
                jComboBox4.addItem("Imie");
                jComboBox4.addItem("Nazwisko");
                jComboBox4.addItem("Pesel");
                jComboBox4.addItem("Rola");
            } else {
                jComboBox4.removeAllItems();
                jComboBox4.addItem("");
                jComboBox4.addItem("ID_Pracownika");
                jComboBox4.addItem("Pesel");

            }

            jComboBox5.removeAllItems();
            jComboBox5.addItem("");
            jComboBox5.addItem("ID_Pracownika");
            jComboBox5.addItem("Imie");
            jComboBox5.addItem("Nazwisko");
            jComboBox5.addItem("Pesel");
            jComboBox5.addItem("Rola");

            jComboBox4.setEnabled(true);

            break;
            case "Kierunki":

            if(jLabel5.getText().equals("policz wystąpienia")) {
                jComboBox4.removeAllItems();
                jComboBox4.addItem("");
                jComboBox4.addItem("ID_Kierunku");
                jComboBox4.addItem("Skad");
                jComboBox4.addItem("Dokad");
            } else {
                jComboBox4.removeAllItems();
                jComboBox4.addItem("");
                jComboBox4.addItem("ID_Kierunku");
            }

            jComboBox5.removeAllItems();
            jComboBox5.addItem("");
            jComboBox5.addItem("ID_Kierunku");
            jComboBox5.addItem("Skad");
            jComboBox5.addItem("Dokad");

            jComboBox4.setEnabled(true);

            break;
            case "Loty":

            if(jLabel5.getText().equals("policz wystąpienia")) {
                jComboBox4.removeAllItems();
                jComboBox4.addItem("");
                jComboBox4.addItem("ID_Lotu");
                jComboBox4.addItem("ID_Kierunku");
                jComboBox4.addItem("ID_Samolotu");
                jComboBox4.addItem("Data_lotu");
            } else {
                jComboBox4.removeAllItems();
                jComboBox4.addItem("");
                jComboBox4.addItem("ID_Lotu");
                jComboBox4.addItem("ID_Kierunku");
                jComboBox4.addItem("ID_Samolotu");
            }

            jComboBox5.removeAllItems();
            jComboBox5.addItem("");
            jComboBox5.addItem("ID_Lotu");
            jComboBox5.addItem("ID_Kierunku");
            jComboBox5.addItem("ID_Samolotu");
            jComboBox5.addItem("Data_lotu");

            jComboBox4.setEnabled(true);

            break;
            case "Samoloty":

            if(jLabel5.getText().equals("policz wystąpienia")) {
                jComboBox4.removeAllItems();
                jComboBox4.addItem("");
                jComboBox4.addItem("ID_Samolotu");
                jComboBox4.addItem("Model");
                jComboBox4.addItem("Rok_produkcji");
                jComboBox4.addItem("Liczba_pasazerow");
            } else {
                jComboBox4.removeAllItems();
                jComboBox4.addItem("");
                jComboBox4.addItem("ID_Samolotu");
                jComboBox4.addItem("Rok_produkcji");
                jComboBox4.addItem("Liczba_pasazerow");
            }

            jComboBox5.removeAllItems();
            jComboBox5.addItem("");
            jComboBox5.addItem("ID_Samolotu");
            jComboBox5.addItem("Model");
            jComboBox5.addItem("Rok_produkcji");
            jComboBox5.addItem("Liczba_pasazerow");

            jComboBox4.setEnabled(true);

            break;
            case "Pasazerowie_lot":

            jComboBox4.removeAllItems();
            jComboBox4.addItem("");
            jComboBox4.addItem("ID_Pasazera");
            jComboBox4.addItem("ID_Lotu");

            jComboBox5.removeAllItems();
            jComboBox5.addItem("");
            jComboBox5.addItem("ID_Pasazera");
            jComboBox5.addItem("ID_Lotu");

            jComboBox4.setEnabled(true);

            break;
            case "Pracownicy_lot":

            jComboBox4.removeAllItems();
            jComboBox4.addItem("");
            jComboBox4.addItem("ID_Pracownika");
            jComboBox4.addItem("ID_Lotu");

            jComboBox5.removeAllItems();
            jComboBox5.addItem("");
            jComboBox5.addItem("ID_Pracownika");
            jComboBox5.addItem("ID_Lotu");

            jComboBox4.setEnabled(true);

            default:

            jComboBox4.setEnabled(false);

            jComboBox6.setEnabled(false);

            jComboBox4.setSelectedItem("");
            jComboBox5.setSelectedItem("");
            jComboBox6.setSelectedItem("");

            break;
        }
    }//GEN-LAST:event_jComboBox3ActionPerformed

    
    
    

    
    
//MAIN
    /**
     * Metoda wyświetlająca okno główne aplikacji.
     *
     * @param args[] Parametry typu String podawane podczas uruchamiania
     * aplikacji.
     * @throws XMLStreamException Wyjątek jest rzucany w razie problemów z
     * parsowaniem pliku XML.
     * @throws FileNotFoundException Wyjątek jest rzucany w razie problemów ze
     * znalezieniem pliku na dysku twardym.
     * @author NetBeans IDE GUI Builder
     */
    public static void main(String args[]) throws FileNotFoundException, XMLStreamException {

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
            java.util.logging.Logger.getLogger(SkybaseManager2017.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SkybaseManager2017.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SkybaseManager2017.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SkybaseManager2017.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                new SkybaseManager2017().setVisible(true);

            }
        });

    }
//MAIN

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables
}
