package ioc.dam.m13.tutor_finder.pruebas;



import ioc.dam.m13.tutorfinder.client.TFClient;
import ioc.dam.m13.tutorfinder.client.TFClientImple;
import ioc.dam.m13.tutorfinder.dtos.UserDTO;
import java.util.Scanner;

/**
 * Test per comprovar el client fent login 
 * retorni les dades de l'usuari.
 * 
 * @author José Luis Puentes Jiménez <jlpuentes74@gmail.com>
 */
public class TestSrv2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        TestSrv2 prg = new TestSrv2();
        prg.inici();

    }
    
    public void inici(){
        
        TFClient client = new TFClientImple();
        UserDTO userDTO = null;
        
        System.out.println("---Login--- ");
        System.out.println("User: ");
        Scanner scanner = new Scanner(System.in);
        String user = scanner.nextLine();
        System.out.println("Password: ");
        String pwd = scanner.nextLine();
        
        boolean ret = client.login(user, pwd);
        
        if (ret) {
            userDTO = client.userData(user);
            
            System.out.println(userDTO.toString());
            
        } else {
            
            System.out.println("Aquest ususari no existeix!!");
        }
        
    
    }

}
