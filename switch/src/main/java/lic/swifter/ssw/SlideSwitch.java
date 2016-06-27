package lic.swifter.ssw;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;

public class SlideSwitch extends View {

    private final int SHAPE_RECT = 1;
    private final int SHAPE_CIRCLE = 2;
    private final int STATE_RIGHT = SHAPE_RECT;
    private final int STATE_LEFT = SHAPE_CIRCLE;

    private boolean slideable;
    private boolean state;
    private int front_color;
    private int back_color;
    private int switch_color;
    private int shape;
    private int min_width;
    private int min_height;
    private int boundary_distance;
    private int open_direction;
    private int width;
    private int height;
    private int front_alpha;
    private int switch_edges;
    private int movable_range;
    private int touch_slop;

    private Paint paint;
    private Rect back_rect;
    private Rect front_rect;
    private Rect switch_rect;
    private RectF circle_rect;
    private RectF circle_switch_rect;

    private float down_x;
    private float down_y;
    private boolean click_flag;
    private int down_switch_left;
    private int down_switch_right;

    private boolean animator_flag;
    private SlideListener listener;
    private ValueAnimator valueAnimator;

    public interface SlideListener {
        void open();

        void close();
    }

    public SlideSwitch(Context context) {
        this(context, null);
    }

    public SlideSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setAntiAlias(true);

        back_rect = new Rect();
        front_rect = new Rect();
        switch_rect = new Rect();
        circle_rect = new RectF();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.slide_switch);
        shape = typedArray.getInt(R.styleable.slide_switch_shape, SHAPE_RECT);
        front_color = typedArray.getColor(R.styleable.slide_switch_front_color, Color.GREEN);
        back_color = typedArray.getColor(R.styleable.slide_switch_back_color, Color.GRAY);
        switch_color = typedArray.getColor(R.styleable.slide_switch_switch_color, Color.BLUE);
        state = typedArray.getBoolean(R.styleable.slide_switch_state, false);
        slideable = typedArray.getBoolean(R.styleable.slide_switch_slideable, true);
        min_width = typedArray.getDimensionPixelSize(R.styleable.slide_switch_min_width, 280);
        min_height = typedArray.getDimensionPixelSize(R.styleable.slide_switch_min_height, 140);
        boundary_distance = typedArray.getDimensionPixelSize(R.styleable.slide_switch_boundary_distance, 5);
        open_direction = typedArray.getInt(R.styleable.slide_switch_open_direction, STATE_RIGHT);
        typedArray.recycle();

        touch_slop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = measureDimension(min_width, widthMeasureSpec);
        height = measureDimension(min_height, heightMeasureSpec);

        if (height < boundary_distance * 2)
            boundary_distance = height / 2;
        if (height >= width)
            width = height + 10;
        switch_edges = height - boundary_distance * 2;
        setMeasuredDimension(width, height);
        computeDrawingArgs();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (shape) {
            case SHAPE_RECT:
                paint.setColor(back_color);
                canvas.drawRect(back_rect, paint);
                paint.setColor(front_color);
                paint.setAlpha(front_alpha);
                canvas.drawRect(front_rect, paint);
                paint.setColor(switch_color);
                canvas.drawRect(switch_rect, paint);
                break;
            case SHAPE_CIRCLE:
                paint.setColor(back_color);
                canvas.drawRoundRect(circle_rect, height / 2, height / 2, paint);
                paint.setColor(front_color);
                paint.setAlpha(front_alpha);
                canvas.drawRoundRect(circle_rect, height / 2, height / 2, paint);
                paint.setColor(switch_color);
                canvas.drawOval(circle_switch_rect, paint);
                break;
            default:
                throw new IllegalArgumentException("only support rect or circle shape.");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!slideable)
            return super.onTouchEvent(event);
        if (valueAnimator != null && valueAnimator.isStarted())
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                down_x = event.getX();
                down_y = event.getY();
                down_switch_left = switch_rect.left;
                down_switch_right = switch_rect.right;

                break;
            case MotionEvent.ACTION_MOVE:
                if(!click_flag && (Math.abs(event.getX() - down_x) > touch_slop || Math.abs(event.getY() - down_y) > touch_slop)) {
                    click_flag = true;
                }

                int diff_x = (int) (event.getX() - down_x + 0.5);
                switch_rect.set(down_switch_left + diff_x, switch_rect.top, down_switch_right + diff_x, switch_rect.bottom);
                if (switch_rect.left < boundary_distance)
                    switch_rect.set(boundary_distance, boundary_distance, boundary_distance + switch_edges, boundary_distance + switch_edges);
                else if (switch_rect.right > back_rect.right - boundary_distance) {
                    switch_rect.set(width - switch_edges - boundary_distance, boundary_distance, width - boundary_distance, boundary_distance + switch_edges);
                }
                circle_switch_rect.set(switch_rect);

                front_alpha = 255 * (switch_rect.left - boundary_distance) / movable_range;
                if (open_direction == STATE_LEFT)
                    front_alpha = 255 - front_alpha;

                invalidateSlide();
                break;
            case MotionEvent.ACTION_UP:
                if(click_flag) {
                    click_flag = false;
                } else {
                    if (switch_rect.left < movable_range / 2)
                        smoothSwitch(STATE_RIGHT);
                    else
                        smoothSwitch(STATE_LEFT);
                    return true;
                }

                if (switch_rect.left < movable_range / 2)
                    smoothSwitch(STATE_LEFT);
                else
                    smoothSwitch(STATE_RIGHT);

                break;
            default:
                break;
        }
        return true;
    }

    private void smoothSwitch(final int direction) {
        if (direction == STATE_LEFT) {
            valueAnimator = ValueAnimator.ofInt(switch_rect.left, boundary_distance);
            valueAnimator.setDuration((switch_rect.left - boundary_distance) << 2);
        } else {
            valueAnimator = ValueAnimator.ofInt(switch_rect.left, width - boundary_distance - switch_edges);
            valueAnimator.setDuration((width - boundary_distance - switch_edges - switch_rect.left) << 2);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (Integer) animation.getAnimatedValue();
                switch_rect.set(currentValue, boundary_distance, currentValue + switch_edges, height - boundary_distance);
                circle_switch_rect.set(switch_rect);
                front_alpha = 255 * (switch_rect.left - boundary_distance) / movable_range;
                if (open_direction == STATE_LEFT)
                    front_alpha = 255 - front_alpha;
                invalidateSlide();
            }
        });
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(animator_flag)
                    return ;
                if(direction == STATE_LEFT && open_direction == STATE_LEFT || direction == STATE_RIGHT && open_direction == STATE_RIGHT) {
                    if(!state && listener != null)
                        listener.open();
                    state = true;
                } else {
                    if(state && listener != null)
                        listener.close();
                    state = false;
                }
            }
        });
        valueAnimator.start();
    }

    private void invalidateSlide() {
        if (Looper.getMainLooper() == Looper.myLooper())
            invalidate();
        else
            postInvalidate();
    }

    public void setState(boolean state) {
        this.state = state;
        if(valueAnimator != null && valueAnimator.isRunning()) {
            animator_flag = true;
            valueAnimator.cancel();
            animator_flag = false;
        }
        computeDrawingArgs();
        invalidateSlide();
        if(listener != null) {
            if(state)
                listener.open();
            else
                listener.close();
        }
    }

    public boolean isOpen() {
        return state;
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY)
            result = specSize;
        else {
            result = defaultSize;
            if (specMode == MeasureSpec.AT_MOST)
                result = Math.min(result, specSize);
        }
        return result;
    }

    private void computeDrawingArgs() {
        back_rect.set(0, 0, width, height);
        front_rect.set(0, 0, width, height);
        circle_rect.set(back_rect);

        switch (open_direction) {
            case STATE_LEFT:
                if (state) {
                    front_alpha = 255;
                    switch_rect.set(boundary_distance, boundary_distance, boundary_distance + switch_edges, boundary_distance + switch_edges);
                } else {
                    front_alpha = 0;
                    switch_rect.set(width - switch_edges - boundary_distance, boundary_distance, width - boundary_distance, boundary_distance + switch_edges);
                }
                break;
            case STATE_RIGHT:
                if (state) {
                    front_alpha = 255;
                    switch_rect.set(width - switch_edges - boundary_distance, boundary_distance, width - boundary_distance, boundary_distance + switch_edges);
                } else {
                    front_alpha = 0;
                    switch_rect.set(boundary_distance, boundary_distance, boundary_distance + switch_edges, boundary_distance + switch_edges);
                }
                break;
            default:
                throw new IllegalArgumentException("only support left or right direction.");
        }
        circle_switch_rect = new RectF(switch_rect);
        movable_range = width - boundary_distance * 2 - switch_edges;
    }

    public void setSlideListener(SlideListener listener) {
        this.listener = listener;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("parcelableState", super.onSaveInstanceState());
        bundle.putBoolean("state", state);
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable parcelableState) {
        if (parcelableState instanceof Bundle) {
            Bundle bundle = (Bundle) parcelableState;
            state = bundle.getBoolean("state");
            parcelableState = bundle.getParcelable("parcelableState");
        }
        super.onRestoreInstanceState(parcelableState);
    }
}
