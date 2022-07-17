package com.siltech.cryptochat.updater;

import com.siltech.cryptochat.updater.models.Update;

public interface UpdateListener{

    void onSuccess(Update update, boolean isUpdateAvailable);

    void onFailed(String error);
}