package com.kasvot.kasvotunnistus;

/**
 * Created by Petri on 17.7.2017.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.speech.tts.TextToSpeech;

import com.google.android.gms.vision.face.Face;

public class FaceGraphic extends GraphicOverlay.Graphic {

    private static final float FACE_POSITION_RADIUS = 15.0f;
    private static final float ID_TEXT_SIZE = 60.0f;
    private static final float ID_Y_OFFSET = 60.0f;
    private static final float ID_X_OFFSET = -60.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;
    private TextToSpeech speaker;
    String utteranceId = this.hashCode() + "";

    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };

    private static int mCurrentColorIndex = 0;
    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;
    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;
    MainActivity main;
    private String hymy = "";
    private String hereilla = "";
    private int silmat = 0;

    FaceGraphic(GraphicOverlay overlay, MainActivity main) {
        super(overlay);
        this.main = main;

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

        speaker = new TextToSpeech(this.main, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                }
            }
        });
    }

    void setId(int id) {
        mFaceId = id;
    }

    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        String onnellisuus = "";
        String vasen = "";
        String oikea = "";

        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
  //      canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);

        if(face.getIsSmilingProbability() > 0.5f){
            onnellisuus = "Iloinen";
            hymy = "Henkilö on onnellinen "; }
        else {
            onnellisuus = "Onneton";
            hymy = "Henkilö ei näytä onnelliselta ";
        }

        if(face.getIsLeftEyeOpenProbability() > 0.5f) {
            vasen = "Vasen silmä auki";
            ++silmat;
        }
        else {
            vasen = "Vasen silmä kiinni";
        }
        if(face.getIsRightEyeOpenProbability() > 0.5f) {
            oikea = "Oikea silmä auki";
            ++silmat;
        }
        else {
            oikea = "Oikea silmä kiinni";
        }

        System.out.println(silmat);
        if(silmat == 2) {  hereilla = "ja hän on täysin hereillä.";  }
        else if(silmat == 0) {  hereilla = "ja hän ei ole hereillä.";  }
        else { hereilla = "ja hän on puolittain hereillä."; }
        silmat = 0;

        if(!speaker.isSpeaking()) {
            speaker.speak(hymy + hereilla, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
            speaker.playSilentUtterance(5000,TextToSpeech.QUEUE_ADD,utteranceId);
        }

        canvas.drawText(onnellisuus, x + ID_X_OFFSET * 2 + 15f, y + ID_Y_OFFSET*4, mIdPaint);
        canvas.drawText(vasen, x + ID_X_OFFSET * 9, y - ID_Y_OFFSET * 2, mIdPaint);
        canvas.drawText(oikea, x - ID_X_OFFSET *1 + 5f, y - ID_Y_OFFSET*2, mIdPaint);

        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);
    }
}




