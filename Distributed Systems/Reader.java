import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Reader {
    private String fileName;
    private static List<String> lines;
    private static int i = 0;

    public Reader(String fileName){
        this.fileName = fileName;
        Scanner input = null;
        File f;
        try {
            f = new File(fileName);
            input = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.out.println("No such file!");
            return;
        }
        lines = new ArrayList<String>();
        while(input.hasNext()){
            lines.add(input.nextLine());
        }
    }
    public static int getBus(){
        StringTokenizer st = new StringTokenizer(lines.get(i++), ",");
        return Integer.parseInt(st.nextToken());
    }
    public static void main(String[] args) {
        new Reader("..\\dataset\\busLinesNew.txt");
        System.out.println(Reader.getBus());
        Reader.getBus();
        System.out.println(Reader.getBus());
    }
}
