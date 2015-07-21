package cn.agiledata.iso8583;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import cn.agiledata.iso8583.entity.GetKeyRequest;
import cn.agiledata.iso8583.entity.GetKeyResponse;
import cn.agiledata.iso8583.entity.SignRequest;
import cn.agiledata.iso8583.entity.SignResponse;
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

	@Test
	// 签到
	public void signIn() throws Exception{
		try {
			
			
			SignRequest objSignReq = new SignRequest();
			
			String terminalTraceNo = String.valueOf(new Date().getTime());
			terminalTraceNo = terminalTraceNo.substring(0,terminalTraceNo.length()-3);
			// FIXME 读取终端号为8位
			objSignReq.setTerminalNo("001003951");
			objSignReq.setTraceNo("0");
			objSignReq.setSeqNo(terminalTraceNo);
			objSignReq.setTransTime(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));	// 交易时间
			objSignReq.setOperator("000001");
			objSignReq.setTerminalSn("0100000000003951");	// PASMid
			objSignReq.setMac("0");
			
			
//			objSignReq.setReserved63("000001"+"00000000000000000000000000" + "000000000" + "000000" + "0" + "00000000000000000000000000"
//			+ "0000000000000000");
			
			
			// 发送签到请求
			SignResponse objSignResp;
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, objSignReq.getCode(),null);
			message.fillBodyData(objSignReq);
			message.pack();
			
			byte[] mac = message.getMacPlain();
			log.info(ISO8583Util.bytesToHexString(mac));
			
			
			String strMac = MACUtil.getX919Mac("B73622783C5A48D4", ISO8583Util.bytesToHexString(mac), MACUtil.FILL_0X80);
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
			// FIXME 读取终端号为8位
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
			
			
			String strMac = MACUtil.getX919Mac("D221323982526330", ISO8583Util.bytesToHexString(mac), MACUtil.FILL_0X80);
			message.setMac(strMac);
			
			byte[] request = message.getMessage();
			log.info(ISO8583Util.printBytes(request));
			
			
			/*ISO8583Socket socket = new ISO8583Socket();
			socket.connect("110.249.212.155", 12306,50000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_HBCC, objKeyReq.getCode(),null);
			msgResponse.setResponse(response);
			msgResponse.unpack();
			
			objKeyResp = new GetKeyResponse();
			msgResponse.getResponseData(objKeyResp);
			objKeyResp.process();*/
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
}
