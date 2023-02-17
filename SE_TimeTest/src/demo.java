import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import javax.management.modelmbean.ModelMBean;
import java.lang.reflect.Parameter;

public class demo {


    public static void test(Pairing bp, int n, Element Y1, Element Z1, Element D, Element[] E, Element[] yy, Element[] w, Element W0, Element W1, Element W2, Element tao1, Element tao2, Element tao3) {
        Element e1 = bp.pairing(Y1.mul(Z1), D);

        Element[] F = new Element[n];
        for (int i = 0; i < n; i++) {
            F[i] = bp.pairing(Y1, E[i]).mul(bp.pairing(D,yy[i]));

        }
        Element sum = F[0];
        for (int i = 1; i < n; i++) {
            sum = sum.mul(F[i]);
        }
        Element x = e1.div(sum);

        Element fin = bp.pairing(W1,tao1).mul(bp.pairing(W2,tao3).mul(x));
        if (fin.isEqual(bp.pairing(W0,tao2))) {
                System.out.println("yes");
            }
            else {
                System.out.println("No");
            }

    }


    public static Element decrpt(Pairing bp, int n, Element K, Element C1, Element[] Ci, Element L, Element[] att, Element[] w ){
        Element e1 = bp.pairing(K, C1);
        Element[] e2 =new Element[n];
        Element[] e3 =new Element[n];

        for (int i=0; i<n; i++){
            e2[i]=bp.pairing(Ci[i],L).powZn(w[i]).getImmutable();
        }
        Element sum =e2[0];
        for (int i = 1; i < n; i++) {
            sum = sum.mul(e2[i]);
        }


        for (int i = 1; i < n; i++) {
            e3[i] = bp.pairing(C1, att[i]).powZn(w[i]).getImmutable();
        }
        Element sum2 =e2[0];
        for (int i = 1; i < n; i++) {
            sum2 = sum2.mul(e3[i]);
        }
        Element xx = e1.div(sum.mul(sum2));
        return xx;

    }


    public static void main(String[] args) {

        Pairing bp = PairingFactory.getPairing("a.properties");
        // 二、选择群上的元素
        Field G1 = bp.getG1();
        Field Zr = bp.getZr();
        Element g = G1.newRandomElement().getImmutable();
        Element f = G1.newRandomElement().getImmutable();
        Element g1 = G1.newRandomElement().getImmutable();
        Element a = Zr.newRandomElement().getImmutable();
        Element b = Zr.newRandomElement().getImmutable();
        Element c = Zr.newRandomElement().getImmutable();
        Element al = Zr.newRandomElement().getImmutable();
        Element bt = Zr.newRandomElement().getImmutable();

        Element f1 = g.powZn(c);
        Element f2 = g.powZn(b);


        int n = 20;

        Element[] att = new Element[n];
        for (int i = 0; i < n; i++) {
            att[i] = G1.newRandomElement().getImmutable();
        }


        Element[] w = new Element[n];
        Element[] ld = new Element[n];
        Element s = Zr.newOneElement();
        Element s1 = Zr.newRandomElement().getImmutable();
        Element s2 = Zr.newRandomElement().getImmutable();
        //System.out.println(s);
        for (int i = 0; i < n; i++) {
            w[i] = Zr.newRandomElement().getImmutable();
            ld[i] = Zr.newRandomElement().getImmutable();
            s = s.add(w[i].mul(ld[i]));
        }


//        Element ss = Zr.newZeroElement();
//        System.out.println(ss);
//        System.out.println(s);
//        System.out.println(ss.sub(s));
//        Element sss = s.negate();
//        System.out.println(sss);
        //System.out.println(s);
        Element s0 = Zr.newZeroElement();
        Element ss = s0.sub(s);
//        System.out.println(ss);
//        System.out.println(ss.add(s));


    Element r = Zr.newRandomElement().getImmutable();
    Element R = Zr.newRandomElement().getImmutable();


    Element KW1 = Zr.newRandomElement().getImmutable();
    Element KW2 = KW1;

        Element t = Zr.newRandomElement().getImmutable();
        long  startTime00 = System.currentTimeMillis();
    Element K = g.powZn(al).mul(f.powZn(t));
    Element L = g.powZn(t);
    Element V = g.powZn((a.mul(c).sub(r)).div(b));
    Element Y = g.powZn(r);
    Element[] yx = new Element[n];
        for(int i =0; i<n; i++){
            yx[i] = att[i].powZn(r);
        }

        long endTime00 = System.currentTimeMillis();
        System.out.println("keygen:" + (endTime00 - startTime00) + "ms");


        long  startTime0 = System.currentTimeMillis();    //获取开始时间

        Element tao1 = (g.powZn(a).mul(f2.powZn(KW2))).powZn(R);
        Element tao2 = f1.powZn(R);
        Element tao3 = V.powZn(R);
        Element Y1 =Y.powZn(R);
        Element Z1 =g1.powZn(r.mulZn(R));
        Element[] yy = new Element[n];
        for(int i =0; i<n; i++){
            yy[i] = yx[i].powZn(R);
        }

        long endTime0 = System.currentTimeMillis();
        System.out.println("token:" + (endTime0 - startTime0) + "ms");





        long  startTime1 = System.currentTimeMillis();    //获取开始时间

        Element W0 = g.powZn(s1.mul(s2)).mul(f2.powZn(s1.mul(KW1)));
        Element W1 = f1.powZn(s1);
        Element W2 = f2.powZn(s2);
        Element C2 = g.powZn(bt.mulZn(s));

        Element C1 = g.powZn(s);
        Element[] Ci = new Element[n];
        for(int i=0;i<n;i++){
            Ci[i]=(f.powZn(ld[i])).mul(att[i].powZn(ss));
        }

        Element D = g.powZn(s);
        Element[] E = new Element[n];

        for(int i =0; i<n; i++){
            E[i] = (g1.powZn(ld[i])).mul((att[i]).powZn(ss));

        }



        long endTime1 = System.currentTimeMillis();
        System.out.println("encrypt:" + (endTime1 - startTime1) + "ms");


        long  startTime = System.currentTimeMillis();    //获取开始时间

        //test(bp, n, Y1, Z1, D, E, yy, w, W0,  W1, W2, tao1, tao2, tao3);
        Element xx = decrpt(bp,n,K,C1,Ci,L,att,w);

//        if (xx.isEqual(bp.pairing(g,g).powZn(al.mulZn(s)))) {
//            System.out.println("yes");
//        }
//        else {
//            System.out.println("No");
//        }

        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("decrypt:" + (endTime - startTime) + "ms");    //输出程序运行时间

    }
}