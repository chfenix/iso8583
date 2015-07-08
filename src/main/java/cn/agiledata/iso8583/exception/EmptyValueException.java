package cn.agiledata.iso8583.exception;

public class EmptyValueException extends Exception {

	private static final long serialVersionUID = 3030521176275590155L;

	public EmptyValueException() {
		super();
	}

	public EmptyValueException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public EmptyValueException(String arg0) {
		super(arg0);
	}

	public EmptyValueException(Throwable arg0) {
		super(arg0);
	}
	
}
