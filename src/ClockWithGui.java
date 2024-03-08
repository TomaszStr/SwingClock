import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.time.LocalTime;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_MITER;

public class ClockWithGui extends JPanel {

    LocalTime time = LocalTime.now();

    ClockWithGui(){
        setOpaque(false);
    }

    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(isOpaque);
    }

    public void paintComponent(Graphics g){
        Graphics2D g2d=(Graphics2D)g;
        g2d.translate(getWidth()/2,getHeight()/2);
        Font font = new Font("Purisa", Font.PLAIN, 16);
        g2d.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);
        for(int i=1;i<13;i++){
            AffineTransform at = new AffineTransform();
            at.rotate(2*Math.PI/12*i);
            Point2D src = new Point2D.Float(0,-120);
            Point2D trg = new Point2D.Float();
            Point2D pnt1 = new Point2D.Float();
            Point2D pnt2 = new Point2D.Float();
            at.transform(src,trg);
            at.transform(new Point2D.Float(0,-105),pnt1);
            at.transform(new Point2D.Float(0,-90),pnt2);
            g2d.drawLine((int) pnt1.getX(), (int) pnt1.getY(), (int) pnt2.getX(), (int) pnt2.getY());
            String str = Integer.toString(i);
            g2d.drawString(str,(int)trg.getX()-metrics.stringWidth(str)/2,(int)trg.getY()+metrics.getHeight()/3);
        }

        for(int i=1;i<61;i++){
            AffineTransform at = new AffineTransform();
            at.rotate(2*Math.PI/60*i);
            Point2D pnt1 = new Point2D.Float();
            Point2D pnt2 = new Point2D.Float();
            at.transform(new Point2D.Float(0,-105),pnt1);
            at.transform(new Point2D.Float(0,-100),pnt2);
            g2d.drawLine((int) pnt1.getX(), (int) pnt1.getY(), (int) pnt2.getX(), (int) pnt2.getY());
        }
        int r=130;
        g2d.drawOval(-r,-r,2*r,2*r);
        r=105;
        g2d.drawOval(-r,-r,2*r,2*r);
        //przesuwac po kawalku
        //hours
        AffineTransform saveAT = g2d.getTransform();
        g2d.rotate(time.getHour()%12*2*Math.PI/12 + (time.getMinute()%60)*2*Math.PI/(12*60));
        g2d.setStroke(new BasicStroke(4, CAP_ROUND,JOIN_MITER));
        g2d.setColor(new Color(255,0,0));
        g2d.drawLine(0,0,0,-50);
        g2d.setTransform(saveAT);

        //minutes
        saveAT = g2d.getTransform();
        g2d.rotate(time.getMinute()%60*2*Math.PI/60 + time.getSecond()%60*2*Math.PI/(60*60));
        g2d.setStroke(new BasicStroke(3, CAP_ROUND,JOIN_MITER));
        g2d.setColor(new Color(0,0,0));
        g2d.drawLine(0,0,0,-75);
        g2d.setTransform(saveAT);

        //seconds
        saveAT = g2d.getTransform();
        g2d.rotate(time.getSecond()%60*2*Math.PI/60);
        g2d.setStroke(new BasicStroke(2, CAP_ROUND,JOIN_MITER));
        g2d.setColor(new Color(0,0,255));
        g2d.drawLine(0,0,0,-100);
        g2d.setTransform(saveAT);

        g2d.setColor(new Color(0,0,0));
        g2d.fillOval(-5,-5,10,10);
    }

    class ClockThread extends Thread{
        @Override
        public void run() {
            for(;;){
                time = LocalTime.now();
                System.out.printf("%02d:%02d:%02d\n",time.getHour(),time.getMinute(),time.getSecond());
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                repaint();
            }
        }
    }

    public static void main(String[] args) {
        ClockWithGui g  = new ClockWithGui();
        JFrame frame = new JFrame("Clock");
        frame.setContentPane(g);
        frame.setSize(700, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);
        g.start();
    }

    public void start() {
        new ClockThread().start();
    }



}