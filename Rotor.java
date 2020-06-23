import java.util.HashMap;

public class Rotor {
    private HashMap<Character, Character> rotorIn;
    private HashMap<Character, Character> rotorOut;
    private HashMap<Integer, Character> levelIn;
    private HashMap<Character, Integer> levelOut;
    private int rev, counter;

    public Rotor() {
        rotorIn = new HashMap<Character, Character>();
        rotorOut = new HashMap<Character, Character>();
        levelIn = new HashMap<Integer, Character>();
        levelOut = new HashMap<Character, Integer>();
        rev = 0;
        counter = 0;
    }

    public void config(String in, String out) {
        for(int i=0; i<26; i++) {
            char c1 = in.charAt(i);
            char c2 = out.charAt(i);
            rotorIn.put(c1, c2);
            rotorOut.put(c2, c1);
        }

        for(int i = 0; i < 26; i++) {
            levelIn.put(i, (char)(i + 'A'));
            levelOut.put((char)(i + 'A'), i);
        }
    }

    public int cipherIn(int in) {
        char c = levelIn.get(in);
        char res = rotorIn.get(c);
        return levelOut.get(res);
    }

    public int cipherOut(int in) {
        char c = levelIn.get(in);
        char res = rotorOut.get(c);
        return levelOut.get(res);
    }

    public void incLevel() {
        for(int i : levelIn.keySet()) {
            int n = levelIn.get(i) - 1;
            if(n < 'A') {n = 'Z';}
            levelIn.replace(i, (char)n);
        }
        for(char c : levelOut.keySet()) {
            levelOut.replace(c, (levelOut.get(c) + 1) % 26);
        }
        counter = (counter + 1) % 26;
    }

    public void setCounter(int n) {
        for (int i = 0; i < n; i++) {
            incLevel();
        }
    }
    
    public void incRev() { rev = (rev + 1) % 26; }

    public int getRev() { return rev; }

    public int getCounter() { return counter; }

}