package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface OlkModelObjectMapper extends Mapper<TOlkModelObjectDo>, MySqlMapper<TOlkModelObjectDo> {


    List<TOlkModelObjectDo> findBeanList(TOlkModelObjectDo bean);

    long findBeanCnt(TOlkModelObjectDo bean);

    @Select("SELECT * FROM t_olk_model_object WHERE name = #{name}")
    List<TOlkModelObjectDo> findByName(@Param("name") String name);
    @Select("SELECT * FROM t_olk_model_object WHERE model_id = #{modelId}")
    List<TOlkModelObjectDo> selectByModelId(@Param("modelId") String modelId);

    @Select("SELECT * FROM t_olk_model_object WHERE real_obj_id = #{relObjId} and model_id=#{modelId} limit 1")
    TOlkModelObjectDo selectByObjectId(@Param("relObjId") String relObjId,@Param("modelId") String modelId);

    //List<DigitalAssetVo> findModelObjecRelData( @Param("modelId") String modelId);

    @Select("SELECT count(*) cnt  FROM t_olk_model_object b, t_olk_model_element a WHERE a.tc_id = b.object_id  and a.model_id  =b.model_id and b.id in( ${ids} ) ")
    Long checkUse(@Param("ids")  String ids);
}

