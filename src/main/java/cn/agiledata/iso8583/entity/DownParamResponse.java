package cn.agiledata.iso8583.entity;

import cn.agiledata.iso8583.MessageFactory;

/**
 * 下载参数反馈
 * 
 * @author zln
 *
 */
public class DownParamResponse extends AbstractResponseMsg {

	
	private static final long serialVersionUID = -5554992663497367693L;
	
	/*
	 * 自定义域60
	 */
	private String reserved60;
	
	/*
	 * 自定义域62
	 */
	private String reserved63;
	
	/*
	 * 河北一卡通参数反馈域
	 */
	private byte[] paramList;

	public DownParamResponse() {
		this.code = MessageFactory.TRANS_CODE_DOWN_PARAM;	// 下载参数
	}

	@Override
	public void process() {
		
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

	public byte[] getParamList() {
		return paramList;
	}

	public void setParamList(byte[] paramList) {
		this.paramList = paramList;
	}
}
