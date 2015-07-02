package cn.agiledata.iso8583.exception;

/**
 * 无法查找到配置异常
 * 
 * @author zln
 *
 */
public class ConfigNotFoundException extends Exception {

	private static final long serialVersionUID = 65744697407888168L;

	public ConfigNotFoundException() {
		super();
	}

	public ConfigNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ConfigNotFoundException(String arg0) {
		super(arg0);
	}

	public ConfigNotFoundException(Throwable arg0) {
		super(arg0);
	}
}
