package cn.bywin.business.federal;

import static cn.bywin.business.bean.component.ComponentEnum.*;
import static cn.bywin.business.bean.component.ComponentEnum.ALGO_TYPE;
import static cn.bywin.business.bean.component.ComponentEnum.Data_COMPONENT;
import static cn.bywin.business.bean.component.ComponentEnum.EVL_TYPE;
import static cn.bywin.business.bean.federal.ApiCodeEnum.*;
import static cn.bywin.business.bean.federal.ApiCodeEnum.JOB_RUNNING_CODE;
import static cn.bywin.business.bean.federal.ApiCodeEnum.JOB_WAIT_CODE;
import static cn.bywin.business.bean.federal.ApiCodeEnum.METRICS;
import static cn.bywin.business.bean.federal.StatusCodeEnum.*;
import static cn.bywin.business.bean.federal.StatusCodeEnum.CANCELED_CODE;
import static cn.bywin.business.bean.federal.StatusCodeEnum.FAIL_CODE;
import static cn.bywin.business.bean.federal.StatusCodeEnum.PREDICT_CODE;
import static cn.bywin.business.bean.federal.StatusCodeEnum.RUNNING_CODE;
import static cn.bywin.business.bean.federal.StatusCodeEnum.SUCCESS_CODE;
import static cn.bywin.business.bean.federal.StatusCodeEnum.WAITING_CODE;
import static cn.bywin.business.util.MapTypeAdapter.createTempFile;
import static cn.bywin.business.util.MapTypeAdapter.deleteFile;

import cn.bywin.business.bean.component.template.DagUtils;
import cn.bywin.business.bean.component.template.JobRuntimeConf;
import cn.bywin.business.bean.component.template.PredictJobRuntimeConf;
import cn.bywin.business.bean.federal.ApiCodeEnum;
import cn.bywin.business.bean.federal.DataSchema;
import cn.bywin.business.bean.federal.ElementDataTvo;
import cn.bywin.business.bean.federal.FComponentDo;
import cn.bywin.business.bean.federal.FDataNodeDo;
import cn.bywin.business.bean.federal.FDataPartyDo;
import cn.bywin.business.bean.federal.FDatasourceDo;
import cn.bywin.business.bean.federal.FModelDo;
import cn.bywin.business.bean.federal.FModelElementDo;
import cn.bywin.business.bean.federal.FModelElementRelDo;
import cn.bywin.business.bean.federal.FModelJobDo;
import cn.bywin.business.bean.federal.FNodePartyDo;
import cn.bywin.business.bean.federal.FProjectDo;
import cn.bywin.business.bean.federal.FlDataDescDo;
import cn.bywin.business.bean.federal.graph.ComponentVos;
import cn.bywin.business.bean.view.federal.FDataPartyVo;
import cn.bywin.business.bean.view.federal.FNodePartyVo;
import cn.bywin.business.common.base.UserDo;
import cn.bywin.business.common.login.LoginUtil;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.JsonUtil;
import cn.bywin.business.common.util.MyBeanUtils;
import cn.bywin.business.service.federal.ComponentService;
import cn.bywin.business.service.federal.DataDescService;
import cn.bywin.business.service.federal.DataPartyService;
import cn.bywin.business.service.federal.ModelElementRelService;
import cn.bywin.business.service.federal.ModelElementService;
import cn.bywin.business.service.federal.ModelJobService;
import cn.bywin.business.service.federal.ModelService;
import cn.bywin.business.service.federal.NodePartyService;
import cn.bywin.business.service.federal.ProjectGuestService;
import cn.bywin.business.util.HttpOperaterUtil;
import cn.bywin.business.util.JdbcUtils;
import cn.bywin.business.util.MapTypeAdapter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import feign.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

/**
 * @Description
 * @Author wangh
 * @Date 2021-07-27
 */
@RefreshScope
@Service
public class FederalModelRunService {


    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ModelElementRelService modelElementRelService;
    @Autowired
    private ModelElementService modelElementService;
    @Autowired
    private ModelJobService modelJobService;
    @Autowired
    private ModelService modelService;
    @Autowired
    private NodePartyService nodePartyService;
    @Autowired
    private ComponentService componentService;
    @Autowired
    private DataPartyService dataPartyService;
    @Autowired
    private DataDescService dataDescService;
    @Autowired
    private ApiPmsService apiPmsService;
    @Autowired
    private ProjectGuestService projectGuestService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private FateApiService fateApiService;

    @Value("${sqlMaxLimit}")
    private Integer sqlMaxLimit;

    public FModelJobDo submit(FModelDo modelDo, FProjectDo fProjectDo, FNodePartyDo host, List<FModelElementDo> elements, UserDo userDo) throws Exception {
        List<FModelElementRelDo> elementRels = modelElementRelService.selectByModelId(modelDo.getId());
        List<FComponentDo> fComponentDos = componentService.findAll();
        Map<String, FComponentDo> idComponentMap = fComponentDos.stream().collect(Collectors.toMap(FComponentDo::getId, e -> e));
        List<String> guests = projectGuestService.findByIds(fProjectDo.getId());
        FNodePartyVo fNodePartyVo = new FNodePartyVo();
        fNodePartyVo.setIds(String.join(",", guests));
        List<Integer> guest = apiPmsService.partyByIds(fNodePartyVo);
        List<FDataPartyDo> hlist = dataPartyService.selectByModelId(Arrays.asList(host.getId()), modelDo.getId(), 1);
        JobRuntimeConf jobRuntimeConf = DagUtils.buildDag(elements, elementRels, idComponentMap, host, guest, hlist);
        String param = JsonUtil.toSimpleJson(jobRuntimeConf);
        Map<String, Object> map = MapTypeAdapter.gsonToMap(param);
        FModelJobDo modelJobDo = new FModelJobDo();
        Map<String, Object> config = new HashMap<>();
        for (FModelElementDo elementDo : elements) {
            FComponentDo componentDo = idComponentMap.get(elementDo.getComponentId());
            if (componentDo != null && componentDo.getTypes() == 4) {
                config.put(componentDo.getComponentEn(), JsonUtil.gson().fromJson(elementDo.getConfig(), Map.class));
            }
        }
        modelJobDo.setDslConfig(JsonUtil.toJson(config).replaceAll("\n", "").trim());
        logger.info("提交任务api:{}", param);
        URI uri = new URI(host.getFlowAddress());
        String value = fateApiService.jobSubmit(uri, map);
        JsonObject res = JsonUtil.toJsonObject(value);
        if (value == null || res == null) {
            modelJobDo.setStatus(FAIL_CODE.getCodeName());
            logger.error("提交任务失败,api:{}异常", res);
            return modelJobDo;
        }
        JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
        String uuid = ComUtil.genId();
        LoginUtil.setBeanInsertUserInfo(modelJobDo, userDo);
        modelJobDo.setId(uuid);
        modelJobDo.setStartTime(ComUtil.chgToTimestamp(new Date()));
        modelJobDo.setTypes(modelDo.getTypes());
        modelJobDo.setModelId(modelDo.getId());
        //判断请求是否成功
        if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
            JsonElement data = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_DATA.getCodeName());
            JsonElement jobId = data.getAsJsonObject().get("job_id");
            JsonElement modelInfo = data.getAsJsonObject().get("model_info");
            modelJobDo.setJobId(jobId.getAsString());
            modelJobDo.setModelConfig(JsonUtil.toJson(modelInfo).replaceAll("\n", "").trim());
            modelJobDo.setHost(host.getId());
            modelJobDo.setVersions(jobId.getAsString());
            modelJobDo.setDeploy(FAIL_CODE.getCodeName());
            modelJobDo.setGuest(String.join(",", guests));
            modelJobDo.setStatus(RUNNING_CODE.getCodeName());
            modelDo.setStatus(RUNNING_CODE.getCodeName());
        } else {
            modelJobDo.setStatus(FAIL_CODE.getCodeName());
            modelDo.setStatus(FAIL_CODE.getCodeName());
            modelJobDo.setHost(fProjectDo.getHost());
            modelJobDo.setDeploy(FAIL_CODE.getCodeName());
            modelJobDo.setGuest(String.join(",", guests));
            logger.error("提交失败:{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
        }
        modelJobService.insertBean(modelJobDo);
        apiPmsService.syncModelJob(modelJobDo, userDo.getTokenId());
        modelService.updateBean(modelDo);
        apiPmsService.syncModel(modelDo, userDo.getTokenId());
        return modelJobDo;
    }


    public FModelJobDo predict(FModelJobDo fModelJobDo, FProjectDo fProjectDo, FNodePartyDo host, ElementDataTvo elementDataTvo, UserDo userDo) throws Exception {
        List<String> guests = projectGuestService.findByIds(fProjectDo.getId());
        FNodePartyVo fNodePartyVo = new FNodePartyVo();
        fNodePartyVo.setIds(String.join(",", guests));
        List<Integer> guest = apiPmsService.partyByIds(fNodePartyVo);
        PredictJobRuntimeConf jobRuntimeConf = PredictJobRuntimeConf.init(host, guest, elementDataTvo, fModelJobDo);
        String param = JsonUtil.toSimpleJson(jobRuntimeConf);
        Map<String, Object> map = MapTypeAdapter.gsonToMap(param);
        logger.info("提交任务api:{}", param);
        URI uri = new URI(host.getFlowAddress());
        String value = fateApiService.jobSubmit(uri, map);
        FModelJobDo modelJobDo = new FModelJobDo();
        JsonObject res = JsonUtil.toJsonObject(value);
        if (value == null || res == null) {
            modelJobDo.setStatus(FAIL_CODE.getCodeName());
            logger.error("预测任务失败,api:{}异常", res);
            return modelJobDo;
        }
        String uuid = ComUtil.genId();
        LoginUtil.setBeanInsertUserInfo(modelJobDo, userDo);
        modelJobDo.setHost(host.getId());
        modelJobDo.setModelId(fModelJobDo.getModelId());
        modelJobDo.setModelConfig(fModelJobDo.getId());
        modelJobDo.setId(uuid);
        modelJobDo.setTypes(2);
        modelJobDo.setDeploy(2);
        //判断请求是否成功
        modelJobDo.setStartTime(ComUtil.chgToTimestamp(new Date()));
        JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
        if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
            JsonElement data = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_DATA.getCodeName());
            JsonElement jobId = data.getAsJsonObject().get("job_id");
            modelJobDo.setJobId(jobId.getAsString());
            modelJobDo.setVersions(jobId.getAsString());
            modelJobDo.setStatus(RUNNING_CODE.getCodeName());
        } else {
            modelJobDo.setStatus(FAIL_CODE.getCodeName());
            logger.error("提交失败:{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
        }
        modelJobService.insertBean(modelJobDo);
        return modelJobDo;
    }

    @Async
    public void status(String job_id, String id, FNodePartyDo fNodePartyDo, boolean isModel, UserDo userDo) throws Exception {

        FModelJobDo fModelJobDo = modelJobService.findById(id);
        if (fModelJobDo == null) {
            fModelJobDo = new FModelJobDo();
            fModelJobDo.setJobId(job_id);
        }
        int num = 0;
        Map<String, Object> map = new HashMap<>(5);
        map.put("job_id", job_id);
        if (isModel) {
            map.put("party_id", fNodePartyDo.getPartyId());
        }
        String jobRuntimeConf = JsonUtil.toJson(map);
        Integer result = 3;
        URI uri = new URI(fNodePartyDo.getFlowAddress());
        try {
            while (Integer.parseInt(JOB_RUNNING_CODE.getCodeName()) == result || Integer.parseInt(JOB_WAIT_CODE.getCodeName()) == result) {
                String value = fateApiService.jobQuery(uri, map);
                logger.info("异步查看任务状态api:{}", jobRuntimeConf);
                JsonObject res = JsonUtil.toJsonObject(value);
                if (value == null || res == null) {
                    result = FAIL_CODE.getCodeName();
                    break;
                }
                JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
                //判断请求是否成功
                if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
                    JsonElement data = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_DATA.getCodeName());
                    JsonElement status = data.getAsJsonArray().get(0).getAsJsonObject().get("f_status");
                    switch (status.getAsString()) {
                        case "success":
                            result = SUCCESS_CODE.getCodeName();
                            break;
                        case "running":
                            result = RUNNING_CODE.getCodeName();
                            break;
                        case "waiting":
                            result = WAITING_CODE.getCodeName();
                            break;
                        case "failed":
                            result = FAIL_CODE.getCodeName();
                            break;
                        case "canceled":
                            result = CANCELED_CODE.getCodeName();
                            break;
                    }
                } else {
                    result = FAIL_CODE.getCodeName();
                    logger.error("运行失败{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
                }
                Thread.sleep(3000);
                num++;
                if (num > 1000) {
                    result = CANCELED_CODE.getCodeName();
                    stop(fModelJobDo, fNodePartyDo);
                    logger.error("运行超时jobId:{}", job_id);
                    logger.error("运行超时,超时时间为:{}", num * 3000);
                    break;
                }
            }
        } catch (Exception e) {
            result = FAIL_CODE.getCodeName();
            e.printStackTrace();
            logger.error("运行失败:{}", e.getMessage());
        } finally {
            if (isModel) {
                fModelJobDo.setStatus(result);
                fModelJobDo.setEndTime(ComUtil.chgToTimestamp(new Date()));
                if (Objects.equals(fModelJobDo.getStatus(), SUCCESS_CODE.getCodeName()) && fModelJobDo.getTypes() != 2) {
                    FComponentDo elementDo = modelElementService.selectComponentByModelId(fModelJobDo.getModelId());
                    if (elementDo != null) {
                        ComponentVos components = new ComponentVos();
                        components.setJobId(fModelJobDo.getJobId());
                        components.setComponentName(elementDo.getComponent());
                        components.setPartyId(fNodePartyDo.getPartyId().toString());
                        List<Object> list = score(components);
                        if (list != null && list.size() > 1) {
                            List<Object> list1 = JsonUtil.deserializeAsList(JsonUtil.toArray(list.get(0).toString()),
                                    ArrayList.class);
                            List<Object> list2 = JsonUtil.deserializeAsList(JsonUtil.toArray(list.get(1).toString()),
                                    ArrayList.class);
                            if (list1 != null && list1.size() > 1) {
                                fModelJobDo.setAuc(Double.valueOf(list1.get(1).toString()));
                            }
                            if (list2 != null && list2.size() > 1) {
                                fModelJobDo.setKs(Double.valueOf(list2.get(1).toString()));
                            }
                            deploy(fModelJobDo, fNodePartyDo);
                        }
                        logger.info("模型评分:{}", list);
                    }
                }

                modelJobService.updateBean(fModelJobDo);
                FModelDo modelDo = modelService.findById(fModelJobDo.getModelId());
                modelDo.setStatus(fModelJobDo.getStatus());
                modelService.updateNoNull(modelDo);
                apiPmsService.syncModel(modelDo, userDo.getTokenId());
                apiPmsService.syncModelJob(fModelJobDo, userDo.getTokenId());
            } else {
                FDataPartyDo fDataPartyDo = dataPartyService.findById(id);
                DataSchema schema = dataSchema(fNodePartyDo, job_id);
                if (schema != null) {
                    String featureColumn = schema.getHeader();//.replaceAll(",y", "").replaceAll("y,", "");
                    fDataPartyDo.setFeatureColumn(featureColumn);
                    fDataPartyDo.setFeatrueNum(featureColumn.split(",").length);
                    fDataPartyDo.setSid(schema.getSid());
                    fDataPartyDo.setUseCnt(0);
                    fDataPartyDo.setDataTotal((int) schema.getCount());
                    List<String> strings = Arrays.asList(schema.getHeader().split(","));
                    if (strings.contains("y")) {
                        fDataPartyDo.setWithLabel(1);
                        fDataPartyDo.setLabelName("y");
                        fDataPartyDo.setLabelType("int");
                    } else {
                        fDataPartyDo.setWithLabel(0);
                    }
                    fDataPartyDo.setStatus(SUCCESS_CODE.getCodeName());
                } else {
                    fDataPartyDo.setStatus(FAIL_CODE.getCodeName());
                }
                dataPartyService.updateBean(fDataPartyDo);
                if (Objects.equals(fDataPartyDo.getStatus(), SUCCESS_CODE.getCodeName())) {
                    List<FDataNodeDo> dataNodeDos = dataPartyService.findByDataId(fDataPartyDo.getId());
                    FDataPartyVo fDataPartyVo = new FDataPartyVo();
                    MyBeanUtils.copyBean2Bean(fDataPartyVo, fDataPartyDo);
                    fDataPartyVo.setNodeList(dataNodeDos);
                    Example exp = new Example(FlDataDescDo.class);
                    Example.Criteria criteria = exp.createCriteria();
                    criteria.andEqualTo("dataId", fDataPartyDo.getId()).andEqualTo("edaType", "info");
                    exp.orderBy("edaOrder");
                    List<FlDataDescDo> tzList = dataDescService.findByExample(exp);
                    fDataPartyVo.setTzInfoList(tzList);
                    apiPmsService.syncData(fDataPartyVo, userDo.getTokenId());
                }
            }
        }
    }

    @Async
    public void upload(FDataPartyDo fDataPartyDo, FNodePartyDo fNodePartyDo, File file,
                       String nodes, UserDo userDo, String type, FDatasourceDo datasourceDo, String sql) throws Exception {

//        //默认 1-10分区
        int random = (int) (Math.random() * 10 + 1);
        String value = null;
        URI uri = new URI(fNodePartyDo.getFlowAddress());
        try {
            if (type != null && type.equals("1")) {
                JdbcUtils jdbcUtils = new JdbcUtils(datasourceDo);
                String exeSql = sql.replaceAll("(\r|\n|\t)+", " ").replaceFirst(";\\s+$", " ").replaceAll("\\s+", " ");
                Pattern pd = Pattern.compile("limit\\s+(\\d+\\s*\\,\\s*)+\\d+\\s*$", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pd.matcher(exeSql);
                if (matcher.find()) {
                    String limit = matcher.group();
                    Pattern pd1 = Pattern.compile("(\\s+|,)*\\d+\\s*$", Pattern.CASE_INSENSITIVE);
                    Matcher matcher1 = pd1.matcher(limit);
                    matcher1.find();
                    String scnt = matcher1.group().replace(",", "").trim();
                    if (Integer.parseInt(scnt) > sqlMaxLimit) {
                        exeSql = "select * from ( " + exeSql + " ) x limit " + sqlMaxLimit;
                    }
                } else {
                    exeSql = exeSql + " limit  " + sqlMaxLimit;
                }

                List<Map<String, Object>> sqlData = jdbcUtils.selectData(exeSql);
                if (sqlData == null || sqlData.size() == 0) {
                    fDataPartyDo.setStatus(FAIL_CODE.getCodeName());
                    logger.error("上传失败,联邦分析数据为空{}", sql);
                    return;
                }
                File tempFile = createTempFile(sqlData);
                FileInputStream fileInputStream = new FileInputStream(tempFile);
                MultipartFile multipartFile = new MockMultipartFile(tempFile.getName(), tempFile.getName(),
                        ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);

                value = fateApiService.upload(uri, multipartFile, 1, fDataPartyDo.getTableName(),
                        fDataPartyDo.getHead() == null ? 1 : fDataPartyDo.getHead(),
                        random, fDataPartyDo.getNamespace(), fNodePartyDo.getWorkMode());
                deleteFile(tempFile);
            } else {

                FileInputStream fileInputStream = new FileInputStream(file);
                MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                        ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
                value = fateApiService.upload(uri, multipartFile, 1, fDataPartyDo.getTableName(),
                        fDataPartyDo.getHead() == null ? 1 : fDataPartyDo.getHead(),
                        random, fDataPartyDo.getNamespace(), fNodePartyDo.getWorkMode());
            }
            JsonObject res = JsonUtil.toJsonObject(value);
            if (value == null || res == null) {
                fDataPartyDo.setStatus(FAIL_CODE.getCodeName());
                return;
            }
            JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
            if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
                JsonElement data = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_DATA.getCodeName());
                JsonElement jobId = data.getAsJsonObject().get("job_id");
                fDataPartyDo.setJobId(jobId.getAsString());
                fDataPartyDo.setStatus(RUNNING_CODE.getCodeName());
            } else {
                logger.error("上传失败:{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
                fDataPartyDo.setStatus(FAIL_CODE.getCodeName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fDataPartyDo.setStatus(FAIL_CODE.getCodeName());
        } finally {
            dataPartyService.updateBean(fDataPartyDo);
            //异步更新任务状态
            if (RUNNING_CODE.getCodeName().equals(fDataPartyDo.getStatus())) {
                status(fDataPartyDo.getJobId(), fDataPartyDo.getId(), fNodePartyDo, false, userDo);
            }
        }
    }

    public DataSchema dataSchema(FNodePartyDo fNodePartyDo, String jobId) throws URISyntaxException {
        DataSchema dataSchema = new DataSchema();
        Map<String, Object> map = new HashMap<>(5);
        map.put("job_id", jobId);
        URI uri = new URI(fNodePartyDo.getFlowAddress());
        String data = fateApiService.history(uri, map);
        JsonObject res = JsonUtil.toJsonObject(data);
        if (data == null || res == null) {
            return null;
        }
        JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
        if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
            JsonElement datalist = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_DATA.getCodeName());
            List<LinkedTreeMap> list = JsonUtil.deserializeAsList(datalist.getAsJsonArray(), ArrayList.class);
            if (list != null & list.size() > 0) {
                LinkedTreeMap<String, LinkedTreeMap> jobValue = list.get(0);
                LinkedTreeMap head = (LinkedTreeMap) jobValue.get(jobId).get("schema");
                LinkedTreeMap count = (LinkedTreeMap) jobValue.get(jobId).get("upload_info");
                dataSchema.setSid(head.get("sid").toString());
                dataSchema.setHeader(head.get("header").toString());
                dataSchema.setCount(Math.round((double) count.get("upload_count")));
            }
        } else {
            logger.error("data schema 查看 失败:{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
            return null;
        }
        return dataSchema;
    }

    public List<ComponentVos> query(FModelJobDo modelLogDo, Integer types) throws URISyntaxException {
        List<ComponentVos> result = new ArrayList<>();
        FNodePartyDo fNodePartyDo = nodePartyService.findAll().get(0);
        List<FComponentDo> fComponentDos = componentService.findAll();
        Map<String, FComponentDo> fComponentDoMap = fComponentDos.stream().collect(Collectors.toMap(FComponentDo::getComponent, e -> e));
        Map<String, Object> map = new HashMap<>(5);
        map.put("job_id", modelLogDo.getJobId());
        map.put("party_id", fNodePartyDo.getPartyId());
        if (PREDICT_CODE.getCodeName().equals(modelLogDo.getTypes())) {
            FModelJobDo fModelJobDo = modelJobService.findById(modelLogDo.getModelConfig());
            if (fModelJobDo == null) {
                logger.error("查询失败:{}", "模型异常");
                return null;
            }
            map.put("role", fNodePartyDo.getId().equals(fModelJobDo.getHost()) ? "guest" : "host");
        } else {
            map.put("role", types == 1 ? "guest" : "host");
        }
        URI uri = new URI(fNodePartyDo.getFlowAddress());
        String value = fateApiService.taskQuery(uri, map);
        modelLogDo.setHost(fNodePartyDo.getName());
        JsonObject res = JsonUtil.toJsonObject(value);
        if (value == null || res == null) {
            logger.error("查询失败:{}", "服务异常");
            return null;
        }
        JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //判断请求是否成功
        List<FDataPartyDo> hlist = dataPartyService.selectByModelId(Arrays.asList(fNodePartyDo.getId()), modelLogDo.getModelId(), types);
        if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
            JsonElement data = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_DATA.getCodeName());
            List<LinkedTreeMap> list = JsonUtil.deserializeAsList(data.getAsJsonArray(), ArrayList.class);
            for (LinkedTreeMap f : list) {
                ComponentVos componentVos = new ComponentVos();
                FComponentDo fComponentDo = fComponentDoMap.get(f.get("f_component_name").toString().substring(0, f.get("f_component_name").
                        toString().lastIndexOf("_")));
                if (fComponentDo != null) {
                    componentVos.setComponentId(fComponentDo.getId());
                    if (types == 2 && fComponentDo.getTypes() == EVL_TYPE) {

                    } else {
                        if (fComponentDo.getComponentEn().toLowerCase().equals(Data_COMPONENT.getComponentName())) {
                            if (hlist != null && hlist.size() > 0) {
                                fComponentDo.setName(fComponentDo.getName().concat("(").concat(hlist.get(0).getName()).concat(")"));
                            }
                        }
                        componentVos.setComponent((types == 2 && fComponentDo.getTypes() == ALGO_TYPE) ? "模型预测" : fComponentDo.getName());
                        if (!"null".equals(String.valueOf(f.get("f_end_time")))) {
                            componentVos.setEndTime(Timestamp.valueOf(format.format(f.get("f_end_time"))));
                            componentVos.setTimes(Timestamp.valueOf(format.format(f.get("f_end_time"))).getTime() -
                                    Timestamp.valueOf(format.format(f.get("f_start_time"))).getTime());
                        }
                        componentVos.setComponentName(f.get("f_component_name").toString());
                        componentVos.setJobId(f.get("f_job_id").toString());
                        componentVos.setPartyId(f.get("f_party_id").toString());
                        componentVos.setRole(f.get("f_role").toString());
                        componentVos.setStatus(f.get("f_party_status").toString());
                        componentVos.setTypes((types == 2 && fComponentDo.getTypes() == ALGO_TYPE) ? 3 : fComponentDo.getTypes());
                        if (!"null".equals(String.valueOf(f.get("f_start_time")))) {
                            componentVos.setStartTime(Timestamp.valueOf(format.format(f.get("f_start_time"))));
                        } else {
                            componentVos.setStartTime(ComUtil.getCurTimestamp());
                        }
                        result.add(componentVos);
                    }

                }
            }
            if (result.size() > 0 && result.stream().filter(e -> "success".equals(e.getStatus())).count() == result.size()) {
                FModelJobDo fModelJobDo = modelJobService.findById(modelLogDo.getId());
                fModelJobDo.setStatus(1);
                modelJobService.updateNoNull(fModelJobDo);
            }
            return result.stream().sorted(Comparator.comparing(ComponentVos::getStartTime)).collect(Collectors.toList());
        } else {
            logger.error("查询失败:{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
            return null;
        }
    }

    public FModelJobDo stop(FModelJobDo info, FNodePartyDo fNodePartyDo) throws Exception {
        Map<String, Object> map = new HashMap<>(5);
        map.put("job_id", info.getJobId());
        URI uri = new URI(fNodePartyDo.getFlowAddress());
        String value = fateApiService.jobStop(uri, map);
        JsonObject res = JsonUtil.toJsonObject(value);
        if (value == null || res == null) {
            info.setStatus(FAIL_CODE.getCodeName());
            logger.error("停止失败,api:{}异常", "stop");
            return info;
        }
        JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
        //判断请求是否成功
        if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
            info.setStatus(CANCELED_CODE.getCodeName());
        } else {
            info.setStatus(FAIL_CODE.getCodeName());
            logger.error("停止失败:{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
        }
        if (!StringUtils.isBlank(info.getId())) {
            modelJobService.updateBean(info);
        }
        return info;
    }


    public FModelJobDo deploy(FModelJobDo info, FNodePartyDo fNodePartyDo) throws URISyntaxException {
        Map<String, Object> map = JsonUtil.gson().fromJson(info.getModelConfig(), Map.class);
        Map<String, Object> deploy = new HashMap<>();
        deploy.put("model_id", map.get("model_id"));
        deploy.put("model_version", map.get("model_version").toString().replace("\"", ""));
        URI uri = new URI(fNodePartyDo.getFlowAddress());
        String value = fateApiService.modelDeploy(uri, deploy);
        JsonObject res = JsonUtil.toJsonObject(value);
        if (value == null || res == null) {
            info.setDeploy(FAIL_CODE.getCodeName());
            logger.error("模型发布失败,api:{}异常", map);
            return info;
        }
        JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
        //判断请求是否成功
        if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
            JsonElement data = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_DATA.getCodeName());
            JsonElement modelInfo = data.getAsJsonObject().get("model_version");
            map.put("model_version", modelInfo.toString());
            info.setModelConfig(JsonUtil.toJson(map).replaceAll("\n", "").trim());
            info.setDeploy(SUCCESS_CODE.getCodeName());
            modelJobService.updateNoNull(info);
        } else {
            info.setDeploy(FAIL_CODE.getCodeName());
            logger.error("模型发布失败:{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
        }
        return info;
    }

    public FDataPartyDo deleteTable(FDataPartyDo info, FNodePartyDo fNodePartyDo) throws URISyntaxException {
        Map<String, Object> map = new HashMap<>(5);
        map.put("namespace", info.getNamespace());
        map.put("table_name", info.getTableName());
        URI uri = new URI(fNodePartyDo.getFlowAddress());
        String value = fateApiService.tableDelete(uri, map);
        JsonObject res = JsonUtil.toJsonObject(value);
        if (value == null || res == null) {
            info.setStatus(FAIL_CODE.getCodeName());
            logger.error("删除联邦学习表失败,api:{}异常", map);
            return info;
        }
        JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
        //判断请求是否成功
        if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
            info.setStatus(SUCCESS_CODE.getCodeName());
        } else {
            info.setStatus(FAIL_CODE.getCodeName());
            logger.error("删除失败:{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
        }
        return info;
    }

    public FModelJobDo rerun(FModelJobDo info, FNodePartyDo fNodePartyDo) throws URISyntaxException {
        Map<String, Object> map = new HashMap<>(5);
        map.put("job_id", info.getJobId());
        map.put("component_name", "pipeline");
        URI uri = new URI(fNodePartyDo.getFlowAddress());
        String value = fateApiService.jobRerun(uri, map);
        JsonObject res = JsonUtil.toJsonObject(value);
        if (value == null || res == null) {
            info.setStatus(FAIL_CODE.getCodeName());
            logger.error("重启失败,api:{}异常", map);
            return info;
        }
        JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
        //判断请求是否成功
        if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
            info.setStatus(RUNNING_CODE.getCodeName());
        } else {
            info.setStatus(FAIL_CODE.getCodeName());
            logger.error("重启失败:{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
        }
        modelJobService.updateBean(info);
        return info;
    }

    public void log(FModelJobDo info, FNodePartyDo fNodePartyDo, HttpServletResponse response) throws Exception {
        Map<String, Object> map = new HashMap<>(2);
        map.put("job_id", info.getJobId());
        URI uri = new URI(fNodePartyDo.getFlowAddress());
        Response download = fateApiService.logDownload(uri, map);
        if (download.status() == 200) {
            response.setHeader("Content-disposition", String.format("attachment;filename=%s_log.tar.gz", info.getJobId()));
            InputStream in = download.body().asInputStream();
            int bytes;
            byte[] buffer = new byte[1024];
            while ((bytes = in.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytes);
            }
        }
    }

    public Map data(ComponentVos components) throws URISyntaxException {
        Map<String, Object> map = new HashMap<>(5);
        FNodePartyDo fNodePartyDo = nodePartyService.findAll().get(0);
        map.put("job_id", components.getJobId());
        map.put("component_name", components.getComponentName());
        if (StringUtils.isNotBlank(components.getPartyId())) {
            map.put("party_id", components.getPartyId());
        } else {
            map.put("party_id", 0);
        }
        if (StringUtils.isNotBlank(components.getRole())) {
            map.put("role", components.getRole());
        }
        URI uri = new URI(fNodePartyDo.getFlowAddress());
        String value = fateApiService.outputData(uri, map);
        JsonObject res = JsonUtil.toJsonObject(value);
        if (value == null || res == null) {
            return null;
        }
        JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
        //判断请求是否成功
        if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
            return JsonUtil.gson().fromJson(value, Map.class);
        } else {
            logger.error("获取组件异常:{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
            return null;
        }
    }

    public List<Object> score(ComponentVos components) throws URISyntaxException {
        Map<String, Object> map = new HashMap<>(8);
        FNodePartyDo fNodePartyDo = nodePartyService.findAll().get(0);
        map.put("job_id", components.getJobId());
        map.put("party_id", components.getPartyId());
        map.put("metric_name", components.getComponentName().concat("_0"));
        map.put("metric_namespace", "train");
        map.put("role", "guest");
        map.put("component_name", "evaluation_0");
        URI uri = new URI(fNodePartyDo.getFlowAddress());
        String value = fateApiService.metricData(uri, map);
        JsonObject res = JsonUtil.toJsonObject(value);
        if (value == null || res == null) {
            return null;
        }
        JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
        //判断请求是否成功
        if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
            JsonElement data = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_DATA.getCodeName());
            return JsonUtil.deserializeAsList(data.getAsJsonArray(), ArrayList.class);
        } else {
            logger.error("获取组件loss数据异常:{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
        }
        return null;
    }

    public Map model(ComponentVos components) throws URISyntaxException {
        Map<String, Object> map = new HashMap<>(5);
        FNodePartyDo fNodePartyDo = nodePartyService.findAll().get(0);
        map.put("component_name", components.getComponentName());
        if (StringUtils.isNotBlank(components.getRole())) {
            map.put("role", components.getRole());
        }
        map.put("job_id", components.getJobId());
        if (StringUtils.isNotBlank(components.getPartyId())) {
            map.put("party_id", components.getPartyId());
        }
        URI uri = new URI(fNodePartyDo.getFlowAddress());
        String value = fateApiService.metricModel(uri, map);
        JsonObject res = JsonUtil.toJsonObject(value);
        if (value == null || res == null) {
            return null;
        }
        JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
        //判断请求是否成功
        if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
            return JsonUtil.gson().fromJson(value, Map.class);
        } else {
            logger.error("获取组件model数据异常:{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
            return null;
        }
    }

    public void export(String modelJobId, HttpServletResponse response) throws Exception {
        FModelJobDo fModelJobDo = modelJobService.findById(modelJobId);
        Map<String, Object> map = new HashMap<>(5);
        FNodePartyDo fNodePartyDo = nodePartyService.findAll().get(0);
        map.put("role", fNodePartyDo.getId().equals(fModelJobDo.getHost()) ? "guest" : "host");
        Map fromJson = JsonUtil.gson().fromJson(fModelJobDo.getModelConfig(), Map.class);
        map.put("model_id", fromJson.get("model_id"));
        map.put("model_version", fromJson.get("model_version").toString().replace("\"", ""));
        map.put("party_id", fNodePartyDo.getPartyId());
        URI uri = new URI(fNodePartyDo.getFlowAddress());
        Response download = fateApiService.modelExport(uri, map);
        if (download.status() == 200) {
            response.setHeader("Content-disposition", String.format("attachment;filename=%s_model.tar.gz", fModelJobDo.getJobId()));
            InputStream in = download.body().asInputStream();
            int bytes;
            byte[] buffer = new byte[1024];
            while ((bytes = in.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytes);
            }
        }
    }

    public void download(String jobId, String partId, String componentName, String role, HttpServletResponse
            response) {
        Map<String, Object> map = new HashMap<>(5);
        FNodePartyDo fNodePartyDo = nodePartyService.findByPartId(partId);
        map.put("job_id", jobId);
        map.put("component_name", componentName);
        if (StringUtils.isNotBlank(partId)) {
            map.put("party_id", partId);
        }
        if (StringUtils.isNotBlank(role)) {
            map.put("role", role);
        }
        String urlInfo = fNodePartyDo.getFlowAddress().concat("/v1/tracking/component/output/data/download");
        String simpleJson = JsonUtil.toSimpleJson(map);
        logger.debug("组件数据下载api:{}", urlInfo);
        new HttpOperaterUtil().sendHttpRequest(urlInfo, simpleJson, jobId.concat("_data_").concat(componentName), response);
    }
//    public void download(String jobId, String partId, String componentName, String role, HttpServletResponse
//            response) throws Exception {
//        Map<String, Object> map = new HashMap<>(5);
//        FNodePartyDo fNodePartyDo = nodePartyService.findByPartId(partId);
//        map.put("job_id", jobId);
//        map.put("component_name", componentName);
//        if (StringUtils.isNotBlank(partId)) {
//            map.put("party_id", partId);
//        }
//        if (StringUtils.isNotBlank(role)) {
//            map.put("role", role);
//        }
//        URI uri = new URI(fNodePartyDo.getFlowAddress());
//        Response download = fateApiService.dataDownload(uri, map);
//        System.out.printf(uri + "download" + download);
//        if (download.status() == 200) {
//            response.setHeader("Content-disposition", String.format("attachment;filename=%s_data.tar.gz", jobId));
//            InputStream in = download.body().asInputStream();
//            int bytes;
//            byte[] buffer = new byte[1024];
//            while ((bytes = in.read(buffer)) != -1) {
//                response.getOutputStream().write(buffer, 0, bytes);
//            }
//        }
//    }

    public Map<String, Object> parameters(ComponentVos componens) throws URISyntaxException {
        List<Object> t = new ArrayList();
        List<Object> v = new ArrayList();
        Map<String, Object> result = new HashMap(10);
        FNodePartyDo fNodePartyDo = nodePartyService.findByPartId(componens.getPartyId());
        Map<String, Object> map = new HashMap<>(5);
        map.put("job_id", componens.getJobId());
        map.put("component_name", componens.getComponentName());
        map.put("party_id", componens.getPartyId());
        map.put("role", componens.getRole());
        URI uri = new URI(fNodePartyDo.getFlowAddress());
        String value = fateApiService.componentMetrics(uri, map);
        JsonObject res = JsonUtil.toJsonObject(value);
        if (value == null || res == null) {
            return null;
        }
        JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
        //判断请求是否成功
        if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
            Map<String, Object> news = JsonUtil.gson().fromJson(value, Map.class);
            String datav = String.valueOf(news.get("data"));
            Map<String, Object> data = JsonUtil.gson().fromJson(datav, Map.class);
            if (!data.isEmpty()) {
                String trainV = String.valueOf((data.get("train")));
                List<Object> list = Arrays.asList(METRICS.getCodeName().split(","));
                if (StringUtils.isNotBlank(trainV)) {
                    List<Object> train = JsonUtil.gson().fromJson(trainV, List.class);
                    t.add(train.get(0));
                    train.stream().forEach(x -> {
                        list.stream().forEach(y -> {
                            if (x.toString().endsWith(y.toString())) {
                                t.add(x);
                            }
                        });
                    });
                }
                result.put("train", t);
                String validateV = String.valueOf((data.get("validate")));
                if (StringUtils.isNotBlank(validateV)) {
                    List<Object> validate = JsonUtil.gson().fromJson(validateV, List.class);
                    if (validate != null && validate.size() > 0) {
                        v.add(validate.get(0));
                        validate.stream().forEach(x -> {
                            list.stream().forEach(y -> {
                                if (x.toString().endsWith(y.toString())) {
                                    v.add(x);
                                }
                            });
                        });
                    }
                }
                result.put("validate", v);
            }
            return result;
        } else {
            logger.error("获取组件loss参数异常:{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
            return result;
        }
    }

    public Map<String, Object> loss(ComponentVos component_name) throws URISyntaxException {
        FNodePartyDo fNodePartyDo = nodePartyService.findAll().get(0);
        Map<String, Object> result = new HashMap();
        for (String name : component_name.getName()) {
            Map<String, Object> map = new HashMap<>(5);
            map.put("job_id", component_name.getJobId());
            map.put("party_id", component_name.getPartyId());
            map.put("metric_name", name);
            map.put("metric_namespace", component_name.getNamespace());
            map.put("role", component_name.getRole());
            map.put("component_name", component_name.getComponentName());
            URI uri = new URI(fNodePartyDo.getFlowAddress());
            String value = fateApiService.metricData(uri, map);
            JsonObject res = JsonUtil.toJsonObject(value);
            if (value == null || res == null) {
                return null;
            }
            JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
            //判断请求是否成功
            if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
                result.put(name, JsonUtil.gson().fromJson(value, Map.class));
            } else {
                logger.error("获取组件loss数据异常:{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
                return result;
            }
        }
        return result;
    }

    public Map<String, Object> jobLogCat(ComponentVos component_name) throws URISyntaxException {
        FNodePartyDo fNodePartyDo = nodePartyService.findAll().get(0);
        Map<String, Object> result = new HashMap();
        Map<String, Object> map = new HashMap<>(5);
        map.put("job_id", component_name.getJobId());
        map.put("log_type", "jobSchedule");
        URI uri = new URI(fNodePartyDo.getFlowAddress());
        String value = fateApiService.logCat(uri, map);
        JsonObject res = JsonUtil.toJsonObject(value);
        if (value == null || res == null) {
            return null;
        }
        JsonElement code = res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_CODE.getCodeName());
        //判断请求是否成功
        if (ApiCodeEnum.API_SUCCESS_CODE.getCodeName().equals(code.toString())) {
            return JsonUtil.gson().fromJson(value, Map.class);
        } else {
            logger.error("获取组件loss数据异常:{}", res.getAsJsonObject().get(ApiCodeEnum.API_RESULT_ERR.getCodeName()).toString());
            return result;
        }
    }
}
