package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Account;
import models.Endorsement;
import models.Skill;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import toolbox.Authenticator;
import toolbox.SessionHelper;
import views.html.ProfileView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ProfileController.
 *
 * @author Jean-Gabriel Genest
 * @version 17.12.03
 * @since 17.12.03
 */
public class ProfileController extends Controller {

    /**
     * Show the profile page of an account.
     *
     * @param accountId the account's id
     * @return the profile page
     * @since 17.12.03
     */
    public Result GET_Profile(final Long accountId) {
        final Account account = Account.db().find(Account.class)
                .setLazyLoadBatchSize(100)
                .where()
                .eq("id", accountId)
                .findUnique();

        if (account == null) {
            return redirect(routes.HomeController.GET_Home());
        }

        account.getSkills().sort((s1, s2) -> {
            if (s1.getEndorsements().size() > s2.getEndorsements().size()) {
                return -1;
            } else if (s1.getEndorsements().size() < s2.getEndorsements().size()) {
                return 1;
            } else {
                return 0;
            }
        });

        boolean privateProfile = false;
        if (SessionHelper.getAccount(session()) != null && account.getId().equals(SessionHelper.getAccount(session()).getId())) {
            privateProfile = true;
        }

        return ok(ProfileView.render(account, privateProfile));
    }


    /**
     * Toggle the endorsement for the skill.
     *
     * @param skillId the if of the skill to toggle
     * @return the number of endorsements for this skill
     * @since 17.12.03
     */
    @Transactional
    @Security.Authenticated(Authenticator.class)
    public Result GET_AJAXEndorseSkill(final Long skillId) {
        final Account account = SessionHelper.getAccount(session());

        final Skill skill = Skill.db().find(Skill.class)
                .where()
                .eq("id", skillId)
                .findUnique();

        if (skill == null || skill.getAccount().getId().equals(account.getId())) {
            return badRequest();
        }

        Endorsement endorsement = skill.getEndorsements().stream()
                .filter(e -> e.getEndorser().getId().equals(account.getId()))
                .findFirst()
                .orElse(null);

        int nbEndorsements = skill.getEndorsements().size();
        if (endorsement == null) {
            endorsement = new Endorsement(account, skill);
            endorsement.save();
            nbEndorsements++;
        } else {
            endorsement.delete();
            nbEndorsements--;
        }
        return ok(Json.toJson(nbEndorsements)).as(Http.MimeTypes.JSON);
    }

    /**
     * Add a skill to the list of skills of the account.
     *
     * @param accountId the id to of the account to which the skill should be added
     * @return a json with the added skill
     * @since 17.12.03
     */
    @Transactional
    @Security.Authenticated(Authenticator.class)
    public Result POST_AJAXSuggestSkill(final Long accountId) {
        final Account account = SessionHelper.getAccount(session());

        final Account endorsedAccount = Account.db().find(Account.class)
                .where()
                .eq("id", accountId)
                .findUnique();

        final JsonNode jsonSkill = request().body().asJson().findValue("skill");

        if (endorsedAccount == null || jsonSkill == null) {
            return badRequest();
        }

        final String suggestedSkill = jsonSkill.asText();

        /* Check if the skill already exists. If not, it's added */
        Skill skill = Skill.db().find(Skill.class)
                .where()
                .eq("name", suggestedSkill)
                .eq("account_id", accountId)
                .findUnique();

        if (skill == null) {
            skill = new Skill(endorsedAccount, suggestedSkill);
            skill.save();
        }

        /* Check if the skill has already been endorsed If not, it's endorsed*/
        Endorsement endorsement = Endorsement.db().find(Endorsement.class)
                .where()
                .eq("endorser_account_id", account.getId())
                .eq("skill_id", skill.getId())
                .findUnique();

        if (endorsement == null) {
            endorsement = new Endorsement(account, skill);
            skill.getEndorsements().add(endorsement);
            endorsement.save();
        }

        final ObjectNode result = Json.newObject();
        result.put("name", skill.getName());
        result.put("id", skill.getId());
        result.put("nbEndorsement", skill.getEndorsements().size());

        return ok(result).as(Http.MimeTypes.JSON);
    }

    /**
     * Edit the user's list of skills.
     *
     * @return as json with the user's complete list of skills
     * @since 17.12.03
     */
    @Transactional
    @Security.Authenticated(Authenticator.class)
    public Result POST_AJAXEditSkills() {
        final Account account = SessionHelper.getAccount(session());

        final List<Skill> newSkills = new ArrayList<>();
        final JsonNode jsonSkills = request().body().asJson().findValue("skills");
        jsonSkills.forEach(jsonSkill -> {
            if (account.getSkills().stream().anyMatch(s -> s.getName().equals(jsonSkill.asText()))) {
                final Skill skill = Skill.db().find(Skill.class)
                        .where()
                        .eq("name", jsonSkill.asText())
                        .eq("account_id", account.getId())
                        .findUnique();
                newSkills.add(skill);

            } else {
                newSkills.add(new Skill(account, jsonSkill.asText()));
            }
        });

        account.getSkills().forEach(s -> {
            if (!newSkills.contains(s)) {
                s.delete();
            }
        });

        account.setSkills(newSkills);
        account.save();

        final ArrayNode result = Json.newArray();
        newSkills.forEach(s -> {
            final ObjectNode skillNode = Json.newObject();
            skillNode.put("name", s.getName());
            skillNode.put("id", s.getId());
            skillNode.put("nbEndorsement", s.getEndorsements().size());
            result.add(skillNode);
        });

        return ok(result).as(Http.MimeTypes.JSON);
    }

    /**
     * Get the list of skills matching the terms sent through json.
     *
     * @return a json with a list of matching skills
     * @since 17.12.03
     */
    @Security.Authenticated(Authenticator.class)
    public Result POST_AJAXRelatedSkills() {
        final Account account = SessionHelper.getAccount(session());

        final String[] qTerm = request().body().asFormUrlEncoded().getOrDefault("q[term]", null);
        String term = "";
        if (qTerm != null) {
            term = qTerm[0];
        }

        final ObjectNode result = Json.newObject();
        final ArrayNode resultsNode = Json.newArray();

        final List<String> alreadyAddedSkills = account.getSkills().stream().map(Skill::getName).collect(Collectors.toList());
        final List<String> skills = Skill.db().find(Skill.class)
                .select("name")
                .where()
                .ilike("name", "%" + term + "%")
                .not().in("name", alreadyAddedSkills)
                .setDistinct(true)
                .setMaxRows(6)
                .findList().stream().map(Skill::getName).collect(Collectors.toList());

        for (final String skill : skills) {
            final ObjectNode jsonSkill = Json.newObject();
            jsonSkill.put("id", skill);
            jsonSkill.put("text", skill);
            resultsNode.add(jsonSkill);
        }

        result.set("results", resultsNode);

        return ok(result).as(Http.MimeTypes.JSON);
    }
}
