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
@Table( name ="fl_model" )
public class FModelDo extends SidEntityDo {
    /**
     *  模型名称
     */
    @ApiModelProperty(required = false, value = "模型名称", hidden = false, example = "")
    @Column(name = "name" )
    private String name;

    @ApiModelProperty(required = false, value = "模型编码", hidden = false, example = "")
    @Column(name = "model")
    private String model;

    /**
     * 描述
     */
    @Column(name = "description" )
    @ApiModelProperty(required = false, value = "描述", hidden = false, example = "")
    private String description;

    /**
     * 状态
     */
    @Column(name = "status" )
    @ApiModelProperty(value = "状态", hidden = true, example = "")
    private Integer status;

    /**
     *  是否可修改
     */
    @Column(name = "disable" )
    @ApiModelProperty(required = true, value = "是否可修改", hidden = true, example = "")
    private Integer disable;

    /**
     * 项目id
     */
    @ApiModelProperty(required = true, value = "项目id", hidden = true, example = "")
    @Column(name = "project_id")
    private String projectId;


    /**
     *  模型dsl 配置
     */
    @Column(name = "config")
    @ApiModelProperty(required = false, value = "模型dsl 配置", hidden = false, example = "")
    private String config;

    /**
     * 是否为模板
     */
    @Column(name = "has_template" )
    @ApiModelProperty(required = true, value = "是否为模板", hidden = true, example = "")
    private Integer hasTemplate;
    /**
     * 类型
     */
    @Column(name = "types" )
    @ApiModelProperty(required = false, value = "类型 1 为纵向 0 为横向", hidden = false, example = "")
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
