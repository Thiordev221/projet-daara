package sn.thiordev221.exception;

public class MaitreIntrouvableException extends DaaraException{
    public MaitreIntrouvableException(String matricule){
        super("Aucun maître pour le matricule " + matricule);
    }
}
