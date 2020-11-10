package com.example.intigritirobotics.ui.E_Store;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EStoreViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EStoreViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}