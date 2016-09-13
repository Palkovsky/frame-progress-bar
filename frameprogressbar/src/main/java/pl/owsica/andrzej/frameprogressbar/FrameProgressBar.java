package pl.owsica.andrzej.frameprogressbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import pl.owsica.andrzej.frameprogressbar.utils.CalculationUtil;
import pl.owsica.andrzej.frameprogressbar.utils.Direction;


/**
 * @author Dawid Macek 2016
 *         <p/>
 *         TO DOs
 *         -support for margin - D, supports vertical and horiozntal margins
 *         -path drawing
 */
public class FrameProgressBar extends ViewGroup {

    private final Paint mFramePaint = new Paint();
    private final Paint mProgressPaint = new Paint();
    private final Path path = new Path();

    private float mProgress;
    private int mFrameThickness;
    private int mLastFrameThickness;
    private int mStartPlace;
    private boolean mClockwise;

    private boolean matchWidth;

    private boolean mIndeterminate;
    private int mIndeterminePlace;
    private float mIndetermineLocation;
    private float mIndetermineLength;
    private float mIndetermineSpeed;

    private int frameBackgroundColor;
    private int frameProgressColor;

    final Rect mContainerRect = new Rect();
    final Rect mFramedChildRect = new Rect();

    public FrameProgressBar(Context context) {
        super(context);
    }

    public FrameProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public FrameProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FrameProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
    }


    /**
     * Common constructor
     */ {
        mStartPlace = Direction.TOP;
        mFrameThickness = 10;

        //Indetermine config
        mIndeterminePlace = mStartPlace;
        mIndetermineLocation = 0;
        mIndetermineSpeed = 20;
        mIndetermineLength = CalculationUtil.convertDpToPx(40, getContext());

        initPaints();
        setWillNotDraw(false);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FrameProgressBar);

        mClockwise = a.getBoolean(R.styleable.FrameProgressBar_clockwise, true);
        mStartPlace = a.getInt(R.styleable.FrameProgressBar_start_place, Direction.TOP);
        mFrameThickness = mLastFrameThickness = (int) a.getDimension(R.styleable.FrameProgressBar_frame_thickness, CalculationUtil.convertDpToPx(10f, getContext()));
        mProgressPaint.setStrokeWidth(mFrameThickness);
        mProgress = a.getFloat(R.styleable.FrameProgressBar_progress, 0f);

        frameBackgroundColor = a.getColor(R.styleable.FrameProgressBar_background_color, ContextCompat.getColor(getContext(), android.R.color.transparent));
        mFramePaint.setColor(frameBackgroundColor);
        frameProgressColor = a.getColor(R.styleable.FrameProgressBar_progress_color, ContextCompat.getColor(getContext(), android.R.color.white));
        mProgressPaint.setColor(frameProgressColor);

        mIndeterminate = a.getBoolean(R.styleable.FrameProgressBar_indeterminate, false);
        mIndetermineSpeed = a.getFloat(R.styleable.FrameProgressBar_indeterminate_speed, 10f);
        mIndetermineLength = a.getDimension(R.styleable.FrameProgressBar_indeterminate_length, CalculationUtil.convertDpToPx(40f, getContext()));

        a.recycle();
    }

    /**
     * Any layout manager that doesn't scroll will want this.
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.progress = mProgress;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setProgress(mProgress);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Measurement will ultimately be computing these values.
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;


        //This ViewGroup will measure and draw only one child
        View child = getChildAt(0);
        MarginLayoutParams lp = (LayoutParams) child.getLayoutParams();

        if (child.getVisibility() != View.GONE) {
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

            maxHeight += lp.bottomMargin + lp.topMargin;
            maxWidth += lp.rightMargin + lp.leftMargin;

            maxWidth += child.getMeasuredWidth() + 2 * mLastFrameThickness;
            maxHeight += child.getMeasuredHeight() + 2 * mLastFrameThickness;


            childState = ViewCompat.combineMeasuredStates(childState, ViewCompat.getMeasuredState(child));
        }

        setMeasuredDimension(ViewCompat.resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                ViewCompat.resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int leftPos = getPaddingLeft();
        final int rightPos = right - left - getPaddingRight();

        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        final View child = getChildAt(0);

        if (child.getVisibility() != View.GONE) {


            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            mContainerRect.left = leftPos - mLastFrameThickness;
            mContainerRect.right = rightPos + mLastFrameThickness;
            mContainerRect.top = parentTop;
            mContainerRect.bottom = parentBottom;

            mFramedChildRect.left = mContainerRect.left + 2 * mLastFrameThickness;
            mFramedChildRect.top = parentTop + mLastFrameThickness;
            mFramedChildRect.right = mContainerRect.right - 2 * mLastFrameThickness;
            mFramedChildRect.bottom = parentBottom - mLastFrameThickness;


            child.layout(mFramedChildRect.left, mFramedChildRect.top, mFramedChildRect.right, mFramedChildRect.bottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mFrameThickness > 0) {

            //Draws background frame
            canvas.drawRect(mContainerRect.left, mContainerRect.top,
                    mFramedChildRect.left, mContainerRect.bottom, mFramePaint); //Left part
            canvas.drawRect(mFramedChildRect.right, mContainerRect.top,
                    mContainerRect.right, mContainerRect.bottom, mFramePaint); //Right part
            canvas.drawRect(mFramedChildRect.left, mFramedChildRect.bottom,
                    mFramedChildRect.right, mContainerRect.bottom, mFramePaint); //Bottom part
            canvas.drawRect(mFramedChildRect.left, mContainerRect.top,
                    mFramedChildRect.right, mFramedChildRect.top, mFramePaint); //Top part

            path.reset();

            int circuit = 2 * canvas.getWidth() + 2 * canvas.getHeight();

            if (isIndeterminate()) {

                float indeterminateSpeed = mIndetermineSpeed / 10;

                switch (mIndeterminePlace) {
                    case Direction.TOP:

                        if (mClockwise) {
                            path.moveTo(mContainerRect.left + mFrameThickness + mIndetermineLocation, mContainerRect.top + mFrameThickness / 2);
                            path.lineTo(mContainerRect.left + mFrameThickness + mIndetermineLocation + mIndetermineLength, mContainerRect.top + mFrameThickness / 2);

                            mIndetermineLocation += indeterminateSpeed;
                            if (mContainerRect.left + mFrameThickness * 2 + mIndetermineLocation + mIndetermineLength > mContainerRect.right) {

                                float cornerOffset = mContainerRect.left + mFrameThickness * 2 + mIndetermineLocation + mIndetermineLength - mContainerRect.right;

                                //Draw Part of right line
                                path.moveTo(mContainerRect.right - mFrameThickness * 1.5f, mContainerRect.top + mFrameThickness / 2);
                                path.lineTo(mContainerRect.right - mFrameThickness * 1.5f, mContainerRect.top + mFrameThickness / 2 + cornerOffset);

                                if (cornerOffset > mIndetermineLength - mFrameThickness) {
                                    mIndeterminePlace = Direction.RIGHT;
                                    mIndetermineLocation = 0;
                                }
                            }
                        } else {

                            path.moveTo(mContainerRect.right - mFrameThickness + mIndetermineLocation, mContainerRect.top + mFrameThickness / 2);
                            path.lineTo(mContainerRect.right - mFrameThickness + mIndetermineLocation - mIndetermineLength, mContainerRect.top + mFrameThickness / 2);

                            mIndetermineLocation -= indeterminateSpeed;

                            if (mContainerRect.right - mFrameThickness * 2 + mIndetermineLocation - mIndetermineLength < mContainerRect.left) {
                                float cornerOffset = mContainerRect.left - (mContainerRect.right - mFrameThickness * 2 + mIndetermineLocation - mIndetermineLength);

                                //Draw part of LEFT line
                                path.moveTo(mContainerRect.left + mFrameThickness * 1.5f, mContainerRect.top + mFrameThickness / 2);
                                path.lineTo(mContainerRect.left + mFrameThickness * 1.5f, mContainerRect.top + mFrameThickness / 2 + cornerOffset);

                                if (cornerOffset > mIndetermineLength - mFrameThickness) {
                                    mIndeterminePlace = Direction.LEFT;
                                    mIndetermineLocation = 0;
                                }
                            }
                        }
                        break;
                    case Direction.BOTTOM:

                        if (mClockwise) {

                            path.moveTo(mContainerRect.right - mFrameThickness + mIndetermineLocation, mContainerRect.bottom - mFrameThickness / 2);
                            path.lineTo(mContainerRect.right - mFrameThickness + mIndetermineLocation - mIndetermineLength, mContainerRect.bottom - mFrameThickness / 2);

                            mIndetermineLocation -= indeterminateSpeed;

                            if (mContainerRect.right - mFrameThickness * 2 + mIndetermineLocation - mIndetermineLength < mContainerRect.left) {

                                float cornerOffset = mContainerRect.left - (mContainerRect.right - mFrameThickness * 2 + mIndetermineLocation - mIndetermineLength);

                                //Draw part of LEFT line
                                path.moveTo(mContainerRect.left + mFrameThickness * 1.5f, mContainerRect.bottom - mFrameThickness / 2);
                                path.lineTo(mContainerRect.left + mFrameThickness * 1.5f, mContainerRect.bottom - mFrameThickness / 2 - cornerOffset);

                                if (cornerOffset > mIndetermineLength - mFrameThickness) {
                                    mIndeterminePlace = Direction.LEFT;
                                    mIndetermineLocation = 0;
                                }
                            }
                        } else {

                            path.moveTo(mContainerRect.left + mFrameThickness + mIndetermineLocation, mContainerRect.bottom - mFrameThickness / 2);
                            path.lineTo(mContainerRect.left + mFrameThickness + mIndetermineLocation + mIndetermineLength, mContainerRect.bottom - mFrameThickness / 2);

                            mIndetermineLocation += indeterminateSpeed;

                            if (mContainerRect.left + mFrameThickness * 2 + mIndetermineLocation + mIndetermineLength > mContainerRect.right) {

                                float cornerOffset = mContainerRect.left + mFrameThickness * 2 + mIndetermineLocation + mIndetermineLength - mContainerRect.right;

                                //Draw Part of right line
                                path.moveTo(mContainerRect.right - mFrameThickness * 1.5f, mContainerRect.bottom - mFrameThickness / 2);
                                path.lineTo(mContainerRect.right - mFrameThickness * 1.5f, mContainerRect.bottom - mFrameThickness / 2 - cornerOffset);

                                if (cornerOffset > mIndetermineLength - mFrameThickness) {
                                    mIndeterminePlace = Direction.RIGHT;
                                    mIndetermineLocation = 0;
                                }
                            }
                        }
                        break;
                    case Direction.LEFT:

                        if (mClockwise) {
                            path.moveTo(mContainerRect.left + mFrameThickness * 1.5f, mContainerRect.bottom + mFrameThickness / 2 - mIndetermineLocation);
                            path.lineTo(mContainerRect.left + mFrameThickness * 1.5f, mContainerRect.bottom + mFrameThickness / 2 - mIndetermineLocation - mIndetermineLength);

                            mIndetermineLocation += indeterminateSpeed;

                            if (mContainerRect.bottom + mFrameThickness / 2 - mIndetermineLocation - mIndetermineLength < mContainerRect.top) {

                                float cornerOffset = mContainerRect.top - (mContainerRect.bottom + mFrameThickness / 2 - mIndetermineLocation - mIndetermineLength);

                                //Draw part of TOP
                                path.moveTo(mContainerRect.left + mFrameThickness * 1.5f, mContainerRect.top + mFrameThickness / 2);
                                path.lineTo(mContainerRect.left + mFrameThickness * 1.5f + cornerOffset, mContainerRect.top + mFrameThickness / 2);

                                if (cornerOffset > mIndetermineLength - mFrameThickness) {
                                    mIndeterminePlace = Direction.TOP;
                                    mIndetermineLocation = 0;
                                }
                            }
                        } else {

                            path.moveTo(mContainerRect.left + mFrameThickness * 1.5f, mContainerRect.top - mFrameThickness / 2 - mIndetermineLocation);
                            path.lineTo(mContainerRect.left + mFrameThickness * 1.5f, mContainerRect.top - mFrameThickness / 2 - mIndetermineLocation + mIndetermineLength);

                            mIndetermineLocation -= indeterminateSpeed;

                            if (mContainerRect.top - mFrameThickness / 2 - mIndetermineLocation + mIndetermineLength > mContainerRect.bottom) {

                                float cornerOffset = mContainerRect.top - mFrameThickness / 2 - mIndetermineLocation + mIndetermineLength - mContainerRect.bottom;

                                //DRAW PART OF BOTTOM LINE
                                path.moveTo(mContainerRect.left + mFrameThickness * 2, mContainerRect.bottom - mFrameThickness / 2);
                                path.lineTo(mContainerRect.left + mFrameThickness * 2 + cornerOffset, mContainerRect.bottom - mFrameThickness / 2);

                                if (cornerOffset > mIndetermineLength - mFrameThickness) {
                                    mIndeterminePlace = Direction.BOTTOM;
                                    mIndetermineLocation = 0;
                                }
                            }
                        }
                        break;
                    case Direction.RIGHT:

                        if (mClockwise) {

                            path.moveTo(mContainerRect.right - mFrameThickness * 1.5f, mContainerRect.top - mFrameThickness / 2 - mIndetermineLocation);
                            path.lineTo(mContainerRect.right - mFrameThickness * 1.5f, mContainerRect.top - mFrameThickness / 2 - mIndetermineLocation + mIndetermineLength);

                            mIndetermineLocation -= indeterminateSpeed;

                            if (mContainerRect.top - mFrameThickness / 2 - mIndetermineLocation + mIndetermineLength > mContainerRect.bottom) {

                                float cornerOffset = mContainerRect.top - mFrameThickness / 2 - mIndetermineLocation + mIndetermineLength - mContainerRect.bottom;

                                //DRAW PART OF BOTTOM LINE
                                path.moveTo(mContainerRect.right - mFrameThickness * 2, mContainerRect.bottom - mFrameThickness / 2);
                                path.lineTo(mContainerRect.right - mFrameThickness * 2 - cornerOffset, mContainerRect.bottom - mFrameThickness / 2);

                                if (cornerOffset > mIndetermineLength - mFrameThickness) {
                                    mIndeterminePlace = Direction.BOTTOM;
                                    mIndetermineLocation = 0;
                                }
                            }
                        } else {

                            path.moveTo(mContainerRect.right - mFrameThickness * 1.5f, mContainerRect.bottom + mFrameThickness / 2 - mIndetermineLocation);
                            path.lineTo(mContainerRect.right - mFrameThickness * 1.5f, mContainerRect.bottom + mFrameThickness / 2 - mIndetermineLocation - mIndetermineLength);

                            mIndetermineLocation += indeterminateSpeed;

                            if (mContainerRect.bottom + mFrameThickness / 2 - mIndetermineLocation - mIndetermineLength < mContainerRect.top) {

                                float cornerOffset = mContainerRect.top - (mContainerRect.bottom + mFrameThickness / 2 - mIndetermineLocation - mIndetermineLength);

                                //Draw part of TOP
                                path.moveTo(mContainerRect.right - mFrameThickness * 1.5f, mContainerRect.top + mFrameThickness / 2);
                                path.lineTo(mContainerRect.right - mFrameThickness * 1.5f - cornerOffset, mContainerRect.top + mFrameThickness / 2);

                                if (cornerOffset > mIndetermineLength - mFrameThickness) {
                                    mIndeterminePlace = Direction.TOP;
                                    mIndetermineLocation = 0;
                                }
                            }
                        }
                        break;
                }

                canvas.drawPath(path, mProgressPaint);
                invalidate();

            } else {

                float length = (circuit * mProgress) / 100;

                boolean drawn = false;
                boolean initialPosition = true;
                int currentPlace = mStartPlace;

                Direction.DrawStop drawEnd = Direction.getDrawEnd(length,
                        canvas.getWidth(), canvas.getHeight(), mStartPlace, mClockwise);


                while (!drawn) {

                    if (initialPosition) {

                        Direction.Vector2 initialPos = Direction.initialPosition(currentPlace, mFrameThickness, mContainerRect, canvas);
                        path.moveTo(initialPos.x, initialPos.y);

                        if (drawEnd.place == currentPlace && Direction.isEndOnFirstLedge(drawEnd, canvas, mClockwise, mProgress) && mProgress != 100) {
                            Direction.Vector2 endPos = Direction.finishPosition(drawEnd, mFrameThickness, mFramedChildRect);
                            path.lineTo(endPos.x, endPos.y);
                            drawn = true;
                        } else {
                            Direction.Vector2 endPos = Direction.calculateEnd(currentPlace, mFrameThickness, mClockwise, mContainerRect, canvas);
                            path.lineTo(endPos.x, endPos.y);
                            currentPlace = Direction.nextDirection(currentPlace, mClockwise);
                        }

                        initialPosition = false;
                    } else {

                        Direction.Vector2 startPos = Direction.startPosition(currentPlace, mFrameThickness, mClockwise, mContainerRect, canvas);
                        path.moveTo(startPos.x, startPos.y);

                        if (drawEnd.place == currentPlace) {
                            Direction.Vector2 endPos = Direction.finishPosition(drawEnd, mFrameThickness, mFramedChildRect);
                            path.lineTo(endPos.x, endPos.y);
                            drawn = true;
                        } else {
                            Direction.Vector2 endPos = Direction.calculateEnd(currentPlace, mFrameThickness, mClockwise, mContainerRect, canvas);
                            path.lineTo(endPos.x, endPos.y);
                            currentPlace = Direction.nextDirection(currentPlace, mClockwise);
                        }
                    }
                }
                //path.moveTo(canvas.getWidth() / 2, mFramedChildRect.top - mFrameThickness / 2);
                //path.lineTo(canvas.getWidth(), mFramedChildRect.top - mFrameThickness / 2);
                canvas.drawPath(path, mProgressPaint);
            }
        }

    }


    private void initPaints() {
        mProgressPaint.setColor(frameProgressColor);
        mProgressPaint.setStrokeWidth(this.mFrameThickness);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);

        mFramePaint.setColor(frameBackgroundColor);
        mFramePaint.setAntiAlias(true);
    }

    /**
     * Methods for monkeys
     */

    public void show() {
        if (!isVisible()) {
            this.mFrameThickness = mLastFrameThickness;
            mProgressPaint.setStrokeWidth(mLastFrameThickness);
            requestLayout(); //re-measure
            invalidate();
        }
    }

    public void hide() {
        if (isVisible()) {
            this.mLastFrameThickness = this.mFrameThickness;
            this.mFrameThickness = 0;
            mProgressPaint.setStrokeWidth(0);
            requestLayout(); //re-measure
            invalidate();
        }
    }

    public boolean isVisible() {
        return (mFrameThickness > 0);
    }

    /**
     * Getters & Setters
     */
    public void setProgress(float mProgress) {
        boolean changed = false;
        if (mProgress != this.mProgress)
            changed = true;

        if (mProgress < 0)
            this.mProgress = 0;
        else if (mProgress > 100)
            this.mProgress = 100;
        else
            this.mProgress = mProgress;

        if (changed) {
            invalidate();
        }
    }

    public void setFrameThickness(int mFrameThickness) {
        if (mFrameThickness < 0) {
            this.mFrameThickness = this.mLastFrameThickness = 0;
            mProgressPaint.setStrokeWidth(0);
            requestLayout(); //re-measure
            invalidate();
        } else if (mFrameThickness != this.mFrameThickness) {
            this.mFrameThickness = this.mLastFrameThickness = CalculationUtil.convertDpToPx(mFrameThickness, getContext());
            mProgressPaint.setStrokeWidth(this.mFrameThickness);
            requestLayout(); //re-measure
            invalidate();
        }
    }

    public void setFrameBackgroundColor(int frameBackgroundColor) {
        this.frameBackgroundColor = frameBackgroundColor;
        mFramePaint.setColor(frameBackgroundColor);
        invalidate();
    }

    public void setFrameProgressColor(int frameProgressColor) {
        this.frameProgressColor = frameProgressColor;
        mProgressPaint.setColor(frameProgressColor);
        invalidate();
    }

    public void setStartPlace(int place) {
        if (Direction.isDirection(place)) {
            this.mStartPlace = place;
            invalidate();
        }
    }

    public void setIndeterminate(boolean indeterminate) {
        this.mIndeterminate = indeterminate;
        invalidate();
    }

    public boolean isIndeterminate() {
        return this.mIndeterminate;
    }

    public void setIndetermineSpeed(float mIndetermineSpeed) {
        this.mIndetermineSpeed = mIndetermineSpeed;
    }

    public float getIndetermineSpeed() {
        return mIndetermineSpeed;
    }

    public void setClockwise(boolean clockwise) {
        this.mClockwise = clockwise;
        this.mIndetermineLocation = 0;
        invalidate();
    }

    /**
     * State persistance class
     */
    static class SavedState extends BaseSavedState {

        float progress;

        public SavedState(Parcel source) {
            super(source);
            progress = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(progress);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    @Override
                    public SavedState createFromParcel(Parcel source) {
                        return new SavedState(source);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    // ----------------------------------------------------------------------
    // The rest of the implementation is for custom per-child layout parameters.
    // If you do not need these (for example you are writing a layout manager
    // that does fixed positioning of its children), you can drop all of this.

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameProgressBar.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }


    public static class LayoutParams extends MarginLayoutParams {
        /**
         * The gravity to apply with the View to which these layout parameters
         * are associated.
         */
        public int gravity = Gravity.CENTER;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.FrameProgressBar);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}