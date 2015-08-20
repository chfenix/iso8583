package cn.agiledata.iso8583.entity;

import java.math.BigDecimal;

import cn.agiledata.iso8583.MessageFactory;

/**
 * 消费冲正请求报文
 * 
 * @author zln
 *
 */
public class ReverseConsumeRequest extends AbstractRequestMsg {

	private static final long serialVersionUID = -6500452916955431281L;
	

	/*
	 * 主账号
	 */
	private String primaryAcctNo;
	
	/*
	 * 交易处理码
	 */
	private String processCode;
	
	/*
	 * 交易金额
	 */
	private BigDecimal amount;
	
	/*
	 * 受卡方系统跟踪号
	 */
	private String traceNo;
	
	/*
	 * 服务点输入方式码
	 */
	private String entryMode;
	
	/*
	 * 卡片序列号
	 */
	private String seqNo;
	
	/*
	 * 服务点条件码
	 */
	private String conditionMode;
	
	/*
	 * 冲正原因
	 */
	private String respCode;
	
	/*
	 * 终端号
	 */
	private String terminalNo;
	
	/*
	 * 商户号
	 */
	private String merNo;
	
	/*
	 * 附加响应数据
	 */
	private String additionalData;
	
	/*
	 * 交易货币代码
	 */
	private String currency;
	
	/*
	 * MAC
	 */
	private String mac;
	
	/*
	 * 交易类型码
	 */
	private String transType;
	
	/*
	 * 批次号
	 */
	private String batchNo;
	
	private String reserved60;
	
	/*
	 * 原交易日期
	 */
	private String originalDate;
	
	/*
	 * 交易日期
	 */
	private String localDate;
	
	/*
	 * 交易时间
	 */
	private String localTime;

	public ReverseConsumeRequest() {
		this.code = MessageFactory.TRANS_CODE_REVERSE_CONSUME;
	}

	public String getPrimaryAcctNo() {
		return primaryAcctNo;
	}

	public void setPrimaryAcctNo(String primaryAcctNo) {
		this.primaryAcctNo = primaryAcctNo;
	}

	public String getProcessCode() {
		return processCode;
	}

	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}

	public String getAmount() {
		// 转换为单位为分的字符串
		if(this.amount != null) {
			return String.valueOf(this.amount.multiply(new BigDecimal(100)).intValue());
		}
		else {
			return null;
		}
	}

	/**
	 * 金额单位为元，两位小数
	 * @param amount
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTraceNo() {
		return traceNo;
	}

	public void setTraceNo(String traceNo) {
		this.traceNo = traceNo;
	}

	public String getEntryMode() {
		return entryMode;
	}

	public void setEntryMode(String entryMode) {
		this.entryMode = entryMode;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public String getConditionMode() {
		return conditionMode;
	}

	public void setConditionMode(String conditionMode) {
		this.conditionMode = conditionMode;
	}
	
	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
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

	public String getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(String additionalData) {
		this.additionalData = additionalData;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
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
	
	public void setReserved60(String reserved60) {
		this.reserved60 = reserved60;
	}
	
	public String getLocalDate() {
		return localDate;
	}

	public void setLocalDate(String localDate) {
		this.localDate = localDate;
	}

	public String getLocalTime() {
		return localTime;
	}

	public void setLocalTime(String localTime) {
		this.localTime = localTime;
	}

	public String getReserved60() {
		return this.reserved60;
	}

	public String getOriginalDate() {
		return originalDate;
	}

	public void setOriginalDate(String originalDate) {
		this.originalDate = originalDate;
	}
	
	
}
