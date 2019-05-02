package ioc.dam.m13.tutorfinder.server;

import ioc.dam.m13.tutorfinder.dtos.UserDTO;
import ioc.dam.m13.tutorfinder.dtos.AdDTO;
import ioc.dam.m13.tutorfinder.dtos.UserMessageDTO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Servidor que s'encarrega de rebre les solicituts dels clients
 * connectar amb el servidor de BBDD i retornar les dades als clients
 * @author José Luis Puentes Jiménez <jlpuentes74@gmail.com>
 */
public class TFServer extends Thread{
    
    public static final int SERVER_PORT = 7474;
    public static final int LOGIN = 0;
    public static final int USER_DATA = 1;
    public static final int NEW_USER = 2;
    public static final int EDIT_USER = 3;
    public static final int LIST_USERS = 5;
    public static final int DEL_USER = 4;
    public static final int CREATE_AD = 8;
    public static final int LIST_ADS_ROLE = 11;
    public static final int LIST_ADS_USER = 10;
    public static final int LIST_ADS_BY_ADMIN = 19;
    public static final int DELETE_PRODUCT = 14;
    public static final int EDIT_USER_PASSWORD = 6;
    public static final int EDIT_PRODUCT = 13;
    public static final int BOOKING_PRODUCT = 12;
    public static final int LIST_ADS_BOOKED_USER = 7;
    public static final int LIST_ADS_BOOKED_OTHER = 18;
    public static final int CANCEL_BOOKING_PRODUCT = 9;
    public static final int GET_AD_TYPE_BY_NAME = 17;
    public static final int CREATE_MESSAGE = 20;
    public static final int LIST_MESSAGES_USER = 21;
    
    private static final String LOGIN_CODE_STRING = "loginString";
    private static final String LOGIN_CODE_BOOLEAN = "loginBoolean";
    
    private static SSLSocket socket = null;
    private static SSLServerSocket serverSocket = null;
    private static DataInputStream dis = null;
    private static DataOutputStream dos = null;
        
    public TFServer( SSLSocket s) {
        
        this.socket = s;        
    }
    
    public static void main(String[] args) throws Exception {
        
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream("src\\ioc\\dam\\m13\\tutorfinder\\certs\\server\\ServerKeyStore.jks"),
            "tutorfinder".toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, "tutorfinder".toCharArray());

        KeyStore trustedStore = KeyStore.getInstance("JKS");
        trustedStore.load(new FileInputStream(
            "src\\ioc\\dam\\m13\\tutorfinder\\certs\\server\\serverTrustedCerts.jks"), "tutorfinder"
            .toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustedStore);

        SSLContext sc = SSLContext.getInstance("TLS");
        TrustManager[] trustManagers = tmf.getTrustManagers();
        KeyManager[] keyManagers = kmf.getKeyManagers();
        sc.init(keyManagers, trustManagers, null);

        SSLServerSocketFactory ssf = sc.getServerSocketFactory();
        serverSocket = (SSLServerSocket) ssf.createServerSocket(SERVER_PORT);
        
        System.out.println("Server is running ...");
        
        while (true) {            
            
            socket = (SSLSocket) serverSocket.accept();
            System.out.println("client accepted");
            TFServer server = new TFServer(socket);
            server.start();
            server.join();      
        }
    }
    
    @Override
    public void run() {
        
        try {
            
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            //llegim el codi de servei
            int srvCod = dis.readInt();
            
            //TODO: Mostra les provas de connexió
            System.out.println("cliente pide: " + srvCod);
                   
            switch(srvCod) {
                case LOGIN:
                    _login(dis, dos);
                    break;
                    
                case USER_DATA:
                    _userData(dis, dos);
                    break;
                    
                case NEW_USER:
                    _newUser(dis, dos);
                    break;
                    
                case EDIT_USER:
                    _editUser(dis, dos);
                    break;
                
                case LIST_USERS:
                    _listUsers(dis,dos);
                    break;
                    
                case DEL_USER:
                    _delUser(dis, dos);
                    break;
                    
                case CREATE_AD:
                    _createAd(dis, dos);
                    break;   
                
                case LIST_ADS_ROLE:
                    _listAdsByRole(dis, dos);
                    break;
                    
                case LIST_ADS_USER:
                    _listAdsByUser(dis, dos);
                    break;
                    
                case DELETE_PRODUCT:
                    _delAd(dis,dos);
                    break;
                    
                case EDIT_USER_PASSWORD:
                    _editUserPswd(dis,dos);
                    break;
                    
                case EDIT_PRODUCT:
                    _editAd(dis,dos);
                    break;
                    
                case BOOKING_PRODUCT:
                    _bookAd(dis,dos);
                    break;
                    
                case LIST_ADS_BOOKED_USER:
                    _listAdsBookedByUser(dis,dos);
                    break;
                    
                case LIST_ADS_BOOKED_OTHER:
                    _listAdsBookedByOther(dis,dos);
                    break;
                    
                case CANCEL_BOOKING_PRODUCT:
                    _cancelBookAd(dis,dos);
                    break;
                    
                case GET_AD_TYPE_BY_NAME:
                    _getAdTypeByName(dis,dos);
                    break;
                    
                case LIST_ADS_BY_ADMIN:
                    _listAdsByAdmin(dis,dos);
                    break;
                    
                case CREATE_MESSAGE:
                    _createMessage(dis,dos);
                    break;
                
                case LIST_MESSAGES_USER:
                    _listMessagesByUser(dis,dos);
                    break;
            }
                    
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }        
    }
    /**
     * Comprova si l'usuari i la contrasenya demanades per la connexió amb el client
     * son correctes al servidor de BBDD, i retorna la resposta per la mateixa connexió 
     * @param dis InputStream del client
     * @param dos OutputStream del client
     */
    private void _login(DataInputStream dis, DataOutputStream dos) {
        String usr, pwd, ret_str;
        Boolean ret_bol;
        
        try {
            
            UserDAO dao =  (UserDAO) TFFactory.getInstance("USER");
           
            switch(dis.readUTF()){
                //Mètode listUsers()
                case LOGIN_CODE_STRING:
                    //Llegin l'usuari i la contrasenya del client
                    usr= dis.readUTF();

                    //TODO: Mostra entrades del client per proves
                    System.out.println("user: " + usr);

                    //Preparem la resposta
                    ret_str = dao.login(usr);

                    //En cas de que sigui nul vol dir que no ha trobat l'usuari a la base de dades
                    if (ret_str == null){
                       ret_str = ""; 
                    }
                    System.out.println(ret_str);

                    //Enviem la resposta
                    dos.writeUTF(ret_str);
                    dos.flush();
                
                    break;
                
                case LOGIN_CODE_BOOLEAN:
                    //Llegin l'usuari i la contrasenya del client
                    usr= dis.readUTF();
                    pwd = dis.readUTF();
           
                    //TODO: Mostra entrades del client per proves
                    System.out.println("user: " + usr);
                    System.out.println("password: " + pwd);
            
                    //Preparem la resposta
                    ret_bol = dao.login(usr, pwd);

                    //Enviem la resposta
                    dos.writeBoolean(ret_bol);
                    dos.flush();
                    
                    break;
            }
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);          
        }
    }
    /**
     * Demana al servidor de BBDD les dades de l'usuari demanat per la 
     * connexió del client, i retorna la resposta per la mateixa connexió 
     * @param dis InputStream del client
     * @param dos OutputStream del client
     */
    
    private void _userData(DataInputStream dis, DataOutputStream dos) {
        try {
            
            UserDAO dao = (UserDAO) TFFactory.getInstance("USER");         
            //Llegin l'usuari i la contrasenya del client
            String usr = dis.readUTF();                      
            //Preparem la resposta
            UserDTO userDTO = dao.userData(usr);
                        
            //Enviem la resposta
            dos.writeInt(userDTO.getUserId());
            dos.writeUTF(userDTO.getUserName());
            dos.writeUTF(userDTO.getUserMail());
            dos.writeUTF(userDTO.getUserPswd());
            dos.writeUTF(userDTO.getUserRole());
            dos.flush();
            System.out.println("user: " + userDTO.getUserId());
                    
        } catch (Exception e) {       
            e.printStackTrace();
            throw new RuntimeException(e);        
        }
    }
    /** 
     * @param dis
     * @param dos 
     */
    private void _newUser(DataInputStream dis, DataOutputStream dos) {
        try {
            
            UserDAO dao =  (UserDAO) TFFactory.getInstance("USER");
            
            //Llegim les dades del client per crear un nou usari
            String userName = dis.readUTF();
            String userMail = dis.readUTF();
            String userPswd = dis.readUTF();
            String userRole = dis.readUTF();
            System.out.println("user: " + userName);
            
            //Creem el nou usuari
            boolean ret = dao.newUser(userName, userMail, userPswd, userRole);
            
            //Enviem la resposta
            dos.writeBoolean(ret);
            dos.flush();
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
            
        }
    }
    /**
     * Modifica les dades d'un ususari amb les dades que rep del client
     * @param dis DataInputStream del client
     * @param dos DataOutputStream del client
     */
    private void _editUser(DataInputStream dis, DataOutputStream dos) {
        
        try {
            
            UserDAO dao = (UserDAO) TFFactory.getInstance("USER");
            
            //Llegim les dades del client a canviar 
            int userId = dis.readInt();
            String userName = dis.readUTF();
            String userMail = dis.readUTF();
            String userRole = dis.readUTF();
            System.out.println("user: " + userName);
            
            //Canviem les dades de l'usari
            boolean ret = dao.editUser(userId, userName, userMail, userRole);
            
            //Retornem al client el resultat
            dos.writeBoolean(ret);
            dos.flush();
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    /**
     * @param dis
     * @param dos 
     */
    private void _editUserPswd(DataInputStream dis, DataOutputStream dos) {
        try {
            
            UserDAO dao =  (UserDAO) TFFactory.getInstance("USER");
            
            //Llegim les dades del client per canbiar la contrasenya
            String userName = dis.readUTF();
            String newPswd = dis.readUTF();
            
            //TODO: Mostra entrades del client per proves
            System.out.println("id " + userName);
            System.out.println("user: " + newPswd);
            
            //Canviem la contrasenya de l'usari
            boolean ret = dao.editUserPswd(userName, newPswd);
            
            //Enviem la resposta
            dos.writeBoolean(ret);
            dos.flush();
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);    
        }
    }
    /**
     * 
     * @param dis
     * @param dos 
     */
    private void _listUsers(DataInputStream dis, DataOutputStream dos) {
         try {
            UserDAO dao = (UserDAO) TFFactory.getInstance("USER");
            ArrayList<UserDTO> users;
            int nUsers;
            
            //Llegim quin mètode sobre carrgat del UserDAO es vol fer servir 
            switch(dis.readUTF()){
                //Mètode listUsers()
                case "listUsers":
                    //Demanen a la BBDD la llista d'usuaris
                    users = dao.listUsers();
                    
                    //Enviem el nombre d'usuaris que hi ha de resposta
                    nUsers = users.size();
                    dos.writeInt(nUsers);
                    dos.flush();
                    
                    for (UserDTO userDTO : users) {
                        //Retornem els objectes per separat al client
                        dos.writeInt(userDTO.getUserId());
                        dos.writeUTF(userDTO.getUserName());
                        dos.writeUTF(userDTO.getUserMail());
                        dos.writeUTF(userDTO.getUserPswd());
                        dos.writeUTF(userDTO.getUserRole());
                        dos.flush();
                    }
                    break;
                                   
            }
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
        }      
    }
    
    /**
     * Esborra un ususari de la BBDD
     * @param dis DataInputStream del client
     * @param dos DataOutputStream del client
    */
    private void _delUser(DataInputStream dis, DataOutputStream dos) {
        try {
            UserDAO dao = (UserDAO) TFFactory.getInstance("USER");
            
            System.out.println("Usuari llegit correctament: prova");
            //Llegim les dades del client per eliminar l'usari
            String userName = dis.readUTF();
            System.out.println("Usuari llegit correctament:" + userName);
            //Eliminem l'usuari
            boolean ret = dao.delUser(userName);
            
            //Retornem al client el resultat
            dos.writeBoolean(ret);
            dos.flush();
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    /** 
     * @param dis
     * @param dos 
     */
    private void _createAd(DataInputStream dis, DataOutputStream dos) {
        try {
            
            AdDAO dao =  (AdDAO) TFFactory.getInstance("AD");
            
            //Llegin les dades de l'anunci a publicar
            Integer ad_user_id = dis.readInt();
            String ad_title = dis.readUTF();
            String ad_description = dis.readUTF();
            Integer ad_type = dis.readInt();
            Integer ad_price= dis.readInt();

            //Preparem la resposta
            boolean ret = dao.createAd(ad_user_id, ad_title, ad_description, ad_type, ad_price);
            
            //Enviem la resposta
            dos.writeBoolean(ret);
            dos.flush();
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);   
        }
    }
    /**
     * @param dis
     * @param dos 
     */
    private void _listAdsByUser(DataInputStream dis, DataOutputStream dos) {
       try {
            AdDAO dao = (AdDAO) TFFactory.getInstance("AD");
            ArrayList<AdDTO> ads = new ArrayList<>();
            int nAds;
            //Llegim l'id d'usuari
            int userId = dis.readInt();
            
            //Demanem a la BBDD la llista de tots els anuncis
            ads = dao.listAdsByUser(userId);
            //Enviem el nombre d'anuncis que hi ha de resposta
            nAds = ads.size();
            dos.writeInt(nAds);
            dos.flush();
            
            for (AdDTO ad : ads) {
                //Retornem els objectes per separat al client
                dos.writeInt(ad.getAdId());
                dos.writeInt(ad.getAdUserId());
                dos.writeUTF(ad.getUserName());
                dos.writeUTF(ad.getAdTittle());
                dos.writeUTF(ad.getAdDescription());
                dos.writeInt(ad.getAdTypeId());
                dos.writeUTF(ad.getTypesName());
                dos.writeInt(ad.getAdPrice());
                System.out.println("user: " + ad.getAdId());
                dos.flush();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    /**
     * @param dis
     * @param dos 
     */
    private void _listAdsByRole(DataInputStream dis, DataOutputStream dos) {
        try {
            AdDAO dao = (AdDAO) TFFactory.getInstance("AD");
            ArrayList<AdDTO> ads = new ArrayList<>();
            int nAds;
            //Llegim l'id del rol
            int roleId = dis.readInt();
            //Demanem a la BBDD la llista de tots els anuncis
            ads = dao.listAdsByRole(roleId);
            //Enviem el nombre d'anuncis que hi ha de resposta
            nAds = ads.size();
            dos.writeInt(nAds);
            dos.flush();
            
            for (AdDTO ad : ads) {
                //Retornem els objectes per separat al client
                dos.writeInt(ad.getAdId());
                dos.writeInt(ad.getAdUserId());
                dos.writeUTF(ad.getUserName());
                dos.writeUTF(ad.getAdTittle());
                dos.writeUTF(ad.getAdDescription());
                dos.writeInt(ad.getAdTypeId());
                dos.writeUTF(ad.getTypesName());
                dos.writeInt(ad.getAdPrice());
                dos.flush();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    /**
     * @param dis
     * @param dos 
     */
    private void _listAdsByAdmin(DataInputStream dis, DataOutputStream dos) {
        try {
            AdDAO dao = (AdDAO) TFFactory.getInstance("AD");
            ArrayList<AdDTO> ads = new ArrayList<>();
            int nAds;
            //Llegim l'id del rol
            int roleId = dis.readInt();
            //Demanem a la BBDD la llista de tots els anuncis
            ads = dao.listAdsByAdmin(roleId);
            //Enviem el nombre d'anuncis que hi ha de resposta
            nAds = ads.size();
            dos.writeInt(nAds);
            dos.flush();
            
            for (AdDTO ad : ads) {
                //Retornem els objectes per separat al client
                dos.writeInt(ad.getAdId());
                dos.writeInt(ad.getAdUserId());
                dos.writeUTF(ad.getUserName());
                dos.writeUTF(ad.getAdTittle());
                dos.writeUTF(ad.getAdDescription());
                dos.writeInt(ad.getAdTypeId());
                dos.writeUTF(ad.getTypesName());
                dos.writeInt(ad.getAdPrice());
                dos.flush();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    private void _listAdsBookedByUser(DataInputStream dis, DataOutputStream dos) {
        try {
            
            AdDAO dao = (AdDAO) TFFactory.getInstance("AD");
            List<AdDTO> ads;
            int nAds;
            
            //LLegim el user_role_id corresponent al tipus d'usuaris que volem llistar
            Integer user_id = dis.readInt();

            //Demanen a la BBDD la llista d'usuaris
            ads = dao.listAdsBookedByUser(user_id);

            //Enviem el nombre d'usuaris que hi ha de resposta
            nAds = ads.size();
            dos.writeInt(nAds);
            dos.flush();
            System.out.println("Numero productes " + nAds);
            
                for (AdDTO adDTO : ads) {
                    //Retornem els objectes per separat al client
                    dos.writeInt(adDTO.getAdId());
                    dos.writeInt(adDTO.getAdUserId());
                    dos.writeUTF(adDTO.getUserName());
                    dos.writeUTF(adDTO.getAdTittle());
                    dos.writeUTF(adDTO.getAdDescription());
                    dos.writeInt(adDTO.getAdTypeId());
                    dos.writeUTF(adDTO.getTypesName());
                    dos.writeInt(adDTO.getAdPrice());
                    dos.writeInt(adDTO.getAdUserReservaId());
                    dos.writeUTF(adDTO.getAdUserReservaName());
                    dos.flush();
                }
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
        }      
    } 
    
    private void _listAdsBookedByOther(DataInputStream dis, DataOutputStream dos) {
        try {
            
            AdDAO dao = (AdDAO) TFFactory.getInstance("AD");
            List<AdDTO> ads;
            int nAds;
            
            //LLegim el user_role_id corresponent al tipus d'usuaris que volem llistar
            Integer user_id = dis.readInt();

            //Demanen a la BBDD la llista d'usuaris
            ads = dao.listAdsBookedByOther(user_id);

            //Enviem el nombre d'usuaris que hi ha de resposta
            nAds = ads.size();
            dos.writeInt(nAds);
            dos.flush();
            System.out.println("Numero productes " + nAds);
            
                for (AdDTO adDTO : ads) {
                    //Retornem els objectes per separat al client
                    dos.writeInt(adDTO.getAdId());
                    dos.writeInt(adDTO.getAdUserId());
                    dos.writeUTF(adDTO.getUserName());
                    dos.writeUTF(adDTO.getAdTittle());
                    dos.writeUTF(adDTO.getAdDescription());
                    dos.writeInt(adDTO.getAdTypeId());
                    dos.writeUTF(adDTO.getTypesName());
                    dos.writeInt(adDTO.getAdPrice());
                    dos.writeInt(adDTO.getAdUserReservaId());
                    dos.writeUTF(adDTO.getAdUserReservaName());
                    dos.flush();
                }
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
        }      
    } 
    /**
     * Esborra un ususari de la BBDD
     * @param dis DataInputStream del client
     * @param dos DataOutputStream del client
    */
    private void _delAd(DataInputStream dis, DataOutputStream dos) {
        try {
            AdDAO dao = (AdDAO) TFFactory.getInstance("AD");
            
            System.out.println("Producte llegit correctament: prova");
            //Llegim les dades del client per eliminar l'usari
            Integer productId = dis.readInt();
            System.out.println("Producte llegit correctament:" + productId);
            //Eliminem l'usuari
            boolean ret = dao.delAd(productId);
            
            //Retornem al client el resultat
            dos.writeBoolean(ret);
            dos.flush();
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    /**
     * @param dis
     * @param dos 
     */
    private void _editAd(DataInputStream dis, DataOutputStream dos) {
        try {
            
            AdDAO dao =  (AdDAO) TFFactory.getInstance("AD");
            
            //Llegin l'usuari i la contrasenya del client
            Integer ad_id = dis.readInt();
            String ad_title = dis.readUTF();
            String ad_descripcio = dis.readUTF();
            Integer ad_type_id = dis.readInt();
            Integer ad_price = dis.readInt();

            
            //Preparem la resposta
            boolean ret = dao.editAd(ad_id, ad_title, ad_descripcio, ad_type_id, ad_price);
            
            //Enviem la resposta
            dos.writeBoolean(ret);
            dos.flush();
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);  
        }
    }
    
     /**
     * @param dis
     * @param dos 
     */
    private void _bookAd(DataInputStream dis, DataOutputStream dos) {
        try {
            
            AdDAO dao =  (AdDAO) TFFactory.getInstance("AD");
            
            //Llegin l'usuari i la contrasenya del client
            Integer ad_id = dis.readInt();
            Integer ad_booking_user = dis.readInt();
            String ad_booking_user_name = dis.readUTF();
          
            //Preparem la resposta
            boolean ret = dao.bookAd(ad_id, ad_booking_user, ad_booking_user_name);
            
            //Enviem la resposta
            dos.writeBoolean(ret);
            dos.flush();
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);  
        }
    }
    
    /**
     * @param dis
     * @param dos 
     */
    private void _cancelBookAd(DataInputStream dis, DataOutputStream dos) {
        try {
            
            AdDAO dao =  (AdDAO) TFFactory.getInstance("AD");
            
            //Llegin l'usuari i la contrasenya del client
            Integer ad_id = dis.readInt();
         
            //Preparem la resposta
             boolean ret = dao.cancelBookAd(ad_id);
            
            //Enviem la resposta
            dos.writeBoolean(ret);
            dos.flush();
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);  
        }
    }
    
    //TODO: documentar _getAdTypeById 
    private void _getAdTypeByName(DataInputStream dis, DataOutputStream dos) {
        
        try {
           
            int adTypeId;
            AdDAO ad = (AdDAO) TFFactory.getInstance("AD");
            //Agafem del client el id a buscar
            String adTypeName = dis.readUTF();
            //Demanem a la BBDD el nom del tipus pel seu id
            adTypeId = ad.getAdTypeByName(adTypeName);

            //Retonem al client la resposta
            dos.writeInt(adTypeId);
            dos.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
    
    /** 
     * @param dis
     * @param dos 
     */
    private void _createMessage(DataInputStream dis, DataOutputStream dos) {
        try {
            
            UserMessageDAO dao =  (UserMessageDAO) TFFactory.getInstance("MESSAGE");
            
            //Llegin les dades de l'anunci a publicar
            Integer sender_id = dis.readInt();
            String sender_name = dis.readUTF();
            String message_text = dis.readUTF();
            String message_date = dis.readUTF();
            Integer receiver_id = dis.readInt();
            String receiver_name = dis.readUTF();

            //Preparem la resposta
            boolean ret = dao.createMessage(sender_id, sender_name, 
                    message_text, message_date, receiver_id, receiver_name);
            
            //Enviem la resposta
            dos.writeBoolean(ret);
            dos.flush();
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);   
        }
    }
    /**
     * @param dis
     * @param dos 
     */
    private void _listMessagesByUser(DataInputStream dis, DataOutputStream dos) {
       try {
            UserMessageDAO dao = (UserMessageDAO) TFFactory.getInstance("MESSAGE");
            ArrayList<UserMessageDTO> messages = new ArrayList<>();
            int nMessages;
            //Llegim l'id d'usuari
            int userId = dis.readInt();
            int receiverId = dis.readInt();
            
            //Demanem a la BBDD la llista de tots els anuncis
            messages = dao.listMessagesByUser(userId,  receiverId );
            //Enviem el nombre d'anuncis que hi ha de resposta
            nMessages = messages.size();
            dos.writeInt(nMessages);
            dos.flush();
            
            for (UserMessageDTO message : messages) {
                //Retornem els objectes per separat al client
                dos.writeInt(message.getMessageId());
                dos.writeInt(message.getMessageUserId());
                dos.writeUTF(message.getMessageUserName());
                dos.writeUTF(message.getMessageText());
                dos.writeUTF(message.getMessageDate());
                dos.writeInt(message.getReceiverUserId());
                dos.writeUTF(message.getReceiverUserName());
                dos.flush();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    

}
