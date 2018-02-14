package mp3tagedit.de.exceptions;

/**
 * Created by Vincent on 14.02.2018.
 */

public class ListSizeMaxReachedException extends Exception {
    public ListSizeMaxReachedException() { super(); }
    public ListSizeMaxReachedException(String message) { super(message); }
    public ListSizeMaxReachedException(String message, Throwable cause) { super(message, cause); }
    public ListSizeMaxReachedException(Throwable cause) { super(cause); }
}
