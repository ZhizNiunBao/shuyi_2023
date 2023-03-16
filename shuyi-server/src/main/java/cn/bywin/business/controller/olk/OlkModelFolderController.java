package cn.bywin.business.controller.olk;


import cn.bywin.business.bean.olk.TOlkModelFolderDo;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.common.util.PageBeanWrapper;
import cn.bywin.business.service.olk.OlkModelFolderService;
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
@Api(tags = "olk模型文件夹-olkmodelfolder")
@RequestMapping("/olkmodelfolder")
public class OlkModelFolderController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger( this.getClass() );

//    @Autowired
//    private BydbModelService modelService;

    @Autowired
    private OlkModelFolderService folderService;

//    @Autowired
//    private BydbDcServerService dcserverService;

    @ApiOperation(value = "新增可信模型文件夹", notes = "新增可信模型文件夹")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "info", value = "可信模型文件夹", dataType = "TOlkModelFolderDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    public Object add( @RequestBody TOlkModelFolderDo info, HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser( request );
            if ( user == null || StringUtils.isBlank( user.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }
//            if( StringUtils.isBlank( user.getOrgNo() ) ){
//                return resMap.setErr("用户信息不完整").getResultMap();
//            }

//            TBydbDcServerDo dcTmp = new TBydbDcServerDo();
//            dcTmp.setManageAccount( user.getUserName() );
//            List<TBydbDcServerDo> dcList = dcserverService.find(dcTmp);
//            if( dcList.size() == 0 ){
//                return  resMap.setErr("用户未关联节点").getResultMap();
//            }
//            else if( dcList.size() > 1 ){
//                String dcName = dcList.stream().map(x -> x.getDcName() + "(" + x.getDcCode() + ")").collect(Collectors.joining(","));
//                logger.error( "用户{},关联多节点:{}",user.getUserName(), dcName );
//                resMap.setDebugeInfo("关联多节点:"+ dcName );
//                return  resMap.setErr("用户关联多节点").getResultMap();
//            }
//            TBydbDcServerDo dcDo = dcList.get(0);

            if ( StringUtils.isBlank( info.getFolderName() ) ) {
                return resMap.setErr( "名称不能为空" ).getResultMap();
            }

            TOlkModelFolderDo parent = null;
            if ( StringUtils.isBlank( info.getPid() ) ) {
                info.setPid( null );
            }
            else{
                parent = folderService.findById( info.getPid() );

                if ( parent == null ) {
                    return resMap.setErr( "上级不存在" ).getResultMap();
                }
//                if (StringUtils.isBlank(parent.getRelCode())) {
//                    return resMap.setErr("请先重新保存上级组织机构").getResultMap();
//                }
                if ( !user.getUserName().equals( parent.getUserAccount() ) ) {
                    return resMap.setErr( "你无权在此分类下操作分类" ).getResultMap();
                }
            }
//            info.setPid(null);
//
            String preCode = "";
            List<TOlkModelFolderDo> pList = null;
            if ( StringUtils.isBlank( info.getPid() ) ) {
//                TOlkModelFolderDo temp = new TOlkModelFolderDo();
//                //temp.setAdminId( userAdmin.getId() );
//                temp.setUserAccount( user.getUserName() );
//                temp.setPid( "#NULL#" );
//                if ( StringUtils.isBlank( info.getRelCode() ) ) {
//                    pList = folderService.findBeanList( temp );
//                    preCode = "A";
//                }
            }
            else {
//                if (StringUtils.isBlank(info.getRelCode())) {
//                    info.setPid(parent.getId());
//                    TOlkModelFolderDo temp = new TOlkModelFolderDo();
//                    temp.setPid(parent.getId());
//                    pList = folderService.findBeanList(temp);
//                    preCode = parent.getRelCode();
//                }
            }
            //if( StringUtils.isBlank( info.getRelCode() )) {
//            String strCode = "";
//            if (pList.size() > 0) {
//                int code = 1;
//                while (true) {
//                    strCode = String.format("%s%03d", preCode, code);
//                    boolean bfound = false;
//                    for ( TOlkModelFolderDo sysDepartmentDo : pList) {
//                        if (strCode.equals(sysDepartmentDo.getRelCode())) {
//                            bfound = true;
//                            break;
//                        }
//                    }
//                    if (!bfound) {
//                        break;
//                    }
//                    code++;
//                }
//
//            } else {
//                strCode = preCode + "001";
//            }
//
//            if (StringUtils.isBlank(info.getRelCode())) {
//                info.setRelCode(strCode);
//            }
            //}

            //info.setAdminId( userAdmin.getId() );
            info.setUserAccount( user.getUserName() );
            info.setUserAccountName( user.getChnName() );
            info.setUserDeptNo( user.getOrgNo() );
            info.setUserDeptNa( user.getOrgName() );
            info.setId( ComUtil.genId() );
            LoginUtil.setBeanInsertUserInfo( info, user );

            TOlkModelFolderDo same = new TOlkModelFolderDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, same );
            if ( StringUtils.isBlank( same.getPid() ) )
                same.setPid( "#NULL#" );
            final long sameNameCount = folderService.findSameNameCount( same );
            if ( sameNameCount > 0 ) {
                return resMap.setErr( "名称已使用" ).getResultMap();
            }

            folderService.insertBean( info );
            //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).addLog(user, info, "新增-可信模型文件夹");
            resMap.setSingleOk( info, "保存成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "修改可信模型文件夹", notes = "修改可信模型文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "String", example = "c2e9e8a5ce0e4a1b8be389dd1a7d5871", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pid", value = "上级id", dataType = "String", example = "c2e9e8a5ce0e4a1b8be389dd1a7d5871", required = false, paramType = "query"),
            @ApiImplicitParam(name = "folderName", value = "文件夹名称", dataType = "String", example = "文件夹", required = false, paramType = "query")
    })
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser( request );
            if ( user == null || StringUtils.isBlank( user.getUserId() ) ) {
                return resMap.setErr( "请先登录" ).getResultMap();
            }

            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            logger.debug( "{}", hru.getAllParaData() );
            TOlkModelFolderDo info = folderService.findById( hru.getNvlPara( "id" ) );

            if ( info == null ) {
                return resMap.setErr( "文件夹不存在" ).getResultMap();
            }

            TOlkModelFolderDo old = new TOlkModelFolderDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, old );

            new PageBeanWrapper( info, hru, "" );

            if ( StringUtils.isBlank( info.getFolderName() ) ) {
                return resMap.setErr( "名称不能为空" ).getResultMap();
            }

            info.setRelCode( old.getRelCode() );
            if ( StringUtils.isBlank( old.getUserAccount() ) ) {
                info.setUserAccount( user.getUserName() );
                info.setUserAccountName( user.getChnName() );
                info.setUserDeptNo( user.getOrgNo() );
                info.setUserDeptNa( user.getOrgName() );
            }
            else {
                info.setUserAccount( old.getUserAccount() );
                info.setUserAccountName( old.getUserAccountName() );
                info.setUserDeptNo( old.getUserDeptNo() );
                info.setUserDeptNa( old.getUserDeptNa() );
            }

            //info.setPid( old.getPid() );
            TOlkModelFolderDo parent = null;
            if ( StringUtils.isBlank( info.getPid() ) ) { //改成多级
                info.setPid( null );
                //info.setRelCode(null);
            }
            else {
                parent = folderService.findById( info.getPid() );

                if ( parent == null ) {
                    return resMap.setErr( "上级不存在" ).getResultMap();
                }
                if( parent.getId().equals( info.getId() ) ){
                    return resMap.setErr( "不能将自己设置为上级" ).getResultMap();
                }
                String pid = parent.getPid();
                while(true){
                    if( info.getId().equals(  pid )){
                        return resMap.setErr( "不能将自己的下级设置为上级" ).getResultMap();
                    }
                    TOlkModelFolderDo pp = folderService.findById( pid );
                    if( pp != null ){
                        pid = pp.getPid();
                    }
                    else{
                        break;
                    }
                }
//                if (StringUtils.isBlank(parent.getRelCode())) {
//                    return resMap.setErr("请先重新保存上级组织机构").getResultMap();
//                }
                if ( !user.getUserName().equals( parent.getUserAccount() ) ) {
                    return resMap.setErr( "你无权在此分类下操作分类" ).getResultMap();
                }
            }

//            if( StringUtils.isBlank( info.getAdminId() )) {
//                info.setAdminId(userAdmin.getId());
//            }
//            else{
//                if( !StringUtils.equals( info.getAdminId(),userAdmin.getId()) ){
//                    return resMap.setErr("你不能操作此类型").getResultMap();
//                }
//            }
            //temp.setUserAccount( user.getUserName() );
            if ( StringUtils.isNotBlank( old.getUserAccount() ) &&
                    !old.getUserAccount().equals( user.getUserName() ) ) {
                return resMap.setErr( "你不能操作此类型" ).getResultMap();
            }

            TOlkModelFolderDo same = new TOlkModelFolderDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, same );
            if ( StringUtils.isBlank( same.getPid() ) )
                same.setPid( "#NULL#" );
            final long sameNameCount = folderService.findSameNameCount( same );
            if ( sameNameCount > 0 ) {
                return resMap.setErr( "名称已使用" ).getResultMap();
            }

            String preCode = "";
            List<TOlkModelFolderDo> pList = null;
            if ( StringUtils.isBlank( info.getPid() ) ) {
//                info.setPid( null );
//                TOlkModelFolderDo temp = new TOlkModelFolderDo();
//                //temp.setAdminId( userAdmin.getId() );
//                temp.setUserAccount( user.getUserName() );
//                temp.setPid( "#NULL#" );
//                if ( StringUtils.isBlank( info.getRelCode() ) ) {
//                    pList = folderService.findBeanList( temp );
//                    preCode = "A";
//                }
            }
            else {

//                if (StringUtils.isBlank(info.getRelCode())) {
//                    info.setPid(parent.getId());
//                    TOlkModelFolderDo temp = new TOlkModelFolderDo();
//                    temp.setPid(parent.getId());
//                    pList = folderService.findBeanList(temp);
//                    preCode = parent.getRelCode();
//                }

            }
//            if (StringUtils.isBlank(info.getRelCode())) {
//                String strCode = "";
//                if (pList.size() > 0) {
//                    int code = 1;
//                    while (true) {
//                        strCode = String.format("%s%03d", preCode, code);
//                        boolean bfound = false;
//                        for ( TOlkModelFolderDo sysDepartmentDo : pList) {
//                            if (strCode.equals(sysDepartmentDo.getRelCode())) {
//                                bfound = true;
//                                break;
//                            }
//                        }
//                        if (!bfound) {
//                            break;
//                        }
//                        code++;
//                    }
//
//                } else {
//                    strCode = preCode + "001";
//                }
//
//                if (StringUtils.isBlank(info.getRelCode())) {
//                    info.setRelCode(strCode);
//                }
//            }

            folderService.updateBean( info );

            //new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(user, old, info, "修改-可信模型文件夹");

            resMap.setSingleOk( info, "保存成功" );

        }
        catch ( Exception ex ) {
            resMap.setErr( "保存失败" );
            logger.error( "保存异常:", ex );
        }
        return resMap.getResultMap();
    }


    @ApiOperation(value = "可信模型文件夹内容", notes = "可信模型文件夹内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "可信模型文件夹 id", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/info", method = {RequestMethod.GET})
    @ResponseBody
    public Object info( String id ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if ( StringUtils.isBlank( id ) ) {
                return resMap.setErr( "id不能为空" ).getResultMap();
            }
            TOlkModelFolderDo modelVo = folderService.findById( id );
            if( modelVo != null) {
                resMap.setSingleOk( modelVo, "成功" );
            }
            else{
                resMap.setErr( "对象不存在" );
            }
        }
        catch ( Exception ex ) {
            resMap.setErr( "查询失败" );
            logger.error( "查询异常:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "删除可信模型文件夹", notes = "删除可信模型文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "可信模型文件夹 id", dataType = "String", required = true, paramType = "query")
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
            if ( !id.matches( "^[a-zA-Z0-9\\-_,]*$" ) ) {
                return resMap.setErr( "id有非法字符" ).getResultMap();
            }
            List<String> split = Arrays.asList( id.split( "," ) );

//            Example exp = new Example(TBydbAdminUserDo.class);
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andIn("adminId", split);
//            int cnt = objectService.findCountByExample(exp);
//            if( cnt >0 ){
//                return resMap.setErr("可信模型文件夹有下级对象不能删除").getResultMap();
//            }

//            Example exp = new Example(TBydbModelDo.class);
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andIn("folderId", split);
//            int cnt = modelService.findCountByExample(exp);
//            if (cnt > 0) {
//                return resMap.setErr("可信模型文件夹已被使用不能删除").getResultMap();
//            }

            Example exp = new Example( TOlkModelFolderDo.class );
            Example.Criteria criteria = exp.createCriteria();
            criteria.andIn( "pid", split );
            criteria.andNotIn( "id", split );
            int cnt = folderService.findCountByExample( exp );
            if ( cnt > 0 ) {
                return resMap.setErr( "可信模型文件夹下级不能删除" ).getResultMap();
            }

            exp = new Example( TOlkModelFolderDo.class );
            criteria = exp.createCriteria();
            criteria.andIn( "id", split );
            List<TOlkModelFolderDo> list = folderService.findByExample( exp );
            List<String> collect = list.stream().map( x -> x.getId() ).collect( Collectors.toList() );
            if ( collect.size() != split.size() ) {
                return resMap.setErr( "有可信模型文件夹不存在" ).getResultMap();
            }
            folderService.deleteByIds( collect );

//            String times = String.valueOf(System.currentTimeMillis());
//            for ( TOlkModelFolderDo info : list) {
//                try {
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).delLog(user, info, "删除-可信模型文件夹" + times);
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

    @ApiOperation(value = "获取可信模型文件夹列表", notes = "获取可信模型文件夹列表")
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
            TOlkModelFolderDo modelVo = new TOlkModelFolderDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo, hru );
            modelVo.setQryCond( ComUtil.chgLikeStr( modelVo.getQryCond() ) );
            modelVo.setFolderName( ComUtil.chgLikeStr( modelVo.getFolderName() ) );
            if ( StringUtils.isBlank( modelVo.getUserAccount() ) ) {
                UserDo user = LoginUtil.getUser( request );
//                TBydbDcServerDo dcTmp = new TBydbDcServerDo();
//                dcTmp.setManageAccount(user.getUserName());
//                List<TBydbDcServerDo> dcList = dcserverService.find(dcTmp);
//                if (dcList.size() == 0) {
//                    return resMap.setErr("用户未关联节点").getResultMap();
//                } else if (dcList.size() > 1) {
//                    String dcName = dcList.stream().map(x -> x.getDcName() + "(" + x.getDcCode() + ")").collect(Collectors.joining(","));
//                    logger.error("用户{},关联多节点:{}", user.getUserName(), dcName);
//                    resMap.setDebugeInfo("关联多节点:" + dcName);
//                    return resMap.setErr("用户关联多节点").getResultMap();
//                }
//                TBydbDcServerDo dcDo = dcList.get(0);
//                modelVo.setDcId(dcDo.getId());
                modelVo.setUserAccount( user.getUserName() );
            }
            logger.debug( "id:{},info:{}", modelVo.getId(), modelVo );
//            if(StringUtils.isBlank( modelVo.getDcId() ) ){
//                UserDo user = LoginUtil.getUser(request);
//                TBydbDcServerDo dcTmp = new TBydbDcServerDo();
//                dcTmp.setManageAccount( user.getUserName() );
//                List<TBydbDcServerDo> dcList = dcserverService.find(dcTmp);
//                if( dcList.size() == 0 ){
//                    return  resMap.setErr("用户未关联节点");
//                }
//                else if( dcList.size() > 1 ){
//                    String dcName = dcList.stream().map(x -> x.getDcName() + "(" + x.getDcCode() + ")").collect(Collectors.joining(","));
//                    logger.error( "用户{},关联多节点:{}",user.getUserName(), dcName );
//                    resMap.setDebugeInfo("关联多节点:"+ dcName );
//                    return  resMap.setErr("用户关联多节点");
//                }
//                modelVo.setDcId( dcList.get(0).getId() );
//            }

            long findCnt = folderService.findBeanCnt( modelVo );
            //modelVo.genPage();
            modelVo.genPage( findCnt );

            List<TOlkModelFolderDo> list = folderService.findBeanList( modelVo );

            resMap.setPageInfo( modelVo.getPageSize(), modelVo.getCurrentPage() );
            resMap.setOk( findCnt, list, "获取可信模型文件夹列表成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取可信模型文件夹列表失败" );
            logger.error( "获取可信模型文件夹列表失败:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "下拉选择项", notes = "下拉选择项")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/option", method = {RequestMethod.GET})
    @ResponseBody
    public Object option( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            TOlkModelFolderDo modelVo = new TOlkModelFolderDo();
            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            new PageBeanWrapper( modelVo, hru );
            UserDo user = LoginUtil.getUser( request );
            modelVo.setUserDeptNo( user.getOrgNo() );
            modelVo.setUserAccount( user.getUserName() );
            List<TOlkModelFolderDo> list = folderService.findBeanList( modelVo );
            resMap.setSingleOk( list, "获取选择列表成功" );
        }
        catch ( Exception ex ) {
            resMap.setErr( "获取选择列表失败" );
            logger.error( "获取选择列表失败:", ex );
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "文件夹树", notes = "文件夹树")
    @ApiImplicitParams({
    })
    @RequestMapping(value = "/typetree", method = {RequestMethod.GET})
    @ResponseBody
    public Object typeTree( HttpServletRequest request ) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser( request );

            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            String type = hru.getNvlPara( "type" );
            if ( type == null || type.equals( "" ) ) {
                type = "root";
            }

            String pid = hru.getNvlPara( "id" );

            logger.info( "{}", hru.getAllParaData() );

            String connectChar = "^";
            String splitChar = "\\^";

            String sql = "";

            List<Object> dataList = new ArrayList<>();

            TOlkModelFolderDo bean = new TOlkModelFolderDo();
            bean.setUserAccount( user.getUserName() );
            bean.setUserDeptNo( user.getOrgNo() );
            final List<TOlkModelFolderDo> itemList = folderService.findBeanList( bean );

            dataList = makeTreeNode( itemList, pid );

            resMap.put( "tree", dataList );
            return resMap.setOk().getResultMap();

        }
        catch ( Exception ex ) {
            resMap.setErr( "获取树结构失败" );
            logger.error( "获取树结构异常:", ex );
        }
        return resMap.getResultMap();
    }

    private List<Object> makeTreeNode( List<TOlkModelFolderDo> list, String pid ) {
        List<Object> retList = new ArrayList<>();
        for ( TOlkModelFolderDo temp : list ) {
            if ( ComUtil.trsEmpty( pid ).equals( ComUtil.trsEmpty( temp.getPid() ) ) ) {
                HashMap<String, Object> node = new HashMap<String, Object>();
                node.put( "type", "item" );
                node.put( "Id",  temp.getId() );
                node.put( "relId", temp.getId() );
                node.put( "value", temp.getId() );
                node.put( "name", temp.getFolderName() );
                node.put( "title", temp.getFolderName() );
                node.put( "itemId", temp.getId() );
                node.put( "pid", temp.getPid() );

                //node.setNorder(norder++);
                List<Object> subList = makeTreeNode( list, temp.getId() );
                if ( subList.size() > 0 ) {
                    node.put( "hasLeaf", true );
                    node.put( "children", subList );
                }
//                else {
//                    node.put( "hasLeaf", false );
//                }
                retList.add( node );
            }
        }
        return retList;
    }

}
