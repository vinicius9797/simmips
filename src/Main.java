import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@SuppressWarnings("Duplicates")
public class Main {

    private static Byte[] memory = new Byte[16384];
    private static int[] op = {0, 8, 12, 13, 35, 43, 15, 4, 5, 10, 2, 3};
    private static int pc = 12288;
    private static int[] funct = {32, 34, 36, 37, 39, 0, 2, 42, 8, 12};
    private static int[] regs = new int[32];
    private static int rd = 0;
    private static int rs = 0;
    private static int rt = 0;
    private static int imm = 0;
    private static int shamt = 0;
    private static int address = 0;

    public static void main(String[] args) throws IOException {

        Arrays.fill(memory, (byte) 0);
        Arrays.fill(regs, 0);

        int sp = 12284;
        int instQuantity = readBytes(args[0], args[1]);;

        while (pc < instQuantity)
        {
            int[] ir = searchInstruction(pc);
            pc += 4;
            swapBytes(ir);
            decodeInstruction(ir);
        }
    }

    /**
     * Busca instrução na memória a partir do valor de pc
     * @param pc posição inicial da instrução
     * @return
     */
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

    private static int readBytes(String inTextName, String inDataName) throws IOException
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

        return pc;
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
//                System.out.print(aux[i][count]);
                count++;
            }
//            System.out.println();
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

    public static void decodeInstruction(int[] ir)
    {
        int irOp = getInterval(ir, 0, 5);
        int irFunct = -1;
        if (irOp == 0)
        {
            irFunct = getInterval(ir, 25, 31);
        }
        System.out.println("irFunct e OP = " + irFunct + " " + irOp);

        switch (irOp)
        {
            case 0:
                switch (irFunct)
                {
                    case 32:
                        add(ir);
                        break;
                    case 34:
                        sub(ir);
                        break;
                }
                break;

            case 8:
                addi(ir);
                break;
        }
    }

    private static void add(int[] ir)
    {
        r(ir);
        regs[rd] = regs[rs] + regs[rt];
    }

    private static void sub(int[] ir)
    {
        r(ir);
        regs[rd] = regs[rs] - regs[rt];
    }

    private static void addi(int[] ir)
    {
        i(ir);

        System.out.println("imm = " + imm);
        System.out.println("rs = " + rs);
        System.out.println("rt = " + rt);

        regs[rt] = regs[rs] + imm;
        System.out.println("regsRT = " + regs[rt]);
    }

    private static void and(int[] ir)
    {
        r(ir);
    }
    private static void or(int[] ir)
    {
        r(ir);
    }
    private static void nor(int[] ir)
    {
        r(ir);
    }

    private static void i(int[] ir)
    {
        rs = getInterval(ir, 6, 10);
        rt = getInterval(ir, 11, 15);
        imm = getInterval(ir, 16, 31);
    }

    private static void r(int[] ir)
    {
        rs = getInterval(ir, 6, 10);
        rt = getInterval(ir, 11, 15);
        rd = getInterval(ir, 16, 20);
        shamt = getInterval(ir, 21, 25);
    }

    private static void j(int[] ir)
    {
        address = getInterval(ir, 6, 31);
    }
}
