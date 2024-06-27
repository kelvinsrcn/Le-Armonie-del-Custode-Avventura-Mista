package Other;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.h2.tools.RunScript;

import Mondo.Casella;
import Tipi.Dialogo;
import Tipi.Item;

public class GestioneDB {

    private static Connection conn;
    private Properties proprietaDB;
    private static GestioneDB instance = null;

    /**
     * Costruttore della classe GestioneDB
     * 
     * @throws SQLException
     */
    private GestioneDB() throws SQLException {
        connect();
    }

    public static GestioneDB getInstance() throws SQLException {
        if (instance == null) {
            instance = new GestioneDB();
        }
        return instance;
    }

    /**
     * Metodo per connettersi al database
     * 
     * @throws SQLException
     */
    private void connect() throws SQLException {
        proprietaDB = new Properties();
        proprietaDB.setProperty("user", "kevin");
        proprietaDB.setProperty("password", "prova");
        conn = DriverManager.getConnection("jdbc:h2:./database", proprietaDB);
    }

    /**
     * Metodo per creare il database
     * 
     * @throws SQLException
     * @throws FileNotFoundException
     */
    public void crea() throws SQLException, FileNotFoundException {
        RunScript.execute(conn, new FileReader("src\\main\\java\\Other\\caselleDB.sql"));
    }

    /**
     * Metodo per caricare la mappa
     * 
     * @return Casella
     * @throws FileNotFoundException
     * @throws SQLException
     */
    public Casella loadMappa(final List<Casella> caselle, int tentativi) throws FileNotFoundException, SQLException {
        Casella start = null;
        Map<Integer, Casella> mapCaselle = new HashMap<>();
        try {
            String getCaselle = "SELECT * FROM Casella";
            Statement stm = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stm.executeQuery(getCaselle);
            while (rs.next()) {
                Casella casella = new Casella(rs.getInt(1));
                casella.setNome(rs.getString(2));
                casella.setDescrizioneIniziale(rs.getString("descrizioneIniziale"));
                casella.setDescrizioneAggiuntiva(rs.getString("descrizioneAggiuntiva"));
                casella.setDescrizioneAggiornata(rs.getString("descrizioneAggiornata"));
                casella.setEnterable(rs.getBoolean("enterable"));
                mapCaselle.put(casella.getId(), casella);
                caselle.add(casella);
                loadItems(casella, 0);

                /*
                 * DEBUG per inizio rapido
                 * 901 Casa Luca
                 * 107 Castello
                 */
                if (casella.getId() == 901) {
                    start = casella;
                }
            }
            rs.beforeFirst();
            while (rs.next()) {
                Casella casella = mapCaselle.get(rs.getInt(1));
                casella.setNorth(mapCaselle.get(rs.getInt(6)));
                casella.setSouth(mapCaselle.get(rs.getInt(7)));
                casella.setEast(mapCaselle.get(rs.getInt(8)));
                casella.setWest(mapCaselle.get(rs.getInt(9)));
            }

            rs.close();
        } catch (org.h2.jdbc.JdbcSQLNonTransientException e) {
            tentativi++;
            connect();
            if (tentativi < 5)
                start = loadMappa(caselle, tentativi);
            else
                throw e;
        } catch (SQLException e) {
            tentativi++;
            crea();
            if (tentativi < 5)
                start = loadMappa(caselle, tentativi);
            else
                throw e;
        }

        return start;
    }

    public List<Dialogo> loadDialoghi(boolean priority, int tentativi) throws SQLException, FileNotFoundException {
        List<Dialogo> dialoghi = new ArrayList<>();
        try {
            String getDialoghi = "SELECT * FROM Dialoghi WHERE priorita = " + priority + ";";
            Statement stm = conn.createStatement();
            try (ResultSet rs = stm.executeQuery(getDialoghi)) {
                while (rs.next()) {
                    Dialogo d = new Dialogo(rs.getInt(1));
                    d.setInfoDialogo(rs.getString(3), rs.getString(6), rs.getString(4), rs.getString(5));
                    dialoghi.add(d);
                }
                rs.close();
            }
        } catch (org.h2.jdbc.JdbcSQLNonTransientException e) {
            tentativi++;
            connect();
            if (tentativi < 5)
                dialoghi = loadDialoghi(priority, tentativi);
            else
                throw e;
        } catch (SQLException e) {
            tentativi++;
            crea();
            if (tentativi < 5)
                dialoghi = loadDialoghi(priority, tentativi);
            else
                throw e;
        }

        return dialoghi;
    }

    public void changeDialogo(Dialogo dialogo, int tentativi) throws SQLException, FileNotFoundException {
        try {
            String getDialoghi = "SELECT * FROM Dialoghi WHERE priorita = " + true + " AND id = "
                    + dialogo.getIdCasella() + ";";
            Statement stm = conn.createStatement();
            try (ResultSet rs = stm.executeQuery(getDialoghi)) {
                while (rs.next()) {
                    dialogo.setInfoDialogo(rs.getString(3), rs.getString(6), rs.getString(4), rs.getString(5));
                }
                rs.close();
            }
        } catch (org.h2.jdbc.JdbcSQLNonTransientException e) {
            tentativi++;
            connect();
            if (tentativi < 5)
                changeDialogo(dialogo, tentativi);
            else
                throw e;
        } catch (SQLException e) {
            tentativi++;
            crea();
            if (tentativi < 5)
                changeDialogo(dialogo, tentativi);
            else
                throw e;
        }
    }

    private void loadItems(Casella c, int tentativi) throws SQLException, FileNotFoundException {
        try {
            String getCaselle = "SELECT * FROM Oggetto WHERE id = " + c.getId() + ";";
            Statement stm = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stm.executeQuery(getCaselle);
            while (rs.next()) {
                Item item = new Item(rs.getString(2).toLowerCase());
                item.setDescription(rs.getString(3));
                Integer quantity = Integer.valueOf(rs.getString(4));
                if (rs.getString(5).equals("TRUE")) {
                    item.setPickable(true);
                } else {
                    item.setPickable(false);
                }
                if (rs.getString(6).equals("TRUE")) {
                    item.setVisible(true);
                } else {
                    item.setVisible(false);
                }

                item.setAlias(getAlias(c.getId(), 0));
                c.addOggetto(item, quantity);
            }
            rs.close();
        } catch (org.h2.jdbc.JdbcSQLNonTransientException e) {
            e.printStackTrace();
            tentativi++;
            connect();
            if (tentativi < 5)
                loadItems(c, tentativi);
            else
                throw e;
        } catch (SQLException e) {
            e.printStackTrace();
            tentativi++;
            crea();
            if (tentativi < 5)
                loadItems(c, tentativi);
            else
                throw e;
        }

    }

    private String[] getAlias(int id, int tentativi) throws SQLException, FileNotFoundException {
        List<String> aliasList = new ArrayList<>();
        String[] alias;
        try {
            String getAlias = "SELECT alias FROM Alias WHERE id = " + id + ";";
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(getAlias);
            while (rs.next()) {
                aliasList.add(rs.getString(1).toLowerCase());
                // System.out.println(aliasList.get(aliasList.size() - 1));
            }
            rs.close();
            alias = aliasList.toArray(new String[aliasList.size()]);
        } catch (org.h2.jdbc.JdbcSQLNonTransientException e) {
            tentativi++;
            connect();
            if (tentativi < 5)
                alias = getAlias(id, tentativi);
            else
                throw e;
        } catch (SQLException e) {
            tentativi++;
            crea();
            if (tentativi < 5)
                alias = getAlias(id, tentativi);
            else
                throw e;
        }

        return alias;
    }

    public void close() throws SQLException {
        conn.close();
    }
}
