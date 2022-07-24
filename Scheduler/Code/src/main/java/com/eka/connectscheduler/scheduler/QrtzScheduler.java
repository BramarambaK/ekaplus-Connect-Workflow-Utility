package com.eka.connectscheduler.scheduler;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import com.eka.connectscheduler.config.AutoWiringSpringBeanJobFactory;
import com.eka.connectscheduler.service.WorkflowJobService;
import com.eka.connectscheduler.utils.SchedulerUtils;

@Configuration
@ConditionalOnExpression("'${using.spring.schedulerFactory}'=='false'")
public class QrtzScheduler {

	final static Logger logger = ESAPI.getLogger(QrtzScheduler.class);

	@Autowired
	private ApplicationContext applicationContext;

	@Value("${eka_connect_host}")
	private String ekaConnectHost;

	@Autowired
	private SchedulerUtils schedulerUtils;
	@Autowired
	private WorkflowJobService workflowJobService;
	@PostConstruct
	public void init() {
		logger.info(Logger.EVENT_SUCCESS, "Hello world from Quartz...");
	}

	@Bean
	public SpringBeanJobFactory springBeanJobFactory() {
		AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
		logger.debug(Logger.EVENT_SUCCESS, "Configuring Job factory in QrtzScheduler");

		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setJobFactory(springBeanJobFactory());
		factory.setQuartzProperties(quartzProperties());
		return factory;
	}

	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("/application.properties"));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}

	@Bean
	public Scheduler scheduler(SchedulerFactoryBean factory) throws SchedulerException {
		logger.debug(Logger.EVENT_SUCCESS, "Getting a handle to the Scheduler");
		Scheduler scheduler = factory.getScheduler();
		List<JobDetail> jobs = jobDetails();

		for (JobDetail job : jobs) {
			scheduler.addJob(job, false);
		}

		List<Trigger> triggers = triggers(jobs);
		for (Trigger trigger : triggers) {
			scheduler.scheduleJob(trigger);
		}

		logger.debug(Logger.EVENT_SUCCESS, "Starting Scheduler threads");
		scheduler.start();
		workflowJobService.executeSampleJobImmediately(jobs);
		return scheduler;
	}

	@Bean
	public List<Trigger> triggers(List<JobDetail> jobs) {
		List<Trigger> triggers = schedulerUtils.buildTriggers(jobs, "cron");
		return triggers;
	}

	@Bean
	public List<JobDetail> jobDetails() {
		List<JobDetail> jobs = schedulerUtils.buildJobs();
		return jobs;
	}

}
