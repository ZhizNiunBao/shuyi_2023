package cn.bywin.business.bean.federal;
/**
 * @Description
 * @Author  wangh
 * @Date 2021-07-27
 */
public enum StatusCodeEnum {
    //api 响应code结果
    SUCCESS_CODE(1),
    //api 响应code结果
    RUNNING_CODE(2),
    //api 响应code结果
    FAIL_CODE(0),
    //api 响应内容 结果key
    WAITING_CODE(3),
    //api 响应内容 code key
    CANCELED_CODE(4),
    // 联邦学习 预测类型
    PREDICT_CODE(2);

    private Integer codeName;

    StatusCodeEnum(Integer codeName) {
        this.codeName = codeName;
    }

    public Integer getCodeName() {
        return codeName;
    }


}
