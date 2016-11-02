package com.fastjava.support.html;

/*
<div class="infoPage">
	<input type="hidden" id="controllerChkHiden" name="controllerChkHiden" />
	<input type="hidden" id="bodyHTML" name="bodyHTML" />

	<div class="path content">
			controller文件路径：<input class="xmlPath" id="controllerPath" name="controllerPath" type="text"/>
			<button onclick="setChkControll('apiHelper_getController')">确定</button>
	</div>

	<div class="content" id="setDiv" style="height:175px">
		<div class="title">
			<span>设置</span>
			<span style="padding-left:821px;">
				<button onclick="setChkControll('apiHelper_createDoc')">生成 api</button>
			</span>
		</div>
		<div>
			<div class="divPadding">
				<p>url公共路径：<input type="text" id="publicUrl" name="publicUrl" /></p>
				<span>
					controller类：
					<div id="controllerDiv">
					</div>
				</span>
			</div>
		</div>
	</div>

	<div class="content" id="contentDiv" style="height:80px">
		<div id="titleDiv" class="title">
			<span>文档预览</span>
			<span style="padding-left:800px;">
				<button onclick="setChkControll('apiHelper_saveAsHTML')">HTML</button>
			</span>
		</div>
		<div class="divPadding" id="docDiv"></div>
	</div>

</div>

<script>
var controllerPath;
var controllerMeta;
var publicUrl;
var doc;

function onloadFunction() {
	//controller路径
	document.getElementById("controllerPath").value = typeof(controllerPath)=="undefined"?"":controllerPath;

	//action名称选项
	var inputs = "";
	if(typeof(controllerPath)!="undefined") {
		for(var i=0; i<controllerMeta.length; i++) {
			var checked = "";
			if(controllerMeta[i][0] == true) {
				checked = "checked";
			}

			if(inputs != "") {
				inputs += "<br/>";
			}
			inputs += '<input type="checkbox" name="controllerChk" value="'+ controllerMeta[i][2] +'" '+ checked +'/>' + controllerMeta[i][1];
		}

		if(controllerMeta.length > 1) {
			document.getElementById("setDiv").style.height = document.getElementById("setDiv").offsetHeight + controllerMeta.length * 21 + "px";
		}
	}
	document.getElementById("controllerDiv").innerHTML = inputs;

	if(typeof(publicUrl)!="undefined") {
		document.getElementById("publicUrl").value = publicUrl;
	}

	//doc文档
	if(typeof(doc)!="undefined") {
		document.getElementById("docDiv").innerHTML = doc;
	}

	var jsonPres = document.getElementsByName("jsonPre");
	for(var i=0; i<jsonPres.length ;i++) {
		if("" != jsonPres[i].innerHTML) {
			jsonPres[i].innerHTML = JSON.stringify(JSON.parse(jsonPres[i].innerHTML), null, "\t");
		}
	}

	document.getElementById("contentDiv").style.height = document.getElementById("titleDiv").offsetHeight + document.getElementById("docDiv").offsetHeight + "px";
}

//读取项目路径
function readPath() {
	setChkControll('apiHelper');
}

//生成文档
function setChkControll(mehtod) {
	var chkStr = "";
	var checkController = document.getElementsByName("controllerChk");
	for(var i=0; i<checkController.length; i++) {
		if(checkController[i].checked) {
			if(chkStr != "") {
				chkStr += ",";
			}
			chkStr += checkController[i].value;
		}
	}

	document.getElementById("controllerChkHiden").value = chkStr;
	document.getElementById("bodyHTML").value = document.getElementById("docDiv").innerHTML;

	doFastJava(mehtod); //读取项目路径
}
</script>
<style>
.infoPage {
	margin-left: auto;
	margin-right:auto;
	width:970px;
}
.content {
	background-color: white;
	height:510px;
	color:#4E4E4E;
	margin-bottom: 20px;
}
.path {
	height:35px;
	padding-top:10px;
	padding-left:35px;
}
.xmlPath {
	width: 670px;
}
.title {
	height:33px;
	border-bottom:1px solid rgba(0,0,0,.15);
	padding-top:10px;
	padding-left:10px;
	font-size:16px;
}
.divPadding {
	padding: 15px 35px 0px;
}
#docDiv {
	background-color: white;
}
pre {
	border:1px solid #ccc;
	background-color:#F8F8F8;
	line-height:20px;
	padding:5px 10px;
	margin:0px;
	font-family:Microsoft Yahei,Helvetica Neue,Hiragino Sans GB,WenQuanYi Micro Hei,sans-serif;
}
table {
	width:100%;
}
th {
	background-color: #ECEBEB;
}
</style>
 */
public class ApiHelperHtml {

    public String html() {
        StringBuffer sb = new StringBuffer();
        String newLine = System.getProperty("line.separator");

        sb.append(newLine).append("<div class=\"infoPage\">")
                .append(newLine).append("	<input type=\"hidden\" id=\"controllerChkHiden\" name=\"controllerChkHiden\" />")
                .append(newLine).append("	<input type=\"hidden\" id=\"bodyHTML\" name=\"bodyHTML\" />")
                .append(newLine).append("")
                .append(newLine).append("	<div class=\"path content\">")
                .append(newLine).append("			controller文件路径：<input class=\"xmlPath\" id=\"controllerPath\" name=\"controllerPath\" type=\"text\"/>")
                .append(newLine).append("			<button onclick=\"setChkControll('apiHelper_getController')\">确定</button>")
                .append(newLine).append("	</div>")
                .append(newLine).append("")
                .append(newLine).append("	<div class=\"content\" id=\"setDiv\" style=\"height:175px\">")
                .append(newLine).append("		<div class=\"title\">")
                .append(newLine).append("			<span>设置</span>")
                .append(newLine).append("			<span style=\"padding-left:821px;\">")
                .append(newLine).append("				<button onclick=\"setChkControll('apiHelper_createDoc')\">生成 api</button>")
                .append(newLine).append("			</span>")
                .append(newLine).append("		</div>")
                .append(newLine).append("		<div>")
                .append(newLine).append("			<div class=\"divPadding\">")
                .append(newLine).append("				<p>url公共路径：<input type=\"text\" id=\"publicUrl\" name=\"publicUrl\" /></p>")
                .append(newLine).append("				<span>")
                .append(newLine).append("					controller类：")
                .append(newLine).append("					<div id=\"controllerDiv\">")
                .append(newLine).append("					</div>")
                .append(newLine).append("				</span>")
                .append(newLine).append("			</div>")
                .append(newLine).append("		</div>")
                .append(newLine).append("	</div>")
                .append(newLine).append("")
                .append(newLine).append("	<div class=\"content\" id=\"contentDiv\" style=\"height:80px\">")
                .append(newLine).append("		<div id=\"titleDiv\" class=\"title\">")
                .append(newLine).append("			<span>文档预览</span>")
                .append(newLine).append("			<span style=\"padding-left:800px;\">")
                .append(newLine).append("				<button onclick=\"setChkControll('apiHelper_saveAsHTML')\">HTML</button>")
                .append(newLine).append("			</span>")
                .append(newLine).append("		</div>")
                .append(newLine).append("		<div class=\"divPadding\" id=\"docDiv\"></div>")
                .append(newLine).append("	</div>")
                .append(newLine).append("	")
                .append(newLine).append("</div>")
                .append(newLine).append("")
                .append(newLine).append("<script>")
                .append(newLine).append("var controllerPath;")
                .append(newLine).append("var controllerMeta;")
                .append(newLine).append("var publicUrl;")
                .append(newLine).append("var doc;")
                .append(newLine).append("")
                .append(newLine).append("function onloadFunction() {")
                .append(newLine).append("	//controller路径")
                .append(newLine).append("	document.getElementById(\"controllerPath\").value = typeof(controllerPath)==\"undefined\"?\"\":controllerPath;")
                .append(newLine).append("	")
                .append(newLine).append("	//action名称选项")
                .append(newLine).append("	var inputs = \"\";")
                .append(newLine).append("	if(typeof(controllerPath)!=\"undefined\") {")
                .append(newLine).append("		for(var i=0; i<controllerMeta.length; i++) {")
                .append(newLine).append("			var checked = \"\";")
                .append(newLine).append("			if(controllerMeta[i][0] == true) {")
                .append(newLine).append("				checked = \"checked\";")
                .append(newLine).append("			}")
                .append(newLine).append("		")
                .append(newLine).append("			if(inputs != \"\") {")
                .append(newLine).append("				inputs += \"<br/>\";")
                .append(newLine).append("			}")
                .append(newLine).append("			inputs += '<input type=\"checkbox\" name=\"controllerChk\" value=\"'+ controllerMeta[i][2] +'\" '+ checked +'/>' + controllerMeta[i][1];")
                .append(newLine).append("		}")
                .append(newLine).append("		")
                .append(newLine).append("		if(controllerMeta.length > 1) {")
                .append(newLine).append("			document.getElementById(\"setDiv\").style.height = document.getElementById(\"setDiv\").offsetHeight + controllerMeta.length * 21 + \"px\";")
                .append(newLine).append("		}")
                .append(newLine).append("	}")
                .append(newLine).append("	document.getElementById(\"controllerDiv\").innerHTML = inputs;")
                .append(newLine).append("	")
                .append(newLine).append("	if(typeof(publicUrl)!=\"undefined\") {")
                .append(newLine).append("		document.getElementById(\"publicUrl\").value = publicUrl;")
                .append(newLine).append("	}")
                .append(newLine).append("	")
                .append(newLine).append("	//doc文档")
                .append(newLine).append("	if(typeof(doc)!=\"undefined\") {")
                .append(newLine).append("		document.getElementById(\"docDiv\").innerHTML = doc;")
                .append(newLine).append("	}")
                .append(newLine).append("	")
                .append(newLine).append("	var jsonPres = document.getElementsByName(\"jsonPre\");")
                .append(newLine).append("	for(var i=0; i<jsonPres.length ;i++) {")
                .append(newLine).append("		if(\"\" != jsonPres[i].innerHTML) {")
                .append(newLine).append("			jsonPres[i].innerHTML = JSON.stringify(JSON.parse(jsonPres[i].innerHTML), null, \"\t\");")
                .append(newLine).append("		}")
                .append(newLine).append("	}")
                .append(newLine).append("	")
                .append(newLine).append("	document.getElementById(\"contentDiv\").style.height = document.getElementById(\"titleDiv\").offsetHeight + document.getElementById(\"docDiv\").offsetHeight + \"px\";")
                .append(newLine).append("}")
                .append(newLine).append("")
                .append(newLine).append("//读取项目路径")
                .append(newLine).append("function readPath() {")
                .append(newLine).append("	setChkControll('apiHelper');")
                .append(newLine).append("}")
                .append(newLine).append("")
                .append(newLine).append("//生成文档")
                .append(newLine).append("function setChkControll(mehtod) {")
                .append(newLine).append("	var chkStr = \"\";")
                .append(newLine).append("	var checkController = document.getElementsByName(\"controllerChk\");")
                .append(newLine).append("	for(var i=0; i<checkController.length; i++) {")
                .append(newLine).append("		if(checkController[i].checked) {")
                .append(newLine).append("			if(chkStr != \"\") {")
                .append(newLine).append("				chkStr += \",\";")
                .append(newLine).append("			}")
                .append(newLine).append("			chkStr += checkController[i].value;")
                .append(newLine).append("		}")
                .append(newLine).append("	}")
                .append(newLine).append("	")
                .append(newLine).append("	document.getElementById(\"controllerChkHiden\").value = chkStr;")
                .append(newLine).append("	document.getElementById(\"bodyHTML\").value = document.getElementById(\"docDiv\").innerHTML;")
                .append(newLine).append("	")
                .append(newLine).append("	doFastJava(mehtod); //读取项目路径")
                .append(newLine).append("}")
                .append(newLine).append("</script>")
                .append(newLine).append("<style>")
                .append(newLine).append(".infoPage {")
                .append(newLine).append("	margin-left: auto;")
                .append(newLine).append("	margin-right:auto;")
                .append(newLine).append("	width:970px;")
                .append(newLine).append("}")
                .append(newLine).append(".content {")
                .append(newLine).append("	background-color: white;")
                .append(newLine).append("	height:510px;")
                .append(newLine).append("	color:#4E4E4E;")
                .append(newLine).append("	margin-bottom: 20px;")
                .append(newLine).append("}")
                .append(newLine).append(".path {")
                .append(newLine).append("	height:35px;")
                .append(newLine).append("	padding-top:10px;")
                .append(newLine).append("	padding-left:35px;")
                .append(newLine).append("}")
                .append(newLine).append(".xmlPath {")
                .append(newLine).append("	width: 670px;")
                .append(newLine).append("}")
                .append(newLine).append(".title {")
                .append(newLine).append("	height:33px;")
                .append(newLine).append("	border-bottom:1px solid rgba(0,0,0,.15);")
                .append(newLine).append("	padding-top:10px;")
                .append(newLine).append("	padding-left:10px;")
                .append(newLine).append("	font-size:16px;")
                .append(newLine).append("}")
                .append(newLine).append(".divPadding {")
                .append(newLine).append("	padding: 15px 35px 0px;")
                .append(newLine).append("}")
                .append(newLine).append("#docDiv {")
                .append(newLine).append("	background-color: white;")
                .append(newLine).append("}")
                .append(newLine).append("pre {")
                .append(newLine).append("	border:1px solid #ccc;")
                .append(newLine).append("	background-color:#F8F8F8;")
                .append(newLine).append("	line-height:20px;")
                .append(newLine).append("	padding:5px 10px;")
                .append(newLine).append("	margin:0px;")
                .append(newLine).append("	font-family:Microsoft Yahei,Helvetica Neue,Hiragino Sans GB,WenQuanYi Micro Hei,sans-serif;")
                .append(newLine).append("}")
                .append(newLine).append("table {")
                .append(newLine).append("	width:100%;")
                .append(newLine).append("}")
                .append(newLine).append("th {")
                .append(newLine).append("	background-color: #ECEBEB;")
                .append(newLine).append("}")
                .append(newLine).append("</style>");


        return sb.toString();
    }
}
