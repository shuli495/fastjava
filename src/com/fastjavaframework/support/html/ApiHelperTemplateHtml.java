package com.fastjavaframework.support.html;

public class ApiHelperTemplateHtml {

    public String commonCSS() {
        StringBuffer sb = new StringBuffer();
        String newLine = System.getProperty("line.separator");

              sb.append(newLine).append(".catalogue {")
                .append(newLine).append("   position: fixed;")
                .append(newLine).append("   top: 15%;")
                .append(newLine).append("   max-height: 75%;")
                .append(newLine).append("   right: 10px;")
                .append(newLine).append("   padding-right: 25px;")
                .append(newLine).append("   background-color: white;")
                .append(newLine).append("   font-size: 0.9em;")
                .append(newLine).append("   overflow-y: auto;")
                .append(newLine).append("}")
                .append(newLine).append(".catalogue a {")
                .append(newLine).append("   color: black;")
                .append(newLine).append("   text-decoration: none;")
                .append(newLine).append("}")
                .append(newLine).append(".catalogue a:hover {")
                .append(newLine).append("   text-decoration: underline;")
                .append(newLine).append("}")
                .append(newLine).append(".catalogue p {")
                .append(newLine).append("   text-align: center;")
                .append(newLine).append("   font-size: x-large;")
                .append(newLine).append("   margin: 0px;")
                .append(newLine).append("}")
                .append(newLine).append("pre {")
                .append(newLine).append("	border:1px solid #ccc;")
                .append(newLine).append("	background-color:#F8F8F8;")
                .append(newLine).append("	min-height:23px;")
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
                .append(newLine).append("}");

        return sb.toString();
    }

    public String commonJS() {
        StringBuffer sb = new StringBuffer();
        String newLine = System.getProperty("line.separator");

          sb.append(newLine).append("// 跳转到标题")
            .append(newLine).append("function gotoTitle(obj) {")
            .append(newLine).append("   window.location.hash='#'+obj.id.replace('catalogue','hash')")
            .append(newLine).append("}")

            .append(newLine).append("// 生成目录")
            .append(newLine).append("function makeCatalogue() {")
            .append(newLine).append("   var h2s = document.getElementsByTagName('h2');")
            .append(newLine).append("   var h3s = document.getElementsByTagName('h3');")
            .append(newLine).append("   var catalogue = document.createElement('div');")
            .append(newLine).append("   catalogue.className = 'catalogue';")

            .append(newLine).append("   for(var i=0; i<h2s.length; i++) {")
            .append(newLine).append("       var ul_id = 'hash' + (i+1);")
            .append(newLine).append("       h2s[i].id = ul_id;")

            .append(newLine).append("       var ul = document.createElement('ul');")
            .append(newLine).append("       var ul_a = document.createElement('a');")
            .append(newLine).append("       ul_a.href = 'javascript:void(0);';")
            .append(newLine).append("       ul_a.id = 'catalogue'+(i+1);")
            .append(newLine).append("       ul_a.onclick = function(){gotoTitle(this)};")
            .append(newLine).append("       ul_a.innerHTML = '<b>'+h2s[i].getElementsByTagName('span')[0].innerHTML+'</b>';")
            .append(newLine).append("       ul.appendChild(ul_a);")

            .append(newLine).append("       for(var j=0; j<h3s.length; j++) {")
            .append(newLine).append("           var h3Index = h3s[j].innerText.split(/\\s/)[0];")
            .append(newLine).append("           var fatherIndex = h3Index.split('.')[0];")

            .append(newLine).append("           // 根据序号筛选h3")
            .append(newLine).append("           if(fatherIndex == (i+1)) {")
            .append(newLine).append("               var li_id = 'hash' + h3Index;")
            .append(newLine).append("               h3s[j].id = li_id;")

            .append(newLine).append("               var li = document.createElement('li');")
            .append(newLine).append("               var li_a = document.createElement('a');")
            .append(newLine).append("               li_a.href = 'javascript:void(0);';")
            .append(newLine).append("               li_a.id = 'catalogue'+h3Index;")
            .append(newLine).append("               li_a.onclick = function(){gotoTitle(this)};")
            .append(newLine).append("               li_a.innerHTML = h3s[j].getElementsByTagName('span')[0].innerHTML;")
            .append(newLine).append("               li.appendChild(li_a);")
            .append(newLine).append("               ul.appendChild(li);")
            .append(newLine).append("           }")
            .append(newLine).append("       }")
            .append(newLine).append("       catalogue.appendChild(ul);")
            .append(newLine).append("   }")


            .append(newLine).append("   var p = document.createElement('p');")
            .append(newLine).append("   var b = document.createElement('b');")
            .append(newLine).append("   var a = document.createElement('a');")
            .append(newLine).append("   a.href = 'javascript:void(0);';")
            .append(newLine).append("   a.innerHTML = '^';")
            .append(newLine).append("   a.onclick = function() {window.location.hash='#navigation'}")
            .append(newLine).append("   b.appendChild(a);")
            .append(newLine).append("   p.appendChild(b);")
            .append(newLine).append("   catalogue.appendChild(p);")
            .append(newLine).append("   document.body.appendChild(catalogue);")
            .append(newLine).append("}");

        return sb.toString();
    }

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
                .append(newLine).append(this.commonCSS())
                .append(newLine).append("</style>")
                .append(newLine).append("</head>")
                .append(newLine).append("")
                .append(newLine).append("<body>")
                .append(newLine).append("<div class=\"content\">")
                .append(newLine).append("api_doc_html")
                .append(newLine).append("</div>")
                .append(newLine).append("<script>")
                .append(newLine).append(this.commonJS())
                .append(newLine).append("makeCatalogue();")
                .append(newLine).append("</script>")
                .append(newLine).append("</body>")
                .append(newLine).append("</html>");

        return sb.toString();
    }
}
