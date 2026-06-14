package exceptions.ereignis;

import exceptions.VerwaltungsException;

public abstract class EreignisException extends VerwaltungsException {
    public EreignisException(String nachricht) {
        super(nachricht);
    }
}
