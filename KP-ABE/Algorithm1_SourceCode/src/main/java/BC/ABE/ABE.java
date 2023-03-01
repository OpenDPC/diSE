package BC.ABE;

import BC.ABE.entity.*;
import BC.ABE.util.Utils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ABE {

    private static String curveParams = "type a\n"
            + "q 87807107996633125224377819847540498158068831994142082"
            + "1102865339926647563088022295707862517942266222142315585"
            + "8769582317459277713367317481324925129998224791\n"
            + "h 12016012264891146079388821366740534204802954401251311"
            + "822919615131047207289359704531102844802183906537786776\n"
            + "r 730750818665451621361119245571504901405976559617\n"
            + "exp2 159\n" + "exp1 107\n" + "sign1 1\n" + "sign0 1\n";


    /**
     * 系统初始化，生成公私钥对
     * @param pk PK
     * @param mk MK
     */
    public void setUp(Pk pk, Mk mk){
        //  由工厂类生成Pairing对象
        Pairing pairing = PairingFactory.getPairing("a.properties");
        // 生成密钥MK，整数域上的随机数x
        mk.p = pairing;
        mk.x = pairing.getZr().newRandomElement();
        mk.x.setToRandom();

        pk.p = pairing;
        // 初始化PK
        pk.g = pairing.getG1().newRandomElement();
        pk.g1 = pairing.getG1().newRandomElement();
        pk.g2 = pairing.getG1().newRandomElement();
        pk.h = pairing.getG1().newRandomElement();
        // g、g2、h为群G1上的随机数，g1=g^x
        pk.g.setToRandom();
        pk.g1 = pk.g.duplicate();
        pk.g1.powZn(mk.x);
        pk.g2.setToRandom();
        pk.h.setToRandom();

        System.out.println("公私钥对初始化结束！");
    }

    /**
     * 密钥生成，根绝公私钥对和访问控制策略，生成外包解密密钥：sk_out、属性密钥：sk_attr
     * @param pk PK
     * @param mk MK
     * @param sk_out 外包属性密钥
     * @param sk_attr 属性密钥
     * @param accessPolicy 访问控制策略，这里为访问控制列表
     */
    public void keyGen(Pk pk, Mk mk, Sk_Out sk_out, Sk_Attr sk_attr, AccessPolicy accessPolicy){
        Pairing pairing = pk.p;
        // 随机生成整数域上的x1、x2，满足x1+x2=x
        Element x1 = pairing.getZr().newRandomElement();
        Element x2 = pairing.getZr().newRandomElement();
        x1.setToRandom();
        x2 = mk.x.duplicate();
        x2.sub(x1);

        // 外包私钥
        // 生成一个首项为x1随机的多项式
        Polys poly = Utils.randPoly(pk.d - 1, x1);
        ArrayList<Element> di0 = new ArrayList<>();
        ArrayList<Element> di1 = new ArrayList<>();
        Element ri = pairing.getZr().newRandomElement();
        Element t = pairing.getG1().newRandomElement();
        for (int i = 0; i < accessPolicy.attrs.size(); i++) {
            // di1 = g^ri
            ri.setToRandom();
            t = pk.g.duplicate();
            t.powZn(ri);
            di1.add(t.duplicate());
            // g2^f(attr_i)
            Element g2 = pk.g2.duplicate();
            Element res = pk.p.getZr().newRandomElement();
            Utils.evalPoly(res, poly, accessPolicy.attrs.get(i));   // 最开始插值输入的下标是属性值
            g2.powZn(res);

            Element g1 = pk.g1.duplicate();
            g1.mulZn(accessPolicy.attrs.get(i));        // 这里直接乘属性我没有看懂
            g1.powZn(ri);
            g1.mul(g2);
            di0.add(g1.duplicate());
        }
        sk_out.di0 = di0;
        sk_out.di1 = di1;

        // TODO
        sk_out.polys = poly;

        // 属性私钥
        Element rc = pairing.getZr().newRandomElement();
        rc.setToRandom();
        // dc1 = g2^x2*(g1h)^rc
        Element g2 = pk.g2.duplicate();
        g2.powZn(x2);
        Element g1 = pk.g1.duplicate();
        g1.mul(pk.h);
        g1.powZn(rc);
        g1.mul(g2);
        sk_attr.dc0 = g1.duplicate();
        // dc1 = g^rc
        Element g = pk.g.duplicate();
        g.powZn(rc);
        sk_attr.dc1 = g.duplicate();

        System.out.println("密钥生成完毕！");
    }

    public CipherText encrypt(Pk pk, Element m, ArrayList<Element> w){
        CipherText cipherText = new CipherText();

        Pairing pairing = pk.p;

        Element s = pairing.getZr().newRandomElement();
        Element c0 = pairing.getGT().newRandomElement();
        Element c1 = pairing.getG1().newRandomElement();
        // Element ci = pairing.getG1().newRandomElement();
        Element cc = pairing.getG1().newRandomElement();
        ArrayList<Element> ci = new ArrayList<>();

        Element g = pk.g.duplicate();
        Element g1 = pk.g1.duplicate();
        Element g2 = pk.g2.duplicate();
        Element h = pk.h.duplicate();

        // c0 = m*e(g1,g2)^s
        s.setToRandom();
        g2.powZn(s);
        c0 = pairing.pairing(g2, g1.duplicate());
        c0.mul(m);
        // c1 = g^s
        c1 = g.duplicate();
        c1.powZn(s);
        // cc = (g1,h)^s
        cc = g1.duplicate();
        cc.mul(h);
        cc.powZn(s);
        // ci = (g1,wi)^s
        for (int i = 0; i < w.size(); i++) {
            Element t = g1.duplicate();
            t.mulZn(w.get(i));
            t.powZn(s);
            ci.add(t.duplicate());
        }

        cipherText.c0 = c0;
        cipherText.c1 = c1;
        cipherText.cc = cc;
        cipherText.ci = ci;
        cipherText.attrs = w;

        System.out.println("加密完成！");
        return cipherText;
    }

    public Element preDecrypt(Sk_Out sk_out, CipherText cipherText, Pk pk){
        Pairing pairing = pk.p;
        Element u = pairing.getG1().newRandomElement();
        Element d = pairing.getG1().newRandomElement();

        Element c1 = cipherText.c1.duplicate();
        ArrayList<Element> ci = (ArrayList<Element>) cipherText.ci.clone();

        ArrayList<Element> di0 = (ArrayList<Element>) sk_out.di0.clone();
        ArrayList<Element> di1 = (ArrayList<Element>) sk_out.di1.clone();

        Element up = pairing.getGT().newRandomElement();
        Element down = pairing.getGT().newRandomElement();
        up.setToOne();
        down.setToOne();

        Element zero = pairing.getZr().newRandomElement();
        zero.setToZero();
        Element one = pairing.getZr().newRandomElement();

        Element ta = pairing.getGT().newRandomElement();
        Element tb = pairing.getGT().newRandomElement();
        tb.setToOne();

        // TODO S集合判断
        // 属性
        ArrayList<Element> S = cipherText.attrs;
        int len = S.size();
        if(len < pk.d){
            throw new RuntimeException("属性值不匹配！预解密失败！");
        }

        // 指数上的拉格朗日插值
        for (int i = 0; i < len; i++) {
            up = pairing.pairing(c1, di0.get(i));
            down = pairing.pairing(di1.get(i), ci.get(i));

            // 求插值基函数（拉格朗日基本多项式）带入0后的值
            // one变量保存结果
            one.setToOne();
            for(int j = 0; j < len; j++){
                if(i != j){
                    Element tmp = zero.duplicate();
                    tmp.sub(S.get(j));
                    Element tmp2 = S.get(i).duplicate();
                    tmp2.sub(S.get(j));
                    tmp2.invert();
                    tmp.mul(tmp2);

                    one.mul(tmp);
                }
            }
            // ta = (up/down)^one
            ta = down.duplicate();
            ta.invert();
            ta.mul(up);
            ta.powZn(one);

            tb.mul(ta.duplicate());
        }
        System.out.println(tb);

/*        // 测试永远匹配成功
        ArrayList<Element> xs = cipherText.attrs;
        ArrayList<Element> ys = new ArrayList<>();
        Element res = pairing.getZr().newRandomElement();
        Element ty = pairing.getZr().newRandomElement();
        for (int i = 0; i < xs.size(); i++) {
            Utils.evalPoly(ty, sk_out.polys, xs.get(i));
            ys.add(ty.duplicate());
        }
        res = Utils.lagCurve(xs, ys, pk.p);     // res = x1
        Element gt = pairing.getGT().newRandomElement();
        gt = pairing.pairing(cipherText.c1.duplicate(), pk.g2.duplicate());
        gt.powZn(res);

        System.out.println(gt);
        System.out.println("预解密完成！");*/

        return tb.duplicate();
    }

    public void decrypt(Sk_Attr sk_attr, Element pre, CipherText cipherText, Pk pk){
        Pairing pairing = pk.p;

        Element res = pairing.getG2().newRandomElement();

        Element up = pairing.getG2().newRandomElement();
        Element down = pairing.getG2().newRandomElement();
        up = cipherText.c0.duplicate();
        up.mul(pairing.pairing(sk_attr.dc1.duplicate(), cipherText.cc.duplicate()));
        down = pre.duplicate();
        down.mul(pairing.pairing(cipherText.c1.duplicate(), sk_attr.dc0.duplicate()));

        down.invert();
        down.mul(up);
        res = down.duplicate();

        System.out.println("最终解密完成！密文：");
        System.out.println(res);
    }
}
