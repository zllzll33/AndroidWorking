package com.luofangyun.shangchao.utils;

import android.os.Environment;

/**
 * 常量配置
 * @author pxd
 * 2015-10-22
 */
public class XDConstantValue {
	
	public static final String USER_TELEPHONE_KEY="telephone";
	public static final String USER_LOGIN_NAME="loginname";
	public static final String USER_PASSWORD_KEY="password";
	public static final String USER_ID_KEY="userid";
	public static final String USER_NICKNAME_KEY="nickname";
	public static final String USER_NAME_KEY="name";
	public static final String USER_FACE_KEY="faceimage";
	public static final String USER_TYPE_KEY="usertype";
	public static final String USER_SEX_KEY="sex";
	
	/**头像地址*/
	public static final String USER_IMAGE_FACE_KEY="faceimage";
	/**是否认证*/
	public static final String USER_AUTHENTICATION_KEY="authenticationstatus";
	/**
	 * 同城2 or 长途1
	 * */
	public static final String USER_IDENTITY_KEY="identity";
	
	/**
	 * 是否登陆过
	 * */
	public static final String ISLOGIN="islogin";
	public static final String ISLOGIN_KEY="YESLOGIN";//确认
	/**
	 * 是否第一次打开
	 * */
	public static final String ISFIRST="isfirst";
	/**城市*/
	public static final String USER_CITY_KEY="city";
	/**省份*/
	public static final String USER_PROVINCE_KEY="Province";
	
	/**
	 *	是否需要刷新
	 * */
	 public static final String ISREQUEST="isrequest";
	 public static final String ISREQUEST_KEY="yes";
	
	 
	 
	  public static final String PROXY_IP = "";
	    public static final int PROXY_PORT = 0;
	 

	    /**
	     * get方式
	     */
	    public static final int GET_METHOD = 1;
	    /**
	     * post方式
	     */
	    public static final int POST_METHOD = 2;
	    /**
	     * post头像上传
	     * */
	    public static final int POST_IMG = 3;
	    /**
	     * 成功码
	     */
	    public static final int SUCCESS = 0;
	    /**
	     * 错误码
	     */
	    public static final int ERROR = 1;
	    /**
	     * 网络未连接
	     */
	    public static final String NO_NET = "网络未连接";
	    public static final String SERVICE_NORESPONSE = "服务器无响应";
	    public static final String DATA_ERROR = "数据解析出错";
	    public static final String NETERROR = "网络故障";
	    public static final String RELOAD = "重新加载";

	 
	    public static final String ENCODING = "UTF-8";
	 
	 
	 
	 
	
	/**
	 * 手机信息保存目录
	 */
	public static final String TELEPHONYINFO_FILE="TELEPHONYINFO";
	public static final String IMEI_KEY="IMEI";//手机imei
	public static final String IMSI_KEY="IMSI";//手机imsi
	public static final String PMBRAND_KEY="PMBRAND";//手机品牌
	public static final String PTYPE_KEY="PTYPE";//手机型号
	public static final String PNUMBER_KEY="PNUMBER";//手机号,可能为空
	public static final String PHONE_WiDTH_KEY="WIDTH";//手机屏幕宽度
	public static final String  PHONE_HEIGHT_KEY="HEIGHT";//手机屏幕高度


	
	
	

	
	
	/**
	 * 存储文件名
	 * Created by zhangp01 on 2016/4/13.
	 */
	 public static final String BASE_SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"";//根目录
	    /** 添加设备 */
	 public static final String USER_ICON = BASE_SD_PATH + "/LaiSengPicTemp/";
	    /* 头像文件 */
	 public static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
	

	 /**
	  * 基础定义
	  * Created by zhangp01 on 2016/4/12.
	  */
	     //待付款操作成功
	    public static final int MYMISSON_WAIT_SUCCESS = 001;
	     //未完成操作成功
	    public static final int MYMISSON_UNCOMPLETE_SUCCESS = 002;
	     //操作成功
	    public static final int RESULT_OK = 3;

	    /* 请求识别码 */
	    public static final int CODE_GALLERY_REQUEST = 0xa0;
	    public static final int CODE_CAMERA_REQUEST = 0xa1;
	    public static final int CODE_CUTTING_REQUEST = 0xa2;
}
