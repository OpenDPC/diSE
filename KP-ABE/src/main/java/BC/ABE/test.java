package BC.ABE; /**
 * @author zq
 * @description t
 * @date 2021/9/15
 */

import BC.ABE.entity.Pk;
import BC.ABE.entity.Polys;
import BC.ABE.util.Utils;
import it.unisa.dia.gas.jpbc.*;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;

public class test {

    private static double[] Lag(double x[], double y[], double x0[]) {
        int m = x.length;
        int n = x0.length;
        double y0[] = new double[n];
        for (int ia = 0; ia < n; ia++) {
            double j = 0;
            for (int ib = 0; ib < m; ib++) {
                double k = 1;
                for (int ic = 0; ic < m; ic++) {
                    if (ib != ic) {
                        k = k * (x0[ia] - x[ic]) / (x[ib] - x[ic]);
                    }
                }
                k = k * y[ib];
                j = j + k;
            }
            y0[ia] = j;
        }
        return y0;
    }

    public static Element lagCurve(ArrayList<Element> x, ArrayList<Element> y, Pairing pairing){
        int m = x.size();
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

    private static void testLag(){
        System.out.println("请输入给定的插值点数量：");
        Scanner input = new Scanner(System.in);
        int m = input.nextInt();
        System.out.println("请输入需求解的插值点数量：");
        int n = input.nextInt();
        double x[] = new double[m];
        double y[] = new double[m];
        double x0[] = new double[n];
        System.out.println("依次输入给定的插值点:");
        for (int i = 0; i < m; i++) {
            x[i] = input.nextDouble();
        }
        System.out.println("依次输入给定的插值点对应的函数值:");
        for (int i = 0; i < m; i++) {
            y[i] = input.nextDouble();
        }
        System.out.println("依次输入需求解的插值点");
        for (int i = 0; i < n; i++) {
            x0[i] = input.nextDouble();
        }
        double y0[] = Lag(x, y, x0);
        System.out.println("运用拉格朗日插值法求解得:");
        for (int i = 0; i < n; i++) {
            System.out.println(y0[i] + " ");
        }
        System.out.println();
        input.close();
    }

    private static void testLagCurve(){
        Pairing pairing = PairingFactory.getPairing("a.properties");//由工厂类生成Pairing对象

        Element f = pairing.getZr().newRandomElement();
        f.set(98556);
        Element r = pairing.getZr().newRandomElement();
        r.set(1);
        Element x = pairing.getZr().newRandomElement();
        x.set(5);

        Polys poly = Utils.testPoly(3, f);

        ArrayList<Element> xs = new ArrayList<>();
        ArrayList<Element> ys = new ArrayList<>();

        for (int i = 74; i < 77; i++) {
            x.set(i);
            Utils.evalPoly(r, poly, x);
            xs.add(x.duplicate());
            ys.add(r.duplicate());
        }

        r = lagCurve(xs, ys, pairing);
        // Utils.evalPoly(r, poly, x);
        System.out.println(r);
    }

    private static void testPedersen(){
        Pairing pairing = PairingFactory.getPairing("a.properties");//由工厂类生成Pairing对象

        Element tmp = pairing.getZr().newRandomElement();
        Element r = pairing.getZr().newRandomElement();
        Element master_secret = pairing.getZr().newRandomElement();
        master_secret.setToZero();
        Element S0 = tmp.setToRandom().setToRandom().duplicate();
        Element S1 = tmp.setToRandom().setToRandom().duplicate();
        Element S2 = tmp.setToRandom().setToRandom().duplicate();
        Element S3 = tmp.setToRandom().setToRandom().duplicate();
        master_secret.add(S0);master_secret.add(S1);master_secret.add(S2);master_secret.add(S3);

        System.out.println(master_secret);

        Polys poly0 = Utils.randPoly(3, S0);
        Polys poly1 = Utils.randPoly(3, S1);
        Polys poly2 = Utils.randPoly(3, S2);
        Polys poly3 = Utils.randPoly(3, S3);

        ArrayList<Element> xs = new ArrayList<>();

        ArrayList<Element> ys0 = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            xs.add(tmp.set(i).duplicate());
            Utils.evalPoly(r, poly0, xs.get(i));
            ys0.add(r.duplicate());
        }
        ArrayList<Element> ys1 = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Utils.evalPoly(r, poly1, xs.get(i));
            ys1.add(r.duplicate());
        }
        ArrayList<Element> ys2 = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Utils.evalPoly(r, poly2, xs.get(i));
            ys2.add(r.duplicate());
        }
        ArrayList<Element> ys3 = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Utils.evalPoly(r, poly3, xs.get(i));
            ys3.add(r.duplicate());
        }

        Element s0 = tmp.setToRandom().setToRandom().duplicate();
        s0.setToZero();
        Element s1 = tmp.setToRandom().setToRandom().duplicate();
        s1.setToZero();
        Element s2 = tmp.setToRandom().setToRandom().duplicate();
        s2.setToZero();
        Element s3 = tmp.setToRandom().setToRandom().duplicate();
        s3.setToZero();

        s0.add(ys0.get(0));s0.add(ys1.get(0));s0.add(ys2.get(0));s0.add(ys3.get(0));
        s1.add(ys0.get(1));s1.add(ys1.get(1));s1.add(ys2.get(1));s1.add(ys3.get(1));
        s2.add(ys0.get(2));s2.add(ys1.get(2));s2.add(ys2.get(2));s2.add(ys3.get(2));
        s3.add(ys0.get(3));s3.add(ys1.get(3));s3.add(ys2.get(3));s3.add(ys3.get(3));

        ArrayList<Element> ys = new ArrayList<>();
        ys.add(s0);ys.add(s1);ys.add(s2);ys.add(s3);

        Element element = Utils.lagCurve(xs, ys, pairing);      // xs:参与者的index  ys：插值
        System.out.println(element);
    }

    public static void main(String[] args) {
        long start = new Date().getTime();
//        String m="message";//明文
//        byte[] bytes = Integer.toString(m.hashCode()).getBytes();//哈希明文，将整数转为字符串进而调用字符串获取字节数组的方法
//        Element h = G1.newElementFromHash(bytes, 0, bytes.length);//将明文m映射为G1群中的元素

        Pairing pairing = PairingFactory.getPairing("a.properties");//由工厂类生成Pairing对象

        Element zr = pairing.getZr().newRandomElement();
        zr.setToRandom();



        byte[] bytes = zr.toBytes();

//        String encoded = Base64.getEncoder().encodeToString(bytes);
        String encoded = "NJpW1K4VKTG1wixGxOZz0v0s2wU=";
        byte[] decode = Base64.getDecoder().decode(encoded);
        // zr.setFromBytes(decode);
        System.out.println(zr);

//        System.out.println(encoded);



        System.out.println(new Date().getTime() -start);

    }
}

