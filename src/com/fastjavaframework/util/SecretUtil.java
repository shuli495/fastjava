package com.fastjavaframework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.SecureRandom;

import com.fastjavaframework.exception.ThrowException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * MD5工具类
 */
public class SecretUtil {

	/**
	 * 传入字符串生成MD5加密码
	 * @param password 密码明文
	 * @return 密码密文
	 */
	public static String md5(String password) {
		if (VerifyUtils.isEmpty(password)) {
			return "";
		}

		// 加密过得直接返回
		if (password.length() == 32) {
			return password;
		}

		MessageDigest messageDigest;
		try {
			// 生成一个MD5加密计算摘要
			messageDigest = MessageDigest.getInstance("MD5");

			// 计算md5函数
			messageDigest.update(password.getBytes());
			// digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
			String pwd = new BigInteger(1, messageDigest.digest()).toString(16);
			return pwd.toUpperCase();
		} catch (Exception e) {
			throw new ThrowException("md5生成错误：" + e.getMessage());
		}
	}

	/**
	 * File MD5码
	 * @param file
	 * @return 32位大写MD5
	 */
	public static String md5ByFile(File file) {
		FileInputStream in = null;
			try {
				in = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new ThrowException("生成md5，读取文件错误：" + e.getMessage());
			}
			return md5ByFile(in,file.length());
	}

	/**
	 * 文件流MD5加密
	 * @param inputStream
	 * @param size	文件长度
	 * @return 32位大写MD5
	 */
	public static String md5ByFile(FileInputStream inputStream, long size) {
		String value = null;
		try {
			MappedByteBuffer byteBuffer = inputStream.getChannel().map(
					FileChannel.MapMode.READ_ONLY, 0, size);
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(byteBuffer);
			BigInteger bi = new BigInteger(1, md5.digest());
			value = bi.toString(16);
		} catch (Exception e) {
			throw new ThrowException("文件流md5加密错误：" + e.getMessage());
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new ThrowException("关闭输入流错误：" + e.getMessage());
				}
			}
		}
		return value;
	}

	/**
	 * AES128加密
	 * @param content 需要被加密的字符串
	 * @param secret 加密需要的密码
	 * @return 16进制密文
	 */
	public static String aes128Encrypt(String content, String secret) {
		try {
			// 根据secret初始化密码
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(secret.getBytes()));
			SecretKey secretKey = kgen.generateKey();

			// 转换为AES专用密钥
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");

			// 创建密码器
			Cipher cipher = Cipher.getInstance("AES");
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);

			// 加密
			byte[] result = cipher.doFinal(byteContent);
			return CommonUtil.parseByte2HexStr(result);

		} catch (Exception e) {
			throw new ThrowException("AES128加密错误：" + e.getMessage());
		}
	}

	/**
	 * 解密AES加密过的字符串
	 * @param content 16进制密文
	 * @param secret 加密时的密码
	 * @return 明文
	 */
	public static String aes128Decrypt(String content, String secret) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(secret.getBytes()));
			SecretKey secretKey = kgen.generateKey();

			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");

			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, key);

			byte[] result = cipher.doFinal(CommonUtil.parseHexStr2Byte(content));
			return new String(result);

		} catch (Exception e) {
			throw new ThrowException("AES128解密错误：" + e.getMessage());
		}
	}

	/**
	 * base64加密
	 * @param content 需要被加密的字符串
	 * @return 密文
     */
	public static String base64Encrypt(String content) {
		byte[] b = null;
		String s = null;
		try {
			b = content.getBytes("utf-8");
		} catch (Exception e) {
			throw new ThrowException("base64加密错误：" + e.getMessage());
		}
		if (b != null) {
			s = new BASE64Encoder().encode(b);
		}
		return s;
	}

	/**
	 * base64解密
	 * @param content 需要被解密的字符串
	 * @return 明文
     */
	public static String base64Decrypt(String content) {
		byte[] b = null;
		String result = null;
		if (content != null) {
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				b = decoder.decodeBuffer(content);
				result = new String(b, "utf-8");
			} catch (Exception e) {
				throw new ThrowException("base64解密错误：" + e.getMessage());
			}
		}
		return result;
	}

}
