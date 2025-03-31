import java.util.Arrays;

public class Player {
    String name;
    byte[] table;
    int currentElement;

    public Player(byte[] table, int currentElement, String name){
        this.table = table.clone();
        this.currentElement = currentElement;
        this.name = name;
    }

    public Player(int tableSize, String name){
        this.table = new byte[tableSize];

        Arrays.fill(table, (byte) -1);

        this.currentElement = 0;

        this.name = name;
    }

    public void setValue(byte value){
        if (value > 20 || value < 1) {
            System.err.printf("Value %d, is outside d20 bounds!%n", value);
            value = 1;
        }

        table[currentElement] = value;
        currentElement = (currentElement+1) % table.length;
    }

    public int getCurrentElement() {
        return currentElement;
    }

    public void setTable(byte[] table) {
        this.table = table;
    }

    public byte[] getTable() {
        return table;
    }

    int findLast(){
        if (table[table.length-1] != -1)
            return table.length-1;
        if (table[0]==-1)
            return -1;

        int left = 0;
        int right = table.length - 1;

        while (left < right){
            int m = left + (right - left + 1) / 2;
            if (table[m] != -1){
                left = m;
            }
            else if (table[m] == -1){
                right = m-1;
            }
        }
        return left;
    }

    public void setTableSize(int newSize){
        if (newSize > table.length){
            int last = findLast();

            byte[] oldTable = table.clone();

            this.table = new byte[newSize];
            Arrays.fill(table, (byte) -1);

            System.arraycopy(oldTable, 0, table, 0, oldTable.length);

            if (last == -1) currentElement = 0;
            else currentElement = last+1;
        } else if (newSize < table.length) {
            int last = findLast();

            byte[] oldTable = table.clone();

            this.table = new byte[newSize];

            System.arraycopy(oldTable, 0, table, 0, table.length);

            if (last == -1) currentElement = 0;
            else currentElement = Math.min(last+1, table.length-1);
        }
    }

    public void setValue(int value){
        setValue((byte) value);
    }

    public int[] getValues(){
        int[] amounts = new int[20];
        for (byte b : table) {
            if (b == -1) break;
            amounts[b - 1] += 1;
        }

        return amounts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
