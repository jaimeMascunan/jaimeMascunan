package ioc.dam.m13.tutorfinder.client;

import ioc.dam.m13.tutorfinder.dtos.UserDTO;

/**
 *  Clase que implemetarà el client per demanar els serveis del 
 *  servidor
 * @author José Luis Puentes Jiménez <jlpuentes74@gmail.com>
 */
public class TFClientImple implements TFClient{

    @Override
    public boolean login(String userName, String pswd) {
        
        return ServiceLocator.login(userName, pswd);
        
    }
    
    @Override
    public UserDTO userData(String userName) {
        
        return ServiceLocator.userData(userName);
        
    }

}
