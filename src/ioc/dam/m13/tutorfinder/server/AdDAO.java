/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ioc.dam.m13.tutorfinder.server;

import ioc.dam.m13.tutorfinder.dtos.AdDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jaime
 */
public class AdDAO {
     //TODO: documentar crateAd
    public boolean createAd (int userId, String tittle, String description, int adTypeId, int price){
        boolean ret = false;
        int result;
        
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        
        try {
            // Agafem una connexió del pool
            con = ConnectionPool.getPool().getConnection();
            //SQL
            String sql = "";
            sql += "INSERT INTO ads (ad_user_id, ad_tittle, ad_description, ad_type_id, ad_price)";
            sql += "VALUES (?, ?, ?, ?, ?) ";
            
                        
            //preparem la inserció
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, userId);
            pstm.setString(2, tittle);
            pstm.setString(3, description);
            pstm.setInt(4, adTypeId);
            pstm.setInt(5, price);

            result = pstm.executeUpdate();
            con.commit();
            
            if (result > 0) {
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
                ConnectionPool.getPool().releaseConnection(con);
                
            } catch (Exception e) {
                
                e.printStackTrace();
                throw new RuntimeException(e);
                
            }
        }
        return ret;
    }
    
     /**
     * Llista tots els usuaris que hi ha a la BBDD
     * @return Retorna un array de UserDTO's amb les dades dels usuaris
     */
    public ArrayList<AdDTO> listAdsByUser(int userId) {
        ArrayList<AdDTO> ads = new ArrayList<>();
        
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
               
        try {
            // Agafem una connexió del pool
            con = ConnectionPool.getPool().getConnection();
            //SQL
            String sql = "";
            sql += "SELECT ad_id, "
                    + "ad_user_id, "
                    + "(select user_name from users where ad_user_id = user_id) AS user_name, "
                    + "ad_tittle, "
                    + "ad_description, "
                    + "ad_type_id, "
                    + "(select ad_types_name from ad_types where ad_type_id = ad_types_id) AS ad_types_name, "
                    + "ad_price ";
            sql += "FROM ads ";
            sql += "WHERE ad_user_id = ?";
            sql += "ORDER BY ad_id ";
            
            //Fem la consulta
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, userId);
            rs = pstm.executeQuery();
            
            while (rs.next()) {                
                // Construïm l'usuari amb la resposta
                AdDTO ad = new AdDTO();
                
                ad.setAdId(rs.getInt("ad_id"));
                ad.setAdUserId(rs.getInt("ad_user_id"));
                ad.setUserName(rs.getString("user_name"));
                ad.setAdTittle(rs.getString("ad_tittle"));
                ad.setAdDescription(rs.getString("ad_description"));
                ad.setAdTypeId(rs.getInt("ad_type_id"));
                ad.setTypesName(rs.getString("ad_types_name"));
                ad.setAdPrice(rs.getInt("ad_price"));
                
                ads.add(ad);              
            } 
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            
            try {
                // Tanquem connexions
                if (rs != null) { rs.close();}
                if (pstm != null) { pstm.close();}
                ConnectionPool.getPool().releaseConnection(con);
                
            } catch (Exception e) {
                
                e.printStackTrace();
                throw new RuntimeException(e);
                
            }
        }
        return ads;
   
    }
    //TODO: documentar listAdsByRole
    public ArrayList<AdDTO> listAdsByRole(int roleId){
        ArrayList<AdDTO> ads = new ArrayList<>();
        
        
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
               
        try {
            // Agafem una connexió del pool
            con = ConnectionPool.getPool().getConnection();
            //SQL
            String sql = "";
            sql += "SELECT ad_id, "
                    + "ad_user_id, "
                    + "(select user_name from users where ad_user_id = user_id) AS user_name, "
                    + "ad_tittle, "
                    + "ad_description, "
                    + "ad_type_id, "
                    + "(select ad_types_name from ad_types where ad_type_id = ad_types_id) AS ad_types_name, "
                    + "ad_price ";
            sql += "FROM ads, users ";
            sql += "WHERE ad_user_id = user_id AND user_role_id = ? AND ad_reservat = false ";
            sql += "ORDER BY ad_id ";
            
            //Fem la consulta
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, roleId);
            rs = pstm.executeQuery();
            
            while (rs.next()) {                
                // Construïm l'usuari amb la resposta
                AdDTO ad = new AdDTO();
                
                ad.setAdId(rs.getInt("ad_id"));
                ad.setAdUserId(rs.getInt("ad_user_id"));
                ad.setUserName(rs.getString("user_name"));
                ad.setAdTittle(rs.getString("ad_tittle"));
                ad.setAdDescription(rs.getString("ad_description"));
                ad.setAdTypeId(rs.getInt("ad_type_id"));
                ad.setTypesName(rs.getString("ad_types_name"));
                ad.setAdPrice(rs.getInt("ad_price"));
                
                ads.add(ad);
                
            }
           
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            
            try {
                // Tanquem connexions
                if (rs != null) { rs.close();}
                if (pstm != null) { pstm.close();}
                ConnectionPool.getPool().releaseConnection(con);
                
            } catch (Exception e) {
                
                e.printStackTrace();
                throw new RuntimeException(e);
                
            }
        }
        return ads;
    }
    
    public ArrayList<AdDTO> listAdsByAdmin(int roleId){
        ArrayList<AdDTO> ads = new ArrayList<>();
        
        
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
               
        try {
            // Agafem una connexió del pool
            con = ConnectionPool.getPool().getConnection();
            //SQL
            String sql = "";
            sql += "SELECT ad_id, "
                    + "ad_user_id, "
                    + "(select user_name from users where ad_user_id = user_id) AS user_name, "
                    + "ad_tittle, "
                    + "ad_description, "
                    + "ad_type_id, "
                    + "(select ad_types_name from ad_types where ad_type_id = ad_types_id) AS ad_types_name, "
                    + "ad_price ";
            sql += "FROM ads, users ";
            sql += "WHERE ad_user_id = user_id AND user_role_id = ? ";
            sql += "ORDER BY ad_id ";
            
            //Fem la consulta
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, roleId);
            rs = pstm.executeQuery();
            
            while (rs.next()) {                
                // Construïm l'usuari amb la resposta
                AdDTO ad = new AdDTO();
                
                ad.setAdId(rs.getInt("ad_id"));
                ad.setAdUserId(rs.getInt("ad_user_id"));
                ad.setUserName(rs.getString("user_name"));
                ad.setAdTittle(rs.getString("ad_tittle"));
                ad.setAdDescription(rs.getString("ad_description"));
                ad.setAdTypeId(rs.getInt("ad_type_id"));
                ad.setTypesName(rs.getString("ad_types_name"));
                ad.setAdPrice(rs.getInt("ad_price"));
                
                ads.add(ad);
                
            }
           
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            
            try {
                // Tanquem connexions
                if (rs != null) { rs.close();}
                if (pstm != null) { pstm.close();}
                ConnectionPool.getPool().releaseConnection(con);
                
            } catch (Exception e) {
                
                e.printStackTrace();
                throw new RuntimeException(e);
                
            }
        }
        return ads;
    }
    
    public List<AdDTO> listAdsBookedByUser(Integer user_id){
        ArrayList<AdDTO> ads = new ArrayList<>();
                
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
               
        try {
            // Agafem una connexió del pool
            con = ConnectionPool.getPool().getConnection();
            System.out.println("connexio creada listuProducts" + con.toString());
            //SQL
            String sql = "";
            sql += "SELECT ad_id, ad_user_id, users.user_name AS user_name, ad_tittle, ad_description, ad_type_id, ad_types_name, ad_price, ad_user_reserva, ad_user_reserva_name ";
            sql += "FROM ads, ad_types, users ";
            sql += "WHERE ads.ad_reservat = true AND ads.ad_type_id = ad_types.ad_types_id AND ads.ad_user_reserva = ? AND ads.ad_user_id = users.user_id";
            
            //Fem la consulta
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, user_id);
            rs = pstm.executeQuery();
            
            while (rs.next()) {                
                // Construïm l'usuari amb la resposta
                AdDTO ad = new AdDTO();
                
                ad.setAdId(rs.getInt("ad_id"));
                ad.setAdUserId(rs.getInt("ad_user_id"));
                ad.setUserName(rs.getString("user_name"));
                ad.setAdTittle(rs.getString("ad_tittle"));
                ad.setAdDescription(rs.getString("ad_description"));
                ad.setAdTypeId(rs.getInt("ad_type_id"));
                ad.setTypesName(rs.getString("ad_types_name"));
                ad.setAdPrice(rs.getInt("ad_price"));
                ad.setUserReservat(rs.getInt("ad_user_reserva"));
                ad.setAdUserReservaName(rs.getString("ad_user_reserva_name"));
                ads.add(ad);      
            }
            System.out.println("Ens ha demanat llistar productes d'altres usuaris booked by user");
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {       
            try {
                // Tanquem connexions
                if (rs != null) { rs.close();}
                if (pstm != null) { pstm.close();}
                ConnectionPool.getPool().releaseConnection(con);
                
            } catch (Exception e) {
                
                e.printStackTrace();
                throw new RuntimeException(e);         
            }
        }
        return ads;
    }    
    
    public List<AdDTO> listAdsBookedByOther(Integer user_id){
        ArrayList<AdDTO> ads = new ArrayList<>();
                
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
               
        try {
            // Agafem una connexió del pool
            con = ConnectionPool.getPool().getConnection();
            System.out.println("connexio creada listuProducts" + con.toString());
            //SQL
            String sql = "";
            sql += "SELECT ad_id, ad_user_id, users.user_name AS user_name, ad_tittle, ad_description, ad_type_id, ad_types_name, ad_price, ad_user_reserva, ad_user_reserva_name ";
            sql += "FROM ads, ad_types, users ";
            sql += "WHERE ads.ad_reservat = true AND ads.ad_type_id = ad_types.ad_types_id AND ads.ad_user_id = ? AND ads.ad_user_id = users.user_id";
            
            //Fem la consulta
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, user_id);
            rs = pstm.executeQuery();
            
            while (rs.next()) {                
                // Construïm l'usuari amb la resposta
                AdDTO ad = new AdDTO();
                
                ad.setAdId(rs.getInt("ad_id"));
                ad.setAdUserId(rs.getInt("ad_user_id"));
                ad.setUserName(rs.getString("user_name"));
                ad.setAdTittle(rs.getString("ad_tittle"));
                ad.setAdDescription(rs.getString("ad_description"));
                ad.setAdTypeId(rs.getInt("ad_type_id"));
                ad.setTypesName(rs.getString("ad_types_name"));
                ad.setAdPrice(rs.getInt("ad_price"));
                ad.setUserReservat(rs.getInt("ad_user_reserva"));
                ad.setAdUserReservaName(rs.getString("ad_user_reserva_name"));
                ads.add(ad);      
            }
            System.out.println("Ens ha demanat llistar productes de l'usuari booked by others");
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {       
            try {
                // Tanquem connexions
                if (rs != null) { rs.close();}
                if (pstm != null) { pstm.close();}
                ConnectionPool.getPool().releaseConnection(con);
                
            } catch (Exception e) {
                
                e.printStackTrace();
                throw new RuntimeException(e);         
            }
        }
        return ads;
    }     
    /**
     * Elimina un prodcte de la BBDD
     * @param productId String amb el id del producte a eliminar
     * @return Retorna True si s'ha eliminat
   */
    public boolean delAd(Integer productId){
        boolean ret = false;
        int success;
        
        Connection con = null;
        PreparedStatement pstm = null;
               
        try {
            // Agafem una connexió del pool
            con = ConnectionPool.getPool().getConnection();
            //SQL
            String sql = "";
            sql += "DELETE FROM ads ";
            sql += "WHERE ad_id = ?";
            
            //preparem la eliminació
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, productId);
            //Executem
            success = pstm.executeUpdate();
            con.commit();
            
            if (success > 0) {
                ret = true;
            }
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {      
            try {
                // Tanquem connexions
                if (pstm != null) { pstm.close();}
                ConnectionPool.getPool().releaseConnection(con);
                
            } catch (Exception e) {
                
                e.printStackTrace();
                throw new RuntimeException(e);
                
            }
        }
        return ret;
    }
    
    public boolean editAd(Integer ad_id, String ad_title, String ad_description, Integer ad_type_id, Integer ad_price) {
        
        boolean ret = false;
        Connection con = null;
        PreparedStatement pstm = null;
        int success;
        
        try {
            // Agafem una connexio del pool
            con = ConnectionPool.getPool().getConnection();
            System.out.println("connexio creada update product " + con.toString());
            
            //SQL
            String sql = "";
            sql += "UPDATE ads SET ad_tittle = ?, ad_description = ?, ad_type_id = ?, ad_price = ? ";
            sql += "WHERE ad_id = ?";
            
            //Fem la consulta
            pstm = con.prepareStatement(sql);
            pstm.setString(1, ad_title);
            pstm.setString(2, ad_description);
            pstm.setInt(3, ad_type_id);
            pstm.setInt(4, ad_price);
            pstm.setInt(5, ad_id);
             
            //Ens retornara el numero de columnes afectades
            success = pstm.executeUpdate();
            
            //Fem el commit de la cunsilta
            con.commit();
                    
            if(success > 0){
                ret = true;
                System.out.println("todo bien: update product");
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
                ConnectionPool.getPool().releaseConnection(con);
            } catch (Exception e) {
                
                e.printStackTrace();
                throw new RuntimeException(e);             
            }
        }       
        return ret;
    }
    
    public boolean bookAd (Integer ad_id, Integer ad_user_booking_id, String ad_user_booking_name) {
        
        boolean ret = false;
        Connection con = null;
        PreparedStatement pstm = null;
        int success;
        
        try {
            // Agafem una connexio del pool
            con = ConnectionPool.getPool().getConnection();
            System.out.println("connexio creada update product " + con.toString());
            
            //SQL
            String sql = "";
            sql += "UPDATE ads SET ad_reservat = true, ad_user_reserva = ?, ad_user_reserva_name = ? ";
            sql += "WHERE ad_id = ?";
            
            //Fem la consulta
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, ad_user_booking_id);
            pstm.setString(2, ad_user_booking_name);
            pstm.setInt(3, ad_id);
             
            //Ens retornara el numero de columnes afectades
            success = pstm.executeUpdate();
            
            //Fem el commit de la cunsilta
            con.commit();
                    
            if(success > 0){
                ret = true;
                System.out.println("todo bien: update product");
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
                ConnectionPool.getPool().releaseConnection(con);
            } catch (Exception e) {
                
                e.printStackTrace();
                throw new RuntimeException(e);             
            }
        }       
        return ret;
    }
    
    public boolean cancelBookAd (Integer ad_id) {
        
        boolean ret = false;
        Connection con = null;
        PreparedStatement pstm = null;
        int success;
        
        try {
            // Agafem una connexio del pool
            con = ConnectionPool.getPool().getConnection();
            System.out.println("connexio creada update product " + con.toString());
            
            //SQL
            String sql = "";
            sql += "UPDATE ads SET ad_reservat = false, ad_user_reserva = null ";
            sql += "WHERE ad_id = ?";
            
            //Fem la consulta
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, ad_id);
             
            //Ens retornara el numero de columnes afectades
            success = pstm.executeUpdate();
            
            //Fem el commit de la cunsilta
            con.commit();
                    
            if(success > 0){
                ret = true;
                System.out.println("todo bien: update product");
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
                ConnectionPool.getPool().releaseConnection(con);
            } catch (Exception e) {
                
                e.printStackTrace();
                throw new RuntimeException(e);             
            }
        }       
        return ret;
    }
    
    //TODO: documentar getAdTypeByName
    public Integer getAdTypeByName(String adTypeName){
        
        Integer ret = null;
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
               
        try {
            // Agafem una connexió del pool
            con = ConnectionPool.getPool().getConnection();
            //SQL
            String sql = "";
            sql += "SELECT ad_types_id ";
            sql += "FROM ad_types ";
            sql += "WHERE ad_types_name = ?";
            
            //Fem la consulta
            pstm = con.prepareStatement(sql);
            pstm.setString(1, adTypeName);
            rs = pstm.executeQuery();
            
            while (rs.next()) {                
                
                ret = rs.getInt("ad_types_id");                          
            }
                
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            
            try {
                // Tanquem connexions
                if (rs != null) { rs.close();}
                if (pstm != null) { pstm.close();}
                ConnectionPool.getPool().releaseConnection(con);
                
            } catch (Exception e) {
                
                e.printStackTrace();
                throw new RuntimeException(e);
                
            }
        }
        
        return ret;
    
    }
}
