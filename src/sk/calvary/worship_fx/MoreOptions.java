/*
 * Created on Nov 17, 2016
 */
package sk.calvary.worship_fx;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class MoreOptions implements Initializable {

    @FXML
    Dialog<Void> dlg;
    @FXML
    ColorPicker ColorPicker1;
    @FXML
    Label Text;
    @FXML
    CheckBox boxCapsLock;
    @FXML
    ComboBox<String> comboFont;

    private static boolean ready;
    private static boolean CapsLock = false;
    private static boolean CapsLockTmp;
    private static Font Font;
    private static Color colorText = Color.WHITE;
    private static Color colorTextTmp;
    private static String Obsah;
    private static String ChoosenFont = "Calibri";

    App getApp() {
        return App.app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboFont.setValue(ChoosenFont);
        Font = Font.loadFont(MoreOptions.class.getResource("/sk/calvary/worship/" + comboFont.getValue() + ".ttf").toExternalForm(), 40);       
   
        Text.setText("Kŕdeľ ďatľov \n"
                + "učí koňa žrať kôru.");
        
        colorTextTmp = colorText;
        CapsLockTmp = CapsLock;
        ColorPicker1.setValue(colorTextTmp);
        boxCapsLock.setSelected(CapsLockTmp);
        if (CapsLockTmp) {
            Obsah = Text.getText();
            Text.setText(Obsah.toUpperCase());
        }
        Text.setTextAlignment(TextAlignment.CENTER);
        Text.setTextFill(colorTextTmp);
        Text.setFont(Font);
    }

    @FXML
    public void CapsLock() {
        if (boxCapsLock.isSelected()) {
            Obsah = Text.getText();
            Text.setText(Obsah.toUpperCase());
            CapsLockTmp = true;
        }
        if ((!boxCapsLock.isSelected()) && (Obsah!=null)) {
            Text.setText(Obsah);
            CapsLockTmp = false;
        }
    }

    @FXML
    public void ColorPicker1() {
        Text.setTextFill(ColorPicker1.getValue());
        colorTextTmp = ColorPicker1.getValue();
    }

    public void updateOptions() {
        CapsLock = CapsLockTmp;
        colorText = colorTextTmp;
        ChoosenFont = comboFont.getValue();
        ScreenView.setFont(comboFont.getValue());
    }

    @FXML
    public void FontName() {
        Font = Font.loadFont(MoreOptions.class.getResource("/sk/calvary/worship/" + comboFont.getValue() + ".ttf").toExternalForm(), 40);       
        Text.setFont(Font);
    }

    public static boolean isReady() {
        return ready;
    }

    public static boolean isCapsLock() {
        return CapsLock;
    }

    public static Color getTextColor() {
        return colorText;
    }
}
