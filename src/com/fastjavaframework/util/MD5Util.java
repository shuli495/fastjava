package com.fastjavaframework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

import com.fastjavaframework.exception.ThrowException;

/**
 * MD5工具类
 */
public class MD5Util {

	/**
	 * 传入字符串生成MD5加密码
	 * @param 密码明文
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

}
