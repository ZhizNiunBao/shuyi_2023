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
@Table( name ="t_tru_model_component" )
public class TTruModelComponentDo extends SidEntityDo {
    /**
     * 组件
     */
    @ApiModelProperty(hidden = false, value = "组件编码，最大长度(32)", example = "")
    @Column( name = "component" )
    private String component;
    /**
     * 组件英文
     */
    @ApiModelProperty(hidden = false, value = "组件英文，最大长度(32)", example = "")
    @Column( name = "component_en" )
    private String componentEn;
    /**
     * 组件名
     */
    @ApiModelProperty(hidden = false,required = true, value = "组件名，最大长度(32)", example = "")
    @Column( name = "name" )
    private String name;
    /**
     * 
     */
    @ApiModelProperty(hidden = true, required = true,value = "父级节点，最大长度(255)", example = "")
    @Column( name = "parent" )
    private String parent;
    /**
     * 详情
     */
    @ApiModelProperty(hidden = false, value = "组件详情，最大长度(255)", example = "")
    @Column( name = "description" )
    private String description;
    /**
     * 类型
     */
    @ApiModelProperty(hidden = true, value = "类型"  ,example = "" )
    @Column( name = "types" )
    private Integer types;
    /**
     * 状态 1 为启用 0 为 下线 2 为 不启用
     */
    @ApiModelProperty(hidden = false, value = "状态 1 为启用 0 为 下线 2 为 不启用"  ,example = "" )
    @Column( name = "status" )
    private Integer status;
    /**
     * 排序
     */
    @ApiModelProperty(hidden = false, value = "排序"  ,example = "" )
    @Column( name = "sorts" )
    private Integer sorts;
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
    /**
     * 
     */
    @ApiModelProperty( value = "层级" ,required = true ,example = "" ,hidden = true)
    @Column( name = "level" )
    private Integer level;
}
