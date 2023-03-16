package cn.bywin.business.bean.federal;

import lombok.Data;

@Data
public class CheckComponent {

    private boolean success;
    private String message;

    public CheckComponent(boolean success,String message){
        this.success=success;
        this.message=message;
    }
}
