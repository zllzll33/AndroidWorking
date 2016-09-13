package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : luofang
 * Author     : Android-mjc
 * Date       : 2016/8/29 0:24
 */
public class AfficheBean {
    public Result result;
    public String status;
    public String summary;
    public class Result{
        public int            pindex;
        public int            totalpage;
        public ArrayList<Json> data;
        public class Json{
            public String       id;
            public String       notifycode;
            public String       notifycontent;
            public String       updateuser;
            public String       notifykind;
            public String       notifydate;
            public String       empphoto;
            public ArrayList<String> images;
            public String       updatetime;
            public String       notifytype;
            public String       relateid;
            public String       relatecode;
            public String       notifytitle;
        }
    }
}
