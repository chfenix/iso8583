package cn.agiledata.iso8583;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;

import cn.agiledata.iso8583.entity.SignRequest;
import cn.agiledata.iso8583.entity.SignResponse;
import cn.agiledata.iso8583.util.ISO8583Socket;

/**
 * 河北一卡通测试
 * 
 * @author zln
 *
 */
public class TestHBCityCard extends TestBase {

	@Test
	// 签到
	public void signIn() throws Exception{
		try {
			SignRequest objSignReq = new SignRequest();
			
			String[] arrTransNo = getBatchAndSeqNo(null);
			objSignReq.setTraceNo(arrTransNo[0]+arrTransNo[1]);	// 交易号
			objSignReq.setTerminalNo("000" + "123456");
			objSignReq.setTraceNo("0");
			
			objSignReq.setReserved60(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")+"0000000001"+"00000000000000" + "");
			
			
			objSignReq.setReserved63("000001"+"00000000000000000000000000" + "000000000" + "000000" + "0" + "00000000000000000000000000"
			+ "0000000000000000");
			
			
			// 发送签到请求
			SignResponse objSignResp;
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_CUPS, objSignReq.getCode(),null);
			message.fillBodyData(objSignReq);
			message.pack();
			byte[] request = message.getMessage();
			
			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("110.249.212.155", 12306,5000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_CUPS, objSignReq.getCode(),null);
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
}
