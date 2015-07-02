package cn.agiledata.iso8583.entity;

import java.math.BigDecimal;

import cn.agiledata.iso8583.MessageFactory;

/**
 * 退货请求报文
 * 
 * @author zln
 *
 */
public class RefundRequest extends AbstractRequestMsg {
	
	private static final long serialVersionUID = 919598472865390208L;

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
	 * 服务点PIN获取码
	 */
	private String pinCaptureCode;
	
	/*
	 * 2磁道数据
	 */
	private String track2Data;
	
	/*
	 * 3磁道数据
	 */
	private String track3Data;
	
	/*
	 * 请求时为原消费交易参考号
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
	 * PIN
	 */
	private String pinData;
	
	/*
	 * 安全控制信息
	 */
	private String securityInfo;
	
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
	
	/*
	 * 原批次号
	 */
	private String originalBatchNo;
	
	/*
	 * 原流水号
	 */
	private String originalTraceNo;
	
	/*
	 * 原交易日期
	 */
	private String originalDate;
	
	public RefundRequest() {
		this.code = MessageFactory.TRANS_CODE_REFUND;
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

	public String getPinCaptureCode() {
		return pinCaptureCode;
	}

	public void setPinCaptureCode(String pinCaptureCode) {
		this.pinCaptureCode = pinCaptureCode;
	}

	public String getTrack2Data() {
		return track2Data;
	}

	public void setTrack2Data(String track2Data) {
		this.track2Data = track2Data;
	}
	
	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getTrack3Data() {
		return track3Data;
	}

	public void setTrack3Data(String track3Data) {
		this.track3Data = track3Data;
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

	public String getPinData() {
		return pinData;
	}

	public void setPinData(String pinData) {
		this.pinData = pinData;
	}

	public String getSecurityInfo() {
		return securityInfo;
	}

	public void setSecurityInfo(String securityInfo) {
		this.securityInfo = securityInfo;
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

	public String getOriginalBatchNo() {
		return originalBatchNo;
	}

	public void setOriginalBatchNo(String originalBatchNo) {
		this.originalBatchNo = originalBatchNo;
	}

	public String getOriginalTraceNo() {
		return originalTraceNo;
	}

	public void setOriginalTraceNo(String originalTraceNo) {
		this.originalTraceNo = originalTraceNo;
	}
	
	public String getOriginalDate() {
		return originalDate;
	}

	public void setOriginalDate(String originalDate) {
		this.originalDate = originalDate;
	}

	/**
	 * 获取60域值
	 * 60.1	交易类型码
	 * 60.2	批次号
	 * 60.3	网络管理码
	 * 60.4	终端读取能力
	 * @return
	 */
	public String getReserved60() {
		return transType + batchNo + "000";
	}
	
	/**
	 * 获取61域值
	 * 
	 * 61.1	原批号
	 * 61.2	原POS流水号
	 * 
	 * @return
	 */
	public String getReserved61() {
		
		return originalBatchNo + originalTraceNo + originalDate;
	}
}
