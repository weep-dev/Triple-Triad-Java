package com.tiduswr.view;

import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import com.tiduswr.model.Player;

public class EndGameDialog {
    /*
     * Interface gráfica do jogo
     */
    private TripleTriadUI frame = null;

    private Player p1 = null;
    private Player p2 = null;

    public EndGameDialog(TripleTriadUI frame) {
        this.frame = frame;
        p1 = frame.getP1().getPlayer();
        p2 = frame.getP2().getPlayer();
    }

    public void show() {
        if (p1.getPoints() == p2.getPoints()) {
            int option = showDraw();

            // Reinicia tabuleiro e devolve as cartas aos jogadores
            if (option == 0) {
                frame.resetGame();
            } else {
                // Encerra jogo
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        } else {
            int option = showWinner();

            if (option == 1) {
                // Encerra jogo
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            } else {
                frame.resetGame();
            }
        }
    }

    private int showWinner() {
        Player winner = p1;
        String scoreLabel = " (" + p1.getPoints() + "x" + p2.getPoints() + ")";

        if (p2.getPoints() > p1.getPoints()) {
            winner = p2;
            scoreLabel = " (" + p2.getPoints() + "x" + p1.getPoints() + ")";
        }

        Object[] options = {"Novo jogo", "Encerrar"};
        return JOptionPane.showOptionDialog(
            frame, 
             "Parabens, " + winner.getName() + "!" + " Você venceu" + scoreLabel + "!", "Jogo encerrado!",
             JOptionPane.YES_NO_OPTION,
             JOptionPane.PLAIN_MESSAGE,
             null,
             options,
             options[0]
        );
    }

    private int showDraw() {
        Object[] options = {"Reiniciar", "Encerrar jogo"};
        return JOptionPane.showOptionDialog(
            frame,
            "O jogo empatou!\n Deseja reiniciar o tabuleiro?", "Empate!",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );
    }
}
