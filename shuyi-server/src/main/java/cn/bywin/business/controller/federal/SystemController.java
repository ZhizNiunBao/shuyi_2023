package cn.bywin.business.controller.federal;

import static cn.bywin.business.common.util.Constants.ADMIN;
import static cn.bywin.business.common.util.Constants.FLSYSTEM;
import static cn.bywin.business.util.MapTypeAdapter.isUrlConnect;

import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.federal.PmsResult;
import cn.bywin.business.bean.system.SysLogDo;
import cn.bywin.business.bean.system.SysMenuDo;
import cn.bywin.business.bean.system.SysRoleDo;
import cn.bywin.business.bean.system.SysRoleMenuDo;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.system.SysUserRoleDo;
import cn.bywin.business.bean.view.ServerUrlVo;
import cn.bywin.business.bean.view.SysMenuVo;
import cn.bywin.business.bean.view.SysRoleMenuVo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.service.system.SysLogService;
import cn.bywin.business.service.system.SysMenuService;
import cn.bywin.business.service.system.SysRoleMenuService;
import cn.bywin.business.service.system.SysRoleService;
import cn.bywin.business.service.system.SysUserRoleService;
import cn.bywin.business.service.system.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 联邦学习系统管理
 * @Author wangh
 * @Date 2021-12-27
 */
@CrossOrigin(value = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        maxAge = 3600)
@RestController
@Api(tags = "联邦学习系统管理")
@RequestMapping("/system")
public class SystemController extends BaseController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private NodePartyService nodePartyService;
    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${exchangeServerUrl}")
    private String exchangeServerUrl;
    @Value("${flowServerUrl}")
    private String flowServerUrl;

    @ApiOperation(value = "首页消息列表", notes = "首页消息列表")
    @RequestMapping(value = "/msglist", method = {RequestMethod.GET})
    public Map<String, Object> msgList(HttpServletRequest request, SysLogDo sysLogDo) {
        ResponeMap result = genResponeMap();
        try {
            FNodePartyDo nodePartyDo = nodePartyService.findAll().get(0);
            if (nodePartyDo == null) {
                return result.setErr("节点不存在").getResultMap();
            }
            MyBeanUtils.chgBeanLikeProperties(sysLogDo, "name", "qryCond");
            sysLogDo.genPage();
            sysLogDo.setCurrentPage(1);
            List<SysLogDo> data = sysLogService.findBeanList(sysLogDo);
            result.setSingleOk(data, "获取成功");
            result.getResultMap().put("info", null);
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "首页消息列表状态更新", notes = "首页消息列表状态更新")
    @RequestMapping(value = "/msgchange", method = {RequestMethod.POST})
    public Map<String, Object> msgChange(@RequestBody SysLogDo info) {
        ResponeMap result = genResponeMap();
        try {
            FNodePartyDo nodePartyDo = nodePartyService.findAll().get(0);
            if (StringUtils.isBlank(info.getId())) {
                return result.setErr("id不能为空").getResultMap();
            }
            if (info.getStatus() == null) {
                return result.setErr("状态不能为空").getResultMap();
            }
            SysLogDo sysLogDo = sysLogService.findById(info.getId());
            if (sysLogDo == null) {
                return result.setErr("数据不存在").getResultMap();
            }
            sysLogService.updateNoNull(info);
            result.setSingleOk(info, "更新成功");
        } catch (Exception e) {
            logger.error("更新失败", e);
            result.setErr("更新失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "服务校验", notes = "服务校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "url", value = "服务url", dataType = "String", required = true, paramType = "query", example = "")
    })
    @RequestMapping(value = "/servercheck", method = {RequestMethod.GET})
    public Map<String, Object> serverCheck(HttpServletRequest request, ServerUrlVo server) {
        ResponeMap result = genResponeMap();
        try {
            if (StringUtils.isBlank(server.getUrl())) {
                return result.setErr("url不能为空").getResultMap();
            }
            if (StringUtils.isBlank(server.getName())) {
                return result.setErr("url名称不能为空").getResultMap();
            }
            if (server.getUrl().toLowerCase().startsWith("http")) {
                boolean isFlag = isUrlConnect(server.getUrl());
                if (isFlag) {
                    redisTemplate.opsForValue().set(server.getName(), String.valueOf(1), Duration.ofHours(1));
                } else {
                    redisTemplate.opsForValue().set(server.getName(), String.valueOf(0), Duration.ofHours(1));
                }
                logger.info("校验{}", server.getUrl() + isFlag);
            } else {
                redisTemplate.opsForValue().set(server.getName(), String.valueOf(0), Duration.ofHours(1));
                return result.setErr("错误的url").getResultMap();
            }
            result.setOk("校验成功");
        } catch (Exception e) {
            logger.error("校验失败", e);
            result.setErr("校验失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "查看顶部菜单", notes = "查看顶部菜单")
    @RequestMapping(value = "/topmenu", method = {RequestMethod.GET})
    public Map<String, Object> topMenu(HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            List<SysMenuDo> all = sysMenuService.findAll();

            List<SysMenuDo> list = sysMenuService.usertopmenu(userDo.getUserId());
            List<SysMenuVo> data = new ArrayList<>();
            list.stream().forEach(e -> {
                SysMenuVo bean = new SysMenuVo();
                try {
                    MyBeanUtils.copyBean2Bean(bean, e);
                    bean.setMenuUrl(e.getMenuName());
                    bean.setHasNext(all.stream().filter(c -> e.getId().equals(c.getPid())).count() > 0 ? 1 : 0);

                    data.add(bean);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            result.setSingleOk(data, "获取成功");
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "查看顶部下级菜单", notes = "查看顶部下级菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "top菜单编码", dataType = "String", required = true, paramType = "query", example = "")
    })
    @RequestMapping(value = "/submenu", method = {RequestMethod.GET})
    public Map<String, Object> subMenu(HttpServletRequest request, String code) {
        ResponeMap result = genResponeMap();

        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            if (StringUtils.isBlank(code)) {
                return result.setErr("code不能为空").getResultMap();
            }
            SysMenuDo menuByCode = sysMenuService.getMenuByCode(code);
            if (menuByCode == null) {
                return result.setErr("菜单不存在").getResultMap();
            }
            List<SysMenuDo> list = sysMenuService.usersubmenu(code, userDo.getUserId());
            List<SysMenuVo> data = new ArrayList<>();
            list.stream().forEach(e -> {
                SysMenuVo bean = new SysMenuVo();
                try {
                    MyBeanUtils.copyBean2Bean(bean, e);

                    bean.setMenuUrl(menuByCode.getMenuName().concat(" / ").concat(e.getMenuName()));
                    data.add(bean);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            result.setSingleOk(data, "获取成功");
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "根据id查看顶部下级菜单", notes = "根据id查看顶部下级菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "菜单id", dataType = "String", required = true, paramType = "query", example = "")
            , @ApiImplicitParam(name = "menuUrl", value = "菜单路由中文", dataType = "String", required = true, paramType = "query", example = "")
            , @ApiImplicitParam(name = "menuType", value = "菜单类型 （ icon 按钮）", dataType = "String", required = false, paramType = "query", example = "menu")
    })
    @RequestMapping(value = "/submenuid", method = {RequestMethod.GET})
    public Map<String, Object> subMenuId(HttpServletRequest request, String id, String menuUrl, @RequestParam(required = false, value = "menuType") String menuType) {
        ResponeMap result = genResponeMap();

        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            if (StringUtils.isBlank(id)) {
                return result.setErr("code不能为空").getResultMap();
            }
            if (StringUtils.isBlank(menuUrl)) {
                return result.setErr("menuUrl不能为空").getResultMap();
            }
            SysMenuDo menuByCode = sysMenuService.findById(id);
            if (menuByCode == null) {
                return result.setErr("菜单不存在").getResultMap();
            }
            List<SysMenuDo> all = sysMenuService.findAll();
            List<SysMenuDo> list = sysMenuService.userSubMenuId(id, userDo.getUserId(), menuType);
            List<SysMenuVo> data = new ArrayList<>();
            list.stream().forEach(e -> {
                SysMenuVo bean = new SysMenuVo();
                try {
                    MyBeanUtils.copyBean2Bean(bean, e);
                    bean.setHasNext(all.stream().filter(c -> e.getId().equals(c.getPid())).count() > 0 ? 1 : 0);
                    bean.setMenuUrl(menuUrl.concat(" / ").concat(e.getMenuName()));
                    data.add(bean);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            result.setSingleOk(data, "获取成功");
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "菜单权限列表", notes = "菜单权限列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", dataType = "String", required = true, paramType = "query", example = "")
    })
    @RequestMapping(value = "/menutree", method = {RequestMethod.GET})
    public Map<String, Object> menuTree(HttpServletRequest request, String roleId) {
        ResponeMap result = genResponeMap();
        try {
            if (StringUtils.isBlank(roleId)) {
                return result.setErr("roleId不能为空").getResultMap();
            }
            List<SysMenuDo> alls = sysMenuService.findAll();
            List<SysMenuVo> datas = new ArrayList<>();
            List<String> meuns = new ArrayList<>();
            List<SysRoleMenuDo> byRoleId = sysRoleMenuService.findByRoleId(roleId);
            for (SysRoleMenuDo sysRoleMenuDo : byRoleId) {
                meuns.add(sysRoleMenuDo.getMenuId());
            }
            for (SysMenuDo sysMenuDo : alls) {
                if (sysMenuDo.getShowFlag() == 1) {
                    SysMenuVo sysMenuVo = new SysMenuVo();
                    MyBeanUtils.copyBean2Bean(sysMenuVo, sysMenuDo);
                    if (meuns.contains(sysMenuDo.getId())) {
                        sysMenuVo.setStatus(1);
                    } else {
                        sysMenuVo.setStatus(0);
                    }
                    datas.add(sysMenuVo);
                }
            }
            List<SysMenuVo> list = sysMenuService.menuTree(datas);
            result.setSingleOk(list, "获取成功");
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "树形菜单列表", notes = "树形菜单列表")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/allmenutree", method = {RequestMethod.GET})
    public Map<String, Object> allMenuTree(HttpServletRequest request, SysMenuDo info) {
        ResponeMap result = genResponeMap();
        try {
            MyBeanUtils.chgBeanLikeProperties(info, "menuName", "qryCond");
            List<SysMenuDo> alls = sysMenuService.findBeanListAll(info);

            List<SysMenuVo> datas = new ArrayList<>();
            for (SysMenuDo sysMenuDo : alls) {
                SysMenuVo sysMenuVo = new SysMenuVo();
                MyBeanUtils.copyBean2Bean(sysMenuVo, sysMenuDo);
                datas.add(sysMenuVo);
            }
            List<SysMenuVo> list = sysMenuService.menuTree(datas);
            result.setSingleOk(list, "获取成功");
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "用户树形菜单", notes = "用户树形菜单")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/usermenutree", method = {RequestMethod.GET})
    public Map<String, Object> userMenuTree(HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {
            UserDo user = LoginUtil.getUser();
           //MyBeanUtils.chgBeanLikeProperties(info, "menuName", "qryCond");
            SysMenuDo info = new SysMenuDo();
            List<SysMenuDo> alls = sysMenuService.findBeanListAll(info);
            SysUserRoleDo urTmp = new SysUserRoleDo();
            urTmp.setUserId( user.getUserId() );
            List<SysUserRoleDo> urList = sysUserRoleService.find(urTmp);
            if( urList.size() >0 ) {
                List<String> rmList = sysRoleMenuService.findByRoleId( urList.get( 0 ).getRoleId() ).stream().map( x->x.getMenuId() ).distinct().collect( Collectors.toList());
                alls = alls.stream().filter( x -> rmList.indexOf( x.getId() ) >= 0 ).sorted((menu1, menu2) -> {
                    return (menu1.getMenuOrder() == null ? 0 : menu1.getMenuOrder()) - (menu2.getMenuOrder() == null ?
                            0 : menu2.getMenuOrder());
                }).collect( Collectors.toList() );
                List<SysMenuVo> datas = new ArrayList<>();
                for ( SysMenuDo sysMenuDo : alls ) {
                    SysMenuVo sysMenuVo = new SysMenuVo();
                    MyBeanUtils.copyBean2Bean( sysMenuVo, sysMenuDo );
                    datas.add( sysMenuVo );
                }
                List<SysMenuVo> list = buildMenu( datas ,null);
                result.setSingleOk(list, "获取成功");
            }
            else{
                List<SysMenuVo> list = new ArrayList<>();
                result.setSingleOk(list, "用户未授权");
            }
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }

    private List<SysMenuVo> buildMenu( List<SysMenuVo> datas, SysMenuVo pmenu){
        List<SysMenuVo> list = new ArrayList<>();
        String pid = "";
        if( pmenu != null ){
            pid = pmenu.getId();
        }
        for ( SysMenuVo data : datas ) {
            if( pid.equals( ComUtil.trsEmpty( data.getPid() ) )){
                list.add( data );
                if(StringUtils.isBlank( pid )){
                    data.setMenuUrl( data.getMenuName() );
                }
                else{
                    data.setMenuUrl( pmenu.getMenuUrl()+"/" + data.getMenuName());
                }
                data.setHasNext( 0 );
                List<SysMenuVo> subList = buildMenu( datas, data );
                if( subList.size()>0){
                    data.setHasNext( 1 );
                    data.setChildren( subList );
                }
            }
        }
        return list;
    }

    @ApiOperation(value = "菜单权限赋值", notes = "菜单权限赋值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", dataType = "String", required = true, paramType = "query", example = "")
            , @ApiImplicitParam(name = "menuIds", value = "菜单id数组", dataType = "String[]", required = true, paramType = "query", example = "")
    })
    @RequestMapping(value = "/rolepower", method = {RequestMethod.POST})
    public Map<String, Object> rolePower(HttpServletRequest request, @RequestBody SysRoleMenuVo info) {
        ResponeMap result = genResponeMap();
        if (StringUtils.isEmpty(info.getRoleId())) {
            return result.setErr("角色id为空").getResultMap();
        }
        if (info.getMenuIds().isEmpty() && info.getMenuIds().size() > 0) {
            return result.setErr("菜单数组为空").getResultMap();
        }
        UserDo userDo = LoginUtil.getUser(request);
        if (userDo == null) {
            return result.setErr("用户未登录").getResultMap();
        }
        SysRoleDo sysRoleDo = sysUserService.getRole(userDo.getUserId());
        if (!FLSYSTEM.equals(sysRoleDo.getId())) {
            if (FLSYSTEM.equals(info.getRoleId())) {
                return result.setErr("不能对超级管理员进行此操作").getResultMap();
            }
        }
        try {
            sysMenuService.updateNoNullData(info, userDo);
            result.setOk("权限赋值成功");
        } catch (Exception e) {
            logger.error("权限赋值失败", e);
            result.setErr("权限赋值失败");
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "菜单列表", notes = "菜单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuName", value = "搜索框 查询条件", dataType = "String", required = false, paramType = "query", example = "1")
            , @ApiImplicitParam(name = "currentPage", value = "分页 页数", dataType = "int", required = false, paramType = "query", example = "1")

    })
    @RequestMapping(value = "/menulist", method = {RequestMethod.GET})
    public Map<String, Object> menuList(HttpServletRequest request, SysMenuDo info) {
        ResponeMap result = genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            SysRoleDo sysRoleDo = sysUserService.getRole(userDo.getUserId());
            if (!FLSYSTEM.equals(sysRoleDo.getId()) && !ADMIN.equals(sysRoleDo.getId())) {
                return result.setErr("该用户权限不是管理员").getResultMap();
            }
            MyBeanUtils.chgBeanLikeProperties(info, "menuName", "qryCond");
            info.genPage();
            long cnt = sysMenuService.findBeanCnt(info);
            List<SysMenuDo> list = sysMenuService.findBeanList(info);
            result.setPageInfo(info.getPageSize(), info.getCurrentPage());
            result.setOk(cnt, list);
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "菜单添加", notes = "菜单添加")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuType", value = "菜单类型", dataType = "String", required = true, paramType = "query", example = "")
            , @ApiImplicitParam(name = "menuName", value = "菜单名称", dataType = "String", required = true, paramType = "query", example = "")
            , @ApiImplicitParam(name = "showFlag", value = "菜单是否显示 1 显示 0 不显示", dataType = "int", required = true, paramType = "query", example = "")
            , @ApiImplicitParam(name = "menuOrder", value = "菜单序号", dataType = "int", required = true, paramType = "query", example = "")
            , @ApiImplicitParam(name = "url", value = "菜单路由", dataType = "String", required = true, paramType = "query", example = "")

    })
    @RequestMapping(value = "/menuadd", method = {RequestMethod.POST})
    public Map<String, Object> menuAdd(HttpServletRequest request, @RequestBody SysMenuDo info) {
        ResponeMap result = genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            SysRoleDo sysRoleDo = sysUserService.getRole(userDo.getUserId());
            if (!FLSYSTEM.equals(sysRoleDo.getId()) && !ADMIN.equals(sysRoleDo.getId())) {
                return result.setErr("该用户权限不是管理员").getResultMap();
            }
            if (StringUtils.isBlank(info.getMenuName())) {
                return result.setErr("菜单名称为空").getResultMap();
            }
            if (StringUtils.isBlank(info.getUrl())) {
                return result.setErr("菜单路由为空").getResultMap();
            }
            if (info.getMenuOrder()==null) {
                return result.setErr("菜单排序为空").getResultMap();
            }
            if(StringUtils.isNotBlank( info.getPid() )){
                SysMenuDo pmenu = sysMenuService.findById( info.getPid() );
                if( pmenu == null){
                    return result.setErr("上级菜单不存在").getResultMap();
                }
                info.setLevel(  pmenu.getLevel()+1 );
            }
            else{
                info.setLevel(  1 );
            }
            if (info.getLevel() == null) {
                return result.setErr("菜单层级为空").getResultMap();
            }
            SysMenuDo old = new SysMenuDo();
            old.setUrl(info.getUrl());
            if (sysMenuService.findBeanCnt(old) > 0) {
                return result.setErr("菜单路由已存在").getResultMap();
            }
            LoginUtil.setBeanInsertUserInfo(info, userDo);
            sysMenuService.insertBean(info);
            result.setOk("成功");
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "菜单修改", notes = "菜单修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "menuType", value = "菜单类型", dataType = "String", required = true, paramType = "query", example = "")
            , @ApiImplicitParam(name = "menuName", value = "菜单名称", dataType = "String", required = true, paramType = "query", example = "")
            , @ApiImplicitParam(name = "showFlag", value = "菜单是否显示 1 显示 0 不显示", dataType = "int", required = true, paramType = "query", example = "")
            , @ApiImplicitParam(name = "menuOrder", value = "菜单序号", dataType = "int", required = true, paramType = "query", example = "")
            , @ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "query", example = "")
            , @ApiImplicitParam(name = "url", value = "菜单路由", dataType = "int", required = true, paramType = "query", example = "")

    })
    @RequestMapping(value = "/menuupdate", method = {RequestMethod.POST})
    public Map<String, Object> menuUpdate(HttpServletRequest request, @RequestBody SysMenuDo info) {
        ResponeMap result = genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            SysRoleDo sysRoleDo = sysUserService.getRole(userDo.getUserId());
            if (!FLSYSTEM.equals(sysRoleDo.getId()) && !ADMIN.equals(sysRoleDo.getId())) {
                return result.setErr("该用户权限不是管理员").getResultMap();
            }
            if (StringUtils.isBlank(info.getId())) {
                return result.setErr("菜单id为空").getResultMap();
            }
            if (StringUtils.isBlank(info.getMenuName())) {
                return result.setErr("菜单名称为空").getResultMap();
            }
            if (info.getMenuOrder()==null) {
                return result.setErr("菜单排序为空").getResultMap();
            }
            SysMenuDo menuDo = sysMenuService.findById(info.getId());
            if (menuDo == null) {
                return result.setErr("菜单不存在").getResultMap();
            }
            if(StringUtils.isNotBlank( info.getPid() ) ){
                if( info.getId().equals(  info.getPid() )){
                    return result.setErr("上级菜单不能设置为自己").getResultMap();
                }
                SysMenuDo pmenu = sysMenuService.findById( info.getPid() );
                if( pmenu == null){
                    return result.setErr("上级菜单不存在").getResultMap();
                }
                info.setLevel(  pmenu.getLevel()+1 );
                List<SysMenuDo> all = sysMenuService.findAll();
                List<String> idList = new ArrayList<>();
                idList.add( info.getId() );
                boolean bsub = false;
                do {
                    bsub =false;
                    for ( SysMenuDo sysMenuDo : all ) {
                        if ( sysMenuDo.getPid() != null ) {
                            if ( idList.indexOf( sysMenuDo.getPid() ) >= 0 && idList.indexOf( sysMenuDo.getId() ) < 0) {
                                bsub = true;
                                idList.add( sysMenuDo.getId() );
                            }
                        }
                    }
                }
                while (  bsub );
                if( idList.indexOf( pmenu.getId() )>=0){
                    return result.setErr("上级菜单不能设置为自己的下级菜单").getResultMap();
                }

            }
            else{
                info.setLevel(  1 );
            }
            if (info.getLevel() == null) {
                return result.setErr("菜单层级为空").getResultMap();
            }
            if (info.getMenuOrder() == null) {
                return result.setErr("菜单排序为空").getResultMap();
            }
            SysMenuDo old = new SysMenuDo();
            old.setUrl(info.getUrl());
            if (sysMenuService.findBeanCnt(old) > 1) {
                return result.setErr("菜单路由已存在").getResultMap();
            }
            sysMenuService.updateNoNull(info);
            result.setOk("成功");
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "菜单删除", notes = "菜单删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "菜单id 列表（多个用逗号分割）", dataType = "String", required = true, paramType = "query", example = "")
    })
    @RequestMapping(value = "/menudel", method = {RequestMethod.DELETE})
    public Map<String, Object> menuDel(HttpServletRequest request, String ids) {
        ResponeMap result = genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            SysRoleDo sysRoleDo = sysUserService.getRole(userDo.getUserId());
            if (!FLSYSTEM.equals(sysRoleDo.getId()) && !ADMIN.equals(sysRoleDo.getId())) {
                return result.setErr("该用户权限不是管理员").getResultMap();
            }
            if (StringUtils.isBlank(ids)) {
                return result.setErr("菜单id为空").getResultMap();
            }
            List<SysMenuDo> all = sysMenuService.findAll();
            List<String> split = Arrays.asList(ids.split("(,|\\s)+"));
            boolean allDelete = true;
            int cnt = split.size();
            for (String id : split) {
                try {
                    SysMenuDo menuDo = sysMenuService.findById(id);
                    if (menuDo == null || all.stream().filter(c -> menuDo.getId().equals(c.getPid())).count() > 0) {
                        allDelete = false;
                        cnt--;
                    } else {
                        sysMenuService.deleteById(menuDo.getId());
                    }
                } catch (Exception e1) {
                    logger.error(e1.getMessage(), e1);
                    allDelete = false;
                    cnt--;
                }
            }
            if (allDelete) {
                result.setOk("删除菜单成功");
            } else {
                if (cnt > 0) {
                    result.setOk("删除菜单部分成功，请刷新页面重试");
                } else {
                    result.setErr("删除菜单失败，请刷新页面重试");
                }
            }
        } catch (Exception e) {
            logger.error("删除失败", e);
            result.setErr("删除失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "获取父级菜单", notes = "获取父级菜单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "level", value = "菜单层级", dataType = "String", required = true, paramType = "query", example = "")

    })
    @RequestMapping(value = "/menuparent", method = {RequestMethod.GET})
    public Map<String, Object> menuParent(HttpServletRequest request, Integer level) {
        ResponeMap result = genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            if (level == null) {
                return result.setErr("菜单层级不能为空").getResultMap();
            }
            SysRoleDo sysRoleDo = sysUserService.getRole(userDo.getUserId());
            if (!FLSYSTEM.equals(sysRoleDo.getId()) && !ADMIN.equals(sysRoleDo.getId())) {
                return result.setErr("该用户权限不是管理员").getResultMap();
            }
            List<SysMenuDo> results = sysMenuService.findAll().stream()
                    .filter(menu -> menu.getLevel() == (level - 1))
                    .collect(Collectors.toList());
            result.setSingleOk(results, "获取父级菜单成功");
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }


}
