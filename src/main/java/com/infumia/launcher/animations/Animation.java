package com.infumia.launcher.animations;

import com.jfoenix.controls.JFXSpinner;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animation {

    public static void salla(Node node) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.17), node);
        scaleTransition.setFromX(0.2);
        scaleTransition.setFromY(0.2);
        scaleTransition.setToX(1.03);
        scaleTransition.setToY(1.03);
        scaleTransition.play();

        scaleTransition.setOnFinished(event -> {
            ScaleTransition scaleTransition2 = new ScaleTransition(Duration.seconds(0.13), node);
            scaleTransition2.setFromX(1.03);
            scaleTransition2.setFromY(1.03);
            scaleTransition2.setToX(0.99);
            scaleTransition2.setToY(0.99);
            scaleTransition2.play();

            scaleTransition2.setOnFinished(event1 -> {
                ScaleTransition scaleTransition3 = new ScaleTransition(Duration.seconds(0.12), node);
                scaleTransition3.setFromX(0.99);
                scaleTransition3.setFromY(0.99);
                scaleTransition3.setToX(1);
                scaleTransition3.setToY(1);
                scaleTransition3.play();
            });
        });
    }

    public static void zoomOut(Node node) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.08), node);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(0.99);
        scaleTransition.setToY(0.99);
        scaleTransition.play();

        scaleTransition.setOnFinished(event -> {
            ScaleTransition scaleTransition2 = new ScaleTransition(Duration.seconds(0.08), node);
            scaleTransition2.setFromX(0.99);
            scaleTransition2.setFromY(0.99);
            scaleTransition2.setToX(1.03);
            scaleTransition2.setToY(1.03);
            scaleTransition2.play();

            scaleTransition2.setOnFinished(event1 -> {
                ScaleTransition scaleTransition3 = new ScaleTransition(Duration.seconds(0.08), node);
                scaleTransition3.setFromX(1.03);
                scaleTransition3.setFromY(1.03);
                scaleTransition3.setToX(0.2);
                scaleTransition3.setToY(0.2);
                scaleTransition3.play();
            });
        });
    }

    public static ShakeTransition shake(Node node) {
        ShakeTransition anim = new ShakeTransition(node);
        return anim;
    }

    public static FadeTransition fadeIn(Duration duration, Node node) {
        FadeTransition fadeTransition = new FadeTransition(duration, node);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        return fadeTransition;
    }

    public static FadeTransition fadeOut(Duration duration, Node node) {
        FadeTransition fadeTransition = new FadeTransition(duration, node);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        return fadeTransition;
    }

    public static JFXSpinnerAnimation spinnerAnimation(Duration duration,double startValue,double endValue, JFXSpinner spinner) {
        JFXSpinnerAnimation anim = new JFXSpinnerAnimation(spinner, startValue, endValue, duration);
        return anim;
    }
}
