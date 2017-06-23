# fastjava
基于java spring mvc、mybatis 的快速开发框架。即插即用，无代码入侵，简化开发流程，帮助developer快速、规范开发。

- 根据controller可生成api文档。
- 根据数据库生成各个数据表的mybatis mapper、dao、service、controller；并自动生成CURD方法。
- CUD有hibernate-validator数据校验。
- mybatis分页查询，查询排序。
- 异常处理、restful返回值规范、缓存、日志、定时任务......等等功能。

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
    	<version>0.9.4</version>
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
        compile 'com.github.shuli495:fastjava:0.9.3'
}
```
- [历史版本](https://github.com/shuli495/fastjava/releases)

## web
本项目没集成spring及web，仅作为框架需要其他项目引用使用。[fastWebsite](http://note.youdao.com/)是已高度集成fastjava的sringMVC web项目，几乎无需配置。