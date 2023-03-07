package cn.bywin.business.mapper.olk;

import cn.bywin.business.bean.olk.TOlkDcServerConfigMapDo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author zzm
 */
@Repository
public interface OlkDcServerConfigMapMapper extends Mapper<TOlkDcServerConfigMapDo>, MySqlMapper<TOlkDcServerConfigMapDo> {
}
