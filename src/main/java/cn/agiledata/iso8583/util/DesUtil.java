package cn.agiledata.iso8583.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import cn.agiledata.iso8583.exception.DesCryptionException;

/**
 * DES加解密工具类
 * 
 * @author zln
 *
 */
public class DesUtil {

	/**
	 * DES基础加解密方法
	 * 
	 * @param key
	 * @param data
	 * @param opmode  按照Cipher的opmode定义
	 * @return
	 */
	private static byte[] des(byte[] key,byte[] data,int opmode) throws DesCryptionException{
		byte[] result = null;
		try {
			// 密钥长度必须为8
			if(key.length != 8) {
				throw new DesCryptionException("expected length of des key is 8! [" + key.length +"]");
			}
			
			// 数据长度必须为8的倍数
			if(data.length%8 != 0) {
				throw new DesCryptionException("expected length of des data must multiple of 8! [" + key.length +"]");
			}
			
			SecretKeyFactory keyFactory;  
	        DESKeySpec dks = new DESKeySpec(key);  
	        keyFactory = SecretKeyFactory.getInstance("DES");  
	        SecretKey secretkey = keyFactory.generateSecret(dks);   
	        //创建Cipher对象  
	        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");    
	        //初始化Cipher对象    
	        cipher.init(opmode, secretkey);   
			
	        result = new byte[data.length];
	        // 如果数据超过8位，循环每8位进行加解密，然后进行拼接
	        int offset = 0;
			for (int i = 0; i < data.length/8; i++) {
				
				// 需要处理的数据逐8位取出
				byte[] tmp = new byte[8];
				System.arraycopy(data, offset, tmp, 0, 8);
				
				// 进行加解密计算
		        byte[] tmpResult = cipher.doFinal(tmp);
		        
		        // 放入返回结果中
		        System.arraycopy(tmpResult, 0, result, offset, 8);
				
				offset += 8;
			}
			
		} catch (DesCryptionException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DesCryptionException(e);
		}
		return result;
	}

	/**
	 * 单DES加密算法
	 * 
	 * @param key
	 * @param data
	 * @return
	 */
	public static byte[] desEncrypt(byte[] key,byte[] data) throws DesCryptionException{
		return des(key, data, Cipher.ENCRYPT_MODE);
	}
	
	/**
	 * 单DES加密算法
	 * 
	 * @param key		十六进制
	 * @param data		十六进制
	 * @return
	 */
	public static String desEncrypt(String key,String data) throws DesCryptionException{
		return ISO8583Util.bytesToHexString(desEncrypt(ISO8583Util.hexStringToByte(key), ISO8583Util.hexStringToByte(data)));
	}
	
	/**
	 * 单DES解密算法
	 * 
	 * @param key
	 * @param data
	 * @return
	 */
	public static byte[] desDecrypt(byte[] key,byte[] data) throws DesCryptionException{
		return des(key, data, Cipher.DECRYPT_MODE);
	}
	
	/**
	 * 单DES解密算法
	 * 
	 * @param key		十六进制
	 * @param data		十六进制
	 * @return
	 */
	public static String desDecrypt(String key,String data) throws DesCryptionException{
		return ISO8583Util.bytesToHexString(desDecrypt(ISO8583Util.hexStringToByte(key), ISO8583Util.hexStringToByte(data)));
	}
	
	/**
	 * 双倍长度密钥DES加密算法(3DES)
	 * 双倍长密钥为16位，将密钥拆分为前后两个密钥Left,Right
	 * 加密过程为
	 * 1.使用Left进行加密
	 * 2.步骤1结果使用Right进行解密
	 * 3.步骤2结果使用Left进行加密
	 * 
	 * @param key
	 * @param data
	 * @return
	 */
	public static byte[] doubleDesEncrypt(byte[] key,byte[] data) throws DesCryptionException{
		byte[] result = null;
		
		if(key.length != 16) {
			throw new DesCryptionException("expected length of des key is 16! [" + key.length +"]");
		}
		
		// 拆分密钥
		byte[] keyLeft = new byte[8];
		byte[] keyRight = new byte[8];
		
		System.arraycopy(key, 0, keyLeft, 0, 8);
		System.arraycopy(key, 8, keyRight, 0, 8);
		
		// 使用Left进行加密
		byte[] tmp = desEncrypt(keyLeft, data);
		
		tmp = desDecrypt(keyRight, tmp);
		
		result = desEncrypt(keyLeft, tmp); 
		
		return result;
	}
	
	/**
	 * 双倍长度密钥DES加密算法(3DES)
	 * 双倍长密钥为16位，将密钥拆分为前后两个密钥Left,Right
	 * 加密过程为
	 * 1.使用Left进行加密
	 * 2.步骤1结果使用Right进行解密
	 * 3.步骤2结果使用Left进行加密
	 * 
	 * @param key
	 * @param data
	 * @return
	 */
	public static String doubleDesEncrypt(String key,String data) throws DesCryptionException{
		return ISO8583Util.bytesToHexString(doubleDesEncrypt(ISO8583Util.hexStringToByte(key), ISO8583Util.hexStringToByte(data)));
	}
	
	/**
	 * 双倍长度密钥DES解密算法(3DES)
	 * 双倍长密钥为16位，将密钥拆分为前后两个密钥Left,Right
	 * 解密过程为
	 * 1.使用Left进行解密
	 * 2.步骤1结果使用Right进行加密
	 * 3.步骤2结果使用Left进行解密
	 * 
	 * @param key
	 * @param data
	 * @return
	 */
	public static byte[] doubleDesDecrypt(byte[] key,byte[] data) throws DesCryptionException{
		byte[] result = null;
		
		if(key.length != 16) {
			throw new DesCryptionException("expected length of des key is 16! [" + key.length +"]");
		}
		
		// 拆分密钥
		byte[] keyLeft = new byte[8];
		byte[] keyRight = new byte[8];
		
		System.arraycopy(key, 0, keyLeft, 0, 8);
		System.arraycopy(key, 8, keyRight, 0, 8);
		
		// 使用Left进行加密
		byte[] tmp = desDecrypt(keyLeft, data);
		
		tmp = desEncrypt(keyRight, tmp);
		
		result = desDecrypt(keyLeft, tmp); 
		
		return result;
	}
	
	/**
	 * 双倍长度密钥DES解密算法(3DES)
	 * 双倍长密钥为16位，将密钥拆分为前后两个密钥Left,Right
	 * 解密过程为
	 * 1.使用Left进行解密
	 * 2.步骤1结果使用Right进行加密
	 * 3.步骤2结果使用Left进行解密
	 * 
	 * @param key
	 * @param data
	 * @return
	 */
	public static String doubleDesDecrypt(String key,String data) throws DesCryptionException{
		return ISO8583Util.bytesToHexString(doubleDesDecrypt(ISO8583Util.hexStringToByte(key), ISO8583Util.hexStringToByte(data)));
	}
}
