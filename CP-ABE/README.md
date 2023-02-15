# CP-ABE

## Definition

CP-ABE (Ciphertext Policy Attribute-based Encryption) is to embed the policy into the ciphertext, and embed the attribute into the key. Because of the policy embedded in the ciphertext, the data owner can determine who can access the ciphertext by setting policies, which is equivalent to making an encrypted access control for this data that can be refined to the attribute level. 

## Algorithm

It contains the following four steps:

- **$Setup(γ,U):$** input the security parameter $γ$ and the complete set of attributes $U$, and output the system public parameter $pk$ and the master key $mk$;
-  **$KeyGen(pk,mk,w):$** input system public parameter $pk$, master key $mk$ and user attribute set $w$, and output user private key $SKw$ corresponding to $w$;
- **$Encrypt(pk,T,m):$** input system public parameter $pk$, access control policy $T$ and plaintext $m$, and output ciphertext $CTT$;
- **$Decrypt(pk, SKw, CTT):$** input system public parameter $pk$, user private key $SKw$ and ciphertext $CTT$, and output plaintext $m$ only if the attribute set $w$ corresponding to $SKw$ satisfies the access control policy $T$ corresponding to $CTT$.

## Application

The application scenario of CP-ABE is usually encrypted storage and fine-grained sharing of data on the public cloud.