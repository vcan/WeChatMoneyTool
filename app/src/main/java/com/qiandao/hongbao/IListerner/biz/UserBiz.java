package com.qiandao.hongbao.IListerner.biz;

import android.content.Context;

import com.qiandao.hongbao.IListerner.IUserBiz;
import com.qiandao.hongbao.IListerner.OnLoginListener;
import com.qiandao.hongbao.IListerner.OnRegisterListener;
import com.qiandao.hongbao.bean.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Dexter0218 on 2016/10/27.
 */
public class UserBiz implements IUserBiz {
    @Override
    public void login(String userName, String password, final OnLoginListener loginListener, Context context) {
        final User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        user.login(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    loginListener.OnSuccess(user);
                } else {
                    loginListener.OnFailed();
                }
            }
        });
    }

    @Override
    public void register(String userName, String password, String email, final OnRegisterListener registerListener, Context context) {
        final User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        user.login(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    registerListener.OnSuccess();
                } else {
                    registerListener.OnError();
                }
            }
        });
    }
}