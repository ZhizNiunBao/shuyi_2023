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
@Table(name = "fl_model_data")
public class FModelDataDo extends SidEntityDo {


    /**
     * project_id
     */
    @Column(name = "model_id")
    @ApiModelProperty(required = true, value = "模型ID", hidden = true, example = "")
    private String modelId;

    /**
     * data_id
     */
    @Column(name = "data_id")
    @ApiModelProperty(required = true, value = "数据集id", hidden = true, example = "")
    private String dataId;
    /**
     * types
     */
    @Column(name = "types")
    @ApiModelProperty(required = true, value = "节点类型", hidden = true, example = "")
    private Integer types;
    /**
     * node_id
     */
    @Column(name = "node_id")
    @ApiModelProperty(required = true, value = "节点id", hidden = true, example = "")
    private String nodeId;
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
