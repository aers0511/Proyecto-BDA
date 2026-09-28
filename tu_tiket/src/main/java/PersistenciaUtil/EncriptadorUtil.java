package persistenciaUtil;

import org.mindrot.jbcrypt.BCrypt;

public class EncriptadorUtil {

    public static String encriptarPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verificarPassword(String password, String hashExistente) {
        if (hashExistente == null || !hashExistente.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(password, hashExistente);
    }
}