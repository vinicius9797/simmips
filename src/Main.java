import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Compilação: javac Main.java
 * Execução: java Main ./programa.text ./programa.data
 *
 * Alunos: Vinicius Eduardo Manduca Ferreira
 *         Larissa Leal
 */

@SuppressWarnings("Duplicates")


public class Main {

    private static Byte[] memory = new Byte[16384];
    private static int[] op = {0, 8, 12, 13, 35, 43, 15, 4, 5, 10, 2, 3, 32, 40};
    private static int pc = 12288;
    private static int[] funct = {32, 34, 36, 37, 39, 0, 2, 42, 8, 12, 24, 26, 16, 18};
    private static int[] regs = new int[32];
    private static int rd = 0;
    private static int rs = 0;
    private static int rt = 0;
    private static int hi = 0;
    private static int lo = 0;
    private static int imm = 0;
    private static int shamt = 0;
    private static int address = 0;
    private static int irFunct = 0;
    private static int irOp = 0;

    private static BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {

        Arrays.fill(memory, (byte) 0);
        Arrays.fill(regs, 0);

        regs[29] = 12284;
        regs[28] = 0;
        int instQuantity = readBytes(args[0], args[1]);

        while (pc < instQuantity)
        {
            int[] ir = searchInstruction(pc);
            decodeInstruction(ir);
            runInstruction(ir);
            if ((irOp != 2) && (irOp != 3) && !(irOp == 0 && irFunct == 8))
            {
                pc += 4;
            }
            //System.out.println(regs[28]);
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

        swapBytes(ir);

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

    private static int[] intToBits(int num, int r)
    {
        int[] aux = new int[r];
        for (int i = r-1; i >= 0; i--) {
            if (num % 2 == 0)
            {
                aux[i] = 0;
                num = num/2;
            } else {
                aux[i] = 1;
                num = (num-1)/2;
            }
        }

        return aux;
    }

    private static int readBytes(String inTextName, String inDataName) throws IOException
    {
        FileInputStream inText = null;
        FileInputStream inData = null;
        int pc = 12288;
        regs[28] = 0;
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

            int zeroCount = 0;
            while ((c = inData.read()) != -1) {
                byte b = (byte) c;
                if (b == 0)
                {
                    zeroCount++;
                } else {
                    zeroCount = 0;
                }

                if (zeroCount == 4)
                {
                    break;
                }

                memory[regs[28]] = b;
                regs[28]++;
            }
        } finally {
            if (inData != null) {
                inData.close();
            }
        }

        return pc;
    }

    private static boolean complementByTwo(int[] array, int init, int end) {
        if (array[init] == 1) {
            for (int i = init; i <= end; i ++)
                array[i] = array[i] == 1 ? 0 : 1;
            int vaium = 1;
            for (int i = end; i >= init; i --) {
                if (array[i] == 0 && vaium == 1) {
                    array[i] = 1;
                    vaium = 0;
                }
                else {
                    if (array[i] == 1 && vaium == 1) {
                        array[i] = 1;
                        vaium = 1;
                    }
                    else {
                        if (array[i] == 1 && vaium == 0) {
                            array[i] = 1;
                            vaium = 0;
                        }
                    }
                }
            }
            return true;
        }

        return false;
    }
    
    private static void signExtend(int[] array, int init, int end)
    {
        int i;
        for (i = init; i+16 < 32; i++) {
            array[i] = array[i+16];
        }

        for (; i < 32; i++) {
            array[i] = 0;
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
                count++;
            }
        }

        int j = 3;
        for (int i = 0; i < 32; i++) {
            array[i] = aux[j][i % 8];
            if (i % 8 == 7)
            {
                j--;
            }
//            System.out.print(array[i]);
        }
//        System.out.println();
    }

    private static void runInstruction(int[] ir) throws IOException
    {
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
                    case 24:
                        mult(ir);
                        break;
                    case 26:
                        divide(ir);
                        break;
                    case 16:
                        mfhi(ir);
                        break;
                    case 18:
                        mflo(ir);
                        break;
                    case 36:
                        and(ir);
                        break;
                    case 37:
                        or(ir);
                        break;
                    case 38:
                        xor(ir);
                        break;
                    case 39:
                        nor(ir);
                        break;
                    case 0:
                        sll(ir);
                        break;
                    case 2:
                        srl(ir);
                        break;
                    case 42:
                        slt(ir);
                        break;
                    case 8:
                        jr(ir);
                        break;
                    case 12:
                        syscall(ir);
                }
                break;

            case 8:
                addi(ir);
                break;
            case 12:
                andi(ir);
                break;
            case 13:
                ori(ir);
                break;
            case 35:
                lw(ir);
                break;
            case 43:
                sw(ir);
                break;
            case 32:
                lb(ir);
                break;
            case 40:
                sb(ir);
                break;
            case 15:
                lui(ir);
                break;
            case 4:
                beq(ir);
                break;
            case 5:
                bne(ir);
                break;
            case 10:
                slti(ir);
                break;
            case 2:
                jump(ir);
                break;
            case 3:
                jal(ir);
                break;
        }
    }

    private static void decodeInstruction(int[] ir)
    {
        irOp = getInterval(ir, 0, 5);
        irFunct = -1;
        if (irOp == 0)
        {
            irFunct = getInterval(ir, 25, 31);
        }
//        System.out.println("irFunct e OP = " + irFunct + " " + irOp);

        
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

//        System.out.println("imm = " + imm);
//        System.out.println("rs = " + rs);
//        System.out.println("rt = " + rt);
        if(imm < 0){
        for (int i = 0; i < ir.length; i++) {
            System.out.print(ir[i]);
        }
        System.out.println();}

            regs[rt] = regs[rs] + imm;


//        System.out.println("regsRT = " + regs[rt]);
    }

    private static void mult(int[] ir)
    {
        r(ir);
        hi = regs[rs] * regs[rt];
        lo = regs[rs] * regs[rt];
    }

    private static void divide(int[] ir)
    {
        r(ir);
        lo = regs[rs] / regs[rt];
        hi = regs[rs] % regs[rt];
    }

    private static void mfhi(int[] ir)
    {
        r(ir);
        regs[rd] = hi;
    }

    private static void mflo(int[] ir)
    {
        r(ir);
        regs[rd] = lo;
    }

    private static void and(int[] ir)
    {
        r(ir);
        regs[rd] = regs[rs] & regs[rt];
    }

    private static void or(int[] ir)
    {
        r(ir);
        regs[rd] = regs[rs] | regs[rt];
    }

    private static void xor(int[] ir)
    {
        r(ir);
        regs[rd] = regs[rs] ^ regs[rt];
    }

    private static void nor(int[] ir)
    {
        r(ir);
        regs[rd] = ~(regs[rs] | regs[rt]);
    }

    private static void andi(int[] ir)
    {
        i(ir);

        regs[rt] = regs[rs] & imm;
    }

    private static void ori(int[] ir)
    {
        i(ir);

        regs[rt] = regs[rs] | imm;
    }

    private static void sll(int[] ir)
    {
        r(ir);
        regs[rd] = regs[rs] << imm;
    }

    private static void srl(int[] ir)
    {
        r(ir);
        regs[rd] = regs[rs] >> imm;
    }

    private static void lw(int[] ir)
    {
//        System.out.println(regs[29]);
//        System.out.println(pc);
        signExtend(ir, 7, 31);
        i(ir);
        regs[rt] = memory[regs[rs] + imm];
    }

    private static void sw(int[] ir)
    {
        i(ir);
//        System.out.println("regs[rs] = " + regs[rs]);
//        System.out.println("imm = " + imm);
//        System.out.println("regs[rs]+imm = " + (regs[rs]+imm));
        memory[regs[rs] + imm] = (byte) regs[rt];
    }

    private static void lb(int[] ir)
    {
        i(ir);
        regs[rt] = memory[regs[rs]+imm];
    }

    private static void sb(int[] ir)
    {
        i(ir);
        memory[regs[rs]+imm] = (byte) regs[rt];

    }

    private static void lui(int[] ir)
    {
        i(ir);
        //TODO: Correct
        regs[rt] = imm;
    }

    private static void beq(int[] ir)
    {
        i(ir);


        if (regs[rs] == regs[rt])
        {
            pc = pc + 4 + (imm << 2);
        }
    }

    private static void bne(int[] ir)
    {
        i(ir);
        if (regs[rs] != regs[rt])
        {
            pc = pc + 4 + (imm << 2);
        }
    }

    private static void slt(int[] ir)
    {
        r(ir);
        if (regs[rs] < regs[rt])
        {
            regs[rd] = 1;
        } else {
            regs[rd] = 0;
        }
    }

    private static void slti(int[] ir)
    {
        i(ir);
        if (regs[rs] < imm)
        {
            regs[rt] = 1;
        } else {
            regs[rt] = 0;
        }
    }

    private static void jump(int[] ir)
    {
        j(ir);
        int[] auxPc = intToBits(pc+4, 32);

        int[] auxAddress = intToBits(address, 26);
//        System.out.println("address = " + address);

//        System.out.println();
        int[] pcConcat = new int[4];

//        System.out.println();

        for (int i = 3; i >= 0; i--) {
            pcConcat[i] = auxPc[i];
        }

        int[] fullAddres = new int[32];
        Arrays.fill(fullAddres, 0);

        for (int i = 0; i < 4; i++) {
            fullAddres[i] = pcConcat[i];
        }

        for (int i = 4; i < 30; i++) {
            fullAddres[i] = auxAddress[i-4];
        }

        fullAddres[30] = 0;
        fullAddres[31] = 0;

//        System.out.println("********************************************");
        address = 0;
        for (int i = 31; i >= 0; i--) {
            if (fullAddres[i] == 1)
            {
                address += Math.pow(2, (-i)+31);
//                System.out.println("address = " + address);
            }
        }
//        System.out.println("********************************************");

//        for (int i = 0; i < fullAddres.length; i++) {
//            System.out.print(fullAddres[i]);
//        }
//        System.out.println();
//
//        System.out.println("address = " + address);

        pc = address;
    }

    private static void jr(int[] ir)
    {
        r(ir);
        pc = regs[rs];
    }

    private static void jal(int[] ir)
    {
        j(ir);
//        System.out.println(address);
        regs[31] = pc+4;
//        System.out.println("regs[31] = " + regs[31]);
//        System.out.println("pc = " + pc);
//        System.out.println("pc+4 = " + (pc+4));
        int[] auxPc = intToBits(pc+4, 32);

        int[] auxAddress = intToBits(address, 26);
//        System.out.println("address = " + address);

//        System.out.println();
        int[] pcConcat = new int[4];

//        System.out.println();

        for (int i = 3; i >= 0; i--) {
            pcConcat[i] = auxPc[i];
        }

        int[] fullAddres = new int[32];
        Arrays.fill(fullAddres, 0);

        for (int i = 0; i < 4; i++) {
            fullAddres[i] = pcConcat[i];
        }

        for (int i = 4; i < 30; i++) {
            fullAddres[i] = auxAddress[i-4];
        }

        fullAddres[30] = 0;
        fullAddres[31] = 0;

//        System.out.println("********************************************");
        address = 0;
        for (int i = 31; i >= 0; i--) {
            if (fullAddres[i] == 1)
            {
                address += Math.pow(2, (-i)+31);
//                System.out.println("address = " + address);
            }
        }
//        System.out.println("********************************************");

//        for (int i = 0; i < fullAddres.length; i++) {
//            System.out.print(fullAddres[i]);
//        }
//        System.out.println();
//
//        System.out.println("address = " + address);
        
        pc = address;
    }

    private static void syscall(int[] ir) throws IOException {
        r(ir);

        //Reg $v0
        int sysop = regs[2];


//        System.out.println("sysop = " + sysop);

        switch (sysop)
        {
            //Print Integer stored at $a0
            case 1:
                System.out.print(regs[4]);
                break;

            //Print String stored at $a0
            case 4:
                for (int i = regs[4]; memory[i] > 0 ; i++) {
                    System.out.print((char) Integer.parseInt(Byte.toString(memory[i])));
                }
                break;

            //Read Integer stored at $v0
            case 5:
                int x = Integer.parseInt(bf.readLine());
                regs[2] = x;
//                System.out.println("regs[4] = " + regs[2]);
                break;

            //Read String stored at $a0
            case 8:
                int address = regs[4];
                int length = regs[5];

                break;

            //Terminate Execution
            case 10:
                System.exit(0);

        }
    }

    private static void i(int[] ir)
    {
        rs = getInterval(ir, 6, 10);
        rt = getInterval(ir, 11, 15);

        boolean negative = complementByTwo(ir, 16, 31);
        imm = getInterval(ir, 16, 31);

        if (negative)
        {
            imm *= -1;
        }
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
