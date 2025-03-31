import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.io.*;


public enum DiceAnalyzer {
    INSTANCE;

    private double p = 1000;
    private int tableSize = 10;

    public void setP(double p) {
        this.p = p;
    }

    public double getP() {
        return p;
    }

    private static long binomial(int n, int k)
    {
        if (k>n-k)
            k=n-k;

        long b=1;
        for (int i=1, m=n; i<=k; i++, m--)
            b=b*m/i;
        return b;
    }

    public static double commProbabilityOfValue(int amount, int total){
        double probabilityEqualOrMore = 0;

        for (int i = amount; i <= total; i++){
            probabilityEqualOrMore += probabilityOfValue(i, total);
        }

        double probabilityEqualOrLess = 0;

        for (int i = 0; i <= amount; i++){
            probabilityEqualOrLess += probabilityOfValue(i, total);
        }
        return Math.min(probabilityEqualOrMore, probabilityEqualOrLess);
    }

    public static double probabilityOfValue(int amount, int total){
        return (double) binomial(total, amount) * Math.pow((double) 1 /20, total) * Math.pow(19, total-amount);
    }

    public boolean doesCheat(Player player){
        int[] values = player.getValues();

        int total = 0;
        for (int value : values)
            total+=value;

        double minValue = 1;

        for (int value : values){
            minValue = Math.min(commProbabilityOfValue(value, total),minValue);
        }

        return 1 / minValue >= p;
    }

    private List<Player> players = new ArrayList<>();

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setTableSize(int tableSize) {
        this.tableSize = tableSize;
        for (Player player : players){
            player.setTableSize(tableSize);
        }
    }

    public int getTableSize() {
        return tableSize;
    }

    public int findPlayerByName(String name){
        for (int i = 0; i < players.size(); i++){
            if (Objects.equals(name, players.get(i).getName())) return i;
        }
        System.err.printf("Player %s does not exist!", name);
        return 0;
    }

    public Player getPlayer(int index){
        if (index < 0 || index >= players.size()) {
            System.err.printf("Player with index %d does not exist!", index);
            return players.getFirst();
        }
        return players.get(index);
    }

    public Player getPlayer(String name){
        return players.get(findPlayerByName(name));
    }

    public void changePlayerName(String oldName, String newName){
        getPlayer(oldName).setName(newName);
    }

    public void addPlayer(byte[] table, int currentElement, String name){
        Player newPlayer = new Player(table, currentElement, name);
        players.add(newPlayer);
    }

    public void addPlayer(String name){
        Player newPlayer = new Player(tableSize, name);
        players.add(newPlayer);
    }

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
     * Saves a list of Player objects to a file, along with the class variable p.
     * The file format will now start with:
     *
     * p:<value of p>
     * ---global---
     *
     * followed by the players as before.
     *
     * @param file    the file to save the players to
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
     * Loads a list of Player objects from a file, and also loads the class variable p.
     * Expects the file to be in the format produced by savePlayers(), with p saved at the top.
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
