package cn.bywin.business.service.federal;

import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.view.federal.FComponentVo;
import cn.bywin.business.mapper.federal.ComponentMapper;
import cn.service.impl.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComponentService extends BaseServiceImpl<FComponentDo, String> {

    @Autowired
    private ComponentMapper componentMapper;

    @Override
    public Mapper<FComponentDo> getMapper() {
        return componentMapper;
    }

    public List<FComponentDo> findBeanList(FComponentDo modelInfo) {
        return componentMapper.findBeanList(modelInfo);
    }

    public List<FComponentVo> findTreeList(FComponentDo modelInfo) {

        List<FComponentVo> data = componentMapper.findTreeList(modelInfo);
        List<FComponentVo> result = data.stream()
                .filter(meun -> StringUtils.isBlank(meun.getPid()))
                .map(menu -> {
                    menu.setChildren(getChildren(menu, data));
                    return menu;
                })
                // 根据排序字段排序
                .sorted((menu1, menu2) -> {
                    return (menu1.getSorts() == null ? 0 : menu1.getSorts()) - (menu2.getSorts() == null ?
                            0 : menu2.getSorts());
                })
                .collect(Collectors.toList());
        return result;
    }

    public long findBeanCnt(FComponentDo bean) {
        return componentMapper.findBeanCnt(bean);
    }

    private List<FComponentVo> getChildren(FComponentVo root, List<FComponentVo> all) {
        List<FComponentVo> children = all.stream()
                .filter(menu -> String.valueOf(menu.getPid()).equals(root.getId()))
                .map((menu) -> {
                    menu.setChildren(getChildren(menu, all));
                    return menu;
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getSorts() == null ? 0 : menu1.getSorts()) - (menu2.getSorts() == null ? 0 :
                            menu2.getSorts());
                })
                .collect(Collectors.toList());
        return children;
    }

}
