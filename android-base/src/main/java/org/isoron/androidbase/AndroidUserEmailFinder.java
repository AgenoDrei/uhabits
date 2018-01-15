package org.isoron.androidbase;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Patterns;

import java.util.regex.Pattern;

import javax.inject.Inject;

public class AndroidUserEmailFinder {

    @NonNull
    private Context context;

    @Inject
    public AndroidUserEmailFinder(@NonNull @AppContext Context context)
    {
        this.context = context;
    }

    @Nullable
    public String getUserEmail() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();

        if(accounts.length == 0)
            return "anonymous@loophabits.org";

        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                return account.name;
            }
        }
        return "anonymous@loophabits.org";
    }
}
