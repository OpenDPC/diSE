# KP-ABE

## Definition

KP-ABE(Key Policy Attribute-based Encryption) is a scheme that embeds policies into keys and attributes into ciphertext. The key corresponds to an access structure and the ciphertext to an attribute set. Decrypt ciphertext only if the attributes in the attribute set satisfy the access policy. This design is close to the static scenario, in which the ciphertext is encrypted and stored on the server with its related attributes. When users are allowed to get some messages, a specific access policy is assigned to the users. If a user wants to decrypt multiple files, he must have multiple keys that meet the match.

## Algorithm

It contains the following four steps:

- **$Setup(γ,U):$** initializes the system and generates system public parameter $pk$ and master key $mk$ by taking security parameter $γ$ and attribute global set $U$ as input;
- **$KeyGen(pk,mk,T):$** input system public parameter $pk$, primary key $mk$, and user access policy $T$, and output user private key $SKT$ corresponding to $T$;
- **$Encrypt(pk,w,m):$** input system public parameter $pk$, attribute set $w$, and plaintext $m$, and output ciphertext $CTw$;
- **$Decrypt(pk,SKT,CTw):$** the algorithm outputs plaintext $m$ when the system public parameter $pk$, user private key $SKT$ and ciphertext $CTw$ are input, and only when the attribute set $w$ corresponding to $CTw$ meets the access control policy $T$ corresponding to $SKT$.

## Application

Its application scenarios are more inclined to pay video sites, log encryption management and so on.

