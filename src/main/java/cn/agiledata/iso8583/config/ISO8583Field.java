package cn.agiledata.iso8583.config;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import cn.agiledata.iso8583.ISO8583Constants;
import cn.agiledata.iso8583.exception.ConfigErrorException;
import cn.agiledata.iso8583.exception.EmptyValueException;
import cn.agiledata.iso8583.util.BCDUtil;
import cn.agiledata.iso8583.util.ISO8583Util;

/**
 * 8583报文域数据封装
 * 
 * @author zln
 *
 */

public class ISO8583Field implements Serializable {
	
	private static final long serialVersionUID = 9220283651992759602L;
	
	private static final Logger log = Logger.getLogger(ISO8583Field.class);

	/*
	 * 域位置
	 */
	private int index;
	
	/*
	 * 域定义最大长度
	 */
	private int length;
	
	/*
	 * 域长度格式
	 * FIX:为固定长度
	 * LLVAR:变长，两位长度
	 * LLLVAR:变长，三位长度
	 */
	private String lengthType;
	
	/*
	 * 域存储方式
	 * ASC:表示ASCII码表示
	 * BCD:表示使用BCD压缩
	 * BIN:表示该域数据使用二进制存储
	 */
	private String mode;
	
	/*
	 * 域对其方式
	 * RIGHTSPACE：表示左靠，长度不够右边补空格
	 * LEFTSPACE:表示右靠，长度不够左边补空格
	 * RIGHTZERO：使用左靠BCD压缩(数据长度为奇数)，末尾补零
	 * LEFTZERO：使用右靠BCD压缩(数据长度为奇数)，行首补零
	 */
	private String align;
	
	/*
	 * 请求报文是否必须出现定义
	 * M:必须出现
	 * C:可不出现（默认）
	 */
	private String req;
	
	/*
	 * 反馈报文是否必须出现定义
	 * M:必须出现
	 * C:可不出现（默认）
	 */
	private String resp;
	
	/*
	 * 域数值
	 */
	private String value;
	
	/*
	 * 处理完毕的域数据
	 */
	private byte[] byteValue;
	
	/*
	 * 数据实际长度
	 */
	private int byteValueLen;
	
	/*
	 * 报文域别名
	 */
	private String alias;
	
	/*
	 * 组合值
	 */
	private String combo;
	
	@XmlAttribute
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@XmlAttribute
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@XmlAttribute
	public String getLengthType() {
		return lengthType;
	}

	public void setLengthType(String lengthType) {
		this.lengthType = lengthType;
	}

	@XmlAttribute
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	@XmlAttribute
	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	@XmlAttribute
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public byte[] getByteValue() {
		return byteValue;
	}

	public void setByteValue(byte[] byteValue) {
		this.byteValue = byteValue;
	}

	public int getByteValueLen() {
		return byteValueLen;
	}

	public void setByteValueLen(int byteValueLen) {
		this.byteValueLen = byteValueLen;
	}

	@XmlAttribute
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@XmlAttribute
	public String getReq() {
		return req;
	}

	public void setReq(String req) {
		this.req = req;
	}

	@XmlAttribute
	public String getResp() {
		return resp;
	}

	public void setResp(String resp) {
		this.resp = resp;
	}
	
	@XmlAttribute
	public String getCombo() {
		return combo;
	}

	public void setCombo(String combo) {
		this.combo = combo;
	}

	/**
	 * 构造byte数组数据
	 */
	public void build() {
		
		try {
			// 处理Value函数定义
			parseValue();
			
			// 数据检查
			if("M".equalsIgnoreCase(req)) {
				// 必须输入检查
				if(value == null) {
					throw new EmptyValueException("the value of Field[" + index + "] is empty!");
				}
			}
			
			int intValueLen = value.length();
			
			// 超长值截断
			if(intValueLen > length) {
				value = value.substring(0,length);
			}
			
			// 定长字段补足长度
			if(lengthType.equals(ISO8583Constants.LENGTH_TYPE_FIX) && intValueLen < length) {
				
				if(StringUtils.isNotBlank(align)) {
					// 左对齐，右补空格
					if(align.equals(ISO8583Constants.ALIGN_RIGHTSPACE)) {
						value = String.format("%-" + length + "s", value);
					}
					// 右对齐，左补空格
					else if(align.equals(ISO8583Constants.ALIGN_LEFTSPACE)) {
						value = String.format("%" + length + "s", value);
					}
					// 左对齐，右补0
					else if(align.equals(ISO8583Constants.ALIGN_RIGHTZERO)) {
						value = value + String.format("%0" + (length - intValueLen) + "d", 0);
					}
					// 右对齐，左补0
					else if(align.equals(ISO8583Constants.ALIGN_LEFTZERO)) {
						value = String.format("%0" + (length - intValueLen) + "d", 0) + value;
					}
				}
				else {
				// 对于没有特殊设置补0规则的，按照模式进行补足位数
				// 模式为BCD的左补0,模式为ASC的右补空格,模式为BIN的左补0
					if(mode.equals(ISO8583Constants.MODE_BCD)) {
						// 左补0
						value = String.format("%0" + (length - intValueLen) + "d", 0) + value;
					}
					else if(mode.equals(ISO8583Constants.MODE_ASC)) {
						// 右补空格
						value = String.format("%-" + length + "s", value);
					}
					else if(mode.equals(ISO8583Constants.MODE_BIN)) {
						// 左补0
						value = String.format("%0" + (length - intValueLen) + "d", 0) + value;
					}
				}
				
			}
			
			// 变长字段长度处理
			int offset = 0;
			byte[] LENVAR = (byte[]) null;
			if(lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLASC)
					|| lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLLASC)
					|| lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLBCD)
					|| lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLLBCD)) {
						
				// ASCII码长度值
				if(lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLASC)) {
					// 二位长度
					offset = 2;
					// 长度补足2位，转为byte数组
					LENVAR = String.format("%02d", intValueLen).getBytes();
				}
				if(lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLLASC)) {
					// 三位长度
					offset = 3;
					// 长度补足3位，转为byte数组
					LENVAR = String.format("%03d", intValueLen).getBytes();
				}
				
				// BCD码长度
				if(lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLBCD)) {
					// 一位长度
					offset = 1;
					LENVAR = BCDUtil.doBCD(String.format("%02d", intValueLen).getBytes());
				}
				if(lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLLBCD)) {
					// 二位长度
					offset = 2;
					LENVAR = BCDUtil.doBCD(String.format("%03d", intValueLen).getBytes());
				}
			}
			
			byte[] filed = null;
			
			// 根据模式生成域数据byte数组
			if(mode.equals(ISO8583Constants.MODE_BCD)) {
				filed = BCDUtil.doBCDLEFT(value.getBytes());
			}
			else if(mode.equals(ISO8583Constants.MODE_ASC)) {
				filed = value.getBytes();
			}
			else if(mode.equals(ISO8583Constants.MODE_BIN)) {
				filed = ISO8583Util.binaryStrToBytes(value);
			}
			else {
				log.error("illegal mode [" + mode + "]!");
				throw new ConfigErrorException("illegal mode [" + mode + "]!");
			}
			
			// 数据处理
			this.byteValue = new byte[offset + filed.length];		// 数据长度为长度声明字段+实际数据长度
					
			if(offset > 0) {
				// 写入长度
				System.arraycopy(LENVAR, 0, this.byteValue, 0, offset);
			}
			// 写入域数据
			System.arraycopy(filed, 0, this.byteValue, offset, filed.length);
			byteValueLen = byteValue.length;
			
			log.info("index:" + index + " alias:" + alias + " value:" + this.value + " content:" + ISO8583Util.printBytes(this.byteValue));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 截取反馈报文中域对应的byte数组，赋值到byteValue中
	 * 
	 * @param response
	 * @param offset 数据起始偏移量
	 */
	/**
	 * @param response
	 * @param offset
	 */
	public void setResponse(byte[] response,int offset) {
		
		int dataOffset = 0;	// 实际数据偏移量
		int intLen = 0;	// 数据长度
		// 获取数据长度
		if(lengthType.equals(ISO8583Constants.LENGTH_TYPE_FIX) ) {
			// 定长字段，长度为定义长度
			intLen = length;
			
			// 模式为BIN,长度除以8获得字节数
			if(mode.equals(ISO8583Constants.MODE_BIN)) {
				intLen = intLen/8;
			}
			
		}
		
		// 变长字段获取长度
		byte[] LENVAR = (byte[]) null;
		if(lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLASC)
				|| lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLLASC)
				|| lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLBCD)
				|| lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLLBCD)) {
			
			if(lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLASC)) {
				// 二位长度
				dataOffset = 2;
			}
			if(lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLLASC)) {
				// 三位长度
				dataOffset = 3;
			}
			// BCD码长度
			if(lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLBCD)) {
				// 一位长度
				dataOffset = 1;
			}
			if(lengthType.equals(ISO8583Constants.LENGTH_TYPE_LLLBCD)) {
				// 二位长度
				dataOffset = 2;
			}
			
			LENVAR = new byte[dataOffset];
			System.arraycopy(response, offset, LENVAR, 0, dataOffset);
			intLen = Integer.parseInt(BCDUtil.bcd2Str(LENVAR));
		}
		
		// 模式为BCD压缩，则实际读取字节长度只有定义长度的一半
		if(mode.equals(ISO8583Constants.MODE_BCD)) {
			if(intLen % 2 !=0) {
				// BCD长度有余数的时候实际字节长度再加1
				intLen += 1;
			}
			
			intLen = intLen/2;
		}
		
		// 根据长度截取数据
		this.byteValue = new byte[intLen];
		System.arraycopy(response, offset + dataOffset, this.byteValue, 0, intLen);
		
		// XXX 注意此处的byteValueLen与实际byteValue不对应，此处长度为包含变长长度定义的部分，便于外部循环累加读取偏移量
		this.byteValueLen = dataOffset + intLen;
		
		log.info("index:" + index 
				+ " alias:" + alias 
				+ " LLLVAR:" + ISO8583Util.printBytes(LENVAR) 
				+ " byteValueLen:" + this.byteValueLen 
				+ " content:" + ISO8583Util.printBytes(this.byteValue));
	}
	
	/**
	 * 解析byte数据，转换为value
	 * 
	 * 此时byte数据已经去掉变长长度定义部分，仅为 纯数据
	 */
	public void parse() {
		try {
			// 根据模式生成域数据byte数组
			if(mode.equals(ISO8583Constants.MODE_BCD)) {
				this.value = BCDUtil.bcd2Str(this.byteValue);
				if(this.value.length() > length) {
					// 实际取值长度大于定义长度，截取相应长度
					this.value = this.value.substring(0,length);
				}
			}
			else if(mode.equals(ISO8583Constants.MODE_ASC)) {
				this.value = new String(this.byteValue,"GB2312").trim();
			}
			else if(mode.equals(ISO8583Constants.MODE_BIN)) {
				this.value = ISO8583Util.byteToBinaryString(this.byteValue);
			}
			
			log.info("index:" + index + " alias:" + alias + " value:" + this.value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 处理value特殊变量定义
	 */
	private void parseValue() {
		try {
			if(StringUtils.isNotBlank(this.value)) {
				// value不为空，判断是否以特殊变量开头
				if(this.value.startsWith(ISO8583Constants.VALUE_DATE)) {
					// 日期变量，截取%d后的内容为日期格式
					this.value = DateFormatUtils.format(new Date(), this.value.substring(2));
				}
			}			
		} catch (Exception e) {
			log.error("field [" + index + "] value parse error!", e);
		}
	}
}
