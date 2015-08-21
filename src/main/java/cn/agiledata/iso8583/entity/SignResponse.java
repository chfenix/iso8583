package cn.agiledata.iso8583.entity;

import org.apache.commons.lang3.StringUtils;

import cn.agiledata.iso8583.ISO8583Constants;
import cn.agiledata.iso8583.MessageFactory;
import cn.agiledata.iso8583.util.ISO8583Util;

/**
 * 签到反馈类
 * 
 * @author zln
 *
 */
public class SignResponse extends AbstractResponseMsg {

	private static final long serialVersionUID = -157265294014942248L;

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
	 * 自定义域62
	 */
	private byte[] reserved62;
	
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
	 * Track Data Key
	 */
	private String trackKey;
	
	/*
	 * 沃支付签到工作密钥返回域
	 */
	private byte[] woepayWk;
	
	
	public SignResponse() {
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

	public byte[] getReserved62() {
		return reserved62;
	}

	public void setReserved62(byte[] reserved62) {
		this.reserved62 = reserved62;
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
	

	public byte[] getWoepayWk() {
		return woepayWk;
	}

	public void setWoepayWk(byte[] woepayWk) {
		this.woepayWk = woepayWk;
	}

	@Override
	public void process() {
		
		// 解析60域
		/* ************************
		 * 60.1	交易类型码
		 * 60.2	批次号
		 * 60.3	网络管理信息码
		 */
		if(StringUtils.isNotBlank(reserved60) && reserved60.length() == 11) {
			this.transType = reserved60.substring(0,2);
			this.batchNo = reserved60.substring(2,8);
			this.desType = reserved60.substring(8,11);
		}
		
		// 解析62域
		/* *********************************
		 * 终端可获取设备信息且在请求报文中出现，使用用法十九；
		 * 应答报文按照下列要求填写：
		 * 当39域为“00”时必选，
		 * 当60.3域填写001时包含PIK、MAK，共24字节；
		 * 当60.3域填写003时包含PIK、MAK，共40字节；
		 * 当60.3域填写004时包含PIK、MAK和TDK，共60字节
		 */
		
		if(reserved62 != null && StringUtils.isNotBlank(this.desType)) {
			byte[] bytePIK = null;
			byte[] byteMAK = null;
			byte[] byteTDK = null;
			if(this.desType.equals(ISO8583Constants.DES_TYPE_DES)) {
				// 001 单倍长密钥算法
				/*
				 * 对于单倍长密钥算法，前12个字节为PIN的工作密钥的密文，后12个字节为MAC的工作密钥的密文。
				 * （其中，前8个字节是密文，后4个字节是checkvalue；前8个字节解出明文后，对8个数值0做单倍长密钥算法，
				 * 取结果的前四位与checkvalue 的值比较应该是一致的）。
				 */
				// PIK
				bytePIK = new byte[8];
				System.arraycopy(this.reserved62, 0, bytePIK, 0, 8);
				
				// MAK
				byteMAK = new byte[8];
				System.arraycopy(this.reserved62, 12, byteMAK, 0, 8);
			}
			
			if(this.desType.equals(ISO8583Constants.DES_TYPE_3DES)) {
				// 003 双倍长密钥算法
				/*
				 * 对于双倍长密钥算法，前20个字节为PIN的工作密钥的密文，
				 * 后20个字节为MAC的工作密钥的密文。（其中，“PIN工作密钥”前16个字节是密文，
				 * 后4个字节是checkvalue；前16个字节解出明文后，对8个数值0做双倍长密钥算法，
				 * 取结果的前四位与checkvalue 的值比较应该是一致的；
				 * “MAC工作密钥”前8个字节是密文，
				 * 再8个字节是二进制零，后4个字节是checkvalue；前8个字节解出明文后，
				 * 对8个数值0做单倍长密钥算法，取结果的前四位与checkvalue 的值比较应该是一致的）。
				 */
				// PIK
				bytePIK = new byte[16];
				System.arraycopy(this.reserved62, 0, bytePIK, 0, 16);
				
				// MAK
				byteMAK = new byte[8];
				System.arraycopy(this.reserved62, 20, byteMAK, 0, 8);
			}
			
			if(this.desType.equals(ISO8583Constants.DES_TYPE_3DES_TRACK)) {
				// 003 双倍长密钥算法（含磁道密钥）
				/*
				 * 对于支持磁道加密的签到报文本域长度为60字节，采用双倍长密钥算法，
				 * 前20个字节为PIN的工作密钥的密文，中间20个字节为MAC的工作密钥，
				 * 后面20个字节为TDK的工作密钥的密文。（其中，“PIN工作密钥”
				 * 前16个字节是密文，后4个字节是checkvalue；前16个字节解出明文后，
				 * 对8个数值0做双倍长密钥算法，取结果的前四位与checkvalue 的值比较应该是一致的；
				 * “MAC工作密钥”前8个字节是密文，再8个字节是二进制零，
				 * 后4个字节是checkvalue；前8个字节解出明文后，对8个数值0做单倍长密钥算法，
				 * 取结果的前四位与checkvalue 的值比较应该是一致的；
				 * “TDK工作密钥”前16个字节是密文，
				 * 后4个字节是checkvalue；前16个字节解出明文后，对8个数值0做双倍长密钥算法，
				 * 取结果的前四位与checkvalue 的值比较应该是一致的）。
				 */
				// PIK
				bytePIK = new byte[16];
				System.arraycopy(this.reserved62, 0, bytePIK, 0, 16);
				
				// MAK
				byteMAK = new byte[8];
				System.arraycopy(this.reserved62, 20, byteMAK, 0, 8);
				
				// TDK
				byteTDK = new byte[16];
				System.arraycopy(this.reserved62, 40, byteTDK, 0, 16);
			}
			
			if(bytePIK != null) {
				// PIK转十六进制字符串
				this.pinKey = ISO8583Util.bytesToHexString(bytePIK);
			}
			if(byteMAK != null) {
				// MAK转十六进制字符串
				this.macKey = ISO8583Util.bytesToHexString(byteMAK);
			}
			if(byteTDK != null) {
				// TDK转十六进制字符串
				this.trackKey = ISO8583Util.bytesToHexString(byteTDK);
			}
		}
		
	}
}
