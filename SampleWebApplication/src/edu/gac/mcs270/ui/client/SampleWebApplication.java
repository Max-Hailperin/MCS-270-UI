package edu.gac.mcs270.ui.client;

import edu.gac.mcs270.ui.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SampleWebApplication implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final Label instructionsLabel = new Label("Please enter your name:");
		final Button sendButton = new Button("Send");
		final TextBox nameField = new TextBox();
		nameField.setText("GWT User");
		final Label errorLabel = new Label();
		errorLabel.setHeight("1em"); // so it doesn't change with vs w/o text

		
		
		// We can add style names to widgets
		sendButton.addStyleName("sendButton");
		errorLabel.addStyleName("error");
		instructionsLabel.addStyleName("instructions");
		
		// Create some panels to hold the widgets together
		final VerticalPanel mainPanel = new VerticalPanel();
		final HorizontalPanel entryPanel = new HorizontalPanel();
		final VerticalPanel outerPanel = new VerticalPanel();
		outerPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		
		// Assemble the widgets into the panels
		entryPanel.add(nameField);
		entryPanel.add(sendButton);
		mainPanel.add(instructionsLabel);
		mainPanel.add(entryPanel);
		mainPanel.add(errorLabel);
		outerPanel.add(mainPanel);     

		// Add the outerPanel to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("applicationContainer").add(outerPanel);

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();
		
		// Instead of displaying each greeting in a dialog box,
		// accumulate them into a scrolling display.
		
		final StackPanel greetingsPanel = new StackPanel();
		final ScrollPanel greetingsScrollPanel = new ScrollPanel();
		greetingsPanel.setSize("30em", "20em"); //width and length of comment box after expand
		greetingsScrollPanel.setSize("50em", "30em");
		greetingsScrollPanel.add(greetingsPanel);
		final CaptionPanel greetingsCaptionPanel = new CaptionPanel("Greetings");
		greetingsCaptionPanel.add(greetingsScrollPanel);
		final Label spacer = new Label();
		spacer.setHeight("1em");
		outerPanel.add(spacer);
		outerPanel.add(greetingsCaptionPanel);

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			private boolean firstTime = true;
			
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				if(firstTime)
					firstTime = false;
				final HTML serverResponseLabel = new HTML();
				
//				testPanel.add(new HTML("<b>Sending name to the server:</b><br>"));
//				testPanel.add(textToServerLabel);
//				testPanel.add(new HTML("<br><b>Server replies:</b>"));
//				testPanel.add(serverResponseLabel);
				
				greetingsPanel.insert(serverResponseLabel, 0);
				greetingsPanel.setStackText(0, textToServer);
				greetingsPanel.showStack(0);
				
				greetingService.greetServer(textToServer,
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								serverResponseLabel
										.addStyleName("error");
								display(SERVER_ERROR);
							}

							public void onSuccess(String result) {
								display(result);
							}
							
							private void display(String message){
								serverResponseLabel.setHTML(message);
								greetingsScrollPanel.scrollToTop();
								sendButton.setEnabled(true);
								sendButton.setFocus(true);
							}
						});
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);
	}
}
