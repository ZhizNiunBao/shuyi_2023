package cn.bywin.business.bean.analysis;

import lombok.Data;

/**
 * @Description 校验组件配置
 * @Author wangh
 * @Date 2021-10-20
 */
@Data
public class TruCheckComponent {

    private boolean success;
    private String message;

    public TruCheckComponent( boolean success, String message){
        this.success=success;
        this.message=message;
    }

    public TruCheckComponent(){
    }


}
