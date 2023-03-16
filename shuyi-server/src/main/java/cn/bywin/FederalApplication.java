package cn.bywin;

import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;


/**
 * @Description 组件初始化
 * @Author wangh
 * @Date 2021-07-30
 */
@EnableAsync
@EnableNacosConfig
// 使swagger2生效
@EnableSwagger2
@EnableFeignClients
@EnableTransactionManagement(proxyTargetClass = true)
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@MapperScan(basePackages = { "cn.bywin.business.mapper" })
public class FederalApplication {

	@PostConstruct
	void setDefaultTimezone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
		//TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
	}
//	@PostConstruct
//	void started() {
//		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//	}

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(FederalApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
	}

/*	@Bean
	public ServletWebServerFactory servletContainer() {

		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {

			@Override
			protected void postProcessContext(Context context) {

				SecurityConstraint securityConstraint = new SecurityConstraint();
				securityConstraint.setUserConstraint("CONFIDENTIAL");
				SecurityCollection collection = new SecurityCollection();
				collection.addPattern("/*");
				securityConstraint.addCollection(collection);
				context.addConstraint(securityConstraint);
			}
		};
		tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
		return tomcat;
	}

	*//**
	 * 让我们的应用支持HTTP是个好想法，但是需要重定向到HTTPS，
	 * 但是不能同时在application.yml中同时配置两个connector，
	 * 所以要以编程的方式配置HTTP connector，然后重定向到HTTPS connector
	 * @return Connector
	 *//*
	private Connector initiateHttpConnector() {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme("http");
		connector.setPort(19191); // http端口
		connector.setSecure(false);
		connector.setRedirectPort(9191); // application.yml中配置的https端口
		return connector;
	}*/

//	@Bean("userConstants")
//	public Constants genConstants(){
//		return new Constants();
//	}
}
