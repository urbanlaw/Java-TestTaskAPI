package helpers;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * helper for encryption, hash generation and such
 */
public class EncryptHelper
{
    /**
     * encode string
     *
     * @param str string to encode
     * @return string, lenght 32
     */
    public static String Encode(String str) throws NoSuchAlgorithmException
    {
        String salt = "vFl4th2A";
        String saltStr = str + salt;

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(saltStr.getBytes());
        byte[] digest = md5.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }

    /**
     * generates random hash
     *
     * @return string, lenght 32
     */
    public static String RandomHash()
    {
        return RandomHash(32);
    }

    /**
     * generates random hash
     *
     * @param length result length
     * @return string, custom length
     */
    public static String RandomHash(int length)
    {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = upper.toLowerCase();
        String digits = "0123456789";
        String alphanum = upper + lower + digits;
        char[] symbols = alphanum.toCharArray();

        Random random = new Random();

        char[] result = new char[length];
        for (int i = 0; i < length; ++i)
        {
            result[i] += symbols[random.nextInt(symbols.length)];
        }
        return new String(result);
    }

    /**
     * generates token for user
     * @param login user login
     * @return string, length 64
     */
    public static String UserToken(String login) throws NoSuchAlgorithmException
    {
        return Encode(login) + RandomHash();
    }
}
