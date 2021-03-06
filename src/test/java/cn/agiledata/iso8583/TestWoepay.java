package cn.agiledata.iso8583;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import cn.agiledata.iso8583.entity.CancelRequest;
import cn.agiledata.iso8583.entity.CancelResponse;
import cn.agiledata.iso8583.entity.ConsumeRequest;
import cn.agiledata.iso8583.entity.ConsumeResponse;
import cn.agiledata.iso8583.entity.RefundRequest;
import cn.agiledata.iso8583.entity.ReverseConsumeRequest;
import cn.agiledata.iso8583.entity.ReverseConsumeResponse;
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
	private static final String MAK = "34850E52A8D0D629C8942CCBEAECD049";
	
	
	private static final String terminalSn="A001020150831101";//终端序列号
	private static final String terminalNo="00068855";//终端号
	private static final String merNo="301800710005746";//商户号
	
	
	private String encyptMacKey="";
	
	/**
	 * 批次号用签单时60域返回的批次号，在撤销和退货时需要匹配上
	 */
	private String batchNo="000001";
	
	

	@Test
	public void doubleDesDecrypt(){
		try {
			String macKey=DesUtil.doubleDesDecrypt(mainKey, "F4C15EB5019B5DFB499DA07B864FB903 ");
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
			//objSignReq.setBatchNo(arrTransNo[0]);	// 批次号
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
			
			System.out.println("pik===============>>"+objSignResp.getPinKey());
			System.out.println("mak===============>>"+objSignResp.getMacKey());
			encyptMacKey=objSignResp.getMacKey();
			
			//获取批次号
			String respBatchNo=objSignResp.getReserved60().substring(43,49);
			System.out.println("batchNo============================>>"+respBatchNo);
			
			
			log.info("descryptMacKey========================>>>>>>>>"+getDescryptMacKey(objSignResp.getMacKey()));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
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
			//signIn();
			//String MAK=getDescryptMacKey(encyptMacKey);
			
			ConsumeRequest objConsume = new ConsumeRequest();
			objConsume.setPrimaryAcctNo("00");	// 卡号
			objConsume.setAmount(new BigDecimal("0.01"));	// 金额
			String[] arrSeqNo = getBatchAndSeqNo(null);
			objConsume.setBatchNo(batchNo);	// 批次号
			String traceNo=arrSeqNo[1];
			objConsume.setTraceNo(traceNo);		// 交易流水号
			Date transDate = new Date();
			String localDate=DateFormatUtils.format(transDate, "yyyyMMdd");
			String localTime=DateFormatUtils.format(transDate, "HHmmss");
			objConsume.setLocalDate(localDate);   //受卡方所在地日期
			objConsume.setLocalTime(localTime); //受卡方所在地时间 
			log.info("batchNo:"+batchNo+" traceNo:"+traceNo+" localDate:"+localDate+" localTime:"+localTime);
			objConsume.setTransType("01"); //交易方式   01.条形码支付  02.NFC刷卡支付
			objConsume.setBarCode("32333435363738454142");
			objConsume.setTerminalSn(terminalSn); //终端序列号
			objConsume.setTerminalNo(terminalNo);	// 终端号
			objConsume.setMerNo(merNo);		// 商户号
			String pinData=MD5.sign("111111", "", "UTF-8");
			objConsume.setWoePinData(pinData);//扫码支付密码
			objConsume.setSecurityInfo("2600000000000000");
			objConsume.setMac("0");

			
			// 发送签到请求
			ConsumeResponse objConsumeResp;
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_WOEPAY, objConsume.getCode(),null);
			message.fillBodyData(objConsume);
			message.pack();
			
			byte[] mac = message.getMacPlain();
			log.info(ISO8583Util.bytesToHexString(mac));
			
			String strMac = MACUtil.getCupEcbMac(MAK, ISO8583Util.bytesToHexString(mac));
			
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
			
			log.info(objConsumeResp.getRespCode()+" "+objConsumeResp.getRespMsg()+" "+objConsumeResp.getAdditionalData());
			
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
		String orgDate="20151104";
		String orgTime="144350";
		String orgBatchNo="000001";
		String orgTraceNo="619430";
		
		try {
			ReverseConsumeRequest objReverse = new ReverseConsumeRequest();
			objReverse.setPrimaryAcctNo("00");	// 卡号
			objReverse.setAmount(new BigDecimal("0.01"));	// 金额
			String[] arrSeqNo = getBatchAndSeqNo(null);
			objReverse.setTraceNo(arrSeqNo[1]);		// 交易流水号
			Date transDate = new Date();
			objReverse.setLocalDate(DateFormatUtils.format(transDate, "yyyyMMdd"));   //受卡方所在地日期
			objReverse.setLocalTime(DateFormatUtils.format(transDate, "HHmmss")); //受卡方所在地时间 
			objReverse.setTransType("01"); //交易方式   01.条形码支付  02.NFC刷卡支付
			objReverse.setTerminalSn(terminalSn); //终端序列号
			objReverse.setTerminalNo(terminalNo);	// 终端号
			objReverse.setMerNo(merNo);		// 商户号
			//原交易日期
			objReverse.setOriginalDate(orgDate);
			//原交易时间
			objReverse.setOriginalTime(orgTime);
			//原批次号
			objReverse.setOrgBatchNo(orgBatchNo);
			//原交易流水号
			objReverse.setOrgTraceNo(orgTraceNo);
			objReverse.setMac("0");
			
			// 发送消费冲正请求
			
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_WOEPAY, objReverse.getCode(),null);
			message.fillBodyData(objReverse);
			message.pack();
			
			byte[] mac = message.getMacPlain();
			log.info(ISO8583Util.bytesToHexString(mac));
			
			
			String strMac = MACUtil.getCupEcbMac(MAK, ISO8583Util.bytesToHexString(mac));
			message.setMac(strMac);
			
			byte[] request = message.getMessage();
			log.info(ISO8583Util.printBytes(request));
			
			
			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("123.125.97.242",8785,5000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_WOEPAY, objReverse.getCode(),null);
			msgResponse.setResponse(response);
			msgResponse.unpack();
			
			ReverseConsumeResponse objReverseResp = new ReverseConsumeResponse();
			msgResponse.getResponseData(objReverseResp);
			objReverseResp.process();
			
			log.info(objReverseResp.getRespCode()+" "+objReverseResp.getRespMsg()+" "+objReverseResp.getAdditionalData());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	@Test
	/**
	 * 消费撤销测试
	 * @throws Exception
	 */
	public void testCancel() throws Exception {
		String orgDate="20151119";
		String orgTime="140837";
		String orgBatchNo="000001";
		String orgTraceNo="913317";
		
		try {
			CancelRequest objCancel = new CancelRequest();
			objCancel.setPrimaryAcctNo("00");	// 卡号
			objCancel.setAmount(new BigDecimal("0.01"));	// 金额
			String[] arrSeqNo = getBatchAndSeqNo(null);
			
			objCancel.setBatchNo(batchNo);
			objCancel.setTraceNo(arrSeqNo[1]);		// 交易流水号
			Date transDate = new Date();
			objCancel.setLocalDate(DateFormatUtils.format(transDate, "yyyyMMdd"));   //受卡方所在地日期
			objCancel.setLocalTime(DateFormatUtils.format(transDate, "HHmmss")); //受卡方所在地时间 
			objCancel.setTransType("01"); //交易方式   01.条形码支付  02.NFC刷卡支付
			objCancel.setTerminalSn(terminalSn); //终端序列号
			objCancel.setTerminalNo(terminalNo);	// 终端号
			objCancel.setMerNo(merNo);		// 商户号
			//原交易日期
			objCancel.setOriginalDate(orgDate);
			//原交易时间
			objCancel.setOriginalTime(orgTime);
			//原批次号
			objCancel.setOriginalBatchNo(orgBatchNo);
			//原交易流水号
			objCancel.setOriginalTraceNo(orgTraceNo);
			objCancel.setMac("0");
			
			// 发送消费冲正请求
			
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_WOEPAY, objCancel.getCode(),null);
			message.fillBodyData(objCancel);
			message.pack();
			
			byte[] mac = message.getMacPlain();
			log.info(ISO8583Util.bytesToHexString(mac));
			
			
			String strMac = MACUtil.getCupEcbMac(MAK, ISO8583Util.bytesToHexString(mac));
			message.setMac(strMac);
			
			byte[] request = message.getMessage();
			log.info(ISO8583Util.printBytes(request));
			
			
			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("123.125.97.242",8785,5000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_WOEPAY, objCancel.getCode(),null);
			msgResponse.setResponse(response);
			msgResponse.unpack();
			
			CancelResponse objCancelResp = new CancelResponse();
			msgResponse.getResponseData(objCancelResp);
			objCancelResp.process();
			
			log.info(objCancelResp.getRespCode()+" "+objCancelResp.getAdditionalData());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	
	@Test
	/**
	 * 消费退货测试
	 * @throws Exception
	 */
	public void testRefund() throws Exception {
		String orgDate="20151105";
		String orgTime="112608";
		String orgBatchNo="000001";
		String orgTraceNo="693968";
		
		try {
			RefundRequest objRefund = new RefundRequest();
			objRefund.setPrimaryAcctNo("00");	// 卡号
			objRefund.setAmount(new BigDecimal("0.01"));	// 金额
			String[] arrSeqNo = getBatchAndSeqNo(null);
			objRefund.setTraceNo(arrSeqNo[1]);		// 交易流水号
			objRefund.setBatchNo(batchNo);
			Date transDate = new Date();
			objRefund.setLocalDate(DateFormatUtils.format(transDate, "yyyyMMdd"));   //受卡方所在地日期
			objRefund.setLocalTime(DateFormatUtils.format(transDate, "HHmmss")); //受卡方所在地时间 
			objRefund.setTransType("01"); //交易方式   01.条形码支付  02.NFC刷卡支付
			objRefund.setTerminalSn(terminalSn); //终端序列号
			objRefund.setTerminalNo(terminalNo);	// 终端号
			objRefund.setMerNo(merNo);		// 商户号
			//原交易日期
			objRefund.setOriginalDate(orgDate);
			//原交易时间
			objRefund.setOriginalTime(orgTime);
			//原批次号
			objRefund.setOriginalBatchNo(orgBatchNo);
			//原交易流水号
			objRefund.setOriginalTraceNo(orgTraceNo);
			objRefund.setMac("0");
			
			// 发送消费冲正请求
			
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_WOEPAY, objRefund.getCode(),null);
			message.fillBodyData(objRefund);
			message.pack();
			
			byte[] mac = message.getMacPlain();
			log.info(ISO8583Util.bytesToHexString(mac));
			
			
			String strMac = MACUtil.getCupEcbMac(MAK, ISO8583Util.bytesToHexString(mac));
			message.setMac(strMac);
			
			byte[] request = message.getMessage();
			log.info(ISO8583Util.printBytes(request));
			
			
			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("123.125.97.242",8785,5000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_WOEPAY, objRefund.getCode(),null);
			msgResponse.setResponse(response);
			msgResponse.unpack();
			
			ReverseConsumeResponse objRefundResp = new ReverseConsumeResponse();
			msgResponse.getResponseData(objRefundResp);
			objRefundResp.process();
			
			log.info(objRefundResp.getRespCode()+" "+objRefundResp.getRespMsg()+" "+objRefundResp.getAdditionalData());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
