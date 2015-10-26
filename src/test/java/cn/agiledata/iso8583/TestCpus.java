package cn.agiledata.iso8583;

import org.junit.Test;

import cn.agiledata.iso8583.entity.SignRequest;
import cn.agiledata.iso8583.entity.SignResponse;
import cn.agiledata.iso8583.util.ISO8583Socket;
import cn.agiledata.iso8583.util.ISO8583Util;

/**
 * 银商预付卡
 * 
 * @author zln
 *
 */
public class TestCpus extends TestBase {

	@Test
	public void signIn() throws Exception {
		try {
			// 设置签到报文
			SignRequest objSignReq = new SignRequest();
			// 测试环境
//			objSignReq.setMerNo("123456789111115");		// 商户号
//			objSignReq.setTerminalNo("11115007");	// 终端号
			// 生产环境
			objSignReq.setMerNo("898130155410097");		// 商户号
			objSignReq.setTerminalNo("91832140");	// 终端号
			
			// 使用秒数生成批次号及流水号
			String[] arrTransNo = getBatchAndSeqNo(null); 
			System.out.println("batchNo:" + arrTransNo[0] + " traceNo:" + arrTransNo[1]);
			objSignReq.setBatchNo(arrTransNo[0]);	// 批次号
			objSignReq.setTraceNo(arrTransNo[1]);	// 交易号
			
			// 生产验证
			objSignReq.setBatchNo("000001");
			objSignReq.setTraceNo("000001");
			
			// 设置签到参数
			objSignReq.setTransType("00");		// 交易类型:签到
			objSignReq.setDesType("003");		// 3DES加密
			objSignReq.setOperator("000");		// 操作员
			
			// 发送签到请求
			SignResponse objSignResp;
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_CUPS, objSignReq.getCode(),null);
			message.fillBodyData(objSignReq);
			message.pack();
			byte[] request = message.getMessage();
			System.out.println(ISO8583Util.printBytes(request));
			
			ISO8583Socket socket = new ISO8583Socket();
			// 测试环境
//			socket.connect("116.228.21.162",4008,5000);
			// 生产环境
			socket.connect("11.131.248.41",4101,10000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_CUPS, objSignReq.getCode(),null);
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
