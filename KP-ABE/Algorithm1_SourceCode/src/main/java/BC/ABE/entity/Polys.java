package BC.ABE.entity;

import it.unisa.dia.gas.jpbc.Element;

/**
 * deg阶的多项式
 */
public class Polys {
        public int deg;
        /* coefficients from [0] x^0 to [deg] x^deg */
        public Element[] coef; /* G_T (of length deg+1) */
}
