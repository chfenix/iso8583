package cn.agiledata.iso8583;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import cn.agiledata.iso8583.entity.ConsumeRequest;
import cn.agiledata.iso8583.entity.ConsumeResponse;
import cn.agiledata.iso8583.entity.SignRequest;
import cn.agiledata.iso8583.entity.SignResponse;
import cn.agiledata.iso8583.exception.DesCryptionException;
import cn.agiledata.iso8583.util.DesUtil;
import cn.agiledata.iso8583.util.ISO8583Socket;
import cn.agiledata.iso8583.util.ISO8583Util;
import cn.agiledata.iso8583.util.MACUtil;
import cn.agiledata.iso8583.util.MD5;

/**
 * 沃支付
 * 
 * @author zln
 *
 */
public class TestWoepay extends TestBase {
	private static final Logger log = Logger.getLogger(TestWoepay.class);
	//主秘钥
	private static final String mainKey="3E51F23DF70758B9BF20467A8F543DBF";
	
	// 以下三个密钥都为明文使用
	private static final String PIK = "1D1BFD40A0150789";
	private static final String MAK = "8A1A860B628564521FF73D547C311C13";
	
	
	private static final String terminalSn="A001020150831101";//终端序列号
	private static final String terminalNo="00068855";//终端号
	private static final String merNo="301800710005746";//商户号
	
	
	private String encyptMacKey="";
	
	

	@Test
	public void doubleDesDecrypt(){
		try {
			String macKey=DesUtil.doubleDesDecrypt(mainKey, "CAD6CACC11B22445359719CAAE6E5006");
			String pinKey=DesUtil.doubleDesDecrypt(mainKey, "EB67391C6EDDABD0C4D3A9D604069F0B");
			System.out.println("macKey:"+macKey+"  "+"pinKey:"+pinKey);
	    } catch (DesCryptionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 获得解密的mac key
	 * @param encryptMacKey
	 * @return
	 */
	private String getDescryptMacKey(String encryptMacKey){
		String desMacKey="";
		try {
			desMacKey=DesUtil.doubleDesDecrypt(mainKey, encryptMacKey);
		} catch (DesCryptionException e) {
			e.printStackTrace();
		}
		return desMacKey;
	}
	
	@Test
	/**
	 * 
	 */
	public void queryPublicKey(){
		String terminalSn="A001020150727001";
		String terminalSnHex=ISO8583Util.bytesToHexString(terminalSn.getBytes());
		System.out.println("terminalSnHex==================>>"+terminalSnHex);
		
		String terminalNo="00000001";
		String terminalNoHex=ISO8583Util.bytesToHexString(terminalNo.getBytes());
		System.out.println("terminalNoHex==================>>"+terminalNoHex);
		
		String merNo="301100110014181";
		String merNoHex=ISO8583Util.bytesToHexString(merNo.getBytes());
		System.out.println("merNoHex==================>>"+merNoHex);
		
		String messageHex="FF0105"+"F90100"+"2810"+terminalSnHex+"2908"+terminalNoHex+"2A0F"+merNoHex;
		System.out.println("messageHex=================>>"+messageHex);
		
		ISO8583Socket socket = new ISO8583Socket();
		try {
			socket.connect("123.125.97.253",8786,5000);
			socket.sendRequest(messageHex.getBytes());
			// 获取返回结果
			byte[] response = socket.getResponse();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Test
	/**
	 * 签到测试
	 */
	public void signIn() throws Exception {
		try {
			// 设置签到报文
			SignRequest objSignReq = new SignRequest();
			// 使用秒数生成批次号及流水号
			String[] arrTransNo = getBatchAndSeqNo(null);
			System.out.println("batchNo:" + arrTransNo[0] + " traceNo:" + arrTransNo[1]);
			objSignReq.setBatchNo(arrTransNo[0]);	// 批次号
			objSignReq.setTraceNo(arrTransNo[1]);	// 交易号
			Date localDate = new Date();
			objSignReq.setLocalDate(DateFormatUtils.format(localDate, "yyyyMMdd")); //收单方所在地日期
			objSignReq.setLocalTime(DateFormatUtils.format(localDate, "HHmmss")); //收单方所在地时间
			objSignReq.setTerminalSn(terminalSn); //终端序列号
			objSignReq.setTerminalNo(terminalNo);	// 终端号
			objSignReq.setMerNo(merNo);		// 商户号
		
			// 发送签到请求
			SignResponse objSignResp;
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_WOEPAY, objSignReq.getCode(),null);
			message.fillBodyData(objSignReq);
			message.pack();
			byte[] request = message.getMessage();
			System.out.println(ISO8583Util.printBytes(request));
			
			ISO8583Socket socket = new ISO8583Socket();
			
			System.out.println("ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
			socket.connect("123.125.97.242",8785,5000);
			socket.sendRequest(request);

			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_WOEPAY, objSignReq.getCode(),null);
			msgResponse.setResponse(response);
			msgResponse.unpack();
			
			objSignResp = new SignResponse();
			msgResponse.getResponseData(objSignResp);
			objSignResp.process();
			
			System.out.println(objSignResp.getRespCode() + " "  + objSignResp.getRespMsg());
			
			System.out.println("====================>>"+ISO8583Util.printBytes(objSignResp.getWoepayWk()));
			//获取批次号
			String respBatchNo=objSignResp.getReserved60().substring(43,49);
			System.out.println("batchNo============================>>"+respBatchNo);
			getPIKandMAK(objSignResp,objSignResp.getWoepayWk());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * 获取返回的pik和mak
	 * @param respObj
	 * @param reserved63
	 */
	private void getPIKandMAK(SignResponse respObj,byte[] reserved63){
		//PIK
    	byte[] bytePIK = new byte[16];
		System.arraycopy(reserved63, 9, bytePIK, 0, 16);
		String pik=ISO8583Util.bytesToHexString(bytePIK);
		System.out.println("pik===============>>"+pik);
		
		//MAK
		byte[] byteMAK = new byte[16];
		System.arraycopy(reserved63, 29, byteMAK, 0, 16);
		String mak=ISO8583Util.bytesToHexString(byteMAK);
		System.out.println("mak===============>>"+mak);
		
		respObj.setPinKey(pik);
		respObj.setMacKey(mak);
		//密文mak
		encyptMacKey=mak;
	}
	
	@Test
	public void testMD5(){
		String pinData=MD5.sign("111111", "", "UTF-8");
		System.out.println("==============asdas====>>>"+pinData);
	}
	
	
	
	@Test
	/**
	 * 消费测试
	 */
	public void testConsume() throws Exception {
		try {
			log.info("================================================");
			//签到
			signIn();
			String MAK=getDescryptMacKey(encyptMacKey);
			
			ConsumeRequest objConsume = new ConsumeRequest();
			objConsume.setPrimaryAcctNo("00");	// 卡号
			objConsume.setAmount(new BigDecimal("0.01"));	// 金额
			//objConsume.setMobile("13817563221");  //手机号
			String[] arrSeqNo = getBatchAndSeqNo(null);
			objConsume.setBatchNo("000001");	// 批次号
			objConsume.setTraceNo(arrSeqNo[1]);		// 交易流水号
			Date transDate = new Date();
			objConsume.setLocalDate(DateFormatUtils.format(transDate, "yyyyMMdd"));   //受卡方所在地日期
			objConsume.setLocalTime(DateFormatUtils.format(transDate, "HHmmss")); //受卡方所在地时间 
			objConsume.setTransType("01"); //交易方式   01.条形码支付  02.NFC刷卡支付
			objConsume.setBarCode("31324230373034304331");
			objConsume.setTerminalSn(terminalSn); //终端序列号
			objConsume.setTerminalNo(terminalNo);	// 终端号
			objConsume.setMerNo(merNo);		// 商户号
			String pinData=MD5.sign("111111", "", "UTF-8");
			objConsume.setPinData(pinData);  //扫码支付密码
			objConsume.setSecurityInfo("2600000000000000");
			objConsume.setMac("0");

			
			// 发送签到请求
			ConsumeResponse objConsumeResp;
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_WOEPAY, objConsume.getCode(),null);
			message.fillBodyData(objConsume);
			message.pack();
			
			byte[] mac = message.getMacPlain();
			log.info(ISO8583Util.bytesToHexString(mac));
			
			String strMac = MACUtil.getWoeEcbMac(MAK, ISO8583Util.bytesToHexString(mac));
			
			message.setMac(strMac);
			
			byte[] request = message.getMessage();

			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("123.125.97.242",8785,5000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_WOEPAY, objConsume.getCode(),null);
			msgResponse.setResponse(response);
			msgResponse.unpack();
			
			objConsumeResp = new ConsumeResponse();
			msgResponse.getResponseData(objConsumeResp);
			objConsumeResp.process();
			
			log.info(objConsumeResp.getRespCode()+" "+objConsumeResp.getRespMsg());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
