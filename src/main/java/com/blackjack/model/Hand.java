package com.blackjack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a player/dealer hand
 */
public class Hand {

    private final List<Card> cards;


    public Hand() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public int size() {
        return cards.size();
    }

    /**
     * Calculates the best hand value
     * @return best possible hand w/o busting
     */
    public int getValue() {
        int total = 0;
        int aceCount = 0;
        for (Card card : cards) {
            total += card.getValue();
            if(card.isAce()) {
                aceCount++;
            }
        }
        // Reduced ace from 11 to 1 as needed
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }
        return total;
    }

    public boolean isSoft() {
        int total = 0;
        int aceCount =0;

        for (Card card : cards) {
            total += card.getValue();
            if(card.isAce()) {
                aceCount++;
            }
        }

        while (total > 21 && aceCount > 0) {
            total -=10;
            aceCount--;
        }
        return aceCount > 0 && total <= 21;
    }

    public boolean isBust() {
        return getValue() > 21;
    }

    public boolean isBJ() {
        return cards.size() == 2 && getValue()==21;
    }

    public boolean canSplit() {
        return cards.size() == 2 && cards.get(0).getRank() == cards.get(1).getRank();
    }

    public boolean canDoubleDown() { return cards.size() == 2; }

    public void clear() {
        cards.clear();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < cards.size(); i++) {
            sb.append(cards.get(i));
            if (i < cards.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("] (Value: ").append(getValue()).append(")");
        return sb.toString();
    }
}
