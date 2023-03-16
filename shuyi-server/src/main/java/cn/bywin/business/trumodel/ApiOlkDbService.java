package cn.bywin.business.trumodel;

import cn.bywin.business.bean.federal.FDataApproveDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.olk.TOlkDatabaseDo;
import cn.bywin.business.bean.olk.TOlkDcServerDo;
import cn.bywin.business.bean.olk.TOlkModelDo;
import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.bean.olk.TOlkObjectDo;
import cn.bywin.business.bean.olk.TOlkSchemaDo;
import cn.bywin.business.bean.view.bydb.DigitalAssetVo;
import cn.bywin.business.bean.view.olk.OlkObjectWithFieldsVo;
import cn.bywin.business.bean.view.olk.VOlkObjectVo;
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
@FeignClient(name = "olkDbService", url = "${trumodelServerUrl}"+"/olkdbapi", configuration = FeignConfiguration.class)
public interface ApiOlkDbService {

    /**
     * 同步目录
     * @param info
     * @param token
     * @return
     */
    @PostMapping("synolkdcserver")
    public ObjectResp<String> synOlkDcServer(@RequestBody TOlkDcServerDo info, @RequestParam("token") String token);

    /**
     * 同步目录
     * @param info
     * @param token
     * @return
     */
    @PostMapping("synolkdatabase")
    public ObjectResp<String> synOlkDatabase(@RequestBody TOlkDatabaseDo info, @RequestParam("token") String token);

    /**
     * 删除目录
     * @param id
     * @param token
     * @return
     */
    @PostMapping("delolkdatabase")
    public  ObjectResp<String> delOlkDatabase( @RequestParam("id")  String id, @RequestParam("token") String token);

    /**
     * 目录列表
     * @param info
     * @param token
     * @return
     */
    @PostMapping("databaselist")
    public List<FDatasourceDo> databaseList(@RequestBody TOlkDatabaseDo info, @RequestParam("token") String token);

    /**
     * 目录明细
     * @param id
     * @param token
     * @return
     */
    @PostMapping("databaseinfo")
    public TOlkDatabaseDo databaseInfo(@RequestParam("id")  String id, @RequestParam("token") String token);

    /**
     * 同步库
     * @param info
     * @param token
     * @return
     */
    @PostMapping("synolkschema")
    public ObjectResp<String> synOlkSchema( @RequestBody TOlkSchemaDo info, @RequestParam("token") String token);

    /**
     * 删除库
     * @param idList
     * @param notCheck
     * @param token
     * @return
     */
    @PostMapping("delolkschema")
    public  ObjectResp<String> delOlkSchema(@RequestBody  List<String> idList,@RequestParam("notCheck") String notCheck, @RequestParam("token") String token);

    /**
     * 库列表
     * @param info
     * @param token
     * @return
     */
    @PostMapping("olkschemalist")
    public List<TOlkSchemaDo> olkSchemaList(@RequestBody TOlkSchemaDo info, @RequestParam("token") String token);

    /**
     * 库明细
     * @param id
     * @param token
     * @return
     */
    @PostMapping("schemainfo")
    public TOlkSchemaDo olkSchemaInfo(@RequestParam("id")  String id, @RequestParam("token") String token);


    /**
     * 同步表
     * @param list
     * @param token
     * @return
     */
    @PostMapping("synolktable")
    public ObjectResp<String> synOlkTable( @RequestBody List<OlkObjectWithFieldsVo> list, @RequestParam("token") String token);

    /**
     * 删除表
     * @param list
     * @param token
     * @return
     */
    @PostMapping("delolktable")
    public  ObjectResp<String> delOlkTable( List<TOlkObjectDo> list, @RequestParam("token") String token);

    /**
     * 库列表
     * @param info
     * @param token
     * @return
     */
    @PostMapping("olktablelist")
    public ListResp<TOlkObjectDo> olkTableList( @RequestBody TOlkObjectDo info, @RequestParam("token") String token);


    /**
     * 库列表
     * @param info
     * @param token
     * @return
     */
    @PostMapping("pmolktablelist")
    public ListResp<VOlkObjectVo> pmOlkTableList( @RequestBody VOlkObjectVo info, @RequestParam("token") String token);

    /**
     * 表明细
     * @param id
     * @param token
     * @return
     */
    @PostMapping("olktableinfo")
    public TOlkObjectDo olkTableInfo(@RequestParam("id")  String id, @RequestParam("token") String token);

    /**
     * 表与字段等明细
     * @param id
     * @param token
     * @return
     */
    @PostMapping("olktablewithsubinfo")
    public ObjectResp<OlkObjectWithFieldsVo> olkTableWithSubInfo( @RequestParam("id")  String id, @RequestParam("token") String token);

    /**
     * 查询用户权限表
     * @param bean
     * @param token
     * @return
     */
    @PostMapping("userolktablewithinfo")
    public ListResp<OlkObjectWithFieldsVo> userOlkTableWithInfo( @RequestBody  OlkObjectWithFieldsVo bean, @RequestParam("token") String token);


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
     * 保存授权
     * @param list
     * @param token
     * @return
     */
    @PostMapping("savegrantobject")
    public ListResp<FDataApproveDo> saveGrantObject( @RequestBody List<FDataApproveDo> list, @RequestParam("token") String token);

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
//    @PostMapping("queryuserapprove")
//    public ListResp<FDataApproveVo> queryUserApprove( @RequestBody List<FDataApproveDo> list, @RequestParam("token") String token);

    /**
     * 上报olk模型使用对象
     * @param modelId
     * @param token
     * @return
     */
    @GetMapping("findolkmodelobjecreldata")
    public ListResp<VOlkObjectVo> findOlkModelObjecRelData( @RequestParam("modelId") String modelId, @RequestParam("token") String token);

    /**
     * 删除olk模型引用表
     * @param idList
     * @param token
     * @return
     */
    @PostMapping("delolkmodelobject")
    public ObjectResp<String> delOlkModelObject( @RequestBody List<String> idList, @RequestParam("token") String token);

    /**
     * 上报olk申请
     * @param info
     * @param token
     * @return
     */
    @PostMapping("synolkapplyobject")
    public ObjectResp<FDataApproveDo> synOlkApplyObject( @RequestBody FDataApproveDo info, @RequestParam("token") String token);

    /**
     * 取消olk申请
     * @param id
     * @param token
     * @return
     */
    @PostMapping("cancelolkapplyobject")
    public ObjectResp<String> cancelOlkApplyObject( @RequestParam(value = "id",required = false) String id, @RequestParam(value = "relId" ,required = false) String relId, @RequestParam(value = "userId",required = false) String userId , @RequestParam("token") String token);

}
