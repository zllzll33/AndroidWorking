package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * Created by win7 on 2016/9/18.
 */
public class SetPatrolLineBean  {
    public Result result;
    public String status;
    public String summary;
    public class Result{
        public int pindex;
        public int totalpage;
        public ArrayList<Json> data;
        public class Json{
            public String pointname;
            public String pointcode,labelcode;
        }
    }
}
