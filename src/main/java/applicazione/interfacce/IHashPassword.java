package applicazione.interfacce;

public interface IHashPassword {
    boolean corrisponde(String plainPassword, String passwordHash);
}