package cn.bywin.business.bean.analysis.olk.template;

import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.FieldFilter_COMPONENT;
import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.Join_COMPONENT;

import cn.bywin.business.bean.analysis.olk.OlkBaseComponenT;
import cn.bywin.business.bean.analysis.olk.OlkCheckComponent;
import cn.bywin.business.bean.olk.TOlkModelComponentDo;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.bean.view.olk.OlkNode;
import cn.bywin.business.beanparse.OlkSrJdbcWhereCondParse;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
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

public class OlkFieldFilterComponent extends OlkBaseComponenT {

    public OlkFieldFilterComponent() {
    }

    @Override
    public List<TOlkModelFieldDo> getShowField( List<TOlkModelFieldDo> fieldDos ) throws Exception {
        List<TOlkModelFieldDo> disData = new ArrayList<>();
        fieldDos.stream().forEach( e -> {
            if ( e.getIsSelect() == 1 ) {
                e.setFieldName( e.getFieldAlias() );
                disData.add( e );
            }
        } );
        List<TOlkModelFieldDo> fieldDoList = disData.stream().sorted( Comparator.comparing( TOlkModelFieldDo::getFilterSort ) ).collect( Collectors.toList() );

        return fieldDoList;
    }

    @Override
    public OlkNode init( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {
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
    public boolean changeSameFieldName( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {
        return false;
    }

    @Override
    public OlkCheckComponent check( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {
        if ( getPreModel().size() != 1 ) {
            return new OlkCheckComponent( false, "字段过滤前置组件个数错误" );
        }
        if ( getPreModel().stream().filter( e -> e.getRunStatus() == 0 ).collect( Collectors.toList() ).size() > 0 ) {
            return new OlkCheckComponent( false, "前置组件未保存该组件无法保存，请配置" );
        }

        return new OlkCheckComponent( true, "success" );
    }

    @Override
    public void build( OlkNode truNode, TOlkModelElementDo fModelElementDo, TOlkModelDo model ) throws Exception {
        TOlkModelElementDo preModel = getPreModel().get( 0 );
        String preSql = preModel.getRunSql();
        fModelElementDo.setRunSql( preSql );
        fModelElementDo.setTableSql( "" );
        if ( truNode.getParams() != null && StringUtils.isNotBlank( truNode.getParams().toString() ) ) {
            if ( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {
                OlkSrJdbcWhereCondParse whereCondParse = new OlkSrJdbcWhereCondParse();
                List<TOlkModelFieldDo> fieldList = new ArrayList<>();
                for ( TOlkModelFieldDo tOlkModelFieldDo : truNode.getOperators().getFields() ) {
                    TOlkModelFieldDo cp = new TOlkModelFieldDo();
                    MyBeanUtils.copyBeanNotNull2Bean( tOlkModelFieldDo,cp );
                    //cp.setTableAlias( cp.getTableAlias()+"_tmp" );
                    cp.setTableAlias( preModel.getElement() );
                    fieldList.add(  cp );
                }
                if ( whereCondParse.paraseComCond( truNode.getParams().toString(), getModel(), fieldList ) ) {
                    String whereSql = whereCondParse.getWhereSql();
                    if ( StringUtils.isNotBlank( whereSql ) ) {
                        List<String> thisfields = new ArrayList<>();
                        StringBuilder sql = new StringBuilder();
                        sql.append("SELECT ");
                        truNode.getOperators().getFields().stream().filter( e -> e.getIsSelect() == 1).forEach( element -> {
                            if(StringUtils.equals(  element.getFieldName(), element.getFieldAlias())){
                                thisfields.add(element.getFieldName());
                            }
                            else {
                                thisfields.add( String.format( "%s AS %s", element.getFieldName(), element.getFieldAlias() ) );
                            }
                        });
                        sql.append(String.join(",", thisfields));
                        sql.append("\r\n FROM (\r\n");
                        sql.append(preSql).append( ") AS " ).append( preModel.getElement() ).append( "\r\n" );
                        sql.append( " WHERE " ).append(  whereSql ).append( "\r\n" );
                        fModelElementDo.setRunSql(sql.toString());
                        fModelElementDo.setTableSql( "" );
                    }
                }
            }
//            else {
//                FlinkWhereCondParse whereCondParse = new FlinkWhereCondParse();
//                if ( whereCondParse.paraseComCond( truNode.getParams().toString(), getModel(), truNode.getOperators().getFields() ) ) {
//                    String whereSql = whereCondParse.getWhereSql();
//                    if ( StringUtils.isNotBlank( whereSql ) ) {
//                        StringBuffer sql = new StringBuffer();
//                        List<String> flist = truNode.getOperators().getFields().stream().filter( x -> x.getIsSelect() > 0 ).map( x -> x.getFieldName() ).collect( Collectors.toList() );
//
//                        sql.append( "CREATE VIEW " ).append( fModelElementDo.getElement() ).append( " AS SELECT " )
//                                .append( String.join( " ,\r\n ", flist ) ).append( " FROM " );
//                        if ( StringUtils.isNotBlank( news ) ) {
//                            sql.append( "\r\n(\r\n" ).append( news ).append( "\r\n)\r\n " ).append( " AS tmp1_" ).append( fModelElementDo.getElement() );
//                        }
//                        else {
//                            sql.append( preModel.getElement() );
//                        }
//
//                        sql.append( "\r\n WHERE \r\n" );
//
//                        whereSql = whereSql.replaceAll( fModelElementDo.getElement().concat( "\\." ), "" );
//                        sql.append( whereSql ).append( ";\r\n" );
//                        fModelElementDo.setRunSql( "" );
//                        fModelElementDo.setTableSql( sql.toString() );
//                    }
//                }
//            }
        }
        fModelElementDo.setConfig( JsonUtil.toJson( truNode.getParams() ).replace( "\n", "" ) );
    }

    @Override
    public List<TOlkModelFieldDo> relExtends( TOlkModelElementRelDo elementInfo ) throws Exception {
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
}
