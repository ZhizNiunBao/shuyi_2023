package cn.bywin.business.bean.olk;

import cn.bywin.business.common.base.SidEntityDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_olk_catalog_type")
public class TOlkCatalogTypeDo extends SidEntityDo {
    /**
     * 上级id
     */
    @ApiModelProperty(value = "上级id，最大长度(32)", hidden = true, example = "")
    @Column(name = "pid")
    private String pid;
    /**
     * 节点id
     */
    @ApiModelProperty(value = "节点id，最大长度(32)", hidden = true, example = "")
    @Column(name = "dc_id")
    private String dcId;
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
     * 归属部门编号
     */
    @ApiModelProperty(required = true, value = "归属部门编号", hidden = false, example = "")
    @Column(name = "user_dept_no")
    private String userDeptNo;
    /**
     * 归属部门名称
     */
    @ApiModelProperty(required = true, value = "归属部门名称", hidden = true, example = "")
    @Column(name = "user_dept_na")
    private String userDeptNa;

    /**
     * 名称
     */
    @ApiModelProperty(required = true, value = "名称，最大长度(20)", example = "分组")
    @Column(name = "type_name")
    private String typeName;
    /**
     * 图片样例
     */
//    @ApiModelProperty(value = "图片样例", hidden = true, example = "")
//    @Column(name = "img_name")
//    private String imgName;

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
     * 排序
     */
    @ApiModelProperty(value = "排序", hidden = true, example = "")
    @Column(name = "norder")
    private Integer norder;
    /**
     * 创建人ID
     */
    @ApiModelProperty(value = "创建人ID", hidden = true, example = "")
    @Column(name = "creator_id")
    private String creatorId;
    /**
     * 创建人帐号
     */
    @ApiModelProperty(value = "创建人帐号", hidden = true, example = "")
    @Column(name = "creator_account")
    private String creatorAccount;
    /**
     * 创建人姓名
     */
    @ApiModelProperty(value = "创建人姓名", hidden = true, example = "")
    @Column(name = "creator_name")
    private String creatorName;
    /**
     * 位置编码
     */
    @ApiModelProperty(value = " 位置编码", hidden = true, example = "")
    @Column(name = "rel_code")
    private String relCode;
}
