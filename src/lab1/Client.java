package lab1;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/*
Клиент читает 2 матрицы в, представленных в символьном виде, из файла и отсылает их в сериализованном виде на сервер
Сервер возвращает результат операции над этими матрицами
Результат, возвращаемый сервером записывается в файл в символьном виде
Работа программы завершается
 */

public class Client {
    public static void main(String args[]) {
        ObjectInputStream inSocket = null;
        ObjectOutputStream outSocket = null;

        BufferedReader inFile = null;
        BufferedWriter outFile = null;

        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите название файла1");
        String fileNameIn1 = scanner.next();
        System.out.println("Введите название файла2");
        String fileNameIn2 = scanner.next();
        System.out.println("Введите название файла для записи результата");
        String fileNameOut = scanner.next();

        Socket clientSocket = null;
        try {
            try {
                try {
                    inFile = new BufferedReader(
                            new FileReader("C:\\Users\\user\\IdeaProjects\\NetworkingProgrammingTechnology" +
                                    "\\src\\lab1\\files\\" + fileNameIn1 + ".txt"));
                    Matrix matrix1 = Matrix.read(inFile);
                    inFile.close();
                    inFile = new BufferedReader(
                            new FileReader("C:\\Users\\user\\IdeaProjects\\NetworkingProgrammingTechnology" +
                                    "\\src\\lab1\\files\\" + fileNameIn2 + ".txt"));
                    Matrix matrix2 = Matrix.read(inFile);
                    inFile.close();

                    clientSocket = new Socket("localHost", 4004);
                    outSocket = new ObjectOutputStream(clientSocket.getOutputStream());
                    inSocket = new ObjectInputStream(clientSocket.getInputStream());
                    Matrix.serializationWrite(matrix1.getMatrixMassive(), outSocket);
                    Matrix.serializationWrite(matrix2.getMatrixMassive(), outSocket);
                    Matrix result = new Matrix(Matrix.serializationRead(inSocket));
                    outFile = new BufferedWriter(
                            new FileWriter("C:\\Users\\user\\IdeaProjects\\NetworkingProgrammingTechnology" +
                                    "\\src\\lab1\\files\\" + fileNameOut + ".txt"));
                    Matrix.write(result, outFile);
                    outFile.close();
                } finally {
                    if (inFile != null) inFile.close();
                    if (outFile != null) outFile.close();
                    if (inSocket != null) inSocket.close();
                    if (outSocket != null) outSocket.close();
                    if (clientSocket != null) clientSocket.close();
                }
            } catch (IOException | MatrixException | ClassNotFoundException exc) {
                outFile = new BufferedWriter(
                        new FileWriter("C:\\Users\\user\\IdeaProjects\\NetworkingProgrammingTechnology" +
                                "\\src\\lab1\\files\\" + fileNameOut + ".txt"));
                outFile.write("операцию не удалось выполнить");
                if(exc.getLocalizedMessage() != null)
                    System.err.println(exc.getLocalizedMessage());
                else
                    System.err.println("Ошибка");
            } finally {
                if(outFile != null) outFile.close();
            }
        } catch (IOException exc) {
            System.out.println(exc.getLocalizedMessage());
        }
    }
}
