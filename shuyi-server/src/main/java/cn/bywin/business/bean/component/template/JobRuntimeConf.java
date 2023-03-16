package cn.bywin.business.bean.component.template;

import cn.bywin.business.bean.federal.FNodePartyDo;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @Descriptio 组件参数配置
 * @Author wangh
 * @Date 2021-07-30
 */
@Data
public class JobRuntimeConf implements Serializable {


    private Map<String, Object> job_runtime_conf = new HashMap<>();
    private Map<String, Object> job_dsl = new HashMap<>();

    public JobRuntimeConf(FNodePartyDo host, List<Integer> guest) {
        Map<String, Object> initiator = new HashMap<>();
        initiator.put("role", "guest");
        initiator.put("party_id", host.getPartyId());
        Map<String, Object> job_parameters = new HashMap<>();
        Map<String, Object> common = new HashMap<>();
        common.put("job_type", "train");
//        common.put("work_mode", host.getWorkMode());
//        common.put("backend", host.getBackend());
//        if (host.getBackend() != Integer.parseInt(BACKEND.getCodeName())) {
//            Map<String, Object> spark = new HashMap<>();
//            spark.put("driver-memory", host.getDriverMemory());
//            spark.put("num-executors", host.getNumExecutors());
//            spark.put("executor-memory", host.getExecutorMemory());
//            spark.put("executor-cores", host.getExecutorCores());
//            common.put("spark_submit_config", spark);
//        }
        job_parameters.put("common", common);
        Map<String, Object> role = Maps.newLinkedHashMap();
        role.put("guest", Arrays.asList(host.getPartyId()));
        role.put("host", guest);
        role.put("arbiter",Arrays.asList(guest.get(0)));//
        job_runtime_conf.put("dsl_version", 2);
        job_runtime_conf.put("initiator", initiator);
        job_runtime_conf.put("job_parameters", job_parameters);
        job_runtime_conf.put("role", role);
    }

    public JobRuntimeConf putconf(String key, Object value) {
        job_runtime_conf.put(key, value);
        return this;
    }

    public JobRuntimeConf putdsl(String key, Object value) {
        job_dsl.put(key, value);
        return this;
    }
}
