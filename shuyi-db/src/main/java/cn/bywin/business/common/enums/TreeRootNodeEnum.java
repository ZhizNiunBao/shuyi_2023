package cn.bywin.business.common.enums;

/**
 * 树根节点默认值
 * @author zzm
 */
public enum TreeRootNodeEnum {

    /**
     * 联邦资源数据源根节点
     */
    DATASOURCE("root", "数据源", "数据源"),

    /**
     * 模型资源树根节点
     */
    MODEL_OBJECT("root", "数据资源", "数据资源");

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
