package com.fastjava.quartz;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * 定时任务运行抽象类
 */
public abstract class QuartzJob implements Job {
	private static Logger logger = LoggerFactory.getLogger(QuartzJob.class);
	
	private static ApplicationContext applicationContext;
	
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		if(null == applicationContext) {
			applicationContext = (ApplicationContext)context.getMergedJobDataMap().get("applicationContext");
		}

		//任务信息
    	ScheduleJobBean scheduleJob = (ScheduleJobBean)context.getMergedJobDataMap().get("scheduleJob");
    	//是否启动日志
    	boolean isLog = scheduleJob.getLog();

		StringBuffer logMessage = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		if(isLog) {
			logMessage.append("任务 ").append(scheduleJob.getGroup()).append(" - ").append(scheduleJob.getName()).append(":\r\n")
				.append("\t启动 ").append(sdf.format(new Date())).append("\r\n");
		}
		
    	//调用任务方法
    	try {
			scheduleJob.getJobClass()
				.getMethod("run",ScheduleJobBean.class)
				.invoke(applicationContext.getBean(scheduleJob.getJobClass()),scheduleJob);
			
			if(isLog) {
				logMessage.append("\t结束 ").append(sdf.format(new Date()));
			}
		} catch (Exception e) {
			if(isLog) {
				logMessage.append("\t错误 ").append(sdf.format(new Date())).append("\r\n\t").append(e.getMessage());
			}
			logger.error("调用任务 " + scheduleJob.getGroup() + " - " + scheduleJob.getName() + " 错误:\r\n" + e.getMessage());
		} finally {
			if(isLog) {
				logger.info(logMessage.toString());
			}
		}
    }
    
    /**
     * 任务默认调用此方法的重写方法
     * @param scheduleJob 任务信息
     */
    public abstract void run(ScheduleJobBean scheduleJob);
}