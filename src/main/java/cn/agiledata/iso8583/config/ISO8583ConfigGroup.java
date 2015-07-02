package cn.agiledata.iso8583.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 8583报文配置组
 * 
 * @author zln
 *
 */
@XmlRootElement(name="config")
public class ISO8583ConfigGroup {

	
	private List<ISO8583Config> configList;
	
	private Map<String, ISO8583Config> configMap;
	
	private int respLen;

	@XmlElement(name="trans")
	public List<ISO8583Config> getConfigList() {
		return configList;
	}

	public void setConfigList(List<ISO8583Config> configList) {
		this.configList = configList;
	}
	
	
	@XmlElement(name = "respLen")
	public int getRespLen() {
		return respLen;
	}

	public void setRespLen(int respLen) {
		this.respLen = respLen;
	}

	/**
	 * 根据具体交易获取配置
	 * 
	 * @param code
	 * @return
	 */
	public ISO8583Config getConfig(String code) {
		if(configMap == null) {
			if(configList != null && configList.size() > 0) {
				configMap = new HashMap<String, ISO8583Config>();
				for (ISO8583Config objConfig : configList) {
					configMap.put(objConfig.getCode(), objConfig);
				}
				return configMap.get(code);
			}
			else {
				return null;
			}
		}
		else {
			return configMap.get(code);
		}
	}
}
