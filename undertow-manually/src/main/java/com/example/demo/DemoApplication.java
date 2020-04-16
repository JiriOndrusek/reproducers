package com.example.demo;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.util.ImmediateInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.event.EventListener;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@SpringBootApplication
public class DemoApplication {
	private static final Set<Class<?>> NO_CLASSES = Collections.emptySet();

	@Autowired
	DelegatingFilterProxyRegistrationBean delegatingFilterProxyRegistrationBean;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() throws Exception {
		System.out.println("starting new server");

		startServlet(new ServletWebServerApplicationContext().getSelfInitializer());
	}

	private void registerServletContainerInitializerToDriveServletContextInitializers(
			DeploymentInfo deployment, ServletContextInitializer initializer) {
		Initializer initializer2 = new Initializer(initializer, delegatingFilterProxyRegistrationBean);
		deployment.addServletContainerInitalizer(new ServletContainerInitializerInfo(
				Initializer.class,
				new ImmediateInstanceFactory<ServletContainerInitializer>(initializer2),
				NO_CLASSES));
	}

	/**
	 * {@link ServletContainerInitializer} to initialize {@link ServletContextInitializer
	 * ServletContextInitializers}.
	 */
	private static class Initializer implements ServletContainerInitializer {

		private final ServletContextInitializer initializers;

		private final DelegatingFilterProxyRegistrationBean delegatingFilterProxyRegistrationBean;

		Initializer(ServletContextInitializer initializers, DelegatingFilterProxyRegistrationBean delegatingFilterProxyRegistrationBean) {
			this.initializers = initializers;
			this.delegatingFilterProxyRegistrationBean = delegatingFilterProxyRegistrationBean;
		}

		@Override
		public void onStartup(Set<Class<?>> classes, ServletContext servletContext)
				throws ServletException {
//			for (ServletContextInitializer initializer : this.initializers) {
//				initializers.onStartup(servletContext);
//			}
			FilterRegistration f = servletContext.addFilter("springSecurityFilterChain", delegatingFilterProxyRegistrationBean.getFilter());
			f.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");;
//			servletContext.addMappingForUrlPatterns(filterInfo, dispatcherTypes, isMatchAfter, urlPatterns);
		}
	}


	private void startServlet(ServletContextInitializer initializer) throws Exception {

		DeploymentInfo servletBuilder = Servlets.deployment();

		registerServletContainerInitializerToDriveServletContextInitializers(servletBuilder, initializer);

		servletBuilder.setClassLoader(MainController.class.getClassLoader())
				.setContextPath("")
				.setDisplayName("application")
				.setDeploymentName("spring-boot");

//				.addServlets(
//						Servlets.servlet("MessageServlet", MessageServlet.class)
//								.addInitParam("message", "Hello World")
//								.addMapping("/*"),
//						Servlets.servlet("MyServlet", MessageServlet.class)
//								.addInitParam("message", "MyServlet")
//								.addMapping("/myservlet"));


		DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
		manager.deploy();
		PathHandler path = Handlers.path(Handlers.redirect("/myapp"))
				.addPrefixPath("/myapp", manager.start());

//		FilterInfo f = new FilterInfo("springSecurityFilterChain", delegatingFilterProxyRegistrationBean.getFilter().getClass(), new ImmediateInstanceFactory(delegatingFilterProxyRegistrationBean.getFilter()));
//		manager.getDeployment().getFilters().addFilter(f);

		Undertow server = Undertow.builder()
				.addHttpListener(8083, "localhost")
				.setHandler(path)
				.build();
		server.start();

		System.out.println("**************************************");
		System.out.println("**************************** started undertow on port 8083");
		System.out.println("**************************************");
	}

}
