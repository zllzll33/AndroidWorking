package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : luofang
 * Author     : Android-mjc
 * Date       : 2016/7/8 11:18
 */
public class MessageCenter {
    public Result result;
    public String       status;
    public String       summary;
    public class Result{
        public int pindex;
        public int totalpage;
        public String notifytitle;
        public ArrayList<Json> data;
        public class Json{
            public String notifydate;
            public String notifycode;
            public String notifykind;
            public String relatecode;
            public String notifytitle;
        }
    }

}