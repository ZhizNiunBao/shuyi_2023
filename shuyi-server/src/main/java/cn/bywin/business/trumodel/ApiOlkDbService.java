package cn.bywin.business.trumodel;

import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.bean.view.bydb.DigitalAssetVo;
import cn.bywin.business.bean.view.olk.OlkObjectWithFieldsVo;
import cn.bywin.business.bean.view.olk.VOlkObjectVo;
import cn.bywin.common.resp.ListResp;
import cn.bywin.common.resp.ObjectResp;
import cn.bywin.config.FeignConfiguration;
import java.util.HashMap;
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
     * 库列表
     * @param info
     * @param token
     * @return
     */
    @PostMapping("pmolktablelist")
    public ListResp<VOlkObjectVo> pmOlkTableList( @RequestBody VOlkObjectVo info, @RequestParam("token") String token);

    /**
     * 表与字段等明细
     * @param id
     * @param token
     * @return
     */
    @PostMapping("olktablewithsubinfo")
    public ObjectResp<OlkObjectWithFieldsVo> olkTableWithSubInfo( @RequestParam("id")  String id, @RequestParam("token") String token);

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
     * 上报模型使用对象
     * @param bean
     * @param token
     * @return
     */
    @PostMapping("synolkmodelobject")
    public ObjectResp<TOlkModelObjectDo> synOlkModelObject( @RequestBody TOlkModelObjectDo bean, @RequestParam("token") String token);

}
