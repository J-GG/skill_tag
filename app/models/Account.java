package models;

import io.ebean.Model;
import org.joda.time.DateTime;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import java.util.List;

/**
 * Account.
 *
 * @author Jean-Gabriel Genest
 * @version 17.12.03
 * @since 17.12.03
 */
@Entity
@Table(name = "account")
public class Account extends Model {

    /**
     * The id of the account.
     *
     * @since 17.12.03
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user's username.
     *
     * @since 17.12.03
     */
    @Column(name = "username", nullable = false, unique = true)
    private final String username;

    /**
     * The user's password.
     *
     * @since 17.12.03
     */
    @Column(name = "password", nullable = false, unique = false)
    private final String password;

    /**
     * The creation date of the account.
     *
     * @since 17.12.03
     */
    @Column(name = "created_at", nullable = true, unique = false)
    private final DateTime createdAt;

    /**
     * The list of skills of the user.
     *
     * @since 17.12.03
     */
    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Skill> skills;

    /**
     * Create a new Acount based on the user's username and password.
     *
     * @param username the user's username
     * @param password the user's plaintext password
     * @since 17.12.03
     */
    public Account(final String username, final String password) {
        this.username = username;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        this.createdAt = DateTime.now();
    }

    /**
     * Get the user's username.
     *
     * @return the user's username
     * @since 17.12.03
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Get the user's password.
     *
     * @return the user's password
     * @since 17.12.03
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Get the id of the account.
     *
     * @return the id of the account
     * @since 17.12.03
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Get the user's list of skills.
     *
     * @return the user's list of skills
     * @since 17.12.03
     */
    public List<Skill> getSkills() {
        return this.skills;
    }

    /**
     * Set the user's list of skills.
     *
     * @param skills the user's list of skills
     * @since 17.12.03
     */
    public void setSkills(final List<Skill> skills) {
        this.skills = skills;
    }
}
