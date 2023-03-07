package cn.bywin.business.bean.bydb;

import cn.bywin.business.common.base.SidEntityDo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @Description  
 * @Author  
 * @Date 2021-11-21 
 */
@Data
@Entity
@Table ( name ="t_bydb_log" )
public class TBydbLogDo  extends SidEntityDo {

	/**
	 * topic
	 */
	@ApiModelProperty(required = true, value = "topicname", hidden = true, example = "")
	@Column(name = "topic_name" )
	private String topicName;

	/**
	 * partition
	 */
	@ApiModelProperty(required = true, value = "partition", hidden = true, example = "")
	@Column(name = "partition_no" )
	private String partitionNo;

	/**
	 * offset_no
	 */
	@ApiModelProperty(required = true, value = "offset_no", hidden = true, example = "")
	@Column(name = "offset_no" )
	private String offsetNo;

	@ApiModelProperty(required = true, value = "hostname", hidden = true, example = "")
	@Column(name = "hostname" )
	private String hostname;

	@ApiModelProperty(required = true, value = "用户ip", hidden = true, example = "")
	@Column(name = "user_ip" )
	private String userIp;

	@ApiModelProperty(required = true, value = "user_name", hidden = true, example = "")
	@Column(name = "user_name" )
	private String userName;

	@ApiModelProperty(required = true, value = "query_id", hidden = true, example = "")
	@Column(name = "query_id" )
	private String queryId;

	@ApiModelProperty(required = true, value = "message", hidden = true, example = "")
	@Column(name = "message" )
	private String message;

	@ApiModelProperty(required = true, value = "message_stmt", hidden = true, example = "")
	@Column(name = "message_stmt" )
	private String messageStmt;

	@ApiModelProperty(required = true, value = "message_time", hidden = true, example = "")
	@Column(name = "message_time" )
	private String messageTime;

	@ApiModelProperty(required = true, value = "message_status", hidden = true, example = "")
	@Column(name = "message_status" )
	private String messageStatus;

	@ApiModelProperty(required = true, value = "log_path", hidden = true, example = "")
	@Column(name = "log_path" )
	private String logPath;

	@JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(required = true, value = "kafka_time", hidden = true, example = "")
	@Column(name = "kafka_time" )
	private Timestamp kafkaTime;


}
