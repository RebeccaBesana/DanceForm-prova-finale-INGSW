package com.example.danceForm.View;

import com.example.danceForm.Model.*;
import com.example.danceForm.Model.Observer.Coreografia;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.example.danceForm.Controller.CoreografiaController;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

@Route("home")
@PageTitle("DanceForm - Home")
@CssImport("./styles/home.css")

public class HomeView extends ProtectedView {

    private final CoreografiaController coreografiaController;
    private final Grid<Coreografia> coreografiaGrid = new Grid<>(Coreografia.class);

    //bottoni per test
    protected Button nuovaCoreografiaButton = new Button("Crea nuova coreografia");
    protected Button notificationButton = new Button(new Icon(VaadinIcon.BELL));

    @Autowired
    public HomeView(CoreografiaController coreografiaController) {
        this.coreografiaController = coreografiaController;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        coreografiaGrid.setColumns("id", "nome");
        coreografiaGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        addClassName("app-background");

        // NAVBAR
        Image logo = new Image("/logo.png", "Logo");
        logo.addClassName("logo-img");

        Span appName = new Span("DanceForm");
        Div logoContainer = new Div(logo, appName);
        logoContainer.addClassName("logo-container");

        Button logoutButton = new Button("Logout");
        logoutButton.addClickListener(e -> UI.getCurrent().navigate("logout"));
        logoutButton.addClassName("logout-button");

        notificationButton.addClassName("notification-button");
        Dialog notificationDialog = new Dialog();
        notificationDialog.setCloseOnOutsideClick(true);
        notificationDialog.setHeaderTitle("Notifiche");

        VerticalLayout notificationContent = new VerticalLayout();
        notificationContent.setSpacing(false);
        notificationContent.setPadding(false);
        notificationDialog.add(notificationContent);

        notificationButton.addClickListener(e -> {
            try {
                mostraNotifiche(notificationContent);
                notificationDialog.open();
            } catch (IOException ex) {
                Notification.show("Errore caricamento notifiche");
                ex.printStackTrace();
            }
        });

        Span spacer = new Span();
        spacer.getStyle().set("flex-grow", "1");

        HorizontalLayout navbar = new HorizontalLayout(logoContainer, spacer, notificationButton, logoutButton);
        navbar.setWidthFull();
        navbar.setPadding(true);
        navbar.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        navbar.addClassName("navbar");
        add(navbar);

        // Container principale
        VerticalLayout container = new VerticalLayout();
        container.addClassName("home-container");

        String coreografoNome = (String) UI.getCurrent().getSession().getAttribute("username");
        Span title = new Span("Benvenuto nella tua Home - "+coreografoNome);
        title.addClassName("welcome-title");


        nuovaCoreografiaButton.addClickListener(e -> creaNuovaCoreografia());
        nuovaCoreografiaButton.addClassName("primary-button");

        container.add(title, nuovaCoreografiaButton);

        // Griglia + AZIONI DENTRO LA GRID
        coreografiaGrid.addComponentColumn(coreografia -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.addClassName("grid-action-buttons");

            Button visualizza = new Button("visualizza", e -> getUI().ifPresent(ui -> ui.navigate("coreografia/" + coreografia.getId())));
            Button rinomina = new Button("rinomina️", e -> apriDialogRinomina(coreografia));
            Button elimina = new Button("elimina", e -> eliminaCoreografia(coreografia));
            Button condividi = new Button("condividi", e -> apriDialogCondivisione(coreografia));

            Stream.of(visualizza, rinomina, elimina, condividi).forEach(btn -> btn.addClassName("secondary-button"));
            layout.add(visualizza, rinomina, elimina, condividi);
            return layout;
        }).setHeader("Azioni").setAutoWidth(true).setFlexGrow(0);

        container.add(coreografiaGrid);
        add(container);

        // Caricamento iniziale
        try {
            aggiornaElencoCoreografie();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Carica tutte le coreografie dalla repository e aggiorna la griglia.
    protected void aggiornaElencoCoreografie() throws IOException {
        Long coreografoId = (Long) UI.getCurrent().getSession().getAttribute("coreografoId");
        List<Coreografia> coreografie = coreografiaController.getCoreografiePerUtente(coreografoId);
        coreografiaGrid.setItems(coreografie);
    }

    //Naviga alla pagina di creazione coreografia.
    private void creaNuovaCoreografia() {
        UI.getCurrent().navigate("crea-coreografia");
    }

    //Helper
    protected void eliminaCoreografia(Coreografia coreografia) {
        try {
            Long CoreografoID = (Long) UI.getCurrent().getSession().getAttribute("coreografoId");
            Notification.show(coreografiaController.eliminaCoreografia(coreografia, CoreografoID));
            aggiornaElencoCoreografie();
        } catch (IOException ex) {
            Notification.show("Errore eliminazione: " + ex.getMessage());
        }
    }

    protected void apriDialogRinomina(Coreografia coreografia) {
        Dialog dialog = new Dialog();

        //inserire nuovo nome coreografia
        TextField nuovoNomeField = new TextField("Nuovo nome");
        nuovoNomeField.setValue(coreografia.getNome());
        nuovoNomeField.setWidthFull();

        //messaggi
        Span messageLabel = new Span();

        Button salvaBtn = new Button("Salva", event -> {
            String nuovoNome = nuovoNomeField.getValue().trim();
            if (nuovoNome.isEmpty()) {
                messageLabel.setText("Il nome non può essere vuoto");
                return;
            }

            // Aggiorna il nome nella coreografia
            try {
                // Salva la coreografia con il nuovo nome (notifica observer internamente)
                coreografiaController.rinominaCoreografia(coreografia.getId(), nuovoNome);

                // Aggiorna la griglia in HomeView
                aggiornaElencoCoreografie();

                Notification.show("Nome coreografia aggiornato");
                dialog.close();

            } catch (IOException e) {
                Notification.show("Errore aggiornamento coreografia: " + e.getMessage());
            }
        });
        salvaBtn.addClassName("secondary-button");

        Button annullaBtn = new Button("Annulla", e -> dialog.close());
        annullaBtn.addClassName("secondary-button");

        HorizontalLayout buttons = new HorizontalLayout(salvaBtn, annullaBtn);
        dialog.add(nuovoNomeField, messageLabel, buttons);
        dialog.open();
    }

    protected void apriDialogCondivisione(Coreografia coreografia) {
        Dialog dialog = new Dialog();

        TextField usernameField = new TextField("Username del coreografo");
        usernameField.setWidthFull();

        //messaggi
        Span messageLabel = new Span();

        Button condividiBtn = new Button("Condividi", event -> {
            String username = usernameField.getValue().trim();

            if (username.isEmpty()) {
                messageLabel.setText("Inserisci uno username valido");
                return;
            }

            try {
                // Chiamata al controller che aggiunge l'osservatore
                coreografiaController.aggiungiOsservatore(coreografia.getId(), username);
                Notification.show("Coreografia condivisa con " + username);
                dialog.close();

            } catch (IllegalArgumentException e) {
                Notification.show("Username non trovato");
            } catch (Exception e) {
                Notification.show("Errore nella condivisione: " + e.getMessage());
            }
        });
        condividiBtn.addClassName("secondary-button");

        Button annullaBtn = new Button("Annulla", e -> dialog.close());
        annullaBtn.addClassName("secondary-button");
        HorizontalLayout buttons = new HorizontalLayout(condividiBtn, annullaBtn);

        dialog.add(usernameField, messageLabel, buttons);
        dialog.open();
    }

    protected void mostraNotifiche(VerticalLayout layout) throws IOException {
        layout.removeAll(); // Pulisci il contenuto precedente
        Long coreografoId = (Long) UI.getCurrent().getSession().getAttribute("coreografoId");
        List<Notifica> notifiche = coreografiaController.recuperaNotifiche(coreografoId);

        if (notifiche.isEmpty()) {
            layout.add(new Span("Nessuna nuova notifica"));
        } else {
            for (Notifica notifica : notifiche) {
                Span msg = new Span("⚠️"+notifica.getMessaggio());
                msg.setWidthFull();
                layout.add(msg);
            }
        }
    }

    public Grid<Coreografia> getCoreografiaGrid() {
        return coreografiaGrid;
    }

}