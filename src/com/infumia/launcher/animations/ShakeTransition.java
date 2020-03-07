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

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.util.Duration;

public class ShakeTransition extends Transition {

    private final Interpolator WEB_EASE = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
    private final Timeline timeline;
    private final Node node;
    private boolean oldCache = false;
    private CacheHint oldCacheHint = CacheHint.DEFAULT;
    private final boolean useCache = true;
    private final double xIni;

    private final DoubleProperty x = new SimpleDoubleProperty();

    /**
     * Create new ShakeTransition
     *
     * @param node The node to affect
     */
    public ShakeTransition(final Node node) {
        this.node = node;
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

        this.timeline = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(x, 5, WEB_EASE)),
                new KeyFrame(Duration.millis(100), new KeyValue(x, -5, WEB_EASE)),
                new KeyFrame(Duration.millis(200), new KeyValue(x, 5, WEB_EASE)),
                new KeyFrame(Duration.millis(300), new KeyValue(x, -5, WEB_EASE)),
                new KeyFrame(Duration.millis(400), new KeyValue(x, 5, WEB_EASE)),
                new KeyFrame(Duration.millis(500), new KeyValue(x, -5, WEB_EASE)),
                new KeyFrame(Duration.millis(600), new KeyValue(x, 5, WEB_EASE)),
                new KeyFrame(Duration.millis(700), new KeyValue(x, -5, WEB_EASE)),
                new KeyFrame(Duration.millis(800), new KeyValue(x, 0, WEB_EASE))
        );
        xIni = node.getTranslateX();
        x.addListener((ob, n, n1) -> node.setTranslateX(xIni + n1.doubleValue()));

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
}