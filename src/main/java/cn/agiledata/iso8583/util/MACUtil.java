package cn.agiledata.iso8583.util;

import org.apache.commons.lang3.StringUtils;

import cn.agiledata.iso8583.exception.MacException;

/**
 * MAC工具类
 * 
 * @author zln
 *
 */
public class MACUtil {
	
	/*
	 * MAC算法补位类型
	 */
	public static final int FILL_0X00 = 0;	// 补位0x00
	public static final int FILL_0X80 = 8;	// 补位0x80
	public static final int FILL_NAN = -1;	// 补位0x80
	
	/**
	 * ANSI X9.9/X9.19 MAC计算
	 *
	 * ANSI X9.9MAC算法  
	 * (1) ANSI X9.9MAC算法只使用单倍长密钥。  
	 * (2)  MAC数据先按8字节分组，表示为D0～Dn，如果Dn不足8字节时，尾部以字节00补齐。  
	 * (3) 用MAC密钥加密D0，加密结果与D1异或作为下一次的输入。 
	 * (4) 将上一步的加密结果与下一分组异或，然后再用MAC密钥加密。 
	 * (5) 直至所有分组结束，取最后结果的左半部作为MAC。
	 * 
	 * 
	 * ANSI X9.19 MAC算法  
	 * (1) ANSI X9.19MAC算法只使用双倍长密钥。  
	 * (2)  MAC数据先按8字节分组，表示为D0～Dn，如果Dn不足8字节时，尾部以字节00补齐。  
	 * (3) 用MAC密钥左半部加密D0，加密结果与D1异或作为下一次的输入。 
	 * (4)  将上一步的加密结果与下一分组异或，然后用MAC密钥左半部加密。 
	 * (5) 直至所有分组结束。  
	 * (6) 用MAC密钥右半部解密(5)的结果。 
	 * (7) 用MAC密钥左半部加密(6)的结果。 
	 * (8) 取(7)的结果的左半部作为MAC。
	 * 
	 * 此方法将X9.9/X9.19算法统计进行计算
	 * 因为当传入密钥为单倍长时，将左右密码都设定为相同的单倍密钥，X9.19算法也可以计算出X9.9的结果
	 * 
	 * 补位规则：
	 * 分为使用0x80补位和使用0x00补位
	 * 有算法在原报文长度正好8位是需要再补足8个字节的80
	 * 此处统一算法为，在原报文基础上直接加入0x80或0x00，然后再整体补足8的倍数字节
	 * 这样即使补0x00算法中被多补了一组8字节的0x00也不影响计算结果
	 * 
	 * @param mak  Mac Key 十六进制 8字节(X9.9)/16字节(X9.19)
	 * @param macData   需要计算mac的明文域数据 十六进制
	 * @param filleType Mac补位类型   POSUtil.FILL_0X00(补位0x00)  POSUtil.FILL_0X80(补位0x80)
	 * @return mac  十六进制
	 * @throws MacException
	 */
	public static String getX919Mac(String mak,String macData, int filleType) throws MacException {
		try {
			// 参数检查
			
			// MAK不能为空
			if(StringUtils.isBlank(mak)) {
				throw new MacException("MAK cannot be null!");
			}
			
			byte[] byteMak = ISO8583Util.hexStringToByte(mak.trim());
			
			// MAK必须为8位或16位
			if(byteMak.length != 8 && byteMak.length != 16) {
				throw new MacException("The length of MAK must be 8 or 16! [" + byteMak.length + "]");
			}
			
			// 拆分MAK为左右MAK，如果MAK为8位，左右MAK均设定为相同
			byte[] makLeft = new byte[8];
			byte[] makRight = new byte[8];
			
			if(byteMak.length == 8) {
				// 8位MAK，为X9.9算法
				makLeft = byteMak;
				makRight = byteMak;
			}
			else {
				// 16位MAK,为X9.19算法
				System.arraycopy(byteMak, 0, makLeft, 0, 8);
				System.arraycopy(byteMak, 8, makRight, 0, 8);
			}
			
			// MAC Block不能为空
			if(StringUtils.isBlank(macData)) {
				throw new MacException("Mac Data must be not null!");
			}
			
			// 根据补位类型进行补位
			if(filleType == FILL_0X00) {
				// 补位0x00
				macData = macData + "00";
			}
			else if(filleType == FILL_0X80) {
				// 补位0x80
				macData = macData + "80";
			}
			else if(filleType == FILL_NAN) {
				// 不进行补位
			}
			else {
				// 补位类型错误
				throw new MacException("Fill type expected {" + FILL_0X00 + "," + FILL_0X80 + "} [" + filleType + "]");
			}
			
			// 明文不足8字节倍数时补足位数
			macData = ISO8583Util.fillMac(macData);
			
			// 分段截取8字节，并进行异或/加密 (注：ASCII明文16位十六进制即为8字节)
			byte[] xorSrc = ISO8583Util.hexStringToByte("0000000000000000");	// 初始异或值为8字节0
			for (int i = 0; i < macData.length()/16; i++) {
				// 截取8字节数据
				String strXor = macData.substring(i*16,i*16+16);
				byte[]  xorDest = ISO8583Util.hexStringToByte(strXor);
				
				// 与上一次结果进行异或
				byte[] xorResult=new byte[8];
				for (int j = 0; j < 8; j++) {
					xorResult[j] = (byte) (xorSrc[j] ^ xorDest[j]);
				}
				
				// 异或结果使用左半MAK进行单des加密,并作为下次异或元数据
				xorSrc = DesUtil.desEncrypt(makLeft, xorResult);
			}
			
			// 循环异或完成，用右半MAK进行解密
			byte[] byteCrypt = new byte[8];
			byteCrypt = DesUtil.desDecrypt(makRight, xorSrc);
			
			// 用左半密钥进行加密
			byteCrypt = DesUtil.desEncrypt(makLeft, byteCrypt);
			
			// 截取前4字节作为MAC结果
			byte[] byteMac = new byte[4];
			System.arraycopy(byteCrypt, 0, byteMac, 0, 4);
			
			// 将结果转为十六进制，并获取十六进制字节数组
			byteMac = ISO8583Util.bytesToHexString(byteMac).getBytes();
			
			// 将十六进制数组转换为字符串
			return ISO8583Util.bytesToHexString(byteMac);
		}catch (MacException e) {
			throw e;
		} catch (Exception e) {
			throw new MacException(e);
		}
	}
	
	/**
	 * 银联ECB MAC算法
	 * 
	 * @param mak  Mac Key 十六进制 8字节
	 * @param macBlock   需要计算mac的明文域数据 十六进制
	 * @return mac  十六进制
	 * @throws MacException
	 */
	public static String getCupEcbMac(String mak,String macBlock) throws MacException {
		try {
			// 参数检查
			
			// MAK必须为8位
			if(StringUtils.isBlank(mak) || mak.trim().length() != 16) {
				throw new MacException("MAK length must be 8 Byte! [" + mak + "]");
			}
			
			// MAC明文数据不能为空
			if(StringUtils.isBlank(macBlock)) {
				throw new MacException("Mac Data must be not null!");
			}
			
			// 明文不足8字节倍数时补足位数
			macBlock = ISO8583Util.fillMac(macBlock);
			
			// 分段截取8字节，并进行异或 (注：ASCII明文16位十六进制即为8字节)
			byte[] xorSrc = ISO8583Util.hexStringToByte("0000000000000000");	// 初始异或值为8字节0
			for (int i = 0; i < macBlock.length()/16; i++) {
				// 截取8字节数据
				String strXor = macBlock.substring(i*16,i*16+16);
				byte[]  xorDest = ISO8583Util.hexStringToByte(strXor);
				
				// 与上一次结果进行异或
				byte[] xorResult=new byte[8];
				for (int j = 0; j < 8; j++) {
					xorResult[j] = (byte) (xorSrc[j] ^ xorDest[j]);
				}
				
				// 异或结果作为下次异或元数据
				xorSrc = xorResult;
			}
			
			// 将最后的异或结果(8字节)转为16进制后获取相关字节数组（16个字节）
			byte[] xorResult = ISO8583Util.bytesToHexString(xorSrc).getBytes();
			
			// 保留结果的前后8个字节
			byte[] xorRsLeft = new byte[8];	// 异或结果前8字节
			System.arraycopy(xorResult, 0, xorRsLeft, 0, 8);
			
			byte[] xorRsRight = new byte[8];	// 异或结果后8字节
			System.arraycopy(xorResult, 8, xorRsRight, 0, 8);
			
			// 取前8字节使用MAK进行DES
			xorRsLeft = DesUtil.desEncrypt(ISO8583Util.hexStringToByte(mak), xorRsLeft);
			
			// 加密后的结果与后8字节异或
			byte[] xorMac = new byte[8];
			for (int j = 0; j < 8; j++) {
				xorMac[j] = (byte) (xorRsLeft[j] ^ xorRsRight[j]);
			}
			
			// 异或结果使用MAK进行DES
			xorMac = DesUtil.desEncrypt(ISO8583Util.hexStringToByte(mak), xorMac);
			
			// 将异或结果转为十六进制，并获取十六进制字节数组
			byte[] byteHexMac = ISO8583Util.bytesToHexString(xorMac).getBytes();
			
			// 取前8字节作为mac
			byte[] byteMac = new byte[8];
			System.arraycopy(byteHexMac, 0, byteMac, 0, 8);
			
			return ISO8583Util.bytesToHexString(byteMac);
		}catch (MacException e) {
			throw e;
		} catch (Exception e) {
			throw new MacException(e);
		}
	}
	
	/**
	 * 河北一卡通ECB算法
	 * 
	 * @param mak  Mac Key 十六进制 8字节
	 * @param macBlock   需要计算mac的明文域数据 十六进制
	 * @return mac  十六进制
	 * @throws MacException
	 */
	public static String getHBCCEcbMac(String mak,String macBlock) throws MacException {
		try {
			// 参数检查
			
			// MAK必须为8位
			if(StringUtils.isBlank(mak) || mak.trim().length() != 16) {
				throw new MacException("MAK length must be 8 Byte! [" + mak + "]");
			}
			
			// MAC明文数据不能为空
			if(StringUtils.isBlank(macBlock)) {
				throw new MacException("Mac Data must be not null!");
			}
			
			// 明文不足8字节倍数时补足位数
			macBlock = ISO8583Util.fillMac(macBlock);
			
			// 分段截取8字节，并进行异或 (注：ASCII明文16位十六进制即为8字节)
			byte[] xorSrc = ISO8583Util.hexStringToByte("0000000000000000");	// 初始异或值为8字节0
			for (int i = 0; i < macBlock.length()/16; i++) {
				// 截取8字节数据
				String strXor = macBlock.substring(i*16,i*16+16);
				byte[]  xorDest = ISO8583Util.hexStringToByte(strXor);
				
				// 与上一次结果进行异或
				byte[] xorResult=new byte[8];
				for (int j = 0; j < 8; j++) {
					xorResult[j] = (byte) (xorSrc[j] ^ xorDest[j]);
				}
				
				// 异或结果作为下次异或元数据
				xorSrc = xorResult;
			}
			
			// 将最后的异或结果(8字节)转为16进制后获取相关字节数组（16个字节）
			byte[] xorResult = ISO8583Util.bytesToHexString(xorSrc).getBytes();

			// 异或结果转十六进制字符串再加8000000000000000
			String strHexMac = ISO8583Util.bytesToHexString(xorResult) + "8000000000000000";
			
			// 使用X9.9计算MAC
			return getX919Mac(mak, strHexMac, FILL_NAN);
		}catch (MacException e) {
			throw e;
		} catch (Exception e) {
			throw new MacException(e);
		}
	}
	


}
