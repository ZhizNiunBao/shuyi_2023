package cn.bywin.business.bean.analysis.olk.template;

import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.FieldFunc_COMPONENT;
import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.Join_COMPONENT;
import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.SqlOperator_COMPONENT;

import cn.bywin.business.bean.analysis.olk.OlkBaseComponenT;
import cn.bywin.business.bean.analysis.olk.OlkCheckComponent;
import cn.bywin.business.bean.olk.TOlkModelComponentDo;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.bean.response.sqloperator.OlkModelOperatorElementRelVo;
import cn.bywin.business.bean.response.sqloperator.OlkTableVo;
import cn.bywin.business.bean.view.olk.OlkNode;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OlkSqlOperatorComponent extends OlkBaseComponenT {
    @Override
    public OlkNode init( OlkNode truNode, TOlkModelElementDo fModelElementDo) throws Exception {
        truNode.setType(SqlOperator_COMPONENT.getComponentName());
        return truNode;
    }

    @Override
    public void build(OlkNode truNode, TOlkModelElementDo fModelElementDo, TOlkModelDo model)
        throws Exception {

        List<TOlkModelElementDo> preModel = getPreModel();
        List<OlkModelOperatorElementRelVo> list = ((List<OlkModelOperatorElementRelVo>) truNode.getParams().get("inCodeElementRel"));

        String runSql = (String) truNode.getParams().get("operatorSql");
        List<OlkTableVo> olkTableVos = (List<OlkTableVo>) truNode.getParams().get("tableRegex");

        for (OlkTableVo olkTableVo : olkTableVos) {
            runSql = runSql.replaceAll(olkTableVo.getTableRegex(), olkTableVo.getTableFullName());
        }

        for (OlkModelOperatorElementRelVo olkModelOperatorElementRelVo : list) {
            for (TOlkModelElementDo tOlkModelElementDo : preModel) {
                if (olkModelOperatorElementRelVo.getInElementId().equals(tOlkModelElementDo.getId())) {
                    runSql = runSql.replaceAll("\\$\\{" + olkModelOperatorElementRelVo.getInCode() + "}","(" + tOlkModelElementDo.getRunSql() + ")");
                }
            }
        }

        fModelElementDo.setRunSql(runSql);
        fModelElementDo.setTableSql("");



    }

    @Override
    public boolean changeSameFieldName(OlkNode truNode, TOlkModelElementDo fModelElementDo)
        throws Exception {
        return false;
    }

    @Override
    public OlkCheckComponent check(OlkNode truNode, TOlkModelElementDo fModelElementDo)
        throws Exception {
        if (getPreModel().stream().filter(e -> e.getRunStatus() == 0).collect(Collectors.toList()).size() > 0) {
            return new OlkCheckComponent(false, "前置组件未保存该组件无法保存，请配置");
        }
        return new OlkCheckComponent(true, "success");
    }


    @Override
    public List<TOlkModelFieldDo> relExtends(TOlkModelElementRelDo elementInfo)
        throws Exception {
        List<TOlkModelFieldDo> list = new ArrayList<>();
        Map<String, TOlkModelComponentDo> idcomponentDoMap = getComponents().stream().collect( Collectors.toMap( TOlkModelComponentDo::getId, e -> e ) );
        Map<String, TOlkModelElementDo> idElementMap = getModel().stream().collect( Collectors.toMap( TOlkModelElementDo::getId, e -> e ) );
        TOlkModelComponentDo componentDo = idcomponentDoMap.get( idElementMap.get( elementInfo.getEndElementId() ).getTcId() );
        getExtendsDos().stream().forEach( tBydbModelFieldDo -> {
            tBydbModelFieldDo.setId( ComUtil.genId() );
            if ( tBydbModelFieldDo.getIsSelect() == 0 ) {
                tBydbModelFieldDo.setIsSelect( -1 );
            }
            if ( componentDo != null && componentDo.getComponentEn().equals( Join_COMPONENT.getComponentName() ) ) {
                tBydbModelFieldDo.setTableAlias( idElementMap.get( elementInfo.getStartElementId() ).getElement() );
            }
            tBydbModelFieldDo.setElementId( elementInfo.getEndElementId() );
            tBydbModelFieldDo.setExtendsId( elementInfo.getStartElementId() );
            list.add( tBydbModelFieldDo );

        } );
        return list;
    }

    @Override
    public List<TOlkModelFieldDo> getShowField(List<TOlkModelFieldDo> fieldDos) throws Exception {
        List<TOlkModelFieldDo> disData = new ArrayList<>();
        fieldDos.stream().forEach(e -> {
            if (e.getIsSelect() == 1) {
                e.setFieldName(e.getFieldAlias());
                disData.add(e);
            }
        });
        List<TOlkModelFieldDo> fieldDoList = disData.stream().sorted(Comparator.comparing(TOlkModelFieldDo::getFilterSort)).collect(Collectors.toList());
        return fieldDoList;
    }

}
