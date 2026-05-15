package com.blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testNumberCardValue() {
        Card five = new Card(Card.Suit.HEARTS, Card.Rank.FIVE);
        assertEquals(5, five.getValue());
    }

    @Test
    void testFaceCardValue() {
        Card king = new Card(Card.Suit.SPADES, Card.Rank.KING);
        assertEquals(10, king.getValue());

        Card queen = new Card(Card.Suit.DIAMONDS, Card.Rank.QUEEN);
        assertEquals(10, queen.getValue());

        Card jack = new Card(Card.Suit.HEARTS, Card.Rank.JACK);
        assertEquals(10, jack.getValue());
    }

    @Test
    void testAceValue() {
        Card ace = new Card(Card.Suit.HEARTS, Card.Rank.ACE);
        assertEquals(11, ace.getValue());
        assertTrue(ace.isAce());
    }

    @Test
    void testIsNotAce() {
        Card ten = new Card(Card.Suit.HEARTS, Card.Rank.TEN);
        assertFalse(ten.isAce());
    }

    @Test
    void testToString() {
        Card card = new Card(Card.Suit.HEARTS, Card.Rank.KING);
        String str = card.toString();
        assertTrue(str.contains("King"));
        assertTrue(str.contains("Hearts"));
    }

    @Test
    void testGetSuit() {
        Card card = new Card(Card.Suit.DIAMONDS, Card.Rank.FIVE);
        assertEquals(Card.Suit.DIAMONDS, card.getSuit());
    }

    @Test
    void testGetRank() {
        Card card = new Card(Card.Suit.HEARTS, Card.Rank.THREE);
        assertEquals(Card.Rank.THREE, card.getRank());
    }
}
