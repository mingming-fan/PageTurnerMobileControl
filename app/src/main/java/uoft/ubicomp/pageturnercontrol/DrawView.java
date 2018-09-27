package uoft.ubicomp.pageturnercontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DrawView extends View {
    Paint paint = new Paint();

    float currentX = 0;
    float currentY = 0;
    float cancelThreshold = 10;
    float leftrightThreshold = 10;
    float width = 0;

    private void init() {
        paint.setAntiAlias(true);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(15);
    }

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setParams(float currentX, float currentY, float cancelThreshold, float leftrightThreshold, float width){
        this.currentX = currentX;
        this.currentY = currentY;
        this.cancelThreshold = cancelThreshold;
        this.leftrightThreshold = leftrightThreshold;
        this.width = width;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawLine(0, this.currentY - cancelThreshold, this.width, this.currentY - cancelThreshold, paint);
        canvas.drawLine(0, this.currentY + cancelThreshold, this.width, this.currentY + cancelThreshold, paint);
        canvas.drawLine(this.currentX - leftrightThreshold, this.currentY - cancelThreshold, this.currentX - leftrightThreshold, this.currentY + cancelThreshold, paint);
        canvas.drawLine(this.currentX + leftrightThreshold, this.currentY - cancelThreshold, this.currentX + leftrightThreshold, this.currentY + cancelThreshold, paint);
    }

}
