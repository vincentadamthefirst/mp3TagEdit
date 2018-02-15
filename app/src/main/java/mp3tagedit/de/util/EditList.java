package mp3tagedit.de.util;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import mp3tagedit.de.exceptions.ListNotInitializedException;
import mp3tagedit.de.exceptions.ListSizeMaxReachedException;
import mp3tagedit.de.exceptions.NoFileInListException;

/**
 * TODO Klasse dokumentieren
 * @author Vincent Adam
 */
public class EditList implements Serializable {

    private ArrayList<String> savedQueue;
    private int position;

    private String description;
    private Date changedDate;

    private int maxSize;

    // TODO mehr Konstruktoren
    // TODO save() und load() Funktionen
    // TODO Implementierung der Zusatzinfos (Datum, Beschreibung, etc)
    // TODO vervollständigen der in-Code Dokumentation

    /**
     * Creates and Object of this Class with a description and a maximum size for the queue
     * that can not be exceeded when adding items to it
     * @param desc a string containing a description of the list, can be null if no description is
     *             needed
     * @param maxSize the maximum possible number of elements in the queue, can be set to 0 if no
     *                maximum size is needed (or call EditList(String desc))
     */
    public EditList(String desc, int maxSize) {
        this.description = desc;
        this.maxSize = maxSize;
    }

    /**
     * Creates an Object of this Class with a description
     * @param desc a string containing a description of the list, can be null if no description is
     *             needed
     */
    public EditList(String desc) {
        this.description = desc;
        this.maxSize = 0;
    }

    /**
     * Creates an Object of this Class
     */
    public EditList() {
        this.description = null;
        this.maxSize = 0;
    }

    // to save the list to file
    public void save(String path) {

    }

    public void save(File path) {

    }

    // to load the list from file
    public void loadList() {

    }

    /**
     * Checks whether the queue is empty or not
     * @return either true (if the queue is empty) or
     * false (if there is at least one file saved)
     * @throws ListNotInitializedException if the queue is not yet initialized
     */
    public boolean isEmpty() throws ListNotInitializedException {
        if (isInitialized()) {
            return savedQueue.size() == 0;
        } else {
            throw new ListNotInitializedException();
        }
    }

    /**
     * Checks whether the queue is already at its maximum capacity of files
     * @return either true (if the list is already full) or
     * false (if there is still room or if there is no maximum size set)
     * @throws ListNotInitializedException if the queue is not yet initialized
     */
    public boolean hasReachedMax() throws ListNotInitializedException {
        if (isInitialized()) {
            return savedQueue.size() == maxSize && maxSize > 0;
        } else {
            throw new ListNotInitializedException();
        }
    }

    /**
     * Checks whether the saved queue is already initialized
     * @return true (if the queue is initialized) or
     * false (if the queue is not initialized)
     */
    private boolean isInitialized() {
        return savedQueue != null;
    }

    /**
     * Adds a File to this queue after checking if it already exists in it.
     * @param f the file that should be added to the queue.
     * @return either true (if the file got added successfully) or
     * false (if the file already existed in the list)
     * @throws ListNotInitializedException if the queue is not yet initialized
     * @throws ListSizeMaxReachedException if a max size for the queue is set and if it is already reached
     */
    public boolean addFile(File f) throws ListNotInitializedException, ListSizeMaxReachedException {
        if (isInitialized()) {
            if (savedQueue.size() == maxSize && maxSize > 0) {
                throw new ListSizeMaxReachedException();
            } else {
                String path = f.getAbsolutePath();
                if (!savedQueue.contains(path)) {
                    savedQueue.add(path);
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            throw new ListNotInitializedException();
        }
    }

    /**
     * Adds a path to this queue after checking if it already exists in it.
     * @param path the path of the file that should be added to the queue.
     * @return either true (if the file got added successfully) or
     * false (if the file already existed in the list)
     * @throws ListNotInitializedException if the queue is not yet initialized
     * @throws ListSizeMaxReachedException if a max size for the queue is set and if it is already reached
     */
    public boolean addFile(String path) throws ListNotInitializedException, ListSizeMaxReachedException {
        if (isInitialized()) {
            if (savedQueue.size() == maxSize && maxSize > 0) {
                throw new ListSizeMaxReachedException();
            } else {
                if (!savedQueue.contains(path)) {
                    savedQueue.add(path);
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            throw new ListNotInitializedException();
        }
    }

    /**
     * Used to access the current File in the queue
     * @return the File on the current position in queue,
     * returns null if the queue is not initialized
     * @throws ListNotInitializedException if the queue is not yet initialized
     */
    public File getCurrent() throws ListNotInitializedException, NoFileInListException {
        if (isInitialized()) {
            if (savedQueue.size() > 0) {
                return new File(savedQueue.get(position));
            } else {
                throw new NoFileInListException();
            }
        } else {
            throw new ListNotInitializedException();
        }
    }

    /**
     * Moves the pointer to the next file in the queue (if it exists) and returns it.
     * @return the next file or null if the list does not contain any more files or if
     * it is not yet initialized
     * @throws ListNotInitializedException if the queue is not yet initialized
     */
    public File next() throws ListNotInitializedException {
        if (hasNext()) {
            position += 1;
            return new File(savedQueue.get(position));
        } else {
            return null;
        }
    }

    /**
     * Moves the pointer to the previous file in the queue (if it exists) and returns it.
     * @return the previous file or null if the list does not contain any more files or if
     * it is not yet intialized
     * @throws ListNotInitializedException if the queue is not yet initialized
     */
    public File previous() throws ListNotInitializedException {
        if (hasPrevious()) {
            position -= 1;
            return new File(savedQueue.get(position));
        } else {
            return null;
        }
    }

    /**
     * Checks whether the current element in the queue has a following element
     * @return either true (if there is such an element)
     * or false (if there is no such element)
     * @throws ListNotInitializedException if the queue is not yet initialized
     */
    public boolean hasNext() throws ListNotInitializedException {
        if (isInitialized()) {
            return savedQueue.size() > 1 && position != savedQueue.size() - 1;
        } else {
            throw new ListNotInitializedException();
        }
    }

    /**
     * Checks whether the current element in the queue has a previous element
     * @return either true (if there is such an element)
     * or false (if there is no such element)
     * @throws ListNotInitializedException if the queue is not yet initialized
     */
    public boolean hasPrevious() throws ListNotInitializedException {
        if (isInitialized()) {
            return savedQueue.size() > 1 && position != 0;
        } else {
            throw new ListNotInitializedException();
        }
    }

    // TODO add throw for ListNotInitializedException
    public ArrayList<String> getQueue() throws ListNotInitializedException {
        if (isInitialized()) {
            return savedQueue;
        } else {
            throw new ListNotInitializedException();
        }
    }

    // TODO add throw for ListNotInitializedException
    public int getPosition() throws ListNotInitializedException {
        if (isInitialized()) {
            return position;
        } else {
            throw new ListNotInitializedException();
        }
    }

    public Date getChangedDate() {
        return changedDate;
    }

    private void setChangedDate(Date newDate) {
        this.changedDate = newDate;
    }

    /**
     * Retrieves the description of the queue
     * @return either the description set or "no description" if no description is set
     */
    public String getDescription() {
        return description == null ? "no description" : description;
    }

    /**
     * Checks if all files that are saved in the queue are still present
     * @return either true (if all files are still there)
     * or false (if at least 1 file is missing)
     * @throws ListNotInitializedException if the queue is not yet initialized
     */
    public boolean checkList() throws ListNotInitializedException {
        if (isInitialized()) {
            for (String s : savedQueue) {
                File tmp = new File(s);
                if (!tmp.exists()) {
                    return false;
                }
            }

            return true;
        } else {
            throw new ListNotInitializedException();
        }
    }
}
