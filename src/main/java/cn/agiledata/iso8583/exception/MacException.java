package cn.agiledata.iso8583.exception;

/**
 * MAC计算异常
 * 
 * @author zln
 *
 */
public class MacException extends Exception {

	private static final long serialVersionUID = 4082762313908160428L;

	public MacException() {
		super();
	}

	public MacException(String message, Throwable cause) {
		super(message, cause);
	}

	public MacException(String message) {
		super(message);
	}

	public MacException(Throwable cause) {
		super(cause);
	}
}
