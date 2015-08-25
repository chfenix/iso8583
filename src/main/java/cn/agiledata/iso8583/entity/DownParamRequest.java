package cn.agiledata.iso8583.entity;

import cn.agiledata.iso8583.MessageFactory;

/**
 * 下载参数请求
 * 
 * @author zln
 * 
 */
public class DownParamRequest extends AbstractRequestMsg {

	private static final long serialVersionUID = -8655350885158691728L;
	
	/*
	 * 终端号
	 */
	private String terminalNo;
	
	/*
	 * 商户号
	 */
	private String merNo;

	public DownParamRequest() {
		code = MessageFactory.TRANS_CODE_DOWN_PARAM; // 下载参数
	}

	public String getTerminalNo() {
		return terminalNo;
	}

	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}

	public String getMerNo() {
		return merNo;
	}

	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}
}
