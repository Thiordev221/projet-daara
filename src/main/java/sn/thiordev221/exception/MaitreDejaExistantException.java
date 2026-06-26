package sn.thiordev221.exception;

public class MaitreDejaExistantException extends DaaraException{
    public MaitreDejaExistantException(String matricule){
        super("Maitre avec matricule " + matricule + " existant");
    }
}
