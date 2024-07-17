package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class Palette extends JPanel implements ActionListener{

    private final Color DEFAULT_COLOR = Color.black;
    private Color color = DEFAULT_COLOR;

    final Color[] COLORS = {Color.RED, Color.black, Color.blue, Color.darkGray,
            Color.gray, Color.green, Color.yellow,Color.lightGray,
            Color.pink, Color.cyan, Color.orange, Color.magenta};
    JButton redBtn = new JButton();
    JButton blackBtn = new JButton();
    JButton blueBtn = new JButton();
    JButton darkgreyBtn = new JButton();
    JButton grayBtn = new JButton();
    JButton greenBtn = new JButton();
    JButton yellowBtn = new JButton();
    JButton lightgreyBtn = new JButton();
    JButton pinkBtn = new JButton();
    JButton cyanBtn = new JButton();
    JButton orangeBtn = new JButton();
    JButton magentaBtn = new JButton();

    JButton colorBtn;

    final JButton[] BUTTONS = {redBtn, blackBtn, blueBtn, darkgreyBtn,
            grayBtn, greenBtn, yellowBtn, lightgreyBtn,
            pinkBtn, cyanBtn, orangeBtn, magentaBtn};


    Palette(){
        this.setLayout(new GridLayout(2, 6, 10, 10));
        createBtn();
    }

    private void createBtn(){
        for(int i = 0; i < 12; i++){
            BUTTONS[i].setBackground(COLORS[i]);
            BUTTONS[i].setPreferredSize(new Dimension(28,28));
            BUTTONS[i].addActionListener(this);
            this.add(BUTTONS[i]);
        }
    }


    public Color getColor(){
        return this.color;
    }

    public void setColorBtn(JButton colorBtn){this.colorBtn = colorBtn;}

    public void resetColor(){
        this.color = DEFAULT_COLOR;
        this.colorBtn.setBackground(DEFAULT_COLOR);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        int idx = Arrays.asList(BUTTONS).indexOf(source);
        this.color = COLORS[idx];
        this.colorBtn.setBackground(this.color);
    }
}
