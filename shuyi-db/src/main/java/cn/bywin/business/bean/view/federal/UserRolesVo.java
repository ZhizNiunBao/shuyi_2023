package cn.bywin.business.bean.view.federal;

import lombok.Data;

import java.util.List;

@Data
public class UserRolesVo {

    private String nodeId;
    private List<String> users;
    private String userName;
    private Integer status;
}
