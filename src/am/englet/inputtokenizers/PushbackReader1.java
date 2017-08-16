package am.englet.inputtokenizers;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

class PushbackReader1 extends PushbackReader {
    final StringBuffer buf1 = new StringBuffer();
    int offset = 0;
    boolean closed = false;

    PushbackReader1(final Reader in) {
        super(in);
    }

    public int read() throws IOException {
        synchronized (lock) {
            ensureOpen();
            if (offset < buf1.length()) {
                final char res = buf1.charAt(offset++);
                checkBufferNeed();
                return res;
            } else
                return super.read();
        }
    }

    private void checkBufferNeed() {
        if (offset == buf1.length())
            buf1.setLength(offset = 0);
    }

    private void ensureOpen() throws IOException {
        if (closed)
            throw new IOException("Stream closed");
    }

    public void close() throws IOException {
        super.close();
        closed = true;
    }

    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        // TODO Auto-generated method stub
        synchronized (lock) {
            ensureOpen();
            try {
                final CharBuffer charBuffer = new CharBuffer(cbuf, off, len);
                if (!charBuffer.isFillable())
                    return 0;
                final int doRead = doRead(charBuffer);
                checkBufferNeed();
                return doRead;
            } catch (final ArrayIndexOutOfBoundsException e) {
                throw new IndexOutOfBoundsException(e.getMessage());
            }
        }
    }

    private int doRead(final CharBuffer charBuffer) throws IOException {
        final int readFromBuffer = readFromBuffer(charBuffer);
        if (!charBuffer.isFillable())
            return readFromBuffer;
        final int readFromStream = readFromStream(charBuffer);
        return (readFromStream == -1) ? (readFromBuffer == 0) ? -1 : readFromBuffer : readFromBuffer + readFromStream;

    }

    private int readFromStream(final CharBuffer charBuffer) throws IOException {
        return super.read(charBuffer.getCbufs(), charBuffer.getOff(), charBuffer.getLen());
    }

    private int readFromBuffer(final CharBuffer charBuffer) {
        final int toRead = charBuffer.getCutToLen(buf1.length() - offset);
        if (toRead > 0) {
            final int offset2 = offset + toRead;
            buf1.getChars(offset, offset2, charBuffer.getCbufs(), charBuffer.getOff());
            offset = offset2;
            charBuffer.moveOff(toRead);
        }
        return toRead;
    }

    public static class CharBuffer {
        private final char[] cbufs;
        private int off;
        private int len;

        public CharBuffer(final char[] cbufs, final int off, final int len) {
            if (len < 0)
                throw new IndexOutOfBoundsException("len < 0: " + len);
            else if ((off < 0) || (off > cbufs.length))
                throw new IndexOutOfBoundsException("(off < 0) || (off > cbuf.length): " + off + ", " + cbufs.length);
            this.cbufs = cbufs;
            this.off = off;
            this.len = len;
        }

        public void moveOff(final int count) {
            off += count;
            len -= count;
        }

        public int getCutToLen(final int count) {
            return len < count ? len : count;
        }

        public char[] getCbufs() {
            return cbufs;
        }

        public int getOff() {
            return off;
        }

        public int getLen() {
            return len;
        }

        public boolean isFillable() {
            return len > 0;
        }
    }

    public void unread(final int c) throws IOException {
        synchronized (lock) {
            ensureOpen();
            if (offset == 0)
                buf1.insert(0, (char) c);
            else
                buf1.setCharAt(--offset, (char) c);
        }
    }

    public void unread(final char[] cbuf, final int off, final int len) throws IOException {
        // TODO Auto-generated method stub
        synchronized (lock) {
            ensureOpen();
            unread0(cbuf, off, len);
        }
    }

    private void unread0(final char[] cbuf, final int off, final int len) throws IOException {
        if (offset == 0)
            buf1.insert(0, cbuf, off, len);
        else if (len > offset) {
            final int l1 = len - offset;
            unread0(cbuf, off + l1, offset);
            unread0(cbuf, off, l1);
        } else {
            final int end = offset;
            buf1.replace(offset = offset - len, end, new String(cbuf, off, len));
        }
    }

    public void unread(final char[] cbuf) throws IOException {
        unread(cbuf, 0, cbuf.length);
    }
}