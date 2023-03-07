package cn.bywin.business.bean.view.federal;

import cn.bywin.business.bean.federal.FComponentDo;
import lombok.Data;

import java.util.List;
@Data
public class FComponentVo  extends FComponentDo  {



    private List<FComponentVo> children;

}
