package controllers;

import forms.MemberForm;
import toolbox.SessionHelper;
import models.Account;
import org.mindrot.jbcrypt.BCrypt;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.MemberFormView;

import javax.inject.Inject;

/**
 * MemberController.
 *
 * @author Jean-Gabriel Genest
 * @version 17.12.03
 * @since 17.12.03
 */
public class MemberController extends Controller {

    /**
     * A helper to deal with forms.
     *
     * @since 17.12.03
     */
    @Inject
    private FormFactory formFactory;

    /**
     * Show the form to register or log in.
     *
     * @return the form to register or log in
     * @since 17.02.03
     */
    public Result GET_Member() {
        if (SessionHelper.getAccount(session()) != null) {
            return redirect(routes.HomeController.GET_Home());
        }

        return ok(MemberFormView.render(null));
    }

    /**
     * Log in or register a user.
     *
     * @return redirect to the home page
     * @since 17.12.03
     */
    public Result POST_Member() {
        final Form<MemberForm> bindedForm = this.formFactory.form(MemberForm.class).bindFromRequest();

        if (bindedForm.hasErrors()) {
            return badRequest(MemberFormView.render(bindedForm));
        }

        final MemberForm memberForm = bindedForm.get();
        final Account account;
        if (memberForm.isRegister()) {
            account = new Account(memberForm.getUsername(), memberForm.getPassword());
            account.save();
        } else {
            account = Account.db().find(Account.class)
                    .where()
                    .eq("username", memberForm.getUsername())
                    .findUnique();
            if (account == null || !BCrypt.checkpw(memberForm.getPassword(), account.getPassword())) {
                return badRequest(MemberFormView.render(bindedForm));
            }
        }

        session("id", account.getId().toString());

        return redirect(routes.HomeController.GET_Home());
    }

    /**
     * Log out a user.
     *
     * @return redirect to the home page
     * @since 17.12.03
     */
    public Result GET_Logout() {
        session().clear();
        Http.Context.current().args.remove("user");

        return redirect(routes.HomeController.GET_Home());
    }
}
