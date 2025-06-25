package org.diariocultural;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Classe responsável por interações básicas com o usuário via console.
 */
public class MediaView {
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Solicita e retorna uma entrada de texto do usuário.
     * @param prompt Mensagem exibida ao usuário.
     * @return Texto digitado.
     */
    public String getInput(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }

    /**
     * Solicita e retorna uma entrada numérica inteira do usuário.
     * @param prompt Mensagem exibida ao usuário.
     * @return Número inteiro digitado.
     */
    public int getIntInput(String prompt) {
        System.out.println(prompt);
        return scanner.nextInt();
    }

    /**
     * Exibe uma mensagem no console.
     * @param message Mensagem a ser exibida.
     */
    public void displayMessage(String message) {
        System.out.println(message);
    }

    /**
     * Retorna uma data fornecida pelo usuário (não implementado).
     * @param s Mensagem ou parâmetro para entrada.
     * @return Data (sempre null atualmente).
     */
    public Date getDateInput(String s) {
        return null;
    }

    /**
     * Retorna uma lista de strings fornecida pelo usuário (não implementado).
     * @param s Mensagem ou parâmetro para entrada.
     * @return Lista de strings (sempre null atualmente).
     */
    protected List<String> getListInput(String s) {
        return null;
    }

    /**
     * Retorna uma resposta de sim ou não do usuário (não implementado).
     * @param s Mensagem ou parâmetro para entrada.
     * @return false por padrão.
     */
    protected boolean getYesNoInput(String s) {
        return false;
    }
}
