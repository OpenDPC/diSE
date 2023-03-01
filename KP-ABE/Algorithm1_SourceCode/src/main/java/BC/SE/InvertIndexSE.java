package BC.SE;

import BC.SE.entity.GlobalKey;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.*;

public class InvertIndexSE {

    public static void main(String[] args) {

        String curveParams = "type a\n"
                + "q 87807107996633125224377819847540498158068831994142082"
                + "1102865339926647563088022295707862517942266222142315585"
                + "8769582317459277713367317481324925129998224791\n"
                + "h 12016012264891146079388821366740534204802954401251311"
                + "822919615131047207289359704531102844802183906537786776\n"
                + "r 730750818665451621361119245571504901405976559617\n"
                + "exp2 159\n" + "exp1 107\n" + "sign1 1\n" + "sign0 1\n";

        Pairing pairing = PairingFactory.getPairing("a.properties");  //

        Field zr = pairing.getZr();
        Field g1 = pairing.getG1();

        Element mk = zr.newElement().setToRandom();

        GlobalKey globalKey = new GlobalKey();
        globalKey.g = g1.newElement().setToRandom();
        globalKey.g_x = globalKey.g.duplicate();
        globalKey.g_x.mulZn(mk);

        Element sk_d = zr.newElement().setToRandom();
        Element pk_d = globalKey.g.duplicate().mulZn(sk_d);

        Element sk_c = zr.newElement().setToRandom();
        Element pk_c = globalKey.g.duplicate().mulZn(sk_c);

        List<Element> kw_s = new ArrayList<>();
        for (int i = 0; i < 100; i++){
            kw_s.add(g1.newElement().setToRandom());
        }

        // 随机选择5个关键字
        int r = new Random().nextInt(6);

        // 对应index1、index2、token1、token2
        List<Element> i1_s = new ArrayList<>();
        List<Element> i2_s = new ArrayList<>();
        List<Element> t1_s = new ArrayList<>();
        List<Element> t2_s = new ArrayList<>();

        // 100个文件的加密索引
        for (int i = 0; i < 100; i++){
            for (int j = 0; j < 5; j++){
                Element kw = kw_s.get( new Random().nextInt(10));

                Element rj = zr.newElement().setToRandom();
                Element index_1 = pairing.pairing(kw.duplicate().mulZn(sk_d), globalKey.g_x);
                index_1.mul(pairing.pairing(globalKey.g.duplicate().mulZn(rj), pk_c.duplicate().mulZn(sk_d)));

                Element index_2 = globalKey.g.duplicate().mulZn(rj);

                i1_s.add(index_1);
                i2_s.add(index_2);
            }

        }

        // 需要检索的五个关键字
        List<Element> targets = Arrays.asList(kw_s.get(0), kw_s.get(3), kw_s.get(4), kw_s.get(8), kw_s.get(9));

        long start = System.currentTimeMillis();
        // 检索关键字生成token
        for (int i = 0; i < 100; i++){
            for (Element target : targets) {
                Element rk = zr.newElement().setToRandom();
                Element token_1 = pairing.pairing(pk_d.duplicate(), target.duplicate().mulZn(mk))
                        .mul(pairing.pairing(globalKey.g.duplicate().mulZn(rk), pk_c.duplicate().mulZn(mk)));
                Element token_2 = globalKey.g.duplicate().mulZn(rk);

                t1_s.add(token_1);
                t2_s.add(token_2);
            }
            if ((i+1) % 10 == 0){
                System.out.println(i + 1 + ":" + (System.currentTimeMillis() - start));
            }
        }

        for (int i = 0; i < 20; i++){
            int s = i * 5, e = (i+1) * 5;
            for (int j = s; j < e; j++){
                Element index_1 = i1_s.get(j);
                Element index_2 = i2_s.get(j);

                for (int k = 0; k < 5; k++){
                    Element token_1 = t1_s.get(k);
                    Element token_2 = t2_s.get(k);

                    Element left = pairing.pairing(index_2, pk_d).mulZn(sk_c).invert().mul(index_1);
                    Element right = pairing.pairing(token_2, globalKey.g.duplicate().mulZn(mk)).mulZn(sk_c).invert().mul(token_1);
                }
            }
        }

        /*
        Element rj = zr.newElement().setToRandom();
        Element index_1 = pairing.pairing(kw_s.get(0).duplicate().mulZn(sk_d), globalKey.g_x);
        index_1.mul(pairing.pairing(globalKey.g.duplicate().mulZn(rj), pk_c.duplicate().mulZn(sk_d)));

        Element index_2 = globalKey.g.duplicate().mulZn(rj);

        Element rk = zr.newElement().setToRandom();
        Element token_1 = pairing.pairing(pk_d.duplicate(), kw_s.get(0).duplicate().mulZn(mk))
                .mul(pairing.pairing(globalKey.g.duplicate().mulZn(rk), pk_c.duplicate().mulZn(mk)));
        Element token_2 = globalKey.g.duplicate().mulZn(rk);

        Element left = pairing.pairing(index_2, pk_d).mulZn(sk_c).invert().mul(index_1);
        Element right = pairing.pairing(token_2, globalKey.g.duplicate().mulZn(mk)).mulZn(sk_c).invert().mul(token_1);

        System.out.println(left.isEqual(right));
        */
    }

}
