package cn.bywin.business.bean.bydb;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "t_tru_model_element_rel")
public class TTruModelElementRelDo extends SidEntityDo {
    /**
     * 创建人账号
     */
    @ApiModelProperty(required = false, value = "创建人账号", hidden = false, example = "")
    @Column(name = "creator_account")
    private String creatorAccount;
    /**
     * 创建人ID
     */
    @ApiModelProperty(required = false, value = "创建人ID", hidden = false, example = "")
    @Column(name = "creator_id")
    private String creatorId;
    /**
     * 创建了姓名
     */
    @ApiModelProperty(required = false, value = "创建了姓名", hidden = false, example = "")
    @Column(name = "creator_name")
    private String creatorName;
    /**
     * 目标元素ID
     */
    @ApiModelProperty(required = false, value = "目标元素ID", hidden = false, example = "")
    @Column(name = "end_element_id")
    private String endElementId;
    /**
     * 终点瞄点
     */
    @ApiModelProperty(required = false, value = "终点瞄点", hidden = false, example = "")
    @Column(name = "end_port_id")
    private String endPortId;
    /**
     * 模型ID
     */
    @ApiModelProperty(required = false, value = "模型ID", hidden = false, example = "")
    @Column(name = "model_id")
    private String modelId;
    /**
     * 起始元素ID
     */
    @ApiModelProperty(required = false, value = "起始元素ID", hidden = false, example = "")
    @Column(name = "start_element_id")
    private String startElementId;
    /**
     * 起始瞄点
     */
    @ApiModelProperty(required = false, value = "起始瞄点", hidden = false, example = "")
    @Column(name = "start_port_id")
    private String startPortId;
}
