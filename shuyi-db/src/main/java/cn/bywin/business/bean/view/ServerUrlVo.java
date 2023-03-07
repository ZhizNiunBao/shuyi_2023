package cn.bywin.business.bean.view;

import lombok.Data;

@Data
public class ServerUrlVo {

    private Integer status;
    private String url;
    private String name;
    private String tips;
    public ServerUrlVo(Integer status,String name,String url,String tips){
        this.status=status;
        this.url=url;
        this.name=name;
        this.tips=tips;
    }

}
