package main.utils;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class PushAnimation {

    public static void nodeAnimation(Node node) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), node);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
        scaleTransition.setOnFinished(e -> {
            ScaleTransition reverseTransition = new ScaleTransition(Duration.millis(100), node);
            reverseTransition.setToX(1);
            reverseTransition.setToY(1);
            reverseTransition.play();
        });
    }

}
