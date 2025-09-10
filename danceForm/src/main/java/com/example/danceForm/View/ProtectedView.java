package com.example.danceForm.View;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public abstract class ProtectedView extends VerticalLayout implements BeforeEnterObserver {
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String user = (String) UI.getCurrent().getSession().getAttribute("username");
        if (user == null || user.isEmpty()) {
            event.forwardTo(LoginView.class);
        }
    }
}
