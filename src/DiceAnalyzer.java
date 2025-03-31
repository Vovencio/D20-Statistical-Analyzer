import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.io.*;

/**
 * An enum singleton for analyzing dice results.
 * Provides methods for calculating probabilities, managing players,
 * and saving/loading player data with a global variable {@code p}.
 */
public enum DiceAnalyzer {
    INSTANCE;

    /** Global probability threshold used for cheat detection. */
    private double p = 1000;
    /** Default table size for players' dice tables. */
    private int tableSize = 10;

    /**
     * Sets the global probability threshold.
     *
     * @param p the new probability threshold
     */
    public void setP(double p) {
        this.p = p;
    }

    /**
     * Returns the global probability threshold.
     *
     * @return the probability threshold
     */
    public double getP() {
        return p;
    }

    /**
     * Computes the binomial coefficient "n choose k".
     *
     * @param n the total number of items
     * @param k the number of items to choose
     * @return the binomial coefficient
     */
    private static long binomial(int n, int k) {
        if (k > n - k)
            k = n - k;

        long b = 1;
        for (int i = 1, m = n; i <= k; i++, m--)
            b = b * m / i;
        return b;
    }

    /**
     * Calculates the cumulative probability of obtaining a value at least
     * or at most a given amount.
     *
     * @param amount the number of successes
     * @param total  the total number of trials
     * @return the smaller of the cumulative probabilities (equal or more, equal or less)
     */
    public static double commProbabilityOfValue(int amount, int total) {
        double probabilityEqualOrMore = 0;

        for (int i = amount; i <= total; i++) {
            probabilityEqualOrMore += probabilityOfValue(i, total);
        }

        double probabilityEqualOrLess = 0;

        for (int i = 0; i <= amount; i++) {
            probabilityEqualOrLess += probabilityOfValue(i, total);
        }
        return Math.min(probabilityEqualOrMore, probabilityEqualOrLess);
    }

    /**
     * Calculates the probability of obtaining exactly a certain number of successes.
     *
     * @param amount the number of successes
     * @param total  the total number of trials
     * @return the probability of that exact outcome
     */
    public static double probabilityOfValue(int amount, int total) {
        return (double) binomial(total, amount) * Math.pow((double) 1 / 20, total)
                * Math.pow(19, total - amount);
    }

    /**
     * Determines the probability that a given player is cheating based on their dice values.
     *
     * @param player the player to analyze
     * @return The expected frequency, which can be used to determine cheating.
     */
    public double cheatProbability(Player player) {
        int[] values = player.getValues();

        int total = 0;
        for (int value : values)
            total += value;

        double minValue = 1;

        for (int value : values) {
            minValue = Math.min(commProbabilityOfValue(value, total), minValue);
        }

        return 1 / minValue;
    }

    /**
     * Determines whether a given player is cheating based on their dice values.
     *
     * @param player the player to analyze
     * @return {@code true} if the player is suspected of cheating, {@code false} otherwise
     */
    public boolean doesCheat(Player player) {
        int[] values = player.getValues();

        int total = 0;
        for (int value : values)
            total += value;

        double minValue = 1;

        for (int value : values) {
            minValue = Math.min(commProbabilityOfValue(value, total), minValue);
        }

        return 1 / minValue >= p;
    }

    /** List of players being analyzed. */
    private List<Player> players = new ArrayList<>();

    /**
     * Returns the list of players.
     *
     * @return the players list
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Sets the list of players.
     *
     * @param players the new players list
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Sets the table size for all players and updates each player's table size accordingly.
     *
     * @param tableSize the new table size
     */
    public void setTableSize(int tableSize) {
        this.tableSize = tableSize;
        for (Player player : players) {
            player.setTableSize(tableSize);
        }
    }

    /**
     * Returns the current table size.
     *
     * @return the table size
     */
    public int getTableSize() {
        return tableSize;
    }

    /**
     * Finds the index of a player by their name.
     *
     * @param name the name of the player to find
     * @return the index of the player, or 0 if not found
     */
    public int findPlayerByName(String name) {
        for (int i = 0; i < players.size(); i++) {
            if (Objects.equals(name, players.get(i).getName()))
                return i;
        }
        System.err.printf("Player %s does not exist!", name);
        return 0;
    }

    /**
     * Retrieves a player by their index.
     *
     * @param index the index of the player
     * @return the player at the specified index, or the first player if index is invalid
     */
    public Player getPlayer(int index) {
        if (index < 0 || index >= players.size()) {
            System.err.printf("Player with index %d does not exist!", index);
            return players.getFirst();  // Changed from getFirst() to get(0) for List compatibility.
        }
        return players.get(index);
    }

    /**
     * Retrieves a player by their name.
     *
     * @param name the name of the player
     * @return the player with the specified name
     */
    public Player getPlayer(String name) {
        return players.get(findPlayerByName(name));
    }

    /**
     * Changes the name of a player.
     *
     * @param oldName the current name of the player
     * @param newName the new name to set
     */
    public void changePlayerName(String oldName, String newName) {
        getPlayer(oldName).setName(newName);
    }

    /**
     * Adds a new player using a specified table, current element, and name.
     *
     * @param table          the dice table as a byte array
     * @param currentElement the current element index in the table
     * @param name           the player's name
     */
    public void addPlayer(byte[] table, int currentElement, String name) {
        Player newPlayer = new Player(table, currentElement, name);
        players.add(newPlayer);
    }

    /**
     * Adds a new player with a specified name and default table size.
     *
     * @param name the player's name
     */
    public void addPlayer(String name) {
        Player newPlayer = new Player(tableSize, name);
        players.add(newPlayer);
    }

    /**
     * Returns a string representation of all players.
     *
     * @return a formatted string listing all players and their tables
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            result.append(player.name)
                    .append(System.lineSeparator())
                    .append(Arrays.toString(player.getTable()));

            if (i < players.size() - 1) {
                result.append(System.lineSeparator()).append("---").append(System.lineSeparator());
            }
        }
        return result.toString();
    }

    /**
     * Saves the global variable {@code p} and the list of players to a file.
     * The file format starts with:
     * <pre>
     * p:&lt;value of p&gt;
     * ---global---
     * </pre>
     * followed by each player's data.
     *
     * @param file the file to save the players to
     * @throws IOException if an I/O error occurs
     */
    public void savePlayers(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Save the global variable p first.
            writer.write("p:" + p);
            writer.newLine();
            writer.write("---global---");
            writer.newLine();

            // Now save each player.
            for (Player player : players) {
                writer.write(player.getName());
                writer.newLine();
                writer.write(Arrays.toString(player.getTable()));
                writer.newLine();
                writer.write(String.valueOf(player.getCurrentElement()));
                writer.newLine();
                writer.write("---");  // separator for clarity
                writer.newLine();
            }
        }
    }

    /**
     * Loads the global variable {@code p} and the list of players from a file.
     * Expects the file to be in the format produced by {@link #savePlayers(File)}.
     *
     * @param file the file to load players from
     * @throws IOException if an I/O error occurs
     */
    public void loadPlayers(File file) throws IOException {
        List<Player> players = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Read the p variable from the first line.
            String pLine = reader.readLine();
            if (pLine != null && pLine.startsWith("p:")) {
                p = Double.parseDouble(pLine.substring(2).trim());
            }
            // Skip the global separator line.
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines or separator lines.
                if (line.trim().isEmpty() || line.equals("---")) {
                    continue;
                }

                // First line is the player's name.
                String name = line.trim();

                // Next line is the table array.
                String arrayLine = reader.readLine();
                byte[] table = parseByteArray(arrayLine);

                // Next line is the currentElement.
                String currentElementLine = reader.readLine();
                int currentElement = Integer.parseInt(currentElementLine.trim());

                // Create a new Player using the table and current element.
                Player player = new Player(table, currentElement, name);
                players.add(player);

                // Read the separator line (if it exists) before processing the next player.
                reader.readLine();
            }
        }

        this.players = players;
    }

    /**
     * Parses a string representation of a byte array.
     * Expected format: "[1, 2, 3, -1, ...]"
     *
     * @param arrayLine the string to parse
     * @return the corresponding byte array
     */
    private static byte[] parseByteArray(String arrayLine) {
        // Remove square brackets and extra whitespace.
        String trimmed = arrayLine.replaceAll("[\\[\\]]", "").trim();
        if (trimmed.isEmpty()) {
            return new byte[0];
        }
        String[] tokens = trimmed.split(",");
        byte[] result = new byte[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = Byte.parseByte(tokens[i].trim());
        }
        return result;
    }
}
