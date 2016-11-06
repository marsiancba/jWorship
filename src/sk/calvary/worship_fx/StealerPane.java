/*
 * Created on 14. 10. 2016
 */
package sk.calvary.worship_fx;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

public class StealerPane extends StackPane {
	Tab myTab;

	public StealerPane() {
		sceneProperty().addListener(x -> {
			Parent p = this;
			List<Node> path = new ArrayList<>();
			for (;;) {
				path.add(p);
				p = p.getParent();
				if (p == null) {
					throw new RuntimeException();
				}
				if (p instanceof TabPane) {
					((TabPane) p).getTabs().forEach(t -> {
						if (path.contains(t.getContent())) {
							setMyTab(t);
						}
					});
					break;
				}
			}
		});
	}

	private void setMyTab(Tab t) {
		myTab = t;
		myTab.selectedProperty().addListener(x -> {
			if (myTab.isSelected() && getChildren().size() == 0) {
				for (Node p0 : myTab.getTabPane().lookupAll("#" + getId())) {
					StealerPane p = (StealerPane) p0;
					if (p != this && p.getChildren().size() > 0) {
						List<Node> orig = new ArrayList<>();
						p.getChildren().forEach(orig::add);
						orig.forEach(getChildren()::add);
					}
				}
			}
		});
	}
}
