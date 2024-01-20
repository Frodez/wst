package info.wst.utils;

public class CommonException extends RuntimeException {

    public CommonException(String message) {
        super(message, null, true, false);
    }

}
