package sn.thiordev221.exception;

public class ProgressionIntrouvableException extends DaaraException{
    public ProgressionIntrouvableException(String id){
        super("Aucune progression pour l'id " + id);
    }
}
