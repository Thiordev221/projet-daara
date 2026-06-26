package sn.thiordev221.exception;

public class TalibeIntrouvableException extends DaaraException{
    public TalibeIntrouvableException(String matricule){
        super("Aucun Talibe pour le matricule " + matricule);
    }
}
