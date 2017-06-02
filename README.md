# fastjava
java spring mvc 快速开发框架。需要java web项目引用本项目，或将本项目打成jar包导入。
> 根据数据库生成各个数据表的mybatis mapper、dao、service、controller；并自动生成CURD方法。
> 根据controller可生成api文档。

<br/><br/>
## 进入fastjava控制台
引用本项目后，web.xml中添加一下代码，访问localhost/fastjava，进入控制台：
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
