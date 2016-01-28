package hello;

import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.security.wss4j.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j.callback.SimplePasswordValidationCallbackHandler;
import org.springframework.ws.soap.security.xwss.callback.SpringPlainTextPasswordValidationCallbackHandler;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.List;
import java.util.Properties;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
	@Bean
	public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean(servlet, "/ws/*");
	}

	@Bean(name = "countries")
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("CountriesPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://spring.io/guides/gs-producing-web-service");
		wsdl11Definition.setSchema(countriesSchema);
		return wsdl11Definition;
	}

	@Bean
	public XsdSchema countriesSchema() {
		return new SimpleXsdSchema(new ClassPathResource("countries.xsd"));
	}

	/**
	 * The wss user name interceptor
     */
	@Bean
	public Wss4jSecurityInterceptor wss4jSecurityInterceptor() {
		Wss4jSecurityInterceptor interceptor = new Wss4jSecurityInterceptor();
		interceptor.setValidationActions("UsernameToken");
		interceptor.setValidationCallbackHandler(simplePasswordValidationCallbackHandler());
		return interceptor;
	}

	/**
	 *  Adds a simple username validator callback
     */
	@Bean
	public SimplePasswordValidationCallbackHandler simplePasswordValidationCallbackHandler() {
		SimplePasswordValidationCallbackHandler simplePasswordValidationCallbackHandler = new SimplePasswordValidationCallbackHandler();
		Properties properties = new Properties();
		properties.setProperty("user", "pass");
		simplePasswordValidationCallbackHandler.setUsers(properties);
		return simplePasswordValidationCallbackHandler;
	}

	/**
	 * Adds security interceptor who validate headers
     */
	@Override
	public void addInterceptors(List<EndpointInterceptor> interceptors) {
		interceptors.add(wss4jSecurityInterceptor());
	}

/*
(NOT USED) A username pass callback handler that uses a {@link AuthenticationManager}
	@Bean
	public SpringPlainTextPasswordValidationCallbackHandler springSecurityPasswordValidationCallbackHandler() {
		SpringPlainTextPasswordValidationCallbackHandler handler = new SpringPlainTextPasswordValidationCallbackHandler();
		handler.setAuthenticationManager(authentication -> new UsernamePasswordAuthenticationToken("", ""));
		return handler;
	}
*/

}
