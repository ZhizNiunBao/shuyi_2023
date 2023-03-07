package cn.bywin.business.service.olk;

import cn.bywin.business.bean.olk.TOlkDcServerConfigMapDo;
import cn.bywin.business.mapper.olk.OlkDcServerConfigMapMapper;
import cn.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zzm
 */
@Service
public class OlkDcServerConfigMapService extends BaseServiceImpl<TOlkDcServerConfigMapDo, String> {

    @Autowired
    private OlkDcServerConfigMapMapper dcServerConfigMapMapper;

    @Override
    public Mapper<TOlkDcServerConfigMapDo> getMapper() {
        return dcServerConfigMapMapper;
    }

    public Map<String, String> findByDcCode(String dcCode) {
        TOlkDcServerConfigMapDo searchInfo = new TOlkDcServerConfigMapDo();
        searchInfo.setDcServerCode(dcCode);
        List<TOlkDcServerConfigMapDo> dcServerConfigMaps = find(searchInfo);
        return dcServerConfigMaps.stream()
                .collect(Collectors
                        .toMap(TOlkDcServerConfigMapDo::getId, e-> String.valueOf(e.getResourceId())));
    }
}
