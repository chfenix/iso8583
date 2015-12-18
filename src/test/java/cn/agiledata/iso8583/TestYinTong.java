package cn.agiledata.iso8583;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import cn.agiledata.iso8583.entity.SignRequest;
import cn.agiledata.iso8583.entity.SignResponse;
import cn.agiledata.iso8583.util.DesUtil;
import cn.agiledata.iso8583.util.ISO8583Socket;
import cn.agiledata.iso8583.util.ISO8583Util;

/**
 * 银通卡测试
 * 
 * @author zln
 *
 */
public class TestYinTong extends TestBase {
	
	private static final Logger log = Logger.getLogger(TestYinTong.class);
	
	// 以下三个密钥都为明文使用
	private static final String TMK = "14EB9AF6AF7D6030";
	private static String PIK = null;
	private static String MAK = null;
	

	@Test
	// 签到
	public void signIn() throws Exception{
		try {
			
			SignRequest objSignReq = new SignRequest();
			
			String[] arrTransNo = getBatchAndSeqNo(null); 
			objSignReq.setBatchNo(arrTransNo[0]);	// 批次号
			objSignReq.setTraceNo(arrTransNo[1]);	// 交易号
			
			objSignReq.setMerNo("004000008710001");		// 商户号
			objSignReq.setTerminalNo("00400708");		// 终端号
			objSignReq.setTransTime(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));	// 交易时间
			objSignReq.setOperator("000");
			objSignReq.setMac("0");
			
			
			// 发送签到请求
			SignResponse objSignResp;
			Message8583 message = MessageFactory.createMessage(MessageFactory.MSG_SPEC_YNYT, objSignReq.getCode(),null);
			message.fillBodyData(objSignReq);
			message.pack();
			
			byte[] request = message.getMessage();
			log.info(ISO8583Util.printBytes(request));
			
			ISO8583Socket socket = new ISO8583Socket();
			socket.connect("182.245.126.125", 10002,50000);
			
			socket.sendRequest(request);
			
			// 获取返回结果
			byte[] response = socket.get8583Response(message.getRespLen());
			Message8583 msgResponse = MessageFactory.createMessage(MessageFactory.MSG_SPEC_YNYT, objSignReq.getCode(),null);
			msgResponse.setResponse(response);
			msgResponse.unpack();
			
			objSignResp = new SignResponse();
			msgResponse.getResponseData(objSignResp);
			objSignResp.process();
			
			log.info(objSignResp.getRespMsg());
			
			log.info("PIK Cipher:" + objSignResp.getPinKey());
			log.info("MAK Cipher:" + objSignResp.getMacKey());
			
			// 解密密文
			this.PIK = DesUtil.desDecrypt(TMK, objSignResp.getPinKey());
			this.MAK = DesUtil.desDecrypt(TMK, objSignResp.getMacKey());
			log.info("PIK Plain:" + PIK);
			log.info("MAK Plain:" + MAK);
			
			// 校验MAC
			log.info("PIK MAK:" + DesUtil.desEncrypt("3030303030303030", objSignResp.getPinKey()));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
		
}
