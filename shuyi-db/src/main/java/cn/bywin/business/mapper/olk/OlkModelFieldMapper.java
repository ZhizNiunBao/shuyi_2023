package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface OlkModelFieldMapper extends Mapper<TOlkModelFieldDo>, MySqlMapper<TOlkModelFieldDo> {
    @Select("SELECT * FROM t_olk_model_field WHERE element_id = #{elementId} and is_select!=-1 order by filter_sort")
    List<TOlkModelFieldDo> selectByElementId( @Param("elementId") String elementId);

    @Select("SELECT * FROM t_olk_model_field WHERE element_id = #{elementId} order by filter_sort")
    List<TOlkModelFieldDo> selectByElementIdAll( @Param("elementId") String elementId);

    @Delete("delete from t_olk_model_field WHERE element_id = #{elementId } ")
    long deleteByElementId(@Param("elementId") String elementId);

    @Delete("delete from t_olk_model_field WHERE  extends_id= #{extendsId} ")
    long deleteByExtendsId(@Param("extendsId") String extendsId);

    @Delete("delete from t_olk_model_field WHERE element_id = #{elementId} and extends_id= #{extendsId} ")
    long deleteByElementIdTable(@Param("elementId") String elementId, @Param("extendsId") String extendsId);

    @Select("SELECT * FROM t_olk_model_field WHERE id = #{id} order by filter_sort")
    TOlkModelFieldDo selectById( @Param("id") String id);

    @Select("SELECT * FROM t_olk_model_field f join t_olk_model_element e on e.id=f.element_id and e.model_id = #{modelId} order by filter_sort")
    List<TOlkModelFieldDo> selectByModelId( @Param("modelId") String modelId);

    @Select("SELECT * FROM t_olk_model_field WHERE element_id = #{elementId} and extends_id= #{elementId} and is_select!=-1 order by filter_sort")
    List<TOlkModelFieldDo> selectByElementIdTable( String elementId);

    void updateById( TOlkModelFieldDo e);

    @Select("SELECT f.* from t_olk_model_element e  join  t_olk_model_field f on e.id=f.element_id and e.model_id= #{modelId} order by e.model_id,f.filter_sort")
    List<TOlkModelFieldDo> selectByModelField( @Param("modelId") String modelId);
}

