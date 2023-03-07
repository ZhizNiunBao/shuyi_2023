package cn.bywin.business.service.bydb;

import cn.bywin.business.bean.bydb.TTruModelFieldDo;
import cn.bywin.business.mapper.bydb.TruModelFieldMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class TruModelFieldService extends BaseServiceImpl<TTruModelFieldDo, String> {

    @Autowired
    private TruModelFieldMapper truModelFieldMapper;

    @Override
    public Mapper<TTruModelFieldDo> getMapper() {
        return truModelFieldMapper;
    }

    public List<TTruModelFieldDo> selectByModelId( String modelId){
        return truModelFieldMapper.selectByModelId(modelId);
    }
    public List<TTruModelFieldDo> selectByElementId( String id){
        return truModelFieldMapper.selectByElementId(id);
    }
    public long deleteByElementId(String elementId){
        return truModelFieldMapper.deleteByElementId(elementId);
    }

    public List<TTruModelFieldDo> selectByElementIdTable( String modelId) {
        return truModelFieldMapper.selectByElementIdTable(modelId);
    }

    public List<TTruModelFieldDo> selectByElementIdAll( String id) {
        return truModelFieldMapper.selectByElementIdAll(id);
    }

    public List<TTruModelFieldDo> selectByModelField( String modelId) {
        return     truModelFieldMapper.selectByModelField(modelId);
    }

    @Transactional(rollbackFor=Exception.class)
    public int saveFields( List<TTruModelFieldDo> addList,List<TTruModelFieldDo> modeList,List<TTruModelFieldDo> delList) {
        if( delList!= null){
            for ( TTruModelFieldDo fieldDo : delList ) {
                truModelFieldMapper.deleteByPrimaryKey(  fieldDo.getId() );
            }
        }
        if( modeList!= null){
            for ( TTruModelFieldDo fieldDo : modeList ) {
                truModelFieldMapper.updateByPrimaryKey(  fieldDo );
            }
        }
        if( addList!= null){
            for ( TTruModelFieldDo fieldDo : addList ) {
                truModelFieldMapper.insert(  fieldDo );
            }
        }
        return     1;
    }
}
