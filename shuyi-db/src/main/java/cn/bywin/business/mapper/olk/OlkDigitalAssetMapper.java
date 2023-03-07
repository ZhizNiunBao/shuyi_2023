package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.view.bydb.DigitalAssetVo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

@Repository
public interface OlkDigitalAssetMapper extends Mapper<DigitalAssetVo>, MySqlMapper<DigitalAssetVo> {

    List<DigitalAssetVo> findBeanList(DigitalAssetVo bean);
    long findBeanCnt(DigitalAssetVo bean);

}