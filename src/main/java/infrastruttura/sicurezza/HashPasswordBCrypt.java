package infrastruttura.sicurezza;

import applicazione.interfacce.IHashPassword;
import org.mindrot.jbcrypt.BCrypt;

public class HashPasswordBCrypt implements IHashPassword {

    @Override
    public boolean corrisponde(String passwordInChiaro, String hashPassword) {
        return BCrypt.checkpw(passwordInChiaro, hashPassword);
    }
}