package com.luofangyun.shangchao.domain;

/**
 * 登录javaBean
 */
public class UserLogBean {
    public Result result;
    public String status;
    public String summary;
    public class Result {
        public String empname;       //用户名
        public String emppone;       //手机号
    }
}
/*public class UserLogBean {
    public Result result;
    public String status;
    public String summary;
    public class Result {
        public String empname;       //用户名
        public String emppone;       //手机号
        public int    ismng;         //是否是企业管理员（0：否， 1：是）
        public String rights;        //权限信息
        public String companycode;   // 所在企业,无企业关联时填0
        public String companyname;   //企业名称
        public String deptcode;      //部门编号,无部门关联时填0
        public String depname;       //所在部门
        public String empphoto;      //头像地址
        public String empsex;        //性别
        public String empaddress;    //地址
    }
}*/
