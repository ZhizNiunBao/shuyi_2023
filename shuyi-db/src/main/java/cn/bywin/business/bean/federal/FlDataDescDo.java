package cn.bywin.business.bean.federal;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "fl_data_desc")
public class FlDataDescDo extends SidEntityDo {
    /**
     * 创建人帐号
     */
    @ApiModelProperty(required = false, value = "创建人帐号", hidden = false, example = "")
    @Column(name = "creator_account")
    private String creatorAccount;
    /**
     * 创建人ID
     */
    @ApiModelProperty(required = false, value = "创建人ID", hidden = false, example = "")
    @Column(name = "creator_id")
    private String creatorId;
    /**
     * 创建人姓名
     */
    @ApiModelProperty(required = false, value = "创建人姓名", hidden = false, example = "")
    @Column(name = "creator_name")
    private String creatorName;
    /**
     * 数据主键
     */
    @ApiModelProperty(required = false, value = "数据主键", hidden = false, example = "")
    @Column(name = "data_id")
    private String dataId;

    /**
     * 排序
     */
    @ApiModelProperty(required = false, value = "排序", hidden = false, example = "")
    @Column(name = "eda_order")
    private Integer edaOrder;
    /**
     * eda类型
     */
    @ApiModelProperty(required = false, value = "eda类型", hidden = false, example = "")
    @Column(name = "eda_type")
    private String edaType;
    /**
     * eda值
     */
    @ApiModelProperty(required = false, value = "eda值", hidden = false, example = "")
    @Column(name = "eda_value")
    private String edaValue;
    /**
     * 字段名称
     */
    @ApiModelProperty(required = false, value = "字段名称", hidden = false, example = "")
    @Column(name = "field_name")
    private String fieldName;
    /**
     * 指标
     */
    @ApiModelProperty(required = false, value = "指标", hidden = false, example = "")
    @Column(name = "indicator")
    private String indicator;
    /**
     * 注释
     */
    @ApiModelProperty(required = false, value = "注释", hidden = false, example = "")
    @Column(name = "remark")
    private String remark;
}
