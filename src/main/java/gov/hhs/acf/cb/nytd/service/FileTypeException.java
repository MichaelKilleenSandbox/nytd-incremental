package gov.hhs.acf.cb.nytd.service;

/**
 * File type exception class extends TransmissionException
 * User: 17628
 */
public class FileTypeException extends TransmissionException {
    
    /**
     * No argument constructor.
     */
    public FileTypeException() {
            super();
    }

    /**
     * Constructor with a message argument.
     */
    public FileTypeException(String message) {
            super(message);
    }

    /**
     * Constructor with a Throwable cause argument.
     */
    public FileTypeException(Throwable cause) {
            super(cause);
    }
}
