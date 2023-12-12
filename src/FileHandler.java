import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * This class is responsible for working with the files, so that the highscore can be saved into the file and it can be read from the file.
 * @author Marcel Pendyk
 * @version 1.0
 */
public class FileHandler {
    private String[] namesArr = new String[1000];
    private int[] scoresArr = new int[1000];
    //read data needs to be in 2 separate arrays
    //save to file data will be in form of parameters in the method

    FileHandler(String fileName)
    {
        readFile(fileName);
    }

    /**
     * A function that reads the lines of a file and prints it out in *String* *int* manner
     * @param fileName name (or path) of the file that is to be read
     */
    public void readFile(String fileName)
    {
        try {
            File readFile = new File(fileName);
            //check if the file exists. If not print an error and return
            if(!readFile.exists())
            {
                System.out.println("file: "+fileName +  " doesn't exist");
                return;
            }
            //Scanner scanner = new Scanner(System.in)
            FileInputStream readFileInputStream = new FileInputStream(readFile);
            Scanner readScanner = new Scanner(readFileInputStream);
            //while scanner has something to scan for print out the values
            int index=0;
            while(readScanner.hasNext())
            {
                namesArr[index]=readScanner.next();
                scoresArr[index ]= readScanner.nextInt();
                index++;
            }
        } catch (IOException e) {
            System.out.println("chuj");
            e.printStackTrace();
        }

    }

    /**
     * A function that allows to save the highscore of the user to the file.
     * @param fileName name (or path) of the file that is to be read
     * @param highscore value of the score that user got
     * @param userName name that user chose for the name that he wanted to save
     */
    public void saveFile(String fileName, int highscore, String userName)
    {
        int entriesNum=0;
        try {
            File saveFile = new File(fileName);
            //check if the file exists. If not print an error and return
            if(!saveFile.exists())
            {
                System.out.println("can't find a file called: " + fileName);
                return;
            }
            //create input stream to read from file and create scanner to read from the input stream
            FileInputStream saveFileInputStream = new FileInputStream(saveFile);
            Scanner saveScanner = new Scanner(saveFileInputStream);
            //read the data and save it to arrays
            while(saveScanner.hasNext())
            {
                //read name
                namesArr[entriesNum] = saveScanner.next();
                try {
                    //read score
                    scoresArr[entriesNum] = saveScanner.nextInt();
                } catch (Exception e) {
                    System.out.println("can't resolve to a int variable at index: " + entriesNum);
                }
                entriesNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int length = namesArr.length;


        // Create new arrays with increased length
        String[] newNamesArr = new String[length + 1];
        int[] newScoresArr = new int[length + 1];

        System.arraycopy(scoresArr,0,newScoresArr,0,length);
        System.arraycopy(namesArr,0,newNamesArr,0,length);

        int index=0;

        while(index<length && highscore<=newScoresArr[index])
            index++;

        System.arraycopy(newScoresArr,0,scoresArr,0,index-1);
        System.arraycopy(newNamesArr,0,namesArr,0,index-1);


        System.arraycopy(newScoresArr,index,scoresArr,index+1,20);
        System.arraycopy(newNamesArr,index,namesArr,index+1,20);


        namesArr[index]=userName;
        scoresArr[index]=highscore;



        try(PrintWriter writer = new PrintWriter(fileName)) {
                //create printWriter instance with filewriter to write to the same file
                for(int i=0; i<entriesNum+1;i++)
                {
                    writer.write(namesArr[i] + " "+ scoresArr[i]+ "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        namesArr = new String[1001];
        scoresArr = new int[1001];


    }


    public int[] getScoresArr() {
        return scoresArr;
    }

    public String[] getNamesArr() {
        return namesArr;
    }
}