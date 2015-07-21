package cn.agiledata.iso8583.entity;

import cn.agiledata.iso8583.MessageFactory;

/**
 * Pin Key /Mac Key请求交易请求类（河北一卡通特有交易）
 * 
 * @author zln
 *
 */
public class GetKeyRequest extends AbstractRequestMsg {
	
	private static final long serialVersionUID = 8312660131185612480L;

	/*
	 * 受卡方系统跟踪号
	 */
	private String traceNo;
	
	/*
	 * 终端号
	 */
	private String terminalNo;
	
	/*
	 * 终端序列号
	 */
	private String terminalSn;
	
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
	
	/*
	 * 交易时间
	 */
	private String transTime;
	
	/*
	 * 流水号
	 */
	private String seqNo;
	
	/*
	 * 保留域 60
	 */
	private String reserved60;
	
	/*
	 * 保留域 63
	 */
	private String reserved63;
	
	private String mac;
	
	public GetKeyRequest() {
		code = MessageFactory.TRANS_CODE_GETKEY;	// 交易编码
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

	public String getTerminalSn() {
		return terminalSn;
	}

	public void setTerminalSn(String terminalSn) {
		this.terminalSn = terminalSn;
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

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public String getReserved60() {
		return reserved60;
	}

	public void setReserved60(String reserved60) {
		this.reserved60 = reserved60;
	}

	public String getReserved63() {
		return reserved63;
	}

	public void setReserved63(String reserved63) {
		this.reserved63 = reserved63;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
}
