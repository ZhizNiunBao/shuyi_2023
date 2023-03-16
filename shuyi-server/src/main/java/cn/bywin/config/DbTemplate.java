package cn.bywin.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
@Configuration
public class DbTemplate {
	@Bean
	@Primary
	public JdbcTemplate primaryJdbcTemplate(DataSource dataSource) {
	    return new JdbcTemplate(dataSource);
	}


}
