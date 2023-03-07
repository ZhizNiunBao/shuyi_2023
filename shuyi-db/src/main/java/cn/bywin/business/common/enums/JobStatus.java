package cn.bywin.business.common.enums;

/**
 * 任务运行状态
 */
public enum JobStatus {

    UN_START(0, "任务未启动"),SUCCESS(1, "任务执行成功"),FAILD(2, "任务执行失败"),
    FORCE_STOP(5, "任务强制停止"),RUNNING(9, "任务正在运行中"),TASK_RUNNING(0, "任务正在运行中");

    private int status;
    private String message;

    public int status() {
        return this.status;
    }

    public String message() {
        return this.message;
    }

    JobStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
