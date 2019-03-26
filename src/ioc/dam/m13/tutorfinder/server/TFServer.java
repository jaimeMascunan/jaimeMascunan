package ioc.dam.m13.tutorfinder.server;

import ioc.dam.m13.tutorfinder.dtos.UserDTO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor que s'encarrega de rebre les solicituts dels clients
 * connectar amb el servidor de BBDD i retornar les dades als clients
 * @author José Luis Puentes Jiménez <jlpuentes74@gmail.com>
 */
public class TFServer extends Thread{
    
    public static final int SERVER_PORT = 7474;
    public static final int LOGIN = 0;
    public static final int USER_DATA = 1;
    public static final int REGISTER = 2;
    
    private Socket socket = null;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
        
    public TFServer( Socket s) {
        
        this.socket = s;        
    }
    
    public static void main(String[] args) throws Exception {
        
        ServerSocket ss = new ServerSocket(SERVER_PORT);
        Socket s;
        
        System.out.println("Server is running ...");
        
        while (true) {            
            
            s = ss.accept();
            new TFServer(s).start();
            
        }
    }
    
    @Override
    public void run() {
        
        try {
            
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            
            //Para probar como hacerlo enviando objetos
            //Faltaria crear un objeto Login o un objeto User
            
            /*
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            Object obj = in.readObject();
            
            if (obj.getClass().equals(UserDAO)) {
                
            }*/
            
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
                    
                case REGISTER:
                    _register(dis, dos);
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
        
        try {
            
            UserDAO dao =  (UserDAO) TFFactory.getInstance("USER");
            
            //Llegin l'usuari i la contrasenya del client
            String usr = dis.readUTF();
            String pwd = dis.readUTF();
            
            //TODO: Mostra entrades del client per proves
            System.out.println("user: " + usr);
            System.out.println("password: " + pwd);
            
            //Preparem la resposta
            boolean ret = dao.login(usr, pwd);
            
            //Enviem la resposta
            dos.writeBoolean(ret);
            
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
            //dos.writeUTF(userDTO.getUserPswd());
            dos.writeUTF(userDTO.getUserRole());
            
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
            
        }
    }
    
    private void _register(DataInputStream dis, DataOutputStream dos) {
        
        try {
            
            UserDAO dao =  (UserDAO) TFFactory.getInstance("USER");
            
            //Llegin l'usuari i la contrasenya del client
            String usr = dis.readUTF();
            String email = dis.readUTF();
            String pwd = dis.readUTF();
            String user_type = dis.readUTF();
            
            //TODO: Mostra entrades del client per proves
            System.out.println("user: " + usr);
            System.out.println("email " + email);
            System.out.println("password: " + pwd);
            System.out.println("user type " + user_type);
            
            //Preparem la resposta
            boolean ret = dao.registerUser(usr, email, pwd, user_type);
            
            //Enviem la resposta
            dos.writeBoolean(ret);
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
            
        }
    }
}
