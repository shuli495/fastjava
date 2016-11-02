package com.fastjava.support.html;

/*<div class="infoPage">

	<div class="content" style="height:855px">
		<div class="title">quartz定时任务</div>
		<div class="margin">
			<font>* 配置在(quartz.xml)com.fastjava.quartz.ScheduleJob类job节点</font><br/>
			<font>* 实现类需要继承com.fastjava.quartz.QuartzJob;类需有@Component注解，spring需扫描该类</font><br/>
			<font>* 日志需父节点的log总开关 、log4j配置文件启动(详情参考日志标签-定时任务日志)</font>
		</div>
		<textarea readonly="readonly" class="margin" style="margin-top:0px;height:180px">
<bean class="com.fastjava.quartz.ScheduleJobBean">
	<property name="jobClass" value="" /><!-- 定时任务实现类 -->
	<property name="name" value="" /><!-- 任务名称 -->
	<property name="group" value="" /><!-- 任务分组 -->
	<property name="status" value="1" /><!-- 0未启用 1启用 -->
	<property name="cron" value="0/10 * * * * ?" /><!-- 调度时间 cron表达式 -->
	<property name="log" vlaue="true" /><!-- 开启日志 默认true -->
</bean></textarea>
		<div class="margin">
			<font>cron表达式</font>
		</div>
		<textarea readonly="readonly" class="margin" style="margin-top:0px;height:485px">
Seconds Minutes Hours DayofMonth Month DayofWeek Year或
Seconds Minutes Hours DayofMonth Month DayofWeek

0 0 10,14,16 * * ? 每天上午10点，下午2点，4点
0 0/30 9-17 * * ? 朝九晚五工作时间内每半小时
0 0 12 ? * WED 表示每个星期三中午12点
"0 0 12 * * ?" 每天中午12点触发
"0 15 10 ? * *" 每天上午10:15触发
"0 15 10 * * ?" 每天上午10:15触发
"0 15 10 * * ? *" 每天上午10:15触发
"0 15 10 * * ? 2005" 2005年的每天上午10:15触发
"0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发
"0 0/5 14 * * ?" 在每天下午2点到下午2:55期间的每5分钟触发
"0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
"0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发
"0 10,44 14 ? 3 WED" 每年三月的星期三的下午2:10和2:44触发
"0 15 10 ? * MON-FRI" 周一至周五的上午10:15触发
"0 15 10 15 * ?" 每月15日上午10:15触发
"0 15 10 L * ?" 每月最后一日的上午10:15触发
"0 15 10 ? * 6L" 每月的最后一个星期五上午10:15触发
"0 15 10 ? * 6L 2002-2005" 2002年至2005年的每月的最后一个星期五上午10:15触发
"0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发</textarea>
	</div>

</div>

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
.title {
	height:40px;
	line-height:40px;
	border-bottom:1px solid rgba(0,0,0,.15);
	padding-left:10px;
	font-size:16px;
}
.margin {
	margin: 15px 35px 0px;
}
.log4jDiv {
	margin-top: 15px;
}
textarea {
	resize: none;
	width: 900px;
	font-size:16px;
	font-family: Microsoft Yahei,Helvetica Neue,Hiragino Sans GB,WenQuanYi Micro Hei,sans-serif
}
</style>
 */
public class QuartzHelperHtml {

    public String html() {
        StringBuffer sb = new StringBuffer();
        String newLine = System.getProperty("line.separator");

        sb.append(newLine).append("<div class=\"infoPage\">")
                .append(newLine).append("")
                .append(newLine).append("	<div class=\"content\" style=\"height:855px\">")
                .append(newLine).append("		<div class=\"title\">quartz定时任务</div>")
                .append(newLine).append("		<div class=\"margin\">")
                .append(newLine).append("			<font>* 配置在(quartz.xml)com.fastjava.quartz.ScheduleJob类job节点</font><br/>")
                .append(newLine).append("			<font>* 实现类需要继承com.fastjava.quartz.QuartzJob;类需有@Component注解，spring需扫描该类</font><br/>")
                .append(newLine).append("			<font>* 日志需父节点的log总开关 、log4j配置文件启动(详情参考日志标签-定时任务日志)</font>")
                .append(newLine).append("		</div>")
                .append(newLine).append("		<textarea readonly=\"readonly\" class=\"margin\" style=\"margin-top:0px;height:180px\">")
                .append(newLine).append("<bean class=\"com.fastjava.quartz.ScheduleJobBean\">")
                .append(newLine).append("	<property name=\"jobClass\" value=\"\" /><!-- 定时任务实现类 -->")
                .append(newLine).append("	<property name=\"name\" value=\"\" /><!-- 任务名称 -->")
                .append(newLine).append("	<property name=\"group\" value=\"\" /><!-- 任务分组 -->")
                .append(newLine).append("	<property name=\"status\" value=\"1\" /><!-- 0未启用 1启用 -->")
                .append(newLine).append("	<property name=\"cron\" value=\"0/10 * * * * ?\" /><!-- 调度时间 cron表达式 -->")
                .append(newLine).append("	<property name=\"log\" vlaue=\"true\" /><!-- 开启日志 默认true -->")
                .append(newLine).append("</bean></textarea>")
                .append(newLine).append("		<div class=\"margin\">")
                .append(newLine).append("			<font>cron表达式</font>")
                .append(newLine).append("		</div>")
                .append(newLine).append("		<textarea readonly=\"readonly\" class=\"margin\" style=\"margin-top:0px;height:485px\">")
                .append(newLine).append("Seconds Minutes Hours DayofMonth Month DayofWeek Year或")
                .append(newLine).append("Seconds Minutes Hours DayofMonth Month DayofWeek")
                .append(newLine).append("")
                .append(newLine).append("0 0 10,14,16 * * ? 每天上午10点，下午2点，4点 ")
                .append(newLine).append("0 0/30 9-17 * * ? 朝九晚五工作时间内每半小时 ")
                .append(newLine).append("0 0 12 ? * WED 表示每个星期三中午12点 ")
                .append(newLine).append("\"0 0 12 * * ?\" 每天中午12点触发 ")
                .append(newLine).append("\"0 15 10 ? * *\" 每天上午10:15触发 ")
                .append(newLine).append("\"0 15 10 * * ?\" 每天上午10:15触发 ")
                .append(newLine).append("\"0 15 10 * * ? *\" 每天上午10:15触发 ")
                .append(newLine).append("\"0 15 10 * * ? 2005\" 2005年的每天上午10:15触发 ")
                .append(newLine).append("\"0 * 14 * * ?\" 在每天下午2点到下午2:59期间的每1分钟触发 ")
                .append(newLine).append("\"0 0/5 14 * * ?\" 在每天下午2点到下午2:55期间的每5分钟触发 ")
                .append(newLine).append("\"0 0/5 14,18 * * ?\" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发 ")
                .append(newLine).append("\"0 0-5 14 * * ?\" 在每天下午2点到下午2:05期间的每1分钟触发 ")
                .append(newLine).append("\"0 10,44 14 ? 3 WED\" 每年三月的星期三的下午2:10和2:44触发 ")
                .append(newLine).append("\"0 15 10 ? * MON-FRI\" 周一至周五的上午10:15触发 ")
                .append(newLine).append("\"0 15 10 15 * ?\" 每月15日上午10:15触发 ")
                .append(newLine).append("\"0 15 10 L * ?\" 每月最后一日的上午10:15触发 ")
                .append(newLine).append("\"0 15 10 ? * 6L\" 每月的最后一个星期五上午10:15触发 ")
                .append(newLine).append("\"0 15 10 ? * 6L 2002-2005\" 2002年至2005年的每月的最后一个星期五上午10:15触发 ")
                .append(newLine).append("\"0 15 10 ? * 6#3\" 每月的第三个星期五上午10:15触发</textarea>")
                .append(newLine).append("	</div>")
                .append(newLine).append("	")
                .append(newLine).append("</div>")
                .append(newLine).append("")
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
                .append(newLine).append(".title {")
                .append(newLine).append("	height:40px;")
                .append(newLine).append("	line-height:40px;")
                .append(newLine).append("	border-bottom:1px solid rgba(0,0,0,.15);")
                .append(newLine).append("	padding-left:10px;")
                .append(newLine).append("	font-size:16px;")
                .append(newLine).append("}")
                .append(newLine).append(".margin {")
                .append(newLine).append("	margin: 15px 35px 0px;")
                .append(newLine).append("}")
                .append(newLine).append(".log4jDiv {")
                .append(newLine).append("	margin-top: 15px;")
                .append(newLine).append("}")
                .append(newLine).append("textarea {")
                .append(newLine).append("	resize: none;")
                .append(newLine).append("	width: 900px;")
                .append(newLine).append("	font-size:16px;")
                .append(newLine).append("	font-family: Microsoft Yahei,Helvetica Neue,Hiragino Sans GB,WenQuanYi Micro Hei,sans-serif")
                .append(newLine).append("}")
                .append(newLine).append("</style>");

        return sb.toString();
    }
}
