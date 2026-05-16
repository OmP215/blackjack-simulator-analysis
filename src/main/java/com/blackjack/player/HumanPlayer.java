package com.blackjack.player;

import com.blackjack.model.GameResult;
import com.blackjack.model.Hand;
import java.util.Scanner;

public class HumanPlayer implements Player {

    private final String name;
    private final Hand hand;
    private final GameResult gameResult;
    private final Scanner scanner;
    private double balance = 500.0; //starting balance

    public HumanPlayer(String name, Scanner scanner) {
        this.name = name;
        this.hand = new Hand();
        this.gameResult = new GameResult();
        this.scanner = scanner;
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

    @Override
    public double placeBet(double minBet, double maxBet) {
        while (true) {
            System.out.printf("Balance: $%.2f%n", balance);
            System.out.printf("Enter your bet (%.0f - %.0f): ", minBet, maxBet);
            try {
                double bet = Double.parseDouble(scanner.nextLine().trim());
                if (bet < minBet || bet > maxBet) {
                    System.out.printf("Bet must be between %.0f and %.0f.%n", minBet, maxBet);
                    continue;
                }
                if (bet > balance) {
                    System.out.printf("Insufficient balance. You have $%.2f.%n", balance);
                    continue;
                }
                return bet;
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
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
        this.balance = Math.max(0, amount); //can never go below 0
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
}
