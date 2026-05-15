package com.blackjack.game;

import com.blackjack.model.*;
import com.blackjack.player.AlgorithmPlayer;
import com.blackjack.player.Player;
import com.blackjack.strategy.BlackjackStrategy;

/**
 * Core game engine
 * <p>
 * Implements standard casino tules:
 *      Dealer hits on 16 or less
 *      Blackjack pays 1.5 : 1
 *      Push returns bet
 *      Bust is a loos
 *      Double down allowed on first two cards
 *      Split allowed when first two cards share same rank
 *      Deck is reshuffled when fewer than 25% of cards
 */
public class BlackjackGame {

    private static final double MIN_BET = 10.0;
    private static final double MAX_BET = 500.0;
    private static final int RESHUFFLE_THRESHOLD = 13;

    private final Deck deck;
    private final Hand dealerHand;

    public BlackjackGame() {
        this.deck = new Deck();
        this.deck.shuffle();
        this.dealerHand = new Hand();
    }

    public Hand getDealerHand() { return dealerHand; }
    public double getMinBet() { return MIN_BET; }
    public double getMaxBet() { return MAX_BET; }

    /**
     * Round Orchestration
     * <p>
     * Plays a single round for a human player
     *
     * @param player    the human player
     * @param callback  used to request an action from the UI
     * @return the outcome of the round
     */
    public GameResult.Outcome playRound(Player player, ActionCallback callback) {
        reshuffleIfNeeded();
        player.resetHand();
        dealerHand.clear();

        double bet = player.placeBet(MIN_BET, MAX_BET);

        //initial d: player gets 2 cards, dealer as well
        player.getHand().addCard(deck.deal());
        dealerHand.addCard(deck.deal());
        player.getHand().addCard(deck.deal());
        dealerHand.addCard(deck.deal());

        //check for player bj
        if (player.getHand().isBJ()) {
            if (dealerHand.isBJ()) {
                GameResult.Outcome outcome = GameResult.Outcome.PUSH;
                player.getGameResult().recordHand(outcome, bet);
                return outcome;
            }
            GameResult.Outcome outcome = GameResult.Outcome.BLACKJACK;
            player.getGameResult().recordHand(outcome, bet);
            return outcome;
        }

        //players turn
        boolean doubled = false;
        boolean playerBusted = false;

        playerLoop:
        while (true) {
            BlackjackStrategy.Action action = callback.requestAction(player.getHand(), dealerHand.getCards().getFirst());

            switch (action) {
                case HIT:
                    player.getHand().addCard(deck.deal());
                    if (player.getHand().isBust()) {
                        playerBusted = true;
                        break playerLoop;
                    }
                    break;
                case STAND:
                    break playerLoop;
                case DOUBLE_DOWN:
                    if (player.getHand().canDoubleDown()) {
                        player.getHand().addCard(deck.deal());
                        bet *= 2;
                        doubled = true;
                        if (player.getHand().isBust()) {
                            playerBusted = true;
                        }
                        break playerLoop;
                    }
                    // if double down not allowed, treat as hit
                    player.getHand().addCard(deck.deal());
                    if (player.getHand().isBust()) {
                        playerBusted = true;
                        break playerLoop;
                    }
                    break;
                case SPLIT:
                    //handled as 2 separate rounds
                    if (player.getHand().canSplit()) {
                        return playSplitRound(player, callback, bet);
                    }

                    //fallback to hit if split not available
                    player.getHand().addCard(deck.deal());
                    if (player.getHand().isBust()) {
                        playerBusted = true;
                        break playerLoop;
                    }
                    break;
            }
        }
        if (playerBusted) {
            GameResult.Outcome outcome = determineOutcome(player.getHand(), dealerHand);
            player.getGameResult().recordHand(outcome, bet);
            return outcome;
        }

        playDealerTurn();

        GameResult.Outcome outcome = determineOutcome(player.getHand(), dealerHand);
        player.getGameResult().recordHand(outcome,bet);
        return outcome;
    }

    /**
     * Plays a complete round for an algo player using the injected strategy
     *
     * @param player the algo player
     * @return the outcome of the round
     */
    public GameResult.Outcome playAlgorithmRound(AlgorithmPlayer player) {
        return playRound((Player) player, (hand, dealerUp) ->
                player.getStrategy().decide(hand, dealerUp));
    }


    // Helpers

    private GameResult.Outcome playSplitRound(Player player, ActionCallback callback, double bet) {
        Card splitCard = player.getHand().getCards().getFirst();
        //create distinct card instances for each split round
        Card splitCard1 = new Card(splitCard.getSuit(), splitCard.getRank());
        Card splitCard2 = new Card(splitCard.getSuit(), splitCard.getRank());

        //play first hand
        Hand firstHand = new Hand();
        firstHand.addCard(splitCard1);
        firstHand.addCard(deck.deal());
        GameResult.Outcome firstOutcome = playHandToCompletion(firstHand, callback, bet);

        //play second hand
        Hand secondHand = new Hand();
        secondHand.addCard(splitCard2);
        secondHand.addCard(deck.deal());
        GameResult.Outcome secondOutcome = playHandToCompletion(secondHand, callback, bet);

        //record both outcomes
        player.getGameResult().recordHand(firstOutcome, bet);
        player.getGameResult().recordHand(secondOutcome, bet);

        //update players visible hand to the first split hand for display purposes
        player.getHand().clear();
        for (Card c : firstHand.getCards()) {
            player.getHand().addCard(c);
        }
        return firstOutcome;
    }

    private GameResult.Outcome playHandToCompletion(Hand hand, ActionCallback callback, double bet) {
        while (true) {
            BlackjackStrategy.Action action = callback.requestAction(hand, dealerHand.getCards().getFirst());
            if (action == BlackjackStrategy.Action.STAND) {
                break;
            }
            if (action == BlackjackStrategy.Action.DOUBLE_DOWN && hand.canDoubleDown()) {
                hand.addCard(deck.deal());
                break;
            }
            hand.addCard(deck.deal());
            if (hand.isBust()) {
                return GameResult.Outcome.LOSS;
            }
        }
        playDealerTurn();
        return determineOutcome(hand,dealerHand);
    }

    private void playDealerTurn() {
        while (dealerHand.getValue() <17) {
            dealerHand.addCard(deck.deal());
        }
    }

    private GameResult.Outcome determineOutcome(Hand playerHand, Hand dealerHand) {
        int playerVal = playerHand.getValue();
        int dealerVal = dealerHand.getValue();

        if (dealerHand.isBust()) {
            return GameResult.Outcome.WIN;
        }
        if (playerVal > dealerVal) {
            return GameResult.Outcome.WIN;
        }
        if (playerVal < dealerVal) {
            return GameResult.Outcome.LOSS;
        }
        return GameResult.Outcome.PUSH;
    }

    /**
     * Deals a single card for the deck, reshuffling is needed
     * Exposed for use by the UI layer during manual play
     *
     * @return the dealt card
     */
    public Card dealCard() {
        reshuffleIfNeeded();
        return deck.deal();
    }

    private void reshuffleIfNeeded() {
        if (deck.size() < RESHUFFLE_THRESHOLD) {
            deck.reset();
        }
    }

    @FunctionalInterface
    public interface ActionCallback {
        BlackjackStrategy.Action requestAction(Hand playerHand, Card dealerUpCard);
    }
}
