package com.example.danceForm.Controller;

import com.example.danceForm.Model.Coreografo;
import com.example.danceForm.Service.CoreografoService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class AuthController {

    private final CoreografoService coreografoService;

    public AuthController(CoreografoService coreografoService) {
        this.coreografoService = coreografoService;
    }

    //Signin: Registra un nuovo coreografo nel sistema
    public void registrazione(Coreografo coreografo) throws IOException {
        coreografoService.saveCoreografo(coreografo);
    }

    //Login: verifica se esiste un coreografo con username e password
    public Coreografo login(String username, String password) throws IOException {
        List<Coreografo> utenti = coreografoService.getAllCoreografi();
        return utenti.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    //Logout: gestito lato Vaadin rimuovendo eventuali sessioni o view,
}
