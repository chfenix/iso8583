package cn.agiledata.iso8583.entity;

import java.io.Serializable;

/**
 * 反馈报文基类
 * 
 * @author zln
 * 
 */
public abstract class AbstractResponseMsg implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * 交易编码
	 */
	protected String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public abstract void process();

}
