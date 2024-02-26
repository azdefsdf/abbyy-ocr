package exception;


@SuppressWarnings("serial")
public class ImageProcessingException extends RuntimeException {

    public ImageProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
