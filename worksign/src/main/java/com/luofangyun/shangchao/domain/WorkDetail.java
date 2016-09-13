package com.luofangyun.shangchao.domain;

/**
 * Description:
 * Copyright  : Copyright (c) 2016
 * Company    : luofang
 * Author     : Android-mjc
 * Date       : 2016/8/28 16:43
 */
public class WorkDetail {
    public Result result;
    public String status;
    public String summary;
    public class Result {
        public String notifytitle;
        public String notifycontent;
        public String notifydate;
        public String empphoto;
        public String images;
    }
}
