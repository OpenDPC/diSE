import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.jpbc.Element;


public class test1 {

    public static void main(String[] args) {
        Pairing bp = PairingFactory.getPairing("a.properties");

        Field G1 = bp.getG1();
        Field Zp = bp.getZr();


        long  startTime = System.currentTimeMillis();    //获取开始时间

        Element g = G1.newRandomElement();

        Element a = Zp.newRandomElement();
        Element b = Zp.newRandomElement();
        Element c = Zp.newRandomElement();
        Element alf = Zp.newRandomElement();

        Element g1 = G1.newRandomElement();
        Element f = G1.newRandomElement();

        Element f1 = g.powZn(c);
        Element f2 = g.powZn(b);





        int n =80;
        Element[] hh = new Element[n];
        for (int i=0;i<n;i++){
             hh[i] = G1.newRandomElement();

        }

        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println((endTime - startTime) + "ms");    //输出程序运行时间


    }

}
