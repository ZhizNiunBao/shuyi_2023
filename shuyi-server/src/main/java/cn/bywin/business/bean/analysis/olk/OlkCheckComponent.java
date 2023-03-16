package cn.bywin.business.bean.analysis.olk;

import lombok.Data;

/**
 * @Description 校验组件配置
 * @Author wangh
 * @Date 2021-10-20
 */
@Data
public class OlkCheckComponent {

    private boolean success;
    private String message;

    public OlkCheckComponent( boolean success, String message){
        this.success=success;
        this.message=message;
    }

    public OlkCheckComponent(){
    }


}
