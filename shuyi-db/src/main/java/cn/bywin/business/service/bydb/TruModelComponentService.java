package cn.bywin.business.service.bydb;

import cn.bywin.business.bean.bydb.TTruModelComponentDo;
import cn.bywin.business.bean.view.bydb.TTruModelComponentVo;
import cn.bywin.business.mapper.bydb.TruModelComponentMapper;
import cn.service.impl.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TruModelComponentService extends BaseServiceImpl<TTruModelComponentDo, String> {

    @Autowired
    private TruModelComponentMapper truModelComponentMapper;

    @Override
    public Mapper<TTruModelComponentDo> getMapper() {
        return truModelComponentMapper;
    }

    public List<TTruModelComponentVo> findBeanList( TTruModelComponentDo modelInfo){
        return truModelComponentMapper.findBeanList(modelInfo);
    }

    public long findBeanCnt( TTruModelComponentDo bean){
        return truModelComponentMapper.findBeanCnt(bean);
    }


    public List<TTruModelComponentVo> findBeanListTree( TTruModelComponentDo modelInfo) {

        List<TTruModelComponentVo> data =truModelComponentMapper.findBeanList(modelInfo);
        String qryCnd = null;
        if( StringUtils.isNotBlank( modelInfo.getQryCond() )){
            qryCnd = modelInfo.getQryCond().trim();
        }

        List<TTruModelComponentVo> result =  new ArrayList<>();
        for ( TTruModelComponentVo menu : data ) {
            if( menu.getLevel() ==1 ){
                List<TTruModelComponentVo> children = getChildren( menu, data, qryCnd );
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

//        List<TTruModelComponentVo> data = truModelComponentMapper.findBeanList(modelInfo);
//        List<TTruModelComponentVo> result = data.stream()
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
    private List<TTruModelComponentVo> getChildren(TTruModelComponentVo root, List<TTruModelComponentVo> all,String qryCnd) {
        List<TTruModelComponentVo> list = new ArrayList<>();
        for ( TTruModelComponentVo menu : all ) {
            if( menu.getParent().equals( root.getId() ) ){
                List<TTruModelComponentVo> children = getChildren( menu, all, qryCnd );
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

//    private List<TTruModelComponentVo> getChildren( TTruModelComponentVo root, List<TTruModelComponentVo> all) {
//        List<TTruModelComponentVo> children = all.stream()
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
