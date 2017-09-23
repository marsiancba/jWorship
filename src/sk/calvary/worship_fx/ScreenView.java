/*
 * Created on 12. 10. 2016
 */
package sk.calvary.worship_fx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import sk.calvary.worship_fx.Screen.ScreenPart;

public class ScreenView extends Pane implements InvalidationListener {

    private Screen screen = new Screen();
    private final Screen lastScreen = new Screen();

    private final Screen previousScreen = new Screen();

    private final Pane stack;

    private Pane backgroundParent;
    private Pane textParent;

    private Node background;
    private Text text;

    private Pane transitionTextParent;
    private Pane transitionBackgroundParent;
    private Node transitionBackground;

    private boolean invalid = false;

    App getApp() {
        return App.app;
    }

    private final InvalidationListener IL = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            invalidate();
        }
    };

    public ScreenView() {
        setScreen(new Screen());

        setPrefWidth(100);
        setPrefHeight(100);

        stack = new Pane();
        stack.setStyle("-fx-background-color: #000000");
        Utils.clipRegion(stack);

        backgroundParent = new Pane();
        textParent = new Pane();
        stack.getChildren().addAll(backgroundParent, textParent);

        getChildren().add(stack);

        widthProperty().addListener(IL);
        heightProperty().addListener(IL);
    }

    public void setScreen(Screen s) {
        if (s == screen) {
            return;
        }
        screen.removeListener(this);
        previousScreen.copyFrom(screen);

        screen = s;
        screen.addListener(this);
        invalidate();
    }

    void updateScreen() {
        invalid = false;

        clearTransition();

        if (screen.isDifferent(lastScreen, ScreenPart.BACKGROUND)) {
            if (background != null) {
                Utils.nodeAspectHeight(background).removeListener(IL);
            }
            transitionBackgroundParent = backgroundParent;
            transitionBackground = background;

            backgroundParent = new Pane();
            background = null;
            stack.getChildren().add(
                    stack.getChildren().indexOf(transitionBackgroundParent),
                    backgroundParent);

            /*
			backgroundParent.getChildren().clear();
			
			if (background != null) {
				getApp().destroyBackgroundMediaNode(background);
				background = null;
			}*/
            background = getApp()
                    .makeBackgroundMediaNode(screen.getBackgroundMedia());
            if (background != null) {
                backgroundParent.getChildren().add(background);
                Utils.nodeAspectHeight(background).addListener(IL);
            }
        }

        if (screen.isDifferent(lastScreen, ScreenPart.TEXT)) {
            transitionTextParent = textParent;
            textParent = new Pane();
            stack.getChildren().add(
                    stack.getChildren().indexOf(transitionTextParent),
                    textParent);
            // textParent.getChildren().clear();

            text = new Text(screen.getText());
            text.setFill(Color.WHITE);
            text.setTextOrigin(VPos.TOP);
            textParent.getChildren().add(text);
        }

        lastScreen.copyFrom(screen);

        updatePositions();

        startTransition();
    }

    protected void updatePositions() {
        Utils.fitRegion(stack, this, screen.getHeight(), false);
        Utils.fill(backgroundParent, stack);
        Utils.fill(textParent, stack);

        if (background != null) {
            double aspect = Utils.nodeAspectHeight(background).get();
            Utils.fitNode(background, backgroundParent, aspect,
                    screen.isBackgroundFillScreen());
        }

        double width = stack.getWidth();
        double height = stack.getHeight();

        screen.textAreaPartProperty().get().position(textParent, stack);

        if (text != null) {
            text.setX(0);
            text.setY(0);
            boolean wrap = screen.textWordWrapProperty().get();
            text.setWrappingWidth(wrap ? textParent.getWidth() : 0);
            screen.textAlignProperty().get().align(text);
            text.setEffect(null);

            double fontSize = height * screen.getTextFontHeight();
            double maxWidth = textParent.getWidth();
            double maxHeight = textParent.getHeight();
            for (int i = 0;; i++) {
                text.setFill(screen.getTextColor());
                if (screen.isTextCapsLock()) {
                    text.setText(text.getText().toUpperCase());
                }
                text.setFont(new Font(fontSize));
                if (getApp().selectedFont != null){
                    if (getApp().selectedFont.isFontLoaded()){
                        text.setFont(new Font(getApp().selectedFont.getFont().getName(), fontSize));
                    }
                }
                
                if (i >= 300) {
                    break;
                }

                if (i == 0) {
                    text.setStrokeWidth(0);
                }
                Bounds bounds = text.getBoundsInLocal();
                double textWidth = bounds.getMaxX();
                double textHeight = bounds.getMaxY();

                boolean ok = true;
                if (wrap) {
                    if (textWidth > 1.05 * maxWidth) {
                        ok = false;
                    }
                } else if (textWidth > 1.001 * maxWidth) {
                    ok = false;
                }
                if (textHeight >= 1.001 * maxHeight) {
                    ok = false;
                }
                if (ok) {
                    break;
                }

                double shrink = 0.99;
                if (textHeight > 2 * maxHeight) {
                    shrink = Math.pow(maxHeight / textHeight, 0.333);
                }
                if (textWidth > 2 * maxWidth) {
                    shrink = Math.pow(textWidth / textWidth, 0.333);
                }

                fontSize *= shrink;
            }

            if (!wrap) {
                // musime to este potencialne posunut
                double textWidth = text.getBoundsInLocal().getMaxX();
                if (textWidth < maxWidth) {
                    switch (screen.textAlignProperty().get()) {
                        case CENTER:
                            text.xProperty().set((maxWidth - textWidth) / 2);
                            break;
                        case RIGHT:
                            text.xProperty().set(maxWidth - textWidth);
                            break;
                        case LEFT:
                        // nic
                    }
                }
            }

            text.setStrokeWidth(fontSize * 0.01);

            if (screen.isTextShadow()) {
                DropShadow shadow = new DropShadow();
                shadow.setOffsetX(fontSize * 0.05);
                shadow.setOffsetY(fontSize * 0.05);
                shadow.setRadius(fontSize * 0.1);
                text.setEffect(shadow);
            }
        }

    }

    void clearTransition() {
        if (transitionBackground != null) {
            getApp().destroyBackgroundMediaNode(transitionBackground);
            transitionBackground = null;
        }
        if (transitionBackgroundParent != null) {
            stack.getChildren().remove(transitionBackgroundParent);
            transitionBackgroundParent = null;
        }
        if (transitionTextParent != null) {
            stack.getChildren().remove(transitionTextParent);
            transitionTextParent = null;
        }

        backgroundParent.setOpacity(1);
        textParent.setOpacity(1);
    }

    void makeTransition(Node from, Node to) {
        double duration = getApp().transitionDurationProperty().get();

        if (duration > 0) {
            FadeTransition trFrom = new FadeTransition(
                    Duration.seconds(duration), from);
            trFrom.setFromValue(1);
            trFrom.setToValue(0);

            FadeTransition trTo = new FadeTransition(Duration.seconds(duration),
                    to);
            trTo.setFromValue(0);
            trTo.setToValue(1);

            trTo.setOnFinished(e -> {
                clearTransition();
            });

            trFrom.play();
            trTo.play();
        } else {
            clearTransition();
        }
    }

    void startTransition() {
        if (this == getApp().screenViewPrepared) {
            // tu nerobime transition
            clearTransition();
            return;
        }
        if (transitionTextParent != null) {
            makeTransition(transitionTextParent, textParent);
        }
        if (transitionBackgroundParent != null) {
            makeTransition(transitionBackgroundParent, backgroundParent);
        }
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable == screen) {
            invalidate();
        }
    }

    private void invalidate() {
        if (invalid) {
            return;
        }
        invalid = true;
        Platform.runLater(this::updateScreen);
    }
}
