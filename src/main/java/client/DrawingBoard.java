package client;

import remote.IRemoteWhiteBoard;
import remote.SerialisedBoard;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/* The effect of calling repaint() is that Swing automatically clears the graphic on
the panel and executes the paintComponent method to redraw the graphics on this panel.
 */

public class DrawingBoard extends JPanel{
    private Palette palette;
    private Shapes shapes = null;
    private final int LEFT = 200;
    private final int TOP = 110;
    private final int WIDTH = 1000;
    private final int HEIGHT = 690;

    private IRemoteWhiteBoard board = null;
    private BufferedImage curImg;


    private int xbegin = 0, ybegin = 0, xend = 0, yend = 0;

    private String saveName = null;
    private String boardID;
    private int closeTimer = 0;

    DrawingBoard(Palette palette, Shapes shapes, String boardID){

        this.palette = palette;
        this.shapes = shapes;
        this.boardID = boardID;
        this.setBounds(LEFT, TOP, WIDTH, HEIGHT);
        this.addMouseListener(mlistener);
        this.addMouseMotionListener(mmlistener);


        try {
            Registry registry = LocateRegistry.getRegistry();
            this.board = (IRemoteWhiteBoard) registry.lookup("whiteboard");
            System.out.println("successfully look up");

        } catch (Exception e) {
            System.out.println("Exception Happened");
        }


    }


    public MouseListener mlistener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {

            if (!hasShape()) return;

            xbegin = xend = e.getX();
            ybegin = yend = e.getY();

        }

        @Override
        public void mouseClicked(MouseEvent e) {

            // if (!hasShape()) return;

            if (isText()){
                System.out.println("check is text");
                String text = JOptionPane.showInputDialog("input your text");
                if (!text.isEmpty()){
                    drawText(text, e.getX(), e.getY());
                }
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            // if (!hasShape()) return;

            xend = e.getX();
            yend = e.getY();
            repaint();
            drawShapes(xbegin, ybegin, xend, yend);

        }
    };

    public MouseMotionListener mmlistener = new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {

            // if (!hasShape()) return;

            xend = e.getX();
            yend = e.getY();
            if (isMouseDraw()){
                mouseDraw(xend, yend);
            }



        }
    };

    public synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {

            this.curImg = this.board.getBoard(this.boardID).getImg();
            g.drawImage(this.curImg, 0, 0, null);

        } catch (RemoteException e) {
            System.out.println("fail");
            System.out.println(e.getMessage());
        }
    }


    private void drawShapes(int x1, int y1, int x2, int y2){
        switch(this.shapes.getShape()){
            case "circle":
                drawCircle(x1, y1, x2, y2);
                break;

            case "line":
                drawLine(x1, y1, x2, y2);
                break;

            case "oval":
                drawOval(x1, y1, x2, y2);
                break;

            case "rectangle":
                drawRectangle(x1, y1, x2, y2);
                break;

            case "mouse":
            case "text":
                break;

            default:
                System.out.println("No such choice");
                break;
        }
    }

    private boolean isMouseDraw(){
        return this.shapes.getShape().equals("mouse");
    }

    private boolean isText(){
        return this.shapes.getShape().equals("text");
    }

    private boolean hasShape(){

        if (this.shapes.getShape() != null){
            return true;
        }

        JOptionPane.showMessageDialog(null, "Please select a shape first");
        return false;
    }

    public void drawText(String text, int x, int y){
        try {
            System.out.println("call draw text");
            this.board.drawText(text, x, y, this.palette.getColor(), this.boardID);
        } catch (RemoteException e) {
            System.out.println("Remote Exception happened");
        }

    }

    public void mouseDraw(int x, int y){
        drawLine(xbegin, ybegin, x, y);
        xbegin = x; ybegin = y;
    }

    public void drawLine(int x1, int y1, int x2, int y2){
        boolean erase = this.shapes.getErase();

        try {
            if (erase) this.board.drawLine(x1, y1, x2, y2, this.getBackground(), 10, this.boardID);
            else this.board.drawLine(x1, y1, x2, y2, this.palette.getColor(), 3, this.boardID);
        } catch(RemoteException e){
            System.out.println("Remote Exception happened");
        }

    }

    public void drawOval(int x1, int y1, int x2, int y2){

        try {
            this.board.drawOval(x1, y1, x2, y2, this.palette.getColor(), this.boardID);
        } catch (RemoteException e) {
            System.out.println("Remote Exception happened");
        }

    }

    public void drawCircle(int x1, int y1, int x2, int y2){

        try {
            this.board.drawCircle(x1, y1, x2, y2, this.palette.getColor(), this.boardID);
        } catch (RemoteException e) {
            System.out.println("Remote Exception happened");
        }

    }

    public void drawRectangle(int x1, int y1, int x2, int y2){
        boolean erase = this.shapes.getErase();

        try {
            if (erase) this.board.drawRectangle(x1, y1, x2, y2, this.getBackground(), erase, this.boardID);
            else this.board.drawRectangle(x1, y1, x2, y2, this.palette.getColor(), erase, this.boardID);

        } catch(RemoteException e){
            System.out.println("Remote Exception happened");
        }

    }

    public Shapes getShapes(){
        return this.shapes;
    }

    public void save(){
        // first time save acts as saveAs
        if (this.saveName == null) {
            saveAs();
            return;
        }

        // already saved just save to the same file
        BufferedImage saveImg = getImage();
        try {
            ImageIO.write(saveImg, "PNG", new File(this.saveName));
            System.out.printf("%s is saved Successfully!%n", this.saveName);
        } catch (IOException e) {
            System.out.println("Save Failed...");
        }


    }

    private BufferedImage getImage(){

        BufferedImage saveImg = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics g = saveImg.getGraphics();

        try {
            g.drawImage(this.board.getBoard(this.boardID).getImg(), 0, 0, null);
        } catch (RemoteException e){
            System.out.println("Remote Exception happened");
        }

        g.dispose();
        return saveImg;

    }

    public void saveAs() {

        BufferedImage saveImg = getImage();
        JFileChooser chooser = new JFileChooser("D:\\java\\projects\\ds_a2");
        int choice = chooser.showSaveDialog(null);

        if (choice == JFileChooser.APPROVE_OPTION){
            String filename = chooser.getSelectedFile().getPath();
            File file = new File(filename);
            try {
                ImageIO.write(saveImg, "PNG", file);
                System.out.printf("%s is saved Successfully!%n", file);
                this.saveName = filename;
            } catch (IOException e) {
                System.out.println("Save Failed...");
            }
        }
    }


    public void load(){

        JFileChooser chooser = new JFileChooser("D:\\java\\projects\\ds_a2");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "JPG & GIF & PNG Images", "jpg", "gif", "png");
        chooser.setFileFilter(filter);
        int choice = chooser.showOpenDialog(null);

        if (choice == JFileChooser.APPROVE_OPTION){
            File file = chooser.getSelectedFile();
            try {
                this.board.setBoard(new SerialisedBoard(ImageIO.read(file)), this.boardID);
                // this.img = ImageIO.read(file);
                System.out.printf("%s is loaded Successfully.%n", file.getName());
            } catch (IOException e) {
                System.out.println("Load Failed...");
            }
            repaint();
        }

    }

    public void close(){
        try {
            // this.board.clearBoard(this.getBackground(), this.boardID);
            this.board.closeBaord(this.boardID);
        } catch(RemoteException e){
            System.out.println("Clear error");
        }

        // this.shapes.setShape(null);
        repaint();
    }


    public void newBoard(){
        try {
            this.board.newBoard(this.boardID);
        } catch(RemoteException e){
            System.out.println("New error");
        }
        this.shapes.setShape(null);
        repaint();
    }

}