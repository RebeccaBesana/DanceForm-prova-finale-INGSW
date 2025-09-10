package com.example.danceForm.View;

import com.example.danceForm.Controller.CoreografiaController;
import com.example.danceForm.Model.*;
import com.example.danceForm.Dto.BallerinoPoint;
import com.example.danceForm.Dto.FormazioneViewData;
import com.example.danceForm.Model.Observer.Coreografia;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;


@Route("coreografia/:id")
@PageTitle("Visualizza coreografia")
@CssImport("./styles/coreografia.css")

public class VisualizzaCoreografiaView extends ProtectedView implements BeforeEnterObserver {

    private final CoreografiaController coreografiaController;
    private Coreografia coreografia;
    private int formazioneCorrente = 0;

    //componenti principali
    private final Span titoloCoreografia = new Span();
    private final Span nomeFormazione = new Span();
    private final VerticalLayout layoutBallerini = new VerticalLayout();
    private final Div palco = new Div();
    private final Span noteSpan = new Span();
    private final Span minutiSpan = new Span();
    private final Button btnPrecedente = new Button("←");
    private final Button btnSuccessiva = new Button("→");

    @Autowired
    public VisualizzaCoreografiaView(CoreografiaController coreografiaController) {
        this.coreografiaController = coreografiaController;

        addClassName("coreo-background");

        // TOP BAR: nome coreografia a sinistra + bottoni in alto a destra
        Button btnModifica = new Button("Modifica");
        Button btnBack = new Button("Torna alla home");
        btnModifica.addClassName("top-button");
        btnBack.addClassName("top-button");

        Span spacer = new Span();
        spacer.getStyle().set("flex-grow", "1");

        titoloCoreografia.addClassName("text1");
        Span modalita = new Span(" - modalità visualizza -");
        modalita.addClassName("mod-text");

        HorizontalLayout topBar = new HorizontalLayout(titoloCoreografia,modalita, spacer, btnModifica, btnBack);
        topBar.setWidthFull();
        topBar.setPadding(true);
        topBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        btnBack.addClickListener(e -> UI.getCurrent().navigate("home"));
        btnModifica.addClickListener(e -> {
            if (coreografia != null) {
                UI.getCurrent().navigate("modifica-coreografia/" + coreografia.getId());
            }
        });

        // LISTA BALLERINI
        VerticalLayout contenitoreBallerini = new VerticalLayout();

        Span titoloLista = new Span("Lista ballerini");
        titoloLista.addClassName("text3");
        layoutBallerini.setPadding(false);
        layoutBallerini.setSpacing(false);
        layoutBallerini.setHeightFull();
        layoutBallerini.getStyle().set("overflow-y", "auto");

        contenitoreBallerini.add(titoloLista, layoutBallerini);
        contenitoreBallerini.addClassName("ballerini-box");

        // PALCO
        nomeFormazione.addClassName("text2");
        palco.addClassName("palco");
        btnPrecedente.addClassName("scroll-button");
        btnSuccessiva.addClassName("scroll-button");
        VerticalLayout palcoConNavigazione = new VerticalLayout(nomeFormazione, palco, new HorizontalLayout(btnPrecedente, btnSuccessiva));
        palcoConNavigazione.setAlignItems(Alignment.CENTER);

        btnPrecedente.addClickListener(e -> cambiaFormazione(-1));
        btnSuccessiva.addClickListener(e -> cambiaFormazione(1));

        VerticalLayout decor = new VerticalLayout(noteSpan, minutiSpan);
        decor.setAlignItems(Alignment.CENTER);
        decor.setDefaultHorizontalComponentAlignment(Alignment.START);
        decor.addClassName("note-minutaggio");

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.add(contenitoreBallerini, palcoConNavigazione, decor);
        mainLayout.setWidthFull();
        mainLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        mainLayout.setSpacing(true);

        add(topBar, mainLayout);
    }

    private void setupBallerini() {
        layoutBallerini.removeAll();
        for (Ballerino b : coreografia.getBallerini()) {
            Span nome = new Span(b.getNome());
            nome.getStyle()
                    .set("margin-left", "10px");
            layoutBallerini.add(nome);
        }
    }

    /**
     * Mostra la formazione corrente usando i dati già “preparati” dal DTO
     * La View si limita a renderizzare.
     */
    private void mostraFormazioneCorrente() {
        if (coreografia.getFormazioni().isEmpty()) return;

        try {
            FormazioneViewData data =
                    coreografiaController.getFormazioneViewData(coreografia.getId(), formazioneCorrente);

            nomeFormazione.setText("Formazione: " + data.getNomeFormazione());

            // Reset palco e disegno dei punti
            palco.removeAll();
            for (BallerinoPoint punto : data.getPunti()) {
                Div pallino = new Div();
                pallino.getStyle()
                        .set("position", "absolute")
                        .set("left", punto.getX() + "px")
                        .set("top", punto.getY() + "px")
                        .set("width", "15px")
                        .set("height", "15px")
                        .set("background-color", "#f8139c")
                        .set("border-radius", "50%")
                        .set("transform", "translate(-50%, -50%)");

                Span nome = new Span(punto.getNome());
                nome.getStyle()
                        .set("position", "absolute")
                        .set("left", punto.getX() + "px")
                        .set("top", (punto.getY() + 15) + "px")
                        .set("transform", "translate(-50%, 0)");

                palco.add(pallino, nome);
            }

            // Note e minutaggio
            noteSpan.setText(data.getNota() != null && !data.getNota().isBlank()
                    ? "📝 Note: " + data.getNota()
                    : "");
            minutiSpan.setText(data.getMinutaggio() != null
                    ? "⏱ Minutaggio: " + data.getMinutaggio() + " min"
                    : "");

            // Navigazione
            btnPrecedente.setEnabled(formazioneCorrente > 0);
            btnSuccessiva.setEnabled(formazioneCorrente < coreografia.getFormazioni().size() - 1);

        } catch (IOException ex) {
            Notification.show("❌ Errore durante il caricamento della formazione");
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            Notification.show("⚠️ " + ex.getMessage());
        }
    }

    private void cambiaFormazione(int delta) {
        int nuovoIndice = formazioneCorrente + delta;
        if (nuovoIndice >= 0 && nuovoIndice < coreografia.getFormazioni().size()) {
            formazioneCorrente = nuovoIndice;
            mostraFormazioneCorrente();
        }
    }

    private void apriDialogCreaFormazione() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        TextField nomeFormazioneField = new TextField("Nome formazione");
        nomeFormazioneField.setWidthFull();
        Span messageLabel = new Span();
        Button creaBtn = new Button("Crea", e -> {
            String nome = nomeFormazioneField.getValue().trim();
            if (nome.isEmpty()) {
                messageLabel.setText("Il nome non può essere vuoto");
                return;
            }
            try {
                coreografiaController.creaFormazioneVuota(coreografia.getId(), nome);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            dialog.close();
            try {
                this.coreografia = coreografiaController.getCoreografiaById(coreografia.getId()); // reload
                mostraFormazioneCorrente();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        creaBtn.addClassName("secondary-button");

        Button annullaBtn = new Button("Annulla", e -> {
            dialog.close();
            UI.getCurrent().navigate("home");
        });
        annullaBtn.addClassName("secondary-button");

        HorizontalLayout buttons = new HorizontalLayout(creaBtn, annullaBtn);
        dialog.add(nomeFormazioneField, messageLabel, buttons);
        dialog.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String idString = event.getRouteParameters().get("id").orElse(null);
        if (idString == null) {
            titoloCoreografia.setText("⚠️ ID non fornito");
            return;
        }

        try {
            Long id = Long.parseLong(idString);
            this.coreografia = coreografiaController.getCoreografiaById(id);

            if (coreografia == null) {
                titoloCoreografia.setText("⚠️ Coreografia non trovata");
                return;
            }

            // Avviso se qualcuno sta modificando (bridge nel controller)
             if (coreografiaController.isBeingEdited(coreografia.getId())) {
                Notification.show("⚠️ Attenzione: la coreografia è in fase di modifica da un altro utente." +
                        " I dati potrebbero non essere aggiornati.");
            }

            titoloCoreografia.setText(coreografia.getNome());
            setupBallerini();

            if (coreografia.getFormazioni().isEmpty()) {
                apriDialogCreaFormazione();
            } else {
                mostraFormazioneCorrente();
            }

        } catch (NumberFormatException | IOException e) {
            titoloCoreografia.setText("❌ Errore nel caricamento");
            e.printStackTrace();
        }
    }

    // getter per test
    public Span getTitoloCoreografiaSpan() { return titoloCoreografia; }
    public Span getNomeFormazioneSpan() { return nomeFormazione; }
    public Span getNoteSpan() { return noteSpan; }
    public Span getMinutiSpan() { return minutiSpan; }
    public Div  getPalcoDiv() { return palco; }
    public VerticalLayout getLayoutBallerini() { return layoutBallerini; }

    // Azioni per cambiare formazione senza dover cliccare i bottoni - per test
    public void nextFormation() { cambiaFormazione(1); }
    public void prevFormation() { cambiaFormazione(-1); }

}
