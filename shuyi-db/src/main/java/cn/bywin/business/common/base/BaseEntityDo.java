package cn.bywin.business.common.base;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class BaseEntityDo implements Serializable {




    /**
     * 创建时间
     */
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_time" )
    @ApiModelProperty(value = "创建时间",hidden = true)
    protected Timestamp createdTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "modified_time" )
    @ApiModelProperty(value = "修改时间",hidden = true)
    protected Timestamp modifiedTime;

    @ApiModelProperty(value = "当前页码",hidden = true)
    @Transient
    //@JsonIgnore
    private Integer currentPage = 1; // 当前页码

    @ApiModelProperty(value = "分页大小",hidden = true)
    @Transient
    //@JsonIgnore
    private Integer pageSize;

    @ApiModelProperty(value = "模糊条件",hidden = true)
    @Transient
    //@JsonIgnore
    private String qryCond;

    @ApiModelProperty(value = "其他id",hidden = true)
    @Transient
    @JsonIgnore
    private String otherId;
    @ApiModelProperty(value = "其他id1",hidden = true)
    @Transient
    @JsonIgnore
    private String[] other1;
    @ApiModelProperty(value = "其他id2",hidden = true)
    @Transient
    @JsonIgnore
    private String[] other2;
    @ApiModelProperty(value = "其他id3",hidden = true)
    @Transient
    @JsonIgnore
    private String[] other3;

//    @Transient
//    @JsonIgnore
//    private List<String> idList;


    @ApiModelProperty(value = "开始时间1",hidden = true)
    @Transient
    @JsonIgnore
    private Date sdt1;
    @ApiModelProperty(value = "开始时间2",hidden = true)
    @Transient
    @JsonIgnore
    private Date sdt2;

    @ApiModelProperty(value = "结束时间1",hidden = true)
    @Transient
    @JsonIgnore
    private Date edt1;

    @ApiModelProperty(value = "结束时间2",hidden = true)
    @Transient
    @JsonIgnore
    private Date edt2;

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public Timestamp getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Timestamp modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    //@JsonIgnore
    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    //@JsonIgnore
    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    //@JsonIgnore
    public String getQryCond() {
        return qryCond;
    }

    public void setQryCond(String qryCond) {
        this.qryCond = qryCond;
    }

    @JsonIgnore
    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public String[] getOther1() {
        return other1;
    }

    public void setOther1(String[] other1) {
        this.other1 = other1;
    }

    public String[] getOther2() {
        return other2;
    }

    public void setOther2(String[] other2) {
        this.other2 = other2;
    }

    public String[] getOther3() {
        return other3;
    }

    public void setOther3(String[] other3) {
        this.other3 = other3;
    }


    public Date getSdt1() {
        return sdt1;
    }

    public void setSdt1(Date sdt1) {
        this.sdt1 = sdt1;
    }

    public Date getSdt2() {
        return sdt2;
    }

    public void setSdt2(Date sdt2) {
        this.sdt2 = sdt2;
    }

    public Date getEdt1() {
        return edt1;
    }

    public void setEdt1(Date edt1) {
        this.edt1 = edt1;
    }

    public Date getEdt2() {
        return edt2;
    }

    public void setEdt2(Date edt2) {
        this.edt2 = edt2;
    }

    @JsonIgnore
    public void genPage( ) {

        if(currentPage == null || currentPage<=0)
            currentPage=1;

        if(pageSize == null || pageSize<=0)
            pageSize=10;

    }
    @JsonIgnore
    public void genPage( long totalCnt ) {
        genPage();
        int maxPage =  (int)totalCnt/getPageSize() ;
        if( totalCnt> maxPage* getPageSize() )
            maxPage ++;
        //System.out.println("maxPage:"+maxPage+",CurrentPage:"+getCurrentPage()+"" );
        if( maxPage < getCurrentPage() ){
            setCurrentPage( maxPage );
        }
        genPage();
    }
    @JsonIgnore
    public String getPageInfo() {

        if(currentPage != null && currentPage>0   &&  pageSize != null)
            return "limit " + ( (currentPage -1) * pageSize ) +"," + pageSize ;
        else
            return "";
    }
}
