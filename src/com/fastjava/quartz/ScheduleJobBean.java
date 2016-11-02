package com.fastjava.quartz;

import java.util.Map;

/**
 * 计划任务信息
 */
public class ScheduleJobBean {
 
    /** 任务id */
    private String id;
 
    /** 任务名称 */
    private String name;
 
    /** 任务分组 */
    private String group;
 
    /** 任务状态 0禁用 1启用 2删除*/
    private String status;
 
    /** 任务运行时间表达式 */
    private String cron;
 
    /** 任务描述 */
    private String desc;
    
    /** 执行类 */
    private Class<? extends QuartzJob> jobClass;
    
    private boolean log = true;

    /** 自定义参数 */
    private Map<String,Object> params;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Class<? extends QuartzJob> getJobClass() {
		return jobClass;
	}

	public void setJobClass(Class<? extends QuartzJob> jobClass) {
		this.jobClass = jobClass;
	}

	public boolean getLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

}
