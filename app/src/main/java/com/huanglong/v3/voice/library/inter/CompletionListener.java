package com.huanglong.v3.voice.library.inter;

import java.util.List;

/**
 * Created by Karthik on 22/01/16.
 */
public interface CompletionListener {

    public void onProcessCompleted(String message, List<String> merger);

}
