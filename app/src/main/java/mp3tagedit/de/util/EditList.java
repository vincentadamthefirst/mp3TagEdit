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

    public EditList(String name) {

    }

    // to save the list to file
    public void save(String path) {

    }

    // to load the list from file
    public void loadList() {

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
     *
     * @return
     * @throws ListNotInitializedException
     */
    public boolean hasNext() throws ListNotInitializedException {
        if (isInitialized()) {
            if (savedQueue.size() <= 1) {
                return false;
            } else {
                return position != savedQueue.size()-1;
            }
        } else {
            throw new ListNotInitializedException();
        }
    }

    /**
     *
     * @return
     * @throws ListNotInitializedException
     */
    public boolean hasPrevious() throws ListNotInitializedException {
        if (isInitialized()) {
            if (savedQueue.size() <= 1) {
                return false;
            } else {
                return position != 0;
            }
        } else {
            throw new ListNotInitializedException();
        }
    }

    // TODO add throw for ListNotInitializedException
    public ArrayList<String> getQueue() {
        return savedQueue;
    }

    // TODO add throw for ListNotInitializedException
    public int getPosition() {
        return position;
    }

    public Date getChangedDate() {
        return null;
    }

    public void setChangedDate(Date newDate) {

    }

    public String getDescription() {
        return null;
    }

    // TODO method to check if all files in the list still exist
    public boolean checkList() {
        return false;
    }
}
