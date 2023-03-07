package cn.bywin.business.bean.bydb;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table( name ="t_tru_model_window" )
public class TTruModelWindowDo {
    @Id
    @Column( name = "id" )
    private String id ;

    @Column( name = "use_tumble_start" )
    private Boolean useTumbleStart;

    @Column( name = "use_tumble_end" )
    private Boolean useTumbleEnd;

    @Column( name = "window_interval" )
    private Integer windowInterval;

    @Column( name = "time_unit" )
    private String timeUnit;

    @Column( name = "watermark_column" )
    private String watermarkColumn;

}
