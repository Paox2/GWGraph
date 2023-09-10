package graph.builder.exception;

public class GraphBuilderException extends Exception {
    public GraphBuilderException() {
        super();
    }

    public GraphBuilderException(String message) {
        super(message);
    }

    public GraphBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphBuilderException(Throwable cause) {
        super(cause);
    }
}