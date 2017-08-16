package am.englet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import am.englet.link.FinalLink;

public class flt {

    /**
     * @param args
     * @throws Exception
     * @throws FileNotFoundException
     */
    public static void main(final String[] args) throws FileNotFoundException,
            Exception {
        // TODO Auto-generated method stub
        final Object l = new FinalLink("rrr", null);
        Utils.outPrintln(System.out, new File("").getAbsolutePath());
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                new FileOutputStream("lll"));
        objectOutputStream.writeObject(l);
        objectOutputStream.close();
        final ObjectInputStream objectInputStream = new ObjectInputStream(
                new FileInputStream("lll"));
        final Object readObject = objectInputStream.readObject();
        System.out.println(readObject);
    }

}
