package cn.bywin.business.bean.system;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table( name ="sys_log" )
@Data
public class SysLogDo extends SidEntityDo {

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    @Column(name = "title")
    private String title;
    /**
     * 内容
     */
    @ApiModelProperty("内容")
    @Column(name = "content")
    private String content;
    /**
     * 状态
     */
    @ApiModelProperty("状态")
    @Column(name = "status")
    private Integer status;

    /**
     * 创建人ID
     */
    @ApiModelProperty("创建人ID")
    @Column(name = "creator_id")
    private String creatorId;

    /**
     * 创建人帐号
     */
    @ApiModelProperty("创建人帐号")
    @Column(name = "creator_account")
    private String creatorAccount;

    /**
     * 创建人姓名
     */
    @ApiModelProperty("创建人姓名")
    @Column(name = "creator_name")
    private String creatorName;
}
