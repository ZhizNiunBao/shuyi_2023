package cn.bywin.business.bean.view.bydb;

import cn.bywin.business.bean.bydb.TBydbObjectDo;
import cn.bywin.business.bean.bydb.TTruModelDo;
import cn.bywin.business.bean.olk.TOlkModelDo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OlkModelVo extends TOlkModelDo{
    //private TOlkModelDo modelDo;
    //private List<TBydbObjectDo> objectDos;
    //private List<TBydbModelElementVo> elements;
    //private List<TBydbModelElementRelDo> elementRels;
    //private List<Map<String, String>> fields;
    private String dcName;
    private String dcCode;
}
