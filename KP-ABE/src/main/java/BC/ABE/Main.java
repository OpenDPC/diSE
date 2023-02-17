package BC.ABE;

import BC.ABE.entity.*;
import BC.ABE.util.Common;
import BC.ABE.util.SerializeUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void genAttrs(Pk pk, String attrDict) throws IOException {
        Pairing p = pk.p;
        Map<String, Object> attrs = new HashMap<>();
        Element element = p.getZr().newRandomElement();
        for (int i = 0; i < 10; i++) {
            element.setToRandom();
            byte[] bytes = element.toBytes();
            // System.out.println(element);
            attrs.put("attr_" + i, bytes);
        }
        File file = new File(attrDict);
        FileOutputStream out = new FileOutputStream(file);
        ObjectOutputStream objOut = new ObjectOutputStream(out);
        objOut.writeObject(attrs);
        objOut.flush();
        objOut.close();
    }

    public static Map<String, Object> recovery(String attrDict) {
        File file = new File(attrDict);
        Object temp = null;
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(in);
            temp = objIn.readObject();
            objIn.close();
            System.out.println("read object success!");
        } catch (IOException e) {
            System.out.println("read object failed");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (Map<String, Object>) temp;
    }

    public static void main(String[] args) throws IOException {
        byte[] pk_byte, mk_byte;
        String pk_file = "pk.dat", mk_file = "mk.dat";
        ABE abe = new ABE();
// 初始化
/*        Pk pk = new Pk();
        Mk mk = new Mk();
        abe.setUp(pk, mk);
        pk_byte = SerializeUtils.serializePk(pk);
        Common.spitFile(pk_file, pk_byte);
        System.out.println(pk.g);

        mk_byte = SerializeUtils.serializeMk(mk);
        Common.spitFile(mk_file, mk_byte);
        System.out.println(mk.x);*/

        // 从文件中读取
        byte[] pub_byte;
        pub_byte = Common.suckFile(pk_file);
        Pk pk = SerializeUtils.unSerializePk(pub_byte);

        genAttrs(pk, "attrs.obj");

        byte[] master_byte;
        master_byte = Common.suckFile(mk_file);
        Mk mk = SerializeUtils.unSerializeMk(master_byte);

        ArrayList<Element> attrs = new ArrayList<>();
        Map<String, Object> map = recovery("attrs.obj");
        Element e = pk.p.getZr().newRandomElement();

        for (int i = 0; i < 4; i++) {
            e.setFromBytes((byte[]) map.get("attr_"+i));
            attrs.add(e.duplicate());
        }

        System.out.println("选择的属性：");
        for (Element attr : attrs) {
            System.out.println(attr);
        }

        Sk_Attr sk_attr = new Sk_Attr();
        Sk_Out sk_out = new Sk_Out();
        AccessPolicy accessPolicy = new AccessPolicy();
        accessPolicy.attrs = attrs;
        abe.keyGen(pk, mk, sk_out, sk_attr, accessPolicy);

        String mesg = "I Love Java";    //明文
        // byte[] bytes = Integer.toString(mesg.hashCode()).getBytes();    //  哈希明文，将整数转为字符串进而调用字符串获取字节数组的方法
        byte[] bytes = mesg.getBytes(StandardCharsets.UTF_8);
        Element m = pk.p.getGT().newElementFromHash(bytes, 0, bytes.length);    //  将明文m映射为GT群中的元素
        System.out.println("加密前的明文为：");
        System.out.println(m);
        CipherText cipherText = abe.encrypt(pk, m, attrs);

        Element prect = abe.preDecrypt(sk_out, cipherText, pk);

        abe.decrypt(sk_attr, prect, cipherText, pk);

/*         Element beta = pk.p.getZr().newRandomElement();
        beta.setToRandom();
        Element alfa =  pk.p.getZr().newRandomElement();
        alfa.setToRandom();

        Element g = pk.g.duplicate();
        Element g1 = pk.g1.duplicate();

        Element t = pk.p.getG1().newRandomElement();
        t = g.duplicate();
        t.powZn(beta);

        Element r = pk.p.getG1().newRandomElement();
        r = g1.duplicate();
        r.powZn(beta);

        Element element = pk.p.pairing(t, g1.duplicate());
        Element element1 = pk.p.pairing(r, g.duplicate());
        System.out.println(element1.isEqual(element));*/

    }
}
