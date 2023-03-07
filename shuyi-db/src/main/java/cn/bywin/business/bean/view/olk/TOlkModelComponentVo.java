package cn.bywin.business.bean.view.olk;

import cn.bywin.business.bean.bydb.TTruModelComponentDo;
import lombok.Data;

import javax.persistence.Entity;
import java.util.List;

@Data
@Entity
public class TOlkModelComponentVo extends TTruModelComponentDo {



    private List<TOlkModelComponentVo> children;

}
