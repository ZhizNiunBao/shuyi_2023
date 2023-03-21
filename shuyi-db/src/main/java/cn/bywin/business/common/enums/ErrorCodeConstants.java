package cn.bywin.business.common.enums;

import cn.bywin.business.common.except.ErrorCode;

/**
 * @author zzm
 */
public interface ErrorCodeConstants {

    // ========== 通用错误码 ==========
    ErrorCode SUCCESS = new ErrorCode("0", "操作成功");
    ErrorCode ERROR = new ErrorCode("500", "操作失败");

    // ========== 客户端错误段 ==========

    ErrorCode BAD_REQUEST = new ErrorCode("400", "请求参数不正确");
    ErrorCode NOT_FOUND = new ErrorCode("404", "请求未找到");
    ErrorCode METHOD_NOT_ALLOWED = new ErrorCode("405", "请求方法不正确");

    // ========== 组件异常 ==========
    ErrorCode HETU_CONNECTION_ERROR = new ErrorCode("601", "Hetu 连接异常");
    ErrorCode HETU_EXECUTE_ERROR = new ErrorCode("602", "Hetu 语句执行异常");

}
