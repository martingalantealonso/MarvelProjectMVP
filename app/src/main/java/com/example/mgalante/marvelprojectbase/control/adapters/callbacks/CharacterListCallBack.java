package com.example.mgalante.marvelprojectbase.control.adapters.callbacks;

import android.view.View;

import com.example.mgalante.marvelprojectbase.api.entities.Characters;

/**
 * Created by mgalante on 18/01/17.
 */

public interface CharacterListCallBack {
    void onClickCharacter(View v, Characters item);
}
