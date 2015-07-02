package cn.agiledata.iso8583;

/**
 * Iso8583报文常量类
 * 
 * @author zln
 *
 */
public class ISO8583Constants {
	
	/*
	 * 长度类型
	 */
	public static final String LENGTH_TYPE_FIX = "FIX";			// 固定长度 
	public static final String LENGTH_TYPE_LLASC = "LLASC";		// 二位变长，长度使用ASCII码存储，占用2个字节
	public static final String LENGTH_TYPE_LLLASC = "LLLASC";	// 三位变长，长度使用ASCII码存储，占用3个字节
	public static final String LENGTH_TYPE_LLBCD = "LLBCD";		// 二位变长，长度使用BCD压缩，占用1个字节
	public static final String LENGTH_TYPE_LLLBCD = "LLLBCD";	// 三位变长，长度使用右靠BCD压缩，占用2个字节（前端补零）
	
	/*
	 * 存储方式
	 */
	public static final String MODE_BCD = "BCD";	// BCD压缩
	public static final String MODE_ASC = "ASC";	// ASCII码表示
	public static final String MODE_BIN = "BIN";	// 二进制
	
	/*
	 * 对齐方式
	 */
	public static final String ALIGN_RIGHTSPACE = "RIGHTSPACE";	// 左靠，长度不够右边补空格
	public static final String ALIGN_LEFTSPACE = "LEFTSPACE";	// 右靠，长度不够左边补空格
	public static final String ALIGN_RIGHTZERO = "RIGHTZERO";	// 左靠BCD压缩(数据长度为奇数)，末尾补零
	public static final String ALIGN_LEFTZERO = "LEFTZERO";		// 右靠BCD压缩(数据长度为奇数)，行首补零
	
	
	
	
	/*
	 * 报文加密方式
	 */
	public static final String DES_TYPE_DES = "001";	// 单倍长密钥算法
	public static final String DES_TYPE_3DES = "003";	// 双倍长密钥算法
	public static final String DES_TYPE_3DES_TRACK = "004";	// 双倍长密钥算法（含磁道密钥
	


}
