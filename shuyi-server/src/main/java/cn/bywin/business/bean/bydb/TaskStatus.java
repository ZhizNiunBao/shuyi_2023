package cn.bywin.business.bean.bydb;

public enum TaskStatus {

    SUBMIT(1,"提交"),
    START(2,"开始"),
    SUCCESS(3,"成功"),
    FAIL(9,"失败");
    int status;
    String msg;

    TaskStatus(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
