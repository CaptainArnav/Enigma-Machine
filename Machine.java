public class Machine {

    private Rotor r1, r2, r3, ref;
    private PlugBoard plugBoard;

    public Machine() {
        resetRotors();
        plugBoard = new PlugBoard();
    }

    public void resetRotors() {
        r1 = new Rotor();
        r2 = new Rotor();
        r3 = new Rotor();
        ref = new Rotor();

        r1.config("ABCDEFGHIJKLMNOPQRSTUVWXYZ", "DMTWSILRUYQNKFEJCAZBPGXOHV");
        r2.config("ABCDEFGHIJKLMNOPQRSTUVWXYZ", "HQZGPJTMOBLNCIFDYAWVEUSRKX");
        r3.config("ABCDEFGHIJKLMNOPQRSTUVWXYZ", "UQNTLSZFMREHDPXKIBVYGJCWOA");
        ref.config("ABCDEFGHIJKLMNOPQRSTUVWXYZ", "QYHOGNECVPUZTFDJAXWMKISRBL");
    }


    public char enigma(char c) {
        int in = plugBoard.pluggedChar(c) - 'A';
        int out = r1.cipherIn(in);
        out = r2.cipherIn(out);
        out = r3.cipherIn(out);
        out = ref.cipherIn(out);
        out = r3.cipherOut(out);
        out = r2.cipherOut(out);
        out = r1.cipherOut(out);
        
        r1.incLevel();
        r1.incRev();
        if(r1.getRev() == 0) { 
            r2.incLevel();
            r2.incRev(); 
            if(r2.getRev() == 0) { 
                r3.incLevel(); 
                r3.incRev();
            }
        }
        return plugBoard.pluggedChar((char)(out + 'A'));
    }

    public void setCounter1(int n) {
        r1.setCounter(n);
    }
    
    public void setCounter2(int n) {
        r2.setCounter(n);
    }

    public void setCounter3(int n) {
        r3.setCounter(n);
    }

    public int getCounter1() {
        return r1.getCounter();
    }

    public int getCounter2() {
        return r2.getCounter();
    }

    public int getCounter3() {
        return r3.getCounter();
    }
    
    public void setPlugBoard(char c1, char c2) {
        plugBoard.config(c1, c2);
    }
}