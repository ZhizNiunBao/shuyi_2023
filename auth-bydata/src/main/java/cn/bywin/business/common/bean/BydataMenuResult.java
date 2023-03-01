package cn.bywin.business.common.bean;

import cn.bywin.business.common.enums.MenuTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * 中台菜单接口返回数据
 * @author zzm
 */
@Data
public class BydataMenuResult {

    private String id;

    private String createdTime;

    private String modifiedTime;

    private String params;

    private String menuName;

    private String parentId;

    private Integer orderNum;

    private String path;

    private String component;

    private String isFrame;

    private String isCache;

    private String menuType;

    private String visible;

    private String status;

    private String perms;

    private String icon;

    private String creatorId;

    private String creatorName;

    private String operatorId;

    private String operatorName;

    private String remark;

    private String modType;

    private String level;

    private String name;

    private List<BydataMenuResult> children;

    private String checkFlag;

    public KungraphMenuVo changeToKungraphMenu() {
        KungraphMenuVo kungraphMenuVo = new KungraphMenuVo();
        kungraphMenuVo.setComponent(component);
        kungraphMenuVo.setIcon(icon);
        kungraphMenuVo.setName(name);
        kungraphMenuVo.setPath(path);
        kungraphMenuVo.setShowPath(path);
        kungraphMenuVo.setShowComponent(component);
        kungraphMenuVo.setType(MenuTypeEnum.changeToKungraphMenu(menuType));
        return kungraphMenuVo;
    }
}
