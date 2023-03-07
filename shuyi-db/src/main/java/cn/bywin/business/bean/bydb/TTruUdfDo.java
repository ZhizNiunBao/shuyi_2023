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
@Table(name = "t_tru_udf")
public class TTruUdfDo extends SidEntityDo {
    /**
     * 
     */
    @ApiModelProperty(required = false, value = "", hidden = false, example = "")
    @Column(name = "belong_type")
    private String belongType;
    /**
     * 数据库类型
     */
    @ApiModelProperty(required = false, value = "数据库类型", hidden = false, example = "")
    @Column(name = "db_type")
    private String dbType;
    /**
     * 分类
     */
    @ApiModelProperty(required = false, value = "分类", hidden = false, example = "")
    @Column(name = "catalog_no")
    private String catalogNo;
    /**
     * 函数名称
     */
    @ApiModelProperty(required = false, value = "函数名称", hidden = false, example = "")
    @Column(name = "function_name")
    private String functionName;
    /**
     * SQL函数
     */
    @ApiModelProperty(required = false, value = "SQL函数", hidden = false, example = "")
    @Column(name = "sql_fun")
    private String sqlFun;
    /**
     * TABLE函数
     */
    @ApiModelProperty(required = false, value = "TABLE函数", hidden = false, example = "")
    @Column(name = "table_fun")
    private String tableFun;
    /**
     * 中文名称
     */
    @ApiModelProperty(required = false, value = "中文名称", hidden = false, example = "")
    @Column(name = "chn_name")
    private String chnName;
    /**
     * md文件
     */
    @ApiModelProperty(required = false, value = "md文件", hidden = false, example = "")
    @Column(name = "md_file")
    private String mdFile;
    /**
     * 说明
     */
    @ApiModelProperty(required = false, value = "说明", hidden = false, example = "")
    @Column(name = "remark")
    private String remark;
    /**
     * 函数类型
     */
    @ApiModelProperty(required = false, value = "函数类型", hidden = false, example = "")
    @Column(name = "function_type")
    private String functionType;
    /**
     * 定义
     */
    @ApiModelProperty(required = false, value = "定义", hidden = false, example = "")
    @Column(name = "function_define")
    private String functionDefine;
    /**
     * 参数类型
     */
    @ApiModelProperty(required = false, value = "参数类型", hidden = false, example = "")
    @Column(name = "argument_type")
    private String argumentType;
    /**
     * 参数是说明
     */
    @ApiModelProperty(required = false, value = "参数是说明", hidden = false, example = "")
    @Column(name = "argument_desc")
    private String argumentDesc;
    /**
     * 返回类型
     */
    @ApiModelProperty(required = false, value = "返回类型", hidden = false, example = "")
    @Column(name = "return_type")
    private String returnType;
    /**
     * 是否变长
     */
    @ApiModelProperty(required = false, value = "是否变长", hidden = false, example = "")
    @Column(name = "variable_arity")
    private String variableArity;
    /**
     * 描述
     */
    @ApiModelProperty(required = false, value = "描述", hidden = false, example = "")
    @Column(name = "description")
    private String description;
    /**
     * 语言
     */
    @ApiModelProperty(required = false, value = "语言", hidden = false, example = "")
    @Column(name = "slanguage")
    private String slanguage;
    /**
     * 内置
     */
    @ApiModelProperty(required = false, value = "内置", hidden = false, example = "")
    @Column(name = "built_in")
    private String builtIn;
    /**
     * 确定性
     */
    @ApiModelProperty(required = false, value = "确定性", hidden = false, example = "")
    @Column(name = "sdeterministic")
    private String sdeterministic;
    /**
     * 图标
     */
    @ApiModelProperty(required = false, value = "图标", hidden = false, example = "")
    @Column(name = "icon")
    private String icon;
    /**
     * 
     */
    @ApiModelProperty(required = false, value = "", hidden = false, example = "")
    @Column(name = "example")
    private String example;
    /**
     * 排序
     */
    @ApiModelProperty(required = false, value = "排序", hidden = false, example = "")
    @Column(name = "norder")
    private Integer norder;
    /**
     * 是否启用 1启用 0 不启用
     */
    @ApiModelProperty(required = false, value = "是否启用 1启用 0 不启用", hidden = false, example = "")
    @Column(name = "enable")
    private Integer enable;
    /**
     * 创建人ID
     */
    @ApiModelProperty(required = false, value = "创建人ID", hidden = false, example = "")
    @Column(name = "creator_id")
    private String creatorId;
    /**
     * 创建人帐号
     */
    @ApiModelProperty(required = false, value = "创建人帐号", hidden = false, example = "")
    @Column(name = "creator_account")
    private String creatorAccount;
    /**
     * 创建人姓名
     */
    @ApiModelProperty(required = false, value = "创建人姓名", hidden = false, example = "")
    @Column(name = "creator_name")
    private String creatorName;
}
