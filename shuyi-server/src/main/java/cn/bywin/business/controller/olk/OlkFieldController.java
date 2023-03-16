package cn.bywin.business.controller.olk;


import cn.bywin.business.bean.olk.TOlkDatabaseDo;
import cn.bywin.business.bean.olk.TOlkFieldDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.olk.TOlkSchemaDo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.job.OlkDataNodeJob;
import cn.bywin.business.service.olk.OlkDatabaseService;
import cn.bywin.business.service.olk.OlkFieldService;
import cn.bywin.business.service.olk.OlkObjectService;
import cn.bywin.business.service.olk.OlkSchemaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;


@CrossOrigin(value = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST,RequestMethod.DELETE},
        maxAge = 3600)
@RestController
@Api(tags = "olk-字段管理-olkfield")
@RequestMapping("/olkfield")
public class OlkFieldController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OlkDatabaseService databaseService;

    @Autowired
    private OlkSchemaService shcmeaService;

    @Autowired
    private OlkObjectService objectService;

    @Autowired
    private OlkFieldService fieldService;

    @ApiOperation(value = "修改olk字段", notes = "修改olk字段")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "modelVo", value = "olk字段", dataType = "TOlkFieldDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update(@RequestBody TOlkFieldDo bean, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null || StringUtils.isBlank(ud.getUserId())) {
                return resMap.setErr("请先登录").getResultMap();
            }
            //@RequestBody TOlkFieldDo modelVo,
//            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
//            logger.debug("{}", hru.getAllParaData());
//            TOlkFieldDo info = fieldService.findById(hru.getNvlPara("id"));

            TOlkFieldDo info = fieldService.findById(bean.getId());

            if (info == null) {
                return resMap.setErr("内容不存在").getResultMap();
            }

            TOlkFieldDo oldData = new TOlkFieldDo();
            MyBeanUtils.copyBeanNotNull2Bean(info, oldData);

            MyBeanUtils.copyBeanNotNull2Bean(bean, info);

            //new PageBeanWrapper(info, hru, "");

            info.setFieldName(oldData.getFieldName());

            if (StringUtils.isBlank(info.getFieldName())) {
                return resMap.setErr("名称不能为空").getResultMap();
            }

            if (StringUtils.isBlank(info.getFieldType())) {
                return resMap.setErr("类型不能为空").getResultMap();
            }

            if (StringUtils.isBlank(info.getObjectId())) {
                return resMap.setErr("对象不能为空").getResultMap();
            }

            TOlkObjectDo objectDo = objectService.findById(info.getObjectId());
            if (objectDo == null) {
                return resMap.setErr("对象不存在").getResultMap();
            }
            info.setSchemaId(objectDo.getSchemaId());
            info.setDbId(objectDo.getDbId());

            //info.setReplaceStatement(chgStateField(info.getChgStatement(), info.getFieldName()));

            if( oldData.getEnable() != null ) {
                info.setEnable( oldData.getEnable() );
            }
            else{
                if( info.getEnable() == null) {
                    info.setEnable( 0 );
                }
            }
//            final long sameNameCount = fieldService.findSameNameCount( info );
//            if( sameNameCount >0 ){
//                return resMap.setErr("名称已使用").getResultMap();
//            }

            TOlkObjectDo tmpObj = new TOlkObjectDo();
            tmpObj.setId( objectDo.getId() );
            tmpObj.setSynFlag(  0 );
            objectService.updateNoNull( tmpObj );

            fieldService.updateBean(info);

            OlkDataNodeJob.addTable( objectDo.getId() );

            resMap.setSingleOk(info, "保存成功");

        } catch (Exception ex) {
            resMap.setErr("保存失败");
            logger.error("保存异常:", ex);
        }
        return resMap.getResultMap();
    }

    private String chgStateField(String data, String field) {
        if (StringUtils.isBlank(data))
            return "";
        String[] split = data.split(",");
        boolean byh = false;
        int yhidx = 0;
        final Pattern pat2 = Pattern.compile("'", Pattern.CASE_INSENSITIVE);
        for (int i = 0; i < split.length; i++) {
            String dd = split[i];
            Matcher matcher1 = pat2.matcher(dd);
            int yhcnt = 0;
            while (matcher1.find()) {
                yhcnt++;
                if (yhcnt == 2)
                    yhcnt = 0;
            }
            if (byh) {
                split[yhidx] = split[yhidx] + "," + dd; //之前有引号
                split[i] = "";
                if (yhcnt == 1) { // 奇数 结束
                    byh = false;
                }
            } else {
                if (yhcnt == 1) {
                    byh = true;
                    yhidx = i;
                }
            }
        }
        final Pattern pat = Pattern.compile("(\\(|^|\\s+)" + field + "(\\s+|\\)|$)", Pattern.CASE_INSENSITIVE);
        final Pattern pat1 = Pattern.compile(field, Pattern.CASE_INSENSITIVE);
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            Matcher matcher1 = pat.matcher(s);
            if (matcher1.find()) {
                String s1 = pat1.matcher(matcher1.group()).replaceFirst(" @@field@@ ");
                String s2 = matcher1.replaceFirst(s1);
                System.out.println(s2);
                split[i] = s2;
            }
        }
        String collect = Arrays.asList(split).stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(","));
        return collect;
    }

    @ApiOperation(value = "批量启用或禁用olk字段内容", notes = "修改olk字段内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "字段id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "enable", value = "启用1，禁用0", dataType = "Integer", required = true, paramType = "query")
    })
    @RequestMapping(value = "/enabledata", method = {RequestMethod.POST})
    @ResponseBody
    public Object enableData(String id, Integer enable, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        String actType = "操作";
        try {
            logger.debug("id:{},enable:{}", id, enable);
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null || StringUtils.isBlank(ud.getUserId())) {
                return resMap.setErr("请先登录").getResultMap();
            }
            if (enable == null || !(enable == 0 || enable == 1)) {
                return resMap.setErr("标记不正确").getResultMap();
            }
            List<String> split = Arrays.asList(id.split("(,|\\s)+"));
            Example exp = new Example(TOlkFieldDo.class);
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn("id", split);

            if (enable == 0) {
                actType = "禁用";
            } else {
                actType = "启用";
            }

            String act = "修改-" + actType + "olk字段" + System.currentTimeMillis();
            if (enable == 1) {
                criteria.andCondition(" (enable =0  or enable is null )");
            } else {
                criteria.andEqualTo("enable", 1);
            }
            List<TOlkFieldDo> chgList = fieldService.findByExample(exp);
            if (chgList.size() != split.size()) {
                return resMap.setErr(" 数据已变化不能操作").getResultMap();
            }
            List<TOlkFieldDo> oldList = new ArrayList<>();

            List<TOlkDatabaseDo> dbList = null;
            List<TOlkSchemaDo> schemaList = null;
            List<TOlkObjectDo> objectList = new ArrayList<>();
            if (enable == 1) {
                for (TOlkFieldDo info : chgList) {
                    TOlkFieldDo old = new TOlkFieldDo();
                    MyBeanUtils.copyBeanNotNull2Bean(info, old);
                    oldList.add(old);
                    info.setEnable(1);
                }
                List<String> idList = chgList.stream().map(x -> x.getDbId()).distinct().collect(Collectors.toList());
                exp = new Example(TOlkDatabaseDo.class);
                criteria = exp.createCriteria();
                criteria.andIn("id", idList);
                criteria.andCondition(" (enable =0  or enable is null )");
                dbList = databaseService.findByExample(exp);
                for (TOlkDatabaseDo tOlkDatabaseDo : dbList) {
                    tOlkDatabaseDo.setEnable(enable);
                    tOlkDatabaseDo.setSynFlag( 0 );
                }

                idList = chgList.stream().map(x -> x.getSchemaId()).distinct().collect(Collectors.toList());
                exp = new Example(TOlkSchemaDo.class);
                criteria = exp.createCriteria();
                criteria.andIn("id", idList);
                criteria.andCondition(" (enable =0  or enable is null )");
                schemaList = shcmeaService.findByExample(exp);
                for (TOlkSchemaDo schemaDo : schemaList) {
                    schemaDo.setEnable(enable);
                    schemaDo.setSynFlag( 0 );
                }
            } else {
                for (TOlkFieldDo info : chgList) {
                    TOlkFieldDo old = new TOlkFieldDo();
                    MyBeanUtils.copyBeanNotNull2Bean(info, old);
                    oldList.add(old);
                    info.setEnable(0);
                }
            }

            List<String> idList = chgList.stream().map(x -> x.getObjectId()).distinct().collect(Collectors.toList());
            exp = new Example(TOlkObjectDo.class);
            criteria = exp.createCriteria();
            criteria.andIn("id", idList);
            //criteria.andCondition(" (enable =0  or enable is null )");
            List<TOlkObjectDo> objTmpList = objectService.findByExample( exp );
            for (TOlkObjectDo tmp : objTmpList) { //只更新指定字段
                TOlkObjectDo objectDo = new TOlkObjectDo();
                objectDo.setId( tmp.getId() );
                if( enable ==1 )
                {
                    objectDo.setEnable(enable);
                }
                objectDo.setSynFlag( 0 );
                objectList.add( objectDo );
            }

            fieldService.updateBeanWithFlag(chgList, dbList, schemaList, objectList);

            OlkDataNodeJob.reInit();

//            for (int i = 0; i < chgList.size(); i++) {
//                TOlkFieldDo info = chgList.get(i);
//                TOlkFieldDo old = oldList.get(i);
//                HashMap<String, Object> map = new HashMap<>();
//                if (dbList != null) {
//                    for (TOlkDatabaseDo tOlkDatabaseDo : dbList) {
//                        if (tOlkDatabaseDo.getId().equals(info.getDbId())) {
//                            map.put("db", tOlkDatabaseDo);
//                            break;
//                        }
//                    }
//                }
//                if (schemaList != null) {
//                    for (TOlkSchemaDo schemaDo : schemaList) {
//                        if (schemaDo.getId().equals(info.getSchemaId())) {
//                            map.put("schema", schemaDo);
//                            break;
//                        }
//                    }
//                }
//
//                if (objectList != null) {
//                    for (TOlkObjectDo objectDo : objectList) {
//                        if (objectDo.getId().equals(info.getObjectId())) {
//                            map.put("object", objectDo);
//                            break;
//                        }
//                    }
//                }
////                String msg = JsonUtil.toJson(map);
////
////                new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(ud, old, info, msg, act);
//            }
            if (enable == 0) {
                resMap.setOk(actType + "成功");
            } else {
                resMap.setOk(actType + "成功");
            }

        } catch (Exception ex) {
            resMap.setErr(actType + "失败");
            logger.error(actType + "异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "olk字段内容", notes = "olk字段内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "语句元素id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    @ResponseBody
    public Object info(String id) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if (StringUtils.isBlank(id)) {
                return resMap.setErr("id不能为空").getResultMap();
            }
            TOlkFieldDo modelVo = fieldService.findById(id);
            resMap.setSingleOk(modelVo, "成功");

        } catch (Exception ex) {
            resMap.setErr("查询失败");
            logger.error("查询异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除olk字段", notes = "删除olk字段")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "olk字段 id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public Object delete(String id, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if (StringUtils.isBlank(id)) {
                return resMap.setErr("id不能为空").getResultMap();
            }
            UserDo user = LoginUtil.getUser(request);
//            if( !id.matches("^[a-zA-Z0-9\\-_,]*$") ){
//                return resMap.setErr("id有非法字符").getResultMap();
//            }
            List<String> ids = Arrays.asList(id.split(",|\\s+"));
            Example exp = new Example(TOlkFieldDo.class);
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn("id", ids);
            List<TOlkFieldDo> list = fieldService.findByExample(exp);
            int cnt = list.size();
            if (cnt == 0) {
                return resMap.setErr("没有数据可删除").getResultMap();
            }
            if (list.size() != ids.size()) {
                return resMap.setErr("有数据不存在").getResultMap();
            }

            List<String> objIdList = list.stream().map( x -> x.getObjectId() ).distinct().collect( Collectors.toList() );
            List<TOlkObjectDo> objList = new ArrayList<>();
            for ( String s : objIdList ) {
                TOlkObjectDo objectDo = new TOlkObjectDo();
                objectDo.setId( s );
                objectDo.setSynFlag( 0 );
                objList.add( objectDo );
            }

//            exp = new Example( TOlkItemObjectDo.class );
//            criteria = exp.createCriteria();
//            criteria.andIn( "objectId", split);
//            List<TOlkItemObjectDo> itemObjlist = itemObjectService.findByExample(exp);

//            exp = new Example( TOlkGroupObjectDo.class );
//            criteria = exp.createCriteria();
//            criteria.andIn( "objectId", split);
//            List<TOlkGroupObjectDo> groupObjlist = groupObjectService.findByExample(exp);

            fieldService.delWithUpdate(list,objList);

            OlkDataNodeJob.addTableList(  objIdList );
//            String times = String.valueOf(System.currentTimeMillis());
//            for (TOlkFieldDo info : list) {
//                try {
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).delLog(user, info, "删除-olk字段" + times);
//                } catch (Exception e1) {
//                    resMap.setErr("删除失败");
//                    logger.error("删除异常:", e1);
//                }
//            }
            resMap.setOk("删除成功");

        } catch (Exception ex) {
            resMap.setErr("删除失败");
            logger.error("删除异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取字段列表", notes = "获取字段列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qryCond", value = "模糊条件", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "currentPage", value = "页数", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", required = false, paramType = "query")
    })
    @RequestMapping(value = "/page", method = {RequestMethod.GET})
    @ResponseBody
    public Object page(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            TOlkFieldDo modelVo = new TOlkFieldDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
            new PageBeanWrapper(modelVo, hru);
            modelVo.setQryCond( ComUtil.chgLikeStr(modelVo.getQryCond()));
            modelVo.setFieldName(ComUtil.chgLikeStr(modelVo.getFieldName()));

            long findCnt = fieldService.findBeanCnt(modelVo);
            modelVo.genPage(findCnt);

            List<TOlkFieldDo> list = fieldService.findBeanList(modelVo);

            resMap.setPageInfo(modelVo.getPageSize(), modelVo.getCurrentPage());
            resMap.setOk(findCnt, list, "获取字段列表成功");
        } catch (Exception ex) {
            resMap.setErr("获取字段列表失败");
            logger.error("获取字段列表失败:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取表字段", notes = "获取表字段")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "objectId", value = "表id", dataType = "String", required = true, paramType = "query"),
    })
    @RequestMapping(value = "/option", method = {RequestMethod.GET})
    @ResponseBody
    public Object option(String objectId, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            TOlkFieldDo modelVo = new TOlkFieldDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
            new PageBeanWrapper(modelVo, hru);
            modelVo.setObjectId(objectId);
            //联邦分析字段
//            if (StringUtils.isNotBlank(objectId) && objectId.startsWith("ide_")) {
//                String id=objectId.split("ide_")[1].replaceAll(" ", "").replaceAll("\\\r|\\\n", "");
//
//                TOlkModelFieldDo fieldDo = olkModelFieldService.findById(id);
//                TOlkModelElementDo elementDos = olkModelService.findById(fieldDo.getElementId());
//                List<TOlkModelFieldDo> fieldDos = new ArrayList<>();
//                if (elementDos != null) {
//                    List<TOlkModelFieldDo> fieldDos1 = olkModelFieldService.selectByElementId(elementDos.getId());
//                    TOlkModelComponentDo componentDo = olkModelComponentService.findById(elementDos.getTcId());
//                    if (componentDo != null&&( componentDo.getComponentEn().equals(GROUP_COMPONENT.getComponentName()) ||
//                            componentDo.getComponentEn().equals(FieldConcat_COMPONENT.getComponentName()))) {
//                        fieldDos1.stream().forEach(e -> {
//                            if (e.getElementId().equals(e.getExtendsId()) && e.getIsSelect() == 1) {
//                                fieldDos.add(e);
//                            }
//                        });
//                    } else {
//                        fieldDos1.stream().forEach(e -> {
//                            if (e.getIsSelect() == 1) {
//                                fieldDos.add(e);
//                            }
//                        });
//                    }
//                }
//                resMap.setSingleOk(fieldDos, "获取表字段");

//            } else {
//                List<TOlkFieldDo> list = fieldService.findBeanList(modelVo);
//                //List<TOlkModelFieldDo> list1 =  olkModelFieldService.selectByModelId();
//                resMap.setPageInfo(modelVo.getPageSize(), modelVo.getCurrentPage());
//                resMap.setSingleOk(list, "获取表字段");
//            }

        } catch (Exception ex) {
            resMap.setErr("获取表字段失败");
            logger.error("获取表字段失败:", ex);
        }
        return resMap.getResultMap();
    }

}
