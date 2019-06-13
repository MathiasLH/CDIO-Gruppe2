import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.ParsePosition;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import nu.pattern.OpenCV;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.*;
import org.opencv.videoio.*;

import java.nio.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.Core;
import org.opencv.core.Scalar;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Timer;

import static org.opencv.core.Core.*;
import static org.opencv.core.CvType.CV_64FC3;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_WIDTH;

public class ComputerVision extends JPanel{
    BufferedImage image;
    Point frontCenter = new Point() , backCenter = new Point(), lastPositionFront = new Point(), lastPositionBack = new Point();
    ArrayList<Point> locationOfBalls = new ArrayList<>();
    static ArrayList<ArrayList<Point>> ballConsistency = new ArrayList<>();
    Mat frame;
    VideoCapture camera;

    public ComputerVision(){
        try {
            init();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void init() throws InterruptedException {

        // Loading core libary to get accesses to the camera
        OpenCV.loadLocally();
        // If not load correctly try:
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //ComputerVision t = new ComputerVision();
        Point lastPositionFront = new Point();
        Point lastPositionBack = new Point();

        // Capturing from usb Camera
        // USB CAM index 4 , own is 0
        camera = new VideoCapture(4);
        //camera.open("/dev/v41/by-id/usb-046d_Logitech_Webcam_C930e_DDCF656E-video-index0");

        // Set resulution
        //1280 - 720        //640 - 480
        camera.set(CV_CAP_PROP_FRAME_WIDTH,640);
        camera.set(CV_CAP_PROP_FRAME_HEIGHT, 480);

        // New Mat frame
        frame = new Mat();

        ArrayList<Double> avgPoints = new ArrayList<Double>();
        double[] vector = new double[4];

        // Show the mat frame
        System.out.println(frame.type());
        camera.read(frame);

        if(!camera.isOpened()){
            System.out.println("Error");
        }
        else
            {



        }
        System.out.println("out");
        //camera.release();
    }

    public void imageLoop(){
        //while(true) {
            // New Picture
            Mat tempImage = new Mat();
            Mat tempImage1 = new Mat();
            Mat tempImage2 = new Mat();
            Mat tempImage3 = new Mat();
            Mat tempImage4 = new Mat();
            Mat tempImage5 = new Mat();
            Mat tempImage6 = new Mat();
            Mat tempImage7 = new Mat();
            Mat combined = new Mat();

            //Point frontCenter = new Point();
            //Point backCenter = new Point();

            if (camera.read(frame)) {

                // Convert color
                Imgproc.cvtColor(frame, tempImage, COLOR_BGR2GRAY);
                //Imgproc.medianBlur(tempImage, tempImage, 11);
                Core.normalize(tempImage,tempImage,60,200,Core.NORM_MINMAX, CV_8UC1);
                HighGui.imshow("whatever2", tempImage);

                // Use HoughCircels to mark the balls
                Mat circles = new Mat();
                Mat fromtcircles = new Mat();
                Mat backcircles = new Mat();

                Imgproc.HoughCircles(tempImage, circles, Imgproc.HOUGH_GRADIENT, 1, (double) tempImage.rows()/100, 50.0, 20.0, 4, 9 );  // save values 50, 50, 25,10,23
                Imgproc.HoughCircles(tempImage, fromtcircles, Imgproc.HOUGH_GRADIENT, 1, (double) tempImage.rows()/100, 50.0, 19.0, 10, 15);  // save values 50, 50, 25,10,23
                Imgproc.HoughCircles(tempImage, backcircles, Imgproc.HOUGH_GRADIENT, 1, (double) tempImage.rows()/100, 50.0, 19.0, 15, 30);  // save values 50, 50, 25,10,23

                //New Mat to detect colors
                Imgproc.cvtColor(frame, tempImage1, COLOR_BGR2HSV);
                Core.normalize(tempImage,tempImage1,10,200,Core.NORM_MINMAX, CV_8UC1);
                Imgproc.cvtColor(frame, tempImage6, COLOR_BGR2HSV);
                Imgproc.cvtColor(frame, tempImage7, COLOR_BGR2GRAY);
                Imgproc.medianBlur(tempImage7, tempImage7, 3);

                //Core.inRange(tempImage2,new Scalar(0,0,0),new Scalar(250,250,180),tempImage2);
                //borders
                inRange(tempImage1, new Scalar(0, 170, 170), new Scalar(190, 255, 255), tempImage2);

                //Goals
                inRange(tempImage1, new Scalar(30, 40, 230), new Scalar(45, 60, 255), tempImage3);


                inRange(tempImage1, new Scalar(70, 20, 230), new Scalar(100, 40, 255), tempImage4);

                //Robot

                // GREEN
                inRange(tempImage1,new Scalar(40,20,170),new Scalar(65,45,200),tempImage5);

                // VIOLET
                inRange(tempImage1,new Scalar(155,55,229),new Scalar(160,65,239),tempImage6);

                inRange(tempImage7,new Scalar(10,10,10),new Scalar(40,40,40),tempImage7);

                locationOfBalls = new ArrayList<>();
                //Colorize circels
                for (int i = 0; i < circles.cols(); i++) {
                    double[] c = circles.get(0, i);
                    Point center = new Point(Math.round(c[0]), Math.round(c[1]));
                    locationOfBalls.add(center);

                }
                ballConsistency.add(0, locationOfBalls);
                if(ballConsistency.size() >= 10){
                    ballConsistency.remove(ballConsistency.size()-1);
                }
                ArrayList<Point> balls = new ArrayList<>();

                for(int i = 0; i < ballConsistency.size(); i++){
                    for(int j = 0; j < ballConsistency.get(i).size(); j++){
                        if(!balls.contains(ballConsistency.get(i).get(j))){
                            balls.add(ballConsistency.get(i).get(j));
                        }
                    }
                }
                for(int i = 0; i < balls.size(); i++){
                    for(int j = 0; j < balls.size(); j++){
                        if(i != j && (balls.get(i).x <= balls.get(j).x+5 && balls.get(i).x >= balls.get(j).x-5) && (balls.get(i).y <= balls.get(j).y+5 && balls.get(i).y >= balls.get(j).y-5)){
                            balls.remove(i);
                            i--;
                            break;
                        }
                    }
                }
                for(int i = 0; i < balls.size(); i++){
                    Imgproc.circle(frame, balls.get(i), 1, new Scalar(255, 100, 100), 7, 8, 0);
                    //int radius = (int) Math.round(c[2]);
                    //mgproc.circle(frame, balls.get(i), radius, new Scalar(255, 0, 255), 3, 8, 0);
                }

                try{

                    double[] c = backcircles.get(0, 0);
                    backCenter = new Point(Math.round(c[0]), Math.round(c[1]));
                    Imgproc.circle(frame, backCenter, 1, new Scalar(0, 100, 100), 3, 8, 0);
                    lastPositionBack = backCenter;
                }catch (NullPointerException e){
                    backCenter = lastPositionBack;
                }
                try {
                    double[] c = backcircles.get(0, 0);
                    c = fromtcircles.get(0, 0);
                    frontCenter = new Point(Math.round(c[0]), Math.round(c[1]));
                    Imgproc.circle(frame, frontCenter, 1, new Scalar(0, 100, 100), 3, 8, 0);

                    lastPositionFront = frontCenter;
                }catch (NullPointerException e){
                    System.out.println("no circle found");
                    frontCenter = lastPositionFront;
                }

                ArrayList<Point> avgRobotFront = new ArrayList<Point>();
                final ArrayList<Point> avgRobotBack = new ArrayList<Point>();
                Point avgGoal2 = new Point();
                Point goal = new Point();
                Point goal2 = new Point();
                Point borders = new Point();
                combined = tempImage2;
                ArrayList<Double> meanForGoal2x = new ArrayList<>();
                ArrayList<Double> meanForGoal2y = new ArrayList<>();
                        /*for (int i = 0; i < tempImage.rows(); i++) {
                            for (int j = 0; j < tempImage.cols(); j++) {

                                if (tempImage2.get(i, j)[0] == 255) {
                                    combined.put(i, j, tempImage2.get(i, j)[0]);
                                    borders = new Point(j,i);
                                }

                                if (tempImage3.get(i, j)[0] == 255) {
                                    combined.put(i, j, tempImage3.get(i, j)[0] );
                                    goal = new Point(j, i);

                                }

                                if (tempImage4.get(i, j)[0] == 255) {
                                    combined.put(i, j, tempImage4.get(i, j)[0]);
                                    goal2 = new Point(j,i);
                                    meanForGoal2x.add(goal2.x);
                                    meanForGoal2y.add(goal2.y);
                                }

                              *//*  if (tempImage5.get(i, j)[0] == 255) {
                                    if(i <= 10 && j <= 10){
                                        avgRobotBack.add(lastPositionBack);
                                    }else{
                                        lastPositionBack = new Point(j,i);
                                    }
                                    combined.put(i, j, tempImage5.get(i, j)[0] );
                                    avgRobotBack.add(new Point(j, i));

                                }
                                if (tempImage6.get(i, j)[0] == 255) {
                                    combined.put(i, j, tempImage6.get(i, j)[0] );
                                    avgRobotFront.add(new Point(j,i));

                                }*//*


                            }


                        }*/

                try {
                    Collections.sort(meanForGoal2x);
                    Collections.sort(meanForGoal2y);
                    goal2.x = meanForGoal2x.get(meanForGoal2x.size() / 2);
                    goal2.y = meanForGoal2y.get(meanForGoal2y.size() / 2);
                }catch (IndexOutOfBoundsException e){
                    //System.out.println("oops");
                }

                double Goalangle;
                double dis;
                // System.out.println("back:"+robotBack +"\n "+"front:"+robotFront);
                dis = Math.sqrt(Math.pow(goal2.x - frontCenter.x, 2) + Math.pow(goal.y - frontCenter.y, 2));
                Imgproc.line(frame, frontCenter, backCenter,  new Scalar(0,0,250), 5);
                Imgproc.line(frame, goal2, goal2,  new Scalar(0,250,0), 5);

                        /*Point robotVector = new Point(frontCenter.x - backCenter.x, frontCenter.y - backCenter.y);
                        Point bigGoalVector = new Point(goal2.x - frontCenter.x, goal2.y - frontCenter.y);
                        Imgproc.line(frame, goal2, backCenter,  new Scalar(250,0,0), 5);
                        Point a = robotVector;
                        Point b = bigGoalVector;
                        double dotProduct = (a.x*b.x)+(a.y*b.y);
                        double magnitudeOfA = Math.sqrt(Math.pow(a.x,2)+Math.pow(a.y,2));
                        double magnitudeOfB = Math.sqrt(Math.pow(b.x,2)+Math.pow(b.y,2));
                        Goalangle = Math.toDegrees(Math.acos(dotProduct/(magnitudeOfA*magnitudeOfB)));*/

                getDirections();


                           /* Goalangle = Math.toDegrees(Math.cos((robotVector.x*bigGoalVector.x + robotVector.y*bigGoalVector.y)/
                                   (Math.sqrt(Math.pow(robotVector.x,2)+Math.pow(robotVector.y,2)))
                                           * Math.sqrt(Math.pow(bigGoalVector.x,2)*Math.pow(bigGoalVector.y,2))));*/

                // System.out.println("frobotfront :" + avgRobotFront.size());
                //System.out.println("robotback ;"+ avgRobotBack.size());
                // System.out.println("goal"+ goal);
                //System.out.println(dis + " - dist ");

                HighGui.imshow("SHIET SON", frame);
                // HighGui.imshow("whatever", tempImage);

                //HighGui.imshow("whatever3", tempImage3);
                //HighGui.imshow("whatever4", tempImage4);
                //HighGui.imshow("whatever5",tempImage5);
                HighGui.waitKey(1);
                //System.out.println(backCenter);


                if (frame.empty()) {
                    System.out.println("1");
                }
                if (frame.type() == CV_8UC1) {
                    System.out.println("2");
                }

            }

        //}
    }

    public double[] getDirections(){
        double[] output = new double[3];
        getAngle(getClosestBall(), output);
        return output;
    }

    public Point getClosestBall(){
        //it is assumes that the back of the robot is the center.
        double minDist = 1000000;
        int minIndex = 1000;
        for(int i = 0; i < locationOfBalls.size(); i++){
            double distance = Math.sqrt(Math.pow(locationOfBalls.get(i).x-backCenter.x, 2) + Math.pow(locationOfBalls.get(i).y - backCenter.y, 2));
            if(distance < minDist){
                minDist = distance;
                minIndex = i;
            }
        }
        if(minIndex != 1000){
            return locationOfBalls.get(minIndex);
        }else return new Point(0,0);
    }

    public double getAngle(Point goal, double[] output){

        if(goal.x > 10 && goal.y > 10){
            Point robotVector = new Point(frontCenter.x - backCenter.x, frontCenter.y - backCenter.y);
            Point bigGoalVector = new Point(goal.x - frontCenter.x, goal.y - frontCenter.y);
            Imgproc.line(frame, goal, backCenter,  new Scalar(250,0,0), 5);
            Point a = robotVector;
            Point b = bigGoalVector;
            double result = ((goal.x - backCenter.x) * (frontCenter.y - backCenter.y)) - ((goal.y - backCenter.y) * (frontCenter.x - backCenter.x));
            System.out.println(result);
            if(result > 0){
                System.out.println("turn left");
                output[1] = 0;
            }else{
                System.out.println("turn right");
                output[1] = 1;
            }
            double dotProduct = (a.x*b.x)+(a.y*b.y);

            double magnitudeOfA = Math.sqrt(Math.pow(a.x,2)+Math.pow(a.y,2));
            double magnitudeOfB = Math.sqrt(Math.pow(b.x,2)+Math.pow(b.y,2));
            double Goalangle = Math.toDegrees(Math.acos(dotProduct/(magnitudeOfA*magnitudeOfB)));
            output[0] = Goalangle;
            double distance = Math.sqrt(Math.pow(goal.x - backCenter.x, 2) + Math.pow(goal.y - backCenter.y, 2));
            output[2] = distance;

            //System.out.println(Goalangle + " - angle" );
            //System.out.println("backCenter: " + backCenter + ", frontCenter: " + frontCenter + ", goal: " + goal);

        }


        return 0;
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }
}
