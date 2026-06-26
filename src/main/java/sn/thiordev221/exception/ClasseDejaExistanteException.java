package sn.thiordev221.exception;

public class ClasseDejaExistanteException extends  DaaraException{
    public ClasseDejaExistanteException(String code){
        super("Classe avec code :" + code + "dèja existante");
    }
}
