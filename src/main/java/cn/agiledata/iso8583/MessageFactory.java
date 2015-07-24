package cn.agiledata.iso8583;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import cn.agiledata.iso8583.config.ISO8583Config;
import cn.agiledata.iso8583.config.ISO8583ConfigGroup;
import cn.agiledata.iso8583.config.ISO8583Field;
import cn.agiledata.iso8583.exception.ConfigNotFoundException;

/**
 * 8583消息工厂类
 * 
 * @author zln
 *
 */
public class MessageFactory {
	
	private static final Logger log = Logger.getLogger(MessageFactory.class);
	
	private static Map<String, ISO8583ConfigGroup> configs;	// 报文配置
	
	/*
	 * 报文规范定义
	 */
	public static final String MSG_SPEC_CUPS = "cups";		// 银商资讯预付卡系统
	public static final String MSG_SPEC_HBCC = "hbcitycard";		// 河北一卡通
	public static final String MSG_SPEC_WOEPAY = "woepay";	// 沃支付沃支付
	
	/*
	 * 动作定义
	 */
	public static final String ACTION_PACK = "pack";	// 组包
	public static final String ACTION_UNPACK = "unpack";	// 解包
	
	/*
	 * 交易类型
	 */
	public static final String TRANS_CODE_SIGN = "sign";		// 签到
	public static final String TRANS_CODE_SIGNOUT = "signOut";		// 签退
	public static final String TRANS_CODE_CONSUME = "consume";	// 消费
	public static final String TRANS_CODE_REVERSE_CONSUME = "reverseConsume";	// 消费冲正
	public static final String TRANS_CODE_CANCEL = "cancel";	// 消费撤销
	public static final String TRANS_CODE_REFUND = "refund";	//  退款
	
	public static final String TRANS_CODE_GETKEY = "getKey";	// Pin Key /Mac Key请求交易（河北一卡通特有交易）
	
	/**
	 * 创建8583消息类
	 * 
	 * @param spec			报文标准
	 * @param transType		交易编号
	 * @return
	 * @throws BusinessException
	 */
	/**
	 * @param spec
	 * @param transType
	 * @param action
	 * @return
	 * @throws BusinessException
	 */
	public static Message8583 createMessage(String spec,String code,String action) throws ConfigNotFoundException,Exception {
		
		Message8583 objMessage = new Message8583();
		
		// 获取报文配置
		ISO8583ConfigGroup configGroups = null;
		if(configs != null && configs.get(spec) != null) {
			// 内存中存在配置信息，直接获取
			configGroups = configs.get(spec);
		}
		else {
			// 内存中不存在配置信息，从文件中获取
			// 根据交易类型载入报文配置
			JAXBContext jaxbContext = JAXBContext.newInstance(ISO8583ConfigGroup.class);  
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();  
            configGroups = (ISO8583ConfigGroup) jaxbUnmarshaller.unmarshal(MessageFactory.class.getResourceAsStream("/config/" + spec +"/iso8583_" + spec + ".xml"));
		}
		
		ISO8583Config config = configGroups.getConfig(code);
		if(config == null) {
			// 未能获取配置，抛出异常
			log.error("spec:" + spec + " code:" + code + " action:" + action + " cann't found config!");
			throw new ConfigNotFoundException("can not found the config in [" + spec + "." + code + "]");
		}
		
		// 报文规范
		objMessage.setSpec(spec);
		
		// 交易类型
		objMessage.setCode(code);
		
		// 根据配置生成消息类
		// 头信息配置
		List<ISO8583Field> listHead = new ArrayList<ISO8583Field>(config.getHead());	// 复制配置
		objMessage.setHeadData(listHead);
		
		// 交易类型配置
		objMessage.setTypeData(config.getType());
		
		List<ISO8583Field> listBody = new ArrayList<ISO8583Field>(config.getBody());	// 复制配置
		objMessage.setBodyData(listBody);
		
		// Mac配置
		objMessage.setMacSetting(config.getMac());
		
		// 反馈报文头定义长度
		objMessage.setRespLen(configGroups.getRespLen());
		
		return objMessage;
		
	}

}
