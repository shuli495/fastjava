# fastjava
快速开发框架。即插即用，无代码入侵，简化开发流程，帮助developer快速、规范开发。

v1+:\
基于spring mvc、mybatis。

- 根据controller可生成api文档。
- 根据数据库生成各个数据表的mybatis mapper、dao、service、controller；并自动生成CURD方法。
- CUD有hibernate-validator数据校验。
- mybatis分页查询，查询排序。
- 异常处理、restful返回值规范、缓存、日志、定时任务......等等功能。

v2+:\
基于spring boot、mybatis。\
生成CURD、各层java文件、api改为单独项目：https://github.com/shuli495/fastjava-maker

## wiki
- [wiki](https://github.com/shuli495/fastjava/wiki)

## 使用
下载源码，您的项目引用本源码。或使用构建工具构建。
### MAVEN
```
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>

<dependencies>
    <dependency>
    	<groupId>com.github.shuli495</groupId>
    	<artifactId>fastjava</artifactId>
    	<version>2.0.0</version>
    </dependency>
</dependencies>
```
### GRADLE
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
        compile 'com.github.shuli495:fastjava:2.0.0'
}
```
### 其他项目引用
使用下面包，不需要引用spring boot、fastjava、fastjava-maker，已集成大部分包。
```
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>

<dependencies>
    <dependency>
    	<groupId>com.github.shuli495</groupId>
    	<artifactId>fastjava-parent</artifactId>
    	<version>2.0.0</version>
    </dependency>
</dependencies>
```

**推荐**使用[fastWebsite](https://github.com/shuli495/fastWebsite)此框架，已高度集成fastjava，几乎无需配置。
- [历史版本](https://github.com/shuli495/fastjava/releases)