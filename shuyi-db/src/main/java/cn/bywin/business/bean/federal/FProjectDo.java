package cn.bywin.business.bean.federal;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
@Data
@Entity
@Table( name ="fl_project" )
public class FProjectDo extends SidEntityDo {
    /**
     * 项目目名称
     */
    @ApiModelProperty( value = "项目名称", hidden = false, example = "")
    @Column(name = "name" )
    private String name;

    @Column(name = "project")
    @ApiModelProperty( value = "项目编码", hidden = true, example = "")
    private String project;

    /**
     * 描述
     */
    @Column(name = "description" )
    @ApiModelProperty( value = "描述", hidden = false, example = "")
    private String description;


    /**
     *  是否可修改
     */
    @Column(name = "disable" )
    @ApiModelProperty( value = "是否可修改", hidden = true, example = "")
    private Integer disable;

    /**
     * 发起方
     */
    @Column(name = "host" )
    @ApiModelProperty( value = "发起方id", hidden = false, example = "",required = true)
    private String host;

    /**
     * 类型
     */
    @Column(name = "types" )
    @ApiModelProperty( value = "类型", hidden = false, example = "")
    private Integer types;

    /**
     * 创建人ID
     */
    @ApiModelProperty(required = true, value = "创建人ID", hidden = true, example = "")
    @Column(name = "creator_id")
    private String creatorId;
    /**
     * 创建人帐号
     */
    @ApiModelProperty(required = true, value = "创建人帐号", hidden = true, example = "")
    @Column(name = "creator_account")
    private String creatorAccount;
    /**
     * 创建人姓名
     */
    @ApiModelProperty(required = true, value = "创建人姓名", hidden = true, example = "")
    @Column(name = "creator_name")
    private String creatorName;

}
