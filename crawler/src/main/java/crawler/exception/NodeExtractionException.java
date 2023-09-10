package crawler.exception;

public class NodeExtractionException extends Exception {
    public NodeExtractionException() {
        super();
    }

    public NodeExtractionException(String message) {
        super(message);
    }

    public NodeExtractionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NodeExtractionException(Throwable cause) {
        super(cause);
    }
}