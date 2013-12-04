package com.coswind.viewpagerindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by coswind on 12/3/13.
 */
public class TabPagerIndicator extends FrameLayout implements ViewPager.OnPageChangeListener {
    private static final CharSequence EMPTY_TITLE = "";

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float mPositionOffset;
    private int mSelectedTabIndex;
    private int mScrolledTabIndex;
    private ViewPager mViewPager;
    private final BottomView mBottomView;

    private final IcsLinearLayout mTabLayout;

    public TabPagerIndicator(Context context) {
        this(context, null);
    }

    public TabPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTabLayout = new IcsLinearLayout(context, R.attr.tabPageIndicatorStyle);
        mBottomView = new BottomView(context);

        // Add Tab Layout.
        addView(mTabLayout, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));


        TypedArray a = context.obtainStyledAttributes(null,
                new int[] { R.attr.bottomIndicatorColor,
                            R.attr.bottomIndicatorHeight },
                R.attr.tabPageIndicatorStyle, 0);

        mPaint.setColor(a.getColor(0, R.color.blue));

        float bottomViewHeightInDp = a.getDimension(1, R.dimen.default_botton_view_height);
        int bottomViewHeightInPx = (int) (getResources().getDisplayMetrics().density * bottomViewHeightInDp + 0.5f);
        // Add Bottom View.
        addView(mBottomView, new LayoutParams(MATCH_PARENT, bottomViewHeightInPx, Gravity.BOTTOM));

        a.recycle();
    }

    public void setViewPager(ViewPager viewPager) {
        if (mViewPager == viewPager) {
            return;
        }

        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }

        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        mTabLayout.removeAllViews();

        PagerAdapter pagerAdapter = mViewPager.getAdapter();

        int count = pagerAdapter.getCount();
        for (int i = 0; i < count; i++) {
            CharSequence charSequence = pagerAdapter.getPageTitle(i);

            if (charSequence == null) {
                charSequence = EMPTY_TITLE;
            }

            addTab(i, charSequence);
        }

        if (mSelectedTabIndex >= count) {
            mSelectedTabIndex = count - 1;
        }

        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

    protected void addTab(int index, CharSequence text) {
        TabView tabView = new TabView(getContext());
        tabView.mIndex = index;
        tabView.setFocusable(true);
        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                TabView tabView = (TabView)view;
                int newSelected = tabView.getIndex();
                mViewPager.setCurrentItem(newSelected);
            }
        });
        tabView.setText(text);

        // Set Weight to 1.
        mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0, MATCH_PARENT, 1));
    }

    public void setCurrentItem(int index) {
        mSelectedTabIndex = index;

        mViewPager.setCurrentItem(mSelectedTabIndex);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        mScrolledTabIndex = i;
        mPositionOffset = v;

        mBottomView.invalidate();
    }

    @Override
    public void onPageSelected(int i) {
        setCurrentItem(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private class TabView extends TextView {
        private int mIndex;

        public TabView(Context context) {
            super(context, null, R.attr.tabPageIndicatorStyle);
        }

        public int getIndex() {
            return mIndex;
        }
    }

    private class BottomView extends View {
        private float pageWidth = 0;
        private int dividerWidth;

        public BottomView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (pageWidth == 0) {
                TabView tabView = (TabView) mTabLayout.getChildAt(mScrolledTabIndex);
                pageWidth = tabView.getWidth();
                dividerWidth = mTabLayout.getDividerWidth();
            }

            float left = pageWidth * (mPositionOffset + mScrolledTabIndex) + (mScrolledTabIndex - 1) * dividerWidth;
            float right = left + pageWidth;
            float bottom = getHeight();

            canvas.drawRect(left, 0, right, bottom, mPaint);
        }
    }
}
