import com.sun.deploy.util.ArrayUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@SuppressWarnings("Duplicates")
public class Main {

    private static Byte[] memory = new Byte[16384];
    private static int[][] op = new int[12][9];

    public static void main(String[] args) throws IOException {
        Arrays.fill(memory, (byte) 0);
        createOpTable(op);
        boolean endExecution = false;
        int pc = 12288;
        int sp = 12284;

        readBytes(args[0], args[1]);

        while (!endExecution)
        {
            int[] ir = searchInstruction(pc);
            pc += 4;
            swapBytes(ir);
            System.out.println("Op: "+getInterval(ir, 0, 5));
            endExecution = true;
        }
    }

    private static int[] searchInstruction(int pc)
    {
        int[] ir = new int[32];
        int ubyte;
        for (int i = 3; i >= 0; i--) {
            ubyte = Byte.toUnsignedInt(memory[pc+i]);
            byteToBits(ir, i*8, ubyte);
        }

        /*for (int i = 0; i < 32; i++) {
            if (i!=0 && i % 8 == 0)
            {
                System.out.println();
            }
            System.out.print(ir[i]);
        }*/
//        System.out.println();
        return ir;
    }

    private static void byteToBits(int[] ir, int pos, int ubyte)
    {
        for (int i = 7; i >= 0; i--) {
            if (ubyte % 2 == 0)
            {
                ir[pos+i] = 0;
                ubyte = ubyte/2;
            } else {
                ir[pos+i] = 1;
                ubyte = (ubyte-1)/2;
            }
        }
    }

    private static void readBytes(String inTextName, String inDataName) throws IOException
    {
        FileInputStream inText = null;
        FileInputStream inData = null;
        int pc = 12288;
        int sp = 0;
        int c;

        try {
            inText = new FileInputStream(inTextName);

            while ((c = inText.read()) != -1) {
                byte b = (byte) c;
                memory[pc] = b;
                pc++;
            }
        } finally {
            if (inText != null) {
                inText.close();
            }
        }

        try {
            inData = new FileInputStream(inDataName);

            while ((c = inData.read()) != -1) {
                byte b = (byte) c;
                memory[sp] = b;
                sp++;
            }
        } finally {
            if (inData != null) {
                inData.close();
            }
        }
    }

    private static int getInterval(int[] array, int init, int end)
    {
        int pot = 1;
        int result = 0;
        for (int i = end; i >= init; i--) {
            result += array[i] * pot;
            pot *= 2;
        }
        return result;
    }

    private static void swapBytes(int[] array)
    {
        int aux[][] = new int[4][8];
        for (int i = 0; i < 4; i++) {
            int count = 0;
            for (int j = i*8; j < 8*(i+1); j++) {
                aux[i][count] = array[j];
                System.out.print(aux[i][count]);
                count++;
            }
            System.out.println();
        }

        int j = 3;
        for (int i = 0; i < 32; i++) {
            array[i] = aux[j][i % 8];
            if (i % 8 == 7)
            {
                j--;
            }
            System.out.print(array[i]);
        }
        System.out.println();
    }

    private static void createOpTable(int[][] op)
    {
        Arrays.fill(op, -1);
        op[0][1] = 32;
        op[0][2] = 34;
        op[0][3] = 36;
        op[0][4] = 37;
        op[0][5] = 39;
        op[0][6] = 0;
        op[0][7] = 2;
        op[0][8] = 42;
        op[0][9] = 8;
    }
}
