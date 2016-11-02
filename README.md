# fastjava
java spring mvc 快速开发框架。java项目，需要java web项目引用，或打成jar包导入。

<br/><br/>
## fastjava控制台
> 可提示框架功能
> 可根据数据库生成各个表的mapper、dao、service、action；并自动生成CURD方法。
> 根据action可生成api文档。

<br/><br/>
## 进入fastjava控制台
web.xml中添加一下代码，访问localhost/fastjava，进入控制台：
```
<servlet>
	<servlet-name>fastJavaView</servlet-name>
	<servlet-class>com.fastjava.Fastjava</servlet-class>
</servlet>
<servlet-mapping>
	<servlet-name>fastJavaView</servlet-name>
	<url-pattern>/fastjava/*</url-pattern>
</servlet-mapping>
```
