import java.util.Arrays;

/**
 * Represents a player with a name, a die table, and a pointer to the current element in the table.
 */
public class Player {
    /** The player's name. */
    String name;
    /** The dice table represented as an array of bytes. */
    byte[] table;
    /** The index of the current element in the table. */
    int currentElement;

    /**
     * Constructs a new Player with a given dice table, current element index, and name.
     *
     * @param table          the dice table as a byte array
     * @param currentElement the index of the current element
     * @param name           the player's name
     */
    public Player(byte[] table, int currentElement, String name) {
        this.table = table.clone();
        this.currentElement = currentElement;
        this.name = name;
    }

    /**
     * Constructs a new Player with a specified table size and name.
     * The dice table is initialized with all values set to -1.
     *
     * @param tableSize the size of the dice table
     * @param name      the player's name
     */
    public Player(int tableSize, String name) {
        this.table = new byte[tableSize];
        Arrays.fill(table, (byte) -1);
        this.currentElement = 0;
        this.name = name;
    }

    /**
     * Sets a new value in the dice table at the current element position.
     * If the value is outside the range [1,20], it is reset to 1.
     *
     * @param value the value to set
     */
    public void setValue(byte value) {
        if (value > 20 || value < 1) {
            System.err.printf("Value %d is outside d20 bounds!%n", value);
            value = 1;
        }
        table[currentElement] = value;
        currentElement = (currentElement + 1) % table.length;
    }

    /**
     * Returns the index of the current element in the dice table.
     *
     * @return the current element index
     */
    public int getCurrentElement() {
        return currentElement;
    }

    /**
     * Sets a new dice table.
     *
     * @param table the new dice table
     */
    public void setTable(byte[] table) {
        this.table = table;
    }

    /**
     * Returns the dice table.
     *
     * @return the dice table as a byte array
     */
    public byte[] getTable() {
        return table;
    }

    /**
     * Finds the index of the last set element in the dice table.
     *
     * @return the index of the last set element, or -1 if the table is empty
     */
    int findLast() {
        if (table[table.length - 1] != -1)
            return table.length - 1;
        if (table[0] == -1)
            return -1;

        int left = 0;
        int right = table.length - 1;

        while (left < right) {
            int m = left + (right - left + 1) / 2;
            if (table[m] != -1) {
                left = m;
            } else if (table[m] == -1) {
                right = m - 1;
            }
        }
        return left;
    }

    /**
     * Resizes the dice table to a new size.
     * If the new size is larger, existing values are preserved and remaining positions are set to -1.
     * If the new size is smaller, the table is truncated.
     *
     * @param newSize the new size of the dice table
     */
    public void setTableSize(int newSize) {
        if (newSize > table.length) {
            int last = findLast();
            byte[] oldTable = table.clone();
            this.table = new byte[newSize];
            Arrays.fill(table, (byte) -1);
            System.arraycopy(oldTable, 0, table, 0, oldTable.length);
            if (last == -1)
                currentElement = 0;
            else
                currentElement = last + 1;
        } else if (newSize < table.length) {
            int last = findLast();
            byte[] oldTable = table.clone();
            this.table = new byte[newSize];
            System.arraycopy(oldTable, 0, table, 0, table.length);
            if (last == -1)
                currentElement = 0;
            else
                currentElement = Math.min(last + 1, table.length - 1);
        }
    }

    /**
     * Sets a new value in the dice table using an integer.
     *
     * @param value the value to set
     */
    public void setValue(int value) {
        setValue((byte) value);
    }

    /**
     * Returns an array containing the counts of each dice value from 1 to 20.
     * The index 0 corresponds to the count for value 1, index 1 for value 2, and so on.
     *
     * @return an int array with counts of each dice value
     */
    public int[] getValues() {
        int[] amounts = new int[20];
        for (byte b : table) {
            if (b == -1)
                break;
            amounts[b - 1] += 1;
        }
        return amounts;
    }

    /**
     * Returns the player's name.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for the player.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }
}
