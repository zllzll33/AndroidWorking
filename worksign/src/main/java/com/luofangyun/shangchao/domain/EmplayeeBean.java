package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * Created by win7 on 2016/9/12.
 */
public class EmplayeeBean {
    public Result result;
    public String status;
    public String summary;
    public class Result{
        public int             pindex;
        public int             totalpage;
        public ArrayList<Emplayee> data;
    }
    public static class Emplayee{
        public String empname,deptcode,empphone,deptname;
    }
}
