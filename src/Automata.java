import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Automata {

    private int n, // # simbolos
            m; // # estados
    private ArrayList<Integer>[][] G;
    private HashMap<String, Integer> symbols = new HashMap<String, Integer>();
    private HashMap<String, Integer> states = new HashMap<String, Integer>();
    private boolean[] finals;

    public Automata(String filename) throws FileNotFoundException {
        Scanner s = new Scanner(new FileReader(filename));
        ArrayList<String> lines = new ArrayList<String>();

        String[] language = s.nextLine().split(",");
        String[] transitionsGroup, transitions;

        for (int i = 0; i < language.length; i++) {
            symbols.put(language[i], i);
        }

        while (s.hasNextLine()) {
            lines.add(s.nextLine());
        }

        m = lines.size();
        n = language.length;
        G = new ArrayList[m][];
        finals = new boolean[m];

        for (int i = 0; i < m; i++) {
            G[i] = new ArrayList[n];

            if (lines.get(i).charAt(0) == '*') {
                finals[i] = true;
                states.put(lines.get(i).substring(1, lines.get(i).indexOf("-")), i);
            } else {
                finals[i] = false;
                states.put(lines.get(i).substring(0, lines.get(i).indexOf("-")), i);
            }
        }

        for (int i = 0; i < m; i++) {
            transitionsGroup = lines.get(i).substring(lines.get(i).indexOf("-") + 1, lines.get(i).length()).split("&");

            for (int j = 0; j < n; j++) {
                transitions = transitionsGroup[j].split(",");
                G[i][j] = new ArrayList();

                for (int k = 0; k < transitions.length; k++) {
                    if (!transitions[k].equals("%")) {
                        G[i][j].add(states.get(transitions[k]));
                    }
                }
            }
        }
    }

    public boolean accepted(String input, int current, int i) {
        boolean a = false;

        if (i == input.length()) {
            return finals[current];
        } else {
            for (int child : G[current][symbols.get(input.charAt(i) + "")]) {
                a |= accepted(input, child, i + 1);
            }
            return a;
        }
    }
}
