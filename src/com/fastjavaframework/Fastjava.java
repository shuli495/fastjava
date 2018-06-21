package com.fastjavaframework;

import com.fastjavaframework.exception.ThrowException;
import com.fastjavaframework.support.html.*;
import com.fastjavaframework.support.servlet.*;
import com.fastjavaframework.util.VerifyUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Fastjava extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Map<String,String> replaceMap = new HashMap<>();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		fastJava(req,resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		fastJava(req,resp);
	}
	
	/**
	 * 设置子页面
	 * 默认进入生成器页面
	 * @param req HttpServletRequest
	 * @return 子页面html
	 */
	private void fastJava(HttpServletRequest req, HttpServletResponse resp) {
		WebApplicationContext  ctx = WebApplicationContextUtils.getWebApplicationContext(req.getSession().getServletContext());
		replaceMap = this.getReplaceMap(req);	//获取前台页面的值
		
		boolean isReHtml = true;	//是否返回html，下载文件不返回
		
		//调用方法类型
		String methodType = null==req.getParameter("methodType")?"mapperHelper":req.getParameter("methodType");
		
		StringBuffer returnJs = new StringBuffer("<script>"); //临时返回前台js
		String subHTML = "";//返回子页面html路径
		
		//调用方法 将返回的参数装入replaceMap，与前台原有数据一并返回；默认进入生成器页面并读取表信息
		//生成器
		if(methodType.startsWith("mapperHelper")) {
			subHTML = new MapperHelperHtml().html();
			
			if("mapperHelper_readPath".equals(methodType)) {	//读取路径
				replaceMap.putAll(new MapperHelper().readPath(req.getParameter("projectPath")));
			} else if("mapperHelper_createFile".equals(methodType)) {	//生成文件
				new MapperHelper().operatorFile(req.getParameter("model"), ctx, req.getParameterMap());
				returnJs.append("alert('生成成功，请刷新项目！');");
			}

			//读取表信息
			if(null == req.getParameter("tableInfos")) {
				replaceMap.putAll(new MapperHelper().makeTableInfo(ctx));
			}
		} else if(methodType.startsWith("moduleHelper")) {	//模块管理
			ModuleHelper moduleHelper = new ModuleHelper();
			subHTML = new ModuleHelperHtml().html();

			//读取配置文件路径
			if("moduleHelper".equals(methodType)) {
				replaceMap.putAll(moduleHelper.readPath(req.getParameter("projectPath")));
			}
			//检验错误类是否启动
			replaceMap.putAll(moduleHelper.errInfo(replaceMap.get("springPath")));
			//检验Druid是否启动
			replaceMap.putAll(moduleHelper.druidInfo(replaceMap.get("webXmlPath")));
		} else if(methodType.startsWith("cacheHelper")) {	//缓存管理
			CacheHelper cacheHelper = new CacheHelper();
			subHTML = new CacheHelperHtml().html();

			//读取配置文件路径
			if("cacheHelper".equals(methodType)) {
				replaceMap.putAll(cacheHelper.readPath(req.getParameter("projectPath")));
			}
			
			//读取实体缓存信息
			if(null != replaceMap.get("mapperPath")) {
				replaceMap.putAll(cacheHelper.cacheInfo(replaceMap.get("mapperPath"),ctx));
			}
		} else if(methodType.startsWith("apiHelper")) {	//api管理
			APIHelper apiHelper = new APIHelper();
			subHTML = new ApiHelperHtml().html();
			
			//读取配置文件路径
			if("apiHelper".equals(methodType)) {
				replaceMap.putAll(apiHelper.readPath(req.getParameter("projectPath")));
			} else if("apiHelper_createDoc".equals(methodType)) {	//生成文档
				replaceMap.putAll(apiHelper.createDoc(req.getParameter("controllerChkHiden"),req.getParameter("publicUrl")));
			} else if("apiHelper_saveAsHTML".equals(methodType)) {	//保存html
				//临时文件路径
				String filePath = apiHelper.saveAsHTML(replaceMap.get("bodyHTML"));
				
		        //下载文件
				if(!VerifyUtils.isEmpty(filePath)) {
			        InputStream in = null;
			        OutputStream out = null;
			        
					try {
						resp.setHeader("Pragma", "No-cache");
						resp.setHeader("Cache-Control", "No-cache");
						resp.setDateHeader("Expires", 0);
						resp.setHeader("Content-Disposition", "attachment;filename=\"api_doc.html\"");

						in = new FileInputStream(filePath);
						int len = 0;
						byte[] buf = new byte[1024];
						out = resp.getOutputStream();
						while ((len = in.read(buf)) > 0) {
							out.write(buf, 0, len);
						}

						isReHtml = false;
					} catch (Exception e) {
						returnJs.append("alert('保存html出错！');");
					} finally {
						try {
							if (in != null) {
								in.close();
							}
							if (out != null) {
								out.close();
							}
						} catch (IOException e) {
							returnJs.append("alert('保存html出错！');");
						}
					}
				}
			}

			//获取controller
			replaceMap.putAll(apiHelper.getController(replaceMap.get("controllerPath"),replaceMap.get("controllerChkHiden")));
		} else if(methodType.startsWith("quartzHelper")) {	//定时任务管理
			subHTML = new QuartzHelperHtml().html();
		}
		
		returnJs.append("</script>");

		//返回前台html
		if(isReHtml) {
			//加入js
			subHTML += returnJs.toString();

			//将子页面拼接入框架页
			String indexHTML = new IndexHtml().html();
			String subPage = "<div id=\"subPage\">";
			String[] indexHTMLs = indexHTML.split(subPage);
			String fastjavaHTML = indexHTMLs[0] + subPage + subHTML + indexHTMLs[1];

			//根据replaceMap替换子页面的值
			for(String key : replaceMap.keySet()) {
				fastjavaHTML = fastjavaHTML.replace("var " + key, "var " + key + "=" + replaceMap.get(key));
			}

			//返回html
			resp.setCharacterEncoding("UTF-8");
			resp.setHeader("content-type","text/html;charset=UTF-8");
			try {
				PrintWriter out = resp.getWriter();
				out.print(fastjavaHTML);
				out.flush();
				out.close();
			} catch (IOException e) {
				throw new ThrowException("生成html错误：" + e.getMessage());
			}
		}
	}
	
	/**
	 * 获取前台页面的值
	 * 前台的值，装入replaceMap，最后返回前台(跳转servlet，前台页面值不变)
	 * @param req HttpServletRequest
	 * @return 前台的值Map<String,String>
	 */
	private Map<String,String> getReplaceMap(HttpServletRequest req) {
		Map<String,String> replaceMap = new HashMap<>();
		String notSignProperty = "tableInfos";	//前台不加双引号的变量
		
		for (String key : req.getParameterMap().keySet()) {
			StringBuffer value = new StringBuffer("");
			if(req.getParameterMap().get(key).length > 0) {
				//添加值开始的双引号，tableInfos数组格式，值不加""号
				if(notSignProperty.indexOf(key) == -1) {
					value.append("\"");
				}
				
				//转换编码
				String paramVal = "";
				try {
					byte[] arrayStr = req.getParameterMap().get(key)[0].getBytes("iso-8859-1");
					paramVal = new String(arrayStr, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					throw new ThrowException("转换参数编码错误：" + e.getMessage());
				}
						
				//格式化路径中的"\"符号
				value.append(paramVal.replaceAll("\\\\", "\\\\\\\\"));
				
				//添加值结尾的双引号，tableInfos数组格式，值不加""号
				if(notSignProperty.indexOf(key) == -1) {
					value.append("\"");
				}
			}
			replaceMap.put(key, value.toString());
		}
		
		return replaceMap;
	}
}
