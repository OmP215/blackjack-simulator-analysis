package com.blackjack.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HandTest {

    private Hand hand;

    @BeforeEach
    void setUp() {
        hand = new Hand();
    }

    @Test
    void testEmptyHandValue() {
        assertEquals(0, hand.getValue());
    }

    @Test
    void testSimpleHandValue() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.SEVEN));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.EIGHT));
        assertEquals(15, hand.getValue());
    }

    @Test
    void testAceCountedAs11WhenPossible() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.SEVEN));
        assertEquals(18, hand.getValue());
    }

    @Test
    void testAceCountedAs1ToAvoidBust() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.KING));
        hand.addCard(new Card(Card.Suit.CLUBS, Card.Rank.FIVE));
        // Ace + King + 5 = 16, ace counts as 1 to avoid bust
        assertEquals(16, hand.getValue());
    }

    @Test
    void testTwoAces() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.ACE));
        // 11 + 1 = 12
        assertEquals(12, hand.getValue());
    }

    @Test
    void testBlackjack() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.KING));
        assertTrue(hand.isBJ());
        assertEquals(21, hand.getValue());
    }

    @Test
    void testNotBlackjackWith21InThreeCards() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.SEVEN));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.SEVEN));
        hand.addCard(new Card(Card.Suit.CLUBS, Card.Rank.SEVEN));
        assertEquals(21, hand.getValue());
        assertFalse(hand.isBJ());
    }

    @Test
    void testBust() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.KING));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.QUEEN));
        hand.addCard(new Card(Card.Suit.CLUBS, Card.Rank.FIVE));
        assertTrue(hand.isBust());
    }

    @Test
    void testNotBust() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.TEN));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.NINE));
        assertFalse(hand.isBust());
    }

    @Test
    void testCanSplit() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.EIGHT));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.EIGHT));
        assertTrue(hand.canSplit());
    }

    @Test
    void testCannotSplitDifferentRanks() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.EIGHT));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.NINE));
        assertFalse(hand.canSplit());
    }

    @Test
    void testCannotSplitThreeCards() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.EIGHT));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.EIGHT));
        hand.addCard(new Card(Card.Suit.CLUBS, Card.Rank.EIGHT));
        assertFalse(hand.canSplit());
    }

    @Test
    void testCanDoubleDown() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.FIVE));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.SIX));
        assertTrue(hand.canDoubleDown());
    }

    @Test
    void testCannotDoubleDownAfterThirdCard() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.FIVE));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.SIX));
        hand.addCard(new Card(Card.Suit.CLUBS, Card.Rank.TWO));
        assertFalse(hand.canDoubleDown());
    }

    @Test
    void testSoftHand() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.SIX));
        assertTrue(hand.isSoft());
    }

    @Test
    void testHardHandAfterAceForcedLow() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.KING));
        hand.addCard(new Card(Card.Suit.CLUBS, Card.Rank.FIVE));
        // Ace must be 1 to avoid bust, so not soft
        assertFalse(hand.isSoft());
    }

    @Test
    void testClear() {
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.KING));
        hand.addCard(new Card(Card.Suit.SPADES, Card.Rank.ACE));
        hand.clear();
        assertEquals(0, hand.size());
        assertEquals(0, hand.getValue());
    }
}