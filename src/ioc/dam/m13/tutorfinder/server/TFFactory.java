package ioc.dam.m13.tutorfinder.server;

import java.util.ResourceBundle;

/**
 * Factory per instanciar diferents implementacions dels DAO's
 * a traves de l'arxiu de configuració "ioc.dam.m13.tutor_finder.server.factory.properties".
 * 
 * @author José Luis Puentes Jiménez <jlpuentes74@gmail.com>
 */
public class TFFactory {
    
    /**
     * Crea una instacia de la classe demanada per parametre
     * @param objName Nom de la clase 
     * @return Retorna un objecte de la classe demanada
     */
    public static Object getInstance(String objName) {
        try {
            //Llegim el factory.properties
            ResourceBundle rb = ResourceBundle.getBundle("ioc.dam.m13.tutorfinder.server.factory");
            //Agafem la classe
            String className = rb.getString(objName);
            //Retornem una instancia de la classe que hem agafat
            Object ret = Class.forName(className).newInstance();
            
            return ret;
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException(e);
            
        }
    }

}
