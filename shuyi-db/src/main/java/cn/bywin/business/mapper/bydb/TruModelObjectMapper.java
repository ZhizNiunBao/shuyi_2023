package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TTruModelObjectDo;
import cn.bywin.business.bean.view.bydb.DigitalAssetVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface TruModelObjectMapper extends Mapper<TTruModelObjectDo>, MySqlMapper<TTruModelObjectDo> {


    List<TTruModelObjectDo> findBeanList(TTruModelObjectDo bean);

    long findBeanCnt(TTruModelObjectDo bean);

    @Select("SELECT * FROM t_tru_model_object WHERE name = #{name}")
    List<TTruModelObjectDo> findByName(@Param("name") String name);
    @Select("SELECT * FROM t_tru_model_object WHERE model_id = #{modelId}")
    List<TTruModelObjectDo> selectByModelId(@Param("modelId") String modelId);

    @Select("SELECT * FROM t_tru_model_object WHERE real_obj_id = #{relObjId} and model_id=#{modelId} limit 1")
    TTruModelObjectDo selectByObjectId(@Param("relObjId") String relObjId,@Param("modelId") String modelId);

    //List<DigitalAssetVo> findModelObjecRelData( @Param("modelId") String modelId);

    @Select("SELECT count(*) cnt  FROM t_tru_model_object b, t_tru_model_element a WHERE a.tc_id = b.object_id  and a.model_id  =b.model_id and b.id in( ${ids} ) ")
    Long checkUse(@Param("ids")  String ids);
}

