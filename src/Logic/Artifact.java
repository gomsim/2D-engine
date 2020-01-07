package Logic;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

public class Artifact {

    public static final int X = 0, Y = 1;

    public double[][] vertices;
    public double[] center;
    public double boxX, boxY, boxW, boxH;
    public Color color = Color.RED;

    public Artifact(int[] x, int[] y){
        vertices = asDoubleMatrix(x,y);
        setBox(vertices[X],vertices[Y]);
        center = calculateCenter(vertices[X],vertices[Y]);
    }
    private void setBox(double[] x, double[] y){
        double[] sortX = (Arrays.copyOf(x, x.length));
        Arrays.sort(sortX);
        double[] sortY = Arrays.copyOf(y, y.length);
        Arrays.sort(sortY);
        boxX = sortX[0];
        boxY = sortY[0];
        boxW = sortX[x.length-1] - sortX[0];
        boxH = sortY[y.length-1] - sortY[0];
    }
    public int[][] verticesAsInt(){
        int[][] asInt = new int[vertices.length][vertices[X].length];
        for (int i = 0; i < asInt[X].length; i++){
            asInt[X][i] = (int)vertices[X][i];
            asInt[Y][i] = (int)vertices[Y][i];
        }
        return asInt;
    }
    public double[][] asDoubleMatrix(int[] x, int[] y){
        double[][] asDouble = new double[2][x.length];
        for (int i = 0; i < asDouble[X].length; i++){
            asDouble[X][i] = x[i];
            asDouble[Y][i] = y[i];
        }
        return asDouble;
    }
    private double[] calculateCenter(double[] x, double[] y){
        double meanX = 0;
        double meanY = 0;
        for (int i = 0; i < x.length; i++){
            meanX += x[i];
        }
        meanX = meanX/x.length;
        for (int i = 0; i < y.length; i++){
            meanY += y[i];
        }
        meanY = meanY/y.length;
        return new double[] {meanX, meanY};
    }
    public void setColor(Color color){
        this.color = color;
    }

    public boolean intersects(int x, int y){
        boolean intersectsX = false, intersectsY = false;
        for (int currX = (int)boxX-2; currX <= x; currX++){
            if(intersectsBorderAt(closestPointIndex(currX,y),currX,y, true)){
                intersectsX = !intersectsX;
                System.out.println("true");
            }
            System.out.println("still checking X");
        }
        System.out.println("get here");
        for (int currY = (int)boxY-2; currY <= y; currY++){
            if(intersectsBorderAt(closestPointIndex(currY,x),currY,x, false)){
                intersectsY = !intersectsY;
                System.out.println("true");

            }
            System.out.println("still checking Y");
        }
        System.out.println(intersectsX + " " + intersectsY);
        return intersectsX && intersectsY;
    }
    private boolean intersectsBorderAt(int vertexIndex, int x, int y, boolean horizontal){
        int x2 = x, y2 = y;
        if (horizontal)
            x2++;
        else
            y2++;
        int vX1 = (int)vertices[X][vertexIndex]-1;
        int vY1 = (int)vertices[Y][vertexIndex]-1;
        int vX2 = (int)vertices[X][vertexIndex]+1;
        int vY2 = (int)vertices[Y][vertexIndex]+1;
        boolean dir1 = rotation(new int[]{x,y},new int[]{x2,y2},new int[]{vX1,vY1});
        boolean dir2 = rotation(new int[]{x,y},new int[]{x2,y2},new int[]{vX2,vY2});
        boolean dir3 = rotation(new int[]{vX1,vY1},new int[]{vX2,vY2},new int[]{x,y});
        boolean dir4 = rotation(new int[]{vX1,vY1},new int[]{vX2,vY2},new int[]{x2,y2});
        System.out.println("dirs: " + dir1 + " "+ dir2 + " "+ dir3 + " "+ dir4);
        return dir1 != dir2 && dir3 != dir4;
    }
    private boolean rotation(int[] a, int[] b, int[] c){
        return (c[Y]-a[Y]) * (b[X]-a[X]) > (b[Y]-a[Y]) * (c[X]-a[X]);
    }
    private int closestPointIndex(int x, int y){
        //BFS
        LinkedList<int[]> que = new LinkedList<>();
        HashSet<int[]> visited = new HashSet<>();
        que.add(new int[] {x,y});
        while (true){
            int[] currPoint = que.removeFirst();
            System.out.println("checking " + currPoint[X] + ":"+ currPoint[Y]);
            visited.add(currPoint);
            if (arrayContains(vertices[X], currPoint[X]) && arrayContains(vertices[Y], currPoint[Y]))
                return correspondingIndex(vertices[X], currPoint[X]);
            for (int[] neighbour: neighbours(currPoint)){
                boolean contains = false;
                for (int[] array: visited){
                    if (equalArrays(array,neighbour))
                        contains = true;
                }
                if (contains)
                    que.add(neighbour);
            }
        }
    }
    private boolean equalArrays(int[] a, int[] b){
        return a[0] == b[0] && a[1] == b[1];
    }
    private int[][] neighbours(int[] point){
        int[][] out = new int[4][2];
        out[0] = new int[] {point[X],point[Y]-1};
        out[1] = new int[] {point[X]+1,point[Y]};
        out[2] = new int[] {point[X],point[Y]+1};
        out[3] = new int[] {point[X]-1,point[Y]};
        return out;
    }
    private int correspondingIndex(double[] array, int x){
        for (int i = 0; i < array.length; i++){
            if (x > array[i]-1 && x < array[i]+1)
                return i;
        }
        return -0;
    }
    private boolean arrayContains(double[] array, int x){
        for (int i = 0; i < array.length; i++){
            if (x > array[i]-1 && x < array[i]+1)
                return true;
        }
        return false;
    }
    public boolean inBox(int x, int y){
        return x > boxX && x < boxX + boxW && y > boxY && y < boxY + boxH;
    }

    public void scale(double scalar){
        double[][] P = new double[][] {Arrays.copyOf(vertices[X], vertices[X].length),Arrays.copyOf(vertices[Y], vertices[Y].length)};
        double[][] C = new double[2][vertices[X].length];
        for (int i = 0; i < vertices[X].length; i++){
            C[0][i] = center[X];//boxX+(boxW/2);
            C[1][i] = center[Y];//boxY+(boxH/2);
        }
        double[][] S = new double[2][vertices[X].length];
        for (int i = 0; i < vertices[X].length; i++){
            S[0][i] = scalar;
            S[1][i] = scalar;
        }
        double[][] result = add(multiplyScal(S, sub(P,C)),C);
        for (int i = 0; i < vertices[X].length; i++){
            vertices[X][i] = result[X][i];
            vertices[Y][i] = result[Y][i];
        }
        setBox(vertices[X],vertices[Y]);
    }
    public void zoom(double scalar){
        double[][] P = new double[][] {Arrays.copyOf(vertices[X], vertices[X].length),Arrays.copyOf(vertices[Y], vertices[Y].length)};
        double[][] S = new double[2][vertices[X].length];
        for (int i = 0; i < vertices[X].length; i++){
            S[0][i] = scalar;
            S[1][i] = scalar;
        }
        double[][] result = multiplyScal(S,P);
        for (int i = 0; i < vertices[X].length; i++){
            vertices[X][i] = result[X][i];
            vertices[Y][i] = result[Y][i];
        }
        setBox(vertices[X],vertices[Y]);
        center = calculateCenter(vertices[X],vertices[Y]);
    }
    public void translate(int distX, int distY){
        //Using formula P'=P+C
        for (int i = 0; i < vertices[X].length; i++){
            vertices[X][i] += distX;
        }
        for (int i = 0; i < vertices[Y].length; i++){
            vertices[Y][i] += distY;
        }
        boxX += distX;
        boxY += distY;
        center[0] += distX;
        center[1] += distY;
    }
    public void rotate(int deg){
        //Using formula P'=R(P-C)+C
        deg *= 90;
        double[][] P = new double[][] {Arrays.copyOf(vertices[X], vertices[X].length),Arrays.copyOf(vertices[Y], vertices[Y].length)};
        double[][] C = new double[2][vertices[X].length];
        for (int i = 0; i < vertices[X].length; i++){
            C[0][i] = center[X];//boxX+(boxW/2);
            C[1][i] = center[Y];//boxY+(boxH/2);
        }
        double[][] R = new double[2][2];
        R[0][0] = Math.cos(deg);
        R[0][1] = -Math.sin(deg);
        R[1][0] = Math.sin(deg);
        R[1][1] = Math.cos(deg);
        double[][] result = add(multiplyRot(R, sub(P,C)),C);
        for (int i = 0; i < vertices[X].length; i++){
            vertices[X][i] = result[X][i];
            vertices[Y][i] = result[Y][i];
        }
        setBox(vertices[X],vertices[Y]);
    }
    private void printArray(double[] array){
        for (int i = 0; i < array.length; i++)
            System.out.print(array[i] + " ");
        System.out.println();
    }

    private double[][] multiplyRot(double[][] R, double[][] P){
        //R*P
        double[][] out = new double[R.length][P[0].length];
        for (int i = 0; i < P[0].length; i++){
            out[0][i] = ((R[0][0] * P[X][i]) + (R[0][1] * P[Y][i]));
            out[1][i] = ((R[1][0] * P[X][i]) + (R[1][1] * P[Y][i]));
        }
        return out;
    }
    private double[][] multiplyScal(double[][] A, double[][] B) {
        //A+B
        double[][] out = new double[A.length][A[0].length];
        for (int i = 0; i < B[0].length; i++) {
            out[0][i] = A[0][i] * B[0][i];
            out[1][i] = A[1][i] * B[1][i];
        }
        return out;
    }
    private double[][] add(double[][] A, double[][] B) {
        //A+B
        double[][] out = new double[A.length][A[0].length];
        for (int i = 0; i < B[0].length; i++) {
            out[0][i] = A[0][i] + B[0][i];
            out[1][i] = A[1][i] + B[1][i];
        }
        return out;
    }
    private double[][] sub(double[][] A, double[][] B) {
        //A-B
        double[][] out = new double[A.length][A[0].length];
        for (int i = 0; i < B[0].length; i++) {
            out[0][i] = A[0][i] - B[0][i];
            out[1][i] = A[1][i] - B[1][i];
        }
        return out;
    }
    public boolean isIn(int x, int y, int w, int h){
        for (int i = 0; i < vertices[X].length; i++){
            if (vertices[X][i] > x && vertices[X][i] < x+w && vertices[Y][i] > y && vertices[Y][i] < y+h){
                return true;
            }
        }
        return false;
    }
    public String toString(){
        String out = "";
        for (int i = 0; i < vertices.length; i++){
            out += vertices[X][i] + ":" + vertices[Y][i] + " ";
        }
        return out;
    }
}
