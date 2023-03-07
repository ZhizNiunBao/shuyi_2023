package cn.bywin.business.bean.federal;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Description
 * @Author wangh
 * @Date 2021-07-27
 */
@Data
@Entity
@Table(name = "fl_component")
public class FComponentDo extends SidEntityDo {
    /**
     * 名称
     */
    @ApiModelProperty(required = true, value = " 组件中文", hidden = false, example = "")
    @Column(name = "name")
    private String name;

    @ApiModelProperty(required = true, value = " 组件编码", hidden = true, example = "")
    @Column(name = "component")
    private String component;

    @Column(name = "component_en")
    @ApiModelProperty(required = true, value = " 组件英文", hidden = false, example = "")
    private String componentEn;

    /**
     * 描述
     */
    @Column(name = "description")
    @ApiModelProperty(required = true, value = "组件描述", hidden = false, example = "")
    private String description;
    /**
     * 组件 md 地址
     */
    @Column(name = "md_path")
    @ApiModelProperty(required = true, value = "组件md地址", hidden = false, example = "")
    private String mdPath;


    /**
     * 纵向横向 区分 1为纵向 0为横向
     */
    @Column(name = "component_type")
    @ApiModelProperty(required = true, value = " 纵向横向：1为纵向 0为横向", hidden = false, example = "")
    private String componentType;
    /**
     * 状态
     */
    @Column(name = "status")
    @ApiModelProperty(value = "状态", hidden = false, example = "")
    private Integer status;
    /**
     * pid
     */
    @Column(name = "pid")
    @ApiModelProperty(value = "pid", hidden = false, example = "")
    private String pid;


    /**
     * 排序
     */
    @Column(name = "sorts")
    @ApiModelProperty(value = "排序", hidden = true, example = "")
    private Integer sorts;

    /**
     * 类型
     */
    @Column(name = "types")
    @ApiModelProperty(value = "类型", hidden = false, example = "")
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
