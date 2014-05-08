package kickupper.ui;

import java.util.Date;

import javax.servlet.annotation.WebServlet;

import kickupper.Evaluation;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
@Theme("kickupper")
public class KickupperUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = KickupperUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout container = new VerticalLayout();
		final VerticalLayout layout = new VerticalLayout();
//		layout.setSizeFull();
//		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);
		
		Label header = new Label("KickUpper");
		header.setStyleName("header");
		

		final TextArea textArea = new TextArea();
		textArea.setValue("Enter project description here...");
		textArea.setWordwrap(true);
		textArea.setHeight("200px");
		textArea.setWidth("700px");
		final Label resultLabel = new Label("");
//		resultLabel.setHeight("200px");
		resultLabel.setSizeFull();
		resultLabel.setWidth("700px");
		resultLabel.setStyleName("resultL");
//		resultLabel.setWidth("500px");
		// Panel panel = new Panel("");
		// panel.setWidth("300px");
		//
//		Evaluation.evaluate("hello god it is");

		resultLabel.setContentMode(ContentMode.HTML);
		Button button = new Button("Analyse my project");
//		button.setWidth("150px");
		button.setIcon(new ThemeResource("images/color20.png"));
		
		
		button.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				String str = Evaluation.evaluateForGUI(textArea.getValue());
				resultLabel.setValue(str);
			}
		});

		layout.addComponent(header);
		layout.addComponent(textArea);
		layout.addComponent(button);
		layout.addComponent(resultLabel);
		

		layout.setComponentAlignment(header, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(button, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(textArea, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(resultLabel, Alignment.MIDDLE_CENTER);
	}
}