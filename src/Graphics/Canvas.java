package Graphics;

import Logic.Artifact;
import Logic.Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Canvas extends JPanel {

    MouseAdapter mouseAdapter = new CanvasListener();
    ArrayList<Integer> tempX = new ArrayList<>();
    ArrayList<Integer> tempY = new ArrayList<>();

    private ArrayList<Artifact> holdingGroup = new ArrayList<>();
    private boolean holdingCtrl;
    Rectangle markingRect = new Rectangle();

    public int[] currPoint = new int[]{0,0};

    public Canvas(){
        setFocusable(true);
        setBackground(Color.WHITE);
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
        addKeyListener(new KeyBoardListener());
        requestFocus();
    }

    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        Graphics2D g2D = (Graphics2D)graphics;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.drawPolygon(ArrayListToArray(tempX), ArrayListToArray(tempY), tempX.size());
        for (Artifact artifact: Program.instance.getArtifacts()){
            graphics.setColor(new Color(0,0,0,25));
            graphics.fillRect((int)artifact.boxX,(int)artifact.boxY,(int)artifact.boxW,(int)artifact.boxH);
            graphics.setColor(artifact.color);
            graphics.fillPolygon(artifact.verticesAsInt()[Artifact.X], artifact.verticesAsInt()[Artifact.Y], artifact.vertices[Artifact.X].length);
            graphics.setColor(Color.BLACK);
            graphics.fillOval((int)artifact.center[Artifact.X]-3,(int)artifact.center[Artifact.Y]-3,6,6);
        }
        graphics.setColor(new Color(0,0,0,25));
        if (markingRect != null)
            graphics.fillRect(markingRect.x,markingRect.y,markingRect.width,markingRect.height);
    }
    private int[] ArrayListToArray(ArrayList<Integer> list){
        int[] output = new int[list.size()];
        for (int i = 0; i < list.size(); i++)
            output[i] = list.get(i);
        return output;
    }
    private void zoom(double scalar, int mousePosX, int mousePosY){
        for (Artifact artifact: Program.instance.getArtifacts()){
            artifact.translate(-mousePosX,-mousePosY);
        }
        for (Artifact artifact: Program.instance.getArtifacts()){
            artifact.zoom(scalar);
        }
        for (Artifact artifact: Program.instance.getArtifacts()){
            artifact.translate(mousePosX,mousePosY);
        }
    }
    public void moveView(int x, int y){
        for (Artifact artifact: Program.instance.getArtifacts()){
            artifact.translate(x,y);
        }
    }
    private class CanvasListener extends MouseAdapter {
        private ArrayList<Integer> x = new ArrayList<>(), y = new ArrayList<>();
        private int lastX, lastY;
        int[] ctrlPoint;

        public void mousePressed(MouseEvent event){
            if (holdingCtrl){
                ctrlPoint = new int[] {event.getX(),event.getY()};
            }else{
                if (!holdingGroup.contains(Program.instance.getArtifact(event.getX(),event.getY()))){
                    if (event.getButton() == MouseEvent.BUTTON3) {
                        for (Artifact artifact: holdingGroup)
                            artifact.setColor(Color.RED);
                        holdingGroup.clear();
                        Artifact toAdd = Program.instance.getArtifact(event.getX(), event.getY());
                        if (toAdd != null)
                            holdingGroup.add(toAdd);
                        //holding = Program.instance.getArtifactAtPoint(event.getX(),event.getY());
                        for (Artifact artifact: holdingGroup)
                            artifact.setColor(Color.YELLOW);
                        System.out.println(holdingGroup.size());
                    } else if (event.getButton() == MouseEvent.BUTTON1) {
                        for (Artifact artifact: holdingGroup)
                            artifact.setColor(Color.RED);
                        holdingGroup.clear();
                        x.clear();
                        y.clear();
                    }
                }
                lastX = event.getX();
                lastY = event.getY();
            }
            repaint();
        }
        public void mouseReleased(MouseEvent event) {
            if (holdingCtrl){
                for (Artifact artifact :holdingGroup)
                    artifact.setColor(Color.RED);
                holdingGroup = Program.instance.getArtifactsInRect(markingRect.x,markingRect.y,markingRect.width,markingRect.height);
                for (Artifact artifact: holdingGroup)
                    artifact.setColor(Color.YELLOW);
            }else{
                if (event.getButton() == MouseEvent.BUTTON3) {
                    if (holdingGroup.size() == 1){
                        for (Artifact artifact: holdingGroup)
                            artifact.setColor(Color.RED);
                        holdingGroup.clear();
                    }
                } else if (event.getButton() == MouseEvent.BUTTON1) {
                    if (!x.isEmpty() && !y.isEmpty()) {
                        Program.instance.addArtifact(ArrayListToArray(x), ArrayListToArray(y));
                    }
                    tempX.clear();
                    tempY.clear();
                }
            }
            markingRect = null;
            repaint();
        }
        public void mouseDragged(MouseEvent event){
            if (holdingCtrl){
                int boxX = Math.min(ctrlPoint[0],event.getX());
                int boxY = Math.min(ctrlPoint[1],event.getY());
                int boxW = Math.abs(event.getX()-ctrlPoint[0]);
                int boxH = Math.abs(event.getY()-ctrlPoint[1]);
                markingRect = new Rectangle(boxX,boxY,boxW,boxH);
            }else{
                if (SwingUtilities.isRightMouseButton(event)) {
                    if (!holdingGroup.isEmpty()) {
                        for (Artifact artifact: holdingGroup)
                            artifact.translate(event.getX()-lastX,event.getY()-lastY);
                    }else{
                        moveView(event.getX()-lastX,event.getY()-lastY);
                    }
                    lastX = event.getX();
                    lastY = event.getY();
                }else if (SwingUtilities.isLeftMouseButton(event)){
                    x.add(event.getX());
                    y.add(event.getY());
                    tempX.add(event.getX());
                    tempY.add(event.getY());
                }
            }
            repaint();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent event) {
            super.mouseWheelMoved(event);
            if (!holdingGroup.isEmpty()){
                for (Artifact artifact: holdingGroup)
                    artifact.rotate(event.getWheelRotation()*event.getScrollAmount());
            }else{
                //NOT IDEAL!!!!!
                if (event.getPreciseWheelRotation() < 0)
                    zoom(1.02,event.getX(),event.getY());
                else
                    zoom(0.98,event.getX(),event.getY());
            }
            repaint();
        }
    }
    private class KeyBoardListener implements KeyListener{
        public void keyPressed(KeyEvent event){
            if (event.getKeyCode() == KeyEvent.VK_CONTROL){
                holdingCtrl = true;
            }else{
                if (event.getKeyCode() == KeyEvent.VK_UP) {
                    for (Artifact artifact: holdingGroup)
                        artifact.scale(1.1);
                } else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
                    for (Artifact artifact: holdingGroup)
                        artifact.scale(0.9);
                }
            }
            repaint();
        }
        public void keyReleased(KeyEvent event){
            if (event.getKeyCode() == KeyEvent.VK_CONTROL){
                holdingCtrl = false;
            }
        }
        public void keyTyped(KeyEvent event){

        }
    }
}
