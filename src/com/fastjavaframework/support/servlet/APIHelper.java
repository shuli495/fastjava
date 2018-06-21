package com.fastjavaframework.support.servlet;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fastjavaframework.base.BaseBean;
import com.fastjavaframework.base.BaseService;
import com.fastjavaframework.support.html.ApiHelperTemplateHtml;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSON;
import com.fastjavaframework.response.Result;
import com.fastjavaframework.util.CommonUtil;
import com.fastjavaframework.util.FileUtil;
import com.fastjavaframework.util.StringUtil;
import com.fastjavaframework.util.VerifyUtils;

import javax.validation.constraints.NotNull;

/**
 * api管理
 */
public class APIHelper {

	/**
	 * 读取spring配置文件路径
	 * @param projectPath
	 * @return
     */
	public Map<String,String> readPath(String projectPath) {
		Map<String,String> replaceMap = new HashMap<>();
		
		if(VerifyUtils.isEmpty(projectPath)) {
			return replaceMap;
		}
		
		//遍历项目子目录
		List<String> paths = FileUtil.iterator(projectPath, "folder", "path", true);
		for(String path : paths) {
			String rePath = "\"" + path.replaceAll("\\\\", "\\\\\\\\") + "\"";
			
			if((path.indexOf("action") != -1 || path.indexOf("controller") != -1) && path.indexOf("class") == -1) {
				replaceMap.put("controllerPath", rePath);
			}
		}
		return replaceMap;
	}
	
	/**
	 * 读取action文件
	 * @param controllerPath controller路径
	 * @param controllerInfo 是否选中
	 * @return 是否选中,action文件名
	 */
	public Map<String,String> getController(String controllerPath,String controllerInfo) {
		StringBuffer controller = new StringBuffer();
		Map<String,String> returnMap = new HashMap<>();
		
		if(VerifyUtils.isEmpty(controllerPath)) {
			return returnMap;
		}
		
		List<String> paths = FileUtil.iterator(controllerPath.replaceAll("\"", ""), "file", "path", true);
		if(paths.size() == 0) {
			return returnMap;
		}
		
		for(String path : paths) {
			String[] pathAttr = path.split("\\\\");
			String controllerFileName = pathAttr[pathAttr.length-1].replace(".java", "");
			
			if(!"".equals(controller.toString())) {
				controller.append(",");
			}
			
			String isChk = "false";
			if(null != controllerInfo && controllerInfo.indexOf(controllerFileName) != -1) {
				isChk = "true";
			}
			
			controller.append("[").append(isChk).append(",\"").append(controllerFileName).append("\",\"").append(path.replaceAll("\\\\", "\\\\\\\\")).append("\"]");
		}
		returnMap.put("controllerMeta", "[" + controller.toString() + "]");
		return returnMap;
	}
	
	/**
	 * 生成文档
	 * @param controllerInfo 勾选的controller
	 * @return 文档html
	 */
	public Map<String, String> createDoc(String controllerInfo, String publicUrl) {
		Map<String, String> returnMap = new HashMap<>();
		String reHTML = "";
		String[] controllers = controllerInfo.split(",");

		for (int classIndex = 0; classIndex < controllers.length; classIndex++) {
			String controller = controllers[classIndex];
			if ("".equals(controller)) {
				continue;
			}

			String oldCode = FileUtil.readFile(controller, "UTF8");	//java文件全部代码
			String javaCode = oldCode;	//java文件class类代码 不包含import


			/*******************************获取类信息begin**************************************/
			// 获取class包名
			String packageName = "";
			String regex = "package\\s+.+\\s*;";
			Matcher matcher = Pattern.compile(regex).matcher(javaCode);
			if (matcher.find()) {
				packageName = matcher.group(0);
			}
			String[] paths = controller.split("\\\\");
			
			// class包名+类名
			String fileClassName = paths[paths.length - 1].split("\\.")[0];	//类名
			String className = packageName.replace("package", "").replace(";", "").trim() 
					+ "." + fileClassName;
			
			//当前java类对象
			Class clz = null;
			try {
				clz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				continue;
			}

			//类注释
			String clzNote = "";
			regex = "public\\s+class\\s+.+\\{";
			Matcher classMatcher = Pattern.compile(regex).matcher(javaCode);
			if (classMatcher.find()) {
				int endIndex = javaCode.indexOf(classMatcher.group(0));
				
				String[] classNotes = javaCode.substring(packageName.length(), endIndex).replaceAll("\\/", "").split("\\*");
				for(String classNote : classNotes) {
					//排除import，注释
					if(!"".equals(classNote.trim()) && !classNote.trim().startsWith("import") && !classNote.trim().startsWith("@") && !classNote.trim().startsWith(";")) {
						clzNote = classNote.replace("\n", "").trim();
						break;
					}
				}
				
				javaCode = javaCode.substring(endIndex);
			}

			if(VerifyUtils.isEmpty(clzNote)) {
				clzNote = fileClassName.trim();
			}

			// 类rest-url
			String classUrl = "";
			for (Annotation annotation : clz.getAnnotations()) {
				if (annotation instanceof RequestMapping) {
					RequestMapping rm = (RequestMapping) annotation;
					if (rm.value().length > 0) {
						classUrl = rm.value()[0];
					}
				}
			}
			classUrl = publicUrl + classUrl;
			/*******************************获取类信息end**************************************/
			

			/*******************************获取方法信息begin***********************************/
			regex = "/\\*";
			Pattern leftpattern = Pattern.compile(regex);
			Matcher leftmatcher = leftpattern.matcher(javaCode);
			regex = "\\)\\s*\\{";
			Pattern rightpattern = Pattern.compile(regex);
			Matcher rightmatcher = rightpattern.matcher(javaCode);

			Map<String,String> notes = new HashMap<>();	//方法注释
			Map<String,String> methodParameters = new HashMap<>();	//方法参数名
			
			int begin = 0;
			while (leftmatcher.find(begin)) {
				//方法代码
				rightmatcher.find(leftmatcher.start());
				String methodStr = javaCode.substring(leftmatcher.start(),rightmatcher.end());
				begin = rightmatcher.end();
				
				if(methodStr.indexOf("public ") != -1) {
					String[] method = methodStr.split("public ");
					
					//方法注释
					String note = "";
					for(String n : method[0].split("\\*")) {
						String newN = n.replaceAll("\\/", "").trim();
						//排除注解
						if(!"".equals(newN) && !newN.startsWith("@")) {
							note = newN;
							break;
						}
					}
					
					//根据方法名暂存注释
					String methodName = method[1].split(" ")[1].split("\\(")[0];
					if(method[1].indexOf("class ") != -1) {
						notes.put("class", note);
					} else {
						notes.put(methodName, note);
					}
					
					//暂存方法参数
					int bracketLInx = method[1].indexOf("(");
					int bracketRInx = method[1].lastIndexOf(")");
					String[] paramsStrs = method[1].substring(bracketLInx + 1, bracketRInx)
									.replaceAll("@.+?(\\)|\\s)", "")
									.replaceAll("\\t", "")
									.replaceAll("\\n", "")
									.split("\\,");
                    int idx = 0;
					for(String paramStr : paramsStrs) {
						String[] paramItem = paramStr.split(" ");
						for (int i=paramItem.length-1;i>=0;i--) {
							String item = paramItem[i];
							if(!"".equals(item.trim())) {
								methodParameters.put(methodName + "_" + idx, item.replace(")",""));
								idx++;
								break;
							}
						}
					}
				}
			}
			/*******************************获取方法信息end***********************************/
			

			
			/*******************************方法注释 入参 出参 begin***********************************/
			Map<Integer,String> methodIndx = new HashMap<>();	//保存方法顺序 排序用
			
			for (int methodIndex=0; methodIndex < clz.getDeclaredMethods().length; methodIndex++) {
				Method method = clz.getDeclaredMethods()[methodIndex];
				
				String methodUrl = "";	//方法rest-url
				String requestType = "";//请求类型
				for (Annotation annotation : method.getAnnotations()) {
					if (annotation instanceof RequestMapping) {
						RequestMapping rm = (RequestMapping) annotation;
						if (rm.value().length > 0) {
							methodUrl = rm.value()[0];
						}
						if (rm.method().length > 0) {
							requestType = rm.method()[0].name();
						}
					}
				}

				
				/*----------------------------入参 begin------------------------------*/
				StringBuffer paramterHtml = new StringBuffer();	//入参html(表格数据)
				StringBuffer paramterBody = new StringBuffer();	//入参json

				Class[] paramterTypes = method.getParameterTypes();
				for(int i=0; i < paramterTypes.length; i++) {
					Class paramterClz = paramterTypes[i];

					//判断参数类型 以下4中需要在api文档中展示
					boolean isReqBody = false;	//json - javabean
					boolean isReqParam = false;	//url参数
                    boolean isPathVal = false;  //路径参数
					boolean isReqHeader = false;//header参数
					boolean isCookieVal = false;//cookie参

					Annotation[] paramterAnnotations = method.getParameterAnnotations()[i];
					for(int j=0; j<paramterAnnotations.length; j++) {
						if(paramterAnnotations[j] instanceof RequestBody) {
							isReqBody = true;
						} else if(paramterAnnotations[j] instanceof RequestParam) {
							isReqParam = true;
						} else if(paramterAnnotations[j] instanceof PathVariable) {
                            isPathVal = true;
                        } else if(paramterAnnotations[j] instanceof RequestHeader) {
                            isReqHeader = true;
                        } else if(paramterAnnotations[j] instanceof CookieValue) {
							isCookieVal = true;
						}
					}
					
					//非以上4种参数不展示
					if(!isReqBody && !isReqParam && !isPathVal && !isReqHeader && !isCookieVal) {
						continue;
					}
					
					
					//参数Class
					String parameterClzStr = "";
					String methodStr = method.toGenericString();	//完整方法头
					String parameterStr = methodStr.substring(methodStr.indexOf("(") + 1, methodStr.indexOf(")"));
					String[] parameters = parameterStr.split(",")[i].split(" ");
					for(int j = parameters.length-1; j >= 0; j--) {
						if(!"".equals(parameters[j].trim())) {
							parameterClzStr = parameters[j].trim();
							break;
						}
					}

					
					String paramterGenerosity = "";	//参数泛型
					String parameterName = methodParameters.get(method.getName()+"_"+i);	//参数名
					boolean isParamNull = false;
                    try {
                        for(Annotation annotation : paramterAnnotations) {
							Map<String, Object> annMap = CommonUtil.getAnnotationValue(annotation);
							if(!VerifyUtils.isEmpty(annMap.get("value"))) {
								parameterName = annMap.get("value").toString();
							}

							if(!VerifyUtils.isEmpty(annMap.get("required"))) {
								isParamNull = (boolean)annMap.get("required");
							}
                        }
                    } catch (Exception e) {
                    }

					if(isReqParam && isParamNull) {
						if(i == 0) {
							methodUrl += "?";
						} else if(i < paramterTypes.length) {
							methodUrl += "&";
						}

						methodUrl += parameterName + "=";
					}

                    String localIn = "";
                    if(isReqHeader) {
                        localIn = "header";
                    } else if(isCookieVal) {
                        localIn = "cookie";
                    } else if(isPathVal) {
                        localIn = "url";
                    } else if(isReqParam) {
                        localIn = "path";
                    } else if(isReqBody) {
                        localIn = "body";
                    }

					//参数是集合
					Class paramClzForHtml = paramterClz;
					String paramFlag = "";
					if(paramterClz.isInterface()) {	//list
						try {
							//参数泛型
							paramterGenerosity = parameterClzStr.substring(parameterClzStr.indexOf("<") + 1, parameterClzStr.indexOf(">"));
							paramClzForHtml = Class.forName(paramterGenerosity);
							paramFlag = "list.";
						} catch (Exception e) {
						}
					} else if(paramterClz.isArray()) {	//数组
						try {
							paramterGenerosity = parameterClzStr.replace("[]","");
							paramClzForHtml = Class.forName(paramterGenerosity);
							paramFlag = "list.";
						} catch (Exception e) {
						}
					}

					//参数html
					paramterHtml.append(setParameterHtml(paramClzForHtml, controllerInfo, parameterName, paramFlag, localIn, isParamNull));
					
					
					//如果参数是json类型，则展示json
					if(isReqBody) {
						//处理数组类型
						if(paramterClz.isArray()) {
							paramterClz = ArrayList.class;
						}

						//参数泛型class
						Class paramterGenerosityClz;
						try {
							paramterGenerosityClz = Class.forName(paramterGenerosity);
						} catch (ClassNotFoundException e) {
							paramterGenerosityClz = null;
						}
						//返回json
						paramterBody.append(JSON.toJSONString(CommonUtil.setDefValue(paramterClz, paramterGenerosityClz)));
					}
				}
				/*----------------------------入参 end------------------------------*/
				


				/*----------------------------出参 begin------------------------------*/
				String returnBody = "";	//出参json
				Class returnClass = method.getReturnType();	//出参class
				
				//无出参
				if(Void.class.getName().equals(returnClass.getName()) || "void".equals(returnClass.getName())) {
					returnBody = "";
				} else if(Object.class.getName().equals(returnClass.getName())) {	//Object类型行出参
					//方法体
					String methodBody = getMethodBody(javaCode, method.getName());
					// return内容
					String methodReturn = this.getMethodReturn(methodBody);

					try {
						returnBody = JSON.toJSONString(this.return2Object(clz, oldCode, methodBody, methodReturn));
					} catch (Exception e) {
						returnBody = "";
					}
				} else {	//非Object类型出参
					returnBody = JSON.toJSONString(CommonUtil.setDefValue(returnClass));
				}
				/*----------------------------出参 end------------------------------*/


				String h3 = VerifyUtils.isEmpty(notes.get(method.getName()))?method.getName():notes.get(method.getName());
				String methodHtml = "<span>"+h3+"</span></h3>"
						+"<pre><b>"+requestType +"</b>&nbsp;&nbsp;"+classUrl+methodUrl+"</pre>"
						+"<p>请求参数：</p>"
						+"<table border=\"1\" cellspacing=\"0\" cellpadding=\"8\">"
						+"<tr><th>参数名</th><th>位于</th><th>数据类型</th><th>必填</th><th>描述</th></tr>"
						+ paramterHtml.toString()
						+"</table>"
						+"<p>请求 body：</p>"
						+"<pre name=\"jsonPre\">" + ("".equals(paramterBody.toString()) ? "" : paramterBody) + "</pre>"
						+"<p>返回 body：</p>"
						+"<pre name=\"jsonPre\">" + ("".equals(returnBody.toString()) ? "" : returnBody) + "</pre></br>";
				
				methodIndx.put(javaCode.indexOf(method.getName()+"("), methodHtml);
			}
			/*******************************方法注释 入参 出参 end***********************************/
			
			
			//方法排序(class.getMethod无序，按代码正确顺序排序)
			String[] methods = new String[methodIndx.size()];
			for(Integer i : methodIndx.keySet()) {
				int smallNum = 0;
				for(Integer j : methodIndx.keySet()) {
					if(!i.equals(j) && i > j) {
						smallNum++;
					}
				}
				
				methods[smallNum] = "<h3>"+(classIndex+1)+"."+(smallNum+1)+"&nbsp;"+methodIndx.get(i);
			}
			
			//返回html
			StringBuffer reStr = new StringBuffer();
			for(String method : methods) {
				reStr.append(method);
			}

			reHTML += "<h2>"+(classIndex+1) + "&nbsp;<span>" + clzNote + "</span></h2>" + reStr.toString();
		}

		returnMap.put("doc", "'" + reHTML + "'");
		return returnMap;
	}

	/**
	 * 判断字符串是否是数字
	 * @param str
	 * @return true是数字
     */
	private boolean isNumber(String str) {
		try {
			Long.parseLong(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断是否是boolean
	 * @param str
	 * @return true是boolean
     */
	private boolean isBoolean(String str) {
		try {
			Boolean.valueOf(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 返回方法return的Object
	 * @param clz		当前java类
	 * @param clzBody	当前java代码
	 * @param methodBody	当前方法体
	 * @param returnString	当前方法return的代码
     * @return	return的值得Object对象
     */
	private Object return2Object(Class clz, String clzBody, String methodBody, String returnString) {
		//判断return的是方法还是变量
		if(returnString.indexOf("(") == -1 && returnString.indexOf(")") == -1) {	// return的是变量
			// renturn 数字
			if(this.isNumber(returnString)) {
				return "0";
			}

			// return 字符串
			if(returnString.startsWith("\"") && returnString.endsWith("\"")) {
				return "String";
			}

			// return boolean
			if(this.isBoolean(returnString)) {
				return true;
			}

			// return 变量
			String variableName = "";	// 查找变量声明的对象
			Matcher variableMatcher = Pattern.compile(".+\\s+" + returnString + "\\s*(\\)|\\=|\\;)").matcher(methodBody);
			if (variableMatcher.find()) {
				String[] variableNames = variableMatcher.group(0).split(returnString)[0].trim().split(" ");
				variableName = variableNames[variableNames.length - 1];
			}

			//变量是String 直接返回
			if("String".equals(variableName)) {
				return "String";
			} else if("Integer".equals(variableName) || "int".equals(variableName)) {	//变量是int返回0
				return "0";
			} else {	//变量是对象
				//查找变量import的class
				String variableClz = this.getImportClz(clzBody, variableName);

				if(!"".equals(variableClz)) {
					try {
						return CommonUtil.setDefValue(Class.forName(variableClz));
					} catch (ClassNotFoundException e) {
						return "";
					}
				}
			}
		} else {	//返回的是方法名

			// return的方法的名
			String returnMethodName = returnString.substring(0, returnString.indexOf("("));

			// return BaseAction<Service> 中的service
			if(returnMethodName.startsWith("this.service.") || returnMethodName.startsWith("super.service.") || returnMethodName.startsWith("service.")) {
				try {
					//调用service中方法的方法名
					String[] clzMethodItems = returnMethodName.split("\\.");
					String servicMethodName = clzMethodItems[clzMethodItems.length-1];

					// service的类
					Class serviceClz = (Class) ((ParameterizedType)clz.getGenericSuperclass()).getActualTypeArguments()[0];

					for(Method clzMethod : serviceClz.getMethods()) {
						if(clzMethod.getName().equals(servicMethodName)) {
							//service泛型中的实体对象
							Class methodGenerosity = (Class) ((ParameterizedType)serviceClz.getGenericSuperclass()).getActualTypeArguments()[1];

							if(BaseBean.class.isAssignableFrom(clzMethod.getReturnType())) {
								return CommonUtil.setDefValue(methodGenerosity);
							} else {
								return CommonUtil.setDefValue(clzMethod.getReturnType(), methodGenerosity);
							}
						}
					}
				} catch (Exception e) {
					return "";
				}
			}

			// return success()/success(obj)
			if("success".equals(returnMethodName)) {
				//返回方法参数
				String returnMethodParamStr = returnString.substring(returnString.indexOf("(")+1,returnString.lastIndexOf(")"));

				//返回方法参数名
				String[] returnMethodParamNames = new String[]{};
				if(returnMethodParamStr.length() > 0) {
					if(returnMethodParamStr.indexOf("(") < returnMethodParamStr.indexOf(",") &&
							returnMethodParamStr.indexOf(")") > returnMethodParamStr.indexOf(",")) {
						returnMethodParamNames = returnMethodParamStr.split("\\)\\s*,");
					} else {
						returnMethodParamNames = returnMethodParamStr.split("\\,");
					}
				}

				// 无参数直接返回success()的值
				if(returnMethodParamNames.length == 0) {
					try {
						return clz.getMethod(returnMethodName).invoke(clz.newInstance());
					} catch (Exception e){
						return "";
					}
				} else {	//有参数

					Class[] returnClzs = new Class[returnMethodParamNames.length];	//返回方法参数类
					Object[] returnObjs = new Object[returnMethodParamNames.length];//返回方法参数对象

					// 有参数先获取参数的默认值对象
					for(int i=0; i<returnMethodParamNames.length; i++) {
						Object returnMethodParamNameObj = this.return2Object(clz, clzBody, methodBody, returnMethodParamNames[i]);
						returnClzs[i] = Object.class;
						returnObjs[i] = returnMethodParamNameObj;
					}

					// 根据参数的值返回success(obj)
					try {
						return clz.getMethod(returnMethodName, returnClzs).invoke(clz.newInstance(), returnObjs);
					} catch (Exception e) {
						return "";
					}
				}
			}

			//包含点 调用其它类中的方法，否则调用本类中的方法
			if(returnMethodName.indexOf(".") != -1) {	//调用其他类的方法

				//类名
				String serviceName = returnMethodName.split("\\.")[0].trim();
				//方法名
				String methodName = returnMethodName.split("\\.")[1].replace("()","").trim();

				//在方法体中查找类的声明。查找到则该类为方法中的私有变量；否则是其他的类
				Matcher paramImportClzMatcher = Pattern
						.compile(".+\\s+" + serviceName + "\\s*(\\(|\\=|\\;|\\))")
						.matcher(methodBody);

				//当前方法的私有类
				if(paramImportClzMatcher.find()) {
					//声明类的字符串
					String matcherStr = paramImportClzMatcher.group(0).trim();
					String[] paramClzs = matcherStr.split(returnMethodName.split("\\.")[0].trim())[0].split(" ");

					for(int k=paramClzs.length - 1;k>=0;k--) {
						if(!"".equals(paramClzs[k])) {
							// 查找类的import值
							String paramImportClz = this.getImportClz(clzBody, paramClzs[k]);
							if(!"".equals(paramImportClz)) {
								try {
									//调用方法的返回值类型
									Class paramClzObj = Class.forName(paramImportClz).getMethod(methodName).getReturnType();
									return CommonUtil.setDefValue(paramClzObj);
								} catch (Exception e) {
									return "";
								}
							}
						}
					}

				} else{	//调用的是其他的类

					// 获取方法引用的其他的类
					Map<String,String> publicServices = this.getPublicVariable(clzBody);

					if(publicServices.containsKey(serviceName)) {
						try {
							// 引用类的class
							Class reClz = Class.forName(publicServices.get(serviceName));

							for(Method method :  reClz.getMethods()) {
								if(method.getName().equals(methodName)) {
									// 调用的方法属于BaseService，获取BaseService的泛型实体，并返回值
									if(method.getDeclaringClass().getName().equals(BaseService.class.getName())) {
										// BaseService的泛型实体
										Class methodGenerosity = (Class) ((ParameterizedType)reClz.getGenericSuperclass()).getActualTypeArguments()[1];
										return CommonUtil.setDefValue(method.getReturnType(), methodGenerosity);

									} else {	// 非BaseServicce中的方法，直接返回方法返回的值
										return CommonUtil.setDefValue(method.getReturnType());
									}
								}
							}
						} catch (Exception e) {
							return "";
						}
					}
				}
			} else {	//return本类中的方法
				try {
					// renturn的方法的名
					returnMethodName = returnMethodName.replaceAll("this\\.","").replaceAll("super\\.","");

					// 方法的返回值
					Class reFunctionClz = clz.getMethod(returnMethodName).getReturnType();

					// 本类中的方法返回的是基本类型，直接返回
					if(!reFunctionClz.getName().equals(Object.class.getName())) {
						return CommonUtil.setDefValue(reFunctionClz);

					} else {	// 本类中的方法返回的是对象，返回该对象的默认值
						// 本类中方法的方法体
						String reMethodBody = this.getMethodBody(clzBody, returnMethodName);
						// 本类中方法的返回值
						String reMethodReturn = this.getMethodReturn(reMethodBody);

						// 返回默认值
						return this.return2Object(clz, clzBody, reMethodBody, reMethodReturn);
					}
				} catch (Exception e) {
					return "";
				}
			}

		}

		return "";
	}

	/**
	 * 保存成html
	 * @param html
	 */
	public String saveAsHTML(String html) {
		FileOutputStream out = null;
		OutputStreamWriter osw = null;
        try {
			String tmpHtml = new ApiHelperTemplateHtml().html().replace("api_doc_html", html.substring(1, html.length() - 1));

			// 临时文件
			File file = File.createTempFile("api_doc", ".html");
			file.deleteOnExit();

			out = new FileOutputStream(file);
			osw = new OutputStreamWriter(out,"UTF-8");
			osw.write(tmpHtml);

			return file.getAbsolutePath();
        } catch (Exception e) {
        } finally {
			try {
				osw.close();
				out.close();
			} catch (IOException e) {
			}
		}

        return "";
	}

	/**
	 * 设置展示参数的html(入参表格)
	 * @param clz 入参对象类
	 * @param controllerInfo 项目路径
	 * @param parameterName 入参对象名
	 * @param paramterFlag 入参对象前缀
     * @param localIn 位于
	 * @param isParamNull 是否必须输入
	 * @return html
	 */
	private String setParameterHtml(Class clz, String controllerInfo, String parameterName, String paramterFlag, String localIn, boolean isParamNull) {
		if(CommonUtil.isModel(clz)) {
			String[] clzPathItem = clz.toString().split("class")[1].split("\\.");
			String projectPath = controllerInfo.split(clzPathItem[0].trim())[0];
			String clzPath = projectPath;
			for(int i=0; i<clzPathItem.length; i++) {
				if(i > 0) {
					clzPath += File.separator;
				}
				clzPath += clzPathItem[i].trim();
			}
            clzPath += ".java";

			return classParameterHtml(clz, clzPath, paramterFlag, localIn);
		} else {
			String html = "<tr><td>"
					+ paramterFlag + parameterName
					+ "</td><td>"
					+ localIn
					+ "</td><td>"
					+ clz.getName().substring(clz.getName().lastIndexOf(".")+1)
					+ "</td><td>"
					+ (isParamNull?"是":"否")
					+ "</td><td></td></tr>";

			return html;
		}
	}

	/**
	 * 处理实体对象
	 * @param clz 入参对象类
     * @param clzPath 类文件路径
	 * @param flag 入参对象名
	 * @return html
	 */
	private String classParameterHtml(Class clz,String clzPath, String flag, String localIn) {
		StringBuffer html = new StringBuffer();

		List<Field> fields = new ArrayList<>();
		for(;clz != Object.class; clz = clz.getSuperclass()) {
			if(!clz.getName().equals(BaseBean.class.getName())) {
				fields.addAll(Arrays.asList(clz.getDeclaredFields()));
			}
		}

        String code = "";
        try {
            code = FileUtil.readFile(clzPath, "UTF8");
        } catch (Exception e) {
        }

		for(Field field : fields) {
			if("serialVersionUID".equals(field.getName()) || field.toString().indexOf(" static ") != -1 || field.toString().indexOf(" final ") != -1) {
				continue;
			}

			if (field.getType().getName().indexOf("java.lang") == -1 && field.getType().getName().indexOf("java.util") == -1
					&& !"int".equals(field.getType().getName()) && !"long".equals(field.getType().getName()) && !"boolean".equals(field.getType().getName())) {
				html.append(classParameterHtml(field.getType(), "", flag + field.getName() + ".", localIn));
				continue;
			}

			html.append("<tr><td>")
				.append(flag).append(field.getName()).append("</td><td>")
                .append(localIn).append("</td><td>")
				.append(field.getType().toString().substring(field.getType().toString().lastIndexOf(".")+1)).append("</td>");

            String isNotNull = "否";
            for(Annotation an : field.getAnnotations()) {
                if(an instanceof NotNull) {
                    isNotNull = "是";
                    break;
                }
            }
            html.append("<td>").append(isNotNull).append("</td>");


            String des = "";
            Matcher classMatcher = Pattern.compile("private\\s+.+\\s+"+field.getName()+"\\s*\\;.*[\\s\\S]").matcher(code);
            if (classMatcher.find()) {
                String matcherStr = classMatcher.group(0);
                if(matcherStr.indexOf("//") != -1) {
                    des = matcherStr.split("//")[1].replaceAll("\n","").replaceAll("\r","");
                }
            }
            html.append("<td>").append(des).append("</td></tr>");
		}

		return html.toString();
	}

	/**
	 * 获取方法体
	 * @param html 完整java代码
	 * @param mehtodName 方法名
	 * @return 方法体string
	 */
	private String getMethodBody(String html, String mehtodName) {
		//截取方法头
		Matcher methodBodyMatcher = Pattern.compile("public\\s+.*\\s+"+mehtodName+"\\s*\\(.*\\)").matcher(html);
		String methodHead = "";
		if (methodBodyMatcher.find()) {
			methodHead = methodBodyMatcher.group(0);
			html = html.substring(html.indexOf(methodHead));
		}

		//成对查找花括号，知道花括号数量相等，则表示方法结束
		int begIndex = html.indexOf("{");
		int endIndex = html.indexOf("}");

		String tempHtml = html.substring(begIndex + 1, endIndex);
		int kNum = StringUtil.counter(tempHtml, '{');
		int bNum = StringUtil.counter(tempHtml, '}');

		while (kNum != bNum) {
			endIndex = endIndex + 1 + html.substring(endIndex + 1).indexOf("}");
			tempHtml = html.substring(begIndex + 1, endIndex);

			kNum = StringUtil.counter(tempHtml, '{');
			bNum = StringUtil.counter(tempHtml, '}');
		}

		return methodHead + "{" + tempHtml + "}";
	}

	/**
	 * 获取方法的return字符串
	 * @param methodBody 方法体
	 * @return
     */
	private String getMethodReturn(String methodBody) {
		//截取return
		String[] returns = methodBody.split("return");
		String returnStr = returns[returns.length-1].replaceAll("\n", "").trim();

		// return的内容
		return returnStr.split(";")[0].trim();
	}

	/**
	 * 获取import的对象的字符串
	 * @param clzBody	类的java全部代码
	 * @param clzName	要查找的类名称
     * @return	类的完整名称(包名.类名) 没查找到返回""
     */
	private String getImportClz(String clzBody, String clzName) {
		Matcher paramImportMatcher = Pattern.compile("import\\s+.+" + clzName + "\\;").matcher(clzBody);
		if(paramImportMatcher.find()) {
			return paramImportMatcher.group(0).replace("import", "").replace(";", "").trim();
		}

		return "";
	}

	/**
	 * 获取当前类的引用的其他类
	 * @param clzBody	类的java全部代码
	 * @return	<引用类的自定义名称，引用类的包名.类名>
     */
	public Map<String, String> getPublicVariable(String clzBody) {
		String regex = "public\\s+.*\\s+.*\\s*;";
		Matcher publicServiceMatcher = Pattern.compile(regex).matcher(clzBody);

		Map<String, String> publicServices = new HashMap<>();
		while (publicServiceMatcher.find()) {
			String[] service = publicServiceMatcher.group(0).replace(";", "").split(" ");

			String serviceClz = service[1].trim();		//类对象
			String serviceClzName = service[2].trim();	//类自定义名

			// 查找import的对象的字符串
			String serviceClzPath = this.getImportClz(clzBody, serviceClz);

			if(!"".equals(serviceClzPath)) {
				publicServices.put(serviceClzName, serviceClzPath);
			}
		}

		return publicServices;
	}
	
}
