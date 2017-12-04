package toolbox;


import models.Account;
import play.mvc.Http;

/**
 * SessionHelper.
 *
 * @author Jean-Gabriel Genest
 * @version 17.12.03
 * @since 17.12.03
 */
public class SessionHelper {

    /**
     * Get the Account of the current user.
     *
     * @param session the HTTP session
     * @return the Account of the current user if they are logged in. Null if not
     */
    public static Account getAccount(final Http.Session session) {
        if (session.get("id") == null) {
            return null;
        }

        return Account.db().find(Account.class, session.get("id"));
    }
}
