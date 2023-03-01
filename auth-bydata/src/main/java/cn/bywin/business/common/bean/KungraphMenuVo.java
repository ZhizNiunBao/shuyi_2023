package cn.bywin.business.common.bean;

import lombok.Data;

/**
 * 坤图菜单返回格式
 * @author zzm
 */
@Data
public class KungraphMenuVo {

    private String path;

    private String component;

    private String showComponent;

    private String icon;

    private String name;

    private String showPath;

    private String type;

}
