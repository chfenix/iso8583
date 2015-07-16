package cn.agiledata.iso8583.util;

import org.apache.commons.lang3.StringUtils;

import cn.agiledata.iso8583.exception.PinBlockException;

/**
 * PIN加解密工具类
 * 
 * @author zln
 *
 */
public class PINUtil {
	
	/**
	 * 获取加密后的PinBlock
	 * 1.卡号去掉最后一位校验位后，取右边的12位，左补0至16位
	 * 2.PIN前面补06，后补FF至16位
	 * 3.处理后的卡号与PIN进行异或
	 * 4.使用PIK进行双倍3DES加密
	 * 
	 * 
	 * @param pik  PIN KEY  十六进制，16字节
	 * @param cardNo  卡号，卡号不参与计算时传null
	 * @param pin  密码  6位ASCII
	 * @return Pin密文 十六进制
	 */
	public static String getPINBlock(String pik,String cardNo,String pin) throws PinBlockException{
		try {
			// 参数检查
			
			// PIK必须为16位
			if(StringUtils.isBlank(pik) || pik.trim().length() != 32) {
				throw new PinBlockException("PIK length must be 16 Byte! [" + pik + "]");
			}
			
			// 卡号不为空时必须大于13位
			if(StringUtils.isNotBlank(cardNo) && cardNo.trim().length() < 13) {
				throw new PinBlockException("CardNo length must greate than 13! [" + cardNo + "]");
			}
			
			// 卡号为空时认为卡号不参与计算，设定卡号为13个0
			if(StringUtils.isBlank(cardNo)) {
				cardNo = String.format("%013d", 0);
			}
			
			// 密码必须为6位
			if(StringUtils.isBlank(pin) || pin.trim().length() != 6) {
				throw new PinBlockException("Pin length must be 6! [" + pin + "0");
			}
			
			// 处理卡号，去掉最后一位校验位后取12位，左补0至16位
			String xorCardNo = cardNo.substring(cardNo.length() - 13,cardNo.length() -1);
			xorCardNo = "0000" + xorCardNo;
			
			// 处理PIN，前面补06，后面补FF至16位
			String xorPin = "06" + pin + "FFFFFFFF";
			
			// 进行异或运算
			byte[] byteCardNo = ISO8583Util.hexStringToByte(xorCardNo);
			byte[] bytePin = ISO8583Util.hexStringToByte(xorPin);
			
			byte[] xorResult=new byte[8];
			for (int i = 0; i < 8; i++) {
				xorResult[i] = (byte) (byteCardNo[i] ^ bytePin[i]);
			}
			
			// 异或结果进行双倍密钥加密
			byte[] result = DesUtil.doubleDesEncrypt(ISO8583Util.hexStringToByte(pik), xorResult);
			
			return ISO8583Util.bytesToHexString(result);
		} catch (PinBlockException e) {
			throw e;
		} catch (Exception e) {
			throw new PinBlockException(e);
		}
	}

}
