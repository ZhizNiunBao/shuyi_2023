package cn.bywin.business.service.olk;

import cn.bywin.business.bean.olk.TOlkModelObjectDo;
import cn.bywin.business.mapper.olk.OlkModelObjectMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class OlkModelObjectService extends BaseServiceImpl<TOlkModelObjectDo, String> {

    @Autowired
    private OlkModelObjectMapper truModelObjectMapper;

    @Override
    public Mapper<TOlkModelObjectDo> getMapper() {
        return truModelObjectMapper;
    }

    public List<TOlkModelObjectDo> findBeanList( TOlkModelObjectDo modelInfo) {
        return truModelObjectMapper.findBeanList(modelInfo);
    }

    public long findBeanCnt( TOlkModelObjectDo bean) {
        return truModelObjectMapper.findBeanCnt(bean);
    }

    public List<TOlkModelObjectDo> findByName( String name) {
        return truModelObjectMapper.findByName(name);
    }
    public List<TOlkModelObjectDo> selectByModelId( String modelId) {
        return truModelObjectMapper.selectByModelId(modelId);
    }
    public TOlkModelObjectDo selectByObjectId( String relObjId,String modelId) {
        return truModelObjectMapper.selectByObjectId(relObjId,modelId);
    }

//    public List<DigitalAssetVo> findModelObjecRelData( String modelId) {
//        return truModelObjectMapper.findModelObjecRelData(modelId);
//    }

    public Long checkUse(String ids) {
        return truModelObjectMapper.checkUse(ids);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer saveModelObjectList(List<TOlkModelObjectDo> list) {
        for ( TOlkModelObjectDo tmp : list ) {
            TOlkModelObjectDo moDo = truModelObjectMapper.selectByPrimaryKey( tmp.getId() );
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
