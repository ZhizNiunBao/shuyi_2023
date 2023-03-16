package cn.bywin.business.bean.component.template;

import static cn.bywin.business.bean.federal.ApiCodeEnum.PREDICT_TYPE;

import cn.bywin.business.bean.federal.ElementDataTvo;
import cn.bywin.business.bean.federal.FModelJobDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.common.util.JsonUtil;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @Descriptio 预测组件参数配置
 * @Author wangh
 * @Date 2021-08-30
 */
@Data
public class PredictJobRuntimeConf implements Serializable {

    @Data
    private static class DataPath {
        private String name;
        private String namespace;
    }

    private Map<String, Object> job_runtime_conf = new HashMap<>();
    //  private Map<String, Object> job_dsl = new HashMap<>();

    public PredictJobRuntimeConf(FNodePartyDo host, List<Integer> guest, ElementDataTvo elementDataTvo, FModelJobDo modelConfig) {
        Map<String, Object> initiator = new HashMap<>();
        initiator.put("role", host.getId().equals(modelConfig.getHost()) ? "guest" : "host");
        initiator.put("party_id", host.getPartyId());
        Map<String, Object> job_parameters = new HashMap<>();
        Map<String, Object> role_parameters = new HashMap<>();
//        job_parameters.put("work_mode", host.getWorkMode());
//        job_parameters.put("backend", host.getBackend());
//        if (host.getBackend() != Integer.parseInt(BACKEND.getCodeName())) {
//            Map<String, Object> spark = new HashMap<>();
//            spark.put("driver-memory", host.getDriverMemory());
//            spark.put("num-executors", host.getNumExecutors());
//            spark.put("executor-memory", host.getExecutorMemory());
//            spark.put("executor-cores", host.getExecutorCores());
//            job_parameters.put("spark_submit_config", spark);
//        }
        job_parameters.put("job_type", PREDICT_TYPE.getCodeName());
        Map map = JsonUtil.gson().fromJson(modelConfig.getModelConfig(), Map.class);
        job_parameters.put("model_id", map.get("model_id"));
        job_parameters.put("model_version", map.get("model_version").toString().replace("\"", ""));
        Map<String, Object> common = new HashMap<>();
        common.put("common", job_parameters);
        Map<String, Object> role = new HashMap<>();
        role.put("guest", Arrays.asList(host.getPartyId()));
        Map<String, Object> component_parameters = new HashMap<>();
        job_runtime_conf.put("dsl_version", 2);
        job_runtime_conf.put("initiator", initiator);
        job_runtime_conf.put("job_parameters", common);
        job_runtime_conf.put("role", role);
        Map<String, Object> guests = new HashMap<>();
        for (int i = 0; i < elementDataTvo.getHost().size(); i++) {
            Map<String, Object> indexs = new HashMap<>();
            Map<String, Object> reder = new HashMap<>();
            Map<String, Object> data = new HashMap<>();
            DataPath dataPath = new DataPath();
            dataPath.setName(elementDataTvo.getHost().get(i).getTableName());
            dataPath.setNamespace(elementDataTvo.getHost().get(i).getNamespace());
//            data.put("need_run", elementDataTvo.getHost().get(i).getWithLabel() == 1 ? true : false);
            data.put("need_run",  false);
            indexs.put("evaluation_" + i, data);
            reder.put("table", dataPath);
            indexs.put("reader_" + i, reder);
            guests.put(String.valueOf(i), indexs);
        }
        if (elementDataTvo.getGuest() != null && elementDataTvo.getGuest().size() > 0) {
            Map<String, Object> hosts = new HashMap<>();
            for (int i = 0; i < elementDataTvo.getGuest().size(); i++) {
                Map<String, Object> reder = new HashMap<>();
                Map<String, Object> indexs = new HashMap<>();
                Map<String, Object> data = new HashMap<>();
                DataPath dataPath = new DataPath();
                dataPath.setName(elementDataTvo.getGuest().get(i).getTableName());
                dataPath.setNamespace(elementDataTvo.getGuest().get(i).getNamespace());
                reder.put("table", dataPath);
                indexs.put("reader_" + i, reder);
                data.put("with_label", elementDataTvo.getGuest().get(i).getWithLabel() == 1 ? true : false);
//                indexs.put("data_transform_0", data);
                hosts.put(String.valueOf(i), indexs);
            }
            component_parameters.put("host", hosts);
            role.put("arbiter", Arrays.asList(guest.get(0)));
            role.put("host", guest);

        }
        component_parameters.put("guest", guests);
        role_parameters.put("role", component_parameters);
        job_runtime_conf.put("component_parameters", role_parameters);
    }

    public static PredictJobRuntimeConf init(FNodePartyDo host, List<Integer> guest, ElementDataTvo elementDataTvo, FModelJobDo fModelJobDo) {
        PredictJobRuntimeConf predictJobRuntimeConf = new PredictJobRuntimeConf(host, guest, elementDataTvo, fModelJobDo);
        return predictJobRuntimeConf;
    }
//    private Map<String, Object> buildMap(List<Object> info) {
//        Map<String, Object> data = new HashMap<>(5);
//        Map<String, Object> args = new HashMap<>(5);
//        Map<String, Object> result = new HashMap<>(5);
//        data.put("eval_data", info);
//        args.put("data", data);
//        result.put("args", args);
//        return result;
//    }
}
