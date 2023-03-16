package cn.bywin.business.common.result;

public class SingleResult<T> extends CommonResult {

    private static final long serialVersionUID = -5583100669483640036L;
    private T data;

    public SingleResult() {
    }

    public SingleResult(String code, String message) {
        super(code, message);
    }

    public SingleResult(String code, String message, T data) {
        super(code, message);
        this.data = data;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SingleResult{" +
            "data=" + data +
            '}';
    }
}
