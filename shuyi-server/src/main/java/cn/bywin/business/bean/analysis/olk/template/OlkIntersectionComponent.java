package cn.bywin.business.bean.analysis.olk.template;

import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.Intersect_COMPONENT;

import cn.bywin.business.bean.analysis.olk.OlkBaseComponenT;
import cn.bywin.business.bean.analysis.olk.OlkCheckComponent;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.bean.view.bydb.Operand;
import cn.bywin.business.bean.view.olk.OlkNode;
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
public class OlkIntersectionComponent extends OlkBaseComponenT {


    private List<Operand> operator = new ArrayList<>();

    private List<String> collectionTable = new ArrayList<>();
    private String collectionType = "INTERSECT";


    public OlkIntersectionComponent() {
    }

    @Override
    public List<TOlkModelFieldDo> getShowField(List<TOlkModelFieldDo> fieldDos) throws Exception {
        List<TOlkModelFieldDo> disData = new ArrayList<>();
        fieldDos.stream().forEach(e -> {
            if (e.getIsSelect() == 1&&StringUtils.isNotBlank(e.getFilterValue())) {
                e.setFieldName(e.getFieldAlias());
                disData.add(e);
            }
        });
        List<TOlkModelFieldDo> fieldDoList = disData.stream().sorted(Comparator.comparing(TOlkModelFieldDo::getFilterSort)).collect(Collectors.toList());

        return fieldDoList;
    }


    @Override
    public OlkNode init( OlkNode truNode, TOlkModelElementDo fModelElementDo) throws Exception {
        if (StringUtils.isNotBlank(fModelElementDo.getConfig())) {
            Map<String, Object> config = MapTypeAdapter.gsonToMap(fModelElementDo.getConfig());
            OlkIntersectionComponent collectionComponent = JsonUtil.gson().fromJson(JsonUtil.toJson(config), OlkIntersectionComponent.class);

            config.put("collectionTable", collectionComponent.getCollectionTable());
            truNode.setParams(config);
        } else {//连接配置初始化
            Map<String, Object> params = new HashMap<>(5);
            collectionTable = getPreModel().stream().sorted(Comparator.comparing(TOlkModelElementDo::getCreatedTime)).map(e -> e.getId()).collect(Collectors.toList());
            params.put("collectionTable", collectionTable);
            params.put("operator", operator);
            truNode.setParams(params);
        }
        Map<String, TOlkModelElementDo> nameElementMap = getPreModel().stream().collect(Collectors.toMap(TOlkModelElementDo::getId, e -> e));
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
    public boolean changeSameFieldName( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {
        return false;
    }

    @Override
    public OlkCheckComponent check( OlkNode truNode, TOlkModelElementDo fModelElementDo) throws Exception {
        OlkIntersectionComponent collectionComponent = JsonUtil.gson().fromJson(JsonUtil.toJson( truNode.getParams()), OlkIntersectionComponent.class);

        if (getPreModel().size() != 2) {
            return new OlkCheckComponent(false, "前置组件个数错误");
        }
        if (getPreModel().stream().filter(e -> e.getRunStatus() == 0).collect(Collectors.toList()).size() > 0) {
            return new OlkCheckComponent(false, "前置组件未保存该组件无法保存，请配置");
        }
        if ( truNode.getViewIds().size() < 1) {
            return new OlkCheckComponent(false, "选中字段不能为空");
        }
        List<TOlkModelFieldDo> operatorList = truNode.getOperators().getFields();
        for (TOlkModelFieldDo e:operatorList){
            if (e.getIsSelect() == 1 && e.getExtendsId().equals(collectionComponent.getCollectionTable().get(0))
                    &&StringUtils.isBlank(e.getFilterValue())) {
                return new OlkCheckComponent(false, "表之间选中字段不能为空");
            }
        }
        return new OlkCheckComponent(true, "success");
    }

    @Override
    public void build( OlkNode truNode, TOlkModelElementDo fModelElementDo, TOlkModelDo model) throws Exception {
        OlkIntersectionComponent collectionComponent = JsonUtil.gson().fromJson( JsonUtil.toJson( truNode.getParams() ), OlkIntersectionComponent.class );


        Map<String, TOlkModelElementDo> idElementMap = getPreModel().stream().collect( Collectors.toMap( TOlkModelElementDo::getId, e -> e ) );
        TOlkModelElementDo left = idElementMap.get( collectionComponent.getCollectionTable().get( 0 ) );
        TOlkModelElementDo right = idElementMap.get( collectionComponent.getCollectionTable().get( 1 ) );
        //获取组件的字段
        List<String> leftFieldDos = new ArrayList<>();
        List<String> rightFieldDos = new ArrayList<>();
        String leftpre = left.getElement();
        String rightpre = right.getElement();

        if( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {
            //StringBuffer runSql = new StringBuffer();
            StringBuffer sql = new StringBuffer();
            Map<String, TOlkModelFieldDo> idFieldMap = truNode.getOperators().getFields().stream()
                    .filter( v -> v.getElementId().equals( right.getId() ) ).collect( Collectors.toMap( TOlkModelFieldDo::getFieldAlias, e -> e ) );
//            truNode.getOperators().getFields().stream().filter( e -> e.getIsSelect() == 1 &&
//                    truNode.getViewIds().contains( e.getId() ) ).forEach(
//                    e -> {
//                        if ( left.getId().equals( e.getElementId() ) ) {
//                            TOlkModelFieldDo leftField = e;
//                            TOlkModelFieldDo rightField = idFieldMap.get( e.getFilterValue() );
//                            if ( rightField != null ) {
//                                leftFieldDos.add( OlkSrJdbcWhereCondParse.getDataFieldName( leftField, idFieldMap ).
//                                        concat( " AS " ).concat( e.getFieldAlias() ) );
//                                rightFieldDos.add( OlkSrJdbcWhereCondParse.getDataFieldName( rightField,
//                                        idFieldMap ).
//                                        concat( " AS " ).concat( e.getFieldAlias() ) );
//                            }
//                        }
//                        else {
//                            e.setFilterValue( "" );
//                        }
//                    } );
            List<TOlkModelFieldDo> fieldList = truNode.getOperators().getFields();
            List<TOlkModelFieldDo> leftFields = fieldList.stream().filter( x -> x.getIsSelect() != null && x.getIsSelect()==1 && left.getId().equals( x.getExtendsId() ) ).collect( Collectors.toList() );
            //List<TOlkModelFieldDo> rightFields = fieldList.stream().filter( x -> right.getId().equals( x.getExtendsId() ) ).collect( Collectors.toList() );

            leftFields.stream().forEach( x->{
                leftFieldDos.add(  x.getFieldName() );
                rightFieldDos.add( x.getFilterValue() );
            } );
            sql.append( "SELECT " ).append( String.join( ",", leftFieldDos ) ).append( " FROM \r\n" );

            sql.append( "(" ).append( left.getRunSql() ).append( ")" ).append( " AS " );

            sql.append( " " ).append( leftpre ).append( "\r\n " ).append( collectionComponent.getCollectionType().toUpperCase() );
            sql.append( "\r\n SELECT " ).append( String.join( ",", rightFieldDos ) ).append( " FROM " );

            sql.append( "\r\n(" ).append( right.getRunSql() ).append( ")" ).append( " AS " );

            sql.append( " " ).append( rightpre );
//            if ( getNextModel() != null && getNextModel().size() > 0 ) {
//                runSql.append( " SELECT * FROM \r\n(" ).append( sql ).append( ") AS tmp_" ).append( fModelElementDo.getElement() ).append( "\r\n WHERE 1=1 " );
//            }
//            else {
//                runSql.append( sql );
//            }
            //sql和配置存储
            fModelElementDo.setRunSql( sql.toString() );
            fModelElementDo.setTableSql( "" );
            fModelElementDo.setConfig( JsonUtil.toJson( collectionComponent ).replace( "\n", "" ) );
        }
        else{
            List<TOlkModelFieldDo> fieldList = truNode.getOperators().getFields();
            List<TOlkModelFieldDo> leftFields = fieldList.stream().filter( x -> x.getIsSelect() != null && x.getIsSelect()==1 && left.getId().equals( x.getExtendsId() ) ).collect( Collectors.toList() );
            //List<TOlkModelFieldDo> rightFields = fieldList.stream().filter( x -> right.getId().equals( x.getExtendsId() ) ).collect( Collectors.toList() );

            leftFields.stream().forEach( x->{
                leftFieldDos.add(  x.getFieldName() );
                rightFieldDos.add( x.getFilterValue() );
            } );

//            Map<String, TOlkModelFieldDo> idFieldMap = node.getOperators().getFields().stream()
//                    .filter( v -> v.getElementId().equals( right.getId() ) ).collect( Collectors.toMap( TOlkModelFieldDo::getFieldAlias, e -> e ) );
//            node.getOperators().getFields().stream().filter( e -> e.getIsSelect() == 1 &&
//                    node.getViewIds().contains( e.getId() ) ).forEach(
//                    e -> {
//                        if ( left.getId().equals( e.getElementId() ) ) {
//                            TOlkModelFieldDo leftField = e;
//                            TOlkModelFieldDo rightField = idFieldMap.get( e.getFilterValue() );
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
    public List<TOlkModelFieldDo> relExtends(TOlkModelElementRelDo elementInfo) throws Exception {

        Map<String, TOlkModelElementDo> idElementMap = getModel().stream().collect(Collectors.toMap(TOlkModelElementDo::getId, e -> e));
        TOlkModelElementDo startElement = idElementMap.get(elementInfo.getStartElementId());
        Map<String, Object> config = MapTypeAdapter.gsonToMap(startElement.getConfig());
        OlkIntersectionComponent collectionComponent = JsonUtil.gson().fromJson(JsonUtil.toJson(config), OlkIntersectionComponent.class);
        List<TOlkModelFieldDo> list = new ArrayList<>();
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
