import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Reader {
    private static List<String> lines;
    private static int i = 0;
    private static int totalLines;
    private String fileName;

    public Reader(String fileName) {
        this.fileName = fileName;
        Scanner input;
        File f;
        try {
            f = new File(fileName);
            input = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.out.println("No such file!");
            return;
        }
        lines = new ArrayList<String>();
        while (input.hasNext()) {
            lines.add(input.nextLine());
        }
        totalLines = lines.size();
    }

    public static String getBus() {
        StringTokenizer st = new StringTokenizer(lines.get(i++), ",");

//        System.out.println("after i++");
        st.nextToken();
        return st.nextToken();
    }

    public static boolean moreBuses() {
//        System.out.println(i);
        return i < totalLines;
    }

    public static void main(String[] args) {
        new Reader("..\\dataset\\busLinesNew.txt");
        System.out.println(Reader.getBus());
        Reader.getBus();
        System.out.println(Reader.getBus());
    }
}
