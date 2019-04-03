package lab2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {

    public static void main(String... args) {
        Scanner scanner = new Scanner(System.in);
        File sourceFile;
        File resultFile;
        String resultFilePath;
        String sourceFilePath;
        RemoteImageProcessingService service = null;
        int width = 0;
        int height = 0;
        Color[][] imagesColors = null;
        boolean connection;
        //соединяемся с rmi сервисом
        do {
            try {
                System.out.println("Please, enter server ip address: ");
                String serverIP = scanner.nextLine();
                Registry registry = LocateRegistry.getRegistry(serverIP, 2000);
                service = (RemoteImageProcessingService) registry.lookup("rmi://ImageProcessingService");
                connection = true;
            } catch (RemoteException | NotBoundException exc) {
                System.err.println(exc.getMessage());
                connection = false;
            }
        } while(!connection);

        do {
            boolean isRead = false;

            //считываем данные из исходного файла
            do {
                System.out.println("Enter source image: ");
                sourceFilePath = scanner.nextLine();
                System.out.println("Enter result files path (png): ");
                resultFilePath = scanner.nextLine();
                sourceFile = new File(sourceFilePath);
                resultFile = new File(resultFilePath);
                // проверяем введенные данные на корректность
                if((sourceFile.isFile() && sourceFile.canRead() && resultFile.isFile() && resultFile.canWrite())) {
                    try {
                        BufferedImage tmpImage = ImageIO.read(sourceFile);
                        isRead = true;
                        //Преобразуем одномерный массив цветов в двумерный массив цветов
                        width = tmpImage.getWidth();
                        height = tmpImage.getHeight();
                        int[] tempImagesIntArray = tmpImage.getRGB(0, 0, width, height, null, 0, width);
                        imagesColors = new Color[height][width];

                        for(int i = 0; i < height; i++)
                            for(int j = 0; j < width; j++)
                                imagesColors[i][j] = new Color(tempImagesIntArray[i*width + j]);
                        } catch (IOException exc) {
                            System.err.println(exc.getMessage());
                            isRead = false;
                        }
                } else {
                    System.out.println("Sorry, cant find files. Check your input and try again.");
                }
            } while (!isRead);

            //вызываем удаленный метод и записываем результаты в файл
            try {
                System.out.println("Please, wait. Server process your image...");
                Color[][] renderedImagesColors = (Color[][])service.processImage(imagesColors);

                BufferedImage resultImage = new BufferedImage(width, height, 1); // imageType = 1 It is RGB image

                int[] imagesIntArray = new int[width*height];

                for(int i = 0; i < height; i++)
                    for(int j = 0; j < width; j++)
                        imagesIntArray[i*width + j] = renderedImagesColors[i][j].getRGB();
                // сохраняем изображение в указанный файл
                resultImage.setRGB(0, 0, width, height, imagesIntArray, 0, width);
                ImageIO.write(resultImage, "png", resultFile);
                System.out.println("Procedure is complete.\nImage saved into " + resultFilePath + " from " + sourceFilePath);
            } catch (IOException exc) {
                System.err.println(exc.getMessage());
            }
        } while (true);
    }
}
