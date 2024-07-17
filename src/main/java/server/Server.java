package server;

import remote.IRemoteWhiteBoard;
import remote.IUserList;
import remote.RemoteUserList;
import remote.RemoteWhiteBoard;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) {

        try {
            IRemoteWhiteBoard board = new RemoteWhiteBoard();
            IUserList userList = new RemoteUserList();

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("whiteboard", board);
            registry.bind("userlist", userList);
            System.out.println("RMI ready");

        } catch(RemoteException re){
            System.out.println("something went wrong when binding");
        } catch (Exception e){
            System.out.println("exception");
        }

    }
}
