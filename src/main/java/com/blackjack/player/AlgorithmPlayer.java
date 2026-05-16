package com.blackjack.player;

import com.blackjack.model.Hand;
import com.blackjack.model.GameResult;
import com.blackjack.strategy.BlackjackStrategy;

public class AlgorithmPlayer implements Player {
    private final String name;
    private final Hand hand;
    private final GameResult gameResult;
    private final BlackjackStrategy strategy;
    private double balance = 500.0;

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
        double strategicBet = strategy.getBetSize(minBet, maxBet);
        double finalBet = Math.min(strategicBet, balance);
        if (finalBet < minBet) {
            finalBet = minBet;
        }
        return finalBet;
    }

    @Override
    public void resetHand() {
        hand.clear();
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void setBalance(double amount) {
        this.balance = Math.max(0, amount);
    }

    @Override
    public void updateBalance(double amount) {
        balance += amount;
        if (balance < 0) balance = 0;
    }

    @Override
    public boolean canPlaceBet(double bet) {
        return balance >= bet;
    }

    @Override
    public String toString() {
        return name + " (Balance: $" + String.format("%.2f", balance) + ")";
    }
}
