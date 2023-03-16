package cn.bywin.business.bean.analysis.olk.template;

import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.FieldFunc_COMPONENT;
import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.Join_COMPONENT;

import cn.bywin.business.bean.analysis.olk.OlkBaseComponenT;
import cn.bywin.business.bean.analysis.olk.OlkCheckComponent;
import cn.bywin.business.bean.olk.TOlkModelComponentDo;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.bean.view.bydb.Operand;
import cn.bywin.business.bean.view.olk.OlkNode;
import cn.bywin.business.common.util.ComUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description 字段合并组件配置
 * @Author wangh
 * @Date 2021-10-20
 */

@Data
public class OlkFieldFuncComponent extends OlkBaseComponenT {

    @Data
    public class Operator {
        private List<Operand> operand;
        private String alias;
        private String aliasType;
        private String operator;
    }

    private List<Operator> operator = new ArrayList<>();
    public OlkFieldFuncComponent() {
    }

    @Override
    public List<TOlkModelFieldDo> getShowField(List<TOlkModelFieldDo> fieldDos)  throws Exception{
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

    @Override
    public OlkNode init( OlkNode truNode, TOlkModelElementDo fModelElementDo) throws Exception {
        truNode.setType(FieldFunc_COMPONENT.getComponentName());
        return truNode;
    }

    @Override
    public boolean changeSameFieldName( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {
        return false;
    }

    @Override
    public OlkCheckComponent check( OlkNode truNode, TOlkModelElementDo fModelElementDo)  throws Exception{
        Pattern pat = Pattern.compile( "^[a-zA-Z]\\w*$",Pattern.CASE_INSENSITIVE );
        if (getPreModel().size() != 1) {
            return new OlkCheckComponent(false, "计算行前置组件个数错误");
        }
        if (getPreModel().stream().filter(e -> e.getRunStatus() == 0).collect(Collectors.toList()).size() > 0) {
            return new OlkCheckComponent(false, "前置组件未保存该组件无法保存，请配置");
        }
        if ( truNode.getViewIds().size() < 1) {
            return new OlkCheckComponent(false, "选中字段不能为空");
        }
        List<TOlkModelFieldDo> fieldList = truNode.getOperators().getFields();
        HashMap<String,String> nameMap = new HashMap();
        for ( TOlkModelFieldDo fieldDo : fieldList ) {
            if(StringUtils.isBlank( fieldDo.getFieldAlias() )){
                return new OlkCheckComponent(false, "字段别名不能为空");
            }
            if(fieldDo.getOrigFlag()!= null && fieldDo.getOrigFlag() !=1 &&  StringUtils.isBlank( fieldDo.getOrderFunc() )){
                return new OlkCheckComponent(false, "增加字段函数不能为空");
            }
            if(nameMap.containsKey( fieldDo.getFieldAlias() )){
                return new OlkCheckComponent(false, fieldDo.getFieldAlias()+",字段别名重名");
            }
            Matcher matcher = pat.matcher( fieldDo.getFieldAlias() );
            if( !matcher.find() ){
                return new OlkCheckComponent(false, fieldDo.getFieldAlias()+"不合法,字段别名只能是字母开头，包含字母、数字、下划线");
            }
            nameMap.put( fieldDo.getFieldAlias(),fieldDo.getFieldAlias() );

        }
        return new OlkCheckComponent(true, "success");
    }

    @Override
    public void build( OlkNode truNode, TOlkModelElementDo fModelElementDo, TOlkModelDo model)  throws Exception{
        List<String> thisfields = new ArrayList<>();
        Map<String, TOlkModelFieldDo> idFieldMap = truNode.getOperators().getFields().stream().collect(Collectors.toMap(TOlkModelFieldDo::getFieldAlias, e -> e));
        if( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {
            String preSql = getPreModel().get(0).getRunSql();
            //String newsql = getMatchedFrom(preSql);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT ");
            truNode.getOperators().getFields().stream().filter( e -> e.getIsSelect() == 1).forEach( element -> {
                //String fieldName = getDataFieldName( element,  idFieldMap);
                if( StringUtils.isNotBlank( element.getOrderFunc() ) ) {
                    thisfields.add( String.format( "%s AS %s", element.getOrderFunc(), element.getFieldAlias() ) );
                }
                else{
                    if(StringUtils.equals(  element.getFieldName(), element.getFieldAlias())){
                        thisfields.add(element.getFieldName());
                    }
                    else {
                        thisfields.add( String.format( "%s AS %s", element.getFieldName(), element.getFieldAlias() ) );
                    }
                }
            });
            sql.append(String.join(",", thisfields));
            sql.append("\r\n FROM (\r\n");
            sql.append(preSql).append( ") AS " ).append( fModelElementDo.getElement() ).append( "\r\n" );
            fModelElementDo.setRunSql(sql.toString());
        }
        else{
            //String preSql = getPreModel().get(0).getRunSql();
            //String newsql = getMatchedFrom(preSql);
            StringBuffer sql = new StringBuffer();
            sql.append("CREATE VIEW ").append( "" ).append( fModelElementDo.getElement() ).append( " AS SELECT \r\n");
            truNode.getOperators().getFields().stream().filter( e -> e.getIsSelect() == 1).forEach( element -> {
                //String fieldName = getDataFieldName( element,  idFieldMap);
                if( StringUtils.isNotBlank( element.getOrderFunc() ) ) {
                    thisfields.add( String.format( "%s AS %s", element.getOrderFunc(), element.getFieldAlias() ) );
                }
                else{
                    if(StringUtils.equals(  element.getFieldName(), element.getFieldAlias())){
                        thisfields.add(element.getFieldName());
                    }
                    else {
                        thisfields.add( String.format( "%s AS %s", element.getFieldName(), element.getFieldAlias() ) );
                    }
                }
            });
            sql.append(String.join(" ,\r\n ", thisfields));
            sql.append(" FROM ");
            sql.append(getPreModel().get(0).getElement());
            sql.append( ";\r\n" );
            fModelElementDo.setRunSql("");
            fModelElementDo.setTableSql(sql.toString());
        }
    }


    @Override
    public List<TOlkModelFieldDo> relExtends(TOlkModelElementRelDo elementInfo)  throws Exception{
        List<TOlkModelFieldDo> list = new ArrayList<>();
        Map<String, TOlkModelComponentDo> idcomponentDoMap = getComponents().stream().collect(Collectors.toMap(TOlkModelComponentDo::getId, e -> e));
        Map<String, TOlkModelElementDo> idElementMap = getModel().stream().collect(Collectors.toMap(TOlkModelElementDo::getId, e -> e));
        TOlkModelComponentDo componentDo = idcomponentDoMap.get(idElementMap.get(elementInfo.getEndElementId()).getTcId());
        getExtendsDos().stream().forEach(tBydbModelFieldDo -> {
            tBydbModelFieldDo.setId( ComUtil.genId());
            if (tBydbModelFieldDo.getIsSelect() == 0) {
                tBydbModelFieldDo.setIsSelect(-1);
            }
            if (componentDo != null && componentDo.getComponentEn().equals(Join_COMPONENT.getComponentName())) {
                tBydbModelFieldDo.setTableAlias(idElementMap.get(elementInfo.getStartElementId()).getElement());
            }
            tBydbModelFieldDo.setElementId(elementInfo.getEndElementId());
            tBydbModelFieldDo.setExtendsId(elementInfo.getStartElementId());
            list.add(tBydbModelFieldDo);

        });
        return list;
    }
}
