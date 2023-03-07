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
@Table( name ="t_tru_model_field" )
public class TTruModelFieldDo extends SidEntityDo {
    /**
     * 模型ID
     */
    @ApiModelProperty( value = "模型ID"  ,example = "" ,hidden = true,required = true )
    @Column( name = "element_id" )
    private String elementId;

    /**
     * 继承字段的上级节点
     */
    @ApiModelProperty(required = true, value = "继承字段的上级节点", hidden = false, example = "")
    @Column(name = "extends_id" )
    private String extendsId;

    @ApiModelProperty(required = true, value = "来源字段id", hidden = false, example = "")
    @Column(name = "from_field_id" )
    private String fromFieldId;
    /**
     * 表id
     */
    @ApiModelProperty( value = "表id"  ,example = "" ,hidden = true,required = true )
    @Column( name = "table_id" )
    private String tableId;
    /**
     * 
     */
    @ApiModelProperty( value = ""  ,example = "" ,hidden = false)
    @Column( name = "field_expr" )
    private String fieldExpr;
    /**
     * 字段别名
     */
    @ApiModelProperty( value = "字段别名"  ,example = "" ,hidden = false,required = true )
    @Column( name = "field_alias" )
    private String fieldAlias;
    /**
     * 继承所属表别名
     */
    @ApiModelProperty( value = "继承所属表别名"  ,example = "", hidden = false  )
    @Column( name = "table_alias" )
    private String tableAlias;
    /**
     * 字段名称
     */
    @ApiModelProperty( value = "字段名称"  ,example = "" ,hidden = false,required = true )
    @Column( name = "field_name" )
    private String fieldName;
    /**
     * 
     */
    @ApiModelProperty( value = ""  ,example = "", hidden = false  )
    @Column( name = "aggregation" )
    private String aggregation;
    /**
     * 排序方式 desc asc
     */
    @ApiModelProperty( value = "排序方式 desc asc"  ,example = "" , hidden = true )
    @Column( name = "order_func" )
    private String orderFunc;
    /**
     * 类型默认值
     */
    @ApiModelProperty( value = "类型默认值"  ,example = "",hidden = false,required = true  )
    @Column( name = "field_type" )
    private String fieldType;

    @ApiModelProperty( value = "类型默认值"  ,example = "",hidden = false,required = true  )
    @Column( name = "orig_field_type" )
    private String origFieldType;

    @ApiModelProperty( value = "原字段 1是 0增加"  ,example = "",hidden = false,required = true  )
    @Column( name = "orig_flag" )
    private Integer origFlag;

    /**
     * 类型默认值
     */
    @ApiModelProperty( value = "对应java类型类型"  ,example = "",hidden = false,required = true  )
    @Column( name = "column_type" )
    private String columnType;

    /**
     * 过滤条件
     */
    @ApiModelProperty( value = "过滤条件"  ,example = "" , hidden = false )
    @Column( name = "filter_config" )
    private String filterConfig;
    /**
     * 和前一个字段过滤 连接方式 （and、or 等）
     */
    @ApiModelProperty( value = "和前一个字段过滤 连接方式 （and、or 等）"  ,example = "", hidden = true  )
    @Column( name = "filter_type" )
    private String filterType;
    /**
     * 是否启用筛选 1 是 0 否
     */
    @ApiModelProperty( value = "是否启用筛选 1 是 0 否"  ,example = "", hidden = true  )
    @Column( name = "filter_status" )
    private Integer filterStatus;
    /**
     * 过滤字段排序
     */
    @ApiModelProperty( value = "过滤字段排序"  ,example = "", hidden = true  )
    @Column( name = "filter_sort" )
    private Integer filterSort;
    /**
     * 过滤筛选值
     */
    @ApiModelProperty( value = "过滤筛选值"  ,example = "", hidden = false  )
    @Column( name = "filter_value" )
    private String filterValue;
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
    @ApiModelProperty( value = ""  ,example = "", hidden = false )
    @Column( name = "field_length" )
    private String fieldLength;
    /**
     * 是否选用 1 是 0 否
     */
    @ApiModelProperty( value = "是否选用 1 是 0 否"  ,example = "" ,hidden = false,required = true)
    @Column( name = "is_select" )
    private Integer isSelect;
}
