package thread_example;

import java.lang.management.RuntimeMXBean;

public class ThreadRunnable implements Runnable {
    String input;

    private ThreadRunnable(String input){
        this.input=input;
    }

    public void run(){
        for (int i=0; i<10; i++){
            System.err.println(i + ":\t" +input);
            try {
                Thread.sleep((int) (Math.random()*500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main (String[] args){
        ThreadRunnable t = new ThreadRunnable("Distributed");
        new Thread(t).start();
        ThreadRunnable t2 = new ThreadRunnable("Systems");
        new Thread(t2).start();


    }
}