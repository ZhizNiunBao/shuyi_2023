package cn.bywin.business.controller.olk;

import static cn.bywin.business.common.enums.TreeRootNodeEnum.DATASOURCE;

import cn.bywin.business.bean.olk.TOlkCatalogTypeDo;
import cn.bywin.business.bean.olk.TOlkDatabaseDo;
import cn.bywin.business.bean.olk.TOlkDcServerDo;
import cn.bywin.business.bean.olk.TOlkFieldDo;
import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.olk.TOlkSchemaDo;
import cn.bywin.business.bean.request.analysis.NewDbRequest;
import cn.bywin.business.bean.request.analysis.QueryDataRequest;
import cn.bywin.business.bean.request.analysis.RefreshSchemaRequest;
import cn.bywin.business.bean.request.analysis.UpdateDatabaseRequest;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.Constants;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.hetu.CatalogInfo;
import cn.bywin.business.hetu.DynamicCatalogResult;
import cn.bywin.business.hetu.EncryptFlag;
import cn.bywin.business.hetu.HetuDynamicCatalogUtil;
import cn.bywin.business.hetu.HetuInfo;
import cn.bywin.business.hetu.HetuJdbcOperate;
import cn.bywin.business.hetu.HetuJdbcOperateComponent;
import cn.bywin.business.service.olk.OlkCatalogTypeService;
import cn.bywin.business.service.olk.OlkDatabaseService;
import cn.bywin.business.service.olk.OlkFieldService;
import cn.bywin.business.service.olk.OlkModelObjectService;
import cn.bywin.business.service.olk.OlkObjectService;
import cn.bywin.business.service.olk.OlkSchemaService;
import cn.bywin.cache.SysParamSetOp;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

@CrossOrigin(value = {"*"},
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
    maxAge = 3600)
@RestController
@Api(tags = "olk-数据目录管理-olkdatabase")
@RequestMapping("/olkdatabase")
public class OlkDatabaseController extends BaseController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OlkDatabaseService databaseService;

    @Autowired
    private OlkSchemaService schemaService;

    @Autowired
    private OlkObjectService objectService;

    @Autowired
    private OlkFieldService fieldService;

    @Autowired
    private OlkCatalogTypeService catalogTypeService;

    @Autowired
    private OlkModelObjectService modelObjService;

    String connectChar = "^";

    private static final String CATALOG_CHNAME_REGEX = "^[a-zA-Z0-9\\\u4e00-\\\u9fa5]{2,30}$";
    private static final Pattern CATALOG_CHNAME_PATTERN = Pattern
        .compile(CATALOG_CHNAME_REGEX, Pattern.CASE_INSENSITIVE);

    private static final String CATALOG_NAME_REGEX = "^[a-zA-Z]{1}[a-zA-Z0-9_]{2,20}$";
    private static final Pattern CATALOG_NAME_PATTERN = Pattern
        .compile(CATALOG_NAME_REGEX, Pattern.CASE_INSENSITIVE);

    private static final String DB_CHNAME_REGEX = "^[a-zA-Z0-9\\\u4e00-\\\u9fa5]{2,30}$";
    private static final Pattern DB_CHNAME_PATTERN = Pattern.compile(DB_CHNAME_REGEX, Pattern.CASE_INSENSITIVE);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private String olkRef = "olk_ref_";

    @Autowired
    private HetuJdbcOperateComponent hetuJdbcOperateComponent;

    @Autowired
    private TOlkDcServerDo dcServerDo;

    @ApiOperation(value = "按配置新建olk数据目录", notes = "按配置新建olk数据目录")
    @RequestMapping(value = "/newdb", method = {RequestMethod.POST})
    public Object newDb(@RequestBody NewDbRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser();

            String catalogName = request.getCatalogName();
            String chName = request.getChName();
            String connectorName = request.getConnectorName();
            String propSet = request.getPropSet();
            String catalogType = request.getCatalogType();

            TOlkDatabaseDo info = new TOlkDatabaseDo();

            Preconditions
                .checkArgument(StringUtils.isNotBlank(chName), "中文名称不能为空,只能为2-30个字符,字符可为中文大小写字母数字");
            Matcher matcher = CATALOG_CHNAME_PATTERN.matcher(chName);
            Preconditions.checkArgument(matcher.find(), "中文名称只能为2-30个字符,字符可为中文大小写字母数字");

            Preconditions.checkArgument(StringUtils.isNotBlank(catalogName),
                "英文名称不能为空,只能为大小写字母开头3-20个字符，其他字符可为大小写字母数字和下划线");
            matcher = CATALOG_NAME_PATTERN.matcher(catalogName);
            Preconditions.checkArgument(matcher.find(), "英文名称只能为大小写字母开头3-20个字符，其他字符可为大小写字母数字和下划线");

            Preconditions.checkArgument(!"system".equalsIgnoreCase(catalogName), "名称不能为system");
            Preconditions.checkArgument(StringUtils.isNotBlank(connectorName), "连接类型不能为空");
            Preconditions.checkArgument(StringUtils.isNotBlank(propSet), "配置不能为空");

            if (StringUtils.isNotBlank(catalogType)) {
                TOlkCatalogTypeDo ctype = catalogTypeService.findById(catalogType);
                Preconditions.checkArgument(ctype != null, "目录类型不存在");

                info.setCatalogType(ctype.getId());
            }

            info.setDbsourceId(Constants.dchetu);
            info.setId("odb" + ComUtil.genId());
            info.setDbChnName(chName);
            info.setUserId(user.getUserId());
            info.setUserName(user.getChnName());
            info.setUserAccount(user.getUserName());
            info.setSynFlag(0);

            info.setDbName(catalogName);
            info.setDcDbName(catalogName);

            info.setDbType(connectorName);
            LoginUtil.setBeanInsertUserInfo(info, user);

            final long sameNameCount = databaseService.findSameNameCount(info);
            if (sameNameCount > 0) {
                return resMap.setErr("名称已使用").getResultMap();
            }

            Map<String, String> properties = new HashMap<>();
            Type jsonType = new TypeToken<List<HashMap>>() {
            }.getType();
            List<Map> list = JsonUtil.deserialize(propSet, jsonType);
            for (Map map : list) {
                String name = map.get("name") == null ? "" : map.get("name").toString().trim();
                String value = map.get("value") == null ? "" : map.get("value").toString().trim();
                if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value)) {
                    properties.put(name, value);
                }
            }

            CatalogInfo catalogInfo = hetuJdbcOperateComponent
                .genCatalogInfo(EncryptFlag.isEncrypt(dcServerDo.getEncryptFlag()),
                    catalogName, connectorName, properties);

            Integer maxOrder = databaseService.findMaxOrder();
            int norder = 10;
            if (maxOrder != null) {
                norder = maxOrder.intValue() + 10;
            }
            info.setNorder(norder);
            info.setEnable(1);

            HetuInfo hetuInfo = hetuJdbcOperateComponent.genHetuInfo();
            boolean bexists = HetuDynamicCatalogUtil.checkCatalogExist(hetuInfo, info.getDbName());
            if (bexists) {
                resMap.setSingleOk(info, "目录已存在");
            }
            DynamicCatalogResult dynamicCatalogResult = HetuDynamicCatalogUtil
                .addCatalog(hetuInfo, catalogInfo);
            if (dynamicCatalogResult.isSuccessful()) {
                //success
                databaseService.insertBean(info);
                String redKey = olkRef + info.getId();
                loadCatalogSchemaOnly(info, redKey, user);
                new Thread(() -> {
                    loadOlkCatalogMeta(info, null, null, redKey, user);
                }).start();
                resMap.setSingleOk(info, "保存成功");
            } else {
                String codeMap = "401 UNAUTHORIZED 没有权限添加目录 没有权限修改目录 没有权限修改目录 \n" +
                    "302 FOUND 目录已存在 - - \n" +
                    "404 NOT_FOUND 动态目录已停用 目录不存在或动态目录已停用 目录不存在或动态目录已停用 \n" +
                    "400 BAD_REQUEST 请求不正确 请求不正确 请求不正确 \n" +
                    "409 CONFLICT 另一个会话正在操作目录 另一个会话正在操作目录 另一个会话正在操作目录 \n" +
                    "500 INTERNAL_SERVER_ERROR 协调节点内部发生错误 协调节点内部发生错误 协调节点内部发生错误 \n" +
                    "201 CREATED 成功 成功 - \n" +
                    "204 NO_CONTENT - - 成功 \n";
                String[] split = codeMap.split("\\n");
                String errMsg = "";
                int responseCode = dynamicCatalogResult.getResponseCode();
                for (String s : split) {
                    if (s.indexOf("" + responseCode) >= 0) {
                        errMsg = s.split("\\s+")[2];
                    }
                }
                resMap.setErr(String.format("提交失败，返回:%s %s", responseCode, errMsg));
            }

        } catch (Exception ex) {
            resMap.setErr("保存失败");
            logger.error("保存异常:", ex);
        }
        return resMap.getResultMap();
    }

    private void loadCatalogSchemaOnly(TOlkDatabaseDo dbInfo, String redKey, UserDo user) {

        List<String> schemaNameList = new ArrayList<>();
        //List<TOlkSchemaDo> delSchemaList = null;

        List<TOlkSchemaDo> allSchemaList = new ArrayList<>();

        BoundValueOperations<String, Object> bvo = redisTemplate.boundValueOps(redKey);
        //HashMap<String, String> schemaMap = new HashMap<>();

        try (HetuJdbcOperate jdbcOp = hetuJdbcOperateComponent.genHetuJdbcOperate()) {

            JsonObject noDealSchema = JsonUtil
                .toJsonObject(SysParamSetOp.readValue("OlkNoDealSchema", "{}"));
            TOlkSchemaDo schTmp = new TOlkSchemaDo();
            schTmp.setDbId(dbInfo.getId());
            List<TOlkSchemaDo> schemaList = schemaService.find(schTmp);

            JsonElement jsonElement = noDealSchema.get(dbInfo.getDbType());
            logger.debug("dbType:{},set:{} , noDealSchema:{}", dbInfo.getDbType(), jsonElement,
                noDealSchema);

            List<String> noDealList = new ArrayList<>();
            noDealList.add("information_schema");
            noDealList.add("sys");
            noDealList.add("performance_schema");
            if (jsonElement != null && !jsonElement.isJsonNull() && StringUtils
                .isNotBlank(jsonElement.getAsString())) {

                String[] split = jsonElement.getAsString().split("\\\r|\\\n|,");
                for (String s : split) {
                    if (StringUtils.isNotBlank(s)) {
                        noDealList.add(s.toLowerCase());
                    }
                }
                logger.info("不处理模式:{},noDealList:{}", jsonElement.getAsString(), noDealList);
            }

            bvo.expire(3, TimeUnit.MINUTES);
            String showSchema = String.format("SHOW SCHEMAS from %s", dbInfo.getDcDbName());
            logger.info(showSchema);
            List<Map<String, Object>> list1 = jdbcOp.selectData(showSchema);
            logger.debug("schema:{}", list1);
            bvo.expire(3, TimeUnit.MINUTES);
            for (Map<String, Object> dat : list1) {
                Iterator<Object> iterator = dat.values().iterator();
                if (iterator.hasNext()) {
                    String sn = iterator.next().toString();
                    if (noDealList.indexOf(sn.toLowerCase()) < 0 && sn.indexOf(" ") < 0) {
                        schemaNameList.add(sn);
                    } else {
                        logger.info("忽略schema:{}", sn);
                    }
                }
            }
            List<TOlkSchemaDo> delList = new ArrayList<>();
            for (TOlkSchemaDo sch : schemaList) {
                if (schemaNameList.indexOf(sch.getSchemaName()) < 0) {
                    delList.add(sch);
                }
            }
            if (delList.size() > 0) {
                List<String> deIdList = delList.stream().map(x -> x.getId())
                    .collect(Collectors.toList());
                schemaService.deleteWhithRel(delList);
            }
            int norder = 10;
            schemaList = schemaService.find(schTmp);
            for (String schemaName : schemaNameList) {
                boolean bfound = false;
                for (TOlkSchemaDo schemaDo : schemaList) {
                    if (schemaDo.getSchemaName().equals(schemaName)) {
                        bfound = true;
                        schemaDo.setNorder(norder);
                        schemaService.updateBean(schemaDo);
                        allSchemaList.add(schemaDo);
                        break;
                    }
                }
                if (!bfound) {
                    TOlkSchemaDo schemaDo = new TOlkSchemaDo();
                    schemaDo.setNorder(norder);
                    schemaDo.setEnable(1);
                    schemaDo.setUserId(dbInfo.getUserId());
                    schemaDo.setUserAccount(dbInfo.getUserAccount());
                    schemaDo.setUserName(dbInfo.getUserName());
                    schemaDo.setSchemaName(schemaName);
                    schemaDo.setScheFullName(dbInfo.getDbName() + "." + schemaName);
                    schemaDo.setDbId(dbInfo.getId());
                    schemaDo.setId("odb" + ComUtil.genId());
                    schemaDo.setSynFlag(0);
                    LoginUtil.setBeanInsertUserInfo(schemaDo, user);
                    schemaService.insertBean(schemaDo);
                    allSchemaList.add(schemaDo);
                    schemaList.add(schemaDo);
                }
                norder += 10;
            }
        } catch (Exception e) {
            logger.error("刷新节点的" + dbInfo.getDcDbName() + "元数据数据失败", e);
        }
        redisTemplate.delete(redKey);

    }

    private void loadOlkCatalogMeta(TOlkDatabaseDo dbInfo,
        TOlkSchemaDo schemaInfo, TOlkObjectDo objectInfo, String redKey, UserDo user) {

        List<TOlkObjectDo> allObjectList = new ArrayList<>();
        List<String> schemaNameList = new ArrayList<>();

        List<TOlkSchemaDo> allSchemaList = new ArrayList<>();

        BoundValueOperations<String, Object> bvo = redisTemplate.boundValueOps(redKey);
        HashMap<String, String> schemaMap = new HashMap<>();
        try (HetuJdbcOperate jdbcOp = hetuJdbcOperateComponent.genHetuJdbcOperate()) {

            //某张表
            if (objectInfo != null) {
                TOlkSchemaDo schemaDo = schemaService.findById(objectInfo.getSchemaId());
                schemaMap.put(schemaDo.getId(), schemaDo.getSchemaName());
                allObjectList.add(objectInfo);
            }
            //整个shcema
            else if (schemaInfo != null) {
                allSchemaList.add(schemaInfo);
            } else {  //整个catalog
                JsonObject noDealSchema = JsonUtil
                    .toJsonObject(SysParamSetOp.readValue("OlkNoDealSchema", "{}"));
                TOlkSchemaDo schTmp = new TOlkSchemaDo();
                schTmp.setDbId(dbInfo.getId());
                List<TOlkSchemaDo> oldList = schemaService.find(schTmp);

                JsonElement jsonElement = noDealSchema.get(dbInfo.getDbType());
                logger.debug("dbType:{},set:{} , noDealSchema:{}", dbInfo.getDbType(), jsonElement,
                    noDealSchema);

                List<String> noDealList = new ArrayList<>();
                noDealList.add("information_schema");
                noDealList.add("sys");
                noDealList.add("performance_schema");
                if (jsonElement != null && !jsonElement.isJsonNull() && StringUtils
                    .isNotBlank(jsonElement.getAsString())) {

                    String[] split = jsonElement.getAsString().split("\\\r|\\\n|,");
                    for (String s : split) {
                        if (StringUtils.isNotBlank(s)) {
                            noDealList.add(s.toLowerCase());
                        }
                    }
                    logger.info("不处理模式:{},noDealList:{}", jsonElement.getAsString(), noDealList);
                }

                bvo.expire(3, TimeUnit.MINUTES);
                String showSchema = String.format("SHOW SCHEMAS from %s", dbInfo.getDcDbName());
                logger.info(showSchema);
                List<Map<String, Object>> list1 = jdbcOp.selectData(showSchema);
                logger.debug("schema:{}", list1);
                bvo.expire(3, TimeUnit.MINUTES);
                for (Map<String, Object> dat : list1) {
                    Iterator<Object> iterator = dat.values().iterator();
                    if (iterator.hasNext()) {
                        String sn = iterator.next().toString();
                        if (noDealList.indexOf(sn.toLowerCase()) < 0 && sn.indexOf(" ") < 0) {
                            schemaNameList.add(sn);
                        } else {
                            logger.info("忽略schema:{}", sn);
                        }
                    }
                }
                List<TOlkSchemaDo> delList = new ArrayList<>();
                for (TOlkSchemaDo sch : oldList) {
                    if (schemaNameList.indexOf(sch.getSchemaName()) < 0) {
                        delList.add(sch);
                    }
                }
                if (delList.size() > 0) {
                    List<String> deIdList = delList.stream().map(x -> x.getId())
                        .collect(Collectors.toList());
                    schemaService.deleteWhithRel(delList);
                }
                int norder = 10;
                oldList = schemaService.find(schTmp);
                for (String schemaName : schemaNameList) {
                    boolean bfound = false;
                    for (TOlkSchemaDo schemaDo : oldList) {
                        if (schemaDo.getSchemaName().equals(schemaName)) {
                            bfound = true;
                            schemaDo.setNorder(norder);
                            schemaService.updateBean(schemaDo);
                            allSchemaList.add(schemaDo);
                            break;
                        }
                    }
                    if (!bfound) {
                        TOlkSchemaDo schemaDo = new TOlkSchemaDo();
                        schemaDo.setNorder(norder);
                        schemaDo.setEnable(1);
                        schemaDo.setUserId(dbInfo.getUserId());
                        schemaDo.setUserName(dbInfo.getUserName());
                        schemaDo.setUserAccount(dbInfo.getUserAccount());
                        schemaDo.setSynFlag(0);
                        schemaDo.setSchemaName(schemaName);
                        schemaDo.setScheFullName(dbInfo.getDbName() + "." + schemaName);
                        schemaDo.setDbId(dbInfo.getId());
                        schemaDo.setId("odb" + ComUtil.genId());
                        LoginUtil.setBeanInsertUserInfo(schemaDo, user);
                        schemaService.insertBean(schemaDo);
                        allSchemaList.add(schemaDo);
                        delList.add(schemaDo);
                    }
                    norder += 10;
                }

            }

            for (TOlkSchemaDo schemaDo : allSchemaList) {
                if (schemaDo.getEnable() == null || schemaDo.getEnable() != 1) {
                    continue;
                }
                schemaMap.put(schemaDo.getId(), schemaDo.getSchemaName());
                //db 所有表
                TOlkObjectDo objTmp = new TOlkObjectDo();
                objTmp.setSchemaId(schemaDo.getId());
                List<TOlkObjectDo> delObjectList = objectService.find(objTmp);
                int objorder = 10;
                bvo.expire(3, TimeUnit.MINUTES);
                String sql = "select table_name as tablename,remarks as comment ,concat( table_cat,'.', table_schem) as schemaname from \"system\".jdbc.tables where  1=1  ";
                sql = String.format(" %s and table_cat ='%s' and table_schem ='%s' ", sql,
                    dbInfo.getDcDbName(), schemaDo.getSchemaName());
                logger.info(sql);
                List<Map<String, Object>> list2 = jdbcOp.selectData(sql);
                logger.debug("table:{}", list2);
                bvo.expire(3, TimeUnit.MINUTES);
                for (Map<String, Object> dat : list2) {
                    String tabName = (String) dat.get("tablename");
                    boolean bobj = false;
                    for (TOlkObjectDo objectDo : delObjectList) {
                        if (objectDo.getObjectName().equals(tabName)) {
                            bobj = true;
                            if (StringUtils.isBlank(objectDo.getObjChnName())) {
                                objectDo.setObjChnName((String) dat.get("comment"));
                            }
                            objectDo.setNorder(objorder);
                            objectService.updateBean(objectDo);
                            delObjectList.remove(objectDo);
                            allObjectList.add(objectDo);
                            break;
                        }
                    }
                    if (!bobj) {
                        TOlkObjectDo objectDo = new TOlkObjectDo();
                        objectDo.setUserId(dbInfo.getUserId());
                        objectDo.setUserName(dbInfo.getUserName());
                        objectDo.setUserAccount(dbInfo.getUserAccount());
                        objectDo.setSynFlag(0);
                        objectDo.setDbId(dbInfo.getId());
                        objectDo.setSchemaId(schemaDo.getId());
                        objectDo.setObjChnName((String) dat.get("comment"));
                        objectDo.setNorder(objorder);
                        objectDo.setObjectName(tabName);
                        objectDo.setObjFullName(
                            schemaDo.getScheFullName() + "." + objectDo.getObjectName());
                        objectDo.setStype("table");
                        objectDo.setEnable(1);
                        objectDo.setShareFlag(0);
                        objectDo.setSynFlag(0);

                        objectDo.setId("odb" + ComUtil.genId());
                        LoginUtil.setBeanInsertUserInfo(objectDo, user);
                        objectService.insertBean(objectDo);
                        allObjectList.add(objectDo);
                    }
                    objorder += 10;


                }
                if (delObjectList.size() > 0) {
                    objectService.deleteWhithOthers(delObjectList);
                }
            }
            for (TOlkObjectDo objectDo : allObjectList) {
                if (objectDo.getEnable() == null || objectDo.getEnable() != 1) {
                    continue;
                }
                List<TOlkFieldDo> addFieldList = new ArrayList<>();
                List<TOlkFieldDo> modFieldList = new ArrayList<>();

                //db 所有字段
                TOlkFieldDo fieldTmp = new TOlkFieldDo();
                fieldTmp.setObjectId(objectDo.getId());
                List<TOlkFieldDo> delFieldList = fieldService.find(fieldTmp);
                HashMap<String, TOlkFieldDo> modMap = new HashMap<>();

                String schemaName = schemaMap.get(objectDo.getSchemaId());
                int fieldorder = 10;
                bvo.expire(3, TimeUnit.MINUTES);
                String sql =
                    "select TABLE_SCHEMA schemaname,TABLE_NAME tablename, COLUMN_NAME columnname,ORDINAL_POSITION ordinalposition,\n"
                        +
                        "data_type as datatype,data_type as columntype,comment columncomment from "
                        + dbInfo.getDcDbName() + ".information_schema.COLUMNS a where 1=1 ";
                sql = String.format(" %s and TABLE_SCHEMA ='%s' ", sql, schemaName);
                sql = String.format(" %s and TABLE_NAME ='%s' ", sql, objectDo.getObjectName());

                logger.debug(sql);
                List<Map<String, Object>> list3 = jdbcOp.selectData(sql);
                logger.debug("field:{}", list3);
                bvo.expire(3, TimeUnit.MINUTES);
                for (Map<String, Object> dat3 : list3) {
                    TOlkFieldDo fieldDo = new TOlkFieldDo();
                    fieldDo.setFieldName((String) dat3.get("columnname"));
                    fieldDo.setFieldType((String) dat3.get("columntype"));
                    fieldDo.setChnName((String) dat3.get("columncomment"));
                    fieldDo.setUserId(dbInfo.getUserId());
                    fieldDo.setUserName(dbInfo.getUserName());
                    fieldDo.setUserAccount(dbInfo.getUserAccount());
                    fieldDo.setDbId(dbInfo.getId());
                    fieldDo.setSchemaId(objectDo.getSchemaId());
                    fieldDo.setObjectId(objectDo.getId());
                    fieldDo
                        .setFieldFullName(objectDo.getObjFullName() + "." + fieldDo.getFieldName());
                    fieldDo.setNorder(fieldorder);
                    fieldDo.setEnable(1);
                    fieldorder += 10;
                    fieldDo.setId("odb" + ComUtil.genId());
                    LoginUtil.setBeanInsertUserInfo(fieldDo, user);
                    boolean bfield = false;
                    for (TOlkFieldDo fd : delFieldList) {
                        if (fieldDo.getObjectId().equals(fd.getObjectId()) && StringUtils
                            .equalsIgnoreCase(fieldDo.getFieldName(), fd.getFieldName())) {
                            bfield = true;
                            TOlkFieldDo old = new TOlkFieldDo();
                            MyBeanUtils.copyBeanNotNull2Bean(fd, old);
                            modMap.put(fd.getId(), old);
                            modFieldList.add(fd);
                            if (StringUtils.isBlank(fd.getChnName())) {
                                fd.setChnName(fieldDo.getChnName());
                            }
                            fd.setFieldName(fieldDo.getFieldName());
                            fd.setFieldFullName(fieldDo.getFieldFullName());
                            fd.setFieldType(fieldDo.getFieldType());
                            fd.setNorder(fieldDo.getNorder());
                            delFieldList.remove(fd);
                            break;
                        }
                    }
                    if (!bfield) {
                        addFieldList.add(fieldDo);
                    }
                }
                TOlkObjectDo objTmp = new TOlkObjectDo();
                objTmp.setId(objectDo.getId());
                objTmp.setSynFlag(0);
                objectService.updateNoNull(objTmp);
                fieldService.saveOneObject(addFieldList, modFieldList, delFieldList);
            }
        } catch (Exception e) {
            logger.error("刷新节点的" + dbInfo.getDcDbName() + "元数据数据失败", e);
        }
        redisTemplate.delete(redKey);

    }

    @ApiOperation(value = "修改olk数据目录", notes = "修改olk数据目录")
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public Object update(@RequestBody UpdateDatabaseRequest request) {
        ResponeMap resMap = this.genResponeMap();

        TOlkDatabaseDo info = databaseService.findById(request.getId());
        Preconditions.checkArgument(info != null, "目录不存在");
        Preconditions.checkArgument(StringUtils.isNotBlank(request.getDbChnName()), "中文名称不能为空,只能为2-30个字符,字符可为中文大小写字母数字");

        Matcher matcher = DB_CHNAME_PATTERN.matcher(info.getDbChnName());
        Preconditions.checkArgument(matcher.find(), "中文名称只能为2-30个字符,字符可为中文大小写字母数字");
        info.setDbChnName(request.getDbChnName());
        databaseService.updateBean(info);
        resMap.setSingleOk(info, "保存成功");

        return resMap.getResultMap();
    }

    @ApiOperation(value = "批量启用或禁用olk数据目录", notes = "批量启用或禁用olk数据目录")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "目录id", dataType = "String", required = true, paramType = "query"),
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
            Example exp = new Example(TOlkDatabaseDo.class);
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn("id", split);

            if (enable == 0) {
                actType = "禁用";
            } else {
                actType = "启用";
            }

            String act = "修改-" + actType + "olk目录" + System.currentTimeMillis();
            if (enable == 1) {
                criteria.andCondition(" (enable =0  or enable is null )");
            } else {
                criteria.andEqualTo("enable", 1);
            }
            List<TOlkDatabaseDo> dbList = databaseService.findByExample(exp);
            if (dbList.size() != split.size()) {
                return resMap.setErr(" 数据已变化不能操作").getResultMap();
            }
            List<String> userIdList = dbList.stream().map(x -> x.getUserId()).distinct()
                .collect(Collectors.toList());
            if (userIdList.size() != 1) {
                return resMap.setErr("不能同时操作不同用户数据").getResultMap();
            }
            String userId = userIdList.get(0);

            if (enable == 0) {
                String ids = "'" + StringUtils.join(split, "','") + "'";
                exp = new Example(TOlkModelObjectDo.class);
                criteria = exp.createCriteria();
                criteria.andCondition(
                    " real_obj_id in (select a.id from t_olk_object a, t_olk_database b where a.db_id =b.id  and b.id in( "
                        + ids + ") )");
                criteria.andEqualTo("userId", userId);
                int cnt = modelObjService.findCountByExample(exp);
                if (cnt > 0) {
                    return resMap.setErr("数据被使用，不能" + actType).getResultMap();
                }
            }

            List<TOlkDatabaseDo> oldList = new ArrayList<>();
            if (enable == 1) {
                for (TOlkDatabaseDo info : dbList) {
                    TOlkDatabaseDo old = new TOlkDatabaseDo();
                    MyBeanUtils.copyBeanNotNull2Bean(info, old);
                    oldList.add(old);
                    info.setEnable(1);
                    info.setSynFlag(0);
                }
            } else {
                for (TOlkDatabaseDo info : dbList) {
                    TOlkDatabaseDo old = new TOlkDatabaseDo();
                    MyBeanUtils.copyBeanNotNull2Bean(info, old);
                    oldList.add(old);
                    info.setEnable(0);
                    info.setSynFlag(0);
                }
            }

            databaseService.updateBeanWithFlag(dbList);
            if (enable == 0) {
                resMap.setOk(actType + "成功");
            } else {
                resMap.setOk(actType + "启用成功");
            }

        } catch (Exception ex) {
            resMap.setErr(actType + "失败");
            logger.error(actType + "异常:", ex);
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "olk数据目录内容", notes = "olk数据目录内容")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "olk数据目录 id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    @ResponseBody
    public Object info(String id) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if (StringUtils.isBlank(id)) {
                return resMap.setErr("id不能为空").getResultMap();
            }
            TOlkDatabaseDo modelVo = databaseService.findById(id);
            resMap.setSingleOk(modelVo, "成功");

        } catch (Exception ex) {
            resMap.setErr("查询失败");
            logger.error("查询异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除olk数据目录", notes = "删除olk数据目录")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "olk数据目录 id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public Object delete(String id) {
        ResponeMap resMap = this.genResponeMap();
        Preconditions.checkArgument(StringUtils.isNotEmpty(id), "id不能为空");
        UserDo user = LoginUtil.getUser();

        List<String> idList = Arrays.asList(id.split("(,|\\s)+")).stream()
            .filter(StringUtils::isNoneBlank).distinct().collect(Collectors.toList());
        Preconditions.checkArgument(idList.size() == 1, "只能删除1个目录");

        TOlkDatabaseDo info = databaseService.findById(idList.get(0));
        Preconditions.checkArgument(info != null, "数据目录不存在");

        Example exp = new Example(TOlkDatabaseDo.class);
        Example.Criteria criteria = exp.createCriteria();
        criteria.andCondition(
            " real_obj_id in (select a.id from t_olk_object a, t_olk_database b where a.db_id =b.id  and b.id in( '"
                + info.getId() + "') )");
        int cnt = modelObjService.findCountByExample(exp);
        Preconditions.checkArgument(cnt <= 0, "数据被使用，不能删除");

        if (Constants.dchetu.equals(info.getDbsourceId())) {
            HetuInfo hetuInfo = hetuJdbcOperateComponent.genHetuInfo();
            if (HetuDynamicCatalogUtil.checkCatalogExist(hetuInfo, info.getDcDbName())) {
                DynamicCatalogResult dynamicCatalogResult = HetuDynamicCatalogUtil
                    .deleteCatalog(hetuInfo, info.getDcDbName());
                Preconditions.checkArgument(dynamicCatalogResult.isSuccessful(), "删除服务端目录失败" + dynamicCatalogResult.getMessage());
            }
        }
        databaseService.deleteWithOther(info);
        resMap.setOk("删除成功");
        return resMap.getResultMap();
    }

    @ApiOperation(value = "数据目录分页", notes = "数据目录分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "qryCond", value = "模糊条件", dataType = "String", required = false, paramType = "query"),
        @ApiImplicitParam(name = "catalogType", value = "目录分组ID", dataType = "String", example = "", required = false, paramType = "query"),
        @ApiImplicitParam(name = "currentPage", value = "页数", dataType = "Integer", required = false, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", required = false, paramType = "query"),
        @ApiImplicitParam(name = "dbsourceId", value = "根节点标识, 默认dchetu", dataType = "String", required = false, paramType = "query")
    })
    @RequestMapping(value = "/page", method = {RequestMethod.GET})
    @ResponseBody
    public Object page(QueryDataRequest request) {
        UserDo user = LoginUtil.getUser();
        TOlkDatabaseDo modelVo = new TOlkDatabaseDo();
        MyBeanUtils.copyBean2Bean(modelVo, request);
        ResponeMap resMap = this.genResponeMap();
        try {
            modelVo.setQryCond(ComUtil.chgLikeStr(modelVo.getQryCond()));
            modelVo.setDbName(ComUtil.chgLikeStr(modelVo.getDbName()));

            long findCnt = databaseService.findBeanCnt(modelVo);
            modelVo.genPage(findCnt);

            List<TOlkDatabaseDo> list = databaseService.findBeanList(modelVo);
            resMap.setPageInfo(modelVo.getPageSize(), modelVo.getCurrentPage());
            resMap.setOk(findCnt, list, "获取数据目录列表成功");
        } catch (Exception ex) {
            resMap.setErr("获取数据目录列表失败");
            logger.error("获取数据目录列表失败:", ex);
        }
        return resMap.getResultMap();
    }

    private List<Object> catalogTypeSubTreeNode(String typeId, final List<TOlkCatalogTypeDo> typeList, final List<TOlkDatabaseDo> dbList,
        final List<TOlkSchemaDo> schemaList, final List<TOlkObjectDo> tabList) {

        List<Object> list = new ArrayList<>();
        int norder = 10;
        if (StringUtils.isBlank(typeId)) {
            List<Object> subList2 = catalogSchemTreeNode("", dbList, schemaList, tabList);
            list.addAll(subList2);
        }
        List<TOlkCatalogTypeDo> tmpList = typeList.stream()
            .filter(x -> ComUtil.trsEmpty(typeId).equals(ComUtil.trsEmpty(x.getPid())))
            .collect(Collectors.toList());
        for (TOlkCatalogTypeDo ctDo : tmpList) {
            HashMap<String, Object> node1 = new HashMap<>();
            node1.put("type", "catalog");
            node1.put("dataType", "catalog");
            node1.put("id", "catalogtype" + connectChar + ctDo.getId());
            node1.put("relId", ctDo.getId());
            node1.put("name", ComUtil.trsEmpty(ctDo.getTypeName()));
            node1.put("tips", ComUtil.trsEmpty(ctDo.getTypeName()));
            node1.put("hasLeaf", true);
            node1.put("norder", norder++);
            node1.put("catalogType", ctDo.getId());
            List<Object> subList1 = catalogSchemTreeNode(ctDo.getId(), dbList, schemaList, tabList);
            List<Object> subList2 = catalogTypeSubTreeNode(ctDo.getId(), typeList, dbList, schemaList, tabList);
            subList1.addAll(subList2);
            node1.put("children", subList1);
            list.add(node1);
        }
        return list;
    }

    private List<Object> catalogSchemTreeNode(String typeId, final List<TOlkDatabaseDo> dbList, final List<TOlkSchemaDo> schemaList,
        final List<TOlkObjectDo> tabList) {

        List<Object> list = new ArrayList<>();
        int norder = 10;
        List<TOlkDatabaseDo> tmpList = dbList.stream()
            .filter(x -> {
                if (StringUtils.isBlank(typeId)) {
                    return x.getCatalogType() == null;
                } else {
                    return ComUtil.trsEmpty(typeId).equals(ComUtil.trsEmpty(x.getCatalogType()));
                }
            })
            .collect(Collectors.toList());
        for (TOlkDatabaseDo db : tmpList) {
            HashMap<String, Object> node1 = new HashMap<>();
            node1.put("dataType", "db");
            node1.put("type", "schema");
            node1.put("dbsourceId", db.getDbsourceId());
            node1.put("dbType", ComUtil.trsEmpty(db.getDbType()).toUpperCase());
            node1.put("id", "catalog" + connectChar + db.getId());
            node1.put("relId", db.getId());
            node1.put("name", db.getDcDbName());
            node1.put("tips",
                StringUtils.isNotBlank(db.getDbChnName()) ? db.getDbChnName() : db.getDcDbName());
            node1.put("dbName", db.getDbName());
            node1.put("dbId", db.getId());
            node1.put("catalogType", typeId);
            node1.put("hasLeaf", true);
            node1.put("norder", norder++);
            int norder2 = 10;
            List<Object> scSubList = new ArrayList<>();
            for (TOlkSchemaDo schemaDo : schemaList) {
                if (db.getId().equals(schemaDo.getDbId())) {
                    HashMap<String, Object> node2 = new HashMap<>();
                    node2.put("dataType", "schema");
                    node2.put("type", "table");
                    node2.put("id", "schema" + connectChar + schemaDo.getId());
                    node2.put("relId", schemaDo.getId());
                    node2.put("name", schemaDo.getSchemaName());
                    node2.put("tips", StringUtils.isNotBlank(schemaDo.getSchemaChnName()) ? schemaDo
                        .getSchemaChnName() : schemaDo.getSchemaName());
                    node2.put("schemaName", schemaDo.getSchemaName());
                    node2.put("schemaId", schemaDo.getId());
                    node2.put("dbName", db.getDbName());
                    node2.put("dbId", db.getId());
                    node2.put("hasLeaf", true);
                    node2.put("norder", norder2++);

                    List<Object> ttSubList = new ArrayList<>();
                    for (TOlkObjectDo objectDo : tabList) {
                        if (schemaDo.getId().equals(objectDo.getSchemaId())) {
                            HashMap<String, Object> node3 = new HashMap<>();
                            node3.put("dataType", "table");
                            node3.put("type", "field");
                            node3.put("id", "table" + connectChar + objectDo.getId());
                            node3.put("relId", objectDo.getId());
                            node3.put("name", objectDo.getObjectName());
                            node3.put("tips",
                                StringUtils.isNotBlank(objectDo.getObjChnName()) ? objectDo
                                    .getObjChnName() : objectDo.getObjectName());
                            node3.put("schemaName", objectDo.getObjectName());
                            node3.put("schemaId", schemaDo.getId());
                            node3.put("dbName", db.getDbName());
                            node3.put("dbId", db.getId());
                            node3.put("hasLeaf", false);
                            node3.put("norder", norder2++);

                            ttSubList.add(node3);
                        }
                    }
                    node2.put("children", ttSubList);
                    scSubList.add(node2);
                }
            }
            node1.put("children", scSubList);
            list.add(node1);
        }
        return list;
    }

    @ApiOperation(value = "节点元数据树", notes = "节点元数据树")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "dbsourceId", value = "数据源类型", dataType = "String", required = false, paramType = "query")
    })
    @RequestMapping(value = "/dcmetatree", method = {RequestMethod.GET})
    @ResponseBody
    public Object dcMetaTree(@RequestParam("dbsourceId") String dbsourceId) {
        ResponeMap resMap = this.genResponeMap();
        try {
            List<Object> dataList = new ArrayList<>();

            TOlkCatalogTypeDo typeTmp = new TOlkCatalogTypeDo();
            List<TOlkCatalogTypeDo> typeList = catalogTypeService.findBeanList(typeTmp);

            TOlkDatabaseDo dbTmp = new TOlkDatabaseDo();
            dbTmp.setDbsourceId(dbsourceId);
            List<TOlkDatabaseDo> dbList = databaseService.findBeanList(dbTmp);


            Example exp = new Example(TOlkSchemaDo.class);
            exp.orderBy("schemaName");
            List<TOlkSchemaDo> schemaList = schemaService.findByExample(exp);

            exp = new Example(TOlkObjectDo.class);
            exp.orderBy("objectName");
            List<TOlkObjectDo> tabList = objectService.findByExample(exp);

            HashMap<String, Object> node = new HashMap<>();
            node.put("type", "root");
            node.put("id", DATASOURCE.getId());
            node.put("relId", "root");
            node.put("value", "root");
            node.put("name", DATASOURCE.getName());
            node.put("title", DATASOURCE.getTitle());
            node.put("itemId", null);
            node.put("pid", null);
            node.put("catalogType", "");
            node.put("dataType", "nodeParty");
            List<Object> s1List = catalogTypeSubTreeNode("", typeList, dbList, schemaList, tabList);
            node.put("children", s1List);
            dataList.add(node);
            resMap.setSingleOk(dataList, "获取树结构成功");
        } catch (Exception ex) {
            resMap.setErr("获取树结构失败");
            resMap.setDebugeInfo(ComUtil.getErrorInfoFromException(ex));
            logger.error("获取树结构异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "刷新模式或表信息", notes = "刷新模式或表信息")
    @RequestMapping(value = "/refreshschematable", method = {RequestMethod.POST})
    public Object refreshSchemaTable(@RequestBody RefreshSchemaRequest request) {
        ResponeMap resMap = this.genResponeMap();

        UserDo user = LoginUtil.getUser();
        String dbId = request.getDbId();
        String schemaId = request.getSchemaId();
        String objectId = request.getObjectId();

        TOlkDatabaseDo olkDb = null;
        TOlkSchemaDo olkSchema = null;
        TOlkObjectDo olkObject = null;

        if (StringUtils.isNotBlank(objectId)) {
            olkObject = objectService.findById(objectId);
            Preconditions.checkArgument(olkObject != null, "指定对象不存在");
            olkDb = databaseService.findById(olkObject.getDbId());
            olkSchema = schemaService.findById(olkObject.getSchemaId());
            logger.debug("从对象{}.{}.{}刷新", olkDb.getDcDbName(),
                olkSchema.getSchemaName(), olkObject.getObjectName());
        } else if (StringUtils.isNotBlank(schemaId)) {
            olkSchema = schemaService.findById(schemaId);
            Preconditions.checkArgument(olkSchema != null, "指定模式不存在");
            olkDb = databaseService.findById(olkSchema.getDbId());
            logger.debug("从模式{}.{}刷新", olkDb.getDcDbName(),
                olkSchema.getSchemaName());
        } else if (StringUtils.isNotBlank(dbId)) {
            olkDb = databaseService.findById(dbId);
            Preconditions.checkArgument(olkDb != null, "指定数据目录不存在");
            logger.debug("从目录{}刷新", olkDb.getDcDbName());
        } else {
            return resMap.setErr("数据目录、模式id与对象id不能同时为空").getResultMap();
        }

        String redKey = olkRef + olkDb.getId();
        boolean bset = redisTemplate.opsForValue().setIfAbsent(redKey, "1", 3, TimeUnit.MINUTES);
        Preconditions.checkArgument(bset, olkDb.getDcDbName() + " 数据正在处理");

        final TOlkDatabaseDo dbInfo = olkDb;
        final TOlkSchemaDo schemaInfo = olkSchema;
        final TOlkObjectDo objectInfo = olkObject;
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadOlkCatalogMeta(dbInfo, schemaInfo, objectInfo, redKey, user);
            }
        }, "处理元数据").start();

        resMap.setOk("数据开始处理");
        return resMap.getResultMap();
    }
}
