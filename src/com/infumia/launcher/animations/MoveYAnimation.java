/**
 *    Copyright 2019-2020 Infumia
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.infumia.launcher.animations;

import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.util.Duration;

public class MoveYAnimation extends Transition{

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
    public MoveYAnimation(final Node node, double startValue, double endValue, Duration duration) {
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
            node.setLayoutY(n1.doubleValue());
        });


        setCycleDuration(Duration.seconds(0.3));
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
