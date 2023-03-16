package cn.bywin.business.controller.olk;

import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.olk.TOlkDatabaseDo;
import cn.bywin.business.bean.olk.TOlkDcServerDo;
import cn.bywin.business.common.base.AuthDepartmentBean;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.hetu.AuthType;
import cn.bywin.business.hetu.CatalogInfo;
import cn.bywin.business.hetu.DynamicCatalogResult;
import cn.bywin.business.hetu.HetuDynamicCatalogUtil;
import cn.bywin.business.hetu.HetuInfo;
import cn.bywin.business.hetu.HetuJdbcOperateComponent;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.service.olk.OlkDatabaseService;
import cn.bywin.business.service.olk.OlkDcServerService;
import cn.bywin.business.trumodel.ApiOlkDbService;
import cn.bywin.cache.ISysParamSetOp;
import cn.bywin.common.resp.ObjectResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;


@CrossOrigin(value = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        maxAge = 3600)
@RestController
@Api(tags = "olk-节点管理-olkdcserver")
@RequestMapping("/olkdcserver")
public class OlkDcServerController extends BaseController {

    protected final Logger logger = LoggerFactory.getLogger( this.getClass() );

    private static final String DC_CODE_PATTERN = "^[a-zA-Z][a-zA-Z0-9]{2,14}$";

//    @Autowired
//    private MenuUtil menuUtil;

    @Autowired
    private ISysParamSetOp setOp;

    @Autowired
    private OlkDatabaseService databaseService;

    @Autowired
    private OlkDcServerService dcserverService;

    @Autowired
    private HetuJdbcOperateComponent hetuComponent;

    @Autowired
    private NodePartyService nodePartyService;

    @Autowired
    private ApiOlkDbService apiOlkDbService;

    @Autowired
    private HetuInfo masterHetuInfo;

    @Value("${olk.isEncryptPassword}")
    private Boolean isEncryptPassword;

    @ApiOperation(value = "新增olk节点", notes = "新增olk节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keystoreFile", value = "keystore文件", dataType = "file", required = false, paramType = "query"),
            @ApiImplicitParam(name = "keytabFile", value = "keytab文件", dataType = "file", required = false, paramType = "query"),
            @ApiImplicitParam(name = "krb5ConfigFile", value = "krb5Config文件", dataType = "file", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dcCode", value = "数据中心代码", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dcName", value = "数据中心名称", dataType = "String", required = true, paramType = "query"),
            //@ApiImplicitParam(name = "deptNo", value = "部门编号", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dcType", value = "是否dc服务 1是 0否", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "jdbcUrl", value = "jdbc地址", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "connectionUrl", value = "olk服务地址", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "connectionUser", value = "olk用户名", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "connectionPwd", value = "olk密码", dataType = "String", required = false, paramType = "query"),
            //@ApiImplicitParam(name = "manageAccount", value = "负责帐号", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "enable", value = "是否启用 1启用 0禁用", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "authType", value = "认证类型 none kerberos ldap", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "remoteServiceName", value = "kerberos 服务名", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "encryptFlag", value = "是否对密码加密, 0不加密, 1 加密", dataType = "String", required = true, paramType = "query"),
            //@ApiImplicitParam(name = "dcPriv", value = "数据中心私钥", dataType = "String", required = false, paramType = "query"),
            //@ApiImplicitParam(name = "dcPub", value = "数据中心公钥", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "cacheTemplete", value = "处理表模板", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "cacheDbSet", value = "处理库配置", dataType = "String", required = false, paramType = "query"),
    })
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    public Object add( @RequestParam(value = "keystoreFile", required = false) MultipartFile keystoreFile,
                       @RequestParam(value = "keytabFile", required = false) MultipartFile keytabFile,
                       @RequestParam(value = "krb5ConfigFile", required = false) MultipartFile krb5ConfigFile,
                       HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        String path = null;
        try {

            UserDo userInfo = LoginUtil.getUser( request );
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
//            if( StringUtils.isBlank( hru.getNvlPara("id") ) ){
//                return  resMap.setErr("id不能为空").getResultMap();
//            }
            logger.info( "keystoreFile:{},keytabFile:{},krb5ConfigFile:{},id:{}", keystoreFile != null, keytabFile != null, krb5ConfigFile != null, hru.getNvlPara( "id" ) );

            TOlkDcServerDo info = new TOlkDcServerDo();
            new PageBeanWrapper( info, hru );
            info.setId( ComUtil.genId() );
            FNodePartyDo nodePartyDo = nodePartyService.findFirst();
            info.setNodePartyId( nodePartyDo.getId() );
            info.setSynFlag( 0 );
            LoginUtil.setBeanInsertUserInfo( info, userInfo );
            if ( info.getDcType() == null || info.getDcType() != 0 ) {
                info.setDcType( 1 );
            }

            String errorMessage = checkAddUpdateArgument( info, userInfo );
            if ( errorMessage != null ) {
                return resMap.setErr( errorMessage ).getResultMap();
            }

//            Example exp = new Example(TOlkDatabaseDo.class);
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andEqualTo("dcId", info.getId());
//            Integer dbcnt = databaseService.findCountByExample(exp);
//            if( dbcnt != null && dbcnt>0){
//                if(!ComUtil.trsEmpty(old.getDcCode()).equals(info.getDcCode())){
//                    return resMap.setErr("元数据已生成，编码不能修改").getResultMap();
//                }
//            }

//            Map<String, byte[]> fileMap = new HashMap<>(files.length);
//            for (MultipartFile file : files) {
//                fileMap.put(file.getOriginalFilename(), file.getBytes());
//            }

            fillOldConfigFile( info, keystoreFile, keytabFile, krb5ConfigFile );
            //TOlkDcServerDo newDcServer = changeDcServerVoToDo(updateDcServer, fileMap);

            if ( info.getEnable() != null && info.getEnable() == 1 ) {
                // 只有节点可用时才会去判断认证文件是否完整
                String authType = info.getAuthType();
                if ( !AuthType.NONE.equals( authType ) ) {
                    if ( info.getKeystoreFile() == null ) {
                        return resMap.setErr( "keystore 文件为空" ).getResultMap();
                    }
                    if ( StringUtils.isEmpty( info.getKeystorePassword() ) ) {
                        return resMap.setErr( "keystore 密码为空" ).getResultMap();
                    }
                }
                if ( AuthType.LDAP.equals( authType ) ) {
                    if ( StringUtils.isEmpty( info.getConnectionPwd() ) ) {
                        return resMap.setErr( "用户密码为空" ).getResultMap();
                    }
                }
                else if ( AuthType.KERBEROS.equals( authType ) ) {
                    if ( info.getKeytabFile() == null ) {
                        return resMap.setErr( "keytab 文件为空" ).getResultMap();
                    }
                    if ( info.getKrb5ConfigFile() == null ) {
                        return resMap.setErr( "krb5 文件为空" ).getResultMap();
                    }
                }

                // 往数据中枢动态添加节点
                if ( info.getEnable() ==1 ) {
                    CatalogInfo catalogInfo = hetuComponent.genAgentCatalogInfo( isEncryptPassword, info );
                    DynamicCatalogResult dynamicCatalogResult = HetuDynamicCatalogUtil.addCatalog( masterHetuInfo, catalogInfo );
                    if ( !dynamicCatalogResult.isSuccessful() ) {
                        return resMap.setErr( "注册启用节点失败,"+dynamicCatalogResult.getMessage() ).getResultMap();
                    }
                    hetuComponent.syncConfigFile( info );
                }
            }
            path = info.getDcCode();
            dcserverService.insertBean( info );
            //TOlkDcServerVo oldDcServerVo = new TOlkDcServerVo();
            //MyBeanUtils.copyBean2Bean(oldDcServerVo, oldDcServerDo);
//            new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""),
//                    HttpRequestUtil.getAllIp(request)).addLog(userInfo, info, "新增-olk节点");
            resMap.setSingleOk( info, "新增成功" );

            ObjectResp<String> retMap = apiOlkDbService.synOlkDcServer( info, userInfo.getTokenId() );

            // 更新成功，则删除已经处理存的配置文件
            File dcConfigPath = new File( HetuJdbcOperateComponent.CONFIG_PATH.concat( path ) );
            FileUtils.deleteDirectory( dcConfigPath );
            path = null;
        }
        catch ( Exception ex ) {
            resMap.setErr( "新增失败" );
            logger.error( "新增异常:", ex );
        }
        finally {
            // 删除临时生成的配置文件
            if ( path != null ) {
                File dcConfigTmpPath = new File( HetuJdbcOperateComponent.CONFIG_TMP_PATH.concat( path ) );
                try {
                    FileUtils.deleteDirectory( dcConfigTmpPath );
                }
                catch ( IOException ignore ) {

                }
            }
        }
        return resMap.getResultMap();
    }

    /*@ApiOperation(value = "修改olk节点基本信息", notes = "修改olk节点基本信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "dcCode", value = "数据中心代码", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dcName", value = "数据中心名称", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "enable", value = "是否启用 1启用 0禁用", dataType = "Integer", required = false, paramType = "query")
    })
    @RequestMapping(value = "/baseupdate", method = {RequestMethod.POST})
    @ResponseBody
    public Object baseUpdate(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        //TOlkDcServerVo updateDcServer = null;
        String path =null;
        try {

            UserDo userInfo = LoginUtil.getUser(request);

            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            if( StringUtils.isBlank( hru.getNvlPara("id"))){
                return  resMap.setErr("id不能为空").getResultMap();
            }
            TOlkDcServerDo oldDcServerDo = new TOlkDcServerDo();
            TOlkDcServerDo info = dcserverService.findById( hru.getNvlPara("id"));
            if (info == null) {
                return resMap.setErr("内容不存在").getResultMap();
            }
            MyBeanUtils.copyBeanNotNull2Bean( info, oldDcServerDo );
            new PageBeanWrapper( info, hru);

//            String errorMessage = checkAddUpdateArgument(updateDcServer, userInfo);
//            if (errorMessage != null) {
//                return resMap.setErr(errorMessage).getResultMap();
//            }

            Example exp = new Example(TOlkDatabaseDo.class);
            Example.Criteria criteria = exp.createCriteria();
            criteria.andEqualTo("dcId", oldDcServerDo.getId());
            Integer dbcnt = databaseService.findCountByExample(exp);
            if( dbcnt != null && dbcnt>0){
                if(!ComUtil.trsEmpty(oldDcServerDo.getDcCode()).equals(info.getDcCode())){
                    return resMap.setErr("元数据已生成，编码不能修改").getResultMap();
                }
            }

//            fillOldConfigFile(updateDcServer, oldDcServerDo, fileMap);
            //TOlkDcServerDo newDcServer = changeDcServerVoToDo(updateDcServer, fileMap);

            if (info.getEnable()!= null && info.getEnable() == 1) {
                // 只有节点可用时才会去判断认证文件是否完整
                String authType = info.getAuthType();
                if (!AuthType.NONE.equals(authType)) {
                    if (info.getKeystoreFile() == null) {
                        return resMap.setErr("keystore 文件为空").getResultMap();
                    }
                    if (StringUtils.isEmpty(info.getKeystorePassword())) {
                        return resMap.setErr("keystore 密码为空").getResultMap();
                    }
                }
                if (AuthType.LDAP.equals(authType)) {
                    if (StringUtils.isEmpty(info.getConnectionPwd())) {
                        return resMap.setErr("用户密码为空").getResultMap();
                    }
                } else if (AuthType.KERBEROS.equals(authType)) {
                    if (info.getKeytabFile() == null) {
                        return resMap.setErr("keytab 文件为空").getResultMap();
                    }
                    if (info.getKrb5ConfigFile() == null) {
                        return resMap.setErr("krb5 文件为空").getResultMap();
                    }
                }

                path = info.getDcCode();

                // 往数据中枢动态添加节点
                CatalogInfo catalogInfo = hetuComponent.genAgentCatalogInfo(isEncryptPassword, info);
                DynamicCatalogResult dynamicCatalogResult = HetuDynamicCatalogUtil.addCatalog(masterHetuInfo, catalogInfo);
                if (!dynamicCatalogResult.isSuccessful()) {
                    return resMap.setErr(dynamicCatalogResult.getMessage()).getResultMap();
                }
            }

            dcserverService.updateBean(info);

            TOlkDcServerVo oldDcServerVo = new TOlkDcServerVo();
            MyBeanUtils.copyBean2Bean(oldDcServerVo, oldDcServerDo);
            new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""),
                    HttpRequestUtil.getAllIp(request)).updateLog(userInfo, oldDcServerVo, info, "修改-olk节点");
            resMap.setSingleOk(info, "更新成功");

            // 更新成功，则删除已经处理的配置文件
            File dcConfigPath = new File(HetuJdbcOperateComponent.CONFIG_PATH.concat(info.getDcCode()));
            FileUtils.deleteDirectory(dcConfigPath);
        } catch (Exception ex) {
            resMap.setErr("更新失败");
            logger.error("更新异常:", ex);
        } finally {
            // 删除临时生成的配置文件
            if( path!= null) {
                File dcConfigTmpPath = new File(HetuJdbcOperateComponent.CONFIG_TMP_PATH.concat(path));
                try {
                    FileUtils.deleteDirectory(dcConfigTmpPath);
                } catch (IOException ignore) {

                }
            }
        }
        return resMap.getResultMap();
    }
     */

    @ApiOperation(value = "修改olk节点", notes = "修改olk节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "keystoreFile", value = "keystore文件", dataType = "file", required = false, paramType = "query"),
            @ApiImplicitParam(name = "keytabFile", value = "keytab文件", dataType = "file", required = false, paramType = "query"),
            @ApiImplicitParam(name = "krb5ConfigFile", value = "krb5Config文件", dataType = "file", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dcCode", value = "数据中心代码", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dcName", value = "数据中心名称", dataType = "String", required = false, paramType = "query"),
            // @ApiImplicitParam(name = "deptNo", value = "部门编号", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "dcType", value = "是否dc服务 1是 0否", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "jdbcUrl", value = "jdbc地址", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "connectionUrl", value = "olk服务地址", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "connectionUser", value = "olk用户名", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "connectionPwd", value = "olk密码", dataType = "String", required = false, paramType = "query"),
            // @ApiImplicitParam(name = "manageAccount", value = "负责帐号", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "enable", value = "是否启用 1启用 0禁用", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "authType", value = "认证类型 none kerberos ldap", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "remoteServiceName", value = "kerberos 服务名", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "encryptFlag", value = "是否对密码加密, 0不加密, 1 加密", dataType = "String", required = false, paramType = "query"),
            //@ApiImplicitParam(name = "dcPriv", value = "数据中心私钥", dataType = "String", required = false, paramType = "query"),
            //@ApiImplicitParam(name = "dcPub", value = "数据中心公钥", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "cacheTemplete", value = "处理表模板", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "cacheDbSet", value = "处理库配置", dataType = "String", required = false, paramType = "query"),
    })
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update( @RequestParam(value = "keystoreFile", required = false) MultipartFile keystoreFile,
                          @RequestParam(value = "keytabFile", required = false) MultipartFile keytabFile,
                          @RequestParam(value = "krb5ConfigFile", required = false) MultipartFile krb5ConfigFile,
                          HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        String path = null;
        try {

            UserDo userInfo = LoginUtil.getUser( request );
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            if ( StringUtils.isBlank( hru.getNvlPara( "id" ) ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }
            logger.info( "keystoreFile:{},keytabFile:{},krb5ConfigFile:{},para:{}", keystoreFile != null, keytabFile != null, krb5ConfigFile != null, hru.getAllParaData() );

            TOlkDcServerDo info = dcserverService.findById( hru.getNvlPara( "id" ) );
            if ( info == null ) {
                return resMap.setErr( "内容不存在" ).getResultMap();
            }

            TOlkDcServerDo old = new TOlkDcServerDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, old );
            new PageBeanWrapper( info, hru );
            info.setDcPriv( old.getDcPriv() );
            info.setDcPub( old.getDcPriv() );
            info.setNodePartyId( old.getNodePartyId() );
            if ( StringUtils.isBlank( info.getNodePartyId() ) ) {
                FNodePartyDo nodePartyDo = nodePartyService.findFirst();
                info.setNodePartyId( nodePartyDo.getId() );
            }
            if ( info.getDcType() == null || info.getDcType() != 0 ) {
                info.setDcType( 1 );
            }
            info.setSynFlag( 0 );

            String errorMessage = checkAddUpdateArgument( info, userInfo );
            if ( errorMessage != null ) {
                return resMap.setErr( errorMessage ).getResultMap();
            }

            Example exp = new Example( TOlkDatabaseDo.class );
            Example.Criteria criteria = exp.createCriteria();

            criteria.andEqualTo( "dcId", info.getId() ).andEqualTo( "dbsourceId", "dchetu" );
            Integer dbcnt = databaseService.findCountByExample( exp );
            if ( dbcnt != null && dbcnt > 0 ) {
                if ( !ComUtil.trsEmpty( old.getDcCode() ).equals( info.getDcCode() ) ) {
                    return resMap.setErr( "元数据已生成，编码不能修改" ).getResultMap();
                }
            }

//            Map<String, byte[]> fileMap = new HashMap<>(files.length);
//            for (MultipartFile file : files) {
//                fileMap.put(file.getOriginalFilename(), file.getBytes());
//            }

            fillOldConfigFile( info, keystoreFile, keytabFile, krb5ConfigFile );
            //TOlkDcServerDo newDcServer = changeDcServerVoToDo(updateDcServer, fileMap);

            if ( info.getEnable() != null && info.getEnable() == 1 ) {
                // 只有节点可用时才会去判断认证文件是否完整
                String authType = info.getAuthType();
                if ( !AuthType.NONE.equals( authType ) ) {
                    if ( info.getKeystoreFile() == null ) {
                        return resMap.setErr( "keystore 文件为空" ).getResultMap();
                    }
                    if ( StringUtils.isEmpty( info.getKeystorePassword() ) ) {
                        return resMap.setErr( "keystore 密码为空" ).getResultMap();
                    }
                }
                if ( AuthType.LDAP.equals( authType ) ) {
                    if ( StringUtils.isEmpty( info.getConnectionPwd() ) ) {
                        return resMap.setErr( "用户密码为空" ).getResultMap();
                    }
                }
                else if ( AuthType.KERBEROS.equals( authType ) ) {
                    if ( info.getKeytabFile() == null ) {
                        return resMap.setErr( "keytab 文件为空" ).getResultMap();
                    }
                    if ( info.getKrb5ConfigFile() == null ) {
                        return resMap.setErr( "krb5 文件为空" ).getResultMap();
                    }
                }

                if ( info.getEnable() != null && info.getEnable() ==1 ) {
                    // 往数据中枢动态添加节点
                    CatalogInfo catalogInfo = hetuComponent.genAgentCatalogInfo( isEncryptPassword, info );
                    DynamicCatalogResult dynamicCatalogResult = HetuDynamicCatalogUtil.addCatalog( masterHetuInfo, catalogInfo );
                    if ( !dynamicCatalogResult.isSuccessful() ) {
                        return resMap.setErr( "注册启用节点失败,"+dynamicCatalogResult.getMessage() ).getResultMap();
                    }
                    hetuComponent.syncConfigFile( info );
                }
            }
            path = info.getDcCode();
            dcserverService.updateBean( info );


            //TOlkDcServerVo oldDcServerVo = new TOlkDcServerVo();
            //MyBeanUtils.copyBean2Bean(oldDcServerVo, oldDcServerDo);
//            new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""),
//                    HttpRequestUtil.getAllIp(request)).updateLog(userInfo, old, info, "修改-olk节点");
            resMap.setSingleOk( info, "更新成功" );

            // 更新成功，则删除已经处理的配置文件
            File dcConfigPath = new File( HetuJdbcOperateComponent.CONFIG_PATH.concat( path ) );
            FileUtils.deleteDirectory( dcConfigPath );
            path = null;

            ObjectResp<String> retMap = apiOlkDbService.synOlkDcServer( info, userInfo.getTokenId() );
        }
        catch ( Exception ex ) {
            resMap.setErr( "更新失败" );
            logger.error( "更新异常:", ex );
        }
        finally {
            // 删除临时生成的配置文件
            if ( path != null ) {
                File dcConfigTmpPath = new File( HetuJdbcOperateComponent.CONFIG_TMP_PATH.concat( path ) );
                try {
                    FileUtils.deleteDirectory( dcConfigTmpPath );
                }
                catch ( IOException ignore ) {

                }
            }
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "注册关联节点", notes = "注册关联节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dcNo", value = "节点编号", dataType = "String", required = true, paramType = "form"),
            @ApiImplicitParam(name = "username", value = "账号", dataType = "String", required = true, paramType = "form")
    })
    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    @ResponseBody
    public Object register( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo ud = LoginUtil.getUser( request );
            if ( ud == null || StringUtils.isBlank( ud.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }

            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            logger.debug( "{}", hru.getAllParaData() );

            String dcNo = hru.getNvlPara( "dcNo" );
            String username = hru.getNvlPara( "username" );
            if ( StringUtils.isNotBlank( dcNo ) ) {
                return resMap.setErr( "编号不能为空" ).getResultMap();
            }
            if ( StringUtils.isNotBlank( username ) ) {
                return resMap.setErr( "账号不能为空" ).getResultMap();
            }

            TOlkDcServerDo dcsTmp = new TOlkDcServerDo();
            dcsTmp.setDcCode( dcNo );

            List<TOlkDcServerDo> list = dcserverService.find( dcsTmp );

            if ( list.size() == 0 ) {
                return resMap.setErr( "编号不存在" ).getResultMap();
            }
            else if ( list.size() > 1 ) {
                return resMap.setErr( "编号对应数据不正确" ).getResultMap();
            }
            TOlkDcServerDo info = list.get( 0 );

            if ( StringUtils.isNotBlank( info.getClientNo() ) ) {
                return resMap.setErr( "节点已注册" ).getResultMap();
            }

            if ( info.getEnable() == null || info.getEnable() != 1 ) {
                return resMap.setErr( "节点未启用" ).getResultMap();
            }

            TOlkDcServerDo old = new TOlkDcServerDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, old );

            info.setClientNo( ComUtil.genId() );

            dcserverService.updateBean( info );

//            new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""),
//                    HttpRequestUtil.getAllIp(request)).updateLog(ud, old, info, "修改-注册节点");

            resMap.setSingleOk( info, "保存成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "olk节点内容", notes = "olk节点内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "节点id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    @ResponseBody
    public Object info( String id ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if ( StringUtils.isBlank( id ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }
            TOlkDcServerDo dcServerDo = dcserverService.findById( id );
            //TOlkDcServerVo dcServerVo = new TOlkDcServerVo();
            //MyBeanUtils.copyBean2Bean(dcServerVo, dcServerDo);
            resMap.setSingleOk( dcServerDo, "成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "查询失败" );
            logger.error( "查询异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除olk节点", notes = "删除olk节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "olk节点 id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    @ResponseBody
    public Object delete( String id, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if ( StringUtils.isBlank( id ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }
            UserDo user = LoginUtil.getUser( request );

            List<String> split = Arrays.asList( id.split( "(,|\\s)+" ) );

            Example exp = new Example( TOlkDcServerDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "id", split );
            List<TOlkDcServerDo> list = dcserverService.findByExample( exp );

            exp = new Example( TOlkDatabaseDo.class );
            criteria = exp.createCriteria();
            criteria.andIn( "dcId", split );
            Integer cnt = databaseService.findCountByExample( exp );
            if ( cnt != null && cnt > 0 ) {
                return resMap.setErr( "节点已被目录使用不能删除" ).getResultMap();
            }

            for ( TOlkDcServerDo dcServerDo : list ) {
                DynamicCatalogResult dynamicCatalogResult = HetuDynamicCatalogUtil.deleteCatalog( masterHetuInfo, dcServerDo.getDcCode() );
                if ( !dynamicCatalogResult.isSuccessful() ) {
                    return resMap.setErr( dynamicCatalogResult.getMessage() ).getResultMap();
                }
            }
            dcserverService.deleteWithOther( list );

//            String times = String.valueOf(System.currentTimeMillis());
//            for (TOlkDcServerDo info : list) {
//                try {
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""),
//                            HttpRequestUtil.getAllIp(request)).delLog(user, info, "删除-olk节点" + times);
//                } catch (Exception e1) {
//                    resMap.setErr("删除失败");
//                    logger.error("删除异常:", e1);
//                }
//            }
            resMap.setOk( "删除成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "删除失败" );
            logger.error( "删除异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取节点列表", notes = "获取节点列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "qryCond", value = "模糊条件", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "currentPage", value = "页数", dataType = "Integer", required = false, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", dataType = "Integer", required = false, paramType = "query")
    })
    @RequestMapping(value = "/page", method = {RequestMethod.GET})
    @ResponseBody
    public Object page( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            TOlkDcServerDo modelVo = new TOlkDcServerDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo, hru );
            modelVo.setQryCond( ComUtil.chgLikeStr( modelVo.getQryCond() ) );
            modelVo.setDcName( ComUtil.chgLikeStr( modelVo.getDcName() ) );

            long findCnt = dcserverService.findBeanCnt( modelVo );
            modelVo.genPage( findCnt );

            List<TOlkDcServerDo> list = dcserverService.findBeanList( modelVo );
            for ( TOlkDcServerDo dcDo : list ) {
                dcDo.setKrb5ConfigFile( null );
                dcDo.setKeytabFile( null );
                dcDo.setKeystoreFile( null );
                dcDo.setDcPriv( null );
                dcDo.setDcPub( null );
            }
            resMap.setPageInfo( modelVo.getPageSize(), modelVo.getCurrentPage() );
            resMap.setOk( findCnt, list, "获取节点列表成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取节点列表失败" );
            logger.error( "获取节点列表失败:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "获取节点选择列表", notes = "获取节点选择列表")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/dcoption", method = {RequestMethod.GET})
    @ResponseBody
    public Object dcOption() {
        ResponeMap resMap = this.genResponeMap();
        try {
            TOlkDcServerDo modelVo = new TOlkDcServerDo();
            modelVo.setEnable( 1 );
            List<TOlkDcServerDo> list = dcserverService.findBeanList( modelVo );
            List<Object> collect = list.stream().map( x -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put( "id", x.getId() );
                map.put( "dcCode", x.getDcCode() );
                map.put( "dcName", x.getDcName() );
//                map.put("deptNo", x.getDeptNo());
//                map.put("deptName", x.getDeptName());
                map.put( "authType", x.getAuthType() );
                return map;
            } ).collect( Collectors.toList() );
            resMap.setSingleOk( collect, "获取节点选择列表成功" );
            //resMap.setSingleOk(changeDcServerDosToVos(list), "获取节点选择列表成功");
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取节点选择列表失败" );
            logger.error( "获取节点选择列表失败:", ex );
        }
        return resMap.getResultMap();
    }

    /*@ApiOperation(value = "获取部门列表", notes = "获取部门列表")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/deptoption", method = {RequestMethod.GET})
    @ResponseBody
    public Object deptOption(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser(request);

            ILoginMenu menu = menuUtil.getMenuCls();
            List<AuthDepartmentBean> deptList = menu.allDepartmentList(setOp, user);
//            AuthDepartmentBean rootdept = deptList.stream()
//                    .filter(x -> StringUtils.isBlank(x.getParentNo()))
//                    .findFirst()
//                    .get();
//            List<AuthDepartmentBean> list = deptList.stream()
//                    .filter(x -> rootdept.getDeptNo().equals(x.getParentNo()))
//                    .collect(Collectors.toList());
//            list = deptList;

            List<AuthDepartmentBean> retList = new ArrayList<>();
            for (AuthDepartmentBean authDepartmentBean : deptList) {
                if( authDepartmentBean.getParentNo() == null){
                    List<AuthDepartmentBean> subList = makeDeptTree(deptList, authDepartmentBean.getDeptNo());
                    if(subList != null && subList.size()>0){
                        authDepartmentBean.setChildren( subList );
                    }
                    retList.add( authDepartmentBean );
                }
            }
            resMap.setSingleOk(retList, "获取部门列表");
        } catch (Exception ex) {
            resMap.setErr("获取部门列表失败");
            logger.error("获取部门列表失败,{}:",menuUtil.getAuthclass(), ex);
        }
        return resMap.getResultMap();
    }
    */

    private List<AuthDepartmentBean> makeDeptTree( List<AuthDepartmentBean> list, String pcode ) {
        List<AuthDepartmentBean> retList = new ArrayList<>();
        for ( AuthDepartmentBean authDepartmentBean : list ) {
            if ( pcode.equals( authDepartmentBean.getParentNo() ) ) {
                List<AuthDepartmentBean> subList = makeDeptTree( list, authDepartmentBean.getDeptNo() );
                if ( subList != null && subList.size() > 0 ) {
                    authDepartmentBean.setChildren( subList );
                }
                retList.add( authDepartmentBean );
            }
        }
        return retList;
    }

    /**
     * 检验参数，返回错误信息
     *
     * @param info 节点信息
     * @return 错误信息，没有错误为空
     */
    private String checkAddUpdateArgument( TOlkDcServerDo info, UserDo userInfo ) throws Exception {
        if ( StringUtils.isBlank( info.getDcName() ) ) {
            return "名称不能为空";
        }

        if ( StringUtils.isBlank( info.getDcCode() ) ) {
            return "编码不能为空";
        }
        if ( !info.getDcCode().matches( DC_CODE_PATTERN ) ) {
            return "编码只能由字母开头包含字母和数字的3到15位字符";
        }
        if ( info.getDcType() == null ) {
            info.setDcType( 1 );
        }
//        TOlkDcServerDo center = dcserverService.findCenter();
//        if( info.getDcType() ==0 ){ // 中心节点只能有一个
//            if( center!= null ){
//                return "中心节点已存在";
//            }
//            info.setEnable(  1 );
//            info.setDcCode( "dc0" );
//            info.setDcName( "中心节点" );
//        }
//        else{
//            if( center == null ){
//                return "请先登记中心节点";
//            }
//        }
//        if (StringUtils.isBlank(info.getDeptNo())) {
//            return "节点单位不能为空";
//        }
        if ( info.getEnable() == null || info.getEnable() != 1 ) {
            info.setEnable( 0 );
        }

        if ( info.getEnable() == 1 ) {
            if ( StringUtils.isBlank( info.getConnectionUrl() ) ) {
                return "节点地址不能为空";
            }
            info.setConnectionUrl( info.getConnectionUrl().trim() );
        }

        TOlkDcServerDo searchInfo = new TOlkDcServerDo();
        MyBeanUtils.copyBean2Bean( searchInfo, info );
        long sameCodeCount = dcserverService.findSameCodeCount( searchInfo );
        if ( sameCodeCount > 0 ) {
            return "编码已使用";
        }

        long sameNameCount = dcserverService.findSameNameCount( searchInfo );
        if ( sameNameCount > 0 ) {
            return "名称已使用";
        }

//        long sameManageUserCount = dcserverService.findSameManageUserCount(searchInfo);
//        if(sameManageUserCount > 0){
//            return "节点管理员已使用";
//        }

        if ( StringUtils.isNotBlank( searchInfo.getConnectionUrl() ) ) {
            long sameUrlCount = dcserverService.findSameUrlCount( searchInfo );
            if ( sameUrlCount > 0 ) {
                return "节点地址已使用";
            }
        }

//        long sameDeptCount = dcserverService.findSameDeptCount(searchInfo);
//        if(sameDeptCount > 0){
//            return "节点单位已使用";
//        }

        LoginUtil.setBeanInsertUserInfo( info, userInfo );

        /*ILoginMenu menu = menuUtil.getMenuCls();

        List<AuthDepartmentBean> deptList = menu.allDepartmentList(setOp, userInfo);
        logger.debug( "{}", JsonUtil.toJson( deptList ) );
//        AuthDepartmentBean rootdept = deptList.stream()
//                .filter(x -> StringUtils.isBlank(x.getParentNo()))
//                .findFirst().get();
        Map<String, AuthDepartmentBean> deptMap = deptList.stream()
                //.filter(x -> rootdept.getDeptNo().equals(x.getParentNo()))
                .collect(Collectors.toMap(x -> x.getDeptNo(), x -> x));
        AuthDepartmentBean dept = deptMap.get(info.getDeptNo());

        if(dept == null){
            return "节点单位不存在";
        }
        info.setDeptName(dept.getDeptName());*/

//        if( StringUtils.isNotBlank( info.getManageAccount() ) ) {
//            List<AuthUserBean> list = menu.systemUserList(setOp, setOp.readParaSetValue(Constants.syspara_SystemCode, ""), userInfo, dept.getDeptNo());
//            logger.debug("{}",JsonUtil.toJson(list));
//            AuthUserBean au = null;
//            for (AuthUserBean authUserBean : list) {
//                if (info.getManageAccount().equals(authUserBean.getUsername())) {
//                    au = authUserBean;
//                    break;
//                }
//            }
//            if( au == null){
//                return "管理账号不存在";
//            }
//        }

        return null;
    }

    /**
     * 转换节点对象，保存认证文件
     * @param dcServerVo 节点参数
     * @param fileMap 认证文件
     * @return 节点配置
     */
//    private TOlkDcServerDo changeDcServerVoToDo(TOlkDcServerVo dcServerVo, Map<String, byte[]> fileMap) throws Exception {
//        TOlkDcServerDo dcServerDo = new TOlkDcServerDo();
//        MyBeanUtils.copyBean2Bean(dcServerDo, dcServerVo);
//
//        String authType = dcServerVo.getAuthType();
//        if (!AuthType.NONE.equals(authType)) {
//            dcServerDo.setKeystoreFile(fileMap.get(dcServerVo.getKeystoreFileName()));
//        }
//        if (AuthType.KERBEROS.equals(authType)) {
//            dcServerDo.setKeytabFile(fileMap.get(dcServerVo.getKeytabFileName()));
//            dcServerDo.setKrb5ConfigFile(fileMap.get(dcServerVo.getKrb5ConfigFileName()));
//        }
//        return dcServerDo;
//    }

    /**
     * 将 dos 列表转换为 vos 列表
     * @param dcServerDos dos 列表
     * @return vos 列表
     */
//    private List<TOlkDcServerVo> changeDcServerDosToVos(List<TOlkDcServerDo> dcServerDos) throws Exception {
//        List<TOlkDcServerVo> dcServerVos = new ArrayList<>();
//        for (TOlkDcServerDo dcServerDo : dcServerDos) {
//            TOlkDcServerVo dcServerVo = new TOlkDcServerVo();
//            MyBeanUtils.copyBean2Bean(dcServerVo, dcServerDo);
//            dcServerVos.add(dcServerVo);
//        }
//        return dcServerVos;
//    }

    /**
     * 更新时，前端不传文件表示不更新，将旧的配置文件填充到新的配置中
     * 有其他参数在后台维护，也需要在这边进行填充
     * @param updateDcServer
     * @param oldDcServerDo
     * @param fileMap
     */
//    private void fillOldConfigFile(TOlkDcServerVo updateDcServer, TOlkDcServerDo oldDcServerDo,
//                                   Map<String, byte[]> fileMap) {
//        String authType = updateDcServer.getAuthType();
//        if (!AuthType.NONE.equals(authType)) {
//            if (fileMap.get(updateDcServer.getKeystoreFileName()) == null) {
//                fileMap.put(oldDcServerDo.getKeystoreFileName(), oldDcServerDo.getKeystoreFile());
//                updateDcServer.setKeystoreFileName(oldDcServerDo.getKeystoreFileName());
//            }
//        }
//        if (AuthType.KERBEROS.equals(authType)){
//            if (fileMap.get(updateDcServer.getKeytabFileName()) == null) {
//                fileMap.put(oldDcServerDo.getKeytabFileName(), oldDcServerDo.getKeytabFile());
//                updateDcServer.setKeytabFileName(oldDcServerDo.getKeytabFileName());
//            }
//            if (fileMap.get(updateDcServer.getKrb5ConfigFileName()) == null) {
//                fileMap.put(oldDcServerDo.getKrb5ConfigFileName(), oldDcServerDo.getKrb5ConfigFile());
//                updateDcServer.setKrb5ConfigFileName(oldDcServerDo.getKrb5ConfigFileName());
//            }
//        }
//    }

    /**
     * 更新时，前端不传文件表示不更新，将旧的配置文件填充到新的配置中
     * 有其他参数在后台维护，也需要在这边进行填充
     *
     * @param updateDcServer
     */
    private void fillOldConfigFile( TOlkDcServerDo updateDcServer, MultipartFile keystoreFile, MultipartFile keytabFile, MultipartFile krb5ConfigFile ) throws IOException {
        String authType = updateDcServer.getAuthType();
        if ( !AuthType.NONE.equals( authType ) ) {
            if ( keystoreFile != null ) {
                updateDcServer.setKeystoreFileName( keystoreFile.getOriginalFilename() );
                updateDcServer.setKeystoreFile( keystoreFile.getBytes() );
            }
        }
        if ( AuthType.KERBEROS.equals( authType ) ) {
            if ( keytabFile != null ) {
                updateDcServer.setKeytabFileName( keytabFile.getOriginalFilename() );
                updateDcServer.setKeytabFile( keytabFile.getBytes() );
            }
            if ( krb5ConfigFile != null ) {
                updateDcServer.setKrb5ConfigFileName( krb5ConfigFile.getOriginalFilename() );
                updateDcServer.setKrb5ConfigFile( krb5ConfigFile.getBytes() );
            }
        }
    }
}
