package com.blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void testDeckHas52Cards() {
        Deck deck = new Deck();
        assertEquals(52, deck.size());
    }

    @Test
    void testDealReducesDeckSize() {
        Deck deck = new Deck();
        deck.deal();
        assertEquals(51, deck.size());
    }

    @Test
    void testDealAllCards() {
        Deck deck = new Deck();
        for (int i = 0; i < 52; i++) {
            assertNotNull(deck.deal());
        }
        assertTrue(deck.isEmpty());
    }

    @Test
    void testDealFromEmptyDeckThrows() {
        Deck deck = new Deck();
        for (int i = 0; i < 52; i++) {
            deck.deal();
        }
        assertThrows(IllegalStateException.class, deck::deal);
    }

    @Test
    void testResetRestoresDeck() {
        Deck deck = new Deck();
        for (int i = 0; i < 52; i++) {
            deck.deal();
        }
        assertTrue(deck.isEmpty());
        deck.reset();
        assertEquals(52, deck.size());
    }

    @Test
    void testShuffleDoesNotChangeDeckSize() {
        Deck deck = new Deck();
        deck.shuffle();
        assertEquals(52, deck.size());
    }

    @Test
    void testIsEmptyOnNewDeck() {
        Deck deck = new Deck();
        assertFalse(deck.isEmpty());
    }

    @Test
    void testIsEmptyAfterAllDealt() {
        Deck deck = new Deck();
        for (int i = 0; i < 52; i++) {
            deck.deal();
        }
        assertTrue(deck.isEmpty());
    }
}