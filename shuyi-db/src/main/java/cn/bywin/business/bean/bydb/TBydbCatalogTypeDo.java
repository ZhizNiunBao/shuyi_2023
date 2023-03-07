package cn.bywin.business.bean.bydb;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_bydb_catalog_type")
public class TBydbCatalogTypeDo extends SidEntityDo {
    /**
     * 上级id
     */
    @ApiModelProperty(required = false, value = "上级id，最大长度(36)", hidden = false, example = "")
    @Column(name = "pid")
    private String pid;
    /**
     * 节点id
     */
    //@ApiModelProperty(required = false, value = "节点id，最大长度(36)", hidden = true, example = "")
    //@Column(name = "dc_id")
    //private String dcId;
    /**
     * 归属用户
     */
    @ApiModelProperty(required = true, value = "归属用户，最大长度(50)", hidden = true, example = "")
    @Column(name = "user_account")
    private String userAccount;
    /**
     * 归属用户名称
     */
    @ApiModelProperty(required = true, value = "归属用户名称", hidden = true, example = "")
    @Column(name = "user_account_name")
    private String userAccountName;
    /**
     * 名称
     */
    @ApiModelProperty(required = true, value = "名称，最大长度(20)", hidden = false, example = "分组")
    @Column(name = "type_name")
    private String typeName;
    /**
     * 图片样例
     */
    //@ApiModelProperty(required = false, value = "图片样例", hidden = true, example = "")
    //@Column(name = "img_name")
    //private String imgName;
    /**
     * 排序
     */
    //@ApiModelProperty(required = false, value = "排序", hidden = true, example = "")
    //@Column(name = "norder")
    //private Integer norder;
    /**
     * 标签
     */
    @ApiModelProperty(required = false, value = "标签", hidden = false, example = "")
    @Column(name = "tags")
    private String tags;
    /**
     * 备注
     */
    @ApiModelProperty(required = false, value = "备注", hidden = false, example = "")
    @Column(name = "remark")
    private String remark;
    /**
     * 归属部门编号
     */
    @ApiModelProperty(required = false, value = "归属部门编号", hidden = true, example = "")
    @Column(name = "user_dept_no")
    private String userDeptNo;
    /**
     * 归属部门名称
     */
    @ApiModelProperty(required = false, value = "归属部门名称", hidden = true, example = "")
    @Column(name = "user_dept_na")
    private String userDeptNa;
    /**
     * 创建人ID
     */
    @ApiModelProperty(required = false, value = "创建人ID", hidden = true, example = "")
    @Column(name = "creator_id")
    private String creatorId;
    /**
     * 创建人帐号
     */
    @ApiModelProperty(required = false, value = "创建人帐号", hidden = true, example = "")
    @Column(name = "creator_account")
    private String creatorAccount;
    /**
     * 创建人姓名
     */
    @ApiModelProperty(required = false, value = "创建人姓名", hidden = true, example = "")
    @Column(name = "creator_name")
    private String creatorName;
    /**
     *  位置编码
     */
    @ApiModelProperty(required = false, value = " 位置编码", hidden = true, example = "")
    @Column(name = "rel_code")
    private String relCode;
}
