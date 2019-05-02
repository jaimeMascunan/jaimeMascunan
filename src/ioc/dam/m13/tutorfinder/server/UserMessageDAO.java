/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ioc.dam.m13.tutorfinder.server;

import ioc.dam.m13.tutorfinder.dtos.UserMessageDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 *
 * @author jaime
 */
public class UserMessageDAO {

     //TODO: documentar crateAd
    public boolean createMessage ( int userId, String senderName, String message, String date, 
            int receiverId, String receiverName){
        
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
            sql += "INSERT INTO user_message (sender_id, sender_name, message_text, message_date, receiver_id, receiver_name) ";
            sql += "VALUES (?, ?, ? ,?, ?, ?) ";
            
                        
            //preparem la inserció
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, userId);
            pstm.setString(2, senderName);
            pstm.setString(3, message);
            pstm.setString(4, date);
            pstm.setInt(5, receiverId);
            pstm.setString(6, receiverName);

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
    public ArrayList<UserMessageDTO> listMessagesByUser(int userId, int receiverId) {
        ArrayList<UserMessageDTO> messages = new ArrayList<>();
        
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
               
        try {
            // Agafem una connexió del pool
            con = ConnectionPool.getPool().getConnection();
            //SQL
            String sql = "";
            sql += "SELECT message_id, "
                    + "sender_id, "
                    + "sender_name, "
                    + "message_text, "
                    + "message_date, "
                    + "receiver_id, "
                    + "receiver_name ";
            sql += "FROM user_message ";
            sql += "WHERE sender_id = ? AND receiver_id = ?";
            sql += "ORDER BY message_id ";
            
            //Fem la consulta
            pstm = con.prepareStatement(sql);
            pstm.setInt(1, userId);
            pstm.setInt(2, receiverId);
            rs = pstm.executeQuery();
            
            while (rs.next()) {                
                // Construïm l'usuari amb la resposta
                UserMessageDTO message = new UserMessageDTO();
                
                message.setMessageId(rs.getInt("message_id"));
                message.setMessageUserId(rs.getInt("sender_id"));
                message.setMessageUserName(rs.getString("sender_name"));
                message.setMessageText(rs.getString("message_text"));
                message.setMessageDate(rs.getString("message_date"));
                message.setReceiverUserId(rs.getInt("receiver_id"));
                message.setReceiverUserName(rs.getString("receiver_name"));
               
                messages.add(message);              
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
        return messages;
    }
}
