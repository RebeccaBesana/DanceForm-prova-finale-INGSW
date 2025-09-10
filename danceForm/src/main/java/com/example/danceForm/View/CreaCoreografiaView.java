package com.example.danceForm.View;

import com.example.danceForm.Model.Observer.Coreografia;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.example.danceForm.Controller.CoreografiaController;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista per creare una nuova coreografia.
 * L'utente inserisce il nome, il numero di ballerini e i nomi per ciascun ballerino.
 */

@Route("crea-coreografia")
@PageTitle("DanceForm - Crea Coreografia")
@CssImport("./styles/home.css")

public class CreaCoreografiaView extends ProtectedView {

    private final CoreografiaController coreografiaController;

    private final TextField nomeCoreografiaField = new TextField("Nome coreografia");
    private final IntegerField numeroBalleriniField = new IntegerField("Numero ballerini");

    private final VerticalLayout balleriniLayout = new VerticalLayout();

    private final List<TextField> campiNomiBallerini = new ArrayList<>();

    //messaggi
    private Span messageLabel = new Span();

    //bottoni
    protected Button generaCampiBalleriniButton = new Button("Inserisci nomi ballerini");
    protected Button salvaButton = new Button("Salva coreografia");

    @Autowired
    public CreaCoreografiaView(CoreografiaController coreografiaController) {
        this.coreografiaController = coreografiaController;

        setSpacing(true);
        setPadding(true);

        addClassName("app-background");
        // NAVBAR
        Image logo = new Image("/logo.png", "Logo");
        logo.addClassName("logo-img");

        Span appName = new Span("DanceForm");
        Div logoContainer = new Div(logo, appName);
        logoContainer.addClassName("logo-container");

        Span spacer = new Span();
        spacer.getStyle().set("flex-grow", "1");

        Button tornaHome = new Button("torna alla Home");
        tornaHome.addClassName("logout-button");
        tornaHome.addClickListener(e -> {getUI().ifPresent(ui -> ui.navigate("home"));});

        HorizontalLayout navbar = new HorizontalLayout(logoContainer,spacer,tornaHome);
        navbar.setWidthFull();
        navbar.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        navbar.addClassName("navbar");
        add(navbar);

        // Container principale
        VerticalLayout container = new VerticalLayout();
        container.addClassName("home-container");
        nomeCoreografiaField.addClassName("text-field");
        numeroBalleriniField.addClassName("text-field");

        generaCampiBalleriniButton.addClassName("secondary-button");

        salvaButton.addClassName("primary-button");

        balleriniLayout.addClassName("ballerini-layout");

        container.add(
                nomeCoreografiaField,
                numeroBalleriniField,
                generaCampiBalleriniButton,
                balleriniLayout,
                messageLabel,
                salvaButton
        );

        add(container);

        generaCampiBalleriniButton.addClickListener(e -> generaCampiBallerini());
        salvaButton.addClickListener(e -> salvaCoreografia());
    }

    //Genera dinamicamente i campi di input per i nomi dei ballerini in base al numero inserito.
    protected void generaCampiBallerini() {
        Integer numero = numeroBalleriniField.getValue();
        if (numero == null || numero <= 0) {
            messageLabel.setText("⚠️ Inserisci un numero valido di ballerini (maggiore di 0)");
            return;
        }

        balleriniLayout.removeAll();
        campiNomiBallerini.clear();

        for (int i = 1; i <= numero; i++) {
            TextField nomeBallerino = new TextField("Nome ballerino " + i);
            nomeBallerino.addClassName("text-field");
            balleriniLayout.add(nomeBallerino);
            campiNomiBallerini.add(nomeBallerino);
        }
    }

    // Salva la coreografia
    private void salvaCoreografia() {
       try {
           String nome = nomeCoreografiaField.getValue();
           Integer numero = numeroBalleriniField.getValue();
           List<String> nomi = campiNomiBallerini.stream()
                   .map(TextField::getValue)
                   .toList();
           Long coreografoId = (Long) UI.getCurrent().getSession().getAttribute("coreografoId");

           Coreografia coreografia = coreografiaController.creaCoreografia(nome, numero, nomi, coreografoId);
           coreografiaController.salvaCoreografia(coreografia);

           Notification.show("✅ Coreografia creata con successo");
           try {
               RouteConfiguration.forSessionScope().removeRoute("crea-coreografia");
           } catch (Exception ignored) {
               // Ignorato nei test o se la sessione non è attiva
           }
           getUI().ifPresent(ui -> ui.navigate("home"));

       } catch(IllegalArgumentException ex) {
            messageLabel.setText("⚠️ " + ex.getMessage());
       } catch (IOException ex) {
           Notification.show("❌ Errore durante il salvataggio: " + ex.getMessage());
       }
    }

    //getter per test

    public IntegerField getNumeroBalleriniField() {
        return numeroBalleriniField;
    }
    public VerticalLayout getBalleriniLayout() {
        return balleriniLayout;
    }
    public Span getMessageLabel() {
        return messageLabel;
    }
    public TextField getNomeCoreografiaField() {
        return nomeCoreografiaField;
    }
    public List<TextField> getCampiNomiBallerini() {
        return campiNomiBallerini;
    }
}
