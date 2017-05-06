package com.arek314.pda;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class OverlayView extends View {
    private Context context;
    private Canvas canvas;
    private Bitmap image;

    public OverlayView(Context context, Bitmap image) {
        super(context);
        this.context = context;
        this.image = image;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        int posX = (canvas.getWidth() - image.getWidth()) / 2;
        int posY = (canvas.getHeight() - image.getHeight()) / 2;
        canvas.drawBitmap(image, posX, posY, null);
    }

    public void clearCanvas() {
        if (canvas != null) {
            Bitmap clearBitmap;
            clearBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(),
                    Bitmap.Config.ARGB_8888);

            canvas = new Canvas(clearBitmap);
        }
    }
}
