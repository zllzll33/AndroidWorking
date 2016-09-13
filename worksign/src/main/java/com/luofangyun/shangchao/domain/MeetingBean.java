package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * 会议信息展示
 */
public class MeetingBean {
    public Result result;
    public String status;
    public String summary;
    public class Result{
        public int pindex;
        public int totalpage;
        public ArrayList<Json> data;
        public class Json{
            public String meetdate;
            public String meetaddress;
            public String mid;
            public String meettheme;
            public String phones;
            public String endtime;
            public String created;
            public String updateuser;
            public String updatetime;
            public String meetcode;
            public int statu;
            public String companyid;
            public String labelname;
            public String meetname;
            public String starttime;
            public String labels;
        }
    }
}

