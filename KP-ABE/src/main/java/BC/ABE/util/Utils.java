package BC.ABE.util;

import BC.ABE.entity.Polys;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.ArrayList;

public class Utils {

    /**
     * 生成一个随机的deg阶多项式，其中第0项为zeroVal
     * @param deg 阶数
     * @param zeroVal a0的值，即第0项的值
     * @return {@link BC.ABE.entity.Polys}
     */
    public static Polys randPoly(int deg, Element zeroVal) {
        int i;
        Polys q = new Polys();
        q.deg = deg;
        q.coef = new Element[deg + 1];

        for (i = 0; i < deg + 1; i++)
            q.coef[i] = zeroVal.duplicate();

        q.coef[0].set(zeroVal);

        for (i = 1; i < deg + 1; i++)
            q.coef[i].setToRandom();

        return q;
    }

    public static Polys testPoly(int deg, Element zeroVal) {
        int i;
        Polys q = new Polys();
        q.deg = deg;
        q.coef = new Element[deg + 1];

        for (i = 0; i < deg + 1; i++)
            q.coef[i] = zeroVal.duplicate();

        q.coef[0].set(zeroVal);

        for (i = 1; i < deg + 1; i++)
            q.coef[i].set(i);

        return q;
    }

    /**
     * 多项式带入x求值
     * @param r 返回值
     * @param q 多项式
     * @param x 变量x
     */
    public static void evalPoly(Element r, Polys q, Element x) {
        int i;
        Element s, t;

        s = r.duplicate();
        t = r.duplicate();

        r.setToZero();
        t.setToOne();

        for (i = 0; i < q.deg + 1; i++) {
            /* r += q->coef[i] * t */
            s = q.coef[i].duplicate();
            s.mul(t);
            r.add(s);

            /* t *= x */
            t.mul(x);
        }
    }

    // 拉格朗日插值法
    public static void lagrangeCoef(Element r, ArrayList<Integer> s, int i) {
        int j, k;
        Element t;

        t = r.duplicate();

        r.setToOne();
        for (k = 0; k < s.size(); k++) {
            j = s.get(k).intValue();
            if (j == i)
                continue;
            t.set(-j);
            r.mul(t); /* num_muls++; */
            t.set(i - j);
            t.invert();
            r.mul(t); /* num_muls++; */
        }
    }

    // 椭圆曲线上的拉格朗日插值，默认求0点位置
    public static Element lagCurve(ArrayList<Element> x, ArrayList<Element> y, Pairing pairing){
        int m = x.size();
        int n = 1;
        Element zero = pairing.getZr().newRandomElement();
        Element r = pairing.getZr().newRandomElement();
        Element ta = pairing.getZr().newRandomElement();
        Element tb = pairing.getZr().newRandomElement();

        zero.setToZero();
        ta.setToZero();
        for (int i = 0; i < m; i++) {
            tb.setToOne();
            for (int j = 0; j < m; j++) {
                if(i != j){
                    // tb = tb * (zero - x.get(j)) / (x.get(i) - x.get(j));
                    Element tmp = zero.duplicate();
                    tmp.sub(x.get(j));
                    Element tmp2 = x.get(i).duplicate();
                    tmp2.sub(x.get(j));
                    tmp2.invert();
                    tmp.mul(tmp2);

                    tb.mul(tmp.duplicate());
                }
            }
            tb.mul(y.get(i));
            ta.add(tb);
        }
        r.set(ta);

        return r.duplicate();
    }
}
