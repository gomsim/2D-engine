package Graphics;

import javax.swing.*;

public class Display extends JFrame {

    Canvas canvas = new Canvas();

    public Display(){
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(canvas);

        setVisible(true);
    }
}
