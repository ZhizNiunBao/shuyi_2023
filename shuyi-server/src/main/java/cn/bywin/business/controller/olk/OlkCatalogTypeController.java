package cn.bywin.business.controller.olk;

import cn.bywin.business.bean.olk.TOlkCatalogTypeDo;
import cn.bywin.business.bean.olk.TOlkDatabaseDo;
import cn.bywin.business.bean.olk.TOlkDcServerDo;
import cn.bywin.business.bean.request.analysis.AddCatalogRequest;
import cn.bywin.business.bean.request.analysis.UpdateCatalogRequest;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.service.olk.OlkCatalogTypeService;
import cn.bywin.business.service.olk.OlkDatabaseService;
import cn.bywin.business.service.olk.OlkDcServerService;
import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
@Api(tags = "olk-数据联邦-目录分组-olkcatalogtype")
@RequestMapping("/olkcatalogtype")
public class OlkCatalogTypeController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OlkDatabaseService databaseService;

    @Autowired
    private OlkCatalogTypeService typeService;

    @ApiOperation(value = "新增olk目录分组", notes = "新增olk目录分组")
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public Object add(@RequestBody AddCatalogRequest request) {
        ResponeMap resMap = this.genResponeMap();

        Preconditions.checkArgument(StringUtils.isNotBlank(request.getTypeName()), "名称不能为空");
        if (StringUtils.isNotBlank(request.getPid())) {
            TOlkCatalogTypeDo parent = typeService.findById(request.getPid());
            Preconditions.checkArgument(parent != null, "上级分组不存在");
        } else {
            request.setPid(null);
        }

        TOlkCatalogTypeDo catalogInfo = new TOlkCatalogTypeDo();
        catalogInfo.setId(ComUtil.genId());
        MyBeanUtils.copyBeanNotNull2Bean(request, catalogInfo);
        long sameNameCount = typeService.findSameNameCount(catalogInfo);
        Preconditions.checkArgument(sameNameCount <= 0, "名称已使用");

        UserDo user = LoginUtil.getUser();
        LoginUtil.setBeanInsertUserInfo(catalogInfo, user);
        catalogInfo.setUserAccount(user.getUserName());
        catalogInfo.setUserAccountName(user.getChnName());
        catalogInfo.setUserDeptNa(user.getOrgNo());
        catalogInfo.setUserDeptNa(user.getOrgName());

        typeService.insertBean(catalogInfo);
        resMap.setSingleOk(catalogInfo, "保存成功");
        return resMap.getResultMap();
    }

    @ApiOperation(value = "修改olk目录分组", notes = "修改olk目录分组")
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public Object update(@RequestBody UpdateCatalogRequest request) {
        ResponeMap resMap = this.genResponeMap();

        Preconditions.checkArgument(StringUtils.isNotBlank(request.getId()), "id不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(request.getTypeName()), "名称不能为空");

        TOlkCatalogTypeDo info = typeService.findById(request.getId());
        Preconditions.checkArgument(info != null, "分组不存在");

        MyBeanUtils.copyBeanNotNull2Bean(request, info);
        final long sameNameCount = typeService.findSameNameCount(info);
        Preconditions.checkArgument(sameNameCount <= 0, "名称已使用");

        typeService.updateBean(info);
        resMap.setSingleOk(info, "保存成功");
        return resMap.getResultMap();
    }

    @ApiOperation(value = "olk目录分组内容", notes = "olk目录分组内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "olk目录分组 id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    @ResponseBody
    public Object info(String id) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if (StringUtils.isBlank(id)) {
                return resMap.setErr("id不能为空").getResultMap();
            }
            TOlkCatalogTypeDo modelVo = typeService.findById(id);
            resMap.setSingleOk(modelVo, "成功");

        } catch (Exception ex) {
            resMap.setErr("查询失败");
            logger.error("查询异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除olk目录分组", notes = "删除olk目录分组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "olk目录分组 id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public Object delete(String id) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if (StringUtils.isBlank(id)) {
                return resMap.setErr("id不能为空").getResultMap();
            }
            if (!id.matches("^[a-zA-Z0-9\\-_,]*$")) {
                return resMap.setErr("id有非法字符").getResultMap();
            }
            List<String> split = Arrays.asList(id.split(","));

            Example exp = new Example(TOlkDatabaseDo.class);
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn("catalogType", split);
            int cnt = databaseService.findCountByExample(exp);
            if( cnt >0 ){
                return resMap.setErr("目录分组已被使用不能删除").getResultMap();
            }

            exp = new Example(TOlkCatalogTypeDo.class);
            criteria = exp.createCriteria();
            criteria.andIn("pid", split);
            criteria.andNotIn( "id",split );
            cnt = typeService.findCountByExample(exp);
            if( cnt >0 ){
                return resMap.setErr("目录分组下级不能删除").getResultMap();
            }

            exp = new Example(TOlkCatalogTypeDo.class);
            criteria = exp.createCriteria();
            criteria.andIn("id", split);
            List<TOlkCatalogTypeDo> list = typeService.findByExample(exp);
            List<String> collect = list.stream().map(x -> x.getId()).collect(Collectors.toList());
            if( collect.size() != split.size() ){
                return resMap.setErr("有目录分组不存在").getResultMap();
            }
            typeService.deleteByIds(collect);
            resMap.setOk("删除成功");

        } catch (Exception ex) {
            resMap.setErr("删除失败");
            logger.error("删除异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取目录分组列表", notes = "获取目录分组列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qryCond", value = "模糊条件", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "typeName", value = "名称", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "currentPage", value = "页数", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", required = false, paramType = "query")
    })
    @RequestMapping(value = "/page", method = {RequestMethod.GET})
    @ResponseBody
    public Object page(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            //if( modelVo == null )

            TOlkCatalogTypeDo modelVo = new TOlkCatalogTypeDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo,hru);
            modelVo.setQryCond(ComUtil.chgLikeStr(modelVo.getQryCond()));
            modelVo.setTypeName(ComUtil.chgLikeStr(modelVo.getTypeName()));

            logger.debug( "id:{},info:{}",modelVo.getId(),modelVo);

            long findCnt = typeService.findBeanCnt(modelVo);
            //modelVo.genPage();
            modelVo.genPage(findCnt);

            List<TOlkCatalogTypeDo> list = typeService.findBeanList(modelVo);

            resMap.setPageInfo(modelVo.getPageSize(), modelVo.getCurrentPage());
            resMap.setOk(findCnt, list, "获取目录分组列表成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            resMap.setErr("获取目录分组列表失败");
            logger.error("获取目录分组列表失败:", ex);
        }
        return resMap.getResultMap();
    }
}
