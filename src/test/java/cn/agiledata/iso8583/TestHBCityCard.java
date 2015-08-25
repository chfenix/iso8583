package cn.agiledata.iso8583;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import cn.agiledata.iso8583.entity.ConsumeRequest;
import cn.agiledata.iso8583.entity.ConsumeResponse;
import cn.agiledata.iso8583.entity.DownParamRequest;
import cn.agiledata.iso8583.entity.DownParamResponse;
import cn.agiledata.iso8583.entity.GetKeyRequest;
import cn.agiledata.iso8583.entity.GetKeyResponse;
import cn.agiledata.iso8583.entity.RefundRequest;
import cn.agiledata.iso8583.entity.RefundResponse;
import cn.agiledata.iso8583.entity.ReverseConsumeRequest;
import cn.agiledata.iso8583.entity.ReverseConsumeResponse;
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
	private static final String TMK = "EC0E89DC76853BC4";
	private static final String PIK = "46163754D6D5F2D3";
	private static final String MAK = "659A4E5F35FF557D";
	
	@Test
	/**
	 * 整合签到测试
	 * @throws Exception
	 */
	public void singInProcess() {
		try {
			signOut();
			signIn();
//			getKey();
		} catch (Exception e) {
		}
	}

	@Test
	// 签到
	public void signIn() throws Exception{
		try {
			
			
			SignRequest objSignReq = new SignRequest();
			
			String terminalTraceNo = String.valueOf(new Date().getTime());
			terminalTraceNo = terminalTraceNo.substring(0,terminalTraceNo.length()-3);
			objSignReq.setTerminalNo("001003949");
			objSignReq.setTraceNo("0");
			objSignReq.setSeqNo(terminalTraceNo);
			objSignReq.setTransTime(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));	// 交易时间
			objSignReq.setOperator("000001");
			objSignReq.setTerminalSn("0100000000003949");	// PASMid
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
			objSignOutReq.setTerminalNo("001003949");
			objSignOutReq.setTraceNo("0");
			objSignOutReq.setSeqNo(terminalTraceNo);
			objSignOutReq.setTransTime(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));	// 交易时间
			objSignOutReq.setOperator("000001");
			objSignOutReq.setTerminalSn("0100000000003949");	// PASMid
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
	public void testDownParam() throws Exception {
		try {
			/*DownParamRequest paramReq = new DownParamRequest();
			
			paramReq.setTerminalNo("001003951");
			paramReq.setMerNo("75900001");	// 商户号
			
			// 发送签到请求
			
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, paramReq.getCode(),null);
			message.fillBodyData(paramReq);
			message.pack();
			
			byte[] request = message.getMessage();
			log.info(ISO8583Util.printBytes(request));
			
			
			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("110.249.212.155", 12306,50000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());*/
			DownParamResponse paramResp = new DownParamResponse();
			String strTest = "600000006360100000000007102000000002C000129940033030303030303031303033393531303030303735393030303031001100000000080001935931303131312020202020202020202020202020202020202031303034353331455443BFA820202020202020202020202031303034353531C8C8B9BABFA8202020202020202020202031303034383031C6D5CDA8BFA8202020202020202020202031303034383631B9F3B1F6BFA8202020202020202020202031303034393031B6A8D6B5BFA8202020202020202020202031303132393837352020202020202020202020202020202031303032D6D0CAAFD3CDB2E2CAD420202020202020202020";
			byte[] response = ISO8583Util.hexStringToByte(strTest);
			
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, paramResp.getCode(),null);
			msgResponse.setResponse(response);
			msgResponse.unpack();
			
			msgResponse.getResponseData(paramResp);
			paramResp.process();
			
			byte[] respParam = paramResp.getParamList();
			String strCardType = "";
			String strDiscount = null;
			String strFlag = new String(ArrayUtils.subarray(respParam, 0, 1),"GB2312");
			if("Y".equals(strFlag)) {
				// 有参数，继续向下解析
				// 参数反馈示例 Y10111                   1004531ETC卡            1004551热购卡           1004801普通卡           1004861贵宾卡           1004901定值卡           10129875                1002中石油测试          
				for (int i = 0; i < (respParam.length -1)/24; i++) {
					String strParam = new String(ArrayUtils.subarray(respParam, i*24+1,(i+1)*24+1),"GB2312");
					// 解析参数
					String strType = strParam.substring(0,4);
					// 系统只关注1004卡类型 1012消费折扣
					if("1004".equals(strType)) {
						// 卡类型白名单
						// 判断是否为可用标志
						if("1".equals(strParam.substring(6,7))) {
							// 标志位可用，截取卡类型保存
							strCardType += strParam.substring(4,6) + ",";
						}
					}
					
					if("1012".equals(strType)) {
						// 折扣
						strDiscount = strParam.substring(4,8);
					}
				}
			}
			
			System.out.println(strCardType);
			System.out.println(strDiscount);
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
			objConsume.setPrimaryAcctNo("0500530000000090");	// 卡号
			objConsume.setAmount(new BigDecimal("0.01"));	// 金额
			objConsume.setTerminalNo("001003949");
			
			String[] arrSeqNo = getBatchAndSeqNo(null);
			System.out.println("transNo:" + arrSeqNo[0] + "|" + arrSeqNo[1]);
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
			
			
			String strMac = MACUtil.getHBCCEcbMac(MAK, ISO8583Util.bytesToHexString(mac));
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
	/**
	 * 消费冲正测试
	 * @throws Exception
	 */
	public void testConsumeReverse() throws Exception {
		try {
			ReverseConsumeRequest objReverse = new ReverseConsumeRequest();
			
			objReverse.setPrimaryAcctNo("0500759000554459");	// 卡号
			objReverse.setAmount(new BigDecimal("0.01"));	// 金额
			objReverse.setTraceNo("039751");		// 交易流水号
			objReverse.setBatchNo("001440");	// 批次号
			objReverse.setOriginalDate("0820110231");	// 原交易时间
			
			Date transDate = new Date();
			objReverse.setLocalDate(DateFormatUtils.format(transDate, "MMdd"));
			objReverse.setLocalTime(DateFormatUtils.format(transDate, "HHmmss"));
			objReverse.setRespCode("06");
			
			objReverse.setTerminalNo("001003951");	// 终端号
			objReverse.setMerNo("75900001");	// 商户号
			
			// 发送消费冲正请求
			
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, objReverse.getCode(),null);
			message.fillBodyData(objReverse);
			message.pack();
			
			byte[] mac = message.getMacPlain();
			log.info(ISO8583Util.bytesToHexString(mac));
			
			
			String strMac = MACUtil.getHBCCEcbMac(MAK, ISO8583Util.bytesToHexString(mac));
			message.setMac(strMac);
			
			byte[] request = message.getMessage();
			log.info(ISO8583Util.printBytes(request));
			
			
			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("110.249.212.155", 12306,5000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, objReverse.getCode(),null);
			msgResponse.setResponse(response);
			msgResponse.unpack();
			
			ReverseConsumeResponse objReverseResp = new ReverseConsumeResponse();
			msgResponse.getResponseData(objReverseResp);
			objReverseResp.process();
			
			log.info(objReverseResp.getRespMsg());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	/**
	 * 退货测试
	 * @throws Exception
	 */
	public void testRefund() throws Exception {
		try {
			RefundRequest objRefund = new RefundRequest();
			objRefund.setPrimaryAcctNo("0500759000554459");	// 卡号
			objRefund.setAmount(new BigDecimal("0.01"));	// 金额
			
			String[] arrSeqNo = getBatchAndSeqNo(null);
			System.out.println("transNo:" + arrSeqNo[0] + "|" + arrSeqNo[1]);
			objRefund.setTraceNo(arrSeqNo[1]);
			
			
			Date transDate = new Date();
			objRefund.setLocalDate(DateFormatUtils.format(transDate, "MMdd"));
			objRefund.setLocalTime(DateFormatUtils.format(transDate, "HHmmss"));
			
			objRefund.setTerminalNo("001003951");	// 终端号
			objRefund.setMerNo("75900001");	// 商户号
			
			objRefund.setBatchNo(arrSeqNo[0]);	// 批次号
			objRefund.setTerminalSn("0100000000003951");

			objRefund.setRefNo("133817819577");	// 原交易参考号
			objRefund.setOriginalDate("0820133817");	// 原交易时间
			objRefund.setOriginalBatchNo("001440");		// 原批次号
			objRefund.setOriginalTraceNo("049097");		// 原流水号
			objRefund.setMac("0");
			
			// 发送退货请求
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, objRefund.getCode(),null);
			message.fillBodyData(objRefund);
			message.pack();
			
			byte[] mac = message.getMacPlain();
			log.info(ISO8583Util.bytesToHexString(mac));
			
			
			String strMac = MACUtil.getHBCCEcbMac(MAK, ISO8583Util.bytesToHexString(mac));
			message.setMac(strMac);
			
			byte[] request = message.getMessage();
			log.info(ISO8583Util.printBytes(request));
			
			
			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("110.249.212.155", 12306,5000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, objRefund.getCode(),null);
			msgResponse.setResponse(response);
			msgResponse.unpack();
			
			RefundResponse objRefundResp = new RefundResponse();
			msgResponse.getResponseData(objRefundResp);
			objRefundResp.process();
			
			log.info(objRefundResp.getRespMsg());
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
			System.out.println("PIK: Plain:" + DesUtil.desDecrypt(strTMK, strPIK) + " CRYPTION:" + strPIK);
			
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
	
	@Test
	public void test() {
		try {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
