package cn.agiledata.iso8583.config;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "trans")  
public class ISO8583Config implements Serializable {

	private static final long serialVersionUID = 3930638048404349114L;

	/*
	 * 交易动作
	 */
	private String code;
	
	@XmlAttribute(name = "code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	/*
	 * 报文头定义
	 */
	private List<ISO8583Field> head;

	@XmlElementWrapper(name = "head")  
    @XmlElement(name = "field")
	public List<ISO8583Field> getHead() {
		return head;
	}

	public void setHead(List<ISO8583Field> head) {
		this.head = head;
	}
	
	
	/*
	 * 报文类型编码
	 */
	private ISO8583Field type;
	
	public ISO8583Field getType() {
		return type;
	}

	public void setType(ISO8583Field type) {
		this.type = type;
	}


	/*
	 * 报文体
	 */
	private List<ISO8583Field> body;
	
	@XmlElementWrapper(name = "body")  
    @XmlElement(name = "field")
	public List<ISO8583Field> getBody() {
		return body;
	}

	public void setBody(List<ISO8583Field> body) {
		this.body = body;
	}
	
	/*
	 * Mac计算位置
	 */
	private String mac;

	@XmlElement(name = "mac")
	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
	

}
