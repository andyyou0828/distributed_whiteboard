package client;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Shapes extends JPanel implements ActionListener {

    JButton circleBtn = new JButton();
    JButton lineBtn = new JButton();
    JButton mouseBtn = new JButton();
    JButton ovalBtn = new JButton();
    JButton rectBtn = new JButton();
    JButton textBtn = new JButton();

    private String shape = null;
    private boolean isErase = false;

    private JLabel shapeLabel;

    JButton[] SHAPE_BTNS = {circleBtn, lineBtn, mouseBtn, ovalBtn, rectBtn, textBtn};

    private final String[] SHAPES = {"circle","line","mouse","oval","rectangle","text"};


    Shapes(){

        this.setLayout(new GridLayout(2, 3, 10,10));
        setImageIcon();
    }

    private void setImageIcon(){
        BufferedImage cc = null, li = null, ms = null, ov = null, rec = null, txt = null;
        try {
            cc = ImageIO.read(new File("D:/java/projects/ds_a2/icon/circle.png"));
            li = ImageIO.read(new File("D:/java/projects/ds_a2/icon/line.png"));
            ms = ImageIO.read(new File("D:/java/projects/ds_a2/icon/mouse.png"));
            ov = ImageIO.read(new File("D:/java/projects/ds_a2/icon/oval.png"));
            rec = ImageIO.read(new File("D:/java/projects/ds_a2/icon/rectangle.png"));
            txt = ImageIO.read(new File("D:/java/projects/ds_a2/icon/text.png"));
        } catch (IOException e) {
            System.out.println("Error when loading the shape files.");
            // e.printStackTrace();
        }

        Image img;
        BufferedImage[] imgs = {cc, li, ms, ov, rec, txt};
        for(int i = 0; i < 6; i++){
            img = (imgs[i] != ov) ? imgs[i].getScaledInstance(22,22,Image.SCALE_SMOOTH)
                    : imgs[i].getScaledInstance(20,24,Image.SCALE_SMOOTH);
            SHAPE_BTNS[i].setIcon(new ImageIcon(img));
            SHAPE_BTNS[i].setPreferredSize(new Dimension(28, 28));
            SHAPE_BTNS[i].addActionListener(this);
            this.add(SHAPE_BTNS[i]);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        int idx = Arrays.asList(SHAPE_BTNS).indexOf(source);
        this.shape = SHAPES[idx];
        this.shapeLabel.setText("Current Shape: " + this.shape);
        this.isErase = false;
    }

    public String getShape(){
        return this.shape;
    }

    public void setShape(String shape){

        this.shape = shape;
        this.shapeLabel.setText("Current Shape: " + this.shape);
    }

    public void setErase(){
        this.isErase = true;
    }

    public boolean getErase(){
        return this.isErase;
    }

    public void setShapeLabel (JLabel shapeLabel){this.shapeLabel = shapeLabel;}
}
