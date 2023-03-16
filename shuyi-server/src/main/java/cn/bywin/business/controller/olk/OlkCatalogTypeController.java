package cn.bywin.business.controller.olk;


import cn.bywin.business.bean.olk.TOlkCatalogTypeDo;
import cn.bywin.business.bean.olk.TOlkDatabaseDo;
import cn.bywin.business.bean.olk.TOlkDcServerDo;
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

    @Autowired
    private OlkDcServerService dcService;


    @ApiOperation(value = "新增olk目录分组", notes = "新增olk目录分组")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "info", value = "olk目录分组", dataType = "TOlkCatalogTypeDo", required = true, paramType = "body")
    })
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    public Object add(@RequestBody TOlkCatalogTypeDo info, HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser(request);
            if (user == null || StringUtils.isBlank(user.getUserId())) {
                return resMap.setErr("请先登录").getResultMap();
            }

            if (StringUtils.isBlank(info.getTypeName())) {
                return resMap.setErr("名称不能为空").getResultMap();
            }

            if( StringUtils.isBlank( info.getPid() ) ){
                info.setPid(null);
            }
//            info.setPid(null);
            info.setId( ComUtil.genId());
            LoginUtil.setBeanInsertUserInfo( info,user );
            info.setUserAccount( user.getUserName() );
            info.setUserAccountName( user.getChnName() );
            info.setUserDeptNa( user.getOrgNo() );
            info.setUserDeptNa( user.getOrgName() );

            String preCode ="";
            List<TOlkCatalogTypeDo> pList = null;
            if( StringUtils.isBlank( info.getPid() ) ){
                info.setPid(null);
//                TOlkCatalogTypeDo temp = new TOlkCatalogTypeDo();
//                temp.setDcId( user.getUserName() );
//                temp.setPid( "#NULL#" );
//                if( StringUtils.isBlank( info.getRelCode() )) {
//                    pList = typeService.findBeanList(temp);
//                    preCode = "A";
//                }
            }
            else{
                final TOlkCatalogTypeDo parent = typeService.findById(info.getPid());

                if( parent == null) {
                    return resMap.setErr("上级分组不存在").getResultMap();
                }
//                if( StringUtils.isBlank(parent.getRelCode()) ){
//                    return resMap.setErr("请先重新保存上级分组").getResultMap();
//                }
                if( !info.getUserAccount().equals( parent.getUserAccount() ) ){
                    return  resMap.setErr("你无权在此分组下添加分组").getResultMap();
                }
                info.setDcId( parent.getDcId() );
//                if( StringUtils.isBlank( info.getRelCode() )) {
//                    info.setPid(parent.getId());
//                    TOlkCatalogTypeDo temp = new TOlkCatalogTypeDo();
//                    temp.setPid(parent.getId());
//                    pList = typeService.findBeanList(temp);
//                    preCode = parent.getRelCode();
//                }
            }
            //if( StringUtils.isBlank( info.getRelCode() )) {
//                String strCode = "";
//                if (pList.size() > 0) {
//                    int code = 1;
//                    while (true) {
//                        strCode = String.format("%s%03d", preCode, code);
//                        boolean bfound = false;
//                        for (TOlkCatalogTypeDo sysDepartmentDo : pList) {
//                            if (strCode.equals(sysDepartmentDo.getRelCode())) {
//                                bfound = true;
//                                break;
//                            }
//                        }
//                        if (!bfound) {
//                            break;
//                        }
//                        code ++;
//                    }
//
//                } else {
//                    strCode = preCode + "001";
//                }
//
//                if (StringUtils.isBlank(info.getRelCode())) {
//                    info.setRelCode(strCode);
//                }
            //}

            if( StringUtils.isNotBlank( info.getDcId() ) ){
                TOlkDcServerDo dcDo = dcService.findById( info.getDcId() );
                if( dcDo == null ){
                    return resMap.setErr("节点不存在").getResultMap();
                }
            }
            else{
                return resMap.setErr("节点不能为空").getResultMap();
            }

            TOlkCatalogTypeDo same = new TOlkCatalogTypeDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, same );
            if( StringUtils.isBlank( same.getPid() ) )
                same.setPid("#NULL#");
            if( StringUtils.isBlank( same.getDcId() ) )
                same.setDcId("#NULL#");
            final long sameNameCount = typeService.findSameNameCount( same );
            if( sameNameCount >0 ){
                return resMap.setErr("名称已使用").getResultMap();
            }

            typeService.insertBean(info);
//            new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).addLog(user, info, "新增-olk目录分组");
            resMap.setSingleOk(info, "保存成功");

        } catch (Exception ex) {
            ex.printStackTrace();
            resMap.setErr("保存失败");
            logger.error("保存异常:", ex);
        }
        return resMap.getResultMap();
    }

    @ApiOperation(value = "修改olk目录分组", notes = "修改olk目录分组")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "id", value = "id", dataType = "String", required = true, paramType = "form"),
            //@ApiImplicitParam(name = "typeName", value = "名称", dataType = "String", required = true, paramType = "query")
    })
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update(@RequestBody TOlkCatalogTypeDo bean,HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser(request);
            if (user == null || StringUtils.isBlank(user.getUserId())) {
                return resMap.setErr("请先登录").getResultMap();
            }

            //HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest( request );
            //logger.debug( "{}",hru.getAllParaData() );
            if( bean == null || StringUtils.isBlank( bean.getId() ) ){
                return resMap.setErr("id不能为空").getResultMap();
            }
            //TOlkCatalogTypeDo info = typeService.findById(hru.getNvlPara("id"));
            TOlkCatalogTypeDo info = typeService.findById(bean.getId());
            if (info == null)
            {
                return resMap.setErr("分组不存在").getResultMap();
            }

            TOlkCatalogTypeDo old = new TOlkCatalogTypeDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, old );

            MyBeanUtils.copyBeanNotNull2Bean( bean, info );

           // new PageBeanWrapper( info,hru,"");

            if (StringUtils.isBlank(info.getTypeName())) {
                return resMap.setErr("名称不能为空").getResultMap();
            }

            info.setRelCode( old.getRelCode() );
            info.setPid( old.getPid() );
//            if( StringUtils.isNotBlank( info.getPid() ) ){ //改成只有一级
//                info.setPid(null);
//                info.setRelCode( null );
//            }
//            else{
//                info.setPid(null);
//            }

            info.setUserAccount( user.getUserName() );
            info.setUserAccountName( user.getChnName() );
            info.setUserDeptNa( user.getOrgNo() );
            info.setUserDeptNa( user.getOrgName() );

            if( StringUtils.isNotBlank( old.getUserAccount() ) &&
                !old.getUserAccount().equals(user.getUserName()) ){
                    return resMap.setErr("你不能操作此类型").getResultMap();
            }

            TOlkCatalogTypeDo same = new TOlkCatalogTypeDo();
            MyBeanUtils.copyBeanNotNull2Bean( info, same );
            if( StringUtils.isBlank( same.getPid() ) )
                same.setPid("#NULL#");
            if( StringUtils.isBlank( same.getDcId() ) )
                same.setDcId("#NULL#");
            final long sameNameCount = typeService.findSameNameCount( same );
            if( sameNameCount >0 ){
                return resMap.setErr("名称已使用").getResultMap();
            }

            String preCode ="";
            List<TOlkCatalogTypeDo> pList = null;
            if( StringUtils.isBlank( info.getPid() ) ){
                info.setPid(null);
//                TOlkCatalogTypeDo temp = new TOlkCatalogTypeDo();
//                //temp.setAdminId( userAdmin.getId() );
//                temp.setDcId( user.getUserName() );
//                temp.setPid( "#NULL#" );
//                if( StringUtils.isBlank( info.getRelCode() )) {
//                    pList = typeService.findBeanList(temp);
//                    preCode = "A";
//                }
            }
            else{
                final TOlkCatalogTypeDo parent = typeService.findById(info.getPid());

                if( parent == null) {
                    return resMap.setErr("上级组织机构不存在").getResultMap();
                }
//                if( StringUtils.isBlank(parent.getRelCode()) ){
//                    return resMap.setErr("请先重新保存上级组织机构").getResultMap();
//                }
                if( !user.getUserName().equals( parent.getUserAccount() ) ){
                    return  resMap.setErr("你无权在此分组下操作分组").getResultMap();
                }
                info.setDcId( parent.getDcId() );

//                if( StringUtils.isBlank( info.getRelCode() )) {
//                    info.setPid(parent.getId());
//                    TOlkCatalogTypeDo temp = new TOlkCatalogTypeDo();
//                    temp.setPid(parent.getId());
//                    pList = typeService.findBeanList(temp);
//                    preCode = parent.getRelCode();
//                }

            }
//            if( StringUtils.isBlank( info.getRelCode() )) {
//                String strCode = "";
//                if (pList.size() > 0) {
//                    int code = 1;
//                    while (true) {
//                        strCode = String.format("%s%03d", preCode, code);
//                        boolean bfound = false;
//                        for (TOlkCatalogTypeDo sysDepartmentDo : pList) {
//                            if (strCode.equals(sysDepartmentDo.getRelCode())) {
//                                bfound = true;
//                                break;
//                            }
//                        }
//                        if (!bfound) {
//                            break;
//                        }
//                        code ++;
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
            if( StringUtils.isNotBlank( info.getDcId() ) ){
                TOlkDcServerDo dcDo = dcService.findById( info.getDcId() );
                if( dcDo == null ){
                    return resMap.setErr("节点不存在").getResultMap();
                }
            }
            else{
                return resMap.setErr("节点不为空").getResultMap();
            }

            typeService.updateBean(info);

//            new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).updateLog(user, old, info, "修改-olk目录分组");

            resMap.setSingleOk(info, "保存成功");

        } catch (Exception ex) {
            ex.printStackTrace();
            resMap.setErr("保存失败");
            logger.error("保存异常:", ex);
        }
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
            ex.printStackTrace();
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
    public Object delete(String id,HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            if (StringUtils.isBlank(id)) {
                return resMap.setErr("id不能为空").getResultMap();
            }
            UserDo user = LoginUtil.getUser(request);
            if (!id.matches("^[a-zA-Z0-9\\-_,]*$")) {
                return resMap.setErr("id有非法字符").getResultMap();
            }
            List<String> split = Arrays.asList(id.split(","));

//            Example exp = new Example(TOlkAdminUserDo.class);
//            Example.Criteria criteria = exp.createCriteria();
//            criteria.andIn("adminId", split);
//            int cnt = objectService.findCountByExample(exp);
//            if( cnt >0 ){
//                return resMap.setErr("目录分组有下级对象不能删除").getResultMap();
//            }

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

//            String times = String.valueOf(System.currentTimeMillis());
//            for (TOlkCatalogTypeDo info : list) {
//                try {
//                    new LogActionOp(SysParamSetOp.readValue(Constants.syspara_SystemCode, ""), HttpRequestUtil.getAllIp(request)).delLog(user, info, "删除-olk目录分组" + times);
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

            if( StringUtils.isBlank( modelVo.getDcId() )) {
                UserDo user = LoginUtil.getUser(request);
//                TOlkDcServerDo dcTmp = new TOlkDcServerDo();
//                dcTmp.setManageAccount(user.getUserName());
//                List<TOlkDcServerDo> dcList = dcserverService.find(dcTmp);
//                if (dcList.size() == 0) {
//                    return resMap.setErr("用户未关联节点").getResultMap();
//                } else if (dcList.size() > 1) {
//                    String dcName = dcList.stream().map(x -> x.getDcName() + "(" + x.getDcCode() + ")").collect(Collectors.joining(","));
//                    logger.error("用户{},关联多节点:{}", user.getUserName(), dcName);
//                    resMap.setDebugeInfo("关联多节点:" + dcName);
//                    return resMap.setErr("用户关联多节点").getResultMap();
//                }
//                TOlkDcServerDo dcDo = dcList.get(0);
//                modelVo.setDcId(dcDo.getId());
                modelVo.setDcId( user.getUserName() );
            }

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



    @ApiOperation(value = "目录分组树", notes = "目录分组树")
    @ApiImplicitParams({
             @ApiImplicitParam(name = "id", value = "模型信息", dataType = "String", required = false, paramType = "form")
    })
    @RequestMapping(value = "/typetree", method = {RequestMethod.GET})
    @ResponseBody
    public Object typeTree(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {
            UserDo user = LoginUtil.getUser(request);

            TOlkDcServerDo dcTmp = new TOlkDcServerDo();
            dcTmp.setEnable( 1 );
            List<TOlkDcServerDo> dcList = dcService.findBeanList(dcTmp);

            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
            String type = hru.getNvlPara("type");
            if (type == null || type.equals("")) {
                type = "root";
            }

            String pid = hru.getNvlPara("id");

            logger.debug("{}",hru.getAllParaData());

//            String connectChar = "^";
//            String splitChar = "\\^";

            List<Object> dataList = new ArrayList<>();

            TOlkCatalogTypeDo bean = new TOlkCatalogTypeDo();
            bean.setUserAccount( user.getUserName() );
            final List<TOlkCatalogTypeDo> itemList = typeService.findBeanList(bean);

            for ( TOlkDcServerDo dcDo : dcList ) {
                HashMap<String,Object> node = new HashMap<String,Object>();
                node.put("type","dc");
                node.put("Id",dcDo.getId());
                node.put("relId",dcDo.getId());
                node.put("value",dcDo.getDcCode());
                node.put("name",dcDo.getDcName());
                node.put("title",dcDo.getDcName());
                node.put("folderId","");
                List<Object> subList = makeTreeNode(dcDo.getId(), itemList, pid );
                node.put("children", subList);
                dataList.add(  node );
            }
            resMap.setSingleOk( dataList,"获取树结构成功");
                return resMap.setOk().getResultMap();

        } catch (Exception ex) {
            resMap.setErr("获取树结构失败");
            logger.error("获取树结构异常:", ex);
        }
        return resMap.getResultMap();
    }

    private List<Object> makeTreeNode(String dcId, List<TOlkCatalogTypeDo> list,String pid){
        List<Object> retList = new ArrayList<>();
        for (TOlkCatalogTypeDo temp : list) {
            if( ComUtil.trsEmpty( pid).equals( ComUtil.trsEmpty( temp.getPid() ) ) && dcId.equals( temp.getDcId() )){
                HashMap<String,Object> node = new HashMap<String,Object>();
                node.put("type","folder");
                node.put("Id", temp.getId());
                node.put("relId",temp.getId());
                node.put("value",temp.getId());
                node.put("name",temp.getTypeName());
                node.put("title",temp.getTypeName());
                node.put("folderId",temp.getId());
                node.put("pid",temp.getPid());
                node.put("dcId",dcId);

                //node.setNorder(norder++);
                List<Object> subList = makeTreeNode(dcId,list, temp.getId() );
                if( subList.size()>0 ){
                    node.put("hasLeaf",true);
                    node.put("children",subList);
                }
                else{
                    node.put("hasLeaf",false);
                }
                retList.add(node);
            }
        }

        return retList;
    }

}
