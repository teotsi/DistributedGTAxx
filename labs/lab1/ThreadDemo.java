package Sychronized_Demo;

public class ThreadDemo extends Thread{
    private Thread t;
    private String threadName;
    PrintDemo PD;

    ThreadDemo(String name, PrintDemo pd){
        threadName=name;
        PD=pd;
    }
    public void run(){
        synchronized(PD){
            PD.printcount();
        }
        System.out.println("Thread " +threadName+ " existing.");
    }
}