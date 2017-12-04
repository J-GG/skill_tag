package models;

import io.ebean.Model;

import javax.persistence.*;

/**
 * Endorsement.
 *
 * @author Jean-Gabriel Genest
 * @version 17.12.03
 * @since 17.12.03
 */
@Entity
@Table(name = "endorsement")
public class Endorsement extends Model {

    /**
     * The id of the endorsement.
     *
     * @since 17.12.03
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The account of the endorser.
     *
     * @since 17.12.03
     */
    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "endorser_account_id", referencedColumnName = "id", nullable = false)
    private final Account endorserAccount;

    /**
     * The endorsed skill.
     *
     * @since 17.12.03
     */
    @ManyToOne(targetEntity = Skill.class)
    @JoinColumn(name = "skill_id", referencedColumnName = "id", nullable = false)
    private final Skill skill;

    /**
     * Create a new Endorsement for the given skill by the endorser.
     *
     * @param endorser the account of the endorser
     * @param skill    the endorsed skill
     * @since 17.12.03
     */
    public Endorsement(final Account endorser, final Skill skill) {
        this.endorserAccount = endorser;
        this.skill = skill;
    }

    /**
     * Get the account of the endorser.
     *
     * @return the account of the endorser
     * @since 17.12.03
     */
    public Account getEndorser() {
        return this.endorserAccount;
    }
}
