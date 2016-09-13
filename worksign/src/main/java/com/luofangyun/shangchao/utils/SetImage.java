package com.luofangyun.shangchao.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luofangyun.shangchao.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 获取本地图片
 * @author zhangp01
 *
 */
public class SetImage {
	private Activity    context;
	private View        parent;
	private String      name;
	private PopupWindow mPopWindow;  //02--app管理器实例
	//01--构造函数
	public SetImage(Activity context,View parent){
		this.context = context;//01--上下文
		this.parent = parent;  //01--this.getWindow().getDecorView(),这样的父view.是指整个窗口view
		//样板:setImage = new SetImage(activity, activity.getWindow().getDecorView() );
	}
	
	/**
	 * 自定义继承arrayadapter适配器
	 * @Title: MyArrayAdapter</p>
	 * @Company: bluemobi.cn </p> 
	 * @author Administrator
	 * @data 2016年7月14日 下午11:58:18
	 */
	public class MyArrayAdapter<T> extends ArrayAdapter {
		List<T> dataList ;
		int  textSize = 15 ;
		private int resourceId;  
		public MyArrayAdapter(Context context, int textViewResourceId, List<T> objects, int size) {
			super(context, textViewResourceId, objects);
			this.resourceId = textViewResourceId;  
			dataList = objects ;
			textSize = size ;
		}
		public MyArrayAdapter(Context context,  int textViewResourceId, List<T> objects) {
			super(context, textViewResourceId, objects);
			this.resourceId = textViewResourceId;  
			dataList = objects ;
		}
		
		@Override
		public T getItem(int position) {
			return dataList.get(position);
			
		}
	}
 /**
  * 02--调用此方法弹出一个popupwindow窗口内容为拍照取消
  * @author linjy
  * @data: 2016年7月6日 下午2:52:33
  * @param name 给这次拍照的图片打个标记
  */
    public void showPopupWindow(String name) {
    	this.name = name;
        // 03--一个自定义的布局，作为显示的内容,先生成出来,后面new popupwindow备用
        View view = LayoutInflater.from(context).inflate(
                R.layout.myinfo_pop_photo, null);

        // 04--下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        final PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        
        // 05--设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);
        // 06--设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        //07-- 在底部显示showatlocation是指定父视图，显示在父控件的某个位置（Gravity.TOP,Gravity.RIGHT等）
        window.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        //08--拍照
        TextView myinfo_photo = (TextView) view.findViewById(R.id.myinfo_photo);
        myinfo_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                choseHeadImageFromCameraCapture();//10--调用选择图片从相机拍照的方法
            }
        });
        //08--给显示的内容布局的组件设置点击事件  第一个从相册取照片
        TextView myinfo_album = (TextView) view.findViewById(R.id.myinfo_album);
        myinfo_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();//09--隐藏窗口
                choseHeadImageFromGallery();//10--调用选择图片从图库的方法
            }
        });
        //11--点击取消按钮取消
        TextView popCacel = (TextView) view.findViewById(R.id.myinfo_cancel);
        popCacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
        //12--点击背景区域取消
        RelativeLayout popCacel2 = (RelativeLayout) view.findViewById(R.id.rl_zong_bj);
        popCacel2.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		window.dismiss();
        	}
        });
    }
    

    public void setSex(View view, String string){
    	((TextView)view).setText(string);
    }
    
    

    /**
     *13-- 启动手机相机拍摄照片作为头像
     */
    public void choseHeadImageFromCameraCapture() {
    	//14--新建一个启动相机的意图
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可用，存储照片文件
        // 判断存储卡是否可用，存储照片文件
        if (hasSdcard()) {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri//15--给意图要开启的activity设置数据
                    .fromFile(new File(Environment//16.参数1.键名为输出,键值为uri字符串.android目录下文件
                            .getExternalStorageDirectory(), XDConstantValue.IMAGE_FILE_NAME)));
            context.startActivityForResult(intentFromCapture, XDConstantValue.CODE_CAMERA_REQUEST);//请求码,有返回值打开界面
        }else {
			UiUtils.ToastUtils("存储设备未就绪");
		}
    }

	/**
	 * 检查设备是否存在SDCard的工具方法
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			// 有存储的SDCard
			return true;
		} else {
			return false;
		}
	}

    /**
     * 从本地相册选取图片作为头像
     */
    public void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent();
        // 设置文件类型
        intentFromGallery.setType("image/*");//type
        intentFromGallery.setAction(Intent.ACTION_PICK);//动作--一种选择的动作
        context.startActivityForResult(intentFromGallery,XDConstantValue.CODE_GALLERY_REQUEST);//请求码,有返回值打开界面
    }
    /**
     * 裁剪原始的图片
     * 在用户调用拍照的页面的 接收结果方法中调用.
     */
    public void cropRawPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");//调用第三方裁剪对象设置数据和类型
        // 设置裁剪
        intent.putExtra("crop", "true");//指定裁剪指令
        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);//指定裁剪比例
        intent.putExtra("aspectY", 1);
        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", UiUtils.dip2px(150));//指定输入宽高
        intent.putExtra("outputY", UiUtils.dip2px(150));
        intent.putExtra("return-data", true);//指定要返回数据
        intent.putExtra("name", name);//指定一个名字.有返回值的接收图片 名字由调用者传入
        context.startActivityForResult(intent, XDConstantValue.CODE_CUTTING_REQUEST);
    }
    /**
     * 从拍照或者图库选择的图片后返回的数据捆中
     * 获得图片并压缩处理存在本地并返回存储位置.
     * 在用户调用拍照的页面的 接收结果方法中调用.
     */
    public String setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();//001直接在接收数据返回的页面,调用此方法,将返回的intent处理.得到数据捆
        String url = null;
        /**
         * 003将图片存成本地文件
         */
        if (extras != null) { //004.返回的数据中有图像内容
            Bitmap photo = extras.getParcelable("data"); //005.将序列化数据转成图片

            // 下面注释的方法是将裁剪之后的图片以Base64Coder的字符方式上 传到服务器，QQ头像上传采用的方法跟这个类似
            ByteArrayOutputStream stream = new ByteArrayOutputStream();//006.新建字节数组输出流
            photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);//007.图片压缩后存入指定流
            byte[] b = stream.toByteArray(); //008 将指定存入数据的图片流转换成字节数组

            SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMddHHmmss");//009构建简单的格式化对象
            // 例如：cc_time=1291778220
            long currentTimeLong = System.currentTimeMillis();//0010当前时间的long值
            String currentTimeString = formatDate.format(new Date(currentTimeLong));//0010解析时间生成字符串
	            File file = new File(XDConstantValue.USER_ICON);//new 头像缓存文件夹对象
	            if(!file .exists()  && !file .isDirectory()){//不存在即创建
	            	file.mkdir();///002创建头像缓存本地文件夹//重复代码
	            }
            File fileTemp = new File(XDConstantValue.USER_ICON,  currentTimeString + ".jpg");//0011.生成缓存图片对象
            	
            try {
                if (!fileTemp.exists()) {//0012头像文件不存在就创建新文件
                    fileTemp.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(//输出流
                        fileTemp);//0013.创建关联此文件的输出流
                fileOutputStream.write(b);//写图片
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //本地加载头像
             url = XDConstantValue.USER_ICON+currentTimeString+".jpg";
        }
        if(url!=null){
        	return url;//返回图片路径字符串
        }else{
        	return null;
        }
    }
}
