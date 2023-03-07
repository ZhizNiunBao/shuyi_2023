package cn.bywin.business.bean.view.bydb;

import cn.bywin.business.bean.bydb.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TBydbModelVo {
    private TTruModelDo modelDo;
    private List<TBydbObjectDo> objectDos;
    private List<TTruModelElementVo> elements;
    private List<TTruModelElementRelDo> elementRels;
    private List<Map<String, String>> fields;
}
