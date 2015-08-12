package cn.agiledata.iso8583;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;

import cn.agiledata.iso8583.entity.SignRequest;
import cn.agiledata.iso8583.entity.SignResponse;
import cn.agiledata.iso8583.util.ISO8583Socket;
import cn.agiledata.iso8583.util.ISO8583Util;

/**
 * 沃支付
 * 
 * @author zln
 *
 */
public class TestWoepay extends TestBase {

	@Test
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
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
