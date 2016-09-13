package com.luofangyun.shangchao.domain;

/**
 * 团队考勤
 */

public class AttAreaBean {
    public String summary;
    public String status;
    public Result result;
    public class Result {
        public int sb_normal;
        public int sb_late;
        public int sb_outrange;
        public int sb_nosignin;
        public int xb_normal;
        public int xb_early;
        public int xb_outrange;
        public int xb_nosignin;
        public int leave;
    }
}
