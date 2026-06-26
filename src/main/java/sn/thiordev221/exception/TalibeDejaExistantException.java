package sn.thiordev221.exception;

public class TalibeDejaExistantException extends DaaraException{
    public TalibeDejaExistantException(String matricule){
        super("Talibé dèja existante " + matricule);
    }
}
