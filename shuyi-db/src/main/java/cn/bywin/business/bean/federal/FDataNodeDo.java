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
 * @Date 2022-01-07
 */
@Data
@Entity
@Table(name = "fl_data_node")
public class FDataNodeDo extends SidEntityDo {

    /**
     * 节点id
     */
    @Column(name = "node_id")
    @ApiModelProperty(required = true, value = "节点id", hidden = false, example = "")
    private String nodeId;

    /**
     * 数据集id
     */
    @Column(name = "data_id")
    @ApiModelProperty(required = true, value = "数据集id", hidden = false, example = "")
    private String dataId;




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
