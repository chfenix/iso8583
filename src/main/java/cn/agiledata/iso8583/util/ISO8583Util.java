package cn.agiledata.iso8583.util;

import java.io.ByteArrayOutputStream;
import java.util.BitSet;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import cn.agiledata.iso8583.exception.IllegalDataException;

/**
 * ISO8583工具类
 * 
 * @author zln
 *
 */
public class ISO8583Util {
	
	private static final Logger log = Logger.getLogger(ISO8583Util.class);

	/**
	 * 打印byte数组数据输出为十六进制ASCII码
	 * 
	 * @param bArray
	 * @return
	 */
	public static final String printBytes(byte[] bArray) {
		
		if(bArray == null) {
			return "N/A";
		}
		
		StringBuffer sbReturn = new StringBuffer(bArray.length);
//		sbReturn.append("\n");

		int j = 0;
		for (int i = 0; i < bArray.length; i++) {
			String sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sbReturn.append(0);
			sbReturn.append(sTemp.toUpperCase()).append(" ");
			j++;
			if (j % 16 != 0)
				continue;
			sbReturn.append("\n");
		}

		return sbReturn.toString();
	}
	
	/**
	 * 将二进制字符串转换为byte[]
	 * 
	 * @param binString
	 * @return
	 */
	public static byte[] binaryStrToBytes(String binString) throws IllegalDataException {
		if (binString.length() % 8 != 0) {
			log.error("binary string not multiple of 8!");
			throw new IllegalDataException("[" + binString + "] not multiple of 8!");
		}
		char[] arrCharBin = binString.toCharArray();
		BitSet bs = new BitSet(arrCharBin.length);
		for (int i = 0; i < arrCharBin.length; i++) {
			switch (arrCharBin[i]) {
				case '0': {
					bs.set(i, false);
				}
					break;
				case '1': {
					bs.set(i, true);
				}
					break;
				default: {
					// 二进制字符串含有0、1之外的其他字符
					log.error("binary string contain illegal character!");
					throw new IllegalDataException("["+ binString + "] contain illegal character!");
				}
			}
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] byteReturn = new byte[binString.length() / 8];

		int pos = 128;
		int b = 0;
		for (int i = 0; i < binString.length(); i++) {
			if (bs.get(i)) {
				b |= pos;
			}
			pos >>= 1;
			if (pos == 0) {
				bos.write(b);
				pos = 128;
				b = 0;
			}
		}
		byteReturn = bos.toByteArray();
		return byteReturn;
	}
	
	/**
	 * 十六进制字符串转byte[]
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringToByte(String hex) {
		int len = hex.length() / 2;
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[(pos + 1)]));
		}
		return result;
	}
	
	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
	
	/**
	 * byte数组转换为十六进制字符串
	 * 
	 * @param bHex
	 * @return
	 */
	public static final String bytesToHexString(byte[] bHex) {
		
		StringBuffer sbReturn = new StringBuffer(bHex.length);

		for (int i = 0; i < bHex.length; i++) {
			String sTemp = Integer.toHexString(0xFF & bHex[i]);
			if (sTemp.length() < 2) {
				sbReturn.append(0);
			}
			sbReturn.append(sTemp.toUpperCase());
		}

		return sbReturn.toString();
	}
	
	/**
	 * byte数组转为二进制字符串
	 * 
	 * @param data
	 * @return
	 */
	public static String byteToBinaryString(byte[] data) {
		StringBuffer sbReturn = new StringBuffer();
		BitSet bs = new BitSet(data.length * 8);
		int pos = 0;
		for (int i = 0; i < data.length; i++) {
			int bit = 128;
			for (int b = 0; b < 8; b++) {
				bs.set(pos++, (data[i] & bit) != 0);
				bit >>= 1;
			}
		}
		for (int i = 0; i < data.length * 8; i++) {
			if (bs.get(i))
				sbReturn.append("1");
			else {
				sbReturn.append("0");
			}
		}
		return sbReturn.toString();
	}
	
	/**
	 * 获取批次号/流水号
	 * 参数为空，使用当前时间毫秒数生成批次号&流水号
	 * 如果参数不为空，通过参数拆分批次号/流水号
	 * 
	 * 批次号6位左补0
	 * 流水号6位
	 * 
	 * @param terminalTraceNo
	 * @return String[] [0]:批次号  [1]:流水号
	 */
	public static String[] getBatchAndSeqNo(String terminalTraceNo) {
		
		if(StringUtils.isBlank(terminalTraceNo)) {
			terminalTraceNo = String.valueOf(new Date().getTime());
			terminalTraceNo = terminalTraceNo.substring(0,terminalTraceNo.length()-3);
		}
		
		String strTraceNo = terminalTraceNo.substring(terminalTraceNo.length() - 6);	// 后六位为交易流水号
		String strBatchNo = terminalTraceNo.substring(0,terminalTraceNo.length() - 6);	// 剩余为批次号
		strBatchNo = String.format("%06d", Integer.parseInt(strBatchNo));		// 左补0至六位
		return new String[]{strBatchNo,strTraceNo};
	}
	
	/**
	 * 将MAC字符串补足8的倍数
	 * 
	 * @param mac
	 * @return
	 */
	public static String fillMac(String mac) {
		if(mac.length()/2%8 != 0) {
			mac = mac + String.format("%0" + ((8-mac.length()/2%8)*2) + "d", 0);
		}
		return mac;
	}
}
