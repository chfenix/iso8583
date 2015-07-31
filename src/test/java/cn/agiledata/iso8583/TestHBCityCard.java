package cn.agiledata.iso8583;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import cn.agiledata.iso8583.entity.ConsumeRequest;
import cn.agiledata.iso8583.entity.ConsumeResponse;
import cn.agiledata.iso8583.entity.GetKeyRequest;
import cn.agiledata.iso8583.entity.GetKeyResponse;
import cn.agiledata.iso8583.entity.SignOutRequest;
import cn.agiledata.iso8583.entity.SignOutResponse;
import cn.agiledata.iso8583.entity.SignRequest;
import cn.agiledata.iso8583.entity.SignResponse;
import cn.agiledata.iso8583.exception.DesCryptionException;
import cn.agiledata.iso8583.util.DesUtil;
import cn.agiledata.iso8583.util.ISO8583Socket;
import cn.agiledata.iso8583.util.ISO8583Util;
import cn.agiledata.iso8583.util.MACUtil;

/**
 * 河北一卡通测试
 * 
 * @author zln
 *
 */
public class TestHBCityCard extends TestBase {
	
	private static final Logger log = Logger.getLogger(TestHBCityCard.class);
	
	// 以下三个密钥都为明文使用
	private static final String TMK = "1A3D2ACE8C519702";
	private static final String PIK = "1D1BFD40A0150789";
	private static final String MAK = "B73622783C5A48D4";
	
	@Test
	/**
	 * 整合签到测试
	 * @throws Exception
	 */
	public void singInProcess() {
		try {
			signOut();
			signIn();
			getKey();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Test
	// 签到
	public void signIn() throws Exception{
		try {
			
			
			SignRequest objSignReq = new SignRequest();
			
			String terminalTraceNo = String.valueOf(new Date().getTime());
			terminalTraceNo = terminalTraceNo.substring(0,terminalTraceNo.length()-3);
			objSignReq.setTerminalNo("001003951");
			objSignReq.setTraceNo("0");
			objSignReq.setSeqNo(terminalTraceNo);
			objSignReq.setTransTime(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));	// 交易时间
			objSignReq.setOperator("000001");
			objSignReq.setTerminalSn("0100000000003951");	// PASMid
			objSignReq.setMac("0");
			
			
			// 发送签到请求
			SignResponse objSignResp;
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, objSignReq.getCode(),null);
			message.fillBodyData(objSignReq);
			message.pack();
			
			byte[] mac = message.getMacPlain();
			log.info(ISO8583Util.bytesToHexString(mac));
			
			
			String strMac = MACUtil.getX919Mac(MAK, ISO8583Util.bytesToHexString(mac), MACUtil.FILL_0X80);
			message.setMac(strMac);
			
			byte[] request = message.getMessage();
			log.info(ISO8583Util.printBytes(request));
			
			
			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("110.249.212.155", 12306,50000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, objSignReq.getCode(),null);
			msgResponse.setResponse(response);
			msgResponse.unpack();
			
			objSignResp = new SignResponse();
			msgResponse.getResponseData(objSignResp);
			objSignResp.process();
			
			log.info(objSignResp.getRespMsg());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	// 获取key
	public void getKey() throws Exception {
		try {
			GetKeyRequest objKeyReq = new GetKeyRequest();
			
			String terminalTraceNo = String.valueOf(new Date().getTime());
			terminalTraceNo = terminalTraceNo.substring(0,terminalTraceNo.length()-3);
			objKeyReq.setTerminalNo("001003951");
			objKeyReq.setTraceNo("0");
			objKeyReq.setSeqNo(terminalTraceNo);
			objKeyReq.setTransTime(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));	// 交易时间
			objKeyReq.setOperator("000001");
			objKeyReq.setTerminalSn("0100000000003951");	// PASMid
			objKeyReq.setMac("0");
			
			// 发送签到请求
			GetKeyResponse objKeyResp;
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, objKeyReq.getCode(),null);
			message.fillBodyData(objKeyReq);
			message.pack();
			
			byte[] mac = message.getMacPlain();
			log.info(ISO8583Util.bytesToHexString(mac));
			
			
			String strMac = MACUtil.getX919Mac(MAK, ISO8583Util.bytesToHexString(mac), MACUtil.FILL_0X80);
			message.setMac(strMac);
			
			byte[] request = message.getMessage();
			log.info(ISO8583Util.printBytes(request));
			
			
			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("110.249.212.155", 12306,50000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, objKeyReq.getCode(),null);
			msgResponse.setResponse(response);
			msgResponse.unpack();
			
			objKeyResp = new GetKeyResponse();
			msgResponse.getResponseData(objKeyResp);
			objKeyResp.process();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	/**
	 * 签退
	 */
	public void signOut() throws Exception {
		try {
			SignOutRequest objSignOutReq = new SignOutRequest();
			
			String terminalTraceNo = String.valueOf(new Date().getTime());
			terminalTraceNo = terminalTraceNo.substring(0,terminalTraceNo.length()-3);
			objSignOutReq.setTerminalNo("001003951");
			objSignOutReq.setTraceNo("0");
			objSignOutReq.setSeqNo(terminalTraceNo);
			objSignOutReq.setTransTime(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));	// 交易时间
			objSignOutReq.setOperator("000001");
			objSignOutReq.setTerminalSn("0100000000003951");	// PASMid
			objSignOutReq.setMac("0");
			
			// 发送签到请求
			SignOutResponse objSignOutResp;
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, objSignOutReq.getCode(),null);
			message.fillBodyData(objSignOutReq);
			message.pack();
			
			byte[] mac = message.getMacPlain();
			log.info(ISO8583Util.bytesToHexString(mac));
			
			
			String strMac = MACUtil.getX919Mac(MAK, ISO8583Util.bytesToHexString(mac), MACUtil.FILL_0X80);
			message.setMac(strMac);
			
			byte[] request = message.getMessage();
			log.info(ISO8583Util.printBytes(request));
			
			
			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("110.249.212.155", 12306,50000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, objSignOutReq.getCode(),null);
			msgResponse.setResponse(response);
			msgResponse.unpack();
			
			objSignOutResp = new SignOutResponse();
			msgResponse.getResponseData(objSignOutResp);
			objSignOutResp.process();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	/**
	 * 消费测试
	 */
	public void testConsume() throws Exception {
		try {
			ConsumeRequest objConsume = new ConsumeRequest();
			objConsume.setPrimaryAcctNo("0500759000554459");	// 卡号
			objConsume.setAmount(new BigDecimal("0.01"));	// 金额
			objConsume.setTerminalNo("001003951");
			
			String[] arrSeqNo = getBatchAndSeqNo(null);
			objConsume.setTraceNo(arrSeqNo[1]);		// 交易流水号
			
			objConsume.setMerNo("75900001");	// 商户号
			objConsume.setPinData(DesUtil.desEncrypt(PIK, "06123456FFFFFFFF"));	// PIN
			
			
			
			objConsume.setBatchNo(arrSeqNo[0]);
			objConsume.setRandomCode("0000000000000000");
			objConsume.setVerifyCode("0000000000000000");
			objConsume.setMac("0");
			
			Date transDate = new Date();
			objConsume.setLocalDate(DateFormatUtils.format(transDate, "MMdd"));
			objConsume.setLocalTime(DateFormatUtils.format(transDate, "HHmmss"));
			
			// 发送签到请求
			ConsumeResponse objConsumeResp;
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, objConsume.getCode(),null);
			message.fillBodyData(objConsume);
			message.pack();
			
			byte[] mac = message.getMacPlain();
			log.info(ISO8583Util.bytesToHexString(mac));
			
			
			String strMac = MACUtil.getX919Mac(MAK, ISO8583Util.bytesToHexString(mac), MACUtil.FILL_0X80);
			message.setMac(strMac);
			
			byte[] request = message.getMessage();
			log.info(ISO8583Util.printBytes(request));
			
			
			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("110.249.212.155", 12306,5000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, objConsume.getCode(),null);
			msgResponse.setResponse(response);
			msgResponse.unpack();
			
			objConsumeResp = new ConsumeResponse();
			msgResponse.getResponseData(objConsumeResp);
			objConsumeResp.process();
			
			log.info(objConsumeResp.getRespMsg());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void testSplitKey() {
		try {
			String reserved63 = "00000169895049C8DEE6FE1D1BFD40A0150789A7447E5CF00C94090B077C0957CF13D301000000000039510000010039510000";
			String strOldTMK = "1A3D2ACE8C519702";
			
			
			System.out.println(reserved63);
			System.out.println("message tmk:" + reserved63.substring(6,22) + " pik:" + reserved63.substring(22,38) + " mak:" + reserved63.substring(38,70));
			
			String strTMK = reserved63.substring(6,22);
			String strPIK = reserved63.substring(22,38);
			String strMAK = reserved63.substring(38,70);
			
			// 解密TMK
			strTMK = DesUtil.desDecrypt(strOldTMK, strTMK);
			System.out.println("TMK:" + strTMK);
			
			// 解密pik
			System.out.println("PIK: Plain:" + strPIK + " CRYPTION:" + DesUtil.desDecrypt(strTMK, strPIK));
			
			// 解密mak
			String strMakLeft = strMAK.substring(0,16);
			String strMakRight = strMAK.substring(16);
			// 解密
			strMakLeft = DesUtil.desDecrypt(strTMK, strMakLeft);
			strMakRight = DesUtil.desDecrypt(strTMK, strMakRight);
			// 异或
			byte[] byteMakLeft = ISO8583Util.hexStringToByte(strMakLeft);
			byte[] byteMakRight = ISO8583Util.hexStringToByte(strMakRight);
			
			byte[] xorResult=new byte[8];
			for (int j = 0; j < 8; j++) {
				xorResult[j] = (byte) (byteMakLeft[j] ^ byteMakRight[j]);
			}
			System.out.println("MAK: Plain:" + ISO8583Util.bytesToHexString(xorResult) + " CRYPTION:" + DesUtil.desEncrypt(strTMK, ISO8583Util.bytesToHexString(xorResult)));
		} catch (DesCryptionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
