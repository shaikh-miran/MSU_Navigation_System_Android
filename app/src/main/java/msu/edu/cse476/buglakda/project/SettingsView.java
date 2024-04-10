package msu.edu.cse476.buglakda.project;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
/**
 * Custom view class for our Puzzle.
 */
public class SettingsView extends View {
    public SettingsView(Context context) {
        super(context);
        init(null, 0);
    }
    public SettingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    public SettingsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }
    private void init(AttributeSet attrs, int defStyle) {
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
