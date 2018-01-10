package com.fastjavaframework.util;

import com.fastjavaframework.exception.ThrowException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件类
 */
public class FileUtil {

	/**
	 * 读取文件编码
	 * @param path 文件路径
	 * @return 文件编码 UTF8 UTF16BE Unicode GBK
     */
	public static String getCodeType(String path) {
		String code = null;
		try {
			BufferedInputStream bin = new BufferedInputStream(
					new FileInputStream(path));
			int p = (bin.read() << 8) + bin.read();

			switch (p) {
				case 0xefbb:
					code = "UTF8";
					break;
				case 0xfffe:
					code = "Unicode";
					break;
				case 0xfeff:
					code = "UTF16BE";
					break;
				default:
					code = "GBK";
			}
		} catch (Exception e) {
			new ThrowException("读取文件编码失败！");
		}
		return code;
	}

	/**
	 * 创建文件夹
	 * @param path 路径
	 * @return 有返回true 没有创建并返回是否成功
	 */
	public static boolean creatFolder(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return file.mkdirs();
		}
		return true;
	}

	/**
	 * 删除单个文件
	 * @param path 路径
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static void deleteFile(String path) {
		File file = new File(path);
		// 路径为文件且存在
		if (file.isFile() && file.exists()) {
			file.delete();
		}
	}

	/**
	 * 删除目录以及目录下的文件
	 * @param path 路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static void deleteFolder(String path, String... fileName) {
		// 如果path不以文件分隔符结尾，自动添加文件分隔符
		if (!path.endsWith(File.separator)) {
			path = path + File.separator;
		}
		File dirFile = new File(path);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return;
		}
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				boolean isNeedDel = true;
				if (!VerifyUtils.isEmpty(fileName)
						&& files[i].getName().indexOf(fileName.toString()) == -1) {
					isNeedDel = false;
				}
				if (isNeedDel) {
					files[i].delete();
				}
			} else { // 删除子目录
				deleteFolder(files[i].getAbsolutePath(), fileName);
			}
		}
	}

	/**
	 * 查询制定目录下的目录或文件
	 * @param path 绝对路径
	 * @param queryType 查询类型 folder目录 file文件 空所有
	 * @param returnType 返回类型 name文件名 path绝对路径
	 * @param isSub 是否查找子目录
	 * @return List<String>
	 */
	public static List<String> iterator(String path, String queryType,
			String returnType, boolean isSub) {
		List<String> fileNames = new ArrayList<>();
		if(VerifyUtils.isEmpty(path)) {
			return fileNames;
		}

		File dir = new File(path);
		File[] files = dir.listFiles();

		if (files == null) {
			return fileNames;
		}

		// 读取当前文件夹下文件
		for (int i = 0; i < files.length; i++) {
			if (("folder".equals(queryType) && files[i].isDirectory())
					|| ("file".equals(queryType) && !files[i].isDirectory())
					|| "".equals(queryType)) {
				switch (returnType) {
					default:
						fileNames.add(files[i].getName());
						break;
					case "name":
						fileNames.add(files[i].getName());
						break;
					case "path":
						fileNames.add(files[i].getPath());
						break;
				}
			}

			// 遍历子目录内文件
			if (isSub && files[i].isDirectory()) {
				fileNames.addAll(iterator(files[i].getPath(), queryType,
						returnType, isSub));
			}
		}

		return fileNames;
	}

	/**
	 * 上传文件
	 * @param file 文件流
	 * @param fileName 文件名
	 * @param path 路径
	 * @return fileName 文件名
	 */
	public static String uploadFile(MultipartFile file, String fileName, String path) {
		if (!creatFolder(path)) {
			new ThrowException("无法创建上传目录！");
		}
		
		if (VerifyUtils.isEmpty(fileName)) {
			fileName = file.getOriginalFilename();
		}
		InputStream stream = null;
		FileOutputStream fs = null;
		try {
			stream = file.getInputStream();
			fs = new FileOutputStream(path + fileName);
			
			byte[] buffer = new byte[1024 * 8];
			int byteread = 0;
			while ((byteread = stream.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
				fs.flush();
			}
		} catch (IOException e) {
			new ThrowException("上传错误：" + e.getMessage());
		} finally {
			if(null != fs) {
				try {
					fs.close();
				} catch (IOException e) {
					new ThrowException("上传错误：" + e.getMessage());
				}
			}
			if(null != stream) {
				try {
					stream.close();
				} catch (IOException e) {
					new ThrowException("上传错误：" + e.getMessage());
				}
			}
		}
		
		return fileName;
	}
	
	/**
	 * 读取以class类为相对路径目标的文件的内容
	 * @param clas class类
	 * @param path 相对于class类的文件路径
	 * @return 文件内容
	 */
	public static String readFile(Class clas,String path) {
		InputStream is = clas.getResourceAsStream(path);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			throw new ThrowException("读取文件错误：" + e.getMessage());
		}

		StringBuffer fileText = new StringBuffer();
		String tempStr = "";
		try {
			while ((tempStr = br.readLine()) != null) {
				fileText.append(tempStr).append("\n");
			}
		} catch (IOException e) {
			throw new ThrowException("拼接读取文件错误：" + e.getMessage());
		}

		return fileText.toString();
	}

    /**
     * 读取文件内容
     * @param path 文件绝对路径
     */
    public static String readFile(String path) {
        return readFile(path, "");
    }
	
	/**
	 * 读取文件内容
	 * @param path 文件绝对路径
     * @param code 文件编码 不传直接查询文件的编码
	 * @return 文件不存在返回""
	 */
	public static String readFile(String path, String code) {
        File file = new File(path);
		if(!file.exists()) {
			return "";
		}

        if(VerifyUtils.isEmpty(code)) {
            code = getCodeType(path);
        }

        InputStreamReader reader = null;
        try {
            FileInputStream fInputStream = new FileInputStream(file);
            reader = new InputStreamReader(fInputStream, code);
            BufferedReader in = new BufferedReader(reader);

            String tempStr = "";
            StringBuffer fileText = new StringBuffer();
            // 一次读入一行，直到读入null为文件结束
            while (( tempStr = in.readLine()) != null) {
                fileText.append(tempStr + "\n");
            }
            reader.close();
            
            return fileText.toString();
        } catch (IOException e) {
			throw new ThrowException("读取文件错误：" + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
        			throw new ThrowException("读取文件错误：" + e.getMessage());
                }
            }
        }
    }
	
	/**
	 * 按行读取文件
	 * @param path
	 * @return
	 */
	public static List<String> readLine(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        try {
        	List<String> list = new ArrayList<>();
            reader = new BufferedReader(new FileReader(file));
            String tempStr = "";
            // 一次读入一行，直到读入null为文件结束
            while ((tempStr = reader.readLine()) != null) {
            	list.add(tempStr);
            }
            reader.close();
            
            return list;
        } catch (IOException e) {
			throw new ThrowException("读取文件错误：" + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
        			throw new ThrowException("读取文件错误：" + e.getMessage());
                }
            }
        }
	}
	
	/**
	 * 写文件
	 * @param content
	 * @param file
	 */
	public static void writeFile(String content, File file) {
		writeFile(content, file, "");
	}
	
	/**
	 * 写文件
	 * @param content
	 * @param file
	 * @param charsetName 字符集 默认UTF-8
	 */
	public static void writeFile(String content, File file, String charsetName) {
		if(VerifyUtils.isEmpty(charsetName)) {
			charsetName = "UTF-8";
		}
		
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStreamWriter write = new OutputStreamWriter(
					new FileOutputStream(file), charsetName);
			BufferedWriter writer = new BufferedWriter(write);
			writer.write(content);
			writer.close();
		} catch (Exception e) {
			throw new ThrowException("写入文件出错：" + e.getMessage());
		}
	}
}
