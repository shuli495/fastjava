package com.fastjava.listener;

import org.springframework.context.ApplicationContext;

public class SystemSet extends ContextLoader {

	@Override
	public void runBeforeContext(ApplicationContext context) {
		String projectPath = getClass().getResource("/").getFile().toString();
		String[] projectPaths = projectPath.split("/WEB-INF")[0].split("/");
		
		System.setProperty("project.name", projectPaths.length>0?projectPaths[projectPaths.length - 1]:"project");
	}

	@Override
	public void runAferContext(ApplicationContext context) {
	}

}
