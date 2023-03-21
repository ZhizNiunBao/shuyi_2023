package cn.bywin.business.common.enums;

/**
 * 树根节点默认值
 * @author zzm
 */
public enum TreeRootNodeEnum {

    /**
     * 联邦资源数据源
     */
    DATASOURCE("root", "数据资源", "数据资源");

    private String id;

    private String name;

    private String title;

    TreeRootNodeEnum(String id, String name, String title) {
        this.id = id;
        this.name = name;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }
}
