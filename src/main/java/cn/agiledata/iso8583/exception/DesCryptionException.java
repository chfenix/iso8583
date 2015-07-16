package cn.agiledata.iso8583.exception;

/**
 * DES加密异常
 * 
 * @author zln
 *
 */
public class DesCryptionException extends Exception {

	private static final long serialVersionUID = -206230938968366093L;

	public DesCryptionException() {
		super();
	}

	public DesCryptionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public DesCryptionException(String arg0) {
		super(arg0);
	}

	public DesCryptionException(Throwable arg0) {
		super(arg0);
	}
}
