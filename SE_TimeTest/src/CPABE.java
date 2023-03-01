import com.csvreader.CsvWriter;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1CurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.util.ElementUtils;

import java.io.IOException;
import java.nio.charset.Charset;


import java.io.IOException;
import java.nio.charset.Charset;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class CPABE {
    public static void main(String[] args){

        TypeA1CurveGenerator parametersGenerator = new TypeA1CurveGenerator(
                3,  // the number of primes
                517 // the bit length of each prime
        );

        PairingParameters typeA1Params = parametersGenerator.generate();
        Pairing bp= PairingFactory.getPairing(typeA1Params);


        Field GT = bp.getGT();
        Field Zn = bp.getZr();
        Field G = bp.getG1();

        Element generator = G.newRandomElement().getImmutable();

        /*long start=System.currentTimeMillis();   //获取开始时间*/


        //Setup
        Element gp = ElementUtils.getGenerator(bp, generator, typeA1Params, 0, 3).getImmutable();
        Element gr = ElementUtils.getGenerator(bp, generator, typeA1Params, 1, 3).getImmutable();

        Element omega = Zn.newRandomElement().getImmutable();
        Element a = Zn.newRandomElement().getImmutable();

        Element R0 = ElementUtils.getGenerator(bp, generator, typeA1Params, 1, 3).getImmutable();
        Element R1 = ElementUtils.getGenerator(bp, generator, typeA1Params, 1, 3).getImmutable();

        Element A0 = gp.mul(R0);
        Element A1 = (gp.powZn(a)).mul(R1);
        Element Y = bp.pairing(gp,gp).powZn(omega);



        /*long end=System.currentTimeMillis(); //获取结束时间

        System.out.println("程序运行时间： "+(end-start)+"ms");*/

        int temp = 11;
        int n =1;
        String[][] content = new String[temp][2];

        for(int i = 0; i < temp; i++){
            Element[] t = new Element[n];
            Element t_sum = Zn.newZeroElement().getImmutable();

            Element[] D = new Element[n];


            for (int j = 0; j < n; j++){
                t[j] = Zn.newRandomElement().getImmutable();
                t_sum = t_sum.add(t[j]);
                //D[j] = gp.powZn(t[j].div(a));
            }


            long start=System.currentTimeMillis();   //获取开始时间

            Element D0 = gp.powZn(omega.sub(t_sum));

            for (int j = 0; j < n; j++){
                D[j] = gp.powZn(t[j].div(a));
            }

            long end=System.currentTimeMillis(); //获取结束时间

            long spendtime = end - start;

            String s = Long.toString(spendtime);
            String nn = Integer.toString(n);

            System.out.println("程序运行时间： "+spendtime+"ms");

            content[i][0] = nn;
            content[i][1] = s;
            n = n + 10;
        }



        String[] headers = {"attributes", "times"};
        write("test.csv", headers, temp, content);

    }



    public static void write(String filePath, String[] headers , int n, String[][] content){

        //String filePath = "testWrite.csv";

        try {
            // 创建CSV写对象
            CsvWriter csvWriter = new CsvWriter(filePath,',', Charset.forName("GBK"));
            //CsvWriter csvWriter = new CsvWriter(filePath);

            // 写表头
            csvWriter.writeRecord(headers);
            for(int i = 0; i < n; i++){
                csvWriter.writeRecord(content[i]);
            }

            csvWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


