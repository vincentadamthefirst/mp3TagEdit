package mp3tagedit.de.exceptions;

/**
 * Created by Vincent on 14.02.2018.
 */

public class ListNotInitializedException extends Exception {
    public ListNotInitializedException() { super(); }
    public ListNotInitializedException(String message) { super(message); }
    public ListNotInitializedException(String message, Throwable cause) { super(message, cause); }
    public ListNotInitializedException(Throwable cause) { super(cause); }
}
