package sk.calvary.misc.ui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class UITools {
    public static UndoManager addUndoSupport(JTextComponent textcomp) {
        final UndoManager undo = new UndoManager();
        Document doc = textcomp.getDocument();

        // Listen for undo and redo events
        doc.addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent evt) {
                undo.addEdit(evt.getEdit());
            }

        });

        // Create an undo action and add it to the text component
        textcomp.getActionMap().put("Undo", new AbstractAction("Undo") {
			private static final long serialVersionUID = 7600608362351595713L;

			public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo.canUndo()) {
                        undo.undo();
                    }
                } catch (CannotUndoException e) {
                }
            }
        });

        // Bind the undo action to ctl-Z
        textcomp.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

        // Create a redo action and add it to the text component
        textcomp.getActionMap().put("Redo", new AbstractAction("Redo") {
			private static final long serialVersionUID = -2307470037115282120L;

			public void actionPerformed(ActionEvent evt) {
                try {
                    if (undo.canRedo()) {
                        undo.redo();
                    }
                } catch (CannotRedoException e) {
                }
            }
        });

        // Bind the redo action to ctl-Y
        textcomp.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
        return undo;
    }

    public static void autoLayout(Container c) {
        c.setLayout(new GridLayout(0, 1));
    }
}
