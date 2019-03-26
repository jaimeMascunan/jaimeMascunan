package ioc.dam.m13.tutorfinder.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Pool de connexions al servidor BBDD.
 * La configuració dels limits del es fan al
 * "ioc.dam.m13.tutor_finder.server.conn_pool_conf.properties"
 * @author José Luis Puentes Jiménez <jlpuentes74@gmail.com>
 */
public class ConnectionPool {
    
    private Vector<Connection> freeConn;
    private Vector<Connection> usedConn;
    
    private String url;
    private String driver;
    private String usr;
    private String pwd;
    
    private int minsize;
    private int maxsize;
    private int steep;
    
    private static ConnectionPool pool = null;
    
    private ConnectionPool(){
        
        try {
            // Agafem les dades de l'arxiu de configuració
            ResourceBundle rb = ResourceBundle.getBundle("ioc.dam.m13.tutorfinder.server.conn_pool_conf");
            
            url = rb.getString("url");
            driver = rb.getString("driver");
            usr = rb.getString("usr");
            pwd = rb.getString("pwd");
            
            /* Dedes de prova per anular el ResouceBundle
            usr = "tfadmin";
            pwd = "kermit74";
            driver = "org.postgresql.Driver";
            url = "jdbc:postgresql://192.168.0.105:5432/TutorFinderDB";
            */
            
            // Aixequem el driver
            Class.forName(driver);
            
            // Agafem els parametres del pool
            minsize = Integer.parseInt(rb.getString("minsize"));
            maxsize = Integer.parseInt(rb.getString("maxsize"));
            steep = Integer.parseInt(rb.getString("steep"));
            
            freeConn = new Vector<>();
            usedConn = new Vector<>();
            
            // instancio les primeres connexions
            _connInstance(minsize);
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
            
        }
    }
    /**
     * Mostra el nombre de conexions disponibles i en ús.
     * @return Retorna en format String les connxions en ús i les disponibles
     */
    public String toString() {
        return "Free connections: " + freeConn.size() +
                ", used connections: " + usedConn.size();
        
    }
    /**
     * Retorna un pool de connexions al servidor de BBDD
     * @return ConnectionPool
     */
    public synchronized static ConnectionPool getPool() {
        
        if (pool == null) {
            pool =  new ConnectionPool();            
        }
        
        return pool;        
    }
    /**
     * Retorna una connexió a la BBDD
     * @return Connection
     */
    public synchronized Connection getConnection() {
        
        if (freeConn.size() == 0) {
            if (!_createMoreConnections()) {
                throw new RuntimeException("No hi han mes connexions disponibles");
            }
        }
        
        Connection con = freeConn.remove(0);
        usedConn.add(con);
        return con;
    }
    /**
     * Esborra una connexio la pila de connexions en ús i
     * la posa a la pila de connexions lliures
     * @param con Connexió a esborrar de les que està en ús
     */
    public synchronized void releaseConnection(Connection con) {
        
        boolean ok = usedConn.remove(con);
        
        if (ok) {
            freeConn.add(con);
            
        } else {
            throw  new RuntimeException("Ha retornat una connexió que nos ens pertany");
        }
    }
    /**
     * Tanca totes les connexions al servidor BBDD.
     */
    public synchronized void close() {
        
        try {
            
            //Tanquem les connexions
            for (Connection con : freeConn) {
                con.close();
            }
            
            for (Connection con : usedConn) {
                con.close();
            }
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
            
        }
    }
    /**
     * Crea un nombre de connexions determinat al servidor de BBDD
     * @param n Nombre de connexions a crear
     */
    private void _connInstance(int n) {
        
        try {
            Connection con;
            for (int i = 0; i < n; i++) {
                con = DriverManager.getConnection(url, usr, pwd);
                con.setAutoCommit(false);
                freeConn.add(con);                
            }
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
            
        }
    }
    /**
     * Afegeix mes connexions al pool si no s'ha sobrepassat 
     * als limits configurats al "conn_pool_conf.properties"
     * @return Retorna true si s'han creat mes connexions
     */
    private boolean _createMoreConnections() {
        
        int nConnActual = freeConn.size() + usedConn.size();
        int n = Math.min(maxsize - nConnActual, steep);
        
        if (n > 0) {
            System.out.println("Creant " + n + " conexions noves...");
            _connInstance(n);
        }
        
        return n > 0;
    }

}
