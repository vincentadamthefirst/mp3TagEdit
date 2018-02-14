package mp3tagedit.de.exceptions;

/**
 * Created by Vincent on 14.02.2018.
 */

public class NoFileInListException extends Exception {
    public NoFileInListException() { super(); }
    public NoFileInListException(String message) { super(message); }
    public NoFileInListException(String message, Throwable cause) { super(message, cause); }
    public NoFileInListException(Throwable cause) { super(cause); }
}
