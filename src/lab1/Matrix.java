package lab1;

import java.io.*;

public class Matrix implements Serializable {

    private int columnsLength;
    private int rowsLength;

    private double[][] matrix;

    public double[][] getMatrixMassive() {
        return  matrix;
    }

    Matrix() {
        rowsLength = 1;
        columnsLength = 1;

        matrix = new double[rowsLength][columnsLength];
    }

    Matrix(int columnsLength, int rowsLength) throws MatrixException {
        if(columnsLength < 0 || rowsLength < 0)
            throw new MatrixException("Неправильные размеры матрицы");
        this.columnsLength = columnsLength;
        this.rowsLength = rowsLength;

        matrix = new double[rowsLength][columnsLength];
    }

    Matrix(int size) throws MatrixException {
        this(size, size);
    }

    Matrix(double[][] matrix) {
        if(matrix.length > 0) {
            this.rowsLength = matrix.length;
            this.columnsLength = matrix[0].length;
            this.matrix = new double[rowsLength][columnsLength];
            for(int i = 0; i < matrix.length; i++)
                for(int j = 0; j < matrix[0].length; j++)
                    this.matrix[i][j] = matrix[i][j];
        } else
            this.matrix = new double[0][0];
    }

    public int getColumnsLength() {
        return columnsLength;
    }

    public int getRowsLength() {
        return rowsLength;
    }

    public void set(int row, int column, double value) {
        matrix[row][column] = value;
    }

    public double get(int row, int column) {
        return matrix[row][column];
    }

    public static Matrix sum(Matrix matrix1, Matrix matrix2) throws MatrixException {
        if (matrix1.columnsLength != matrix2.columnsLength || matrix1.rowsLength != matrix2.rowsLength)
            throw new MatrixException("Размеры матриц различны");
        Matrix result = new Matrix(matrix1.columnsLength, matrix1.rowsLength);
        for (int i = 0; i < matrix1.rowsLength; i++)
            for (int j = 0; j < matrix1.columnsLength; j++)
                result.matrix[i][j] = matrix1.matrix[i][j] + matrix2.matrix[i][j];
        return result;
    }

    public static void matrixSerializationWrite(Matrix matrix, ObjectOutputStream out) throws IOException {
        out.writeObject(matrix);
        out.flush();
    }

    public static Matrix matrixSerializationRead(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return (Matrix)in.readObject();
    }

    public static void serializationWrite(double[][] matrix, ObjectOutputStream out) throws IOException {
        out.writeObject(matrix);
        out.flush();
    }

    public static double[][] serializationRead(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return (double[][])in.readObject();
    }

    public static void write(Matrix matrix, Writer out) throws IOException {
        out.write(String.valueOf(matrix.rowsLength));
        out.write('\n');
        out.write(String.valueOf(matrix.columnsLength));
        out.write('\n');
        for(int i = 0; i < matrix.rowsLength; i++) {
            for (int j = 0; j < matrix.columnsLength; j++) {
                out.write(String.valueOf(matrix.matrix[i][j]));
                out.write(' ');
            }
            out.write('\n');
        }
        out.flush();
    }

    public static Matrix read(BufferedReader in) throws IOException, MatrixException {
        String buffer = in.readLine();
        int rowsLength = Integer.parseInt(buffer);
        buffer = in.readLine();
        int columnsLength = Integer.parseInt(buffer);
        Matrix result = new Matrix(columnsLength, rowsLength);
        for(int i = 0; i < result.rowsLength; i++) {
            buffer = in.readLine();
            int position = 0;
            for(int j = 0; j < result.columnsLength; j++) {
                int positionEnd = position;
                while(positionEnd < buffer.length() && buffer.charAt(positionEnd) != ' ') { positionEnd++; }
                double number = Double.parseDouble(buffer.substring(position, positionEnd));
                position = ++positionEnd;
                result.matrix[i][j] = number;
            }
        }
        return result;
    }
}
