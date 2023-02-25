package BC.ABE;


import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class Demo {

    public static void main(String[] args) {

        // 使用a.properties文件内容初始化配对参数
        Pairing pairing = PairingFactory.getPairing("a.properties");

        // 随机选择G1群上的一个元素e1
        Element e1 = pairing.getG1().newRandomElement();
        // e1设置为随机值
        e1.setToRandom();
        // 随机选择G2群上的一个元素e1
        Element e2 = pairing.getG2().newRandomElement();
        // e2设置为随机值
        e2.setToRandom();

        // 随机选择Zr群上的一个元素x
        Element x = pairing.getZr().newRandomElement();
        // x设置为随机值
        x.setToRandom();

        // 复制一份e1
        Element e1_copy = e1.duplicate();
        // 计算e1^x
        e1_copy.mulZn(x);
        // 计算e(e1^x,e2)
        Element mp1 = pairing.pairing(e1_copy, e2);

        // 计算e(e1, e2)^x
        Element mp2 = pairing.pairing(e1, e2);
        mp2.mulZn(x);

        System.out.println(mp1.isEqual(mp2));
    }
}
