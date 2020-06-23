import java.util.HashMap;

public class PlugBoard {

    HashMap<Character, Character> board;

    public PlugBoard() {
        board = new HashMap<Character, Character>();
    }

    public void config(char c1, char c2) {
        board.put(c1, c2);
        board.put(c2, c1);
    }

    public char pluggedChar(char c) {
        return board.getOrDefault(c, c);
    }
}