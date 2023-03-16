package cn.bywin.business.common.result;

import java.util.List;

public class ListResult<T> extends CommonResult {

    private static final long serialVersionUID = -7494436587763513567L;
    private ListData<T> data;

    public ListResult() {
    }

    public ListResult(String code, String message) {
        super(code, message);
    }

    public ListResult(String code, String message, List<T> dataList) {
        super(code, message);
        this.data = new ListData();
        this.data.setDataList(dataList);
        this.data.setCurrentPage(1);
        this.data.setTotalPage(1);
    }

    public ListResult(String code, String message, List<T> dataList, Integer currentPage,
        Integer rows, Integer total) {
        super(code, message);
        this.data = new ListData();
        this.data.setDataList(dataList);
        if (null == currentPage) {
            currentPage = 1;
        }

        this.data.setCurrentPage(currentPage);
        this.data.setTotalPage(getTotalPage(rows, total));
        this.data.setTotalCount(total);
        this.data.setRows(rows);
    }

    public ListResult(String code, String message, List<T> dataList, Integer currentPage,
        Integer rows, Integer total, List<T> meta) {
        super(code, message);
        this.data = new ListData();
        this.data.setDataList(dataList);
        this.data.setMeta(meta);

        this.data.setCurrentPage(currentPage);
        if (rows != null && total != null) {
            this.data.setTotalPage(getTotalPage(rows, total));
        }
        this.data.setTotalCount(total);
        this.data.setRows(rows);
    }

    public static int getTotalPage(Integer rows, Integer total) {
        if (null != rows && null != total && rows >= 1 && total >= 1 && rows < total) {
            int totalPage = total / rows;
            if (total % rows > 0) {
                ++totalPage;
            }

            return totalPage;
        } else {
            return 1;
        }
    }

    public ListData<T> getData() {
        return this.data;
    }

    public void setData(ListData<T> data) {
        this.data = data;
    }
}
