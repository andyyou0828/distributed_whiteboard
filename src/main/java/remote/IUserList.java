package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IUserList extends Remote {

    void addUser(String username, String boardID, boolean isManager) throws RemoteException;

    ArrayList<String> getUserList(String boardID) throws RemoteException;

    void removeUser(String username, String boardID) throws RemoteException;

    void ManagerLeft(String boardID) throws RemoteException;

    boolean containsBoard(String boardID) throws RemoteException;

    void addBoard(String boardID) throws RemoteException;

    void wanna_join(String username, String boardID) throws RemoteException;

    String get_join_user(String boardID) throws RemoteException;

    void approved(String username, String boardID, int decision) throws RemoteException;

    int getJoinDecision(String username, String boardID) throws RemoteException;

}