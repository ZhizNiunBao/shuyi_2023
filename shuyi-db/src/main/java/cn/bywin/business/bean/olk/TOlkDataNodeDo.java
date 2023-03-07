package cn.bywin.business.bean.olk;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_olk_data_node")
public class TOlkDataNodeDo extends SidEntityDo {
    /**
     * 数据id
     */
    @ApiModelProperty(required = false, value = "数据id", hidden = false, example = "")
    @Column(name = "data_id")
    private String dataId;
    /**
     * 数据类型 db表 ds数据集
     */
    @ApiModelProperty(required = false, value = "数据类型 db表 ds数据集", hidden = false, example = "")
    @Column(name = "data_type")
    private String dataType;
    /**
     * 节点id
     */
    @ApiModelProperty(required = false, value = "节点id", hidden = false, example = "")
    @Column(name = "node_id")
    private String nodeId;
    /**
     * 创建人id
     */
    @ApiModelProperty(required = false, value = "创建人id", hidden = false, example = "")
    @Column(name = "creator_id")
    private String creatorId;
    /**
     * 创建人账号
     */
    @ApiModelProperty(required = false, value = "创建人账号", hidden = false, example = "")
    @Column(name = "creator_account")
    private String creatorAccount;
    /**
     * 创建者名称
     */
    @ApiModelProperty(required = false, value = "创建者名称", hidden = false, example = "")
    @Column(name = "creator_name")
    private String creatorName;
}
