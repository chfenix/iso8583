package cn.agiledata.iso8583;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class TestBase {
	
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
	protected String[] getBatchAndSeqNo(String terminalTraceNo) {
		
		if(StringUtils.isBlank(terminalTraceNo)) {
			terminalTraceNo = String.valueOf(new Date().getTime());
		}
		
		String strTraceNo = terminalTraceNo.substring(terminalTraceNo.length() - 6);	// 后六位为交易流水号
		String strBatchNo = terminalTraceNo.substring(0,terminalTraceNo.length() - 6);	// 剩余为批次号
		strBatchNo = String.format("%06d", Integer.parseInt(strBatchNo));		// 左补0至六位
		return new String[]{strBatchNo,strTraceNo};
	}

}
