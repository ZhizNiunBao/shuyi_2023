package cn.bywin.business.controller.interf;


import cn.bywin.business.bean.bydb.TTruModelDo;
import cn.bywin.business.bean.bydb.TTruModelTaskLogDo;
import cn.bywin.business.bean.bydb.TaskStatus;
import cn.bywin.business.common.base.BaseController;
import cn.bywin.business.common.base.ResponeMap;
import cn.bywin.business.common.util.ComUtil;
import cn.bywin.business.common.util.HttpRequestUtil;
import cn.bywin.business.service.bydb.TruModelService;
import cn.bywin.business.service.bydb.TruModelTaskLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(value = {"*"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        maxAge = 3600)
@RestController
@Api(tags = "trumodel-可信模型运行对外接口-modelinterf")
@RequestMapping("/v1/modelinterf")
public class ModelInterfController extends BaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TruModelService bydbModelService;

    @Autowired
    private TruModelTaskLogService modelTaskLogService;

    @ApiOperation(value = "更新可信模型运行结果", notes = "更新可信模型运行结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", value = "联邦分析Id", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "taskResult", value = "成败 1成功 0失败", dataType = "Integer", required = true, paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "String", required = false, paramType = "query"),
            @ApiImplicitParam(name = "note", value = "说明", dataType = "String", required = false, paramType = "query"),
    })
    @RequestMapping(value = "/modelresult", method = {RequestMethod.POST})
    @ResponseBody
    public Object modelresult(HttpServletRequest request) {
        ResponeMap resMap = this.genResponeMap();
        try {

            HttpRequestUtil hru = HttpRequestUtil.parseHttpRequest(request);
            logger.info("{}", hru.getAllParaData());
            TTruModelDo modelDo = bydbModelService.findById(hru.getNvlPara("modelId"));

            if (modelDo == null) {
                return resMap.setErr("模型不存在").getResultMap();
            }
            String endTime = hru.getNvlPara("endTime");
            String startTime = hru.getNvlPara("startTime");
            String taskNo = hru.getNvlPara("taskNo");
            String taskResult = hru.getNvlPara("taskResult");
            String note = hru.getNvlPara("note");

            TTruModelDo info = new TTruModelDo();
            info.setId(modelDo.getId());

            List<TTruModelTaskLogDo> unfinishedList = modelTaskLogService.findUnfinished(modelDo.getId());
            Date denddate = null;
            TTruModelTaskLogDo logDo = null; //
            if (StringUtils.isNotBlank(endTime)) { //结束
                denddate = ComUtil.strToDate(endTime);
                //TBydbDatasetDo old = new TBydbDatasetDo();
                //MyBeanUtils.copyBeanNotNull2Bean(datasetDo, old);
                if ("1".equals(taskResult)) {
                    info.setCacheFlag(TaskStatus.SUCCESS.getStatus());
                    info.setLastRunTime(denddate);
                } else {
                    info.setCacheFlag(TaskStatus.FAIL.getStatus());
                }
                if (unfinishedList.size() > 0) {
                    TTruModelTaskLogDo logDo1 = unfinishedList.get(0);
                    logDo1.setEndTime(denddate);
                    if ("1".equals(taskResult)) {
                        logDo1.setCacheFlag(TaskStatus.SUCCESS.getStatus());
                    } else {
                        logDo1.setCacheFlag(TaskStatus.FAIL.getStatus());
                    }
                    logDo1.setRemark(note);
                    logDo1.setModifiedTime(ComUtil.getCurTimestamp());
                    for (int i = 1; i < unfinishedList.size(); i++) {
                        TTruModelTaskLogDo tmp = unfinishedList.get(i);
                        tmp.setCacheFlag(TaskStatus.FAIL.getStatus());
                        tmp.setEndTime(denddate);
                        tmp.setRemark("超时停止");
                        tmp.setModifiedTime(ComUtil.getCurTimestamp());
                    }
                } else {
                    logDo = new TTruModelTaskLogDo();
                    logDo.setModelId(modelDo.getId());
                    logDo.setId(ComUtil.genId());
                    logDo.setCreatedTime(ComUtil.getCurTimestamp());
                    if ("1".equals(taskResult)) {
                        logDo.setCacheFlag(TaskStatus.SUCCESS.getStatus());
                    } else {
                        logDo.setCacheFlag(TaskStatus.FAIL.getStatus());
                    }
                    logDo.setRemark(note);
                    if (StringUtils.isNotBlank(endTime)) { //结束
                        logDo.setStartTime(ComUtil.strToDate(endTime));
                    } else {
                        logDo.setStartTime(logDo.getCreatedTime());
                    }
                    logDo.setEndTime(denddate);
                }

                bydbModelService.updateBeanWithLog(info, logDo, unfinishedList);

            } else if (StringUtils.isNotBlank(startTime)) { //开始
                Date dstartTime = ComUtil.strToDate(startTime);
                //TBydbDatasetDo old = new TBydbDatasetDo();
                //MyBeanUtils.copyBeanNotNull2Bean(info, old);
                if ("1".equals(taskResult)) { //启动成功
                    info.setCacheFlag(TaskStatus.START.getStatus());
                } else {
                    info.setCacheFlag(TaskStatus.FAIL.getStatus());
                }
                if (unfinishedList.size() > 0) {
                    for (int i = 0; i < unfinishedList.size(); i++) {
                        TTruModelTaskLogDo tmp = unfinishedList.get(i);
                        tmp.setCacheFlag(TaskStatus.FAIL.getStatus());
                        tmp.setEndTime(ComUtil.getCurTimestamp());
                        tmp.setModifiedTime(ComUtil.chgToTimestamp(tmp.getEndTime()));
                        tmp.setRemark("超时停止");
                    }
                }

                logDo = new TTruModelTaskLogDo();
                logDo.setModelId(modelDo.getId());
                logDo.setId(ComUtil.genId());
                logDo.setStartTime(dstartTime);
                logDo.setCreatedTime(ComUtil.getCurTimestamp());
                if ("1".equals(taskResult)) {
                    logDo.setCacheFlag(TaskStatus.START.getStatus());
                    logDo.setRemark(note);
                } else {
                    logDo.setEndTime(ComUtil.getCurTimestamp());
                    if (StringUtils.isNotBlank(note)) {
                        logDo.setRemark(note + "\r\n" + "启动失败");
                    } else {
                        logDo.setRemark("启动失败");
                    }
                    logDo.setCacheFlag(TaskStatus.FAIL.getStatus());
                }
                bydbModelService.updateBeanWithLog(info, logDo, unfinishedList);
            } else {
                return resMap.setErr("任务开始和结束时间不能同时为空").getResultMap();
            }
            resMap.setOk("更新缓存结果成功");

        } catch (Exception ex) {
            resMap.setErr("更新缓存结果失败");
            logger.error("更新缓存结果异常:", ex);
        }
        return resMap.getResultMap();
    }
}
