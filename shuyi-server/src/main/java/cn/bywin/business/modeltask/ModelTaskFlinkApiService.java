package cn.bywin.business.modeltask;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description
 * @Author llk
 * @Date 2022-03-14
 */
@FeignClient(name = "modelTaskFlinkApiService", url = "${modelTaskFlinkUrl}")
public interface ModelTaskFlinkApiService {

    /**
     * 提交flink任务
     *
     * @param info 数据集信息
     */
    @PostMapping("/task/execute")
    String runModelTask(@RequestBody Map<String,String> info);

    /**
     * 获取flink sql 血缘
     *
     * @param info 语句
     */
    @PostMapping("/task/getLineage")
    String bloodShip(@RequestBody Map<String,String> info);

}
