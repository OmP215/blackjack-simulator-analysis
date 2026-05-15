# Blackjack Simulator

A comprehensive Java-based blackjack simulator designed for analyzing and comparing different player strategies. This project implements the core game engine with support for both interactive manual play and automated strategy execution through a customizable algorithm injection system.

## Overview

The Blackjack Simulator provides a controlled environment to test blackjack strategies against standard casino rules. Players can develop custom strategies, run simulations across thousands of hands, and analyze statistical outcomes including win rates, profit/loss, and other performance metrics.

## Features

- **Manual Play Mode**: Interactive command-line interface for human players to make real-time decisions
- **Algorithm Injection System**: Implement custom strategies by extending the BlackjackStrategy interface without modifying core game logic
- **Simulation Mode**: Run automated games with custom algorithms to gather statistical data
- **Balance Management**: Players start with a set bankroll and cannot continue playing once it reaches zero
- **Standard Casino Rules**:
  - Dealer hits on 16 or less, stands on 17 or more
  - Blackjack pays 1.5 to 1
  - Push (tie) returns the bet
  - Bust is an automatic loss
  - Double down allowed on first two cards
  - Split allowed when first two cards share the same rank
- **Statistics Tracking**: Records wins, losses, pushes, and total hands played
- **Basic Strategy Implementation**: Includes an example strategy based on mathematically optimal blackjack play

## Project Structure

```
blackjack-simulator/
src/
├── main/java/com/blackjack/
│   ├── model/
│   │   ├── Card.java              - Represents a playing card (suit and rank)
│   │   ├── Deck.java              - Manages card deck (shuffle, deal, reset)
│   │   ├── Hand.java              - Represents player/dealer hand (value calculation, soft/hard detection)
│   │   └── GameResult.java        - Tracks game statistics (wins, losses, profit/loss)
│   ├── player/
│   │   ├── Player.java            - Interface for player implementations
│   │   ├── HumanPlayer.java       - Interactive player for manual gameplay
│   │   └── AlgorithmPlayer.java   - Automated player executing injected strategies
│   ├── strategy/
│   │   ├── BlackjackStrategy.java - Interface for custom strategy implementations
│   │   └── BasicStrategy.java     - Example strategy (optimal play)
│   ├── game/
│   │   └── BlackjackGame.java     - Core game engine orchestrating rounds and game logic
│   ├── ui/
│   │   └── GameUI.java            - User interface handling menu and game display
│   └── Main.java                  - Application entry point
└── test/java/com/blackjack/
    ├── model/
    │   ├── CardTest.java
    │   ├── HandTest.java
    │   └── DeckTest.java
    ├── game/
    │   └── BlackjackGameTest.java
    └── strategy/
        └── BasicStrategyTest.java
pom.xml                           - Maven configuration
```

## Getting Started

### Prerequisites

- Java 25
- Maven 3.6 or higher

### Installation

1. Clone the repository:
```bash
git clone https://github.com/OmP215/blackjack-simulator.git
cd blackjack-simulator
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn exec:java -Dexec.mainClass="com.blackjack.Main"
```

Or directly from the target directory:
```bash
java -jar target/blackjack-simulator-1.0-SNAPSHOT.jar
```

## Usage

### Manual Play Mode

1. Select "Play Manual Game" from the main menu
2. Enter your desired bet amount
3. Make decisions for each hand:
  - [H]it - Draw another card
  - [S]tand - Keep your current total
  - [D]ouble - Double your bet and receive one more card (only on first two cards)
  - [P]Split - Split a pair into two separate hands (only when applicable)
4. View the outcome and your updated balance
5. Continue playing or return to the main menu

### Algorithm Simulation Mode

1. Select "Run Algorithm Simulation" from the main menu
2. Enter the number of hands to simulate
3. The simulator runs the selected strategy automatically
4. View comprehensive statistics including:
  - Total hands played
  - Final balance
  - Profit/Loss
  - Win percentage

## Implementing Custom Strategies

To create your own blackjack strategy:

1. Create a new class implementing the `BlackjackStrategy` interface:

```java
package com.blackjack.strategy;

import com.blackjack.model.Card;
import com.blackjack.model.Hand;

public class MyCustomStrategy implements BlackjackStrategy {

    @Override
    public Action decide(Hand playerHand, Card dealerUpCard) {
        int playerValue = playerHand.getValue();
        int dealerValue = dealerUpCard.getValue();
        
        // Your strategy logic here
        // Return one of: HIT, STAND, DOUBLE_DOWN, SPLIT
        
        if (playerValue >= 17) {
            return Action.STAND;
        }
        return Action.HIT;
    }

    @Override
    public double getBetSize(double minBet, double maxBet) {
        // Return your betting strategy (flat, progressive, etc.)
        return minBet;
    }

    @Override
    public String toString() {
        return "My Custom Strategy";
    }
}
```

2. Update the UI to allow selection of your strategy
3. Run the simulation to analyze your strategy's performance

## Strategy Analysis

The simulator provides detailed metrics to compare strategies:

- **Win Rate**: Percentage of hands won vs. total hands played
- **Profit/Loss**: Total change in bankroll over the simulation
- **Hands Played**: Total hands before running out of funds
- **Average Bet**: Can be used to track betting patterns

Use these metrics to compare different strategies and optimize for desired outcomes.

## Game Rules

### Standard Blackjack Rules Implemented

- Player and dealer each receive two cards initially
- Player acts first (hit, stand, double, split)
- Dealer hits on 16 or less, stands on 17 or more (including soft 17)
- Ace counts as 1 or 11 (whichever is better)
- Blackjack (ace + 10-value card on first two cards) pays 1.5 to 1
- Push (tie) returns the original bet
- Bust (total > 21) is an automatic loss
- Double down: Double the bet and receive exactly one more card (only on first two cards)
- Split: Divide a pair into two separate hands (only when first two cards share the same rank)

### Deck Management

- Standard 52-card deck
- Automatic reshuffle when fewer than 25% of cards remain
- Each new game uses a freshly shuffled deck

## Testing

Run the unit tests:

```bash
mvn test
```

Test coverage includes:
- Card ranking and value calculation
- Hand evaluation with ace logic
- Game outcome determination
- Strategy decision logic
- Player balance management

## Architecture

### Design Patterns

**Strategy Pattern**: The `BlackjackStrategy` interface allows different player algorithms to be injected without modifying the core game logic.

**Callback Pattern**: The `ActionCallback` interface in `BlackjackGame` decouples the game engine from UI concerns, allowing both interactive and automated play modes to use the same core logic.

### Separation of Concerns

- **Model**: Card, Deck, Hand, GameResult - Core game data structures
- **Player**: Player interface, HumanPlayer, AlgorithmPlayer - Different player implementations
- **Strategy**: BlackjackStrategy interface and implementations - Decision logic
- **Game**: BlackjackGame - Core game engine orchestration
- **UI**: GameUI - User interaction and display
- **Main**: Application entry point


## Future Enhancements

- Balance and Bankroll Management
- Insurance option handling
- Surrender option
- Multiple deck variance analysis
- Card counting strategy support
- Advanced statistics (variance, standard deviation)
- CSV export for statistical analysis
- GUI interface using Swing or JavaFX
- Multi-player support

## Contributing

To contribute improvements or new strategies:

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Submit a pull request

## License

This project is provided as-is for educational and research purposes.
