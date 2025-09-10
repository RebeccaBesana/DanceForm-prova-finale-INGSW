package com.example.danceForm.View;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

//opzionale
@Route("logout")
@PageTitle("Logout")
@CssImport("./styles/auth.css")
public class LogoutView extends ProtectedView{

    private final Button tornaAlLogin = new Button("Torna al login");

    public LogoutView() {
        setSpacing(true);
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Sfondo
        addClassName("view-background");

        // logo
        Image logo = new Image("/logo.png", "Logo");
        logo.addClassName("logo-img");

        H1 titolo = new H1("Logout effettuato");
        titolo.addClassName("auth1-title");
        Paragraph messaggio = new Paragraph("Hai effettuato il logout con successo.");
        messaggio.addClassName("auth2-title");

        tornaAlLogin.addClickListener(event -> {
            UI.getCurrent().getSession().close();
            UI.getCurrent().navigate("");
        });
        tornaAlLogin.addClassName("auth2-button");

        VerticalLayout container = new VerticalLayout(titolo, messaggio,tornaAlLogin);
        container.addClassName("auth-container");

        add(logo,container);
    }

    public Button getTornaAlLogin() {
        return tornaAlLogin;
    }
}
