package ioc.dam.m13.tutorfinder.server;

import ioc.dam.m13.tutorfinder.dtos.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Accedeix a les dades dun usuari que hi han a la BBDD
 * @author José Luis Puentes Jiménez <jlpuentes74@gmail.com>
 */
public class UserDAO {
    
    /**
     * Comprova si l'usuari i la contrasenya son correctes a la BBDD
     * @param userName Nom de l'usuari
     * @param pswd Contrasenya de l'usuari
     * @return Retorna true si hi es a la BBDD
     */
    public boolean login(String userName, String pswd) {
        
        boolean ret = false;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        
        try {
            // Agafem una connexio del pool
            con = ConnectionPool.getPool().getConnection();
            
            //SQL
            String sql = "";
            sql += "SELECT user_name, user_pswd ";
            sql += "FROM users ";
            sql += "WHERE user_name = ?";
            
            //Fem la consulta
            pstm = con.prepareStatement(sql);
            pstm.setString(1, userName);
            rs = pstm.executeQuery();
            String dbPswd = null;
            
            while (rs.next()) {  
                
                dbPswd = rs.getString("user_pswd");
                System.out.println("user password dao: " + dbPswd);
            }
            
            
            // Comparem la contrasenya amb el resultat de la consulta
            if (pswd.equals(dbPswd)) {                
                ret = true;
            }
                    
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
            
        } finally {
            
            try {
                // Tanquem connexions
                if (rs != null) { rs.close();}
                if (pstm != null) { pstm.close();}
                
            } catch (Exception e) {
                
                e.printStackTrace();
                throw new RuntimeException(e);   
            }
        }
        return ret;
    }
    /**
     * Demana les dades que hi ha d'un usuari a la BBDD 
     * @param userName Nom de l'usuari
     * @return Retorna un objecte UserDTO amb les dades de l'usuari
     */
    public UserDTO userData(String userName) {
        
        UserDTO user = new UserDTO();
        
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            // Agafem una connexió del pool
            con = ConnectionPool.getPool().getConnection();
            //SQL
            String sql = "";
            sql += "SELECT user_id, user_name, user_mail, user_pswd, role_name ";
            sql += "FROM users, roles ";
            sql += "WHERE users.user_name = ? AND users.user_role_id = roles.role_id";
            
            //Fem la consulta
            pstm = con.prepareStatement(sql);
            pstm.setString(1, userName);
            rs = pstm.executeQuery();
            
            while (rs.next()) {                
                
                // Construïm l'usuari amb la resposta
                user.setUserId(rs.getInt("user_id"));
                user.setUserName(rs.getString("user_name"));
                user.setUserMail(rs.getString("user_mail"));
                user.setUserPswd(rs.getString("user_pswd"));
                user.setUserRole(rs.getString("role_name"));
                System.out.println(user.toString());          
            }
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            
            try {
                // Tanquem connexions
                if (rs != null) { rs.close();}
                if (pstm != null) { pstm.close();}
                
            } catch (Exception e) {
                
                e.printStackTrace();
                throw new RuntimeException(e);            
            }
        }
        return user;
    }

    /**
     * Inserta un usuari a la base de dades
     * @param userName
     * @param email
     * @param password
     * @param userType
     * @return 
     */
    public boolean registerUser(String userName, String email, String password, String userType) {
        
        boolean ret = false;
        Connection con = null;
        PreparedStatement pstm = null;
        int success;
        
        try {
            // Agafem una connexio del pool
            con = ConnectionPool.getPool().getConnection();
            
            //SQL
            String sql = "";
            sql += "INSERT INTO users (user_name, user_mail, user_pswd, user_role_id) ";
            sql += "VALUES (?, ?, ?, (SELECT role_id FROM roles WHERE role_name = ?))";
            
            //Fem la consulta
            pstm = con.prepareStatement(sql);
            pstm.setString(1, userName);
            pstm.setString(2, email);
            pstm.setString(3, password);
            pstm.setString(4, userType);
            
            //Ens retornara el numero de columnes afectades
            success = pstm.executeUpdate();
            
            //Fem el commit de la cunsilta
            con.commit();
                    
            if(success > 0){
                ret = true;
                System.out.println("todo bien");
            }else{
                System.out.println("Fallo en la ejecucion de la consulta");
            }
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
            
        } finally {
            
            try {
                // Tanquem connexions
                if (pstm != null) { pstm.close();}
                
            } catch (Exception e) {
                
                e.printStackTrace();
                throw new RuntimeException(e);             
            }
        }       
        return ret;
    }
}
