package com.chenmo.wuzhiqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：沉默
 * 日期：2017/3/8
 * QQ:823925783
 */

public class WuZhiQiView extends View {

    /**
     * 棋盘的宽度
     */
    private int mPanelWidth;
    /**
     * 行高
     */
    private float mLineHeight;

    /**
     * 最大多少行
     */
    private int MAX_LINE = 10;

    private Paint mpaint;


    /**
     * 红色棋子
     */
    private Bitmap redPicec;

    /**
     * 黑色棋子
     */
    private Bitmap blackPicec;


    /**
     * 比例
     */
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;
    /**
     * 棋子坐标
     */
    private ArrayList<Point> redArray = new ArrayList<>();
    /**
     * 棋子坐标
     */
    private ArrayList<Point> blackArray = new ArrayList<>();

    /**
     * 当前是白棋下
     */
    private boolean isCurrentRed = true;

    private boolean isGameOver;
    private boolean isRedWin;
    private int MAX_COUNT = 5;

    public WuZhiQiView(Context context) {
        this(context, null);
    }

    public WuZhiQiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WuZhiQiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(0x44ff0000);
        init();
    }

    private void init() {
        mpaint = new Paint();
        mpaint.setColor(0x88000000);

        mpaint.setAntiAlias(true);
        //设置防抖动。
        mpaint.setDither(true);
        mpaint.setStyle(Paint.Style.STROKE);

        redPicec = BitmapFactory.decodeResource(getResources(), R.drawable.red_qi);
        blackPicec = BitmapFactory.decodeResource(getResources(), R.drawable.black_qi);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heighSize = MeasureSpec.getSize(heightMeasureSpec);
        int heighMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heighSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heighSize;
        } else if (heighMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;

        int pieceWidth = (int) (mLineHeight * ratioPieceOfLineHeight);
        redPicec = Bitmap.createScaledBitmap(redPicec, pieceWidth, pieceWidth, false);
        blackPicec = Bitmap.createScaledBitmap(blackPicec, pieceWidth, pieceWidth, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isGameOver) return false;
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {

            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x, y);

            if (redArray.contains(p) || blackArray.contains(p)) {
                return false;
            }

            if (isCurrentRed) {
                redArray.add(p);
            } else {
                blackArray.add(p);
            }
            invalidate();
            isCurrentRed = !isCurrentRed;
        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoard(canvas);
        drawPieces(canvas);
        checkGameOVer();
    }


    private GameOverListener gameoverlistener;

    public interface GameOverListener {
        void restart();
    }


    public void setGameoverlistener(GameOverListener gameoverlistener) {
        this.gameoverlistener = gameoverlistener;
    }

    public void restart() {
        isGameOver = false;
        isRedWin = false;
        redArray.clear();
        blackArray.clear();
        invalidate();
    }

    private void checkGameOVer() {
        boolean redWin = checkFiveInLine(redArray);
        boolean blackWin = checkFiveInLine(blackArray);

        if (redWin || blackWin) {
            isGameOver = true;
            isRedWin = redWin;
            String text = isRedWin ? "红旗赢" : "黑棋赢";
            if (gameoverlistener != null) {
                gameoverlistener.restart();
            } else {
                restart();
            }
        }
    }

    private boolean checkFiveInLine(List<Point> points) {

        for (Point p : points) {
            int x = p.x;
            int y = p.y;
            boolean win = checkHorizontal(x, y, points);
            if (win) return true;
            win = checkVertical(x, y, points);
            if (win) return true;
            win = checkLeftDiagonal(x, y, points);
            if (win) return true;
            win = checkRightDiagonal(x, y, points);
            if (win) return true;

        }
        return false;

    }

    /**
     * 判断x，y位置的棋子，是否横向5颗棋子
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> points) {

        int count = 1;
        //横向左
        for (int i = 1; i < MAX_COUNT; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT) return true;
        //横向右
        for (int i = 1; i < MAX_COUNT; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT) return true;

        return false;
    }

    /**
     * 判断x，y位置的棋子，是否纵向5颗棋子
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkVertical(int x, int y, List<Point> points) {

        int count = 1;
        //上
        for (int i = 1; i < MAX_COUNT; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT) return true;
        //下
        for (int i = 1; i < MAX_COUNT; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT) return true;

        return false;
    }

    /**
     * 判断x，y位置的棋子，是否左斜5颗棋子
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {

        int count = 1;
        //上
        for (int i = 1; i < MAX_COUNT; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT) return true;
        //下
        for (int i = 1; i < MAX_COUNT; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT) return true;

        return false;
    }

    /**
     * 判断x，y位置的棋子，是否左斜5颗棋子
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkRightDiagonal(int x, int y, List<Point> points) {

        int count = 1;
        //上
        for (int i = 1; i < MAX_COUNT; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT) return true;
        //下
        for (int i = 1; i < MAX_COUNT; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT) return true;

        return false;
    }

    private void drawPieces(Canvas canvas) {
        for (int i = 0; i < redArray.size(); i++) {
            Point redPoint = redArray.get(i);
            canvas.drawBitmap(redPicec,
                    (redPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (redPoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    null);
        }
        for (int i = 0; i < blackArray.size(); i++) {
            Point blackPoint = blackArray.get(i);
            canvas.drawBitmap(blackPicec,
                    (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight,
                    null);
        }
    }

    private void drawBoard(Canvas canvas) {

        int w = mPanelWidth;
        float lineHeight = mLineHeight;

        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);
            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mpaint);

            canvas.drawLine(y, startX, y, endX, mpaint);
        }
    }

    /**
     * 保存Activity状态
     */

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_RED_ARRAY = "instance_red_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, isGameOver);
        bundle.putParcelableArrayList(INSTANCE_RED_ARRAY, redArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, blackArray);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            isGameOver = (boolean) bundle.get(INSTANCE_GAME_OVER);
            redArray = (ArrayList<Point>) bundle.get(INSTANCE_RED_ARRAY);
            blackArray = (ArrayList<Point>) bundle.get(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}