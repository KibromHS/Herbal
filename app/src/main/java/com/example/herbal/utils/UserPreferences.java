package com.example.herbal.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.herbal.models.HealerModel;
import com.example.herbal.models.UserModel;

public class UserPreferences {
    private static SharedPreferences preferences;
    private static final String PREF_KEY = "key-data";
    private static final String USER_KEY = "key-user";

    public static void init(@NonNull Context context) {
        preferences = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
    }

    public static void setUser(@NonNull UserModel user) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_KEY, user.toJson());
        editor.apply();
    }

    @Nullable
    public static UserModel getUser() {
        String json = preferences.getString(USER_KEY, null);
        return json == null ? null : UserModel.fromJson(json);
    }

    public static void setHealer(HealerModel healer) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_KEY, healer.toJson());
        editor.apply();
    }

    public static HealerModel getHealer() {
        String json = preferences.getString(USER_KEY, null);
        return json == null ? null : HealerModel.fromJson(json);
    }
}
