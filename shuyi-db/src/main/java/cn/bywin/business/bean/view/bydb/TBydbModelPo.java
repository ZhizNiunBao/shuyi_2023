package cn.bywin.business.bean.view.bydb;

import cn.bywin.business.bean.bydb.TTruModelDo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TBydbModelPo extends  TTruModelDo{

    private List<Map<String, String>> fields;
}
