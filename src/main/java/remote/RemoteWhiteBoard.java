package remote;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class RemoteWhiteBoard extends UnicastRemoteObject implements IRemoteWhiteBoard {

    // private HashMap<String, BufferedImage> boards = new HashMap<>();
    // private BufferedImage board;
    // private SerialisedBoard board;

    private HashMap<String, SerialisedBoard> boards;
    private HashMap<String, Boolean> isClosed = new HashMap<>();
    public RemoteWhiteBoard() throws RemoteException {
        System.out.println("called constructor");
        // this.board = new SerialisedBoard();
        this.boards = new HashMap<>();
    }



    @Override
    public void drawText(String text, int x, int y, Color color,String boardID) throws RemoteException {
        if (this.isClosed.get(boardID)) return;
        Graphics2D g2D = (Graphics2D) this.boards.get(boardID).getImg().getGraphics();
        g2D.setColor(color);
        g2D.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        g2D.drawString(text, x, y);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2, Color color, int strokeWidth,String boardID) throws RemoteException {
        if (this.isClosed.get(boardID)) return;
        Graphics2D g2D = (Graphics2D) this.boards.get(boardID).getImg().getGraphics();
        g2D.setPaint(color);
        g2D.setStroke(new BasicStroke(strokeWidth));
        g2D.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawOval(int x1, int y1, int x2, int y2, Color color,String boardID) throws RemoteException {
        if (this.isClosed.get(boardID)) return;
        Graphics2D g2D = (Graphics2D) this.boards.get(boardID).getImg().getGraphics();
        g2D.setPaint(color);
        g2D.setStroke(new BasicStroke(3));
        int startX = Math.min(x1, x2);
        int startY = Math.min(y1, y2);
        int endX = Math.max(x1, x2);
        int endY = Math.max(y1, y2);
        g2D.drawOval(startX, startY, endX - startX, endY - startY);

    }

    @Override
    public void drawCircle(int x1, int y1, int x2, int y2, Color color,String boardID) throws RemoteException {
        if (this.isClosed.get(boardID)) return;
        Graphics2D g2D = (Graphics2D) this.boards.get(boardID).getImg().getGraphics();
        g2D.setPaint(color);
        g2D.setStroke(new BasicStroke(3));
        int startX = Math.min(x1, x2);
        int startY = Math.min(y1, y2);
        int endX = Math.max(x1, x2);
        int endY = Math.max(y1, y2);
        int radius = Math.min(endX - startX, endY - startY);
        g2D.drawOval(startX, startY, radius, radius);
    }

    @Override
    public void drawRectangle(int x1, int y1, int x2, int y2, Color color, boolean erase,String boardID) throws RemoteException {
        if (this.isClosed.get(boardID)) return;
        Graphics2D g2D = (Graphics2D) this.boards.get(boardID).getImg().getGraphics();
        int strokeWidth = (erase) ? 10 : 3;
        g2D.setPaint(color);
        g2D.setStroke(new BasicStroke(strokeWidth));
        int startX = Math.min(x1, x2);
        int startY = Math.min(y1, y2);
        int endX = Math.max(x1, x2);
        int endY = Math.max(y1, y2);
        if (erase) g2D.fillRect(startX, startY, endX - startX, endY - startY);
        else g2D.drawRect(startX, startY, endX - startX, endY - startY);
    }

    @Override
    public SerialisedBoard getBoard(String boardID) throws RemoteException {
        return this.boards.get(boardID);
    }

    @Override
    public void newBoard(String boardID) throws RemoteException {
        this.boards.put(boardID, new SerialisedBoard());
        this.isClosed.put(boardID, false);
    }

    @Override
    public void closeBaord(String boardID) throws RemoteException {
        this.isClosed.put(boardID, true);
        this.boards.put(boardID, new SerialisedBoard(true));
    }

    @Override
    public void setBoard(SerialisedBoard img,String boardID) throws RemoteException {
        this.boards.put(boardID, img);
    }

}
