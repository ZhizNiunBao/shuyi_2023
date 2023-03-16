package cn.bywin.business.federal;

import cn.bywin.config.FeignConfiguration;
import java.net.URI;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description
 * @Author wangh
 * @Date 2022-03-29
 */
@FeignClient(name = "flowServerUrl", url = "${flowServerUrl}", configuration = FeignConfiguration.class)
public interface FateApiService {


    @RequestMapping(value = "v1/data/upload", method = {RequestMethod.GET}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String upload(URI uri, @RequestPart("file") MultipartFile file,
                  @RequestParam("drop") Integer drop,
                  @RequestParam("table_name") String table_name,
                  @RequestParam("head") Integer head,
                  @RequestParam("partition") Integer partition,
                  @RequestParam("namespace") String namespace,
                  @RequestParam("work_mode") Integer work_mode);


    @PostMapping(value = "v1/job/log/download", consumes = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    @ResponseBody
    feign.Response logDownload(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping(value = "v1/model/export", consumes = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    @ResponseBody
    feign.Response modelExport(URI uri, @RequestBody Map<String, Object> map);

//    @ResponseBody
//    @RequestMapping(value = "v1/tracking/component/output/data/download", method = {RequestMethod.GET}, consumes = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
//    feign.Response dataDownload(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/job/submit")
    String jobSubmit(URI uri, @RequestBody Object map);

    @PostMapping("v1/job/query")
    String jobQuery(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/data/upload/history")
    String history(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/job/task/query")
    String taskQuery(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/job/stop")
    String jobStop(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/model/deploy")
    String modelDeploy(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/table/delete")
    String tableDelete(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/job/rerun")
    String jobRerun(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/tracking/component/metrics")
    String componentMetrics(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/tracking/component/output/data")
    String outputData(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/tracking/component/metric_data")
    String metricData(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/tracking/component/output/model")
    String metricModel(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/log/cat")
    String logCat(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/model/bind")
    String bind(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/model/unbind")
    String unbind(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/model/load")
    String load(URI uri, @RequestBody Map<String, Object> map);

    @PostMapping("v1/model/unload")
    String unload(URI uri, @RequestBody Map<String, Object> map);

}
