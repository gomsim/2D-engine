package Logic;

import Graphics.Display;

import java.util.ArrayList;

public class Program {

    public static Program instance = new Program();
    private Display display;
    private ArrayList<Artifact> artifacts = new ArrayList<>();

    public static final void main(String[] args){
        instance.run();
    }

    private void run(){
         display = new Display();
    }

    public void addArtifact(int[] x, int[] y){
        artifacts.add(new Artifact(x,y));
    }
    public ArrayList<Artifact> getArtifacts(){
        return artifacts;
    }
    public Artifact getArtifact(int x, int y){
        for (Artifact artifact: artifacts){
            if (artifact.inBox(x,y)){
                return artifact;
            }
        }
        return null;
    }
    public Artifact getArtifactAtPoint(int x, int y){
        Artifact foundArtifact = null;
        for (Artifact artifact: artifacts){ if (artifact.inBox(x,y))
                foundArtifact = artifact;
        }
        if (foundArtifact == null || !foundArtifact.intersects(x,y))
            return null;
        else
            return foundArtifact;
    }
    public ArrayList<Artifact> getArtifactsInRect(int x, int y, int w, int h){
        ArrayList<Artifact> out = new ArrayList<>();
        for (Artifact artifact: artifacts){
            if (artifact.isIn(x, y, w, h)){
                out.add(artifact);
            }
        }
        return out;
    }
}
