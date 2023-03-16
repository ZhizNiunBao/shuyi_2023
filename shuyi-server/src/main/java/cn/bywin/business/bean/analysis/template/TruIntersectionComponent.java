package cn.bywin.business.bean.analysis.template;

import static cn.bywin.business.bean.analysis.TruComponentEnum.Intersect_COMPONENT;

import cn.bywin.business.bean.analysis.TruBaseComponenT;
import cn.bywin.business.bean.analysis.TruCheckComponent;
import cn.bywin.business.bean.bydb.TTruModelDo;
import cn.bywin.business.bean.bydb.TTruModelElementDo;
import cn.bywin.business.bean.bydb.TTruModelElementRelDo;
import cn.bywin.business.bean.bydb.TTruModelFieldDo;
import cn.bywin.business.bean.view.bydb.Operand;
import cn.bywin.business.bean.view.bydb.TruNode;
import cn.bywin.business.beanparse.TruSrJdbcWhereCondParse;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.util.analysis.MapTypeAdapter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description 交集组件配置
 * @Author wangh
 * @Date 2021-10-20
 */

@Data
public class TruIntersectionComponent extends TruBaseComponenT {


    private List<Operand> operator = new ArrayList<>();

    private List<String> collectionTable = new ArrayList<>();
    private String collectionType = "INTERSECT";


    public TruIntersectionComponent() {
    }

    @Override
    public List<TTruModelFieldDo> getShowField(List<TTruModelFieldDo> fieldDos) throws Exception {
        List<TTruModelFieldDo> disData = new ArrayList<>();
        fieldDos.stream().forEach(e -> {
            if (e.getIsSelect() == 1&&StringUtils.isNotBlank(e.getFilterValue())) {
                e.setFieldName(e.getFieldAlias());
                disData.add(e);
            }
        });
        List<TTruModelFieldDo> fieldDoList = disData.stream().sorted(Comparator.comparing(TTruModelFieldDo::getFilterSort)).collect(Collectors.toList());

        return fieldDoList;
    }


    @Override
    public TruNode init( TruNode truNode, TTruModelElementDo fModelElementDo) throws Exception {
        if (StringUtils.isNotBlank(fModelElementDo.getConfig())) {
            Map<String, Object> config = MapTypeAdapter.gsonToMap(fModelElementDo.getConfig());
            TruIntersectionComponent collectionComponent = JsonUtil.gson().fromJson(JsonUtil.toJson(config), TruIntersectionComponent.class);

            config.put("collectionTable", collectionComponent.getCollectionTable());
            truNode.setParams(config);
        } else {//连接配置初始化
            Map<String, Object> params = new HashMap<>(5);
            collectionTable = getPreModel().stream().sorted(Comparator.comparing(TTruModelElementDo::getCreatedTime)).map(e -> e.getId()).collect(Collectors.toList());
            params.put("collectionTable", collectionTable);
            params.put("operator", operator);
            truNode.setParams(params);
        }
        Map<String, TTruModelElementDo> nameElementMap = getPreModel().stream().collect(Collectors.toMap(TTruModelElementDo::getId, e -> e));
        if (getPreModel().size() > 0) {
            truNode.getOperators().getFields().stream().forEach( e -> {
                e.setElementId(e.getExtendsId());
                e.setTableId(nameElementMap.get(e.getElementId()).getName());
            });
        }
        truNode.setType(Intersect_COMPONENT.getComponentName());
        return truNode;
    }

    @Override
    public boolean changeSameFieldName( TruNode truNode, TTruModelElementDo fModelElementDo ) throws Exception {
        return false;
    }

    @Override
    public TruCheckComponent check( TruNode truNode, TTruModelElementDo fModelElementDo) throws Exception {
        TruIntersectionComponent collectionComponent = JsonUtil.gson().fromJson(JsonUtil.toJson( truNode.getParams()), TruIntersectionComponent.class);

        if (getPreModel().size() != 2) {
            return new TruCheckComponent(false, "前置组件个数错误");
        }
        if (getPreModel().stream().filter(e -> e.getRunStatus() == 0).collect(Collectors.toList()).size() > 0) {
            return new TruCheckComponent(false, "前置组件未保存该组件无法保存，请配置");
        }
        if ( truNode.getViewIds().size() < 1) {
            return new TruCheckComponent(false, "选中字段不能为空");
        }
        List<TTruModelFieldDo> operatorList = truNode.getOperators().getFields();
        for (TTruModelFieldDo e:operatorList){
            if (e.getIsSelect() == 1 && e.getExtendsId().equals(collectionComponent.getCollectionTable().get(0))
                    &&StringUtils.isBlank(e.getFilterValue())) {
                return new TruCheckComponent(false, "表之间选中字段不能为空");
            }
        }
        return new TruCheckComponent(true, "success");
    }

    @Override
    public void build( TruNode truNode, TTruModelElementDo fModelElementDo, TTruModelDo model) throws Exception {
        TruIntersectionComponent collectionComponent = JsonUtil.gson().fromJson( JsonUtil.toJson( truNode.getParams() ), TruIntersectionComponent.class );


        Map<String, TTruModelElementDo> idElementMap = getPreModel().stream().collect( Collectors.toMap( TTruModelElementDo::getId, e -> e ) );
        TTruModelElementDo left = idElementMap.get( collectionComponent.getCollectionTable().get( 0 ) );
        TTruModelElementDo right = idElementMap.get( collectionComponent.getCollectionTable().get( 1 ) );
        //获取组件的字段
        List<String> leftFieldDos = new ArrayList<>();
        List<String> rightFieldDos = new ArrayList<>();
        String leftpre = left.getElement();
        String rightpre = right.getElement();
        if( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {
            StringBuffer runSql = new StringBuffer();
            StringBuffer sql = new StringBuffer();
            Map<String, TTruModelFieldDo> idFieldMap = truNode.getOperators().getFields().stream()
                    .filter( v -> v.getElementId().equals( right.getId() ) ).collect( Collectors.toMap( TTruModelFieldDo::getFieldAlias, e -> e ) );
            truNode.getOperators().getFields().stream().filter( e -> e.getIsSelect() == 1 &&
                    truNode.getViewIds().contains( e.getId() ) ).forEach(
                    e -> {
                        if ( left.getId().equals( e.getElementId() ) ) {
                            TTruModelFieldDo leftField = e;
                            TTruModelFieldDo rightField = idFieldMap.get( e.getFilterValue() );
                            if ( rightField != null ) {
                                leftFieldDos.add( TruSrJdbcWhereCondParse.getDataFieldName( leftField, idFieldMap ).
                                        concat( " AS " ).concat( e.getFieldAlias() ) );
                                rightFieldDos.add( TruSrJdbcWhereCondParse.getDataFieldName( rightField,
                                        idFieldMap ).
                                        concat( " AS " ).concat( e.getFieldAlias() ) );
                            }
                        }
                        else {
                            e.setFilterValue( "" );
                        }
                    } );
            sql.append( "SELECT " ).append( String.join( ",", leftFieldDos ) ).append( " FROM \r\n" );
            if ( StringUtils.isNotBlank( left.getRunSql() ) ) {
                sql.append( "(" ).append( left.getRunSql() ).append( ")" ).append( " AS " );
            }
            sql.append( " " ).append( leftpre ).append( "\r\n " ).append( collectionComponent.getCollectionType().toUpperCase() );
            sql.append( "\r\n SELECT " ).append( String.join( ",", rightFieldDos ) ).append( " FROM " );
            if ( StringUtils.isNotBlank( right.getRunSql() ) ) {
                sql.append( "\r\n(" ).append( right.getRunSql() ).append( ")" ).append( " AS " );
            }
            sql.append( " " ).append( rightpre );
            if ( getNextModel() != null && getNextModel().size() > 0 ) {
                runSql.append( " SELECT * FROM \r\n(" ).append( sql ).append( ") AS tmp_" ).append( fModelElementDo.getElement() ).append( "\r\n WHERE 1=1 " );
            }
            else {
                runSql.append( sql );
            }
            //sql和配置存储
            fModelElementDo.setRunSql( runSql.toString() );
            fModelElementDo.setConfig( JsonUtil.toJson( collectionComponent ).replace( "\n", "" ) );
        }
        else{
            List<TTruModelFieldDo> fieldList = truNode.getOperators().getFields();
            List<TTruModelFieldDo> leftFields = fieldList.stream().filter( x -> x.getIsSelect() != null && x.getIsSelect()==1 && left.getId().equals( x.getExtendsId() ) ).collect( Collectors.toList() );
            //List<TTruModelFieldDo> rightFields = fieldList.stream().filter( x -> right.getId().equals( x.getExtendsId() ) ).collect( Collectors.toList() );

            leftFields.stream().forEach( x->{
                leftFieldDos.add(  x.getFieldName() );
                rightFieldDos.add( x.getFilterValue() );
            } );

//            Map<String, TTruModelFieldDo> idFieldMap = node.getOperators().getFields().stream()
//                    .filter( v -> v.getElementId().equals( right.getId() ) ).collect( Collectors.toMap( TTruModelFieldDo::getFieldAlias, e -> e ) );
//            node.getOperators().getFields().stream().filter( e -> e.getIsSelect() == 1 &&
//                    node.getViewIds().contains( e.getId() ) ).forEach(
//                    e -> {
//                        if ( left.getId().equals( e.getElementId() ) ) {
//                            TTruModelFieldDo leftField = e;
//                            TTruModelFieldDo rightField = idFieldMap.get( e.getFilterValue() );
//                            if ( rightField != null ) {
//                                leftFieldDos.add( getDataFieldName( leftField, idFieldMap ).
//                                        concat( " AS " ).concat( e.getFieldAlias() ) );
//                                rightFieldDos.add( getDataFieldName( rightField,
//                                        idFieldMap ).
//                                        concat( " AS " ).concat( e.getFieldAlias() ) );
//                            }
//                        }
//                        else {
//                            e.setFilterValue( "" );
//                        }
//                    } );
            StringBuffer sql = new StringBuffer();
            sql.append( "CREATE VIEW " ).append( fModelElementDo.getElement() ).append( " AS \r\n" );
            sql.append( "SELECT " ).append( String.join( ",", leftFieldDos ) ).append( " FROM \r\n" );
            if ( StringUtils.isNotBlank( left.getRunSql() ) ) {
                sql.append( "(" ).append( left.getRunSql() ).append( ")" ).append( " AS " );
            }
            sql.append( " " ).append( leftpre ).append( "\r\n " ).append( collectionComponent.getCollectionType().toUpperCase() );
            sql.append( "\r\n SELECT " ).append( String.join( ",", rightFieldDos ) ).append( " FROM " );
            if ( StringUtils.isNotBlank( right.getRunSql() ) ) {
                sql.append( "\r\n(" ).append( right.getRunSql() ).append( ")" ).append( " AS " );
            }
            sql.append( " " ).append( rightpre );
//            if ( getNextModel() != null && getNextModel().size() > 0 ) {
//                runSql.append( " SELECT * FROM \r\n(" ).append( sql ).append( ") AS tmp1_" ).append( fModelElementDo.getElement() ).append( "\r\n WHERE 1=1 " );
//            }
//            else {
//                runSql.append( sql );
//            }
            //sql.append( "\r\n ) tmp1_" ).append(  fModelElementDo.getElement() );
            sql.append( ";\r\n" );

            //sql和配置存储
            fModelElementDo.setRunSql( "" );
            fModelElementDo.setTableSql( sql.toString() );
            fModelElementDo.setConfig( JsonUtil.toJson( collectionComponent ).replace( "\n", "" ) );
        }
    }

    @Override
    public List<TTruModelFieldDo> relExtends(TTruModelElementRelDo elementInfo) throws Exception {

        Map<String, TTruModelElementDo> idElementMap = getModel().stream().collect(Collectors.toMap(TTruModelElementDo::getId, e -> e));
        TTruModelElementDo startElement = idElementMap.get(elementInfo.getStartElementId());
        Map<String, Object> config = MapTypeAdapter.gsonToMap(startElement.getConfig());
        TruIntersectionComponent collectionComponent = JsonUtil.gson().fromJson(JsonUtil.toJson(config), TruIntersectionComponent.class);
        List<TTruModelFieldDo> list = new ArrayList<>();
        getExtendsDos().stream().forEach(tBydbModelFieldDo -> {
            if (collectionComponent.getCollectionTable().get(0).equals(tBydbModelFieldDo.getExtendsId())) {
                tBydbModelFieldDo.setId( ComUtil.genId());
                if (tBydbModelFieldDo.getIsSelect() == 0) {
                    tBydbModelFieldDo.setIsSelect(-1);
                }
                tBydbModelFieldDo.setFieldName(tBydbModelFieldDo.getFieldAlias());
                tBydbModelFieldDo.setElementId(elementInfo.getEndElementId());
                tBydbModelFieldDo.setExtendsId(elementInfo.getStartElementId());
                tBydbModelFieldDo.setTableAlias(idElementMap.get(elementInfo.getStartElementId()).getElement());
                list.add(tBydbModelFieldDo);
            }

        });
        return list;
    }

}
