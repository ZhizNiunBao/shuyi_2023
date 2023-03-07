package cn.bywin;

import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;

import javax.annotation.PostConstruct;
import java.util.TimeZone;


/**
 * @Description 组件初始化
 * @Author wangh
 * @Date 2021-07-30
 */
@EnableAsync
// 使swagger2生效
@EnableSwagger2
@EnableNacosConfig
@EnableTransactionManagement(proxyTargetClass = true)
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@MapperScan(basePackages = { "cn.bywin.business.mapper" })
public class TransformApplication {

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
		SpringApplication app = new SpringApplication(TransformApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
	}

//	@Bean("userConstants")
//	public Constants genConstants(){
//		return new Constants();
//	}
}
