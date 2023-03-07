package cn.bywin.business.bean.view.security;

import lombok.Data;

import java.util.Date;

/**
 * @author zzm
 */
@Data
public class UpdateFileResult {

    private String fileName;

    private Integer size;

    private Date createTime;

    private String description;

    private String fullName;

    private String alias;

    private Integer pid;

    private Boolean directory;

    private Integer id;

    private String type;

    private Date updateTime;

    private Integer userId;

}
