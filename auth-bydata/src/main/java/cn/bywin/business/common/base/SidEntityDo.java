package cn.bywin.business.common.base;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.util.List;

public class SidEntityDo extends  BaseEntityDo {

    @Id
    @ApiModelProperty(required = true, value = "主键id，最大长度(32)", example = "009f6ae642c449deb48ed8875040429e")
    @Column(name = "id" )
    protected String id;

    @Transient
    @JsonIgnore
    private List<String> idList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }

}
