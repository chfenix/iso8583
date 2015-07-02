package cn.agiledata.iso8583.exception;

/**
 * 数据非法异常
 * 
 * @author zln
 *
 */
public class IllegalDataException extends Exception {

	private static final long serialVersionUID = 1452072663492989774L;

	public IllegalDataException() {
		super();
	}

	public IllegalDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalDataException(String message) {
		super(message);
	}

	public IllegalDataException(Throwable cause) {
		super(cause);
	}
}
