package com.luofangyun.shangchao.fragment;


import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BasePager;
import com.luofangyun.shangchao.base.impl.AddressListPager;
import com.luofangyun.shangchao.base.impl.ApplicationPager;
import com.luofangyun.shangchao.base.impl.MessageCenterPager;
import com.luofangyun.shangchao.base.impl.MySelfPager;
import com.luofangyun.shangchao.viewpager.NoScrollViewPager;
import java.util.ArrayList;

/**
 * 主页的Fragment
 */

public class ContentFragment extends BaseFragment {
    private NoScrollViewPager    mViewPager;
    private RadioGroup           contentRg;
    private ArrayList<BasePager> mList;//4个标签页的集合

    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fragment_content, null);
        mViewPager = (NoScrollViewPager) view.findViewById(R.id.vp_content);
        contentRg = (RadioGroup) view.findViewById(R.id.content_rg);
        return view;
    }
    @Override
    public void initData() {
        //初始化4个标签页面对象
        mList = new ArrayList<BasePager>();
        mList.add(new MessageCenterPager(mActivity));
        mList.add(new AddressListPager(mActivity));
        mList.add(new ApplicationPager(mActivity));
        mList.add(new MySelfPager(mActivity));
        mViewPager.setAdapter(new ContentAdapter());
        //RadioGroup的选择监听
        contentRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_news:
                        //消息中心
                        mViewPager.setCurrentItem(0, false);//去掉页面切换的动画
                        break;
                    case R.id.rb_address:
                        mViewPager.setCurrentItem(1, false);       //通讯录
                        break;
                    case R.id.rb_app:
                        mViewPager.setCurrentItem(2, false);       //应用
                        break;
                    case R.id.rb_my:
                        mViewPager.setCurrentItem(3, false);       //我
                        break;
                }
            }
        });

        //监听ViewPager页面切换事件, 初始化当前页面数据
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                BasePager pager = mList.get(position);
                pager.initData();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //手动初始化第一个页面的数据
        mList.get(0).initData();
    }
    class ContentAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //获取当前页面对象
            BasePager pager = mList.get(position);
            if (position == 0) {
                pager.initData();
            }
            container.addView(pager.mRootView);
            return pager.mRootView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
