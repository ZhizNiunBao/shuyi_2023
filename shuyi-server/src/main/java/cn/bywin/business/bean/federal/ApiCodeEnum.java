package cn.bywin.business.bean.federal;

/**
 * @Description
 * @Author wangh
 * @Date 2021-07-27
 */
public enum ApiCodeEnum {
    //api 响应code结果
    API_SUCCESS_CODE("0"),
    //api 响应code结果
    JOB_RUNNING_CODE("2"),
    //api 响应code结果
    JOB_WAIT_CODE("3"),
    //api 响应内容 结果key
    API_RESULT_DATA("data"),
    //api 响应内容 code key
    API_RESULT_CODE("retcode"),
    //api 响应内容 信息 key
    API_RESULT_ERR("retmsg"),
    //api 任务类型
    SUBMIT_TYPE("train"),
    PREDICT_TYPE("predict"),
    //运行模式 0 为 eggroll 1 为spark rabbitmq 2 为 spark pulsar
    BACKEND("0"),
    //分区
    PARTITION("1"),
    //数据存储模式 1 为mysql
    WORK_MODE("1"),
    ROLE_HOST("host"),
    //模型预测图表支持类型
    METRICS("ks_fpr,ks_tpr,lift,gain,accuracy,precision,recall,roc");


    private String codeName;

    ApiCodeEnum(String codeName) {
        this.codeName = codeName;
    }

    public String getCodeName() {
        return codeName;
    }


}
