/**
 * 04.05.2010
 * 
 * 1
 * 
 */
package am.englet;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

import am.englet.inputtokenizers.BackAdapterBasedIterator;
import am.englet.inputtokenizers.EngletParserReaderToTokenizerAdapter;
import am.englet.inputtokenizers.ReaderToTokenizerAdapter;
import am.englet.link.backadapters.LineReaderStrategy;

public final class EngletParserReaderTokenizerFactory implements
        ServiceTokenizerFactory {
    public Iterator forObject(final Object object) {
        return (object instanceof Reader) ? new BackAdapterBasedIterator(
                new EngletParserReaderToTokenizerAdapter((Reader) object),
                new LineReaderStrategy()) : forObject(new StringReader(object
                .toString()));
    }

    public static void main(final String[] args) throws Exception {
        Utils.outPrintln(System.out, new File("").getAbsolutePath());
        final ReaderToTokenizerAdapter a = new EngletParserReaderToTokenizerAdapter(
                new FileReader("ReaderToTokenizerAdapterTest"));
        final BackAdapterBasedIterator i = new BackAdapterBasedIterator(a,
                new LineReaderStrategy());
        // final Object b;
        while (i.hasNext())
            Utils.outPrintln(System.out, "-" + i.next());
        // while ((b = a.getNext()) != null)
        // System.out.println("-" + b);
    }
}