package cn.agiledata.iso8583.exception;

public class ConfigErrorException extends Exception {

	private static final long serialVersionUID = -4165755508567753764L;

	public ConfigErrorException() {
		super();
	}

	public ConfigErrorException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ConfigErrorException(String arg0) {
		super(arg0);
	}

	public ConfigErrorException(Throwable arg0) {
		super(arg0);
	}
}
