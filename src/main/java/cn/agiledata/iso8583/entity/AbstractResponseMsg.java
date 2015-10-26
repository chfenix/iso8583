package cn.agiledata.iso8583.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * 反馈报文基类
 * 
 * @author zln
 * 
 */
public abstract class AbstractResponseMsg implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final String PREFIX_MSG = "iso8583.respmsg.";
	
	private static final String COMMON_ERROR = "ZZ";

	/*
	 * 交易编码
	 */
	protected String code;
	
	/*
	 * 应答码
	 */
	private String respCode;
	
	/*
	 * 报文规范
	 */
	private String spec;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	
	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	/**
	 * 根据配置文件和返回编码转换异常信息，未能正确转换则返回空
	 * 
	 * @return
	 */
	public String getRespMsg() {
		try {
			if(StringUtils.isNotBlank(this.spec)) {
				// 报文规范不为空，读取配置文件
				Properties P = new Properties();
				P.load(AbstractResponseMsg.class.getResourceAsStream("/config/" + spec +"/respmsg_" + spec + ".properties"));
				if (P.containsKey(PREFIX_MSG + this.respCode)) {
					return "[" + this.respCode + "]" + new String(P.getProperty(PREFIX_MSG + this.respCode).getBytes("UTF-8"),"UTF-8");
				}
				else {
					return "[" + this.respCode + "]" + new String(P.getProperty(PREFIX_MSG + COMMON_ERROR).getBytes("UTF-8"),"UTF-8");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public abstract void process();

}
