package cn.bywin.business.bean.analysis.olk.template;

import static cn.bywin.business.bean.analysis.olk.OlkComponentEnum.Join_COMPONENT;

import cn.bywin.business.bean.analysis.olk.OlkBaseComponenT;
import cn.bywin.business.bean.analysis.olk.OlkCheckComponent;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.bean.view.bydb.Operand;
import cn.bywin.business.bean.view.olk.OlkNode;
import cn.bywin.business.beanparse.OlkSrJdbcWhereCondParse;
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
public class OlkJoinComponent extends OlkBaseComponenT {


    @Data
    public class Operator {
        private List<Operand> operand;
        private String operator;
    }

    private List<String> joinTable = new ArrayList<>();
    private String joinType = "left";
    private List<Operator> operator = new ArrayList<>();
    private String joinFieldMatchType = "custom";

    public OlkJoinComponent() {
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

    @Override
    public synchronized OlkNode init( OlkNode truNode, TOlkModelElementDo fModelElementDo) throws Exception {
        joinTable = getPreModel().stream().sorted(Comparator.comparing(TOlkModelElementDo::getCreatedTime)).map(e -> e.getId()).collect(Collectors.toList());
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

        Map<String, TOlkModelElementDo> nameElementMap = getPreModel().stream().collect(Collectors.toMap(TOlkModelElementDo::getId, e -> e));
        //Map<String, TOlkModelElementDo> extMap = getExtendsDos().stream().collect( Collectors.toMap( x -> x.getId(), x -> nameElementMap.get( x.getElementId() ) ) );
        if (getPreModel().size() == 2) {
            truNode.getOperators().getFields().stream().forEach( e -> {
                e.setElementId(e.getExtendsId());
                e.setTableId(nameElementMap.get(e.getExtendsId()).getName());
            });
//            Map<String, String> idElementMap = node.getOperators().getFields().stream().filter(e -> e.getExtendsId().equals(joinTable.get(0))).collect(Collectors.toMap(TOlkModelFieldDo::getFieldAlias, e -> e.getFieldName()));
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
    public boolean changeSameFieldName( OlkNode truNode, TOlkModelElementDo fModelElementDo ) throws Exception {
        return false;
    }

    @Override
    public OlkCheckComponent check( OlkNode truNode, TOlkModelElementDo fModelElementDo) throws Exception {
        OlkJoinComponent joinComponent = JsonUtil.gson().fromJson(JsonUtil.toJson( truNode.getParams()), OlkJoinComponent.class);
        if( joinComponent.getJoinTable()==null || joinComponent.getJoinTable().size()!=2){
            return new OlkCheckComponent(false, "需要两个组件进行连接，请重新配置");
        }
        if (getPreModel().size() != 2) {
            return new OlkCheckComponent(false, "连接前置组件存在问题，请重新配置");
        }
        long count = getPreModel().stream().filter(e -> e.getRunStatus() == 0).count();
        if (count > 0) {
            return new OlkCheckComponent(false, "前置组件未保存该组件无法保存，请配置");
        }
        Map<Object, Long> distanceName = truNode.getOperators().getFields().stream().collect(
                Collectors.groupingBy(TOlkModelFieldDo::getFieldAlias, Collectors.counting()));
        List<Object> collect3 = distanceName.keySet().stream().
                filter(key -> distanceName.get(key) > 1).collect(Collectors.toList());
        if (collect3.size() > 0) {
            return new OlkCheckComponent(false, String.format("别名有重复%s", collect3));
        }
        List<Operator> operator = joinComponent.getOperator();
        if (operator.size() == 0 || operator.get(0).getOperand().size() != 2 ||
                StringUtils.isBlank(operator.get(0).getOperand().get(0).getFieldName())
                || StringUtils.isBlank(operator.get(0).getOperand().get(1).getFieldName())) {
            return new OlkCheckComponent(false, "连接条件不能为空");
        }
        String id = joinComponent.getJoinTable().get( 0 );
        HashMap<String,String> leftMap = new HashMap<>();
        List<TOlkModelFieldDo> fields = truNode.getOperators().getFields();
        for ( TOlkModelFieldDo field : fields ) {
            if( id.equals( field.getExtendsId() ) ){
                leftMap.put( field.getFieldName(),field.getFieldAlias() );
            }
        }

        id = joinComponent.getJoinTable().get( 1 );
        HashMap<String,String> rightMap = new HashMap<>();
        for ( TOlkModelFieldDo field : fields ) {
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
                        return new OlkCheckComponent(false, String.format( "左列条件字段%s不存在", operand1.getFieldName()));
                    }
                }
            }
            if( operand.size()>1){
                Operand operand1 = operand.get( 1 );
                if(StringUtils.isNotBlank( operand1.getFieldName() )){
                    if(!rightMap.containsKey( operand1.getFieldName() )){
                        return new OlkCheckComponent(false, String.format( "右列条件字段%s不存在", operand1.getFieldName()));
                    }
                }
            }
        }

        if ( truNode.getViewIds().size() < 1) {
            return new OlkCheckComponent(false, "选中字段不能为空");
        }
        return new OlkCheckComponent(true, "success");
    }

    @Override
    public void build( OlkNode truNode, TOlkModelElementDo fModelElementDo, TOlkModelDo  model) throws Exception {

        OlkJoinComponent joinComponent = JsonUtil.gson().fromJson( JsonUtil.toJson( truNode.getParams() ), OlkJoinComponent.class );
        Map<String, TOlkModelElementDo> idElementMap = getPreModel().stream().collect( Collectors.toMap( TOlkModelElementDo::getId, e -> e ) );
        TOlkModelElementDo left = idElementMap.get( joinComponent.getJoinTable().get( 0 ) );
        TOlkModelElementDo right = idElementMap.get( joinComponent.getJoinTable().get( 1 ) );
        Map<String, TOlkModelFieldDo> idFieldMap = truNode.getOperators().getFields().stream().collect( Collectors.toMap( TOlkModelFieldDo::getFieldAlias, e -> e ) );
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
                        String fieldName = OlkSrJdbcWhereCondParse.getDataFieldName( e,tabAlias, idFieldMap );
                        field.append( fieldName );
                        if( !StringUtils.equals( fieldName,e.getFieldAlias() ) ) {
                            field.append( " AS " ).append( e.getFieldAlias() );
                        }
                        fieldDos.add( field.toString() );
                    } );

            sql.append( "SELECT " ).append( String.join( " ,\r\n ", fieldDos ) ).append( " FROM " );

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
             fModelElementDo.setTableSql( "" );
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
    public List<TOlkModelFieldDo> relExtends(TOlkModelElementRelDo elementInfo) throws Exception {
        List<TOlkModelFieldDo> list = new ArrayList<>();
        Map<String, TOlkModelElementDo> idElementMap = getModel().stream().collect(Collectors.toMap(TOlkModelElementDo::getId, e -> e));
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
