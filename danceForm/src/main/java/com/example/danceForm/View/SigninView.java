package com.example.danceForm.View;

import com.example.danceForm.Controller.AuthController;
import com.example.danceForm.Model.Coreografo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;

import org.springframework.beans.factory.annotation.Autowired;

@Route("signin")
@PageTitle("DanceForm - Registrazione")
@CssImport("./styles/auth.css")

public class SigninView extends VerticalLayout {

    private final AuthController authController;

    // Campi per registrazione
    private final TextField registerUsername = new TextField("Username");
    private final PasswordField registerPassword = new PasswordField("Password");

    // Messaggio dinamico
    private final Span messageLabel = new Span();

    //Bottoni
    private final Button signinButton = new Button("Sign in");
    private final Button loginButton = new Button("Torna al login");

    @Autowired
    public SigninView(AuthController authController) {
        this.authController = authController;

        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Sfondo
        addClassName("view-background");

        // Titolo + logo
        Image logo = new Image("/logo.png", "Logo");
        logo.addClassName("logo-img");

        Span title = new Span("DanceForm");
        title.addClassName("auth1-title");

        Span subtitle = new Span("Benvenuto, crea un nuovo account:");
        subtitle.addClassName("auth2-title");

        // Pulsanti
        signinButton.addClickListener(e -> registrati());
        signinButton.addClassName("auth1-button");

        loginButton.addClickListener(e -> UI.getCurrent().navigate(""));
        loginButton.addClassName("auth2-button");

        // Container
        VerticalLayout container = new VerticalLayout(title,subtitle, registerUsername, registerPassword, signinButton, loginButton, messageLabel);
        container.addClassName("auth-container");

        add(logo,container);
    }

    private void registrati() {
        String username = registerUsername.getValue();
        String password = registerPassword.getValue();

        // Controlla che i campi non siano vuoti
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("⚠️Completa tutti i campi");
            return;
        }

        try {
            Coreografo nuovo = new Coreografo(username, password);
            authController.registrazione(nuovo);
            // Login automatico dopo registrazione
            UI.getCurrent().getSession().setAttribute("username", username);
            UI.getCurrent().navigate("home");
            Notification.show("✅ Registrazione completata.");
        } catch (IllegalArgumentException e) {
            messageLabel.setText("⚠️ Username già esistente.");
        } catch (Exception e) {
            Notification.show("❌ Errore durante la registrazione." + e.getMessage());
        }
    }

    // getter utili per test
    public TextField getRegisterUsername() {
        return registerUsername;
    }
    public PasswordField getRegisterPassword() {
        return registerPassword;
    }
    public Span getMessageLabel() { return messageLabel; }
    public Button getLoginButton(){
        return loginButton;
    }
    public Button getSigninButton() {
        return signinButton;
    }
}


