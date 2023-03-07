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
@Table(name = "t_tru_model_object")
public class TTruModelObjectDo extends SidEntityDo {

    /**
     * 
     */
    @ApiModelProperty(required = true, value = "", hidden = true, example = "")
    @Column(name = "object_id")
    private String objectId;
    /**
     * 对象分类 db表 ds数据集
     */
    @ApiModelProperty(required = true, value = "对象分类 db表 ds数据集", hidden = true, example = "")
    @Column(name = "stype")
    private String stype;
    /**
     * 
     */
    @ApiModelProperty(required = true, value = "", hidden = false, example = "")
    @Column(name = "real_obj_id")
    private String realObjId;
    /**
     * 
     */
    @ApiModelProperty(required = true, value = "", hidden = true, example = "")
    @Column(name = "model_id")
    private String modelId;
    /**
     * 
     */
//    @ApiModelProperty(required = true, value = "", hidden = true, example = "")
//    @Column(name = "dc_id")
//    private String dcId;
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
    @ApiModelProperty(required = true, value = "表名称", hidden = false, example = "")
    @Column(name = "object_name")
    private String objectName;
    /**
     * 表完整名称
     */
    @ApiModelProperty(required = true, value = "表完整名称", hidden = false, example = "")
    @Column(name = "obj_full_name")
    private String objFullName;
    /**
     * 中文名称
     */
    @ApiModelProperty(required = true, value = "中文名称", hidden = false, example = "")
    @Column(name = "obj_chn_name")
    private String objChnName;
    /**
     * 图标
     */
    @ApiModelProperty(required = true, value = "图标", hidden = false, example = "")
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
    @ApiModelProperty(required = true, value = "是否起用 1是 0否", hidden = true, example = "")
    @Column(name = "enable")
    private Integer enable;

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
     * 上传标志 1已上传 3待上传
     */
    @ApiModelProperty(required = false, value = "上传标志 1已上传 3待上传", hidden = false, example = "")
    @Column(name = "syn_flag")
    private Integer synFlag;

    /**
     * 分享时间
     */
//    @ApiModelProperty(required = true, value = "分享时间", hidden = true, example = "")
//    @Column(name = "share_date")
//    private Date shareDate;
    /**
     * 分享标志 1分享 0未分享
     */
//    @ApiModelProperty(required = true, value = "分享标志 1分享 0未分享", hidden = true, example = "")
//    @Column(name = "share_flag")
//    private Integer shareFlag;
    /**
     * 审核标志
     */
//    @ApiModelProperty(required = true, value = "审核标志", hidden = true, example = "")
//    @Column(name = "check_flag")
//    private Integer checkFlag;
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
