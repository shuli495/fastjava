package com.fastjavaframework.support.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fastjavaframework.base.BaseBean;
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
@SuppressWarnings("unchecked")
public class APIHelper {
	
	//读取spring配置文件路径
	public Map<String,String> readPath(String projectPath) {
		Map<String,String> replaceMap = new HashMap<>();
		
		if(VerifyUtils.isEmpty(projectPath)) {
			return replaceMap;
		}
		
		//遍历项目子目录
		List<String> paths = FileUtil.iterator(projectPath, "folder", "path", true);
		for(String path : paths) {
			String rePath = "\"" + path.replaceAll("\\\\", "\\\\\\\\") + "\"";
			
			if(path.indexOf("action") != -1 && path.indexOf("class") == -1) {
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
			Matcher matcher = Pattern.compile("package\\s+.+\\s*;").matcher(javaCode);
			if (matcher.find()) {
				packageName = matcher.group(0);
			}
			String[] paths = controller.split("\\\\");
			
			// class包名+类名
			String className = packageName.replace("package", "").replace(";", "").trim() 
					+ "." + paths[paths.length - 1].split("\\.")[0];
			
			//当前java类对象
			Class clz = null;
			try {
				clz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				continue;
			}

			//类注释
			String clzNote = "";
			Matcher classMatcher = Pattern.compile("public\\s+class\\s+.+\\{").matcher(javaCode);
			if (classMatcher.find()) {
				int endIndex = javaCode.indexOf(classMatcher.group(0));
				
				String[] classNotes = javaCode.substring(packageName.length(), endIndex).replaceAll("\\/", "").split("\\*");
				for(String classNote : classNotes) {
					//排除import，注释
					if(!"".equals(classNote.trim()) && !classNote.trim().startsWith("import") && !classNote.trim().startsWith("@")) {
						clzNote = classNote.replace("\n", "");
					}
				}
				
				javaCode = javaCode.substring(endIndex);
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
			Pattern leftpattern = Pattern.compile("/\\*");
			Matcher leftmatcher = leftpattern.matcher(javaCode);
			Pattern rightpattern = Pattern.compile("\\)\\s*\\{");
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
				StringBuffer paramterJson = new StringBuffer();	//入参json

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
					
					//非4中参数不展示
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
						} catch (ClassNotFoundException e) {
						}
					} else if(paramterClz.isArray()) {	//数组
						try {
							paramterGenerosity = parameterClzStr.replace("[]","");
							paramClzForHtml = Class.forName(paramterGenerosity);
							paramFlag = "list.";
						} catch (ClassNotFoundException e) {
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
						paramterJson.append(JSON.toJSON(CommonUtil.setDefValue(paramterClz, paramterGenerosityClz)).toString());
					}
				}
				/*----------------------------入参 end------------------------------*/
				


				/*----------------------------出参 begin------------------------------*/
				String returnJson = "{}";	//出参json
				Class returnClass = method.getReturnType();	//出参class
				
				//无出参
				if(Void.class.getName().equals(returnClass.getName())) {
					returnJson = "{}";
				} else if(Result.class.getName().equals(returnClass.getName())) {	//Relult类型行出参
					//方法体
					String methodBody = getMethodBody(javaCode, method.getName());
					
					//截取return
					String[] returns = methodBody.split("return");
					for(int i=1 ; i<returns.length; i++) {
						String returnStr = returns[i].replaceAll("\n", "").trim();
						if(VerifyUtils.isEmpty(returnStr)) {
							continue;
						}
						
						returnStr = returnStr.split(";")[0];
						
						//返回方法名
						String returnMethodName = returnStr.substring(0, returnStr.indexOf("("));
						//返回方法参数
						String returnMethodParamStr = returnStr.substring(returnStr.indexOf("(")+1);
						
						//返回方法参数名
						String[] returnMethodParamNames = new String[]{};
						if(returnMethodParamStr.length() > 1) {
							returnMethodParamNames = returnMethodParamStr.substring(0, returnMethodParamStr.length() - 1).split("\\,");
						}

						Class[] returnClzs = new Class[returnMethodParamNames.length];	//返回方法参数类
						Object[] returnObjs = new Object[returnMethodParamNames.length];//返回方法参数对象
						
						//返回方法无参数
						if(returnMethodParamNames.length == 0) {
							try {
								//返回json(直接调用类中返回方法)
								returnJson = JSON.toJSON(clz.getMethod(returnMethodName).invoke(returnMethodName)).toString();
							} catch (Exception e) {
							}
						} else {	//返回方法有参数
							for(int j=0; j < returnMethodParamNames.length; j++) {
								String returnMethodParamName = returnMethodParamNames[j].trim();
								
								//参数是调用泛型service中方法
								if(returnMethodParamName.startsWith("this.service.") || returnMethodParamName.startsWith("super.service.") || returnMethodParamName.startsWith("service.")) {
									try {
										//调用service中方法的方法名
										String[] clzMethodItems = returnMethodParamName.split("\\(")[0].split("\\.");
										String servicMethodName = clzMethodItems[clzMethodItems.length-1];
										
										//service class对象
										Class serviceClz = (Class) ((ParameterizedType)clz.getGenericSuperclass()).getActualTypeArguments()[0];
										
										for(Method clzMethod : serviceClz.getMethods()) {
											if(clzMethod.getName().equals(servicMethodName)) {
												//service泛型中的实体对象
												Class methodGenerosity = (Class) ((ParameterizedType)serviceClz.getGenericSuperclass()).getActualTypeArguments()[1];

                                                returnClzs[j] = Object.class;
												if(BaseBean.class.isAssignableFrom(clzMethod.getReturnType())) {
                                                    returnObjs[j] = CommonUtil.setDefValue(methodGenerosity, null);
												} else {
                                                    returnObjs[j] = CommonUtil.setDefValue(clzMethod.getReturnType(), methodGenerosity);
                                                }
												break;
											}
										}
									} catch (Exception e) {
									}
								}
								
								//过滤框架中返回success("","")方法
								if("success".equals(returnMethodName) && returnMethodParamNames.length > 1 && j != 0) {
									returnClzs[j] = String.class;
									returnObjs[j] = returnObjs[0];
									break;
								}
								
								//参数是字符串 直接返回字符串内容
								if(returnMethodParamName.startsWith("\"") && returnMethodParamName.endsWith("\"")) {
									returnClzs[j] = String.class;
									returnObjs[j] = returnMethodParamName.replaceAll("\"", "");
									continue;
								}
								
								//参数类名
								String paramClzName = "";
								Matcher paramMatcher = Pattern.compile(".+\\s+" + returnMethodParamName + "\\s*(\\)|\\=|\\;)").matcher(methodBody);
								if (paramMatcher.find()) {
									String[] paramClzNames = paramMatcher.group(0).split(returnMethodParamName)[0].trim().split(" ");
									paramClzName = paramClzNames[paramClzNames.length - 1];
								}
								
								//查找到参数的类名 说明返回的是对象，否则调用的方法
								if(!"".equals(paramClzName)) {	//返回对象
									
									//返回类型为String 直接返回
									if("String".equals(paramClzName)) {
										returnClzs[j] = Object.class;
										returnObjs[j] = "String";
										continue;
									} else if("Integer".equals(paramClzName) || "int".equals(paramClzName)) {	//int返回0
										returnClzs[j] = Object.class;
										returnObjs[j] = 0;
										continue;
									}
									
									//参数class名
									Matcher paramImportMatcher = Pattern.compile("import\\s+.+" + paramClzName + "\\;").matcher(oldCode);
									returnClzs[j] = Object.class;
									if(paramImportMatcher.find()) {
										try {
											//返回参数对象
											returnObjs[j] = CommonUtil.setDefValue(Class.forName(paramImportMatcher.group(0).replace("import", "").replace(";", "").trim()),null);
										} catch (ClassNotFoundException e) {
										}
									}
									
								} else {	//调用方法
									
									//包含点 调用其它类中的方法，否则调用本类中的方法
									if(returnMethodParamName.indexOf(".") != -1) {	//调用其他类的方法
										//查找调用方法所属的类名
										Matcher paramImportClzMatcher = Pattern
																		.compile(".+\\s+" + returnMethodParamName.split("\\.")[0].trim() + "\\s*(\\(|\\=|\\;|\\))")
																		.matcher(methodBody);
										if(paramImportClzMatcher.find()) {
                                            String matcherStr = paramImportClzMatcher.group(0).trim();
                                            String[] paramClzs = matcherStr.split(returnMethodParamName.split("\\.")[0].trim())[0].split(" ");

                                            String paramClz = "";
                                            for(int k=paramClzs.length - 1;k>0;k--) {
                                                if(!"".equals(paramClzs[k])) {
                                                    paramClz = paramClzs[k];
                                                    break;
                                                }
                                            }

                                            if(!"".equals(paramClz)) {
                                                //查找调用方法所属类的class
                                                Matcher paramImportMatcher = Pattern
                                                        .compile("import\\s+.+" + paramClz + "\\;")
                                                        .matcher(oldCode);

                                                if(paramImportMatcher.find()) {
                                                    paramClzName = paramImportMatcher.group(0).replace("import", "").replace(";","").trim();
                                                    try {
                                                        //调用方法的返回值类型
                                                        Class paramClzObj = Class.forName(paramClzName).getMethod(returnMethodParamName.split("\\.")[1].replace("()","").trim()).getReturnType();
                                                        returnClzs[j] = paramClzObj;
                                                        returnObjs[j] = CommonUtil.setDefValue(paramClzObj,null);
                                                    } catch (Exception e) {
                                                    }
                                                }
                                            }
										}
									} else {	//调用本类的方法
										try {
											paramClzName = clz.getMethod(returnMethodParamName).getReturnType().toString();
											returnClzs[j] = Object.class;
											returnObjs[j] = CommonUtil.setDefValue(Class.forName(paramClzName),null);
										} catch (Exception e) {
										}
									}
								}
							
							}}

						//调用返回方法  返回json
						try {
							//计算非null参数的最大个数
							int returnClzsMaxSize = 0;
							for(Class reClz : returnClzs) {
								if(null == reClz) {
									continue;
								}
								returnClzsMaxSize++;
							}
							
							Class[] realReturnClzs = new Class[returnClzsMaxSize];
							Object[] realReturnObjs = new Object[returnClzsMaxSize];

							//过滤null参数
                            int idx = 0;
							for (int k = 0; k < returnClzs.length; k++) {
								if(null == returnClzs[k]) {
									continue;
								}
								realReturnClzs[idx] = returnClzs[k];
								realReturnObjs[idx] = returnObjs[k];
                                idx++;
							}
							
							//调用方法
							returnJson = JSON.toJSON(clz.getMethod(returnMethodName, realReturnClzs).invoke(clz.newInstance(), realReturnObjs)).toString();
						} catch (Exception e) {
							returnJson = "{}";
						}
					}
				} else {	//非Result类型出参
					returnJson = JSON.toJSON(CommonUtil.setDefValue(returnClass, null)).toString();
				}
				/*----------------------------出参 end------------------------------*/
				

				String methodHtml = notes.get(method.getName())+"</h3>"
						+"<pre><b>"+requestType +"</b>&nbsp;&nbsp;"+classUrl+methodUrl+"</pre>"
						+"<p>请求参数：</p>"
						+"<table border=\"1\" cellspacing=\"0\" cellpadding=\"8\">"
						+"<tr><th>参数名</th><th>位于</th><th>数据类型</th><th>必填</th><th>描述</th></tr>"
						+ paramterHtml.toString()
						+"</table>"
						+"<p>请求 json：</p>"
						+"<pre name=\"jsonPre\">" + ("".equals(paramterJson.toString()) ? "{}" : paramterJson) + "</pre>"
						+"<p>返回 json：</p>"
						+"<pre name=\"jsonPre\">" + ("".equals(returnJson.toString()) ? "{}" : returnJson) + "</pre></br>";
				
				methodIndx.put(javaCode.indexOf(method.getName()+"("), methodHtml);
			}
			/*******************************方法注释 入参 出参 end***********************************/
			
			
			//方法排序(class.getMethod无序，按代码正确顺序排序)
			String[] methods = new String[methodIndx.size()];
			for(Integer i : methodIndx.keySet()) {
				int smallNum = 0;
				for(Integer j : methodIndx.keySet()) {
					if(i != j && i > j) {
						smallNum++;
					}
				}
				
				methods[smallNum] = "<h3>"+(classIndex+1)+"."+(smallNum+1)+" "+methodIndx.get(i);
			}
			
			//返回html
			StringBuffer reStr = new StringBuffer();
			for(String method : methods) {
				reStr.append(method);
			}
			returnMap.put("doc", "'<h2>"+(classIndex+1) + clzNote + "</h2>" + reStr.toString() + "'");
		}
		return returnMap;
	}
	
	/**
	 * 保存成html
	 * @param html
	 */
	public String saveAsHTML(String html) {
        try { 
			String tempHtml = FileUtil.readFile(this.getClass(), "../html/apiHelper_template.html");
			
			File file = File.createTempFile("api_doc", ".html");
			file.deleteOnExit();
	
			BufferedWriter output = new BufferedWriter(new FileWriter(file));  
			output.write(tempHtml.replace("api_doc_html", html.substring(1, html.length() - 1)));  
			output.close();
	         
	         return file.getAbsolutePath();
        } catch (Exception e) {  
        	e.printStackTrace();
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
		if(clz.getName().indexOf("java.lang") == -1) {
			String[] clzPathItem = clz.toString().split("class")[1].split("\\.");
			String projectPath = controllerInfo.split("\\" + File.separator + clzPathItem[0].trim() + File.separator + "\\")[0];
			String clzPath = projectPath;
			for(String clzPathStr : clzPathItem) {
				clzPath += File.separator + clzPathStr.trim();
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

			if (field.getType().getName().indexOf("java.lang") == -1 && !"int".equals(field.getType().getName()) && !"long".equals(field.getType().getName())) {
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
		Matcher methodBodyMatcher = Pattern.compile("public\\s+Result\\s+"+mehtodName+"\\s*\\(.*\\)").matcher(html);
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
	
}
