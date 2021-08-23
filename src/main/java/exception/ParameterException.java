package exception;

/**
 * @descriptions: 参数异常
 * @author: zhangfaquan
 * @date: 2021/7/15 19:44
 * @version: 1.0
 */
public class ParameterException extends RuntimeException {

    public ParameterException(String message) {
        super(message);
    }

    public ParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParameterException(Throwable cause) {
        super(cause);
    }
}
