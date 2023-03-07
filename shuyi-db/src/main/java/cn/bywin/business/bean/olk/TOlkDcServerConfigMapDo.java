package cn.bywin.business.bean.olk;

import cn.bywin.business.common.base.SidEntityDo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author firepation
 */
@Data
@Entity
@Table(name ="t_olk_dc_server_config_map")
public class TOlkDcServerConfigMapDo extends SidEntityDo {

  /**
   * 文件类型
   */
  @Column( name = "file_type" )
  private String fileType;

  /**
   * 上传到海豚后返回的文件id
   */
  @Column( name = "resource_id" )
  private Integer resourceId;

  /**
   * 节点代码
   */
  @Column( name = "dc_server_code" )
  private String dcServerCode;

  @Column(name = "success")
  private Boolean success;
}
