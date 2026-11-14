package inf563.tp6.login.pages;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;

/**
 * Calcule le code a 6 chiffres d'une application d'authentification (Google Authenticator...)
 * a partir de la cle secrete affichee par Facebook lors de l'activation de la 2FA (RFC 6238).
 */
public final class Totp {

    private Totp() {
    }

    public static String codeActuel(String cleBase32) {
        return code(cleBase32, System.currentTimeMillis() / 1000);
    }

    static String code(String cleBase32, long secondes) {
        try {
            byte[] cle = base32(cleBase32.replace(" ", "").toUpperCase());
            long intervalle = secondes / 30;
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(cle, "HmacSHA1"));
            byte[] h = mac.doFinal(ByteBuffer.allocate(8).putLong(intervalle).array());
            int o = h[h.length - 1] & 0x0f;
            int binaire = ((h[o] & 0x7f) << 24) | ((h[o + 1] & 0xff) << 16) | ((h[o + 2] & 0xff) << 8) | (h[o + 3] & 0xff);
            return String.format("%06d", binaire % 1_000_000);
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de calculer le code 2FA", e);
        }
    }

    private static byte[] base32(String s) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        ByteBuffer out = ByteBuffer.allocate(s.length() * 5 / 8);
        int tampon = 0;
        int bits = 0;
        for (char c : s.replace("=", "").toCharArray()) {
            tampon = (tampon << 5) | alphabet.indexOf(c);
            bits += 5;
            if (bits >= 8) {
                out.put((byte) (tampon >> (bits - 8)));
                bits -= 8;
            }
        }
        return java.util.Arrays.copyOf(out.array(), out.position());
    }
}
