package remote;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

public class SerialisedBoard implements Serializable {

    private BufferedImage img;
    private final int WIDTH = 1000;
    private final int HEIGHT = 690;

    private final BufferedImage NULL_BOARD = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

    SerialisedBoard(){
        this.img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    }

    public SerialisedBoard(BufferedImage img){
        this.img = img;
    }

    SerialisedBoard(boolean isClosed){
        setNullBoard();
        this.img = this.NULL_BOARD;
    }

    private void setNullBoard(){
        Graphics2D g2D = (Graphics2D) this.NULL_BOARD.getGraphics();
        g2D.setBackground(Color.gray);
        g2D.setColor(Color.black);
        g2D.setFont(new Font("TimesRoman", Font.PLAIN, 30));
        g2D.drawString("The manager has closed the whiteboard.", 250, 250);
    }

    public BufferedImage getImg(){
        return this.img;
    }


    private void writeObject(java.io.ObjectOutputStream stream) throws IOException {
        ImageIO.write(this.img, "png", stream);
    }

    private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        this.img = ImageIO.read(stream);
    }
}
