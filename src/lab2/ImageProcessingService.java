package lab2;

import java.awt.*;
import java.io.IOException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ImageProcessingService implements RemoteImageProcessingService {

    public final static String BINDING_NAME = "rmi://ImageProcessingService";
    private final int WINDOW_SIZE = 3;
    private final int gap = WINDOW_SIZE/2;
    private final double [][] WINDOW = {{1.0/9, 1.0/9, 1.0/9}, {1.0/9, 1.0/9, 1.0/9}, {1.0/9, 1.0/9, 1.0/9}};

    @Override
    public Object processImage(Color[][] sourceImage) throws IOException {
        System.out.println("Start image processing");
        int height = sourceImage.length;
        int width = sourceImage[0].length;

        //Создаем расширенную матрицу
        Color[][] tmpImage = new Color[height + 2*gap][width + 2*gap];
        int tmpHeight = height + 2*gap;
        int tmpWidth = width + 2*gap;

        //Заполнение временного расширенного изображения
        //Заполняем углы
        for (int i = 0; i < gap; i++) {
            for(int j = 0; j < gap; j++) {
                tmpImage[i][j] = sourceImage[0][0];
                tmpImage[i][tmpWidth - 1 - j] = sourceImage[0][width - 1];
                tmpImage[tmpHeight - 1 - i][j] = sourceImage[height - 1][0];
                tmpImage[tmpHeight - 1 - i][tmpWidth - 1 - j] = sourceImage[height - 1][width - 1];
            }
        }

        //Заполняем границы слева и справа
        for(int i = gap; i < tmpHeight - gap; i++) {
            for(int j = 0; j < gap; j++) {
                tmpImage[i][j] = sourceImage[i - gap][j];
                tmpImage[i][tmpWidth - 1 - j] = sourceImage[i - gap][width - 1 - j];
            }
        }

        //Заполняем границы сверху и снизу
        for(int i = 0; i < gap; i++) {
            for(int j = gap; j < tmpWidth - gap; j++) {
                tmpImage[i][j] = sourceImage[i][j - gap];
                tmpImage[tmpHeight - 1 - i][j] = sourceImage[height - 1 - i][j - gap];
            }
        }

        //Заполняем центр
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                tmpImage[i + gap][j + gap] = sourceImage[i][j];
            }
        }

        Color[][] processedImage = new Color[height][width];

        try {
            for (int i = gap; i < tmpHeight - gap; i++) {
                for (int j = gap; j < tmpWidth - gap; j++) {
                    int R = 0;
                    int G = 0;
                    int B = 0;
                    for (int k = 0; k < WINDOW_SIZE; k++) {
                        for (int m = 0; m < WINDOW_SIZE; m++) {
                            Color currentPixel = tmpImage[i - gap + k][j - gap + m];
                            R += currentPixel.getRed() * WINDOW[k][m];
                            G += currentPixel.getGreen() * WINDOW[k][m];
                            B += currentPixel.getBlue() * WINDOW[k][m];
                        }
                    }
                    if (R < 0) R = 0;
                    if (R > 255) R = 255;
                    if (G < 0) G = 0;
                    if (G > 255) G = 255;
                    if (B < 0) B = 0;
                    if (B > 255) B = 255;

                    processedImage[i - gap][j - gap] = new Color(R, G, B);
                }
            }
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
        }
        System.out.println("Image processed");
        return processedImage;
    }

    public static void main(String... args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, enter your ip address: ");
        String serverIP = scanner.nextLine();
        try {
            System.out.println("Initializing image processing service...");
            //cоздание объекта для удаленного доступа
            final ImageProcessingService service = new ImageProcessingService();
            //создание реестра расшаренных объектов
            final Registry localRegistry = LocateRegistry.createRegistry(2000);
            //создание "заглушки" – приемника удаленных вызовов
            Remote stub = UnicastRemoteObject.exportObject(service, 0);
            //регистрация "заглушки" в реестре
            localRegistry.bind(BINDING_NAME, service);
            System.out.println("Setting systems parameters...");
            System.setProperty("java.rmi.server.hostname", serverIP);
            System.out.println("Service is ready!");
            while(true) Thread.sleep(Integer.MAX_VALUE);
        } catch (AlreadyBoundException | RemoteException | InterruptedException exc) {
            System.err.println(exc.getMessage());
        }
    }
}