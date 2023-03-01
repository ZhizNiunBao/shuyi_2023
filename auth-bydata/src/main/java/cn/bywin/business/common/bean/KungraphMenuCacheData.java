package cn.bywin.business.common.bean;

import lombok.Data;

import java.util.List;

/**
 * 缓存在 Redis 的菜单
 * @author zzm
 */
@Data
public class KungraphMenuCacheData extends KungraphMenuVo {

    private List<KungraphMenuCacheData> kungraphMenuCaches;

}
