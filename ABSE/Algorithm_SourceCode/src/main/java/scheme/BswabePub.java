package scheme;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.io.Serializable;

public class BswabePub implements Serializable {
	/*
	 * A public key
	 */
	public String pairingDesc; //配对描述？
	public Pairing p;		
	public Element g1;				/* G_1*/
	public Element g_a;				/* G_1 */
	public Element g_b;				/* G_1 */
	public Element g_c;				/* G_1 */
	public Element []u ;            /* G_1*/
	public Element []y ;            /* G_T*/
	public Element g2;			    /* G_2 */

	
	//public Element gp;			/* G_2 */
	//public Element g_hat_alpha;	/* G_T */
}
