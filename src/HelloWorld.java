import lejos.hardware.Button;
import lejos.hardware.motor.Motor;

public class HelloWorld
{
    public static void main(String[] args)
    {
        Robot robot = new Robot();
        Motor.A.forward();
        Motor.B.forward();
        //robot.MoveBackwardC(4200);
        //Button.waitForAnyPress();
        //robot.MoveForwardAB(400);
        //robot.MoveForwardC(4200);
        //robot.TurnClockwiseAB(2500, 900);
        Button.waitForAnyPress();
    }
}