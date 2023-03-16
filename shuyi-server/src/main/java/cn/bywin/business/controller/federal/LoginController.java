package cn.bywin.business.controller.federal;

import static cn.bywin.business.common.util.Constants.ADMIN;
import static cn.bywin.business.common.util.Constants.FLSYSTEM;

import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.federal.FModelCollectDo;
import cn.bywin.business.bean.federal.FModelJobDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.federal.PmsResult;
import cn.bywin.business.bean.system.SysLogDo;
import cn.bywin.business.bean.system.SysRoleDo;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.system.SysUserRoleDo;
import cn.bywin.business.bean.view.TokenVo;
import cn.bywin.business.bean.view.UserVo;
import cn.bywin.business.bean.view.federal.FDataApproveVo;
import cn.bywin.business.bean.view.federal.FModelJobVo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.encrypt.Des;
import cn.bywin.business.common.encrypt.JwtHs;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.Constants;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.federal.ApiPmsService;
import cn.bywin.business.service.federal.ModelJobService;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.service.system.SysLogService;
import cn.bywin.business.service.system.SysRoleService;
import cn.bywin.business.service.system.SysUserRoleService;
import cn.bywin.business.service.system.SysUserService;
import cn.bywin.business.util.Code;
import cn.bywin.cache.RsaCache;
import cn.bywin.cache.UserRedisCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

/**
 * @Description 联邦学习登录模块
 * @Author wangh
 * @Date 2021-12-26
 */
@CrossOrigin(value = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        maxAge = 3600)
@RestController
@Api(tags = "联邦学习登录管理")
@RequestMapping("/user")
public class LoginController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Code code;
    @Autowired
    private RsaCache rsaCache;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private UserRedisCache userRedisCache;
    @Autowired
    private NodePartyService nodePartyService;
    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private ApiPmsService apiPmsService;

    @Autowired
    private ModelJobService modelJobService;

    @ApiOperation(value = "用户信息", notes = "用户信息")
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    public Map<String, Object> info(HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            SysUserDo userInfo = sysUserService.findById(userDo.getUserId());
            UserVo userVo = new UserVo();
            MyBeanUtils.copyBean2Bean(userVo, userInfo);
            SysRoleDo sysRoleDo = sysUserService.getRole(userDo.getUserId());
            userVo.setRole(sysRoleDo.getId());
            userVo.setPassword("*");
            userVo.setRoleName(sysRoleDo.getRoleName());
            result.setSingleOk(userVo, "获取成功");
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "指定用户id信息", notes = "指定用户id信息")
    @RequestMapping(value = "/userinfo", method = {RequestMethod.GET})
    public Map<String, Object> userInfo(HttpServletRequest request, String id) {
        ResponeMap result = genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            SysUserDo userInfo = sysUserService.findById(id);
            UserVo userVo = new UserVo();
            MyBeanUtils.copyBean2Bean(userVo, userInfo);
            SysRoleDo sysRoleDo = sysUserService.getRole(userDo.getUserId());
            userVo.setRole(sysRoleDo.getId());
            userVo.setPassword("*");
            userVo.setRoleName(sysRoleDo.getRoleName());
            result.setSingleOk(userVo, "获取成功");
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "节点信息", notes = "节点信息")
    @RequestMapping(value = "/nodeinfo", method = {RequestMethod.GET})
    public Map<String, Object> nodeinfo(HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            SysUserDo systemU = sysUserService.getSystemUser(FLSYSTEM);
            UserVo userVo = new UserVo();
            MyBeanUtils.copyBean2Bean(userVo, systemU);
            FNodePartyDo nodePartyDo = nodePartyService.findAll().get(0);
            userVo.setUsername(nodePartyDo.getName());
            userVo.setIcon(nodePartyDo.getIcon());
            userVo.setIsStatus(nodePartyDo.getIsStatus());
            userVo.setIsOpen(nodePartyDo.getIsOpen());
            result.setSingleOk(userVo, "获取节点信息成功");
        } catch (Exception e) {
            logger.error("获取节点信息失败", e);
            result.setErr("获取节点信息失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "更新节点用户信息", notes = "更新节点用户信息")
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public Map<String, Object> update(@RequestBody UserVo userVo, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        if (StringUtils.isNotBlank(userVo.getMobile())) {
            return result.setErr("手机号不能更新").getResultMap();
        }
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            SysRoleDo sysRoleDo = sysUserService.getRole(userDo.getUserId());
            if (sysRoleDo == null || !FLSYSTEM.equals(sysRoleDo.getId())) {
                return result.setErr("该用户权限不是超级管理员").getResultMap();
            }
            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            nodePartyDo.setName(userVo.getUsername());
            nodePartyDo.setIcon(userVo.getIcon());
            nodePartyDo.setIsOpen(userVo.getIsOpen());
            nodePartyDo.setIsStatus(userVo.getIsStatus());
            FNodePartyDo pmsNode = new FNodePartyDo();
            MyBeanUtils.copyBean2Bean(pmsNode, nodePartyDo);
            pmsNode.setIp("*");
            //接口推送到pms
            PmsResult pmsResult = apiPmsService.syncNode(pmsNode);
            if (!pmsResult.isSuccess()) {
                return result.setErr("操作失败，检查pms服务是否正常").getResultMap();
            }
            nodePartyService.updateNoNull(nodePartyDo);

            result.setOk("更新成功");
        } catch (Exception e) {
            logger.error("更新失败", e);
            result.setErr("更新失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "当前用户更新信息", notes = "当前用户更新信息")
    @RequestMapping(value = "/curuserupdate", method = {RequestMethod.POST})
    public Map<String, Object> curUserUpdate(@RequestBody SysUserDo userVo, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        if (StringUtils.isNotBlank(userVo.getMobile())) {
            return result.setErr("手机号不能更新").getResultMap();
        }
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            if (StringUtils.isBlank(userDo.getUserId())) {
                return result.setErr("用户id未登录").getResultMap();
            }
            SysUserDo info = sysUserService.findById(userDo.getUserId());
            SysUserDo old = new SysUserDo();
            MyBeanUtils.copyBeanNotNull2Bean(info, old);
            MyBeanUtils.copyBeanNotNull2Bean(userVo, info);
            info.setId(old.getId());
            info.setMobile(null);
            info.setPassword(null);
            info.setCreatorAccount(null);
            info.setCreatorId(null);
            info.setCreatorName(null);
            info.setIsLock(null);
            info.setRegTime(null);
            info.setCreatedTime(null);
            //info.setIsLock(old.getIsLock());
            info.setModifiedTime(ComUtil.getCurTimestamp());
            sysUserService.updateNoNull(info);
            if (!old.getUsername().equals(info.getUsername())) {
                SysRoleDo sysRoleDo = sysUserService.getRole(userDo.getUserId());
                if (sysRoleDo != null && FLSYSTEM.equals(sysRoleDo.getId())) {
                    FNodePartyDo nodePartyDo = nodePartyService.findAll().get(0);
                    nodePartyDo.setName(info.getUsername());
                    nodePartyService.updateNoNull(nodePartyDo);
                    //接口推送到pms
                    PmsResult pmsResult = apiPmsService.syncNode(nodePartyDo);
                    if (!pmsResult.isSuccess()) {
                        return result.setErr("操作失败，检查pms服务是否正常").getResultMap();
                    }
                }
            }

            result.setOk("更新成功");
        } catch (Exception e) {
            logger.error("更新失败", e);
            result.setErr("更新失败");
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "更新用户密码", notes = "更新用户密码")
    @RequestMapping(value = "/updatepwd", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", dataType = "String", required = true, paramType = "query", example = "1")
            , @ApiImplicitParam(name = "oldPassword", value = "旧密码", dataType = "String", required = true, paramType = "query", example = "1")
            , @ApiImplicitParam(name = "password", value = "新密码", dataType = "String", required = true, paramType = "query", example = "1")

    })
    public Map<String, Object> updatePwd(@RequestBody UserVo userVo, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        if (StringUtils.isBlank(userVo.getOldPassword())) {
            return result.setErr("旧密码不能为空").getResultMap();
        }
        if (StringUtils.isBlank(userVo.getId())) {
            return result.setErr("用户id不能为空").getResultMap();
        }
        if (StringUtils.isBlank(userVo.getPassword())) {
            return result.setErr("新密码不能为空").getResultMap();
        }
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            SysUserDo userInfo = sysUserService.findById(userVo.getId());
            if (userInfo == null) {
                return result.setErr("用户不存在").getResultMap();
            }
            if (!userDo.getUserId().equals(userInfo.getId())) {
                return result.setErr("用户不匹配").getResultMap();
            }
            String oldPwd = Des.encrypt(userVo.getOldPassword(), Constants.DESPWD);
            if (!userInfo.getPassword().equals(oldPwd)) {
                return result.setErr("旧密码不正确").getResultMap();
            }
            String newPwd = Des.encrypt(userVo.getPassword(), Constants.DESPWD);
            userInfo.setPassword(newPwd);
            sysUserService.updateNoNull(userInfo);
            clearLogin(userDo, request);
            result.setOk("更新密码成功");
        } catch (Exception e) {
            logger.error("更新密码失败", e);
            result.setErr("更新密码失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    @RequestMapping(value = "/userlist", method = {RequestMethod.GET})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "页数", dataType = "int", required = false, paramType = "query", example = "1")
            , @ApiImplicitParam(name = "pageSize", value = "每页大小", dataType = "int", required = false, paramType = "query", example = "10")

    })
    public Map<String, Object> userList(UserVo user, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            MyBeanUtils.chgBeanLikeProperties(user, "username", "qryCond", "mobile");
            user.genPage();
            long cnt = sysUserService.findAllByRoleCnt(user);
            List<UserVo> list = sysUserService.findAllByRole(user);
            result.setPageInfo(user.getPageSize(), user.getCurrentPage());
            result.setOk(cnt, list);
        } catch (Exception e) {
            logger.error("获取失败", e);
            result.setErr("获取失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "超级管理员更换", notes = "超级管理员更换")
    @RequestMapping(value = "/updatesystem", method = {RequestMethod.POST})
    public Map<String, Object> updateSystem(String userId, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            SysRoleDo sysRoleDo = sysUserService.getRole(userDo.getUserId());
            if (!FLSYSTEM.equals(sysRoleDo.getId())) {
                return result.setErr("该用户权限不是超级管理员").getResultMap();
            }
            SysUserDo userVo = sysUserService.findById(userId);
            if (userVo == null) {
                return result.setErr("用户不存在").getResultMap();
            }
            sysUserService.updateSystemPower(userDo.getUserId(), userId);
            result.setOk("超级权限更换成功");
        } catch (Exception e) {
            logger.error("超级权限更换失败", e);
            result.setErr("超级权限更换失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "修改角色", notes = "修改角色")
    @RequestMapping(value = "/roleadmin", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true, paramType = "query", example = "1")
            , @ApiImplicitParam(name = "roleId", value = "角色id", dataType = "String", required = true, paramType = "query", example = "1")

    })
    public Map<String, Object> roleAdmin(@RequestBody SysUserRoleDo sysUserRoleDo, HttpServletRequest request) {
        ResponeMap result = genResponeMap();
        try {
            UserDo userDo = LoginUtil.getUser(request);
            if (userDo == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            if (StringUtils.isBlank(sysUserRoleDo.getUserId())) {
                return result.setErr("用户id为空").getResultMap();
            }
            if (StringUtils.isBlank(sysUserRoleDo.getRoleId())) {
                return result.setErr("角色id为空").getResultMap();
            }
            if (FLSYSTEM.equals(sysUserRoleDo.getRoleId())) {
                return result.setErr("不能添加超级管理员角色").getResultMap();
            }
//            SysRoleDo sysRoleDo = sysUserService.getRole(userDo.getUserId());
//            if (!FLSYSTEM.equals(sysRoleDo.getId())) {
//                return result.setErr("该用户权限不是超级管理员").getResultMap();
//            }
            SysUserDo userVo = sysUserService.findById(sysUserRoleDo.getUserId());
            if (userVo == null) {
                return result.setErr("用户不存在").getResultMap();
            }

            SysRoleDo roleDo = sysRoleService.findById(sysUserRoleDo.getRoleId());
            if (roleDo == null) {
                return result.setErr("角色不存在").getResultMap();
            }
            SysUserRoleDo urTmp = new SysUserRoleDo();
            //urTmp.setRoleId(  roleDo.getId() );
            urTmp.setUserId(userVo.getId());
            List<SysUserRoleDo> list = sysUserRoleService.find(urTmp);
            if (list.size() == 0) {
                SysUserRoleDo ul = new SysUserRoleDo();
                ul.setRoleId(roleDo.getId());
                ul.setUserId(userVo.getId());
                ul.setId(ComUtil.genId());
                LoginUtil.setBeanInsertUserInfo(ul, userDo);
                sysUserRoleService.insertBean(ul);
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        SysUserRoleDo ul = list.get(i);
                        ul.setRoleId(roleDo.getId());
                        ul.setModifiedTime(ComUtil.getCurTimestamp());
                        sysUserRoleService.updateBean(ul);
                    } else {
                        sysUserRoleService.deleteById(list.get(i).getId());
                    }
                }
            }
            result.setOk("修改角色成功");
        } catch (Exception e) {
            logger.error("修改角色失败", e);
            result.setErr("修改角色失败");
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "获取动态验证码", notes = "获取动态验证码")
    @RequestMapping(value = "/captcha", method = {RequestMethod.GET})
    public void captcha(HttpServletResponse response) {
        //设置响应类型
        response.setContentType("image/png");
        // 不缓存此内容
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        try {
            BufferedImage imge = code.getCode();
            //必须使用write才能写出验证码
            ImageIO.write(imge, "PNG", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "登录", notes = "登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", required = true, paramType = "query", example = "1")
            , @ApiImplicitParam(name = "password", value = "密码", dataType = "String", required = true, paramType = "query", example = "1")
            , @ApiImplicitParam(name = "code", value = "验证码", dataType = "String", required = true, paramType = "query", example = "1")

    })
    @RequestMapping(value = "/login", method = {RequestMethod.GET})
    public Map<String, Object> login(UserVo user, HttpServletRequest request) throws Exception {
        ResponeMap result = genResponeMap();
        if (StringUtils.isBlank(user.getMobile())) {
            return result.setErr("手机号不能为空").getResultMap();
        }
        if (StringUtils.isBlank(user.getPassword())) {
            return result.setErr("用户密码为空").getResultMap();
        }
        if (StringUtils.isBlank(user.getCode())) {
            return result.setErr("验证码为空").getResultMap();
        }
        String redisCode = code.check(user.getCode().toLowerCase());
        if (StringUtils.isBlank(redisCode) || !user.getCode().toLowerCase().equals(redisCode)) {
            return result.setErr("验证码错误").getResultMap();
        }
        try {
            String newPwd = Des.encrypt(user.getPassword(), Constants.DESPWD);
            user.setPassword(newPwd);
            long cnt = sysUserService.findBeanCnt(user);
            if (cnt < 1) {
                return result.setErr("账号密码错误").getResultMap();
            } else {
                SysUserDo systemu = sysUserService.getSystemUser(FLSYSTEM);
                SysUserDo sysUserDo = sysUserService.find(user).get(0);
                if (sysUserDo.getIsLock() != 1) {
                    return result.setErr(String.format("用户未审批，联系管理员%s,联系方式为:%s", systemu.getChnname(),
                            systemu.getMobile())).getResultMap();
                }
                UserDo userDo = new UserDo();
                userDo.setUserId(sysUserDo.getId());
                userDo.setUserName(sysUserDo.getMobile());
                userDo.setChnName(sysUserDo.getUsername());
                SysRoleDo sysRoleDo = sysUserService.getRole(userDo.getUserId());
                if (FLSYSTEM.equals(sysRoleDo.getId())) {
                    userDo.setAdminIf(2);
                } else if (ADMIN.equals(sysRoleDo.getId())) {
                    userDo.setAdminIf(1);
                } else {
                    userDo.setAdminIf(0);
                }
                FNodePartyDo nodePartyDo = nodePartyService.findAll().get(0);
                if (nodePartyDo == null) {
                    return result.setErr("节点不存在").getResultMap();
                }
                TokenVo tokenVo = new TokenVo();
                tokenVo.setUuid(ComUtil.genId());
                tokenVo.setNode(nodePartyDo.getId());
                tokenVo.setUserId(userDo.getUserId());
                tokenVo.setTs(System.currentTimeMillis());
                String text = JsonUtil.toJson(tokenVo);
                String token = JwtHs.buildJWT(text);
                userDo.setTokenId(token);
                userDo.resetLastAct();
                userRedisCache.setUser(userDo);
                LoginUtil.setUser(request, userDo);
                sysUserDo.setModifiedTime(ComUtil.getCurTimestamp());
                sysUserService.updateNoNull(sysUserDo);
            }
            result.setOk("成功");
        } catch (Exception e) {
            logger.error("失败", e);
            result.setErr("失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "注册", notes = "注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "password", value = "密码", dataType = "String", required = true, paramType = "query", example = "1")
            , @ApiImplicitParam(name = "chnname", value = "昵称", dataType = "String", required = true, paramType = "query", example = "1")
            , @ApiImplicitParam(name = "mobile", value = "电话", dataType = "String", required = false, paramType = "query", example = "1")
            , @ApiImplicitParam(name = "email", value = "邮箱", dataType = "String", required = false, paramType = "query", example = "1")
    })
    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    public Map<String, Object> register(@RequestBody UserVo user) throws Exception {
        ResponeMap result = genResponeMap();
        if (StringUtils.isBlank(user.getMobile())) {
            return result.setErr("手机号不能为空").getResultMap();
        }
        if (StringUtils.isBlank(user.getPassword())) {
            return result.setErr("用户密码为空").getResultMap();
        }
        String reg = "^[a-zA-Z]{1}[a-zA-Z0-9_]{2,20}$";
        Pattern pat = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pat.matcher(user.getPassword());
        if (!matcher.find()) {
            return result.setErr("密码只能为大小写字母开头3-20个字符，其他字符可为大小写字母数字和下划线").getResultMap();
        }
        if (StringUtils.isBlank(user.getUsername())) {
            return result.setErr("用户不能为空").getResultMap();
        }
        if (StringUtils.isBlank(user.getCode())) {
            return result.setErr("验证码为空").getResultMap();
        }
        String redisCode = code.check(user.getCode().toLowerCase());
        if (StringUtils.isBlank(redisCode) || !user.getCode().toLowerCase().equals(redisCode)) {
            return result.setErr("验证码错误").getResultMap();
        }
        try {
            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            SysUserDo sysUserDo = new SysUserDo();
            MyBeanUtils.copyBean2Bean(sysUserDo, user);
            String newPwd = Des.encrypt(user.getPassword(), Constants.DESPWD);
            sysUserDo.setPassword(newPwd);
            sysUserDo.setUsername(sysUserDo.getUsername().trim());
            sysUserDo.setNodePartyId( nodePartyDo.getId() );
            Integer res = sysUserService.insertUser(sysUserDo);
            SysUserDo synUser = new SysUserDo();
            if (res != 1) {
                return result.setErr("手机号已存在").getResultMap();
            } else {
                SysLogDo sysLogDo = new SysLogDo();
                MyBeanUtils.copyBean2Bean(sysUserDo, user);
                sysLogDo.setTitle(String.format("注册:用户%s向本节点进行注册", sysUserDo.getUsername()));
                sysLogDo.setContent(String.format("用户%s向本节点进行注册,用户手机号为:%s、邮箱为:%s。如需通过审批，请管理员进行操作"
                        , sysUserDo.getUsername(), sysUserDo.getMobile(), sysUserDo.getEmail()));
                sysLogDo.setStatus(0);
                if (sysUserService.findAll().size() == 0) {
                    //接口推送到pms
                    PmsResult pmsResult = apiPmsService.syncNode(nodePartyDo);
                    if (!pmsResult.isSuccess()) {
                        return result.setErr("操作失败，检查pms服务是否正常").getResultMap();
                    }
                }
                sysLogService.insertBean(sysLogDo);
            }


            result.setOk("注册成功");
        } catch (Exception e) {
            logger.error("注册失败", e);
            result.setErr("注册失败");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "检测登录", notes = "检测登录")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/checklogin", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public Object checkLogin(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
//			String name = AssertionHolder.getAssertion().getPrincipal().getName();
//			logger.info("cur user is :" +name );
            UserDo user = LoginUtil.getUser(request);

            if (user != null && StringUtils.isNotBlank(user.getUserId())) {
                logger.debug(user.toString());
            } else {
                resMap.put("url", "./index.html");
                resMap.put("logoutUrl", "./index.html");
                return resMap.setErr("用户未登录").getResultMap();
            }
            HashMap<String, Object> userDo1 = new HashMap<>();
            MyBeanUtils.copyBean2Map(userDo1, user);

            userDo1.remove("ip");
            userDo1.remove("class");
            //user.setIp( HttpRequestUtil.getAllIp( request ));
            resMap.put("data", userDo1);
            //resMap.put("code","0");
            resMap.setOk();
        } catch (Exception ex) {
            ex.printStackTrace();
            resMap.setErr("用户验证异常");
            logger.error("用户验证异常", ex);
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "退出登录", notes = "退出登录")
    @ApiImplicitParams({

    })
    @RequestMapping(value = "/logout", method = {RequestMethod.GET})
    @ResponseBody
    public Object logout(HttpServletRequest request) {
        ResponeMap result = new ResponeMap();
        try {
            UserDo ud = LoginUtil.getUser(request);
            clearLogin(ud, request);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        result.setOk("退出成功");
        return result.getResultMap();
    }

    @ApiOperation(value = "删除用户", notes = "删除用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true, paramType = "query", example = "1")

    })
    @RequestMapping(value = "/deluser", method = {RequestMethod.DELETE})
    @ResponseBody
    public Object delUser(String userId, HttpServletRequest request) {
        ResponeMap result = new ResponeMap();
        try {
            if (StringUtils.isBlank(userId)) {
                return result.setErr("用户id不能为空").getResultMap();
            }
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            SysRoleDo sysRoleDo = sysUserService.getRole(ud.getUserId());
            if (!FLSYSTEM.equals(sysRoleDo.getId())) {
                return result.setErr("该用户权限不是超级管理员").getResultMap();
            }
            sysUserService.deleteById(userId);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        result.setOk("删除成功");
        return result.getResultMap();
    }


    public void clearLogin(UserDo ud, HttpServletRequest request) {
        SysUserDo sysUserDo = sysUserService.findById(ud.getUserId());
        if (sysUserDo != null) {
            sysUserDo.setModifiedTime(ComUtil.getCurTimestamp());
            sysUserService.updateNoNull(sysUserDo);
        }
        if (ud != null) {
            logger.info(ud.toString());
            userRedisCache.removeUser(ud.getTokenId());
        } else {
            logger.info("no login");
        }
        LoginUtil.setUser(request, null);
        Enumeration em = request.getSession().getAttributeNames();
        while (em.hasMoreElements()) {
            request.getSession().removeAttribute(em.nextElement().toString());
        }
    }


    @ApiOperation(value = "提交审批", notes = "提交审批")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nodeId", value = "节点id", dataType = "String", required = true, paramType = "query", example = "1")
            , @ApiImplicitParam(name = "dataId", value = "数据id", dataType = "String", required = true, paramType = "query", example = "1")
            , @ApiImplicitParam(name = "approve", value = "审批状态改为2", dataType = "String", required = true, paramType = "query", example = "1")
            , @ApiImplicitParam(name = "content", value = "审批理由", dataType = "String", required = true, paramType = "query", example = "1")
            , @ApiImplicitParam(name = "projectId", value = "项目id", dataType = "String", required = true, paramType = "query", example = "1")

    })
    @RequestMapping(value = "/syncapprove", method = {RequestMethod.POST})
    @ResponseBody
    public Object syncapprove(@RequestBody FDataApproveVo info, HttpServletRequest request) {
        ResponeMap result = new ResponeMap();
        try {
            if (StringUtils.isBlank(info.getDataId())) {
                return result.setErr("数据id不能为空").getResultMap();
            }
            if (StringUtils.isBlank(info.getNodeId())) {
                return result.setErr("节点id不能为空").getResultMap();
            }
            if (StringUtils.isBlank(info.getProjectId())) {
                return result.setErr("项目id不能为空").getResultMap();
            }
            if (info.getApprove() == null) {
                return result.setErr("审批状态不能为空").getResultMap();
            }
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            FDataApproveDo fDataApproveDo = apiPmsService.byProjectNodeDataId(info);

            if (fDataApproveDo == null) {
                return result.setErr("数据不存在").getResultMap();
            }
            apiPmsService.syncApprove(info);
            result.setOk("提交审批成功");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setErr("提交审批异常");
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "修改审批", notes = "修改审批")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/updateapprove", method = {RequestMethod.POST})
    @ResponseBody
    public Object updateApprove(@RequestBody FDataApproveDo info, HttpServletRequest request) {
        ResponeMap result = new ResponeMap();
        try {

            if (StringUtils.isBlank(info.getId())) {
                return result.setErr("审批id不能为空").getResultMap();
            }
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            PmsResult pmsResult = apiPmsService.syncApprove(info);
            if (!pmsResult.isSuccess()) {
                return result.setErr("操作失败，检查pms服务是否正常").getResultMap();
            }
            result.setOk("修改审批成功");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setErr("修改审批异常");
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "删除审批", notes = "删除审批")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "审批id", dataType = "String", required = true, paramType = "query", example = "1")

    })
    @RequestMapping(value = "/delapprove", method = {RequestMethod.DELETE})
    @ResponseBody
    public Object delApprove(String id, HttpServletRequest request) {
        ResponeMap result = new ResponeMap();
        try {
            if (StringUtils.isBlank(id)) {
                return result.setErr("审批id不能为空").getResultMap();
            }
            FDataApproveDo fDataApproveDo = new FDataApproveDo();
            fDataApproveDo.setId(id);
            PmsResult pmsResult = apiPmsService.delApprove(fDataApproveDo);
            if (!pmsResult.isSuccess()) {
                return result.setErr("操作失败，检查pms服务是否正常").getResultMap();
            }
            result.setOk("操作成功");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setErr("删除审批异常");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "审批列表", notes = "审批列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "审批类型（2我的待审批 1 我的审批成功 0 我的审批失败 9 我发起的审批列表）", dataType = "String", required = true, paramType = "query", example = "1")

    })
    @RequestMapping(value = "/getapprove", method = {RequestMethod.GET})
    @ResponseBody
    public Object getapprove(FDataApproveVo info, HttpServletRequest request) {
        ResponeMap result = new ResponeMap();
        try {
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            if (info.getType() == null) {
                return result.setErr("审批类型不能为空").getResultMap();
            }
            info.genPage();
            if (info.getType() == 9) {
                info.setCreatorId(ud.getUserId());
            } else if (info.getType() == 1) {
                info.setUserId(ud.getUserId());
                info.setApprove((info.getType()));
            } else if (info.getType() == 2) {
                info.setUserId(ud.getUserId());
                info.setApprove(info.getType());
            } else if (info.getType() == 0) {
                info.setUserId(ud.getUserId());
                info.setApprove(info.getType());
            }
            logger.debug(JsonUtil.toSimpleJson(info));
            return apiPmsService.getApprove(info);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setErr("获取审批列表失败").getResultMap();
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "模型收藏列表", notes = "模型收藏列表")
    @RequestMapping(value = "/modelcollect", method = {RequestMethod.GET})
    @ResponseBody
    public Object modelCollect(FModelJobVo fModelJobVo, HttpServletRequest request) {
        ResponeMap result = new ResponeMap();
        try {
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            fModelJobVo.setUserId(ud.getUserId());
            MyBeanUtils.chgBeanLikeProperties(fModelJobVo, "versions", "qryCond");
            fModelJobVo.genPage();
            long cnt = modelJobService.findBeanCollectCnt(fModelJobVo);
            List<FModelJobVo> list = modelJobService.findBeanCollectList(fModelJobVo);
            list.forEach(e -> {
                e.setHost("发起方");
            });
            result.setPageInfo(fModelJobVo.getPageSize(), fModelJobVo.getCurrentPage());
            result.setOk(cnt, list);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setErr("模型收藏列表失败").getResultMap();
        }
        return result.getResultMap();
    }


    @ApiOperation(value = "取消收藏", notes = "取消收藏")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "收藏id", dataType = "String", required = true, paramType = "query", example = "1")

    })
    @RequestMapping(value = "/delmodelcollect", method = {RequestMethod.DELETE})
    @ResponseBody
    public Object delModelCollect(String id, HttpServletRequest request) {
        ResponeMap result = new ResponeMap();
        try {
            UserDo ud = LoginUtil.getUser(request);
            if (ud == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            if (StringUtils.isBlank(id)) {
                return result.setErr("收藏id不能为空").getResultMap();
            }
            FModelCollectDo fModelCollectDo = new FModelCollectDo();
            fModelCollectDo.setModelJoBId(id);
            fModelCollectDo.setUserId(ud.getUserId());
            modelJobService.deleteModelCollect(fModelCollectDo);
            result.setOk("取消收藏成功");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setErr("删除审批异常");
        }
        return result.getResultMap();
    }

    @ApiOperation(value = "收藏模型", notes = "收藏模型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "收藏id", dataType = "String", required = true, paramType = "query")

    })
    @RequestMapping(value = "/addmodelcollect", method = {RequestMethod.POST})
    @ResponseBody
    public Object addModelCollect(HttpServletRequest request, @RequestBody FModelCollectDo fModelCollectDo) {
        ResponeMap result = new ResponeMap();
        try {

            UserDo ud = LoginUtil.getUser(request);
            if (ud == null) {
                return result.setErr("用户未登录").getResultMap();
            }
            if (StringUtils.isBlank(fModelCollectDo.getModelJoBId())) {
                return result.setErr("模型id不能为空").getResultMap();
            }
            fModelCollectDo.setUserId(ud.getUserId());
            FModelJobDo byId = modelJobService.findById(fModelCollectDo.getModelJoBId());
            if (byId == null) {
                return result.setErr("模型不存在空").getResultMap();
            }
            long l = modelJobService.addModelCollect(fModelCollectDo);
            if (l > 0) {
                return result.setErr("模型已收藏").getResultMap();
            }
            result.setOk("收藏模型成功");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setErr("收藏模型异常");
        }
        return result.getResultMap();
    }
}
