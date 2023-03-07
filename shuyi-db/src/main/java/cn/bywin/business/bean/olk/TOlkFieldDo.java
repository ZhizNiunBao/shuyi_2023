package cn.bywin.business.bean.olk;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_olk_field")
public class TOlkFieldDo extends SidEntityDo {
    /**
     * 节点id
     */
    @ApiModelProperty(value = "节点id，最大长度(32)", hidden = true, example = "")
    @Column(name = "dc_id")
    private String dcId;
    /**
     * 节点id
     */
    @ApiModelProperty(required = false, value = "节点id", hidden = false, example = "")
    @Column(name = "node_party_id")
    private String nodePartyId;
    /**
     * 归属账号
     */
    @ApiModelProperty(required = false, value = "归属账号", hidden = false, example = "")
    @Column(name = "user_account")
    private String userAccount;
    /**
     * 归属用户id
     */
    @ApiModelProperty(required = false, value = "归属用户id", hidden = false, example = "")
    @Column(name = "user_id")
    private String userId;
    /**
     * 归属用户名
     */
    @ApiModelProperty(required = false, value = "归属用户名", hidden = false, example = "")
    @Column(name = "user_name")
    private String userName;
    /**
     * 数据库id
     */
    @ApiModelProperty(required = true, value = "数据库id", hidden = true, example = "")
    @Column(name = "db_id")
    private String dbId;
    /**
     * 模式id
     */
    @ApiModelProperty(required = true, value = "模式id", hidden = true, example = "")
    @Column(name = "schema_id")
    private String schemaId;
    /**
     * 表id
     */
    @ApiModelProperty(required = true, value = "表id", hidden = true, example = "")
    @Column(name = "object_id")
    private String objectId;
    /**
     * 字段名称
     */
    @ApiModelProperty(required = true, value = "字段名称，最大长度(50)", example = "dept_name")
    @Column(name = "field_name")
    private String fieldName;
    /**
     * 字段全名称
     */
    @ApiModelProperty(required = true, value = "字段全名称，最大长度(250)", hidden = true, example = "")
    @Column(name = "field_full_name")
    private String fieldFullName;
    /**
     * 中文名称
     */
    @ApiModelProperty(required = true, value = "中文名称，最大长度(50)", example = "部门名称")
    @Column(name = "chn_name")
    private String chnName;
    @ApiModelProperty(required = true, value = "原始字段类型，最大长度(20)", example = "varchar(30)")
    @Column(name = "col_type")
    private String colType;
    /**
     * 字段类型
     */
    @ApiModelProperty(required = true, value = "字段类型，最大长度(20)", example = "varchar(30)")
    @Column(name = "field_type")
    private String fieldType;
    /**
     * 字段长度
     */
    @ApiModelProperty(required = true, value = "字段长度", hidden = true, example = "")
    @Column(name = "field_length")
    private Long fieldLength;
    /**
     * 字段精度
     */
    @ApiModelProperty(required = true, value = "字段精度", hidden = true, example = "")
    @Column(name = "field_precision")
    private Integer fieldPrecision;
    /**
     * 默认转化
     */
    @ApiModelProperty(required = true, value = "默认转化，最大长度(50)", example = "")
    @Column(name = "chg_statement")
    private String chgStatement;
    /**
     * 函数分类
     */
    @ApiModelProperty(required = true, value = "函数分类", hidden = true, example = "")
    @Column(name = "fun_type")
    private String funType;
    /**
     * 
     */
    @ApiModelProperty(required = true, value = "", hidden = true, example = "")
    @Column(name = "replace_statement")
    private String replaceStatement;
    /**
     * 排序
     */
    @ApiModelProperty(required = true, value = "排序", hidden = true, example = "")
    @Column(name = "norder")
    private Integer norder;
    /**
     * 是否起用 1是 0否
     */
    @ApiModelProperty(required = true, value = "是否起用 1是 0否", example = "1")
    @Column(name = "enable")
    private Integer enable;

    /**
     * 删除标志 1已删除
     */
    @ApiModelProperty(required = false, value = "删除标志 1已删除", hidden = true, example = "0")
    @Column(name = "del_flag")
    private Integer delFlag;

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
