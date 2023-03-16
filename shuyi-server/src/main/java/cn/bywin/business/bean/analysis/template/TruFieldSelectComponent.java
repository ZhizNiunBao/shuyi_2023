package cn.bywin.business.bean.analysis.template;

import static cn.bywin.business.bean.analysis.TruComponentEnum.FieldSelect_COMPONENT;
import static cn.bywin.business.bean.analysis.TruComponentEnum.Join_COMPONENT;
import static cn.bywin.business.util.analysis.SqlFilterUtils.getMatchedFrom;

import cn.bywin.business.bean.analysis.TruBaseComponenT;
import cn.bywin.business.bean.analysis.TruCheckComponent;
import cn.bywin.business.bean.bydb.TTruModelComponentDo;
import cn.bywin.business.bean.bydb.TTruModelDo;
import cn.bywin.business.bean.bydb.TTruModelElementDo;
import cn.bywin.business.bean.bydb.TTruModelElementRelDo;
import cn.bywin.business.bean.bydb.TTruModelFieldDo;
import cn.bywin.business.bean.view.bydb.Operand;
import cn.bywin.business.bean.view.bydb.TruNode;
import cn.bywin.business.beanparse.TruSrJdbcWhereCondParse;
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
public class TruFieldSelectComponent extends TruBaseComponenT {

    private List<Operand> operator = new ArrayList<>();
    public TruFieldSelectComponent() {
    }

    @Override
    public List<TTruModelFieldDo> getShowField(List<TTruModelFieldDo> fieldDos)  throws Exception{
        List<TTruModelFieldDo> disData = new ArrayList<>();
        fieldDos.stream().forEach(e -> {
            if (e.getIsSelect() == 1) {
                e.setFieldName(e.getFieldAlias());
                disData.add(e);
            }
        });
        List<TTruModelFieldDo> fieldDoList = disData.stream().sorted(Comparator.comparing(TTruModelFieldDo::getFilterSort)).collect(Collectors.toList());
        return fieldDoList;
    }
    @Override
    public TruNode init( TruNode truNode, TTruModelElementDo fModelElementDo) throws Exception {
        truNode.setType(FieldSelect_COMPONENT.getComponentName());
        return truNode;
    }

    @Override
    public boolean changeSameFieldName( TruNode truNode, TTruModelElementDo fModelElementDo ) throws Exception {
        return false;
    }

    @Override
    public TruCheckComponent check( TruNode truNode, TTruModelElementDo fModelElementDo)  throws Exception{
        if (getPreModel().size() != 1) {
            return new TruCheckComponent(false, "字段设置前置组件个数错误");
        }
        if (getPreModel().stream().filter(e -> e.getRunStatus() == 0).collect(Collectors.toList()).size() > 0) {
            return new TruCheckComponent(false, "前置组件未保存该组件无法保存，请配置");
        }
        if ( truNode.getViewIds().size()<1) {
            return new TruCheckComponent(false, "选中字段不能为空");
        }
        return new TruCheckComponent(true, "success");
    }

    @Override
    public void build( TruNode truNode, TTruModelElementDo fModelElementDo, TTruModelDo  model)  throws Exception{
        TTruModelElementDo preModel = getPreModel().get( 0 );
        String preSql = preModel.getRunSql();
        String newsql = getMatchedFrom(preSql);
        StringBuffer sql = new StringBuffer();
        if( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {
            List<String> thisfields = new ArrayList<>();
            sql.append( "SELECT " );
            Map<String, TTruModelFieldDo> idFieldMap = truNode.getOperators().getFields().stream().collect( Collectors.toMap( TTruModelFieldDo::getFieldAlias, e -> e ) );
            truNode.getOperators().getFields().stream().filter( e -> e.getIsSelect() == 1 ).forEach( element -> {
                String fieldName = TruSrJdbcWhereCondParse.getDataFieldName( element, idFieldMap );
                thisfields.add( fieldName.concat( " AS " ).concat( element.getFieldAlias() ) );
            } );
            sql.append( String.join( ",", thisfields ) );
            sql.append( " FROM " );
            if ( StringUtils.isNotBlank( newsql ) ) {
                sql.append( newsql );
            }
            else {
                sql.append( getPreModel().get( 0 ).getElement() );
            }
            fModelElementDo.setRunSql( sql.toString() );
            fModelElementDo.setTableSql( "" );
        }
        else{
            List<String> thisfields = new ArrayList<>();
            sql.append( " CREATE VIEW " ).append( fModelElementDo.getElement() ).append( " AS  SELECT \r\n" );
            Map<String, TTruModelFieldDo> idFieldMap = truNode.getOperators().getFields().stream().collect( Collectors.toMap( TTruModelFieldDo::getFieldAlias, e -> e ) );
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
    public List<TTruModelFieldDo> relExtends(TTruModelElementRelDo elementInfo)  throws Exception{
        List<TTruModelFieldDo> list = new ArrayList<>();
        Map<String, TTruModelComponentDo> idcomponentDoMap = getComponents().stream().collect(Collectors.toMap(TTruModelComponentDo::getId, e -> e));
        Map<String, TTruModelElementDo> idElementMap = getModel().stream().collect(Collectors.toMap(TTruModelElementDo::getId, e -> e));
        TTruModelComponentDo componentDo = idcomponentDoMap.get(idElementMap.get(elementInfo.getEndElementId()).getTcId());
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
