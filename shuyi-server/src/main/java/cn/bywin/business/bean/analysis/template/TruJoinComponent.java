package cn.bywin.business.bean.analysis.template;

import static cn.bywin.business.bean.analysis.TruComponentEnum.Join_COMPONENT;

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
 * @Description 表连接组件配置
 * @Author wangh
 * @Date 2021-10-20
 */
@Data
public class TruJoinComponent extends TruBaseComponenT {


    @Data
    public class Operator {
        private List<Operand> operand;
        private String operator;
    }

    private List<String> joinTable = new ArrayList<>();
    private String joinType = "left";
    private List<Operator> operator = new ArrayList<>();
    private String joinFieldMatchType = "custom";

    public TruJoinComponent() {
    }

    @Override
    public List<TTruModelFieldDo> getShowField(List<TTruModelFieldDo> fieldDos) throws Exception {
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
    public synchronized TruNode init( TruNode truNode, TTruModelElementDo fModelElementDo) throws Exception {
        joinTable = getPreModel().stream().sorted(Comparator.comparing(TTruModelElementDo::getCreatedTime)).map(e -> e.getId()).collect(Collectors.toList());
        if (StringUtils.isNotBlank(fModelElementDo.getConfig())) {
            Map<String, Object> config = MapTypeAdapter.gsonToMap(fModelElementDo.getConfig());
            config.put("joinTable", joinTable);
            truNode.setParams(config);
        } else {//连接配置初始化
            Map<String, Object> params = new HashMap<>(5);
            params.put("joinTable", joinTable);
            params.put("joinType", joinType);
            params.put("operator", operator);
            truNode.setParams(params);
        }

        Map<String, TTruModelElementDo> nameElementMap = getPreModel().stream().collect(Collectors.toMap(TTruModelElementDo::getId, e -> e));
        //Map<String, TTruModelElementDo> extMap = getExtendsDos().stream().collect( Collectors.toMap( x -> x.getId(), x -> nameElementMap.get( x.getElementId() ) ) );
        if (getPreModel().size() == 2) {
            truNode.getOperators().getFields().stream().forEach( e -> {
                e.setElementId(e.getExtendsId());
                e.setTableId(nameElementMap.get(e.getExtendsId()).getName());
            });
//            Map<String, String> idElementMap = node.getOperators().getFields().stream().filter(e -> e.getExtendsId().equals(joinTable.get(0))).collect(Collectors.toMap(TTruModelFieldDo::getFieldAlias, e -> e.getFieldName()));
//            node.getOperators().getFields().stream().filter(e -> e.getExtendsId().equals(joinTable.get(1))).forEach(e -> {
//                if (idElementMap.values().contains(e.getFieldAlias())) {
//                    long count = node.getOperators().getFields().stream().filter(
//                            r -> r.getFieldName().equals(idElementMap.get(e.getFieldName()))
//                    ).count();
//                    String value = e.getFieldAlias().concat("_").concat(String.valueOf(count - 1));
//                    e.setFieldAlias(value);
//                }
//            });
        }
        truNode.setType(Join_COMPONENT.getComponentName());
        return truNode;
    }

    @Override
    public boolean changeSameFieldName( TruNode truNode, TTruModelElementDo fModelElementDo ) throws Exception {
        return false;
    }

    @Override
    public TruCheckComponent check( TruNode truNode, TTruModelElementDo fModelElementDo) throws Exception {
        TruJoinComponent joinComponent = JsonUtil.gson().fromJson(JsonUtil.toJson( truNode.getParams()), TruJoinComponent.class);
        if( joinComponent.getJoinTable()==null || joinComponent.getJoinTable().size()!=2){
            return new TruCheckComponent(false, "需要两个组件进行连接，请重新配置");
        }
        if (getPreModel().size() != 2) {
            return new TruCheckComponent(false, "连接前置组件存在问题，请重新配置");
        }
        long count = getPreModel().stream().filter(e -> e.getRunStatus() == 0).count();
        if (count > 0) {
            return new TruCheckComponent(false, "前置组件未保存该组件无法保存，请配置");
        }
        Map<Object, Long> distanceName = truNode.getOperators().getFields().stream().collect(
                Collectors.groupingBy(TTruModelFieldDo::getFieldAlias, Collectors.counting()));
        List<Object> collect3 = distanceName.keySet().stream().
                filter(key -> distanceName.get(key) > 1).collect(Collectors.toList());
        if (collect3.size() > 0) {
            return new TruCheckComponent(false, String.format("别名有重复%s", collect3));
        }
        List<Operator> operator = joinComponent.getOperator();
        if (operator.size() == 0 || operator.get(0).getOperand().size() != 2 ||
                StringUtils.isBlank(operator.get(0).getOperand().get(0).getFieldName())
                || StringUtils.isBlank(operator.get(0).getOperand().get(1).getFieldName())) {
            return new TruCheckComponent(false, "连接条件不能为空");
        }
        String id = joinComponent.getJoinTable().get( 0 );
        HashMap<String,String> leftMap = new HashMap<>();
        List<TTruModelFieldDo> fields = truNode.getOperators().getFields();
        for ( TTruModelFieldDo field : fields ) {
            if( id.equals( field.getExtendsId() ) ){
                leftMap.put( field.getFieldName(),field.getFieldAlias() );
            }
        }

        id = joinComponent.getJoinTable().get( 1 );
        HashMap<String,String> rightMap = new HashMap<>();
        for ( TTruModelFieldDo field : fields ) {
            if( id.equals( field.getExtendsId() ) ){
                rightMap.put( field.getFieldName(),field.getFieldAlias() );
            }
        }
        for ( Operator operator1 : operator ) {
            List<Operand> operand = operator1.getOperand();
            if( operand.size()>0){
                Operand operand1 = operand.get( 0 );
                if(StringUtils.isNotBlank( operand1.getFieldName() )){
                    if(!leftMap.containsKey( operand1.getFieldName() )){
                        return new TruCheckComponent(false, String.format( "左列条件字段%s不存在", operand1.getFieldName()));
                    }
                }
            }
            if( operand.size()>1){
                Operand operand1 = operand.get( 1 );
                if(StringUtils.isNotBlank( operand1.getFieldName() )){
                    if(!rightMap.containsKey( operand1.getFieldName() )){
                        return new TruCheckComponent(false, String.format( "右列条件字段%s不存在", operand1.getFieldName()));
                    }
                }
            }
        }

        if ( truNode.getViewIds().size() < 1) {
            return new TruCheckComponent(false, "选中字段不能为空");
        }
        return new TruCheckComponent(true, "success");
    }

    @Override
    public void build( TruNode truNode, TTruModelElementDo fModelElementDo, TTruModelDo  model) throws Exception {

        TruJoinComponent joinComponent = JsonUtil.gson().fromJson( JsonUtil.toJson( truNode.getParams() ), TruJoinComponent.class );
        Map<String, TTruModelElementDo> idElementMap = getPreModel().stream().collect( Collectors.toMap( TTruModelElementDo::getId, e -> e ) );
        TTruModelElementDo left = idElementMap.get( joinComponent.getJoinTable().get( 0 ) );
        TTruModelElementDo right = idElementMap.get( joinComponent.getJoinTable().get( 1 ) );
        Map<String, TTruModelFieldDo> idFieldMap = truNode.getOperators().getFields().stream().collect( Collectors.toMap( TTruModelFieldDo::getFieldAlias, e -> e ) );
        String leftpre = left.getElement();
        String rightpre = right.getElement();
        List<Operator> operators = joinComponent.getOperator();

        Map<String, String> fromMap = getExtendsDos().stream().filter( x->x.getIsSelect()>0 ).collect( Collectors.toMap( x -> x.getId(), x -> x.getTableAlias() ) );

        if( !"tee".equalsIgnoreCase( model.getConfig() ) && !"flink".equalsIgnoreCase( model.getConfig() ) ) {
            StringBuffer sql = new StringBuffer();
            //StringBuffer runSql = new StringBuffer();

            //获取组件的字段
            List<String> fieldDos = new ArrayList<>();

            truNode.getOperators().getFields().stream().filter( e -> e.getIsSelect() == 1 ).forEach(
                    e -> {
                        StringBuffer field = new StringBuffer();
                        String  tabAlias= fromMap.get( e.getFromFieldId() ).concat( "." );
                        String fieldName = TruSrJdbcWhereCondParse.getDataFieldName( e,tabAlias, idFieldMap );
                        field.append( fieldName );
                        field.append( " AS " ).append( e.getFieldAlias() );
                        fieldDos.add( field.toString() );
                    } );

            sql.append( "SELECT " ).append( String.join( ",", fieldDos ) ).append( " FROM " );

            String runsql1 = left.getRunSql();
            //Pattern pattern = Pattern.compile( "CREATE TABLE(.|\\s)*?WITH(.|\\s)*?\\);\\s+", Pattern.CASE_INSENSITIVE);
            //Matcher matcher = pattern.matcher( runsql1 );
            //runsql1 = matcher.replaceAll( "" );

            if( StringUtils.isNotBlank( runsql1 )) {
                sql.append( "\r\n(\r\n" ).append( runsql1 ).append( "\r\n) " ).append( " AS " );
            }
            sql.append( leftpre ).append( "\r\n " ).append( joinComponent.getJoinType().toUpperCase().concat( " JOIN " ) );

            runsql1 = right.getRunSql();
            //matcher = pattern.matcher( runsql1 );
            //runsql1 = matcher.replaceAll( "" );
            if( StringUtils.isNotBlank( runsql1 )) {
                sql.append( "\r\n(\r\n" ).append( runsql1 ).append( "\r\n)" ).append( " AS " );
            }
            sql.append( rightpre ).append( "\r\n ON " );

            //连接组件的字段
            for ( Operator operator : operators ) {
                //sql.append( " AND " );
                sql.append( leftpre.concat( "." ).concat( operator.getOperand().get( 0 ).getFieldName() ) ).append( operator.getOperator() )
                        .append( rightpre.concat( "." ) ).append( operator.getOperand().get( 1 ).getFieldName() );
            }
//            if ( getNextModel() != null && getNextModel().size() > 0 ) {
//                runSql.append( " SELECT * FROM (" ).append( sql ).append( ") AS " ).append( fModelElementDo.getElement() ).append( " WHERE 1=1 " );
//            }
//            else {
//                runSql.append( sql );
//            }
            //sql和配置存储
            fModelElementDo.setRunSql( sql.toString() );
            fModelElementDo.setConfig( JsonUtil.toJson( joinComponent ).replace( "\n", "" ) );
        }
        else {
            StringBuffer sql = new StringBuffer();
            //获取组件的字段
            List<String> field2 = new ArrayList<>();
            truNode.getOperators().getFields().stream().filter( e -> e.getIsSelect() == 1 ).forEach(
                    e -> {
                        StringBuffer field = new StringBuffer();
                        String  tabAlias= fromMap.get( e.getFromFieldId() );

                        String fieldName = e.getFieldName() ;//FlinkWhereCondParse.getDataFieldName( e,tabAlias, idFieldMap );
                        field.append( tabAlias );
                        field.append( "." );
                        field.append( fieldName );
                        field.append( " AS " ).append( e.getFieldAlias() );
                        field2.add( field.toString() );
                    } );

            sql.append( "CREATE VIEW " ).append( fModelElementDo.getElement() ).append( " AS SELECT " ).append( String.join( " ,\r\n ", field2 ) ).append( "\r\n FROM " );

            sql.append( leftpre ).append( " " ).append( joinComponent.getJoinType().toUpperCase().concat( " JOIN " ) );
            sql.append( rightpre ).append( " ON 1=1 " );

            //连接组件的字段
            for ( Operator operator : operators ) {
                sql.append( " AND " );
                sql.append( leftpre.concat( "." ).concat( operator.getOperand().get( 0 ).getFieldName() ) ).append( operator.getOperator() )
                        .append( rightpre.concat( "." ) ).append( operator.getOperand().get( 1 ).getFieldName() );
            }
//        if (getNextModel() != null && getNextModel().size() > 0) {
//            runSql.append(" SELECT * FROM (").append(sql).append(") AS ").append(fModelElementDo.getElement()).append(" WHERE 1=1 ");
//        } else {
//            runSql.append(sql);
//        }
            //sql和配置存储
            //System.out.println( sql2.toString() );
            sql.append( ";\r\n" );

            fModelElementDo.setRunSql( "" );
            fModelElementDo.setTableSql( sql.toString() );
            fModelElementDo.setConfig( JsonUtil.toJson( joinComponent ).replace( "\n", "" ) );

        }
    }

    @Override
    public List<TTruModelFieldDo> relExtends(TTruModelElementRelDo elementInfo) throws Exception {
        List<TTruModelFieldDo> list = new ArrayList<>();
        Map<String, TTruModelElementDo> idElementMap = getModel().stream().collect(Collectors.toMap(TTruModelElementDo::getId, e -> e));
        getExtendsDos().stream().forEach(tBydbModelFieldDo -> {
            tBydbModelFieldDo.setId( ComUtil.genId());
            if (tBydbModelFieldDo.getIsSelect() == 0) {
                tBydbModelFieldDo.setIsSelect(-1);
            }
            tBydbModelFieldDo.setFieldName(tBydbModelFieldDo.getFieldAlias());
            tBydbModelFieldDo.setElementId(elementInfo.getEndElementId());
            tBydbModelFieldDo.setExtendsId(elementInfo.getStartElementId());
            tBydbModelFieldDo.setTableAlias(idElementMap.get(elementInfo.getStartElementId()).getElement());
            list.add(tBydbModelFieldDo);

        });
        return list;
    }

}
