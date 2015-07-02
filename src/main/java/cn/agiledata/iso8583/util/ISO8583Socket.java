package cn.agiledata.iso8583.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;

import org.apache.log4j.Logger;


/**
 * 8583 Socket通讯类
 * 
 * @author zln
 * 
 */
public class ISO8583Socket {
	
	private static final Logger log = Logger.getLogger(ISO8583Socket.class);

	private Socket socket = null;
	private String ipAddress = null;
	private int port = 0;
	private int timeout = 0;

	private InputStream inputStream;

	private OutputStream outputStream;

	private int readLen = 8; // 每次读取报文长度

	private int lenMsg = 8; // 存放返回报文长度信息的内容长度
	
	private String code;

	public ISO8583Socket() {
	}
	
	public ISO8583Socket(Socket exSocket) throws IOException {
		this.socket = exSocket;
		outputStream = exSocket.getOutputStream();
		inputStream = exSocket.getInputStream();
	}

	/**
	 * 建立一条TCP/IP连接
	 * 
	 * @param ipAddress
	 *            String 目标ip地址
	 * @param port
	 *            int 目标端口
	 * @param timeout
	 *            int 超时时间
	 * @throws IOException
	 */
	public void connect(String ipAddress, int port, int timeout)
			throws IOException {
		setIpAddress(ipAddress);
		setPort(port);
		setTimeout(timeout);
		if (socket == null) {
			try {
				socket = new Socket(ipAddress, port);
				socket.setSoTimeout(timeout);

				outputStream = socket.getOutputStream();
				inputStream = socket.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
		}
	}

	/**
	 * 关闭TCP/IP连接
	 */
	public void close() {
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送信息
	 * 
	 * @param content 发送内容
	 */
	public void sendRequest(byte[] content) throws Exception {
		try {
			if(content == null || content.length == 0) {
				close();
				return;
			}
			
			outputStream.write(content);
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
			close();
			throw e;
		}
	}

	
	/**
	 * 读取返回内容
	 * 
	 * @param len 报文长度定义字节数
	 * @throws Exception 
	 */
	public byte[] get8583Response(int len) throws Exception {
		byte msgBody[] = null;
		try {
			int dataLen; // 实际读取数据长度
			// 从socket读取报文前半部分的长度信息
			byte byteMsgLen[] = new byte[len]; // 存放前半部分报文的长度信息
			inputStream.read(byteMsgLen, 0, len);
			
			log.info("response len:" + ISO8583Util.printBytes(byteMsgLen));
			
			// 取得报文长度
			int msgBodyLen = Integer.parseInt(new BigInteger(1, byteMsgLen).toString(10));

			// 读取报文内容
			msgBody = new byte[msgBodyLen];
			readLen = msgBodyLen;

			while (readLen > 0) {
				dataLen = inputStream.read(msgBody, msgBodyLen - readLen,
						readLen);
				readLen = readLen - dataLen;
			}
			
			log.info("response content:\n" + ISO8583Util.printBytes(msgBody));
		} catch (Exception e) {
			e.printStackTrace();
			close();
			throw e;
		} finally {
		}

		return msgBody;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getReadLen() {
		return readLen;
	}

	public void setReadLen(int readLen) {
		this.readLen = readLen;
	}

	public int getLenMsg() {
		return lenMsg;
	}

	public void setLenMsg(int lenMsg) {
		this.lenMsg = lenMsg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
