package remote;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteWhiteBoard extends Remote {

    void drawText(String text, int x, int y, Color color,String boardID) throws RemoteException;

    void drawLine(int x1, int y1, int x2, int y2, Color color, int strokeWidth, String boardID) throws RemoteException;

    void drawOval(int x1, int y1, int x2, int y2, Color color,String boardID) throws RemoteException;

    void drawCircle(int x1, int y1, int x2, int y2, Color color,String boardID) throws RemoteException;

    void drawRectangle(int x1, int y1, int x2, int y2, Color color, boolean isErase,String boardID) throws RemoteException;

    SerialisedBoard getBoard(String boardID) throws RemoteException;

    void setBoard(SerialisedBoard img,String boardID) throws RemoteException;

    void newBoard(String boardID) throws RemoteException;

    void closeBaord(String boardID) throws RemoteException;

}
