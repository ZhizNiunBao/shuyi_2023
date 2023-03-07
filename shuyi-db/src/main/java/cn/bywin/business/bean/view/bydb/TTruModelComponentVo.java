package cn.bywin.business.bean.view.bydb;

import cn.bywin.business.bean.bydb.TTruModelComponentDo;
import lombok.Data;

import javax.persistence.Entity;
import java.util.List;

@Data
@Entity
public class TTruModelComponentVo extends TTruModelComponentDo {



    private List<TTruModelComponentVo> children;

}
