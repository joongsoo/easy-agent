package software.fitz.easyagent.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {

    private IOUtils() {
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            final byte[] buffer = new byte[4096];
            int readCount;

            while ((readCount = inputStream.read(buffer, 0, buffer.length)) != -1) {
                outputStream.write(buffer, 0, readCount);
            }

            outputStream.flush();

            return outputStream.toByteArray();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
