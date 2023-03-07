package cn.bywin.business.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

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
