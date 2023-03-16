package cn.bywin.business.common.result;

import lombok.Data;

@Data
class Pages {

    private String psize;
    private String tcount;
    private String pno;
    private String tsize;

    public Pages(String psize, String tcount, String pno, String tsize) {
        this.psize = psize;
        this.tcount = tcount;
        this.pno = pno;
        this.tsize = tsize;
    }
}
