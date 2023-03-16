package cn.bywin.business.common.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageResult<T> implements Serializable {

    private T datas;
    private List<Pages> pages;

    public PageResult() {
    }

    public void setPages(String psize, String tcount, String pno, String tsize) {
        Pages page = new Pages(psize, tcount, pno, tsize);
        pages = new ArrayList<>();
        pages.add(page);
    }

    public T getDatas() {
        return datas;
    }

    public void setDatas(T datas) {
        this.datas = datas;
    }

    public List<Pages> getPages() {
        return pages;
    }
}
