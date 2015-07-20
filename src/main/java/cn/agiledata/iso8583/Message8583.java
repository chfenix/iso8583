package cn.agiledata.iso8583;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import cn.agiledata.iso8583.config.ISO8583Field;
import cn.agiledata.iso8583.config.ISO8583Mac;
import cn.agiledata.iso8583.entity.AbstractRequestMsg;
import cn.agiledata.iso8583.entity.AbstractResponseMsg;
import cn.agiledata.iso8583.exception.ConfigErrorException;
import cn.agiledata.iso8583.util.ISO8583Util;

/**
 * 8583消息体
 * 
 * @author zln
 * 
 */
public class Message8583 {
	
	private static final Logger log = Logger.getLogger(Message8583.class);
	
	/*
	 * 反馈报文长度占位数
	 */
	private int respLen;
	
	/*
	 * 报文长度
	 */
	private byte[] length;

	/*
	 * 报文头
	 */
	private byte[] head;
	
	/*
	 * 交易类型
	 */
	private byte[] type;

	/*
	 * 位图
	 */
	private byte[] bitMap;
	
	/*
	 * 报文体
	 */
	private byte[] body;
	
	/*
	 * 全报文数据
	 */
	private byte[] message;
	
	/*
	 * 交易类型
	 */
	private ISO8583Field typeData;
	
	/*
	 * 报文头数据Map
	 */
	private TreeMap<Integer, ISO8583Field> headData;
	
	/*
	 * 报文体数据Map
	 */
	private TreeMap<Integer, ISO8583Field> bodyData;
	
	/*
	 * 报文属性别名与位置映射Map
	 */
	private HashMap<String, Integer> bodyAliasIndex;
	
	/*
	 * 反馈报文
	 */
	private byte[] reponse = null;
	
	/*
	 * 位图Set
	 */
	private BitSet bitSet = null;
	
	/*
	 * Mac明文报文
	 */
	private byte[] macPlain;
	
	/*
	 * Mac域配置
	 */
	private ISO8583Mac macSetting;
	
	/*
	 * Mac自定义key数据域
	 */
	private Map<String, ISO8583Field> macData;
	
	/*
	 * 报文规范
	 */
	private String spec;
	
	
	/**
	 * 赋值头数据至headData中
	 * 根据数据中index作为map的key
	 * 
	 * @param objField
	 */
	public void setHeadData(ISO8583Field objField) {
		if(headData == null) {
			headData = new TreeMap<Integer, ISO8583Field>();
		}
		headData.put(objField.getIndex(), objField);
	}
	
	/**
	 * 赋值头数据至headData中
	 * 根据数据中index作为map的key
	 * 
	 * @param listField
	 */
	public void setHeadData(List<ISO8583Field> listField) {
		if(listField != null && listField.size() > 0) {
			headData = new TreeMap<Integer, ISO8583Field>();
			for (ISO8583Field objField : listField) {
				headData.put(objField.getIndex(), objField);
			}
		}
	}
	
	/**
	 * 赋值消息体数据至bodyData中
	 * 根据数据中index作为map的key
	 * 
	 * @param objField
	 */
	public void setBodyData(ISO8583Field objField) {
		if(bodyData == null) {
			bodyData = new TreeMap<Integer, ISO8583Field>();
			bodyAliasIndex = new HashMap<String, Integer>();
		}
		bodyData.put(objField.getIndex(), objField);
		bodyAliasIndex.put(objField.getAlias(), objField.getIndex());
		
	}
	
	/**
	 * 赋值消息体数据至bodyData中
	 * 根据数据中index作为map的key
	 * 
	 * @param listField
	 */
	public void setBodyData(List<ISO8583Field> listField) {
		if(listField != null && listField.size() > 0) {
			bodyData = new TreeMap<Integer, ISO8583Field>();
			bodyAliasIndex = new HashMap<String, Integer>();
			
			for (ISO8583Field objField : listField) {
				bodyData.put(objField.getIndex(), objField);
				bodyAliasIndex.put(objField.getAlias(), objField.getIndex());
			}
		}
		
	}

	
	/**
	 * 构造消息头字节数组
	 */
	private void buildHead() {
		int intLen = 0;
		
		for (ISO8583Field objField : headData.values()) {
			objField.build();
			intLen += objField.getByteValueLen();
		}
		this.head = new byte[intLen];

		int pos = 0;
		for (ISO8583Field objField : headData.values()) {
			byte[] byteValue = objField.getByteValue();
			int length = objField.getByteValueLen();
			System.arraycopy(byteValue, 0, this.head, pos, length);
			pos += length;
		}
		log.info("head:"  + ISO8583Util.printBytes(this.head));
	}
	
	/**
	 * 构造交易类型字节数组
	 */
	private void buildType() {
		this.typeData.build();
		this.type = this.typeData.getByteValue();
		log.info("type:"  + ISO8583Util.printBytes(this.type));
	}

	/**
	 * 根据消息体中数据生成位图
	 */
	private void buildBitMap() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		this.bitSet = new BitSet(64);
		for (Integer key : bodyData.keySet()) {
			ISO8583Field objField = bodyData.get(key);
			if (StringUtils.isNotBlank(objField.getValue())) {
				bitSet.set(key.intValue() - 1, true);
			}
		}

		if (bitSet.length() > 64) {
			BitSet b2 = new BitSet(128);
			b2.or(bitSet);
			bitSet = b2;
			bitSet.set(0, true);
		}

		int pos = 128;
		int b = 0;
		for (int i = 0; i < bitSet.size(); i++) {
			if (bitSet.get(i)) {
				b |= pos;
			}
			pos >>= 1;
			if (pos == 0) {
				bos.write(b);
				pos = 128;
				b = 0;
			}
		}
		
		String strBitMap = String.format("%0" + (bos.size() * 8) + "d", new BigInteger(new BigInteger(1, bos.toByteArray()).toString(2)));
		log.info("bitMap bin:" + strBitMap);
		log.info("bitMap index:" + bitMapToList(strBitMap));
		this.bitMap = bos.toByteArray();
		log.info("bitMap:"  + ISO8583Util.printBytes(this.bitMap));
	}

	/**
	 * 构造消息体字节数组
	 */
	private void buildBody() {
		int intLen = 0;
		
		for (ISO8583Field objField : bodyData.values()) {
			if(StringUtils.isBlank(objField.getValue())) {
				continue;
			}
			objField.build();
			intLen += objField.getByteValueLen();
		}
		this.body = new byte[intLen];

		int pos = 0;
		for (ISO8583Field objField : bodyData.values()) {
			if(StringUtils.isBlank(objField.getValue())) {
				continue;
			}
			byte[] byteValue = objField.getByteValue();
			int length = objField.getByteValueLen();
			System.arraycopy(byteValue, 0, this.body, pos, length);
			pos += length;
		}
		
		log.info("body:\n"  + ISO8583Util.printBytes(this.body));
	}
	

	public void setTypeData(ISO8583Field typeData) {
		this.typeData = typeData;
	}
	
	/**
	 * 设置交易特有信息
	 * 
	 * @param mapBody
	 */
	public void fillBodyData(AbstractRequestMsg requestMsg) throws ConfigErrorException,Exception {
		
		Map<String, Object> mapRequset = PropertyUtils.describe(requestMsg);
		
		// 检查域中是否有组合值设置
		for (ISO8583Field objField : bodyData.values()) {
			if(StringUtils.isNotBlank(objField.getCombo())) {
				// 有组合域值，进行拼装
				// 解析设置
				StringBuffer sbValue = new StringBuffer();
				String[] arrCombo = objField.getCombo().split("\\+");
				for (int i = 0; i < arrCombo.length; i++) {
					String strKey = arrCombo[i].trim();
					if(strKey.startsWith("'") || strKey.endsWith("'")) {
						// 判断是否有完整的单引号
						if(!strKey.startsWith("'") || !strKey.endsWith("'")) {
							throw new ConfigErrorException("combo config error![" + objField.getCombo() + "] index:" + objField.getIndex());
						}
						// 删除单引号,拼装值
						sbValue.append(strKey.substring(1, strKey.length() - 1));
					}
					else {
						// 非单引号，从传入数据中获取内容
						if(mapRequset.get(strKey) != null) {
							sbValue.append(mapRequset.get(strKey));
						}
					}
				}
				objField.setValue(sbValue.toString());
			}
		}
		
		// 根据配置别名赋值，注意必须放到组合值之后，防止对某个域明确赋值无效
		for (Map.Entry<String, Object> entry : mapRequset.entrySet()) {
			Integer index = bodyAliasIndex.get(entry.getKey());
			if(index == null || entry.getValue() == null) {
				continue;
			}
			ISO8583Field objField = bodyData.get(index);
			objField.setValue(entry.getValue().toString());
			bodyData.put(index, objField);
		}
		
		if(macData != null) {
			// 遍历Mac特殊域配置，从赋值属性中取值
			for (Map.Entry<String, ISO8583Field> entry : macData.entrySet()) {
				if(mapRequset.get(entry.getKey()) == null) {
					// 赋值属性中没有mac所需内容
					continue;
				}
				
				// 将赋值属性值保存与Mac特殊域中
				ISO8583Field objField = entry.getValue();
				objField.setValue(mapRequset.get(entry.getKey()).toString());
			}
		}
	}
	
	/**
	 * 生成报文数据字节数组数据 (包含消息头、交易类型、位图、消息体)
	 * 生成的报文通过getMessage获取
	 */
	public void pack() {
		// 构造byte数组数据
		buildHead();	// 消息头
		buildType();	// 交易类型
		buildBitMap();	// 位图
		buildBody();	// 消息体
		
		int offset = 0;
		int intHeadLen = this.head.length;
		int intTypeLen = this.type.length;
		int intBitMapLen = this.bitMap.length;
		int intBodyLen = this.body.length;
		int intMsgLen = intHeadLen + intTypeLen + intBitMapLen + intBodyLen;
		
		byte[] content = new byte[intMsgLen];
		
		// 生成全报文
		System.arraycopy(this.head, 0, content, offset, intHeadLen);
		offset += intHeadLen;
		
		System.arraycopy(this.type, 0, content, offset, intTypeLen);
		offset += intTypeLen;
		
		System.arraycopy(this.bitMap, 0, content, offset, intBitMapLen);
		offset += intBitMapLen;
		
		System.arraycopy(this.body, 0, content, offset, intBodyLen);
		
		// 计算报文长度
		length = new byte[]{(byte) (content.length/256),(byte)(content.length%256)};
		
		
		this.message = new byte[content.length+2];
		System.arraycopy(length, 0, this.message, 0, 2); //拷贝长度
		System.arraycopy(content, 0, this.message, 2, content.length); //拷贝报文
		
		log.info("message:\n" + ISO8583Util.printBytes(this.message));
		
		// 生成mac明文
		// 解析mac明文域要求
		if(this.macSetting != null && StringUtils.isNotBlank(this.macSetting.getKeySequence())) {
			String[] arrIndex = this.macSetting.getKeySequence().split("\\|");
			
			int intPlainLen = 0;
			List<byte[]> listPlain = new ArrayList<byte[]>();
			for (int i = 0; i < arrIndex.length; i++) {
				String strKey = arrIndex[i];
				if(strKey.equals("head")) {
					intPlainLen += this.head.length;
					listPlain.add(this.head);
					continue;
				}
				if(strKey.equals("type")) {
					// 累加type
					intPlainLen += this.type.length;
					listPlain.add(this.type);
					continue;
				}
				if(strKey.equals("bitMap")) {
					// 累加位图
					intPlainLen += this.bitMap.length;
					listPlain.add(this.bitMap);
					continue;
				}
				
				// mac位置为数字，识别为域位置，解析位置获取具体域信息
				ISO8583Field objField = null;
				if(StringUtils.isNumeric(strKey)) {
					int intIndex = Integer.parseInt(strKey);
					objField = bodyData.get(intIndex);
					
				}
				else {
					// MAC非数字，也非确定的key,识别为参数名称，从参数中获取具体信息
					objField = macData.get(strKey);
					if(objField != null) {
						objField.build();
					}
				}
					
				if(objField == null || objField.getByteValue() == null) {
					// 域值为空
					log.error("mac plain setting [" + strKey + "] cannot found in message!");
					continue;
				}
				
				intPlainLen += objField.getByteValueLen();
				listPlain.add(objField.getByteValue());
				continue;
			}
			
			this.macPlain = new byte[intPlainLen];
			
			// 生成Mac明文
			int macOffset = 0;
			for (byte[] objByteField : listPlain) {
				System.arraycopy(objByteField, 0, macPlain, macOffset, objByteField.length);
				macOffset += objByteField.length;
			}
		}
		
	}
	
	/**
	 * 返回报文数据
	 * 必须首先通过pack生成才可以获取到返回数据
	 * 
	 * @return
	 */
	public byte[] getMessage() {
		return this.message;
	}
	
	public void setMessage(byte[] message) {
		this.message = message;
	}
	
	/**
	 * 获取Mac明文
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public byte[] getMacPlain() {
		return this.macPlain;
	}
	
	/**
	 * 附加签名后的mac至报文尾
	 * @param mac
	 */
	public void setMac(String mac) {
		byte[] byteMac = ISO8583Util.hexStringToByte(mac);
		System.arraycopy(byteMac, 0, this.message, this.message.length - byteMac.length, byteMac.length);
	}
	
	/**
	 * 赋值反馈报文
	 * 
	 * @param response
	 */
	public void setResponse(byte[] response) {
		this.reponse = response;
	}
	
	/**
	 * 反馈报文解包
	 * 
	 * @return
	 */
	public void unpack() throws Exception {
		
		// 截取报文头
		int offset = 0;
		for (ISO8583Field objHead : headData.values()) {
			objHead.setResponse(this.reponse, offset);
			objHead.parse();	// 解析数据
			offset += objHead.getByteValueLen();
		}
		
		// 截取交易类型
		this.typeData.setResponse(this.reponse, offset);
		this.typeData.parse();	// 解析数据
		offset += this.typeData.getByteValueLen();
		
		// 截取位图
		// 首先截取64位位图
		byte[] bitMap = new byte[8]; 
		System.arraycopy(this.reponse, offset, bitMap, 0, 8); //拷贝长度
		log.info("base bitMap" + ISO8583Util.printBytes(bitMap));
		
		String strBitMap = new BigInteger(1,bitMap).toString(2);
		strBitMap = String.format("%064d", new BigInteger(strBitMap));
		log.info("base bitMap bin:" + strBitMap);
		
		if(strBitMap.startsWith("1")) {
			// 如果位图第一位是1，则获取1域的额外64位扩展位图
			log.info("get extend bitmap!");
			bitMap = new byte[16];
			System.arraycopy(this.reponse, offset, bitMap, 0, 16); // 获取全部位图
			log.info("all bitMap" + ISO8583Util.printBytes(bitMap));
			
			strBitMap = new BigInteger(1,bitMap).toString(2);
			strBitMap = String.format("%064d", new BigInteger(strBitMap));
			log.info("all bitMap bin:" + strBitMap);;
			offset += 16;
		}
		else {
			offset += 8;
		}
		
		// 解析位图，获取有数据的位置
		List<Integer> listData = bitMapToList(strBitMap);
		
		log.info("data index:" + listData);
		
		// 解析消息体
		for (Integer intIndex : listData) {
			ISO8583Field objField = bodyData.get(intIndex);
			objField.setResponse(this.reponse, offset);;
			objField.parse();
			offset += objField.getByteValueLen();
		}
			
	}
	
	/**
	 * 获取解析后的交易反馈类
	 * 
	 * @param responseMsg
	 * @return
	 * @throws BusinessException
	 */
	public AbstractResponseMsg getResponseData(AbstractResponseMsg responseMsg) throws Exception {

		Map<String, Object> mapReturn = new HashMap<String, Object>();
		if (this.bodyData != null) {
			for (ISO8583Field objField : this.bodyData.values()) {
				if(objField.getMode().equals(ISO8583Constants.MODE_BIN)) {
					mapReturn.put(objField.getAlias(), objField.getByteValue());
				}
				else {
					mapReturn.put(objField.getAlias(), objField.getValue());
				}
			}
		}

		PropertyUtils.copyProperties(responseMsg, mapReturn);
		responseMsg.setSpec(this.spec);		// 报文规范
		
		return responseMsg;
	}
	
	/**
	 * 位图二进制字符串转换为List
	 * 
	 * @param bitMap
	 * @return
	 */
	private List<Integer> bitMapToList(String bitMap) {
		List<Integer> listData = new ArrayList<Integer>();
		for (int i = 1; i <= bitMap.toCharArray().length; i++) {
			if('1' == bitMap.toCharArray()[i-1]) {
				listData.add(i);
			}
		}
		
		return listData;
	}

	public void setMacSetting(ISO8583Mac macSetting) {
		this.macSetting = macSetting;
		
		if(macSetting != null && macSetting.getKeys() != null && macSetting.getKeys().size() > 0) {
			// 有特殊Mac域设置，保存设定值
			macData = new HashMap<String, ISO8583Field>();
			
			// 以别名作为key保存在map中
			for (ISO8583Field objField : macSetting.getKeys()) {
				macData.put(objField.getAlias(), objField);
			}
		}
	}

	public int getRespLen() {
		return respLen;
	}

	public void setRespLen(int respLen) {
		this.respLen = respLen;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}
	
}