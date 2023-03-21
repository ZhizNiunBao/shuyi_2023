package cn.bywin.business.bean.olk;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "t_olk_object")
public class TOlkObjectDo extends SidEntityDo {

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
     * 上传标志 1已上传 3待上传
     */
    @ApiModelProperty(required = false, value = "上传标志 1已上传 3待上传", hidden = false, example = "")
    @Column(name = "syn_flag")
    private Integer synFlag;
    /**
     * 同步标志 0未同步 1已同步 2待同步
     */
    @ApiModelProperty(required = false, value = "同步标志 0未同步 1已同步 2待同步", hidden = false, example = "")
    @Column(name = "share_flag")
    private Integer shareFlag;
    /**
     * 同步操作时间
     */
    @ApiModelProperty(required = false, value = "同步操作时间", hidden = false, example = "")
    @Column(name = "share_time")
    private Date shareTime;
    /**
     * 数据库id
     */
    @ApiModelProperty(required = true, value = "数据库id", hidden = true, example = "")
    @Column(name = "db_id")
    private String dbId;
    /**
     * 名称
     */
    @ApiModelProperty(required = true, value = "名称", hidden = true, example = "")
    @Column(name = "schema_id")
    private String schemaId;
    /**
     * 表名称
     */
    @ApiModelProperty(required = true, value = "表名称，最大长度(100)", example = "t_department")
    @Column(name = "object_name")
    private String objectName;
    /**
     * 表完整名称
     */
    @ApiModelProperty(required = true, value = "表完整名称", hidden = true, example = "")
    @Column(name = "obj_full_name")
    private String objFullName;
    /**
     * 中文名称
     */
    @ApiModelProperty(required = true, value = "中文名称，最大长度(50)", example = "部门表")
    @Column(name = "obj_chn_name")
    private String objChnName;
    /**
     * 分类
     */
    @ApiModelProperty(required = true, value = "分类", hidden = true, example = "")
    @Column(name = "stype")
    private String stype;
    /**
     * 图标
     */
    @ApiModelProperty(required = true, value = "图标", hidden = true, example = "")
    @Column(name = "icon")
    private String icon;
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
