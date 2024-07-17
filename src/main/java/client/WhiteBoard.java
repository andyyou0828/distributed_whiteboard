package client;

import remote.IUserList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class WhiteBoard extends JFrame implements ActionListener, WindowListener {


    private DrawingBoard drawingPanel;

    private String username = null;

    private boolean isManager;

    private Palette palette;

    private Shapes shapes;
    JButton saveBtn = new JButton("Save");
    JButton saveAsBtn = new JButton("Save As");
    JButton loadBtn = new JButton("Load");
    JButton newBtn = new JButton("New");
    JButton closeBtn = new JButton("Close");

    JButton mouseEraseBtn = new JButton("Erase by Mouse");
    JButton selectEraseBtn = new JButton("Erase by Select");

    private final String FILENAME_PROMPT = "input the filename";

    private final JScrollPane scrollPane = new JScrollPane();
    private JList<String> list = new JList<>();
    private ArrayList<String> users;

    private String kickedUsername;

    private String boardID;
    private IUserList userlist;

    private Thread updateThread;
    private final int UPDATE_INTERVAL = 1500;



    public WhiteBoard(String username, boolean isManager, String boardID, IUserList userlist){

        this.username = username;
        this.boardID = boardID;
        this.isManager = isManager;
        this.userlist = userlist;


        this.setTitle("Distributed White Board App");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(1200, 800);
        this.setLayout(null);
        this.setResizable(false);
        this.setLocationRelativeTo(null);


        // Separation Panel
        JPanel separatePanel = new JPanel();
        separatePanel.setBounds(0, 105, 1200, 5);
        separatePanel.setBackground(Color.black);


        initFilePanel();
        initColorPanel();
        initShapePanel();
        initEraserPanel();
        initStatusPanel();
        this.add(separatePanel);
        initUserPanel();

        // Drawing Panel
        this.drawingPanel = new DrawingBoard(palette, shapes, boardID);
        if (isManager) this.drawingPanel.newBoard();
        this.add(this.drawingPanel);


        // if is not manager => normal user setup
        if (!isManager) userSetup();

        this.addWindowListener(this);

        this.setVisible(true);


        // keep updating the UI
        this.updateThread = new Thread(() -> {

            while (true){

                // acceptUser();
                this.drawingPanel.repaint();
                updateUserList();
                try {
                    Thread.sleep(UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                    System.out.println("Interrupted");
                    break;
                }
            }


        });

        this.updateThread.start();


    }

    private void initFilePanel(){
        // File Panel
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        // filePanel.setBackground(Color.BLUE);
        filePanel.setBounds(0, 0, 300, 105);
        JLabel fileLabel = new JLabel("File",SwingConstants.CENTER);
        fileLabel.setPreferredSize(new Dimension(250, 18));
        fileLabel.setFont(new Font("ariel", Font.PLAIN, 18));
        filePanel.add(fileLabel);
        this.saveBtn.addActionListener(this);
        filePanel.add(this.saveBtn);
        this.saveAsBtn.addActionListener(this);
        filePanel.add(this.saveAsBtn);
        this.loadBtn.addActionListener(this);
        filePanel.add(this.loadBtn);
        this.newBtn.addActionListener(this);
        filePanel.add(this.newBtn);
        this.closeBtn.addActionListener(this);
        filePanel.add(this.closeBtn);
        this.add(filePanel);
    }

    private void initColorPanel(){
        // Colors Panel
        JPanel colorPanel = new JPanel();
        colorPanel.setBounds(300, 0, 300, 105);
        JLabel colorLabel = new JLabel("Colors",SwingConstants.CENTER);
        colorLabel.setPreferredSize(new Dimension(250, 18));
        colorLabel.setFont(new Font("ariel", Font.PLAIN, 18));
        colorPanel.add(colorLabel);
        // Palette palette = new Palette();
        this.palette = new Palette();
        colorPanel.add(this.palette);
        this.add(colorPanel);
    }

    private void initShapePanel(){
        // Shapes Panel
        JPanel shapePanel = new JPanel();
        shapePanel.setBounds(600, 0, 175, 105);
        JLabel shapeLabel = new JLabel("Shapes",SwingConstants.CENTER);
        shapeLabel.setPreferredSize(new Dimension(250, 18));
        shapeLabel.setFont(new Font("ariel", Font.PLAIN, 18));
        shapePanel.add(shapeLabel);
        this.shapes = new Shapes();
        shapePanel.add(this.shapes);
        this.add(shapePanel);
    }

    private void initEraserPanel(){
        // Eraser Panel
        JPanel eraserPanel = new JPanel();
        eraserPanel.setBounds(775, 0, 175, 105);
        JLabel eraserLabel = new JLabel("Eraser",SwingConstants.CENTER);
        eraserLabel.setPreferredSize(new Dimension(250, 18));
        eraserLabel.setFont(new Font("ariel", Font.PLAIN, 18));
        eraserPanel.add(eraserLabel);
        this.mouseEraseBtn.addActionListener(this);
        eraserPanel.add(this.mouseEraseBtn);
        this.selectEraseBtn.addActionListener(this);
        eraserPanel.add(this.selectEraseBtn);
        this.add(eraserPanel);
    }

    private void initStatusPanel(){
        // Status Panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        statusPanel.setBounds(950, 0, 250, 105);
        JLabel statusLabel = new JLabel("Status",SwingConstants.CENTER);
        statusLabel.setPreferredSize(new Dimension(250, 18));
        statusLabel.setFont(new Font("ariel", Font.PLAIN, 18));
        statusPanel.add(statusLabel);
        JLabel curColorLabel = new JLabel("Current Color: ",  SwingConstants.CENTER);
        curColorLabel.setPreferredSize(new Dimension(120, 25));
        curColorLabel.setFont(new Font("ariel", Font.PLAIN, 18));
        statusPanel.add(curColorLabel);
        JButton colorBtn = new JButton();
        colorBtn.setPreferredSize(new Dimension(22,22));
        colorBtn.setEnabled(false);
        this.palette.setColorBtn(colorBtn);
        this.palette.resetColor();
        statusPanel.add(colorBtn);
        JLabel curShapeLabel = new JLabel("Current Shape: null",  SwingConstants.CENTER);
        curShapeLabel.setPreferredSize(new Dimension(250, 25));
        curShapeLabel.setFont(new Font("ariel", Font.PLAIN, 18));
        this.shapes.setShapeLabel(curShapeLabel);
        statusPanel.add(curShapeLabel);
        this.add(statusPanel);
    }

    private void initUserPanel(){
        // Participant Panel
        JPanel partPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        partPanel.setBounds(0, 110, 175, 690);
        partPanel.setBorder(new MatteBorder(0,0,0,5,Color.black));
        JLabel unLabel = new JLabel("Your Username:", SwingConstants.CENTER);
        unLabel.setPreferredSize(new Dimension(195, 25));
        unLabel.setFont(new Font("ariel", Font.PLAIN, 18));
        JLabel usernameLabel = new JLabel(this.username, SwingConstants.CENTER);
        usernameLabel.setPreferredSize(new Dimension(195, 25));
        usernameLabel.setFont(new Font("ariel", Font.PLAIN, 18));
        JLabel curUsersLabel = new JLabel("All Users", SwingConstants.CENTER);
        curUsersLabel.setPreferredSize(new Dimension(195, 25));
        curUsersLabel.setFont(new Font("ariel", Font.PLAIN, 15));

        //this.list.setPreferredSize(new Dimension(150, 400));
        //this.list.setFixedCellHeight(30);
        this.scrollPane.setPreferredSize(new Dimension(150, 400));
        updateUserList();

        // this.kickBtn.setPreferredSize(new Dimension(10,7));

        partPanel.add(unLabel);
        partPanel.add(usernameLabel);
        partPanel.add(curUsersLabel);
        partPanel.add(this.scrollPane);
        this.add(partPanel);


    }

    private void updateUserList(){

        // System.out.println("Update user list");

        try {
            this.users = this.userlist.getUserList(this.boardID);

            // the manager left the app
            if (this.users.size() == 0){
                this.updateThread.interrupt();
                JOptionPane.showMessageDialog(null, "The manager has left the app.");
                System.exit(0);
            }

            // if the username is not in the userlist means the current user is kicked
            else if (!this.users.contains(this.username)){
                this.updateThread.interrupt();
                JOptionPane.showMessageDialog(null, "You are kicked by the manager");
                System.exit(0);
            }

            this.list = new JList<String>(this.users.toArray(new String[this.users.size()]));
            this.list.setFixedCellHeight(30);
            this.list.setLayoutOrientation(JList.VERTICAL);
            this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.list.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    kickedUsername = users.get(list.getSelectedIndex());

                    if (kickedUsername.equals(username)) {
                        JOptionPane.showMessageDialog(null, "Cannot kick yourself out!!!");
                        return;
                    }


                    int choice = JOptionPane.showConfirmDialog(null, "kick out " + kickedUsername,
                            "Confirm Kick", JOptionPane.YES_NO_OPTION);

                    if (choice == JOptionPane.YES_OPTION){
                        kickUser();
                    }
                }
            });
            if (!this.isManager) this.list.setEnabled(false);
            this.scrollPane.setViewportView(this.list);

        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(null, "Fail to update User List");
        }

    }






    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        if (btn == this.closeBtn){
            System.out.println("Close drawing board");
            this.drawingPanel.close();
        }


        else if (btn == newBtn){
            System.out.println("New drawing board");
            this.drawingPanel.newBoard();
        }

        else if (btn == mouseEraseBtn){
            System.out.println("Erase by mouse");
            this.drawingPanel.getShapes().setErase();
            this.drawingPanel.getShapes().setShape("mouse");
        }

        else if (btn == selectEraseBtn){
            System.out.println("Erase by crop");
            this.drawingPanel.getShapes().setErase();
            this.drawingPanel.getShapes().setShape("rectangle");
        }

        else if (btn == saveBtn){
            this.drawingPanel.save();
        }

        else if (btn == saveAsBtn){
            this.drawingPanel.saveAs();
        }

        else if (btn == loadBtn){
            this.drawingPanel.load();
        }

    }

    private void kickUser(){
        try {
            this.userlist.removeUser(kickedUsername, this.boardID);
        } catch (RemoteException ex) {
            System.out.println("Remote Exception when kicking out user");
        }
    }

    private void acceptUser() {
        String user = "";
        try {
            user = this.userlist.get_join_user(this.boardID);
            if (user.equals("")) return;

            int decision = JOptionPane.showConfirmDialog(null, user + " wants to join the whiteboard");
            if (decision == JOptionPane.YES_OPTION){
                this.userlist.approved(user, this.boardID, 1);
            }
            if (decision == JOptionPane.NO_OPTION){
                this.userlist.approved(user, this.boardID, 2);
            }

        } catch (RemoteException e) {
            System.out.println("Error when trying to get join request");
        }


    }

    // if this is a normal user
    private void userSetup(){
        // disable file panel button
        this.saveBtn.setEnabled(false);
        this.saveAsBtn.setEnabled(false);
        this.loadBtn.setEnabled(false);
        this.newBtn.setEnabled(false);
        this.closeBtn.setEnabled(false);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

        System.out.println("User closed the app");
        if (this.updateThread != null) this.updateThread.interrupt();

        try {
            if (isManager){
                this.userlist.ManagerLeft(this.boardID);
            } else {
                this.userlist.removeUser(this.username, this.boardID);
            }
        }
        catch (RemoteException re){
            System.out.println("Remote Exception Happened");
        }

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
