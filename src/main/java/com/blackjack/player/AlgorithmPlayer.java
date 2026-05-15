package com.blackjack.player;

import com.blackjack.model.Hand;
import com.blackjack.model.GameResult;
import com.blackjack.strategy.BlackjackStrategy;

public class AlgorithmPlayer implements Player {
    private final String name;
    private final Hand hand;
    private final GameResult gameResult;
    private final BlackjackStrategy strategy;

    public AlgorithmPlayer(String name, BlackjackStrategy strategy) {
        this.name = name;
        this.hand = new Hand();
        this.gameResult = new GameResult();
        this.strategy = strategy;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Hand getHand() {
        return hand;
    }

    @Override
    public GameResult getGameResult() {
        return gameResult;
    }

    public BlackjackStrategy getStrategy() {
        return strategy;
    }

    @Override
    public double placeBet(double minBet, double maxBet) {
        double bet = strategy.getBetSize(minBet, maxBet);
        return Math.max(minBet, Math.min(maxBet, bet));
    }

    @Override
    public void resetHand() {
        hand.clear();
    }
}
