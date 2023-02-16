package scheme;
import it.unisa.dia.gas.jpbc.CurveParameters;
import it.unisa.dia.gas.jpbc.Element;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.DefaultCurveParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import java.io.ByteArrayInputStream;


import index.Index;

public class Bswabe {
	public static String curveParams = "type a\n"
			+ "q 87807107996633125224377819847540498158068831994142082"
			+ "1102865339926647563088022295707862517942266222142315585"
			+ "8769582317459277713367317481324925129998224791\n"
			+ "h 12016012264891146079388821366740534204802954401251311"
			+ "822919615131047207289359704531102844802183906537786776\n"
			+ "r 730750818665451621361119245571504901405976559617\n"
			+ "exp2 159\n" + "exp1 107\n" + "sign1 1\n" + "sign0 1\n";


	public static void setup(String []u,BswabePub pub, BswabeMsk msk) {
		
//		System.out.println("---setup phase---");
		int n = u.length; //�û����Եĳ���
		Element a,b,c;
		msk.r = new Element[2*n]; /* Z_r */
		msk.x = new Element[2*n];/*G_1*/
		pub.u = new Element[2*n];/* G_1*/
		pub.y = new Element[2*n];/* G_T*/
		CurveParameters params = new DefaultCurveParameters()
				.load(new ByteArrayInputStream(curveParams.getBytes()));

		pub.pairingDesc = curveParams;
		
		pub.p = PairingFactory.getPairing(params);
		Pairing pairing = pub.p;
		
		//pub
		pub.g1 = pairing.getG1().newRandomElement();
		
		pub.g_a = pairing.getG1().newRandomElement();
		pub.g_b = pairing.getG1().newRandomElement();
		pub.g_c = pairing.getG1().newRandomElement();
		pub.g2 = pairing.getG2().newRandomElement();
		pub.g2.set(pub.g1);
		

		for(int i=0;i<2*u.length;i++){
			pub.y[i] = pairing.getGT().newElement();
			pub.u[i]=pairing.getG1().newElement();
		}
		
		a= pairing.getZr().newElement();
		b= pairing.getZr().newElement();
		c= pairing.getZr().newElement();
		for(int i=0;i<2*u.length;i++){
			msk.r[i]=pairing.getZr().newRandomElement();
			msk.x[i]=pairing.getG2().newRandomElement();
		}
		msk.a = a.setToRandom();
		msk.b = b.setToRandom();
		msk.c = c.setToRandom();
		
		
		pub.g_a = pub.g1.duplicate();
		pub.g_a = pub.g_a.powZn(msk.a);
		
		pub.g_b = pub.g1.duplicate();
		pub.g_b = pub.g_b.powZn(msk.b);
		
		pub.g_c = pub.g1.duplicate();
		pub.g_c = pub.g_c.powZn(msk.c);
		
		Element r_neg = pairing.getZr().newElement();
		
		for(int i=0;i<2*u.length;i++){
			pub.y[i] = pairing.pairing(pub.g1, msk.x[i]);
			r_neg = msk.r[i].duplicate();
			r_neg.negate();
			pub.u[i] = pub.g1.duplicate();
			pub.u[i]=pub.u[i].powZn(r_neg);
		}	

	}
	
	public static BswabeCph enc(String []u,BswabePub pub, String []policy, Index index)
			throws Exception {
		BswabeCph cph = new BswabeCph();	
		Pairing pairing = pub.p;
		
		cph.w0 = pairing.getG1().newElement();
		cph.w = pairing.getG1().newElement();
		cph.w1 = pairing.getG1().newElement();
		
		Element t1 = pairing.getZr().newRandomElement();
		Element t2 = pairing.getZr().newRandomElement();
//		Element t1 = pairing.getZr().newElement();
//		Element t2 = pairing.getZr().newElement();
		Element m=pairing.getZr().newElement();

		Element add = t1.duplicate();
		add.add(t2);
		Element w01 = pub.g_a.duplicate();
		w01.powZn(add);
		Element w02 = pub.g_b.duplicate();
		w02.powZn(t1);
		
		//Index indTemp;
		byte []ind = index.word.getBytes();
		m=m.setFromHash(ind, 0, ind.length);

		cph.w0 = pub.g_c.duplicate();
		cph.w0.powZn(t1);			
		cph.w = w01.duplicate();
		cph.w.mul(w02.powZn(m));
		cph.w1 = pub.g1.duplicate();
		cph.w1.powZn(t2);
		
		
		for(int i=0;i<u.length;i++){
			if(isContain(policy,u[i])){
				cph.w1.mul(pub.u[i]);
			}
			else{
				cph.w1.mul(pub.u[i+u.length]);

			}
		}

		return cph;
	}

	/*
	 * Generate a private key with the given set of attributes.
	 */
	public static BswabePrv keygen(String []u,BswabePub pub, BswabeMsk msk, String[] attrs){
		int len = u.length;
		BswabePrv prv = new BswabePrv();
		Pairing pairing= pub.p;
		prv.sig = new Element[len];
		prv.y = new Element[len];
		
		
		prv.v = pairing.getG2().newElement();
		prv.sig_user = pairing.getG2().newElement();
		prv.y_user = pairing.getGT().newElement();
		for(int i=0;i<len;i++){
			prv.sig[i] = pairing.getG2().newElement();
			prv.y[i] = pairing.getGT().newElement();
		}
		
		prv.v = pub.g2.duplicate();	
		prv.v.powZn(msk.a);
		prv.v.powZn(msk.c);
		
		for(int i=0;i<len;i++)
			prv.sig[i] = prv.v.duplicate();
		
		for(int i=0;i<len;i++){
			if(isContain(attrs,u[i])){
				prv.sig[i].powZn(msk.r[i]);
				prv.sig[i].mul(msk.x[i]);
				prv.y[i] = pub.y[i].duplicate();
			}
			else{
				prv.sig[i].powZn(msk.r[i+len]);
				prv.sig[i].mul(msk.x[i+len]);
				prv.y[i] = pub.y[i+len].duplicate();				
			}
		}
		prv.sig_user = prv.sig[0].duplicate();
		prv.y_user = prv.y[0].duplicate();
		for(int i=1;i<len;i++){
			prv.sig_user.mul(prv.sig[i]);
			prv.y_user.mul(prv.y[i]);
		}
		//System.err.println(prv.sig[0].isEqual(prv.sig[u.length-1]));
		//System.err.println(pub.y[3].isEqual(pub.y[5]));
		return prv;
		
	}
	
	public static boolean isContain(String []attrs,String u){
		boolean ret = false;
		for(int i=0;i<attrs.length;i++){
			if(u.equals(attrs[i])){
				ret = true;
				break;
			}
		}
		return ret;
	}


	public static BswabeToken tokgen(BswabePrv prv,BswabePub pub, String word){
//		System.out.println("---begin token generation phase---");
		Pairing pairing = pub.p;
		BswabeToken token = new BswabeToken();
		token.tok1 = pairing.getG2().newElement();
		token.tok2 = pairing.getG2().newElement();
		token.tok3 = pairing.getG2().newElement();
		token.tok4 = pairing.getG2().newElement();
		token.tok5 = pairing.getGT().newElement();	
			
		Element s = pairing.getZr().newElement();
		s.setToRandom();
		Element wm = pairing.getZr().newElement();//Ҫ���ܵĹؼ���
		byte []w = word.getBytes();
		wm = wm.setFromHash(w, 0, w.length);
		
				
		token.tok1 = pub.g_b.duplicate();
		token.tok1.powZn(wm);		
		token.tok1.mul(pub.g_a);
		token.tok1.powZn(s);		
		token.tok2 = pub.g_c.duplicate();
		token.tok2.powZn(s);		
		token.tok3 = prv.v.duplicate();
		token.tok3.powZn(s);		
		token.tok4 = prv.sig_user.duplicate();
		token.tok4.powZn(s);		
		token.tok5 = prv.y_user.duplicate();
		token.tok5.powZn(s);
		
		return token;
		
	}

	
	public static boolean search(BswabePub pub, BswabeToken token, BswabeCph cph) {
//		System.out.println("---begin search generation phase---");
		boolean ret = false;		
		Pairing pairing = pub.p;
		Element E = pairing.getGT().newElement();
		
		E=pairing.pairing(cph.w1,token.tok3);
		E=E.mul(pairing.pairing(pub.g1, token.tok4));
		E=E.div(token.tok5);
		
		Element left = pairing.getGT().newElement();
		Element right = pairing.getGT().newElement();
		
		left = pairing.pairing(cph.w0, token.tok1);	
		left = left.mul(E);
		right = pairing.pairing(cph.w, token.tok2);	
		
		if(left.equals(right)){
			ret = true;			

		}
		return ret;		
	}
}
