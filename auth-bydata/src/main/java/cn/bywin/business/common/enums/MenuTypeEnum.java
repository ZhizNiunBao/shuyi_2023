package cn.bywin.business.common.enums;

/**
 * @author zzm
 */
public enum MenuTypeEnum {

    /**
     * 菜单
     */
    MEUN("menu", "C"),

    /**
     * 按钮
     */
    BUTTON("button", "B");

    private String kungraphMenuType;

    private String bydataMenuType;

    /**
     * 构造函数
     * @param kungraphMenuType   坤图菜单类型
     * @param bydataMenuType     中台菜单类型
     */
    MenuTypeEnum(String kungraphMenuType, String bydataMenuType) {
        this.kungraphMenuType = kungraphMenuType;
        this.bydataMenuType = bydataMenuType;
    }

    /**
     * 将中台菜单类型转换为坤图菜单类型
     * @param bydataMenuType   中台菜单类型
     * @return                 图谱菜单类型
     */
    public static String changeToKungraphMenu(String bydataMenuType) {
        MenuTypeEnum[] values = values();
        for (MenuTypeEnum value : values) {
            if (value.getBydataMenuType().equals(bydataMenuType)) {
                return value.getKungraphMenuType();
            }
        }
        return null;
    }

    public String getKungraphMenuType() {
        return kungraphMenuType;
    }

    public String getBydataMenuType() {
        return bydataMenuType;
    }
}
