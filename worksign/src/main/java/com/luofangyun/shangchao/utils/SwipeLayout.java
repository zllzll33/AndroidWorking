package com.luofangyun.shangchao.utils;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * ViewDragHelper的使用方法
 * 1、得到ViewDragHelper的实例对象
 * 2、将触摸事件交给ViewDragHelper的实例对象来处理
 * 3、让mCallBack提供正确的信息之后，才能够把子View拖动
 * 3.1.tryCaptureView
 * 3.2.clampViewPositionHorizontal/clampViewPositionVertical
 * 完善SwipeLayout的逻辑
 * 1、控制只能在水平方向上移动 不能在竖直方向上移动
 * 2、控制隐藏菜单默认的位置
 * 3、拖动左面板的时候  能够把右面板也拖出来
 * 4、拖动右面板的时候  左面板也跟着动
 * 5、限定拖动范围
 * 5.1左面板  -rightViewWidth~0
 * 5.2右面板  leftViewWidth-rightViewWidth ~ leftViewWidth
 * 6、自动平滑打开 、自动平滑关闭
 */

public class SwipeLayout extends FrameLayout {

    private int rightTempLeft;
    private int tempLeft;
    private ViewDragHelper viewDragHelper;

    private ViewDragHelper.Callback mCallBack = new ViewDragHelper.Callback() {



        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        public int clampViewPositionHorizontal(View child, int left, int dx) {

            if (child == viewLeft) {
                if (left > 0) {
                    left = 0;
                } else if (left < -rightViewWidth) {
                    left = -rightViewWidth;
                }
            } else if (child == viewRight) {
                if (left > leftViewWidth) {
                    left = leftViewWidth;
                } else if (left < leftViewWidth - rightViewWidth) {
                    left = leftViewWidth - rightViewWidth;
                }
            }

            return left;
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }

        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

            if (changedView == viewLeft) {
                viewRight.offsetLeftAndRight(dx);
            } else if (changedView == viewRight) {
                viewLeft.offsetLeftAndRight(dx);
            }
            checkState();
            invalidate();

        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (xvel > 200) {
                //关闭
                close();
            } else if (xvel < -200) {
                //打开
                open();
            } else {
                tempLeft = viewRight.getLeft();
                rightTempLeft = viewLeft.getLeft();
                if (tempLeft > leftViewWidth - rightViewWidth / 2) {
                    //关闭
                    close();
                } else {
                    //打开
                    open();
                }
            }
        }


    };
    private void open() {
        viewDragHelper.smoothSlideViewTo(viewLeft, -rightViewWidth, 0);
        invalidate();
    }

    public void close() {
        viewDragHelper.smoothSlideViewTo(viewLeft, 0, 0);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (viewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    private View viewLeft;

    private View viewRight;

    private int leftViewWidth;

    private int rightViewWidth;

    private int leftViewHeight;


    public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, mCallBack);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       viewDragHelper.processTouchEvent(event);
        return super.onTouchEvent(event);
    }
    //当所有控件都加载完成的时候会回调此方法
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount < 2) {
            throw new RuntimeException("SwipeLayout must have at least 2 children");
        }

        viewLeft = getChildAt(0);
        viewRight = getChildAt(1);
    }

    //0~具体的值
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        leftViewWidth = viewLeft.getMeasuredWidth();
        rightViewWidth = viewRight.getMeasuredWidth();
        leftViewHeight = viewLeft.getMeasuredHeight();

    }

    //onMeasure-onLayout-onDraw

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        viewRight.layout(leftViewWidth, 0, leftViewWidth + rightViewWidth, 0 + leftViewHeight);

    }

    public interface SwipeListener {
        public void onOpened(SwipeLayout layout);

        public void onClosed(SwipeLayout layout);

        public void onDraging(SwipeLayout layout);

        public void onStartOpen(SwipeLayout layout);

        public void onStartClose(SwipeLayout layout);
    }

    private SwipeListener listener;

    public void setSwipeListener(SwipeListener listener) {
        this.listener = listener;
    }

    private void notifyOpen(SwipeLayout layout) {
        if (listener != null) {
            listener.onOpened(layout);
        }
    }

    private void notifyClose(SwipeLayout layout) {
        if (listener != null) {
            listener.onClosed(layout);
        }
    }

    private void notifyDraging(SwipeLayout layout) {
        if (listener != null) {
            listener.onDraging(layout);
        }
    }

    private void notifyOnStartOpen(SwipeLayout layout) {
        if (listener != null) {
            listener.onStartOpen(layout);
        }
    }

    private void notifyOnStartClose(SwipeLayout layout) {
        if (listener != null) {
            listener.onStartClose(layout);
        }
    }

    public enum SwipeState {
        OPENED, CLOSED, DRAGING;
    }

    private SwipeState mCurrentState = SwipeState.CLOSED;

    private void checkState() {
        int tempLeft = viewRight.getLeft();

        SwipeState prevState = mCurrentState;

        if (tempLeft == leftViewWidth) {
            mCurrentState = SwipeState.CLOSED;
            notifyClose(this);
        } else if (tempLeft == leftViewWidth - rightViewWidth) {
            mCurrentState = SwipeState.OPENED;
            notifyOpen(this);
        } else {
            mCurrentState = SwipeState.DRAGING;
            notifyDraging(this);
        }


        if (prevState == SwipeState.CLOSED && mCurrentState == SwipeState.DRAGING) {
            notifyOnStartOpen(this);
        }

        if (prevState == SwipeState.OPENED && mCurrentState == SwipeState.DRAGING) {
            notifyOnStartClose(this);
        }

    }

}
