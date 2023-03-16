package cn.bywin.business.trumodel;

import cn.bywin.business.bean.bydb.TBydbDatabaseDo;
import cn.bywin.business.bean.bydb.TBydbObjectDo;
import cn.bywin.business.bean.bydb.TBydbSchemaDo;
import cn.bywin.business.bean.bydb.TTruFavouriteObjectDo;
import cn.bywin.business.bean.bydb.TTruModelDo;
import cn.bywin.business.bean.bydb.TTruModelObjectDo;
import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.bean.system.SysUserDo;
import cn.bywin.business.bean.view.VBydbObjectVo;
import cn.bywin.business.bean.view.bydb.BydbObjectFieldsVo;
import cn.bywin.business.bean.view.bydb.BydbTabDatasetUseProjVo;
import cn.bywin.business.bean.view.bydb.DigitalAssetVo;
import cn.bywin.business.bean.view.federal.FDataApproveVo;
import cn.bywin.business.bean.view.federal.NodePartyView;
import cn.bywin.common.resp.BaseRespone;
import cn.bywin.common.resp.ListResp;
import cn.bywin.common.resp.ObjectResp;
import cn.bywin.config.FeignConfiguration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description
 * @Author me
 * @Date 2022-04-11
 */
@FeignClient(name = "trumodelService", url = "${trumodelServerUrl}"+"/trumodelapi", configuration = FeignConfiguration.class)
public interface ApiTruModelService {

    /**
     * 同步数据源
     * @param info
     * @param token
     * @return
     */
    @PostMapping("nodepartylist")
    public ListResp<NodePartyView> nodePartyList( @RequestBody FNodePartyDo info, @RequestParam("token") String token);

    /**
     * 指定或全部节点
     *  @param id
     * @param token
     * @return
     */
    @PostMapping("allnodeparty")
    public List<NodePartyView> allNodeParty( @RequestParam(value = "id",required = false) String id, @RequestParam("token") String token);

    /**
     * 查询用户信息
     * @param info
     * @param token
     * @return
     */
    @PostMapping("nodeuserlist")
    public ListResp<SysUserDo> nodeUserList( @RequestBody SysUserDo info, @RequestParam("token") String token);

    /**
     * 同步数据源
     * @param info
     * @param token
     * @return
     */
    @PostMapping("syndbsource")
    public ObjectResp<String> synDbsource( @RequestBody FDatasourceDo info, @RequestParam("token") String token);

    /**
     * 删除数据源
     * @param idList
     * @param token
     * @return
     */
    @PostMapping("deldbsource")
    public  ObjectResp<String> delDbsource(@RequestBody  List<String> idList, @RequestParam("token") String token);

    /**
     * 数据源列表
     * @param info
     * @param token
     * @return
     */
    @PostMapping("dbsourcelist")
    public List<FDatasourceDo> dbsourceList(@RequestBody FDatasourceDo info, @RequestParam("token") String token);

    /**
     * 数据源明细
     * @param id
     * @param token
     * @return
     */
    @PostMapping("dbsourceinfo")
    public FDatasourceDo dbsourceInfo(@RequestParam("id") String id, @RequestParam("token") String token);

    /**
     * 同步目录
     * @param info
     * @param token
     * @return
     */
    @PostMapping("syndatabase")
    public ObjectResp<String> synDatabase( @RequestBody TBydbDatabaseDo info, @RequestParam("token") String token);

    /**
     * 删除目录
     * @param id
     * @param token
     * @return
     */
    @PostMapping("deldatabase")
    public  ObjectResp<String> delDatabase( @RequestParam("id")  String id, @RequestParam("token") String token);

    /**
     * 目录列表
     * @param info
     * @param token
     * @return
     */
    @PostMapping("databaselist")
    public List<FDatasourceDo> databaseList(@RequestBody TBydbDatabaseDo info, @RequestParam("token") String token);

    /**
     * 目录明细
     * @param id
     * @param token
     * @return
     */
    @PostMapping("databaseinfo")
    public TBydbDatabaseDo databaseInfo(@RequestParam("id")  String id, @RequestParam("token") String token);

    /**
     * 同步库
     * @param info
     * @param token
     * @return
     */
    @PostMapping("synschema")
    public ObjectResp<String> synSchema( @RequestBody TBydbSchemaDo info, @RequestParam("token") String token);

    /**
     * 删除库
     * @param idList
     * @param notCheck
     * @param token
     * @return
     */
    @PostMapping("delschema")
    public  ObjectResp<String> delSchema(@RequestBody  List<String> idList,@RequestParam("notCheck") String notCheck, @RequestParam("token") String token);

    /**
     * 库列表
     * @param info
     * @param token
     * @return
     */
    @PostMapping("schemalist")
    public List<TBydbSchemaDo> schemaList(@RequestBody TBydbSchemaDo info, @RequestParam("token") String token);

    /**
     * 库明细
     * @param id
     * @param token
     * @return
     */
    @PostMapping("schemainfo")
    public TBydbSchemaDo schemaInfo(@RequestParam("id")  String id, @RequestParam("token") String token);


    /**
     * 同步表
     * @param list
     * @param token
     * @return
     */
    @PostMapping("syntable")
    public ObjectResp<String> synTable( @RequestBody List<BydbObjectFieldsVo> list, @RequestParam("token") String token);

    /**
     * 删除表
     * @param id
     * @param token
     * @return
     */
    @PostMapping("deltable")
    public  ObjectResp<String> delTable( @RequestBody List<String> id, @RequestParam("token") String token);

    /**
     * 库列表
     * @param info
     * @param token
     * @return
     */
    @PostMapping("tablelist")
    public ListResp<TBydbObjectDo> tableList( @RequestBody TBydbObjectDo info, @RequestParam("token") String token);

    /**
     * 库列表
     * @param info
     * @param token
     * @return
     */
    @PostMapping("pmtablelist")
    public ListResp<VBydbObjectVo> pmTableList( @RequestBody VBydbObjectVo info, @RequestParam("token") String token);

    /**
     * 表明细
     * @param id
     * @param token
     * @return
     */
    @PostMapping("tableinfo")
    public TBydbObjectDo tableInfo(@RequestParam("id")  String id, @RequestParam("token") String token);

    /**
     * 表与字段等明细
     * @param id
     * @param token
     * @return
     */
    @PostMapping("tablewithsubinfo")
    public ObjectResp<BydbObjectFieldsVo> tableWithSubInfo(@RequestParam("id")  String id, @RequestParam("token") String token);

    /**
     * 表数据源
     * @param id
     * @param token
     * @return
     */
    @PostMapping("tabledbsource")
    public ObjectResp<FDatasourceDo> tableDbSource(@RequestParam("id")  String id, @RequestParam("token") String token);

    /**
     * 资源使用情况
     * @param id
     * @param token
     * @return
     */
    @PostMapping("tabdatasetuseproj")
    public ListResp<BydbTabDatasetUseProjVo> tabDataSetUseProj( @RequestParam("id")  String id, @RequestParam("token") String token);
    /**
     * 获取pms可信任务数据地图列表
     * @param  modelVo
     * @param token
     * @return
     */
    @PostMapping("digitalassetsearchlist")
    public ListResp<DigitalAssetVo> digitalAssetSearchList( @RequestBody Map<String,Object> modelVo, @RequestParam("token") String token);

    /**
     * 获取pms数字地图字段列表
     * @param  id
     * @param token
     * @return
     */
    @GetMapping("digitalassettabfieldlist")
    public ListResp<HashMap> digitalAssetTabFieldList( @RequestParam ("id") String id, @RequestParam("token") String token);

    /**
     * 获取pms可信任务数据地图列表
     * @param  modelVo
     * @param token
     * @return
     */
    @PostMapping("digitalassetolksearchlist")
    public ListResp<DigitalAssetVo> digitalAssetOlkSearchList( @RequestBody Map<String,Object> modelVo, @RequestParam("token") String token);

    /**
     * 获取pms数字地图字段列表
     * @param  id
     * @param token
     * @return
     */
    @GetMapping("digitalassetolktabfieldlist")
    public ListResp<HashMap> digitalAssetOlkTabFieldList( @RequestParam ("id") String id, @RequestParam("token") String token);


    /**
     * 上报模型
     * @param modelDo
     * @param token
     * @return
     */
    @PostMapping("syntrumodel")
    public ObjectResp<String> synTruModel( @RequestBody TTruModelDo modelDo, @RequestParam("token") String token);

    /**
     * 删除模型
     * @param modelDo
     * @param token
     * @return
     */
    @PostMapping("deltrumodel")
    public ObjectResp<String> delTruModel( @RequestBody TTruModelDo modelDo, @RequestParam("token") String token);

    /**
     * 上报模型使用对象
     * @param bean
     * @param token
     * @return
     */
    @PostMapping("syntrumodelobject")
    public ObjectResp<TTruModelObjectDo> synTruModelObject( @RequestBody TTruModelObjectDo bean, @RequestParam("token") String token);


    /**
     * 上报模型
     * @param modelDo
     * @param token
     * @return
     */
    @PostMapping("synolkmodel")
    public ObjectResp<String> synOlkModel( @RequestBody TOlkModelDo modelDo, @RequestParam("token") String token);

    /**
     * 删除模型
     * @param modelDo
     * @param token
     * @return
     */
    @PostMapping("delolkmodel")
    public ObjectResp<String> delOlkModel( @RequestBody TOlkModelDo modelDo, @RequestParam("token") String token);

    /**
     * 上报模型使用对象
     * @param bean
     * @param token
     * @return
     */
    @PostMapping("synolkmodelobject")
    public ObjectResp<TOlkModelObjectDo> synOlkModelObject( @RequestBody TOlkModelObjectDo bean, @RequestParam("token") String token);


    /**
     * 查询模型使用对象权限
     * @param list
     * @param token
     * @return
     */
    @PostMapping("queryuserapprove")
    public ListResp<FDataApproveVo> queryUserApprove( @RequestBody List<FDataApproveDo> list, @RequestParam("token") String token);

    @PostMapping("synmodelobjectList")
    public List<TTruModelObjectDo> synmodelobjectList( @RequestBody List<TTruModelObjectDo> tabList, @RequestParam("token") String token);

    /**
     * 上报可信模型使用对象
     * @param modelId
     * @param token
     * @return
     */
    @GetMapping("findtrumodelobjecreldata")
    public ListResp<VBydbObjectVo> findTruModelObjecRelData( @RequestParam("modelId") String modelId, @RequestParam("token") String token);

    /**
     * 删除可信模型引用表
     * @param idList
     * @param token
     * @return
     */
    @PostMapping("deltrumodelobject")
    public ObjectResp<String> delTruModelObject( @RequestBody List<String> idList, @RequestParam("token") String token);


    /**
     * 上报olk模型使用对象
     * @param modelId
     * @param token
     * @return
     */
    @GetMapping("findolkmodelobjecreldata")
    public ListResp<VBydbObjectVo> findOlkModelObjecRelData( @RequestParam("modelId") String modelId, @RequestParam("token") String token);

    /**
     * 删除olk模型引用表
     * @param idList
     * @param token
     * @return
     */
    @PostMapping("delolkmodelobject")
    public ObjectResp<String> delOlkModelObject( @RequestBody List<String> idList, @RequestParam("token") String token);


    /**
     * 上报收藏
     * @param info
     * @param token
     * @return
     */
    @PostMapping("synfavouriteobject")
    public ObjectResp<TTruFavouriteObjectDo> synFavouriteObject( @RequestBody TTruFavouriteObjectDo info, @RequestParam("token") String token);

    /**
     * 删除收藏
     * @param list
     * @param token
     * @return
     */
    @PostMapping("delfavouriteobject")
    public BaseRespone delFavouriteObject( @RequestBody List<TTruFavouriteObjectDo> list, @RequestParam("token") String token);


    /**
     * 上报申请
     * @param info
     * @param token
     * @return
     */
    @PostMapping("synapplyobject")
    public ObjectResp<FDataApproveDo> synApplyObject( @RequestBody FDataApproveDo info, @RequestParam("token") String token);

    /**
     * 保存授权
     * @param list
     * @param token
     * @return
     */
    @PostMapping("savedataapprove")
    public ListResp<FDataApproveDo> saveDataApprove( @RequestBody List<FDataApproveDo> list, @RequestParam("token") String token);
    /**
     * 删除申请
     * @param list
     * @param token
     * @return
     */
//    @PostMapping("delapplyobject")
//    public ObjectResp<String> delApplyObject( @RequestBody List<TTruApplyObjectDo> list, @RequestParam("token") String token);

    /**
     * 取消申请
     * @param id
     * @param token
     * @return
     */
    @PostMapping("cancelapplyobject")
    public BaseRespone cancelApplyObject( @RequestParam(value = "id",required = false) String id, @RequestParam(value = "relId" ,required = false) String relId, @RequestParam(value = "userId",required = false) String userId , @RequestParam("token") String token);

}
