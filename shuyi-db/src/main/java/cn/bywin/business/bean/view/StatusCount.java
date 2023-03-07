package cn.bywin.business.bean.view;

import lombok.Data;

@Data
public class StatusCount {

    private Integer total;
    private Integer fail;
    private Integer success;
    private Integer run;
    //private Integer other;
    private Integer submit;
    private Integer unsubmit;

    public StatusCount(){
        this.total=0;
        this.fail=0;
        this.success=0;
        this.run=0;
        //this.other=0;
        submit = 0;
        unsubmit = 0;
    }
}
