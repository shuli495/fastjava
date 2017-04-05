package com.fastjava.support.html;

/*
<div class="infoPage">
	<div class="classInfo">
		<div class="title">类信息</div>
		<div class="content">
			<div class="classDiv">
				Action：</br>
				<div class="inputDiv">命名空间&nbsp;<input type="text" id="actionNameSpace" name="actionNameSpace"/></div>
				<div class="inputDiv">文件路径&nbsp;<input type="text" id="actionPath" name="actionPath"/></div>
			</div>
			<div class="classDiv">
				Service：</br>
				<div class="inputDiv">命名空间&nbsp;<input type="text" id="serviceNameSpace" name="serviceNameSpace"/></div>
				<div class="inputDiv">文件路径&nbsp;<input type="text" id="servicePath" name="servicePath"/></div>
			</div>
			<div class="classDiv">
				Dao：</br>
				<div class="inputDiv">命名空间&nbsp;<input type="text" id="daoNameSpace" name="daoNameSpace"/></div>
				<div class="inputDiv">文件路径&nbsp;<input type="text" id="daoPath" name="daoPath"/></div>
			</div>
			<div class="classDiv">
				BO：</br>
				<div class="inputDiv">命名空间&nbsp;<input type="text" id="boNameSpace" name="boNameSpace"/></div>
				<div class="inputDiv">文件路径&nbsp;<input type="text" id="boPath" name="boPath"/></div>
			</div>
			<div class="classDiv">
				VO：</br>
				<div class="inputDiv">命名空间&nbsp;<input type="text" id="voNameSpace" name="voNameSpace"/></div>
				<div class="inputDiv">文件路径&nbsp;<input type="text" id="voPath" name="voPath"/></div>
			</div>
			<div class="classDiv">
				MapperXML：</br>
				<div class="inputDiv">文件路径&nbsp;<input type="text" id="mapperPath" name="mapperPath"/></div>
			</div>
			<div class="classDiv">
				ValidationMessages.properties：</br>
				<div class="inputDiv">文件路径&nbsp;<input type="text" id="validationMessagesPath" name="validationMessagesPath"/></div>
			</div>
			<div class="subButDiv">
				<button id="makeBut" onclick="setChkVal();doFastJava('mapperHelper_createFile')" >生成</button>
			</div>
		</div>
	</div>

	<div class="tableInfo">
		<div class="title">表信息</div>
		<div class="content">
			<input type="hidden" id="tableInfos" name="tableInfos"/>
			<div class="tableSearch">
				表名&nbsp;<input type="text" id="selTableByName" name="selTableByName" oninput="selectByName(this.value);"/>
			</div>
			<div class="tableTilte">
				<div class="tableChk"><input type="checkbox" id="checkAll" name="checkAll" onclick="checkTableAll(this)"></div>
				<div class="tableTitle tableNameWidth">表名</div>
				<div class="tableTitle tableDelWidth">创建时间</div>
				<div class="tableTitle tableDelWidth">修改时间</div>
				<div class="tableTitle tableDelWidth">删除字段</div>
				<div class="tableTitle tableDelWidth">删除时间</div>
			</div>
			<div id="tableCheck"></div>
		</div>
	</div>
</div>

<script>
var tableInfos;
var selTableByName;
var checkAll;
var actionNameSpace;
var actionPath;
var serviceNameSpace;
var servicePath;
var daoNameSpace;
var daoPath;
var boNameSpace;
var boPath;
var voNameSpace;
var voPath;
var mapperPath;
var validationMessagesPath;

function onloadFunction() {
	document.getElementById("selTableByName").value = typeof(selTableByName)=="undefined"?"":selTableByName;
	document.getElementById("checkAll").checked = typeof(checkAll)=="undefined"?false:true;
	document.getElementById("actionNameSpace").value = typeof(actionNameSpace)=="undefined"?"":actionNameSpace;
	document.getElementById("actionPath").value = typeof(actionPath)=="undefined"?"":actionPath;
	document.getElementById("serviceNameSpace").value = typeof(serviceNameSpace)=="undefined"?"":serviceNameSpace;
	document.getElementById("servicePath").value = typeof(servicePath)=="undefined"?"":servicePath;
	document.getElementById("daoNameSpace").value = typeof(daoNameSpace)=="undefined"?"":daoNameSpace;
	document.getElementById("daoPath").value = typeof(daoPath)=="undefined"?"":daoPath;
	document.getElementById("boNameSpace").value = typeof(boNameSpace)=="undefined"?"":boNameSpace;
	document.getElementById("boPath").value = typeof(boPath)=="undefined"?"":boPath;
	document.getElementById("voNameSpace").value = typeof(voNameSpace)=="undefined"?"":voNameSpace;
	document.getElementById("voPath").value = typeof(voPath)=="undefined"?"":voPath;
	document.getElementById("mapperPath").value = typeof(mapperPath)=="undefined"?"":mapperPath;
	document.getElementById("validationMessagesPath").value = typeof(validationMessagesPath)=="undefined"?"":validationMessagesPath;

	var divs = "";
	var tableInfoStr = "[";
	for(var i=0; i<tableInfos.length; i++) {
		var checked = "";
		if(tableInfos[i][0] == true) {
			checked = "checked";
		}
		var table = tableInfos[i][1].split("&");
		divs += "<div class='tableRow'>"
			  + "<div class='tableChk'><input type='checkbox' name='tableChked' value='"+table[0]+"&"+table[1]+"&"+table[2]+"&"+table[3]+"&"+table[4]+"' "+checked+"/></div>"
			  + "<div class='tableRowDiv tableName tableNameWidth'>"+table[0]+"</div>"
			  + "<div class='tableRowDiv tableDel tableDelWidth'><input value='" + table[1] + "'/></div>"
			  + "<div class='tableRowDiv tableDel tableDelWidth'><input value='" + table[2] + "'/></div>"
			  + "<div class='tableRowDiv tableDel tableDelWidth'><input value='" + table[3] + "'/></div>"
			  + "<div class='tableRowDiv tableDel tableDelWidth'><input value='" + table[4] + "'/></div>"
			  + "</div>";

	    if(tableInfoStr != "[") {
	    	tableInfoStr += ",";
	    }
		tableInfoStr += "[" + tableInfos[i][0] + ",'" + table[0] + "&" + table[1] + "&" + table[2] + "']";
	}

	document.getElementById("tableInfos").value = tableInfoStr + "]";
	document.getElementById("tableCheck").innerHTML = divs;
	selectByName(selTableByName);
}

//读取项目路径
function readPath() {
	setChkVal();	//设置checkbox值
	doFastJava('mapperHelper_readPath'); //读取项目路径
}

//全选
function checkTableAll(obj) {
	obj.value = obj.checked;
	var checkboxs = document.getElementsByName('tableChked');
	for(i=0; i<checkboxs.length; i++) {
		checkboxs[i].checked = obj.checked;
	}
}

//按name搜索table
function selectByName(val) {
	if(typeof(val) == "undefined" || val == "") {
		return;
	}

	var tableChecks = document.getElementById("tableCheck").childNodes;
	for(i=0; i<tableChecks.length; i++) {
		var tableName = tableChecks[i].childNodes[1].innerHTML;
		if(tableName.indexOf(val) == -1) {
			tableChecks[i].style.display = "none";
		} else {
			tableChecks[i].style.display = "";
		}
	}
}

//设置表checkBox的value信息，后台生成类用，每个走后台事件调用
function setChkVal() {
	var tableInfoStr = "[";
	var tableChecks = document.getElementById("tableCheck").childNodes;
	for(i=0; i<tableChecks.length; i++) {
		var isChecked = tableChecks[i].childNodes[0].childNodes[0].checked;
		var tableName = tableChecks[i].childNodes[1].innerHTML;
		var createTimeCol = tableChecks[i].childNodes[2].childNodes[0].value;
		var updateTimeCol = tableChecks[i].childNodes[3].childNodes[0].value;
		var delColumn = tableChecks[i].childNodes[4].childNodes[0].value;
		var delTimeCol = tableChecks[i].childNodes[5].childNodes[0].value;

	    if(tableInfoStr != "[") {
	    	tableInfoStr += ",";
	    }
		tableInfoStr += "[" + isChecked + ",'" + tableName + "&" + createTimeCol + "&" + updateTimeCol + "&" + delColumn + "&" + delTimeCol + "]";
	}

	//替换表信息隐藏域是否选中的值
	document.getElementById("tableInfos").value = tableInfoStr + "]";
}
</script>

<style>
.infoPage {
	margin-left: auto;
	margin-right:auto;
	width:1070px;
}
.classInfo {
	width:520px;
	float:left;
	display:inline;
	position:initial;
}
.tableInfo {
	width:530px;
	float:left;
	display:inline;
	margin-left:20px;
}
.title {
	background-color: white;
	height:40px;
	line-height:40px;
	border-bottom:1px solid rgba(0,0,0,.15);
	padding-left:10px;
	font-size:16px;
	color:grey;
}
.content {
	background-color: white;
	height:660px;
	color:#4E4E4E;
}
.tableSearch {
	clear:both;
	padding-top:22px;
	padding-left:15px;
}
.tableTilte {
	clear:both;
	padding-top:10px;
	padding-left:15px;
	padding-right:15px;
}
#tableCheck {
	clear:both;
	height:408px;
	overflow:auto;
	padding-left:15px;
	padding-right:15px;
}
.tableRow {
	clear:both;
	padding-top:5px;
}
.tableChk {
	float:left;
	display:inline;
	width:5%;
    padding-bottom: 4px;
}
.tableTitle {
	float:left;
	display:inline;
	width:30%;
	text-align:center;
}
.tableNameWidth {
	width:25%;
	padding-left:1%;
}
.tableDelWidth {
	width:17%;
}
.tableRowDiv {
	float:left;
	display:inline;
	border-bottom:1px solid rgba(0,0,0,.15);
}
.tableName {
    padding-bottom: 4px;
}
.tableDel {
	text-align:center;
    padding-bottom: 2px;
}
.tableDel>input {
	width:70px;
}
.classDiv {
	padding-top:20px;
	padding-left:15px;
}
.classChkDiv {
	vertical-align:middle
}
.inputDiv {
	padding-top:2px;
}
.inputDiv>input {
	width:410px;
}
.subButDiv {
	padding-top:25px;
	padding-bottom:10px;
	text-align:center;
}
.subButDiv>button {
	width:80px;
	font-family:Microsoft Yahei,Helvetica Neue,Hiragino Sans GB,WenQuanYi Micro Hei,sans-serif;
}
</style>
 */
public class MapperHelperHtml {
    public String html() {
        StringBuffer sb = new StringBuffer();
        String newLine = System.getProperty("line.separator");

        sb.append(newLine).append("<div class=\"infoPage\">")
                .append(newLine).append("	<div class=\"classInfo\">")
                .append(newLine).append("		<div class=\"title\">类信息</div>")
                .append(newLine).append("		<div class=\"content\">")
                .append(newLine).append("			<div class=\"classDiv\">")
                .append(newLine).append("				Action：</br>")
                .append(newLine).append("				<div class=\"inputDiv\">命名空间&nbsp;<input type=\"text\" id=\"actionNameSpace\" name=\"actionNameSpace\"/></div>")
                .append(newLine).append("				<div class=\"inputDiv\">文件路径&nbsp;<input type=\"text\" id=\"actionPath\" name=\"actionPath\"/></div>")
                .append(newLine).append("			</div>")
                .append(newLine).append("			<div class=\"classDiv\">")
                .append(newLine).append("				Service：</br>")
                .append(newLine).append("				<div class=\"inputDiv\">命名空间&nbsp;<input type=\"text\" id=\"serviceNameSpace\" name=\"serviceNameSpace\"/></div>")
                .append(newLine).append("				<div class=\"inputDiv\">文件路径&nbsp;<input type=\"text\" id=\"servicePath\" name=\"servicePath\"/></div>")
                .append(newLine).append("			</div>")
                .append(newLine).append("			<div class=\"classDiv\">")
                .append(newLine).append("				Dao：</br>")
                .append(newLine).append("				<div class=\"inputDiv\">命名空间&nbsp;<input type=\"text\" id=\"daoNameSpace\" name=\"daoNameSpace\"/></div>")
                .append(newLine).append("				<div class=\"inputDiv\">文件路径&nbsp;<input type=\"text\" id=\"daoPath\" name=\"daoPath\"/></div>")
                .append(newLine).append("			</div>")
                .append(newLine).append("			<div class=\"classDiv\">")
                .append(newLine).append("				BO：</br>")
                .append(newLine).append("				<div class=\"inputDiv\">命名空间&nbsp;<input type=\"text\" id=\"boNameSpace\" name=\"boNameSpace\"/></div>")
                .append(newLine).append("				<div class=\"inputDiv\">文件路径&nbsp;<input type=\"text\" id=\"boPath\" name=\"boPath\"/></div>")
                .append(newLine).append("			</div>")
                .append(newLine).append("			<div class=\"classDiv\">")
                .append(newLine).append("				VO：</br>")
                .append(newLine).append("				<div class=\"inputDiv\">命名空间&nbsp;<input type=\"text\" id=\"voNameSpace\" name=\"voNameSpace\"/></div>")
                .append(newLine).append("				<div class=\"inputDiv\">文件路径&nbsp;<input type=\"text\" id=\"voPath\" name=\"voPath\"/></div>")
                .append(newLine).append("			</div>")
                .append(newLine).append("			<div class=\"classDiv\">")
                .append(newLine).append("				MapperXML：</br>")
                .append(newLine).append("				<div class=\"inputDiv\">文件路径&nbsp;<input type=\"text\" id=\"mapperPath\" name=\"mapperPath\"/></div>")
                .append(newLine).append("			</div>")
                .append(newLine).append("			<div class=\"classDiv\">")
                .append(newLine).append("				ValidationMessages.properties：</br>")
                .append(newLine).append("				<div class=\"inputDiv\">文件路径&nbsp;<input type=\"text\" id=\"validationMessagesPath\" name=\"validationMessagesPath\"/></div>")
                .append(newLine).append("			</div>")
                .append(newLine).append("			<div class=\"subButDiv\">")
                .append(newLine).append("				<button id=\"makeBut\" onclick=\"setChkVal();doFastJava('mapperHelper_createFile')\" >生成</button>")
                .append(newLine).append("			</div>")
                .append(newLine).append("		</div>")
                .append(newLine).append("	</div>")
                .append(newLine).append("")
                .append(newLine).append("	<div class=\"tableInfo\">")
                .append(newLine).append("		<div class=\"title\">表信息</div>")
                .append(newLine).append("		<div class=\"content\">")
                .append(newLine).append("			<input type=\"hidden\" id=\"tableInfos\" name=\"tableInfos\"/>")
                .append(newLine).append("			<div class=\"tableSearch\">")
                .append(newLine).append("				表名&nbsp;<input type=\"text\" id=\"selTableByName\" name=\"selTableByName\" oninput=\"selectByName(this.value);\"/>")
                .append(newLine).append("			</div>")
                .append(newLine).append("			<div class=\"tableTilte\">")
                .append(newLine).append("				<div class=\"tableChk\"><input type=\"checkbox\" id=\"checkAll\" name=\"checkAll\" onclick=\"checkTableAll(this)\"></div>")
                .append(newLine).append("				<div class=\"tableTitle tableNameWidth\">表名</div>")
                .append(newLine).append("				<div class=\"tableTitle tableDelWidth\">创建时间</div>")
                .append(newLine).append("				<div class=\"tableTitle tableDelWidth\">修改时间</div>")
                .append(newLine).append("				<div class=\"tableTitle tableDelWidth\">删除字段</div>")
                .append(newLine).append("				<div class=\"tableTitle tableDelWidth\">删除时间</div>")
                .append(newLine).append("			</div>")
                .append(newLine).append("			<div id=\"tableCheck\"></div>")
                .append(newLine).append("		</div>")
                .append(newLine).append("	</div>")
                .append(newLine).append("</div>")
                .append(newLine).append("")
                .append(newLine).append("<script>")
                .append(newLine).append("var tableInfos;")
                .append(newLine).append("var selTableByName;")
                .append(newLine).append("var checkAll;")
                .append(newLine).append("var actionNameSpace;")
                .append(newLine).append("var actionPath;")
                .append(newLine).append("var serviceNameSpace;")
                .append(newLine).append("var servicePath;")
                .append(newLine).append("var daoNameSpace;")
                .append(newLine).append("var daoPath;")
                .append(newLine).append("var boNameSpace;")
                .append(newLine).append("var boPath;")
                .append(newLine).append("var voNameSpace;")
                .append(newLine).append("var voPath;")
                .append(newLine).append("var mapperPath;")
                .append(newLine).append("var validationMessagesPath;")
                .append(newLine).append("")
                .append(newLine).append("function onloadFunction() {")
                .append(newLine).append("	document.getElementById(\"selTableByName\").value = typeof(selTableByName)==\"undefined\"?\"\":selTableByName;")
                .append(newLine).append("	document.getElementById(\"checkAll\").checked = typeof(checkAll)==\"undefined\"?false:true;")
                .append(newLine).append("	document.getElementById(\"actionNameSpace\").value = typeof(actionNameSpace)==\"undefined\"?\"\":actionNameSpace;")
                .append(newLine).append("	document.getElementById(\"actionPath\").value = typeof(actionPath)==\"undefined\"?\"\":actionPath;")
                .append(newLine).append("	document.getElementById(\"serviceNameSpace\").value = typeof(serviceNameSpace)==\"undefined\"?\"\":serviceNameSpace;")
                .append(newLine).append("	document.getElementById(\"servicePath\").value = typeof(servicePath)==\"undefined\"?\"\":servicePath;")
                .append(newLine).append("	document.getElementById(\"daoNameSpace\").value = typeof(daoNameSpace)==\"undefined\"?\"\":daoNameSpace;")
                .append(newLine).append("	document.getElementById(\"daoPath\").value = typeof(daoPath)==\"undefined\"?\"\":daoPath;")
                .append(newLine).append("	document.getElementById(\"boNameSpace\").value = typeof(boNameSpace)==\"undefined\"?\"\":boNameSpace;")
                .append(newLine).append("	document.getElementById(\"boPath\").value = typeof(boPath)==\"undefined\"?\"\":boPath;")
                .append(newLine).append("	document.getElementById(\"voNameSpace\").value = typeof(voNameSpace)==\"undefined\"?\"\":voNameSpace;")
                .append(newLine).append("	document.getElementById(\"voPath\").value = typeof(voPath)==\"undefined\"?\"\":voPath;")
                .append(newLine).append("	document.getElementById(\"mapperPath\").value = typeof(mapperPath)==\"undefined\"?\"\":mapperPath;")
                .append(newLine).append("	document.getElementById(\"validationMessagesPath\").value = typeof(validationMessagesPath)==\"undefined\"?\"\":validationMessagesPath;")
                .append(newLine).append("")
                .append(newLine).append("	var divs = \"\";")
                .append(newLine).append("	var tableInfoStr = \"[\";")
                .append(newLine).append("	for(var i=0; i<tableInfos.length; i++) {")
                .append(newLine).append("		var checked = \"\";")
                .append(newLine).append("		if(tableInfos[i][0] == true) {")
                .append(newLine).append("			checked = \"checked\";")
                .append(newLine).append("		}")
                .append(newLine).append("		var table = tableInfos[i][1].split(\"&\");")
                .append(newLine).append("		divs += \"<div class='tableRow'>\"")
                .append(newLine).append("			  + \"<div class='tableChk'><input type='checkbox' name='tableChked' value='\"+table[0]+\"&\"+table[1]+\"&\"+table[2]+\"&\"+table[3]+\"&\"+table[4]+\"' \"+checked+\"/></div>\"")
                .append(newLine).append("			  + \"<div class='tableRowDiv tableName tableNameWidth'>\"+table[0]+\"</div>\"")
                .append(newLine).append("			  + \"<div class='tableRowDiv tableDel tableDelWidth'><input value='\" + table[1] + \"'/></div>\"")
                .append(newLine).append("			  + \"<div class='tableRowDiv tableDel tableDelWidth'><input value='\" + table[2] + \"'/></div>\"")
                .append(newLine).append("			  + \"<div class='tableRowDiv tableDel tableDelWidth'><input value='\" + table[3] + \"'/></div>\"")
                .append(newLine).append("			  + \"<div class='tableRowDiv tableDel tableDelWidth'><input value='\" + table[4] + \"'/></div>\"")
                .append(newLine).append("			  + \"</div>\";")
                .append(newLine).append("		")
                .append(newLine).append("	    if(tableInfoStr != \"[\") {")
                .append(newLine).append("	    	tableInfoStr += \",\";")
                .append(newLine).append("	    }")
                .append(newLine).append("		tableInfoStr += \"[\" + tableInfos[i][0] + \",'\" + table[0] + \"&\" + table[1] + \"&\" + table[2] + \"']\";")
                .append(newLine).append("	}")
                .append(newLine).append("	")
                .append(newLine).append("	document.getElementById(\"tableInfos\").value = tableInfoStr + \"]\";")
                .append(newLine).append("	document.getElementById(\"tableCheck\").innerHTML = divs;")
                .append(newLine).append("	selectByName(selTableByName);")
                .append(newLine).append("}")
                .append(newLine).append("")
                .append(newLine).append("//读取项目路径")
                .append(newLine).append("function readPath() {")
                .append(newLine).append("	setChkVal();	//设置checkbox值")
                .append(newLine).append("	doFastJava('mapperHelper_readPath'); //读取项目路径")
                .append(newLine).append("}")
                .append(newLine).append("")
                .append(newLine).append("//全选")
                .append(newLine).append("function checkTableAll(obj) {")
                .append(newLine).append("	obj.value = obj.checked;")
                .append(newLine).append("	var checkboxs = document.getElementsByName('tableChked');")
                .append(newLine).append("	for(i=0; i<checkboxs.length; i++) {")
                .append(newLine).append("		checkboxs[i].checked = obj.checked;")
                .append(newLine).append("	}")
                .append(newLine).append("}")
                .append(newLine).append("")
                .append(newLine).append("//按name搜索table")
                .append(newLine).append("function selectByName(val) {")
                .append(newLine).append("	if(typeof(val) == \"undefined\" || val == \"\") {")
                .append(newLine).append("		return;")
                .append(newLine).append("	}")
                .append(newLine).append("	")
                .append(newLine).append("	var tableChecks = document.getElementById(\"tableCheck\").childNodes;")
                .append(newLine).append("	for(i=0; i<tableChecks.length; i++) {")
                .append(newLine).append("		var tableName = tableChecks[i].childNodes[1].innerHTML;")
                .append(newLine).append("		if(tableName.indexOf(val) == -1) {")
                .append(newLine).append("			tableChecks[i].style.display = \"none\";")
                .append(newLine).append("		} else {")
                .append(newLine).append("			tableChecks[i].style.display = \"\";")
                .append(newLine).append("		}")
                .append(newLine).append("	}")
                .append(newLine).append("}")
                .append(newLine).append("")
                .append(newLine).append("//设置表checkBox的value信息，后台生成类用，每个走后台事件调用")
                .append(newLine).append("function setChkVal() {")
                .append(newLine).append("	var tableInfoStr = \"[\";")
                .append(newLine).append("	var tableChecks = document.getElementById(\"tableCheck\").childNodes;")
                .append(newLine).append("	for(i=0; i<tableChecks.length; i++) {")
                .append(newLine).append("		var isChecked = tableChecks[i].childNodes[0].childNodes[0].checked;")
                .append(newLine).append("		var tableName = tableChecks[i].childNodes[1].innerHTML;")
                .append(newLine).append("		var createTimeCol = tableChecks[i].childNodes[2].childNodes[0].value;")
                .append(newLine).append("		var updateTimeCol = tableChecks[i].childNodes[3].childNodes[0].value;")
                .append(newLine).append("		var delColumn = tableChecks[i].childNodes[4].childNodes[0].value;")
                .append(newLine).append("		var delTimeCol = tableChecks[i].childNodes[5].childNodes[0].value;")
                .append(newLine).append("")
                .append(newLine).append("	    if(tableInfoStr != \"[\") {")
                .append(newLine).append("	    	tableInfoStr += \",\";")
                .append(newLine).append("	    }")
                .append(newLine).append("		tableInfoStr += \"[\" + isChecked + \",'\" + tableName + \"&\" + createTimeCol + \"&\" + updateTimeCol + \"&\" + delColumn + \"&\" + delTimeCol + \"']\";")
                .append(newLine).append("	}")
                .append(newLine).append("")
                .append(newLine).append("	//替换表信息隐藏域是否选中的值")
                .append(newLine).append("	document.getElementById(\"tableInfos\").value = tableInfoStr + \"]\";")
                .append(newLine).append("}")
                .append(newLine).append("</script>")
                .append(newLine).append("")
                .append(newLine).append("<style>")
                .append(newLine).append(".infoPage {")
                .append(newLine).append("	margin-left: auto;")
                .append(newLine).append("	margin-right:auto;")
                .append(newLine).append("	width:1070px;")
                .append(newLine).append("}")
                .append(newLine).append(".classInfo {")
                .append(newLine).append("	width:520px;")
                .append(newLine).append("	float:left;")
                .append(newLine).append("	display:inline;")
                .append(newLine).append("	position:initial;")
                .append(newLine).append("}")
                .append(newLine).append(".tableInfo {")
                .append(newLine).append("	width:530px;")
                .append(newLine).append("	float:left;")
                .append(newLine).append("	display:inline;")
                .append(newLine).append("	margin-left:20px;")
                .append(newLine).append("}")
                .append(newLine).append(".title {")
                .append(newLine).append("	background-color: white;")
                .append(newLine).append("	height:40px;")
                .append(newLine).append("	line-height:40px;")
                .append(newLine).append("	border-bottom:1px solid rgba(0,0,0,.15);")
                .append(newLine).append("	padding-left:10px;")
                .append(newLine).append("	font-size:16px;")
                .append(newLine).append("	color:grey;")
                .append(newLine).append("}")
                .append(newLine).append(".content {")
                .append(newLine).append("	background-color: white;")
                .append(newLine).append("	height:660px;")
                .append(newLine).append("	color:#4E4E4E;")
                .append(newLine).append("}")
                .append(newLine).append(".tableSearch {")
                .append(newLine).append("	clear:both;")
                .append(newLine).append("	padding-top:22px;")
                .append(newLine).append("	padding-left:15px;")
                .append(newLine).append("}")
                .append(newLine).append(".tableTilte {")
                .append(newLine).append("	clear:both;")
                .append(newLine).append("	padding-top:10px;")
                .append(newLine).append("	padding-left:15px;")
                .append(newLine).append("	padding-right:15px;")
                .append(newLine).append("}")
                .append(newLine).append("#tableCheck {")
                .append(newLine).append("	clear:both;")
                .append(newLine).append("	height:408px;")
                .append(newLine).append("	overflow:auto;")
                .append(newLine).append("	padding-left:15px;")
                .append(newLine).append("	padding-right:15px;")
                .append(newLine).append("}")
                .append(newLine).append(".tableRow {")
                .append(newLine).append("	clear:both;")
                .append(newLine).append("	padding-top:5px;")
                .append(newLine).append("}")
                .append(newLine).append(".tableChk {")
                .append(newLine).append("	float:left;")
                .append(newLine).append("	display:inline;")
                .append(newLine).append("	width:5%;")
                .append(newLine).append("    padding-bottom: 4px;")
                .append(newLine).append("}")
                .append(newLine).append(".tableTitle {")
                .append(newLine).append("	float:left;")
                .append(newLine).append("	display:inline;")
                .append(newLine).append("	width:30%;")
                .append(newLine).append("	text-align:center;")
                .append(newLine).append("}")
                .append(newLine).append(".tableNameWidth {")
                .append(newLine).append("	width:25%;")
                .append(newLine).append("	padding-left:1%;")
                .append(newLine).append("}")
                .append(newLine).append(".tableDelWidth {")
                .append(newLine).append("	width:17%;")
                .append(newLine).append("}")
                .append(newLine).append(".tableRowDiv {")
                .append(newLine).append("	float:left;")
                .append(newLine).append("	display:inline;")
                .append(newLine).append("	border-bottom:1px solid rgba(0,0,0,.15);")
                .append(newLine).append("}")
                .append(newLine).append(".tableName {")
                .append(newLine).append("    padding-bottom: 4px;")
                .append(newLine).append("}")
                .append(newLine).append(".tableDel {")
                .append(newLine).append("	text-align:center;")
                .append(newLine).append("    padding-bottom: 2px;")
                .append(newLine).append("}")
                .append(newLine).append(".tableDel>input {")
                .append(newLine).append("	width:70px;")
                .append(newLine).append("}")
                .append(newLine).append(".classDiv {")
                .append(newLine).append("	padding-top:20px;")
                .append(newLine).append("	padding-left:15px;")
                .append(newLine).append("}")
                .append(newLine).append(".classChkDiv {")
                .append(newLine).append("	vertical-align:middle")
                .append(newLine).append("}")
                .append(newLine).append(".inputDiv {")
                .append(newLine).append("	padding-top:2px;")
                .append(newLine).append("}")
                .append(newLine).append(".inputDiv>input {")
                .append(newLine).append("	width:410px;")
                .append(newLine).append("}")
                .append(newLine).append(".subButDiv {")
                .append(newLine).append("	padding-top:25px;")
                .append(newLine).append("	padding-bottom:10px;")
                .append(newLine).append("	text-align:center;")
                .append(newLine).append("}")
                .append(newLine).append(".subButDiv>button {")
                .append(newLine).append("	width:80px;")
                .append(newLine).append("	font-family:Microsoft Yahei,Helvetica Neue,Hiragino Sans GB,WenQuanYi Micro Hei,sans-serif;")
                .append(newLine).append("}")
                .append(newLine).append("</style>");

        return sb.toString();
    }
}
