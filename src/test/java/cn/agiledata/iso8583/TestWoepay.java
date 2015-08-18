package cn.agiledata.iso8583;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import cn.agiledata.iso8583.entity.ConsumeRequest;
import cn.agiledata.iso8583.entity.ConsumeResponse;
import cn.agiledata.iso8583.entity.SignRequest;
import cn.agiledata.iso8583.entity.SignResponse;
import cn.agiledata.iso8583.util.DesUtil;
import cn.agiledata.iso8583.util.ISO8583Socket;
import cn.agiledata.iso8583.util.ISO8583Util;
import cn.agiledata.iso8583.util.MACUtil;

/**
 * 沃支付
 * 
 * @author zln
 *
 */
public class TestWoepay extends TestBase {
	private static final Logger log = Logger.getLogger(TestWoepay.class);
	
	// 以下三个密钥都为明文使用
	private static final String TMK = "1A3D2ACE8C519702";
	private static final String PIK = "1D1BFD40A0150789";
	private static final String MAK = "B73622783C5A48D4";

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
			objSignReq.setTerminalSn("A001020150727001"); //终端序列号
			objSignReq.setTerminalNo("00000001");	// 终端号
			objSignReq.setMerNo("301100110014181");		// 商户号
		
			// 发送签到请求
			SignResponse objSignResp;
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_WOEPAY, objSignReq.getCode(),null);
			message.fillBodyData(objSignReq);
			message.pack();
			byte[] request = message.getMessage();
			System.out.println(ISO8583Util.printBytes(request));
			
			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("123.125.97.253",8785,5000);
			
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
			
			System.out.println("====================>>"+ISO8583Util.printBytes(objSignResp.getReserved63()));
			String respBatchNo=objSignResp.getReserved60().substring(43,49);
			System.out.println("batchNo============================>>"+respBatchNo);
			getPIKandMAK(objSignResp,objSignResp.getReserved63());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
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
	}
	
	@Test
	/**
	 * 消费测试
	 */
	public void testConsume() throws Exception {
		try {
			ConsumeRequest objConsume = new ConsumeRequest();
			objConsume.setPrimaryAcctNo("00000000000000000000");	// 卡号
			objConsume.setAmount(new BigDecimal("0.01"));	// 金额
			objConsume.setMobile("");  //手机号
			String[] arrSeqNo = getBatchAndSeqNo(null);
			objConsume.setBatchNo("000001");	// 批次号
			objConsume.setTraceNo(arrSeqNo[1]);		// 交易流水号
			Date transDate = new Date();
			objConsume.setLocalDate(DateFormatUtils.format(transDate, "yyyyMMdd"));   //受卡方所在地日期
			objConsume.setLocalTime(DateFormatUtils.format(transDate, "HHmmss")); //受卡方所在地时间 
			objConsume.setTransType("02"); //交易方式   01.条形码支付  02.NFC刷卡支付
			objConsume.setTerminalSn("A001020150727001"); //终端序列号
			objConsume.setTerminalNo("00000001");	// 终端号
			objConsume.setMerNo("301100110014181");		// 商户号
			objConsume.setMac("0");
			
			// 发送签到请求
			ConsumeResponse objConsumeResp;
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_WOEPAY, objConsume.getCode(),null);
			message.fillBodyData(objConsume);
			message.pack();
			
			byte[] mac = message.getMacPlain();
			log.info(ISO8583Util.bytesToHexString(mac));
			
			
			String strMac = MACUtil.getX919Mac(MAK, ISO8583Util.bytesToHexString(mac), MACUtil.FILL_0X80);
			message.setMac(strMac);
			
			byte[] request = message.getMessage();
			log.info(ISO8583Util.printBytes(request));
			
			
			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("123.125.97.253",8785,5000);
			
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
