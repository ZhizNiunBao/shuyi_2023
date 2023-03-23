package cn.bywin.business.controller.olk;


import cn.bywin.business.bean.olk.TOlkUdfDo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.service.olk.OlkUdfService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(value = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        maxAge = 3600)
@RestController
@Api(tags = "olk-UDF管理-olkudf" )
@RequestMapping("/olkudf")
public class OlkUdfController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OlkUdfService udfService;

    @ApiOperation(value = "新增bydbUDF", notes = "新增bydbUDF")
    @ApiImplicitParams({
           // @ApiImplicitParam(name = "info", value = "bydbUDF", dataType = "TTruUdfDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    public Object add(@RequestBody TOlkUdfDo info, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null || StringUtils.isBlank(ud.getUserId())) {
                return resMap.setErr("请先登录").getResultMap();
            }
//            if (StringUtils.isBlank(info.getDbName())) {
//                return resMap.setErr("名称不能为空").getResultMap();
//            }

            info.setId(UUID.randomUUID().toString().replaceAll("-",""));
            LoginUtil.setBeanInsertUserInfo( info,ud );

            udfService.insertBean(info);

//            HashMap<String,Object> data = new HashMap<>();
//            data.put("type","element");
//            data.put("id","element"+ connectChar + info.getId());
//            data.put("folderId",info.getFolderId());
//            data.put("name",info.getName());
//            data.put("icon",info.getTypeIconUrl());
//            data.put("realId",info.getId());
//            //data.put("stype", stype);
//            data.put("catalogCode", info.getCatalogCode());
//            data.put("busiId",info.getBusiId());
//            data.put("hasLeaf",false);
//
//            resMap.put("treeNode", data );

            resMap.setSingleOk(info, "保存成功");

        } catch (Exception ex) {
            ex.printStackTrace();
            resMap.setErr("保存失败");
            logger.error("保存异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "修改bydbUDF", notes = "修改bydbUDF")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelVo", value = "bydbUDF", dataType = "TOlkUdfDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null || StringUtils.isBlank(ud.getUserId())) {
                return resMap.setErr("请先登录").getResultMap();
            }
            //@RequestBody TOlkUdfDo modelVo,
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            logger.debug( "{}",hru.getAllParaData() );
            TOlkUdfDo info = udfService.findById(hru.getNvlPara("id"));

            if (info == null)
            {
                return resMap.setErr("内容不存在").getResultMap();
            }

            TOlkUdfDo oldData = new TOlkUdfDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, oldData );

            new PageBeanWrapper(info,hru);



//            if (StringUtils.isBlank(info.getDbName())) {
//                return resMap.setErr("名称不能为空").getResultMap();
//            }

            udfService.updateBean(info);

            resMap.setSingleOk(info, "保存成功");

        } catch (Exception ex) {
            ex.printStackTrace();
            resMap.setErr("保存失败");
            logger.error("保存异常:", ex);
        }
        return resMap.getResultMap();
    }



    @ApiOperation(value = "bydbUDF内容", notes = "bydbUDF内容")
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
            TOlkUdfDo modelVo = udfService.findById(id);
            resMap.setSingleOk(modelVo, "成功");

        } catch (Exception ex) {
            ex.printStackTrace();
            resMap.setErr("查询失败");
            logger.error("查询异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除bydbUDF", notes = "删除bydbUDF")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "bydbUDF id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public Object delete(String id) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if (StringUtils.isBlank(id)) {
                return resMap.setErr("id不能为空").getResultMap();
            }
            udfService.deleteById(id);
            resMap.setOk("删除成功");

        } catch (Exception ex) {
            ex.printStackTrace();
            resMap.setErr("删除失败");
            logger.error("删除异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取UDF列表", notes = "获取UDF列表")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "currentPage", value = "当前页", dataType = "String", required = true, paramType = "query"),
            //@ApiImplicitParam(name = "pageSize", value = "页数", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "modelVo", value = "UDF信息", dataType = "TOlkUdfDo", required = false, paramType = "query")
    })
    @RequestMapping(value = "/page", method = {RequestMethod.GET})
    @ResponseBody
    public Object page(TOlkUdfDo modelVo) {
        ResponeMap resMap = this.genResponeMap();
        try {
            modelVo.setQryCond( ComUtil.chgLikeStr(modelVo.getQryCond()));
            //modelVo.setDbName(ComUtil.chgLikeStr(modelVo.getDbName()));

            List<TOlkUdfDo> list = udfService.findBeanList(modelVo);
            long findCnt = udfService.findBeanCnt(modelVo);
            resMap.setPageInfo(modelVo.getPageSize(), modelVo.getCurrentPage());
            resMap.setOk(findCnt, list, "获取UDF列表成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            resMap.setErr("获取UDF列表");
            logger.error("获取UDF列表失败:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取UDF选择列表", notes = "获取UDF选择列表")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/option", method = {RequestMethod.GET})
    @ResponseBody
    public Object option( String belongType ,String datasetId,String databaseId,String datasourceId,String qryCond ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            /*String dsId = datasourceId;
            if( StringUtils.isNotBlank( databaseId ) ){
                TBydbDatabaseDo dbDo = databaseService.findById(databaseId);
                if( dbDo == null ){
                    return resMap.setErr("数据目录不存在").getResultMap();
                }
                dsId = dbDo.getDbsourceId();
            }
            else if( StringUtils.isNotBlank( datasetId ) ){
                TBydbDatasetDo datasetDo = datasetService.findById(datasetId);
                if( datasetDo == null ){
                    return resMap.setErr("数据集不存在").getResultMap();
                }
                dsId = datasetDo.getDatasourceId();
            }
            if( StringUtils.isBlank( dsId ) ){
                return resMap.setErr("数据源未指定").getResultMap();
            }
            if( Constants.dchetu.equals( dsId) )
            {
                dsId = "olk";
            }
            else {
                //TBydbDbSourceDo dbSourceDo = dbSourceService.findById(dsId);
                //dsId = dbSourceDo.getDsType();
            }*/

            TOlkUdfDo modelVo =new TOlkUdfDo();
            if(StringUtils.isNotBlank( qryCond )) {
                modelVo.setQryCond(String.format( "%%%s%%" ,qryCond));
            }
            if( StringUtils.isNotBlank( belongType ))
                modelVo.setBelongType( belongType );
            modelVo.setEnable( 1 );
            List<TOlkUdfDo> list = udfService.findBeanList(modelVo);
            resMap.setSingleOk(list, "获取UDF选择列表成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            resMap.setErr("获取UDF选择列表失败");
            logger.error("获取UDF选择列表失败:", ex);
        }
        return resMap.getResultMap();
    }

}
