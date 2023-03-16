package cn.bywin.business.bean.component.template;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * @Description 组件dag参数
 * @Author wangh
 * @Date 2021-07-27
 */
@Data
public class ComponentDsl {


    private String module;

    private Map<String, Object> input = new HashMap<>();

    private Map<String, Object> output = new HashMap<>();

    public ComponentDsl() {
    }

    public ComponentDsl(String module, Map<String, Object> output) {
        this.module = module;
        this.output = output;
    }

}
