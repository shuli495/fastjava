package com.fastjavaframework.support.html;

/*
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
body {
	background : #F5F5F5;
	margin: 0;
	font-family:Microsoft Yahei,Helvetica Neue,Hiragino Sans GB,WenQuanYi Micro Hei,sans-serif;
}
#navigation {
	background-color: white;
	-webkit-box-shadow: 0 0 2px 0 rgba(0,0,0,.5);
	box-shadow:0px 0px 2px 0px rgba(0,0,0,0.5);
	height: 50px;
	padding-left:10%;
	line-height:50px;
}
#navigation > font {
	font-size:28px;
	font-weight:bold;
	color:#404040;
}
#navigation > a {
	margin-left:50px;
	font-size:16px;
	text-decoration:none;
	color:#383838;
}
#navigation > a:hover {
	color:#3075DD;
}
#subPage {
	position:absolute;
	width:100%;
}
.pathDiv {
	background-color:white;
	margin:50px auto 30px auto;
	padding-top:10px;
	padding-left:20px;
	height:40px;
	width:950px;
	border-bottom:1px solid rgba(0,0,0,.15);
	font-size:18px;
}
.pathDiv>input {
	width:790px;
	height:20px;
}
.pathDiv>button {
	font-size:13px;
}
</style>
</head>

<body>
<div id="navigation">
<font>FastJava</font>
<a href="fastjava?methodType=mapperHelper" id="mapperHelper">类生成器</a>
<a href="fastjava?methodType=apiHelper" id="apiHelper">API生成器</a>
<a href="fastjava?methodType=moduleHelper" id="mooduleHelper">模块管理</a>
<a href="fastjava?methodType=cacheHelper" id="cacheHelper">缓存</a>
<a href="fastjava?methodType=logHelper" id="logHelper">日志</a>
<a href="fastjava?methodType=quartzHelper" id="quartzHelper">定时任务</a>
<a href="fastjava?methodType=deploymentHelper" id="deploymentHelper">发版优化</a>
</div>

<form action="fastjava" id="myForm" method="post">
<input type="hidden" value="" id="methodType" name="methodType" text="后台调用方法" />
	<div class="pathDiv">
		项目路径&nbsp;&nbsp;<input type="text" id="projectPath" name="projectPath"/>&nbsp;&nbsp;
		<button id="selectPath" onclick="readPath();">确定</button>
	</div>
	<div id="subPage">
	</div>
</form>
<script>
var projectPath;
window.onload = function() {
	document.getElementById("projectPath").value = typeof(projectPath)=="undefined"?"":projectPath;

	var navigationA = document.getElementById("navigation").getElementsByTagName("a");
	for(var i=0; i<navigationA.length; i++) {
		navigationA[i].href = navigationA[i].href + "&projectPath=" + document.getElementById("projectPath").value;
	}

	onloadFunction();
}

//调用servlet
function doFastJava(methodType) {
	document.getElementById("methodType").value = methodType;
	document.getElementById("myForm").submit();
}
</script>
</body>
</html>
 */
public class IndexHtml {

    public String html() {
        StringBuffer sb = new StringBuffer();
        String newLine = System.getProperty("line.separator");
        
        sb.append(newLine).append("<!DOCTYPE HTML>")
                .append(newLine).append("<html>")
                .append(newLine).append("<head>")
                .append(newLine).append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">")
                .append(newLine).append("<style>")
                .append(newLine).append("body {")
                .append(newLine).append("	background : #F5F5F5;")
                .append(newLine).append("	margin: 0;")
                .append(newLine).append("	font-family:Microsoft Yahei,Helvetica Neue,Hiragino Sans GB,WenQuanYi Micro Hei,sans-serif;")
                .append(newLine).append("}")
                .append(newLine).append("#navigation {")
                .append(newLine).append("	background-color: white;")
                .append(newLine).append("	-webkit-box-shadow: 0 0 2px 0 rgba(0,0,0,.5);")
                .append(newLine).append("	box-shadow:0px 0px 2px 0px rgba(0,0,0,0.5);")
                .append(newLine).append("	height: 50px;")
                .append(newLine).append("	padding-left:10%;")
                .append(newLine).append("	line-height:50px;")
                .append(newLine).append("}")
                .append(newLine).append("#navigation > font {")
                .append(newLine).append("	font-size:28px;")
                .append(newLine).append("	font-weight:bold;")
                .append(newLine).append("	color:#404040;")
                .append(newLine).append("}")
                .append(newLine).append("#navigation > a {")
                .append(newLine).append("	margin-left:50px;")
                .append(newLine).append("	font-size:16px;")
                .append(newLine).append("	text-decoration:none;")
                .append(newLine).append("	color:#383838;")
                .append(newLine).append("}")
                .append(newLine).append("#navigation > a:hover {")
                .append(newLine).append("	color:#3075DD;")
                .append(newLine).append("}")
                .append(newLine).append("#subPage {")
                .append(newLine).append("	position:absolute;")
                .append(newLine).append("	width:100%;")
                .append(newLine).append("}")
                .append(newLine).append(".pathDiv {")
                .append(newLine).append("	background-color:white;")
                .append(newLine).append("	margin:50px auto 30px auto;")
                .append(newLine).append("	padding-top:10px;")
                .append(newLine).append("	padding-left:20px;")
                .append(newLine).append("	height:40px;")
                .append(newLine).append("	width:950px;")
                .append(newLine).append("	border-bottom:1px solid rgba(0,0,0,.15);")
                .append(newLine).append("	font-size:18px;")
                .append(newLine).append("}")
                .append(newLine).append(".pathDiv>input {")
                .append(newLine).append("	width:790px;")
                .append(newLine).append("	height:20px;")
                .append(newLine).append("}")
                .append(newLine).append(".pathDiv>button {")
                .append(newLine).append("	font-size:13px;")
                .append(newLine).append("}")
                .append(newLine).append("</style>")
                .append(newLine).append("</head>")
                .append(newLine).append("")
                .append(newLine).append("<body>")
                .append(newLine).append("<div id=\"navigation\">")
                .append(newLine).append("<font>FastJava</font>")
                .append(newLine).append("<a href=\"fastjava?methodType=mapperHelper\" id=\"mapperHelper\">类生成器</a>")
                .append(newLine).append("<a href=\"fastjava?methodType=apiHelper\" id=\"apiHelper\">API生成器</a>")
                .append(newLine).append("<a href=\"fastjava?methodType=moduleHelper\" id=\"mooduleHelper\">模块管理</a>")
                .append(newLine).append("<a href=\"fastjava?methodType=cacheHelper\" id=\"cacheHelper\">缓存</a>")
                .append(newLine).append("<a href=\"fastjava?methodType=logHelper\" id=\"logHelper\">日志</a>")
                .append(newLine).append("<a href=\"fastjava?methodType=quartzHelper\" id=\"quartzHelper\">定时任务</a>")
                .append(newLine).append("<a href=\"fastjava?methodType=deploymentHelper\" id=\"deploymentHelper\">发版优化</a>")
                .append(newLine).append("</div>")
                .append(newLine).append("")
                .append(newLine).append("<form action=\"fastjava\" id=\"myForm\" method=\"post\">")
                .append(newLine).append("<input type=\"hidden\" value=\"\" id=\"methodType\" name=\"methodType\" text=\"后台调用方法\" />")
                .append(newLine).append("	<div class=\"pathDiv\">")
                .append(newLine).append("		项目路径&nbsp;&nbsp;<input type=\"text\" id=\"projectPath\" name=\"projectPath\"/>&nbsp;&nbsp;")
                .append(newLine).append("		<button id=\"selectPath\" onclick=\"readPath();\">确定</button>")
                .append(newLine).append("	</div>")
                .append(newLine).append("	<div id=\"subPage\">")
                .append(newLine).append("	</div>")
                .append(newLine).append("</form>")
                .append(newLine).append("<script>")
                .append(newLine).append("var projectPath;")
                .append(newLine).append("window.onload = function() {")
                .append(newLine).append("	document.getElementById(\"projectPath\").value = typeof(projectPath)==\"undefined\"?\"\":projectPath;")
                .append(newLine).append("")
                .append(newLine).append("	var navigationA = document.getElementById(\"navigation\").getElementsByTagName(\"a\");")
                .append(newLine).append("	for(var i=0; i<navigationA.length; i++) {")
                .append(newLine).append("		navigationA[i].href = navigationA[i].href + \"&projectPath=\" + document.getElementById(\"projectPath\").value;")
                .append(newLine).append("	}")
                .append(newLine).append("	")
                .append(newLine).append("	onloadFunction();")
                .append(newLine).append("}")
                .append(newLine).append("")
                .append(newLine).append("//调用servlet")
                .append(newLine).append("function doFastJava(methodType) {")
                .append(newLine).append("	document.getElementById(\"methodType\").value = methodType;")
                .append(newLine).append("	document.getElementById(\"myForm\").submit();")
                .append(newLine).append("}")
                .append(newLine).append("</script>")
                .append(newLine).append("</body>")
                .append(newLine).append("</html>");


        return sb.toString();
    }
}
