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
@FeignClient(name = "olkTaskFlinkApiService", url = "${modelTaskFlinkUrl}")
public interface OlkTaskFlinkApiService {

    /**
     * 提交flink任务
     *
     * @param info 数据集信息
     */
    @PostMapping("/task/olkModel")
    String runModelTask(@RequestBody Map<String,String> info);
}
