import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@SuppressWarnings("Duplicates")
public class Main {

    private static Byte[] memory = new Byte[16384];

    public static void main(String[] args) throws IOException {
        Arrays.fill(memory, (byte) 0);
        boolean endExecution = false;
        int pc = 12288;
        int sp = 12284;

        System.out.println(Integer.parseInt(Integer.toBinaryString(32)));

        System.exit(0);

        readBytes(args[0], args[1]);

        while (!endExecution)
        {
            int[] ir = searchInstruction(pc);
            pc += 4;
            endExecution = true;
        }
    }

    private static int[] searchInstruction(int pc)
    {
        int[] ir = new int[32];
        int ubyte;
        for (int i = 0; i < 4; i++) {
            ubyte = Byte.toUnsignedInt(memory[pc+i]);
            byteToBits(ir, i*8, ubyte);
        }

        for (int i = 0; i < 32; i++) {
            System.out.print(ir[i]);
        }
        System.out.println();
        return ir;
    }

    private static void byteToBits(int[] ir, int pos, int ubyte)
    {
        for (int i = 0; i < 8; i++) {
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
        int radix = 4;

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

        return init;
    }
}
