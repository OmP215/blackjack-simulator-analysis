package com.blackjack.strategy;

import com.blackjack.model.Hand;
import com.blackjack.model.Card;

/**
 * Interface defining a blackjack playing strategy.
 *
 * Implement this interface to create a custom playing algorithm. The game engine
 * will call {@link #decide(Hand, Card)} for every decision during a hand.
 *
 * Example implementation:
 * {@code
 * public class MyStrategy implements BlackjackStrategy {
 *     public Action decide(Hand playerHand, Card dealerUpCard) {
 *         if (playerHand.getValue() < 17) {
 *             return Action.HIT;
 *         }
 *         return Action.STAND;
 *     }
 *
 *     public double getBetSize(double minBet, double maxBet) {
 *         return minBet;
 *     }
 * }
 * }
 */
public interface BlackjackStrategy {

    /**
     * Actions a player can take during their turn.
     */
    enum Action {
        HIT, STAND, DOUBLE_DOWN, SPLIT
    }

    /**
     * Decides what action to take given the player's hand and the dealer's face-up card.
     *
     * @param playerHand  the player's current hand
     * @param dealerUpCard the dealer's visible face-up card
     * @return the action the player should take
     */
    Action decide(Hand playerHand, Card dealerUpCard);

    /**
     * Returns the bet size for the next hand.
     *
     * @param minBet the table minimum bet
     * @param maxBet the table maximum bet
     * @return the desired bet amount (clamped to [minBet, maxBet] by the game engine)
     */
    double getBetSize(double minBet, double maxBet);
}