package org.diariocultural.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Controlador para a tela de boas-vindas (WelcomeScreen.fxml).
 */
// O nome da classe foi atualizado
public class WelcomeScreenController {

    private Runnable onAccessAction;

    public void setOnAccessAction(Runnable onAccessAction) {
        this.onAccessAction = onAccessAction;
    }

    @FXML
    private void handleAccessAction(ActionEvent event) {
        if (onAccessAction != null) {
            onAccessAction.run();
        }
    }
}