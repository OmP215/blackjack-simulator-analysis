package com.blackjack.model;

public class GameResult {

    public enum Outcome { WIN, LOSS, PUSH, BLACKJACK}
    private int handsPlayed;
    private int wins;
    private int losses;
    private int pushes;
    private int blackjacks;
    private double balance;
    private double totalBet;

    public GameResult() {
        this.handsPlayed = 0;
        this.wins = 0;
        this.losses = 0;
        this.pushes = 0;
        this.blackjacks = 0;
        this.balance = 0.0;
        this.totalBet = 0.0;
    }

    /**
     * Records result of a hand
     */
    public void recordHand(Outcome outcome, double bet) {
        handsPlayed++;
        totalBet += bet;

        switch (outcome) {
            case WIN:
                wins++;
                balance += bet;
                break;
            case LOSS:
                losses++;
                balance -= bet;
                break;
            case PUSH:
                pushes++;
                break;
            case BLACKJACK:
                blackjacks++;
                wins++;
                balance += bet*1.5;
                break;
        }
    }

    //getter functions
    public int getHandsPlayed() { return handsPlayed; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getPushes(){ return pushes; }
    public int getBlackjacks() { return blackjacks; }
    public double getBalance() { return balance; }
    public double getTotalBet() { return totalBet; }

    public double getWinPercentage() {
        if (handsPlayed == 0) {
            return 0.0;
        }
        return (wins * 100.0) / handsPlayed;
    }

    public double getProfitLossPercentage() {
        if (totalBet == 0) {
            return 0.0;
        }
        return (balance/totalBet) * 100.0;
    }

    @Override
    public String toString() {
        return String.format(
                "Hands: %d | Wins: %d | Losses: %d | Pushes: %d | Blackjacks: %d | " +
                        "Win%%: %.1f%% | Net: %.2f",
                handsPlayed, wins, losses, pushes, blackjacks,
                getWinPercentage(), balance);
    }
}
