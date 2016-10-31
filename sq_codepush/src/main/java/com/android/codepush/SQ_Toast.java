package com.android.codepush;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by zhang on 2016/10/28.
 */

public class SQ_Toast {

    private Context mCt;
    private Toast mToast=null;

    public SQ_Toast(Context mCt) {
        this.mCt = mCt;
    }

    public void ShortShow(String msg){
        if(mToast==null){
           createToast(0,msg);
        }
        mToast.show();
    }

    public void LongShow(String msg){
        if(mToast==null){
            createToast(1,msg);
        }
        mToast.show();
    }

    public void  createToast(int Type,String msg){
        mToast=new Toast(mCt);
        mToast.setText(msg);
        if(Type==0){
            mToast.setDuration(Toast.LENGTH_SHORT);
        }else{
            mToast.setDuration(Toast.LENGTH_LONG);
        }
    }
}
