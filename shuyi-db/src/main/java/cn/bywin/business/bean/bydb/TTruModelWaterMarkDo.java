package cn.bywin.business.bean.bydb;

import cn.bywin.business.mapper.bydb.TTruModelWaterMarkMapper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name ="t_tru_model_water_mark" )
public class TTruModelWaterMarkDo {
    @Id
    @Column( name = "element_id" )
    private String elementId;

    @Column( name = "water_mark_internal" )
    private Integer waterMarkInternal;

    @Column( name = "water_mark_unit" )
    private String waterMarkUnit;

    @Column( name = "water_mark_for" )
    private String waterMarkFor;

    @Override
    public String toString() {
        return "TTruModelWaterMarkDo{" +
                "elementId='" + elementId + '\'' +
                ", waterMarkInternal=" + waterMarkInternal +
                ", waterMarkUnit='" + waterMarkUnit + '\'' +
                ", waterMarkFor='" + waterMarkFor + '\'' +
                '}';
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public Integer getWaterMarkInternal() {
        return waterMarkInternal;
    }

    public void setWaterMarkInternal(Integer waterMarkInternal) {
        this.waterMarkInternal = waterMarkInternal;
    }

    public String getWaterMarkUnit() {
        return waterMarkUnit;
    }

    public void setWaterMarkUnit(String waterMarkUnit) {
        this.waterMarkUnit = waterMarkUnit;
    }

    public String getWaterMarkFor() {
        return waterMarkFor;
    }

    public void setWaterMarkFor(String waterMarkFor) {
        this.waterMarkFor = waterMarkFor;
    }
}
