package es.codeurjc.web.nitflex;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ImageTestUtils {

    public static boolean areSameBlob(Blob blob1, Blob blob2) throws SQLException, IOException {
        if (blob1 == null && blob2 == null) {
            return true; // Both blobs are null, consider them equal
        }
    
        if (blob1 == null || blob2 == null) {
            return false; // One of the blobs is null, they are not equal
        }
    
        if (blob1.length() != blob2.length()) {
            return false; // Blobs have different lengths, they are not equal
        }
    
        InputStream is1 = blob1.getBinaryStream();
        InputStream is2 = blob2.getBinaryStream();
    
        try {
            int byte1 = is1.read();
            int byte2 = is2.read();
    
            while (byte1 != -1 && byte2 != -1) {
                if (byte1 != byte2) {
                    return false; // Bytes are different, blobs are not equal
                }
                byte1 = is1.read();
                byte2 = is2.read();
            }
    
            return byte1 == byte2; // If both streams reached the end at the same time, blobs are equal
        } finally {
            is1.close();
            is2.close();
        }
    }

    public static MultipartFile createSampleImage() {
        try {
            File file = new File("images/deadpool.jpg");
            FileInputStream input = new FileInputStream(file);
            return new MockMultipartFile("deadpool.jpg","deadpool.jpg", "image/jpeg", input);
        } catch (IOException e) {
            fail("Error creating sample image");
            return null;
        }
    }



}
