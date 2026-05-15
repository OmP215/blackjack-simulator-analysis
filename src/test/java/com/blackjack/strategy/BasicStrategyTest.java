package com.blackjack.strategy;

import com.blackjack.model.Card;
import com.blackjack.model.Hand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BasicStrategyTest {

    private BasicStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new BasicStrategy();
    }

    // Hard totals

    @Test
    void testHardStandOn17OrMore() {
        Hand hand = makeHard(10, 7); // hard 17
        Card dealerUp = card(Card.Rank.SEVEN);
        assertEquals(BlackjackStrategy.Action.STAND, strategy.decide(hand, dealerUp));
    }

    @Test
    void testHardHitOn16VsStrongDealer() {
        Hand hand = makeHard(10, 6); // hard 16
        Card dealerUp = card(Card.Rank.SEVEN);
        assertEquals(BlackjackStrategy.Action.HIT, strategy.decide(hand, dealerUp));
    }

    @Test
    void testHardStandOn16VsWeakDealer() {
        Hand hand = makeHard(10, 6); // hard 16
        Card dealerUp = card(Card.Rank.SIX);
        assertEquals(BlackjackStrategy.Action.STAND, strategy.decide(hand, dealerUp));
    }

    @Test
    void testDoubleDownOnElevenVsAnyDealer() {
        Hand hand = makeHard(6, 5); // hard 11
        Card dealerUp = card(Card.Rank.NINE);
        assertEquals(BlackjackStrategy.Action.DOUBLE_DOWN, strategy.decide(hand, dealerUp));
    }

    @Test
    void testDoubleDownOnTenVsWeak() {
        Hand hand = makeHard(6, 4); // hard 10
        Card dealerUp = card(Card.Rank.SIX);
        assertEquals(BlackjackStrategy.Action.DOUBLE_DOWN, strategy.decide(hand, dealerUp));
    }

    @Test
    void testHitOnTenVsStrongDealer() {
        Hand hand = makeHard(6, 4); // hard 10
        Card dealerUp = card(Card.Rank.ACE); // dealer ace = 11
        assertEquals(BlackjackStrategy.Action.HIT, strategy.decide(hand, dealerUp));
    }

    // Soft totals

    @Test
    void testSoftStandOnSoft20() {
        Hand hand = makeSoft(9); // A+9 = soft 20
        Card dealerUp = card(Card.Rank.EIGHT);
        assertEquals(BlackjackStrategy.Action.STAND, strategy.decide(hand, dealerUp));
    }

    @Test
    void testSoftHitOnSoft18VsStrongDealer() {
        Hand hand = makeSoft(7); // A+7 = soft 18
        Card dealerUp = card(Card.Rank.NINE);
        assertEquals(BlackjackStrategy.Action.HIT, strategy.decide(hand, dealerUp));
    }

    @Test
    void testSoftDoubleOnSoft18VsWeak() {
        Hand hand = makeSoft(7); // A+7 = soft 18
        Card dealerUp = card(Card.Rank.FIVE);
        assertEquals(BlackjackStrategy.Action.DOUBLE_DOWN, strategy.decide(hand, dealerUp));
    }

    // Pairs / splits

    @Test
    void testAlwaysSplitAces() {
        Hand hand = makePair(Card.Rank.ACE);
        Card dealerUp = card(Card.Rank.EIGHT);
        assertEquals(BlackjackStrategy.Action.SPLIT, strategy.decide(hand, dealerUp));
    }

    @Test
    void testAlwaysSplitEights() {
        Hand hand = makePair(Card.Rank.EIGHT);
        Card dealerUp = card(Card.Rank.TEN);
        assertEquals(BlackjackStrategy.Action.SPLIT, strategy.decide(hand, dealerUp));
    }

    @Test
    void testNeverSplitTens() {
        Hand hand = makePair(Card.Rank.TEN);
        Card dealerUp = card(Card.Rank.SIX);
        assertEquals(BlackjackStrategy.Action.STAND, strategy.decide(hand, dealerUp));
    }

    @Test
    void testSplitNinesVsWeak() {
        Hand hand = makePair(Card.Rank.NINE);
        Card dealerUp = card(Card.Rank.SIX);
        assertEquals(BlackjackStrategy.Action.SPLIT, strategy.decide(hand, dealerUp));
    }

    @Test
    void testStandNinesVsDealer7() {
        Hand hand = makePair(Card.Rank.NINE);
        Card dealerUp = card(Card.Rank.SEVEN);
        assertEquals(BlackjackStrategy.Action.STAND, strategy.decide(hand, dealerUp));
    }

    // Bet size

    @Test
    void testBetSizeIsMinBet() {
        assertEquals(10.0, strategy.getBetSize(10.0, 500.0));
    }

    @Test
    void testToString() {
        assertNotNull(strategy.toString());
        assertFalse(strategy.toString().isEmpty());
    }

    // Helpers

    /** Creates a hard hand with exactly two number card values. */
    private Hand makeHard(int v1, int v2) {
        Hand hand = new Hand();
        hand.addCard(numberCard(v1));
        hand.addCard(numberCard(v2));
        return hand;
    }

    /** Creates a soft hand: Ace + a number card. */
    private Hand makeSoft(int numValue) {
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        hand.addCard(numberCard(numValue));
        return hand;
    }

    /** Creates a pair hand. */
    private Hand makePair(Card.Rank rank) {
        Hand hand = new Hand();
        hand.addCard(new Card(Card.Suit.HEARTS, rank));
        hand.addCard(new Card(Card.Suit.SPADES, rank));
        return hand;
    }

    private Card numberCard(int value) {
        for (Card.Rank r : Card.Rank.values()) {
            if (r.getValue() == value && r != Card.Rank.ACE) {
                return new Card(Card.Suit.CLUBS, r);
            }
        }
        throw new IllegalArgumentException("No rank with value " + value);
    }

    private Card card(Card.Rank rank) {
        return new Card(Card.Suit.DIAMONDS, rank);
    }
}