package cn.agiledata.iso8583.entity;

import cn.agiledata.iso8583.MessageFactory;

/**
 * 签到请求类
 * 
 * @author zln
 *
 */
public class SignRequest extends AbstractRequestMsg {
	
	private static final long serialVersionUID = 8449930205426671213L;
	
	/*
	 * 受卡方系统跟踪号
	 */
	private String traceNo;
	
	/*
	 * 终端号
	 */
	private String terminalNo;
	
	/*
	 * 商户号
	 */
	private String merNo;
	
	/*
	 * 交易类型码
	 */
	private String transType;
	
	/*
	 * 批次号
	 */
	private String batchNo;
	
	/*
	 * 加密类型
	 */
	private String desType;
	
	/*
	 * 操作员
	 */
	private String operator;
	
	public SignRequest() {
		code = MessageFactory.TRANS_CODE_SIGN;	// 交易编码
	}

	public String getTraceNo() {
		return traceNo;
	}

	public void setTraceNo(String traceNo) {
		this.traceNo = traceNo;
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

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getDesType() {
		return desType;
	}

	public void setDesType(String desType) {
		this.desType = desType;
	}
	
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * 获取60域值
	 * 60.1	交易类型码
	 * 60.2	批次号
	 * 60.3	网络管理信息码
	 * 
	 * @return
	 */
	public String getReserved60() {
		return transType + batchNo + desType;
	}
	
	/**
	 * 获取63域值
	 * 63.1	操作员代码
	 * 
	 * @return
	 */
	public String getReserved63() {
		return this.operator;
	}
}
