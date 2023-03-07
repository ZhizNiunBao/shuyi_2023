package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TTruModelWaterMarkDo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.BaseMapper;

@Mapper
public interface TTruModelWaterMarkMapper extends BaseMapper<TTruModelWaterMarkDo> {
    @Insert("INSERT INTO t_tru_model_water_mark (element_id, water_mark_internal, water_mark_unit, water_mark_for) VALUES(#{elementId}, #{waterMarkInternal}, #{waterMarkUnit}, #{waterMarkFor}) AS VALS ON DUPLICATE KEY UPDATE  water_mark_internal = VALS.water_mark_internal, water_mark_unit = VALS.water_mark_unit, water_mark_for = VALS.water_mark_for")
    Integer upsert(TTruModelWaterMarkDo waterMarkDo);
}
