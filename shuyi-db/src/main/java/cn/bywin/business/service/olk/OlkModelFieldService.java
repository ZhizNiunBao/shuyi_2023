package cn.bywin.business.service.olk;

import cn.bywin.business.bean.olk.TOlkModelFieldDo;
import cn.bywin.business.mapper.bydb.TruModelFieldMapper;
import cn.bywin.business.mapper.olk.OlkModelFieldMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class OlkModelFieldService extends BaseServiceImpl<TOlkModelFieldDo, String> {

    @Autowired
    private OlkModelFieldMapper truModelFieldMapper;

    @Override
    public Mapper<TOlkModelFieldDo> getMapper() {
        return truModelFieldMapper;
    }

    public List<TOlkModelFieldDo> selectByModelId( String modelId){
        return truModelFieldMapper.selectByModelId(modelId);
    }
    public List<TOlkModelFieldDo> selectByElementId( String id){
        return truModelFieldMapper.selectByElementId(id);
    }
    public long deleteByElementId(String elementId){
        return truModelFieldMapper.deleteByElementId(elementId);
    }

    public List<TOlkModelFieldDo> selectByElementIdTable( String modelId) {
        return truModelFieldMapper.selectByElementIdTable(modelId);
    }

    public List<TOlkModelFieldDo> selectByElementIdAll( String id) {
        return truModelFieldMapper.selectByElementIdAll(id);
    }

    public List<TOlkModelFieldDo> selectByModelField( String modelId) {
        return     truModelFieldMapper.selectByModelField(modelId);
    }

    @Transactional(rollbackFor=Exception.class)
    public int saveFields( List<TOlkModelFieldDo> addList,List<TOlkModelFieldDo> modeList,List<TOlkModelFieldDo> delList) {
        if( delList!= null){
            for ( TOlkModelFieldDo fieldDo : delList ) {
                truModelFieldMapper.deleteByPrimaryKey(  fieldDo.getId() );
            }
        }
        if( modeList!= null){
            for ( TOlkModelFieldDo fieldDo : modeList ) {
                truModelFieldMapper.updateByPrimaryKey(  fieldDo );
            }
        }
        if( addList!= null){
            for ( TOlkModelFieldDo fieldDo : addList ) {
                truModelFieldMapper.insert(  fieldDo );
            }
        }
        return     1;
    }
}
