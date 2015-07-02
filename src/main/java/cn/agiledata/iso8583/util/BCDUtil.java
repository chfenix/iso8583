package cn.agiledata.iso8583.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import cn.agiledata.iso8583.exception.IllegalDataException;

/**
 * 8583报文BCD格式转换工具类
 * 
 * @author zln
 *
 */
public class BCDUtil {
	
	private static final Logger log = Logger.getLogger(BCDUtil.class);

	/**
	 * 判断数据是否可以通过BCD转换
	 * 
	 * @param param
	 * @return
	 */
	public static boolean canbeBCD(byte[] param) {
		for (int i = 0; i < param.length; i++) {
			boolean flag1 = (param[i] > -1) && (param[i] < 16);
			boolean flag2 = (param[i] > 47) && (param[i] < 58);
			boolean flag3 = (param[i] > 64) && (param[i] < 71);
			boolean flag4 = (param[i] > 96) && (param[i] < 103);
			if ((!flag1) && (!flag2) && (!flag3) && (!flag4)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 将数据进行BCD压缩
	 * 数据左对齐，不足偶数为右补0
	 * 
	 * @param param
	 * @return
	 * @throws IllegalDataException
	 */
	public static byte[] doBCD(byte[] param) throws IllegalDataException{
		if (param == null) {
			log.error("parameter is empty!");
			return null;
		}
		
		if (!canbeBCD(param)) {
			log.error("argument can not convert to BCD. the data out of rang [0x00~0x0F],[0x30 ~ 0x39], [0x41 ~ 0x46],[0x61~0x66]");
			throw new IllegalDataException("[" + param + "] can not convert to BCD.");
		}
		for (int i = 0; i < param.length; i++) {
			if ((param[i] > -1) && (param[i] < 10)) {
				param[i] = (byte) (param[i] + 48);
			}
			if ((param[i] > 9) && (param[i] < 16)) {
				param[i] = (byte) (param[i] + 55);
			}
		}
		byte[] bcdData = (byte[]) null;
		String bcdStr = new String(param);
		if (bcdStr.length() % 2 == 0) {
			bcdData = str2Bcd(bcdStr);
		} else {
			bcdStr = "0" + bcdStr;
			bcdData = str2Bcd(bcdStr);
		}
		return bcdData;
	}

	/**
	 * 将数据进行BCD压缩
	 * 数据右对齐，不足偶数为左补0
	 * 
	 * @param param
	 * @return
	 * @throws IllegalDataException
	 */
	public static byte[] doBCDLEFT(byte[] param) throws IllegalDataException{
		if (param == null) {
			log.error("parameter is empty!");
			return null;
		}
		if (!canbeBCD(param)) {
			log.error("argument can not convert to BCD. the data out of rang [0x00~0x0F],[0x30 ~ 0x39], [0x41 ~ 0x46],[0x61~0x66]");
			throw new IllegalDataException("[" + param + "] can not convert to BCD.");
		}
		for (int i = 0; i < param.length; i++) {
			if ((param[i] > -1) && (param[i] < 10)) {
				param[i] = (byte) (param[i] + 48);
			}
			if ((param[i] > 9) && (param[i] < 16)) {
				param[i] = (byte) (param[i] + 55);
			}
		}
		byte[] bcdData = (byte[]) null;
		String bcdStr = new String(param);
		if (bcdStr.length() % 2 == 0) {
			bcdData = str2Bcd(bcdStr);
		} else {
			bcdStr = bcdStr + "0";
			bcdData = str2Bcd(bcdStr);
		}
		return bcdData;
	}

	/**
	 * 转换字符换位BCD压缩后的byte[]
	 * 
	 * @param bcdString
	 * @return
	 */
	public static byte[] str2Bcd(String bcdString) {
		int len = bcdString.length();
		int mod = len % 2;

		if (mod != 0) {
			bcdString = "0" + bcdString;
			len = bcdString.length();
		}

		byte[] abt = new byte[len];
		if (len >= 2) {
			len /= 2;
		}

		byte[] bbt = new byte[len];
		abt = bcdString.getBytes();

		for (int p = 0; p < bcdString.length() / 2; p++) {
			int j;
			if ((abt[(2 * p)] >= 48) && (abt[(2 * p)] <= 57)) {
				j = abt[(2 * p)] - 48;
			} else {
				if ((abt[(2 * p)] >= 97) && (abt[(2 * p)] <= 122)) {
					j = abt[(2 * p)] - 97 + 10;
				} else
					j = abt[(2 * p)] - 65 + 10;
			}
			int k;
			if ((abt[(2 * p + 1)] >= 48) && (abt[(2 * p + 1)] <= 57)) {
				k = abt[(2 * p + 1)] - 48;
			} else {
				if ((abt[(2 * p + 1)] >= 97) && (abt[(2 * p + 1)] <= 122)) {
					k = abt[(2 * p + 1)] - 97 + 10;
				} else {
					k = abt[(2 * p + 1)] - 65 + 10;
				}
			}
			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

	/**
	 * 解析BCD压缩的byte[]转换为字符串
	 * 
	 * @param bcdData
	 * @return
	 */
	public static String bcd2Str(byte[] bcdData) {
		return Hex.encodeHexString(bcdData);
	}
}
