package cn.bywin.business.service.bydb;


import cn.bywin.business.bean.bydb.*;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.view.bydb.TruNode;
import cn.bywin.business.bean.view.bydb.TruOperators;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.mapper.bydb.*;
import cn.bywin.business.mapper.federal.DataSourceMapper;
import cn.service.impl.BaseServiceImpl;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class TruModelElementService extends BaseServiceImpl<TTruModelElementDo, String> {

    @Autowired
    private TrumodelWindowMapper  trumodelWindowMapper;

    @Autowired
    private TruModelElementMapper truModelElementMapper;
    @Autowired
    private TruModelElementRelMapper truModelElementRelMapper;

    @Autowired
    private TruModelFieldMapper truModelFieldMapper;

//    @Autowired
//    private BydbFieldMapper bydbFieldMapper;
//    @Autowired
//    private BydbDsColumnMapper bydbDsColumnMapper;
//    @Autowired
//    private TruModelFieldMapper truModelFieldMapper;
    @Autowired
    private TruModelObjectMapper bydbModelObjectMapper;

//    @Autowired
//    private BydbObjectMapper tableMapper;
//    @Autowired
//    private BydbDatabaseMapper databaseMapper;

    @Autowired
    private TruModelElementJobMapper truModelElementJobMapper;
    @Autowired
    private DataSourceMapper dataSourceMapper;
    @Override
    public Mapper<TTruModelElementDo> getMapper() {
        return truModelElementMapper;
    }

    public List<TTruModelElementDo> selectByModelIdWithDetail(String modelId) {
        return truModelElementMapper.selectByModelIdWithDetail(modelId);
    }

    public List<TTruModelElementDo> selectStartId(String vertexId) {

        return truModelElementMapper.selectStartId(vertexId);
    }
    public List<TTruModelElementDo> selectEndId(String vertexId) {

        return truModelElementMapper.selectEndId(vertexId);
    }

    public List<TTruModelElementDo> selectByModelId(String modelId) {
        return truModelElementMapper.selectByModelId(modelId);
    }

    @Transactional(rollbackFor = Exception.class)
    public TruNode getNodes( TTruModelElementDo elementInfo) throws Exception {
        TruNode truNode = new TruNode();
        List<TTruModelFieldDo> fieldDos = new ArrayList<>();
        TruOperators truOperators = new TruOperators();

        if (elementInfo.getElementType() == 1) {
            String tcId = elementInfo.getTcId();
            TTruModelObjectDo tBydbDatabaseDo = bydbModelObjectMapper.selectByObjectId(tcId,elementInfo.getModelId());
//            if( tBydbDatabaseDo == null){
//                throw new Exception(String.format( "%s表不存在",elementInfo.getTcId() ));
//            }
            truNode.setDatabase(tBydbDatabaseDo);
            truNode.setTotal(  elementInfo.getTotal() );

//           if( tcId.startsWith( "db" )){
//               TBydbObjectDo table = tableMapper.selectByPrimaryKey( tcId.substring( 2 ) );
//               if( table == null){
//                   throw new Exception(String.format( "%s对应%s表不存在",elementInfo.getTcId(),tBydbDatabaseDo.getObjFullName() ));
//               }
//               TBydbDatabaseDo databaseDo = databaseMapper.selectByPrimaryKey( table.getDbId() );
//               if( databaseDo == null){
//                   throw new Exception(String.format( "表%s.%s目录不存在",elementInfo.getTcId(),tBydbDatabaseDo.getObjFullName() ));
//               }
//               FDatasourceDo fDatasourceDo = dataSourceMapper.selectByPrimaryKey( databaseDo.getDbsourceId() );
//               if( fDatasourceDo == null){
//                   throw new Exception(String.format( "表%s目录%s数据源%s不存在",tBydbDatabaseDo.getObjFullName(),databaseDo.getDbName(),databaseDo.getDbsourceId() ));
//               }
//               node.setDbSource( fDatasourceDo );
//           }

            fieldDos = truModelFieldMapper.selectByElementId(elementInfo.getId());
        } else {
            if( "t_datasource".equalsIgnoreCase( elementInfo.getIcon() )) {
                JsonObject jsonObject = JsonUtil.toJsonObject( elementInfo.getConfig() );
                if (jsonObject != null && jsonObject.has( "datasourceId" ) && !jsonObject.get( "datasourceId" ).isJsonNull() ) {
                    String datasourceId = jsonObject.get( "datasourceId" ).getAsString();
//                    if ( StringUtils.isBlank( datasourceId ) ) {
//                        throw new Exception( String.format( "数据源未指定", datasourceId ) );
//                    }
                    FDatasourceDo fDatasourceDo = dataSourceMapper.selectByPrimaryKey( datasourceId );
//                    if ( fDatasourceDo == null ) {
//                        throw new Exception( String.format( "数据源%s不存在", datasourceId ) );
//                    }
                    truNode.setDbSource( fDatasourceDo );
                }
            }
            fieldDos = truModelFieldMapper.selectByElementId(elementInfo.getId());
            List<TTruModelFieldDo> filterDos = truModelFieldMapper.selectByElementIdTable(elementInfo.getId());
            List<TTruModelFieldDo> filterList = filterDos.stream().sorted(Comparator.comparing(TTruModelFieldDo::getFilterSort)).collect(Collectors.toList());
            truOperators.setFilters(filterList);
        }
        List<String> viewNodes = new ArrayList<>();
        fieldDos.stream().sorted(Comparator.comparing(TTruModelFieldDo::getFilterSort)).forEach(e -> {
            if (e.getIsSelect() == 1) {
                viewNodes.add(e.getId());
            }
        });
        List<TTruModelFieldDo> fieldDoList = fieldDos.stream().sorted(Comparator.comparing(TTruModelFieldDo::getFilterSort)).collect(Collectors.toList());
        truNode.setViewIds(viewNodes);
        TBydbFieldDo tBydbFieldDo = new TBydbFieldDo();
        tBydbFieldDo.setObjectId(elementInfo.getTcId());
        truOperators.setFields(fieldDoList);
        truNode.setId(elementInfo.getId());
        truNode.setName(elementInfo.getName());
        truNode.setTotal(elementInfo.getTotal());
        truNode.setTcId( elementInfo.getTcId() );
        truNode.setOrigName( elementInfo.getOrigName() );
        truNode.setModelId( elementInfo.getModelId() );
        truNode.setOperators( truOperators );
        truNode.setNodeStatus(elementInfo.getRunStatus());
        return truNode;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteById(String id) {
        TTruModelElementDo fModelElementDo = truModelElementMapper.selectById(id);
        List<TTruModelElementRelDo> elementRels = truModelElementRelMapper.selectByVertexId(id);
        elementRels.stream().forEach(element -> {
            truModelElementRelMapper.deleteByPrimaryKey(element.getId());
            truModelFieldMapper.deleteByElementIdTable(element.getEndElementId(), element.getEndElementId());
        });
        truModelFieldMapper.deleteByElementId(fModelElementDo.getId());
        truModelFieldMapper.deleteByExtendsId(fModelElementDo.getId());
        truModelElementMapper.deleteByPrimaryKey(id);
        return getMapper().deleteByPrimaryKey(id);
    }


    @Transactional(rollbackFor = Exception.class)
    public void insertBeanDetail(TTruModelElementDo elementInfo,List<TBydbFieldDo> fieldList) throws Exception {
        TTruModelWindowDo windowDo = new TTruModelWindowDo();

        String uuid = ComUtil.genId();

        if ("t_window".equalsIgnoreCase(elementInfo.getIcon())) {
            elementInfo.setIcon("t_aggregation");
            windowDo.setId(uuid);
            windowDo.setUseTumbleEnd(false);
            windowDo.setUseTumbleStart(false);
            windowDo.setTimeUnit("SECOND");
            windowDo.setWindowInterval(1);
            trumodelWindowMapper.insert(windowDo);
        }

        List<String> eleList = truModelElementMapper.selectByModelId( elementInfo.getModelId() ).stream()
                .filter( x -> StringUtils.isNotBlank( x.getElement() ) ).map( x -> x.getElement() ).collect( Collectors.toList() );

        int cnt = 1;
        while( true ){
            String format = String.format( "t%d", cnt );
            if( eleList.indexOf( format ) <0 ){
                break;
            }
            cnt++;
        }

        long nameCount = truModelElementMapper.countNameByModelId(elementInfo.getModelId(), elementInfo.getName());
        elementInfo.setId(uuid);
        if (nameCount > 0) {
            elementInfo.setName(elementInfo.getName().concat("_").concat(String.valueOf(nameCount)));
        }
        elementInfo.setRunStatus(0);

        String pre = "t".concat(String.valueOf(cnt));
        elementInfo.setElement(pre);
        elementInfo.setCreatedTime((new Timestamp(System.currentTimeMillis())));
        elementInfo.setModifiedTime((new Timestamp(System.currentTimeMillis())));
        if (elementInfo.getElementType() == 1) {
            truModelElementMapper.insert(elementInfo);
            //if ("db".equals(elementInfo.getTcId().substring(0, 2))) {
                //TBydbFieldDo tBydbFieldDo = new TBydbFieldDo();
                //tBydbFieldDo.setObjectId(elementInfo.getTcId().substring(2));
                //List<TBydbFieldDo> fieldList = bydbFieldMapper.findBeanList(tBydbFieldDo);
                int norder = 0;
                for (TBydbFieldDo fieldDo : fieldList) {
                    TTruModelFieldDo modField = new TTruModelFieldDo();
                    MyBeanUtils.copyBean2Bean(modField, fieldDo);
                    modField.setElementId(uuid);
                    modField.setTableAlias(pre);
                    modField.setFilterSort(norder++);
                    modField.setFilterStatus(0);
                    modField.setFieldExpr(StringUtils.isBlank(fieldDo.getChnName()) ? fieldDo.getFieldName() : fieldDo.getChnName());
                    modField.setIsSelect(1);
                    modField.setOrigFieldType( fieldDo.getFieldType() );
                    modField.setOrigFlag( 1 );
                    modField.setTableId(fieldDo.getObjectId());
                    modField.setFieldAlias(fieldDo.getFieldName());
                    modField.setId(ComUtil.genId());

                    truModelFieldMapper.insert(modField);
                }
            //} else {
                /*List<TBydbDsColumnDo> colList = bydbDsColumnMapper.findByDatasetId(elementInfo.getTcId().substring(2));
                colList = colList.stream().filter(x -> x.getEnable() != null && x.getEnable() == 1).collect(Collectors.toList());
                List<TBydbDsColumnDo> groupList = colList.stream().filter(x -> "group".equalsIgnoreCase(x.getEtype())).collect(Collectors.toList());
                if (groupList.size() > 0) {
                    colList = groupList;
                }
                for (TBydbDsColumnDo fieldDo : colList) {
                    TTruModelFieldDo truModelFieldDo = new TTruModelFieldDo();
                    MyBeanUtils.copyBean2Bean(TTruModelFieldDo, fieldDo);
                    truModelFieldDo.setElementId(uuid);
                    truModelFieldDo.setFieldType(fieldDo.getOrgType());
                    truModelFieldDo.setFilterStatus(0);
                    truModelFieldDo.setFilterSort(fieldDo.getNorder() == null ? 0 : fieldDo.getNorder());
                    truModelFieldDo.setIsSelect(1);
                    truModelFieldDo.setTableAlias(pre);
                    truModelFieldDo.setCreatedTime(ComUtil.getCurTimestamp());
                    truModelFieldDo.setTableId(fieldDo.getDsId());
                    truModelFieldDo.setFieldExpr(StringUtils.isBlank(fieldDo.getChnName()) ? fieldDo.getColumnAliasName() : fieldDo.getChnName());
                    truModelFieldDo.setFieldName(fieldDo.getColumnAliasName());
                    truModelFieldDo.setFieldAlias(fieldDo.getColumnAliasName());
                    truModelFieldDo.setId(ComUtil.genId());
                    bydbModelFieldMapper.insert(truModelFieldDo);
                }*/
           // }
        } else {
            truModelElementMapper.insert(elementInfo);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBeanDetail(TTruModelElementDo elementInfo, TruNode truNode ) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        elementInfo.setModifiedTime((new Timestamp(System.currentTimeMillis())));
        List<String> viewNodes = new ArrayList<>();
        viewNodes.addAll( truNode.getViewIds());
        truModelElementMapper.updateByPrimaryKey(elementInfo);

        boolean bchg = false;
        String type =  truNode.getType();
        String[] fieldNames= {"isSelect","fieldName","FieldAlias","origFieldType","origFlag","fieldExpr","FilterSort","FilterValue","FilterConfig","Aggregation","orderFunc","ElementId","ColumnType","FieldType","TableAlias" };
        if( truNode.getParams() != null) {

            if (  "t_collection".equalsIgnoreCase( type )|| "t_intersect".equalsIgnoreCase( type ) ) {
                //List<String> collectionTable = (List<String>) node.getParams().get( "collectionTable" );
                List<TTruModelFieldDo> leftList = truNode.getOperators().getFields().stream().filter( x -> x.getIsSelect()==1 && StringUtils.isNotBlank(  x.getFilterValue() ) ).collect( Collectors.toList() );
                viewNodes.clear();
                leftList.forEach( x -> viewNodes.add( x.getId() ) );
                //List<String> fieldNameList = leftList.stream().map( x -> x.getFilterValue() ).collect( Collectors.toList() );
                //List<TTruModelFieldDo> rightList = node.getOperators().getFields().stream().filter( x -> x.getElementId().equals( collectionTable.get( 1 ) ) && fieldNameList.contains( x.getFieldName() ) ).collect( Collectors.toList() );
                //rightList.forEach( x-> viewNodes.add( x.getId() ) );
            }
            else if ( "t_aggregation".equalsIgnoreCase( type ) ){
                viewNodes.clear();
//                List<Map<String,Object>> selList = (List<Map<String,Object>>) node.getParams().get( "field" );
//                List<Map<String,Object>> groupList = (List<Map<String,Object>>) node.getParams().get( "group" );
//                for ( Map<String,Object> fieldDo : selList ) {
//                    viewNodes.add( fieldDo.get( "id" ).toString() );
//                }
//                for ( Map<String,Object> fieldDo : groupList ) {
//                    viewNodes.add( fieldDo.get( "id" ).toString() );
//                }
            }
        }

        List<TTruModelFieldDo> delList = truModelFieldMapper.selectByElementId( elementInfo.getId() );
        Map<String, TTruModelFieldDo> fieldMap = delList.stream().collect( Collectors.toMap( x -> x.getId(), x -> x ) );
        int norder = 0;

        if(truNode.getOperators().getFunctions() != null) {
            truNode.getOperators().getFields().addAll(truNode.getOperators().getFunctions());
        }

        for ( int i = 0; i< truNode.getOperators().getFields().size(); i++){
            TTruModelFieldDo fieldDo=   truNode.getOperators().getFields().get(i);
            fieldDo.setAggregation( null );
            fieldDo.setElementId( elementInfo.getId() );
            fieldDo.setTableAlias( elementInfo.getElement() );
            TTruModelFieldDo ftmp = fieldMap.get( fieldDo.getId() );
            if( ftmp == null){
                //add one
                fieldDo.setId( ComUtil.genId() );
                fieldDo.setIsSelect(1);
                fieldDo.setElementId( elementInfo.getId() );
                truModelFieldMapper.insert( fieldDo );
            }
            else{
                delList.remove( ftmp );
                if (viewNodes.contains(fieldDo.getId())) {
                    fieldDo.setIsSelect(1);
                    //viewNodes.remove( fieldDo.getId() );
                } else {
                    fieldDo.setIsSelect(0);
                }
                fieldDo.setFilterSort(norder++);

                if( !MyBeanUtils.checkSameProperty( ftmp,fieldDo,true,"isSelect","fieldName","origFieldType","origFlag","FieldAlias","fieldExpr" ) ){
                    bchg = true;
                }
                if( !MyBeanUtils.checkSameProperty( fieldDo,ftmp,true,
                        fieldNames ) ){
                    MyBeanUtils.copyBeanProp( fieldDo,ftmp,true,fieldNames );
                    truModelFieldMapper.updateByPrimaryKey(ftmp);
                }
            }
        }

        if(truNode.getOperators().getFunctions() != null) {
            truNode.getOperators().getFields().removeAll(truNode.getOperators().getFunctions());
        }


        if ( "t_aggregation".equalsIgnoreCase( type ) ){

            List<TTruModelFieldDo> selList =JsonUtil.deserialize(  JsonUtil.toJson( truNode.getParams().get( "field" )) , new TypeToken<List<TTruModelFieldDo>>() { }.getType()); //聚合
            List<TTruModelFieldDo> groupList =JsonUtil.deserialize(  JsonUtil.toJson( truNode.getParams().get( "group" )) , new TypeToken<List<TTruModelFieldDo>>() { }.getType()); ////分组

            for ( TTruModelFieldDo fieldDo : groupList ) {
                fieldDo.setIsSelect(1);
                fieldDo.setElementId( elementInfo.getId() );
                fieldDo.setTableAlias( elementInfo.getElement() );
                fieldDo.setFilterSort(norder++);
                TTruModelFieldDo ftmp = fieldMap.get( fieldDo.getId() );
                if( ftmp != null){
                    delList.remove( ftmp );
                    if(StringUtils.isBlank( ftmp.getAggregation() )) { //原字段 不处理
                         ftmp = null;
                     }
                }
                if( ftmp == null){
                    //add one
                    fieldDo.setId( ComUtil.genId() );
                    fieldDo.setAggregation( "group_by_field" );
                    bchg = true;
                    truModelFieldMapper.insert( fieldDo );
                }
                else{
                    if( !MyBeanUtils.checkSameProperty( ftmp,fieldDo,true,"isSelect","fieldName","origFieldType","origFlag","FieldAlias","fieldExpr" ) ){
                        bchg = true;
                    }
                    if( !MyBeanUtils.checkSameProperty( fieldDo,ftmp,true, fieldNames ) ){
                        MyBeanUtils.copyBeanProp( fieldDo,ftmp,true,fieldNames );
                        truModelFieldMapper.updateByPrimaryKey(ftmp);
                    }
                }
            }
            for ( TTruModelFieldDo fieldDo : selList ) {
                fieldDo.setIsSelect(1);
                fieldDo.setElementId( elementInfo.getId() );
                fieldDo.setTableAlias( elementInfo.getElement() );
                fieldDo.setFilterSort(norder++);
                TTruModelFieldDo ftmp = fieldMap.get( fieldDo.getId() );
                if( ftmp != null){
                    delList.remove( ftmp );
                    if(StringUtils.isBlank( ftmp.getAggregation() )) { //原字段 不处理
                        ftmp = null;
                    }
                }
                if( ftmp == null){
                    //add one
                    fieldDo.setId( ComUtil.genId() );
                    bchg = true;
                    truModelFieldMapper.insert( fieldDo );
                }
                else{
                    if( !MyBeanUtils.checkSameProperty( ftmp,fieldDo,true,"isSelect","fieldName","origFieldType","origFlag","FieldAlias","fieldExpr"  ) ){
                        bchg = true;
                    }
                    if( !MyBeanUtils.checkSameProperty( fieldDo,ftmp,true,fieldNames ) ){
                        MyBeanUtils.copyBeanProp( fieldDo,ftmp,true,fieldNames);
                        truModelFieldMapper.updateByPrimaryKey(ftmp);
                    }
                }
            }


            if (truNode.getOperators().getWindow() != null && truNode.getOperators().getWindow().getUseTumbleEnd() != null) {
                TTruModelFieldDo fieldDo = new TTruModelFieldDo();
                fieldDo.setElementId(truNode.getId());
                fieldDo.setFieldName("TUMBLE_END");
                truModelFieldMapper.delete(fieldDo);
                if (truNode.getOperators().getWindow().getUseTumbleEnd()) {
                    fieldDo.setId(ComUtil.genId());
                    fieldDo.setFieldType("TIMESTAMP");
                    fieldDo.setFieldAlias("TUMBLE_END");
                    fieldDo.setFieldExpr("TUMBLE_END");
                    fieldDo.setElementId(truNode.getId());
                    fieldDo.setFilterSort(20);
                    fieldDo.setIsSelect(1);
                    truModelFieldMapper.insert(fieldDo);
                }
            }

            if (truNode.getOperators().getWindow() != null && truNode.getOperators().getWindow().getUseTumbleStart() != null) {
                TTruModelFieldDo fieldDo = new TTruModelFieldDo();
                fieldDo.setElementId(truNode.getId());
                fieldDo.setFieldName("TUMBLE_START");
                truModelFieldMapper.delete(fieldDo);
                if (truNode.getOperators().getWindow().getUseTumbleStart()) {
                    fieldDo.setId(ComUtil.genId());
                    fieldDo.setFieldType("TIMESTAMP");
                    fieldDo.setFieldAlias("TUMBLE_START");
                    fieldDo.setFieldExpr("TUMBLE_START");
                    fieldDo.setElementId(truNode.getId());
                    fieldDo.setFilterSort(21);
                    fieldDo.setIsSelect(1);
                    truModelFieldMapper.insert(fieldDo);
                }
            }
        }
        for ( TTruModelFieldDo fieldDo : delList ) {
            bchg = true;
            truModelFieldMapper.deleteByPrimaryKey( fieldDo.getId() );
        }

        /*List<TTruModelFieldDo> elementFieldDos = node.getOperators().getFields();
        List<TTruModelFieldDo> delFieldList = truModelFieldMapper.selectByElementIdAll( elementInfo.getId() );
        List<TTruModelFieldDo> addFieldList = new ArrayList<>();
        List<TTruModelFieldDo> modFieldList = new ArrayList<>();
        boolean bchg = false;
        if ( elementFieldDos.size() != delFieldList.size() ) {
            bchg = true;
        }

        Map<String, TTruModelFieldDo> fieldMap = new HashMap<>();

        int norder = 1;
        for ( TTruModelFieldDo f2 : elementFieldDos ) {
            TTruModelFieldDo f1 = fieldMap.get( f2.getFieldName() );
            if ( f1 != null ) {
                modFieldList.add( f1 );
                if ( !f1.getFieldAlias().equals( f2.getFieldAlias() ) || f1.getIsSelect() != f2.getIsSelect() ) {
                    bchg = true;
                }
                f1.setFilterSort( norder++ );
                f1.setFieldAlias( f2.getFieldAlias() );
                f1.setIsSelect( f2.getIsSelect() );
                f1.setAggregation( f2.getAggregation() );
                f1.setFieldExpr( f2.getFieldExpr() );
                f1.setModifiedTime( ComUtil.getCurTimestamp() );
                delFieldList.remove( f1 );
            }
            else {
                f2.setFilterSort( norder++ );
                f2.setModifiedTime( ComUtil.getCurTimestamp() );
                addFieldList.add( f2 );
                bchg = true;
            }
        }
        for ( TTruModelFieldDo fieldDo : delFieldList ) {
            truModelFieldMapper.deleteByPrimaryKey(  fieldDo.getId() );
        }
        for ( TTruModelFieldDo fieldDo : modFieldList ) {
            truModelFieldMapper.updateByPrimaryKey( fieldDo );
        }
        for ( TTruModelFieldDo fieldDo : addFieldList ) {
            truModelFieldMapper.insert( fieldDo );
        }*/
         //addFieldList, modFieldList, delFieldList;

        if ( bchg ) {

            List<TTruModelElementRelDo> relList = truModelElementRelMapper.selectByModelId( elementInfo.getModelId() );
            List<TTruModelElementDo> eleList = truModelElementMapper.selectByModelId( elementInfo.getModelId() );

            Map<String,String> starMap = new HashMap<>();
            starMap.put( elementInfo.getId(),elementInfo.getId() );
            boolean bok = false;
            while( !bok ) {
                bok = true;
                for ( TTruModelElementRelDo relDo : relList ) {
                    if ( starMap.containsKey( relDo.getStartElementId() ) ) {
                        starMap.put( relDo.getEndElementId(), relDo.getEndElementId() );
                        relList.remove( relDo );
                        bok = false;
                        break;
                    }
                }
            }
            starMap.remove( elementInfo.getId() );
            for ( TTruModelElementDo elementDo : eleList ) {
                if( starMap.containsKey( elementDo.getId() )){
                    TTruModelElementDo etmp = new TTruModelElementDo();
                    etmp.setId( elementDo.getId() );
                    etmp.setRunStatus( 0 );
                    etmp.setModifiedTime( ComUtil.getCurTimestamp() );
                    truModelElementMapper.updateByPrimaryKeySelective( etmp );
                }
            }
        }

//        if (node.getOperators().getFilters() != null && node.getOperators().getFilters().size() > 0) {
//            bydbModelFieldMapper.deleteByElementIdTable(elementInfo.getId(), elementInfo.getId());
//            node.getOperators().getFilters().stream().forEach(e -> {
//                e.setCreatedTime(ComUtil.getCurTimestamp());
//                bydbModelFieldMapper.insert(e);
//            });
//
//        }

    }


}
