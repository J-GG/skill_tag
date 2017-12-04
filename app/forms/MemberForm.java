package forms;

import models.Account;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

/**
 * MemberForm.
 *
 * @author Jean-Gabriel Genest
 * @version 17.12.03
 * @since 17.12.03
 */
@Constraints.Validate
public class MemberForm implements Constraints.Validatable<List<ValidationError>> {
    /**
     * The username filled in the form.
     *
     * @since 17.12.03
     */
    @Constraints.Required
    @Constraints.MinLength(3)
    @Constraints.MaxLength(25)
    private String username;

    /**
     * The password filled in the form.
     *
     * @since 17.12.03
     */
    @Constraints.Required
    @Constraints.MinLength(6)
    private String password;

    /**
     * Whether the user wants to register or to log in.
     *
     * @since 17.12.03
     */
    @Constraints.Required
    private boolean register;

    @Override
    public List<ValidationError> validate() {
        final List<ValidationError> errors = new ArrayList<>();

        if (this.register) {
            if (Account.db().find(Account.class)
                    .where()
                    .eq("username", this.username)
                    .findCount() > 0) {
                errors.add(new ValidationError("username", "MEMBER.REGISTER.DUPLICATE_USERNAME"));
            }
        } else {
            final Account account = Account.db().find(Account.class)
                    .where()
                    .eq("username", this.username)
                    .findUnique();
            if (account == null || !BCrypt.checkpw(this.password, account.getPassword())) {
                errors.add(new ValidationError("", "MEMBER.LOGIN.ERROR"));
            }
        }

        return errors.isEmpty() ? null : errors;
    }

    /**
     * Dies the user want to register or log in?
     *
     * @return true if they want to register
     * @since 17.12.03
     */
    public boolean isRegister() {
        return this.register;
    }

    /**
     * Get the filled in password.
     *
     * @return the password
     * @since 17.12.03
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Get the filled in username.
     *
     * @return the username
     * @since 17.12.03
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Set whether the user wants to register or log in.
     *
     * @param register true if they want to register
     * @since 17.12.03
     */
    public void setRegister(final boolean register) {
        this.register = register;
    }

    /**
     * Set the password
     *
     * @param password the password
     * @since 17.12.03
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Set the username
     *
     * @param username the username
     * @since 17.12.03
     */
    public void setUsername(final String username) {
        this.username = username;
    }
}
