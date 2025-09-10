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
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;

@Route("modifica-coreografia/:id")
@PageTitle("Modifica coreografia")
@CssImport("./styles/coreografia.css")

public class ModificaCoreografiaView extends ProtectedView implements BeforeEnterObserver {
    private final CoreografiaController coreografiaController;
    private Coreografia coreografia;
    private int formazioneCorrente = 0;

    private final Span titoloCoreografia = new Span();
    private final Span nomeFormazione = new Span("Formazione:");
    private final VerticalLayout layoutBallerini = new VerticalLayout();
    private final Div palco = new Div();
    private final Span noteSpan = new Span();
    private final Span minutiSpan = new Span();
    private final Button btnPrecedente = new Button("←");
    private final Button btnSuccessiva = new Button("→");

    private Ballerino ballerinoSelezionato = null;
    private Span spanSelezionato = null;

    @Autowired
    public ModificaCoreografiaView(CoreografiaController coreografiaController) {
        this.coreografiaController = coreografiaController;

        addClassName("coreo-background");

        // TOP BAR
        Button btnVisualizza = new Button("Visualizza");
        Button btnBack = new Button("Torna alla home");
        btnVisualizza.addClassName("top-button");
        btnBack.addClassName("top-button");

        btnBack.addClickListener(e -> {salvaModifiche(); UI.getCurrent().navigate("home");});
        btnVisualizza.addClickListener(e -> {salvaModifiche();
            if (coreografia != null) {
                UI.getCurrent().navigate("coreografia/" + coreografia.getId());
            }
        });

        Span spacer = new Span();
        spacer.getStyle().set("flex-grow", "1");

        titoloCoreografia.addClassName("text1");
        Span modalita = new Span(" - modalità modifica -");
        modalita.addClassName("mod-text");

        HorizontalLayout topBar = new HorizontalLayout(titoloCoreografia,modalita, spacer, btnVisualizza, btnBack);
        topBar.setWidthFull();
        topBar.setPadding(true);
        topBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);


        // LISTA BALLERINI
        Span titoloLista = new Span("Lista ballerini");
        titoloLista.addClassName("text3");
        layoutBallerini.setPadding(false);
        layoutBallerini.setSpacing(false);
        layoutBallerini.setHeightFull();
        layoutBallerini.getStyle().set("overflow-y", "auto");
        VerticalLayout listaContainer = new VerticalLayout(titoloLista, layoutBallerini);
        listaContainer.addClassName("ballerini-box");

        //PALCO
        nomeFormazione.addClassName("text2");
        palco.addClassName("palco");
        palco.getElement().addEventListener("click", e -> {
            if (ballerinoSelezionato == null) {
                Notification.show("Seleziona un ballerino");
                return;
            }

            int x = (int) e.getEventData().getNumber("event.offsetX");
            int y = (int) e.getEventData().getNumber("event.offsetY");
            try {
                coreografiaController.assignPosizione(coreografia.getId(), formazioneCorrente, ballerinoSelezionato.getId(), x, y);
                // ricarica dati e ridisegna
                coreografia = coreografiaController.getCoreografiaById(coreografia.getId());
                mostraFormazioneCorrente();
            } catch (IOException ex) {
                Notification.show("Errore nel salvataggio posizione");
            }
        }).addEventData("event.offsetX").addEventData("event.offsetY");

        btnPrecedente.addClassName("scroll-button");
        btnSuccessiva.addClassName("scroll-button");
        VerticalLayout palcoConNavigazione = new VerticalLayout(nomeFormazione, palco, new HorizontalLayout(btnPrecedente, btnSuccessiva));
        palcoConNavigazione.setAlignItems(Alignment.CENTER);

        btnPrecedente.addClickListener(e -> cambiaFormazione(-1));
        btnSuccessiva.addClickListener(e -> cambiaFormazione(1));

        //Controlli formazione
        Button btnAggiungiFormazione = new Button("Aggiungi formazione", e -> apriDialogCreaFormazione());
        Button btnEliminaFormazione = new Button("Elimina formazione", e -> {
            try {
                coreografiaController.removeFormazione(coreografia.getId(), formazioneCorrente);
                coreografia = coreografiaController.getCoreografiaById(coreografia.getId());
                formazioneCorrente = Math.max(0, Math.min(formazioneCorrente, coreografia.getFormazioni().size() - 1));
               // caso eliminazione unica formazione
                if (coreografia.getFormazioni().isEmpty()) {
                    apriDialogCreaFormazione();
                } else {
                    mostraFormazioneCorrente();
                }
            } catch (IOException ex) {
                Notification.show("Errore nel salvataggio");
            } catch (IllegalArgumentException ex) {
                Notification.show("⚠️ " + ex.getMessage());
            }
        });

        Button btnNota = new Button("Aggiungi nota", e -> apriDialogNota());
        Button btnMinuti = new Button("Aggiungi minutaggio", e -> apriDialogMinutaggio());
        btnAggiungiFormazione.addClassName("modify-button");
        btnEliminaFormazione.addClassName("modify-button");
        btnNota.addClassName("modify-button");
        btnMinuti.addClassName("modify-button");

        VerticalLayout decor = new VerticalLayout(noteSpan, minutiSpan);
        decor.addClassName("note-minutaggio");

        VerticalLayout destra = new VerticalLayout(btnAggiungiFormazione, btnEliminaFormazione, btnNota, btnMinuti,decor);
        destra.setAlignItems(Alignment.START);
        destra.setDefaultHorizontalComponentAlignment(Alignment.START);
        String s="400px";
        destra.setMaxWidth(s);

        HorizontalLayout centro = new HorizontalLayout(listaContainer, palcoConNavigazione, destra);
        centro.setWidthFull();
        centro.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        centro.setSpacing(true);


        add(topBar, centro);
    }

    protected void salvaModifiche(){
        try {
            Long autoreId = (Long) UI.getCurrent().getSession().getAttribute("coreografoId");
            coreografiaController.saveAndNotifyModified(coreografia,autoreId);
            Notification.show("Modifiche salvate");
            String sessionId = UI.getCurrent().getSession().getSession().getId();
            coreografiaController.releaseEditLock(coreografia.getId(), sessionId);
        } catch (IOException ex) {
            Notification.show("Errore nel salvataggio");
        }
    }

    protected void setupBallerini() {
        layoutBallerini.removeAll();
        for (Ballerino b : coreografia.getBallerini()) {
            Span nome = new Span(b.getNome());
            nome.getStyle().set("margin-left", "10px").set("cursor", "pointer");
            nome.addClickListener(e -> {
                // Ripristina stile precedente
                if (spanSelezionato != null) {
                    spanSelezionato.getStyle().remove("font-weight").remove("text-decoration");
                }
                // Applica stile al selezionato
                nome.getStyle().set("font-weight", "bold").set("text-decoration", "underline");
                spanSelezionato = nome;
                ballerinoSelezionato = b;
                Notification.show("Selezionato: " + b.getNome());
            });
            layoutBallerini.add(nome);
        }
    }

    private void mostraFormazioneCorrente() {
        if (coreografia.getFormazioni().isEmpty()) return;
        try {
            FormazioneViewData data = coreografiaController.getFormazioneViewData(coreografia.getId(), formazioneCorrente);

            nomeFormazione.setText("Formazione: " + data.getNomeFormazione());
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

            noteSpan.setText(data.getNota() != null && !data.getNota().isBlank() ? "📝 Nota: " + data.getNota() : "");
            minutiSpan.setText(data.getMinutaggio() != null && !data.getMinutaggio().isBlank() ? "⏱ Minutaggio: " + data.getMinutaggio() : "");

            btnPrecedente.setEnabled(formazioneCorrente > 0);
            btnSuccessiva.setEnabled(formazioneCorrente < coreografia.getFormazioni().size() - 1);

        } catch (IOException ex) {
            Notification.show("Errore caricamento formazione");
        } catch (IllegalArgumentException ex) {
            Notification.show("⚠️ " + ex.getMessage());
        }
    }

    private void cambiaFormazione(int delta) {
        int nuovo = formazioneCorrente + delta;
        if (nuovo >= 0 && nuovo < coreografia.getFormazioni().size()) {
            formazioneCorrente = nuovo;
            mostraFormazioneCorrente();
        }
    }

    protected void apriDialogCreaFormazione() {
        Dialog dialog = new Dialog();
        TextField nomeF = new TextField("Nome formazione");
        nomeF.setWidthFull();
        Span messageLabel = new Span();
        Button crea = new Button("Crea", e -> {
            String nome = nomeF.getValue().trim();
            if (nome.isEmpty()) {
                messageLabel.setText("Il nome non può essere vuoto");
                return;
            }
            try {
                coreografiaController.creaFormazioneVuota(coreografia.getId(), nome);
                coreografia = coreografiaController.getCoreografiaById(coreografia.getId());
                mostraFormazioneCorrente();
                dialog.close();
            } catch (IOException ex) {
                Notification.show("Errore");
            }
        });
        crea.addClassName("secondary-button");
        Button annullaBtn = new Button("Annulla", e -> dialog.close());
        annullaBtn.addClassName("secondary-button");

        dialog.add(nomeF, messageLabel, new HorizontalLayout(crea, annullaBtn));
        dialog.open();
    }

    protected void apriDialogNota() {
        Dialog dialog = new Dialog();
        TextField notaField = new TextField("Nota");
        notaField.setWidthFull();

        // Prefill dalla DTO (se già presente nota)
        try {
            FormazioneViewData data = coreografiaController.getFormazioneViewData(coreografia.getId(), formazioneCorrente);
            if (data.getNota() != null) notaField.setValue(data.getNota());
        } catch (Exception ignored) {}

        Button salva = new Button("Salva", e -> {
            try {
                coreografiaController.upsertNota(coreografia.getId(), formazioneCorrente, notaField.getValue());
                coreografia = coreografiaController.getCoreografiaById(coreografia.getId());
                mostraFormazioneCorrente();
                dialog.close();
            } catch (IOException ex) {
                Notification.show("Errore salvataggio nota");
            }
        });
        salva.addClassName("secondary-button");

        Button annulla = new Button("Annulla", e -> dialog.close());
        annulla.addClassName("secondary-button");

        dialog.add(notaField, new HorizontalLayout(salva, annulla));
        dialog.open();
    }

    protected void apriDialogMinutaggio() {
        Dialog dialog = new Dialog();
        TextField minutiField = new TextField("Minutaggio (min)");
        minutiField.setWidthFull();

        // Prefill dalla DTO (se già presente minutaggio)
        try {
            FormazioneViewData data = coreografiaController.getFormazioneViewData(coreografia.getId(), formazioneCorrente);
            if (data.getMinutaggio() != null) minutiField.setValue(data.getMinutaggio());
        } catch (Exception ignored) {}

        Button salva = new Button("Salva", e -> {
            try {
                coreografiaController.upsertMinutaggio(coreografia.getId(), formazioneCorrente, minutiField.getValue());
                coreografia = coreografiaController.getCoreografiaById(coreografia.getId());
                mostraFormazioneCorrente();
                dialog.close();
            } catch (IOException ex) {
                Notification.show("Errore salvataggio minutaggio");
            }
        });
        salva.addClassName("secondary-button");

        Button annulla = new Button("Annulla", e -> dialog.close());
        annulla.addClassName("secondary-button");

        dialog.add(minutiField, new HorizontalLayout(salva, annulla));
        dialog.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String idString = event.getRouteParameters().get("id").orElse(null);
        if (idString == null) return;
        try {
            Long id = Long.parseLong(idString);
            String sessionId = UI.getCurrent().getSession().getSession().getId();

            // Verifica e acquisizione lock tramite access manager
            if (!coreografiaController.tryAcquireEditLock(id, sessionId)) {
                Notification.show("⚠️ IMPOSSIBILE MODIFICARE! Coreografia già in modifica da un altro utente");
                event.forwardTo("home");
                return;
            }
            // Rilascio lock alla chiusura della sessione o navigazione
            UI.getCurrent().addDetachListener(e -> coreografiaController.releaseEditLock(id, sessionId));

            coreografia = coreografiaController.getCoreografiaById(id);
            if (coreografia != null) {
                titoloCoreografia.setText(coreografia.getNome());
                setupBallerini();
                mostraFormazioneCorrente();
            }

        } catch (IOException e) {
            Notification.show("Errore caricamento coreografia");
        }
    }

    // getter per test
    public Span getTitoloCoreografiaSpan() { return titoloCoreografia; }
    public Span getNomeFormazioneSpan() { return nomeFormazione; }
    public Span getNoteSpan() { return noteSpan; }
    public Span getMinutiSpan() { return minutiSpan; }
    public Div  getPalcoDiv() { return palco; }
    public VerticalLayout getLayoutBallerini() {
        return layoutBallerini;
    }

    public void setCoreografiaForTest(Coreografia c) { this.coreografia = c; }

    // Azioni per cambiare formazione senza dover cliccare i bottoni - per test
    public void nextFormation() { cambiaFormazione(1); }
    public void prevFormation() { cambiaFormazione(-1); }
}