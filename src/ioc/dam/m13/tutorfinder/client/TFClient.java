package ioc.dam.m13.tutorfinder.client;

import ioc.dam.m13.tutorfinder.dtos.UserDTO;

/**
 * Interface per crear les diferents implementacions 
 * de clients amb els serveis del TFserver
 * @author José Luis Puentes Jiménez <jlpuentes74@gmail.com>
 */
public interface TFClient {
    /**
     * Demana per fer login al servidor
     * @param userName Nom d'usuari al servidor
     * @param pswd Contrasenya de l'usuari
     * @return True si l'usuari és al server
     */    
    public boolean login(String userName, String pswd);
    
    /**
     * Demana les dades de l'usuari
     * @param userName Nom de l'usuari
     * @return Retorna el objecte amb les dades d'usuari
     */
    public UserDTO userData(String userName);
}
