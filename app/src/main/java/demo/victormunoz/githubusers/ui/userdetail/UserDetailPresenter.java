package demo.victormunoz.githubusers.ui.userdetail;

import android.app.Activity;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Named;

import demo.victormunoz.githubusers.ui.di.module.GitHubModule;
import demo.victormunoz.githubusers.ui.App;
import demo.victormunoz.githubusers.api.model.User;
import demo.victormunoz.githubusers.utils.espresso.EspressoIdlingResource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

public class UserDetailPresenter implements UserDetailContract.UserActionsListener, Callback<User> {

    private final UserDetailContract.View mUsersView;
    @Inject
    GitHubModule.GitHubApiInterface githubUserAPI;
    @Inject @Named("client_id")
    String clientID;
    @Inject @Named("client_secret")
    String clientSecret;

    public UserDetailPresenter(@NonNull Activity activity) {
        mUsersView = (UserDetailContract.View) checkNotNull(activity, "usersView cannot be null!");

    }
    /**
     *  Use the Retrofit library to ask for an user's information to the GitHub API.
     */
    @Override
    public void loadUserDetails(String login) {
        // prepare call in Retrofit 2.0
        Call<User> call = githubUserAPI.getUser(
                login,
                clientID,
                clientSecret);
        //asynchronous call
        call.enqueue(this);
        EspressoIdlingResource.increment();

    }

    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        EspressoIdlingResource.decrement();
        if (response.isSuccessful()) {
            User user = response.body();
            mUsersView.displayUserDetails(user);
        } else {
            mUsersView.onLoadUserDetailsFail();
        }
    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {
        EspressoIdlingResource.decrement();
        mUsersView.onLoadUserDetailsFail();

    }
}
