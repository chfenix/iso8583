package cn.agiledata.iso8583.entity;

import cn.agiledata.iso8583.MessageFactory;

/**
 * Pin Key /Mac Key请求交易反馈类（河北一卡通特有交易）
 * 
 * @author zln
 *
 */
public class GetKeyResponse extends AbstractResponseMsg {

	private static final long serialVersionUID = -6567594524956856744L;

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
	 * 自定义域60
	 */
	private String reserved60;
	
	/*
	 * 自定义域63
	 */
	private String reserved63;
	
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
	 * PIN Key
	 */
	private String pinKey;
	
	/*
	 * MAC Key
	 */
	private String macKey;
	
	/*
	 * Terminal Master Key
	 */
	private String masterKey;
	
	/*
	 * Track Data Key
	 */
	private String trackKey;
	
	
	public GetKeyResponse() {
		this.code = MessageFactory.TRANS_CODE_SIGN;	// 签到
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
	
	public String getPinKey() {
		return pinKey;
	}

	public void setPinKey(String pinKey) {
		this.pinKey = pinKey;
	}

	public String getMacKey() {
		return macKey;
	}

	public void setMacKey(String macKey) {
		this.macKey = macKey;
	}
	
	public String getTrackKey() {
		return trackKey;
	}

	public void setTrackKey(String trackKey) {
		this.trackKey = trackKey;
	}
	
	public String getMasterKey() {
		return masterKey;
	}

	public void setMasterKey(String masterKey) {
		this.masterKey = masterKey;
	}

	@Override
	public void process() {
		
		// 解析返回的三个密钥
		// 截取TMK 
		this.masterKey = this.reserved63.substring(6,22);
		
		// 截取PIK
		this.pinKey = this.reserved63.substring(22,38);
		
		// 截取MAK
		this.macKey = this.reserved63.substring(38,70);
		
	}
}
