package com.blackjack.player;

import com.blackjack.model.GameResult;
import com.blackjack.model.Hand;
import java.util.Scanner;

public class HumanPlayer implements Player {

    private final String name;
    private final Hand hand;
    private final GameResult gameResult;
    private final Scanner scanner;

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
            System.out.printf("Enter your bet (%.0f - %.0f): ", minBet, maxBet);
            try {
                double bet = Double.parseDouble(scanner.nextLine().trim());
                if (bet >= minBet && bet <= maxBet) {
                    return bet;
                }
                System.out.printf("Bet must be between %.0f and %.0f.%n", minBet, maxBet);
            } catch (Exception e) {
                System.out.println("Invalid input. Please eneter a number.");
            }
        }
    }

    @Override
    public void resetHand() {
        hand.clear();
    }
}
