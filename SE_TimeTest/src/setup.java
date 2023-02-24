import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a1.TypeA1CurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.util.ElementUtils;

public class setup {


    public static void main(String[] args) {

// JPBC Type A1 pairing generator...
        TypeA1CurveGenerator  pg = new TypeA1CurveGenerator(
                4,  // the number of primes
                256 // the bit length of each prime
        );

        PairingParameters typeA1Params = pg.generate();
        Pairing pairing = PairingFactory.getPairing(typeA1Params);

        Field G1 = pairing.getG1();
        Field Zp = pairing.getZr();




        Element generator = G1.newRandomElement().getImmutable();


        Element g = ElementUtils.getGenerator(pairing, generator , typeA1Params, 0, 4).getImmutable();
        Element h = ElementUtils.getGenerator(pairing, generator , typeA1Params, 0, 4).getImmutable();
//        Element X_3 = ElementUtils.getGenerator(pairing, generator , typeA1Params, 2, 4).getImmutable();
//        Element X_4 = ElementUtils.getGenerator(pairing, generator , typeA1Params, 3, 4).getImmutable();
        Element Z = ElementUtils.getGenerator(pairing, generator , typeA1Params, 3, 4).getImmutable();

        Element alf = Zp.newRandomElement().getImmutable();
        Element a = Zp.newRandomElement().getImmutable();

        long  startTime = System.currentTimeMillis();    //获取开始时间


        Element g_a = g.powZn(a);
        Element e_gg = pairing.pairing(g,g).powZn(alf);
        Element H = h.mul(Z);





        int n =5;
        Element[] u = new Element[n];
        for (int i=0;i<n;i++){
            u[i] = ElementUtils.getGenerator(pairing, generator , typeA1Params, 0, 4).getImmutable();

        }

        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println((endTime - startTime) + "ms");    //输出程序运行时间


    }
}
