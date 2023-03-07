package cn.bywin.business.service.olk;

import cn.bywin.business.bean.olk.TOlkModelComponentDo;
import cn.bywin.business.bean.view.olk.TOlkModelComponentVo;
import cn.bywin.business.mapper.bydb.TruModelComponentMapper;
import cn.bywin.business.mapper.olk.OlkModelComponentMapper;
import cn.service.impl.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class OlkModelComponentService extends BaseServiceImpl<TOlkModelComponentDo, String> {

    @Autowired
    private OlkModelComponentMapper truModelComponentMapper;

    @Override
    public Mapper<TOlkModelComponentDo> getMapper() {
        return truModelComponentMapper;
    }

    public List<TOlkModelComponentVo> findBeanList( TOlkModelComponentDo modelInfo){
        return truModelComponentMapper.findBeanList(modelInfo);
    }

    public long findBeanCnt( TOlkModelComponentDo bean){
        return truModelComponentMapper.findBeanCnt(bean);
    }


    public List<TOlkModelComponentVo> findBeanListTree( TOlkModelComponentDo modelInfo) {

        List<TOlkModelComponentVo> data =truModelComponentMapper.findBeanList(modelInfo);
        String qryCnd = null;
        if( StringUtils.isNotBlank( modelInfo.getQryCond() )){
            qryCnd = modelInfo.getQryCond().trim();
        }

        List<TOlkModelComponentVo> result =  new ArrayList<>();
        for ( TOlkModelComponentVo menu : data ) {
            if( menu.getLevel() ==1 ){
                List<TOlkModelComponentVo> children = getChildren( menu, data, qryCnd );
                if( children ==null || children.size()==0){
                    if(qryCnd ==null || menu.getName().indexOf(  qryCnd )>=0){
                        result.add( menu );
                    }
                }
                else{
                    menu.setChildren( children );
                    result.add( menu );
                }
            }
        }

//        List<TOlkModelComponentVo> data = truModelComponentMapper.findBeanList(modelInfo);
//        List<TOlkModelComponentVo> result = data.stream()
//                .filter(meun -> meun.getLevel() == 1)
//                .map(menu -> {
//                    menu.setChildren(getChildren(menu, data));
//                    return menu;
//                })
//                // 根据排序字段排序
//                .sorted((menu1, menu2) -> {
//                    return (menu1.getSorts() == null ? 0 : menu1.getSorts()) - (menu2.getSorts() == null ?
//                            0 : menu2.getSorts());
//                })
//                .collect(Collectors.toList());

        return  result;
    }
    private List<TOlkModelComponentVo> getChildren(TOlkModelComponentVo root, List<TOlkModelComponentVo> all,String qryCnd) {
        List<TOlkModelComponentVo> list = new ArrayList<>();
        for ( TOlkModelComponentVo menu : all ) {
            if( menu.getParent().equals( root.getId() ) ){
                List<TOlkModelComponentVo> children = getChildren( menu, all, qryCnd );
                if( children ==null || children.size()==0){
                    if(qryCnd ==null || menu.getName().indexOf(  qryCnd )>=0){
                        list.add( menu );
                    }
                }
                else{
                    menu.setChildren( children );
                    list.add( menu );
                }
            }
        }
        if( list.size()>0)
            return list;
        else
            return  null;
    }

//    private List<TOlkModelComponentVo> getChildren( TOlkModelComponentVo root, List<TOlkModelComponentVo> all) {
//        List<TOlkModelComponentVo> children = all.stream()
//                .filter(menu -> String.valueOf(menu.getParent()).equals(root.getId()))
//                .map((menu) -> {
//                    menu.setChildren(getChildren(menu, all));
//                    return menu;
//                })
//                .sorted((menu1, menu2) -> {
//                    return (menu1.getSorts() == null ? 0 : menu1.getSorts()) - (menu2.getSorts() == null ? 0 :
//                            menu2.getSorts());
//                })
//                .collect(Collectors.toList());
//        return children;
//    }
}
