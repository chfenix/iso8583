package cn.agiledata.iso8583;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import cn.agiledata.iso8583.util.DesUtil;
import cn.agiledata.iso8583.util.ISO8583Util;
import cn.agiledata.iso8583.util.MACUtil;
import cn.agiledata.iso8583.util.PINUtil;

public class TestUtil extends TestBase {

	@Test
	public void testPinBlock() throws Exception {
		
		String pik = "84C5491C3834928E7B26B5409A942BCF";
		String mak = "C9E3E3B489B520FE";
		
		String tmk = "44444444444444444444444444444444";
		
		System.out.println(DesUtil.desDecrypt("7F52E3201FDF045E", "28C7AFB533AEE74D"));
		
//		String macData = "0200302004C020C0981100000000000000000101386702100006379001100000000027D00000000000000000000031313131353030373132333435363738393131313131353135363D87C8B8FA17C67C26000000000000000011220014370000";
		String macData = "20131021172500000000000101000000000033040010033040000010000000000000000000000000";
		
		pik = DesUtil.doubleDesDecrypt(tmk, pik);
		System.out.println("decpik: " + pik);
		String cardNo = "9001100000000027";
		String pin = "123456";
				
		System.out.println("pinblcok:" + PINUtil.getPINBlock(pik, cardNo, pin));
		
		mak = DesUtil.doubleDesDecrypt(tmk, mak);
		mak = "D221323982526330";
		System.out.println("decmak: " + mak);
		System.out.println("mac:"  +MACUtil.getX919Mac(mak, macData,MACUtil.FILL_0X80));
		
//		System.out.println(ISO8583Util.bytesToHexString("02E48E65".getBytes()));
		
	}
	
	@Test
	public void testHBCityCardKeyResp() throws Exception {
		String strTMK = "37333045313939353633433446363337";
		String strPIK = "31443142464434304130313530373839";
		String strMAK = "4137343437453543463030433934303930423037374330393537434631334433";
		
		strTMK = new String(ISO8583Util.hexStringToByte(strTMK));
		strPIK = new String(ISO8583Util.hexStringToByte(strPIK));
		strMAK = new String(ISO8583Util.hexStringToByte(strMAK));
		
		// 解密TMK
		strTMK = DesUtil.desDecrypt("0864297531133457", strTMK);
		System.out.println("tmk:" + strTMK);
		
		// 解密pik
		strPIK = DesUtil.desDecrypt(strTMK, strPIK);
		System.out.println("pik:"  +strPIK);
		
		// 解密mak
		String strMakLeft = strMAK.substring(0,16);
		String strMakRight = strMAK.substring(16);
		// 解密
		strMakLeft = DesUtil.desDecrypt(strTMK, strMakLeft);
		strMakRight = DesUtil.desDecrypt(strTMK, strMakRight);
		// 异或
		byte[] byteMakLeft = ISO8583Util.hexStringToByte(strMakLeft);
		byte[] byteMakRight = ISO8583Util.hexStringToByte(strMakRight);
		
		byte[] xorResult=new byte[8];
		for (int j = 0; j < 8; j++) {
			xorResult[j] = (byte) (byteMakLeft[j] ^ byteMakRight[j]);
		}
		System.out.println("mak:" + ISO8583Util.bytesToHexString(xorResult));
	}
	
	
}
