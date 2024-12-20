// Generated by delombok at Sat Oct 12 00:30:19 BRT 2024
package com.tiduswr.view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.tiduswr.model.CardData;
import com.tiduswr.model.Player;
import com.tiduswr.model.PlayerCardData;

/**
 * Componente que representa as cartas de um jogador em uma interface de jogo.
 * Permite a seleção e a interação com as cartas na mão do jogador.
 */
public class PlayerCards extends JPanel {
    /**
     * Jogador associado a estas cartas
     */
    private Player player;
    /**
     * Número de cartas na mão
     */
    private int handSize = 5;
    /**
     * Indica se as cartas estão ativas para seleção
     */
    private boolean cardsActive = true;
    /**
     * Índice da carta selecionada
     */
    private int selectedIndex;

    /**
     * Construtor da classe PlayerCards.
     * Inicializa o painel com as cartas do jogador e configura os ouvintes de
     * eventos.
     *
     * @param father A interface do jogo que contém este componente.
     * @param player O jogador associado a estas cartas.
     * @param width  Largura preferida do painel.
     * @param height Altura preferida do painel.
     */
    public PlayerCards(TripleTriadUI father, Player player, int width, int height) {
        this.player = player;
        selectedIndex = -1;
        setLayout(new GridLayout(5, 1)); // Layout em grade para as cartas
        setPreferredSize(new Dimension(width, height)); // Define o tamanho preferido
        List<CardData> cards = player.getCards();
        for (int i = 0; i < 5; i++) {
            var index = i;
            var cardData = cards.get(index);
            var playerCardData = new PlayerCardData(cardData, player, 0, false);
            var cardComponent = new CardComponent(playerCardData, e -> {
                if (!cardsActive || playerCardData.isFlipped())
                    return;
                father.getGameLog().addLogMessage(String.format("A carta \'%s\' foi selecionada!", cardData.getName()));
                father.getSoundServices().getSoundService("selection").play();
                selectedIndex = player.getCards().indexOf(cardData);
                updateBorders();
            });
            add(cardComponent);
        }
        setBorder(BorderFactory.createTitledBorder(getPlayer().getName()));
        // Listener para resetar seleção ao clicar fora das cartas
        father.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!contains(e.getPoint()) && selectedIndex != -1 && cardsActive) {
                    selectedIndex = -1;
                    updateBorders();
                }
            }
        });
        // Exemplo de passar uma carta da mão para o campo
        father.getBoard().addPositionListener((row, col) -> {
            var selected = getSelected();
            if (selected != null) {
                if (!father.getBoard().addCard(selected, row, col)) {
                    father.getSoundServices().getSoundService("error").play();
                    father.getGameLog()
                            .addLogMessage(String.format("A posição [%d, %d] já possui uma carta!", row, col));
                } else {
                    father.getSoundServices().getSoundService("card-placed").play();
                    removeSelected();
                    
                    father.addTurn();
                    father.switchTurn();
                }
                rules(father, row, col, selected);

                if (father.getBoard().isFull()) {
                    father.showEndGameDialog();
                }
            }
        });
    }

    /**
     * Define se as cartas estão ativas para seleção.
     *
     * @param enabled True para ativar a seleção de cartas, false para desativar.
     */
    public void setCardsActive(boolean enabled) {
        this.cardsActive = enabled;
    }

    /**
     * Atualiza as bordas das cartas com base na seleção atual.
     */
    private void updateBorders() {
        for (int i = 0; i < handSize; i++) {
            var cardComponent = (CardComponent) getComponent(i);
            cardComponent.setCardIsSelected(i == selectedIndex); // Destaca a carta selecionada
        }
        revalidate();
        repaint();
    }

    /**
     * Obtém a carta atualmente selecionada.
     *
     * @return A carta selecionada, ou null se nenhuma carta estiver selecionada.
     */
    public PlayerCardData getSelected() {
        if (selectedIndex != -1) {
            return ((CardComponent) getComponent(selectedIndex)).getInfo();
        }
        return null;
    }

    /**
     * Remove a carta atualmente selecionada da mão do jogador.
     *
     * @return True se uma carta foi removida, false caso contrário.
     */
    public boolean removeSelected() {
        var selected = getSelected();
        if (selected == null)
            return false;
        CardComponent selectedCard = (CardComponent) getComponent(selectedIndex);
        player.getCards().removeIf(card -> card.equals(selectedCard.getInfo().getCardData()));
        selectedIndex = -1;
        handSize--;
        remove(selectedCard);
        revalidate();
        repaint();
        return true;
    }

    /**
     * Obtém uma lista de todos os dados das cartas do jogador.
     *
     * @return Uma lista de objetos PlayerCardData.
     */
    private List<PlayerCardData> getAllPlayerCardData() {
        List<PlayerCardData> cardDataList = new ArrayList<>();
        for (int i = 0; i < getComponentCount(); i++) {
            CardComponent cardComponent = (CardComponent) getComponent(i);
            cardDataList.add(cardComponent.getInfo());
        }
        return cardDataList;
    }

    /**
     * Processa todos os dados das cartas do jogador, aplicando uma ação a cada um.
     *
     * @param action A ação a ser aplicada, representada como um BiConsumer que
     *               recebe o índice e os dados da carta.
     */
    public void processAllPlayerCardData(BiConsumer<Integer, PlayerCardData> action) {
        var cards = getAllPlayerCardData();
        for (int i = 0; i < cards.size(); i++) {
            action.accept(i, cards.get(i));
        }
        revalidate();
        repaint();
    }

    /**
     * Obtém o jogador associado a estas cartas.
     *
     * @return O jogador que possui estas cartas.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Obtém o número de cartas na mão do jogador.
     *
     * @return O número de cartas na mão.
     */
    public int getHandSize() {
        return this.handSize;
    }

    /**
     * Verifica se as cartas estão ativas para seleção.
     *
     * @return True se as cartas estiverem ativas para seleção, caso contrário,
     *         false.
     */
    public boolean isCardsActive() {
        return this.cardsActive;
    }

    /**
     * Obtém o índice da carta atualmente selecionada.
     *
     * @return O índice da carta selecionada.
     */
    public int getSelectedIndex() {
        return this.selectedIndex;
    }
    
    /**
     * Função que define como as regras vão funcionar.
     * @param father A interface do jogo que contem o componente.
     * @param row   Linha em que a carta foi jogada.
     * @param col   Coluna em que a carta foi jogada.
     * @param card  Carta que foi jogada.
     */
    public void rules(TripleTriadUI father, int row, int col, PlayerCardData card) {
    
        int sumRg = 0;
        int sumLf = 0;
        int sumUp = 0;
        int sumDw = 0;
    
        
        if (col + 1 < father.getBoard().getBoardCards()[row].length 
            && father.getBoard().getBoardCards()[row][col + 1] != null 
            && father.getBoard().getBoardCards()[row][col + 1].getInfo() != null) {
            sumRg = card.getCardData().getRight() + father.getBoard().getBoardCards()[row][col + 1].getInfo().getCardData().getLeft();
        }
    
        
        if (col - 1 >= 0 
            && father.getBoard().getBoardCards()[row][col - 1] != null 
            && father.getBoard().getBoardCards()[row][col - 1].getInfo() != null) {
            sumLf = card.getCardData().getLeft() + father.getBoard().getBoardCards()[row][col - 1].getInfo().getCardData().getRight();
        }
    
        
        if (row - 1 >= 0 
            && father.getBoard().getBoardCards()[row - 1][col] != null 
            && father.getBoard().getBoardCards()[row - 1][col].getInfo() != null) {
            sumUp = card.getCardData().getUp() + father.getBoard().getBoardCards()[row - 1][col].getInfo().getCardData().getDown();
        }
    
        
        if (row + 1 < father.getBoard().getBoardCards().length 
            && father.getBoard().getBoardCards()[row + 1][col] != null 
            && father.getBoard().getBoardCards()[row + 1][col].getInfo() != null) {
            sumDw = card.getCardData().getDown() + father.getBoard().getBoardCards()[row + 1][col].getInfo().getCardData().getUp();
        }
    
        
        if (sumRg == sumUp && sumRg != 0 && sumUp != 0) {
            if (father.getBoard().getBoardCards()[row - 1][col].getInfo() != null && father.getBoard().getBoardCards()[row - 1][col].getInfo().getOwner() != player) {
                father.getBoard().getBoardCards()[row - 1][col].getInfo().setOwner(player);
            }    
            if (father.getBoard().getBoardCards()[row][col + 1].getInfo() != null && father.getBoard().getBoardCards()[row][col + 1].getInfo().getOwner() != player) {
                father.getBoard().getBoardCards()[row][col + 1].getInfo().setOwner(player);
            }
        }
        if (sumLf == sumDw && sumLf != 0 && sumDw != 0) {
            if (father.getBoard().getBoardCards()[row][col - 1].getInfo() != null && father.getBoard().getBoardCards()[row][col - 1].getInfo().getOwner() != player) {
                father.getBoard().getBoardCards()[row][col - 1].getInfo().setOwner(player);
            }
            if (father.getBoard().getBoardCards()[row + 1][col].getInfo() != null && father.getBoard().getBoardCards()[row + 1][col].getInfo().getOwner() != player) {
                father.getBoard().getBoardCards()[row + 1][col].getInfo().setOwner(player);
            }
        }
    
        
        if (col + 1 < father.getBoard().getBoardCards()[row].length 
            && father.getBoard().getBoardCards()[row][col + 1] != null 
            && father.getBoard().getBoardCards()[row][col + 1].getInfo() != null
            && card.getCardData().getRight() > father.getBoard().getBoardCards()[row][col + 1].getInfo().getCardData().getLeft() 
            && player != father.getBoard().getBoardCards()[row][col + 1].getInfo().getOwner()) {
    
            father.getBoard().getBoardCards()[row][col + 1].getInfo().setOwner(player);
            attScores(father, player);
        }
    
        if (col - 1 >= 0 
            && father.getBoard().getBoardCards()[row][col - 1] != null 
            && father.getBoard().getBoardCards()[row][col - 1].getInfo() != null
            && card.getCardData().getLeft() > father.getBoard().getBoardCards()[row][col - 1].getInfo().getCardData().getRight() 
            && player != father.getBoard().getBoardCards()[row][col - 1].getInfo().getOwner()) {
    
            father.getBoard().getBoardCards()[row][col - 1].getInfo().setOwner(player);
            attScores(father, player);
        }
    
        if (row - 1 >= 0 
            && father.getBoard().getBoardCards()[row - 1][col] != null 
            && father.getBoard().getBoardCards()[row - 1][col].getInfo() != null
            && card.getCardData().getUp() > father.getBoard().getBoardCards()[row - 1][col].getInfo().getCardData().getDown() 
            && player != father.getBoard().getBoardCards()[row - 1][col].getInfo().getOwner()) {
    
            father.getBoard().getBoardCards()[row - 1][col].getInfo().setOwner(player);
            attScores(father, player);
        }
    
        if (row + 1 < father.getBoard().getBoardCards().length 
            && father.getBoard().getBoardCards()[row + 1][col] != null 
            && father.getBoard().getBoardCards()[row + 1][col].getInfo() != null
            && card.getCardData().getDown() > father.getBoard().getBoardCards()[row + 1][col].getInfo().getCardData().getUp() 
            && player != father.getBoard().getBoardCards()[row + 1][col].getInfo().getOwner()) {
    
            father.getBoard().getBoardCards()[row + 1][col].getInfo().setOwner(player);
            attScores(father, player);
        }
    }

    private void attScores(TripleTriadUI father, Player player) {
        if (player == father.getP1().getPlayer()) {
            player.addPoints();
            father.getP2().getPlayer().dcrPoints();
        } else {
            player.addPoints();
            father.getP1().getPlayer().dcrPoints();
        }
        father.getScore().updateScores();
    }
}
