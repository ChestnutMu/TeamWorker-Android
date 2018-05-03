package cn.chestnut.mvvm.teamworker.module.mine;

import android.databinding.DataBindingUtil;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityChangePasswordBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.utils.MD5;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;

public class ChangePasswordActivity extends BaseActivity {

    private ActivityChangePasswordBinding binding;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("修改密码");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_change_password, viewGroup, true);
        addListener();
    }

    @Override
    protected void addListener() {
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = binding.etOldPassword.getText().toString();
                String newPassword = binding.etNewPassword.getText().toString();
                if (StringUtil.isBlank(oldPassword)) {
                    showToast("旧密码不能为空");
                } else if (StringUtil.isBlank(newPassword)) {
                    showToast("新密码不能为空");
                } else if (oldPassword.length() < 6 || newPassword.length() < 6) {
                    showToast("密码不能小于6位");
                } else {
                    updatePassword(oldPassword, newPassword);
                }
            }
        });
    }

    private void updatePassword(String oldPassword, String newPassword) {
        ArrayMap<String, String> params = new ArrayMap<>(2);
        oldPassword = MD5.MD5(oldPassword);
        newPassword = MD5.MD5(newPassword);
        params.put("oldPassword", oldPassword);
        params.put("newPassword", newPassword);
        RequestManager.getInstance(this).executeRequest(HttpUrls.UPDATE_PASSWORD, params, new AppCallBack<ApiResponse<Object>>() {
            @Override
            public void next(ApiResponse<Object> response) {
                showToast(response.getMessage());
            }

            @Override
            public void error(Throwable error) {

            }

            @Override
            public void complete() {

            }
        });
    }
}
