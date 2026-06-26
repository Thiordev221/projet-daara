package sn.thiordev221.exception;

public class ClasseIntrouvableException extends DaaraException{
    public ClasseIntrouvableException(String code){
        super("Aucune classe pour le code " + code);
    }
}
