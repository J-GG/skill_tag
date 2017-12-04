package models;

import io.ebean.Model;

import javax.persistence.*;
import java.util.List;

/**
 * Skill.
 *
 * @author Jean-Gabriel Genest
 * @version 17.12.03
 * @since 17.12.03
 */
@Entity
@Table(name = "skill")
public class Skill extends Model {

    /**
     * The if of skill.
     *
     * @since 17.12.03
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The account to which the skill is attached to.
     *
     * @since 17.12.03
     */
    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private final Account account;

    /**
     * The name of the skill.
     *
     * @since 17.12.03
     */
    @Column(name = "name", nullable = false)
    private final String name;

    /**
     * The list of endorsements for this skill.
     *
     * @since 17.12.04
     */
    @OneToMany(mappedBy = "skill", fetch = FetchType.EAGER)
    private List<Endorsement> endorsements;

    /**
     * Create a new Skill based on its name and the account it's attached to.
     *
     * @param account the account the skill is attached to
     * @param name    the name of the skill
     * @since 17.12.03
     */
    public Skill(final Account account, final String name) {
        this.account = account;
        this.name = name;
    }

    /**
     * Get the name of the skill.
     *
     * @return the name of the skill
     * @since 17.12.03
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the account the skill is attached to.
     *
     * @return the account the skill is attached to
     * @since 17.12.03
     */
    public Account getAccount() {
        return this.account;
    }

    /**
     * Get the id of the skill.
     *
     * @return the id of the skill
     * @since 17.12.03
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Get the list of endorsements for this skill.
     *
     * @return the list of endorsements for this skill
     * @since 17.12.04
     */
    public List<Endorsement> getEndorsements() {
        return this.endorsements;
    }
}
