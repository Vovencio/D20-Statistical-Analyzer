# D20 Statistical Analyzer

This project provides a Java-based statistical analysis tool for evaluating D20 dice rolls. The tool helps detect potential cheating by analyzing dice results across multiple players.

**Note:** This documentation and JavaDoc were generated using ChatGPT, but I wrote the code myself 😸

## Features
- Calculate probabilities of dice outcomes
- Detect suspicious patterns in dice rolls
- Manage multiple players with customizable dice tables
- Save and load player data

## Installation
Clone the repository:
```sh
 git clone https://github.com/Vovencio/D20-Statistical-Analyzer.git
```

## Usage
The main functionality is provided through the `DiceAnalyzer` singleton. A small example demonstrating basic usage is found in Main.java.

## Classes
### `DiceAnalyzer`
- Singleton for statistical analysis
- Computes probabilities of outcomes
- Detects cheating using statistical thresholds
- Saves and loads player data

### `Player`
- Represents a player in the analysis
- Stores dice roll history
- Supports dynamic table resizing

## Saving and Loading Data
Players and global probability settings can be saved and loaded from a file:
```java
File file = new File("players.txt");
analyzer.savePlayers(file);
analyzer.loadPlayers(file);
```

## License
This project is licensed under the Apache-2.0 License.

## Contributions
Contributions are welcome! Feel free to submit pull requests or report issues.

## Repository
[GitHub Repository](https://github.com/Vovencio/D20-Statistical-Analyzer)
