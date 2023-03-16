package cn.bywin.business.bean.federal;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
@Data
public class ModelElementVto {

    private String id;

    /**
     * 前端需要的参数
     */
    private List<Map> items;

    private String componentId;

    /**
     * 组件分类
     */
    private String classify;

    /**
     * 组件名称
     */
    private String name;

    /**
     * 组件标识
     */
    private String stype;

    private boolean hasTemplate;

    /**
     * 项目id
     */
    private String modelId;

    /**
     * x坐标
     */
    private String x;

    /**
     * y坐标
     */
    private String y;

    /**
     * 图标形状
     */
    private String shape;

    /**
     * 图标
     */
    private String icon;

}
