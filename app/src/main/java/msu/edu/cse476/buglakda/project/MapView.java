package msu.edu.cse476.buglakda.project;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/**
 * Custom view class for our Puzzle.
 */
public class MapView extends View {
    public MapView(Context context) {
        super(context);
        init(null, 0);
    }
    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }
    private void init(AttributeSet attrs, int defStyle) {
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action==MotionEvent.ACTION_DOWN) {

            float x = event.getX();
            float y = event.getY();
            handleSingleTouch(x, y);
            return true;
        }
        return super.onTouchEvent(event);
    }
    private void handleSingleTouch(float x, float y) {
        // debug, will do something in the future
    }
}
