package com.blackjack.ui;

import com.blackjack.game.BlackjackGame;
import com.blackjack.model.Card;
import com.blackjack.model.Hand;
import com.blackjack.model.GameResult;
import com.blackjack.player.AlgorithmPlayer;
import com.blackjack.player.HumanPlayer;
import com.blackjack.strategy.BlackjackStrategy;
import com.blackjack.strategy.BasicStrategy;

import java.util.Scanner;

/**
 * Console based UI for the blackjack simulator.
 * <p>
 * Handles all user interaction, display formatting, and routes the user to either
 * manual play mode or algorithm simulation mode
 */
public class GameUI {
    private static final String SEPARATOR = "=".repeat(50);
    private static final String THIN_SEP = "-".repeat(50);

    private final Scanner scanner;
    private final BlackjackGame game;

    public GameUI() {
        this.scanner = new Scanner(System.in);
        this.game = new BlackjackGame();
    }

    //Entry Point
    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    runManualMode();
                    break;
                case "2":
                    runAlgorithmMode();
                    break;
                case "3":
                    System.out.println("Thanks for playing");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid Choice. Please enter 1,2, or 3");

            }
        }
    }

    //Manual Mode
    private void runManualMode() {
        System.out.println("Enter username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            username = "player";
        }
        HumanPlayer player = new HumanPlayer(username, scanner);

        System.out.println(SEPARATOR);
        System.out.println("Welcome, " + username + "! Starting balance: $" + String.format("%.2f", player.getBalance()));
        System.out.println("Type 'quit' at any prompt to return to the menu.");
        System.out.println(SEPARATOR);

        while (true) {
            if (player.getBalance() <= 0) {
                System.out.println();
                System.out.println(SEPARATOR);
                System.out.println("GAME OVER! You're out of money.");
                System.out.println("Final Balance: $" + String.format("%.2f", player.getBalance()));
                System.out.println(SEPARATOR);
                break;
            }
            System.out.println();
            System.out.println(THIN_SEP);
            System.out.printf("Balance: $%.2f | %s%n", player.getBalance(), player.getGameResult());
            System.out.println(THIN_SEP);

            //Phase 1: bet
            System.out.printf("Enter your bet (%.0f - %.0f) or 'quit': ", game.getMinBet(), game.getMaxBet());
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("quit")) {
                break;
            }

            double bet;
            try {
                bet = Double.parseDouble(input);
                if (bet < game.getMinBet() || bet > game.getMaxBet()) {
                    System.out.printf("Bet must be between %.0f and %.0f.%n", game.getMinBet(), game.getMaxBet());
                    continue;
                }
                if (bet > player.getBalance()) {
                    System.out.printf("Insufficient Balance! You have $%.2f.%n", player.getBalance());
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            playManualRound(player, bet);
        }
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("Session summary for " + player.getName());
        printStats(player.getGameResult(), player.getBalance());
        System.out.println(SEPARATOR);
    }

    /**
     * Plays a single round in manual mode
     * All game state updates are handled here so the ui and display the hand after every card
     */
    private void playManualRound(HumanPlayer player, double bet) {
        player.resetHand();
        game.getDealerHand().clear();

        //Initial d
        player.getHand().addCard(dealCard());
        game.getDealerHand().addCard(dealCard());
        player.getHand().addCard(dealCard());
        game.getDealerHand().addCard(dealCard());

        printGameState(player.getHand(), game.getDealerHand(), true);

        //Player BJ
        if (player.getHand().isBJ()) {
            System.out.println("\n*** BLACKJACK! ***");
            revealDealer();
            if (game.getDealerHand().isBJ()) {
                System.out.println("Dealer also has blackjack. PUSH.");
                player.getGameResult().recordHand(GameResult.Outcome.PUSH, bet);
                player.updateBalance(0);
                printOutcome(GameResult.Outcome.PUSH, bet);
            } else {
                player.getGameResult().recordHand(GameResult.Outcome.BLACKJACK, bet);
                double blackjackWinnings = bet * 1.5;
                player.updateBalance(blackjackWinnings);
                printOutcome(GameResult.Outcome.BLACKJACK, bet);
            }
            return;
        }

        //Players turn
        boolean playerBusted = false;
        boolean splitHandled = false;
        double activeBet = bet;

        playerLoop:
        while (true) {
            BlackjackStrategy.Action action = promptAction(player.getHand());
            switch (action) {
                case HIT:
                    player.getHand().addCard(dealCard());
                    printGameState(player.getHand(), game.getDealerHand(), true);
                    if (player.getHand().isBust()) {
                        System.out.println("BUST! You went over 21!");
                        playerBusted = true;
                        break playerLoop;
                    }
                    break;
                case STAND:
                    break playerLoop;
                case DOUBLE_DOWN:
                    if (player.getHand().canDoubleDown()) {
                        player.getHand().addCard(dealCard());
                        activeBet *= 2;
                        System.out.printf("Doubled down! New bet: %.0f%n", activeBet);
                        printGameState(player.getHand(), game.getDealerHand(), true);
                        if (player.getHand().isBust()) {
                            System.out.println("Bust! You went over 21!");
                            playerBusted = true;
                        }
                        break playerLoop;
                    }
                    System.out.println("Double down not available at this time. Choose another action.");
                    break;
                case SPLIT:
                    if (player.getHand().canSplit()) {
                        System.out.println("Split chosen. Playing as 2 separate hands.");
                        playSplitHandsManually(player, bet);
                        splitHandled = true;
                        break playerLoop;
                    }
                    System.out.println("Split not available (cards dont match). Choose another action.");
                    break;
            }
        }

        //Split already recorded outcomes internally, skip post loop recording
        if (splitHandled) {
            return;
        }

        if (playerBusted) {
            player.getGameResult().recordHand(GameResult.Outcome.LOSS, activeBet);
            player.updateBalance(-activeBet);
            printOutcome(GameResult.Outcome.LOSS, activeBet);
        } else {
            revealDealer();
            GameResult.Outcome outcome = evaluateOutcome(player.getHand());
            player.getGameResult().recordHand(outcome, activeBet);

            if (outcome == GameResult.Outcome.WIN) {
                player.updateBalance(activeBet);
            } else if (outcome == GameResult.Outcome.LOSS) {
                player.updateBalance(-activeBet);
            }
            printOutcome(outcome, activeBet);
        }
    }


    private void runAlgorithmMode() {
        System.out.println(SEPARATOR);
        System.out.println("Algorithm Simulation Mode");
        System.out.println(SEPARATOR);
        System.out.println("Available strategies:");
        System.out.println("   1. Basic Strategy");
        scanner.nextLine();

        BlackjackStrategy strategy = new BasicStrategy();
        String stratName = strategy.toString();
        System.out.println("Using: " + stratName);

        System.out.println("Number of hands to simulate");
        int hands;
        try {
            hands = Integer.parseInt(scanner.nextLine().trim());
            if (hands <= 0) {
                System.out.println("Must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }

        AlgorithmPlayer bot = new AlgorithmPlayer("Bot (" + stratName + ")", strategy);
        System.out.println();
        System.out.println("Running " + hands + " hands...");

        long start = System.currentTimeMillis();
        int reportInterval = Math.max(1, hands/10);
        for (int i = 1; i <= hands; i++) {
            if (bot.getBalance() <= 0) {
                System.out.printf("  Hand %d/%d - Bot BUSTED! Out of money.%n", i, hands);
                break;
            }
            game.playAlgorithmRound(bot);
            if (hands <= 100 || i % reportInterval == 0) {
                System.out.printf("   Hand %d/%d - %s/%n", i ,hands, bot.getGameResult());
            }
        }
        long elapsed = System.currentTimeMillis() - start;

        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("Simulation Complete - " + bot.getName());
        System.out.printf("Time Elapsed: %d ms%n", elapsed);
        printStats(bot.getGameResult(), bot.getBalance());
        System.out.println(SEPARATOR);
    }


    private void playSplitHandsManually(HumanPlayer player, double bet) {
        Card splitCard = player.getHand().getCards().getFirst();

        //Create 2 distinct card instanced w/ same rank/suit for each split hand
        Card splitCard1 = new Card(splitCard.getSuit(), splitCard.getRank());
        Card splitCard2 = new Card(splitCard.getSuit(), splitCard.getRank());

        Hand hand1 = new Hand();
        hand1.addCard(splitCard1);
        hand1.addCard(dealCard());

        Hand hand2 = new Hand();
        hand2.addCard(splitCard2);
        hand2.addCard(dealCard());

        Hand[] splitHands = { hand1, hand2 };

        for (int i = 0; i < 2; i++) {
            System.out.printf("%n--- Split Hand %d ---%n", i + 1);
            Hand hand = splitHands[i];
            boolean busted = false;

            while (true) {
                printGameState(hand, game.getDealerHand(), true);
                if (hand.isBust()) {
                    System.out.println("BUST on split hand " + (i+1));
                    busted = true;
                    break;
                }
                BlackjackStrategy.Action action = promptAction(hand);
                if (action == BlackjackStrategy.Action.STAND) {
                    break;
                }
                if (action == BlackjackStrategy.Action.DOUBLE_DOWN && hand.canDoubleDown()) {
                    hand.addCard(dealCard());
                    printGameState(hand, game.getDealerHand(), true);
                    if (hand.isBust()) {
                        System.out.println("BUST on split hand " + (i+1));
                        busted = true;
                    }
                    break;
                }
                hand.addCard(dealCard());
            }
            if (busted) {
                player.getGameResult().recordHand(GameResult.Outcome.LOSS, bet);
                printOutcome(GameResult.Outcome.LOSS, bet);
            } else {
                revealDealer();
                GameResult.Outcome outcome = evaluateOutcome(hand);
                player.getGameResult().recordHand(outcome, bet);
                printOutcome(outcome, bet);
            }
        }
    }

    //Display Helpers
    private void printBanner() {
        System.out.println(SEPARATOR);
        System.out.println("     BLACKJACK SIMULATOR     ");
        System.out.println(SEPARATOR);
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("MAIN MENU");
        System.out.println("   1. Play Manual Game");
        System.out.println("   2. Run Algorithm Simulation");
        System.out.println("   3. Exit");
        System.out.println("Choice: ");
    }

    private void printGameState(Hand playerHand, Hand dealerHand, boolean hideSecondDealerCard) {
        System.out.println();
        if (hideSecondDealerCard) {
            System.out.println("Dealer: [" + dealerHand.getCards().getFirst() + ", ???]");
        } else {
            System.out.println("Dealer: " + dealerHand);
        }
        System.out.println("You:    " + playerHand);
    }

    private BlackjackStrategy.Action promptAction(Hand hand) {
        StringBuilder menu = new StringBuilder("Actions: [H]it, [S]tand");
        if (hand.canDoubleDown()) {
            menu.append(", [D]ouble");
        }
        if (hand.canSplit()) {
            menu.append(", [P]Split");
        }
        menu.append(" > ");

        while (true) {
            System.out.println(menu);
            String input = scanner.nextLine().trim().toUpperCase();
            switch (input) {
                case "H": return BlackjackStrategy.Action.HIT;
                case "S": return BlackjackStrategy.Action.STAND;
                case "D": return BlackjackStrategy.Action.DOUBLE_DOWN;
                case "P": return BlackjackStrategy.Action.SPLIT;
                default:
                    System.out.println("Invalid input. Try again.");
            }
        }
    }

    private void revealDealer() {
        System.out.println("\nDealer reveals: " + game.getDealerHand());
        while (game.getDealerHand().getValue() < 17) {
            game.getDealerHand().addCard(dealCard());
            System.out.println("Dealer hits:    " + game.getDealerHand());
        }
        if (game.getDealerHand().isBust()) {
            System.out.println("Dealer BUSTS!");
        }
    }

    private GameResult.Outcome evaluateOutcome(Hand playerHand) {
        Hand dealerHand = game.getDealerHand();
        if (dealerHand.isBust()) {
            return GameResult.Outcome.WIN;
        }
        int pv = playerHand.getValue();
        int dv = dealerHand.getValue();
        if (pv > dv) return GameResult.Outcome.WIN;
        if (dv > pv) return GameResult.Outcome.LOSS;
        return GameResult.Outcome.PUSH;
    }

    private void printOutcome(GameResult.Outcome outcome, double bet) {
        switch (outcome) {
            case WIN:       System.out.printf("*** WIN! +%.0f ***%n", bet); break;
            case BLACKJACK: System.out.printf("*** BLACKJACK! +%.2f ***%n", bet * 1.5); break;
            case LOSS:      System.out.printf("*** LOSS. -%.0f ***%n", bet); break;
            case PUSH:      System.out.println("*** PUSH. Bet returned. ***"); break;
        }
    }

    private void printStats(GameResult result, double currentBalance) {
        double startingBalance = 500.0;
        double profitLoss = currentBalance - startingBalance;

        System.out.printf("Hands played: %d%n",    result.getHandsPlayed());
        System.out.printf("Wins            : %d%n",    result.getWins());
        System.out.printf("Losses          : %d%n",    result.getLosses());
        System.out.printf("Pushes          : %d%n",    result.getPushes());
        System.out.printf("Blackjacks      : %d%n",    result.getBlackjacks());
        System.out.printf("Win %%          : %.2f%%%n", result.getWinPercentage());
        System.out.printf("Starting Balance: $%.2f%n", startingBalance);
        System.out.printf("Current Balance : $%.2f%n", currentBalance);
        System.out.printf("Net Profit/Loss : $%.2f%n", profitLoss);
        if (startingBalance > 0) {
            System.out.printf("P/L %%          : %.2f%%%n", (profitLoss / startingBalance) * 100);
        }
    }

    private Card dealCard() {
        return game.dealCard();
    }
}
