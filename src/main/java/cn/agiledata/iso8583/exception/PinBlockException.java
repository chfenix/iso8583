package cn.agiledata.iso8583.exception;

/**
 * PINBlock计算异常
 * 
 * @author zln
 *
 */
public class PinBlockException extends Exception {

	private static final long serialVersionUID = -1649360318995910167L;

	public PinBlockException() {
		super();
	}

	public PinBlockException(String message, Throwable cause) {
		super(message, cause);
	}

	public PinBlockException(String message) {
		super(message);
	}

	public PinBlockException(Throwable cause) {
		super(cause);
	}
}
