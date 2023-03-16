package cn.bywin.business.bean.analysis.olk.template;

import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.FieldSelect_COMPONENT;
import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.Join_COMPONENT;
import static cn.bywin.business.util.analysis.SqlFilterUtils.getMatchedFrom;

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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description 字段选择组件配置
 * @Author wangh
 * @Date 2021-10-20
 */

@Data
public class OlkFieldSelectComponent extends OlkBaseComponenT {

    private List<Operand> operator = new ArrayList<>();
    public OlkFieldSelectComponent() {
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
        truNode.setType(FieldSelect_COMPONENT.getComponentName());
        return truNode;
    }

    @Override
    public boolean changeSameFieldName( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {
        return false;
    }

    @Override
    public OlkCheckComponent check( OlkNode truNode, TOlkModelElementDo fModelElementDo)  throws Exception{
        if (getPreModel().size() != 1) {
            return new OlkCheckComponent(false, "字段设置前置组件个数错误");
        }
        if (getPreModel().stream().filter(e -> e.getRunStatus() == 0).collect(Collectors.toList()).size() > 0) {
            return new OlkCheckComponent(false, "前置组件未保存该组件无法保存，请配置");
        }
        if ( truNode.getViewIds().size()<1) {
            return new OlkCheckComponent(false, "选中字段不能为空");
        }
        return new OlkCheckComponent(true, "success");
    }

    @Override
    public void build( OlkNode truNode, TOlkModelElementDo fModelElementDo, TOlkModelDo  model)  throws Exception{
        TOlkModelElementDo preModel = getPreModel().get( 0 );

        if( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {
            //String preSql = preModel.getRunSql();
            //String newsql = getMatchedFrom(preSql);
            StringBuilder sql = new StringBuilder();
            List<String> thisfields = new ArrayList<>();
            sql.append( "SELECT " );
            Map<String, TOlkModelFieldDo> idFieldMap = truNode.getOperators().getFields().stream().collect( Collectors.toMap( TOlkModelFieldDo::getFieldAlias, e -> e ) );
            truNode.getOperators().getFields().stream().filter( e -> e.getIsSelect() == 1 ).forEach( element -> {
                String fieldName = element.getFieldName();
                if( StringUtils.equals( fieldName,element.getFieldAlias() )){
                    thisfields.add( fieldName );
                }
                else {
                    thisfields.add( fieldName.concat( " AS " ).concat( element.getFieldAlias() ) );
                }
            } );
            sql.append( String.join( " ,\r\n ", thisfields ) );
            sql.append( " FROM \r\n( " );

            sql.append( preModel.getRunSql() ).append( ") AS " ).append( preModel.getElement() );

            fModelElementDo.setRunSql( sql.toString() );
            fModelElementDo.setTableSql( "" );
        }
        else{
            String preSql = preModel.getRunSql();
            String newsql = getMatchedFrom(preSql);
            StringBuffer sql = new StringBuffer();
            List<String> thisfields = new ArrayList<>();
            sql.append( " CREATE VIEW " ).append( fModelElementDo.getElement() ).append( " AS  SELECT \r\n" );
            Map<String, TOlkModelFieldDo> idFieldMap = truNode.getOperators().getFields().stream().collect( Collectors.toMap( TOlkModelFieldDo::getFieldAlias, e -> e ) );
            truNode.getOperators().getFields().stream().filter( e -> e.getIsSelect() == 1 ).forEach( element -> {
                //String fieldName = getDataFieldName( element, idFieldMap );
                String fieldName = element.getFieldName();
                //fieldName = fieldName.replaceAll( element.getTableAlias().concat( "\\." ), "" );
                if ( fieldName.equals( element.getFieldAlias() ) ) {
                    thisfields.add( fieldName );
                }
                else {
                    thisfields.add( fieldName.concat( " AS " ).concat( element.getFieldAlias() ) );
                }
            } );
            sql.append( String.join( " ,\r\n ", thisfields ) );
            sql.append( " FROM " );
            if ( StringUtils.isNotBlank( newsql ) ) {
               sql.append( " \r\n(\r\n " ).append( newsql ).append( " \r\n) as tmp1_" ).append( fModelElementDo.getElement() ).append( " \r\n" );
            }
            else {
                sql.append( getPreModel().get( 0 ).getElement() );
            }
            sql.append( ";\r\n" );
            fModelElementDo.setRunSql( "" );
            fModelElementDo.setTableSql( sql.toString() );
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
