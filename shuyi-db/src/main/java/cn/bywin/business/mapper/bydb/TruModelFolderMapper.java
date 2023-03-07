package cn.bywin.business.mapper.bydb;

import cn.bywin.business.bean.bydb.TTruModelFolderDo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface TruModelFolderMapper extends Mapper<TTruModelFolderDo>, MySqlMapper<TTruModelFolderDo> {

    List<TTruModelFolderDo> findBeanList( TTruModelFolderDo bean);
    long findBeanCnt( TTruModelFolderDo bean);

    @Select(value = " select count(*) cnt from  t_tru_model_folder where ( pid is null and '${pid}' = '#NULL#'  or pid = '${pid}' ) and folder_name= #{folderName} " +
            "and id != #{id} and user_account =#{userAccount}" )
    long findSameNameCount( TTruModelFolderDo bean);

    @Delete(value = " delete from t_tru_model_folder where user_account= #{userAccount} order by folder_name" )
    long deleteByDcId(@Param("dcId") String dcId);

}