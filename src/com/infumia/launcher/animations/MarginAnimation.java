package com.infumia.launcher.animations;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class MarginAnimation extends Transition {

    private Timeline timeline;
    private final Node node;
    private boolean oldCache = false;
    private CacheHint oldCacheHint = CacheHint.DEFAULT;
    private final boolean useCache = true;
    private double startValue;
    private double endValue;
    private Duration duration;

    private final DoubleProperty x = new SimpleDoubleProperty();

    /**
     * Create new ShakeTransition
     *
     * @param node The node to affect
     */

    public MarginAnimation(final Node node, double startValue, double endValue, Duration duration) {
        this.node = node;
        this.startValue = startValue;
        this.endValue = endValue;
        this.duration = duration;
        statusProperty().addListener((ov, t, newStatus) -> {
            switch (newStatus) {
                case RUNNING:
                    starting();
                    break;
                default:
                    stopping();
                    break;
            }
        });

        double sec = this.duration.toSeconds();

        this.timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(x, this.startValue, Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(sec), new KeyValue(x, this.endValue, Interpolator.LINEAR))
        );
        x.addListener((ob, n, n1) -> {
            node.getProperties().put("vbox-margin", new Insets(n1.doubleValue(),0,0,0));
            node.getParent().requestLayout();
        });


        setCycleDuration(Duration.seconds(1));
        setDelay(Duration.seconds(0));
    }
    /**
     * Called when the animation is starting
     */
    protected final void starting() {
        if (useCache) {
            oldCache = node.isCache();
            oldCacheHint = node.getCacheHint();
            node.setCache(true);
            node.setCacheHint(CacheHint.SPEED);
        }
    }

    /**
     * Called when the animation is stopping
     */
    protected final void stopping() {
        if (useCache) {
            node.setCache(oldCache);
            node.setCacheHint(oldCacheHint);
        }
    }
    @Override
    protected void interpolate(double d) {
        timeline.playFrom(Duration.seconds(d));
        timeline.stop();
    }

    public void setStartValue(double startValue) {
        this.startValue = startValue;
        this.timeline.getKeyFrames().remove(this.timeline.getKeyFrames().get(0));
        this.timeline.getKeyFrames().add(0, new KeyFrame(Duration.seconds(0), new KeyValue(x, this.startValue, Interpolator.LINEAR)));
    }

    public void setEndValue(double endValue) {
        this.endValue = endValue;
        this.timeline.getKeyFrames().remove(this.timeline.getKeyFrames().get(1));
        this.timeline.getKeyFrames().add(1, new KeyFrame(duration, new KeyValue(x, this.endValue, Interpolator.LINEAR)));
    }
}