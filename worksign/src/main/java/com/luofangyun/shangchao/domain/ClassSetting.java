package com.luofangyun.shangchao.domain;

import java.util.ArrayList;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : luofang
 * Author     : Android-mjc
 * Date       : 2016/8/28 18:49
 */
public class ClassSetting {
    public Result result;
    public String status;
    public String summary;
    public class Result {
        public int pindex;
        public int totalpage;
        public ArrayList<Json> data;
        public class Json {
            public String timecode;
            public String timename;
            public String timesb;
            public String timexb;
            public String labelname;
        }
    }
}
