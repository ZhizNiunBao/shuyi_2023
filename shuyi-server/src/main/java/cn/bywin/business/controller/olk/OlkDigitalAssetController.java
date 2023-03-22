package cn.bywin.business.controller.olk;

import cn.bywin.business.bean.olk.TOlkDatabaseDo;
import cn.bywin.business.bean.olk.TOlkFieldDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.request.analysis.QueryTableRequest;
import cn.bywin.business.bean.view.bydb.DigitalAssetVo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.service.olk.OlkDatabaseService;
import cn.bywin.business.service.olk.OlkDigitalAssetService;
import cn.bywin.business.service.olk.OlkFieldService;
import cn.bywin.business.service.olk.OlkObjectService;
import cn.bywin.business.util.JdbcTypeToJavaTypeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(value = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        maxAge = 3600)
@RestController
@Api(tags = "olkdigit-数字地图-olkdigitalasset")
@RequestMapping("/olkdigitalasset")
public class OlkDigitalAssetController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OlkDatabaseService databaseService;

    @Autowired
    private OlkDigitalAssetService olkDigitalAssetService;

    @Autowired
    private OlkObjectService bydbObjectService;

    @Autowired
    private OlkFieldService fieldService;

    @ApiOperation(value = "olk数字地图内容", notes = "olk数字地图内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "query")
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


    @ApiOperation(value = "获取数字地图字段列表", notes = "获取数字地图字段列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/tabfieldlist", method = {RequestMethod.GET})
    @ResponseBody
    public Object tabFieldList(String id) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if (StringUtils.isBlank(id)) {
                return resMap.setErr("id不能为空").getResultMap();
            }
            //if(id.startsWith("db")){
            //String id1 = id.substring(2);
            TOlkObjectDo objectDo = bydbObjectService.findById(id);
            if( objectDo != null){
                TOlkFieldDo tmp = new TOlkFieldDo();
                tmp.setObjectId( objectDo.getId() );
                tmp.setEnable( 1 );
                List<TOlkFieldDo> beanList = fieldService.findBeanList(tmp);
                List<Object> list = beanList.stream().map(x -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", x.getId());
                    map.put("columnName", x.getFieldName());
                    //map.put("chgStatement", x.getChgStatement());
                    map.put("chgStatement", "");
                    map.put("orgType", x.getFieldType());
                    map.put("columnType", JdbcTypeToJavaTypeUtil.chgType( x.getFieldType() ) );
                    //map.put("columnType",  x.getFieldType() );
                    map.put("chnName", x.getChnName());
                    map.put("tips", StringUtils.isBlank( x.getChnName())? x.getChnName():x.getFieldName());
                    return map;
                }).collect( Collectors.toList());
                resMap.setSingleOk(list, "获取数字地图字段列表成功");
            }
            else{
                resMap.setErr("获取数字地图字段列表失败,无效id");
            }

        } catch (Exception ex) {
            resMap.setErr("获取数字地图字段列表失败");
            logger.error("获取数字地图字段列表异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "olk数据地图列表", notes = "olk数据地图列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qryCond", value = "模糊条件", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dataType", value = "数据类型, 全部pub, 收藏 favorite", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "deptNo", value = "部门编号", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "ssj", value = "时间条件 day week month year", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "currentPage", value = "页数", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", required = false, paramType = "query")
    })
    @RequestMapping(value = "/olksearchlist", method = {RequestMethod.GET})
    @ResponseBody
    public Object olkSearchList(QueryTableRequest request) {
        ResponeMap resMap = genResponeMap();

        DigitalAssetVo modelVo = new DigitalAssetVo();
        MyBeanUtils.copyBean2Bean(modelVo, request);
        modelVo.setQryCond(ComUtil.chgLikeStr(modelVo.getQryCond()));
        modelVo.setScatalog("db");

        long findCnt = olkDigitalAssetService.findBeanCnt(modelVo);
        modelVo.genPage(findCnt);

        List<DigitalAssetVo> list = olkDigitalAssetService.findBeanList(modelVo);
        for (DigitalAssetVo digitalAssetVo : list) {
            if( StringUtils.isBlank( digitalAssetVo.getObjChnName() )){
                digitalAssetVo.setObjChnName( digitalAssetVo.getObjectName());
            }
        }

        resMap.setPageInfo(modelVo.getPageSize(), modelVo.getCurrentPage());
        resMap.setOk(findCnt, list, "获取数字地图列表成功");
        return resMap.getResultMap();
    }
}

