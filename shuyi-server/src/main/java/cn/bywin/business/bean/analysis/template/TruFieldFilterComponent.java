package cn.bywin.business.bean.analysis.template;

import static cn.bywin.business.bean.analysis.TruComponentEnum.FieldFilter_COMPONENT;
import static cn.bywin.business.bean.analysis.TruComponentEnum.Join_COMPONENT;

import cn.bywin.business.bean.analysis.TruBaseComponenT;
import cn.bywin.business.bean.analysis.TruCheckComponent;
import cn.bywin.business.bean.bydb.TTruModelComponentDo;
import cn.bywin.business.bean.bydb.TTruModelDo;
import cn.bywin.business.bean.bydb.TTruModelElementDo;
import cn.bywin.business.bean.bydb.TTruModelElementRelDo;
import cn.bywin.business.bean.bydb.TTruModelFieldDo;
import cn.bywin.business.bean.view.bydb.TruNode;
import cn.bywin.business.beanparse.FlinkWhereCondParse;
import cn.bywin.business.beanparse.TruSrJdbcWhereCondParse;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.util.analysis.MapTypeAdapter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description 字段过滤组件配置
 * @Author wangh
 * @Date 2021-10-20
 */

public class TruFieldFilterComponent extends TruBaseComponenT {

    public TruFieldFilterComponent() {
    }

    @Override
    public List<TTruModelFieldDo> getShowField( List<TTruModelFieldDo> fieldDos ) throws Exception {
        List<TTruModelFieldDo> disData = new ArrayList<>();
        fieldDos.stream().forEach( e -> {
            if ( e.getIsSelect() == 1 ) {
                e.setFieldName( e.getFieldAlias() );
                disData.add( e );
            }
        } );
        List<TTruModelFieldDo> fieldDoList = disData.stream().sorted( Comparator.comparing( TTruModelFieldDo::getFilterSort ) ).collect( Collectors.toList() );

        return fieldDoList;
    }

    @Override
    public TruNode init( TruNode truNode, TTruModelElementDo fModelElementDo ) throws Exception {
        if ( StringUtils.isNotBlank( fModelElementDo.getConfig() ) ) {
            truNode.setParams( MapTypeAdapter.gsonToMap( fModelElementDo.getConfig() ) );
        }
        else {
            truNode.setParams( null );
        }
        truNode.setType( FieldFilter_COMPONENT.getComponentName() );
        return truNode;
    }

    @Override
    public boolean changeSameFieldName( TruNode truNode, TTruModelElementDo fModelElementDo ) throws Exception {
        return false;
    }

    @Override
    public TruCheckComponent check( TruNode truNode, TTruModelElementDo fModelElementDo ) throws Exception {
        if ( getPreModel().size() != 1 ) {
            return new TruCheckComponent( false, "字段过滤前置组件个数错误" );
        }
        if ( getPreModel().stream().filter( e -> e.getRunStatus() == 0 ).collect( Collectors.toList() ).size() > 0 ) {
            return new TruCheckComponent( false, "前置组件未保存该组件无法保存，请配置" );
        }

        return new TruCheckComponent( true, "success" );
    }

    @Override
    public void build( TruNode truNode, TTruModelElementDo fModelElementDo, TTruModelDo model ) throws Exception {
        TTruModelElementDo preModel = getPreModel().get( 0 );
        String news = preModel.getRunSql();
        fModelElementDo.setRunSql( news );

        if ( truNode.getParams() != null && StringUtils.isNotBlank( truNode.getParams().toString() ) ) {


            if ( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {
                TruSrJdbcWhereCondParse whereCondParse = new TruSrJdbcWhereCondParse();
                if ( whereCondParse.paraseComCond( truNode.getParams().toString(), getModel(), truNode.getOperators().getFields() ) ) {
                    String whereSql = whereCondParse.getWhereSql();
                    if ( StringUtils.isNotBlank( whereSql ) ) {
                        StringBuffer sql = new StringBuffer( news );
                        sql.append( " AND " );
                        sql.append( whereSql );
                        fModelElementDo.setRunSql( sql.toString() );
                        fModelElementDo.setTableSql( "" );
                    }
                }
            }
            else {
                FlinkWhereCondParse whereCondParse = new FlinkWhereCondParse();
                if ( whereCondParse.paraseComCond( truNode.getParams().toString(), getModel(), truNode.getOperators().getFields() ) ) {
                    String whereSql = whereCondParse.getWhereSql();
                    if ( StringUtils.isNotBlank( whereSql ) ) {
                        StringBuffer sql = new StringBuffer();
                        List<String> flist = truNode.getOperators().getFields().stream().filter( x -> x.getIsSelect() > 0 ).map( x -> x.getFieldName() ).collect( Collectors.toList() );

                        sql.append( "CREATE VIEW " ).append( fModelElementDo.getElement() ).append( " AS SELECT " )
                                .append( String.join( " ,\r\n ", flist ) ).append( " FROM " );
                        if ( StringUtils.isNotBlank( news ) ) {
                            sql.append( "\r\n(\r\n" ).append( news ).append( "\r\n)\r\n " ).append( " AS tmp1_" ).append( fModelElementDo.getElement() );
                        }
                        else {
                            sql.append( preModel.getElement() );
                        }

                        sql.append( "\r\n WHERE \r\n" );

                        whereSql = whereSql.replaceAll( fModelElementDo.getElement().concat( "\\." ), "" );
                        sql.append( whereSql ).append( ";\r\n" );
                        fModelElementDo.setRunSql( "" );
                        fModelElementDo.setTableSql( sql.toString() );
                    }
                }
            }
        }
        fModelElementDo.setConfig( JsonUtil.toJson( truNode.getParams() ).replace( "\n", "" ) );
    }

    @Override
    public List<TTruModelFieldDo> relExtends( TTruModelElementRelDo elementInfo ) throws Exception {
        List<TTruModelFieldDo> list = new ArrayList<>();
        Map<String, TTruModelComponentDo> idcomponentDoMap = getComponents().stream().collect( Collectors.toMap( TTruModelComponentDo::getId, e -> e ) );
        Map<String, TTruModelElementDo> idElementMap = getModel().stream().collect( Collectors.toMap( TTruModelElementDo::getId, e -> e ) );
        TTruModelComponentDo componentDo = idcomponentDoMap.get( idElementMap.get( elementInfo.getEndElementId() ).getTcId() );
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
}
