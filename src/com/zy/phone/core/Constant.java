package com.zy.phone.core;

/**
 * 作者：liemng on 2017/3/31
 * 邮箱：859686819@qq.com
 */

public class Constant {
	public static final boolean DEBUG = true;
	
    public static final String VERSION = "1.2.1";//版本

    // 秘钥
    public static String ADP_CODE = "AppCode";

    // 用户ID，用于记录开发者应用的唯一用户标识,default为空.
    public static String USER_ID = "";


    //---数据库相关配置
    public static final String DATABASE_NAME = "zy.db";
    public static final int DATABASE_VERSION = 1;
    
    //---获取广告请求参数.
    public static final String ADVERTS_LIST_URL = "http://sdk.chinazmob.com/api/GetAdsList.php";
    public static final String APPCODE = "AppCode";
    public static final String IMEI = "IMEI";
    public static final String IP = "IP";
    public static final String SDK_VERSION = "Version";
    public static final String PACKAGE = "PackName";
    public static final String IMSI = "IMSI";
    public static final String ANDROID_ID = "AndroidId";
    public static final String SYSTEM_VERSION = "SysVer";
    public static final String MODEL = "Model";/**手机型号*/
    public static final String MAC = "Mac";
    public static final String OPERATOR = "Operator";
    public static final String NETTYPE = "NetType";
    public static final String OTHER = "Other";/**开发者参数（开发者提供）*/
    public static final String BRAND = "Brand";
    public static final String RESOLUTION = "Resolution";
    
    public static final String SDK_VERSION_CODE = "1.2.1";
    public static final String APP_CODE = "e9ff112bc855bf4z";
    
    //---获取广告详情.
    public static final String ADVERTS_DETAIL_URL = "http://sdk.chinazmob.com/api/GetAdsInfo.php";
    public static final String ADVERTS_ID = "AdsId";
    
    //--用户完成安装的URL
    public static final String WHEN_HAS_INSTALLED_URL = "http://sdk.chinazmob.com/api/FinishInstall.php";
    
    //--用户完成安装的URL
    public static final String WHEN_TASK_FINISHED_URL = "http://sdk.chinazmob.com/api/FinishTask.php";
    
    //--获取APK的下载路劲的URL
    public static final String FETCH_APK_DOWNLOAD_URL = "http://sdk.chinazmob.com/api/GetApkDown.php";
    
    //--获取APK的下载路劲的URL
    public static final String FETCH_TASK_TIME_URL = "http://sdk.chinazmob.com/api/GetAdsTime.php";
}
