package sk.calvary.misc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Inputstream that store file size and readed bytes. Useful to show progress of
 * reading file.
 */

public class CountableInputStream extends InputStream {

    private InputStream in = null;

    public String name = "";

    //
    public long length = -1;

    public long read = 0;

    //
    public CountableInputStream(InputStream is) throws IOException {
        in = is;
    }

    public CountableInputStream(InputStream is, String fileName)
            throws IOException {
        this.in = is;
        this.name = fileName;
        length = (new File(fileName)).length();
    }

    public CountableInputStream(URL url) throws IOException {
        try {
            URLConnection uc = url.openConnection();
            length = uc.getContentLength();
            name = url.toExternalForm();
            this.in = new BufferedInputStream(uc.getInputStream()); //url.openStream();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public int available() throws IOException {
        return in.available();
    }

    public void close() throws IOException {
        in.close();
    }

    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
    }

    public boolean markSupported() {
        return in.markSupported();
    }

    public int read() throws java.io.IOException {
        int ret = in.read();
        read++;
        return ret;
    }

    public int read(byte b[]) throws IOException {
        int size = in.read(b);
        read += size;
        return size;
    }

    public int read(byte b[], int off, int len) throws IOException {
        int size = in.read(b, off, len);
        read += size;
        return size;
    }

    public synchronized void reset() throws IOException {
        in.reset();
    }

    public long skip(long n) throws IOException {
        return in.skip(n);
    }
}