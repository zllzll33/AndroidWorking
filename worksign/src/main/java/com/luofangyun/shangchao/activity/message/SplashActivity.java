package com.luofangyun.shangchao.activity.message;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.MainActivity;
import com.luofangyun.shangchao.utils.PrefUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * 闪屏页面
 */
public class SplashActivity extends Activity {

    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitSysBar();
        setContentView(R.layout.activity_splash);
        //查找需要的控件
        initView();
        //加载数据
        initData();
    }
    private void initView() {
        iv = (ImageView) findViewById(R.id.iv);
    }
    private void initData() {
        //缩放
        ScaleAnimation animScale = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        animScale.setDuration(1000);
        animScale.setFillAfter(true);

        //渐变
        AlphaAnimation animAlpha = new AlphaAnimation(0, 1);
        animAlpha.setDuration(2000);
        animAlpha.setFillAfter(true);

        //动画集合
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(animScale);
        set.addAnimation(animAlpha);
        //启动动画
//        iv.startAnimation(set);
        boolean loginStatus=  PrefUtils.getBoolean(getApplicationContext(),"loginstaus",false);
//        Log.e("loginStatus",String.valueOf(loginStatus));
        if(loginStatus==false) {
            startActivity(new Intent(getApplicationContext(),
                    UserEnterActivity.class));
            finish();
        }
        else
        {
            Intent intent=new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }

        set.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            //动画结束
            @Override
            public void onAnimationEnd(Animation animation) {
                //跳转到登录页面
              boolean loginStatus=  PrefUtils.getBoolean(getApplicationContext(),"loginstaus",false);
                Log.e("loginStatus",String.valueOf(loginStatus));
                if(loginStatus==false) {
                    startActivity(new Intent(getApplicationContext(),
                            UserEnterActivity.class));
                    finish();
                }
                else
                {
                    Intent intent=new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });
    }
    protected void InitSysBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
    }
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
