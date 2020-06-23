import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class Enigma {

    private Machine machine;

    private int prev = -1; // keeps track of previous code output which turned yellow

    private Stack<Color> s = new Stack<Color>(); // different colors for each pair connections of the plug board

    private int pair = 0; // used to keep track of pair wise connections of the plug board
    private char[] pairConfig = new char[2]; // store the pair connections of the plug board

    private boolean stopSlider = false; //to stop the the counter getting updated if the slider is moved from required position

    private JTextField rotor1Text, rotor2Text, rotor3Text;
    private JSlider rotor1, rotor2, rotor3;
    private JButton reset;

    private List<CodeCircle> codeCircles;
    private HashMap<Character, Integer> codeCircleMap;

    private List<RoundButton> buttons;

    private List<PlugButton> plugButtons;

    public Enigma() {
        machine = new Machine();
    }

    /**
     * class representing the output letters/buttton of the inputted code
     */
    @SuppressWarnings("serial")
    private class CodeCircle extends JButton {
        private Color color = new Color(230, 229, 229);

        public CodeCircle(String label) {
            super(label);
            Dimension size = getPreferredSize();
            size.width = size.height = Math.max(size.width, size.height);
            setPreferredSize(size);
            setContentAreaFilled(false);
        }

        public void paintComponent(Graphics g) {
            g.setColor(color);
            g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
            super.paintComponent(g);
        }

        public void paintBorder(Graphics g) {
            g.setColor(getForeground());
            g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
        }

        public void setYellow() {
            color = Color.YELLOW;
        }

        public void removeYellow() {
            color = new Color(230, 229, 229);
        }
    }

    /**
     * class representing the input buttons for thr code
     */
    @SuppressWarnings("serial")
    private class RoundButton extends JButton {
        public RoundButton(String label) {
            super(label);
            Dimension size = getPreferredSize();
            size.width = size.height = Math.max(size.width, size.height);
            setPreferredSize(size);
            setContentAreaFilled(false);
        }

        public void paintComponent(Graphics g) {
            g.setColor(new Color(180, 177, 177));
            g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
            super.paintComponent(g);
        }

        public void paintBorder(Graphics g) {
            g.setColor(getForeground());
            g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
        }
    }

    /**
     * class representing the plug board connections.
     */
    @SuppressWarnings("serial")
    private class PlugButton extends JButton {
        private boolean flag = false;
        private Color color = new Color(180, 177, 177);

        public PlugButton(String label) {
            super(label);
            Dimension size = getPreferredSize();
            size.width = size.height = Math.max(size.width, size.height);
            setPreferredSize(size);
            setContentAreaFilled(false);
        }

        public void paintComponent(Graphics g) {
            g.setColor(color);
            g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
            super.paintComponent(g);
        }

        public void paintBorder(Graphics g) {
            g.setColor(getForeground());
            g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
        }

        public boolean hasColor() {
            return flag;
        }

        public void addColor() {
            flag = true;
            color = s.isEmpty() ? color : s.pop();
        }
    }

    public void addRotors(JPanel rotorText, JPanel rotor) {
        reset = new JButton("RESET");
        reset.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                machine.resetRotors();
                stopSlider = false;
                rotor1.setValue(0);
                rotor2.setValue(0);
                rotor3.setValue(0);
                if (prev != -1) {
                    codeCircles.get(prev).removeYellow();
                    codeCircles.get(prev).repaint();
                    prev = -1;
                }
            }

        });

        rotor1Text = new JTextField("0", 2);
        rotor1Text.setMargin(new Insets(5, 5, 5, 5));
        rotor1Text.setEditable(false);
        rotor2Text = new JTextField("0", 2);
        rotor2Text.setMargin(new Insets(5, 5, 5, 5));
        rotor2Text.setEditable(false);
        rotor3Text = new JTextField("0", 2);
        rotor3Text.setMargin(new Insets(5, 5, 5, 5));
        rotor3Text.setEditable(false);

        rotorText.add(rotor3Text);
        rotorText.add(Box.createRigidArea(new Dimension(20, 5)));
        rotorText.add(rotor2Text);
        rotorText.add(Box.createRigidArea(new Dimension(20, 5)));
        rotorText.add(rotor1Text);

        rotor1 = new JSlider(0, 25, 0);
        rotor1.setMinorTickSpacing(5);
        rotor1.setPaintTicks(true);
        rotor1.setOrientation(JSlider.VERTICAL);
        rotor1.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int val = rotor1.getValue();
                rotor1Text.setText(Integer.toString(val));
            }
        });

        rotor2 = new JSlider(0, 25, 0);
        rotor2.setMinorTickSpacing(5);
        rotor2.setPaintTicks(true);
        rotor2.setOrientation(JSlider.VERTICAL);
        rotor2.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int val = rotor2.getValue();
                rotor2Text.setText(Integer.toString(val));
            }
        });

        rotor3 = new JSlider(0, 25, 0);
        rotor3.setMinorTickSpacing(5);
        rotor3.setPaintTicks(true);
        rotor3.setOrientation(JSlider.VERTICAL);
        rotor3.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int val = rotor3.getValue();
                rotor3Text.setText(Integer.toString(val));
            }
        });

        // reverse order according to the machine layout
        rotor.add(Box.createRigidArea(new Dimension(120, 5)));
        rotor.add(rotor3);
        rotor.add(Box.createRigidArea(new Dimension(20, 5)));
        rotor.add(Box.createRigidArea(new Dimension(20, 5)));
        rotor.add(rotor2);
        rotor.add(Box.createRigidArea(new Dimension(20, 5)));
        rotor.add(Box.createRigidArea(new Dimension(20, 5)));
        rotor.add(rotor1);
        rotor.add(Box.createRigidArea(new Dimension(20, 5)));
        rotor.add(Box.createRigidArea(new Dimension(20, 5)));
        rotor.add(reset);
    }

    public void addCodeCircles(JPanel code1, JPanel code2, JPanel code3) {

        codeCircles = new ArrayList<CodeCircle>();
        createCodeCircle(codeCircles);
        codeCircleMap = new HashMap<Character, Integer>();

        // for disabled buttons -- text color
        UIManager.getDefaults().put("Button.disabledText", Color.BLACK);

        for (int i = 0; i < 9; i++) {
            codeCircles.get(i).setEnabled(false);
            code1.add(codeCircles.get(i));
            code1.add(Box.createRigidArea(new Dimension(20, 5)));
            char c = codeCircles.get(i).getText().charAt(0);
            codeCircleMap.put(c, i);
        }
        for (int i = 9; i < 17; i++) {
            codeCircles.get(i).setEnabled(false);
            code2.add(codeCircles.get(i));
            code2.add(Box.createRigidArea(new Dimension(20, 5)));
            char c = codeCircles.get(i).getText().charAt(0);
            codeCircleMap.put(c, i);
        }
        for (int i = 17; i < 26; i++) {
            codeCircles.get(i).setEnabled(false);
            code3.add(codeCircles.get(i));
            code3.add(Box.createRigidArea(new Dimension(20, 5)));
            char c = codeCircles.get(i).getText().charAt(0);
            codeCircleMap.put(c, i);
        }

    }

    public void addRoundButtons(JPanel letter1, JPanel letter2, JPanel letter3) {
        buttons = new ArrayList<RoundButton>();
        createRoundButton(buttons);

        for (int i = 0; i < 9; i++) {
            buttons.get(i).addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!stopSlider) {
                        stopSlider = true;
                        machine.setCounter1(rotor1.getValue());
                        machine.setCounter2(rotor2.getValue());
                        machine.setCounter3(rotor3.getValue());
                    }
                    String s = e.getActionCommand();
                    if (prev != -1) {
                        codeCircles.get(prev).removeYellow();
                        codeCircles.get(prev).repaint();
                    }
                    char c = machine.enigma(s.charAt(0));
                    codeCircles.get(codeCircleMap.get(c)).setYellow();
                    codeCircles.get(codeCircleMap.get(c)).repaint();
                    prev = codeCircleMap.get(c);
                    int counter1 = machine.getCounter1();
                    int counter2 = machine.getCounter2();
                    int counter3 = machine.getCounter3();
                    rotor1.setValue(counter1);
                    rotor2.setValue(counter2);
                    rotor3.setValue(counter3);
                    rotor1Text.setText(Integer.toString(counter1));
                    rotor2Text.setText(Integer.toString(counter2));
                    rotor3Text.setText(Integer.toString(counter3));
                }

            });
            letter1.add(buttons.get(i));
            letter1.add(Box.createRigidArea(new Dimension(20, 5)));
        }
        for (int i = 9; i < 17; i++) {
            buttons.get(i).addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!stopSlider) {
                        stopSlider = true;
                        machine.setCounter1(rotor1.getValue());
                        machine.setCounter2(rotor2.getValue());
                        machine.setCounter3(rotor3.getValue());
                    }
                    String s = e.getActionCommand();
                    if (prev != -1) {
                        codeCircles.get(prev).removeYellow();
                        codeCircles.get(prev).repaint();
                    }
                    char c = machine.enigma(s.charAt(0));
                    codeCircles.get(codeCircleMap.get(c)).setYellow();
                    codeCircles.get(codeCircleMap.get(c)).repaint();
                    prev = codeCircleMap.get(c);
                    int counter1 = machine.getCounter1();
                    int counter2 = machine.getCounter2();
                    int counter3 = machine.getCounter3();
                    rotor1.setValue(counter1);
                    rotor2.setValue(counter2);
                    rotor3.setValue(counter3);
                    rotor1Text.setText(Integer.toString(counter1));
                    rotor2Text.setText(Integer.toString(counter2));
                    rotor3Text.setText(Integer.toString(counter3));
                }

            });
            letter2.add(buttons.get(i));
            letter2.add(Box.createRigidArea(new Dimension(20, 5)));
        }
        for (int i = 17; i < 26; i++) {
            buttons.get(i).addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!stopSlider) {
                        stopSlider = true;
                        machine.setCounter1(rotor1.getValue());
                        machine.setCounter2(rotor2.getValue());
                        machine.setCounter3(rotor3.getValue());
                    }
                    String s = e.getActionCommand();
                    if (prev != -1) {
                        codeCircles.get(prev).removeYellow();
                        codeCircles.get(prev).repaint();
                    }
                    char c = machine.enigma(s.charAt(0));
                    codeCircles.get(codeCircleMap.get(c)).setYellow();
                    codeCircles.get(codeCircleMap.get(c)).repaint();
                    prev = codeCircleMap.get(c);
                    int counter1 = machine.getCounter1();
                    int counter2 = machine.getCounter2();
                    int counter3 = machine.getCounter3();
                    rotor1.setValue(counter1);
                    rotor2.setValue(counter2);
                    rotor3.setValue(counter3);
                    rotor1Text.setText(Integer.toString(counter1));
                    rotor2Text.setText(Integer.toString(counter2));
                    rotor3Text.setText(Integer.toString(counter3));
                }

            });
            letter3.add(buttons.get(i));
            letter3.add(Box.createRigidArea(new Dimension(20, 5)));
        }
    }

    public void addPlugButtons(JPanel plugBoardButton) {

        plugButtons = new ArrayList<PlugButton>();
        createPlugButton(plugButtons);

        for (int i = 0; i < 10; i++) {
            Color c = new Color((int) (256 * Math.random()), (int) (256 * Math.random()), (int) (256 * Math.random()));
            s.push(c);
            s.push(c);
        }

        for (int i = 0; i < 26; i++) {
            plugButtons.get(i).addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    PlugButton plug = (PlugButton) e.getSource();
                    if (!s.isEmpty() && !plug.hasColor()) {
                        plug.addColor();
                        plug.repaint();
                        pairConfig[pair] = plug.getText().charAt(0);
                        if (pair == 1) {
                            machine.setPlugBoard(pairConfig[0], pairConfig[1]);
                        }
                        pair = (pair + 1) % 2;
                    }
                }

            });
            plugBoardButton.add(plugButtons.get(i));
            plugBoardButton.add(Box.createRigidArea(new Dimension(2, 2)));
        }
    }

    public void createCodeCircle(List<CodeCircle> codeCircles) {
        String[] sequence = { "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "A", "S", "D", "F", "G", "H", "J", "K", "P",
                "Z", "X", "C", "V", "B", "N", "M", "L" };

        for (String s : sequence) {
            CodeCircle codeCircle = new CodeCircle(s);
            codeCircles.add(codeCircle);
        }
    }

    public void createRoundButton(List<RoundButton> buttons) {
        String[] sequence = { "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "A", "S", "D", "F", "G", "H", "J", "K", "P",
                "Z", "X", "C", "V", "B", "N", "M", "L" };
        for (String s : sequence) {
            RoundButton button = new RoundButton(s);
            buttons.add(button);
        }
    }

    public void createPlugButton(List<PlugButton> plugButtons) {
        for (char i = 'A'; i <= 'Z'; i++) {
            PlugButton button = new PlugButton(Character.toString(i));
            plugButtons.add(button);
        }
    }


    public static void main(String[] args) {
        Enigma enigma = new Enigma();

        // parent container
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // container for all CodeCirles which is the code output
        JPanel code = new JPanel();
        code.setLayout(new BoxLayout(code, BoxLayout.Y_AXIS));
        code.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel code1 = new JPanel();
        JPanel code2 = new JPanel();
        JPanel code3 = new JPanel();

        code1.setLayout(new BoxLayout(code1, BoxLayout.X_AXIS));
        code1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        code2.setLayout(new BoxLayout(code2, BoxLayout.X_AXIS));
        code2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        code3.setLayout(new BoxLayout(code3, BoxLayout.X_AXIS));
        code3.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        enigma.addCodeCircles(code1, code2, code3);
        // codeCircles end


        // container with all the text fields for the rotor positions
        JPanel rotorText = new JPanel();
        rotorText.setLayout(new FlowLayout(FlowLayout.CENTER));

        // container with all the sliders to set the position of the rotors
        JPanel rotor = new JPanel();
        rotor.setLayout(new BoxLayout(rotor, BoxLayout.X_AXIS));
        rotor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        enigma.addRotors(rotorText, rotor);
        // rotors end

        // container for all RoundButtons which is the code input
        JPanel button = new JPanel();
        button.setLayout(new BoxLayout(button, BoxLayout.Y_AXIS));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel letter1 = new JPanel();
        JPanel letter2 = new JPanel();
        JPanel letter3 = new JPanel();

        letter1.setLayout(new BoxLayout(letter1, BoxLayout.X_AXIS));
        letter1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        letter2.setLayout(new BoxLayout(letter2, BoxLayout.X_AXIS));
        letter2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        letter3.setLayout(new BoxLayout(letter3, BoxLayout.X_AXIS));
        letter3.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        enigma.addRoundButtons(letter1, letter2, letter3);
        // end RoundButtons

        // container for all PLugButtons which represents the plug board
        JPanel plugBoardButton = new JPanel();
        plugBoardButton.setLayout(new BoxLayout(plugBoardButton, BoxLayout.X_AXIS));
        plugBoardButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        enigma.addPlugButtons(plugBoardButton);
        // end PlugButtons

        code.add(code1);
        code.add(code2);
        code.add(code3);

        button.add(letter1);
        button.add(letter2);
        button.add(letter3);

        content.add(rotor);
        content.add(rotorText);
        content.add(code);
        content.add(button);
        content.add(plugBoardButton);

        JFrame window = new JFrame("Enigma");
        window.setContentPane(content);
        window.setSize(1200, 700);
        window.setLocation(30, 30);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

    }
}