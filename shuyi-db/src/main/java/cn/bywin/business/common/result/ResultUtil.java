package cn.bywin.business.common.result;

import cn.bywin.business.common.enums.ErrorCodeConstants;
import cn.bywin.business.common.except.ErrorCode;
import java.util.List;

/**
 * Created by Administrator on 2018/6/29 0029.
 */
public class ResultUtil {

    private static final String SUCCESS_CODE;
    private static final String SUCCESS_MESSAGE;
    private static final String ERROR_CODE;
    private static final String ERROR_MESSAGE;

    public ResultUtil() {
    }

    public static CommonResult getResult() {
        return new CommonResult(SUCCESS_CODE, SUCCESS_MESSAGE);
    }

    public static CommonResult getResult(String code, String message) {
        return new CommonResult(code, message);
    }

    public static <T> SingleResult<T> successSingleResult(T t) {
        return new SingleResult(SUCCESS_CODE, SUCCESS_MESSAGE, t);
    }

    public static <T> SingleResult<T> successSingleResult(T t, String message) {
        return new SingleResult(SUCCESS_CODE, message, t);
    }

    public static <T> SingleResult<T> successSingleResult(T t, String code, String message) {
        return new SingleResult(code, message, t);
    }

    public static <T> SingleResult<T> errorSingleResult(T t, String message) {
        return new SingleResult(ERROR_CODE, message, t);
    }

    public static <T> SingleResult<T> errorSingleResult(T t) {
        return new SingleResult(ERROR_CODE, ERROR_MESSAGE, t);
    }

    public static <T> SingleResult<T> errorSingleResult(String message) {
        return new SingleResult(ERROR_CODE, message);
    }

    public static <T> SingleResult<T> errorSingleResult(String code, String message) {
        return new SingleResult(code, message);
    }

    public static <T> SingleResult<T> errorSingleResult(ErrorCode t) {
        return new SingleResult(t.getCode(), t.getMessage());
    }

    public static <T> ListResult<T> errorListResult(List<T> dataList, String message) {
        return new ListResult(ERROR_CODE, message, dataList);
    }

    public static <T> ListResult<T> successListResult(List<T> dataList) {
        return new ListResult(SUCCESS_CODE, SUCCESS_MESSAGE, dataList);
    }

    public static <T> ListResult<T> errorListResult(String message) {
        return new ListResult(ERROR_CODE, message, null);
    }

    public static <T> ListResult<T> successListResult(List<T> dataList, Integer currentPage,
        Integer rows, Integer total) {
        return new ListResult(SUCCESS_CODE, SUCCESS_MESSAGE, dataList, currentPage, rows, total);
    }

    public static <T> ListResult<T> successListResult(List<T> dataList, Integer currentPage,
        Integer rows, Integer total, List<T> meta) {
        return new ListResult(SUCCESS_CODE, SUCCESS_MESSAGE, dataList, currentPage, rows, total,
            meta);
    }

    static {
        SUCCESS_CODE = ErrorCodeConstants.SUCCESS.getCode();
        SUCCESS_MESSAGE = ErrorCodeConstants.SUCCESS.getMessage();
        ERROR_CODE = ErrorCodeConstants.ERROR.getCode();
        ERROR_MESSAGE = ErrorCodeConstants.ERROR.getMessage();
    }
}
