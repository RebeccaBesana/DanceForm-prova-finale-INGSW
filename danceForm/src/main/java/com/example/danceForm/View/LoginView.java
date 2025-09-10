package com.example.danceForm.View;

import com.example.danceForm.Controller.AuthController;
import com.example.danceForm.Model.Coreografo;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.dependency.CssImport;

/**
 * View iniziale per login.
 * Utilizza il controller AuthController per accedere ai dati e validare le credenziali.
 */

@Route("") // Imposta questa come pagina iniziale
@PageTitle("DanceForm - Login")
@CssImport("./styles/auth.css")
public class LoginView extends VerticalLayout {

    private final AuthController authController;

    // Campi per login
    private final TextField loginUsername = new TextField("Username");
    private final PasswordField loginPassword = new PasswordField("Password");

    // Messaggio dinamico
    private final Span messageLabel = new Span();

    // Bottoni
    private final Button loginButton = new Button("Login");
    private final Button registerButton = new Button("Crea un account");

    @Autowired
    public LoginView(AuthController authController) {
        this.authController = authController;

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        // Sfondo
        addClassName("view-background");

        // Titolo + logo
        Image logo = new Image("/logo.png", "Logo");
        logo.addClassName("logo-img");

        Span title = new Span("DanceForm");
        title.addClassName("auth1-title");

        // Pulsanti
        //Button loginButton = new Button("Login");
        loginButton.addClickListener(e -> login());
        loginButton.addClickShortcut(Key.ENTER);
        loginButton.addClassName("auth1-button");

        //Button registerButton = new Button("Crea un account");
        registerButton.addClickListener(e -> UI.getCurrent().navigate("signin"));
        registerButton.addClassName("auth2-button");

        // Container
        VerticalLayout container = new VerticalLayout(title, loginUsername, loginPassword, loginButton, registerButton, messageLabel);
        container.addClassName("auth-container");

        add(logo,container);
    }


    private void login() {
        try {
            String username = loginUsername.getValue();
            String password = loginPassword.getValue();

            Coreografo user = authController.login(username, password);
            if (user != null) {
                UI.getCurrent().getSession().setAttribute("coreografoId", user.getId());
                UI.getCurrent().getSession().setAttribute("username", user.getUsername());
                UI.getCurrent().navigate("home");
            } else {
                messageLabel.setText("⚠️ Username o password errati.");
            }
        } catch (Exception ex) {
            messageLabel.setText("❌ Errore durante il login: " + ex.getMessage());
        }
    }

    // getter utili per test

    public TextField getLoginUsername() { return loginUsername; }
    public PasswordField getLoginPassword() { return loginPassword; }
    public Span getMessageLabel() { return messageLabel; }
    public Button getLoginButton() {
        return loginButton;
    }
    public Button getRegisterButton() {
        return registerButton;
    }
}
