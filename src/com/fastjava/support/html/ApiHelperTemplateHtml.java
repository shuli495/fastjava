package com.fastjava.support.html;

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
.content {
	width: 70%;
	background-color: white;
	margin-top: 50px;
	margin-bottom: 50px;
	margin-left: auto;
	margin-right: auto;
	padding: 10px 30px;
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
</head>

<body>
<div class="content">
api_doc_html
</div>
</body>
</html>
 */
public class ApiHelperTemplateHtml {

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
                .append(newLine).append(".content {")
                .append(newLine).append("	width: 70%;")
                .append(newLine).append("	background-color: white;")
                .append(newLine).append("	margin-top: 50px;")
                .append(newLine).append("	margin-bottom: 50px;")
                .append(newLine).append("	margin-left: auto;")
                .append(newLine).append("	margin-right: auto;")
                .append(newLine).append("	padding: 10px 30px;")
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
                .append(newLine).append("</style>")
                .append(newLine).append("</head>")
                .append(newLine).append("")
                .append(newLine).append("<body>")
                .append(newLine).append("<div class=\"content\">")
                .append(newLine).append("api_doc_html")
                .append(newLine).append("</div>")
                .append(newLine).append("</body>")
                .append(newLine).append("</html>");

        return sb.toString();
    }
}
