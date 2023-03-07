package cn.bywin.business.service.federal;

import cn.bywin.business.bean.federal.FlDataDescDo;
import cn.bywin.business.mapper.federal.DataDescMapper;
import cn.bywin.business.mapper.federal.DataPartyMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Service
public class DataDescService extends BaseServiceImpl<FlDataDescDo, String> {

    @Autowired
    private DataPartyMapper dataPartyMapper;
    @Autowired
    private DataDescMapper dataDescMapper;


    @Override
    public Mapper<FlDataDescDo> getMapper() {
        return dataDescMapper;
    }

    public List<FlDataDescDo> selectByDataId(String dataId) {
        return dataDescMapper.selectByDataId(dataId);
    }

    public List<FlDataDescDo> findBeanList(FlDataDescDo modelInfo) {
        return dataDescMapper.findBeanList(modelInfo);
    }

    public long findBeanCnt(FlDataDescDo bean) {
        return dataDescMapper.findBeanCnt(bean);
    }

}
