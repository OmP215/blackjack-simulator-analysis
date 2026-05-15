package com.blackjack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a std 52 hand deck w/ shuffling
 */
public class Deck {
    private final List<Card> cards;

    public Deck() {
        cards = new ArrayList<>(52);
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Refills deck and shuffles
     * Call when deck is low
     */
    public void reset() {
        cards.clear();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        Collections.shuffle(cards);
    }

    public Card deal() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("deck is empty");
        }
        return cards.remove(cards.size() -1);
    }

    public int size() {
        return cards.size();
    }
    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
