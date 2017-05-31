package com.fastjavaframework.quartz;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.fastjavaframework.listener.ContextLoader;

/**
 * 计划任务信息列表
 */
public class ScheduleJob extends ContextLoader {
	private static Logger logger = LoggerFactory.getLogger(ScheduleJob.class);

	/** 调度器 */
	private StdScheduler scheduler;

	/** 任务列表 */
	private List<ScheduleJobBean> jobs;

	/** 启动日志 */
	private boolean log = false;

	@Override
	public void runBeforeContext(ApplicationContext context) {
	}

	@Override
	public void runAferContext(ApplicationContext context) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		if(log && jobs.size() > 0) {
			logger.info("构建任务开始 "+sdf.format(new Date()));
		}
		
		//构建xml中的计划任务
		for(ScheduleJobBean job : jobs) {
			try {
				//未启动
				if("0".equals(job.getStatus())) {
					continue;
				}
				
				JobDetail jobDetail = JobBuilder.newJob(job.getJobClass()).withIdentity(job.getName(), job.getGroup()).build();
				
				//设置任务是否启动日志
				if(!log) {
					job.setLog(false);
				}

				jobDetail.getJobDataMap().put("log", log);	//是否启动日志
				jobDetail.getJobDataMap().put("scheduleJob", job);	//job信息
				jobDetail.getJobDataMap().put("applicationContext", context);	//spring上下文
	
				//表达式调度构建器
				CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCron());
				//按新的cron表达式构建一个新的trigger
				CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(job.getName(), job.getGroup()).withSchedule(scheduleBuilder).build();
				
				scheduler.scheduleJob(jobDetail, trigger);
			} catch (SchedulerException e) {
				String message = "构建任务 " + job.getGroup() +" - " + job.getName() + " 失败：\n" + e.getMessage();
				logger.error(message);
				if(log) {
					logger.info(message);
				}
			}
			
			if(log) {
				logger.info("构建任务 " + job.getGroup() +" - " + job.getName() + " 成功");
			}
		}
		
		if(log) {
			logger.info("构建任务完成 " + sdf.format(new Date()) + " 共构建" + jobs.size() + "条任务");
		}
	
	
	}

	public StdScheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(StdScheduler scheduler) {
		this.scheduler = scheduler;
	}

	public List<ScheduleJobBean> getJobs() {
		return jobs;
	}

	public void setJobs(List<ScheduleJobBean> jobs) {
		this.jobs = jobs;
	}

	public boolean isLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
	}
	
}
