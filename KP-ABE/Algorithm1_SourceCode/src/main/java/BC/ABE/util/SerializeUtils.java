package BC.ABE.util;

import BC.ABE.entity.Mk;
import BC.ABE.entity.Pk;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class SerializeUtils {

	/* Method has been test okay */
	public static void serializeElement(ArrayList<Byte> arrlist, Element e) {
		byte[] arr_e = e.toBytes();
		serializeUint32(arrlist, arr_e.length);
		byteArrListAppend(arrlist, arr_e);
	}

	/* Method has been test okay */
	public static int unserializeElement(byte[] arr, int offset, Element e) {
		int len;
		int i;
		byte[] e_byte;

		len = unserializeUint32(arr, offset);
		e_byte = new byte[(int) len];
		offset += 4;
		for (i = 0; i < len; i++)
			e_byte[i] = arr[offset + i];
		e.setFromBytes(e_byte);

		return (int) (offset + len);
	}

	public static void serializeString(ArrayList<Byte> arrlist, String s) {
		byte[] b = s.getBytes();
		serializeUint32(arrlist, b.length);
		byteArrListAppend(arrlist, b);
	}

	/*
	 * Usage:
	 * 
	 * StringBuffer sb = new StringBuffer("");
	 * 
	 * offset = unserializeString(arr, offset, sb);
	 * 
	 * String str = sb.substring(0);
	 */
	public static int unserializeString(byte[] arr, int offset, StringBuffer sb) {
		int i;
		int len;
		byte[] str_byte;
	
		len = unserializeUint32(arr, offset);
		offset += 4;
		str_byte = new byte[len];
		for (i = 0; i < len; i++)
			str_byte[i] = arr[offset + i];
	
		sb.append(new String(str_byte));
		return offset + len;
	}

	/* Method has been test okay */
	/* potential problem: the number to be serialize is less than 2^31 */
	private static void serializeUint32(ArrayList<Byte> arrlist, int k) {
		int i;
		byte b;
	
		for (i = 3; i >= 0; i--) {
			b = (byte) ((k & (0x000000ff << (i * 8))) >> (i * 8));
			arrlist.add(Byte.valueOf(b));
		}
	}

	/*
	 * Usage:
	 * 
	 * You have to do offset+=4 after call this method
	 */
	/* Method has been test okay */
	private static int unserializeUint32(byte[] arr, int offset) {
		int i;
		int r = 0;
	
		for (i = 3; i >= 0; i--)
			r |= (byte2int(arr[offset++])) << (i * 8);
		return r;
	}

	private static int byte2int(byte b) {
		if (b >= 0)
			return b;
		return (256 + b);
	}

	private static void byteArrListAppend(ArrayList<Byte> arrlist, byte[] b) {
		int len = b.length;
		for (int i = 0; i < len; i++)
			arrlist.add(Byte.valueOf(b[i]));
	}

	private static byte[] Byte_arr2byte_arr(ArrayList<Byte> B) {
		int len = B.size();
		byte[] b = new byte[len];
	
		for (int i = 0; i < len; i++)
			b[i] = B.get(i).byteValue();
	
		return b;
	}

	public static byte[] serializePk(Pk pk) {
		ArrayList<Byte> arrlist = new ArrayList<Byte>();
		serializeElement(arrlist, pk.g);
		serializeElement(arrlist, pk.g1);
		serializeElement(arrlist, pk.g2);
		serializeElement(arrlist, pk.h);
		return Byte_arr2byte_arr(arrlist);
	}

	public static Pk unSerializePk(byte[] pub_byte) {
		int offset = 0;
		Pk pk = new Pk();
		Pairing pairing = PairingFactory.getPairing("a.properties");	//由工厂类生成Pairing对象

		pk.p = pairing;
		pk.g = pairing.getG1().newRandomElement();
		pk.g1 = pairing.getG1().newRandomElement();
		pk.g2 = pairing.getG1().newRandomElement();
		pk.h = pairing.getG1().newRandomElement();
		pk.d = 4;
		offset = unserializeElement(pub_byte, offset, pk.g);
		offset = unserializeElement(pub_byte, offset, pk.g1);
		offset = unserializeElement(pub_byte, offset, pk.g2);
		offset = unserializeElement(pub_byte, offset, pk.h);

		return pk;
	}

	public static byte[] serializeMk(Mk mk) {
		ArrayList<Byte> arrlist = new ArrayList<Byte>();
		serializeElement(arrlist, mk.x);
		return Byte_arr2byte_arr(arrlist);
	}

	public static Mk unSerializeMk(byte[] master_byte) {
		Mk mk = new Mk();
		Pairing pairing = PairingFactory.getPairing("a.properties");//由工厂类生成Pairing对象
		mk.x = pairing.getZr().newRandomElement();
		int offset = 0;

		offset = unserializeElement(master_byte, offset, mk.x);

		return mk;
	}
}
