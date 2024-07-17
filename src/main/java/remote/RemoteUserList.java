package remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class RemoteUserList extends UnicastRemoteObject implements IUserList {

    private HashMap<String, HashMap<String, Integer>> approve_map = new HashMap<>();
    private HashMap<String, ArrayList<String>> approve_user = new HashMap<>();
    private HashMap<String, ArrayList<String>> users;
    private HashSet<String> boardIDs;
    public RemoteUserList() throws RemoteException {
        System.out.println("called remote list constructor");
        boardIDs = new HashSet<>();
        users = new HashMap<>();
    }

    @Override
    public void addUser(String username, String boardID, boolean isManager) throws RemoteException {
        if (isManager){
            ArrayList<String> user = new ArrayList<>();
            user.add(username);
            this.users.put(boardID, user);
            this.approve_map.put(boardID, new HashMap<>());
            this.approve_user.put(boardID, new ArrayList<>());
            return;
        }
        this.users.get(boardID).add(username);
    }


    @Override
    public ArrayList<String> getUserList(String boardID) throws RemoteException {
        System.out.println("get list of length " + this.users.get(boardID).get(0));
        return this.users.get(boardID);
    }

    @Override
    public void removeUser(String username, String boardID) throws RemoteException {
        System.out.println(username + " left the whiteboard");
        this.users.get(boardID).remove(username);
    }

    @Override
    public void ManagerLeft(String boardID) throws RemoteException {
        System.out.println("Manager has left");
        this.users.get(boardID).clear();
        this.boardIDs.remove(boardID);
    }

    @Override
    public boolean containsBoard(String boardID) throws RemoteException {
        return this.boardIDs.contains(boardID);
    }

    @Override
    public void addBoard(String boardID) throws RemoteException {
        this.boardIDs.add(boardID);
    }

    @Override
    public void wanna_join(String username, String boardID) throws RemoteException{
        System.out.println(username + " wants to join " + boardID);
        this.approve_map.get(boardID).put(username, 0);
        this.approve_user.get(boardID).add(username);
        System.out.println(this.approve_map.get(boardID).get(username));
    }

    @Override
    public String get_join_user(String boardID) throws RemoteException{
        if (!this.approve_user.get(boardID).isEmpty()) return this.approve_user.get(boardID).get(0);
        return "";
    }

    @Override
    public void approved(String username, String boardID, int decision) throws RemoteException{
        this.approve_map.get(boardID).put(username, decision);
        this.approve_user.get(boardID).remove(username);
    }

    @Override
    public int getJoinDecision(String username, String boardID) throws RemoteException{
        return this.approve_map.get(boardID).get(username);
    }

}
