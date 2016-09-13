package com.luofangyun.shangchao.domain;

/**
 * 个人资料
 */
public class Personal {
    public Result result;
    public String status;
    public String summary;
    public class Result{
        public String username;
        public String telnum;
        public String empphoto;
        public String empsex;
        public String empbirthday;
        public String empaddress;
        public String empphone;
    }
}
