package scheme;

import it.unisa.dia.gas.jpbc.Element;

import java.io.Serializable;

public class BswabeMsk implements Serializable {
	/*
	 * A master secret key
	 */
	public Element a,b,c; /* Z_r */
	public Element []r;/*Z_r*/
	public Element []x ;/*G_1*/
	
	
	
	public Element g_alpha; /* G_2 */	
}
