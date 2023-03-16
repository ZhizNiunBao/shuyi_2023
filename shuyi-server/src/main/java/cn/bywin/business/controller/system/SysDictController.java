package cn.bywin.business.controller.system;


import cn.bywin.business.bean.system.SysDictDo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.service.system.SysDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(value = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        maxAge = 3600)
@RestController
@RequestMapping({"/sysdict"})

@Api(tags = "sys-字典管理")
public class SysDictController extends BaseController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SysDictService commServ;

    @ApiOperation(value = "新增字典类型", notes = "新增字典类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "info", value = "字典", dataType = "SysDictDo", required = true, paramType = "body")
    })
    @ResponseBody
    @RequestMapping(value = {"/addtop"}, method = {RequestMethod.POST})
    public Object addTop(HttpServletRequest request) {
        SysDictDo info = new SysDictDo();
        ResponeMap resMap = this.genResponeMap();

        try {
            UserDo userDo = LoginUtil.getUser(request);
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
            logger.debug( "{}",hru.getAllParaData() );
            new PageBeanWrapper( info,hru,"");
            info.setId("#NULL#");
            info.setTopId(null);
            info.setPid(null);

            if (StringUtils.isBlank(info.getDictCode())) {
                return resMap.setErr("编码不能为空").getResultMap();
            }
            if (commServ.checkCode(info) > 0) {
                return resMap.setErr("编码已存在").getResultMap();
            }
            if (StringUtils.isBlank(info.getDictName())) {
                return resMap.setErr("名称不能为空").getResultMap();
            }
            if (commServ.checkName(info) > 0) {
                return resMap.setErr("名称已存在").getResultMap();
            }

            info.setId(UUID.randomUUID().toString().replaceAll("-", ""));

            commServ.insertBean(info);
            resMap.setSingleOk(info, "新增成功");
            //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).addLog(userDo, info, "新增-字典类型");

        } catch (Exception ex) {
            resMap.setErr("新增失败");
            logger.error("新增异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "新增字典", notes = "新增字典")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "info", value = "工单管理", dataType = "SysDictDo", required = true, paramType = "body",dataTypeClass = SysDictDo.class)
    })
    @ResponseBody
    @RequestMapping(value = {"/addsub"}, method = {RequestMethod.POST})
    public Object addSub(HttpServletRequest request) {
        SysDictDo info = new SysDictDo();
        ResponeMap resMap = this.genResponeMap();

        try {
            UserDo userDo = LoginUtil.getUser(request);
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
            logger.debug( "{}",hru.getAllParaData() );
            new PageBeanWrapper( info,hru,"");
            info.setId("#NULL#");
            if (StringUtils.isBlank(info.getPid())) {
                return resMap.setErr("上级不能为空").getResultMap();
            }
            if (StringUtils.isBlank(info.getDictCode())) {
                return resMap.setErr("编码不能为空").getResultMap();
            }
            if (commServ.checkCode(info) > 0) {
                return resMap.setErr("编码已存在").getResultMap();
            }
            if (StringUtils.isBlank(info.getDictName())) {
                return resMap.setErr("名称不能为空").getResultMap();
            }
            if (commServ.checkName(info) > 0) {
                return resMap.setErr("名称已存在").getResultMap();
            }
            if( StringUtils.isBlank( info.getDisplay() ) ){
                info.setDisplay( "1" );
            }
            final SysDictDo pdict = commServ.findById(info.getPid());
            if (pdict.getPid() == null) {
                info.setTopId(pdict.getId());
                info.setTopCode(pdict.getDictCode());
            } else {
                info.setTopId(pdict.getTopId());
                //final SysDictDo topDict = commServ.findById(pdict.getTopId());
                info.setTopCode(pdict.getTopCode());
            }

            info.setId(UUID.randomUUID().toString().replaceAll("-", ""));

            commServ.insertBean(info);
            resMap.setSingleOk(info, "新增成功");
            //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).addLog(userDo, info, "新增-字典");
        } catch (Exception ex) {
            resMap.setErr("新增失败");
            logger.error("新增异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "修改字典类型", notes = "修改字典类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "info", value = "字典", dataType = "SysDictDo", required = true, paramType = "body")
    })
    @ResponseBody
    @RequestMapping(value = {"/updatetop"}, method = {RequestMethod.POST})
    public Object updateTop(HttpServletRequest request) {

        ResponeMap resMap = this.genResponeMap();

        try {
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
            logger.debug("{}", hru.getAllParaData());

            SysDictDo info = new SysDictDo();
            SysDictDo old = new SysDictDo();
            UserDo userDo = LoginUtil.getUser(request);
            new PageBeanWrapper( info,hru,"");
            info = commServ.findById(info.getId());
            if( StringUtils.isNotBlank( info.getPid() ) ){
                return resMap.setErr("只能修改字典类型").getResultMap();
            }

            MyBeanUtils.copyBeanNotNull2Bean(info, old);
            new PageBeanWrapper( info,hru,"");

            info.setPid( null );
            if (StringUtils.isBlank(info.getDictCode())) {
                return resMap.setErr("编码不能为空").getResultMap();
            }
            if (commServ.checkCode(info) > 0) {
                return resMap.setErr("编码已存在").getResultMap();
            }
            if (StringUtils.isBlank(info.getDictName())) {
                return resMap.setErr("名称不能为空").getResultMap();
            }
            if (commServ.checkName(info) > 0) {
                return resMap.setErr("名称已存在").getResultMap();
            }

            commServ.updateTop(info);
            resMap.setSingleOk(info, "修改成功");
            //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(userDo, old, info, "修改-字典类型");

        } catch (Exception ex) {
            resMap.setErr("修改失败");
            logger.error("修改异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "修改字典", notes = "修改字典")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "info", value = "字典", dataType = "SysDictDo", required = true, paramType = "body")
    })
    @ResponseBody
    @RequestMapping(value = {"/updatesub"}, method = {RequestMethod.POST})
    public Object updateSub(HttpServletRequest request) {

        ResponeMap resMap = this.genResponeMap();

        try {
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
            SysDictDo info = new SysDictDo();
            SysDictDo old = new SysDictDo();
            UserDo userDo = LoginUtil.getUser(request);
            logger.debug( "{}",hru.getAllParaData() );
            new PageBeanWrapper( info,hru,"");
            info = commServ.findById(info.getId());
            MyBeanUtils.copyBeanNotNull2Bean(info, old);
            new PageBeanWrapper(info, hru);

            if (StringUtils.isBlank(info.getPid())) {
                return resMap.setErr("上级不能为空").getResultMap();
            }

            if (StringUtils.isBlank(info.getDictCode())) {
                return resMap.setErr("编码不能为空").getResultMap();
            }
            if (commServ.checkCode(info) > 0) {
                return resMap.setErr("编码已存在").getResultMap();
            }
            if (StringUtils.isBlank(info.getDictName())) {
                return resMap.setErr("名称不能为空").getResultMap();
            }
            if (commServ.checkName(info) > 0) {
                return resMap.setErr("名称已存在").getResultMap();
            }

            final SysDictDo pdict = commServ.findById(info.getPid());

            if (pdict.getPid() == null) {
                info.setTopId(pdict.getId());
                info.setTopCode(pdict.getDictCode());
            } else {
                info.setTopId(pdict.getTopId());
                //final SysDictDo topDict = commServ.findById(info.getPid());
                info.setTopCode(pdict.getTopCode());
            }

            commServ.updateBean(info);
            resMap.setSingleOk(info, "修改成功");
            //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(userDo, old, info, "修改-字典");

        } catch (Exception ex) {
            resMap.setErr("修改失败");
            logger.error("修改异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "字典内容", notes = "字典内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "字典id", dataType = "String", required = true, paramType = "query"),
    })
    @ResponseBody
    @RequestMapping(value = "/info", method = {RequestMethod.GET})

    public Object info(String id) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if (StringUtils.isBlank(id)) {
                return resMap.setErr("id不能为空").getResultMap();
            }
            SysDictDo info = commServ.findById(id);
            resMap.setSingleOk(info, "成功");

        } catch (Exception ex) {
            ex.printStackTrace();
            resMap.setErr("查询失败");
            logger.error("查询异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除字典", notes = "根据主键ID字典")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "query")
    })
    @ResponseBody
    @RequestMapping(value = {"/delbyid"}, method = {RequestMethod.DELETE})
    public Object delById(String id, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (StringUtils.isBlank(id)) {
                return resMap.setErr("id不能为空").getResultMap();
            }
            SysDictDo info = commServ.findById(id);
            if (info == null) {
                return resMap.setErr("删除失败,对象不存在").getResultMap();
            }
            final List<SysDictDo> subList = commServ.findSubDict(info.getId());
            if (subList.size() > 0) {
                return resMap.setErr("存在下级对象，不能删除").getResultMap();
            }
            commServ.deleteById(id);
            //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).delLog(userDo, info, "删除-字典");
            resMap.setOk("删除成功");
        } catch (Exception ex) {
            resMap.setErr("删除失败");
            logger.error("删除异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除多个字典", notes = "根据多个主键ID字典")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "ids", dataType = "String", required = true, paramType = "query")
    })
    @ResponseBody
    @RequestMapping(value = {"/delbyids"}, method = {RequestMethod.DELETE})
    public Object delByIds(String ids, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser(request);

            if (StringUtils.isBlank(ids)) {
                return resMap.setErr("id不能为空").getResultMap();
            }
            if (!ids.matches("^[a-zA-Z0-9\\-_,]*$")) {
                return resMap.setErr("id有非法字符").getResultMap();
            }
            final String[] split = ids.split(",");
            final String join = "'" + String.join("','", split) + "'";
            if (commServ.findSubDictCnt(join) > 0) {
                return resMap.setErr("存在下级对象，不能删除").getResultMap();
            }
            List<SysDictDo> list = commServ.findDictByIds(join);

            commServ.deleteByIds(join);

//            final String scode = SysParamSetOp.readValue( Constants.syspara_SystemCode, "");
//            for (SysDictDo info : list) {
//                new LogActionOp(scode, HttpRequestUtil.getAllIp(request)).delLog(userDo, info, "删除-字典");
//            }
            resMap.setOk("删除成功");
        } catch (Exception ex) {
            resMap.setErr("删除失败");
            logger.error("删除异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "分页查询", notes = "按条件进行数据分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "info", value = "字典信息", dataType = "SysDictDo", required = true, paramType = "body")
    })
    @ResponseBody
    @RequestMapping(value = {"/page"}, method = {RequestMethod.GET})
    public Object page(SysDictDo info, HttpServletRequest httpServletRequest) {
        ResponeMap resMap = this.genResponeMap();
        UserDo userDo = LoginUtil.getUser(httpServletRequest);
        try {

            info.setQryCond( ComUtil.chgLikeStr(info.getQryCond()));
            info.setDictName(ComUtil.chgLikeStr(info.getDictName()));
            info.setDictCode(ComUtil.chgLikeStr(info.getDictCode()));
            resMap.setPageInfo(info.getPageSize(), info.getCurrentPage());
            Long findCnt = commServ.findBeanCnt(info);
            List<SysDictDo> list = commServ.findBeanList(info);
            //resMap.setPageInfo(findCnt,info.getCurrentPage());
            resMap.setOk(findCnt, list, "获取字典列表成功");
        } catch (Exception ex) {
            resMap.setErr("获取字典列表");
            logger.error("获取字典列表失败:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取第一级字典列表", notes = "获取第一级字典列表")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "topCode", value = "业务id", dataType = "String", required = true, paramType = "query")
    })
    @ResponseBody
    @RequestMapping(value = "/dictfirstoption/{topCode}", method = {RequestMethod.GET})
    public Object dictFirstOption(@PathVariable String topCode, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null || StringUtils.isBlank(ud.getUserId())) {
                return resMap.setErr("请先登录").getResultMap();
            }
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            boolean ball = "1".equals( hru.getNvlPara("all") ) || "true".equalsIgnoreCase( hru.getNvlPara("all") );
            final List<SysDictDo> list = commServ.findLevel1DictByTopCode(topCode);
            if(!ball ){
                for (int i = list.size() - 1; i >= 0; i--) {
                    if(  "0".equals( list.get(i).getDisplay() ) ){
                        list.remove( i );
                    }
                }
            }
            resMap.setSingleOk(list, "获取成功");
        } catch (Exception ex) {
            resMap.setErr("获取字典失败");
            logger.error("获取字典失败:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取字典类型", notes = "获取字典类型")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "topCode", value = "业务id", dataType = "String", required = true, paramType = "query")
    })
    @ResponseBody
    @RequestMapping(value = "/typeoption", method = {RequestMethod.GET})
    public Object typeoption(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null || StringUtils.isBlank(ud.getUserId())) {
                return resMap.setErr("请先登录").getResultMap();
            }
            final List<SysDictDo> list = commServ.findAllType();
            resMap.setSingleOk(list, "获取成功");
        } catch (Exception ex) {
            resMap.setErr("获取字典类型失败");
            logger.error("获取字典类型失败:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "字典树", notes = "字典树")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "字典或类型id", dataType = "String", required = false, paramType = "query")
    })
    @ResponseBody
    @RequestMapping(value = {"/dicttree"}, method = {RequestMethod.GET})
    public Object dictTree(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        UserDo userDo = LoginUtil.getUser(request);
        try {
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
            logger.debug("{}", hru.getAllParaData());
            final String id = hru.getNvlPara("id");

            //SysDictDo tmp = new SysDictDo();
            List<SysDictDo> dictList = null;
            if (StringUtils.isNotBlank(id)) {
                //tmp.setPid( id );
                dictList = commServ.findSubDict(id);
            } else {
                //tmp.setPid( "#NULL#" );
                dictList = commServ.findAllType();
            }

            List<Object> list = new ArrayList<>();
            for (SysDictDo sysDictDo : dictList) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", sysDictDo.getId());
                map.put("name", sysDictDo.getDictName());
                map.put("code", sysDictDo.getDictCode());
                if (StringUtils.isBlank(sysDictDo.getPid())) {
                    map.put("type", "type");
                } else {
                    map.put("type", "dict");
                }
                map.put("hasLeaf", true);
                map.put("children", new ArrayList<>());
                list.add(map);
            }
            resMap.setSingleOk(list, "获取字典树成功");
        } catch (Exception ex) {
            resMap.setErr("获取字典树树");
            logger.error("获取字典树失败:", ex);
        }
        return resMap.getResultMap();
    }

}
