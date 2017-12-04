package controllers;

import models.Account;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.HomeView;

import java.util.List;

/**
 * HomeController.
 *
 * @author Jean-Gabriel Genest
 * @version 17.12.03
 * @since 17.12.03
 */
public class HomeController extends Controller {

    /**
     * Show the home page with the list of users.
     *
     * @return the home page with the list of users
     * @since 17.12.03
     */
    public Result GET_Home() {
        final List<Account> accounts = Account.db().find(Account.class)
                .orderBy("created_at DESC")
                .findList();

        return ok(HomeView.render(accounts));
    }
}
