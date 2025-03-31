import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        DiceAnalyzer analyzer = DiceAnalyzer.INSTANCE;

        // Name of your save file
        File saveFile = new File("players.txt");

        if (!saveFile.exists()) {
            System.out.println("Save file does not exist!");
            // Add players manually
            analyzer.addPlayer("Vova");

            // Table Size is the amount of data points (throws)
            analyzer.setTableSize(10);

            Player vova = analyzer.getPlayer(0);

            // Add a value to a player
            vova.setValue(18);

            // Print Players for Debugging
            System.out.println(analyzer);

            // Save Players
            analyzer.savePlayers(saveFile);
        }
        else {
            // Load Players
            analyzer.loadPlayers(saveFile);

            System.out.println("Save file does exist!");

            // Print Players for Debugging
            System.out.println(analyzer);

            // Get Vova
            Player vova = analyzer.getPlayer("Vova");

            // Add a value to Vova
            vova.setValue(1);

            // We can also add with the index
            analyzer.getPlayer(0).setValue(2);
            // Or with the name
            analyzer.getPlayer("Vova").setValue(1);

            analyzer.setTableSize(20);

            for (int i = 1; i < 21; i++){
                vova.setValue(i);
            }

            System.out.println(Arrays.toString(vova.getValues()));

            // Print Players for Debugging
            System.out.println(analyzer);

            // Let's save Players!
            analyzer.savePlayers(saveFile);

            System.out.println(analyzer.doesCheat(vova));
        }
    }
}