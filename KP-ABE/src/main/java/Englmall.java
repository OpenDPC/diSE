import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class Englmall {

    public static void main(String[] args) {
        // 使用配对库模拟Elgamal同态
        Pairing pairing = PairingFactory.getPairing("a.properties");

        // 整数域
        Field zr = pairing.getZr();
        // 表示q
        Element q = zr.newRandomElement().setToRandom();
        // 表示q的原根
        Element g = zr.newRandomElement().setToRandom();
        // 私钥x
        Element x = zr.newRandomElement().setToRandom();
        // 公钥y
        Element y = zr.newRandomElement();
        y = g.duplicate();
        // 计算 y=g^x
        y.powZn(x);

        // 明文m1
        Element m1 = zr.newRandomElement().setToRandom();
        // 加密m1的随机数k1
        Element k1 = zr.newRandomElement().setToRandom();
        // 初始化密文c11
        Element c11 = g.duplicate();
        // 计算 c11=g^k1
        c11.powZn(k1);
        Element c12 = y.duplicate();
        c12.powZn(k1);
        c12.mul(m1);


        Element m2 = zr.newRandomElement().setToRandom();
        Element k2 = zr.newRandomElement().setToRandom();
        Element c21 = g.duplicate();
        c21.powZn(k2);
        Element c22 = y.duplicate();
        c22.powZn(k2);
        c22.mul(m2);

        System.out.print("明文m1:");
        System.out.println(m1);
        System.out.print("明文m2:");
        System.out.println(m2);
        System.out.print("明文m1*m2: ");
        Element t1 = m1.duplicate();
        t1.mul(m2);
        System.out.println(t1);

        System.out.println("=================================================================");

        System.out.print("密文m1:\n");
        System.out.println(c11);
        System.out.println(c12);

        System.out.print("密文m2:\n");
        System.out.println(c21);
        System.out.println(c22);

        c11.mul(c21);
        c12.mul(c22);
        System.out.println("=================================================================");
        System.out.print("密文m1*密文m2：\n");
        System.out.print(c11);
        System.out.println(" ");
        System.out.println(c12);
        System.out.println("=================================================================");
        c11.powZn(x);
        c11.invert();
        c11.mul(c12);

        System.out.println("解密后结果：");
        System.out.println(c11);

    }
}
