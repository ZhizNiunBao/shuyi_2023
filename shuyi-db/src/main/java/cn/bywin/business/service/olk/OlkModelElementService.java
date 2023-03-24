package cn.bywin.business.service.olk;


import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.olk.TOlkFieldDo;
import cn.bywin.business.bean.olk.TOlkModelElementDo;
import cn.bywin.business.bean.olk.TOlkModelElementRelDo;
import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.bean.view.olk.OlkNode;
import cn.bywin.business.bean.view.olk.OlkOperators;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.mapper.federal.DataSourceMapper;
import cn.bywin.business.mapper.olk.OlkModelElementJobMapper;
import cn.bywin.business.mapper.olk.OlkModelElementMapper;
import cn.bywin.business.mapper.olk.OlkModelElementRelMapper;
import cn.bywin.business.mapper.olk.OlkModelFieldMapper;
import cn.bywin.business.mapper.olk.OlkModelObjectMapper;
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
public class OlkModelElementService extends BaseServiceImpl<TOlkModelElementDo, String> {


    @Autowired
    private OlkModelElementMapper truModelElementMapper;
    @Autowired
    private OlkModelElementRelMapper truModelElementRelMapper;

    @Autowired
    private OlkModelFieldMapper truModelFieldMapper;

//    @Autowired
//    private BydbFieldMapper bydbFieldMapper;
//    @Autowired
//    private BydbDsColumnMapper bydbDsColumnMapper;
//    @Autowired
//    private TruModelFieldMapper truModelFieldMapper;
    @Autowired
    private OlkModelObjectMapper bydbModelObjectMapper;

//    @Autowired
//    private BydbObjectMapper tableMapper;
//    @Autowired
//    private BydbDatabaseMapper databaseMapper;

    @Autowired
    private OlkModelElementJobMapper truModelElementJobMapper;
    @Autowired
    private DataSourceMapper dataSourceMapper;
    @Override
    public Mapper<TOlkModelElementDo> getMapper() {
        return truModelElementMapper;
    }

    public List<TOlkModelElementDo> selectByModelIdWithDetail(String modelId) {
        return truModelElementMapper.selectByModelIdWithDetail(modelId);
    }

    public List<TOlkModelElementDo> selectStartId(String vertexId) {

        return truModelElementMapper.selectStartId(vertexId);
    }
    public List<TOlkModelElementDo> selectEndId(String vertexId) {

        return truModelElementMapper.selectEndId(vertexId);
    }

    public List<TOlkModelElementDo> selectByModelId(String modelId) {
        return truModelElementMapper.selectByModelId(modelId);
    }

    @Transactional(rollbackFor = Exception.class)
    public OlkNode getNodes( TOlkModelElementDo elementInfo) throws Exception {
        OlkNode truNode = new OlkNode();
        List<TOlkModelFieldDo> fieldDos = new ArrayList<>();
        OlkOperators truOperators = new OlkOperators();

        if (elementInfo.getElementType() == 1) {
            String tcId = elementInfo.getTcId();
            TOlkModelObjectDo tBydbDatabaseDo = bydbModelObjectMapper.selectByObjectId(tcId,elementInfo.getModelId());
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
            List<TOlkModelFieldDo> filterDos = truModelFieldMapper.selectByElementIdTable(elementInfo.getId());
            List<TOlkModelFieldDo> filterList = filterDos.stream().sorted(Comparator.comparing(TOlkModelFieldDo::getFilterSort)).collect(Collectors.toList());
            truOperators.setFilters(filterList);
        }
        List<String> viewNodes = new ArrayList<>();
        fieldDos.stream().sorted(Comparator.comparing(TOlkModelFieldDo::getFilterSort)).forEach(e -> {
            if (e.getIsSelect() == 1) {
                viewNodes.add(e.getId());
            }
        });
        List<TOlkModelFieldDo> fieldDoList = fieldDos.stream().sorted(Comparator.comparing(TOlkModelFieldDo::getFilterSort)).collect(Collectors.toList());
        truNode.setViewIds(viewNodes);
        TOlkFieldDo tBydbFieldDo = new TOlkFieldDo();
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
        TOlkModelElementDo fModelElementDo = truModelElementMapper.selectById(id);
        List<TOlkModelElementRelDo> elementRels = truModelElementRelMapper.selectByVertexId(id);
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
    public void insertBeanDetail(TOlkModelElementDo elementInfo,List<TOlkFieldDo> fieldList) throws Exception {
        String uuid = ComUtil.genId();
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
                for (TOlkFieldDo fieldDo : fieldList) {
                    TOlkModelFieldDo modField = new TOlkModelFieldDo();
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
                    TOlkModelFieldDo truModelFieldDo = new TOlkModelFieldDo();
                    MyBeanUtils.copyBean2Bean(TOlkModelFieldDo, fieldDo);
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
    public void updateBeanDetail(TOlkModelElementDo elementInfo, OlkNode truNode ) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

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
                List<TOlkModelFieldDo> leftList = truNode.getOperators().getFields().stream().filter( x -> x.getIsSelect()==1 && StringUtils.isNotBlank(  x.getFilterValue() ) ).collect( Collectors.toList() );
                viewNodes.clear();
                leftList.forEach( x -> viewNodes.add( x.getId() ) );
                //List<String> fieldNameList = leftList.stream().map( x -> x.getFilterValue() ).collect( Collectors.toList() );
                //List<TOlkModelFieldDo> rightList = node.getOperators().getFields().stream().filter( x -> x.getElementId().equals( collectionTable.get( 1 ) ) && fieldNameList.contains( x.getFieldName() ) ).collect( Collectors.toList() );
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
            }else if ( "t_operator".equalsIgnoreCase( type ) ){
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

        List<TOlkModelFieldDo> delList = truModelFieldMapper.selectByElementId( elementInfo.getId() );
        Map<String, TOlkModelFieldDo> fieldMap = delList.stream().collect( Collectors.toMap( x -> x.getId(), x -> x ) );
        int norder = 0;
        for ( int i = 0; i< truNode.getOperators().getFields().size(); i++){
            TOlkModelFieldDo fieldDo=   truNode.getOperators().getFields().get(i);
            fieldDo.setAggregation( null );
            fieldDo.setElementId( elementInfo.getId() );
            fieldDo.setTableAlias( elementInfo.getElement() );
            TOlkModelFieldDo ftmp = fieldMap.get( fieldDo.getId() );
            if( ftmp == null){
                //add one
                fieldDo.setId( ComUtil.genId() );
                fieldDo.setElementId( elementInfo.getId() );
                truModelFieldMapper.insert( fieldDo );
            }
            else{
                delList.remove( ftmp );
                if (viewNodes.contains(fieldDo.getId())) {
                    fieldDo.setIsSelect(1);
                    //viewNodes.remove( fiel
                    //
                    // dDo.getId() );
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

        if ( "t_aggregation".equalsIgnoreCase( type ) ){

            List<TOlkModelFieldDo> selList =JsonUtil.deserialize(  JsonUtil.toJson( truNode.getParams().get( "field" )) , new TypeToken<List<TOlkModelFieldDo>>() { }.getType()); //聚合
            List<TOlkModelFieldDo> groupList =JsonUtil.deserialize(  JsonUtil.toJson( truNode.getParams().get( "group" )) , new TypeToken<List<TOlkModelFieldDo>>() { }.getType()); ////分组

            for ( TOlkModelFieldDo fieldDo : groupList ) {
                fieldDo.setIsSelect(1);
                fieldDo.setElementId( elementInfo.getId() );
                fieldDo.setTableAlias( elementInfo.getElement() );
                fieldDo.setFilterSort(norder++);
                TOlkModelFieldDo ftmp = fieldMap.get( fieldDo.getId() );
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
            for ( TOlkModelFieldDo fieldDo : selList ) {
                fieldDo.setIsSelect(1);
                fieldDo.setElementId( elementInfo.getId() );
                fieldDo.setTableAlias( elementInfo.getElement() );
                fieldDo.setFilterSort(norder++);
                TOlkModelFieldDo ftmp = fieldMap.get( fieldDo.getId() );
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
        }
        for ( TOlkModelFieldDo fieldDo : delList ) {
            bchg = true;
            truModelFieldMapper.deleteByPrimaryKey( fieldDo.getId() );
        }

        /*List<TOlkModelFieldDo> elementFieldDos = node.getOperators().getFields();
        List<TOlkModelFieldDo> delFieldList = truModelFieldMapper.selectByElementIdAll( elementInfo.getId() );
        List<TOlkModelFieldDo> addFieldList = new ArrayList<>();
        List<TOlkModelFieldDo> modFieldList = new ArrayList<>();
        boolean bchg = false;
        if ( elementFieldDos.size() != delFieldList.size() ) {
            bchg = true;
        }

        Map<String, TOlkModelFieldDo> fieldMap = new HashMap<>();

        int norder = 1;
        for ( TOlkModelFieldDo f2 : elementFieldDos ) {
            TOlkModelFieldDo f1 = fieldMap.get( f2.getFieldName() );
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
        for ( TOlkModelFieldDo fieldDo : delFieldList ) {
            truModelFieldMapper.deleteByPrimaryKey(  fieldDo.getId() );
        }
        for ( TOlkModelFieldDo fieldDo : modFieldList ) {
            truModelFieldMapper.updateByPrimaryKey( fieldDo );
        }
        for ( TOlkModelFieldDo fieldDo : addFieldList ) {
            truModelFieldMapper.insert( fieldDo );
        }*/
         //addFieldList, modFieldList, delFieldList;

        if ( bchg ) {

            List<TOlkModelElementRelDo> relList = truModelElementRelMapper.selectByModelId( elementInfo.getModelId() );
            List<TOlkModelElementDo> eleList = truModelElementMapper.selectByModelId( elementInfo.getModelId() );

            Map<String,String> starMap = new HashMap<>();
            starMap.put( elementInfo.getId(),elementInfo.getId() );
            boolean bok = false;
            while( !bok ) {
                bok = true;
                for ( TOlkModelElementRelDo relDo : relList ) {
                    if ( starMap.containsKey( relDo.getStartElementId() ) ) {
                        starMap.put( relDo.getEndElementId(), relDo.getEndElementId() );
                        relList.remove( relDo );
                        bok = false;
                        break;
                    }
                }
            }
            starMap.remove( elementInfo.getId() );
            for ( TOlkModelElementDo elementDo : eleList ) {
                if( starMap.containsKey( elementDo.getId() )){
                    TOlkModelElementDo etmp = new TOlkModelElementDo();
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
