package cn.bywin.business.service.bydb;

import cn.bywin.business.bean.bydb.TTruModelElementDo;
import cn.bywin.business.bean.bydb.TTruModelElementRelDo;
import cn.bywin.business.bean.bydb.TTruModelObjectDo;
import cn.bywin.business.bean.view.bydb.DigitalAssetVo;
import cn.bywin.business.mapper.bydb.TruModelObjectMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class TruModelObjectService extends BaseServiceImpl<TTruModelObjectDo, String> {

    @Autowired
    private TruModelObjectMapper truModelObjectMapper;

    @Override
    public Mapper<TTruModelObjectDo> getMapper() {
        return truModelObjectMapper;
    }

    public List<TTruModelObjectDo> findBeanList( TTruModelObjectDo modelInfo) {
        return truModelObjectMapper.findBeanList(modelInfo);
    }

    public long findBeanCnt( TTruModelObjectDo bean) {
        return truModelObjectMapper.findBeanCnt(bean);
    }

    public List<TTruModelObjectDo> findByName( String name) {
        return truModelObjectMapper.findByName(name);
    }
    public List<TTruModelObjectDo> selectByModelId( String modelId) {
        return truModelObjectMapper.selectByModelId(modelId);
    }
    public TTruModelObjectDo selectByObjectId( String relObjId,String modelId) {
        return truModelObjectMapper.selectByObjectId(relObjId,modelId);
    }

//    public List<DigitalAssetVo> findModelObjecRelData( String modelId) {
//        return truModelObjectMapper.findModelObjecRelData(modelId);
//    }

    public Long checkUse(String ids) {
        return truModelObjectMapper.checkUse(ids);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer saveModelObjectList(List<TTruModelObjectDo> list) {
        for ( TTruModelObjectDo tmp : list ) {
            TTruModelObjectDo moDo = truModelObjectMapper.selectByPrimaryKey( tmp.getId() );
            if( moDo != null ){
                truModelObjectMapper.updateByPrimaryKey( tmp );
            }
            else{
                truModelObjectMapper.insert( tmp );
            }
        }
        return list.size();
    }


}
