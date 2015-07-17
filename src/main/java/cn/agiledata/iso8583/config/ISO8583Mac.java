package cn.agiledata.iso8583.config;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ISO8583 Mac配置
 * 
 * @author zln
 *
 */
@XmlRootElement(name="mac")
public class ISO8583Mac implements Serializable {

	private static final long serialVersionUID = -1523194952713522892L;
	
	/*
	 * mac明文顺序
	 */
	private String keySequence;
	
	@XmlElement(name = "keySequence")
	public String getKeySequence() {
		return keySequence;
	}

	public void setKeySequence(String keySequence) {
		this.keySequence = keySequence;
	}
	
	/*
	 * mac数据定义
	 */
	private List<ISO8583Field> keys;
	
	@XmlElementWrapper(name = "keys")  
    @XmlElement(name = "field")
	public List<ISO8583Field> getKeys() {
		return keys;
	}

	public void setKeys(List<ISO8583Field> keys) {
		this.keys = keys;
	}
	
	

}
