package com.CapitalCommute.commute.util;

import java.util.Random;

import org.springframework.stereotype.Component;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

@Component
public class CaptchaGenerator {
private String currentSessionId;
    
    private static final String CHARS  = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int    WIDTH  = 220;
    private static final int    HEIGHT = 65;
    private static final int    LENGTH = 5;
    private static final Random RANDOM = new Random();

   
    private String currentCode;

    
    public Canvas generate() {
        currentCode = generateCode();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        draw(gc, currentCode);
        return canvas;
    }

    
    public boolean verify(String userInput) {
        if (userInput == null || currentCode == null) return false;
        return currentCode.equalsIgnoreCase(userInput.trim());
    }

    
    public String getCurrentCode() {
        return currentCode;
    }

    
    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
    

    
    private void draw(GraphicsContext gc, String code) {

        
        gc.setFill(Color.web("#1e293b"));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        for (int i = 0; i < 80; i++) {
            double x = RANDOM.nextDouble() * WIDTH;
            double y = RANDOM.nextDouble() * HEIGHT;
            double size = 1 + RANDOM.nextDouble() * 2;
            gc.setFill(Color.color(
                0.3 + RANDOM.nextDouble() * 0.3,
                0.3 + RANDOM.nextDouble() * 0.3,
                0.5 + RANDOM.nextDouble() * 0.3,
                0.4 + RANDOM.nextDouble() * 0.4
            ));
            gc.fillOval(x, y, size, size);
        }

        
        for (int i = 0; i < 6; i++) {
            gc.setStroke(Color.color(
                0.2 + RANDOM.nextDouble() * 0.4,
                0.3 + RANDOM.nextDouble() * 0.4,
                0.6 + RANDOM.nextDouble() * 0.4,
                0.3 + RANDOM.nextDouble() * 0.4
            ));
            gc.setLineWidth(0.5 + RANDOM.nextDouble());
            gc.strokeLine(
                RANDOM.nextDouble() * WIDTH,
                RANDOM.nextDouble() * HEIGHT,
                RANDOM.nextDouble() * WIDTH,
                RANDOM.nextDouble() * HEIGHT
            );
        }

        
        double charWidth = (double)(WIDTH - 20) / LENGTH;
        double startX    = 10;

        for (int i = 0; i < code.length(); i++) {
            String ch = String.valueOf(code.charAt(i));

            
            Color charColor = Color.color(
                0.5 + RANDOM.nextDouble() * 0.5,
                0.6 + RANDOM.nextDouble() * 0.4,
                0.8 + RANDOM.nextDouble() * 0.2
            );
            gc.setFill(charColor);

           
            int fontSize = 24 + RANDOM.nextInt(10);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));

            
            double x = startX + (i * charWidth) + RANDOM.nextDouble() * 5;
            double y = 35 + (RANDOM.nextDouble() - 0.5) * 15;

            gc.save();
            gc.translate(x + charWidth / 2, y);
            double rotation = (RANDOM.nextDouble() - 0.5) * 30; 
            gc.rotate(rotation);
            gc.fillText(ch, -charWidth / 4, 0);
            gc.restore();
        }

        
        for (int i = 0; i < 3; i++) {
            gc.setStroke(Color.color(
                0.4 + RANDOM.nextDouble() * 0.3,
                0.5 + RANDOM.nextDouble() * 0.3,
                0.7 + RANDOM.nextDouble() * 0.3,
                0.2
            ));
            gc.setLineWidth(1);
            double y1 = RANDOM.nextDouble() * HEIGHT;
            double y2 = RANDOM.nextDouble() * HEIGHT;
            gc.strokeLine(0, y1, WIDTH, y2);
        }

        
        gc.setStroke(Color.web("#3b82f6", 0.5));
        gc.setLineWidth(1.5);
        gc.strokeRect(1, 1, WIDTH - 2, HEIGHT - 2);
        gc.strokeRoundRect(1, 1, WIDTH - 2, HEIGHT - 2, 8, 8);
    }

    public String generateBase64Image(String captchaText) {
        
        throw new UnsupportedOperationException("Unimplemented method 'generateBase64Image'");
    }
}
