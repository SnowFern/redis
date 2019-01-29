package com.ice.redis.customize;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.springframework.stereotype.Service;

/**
 * diy简易的redis客户端
 * 
 * @author zy1693
 *
 */
@Service
public class CustomizeServiceImpl implements CustomizeService {

	private Socket socket;

	/**
	 * 
	 * Redis序列化协议 RESP序列化协议
	 * 
	 * 
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */

	public CustomizeServiceImpl() throws UnknownHostException, IOException {
		socket = new Socket("47.107.38.28", 6379);
		StringBuffer str = new StringBuffer();
		str.append("*2").append("\r\n");
		str.append("$4").append("\r\n");
		str.append("auth").append("\r\n");

		str.append("$").append("dooviot123456".getBytes().length).append("\r\n");
		str.append("dooviot123456").append("\r\n");
		socket.getOutputStream().write(str.toString().getBytes());
		byte[] response = new byte[2048];
		socket.getInputStream().read(response);
		System.out.println(new String(response));
	}

	@Override
	public String get(String key) {
		try {
			StringBuffer str = new StringBuffer();
			str.append("*2").append("\r\n");
			str.append("$3").append("\r\n");
			str.append("get").append("\r\n");
			// key
			str.append("$").append(key.getBytes().length).append("\r\n");
			str.append(key).append("\r\n");

			// 向redis发送resp

			socket.getOutputStream().write(str.toString().getBytes());

			byte[] response = new byte[2048];

			socket.getInputStream().read(response);

			return new String(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String set(String key, String value) {
		try {
			StringBuffer str = new StringBuffer();
			str.append("*3").append("\r\n");
			str.append("$3").append("\r\n");
			str.append("set").append("\r\n");
			// key
			str.append("$").append(key.getBytes().length).append("\r\n");
			str.append(key).append("\r\n");
			// value
			str.append("$").append(value.getBytes().length).append("\r\n");
			str.append(value).append("\r\n");

			// 向redis发送resp
			socket.getOutputStream().write(str.toString().getBytes());
			byte[] response = new byte[2048];
			socket.getInputStream().read(response);

			return new String(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
