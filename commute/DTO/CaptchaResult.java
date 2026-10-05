package com.CapitalCommute.commute.DTO;

import javafx.scene.canvas.Canvas;

public class CaptchaResult {
    private final Canvas canvas;
    private final String sessionId;

    public CaptchaResult(Canvas canvas, String sessionId) {
        this.canvas = canvas;
        this.sessionId = sessionId;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public String getSessionId() {
        return sessionId;
    }


}
