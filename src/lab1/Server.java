package lab1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
Сервер получает от клиента 2 матрицы
Выполняют операцию сложения матриц
Отправляет результат сложения матриц клиенту и завершает работу
 */

public class Server {
    public static void main(String args[]) {
        ServerSocket serverSocket;
        Socket clientSocket;
        ObjectInputStream inSocket = null;
        ObjectOutputStream outSocket = null;
        try {
            serverSocket = new ServerSocket(4004);
            while (true) {
                clientSocket = serverSocket.accept();
                try {
                    outSocket = new ObjectOutputStream(clientSocket.getOutputStream());
                    inSocket = new ObjectInputStream(clientSocket.getInputStream());
                    Matrix matrix1 = new Matrix(Matrix.serializationRead(inSocket));
                    Matrix matrix2 = new Matrix(Matrix.serializationRead(inSocket));
                    Matrix.serializationWrite(Matrix.sum(matrix1, matrix2).getMatrixMassive(), outSocket);
                } finally {
                    if (inSocket != null) inSocket.close();
                    if (outSocket != null) outSocket.close();
                    if (clientSocket != null) clientSocket.close();
                }
            }
        }catch (IOException | MatrixException | ClassNotFoundException exc) {
                System.err.println(exc.getLocalizedMessage());
        }
    }
}
