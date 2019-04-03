package lab2;

import java.awt.Color;
import java.io.IOException;
import java.rmi.Remote;

public interface RemoteImageProcessingService extends Remote {

    Object processImage(Color[][] imagesByteArray) throws IOException;

}
