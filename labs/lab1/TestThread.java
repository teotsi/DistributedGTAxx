package Sychronized_Demo;

public class TestThread {
    public static void main(String[] args) {
        PrintDemo PD = new PrintDemo();

        ThreadDemo t1 = new ThreadDemo("Thread -1", PD);
        ThreadDemo t2 = new ThreadDemo("Thread -2", PD);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (Exception e) {
            System.out.println("Interrupted");
        }

    }
}