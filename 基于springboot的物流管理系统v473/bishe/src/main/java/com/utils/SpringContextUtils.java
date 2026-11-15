
package com.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;


/**
 * Spring Context 工具类
 */
@Component
public class SpringContextUtils implements ApplicationContextAware, ApplicationListener<ContextClosedEvent> {
	public static volatile ApplicationContext applicationContext; 
	 
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpringContextUtils.applicationContext = applicationContext;
	}

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		ApplicationContext context = SpringContextUtils.applicationContext;
		if (context == event.getApplicationContext()) {
			SpringContextUtils.applicationContext = null;
		}
	}

	public static boolean isContextActive() {
		ApplicationContext context = applicationContext;
		if (context == null) {
			return false;
		}
		if (context instanceof ConfigurableApplicationContext) {
			return ((ConfigurableApplicationContext) context).isActive();
		}
		return true;
	}

	public static Object getBean(String name) {
		return applicationContext.getBean(name);
	}

	public static <T> T getBean(String name, Class<T> requiredType) {
		return applicationContext.getBean(name, requiredType);
	}

	public static <T> T getBeanIfActive(String name, Class<T> requiredType) {
		if (!isContextActive()) {
			return null;
		}
		return applicationContext.getBean(name, requiredType);
	}

	public static boolean containsBean(String name) {
		return applicationContext.containsBean(name);
	}

	public static boolean isSingleton(String name) {
		return applicationContext.isSingleton(name);
	}

	public static Class<? extends Object> getType(String name) {
		return applicationContext.getType(name);
	}

}
