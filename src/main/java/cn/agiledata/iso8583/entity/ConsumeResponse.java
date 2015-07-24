package cn.agiledata.iso8583.entity;


/**
 * 消费反馈报文
 * 
 * @author zln
 *
 */
public class ConsumeResponse extends AbstractResponseMsg {

	private static final long serialVersionUID = -9085181254454823804L;
	
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
	private String amount;
	
	/*
	 * 受卡方系统跟踪号
	 */
	private String traceNo;
	
	/*
	 * 受卡方所在地时间
	 */
	private String localTime;
	
	/*
	 * 受卡方所在地日期
	 */
	private String localDate;
	
	/*
	 * 清算日期
	 */
	private String settleDate;
	
	/*
	 * 卡片序列号
	 */
	private String seqNo;
	
	/*
	 * 服务点条件码
	 */
	private String conditionMode;
	
	/*
	 * 受理方标识码
	 */
	private String acqIdNo;
	
	/*
	 * 检索参考号
	 */
	private String refNo;
	
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
	 * 余额
	 */
	private String balance;
	
	/*
	 * 自定义域60
	 */
	private String reserved60;
	
	/*
	 * 自定义域62
	 */
	private String reserved62;
	
	/*
	 * 自定义域63
	 */
	private String reserved63;
	
	/*
	 * MAC
	 */
	private byte[] mac;

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
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTraceNo() {
		return traceNo;
	}

	public void setTraceNo(String traceNo) {
		this.traceNo = traceNo;
	}

	public String getLocalTime() {
		return localTime;
	}

	public void setLocalTime(String localTime) {
		this.localTime = localTime;
	}

	public String getLocalDate() {
		return localDate;
	}

	public void setLocalDate(String localDate) {
		this.localDate = localDate;
	}

	public String getSettleDate() {
		return settleDate;
	}

	public void setSettleDate(String settleDate) {
		this.settleDate = settleDate;
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

	public String getAcqIdNo() {
		return acqIdNo;
	}

	public void setAcqIdNo(String acqIdNo) {
		this.acqIdNo = acqIdNo;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
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

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getReserved60() {
		return reserved60;
	}

	public void setReserved60(String reserved60) {
		this.reserved60 = reserved60;
	}

	public String getReserved62() {
		return reserved62;
	}

	public void setReserved62(String reserved62) {
		this.reserved62 = reserved62;
	}

	public String getReserved63() {
		return reserved63;
	}

	public void setReserved63(String reserved63) {
		this.reserved63 = reserved63;
	}

	public byte[] getMac() {
		return mac;
	}

	public void setMac(byte[] mac) {
		this.mac = mac;
	}

	@Override
	public void process() {
		
	}
}
