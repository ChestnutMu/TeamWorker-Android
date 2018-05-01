package cn.chestnut.mvvm.teamworker.module.approval;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityUseGoodDetailBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.UseGood;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/29 14:55:02
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class UseGoodDetailActivity extends BaseActivity {

    private ActivityUseGoodDetailBinding binding;

    private UseGood useGood;

    private UseGoodDetailAdapter adapter;

    private List<UseGoodDetails> useGoodDetailsList;

    private static int PASS_USE_GOOD_STATUS = 1; //通过物品领用申请

    private static int OFF_PASS_USE_GOOD_STATUS = 2;//不通过物品领用申请

    /**
     * 请假状态 -1收回请求 0 已申请，待审批；1 已审批，通过；2 已审批，不通过
     */
    public static final int STATUS_USE_GOOD_RETURN = -1;

    public static final int STATUS_USE_GOOD_WAITING = 0;

    public static final int STATUS_USE_GOOD_PASS = 1;

    public static final int STATUS_USE_GOOD_UNPASS = 2;

    private boolean isUpdated = false;


    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("物品领用申请详情");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_use_good_detail, viewGroup, true);
        initView();
        addListener();
    }

    @Override
    protected void initView() {
        useGood = (UseGood) getIntent().getSerializableExtra("useGood");

        //如果物品领用申请的发起人是自己且物品领用申请的状态为待处理则显示"回收物品领用申请"按钮
        if (useGood.getStatus() == STATUS_USE_GOOD_WAITING) {
            if (useGood.getUserId().equals(PreferenceUtil.getInstances(this).getPreferenceString("userId"))) {
                binding.tvReturn.setVisibility(View.VISIBLE);
            } else {  //如果物品领用申请的发起人不是自己且物品领用申请的状态为待处理则显示"通过"和"不通过"按钮
                binding.llApproval.setVisibility(View.VISIBLE);
            }
        }
        binding.setUseGood(useGood);

        useGoodDetailsList = new ArrayList<>();
        adapter = new UseGoodDetailAdapter(this, R.layout.item_use_good_detail, BR.useGood, useGoodDetailsList);

        //初始化物品领用时间轴的第一条数据
        UseGoodDetails firstUseGood = new UseGoodDetails();
        firstUseGood.setTime(useGood.showCommitTime());
        firstUseGood.setNickname(useGood.getUserNickname());
        firstUseGood.setAvatar(useGood.getUserAvatar());
        firstUseGood.setAction("发起申请");
        useGoodDetailsList.add(firstUseGood);

        //初始化请假时间时间轴的第二条数据
        int useGoodStatus = useGood.getStatus();
        if (useGoodStatus == STATUS_USE_GOOD_RETURN) {
            UseGoodDetails useGoodDetails = new UseGoodDetails();
            useGoodDetails.setTime(useGood.showHandleTime());
            useGoodDetails.setNickname(useGood.getUserNickname());
            useGoodDetails.setAvatar(useGood.getUserAvatar());
            useGoodDetails.setAction("已收回物品领用申请");
            useGoodDetails.setHandleReason(useGood.getHandleReason());
            useGoodDetailsList.add(useGoodDetails);
        } else if (useGoodStatus == STATUS_USE_GOOD_WAITING) {
            UseGoodDetails useGoodDetails = new UseGoodDetails();
            useGoodDetails.setTime(useGood.getWaitedTime());//多长时间之间申请的
            useGoodDetails.setNickname(useGood.getUserNickname());
            useGoodDetails.setAvatar(useGood.getUserAvatar());
            useGoodDetails.setAction("等待审批中");
            useGoodDetailsList.add(useGoodDetails);
        } else if (useGoodStatus == STATUS_USE_GOOD_PASS) {
            UseGoodDetails useGoodDetails = new UseGoodDetails();
            useGoodDetails.setTime(useGood.showHandleTime());
            useGoodDetails.setNickname(useGood.getAdminNickname());
            useGoodDetails.setAvatar(useGood.getAdminAvatar());
            useGoodDetails.setAction("已审批，通过");
            useGoodDetails.setHandleReason(useGood.getHandleReason());
            useGoodDetailsList.add(useGoodDetails);
        } else if (useGoodStatus == STATUS_USE_GOOD_UNPASS) {
            UseGoodDetails useGoodDetails = new UseGoodDetails();
            useGoodDetails.setTime(useGood.showCommitTime());
            useGoodDetails.setNickname(useGood.getAdminNickname());
            useGoodDetails.setAvatar(useGood.getAdminAvatar());
            useGoodDetails.setAction("已审批，不通过");
            useGoodDetails.setHandleReason(useGood.getHandleReason());
            useGoodDetailsList.add(useGoodDetails);
        }
        binding.lvUseGood.setAdapter(adapter);

    }

    @Override
    protected void addListener() {
        binding.tvReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReturnDialog();
            }
        });

        binding.tvPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPassDialog();
            }
        });

        binding.tvOffPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOffPassDialog();
            }
        });

    }

    private void showPassDialog() {
        final EditText editText = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("请输入通过物品领用申请的理由")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() > 0) {
                            handleUseGood(editText.getText().toString(), PASS_USE_GOOD_STATUS);
                        } else {
                            showToast("理由不能为空");
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();

    }

    private void showOffPassDialog() {
        final EditText editText = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("请输入不通过物品领用申请的理由")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() > 0) {
                            handleUseGood(editText.getText().toString(), OFF_PASS_USE_GOOD_STATUS);
                        } else {
                            showToast("理由不能为空");
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void showReturnDialog() {
        final EditText editText = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("请输入回收物品领用申请的理由")
                .setView(editText)
                .setPositiveButton("回收", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() > 0) {
                            returnUseGood(editText.getText().toString());
                        } else {
                            showToast("理由不能为空");
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void handleUseGood(String handleReason, int handleStatus) {
        ArrayMap<String, Object> params = new ArrayMap<>();
        params.put("useGoodId", useGood.getUseGoodId());
        params.put("handleReason", handleReason);
        params.put("handleStatus", handleStatus);
        params.put("nickname", PreferenceUtil.getInstances(this).getPreferenceString("nickname"));
        params.put("avatar", PreferenceUtil.getInstances(this).getPreferenceString("avatar"));
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.HANDLE_USE_GOOD, params, new AppCallBack<ApiResponse<UseGood>>() {
            @Override
            public void next(ApiResponse<UseGood> response) {
                if (response.isSuccess()) {
                    isUpdated = true;
                    binding.tvStatus.setText(response.getData().showStatus());
                    binding.llApproval.setVisibility(View.GONE);
                    useGoodDetailsList.remove(1);
                    UseGoodDetails useGoodDetails = new UseGoodDetails();
                    useGoodDetails.setTime(response.getData().showHandleTime());
                    useGoodDetails.setNickname(response.getData().getAdminNickname());
                    useGoodDetails.setAvatar(response.getData().getAdminAvatar());
                    useGoodDetails.setAction(response.getData().showStatus());
                    useGoodDetails.setHandleReason(response.getData().getHandleReason());
                    useGoodDetailsList.add(useGoodDetails);
                    adapter.notifyDataSetChanged();
                }
                showToast(response.getMessage());
            }

            @Override
            public void error(Throwable error) {
                hideProgressDialog();
            }

            @Override
            public void complete() {
                hideProgressDialog();
            }
        });
    }

    private void returnUseGood(String handleReason) {
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("useGoodId", useGood.getUseGoodId());
        params.put("handleReason", handleReason);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.RETURN_USE_GOOD, params, new AppCallBack<ApiResponse<UseGood>>() {
            @Override
            public void next(ApiResponse<UseGood> response) {
                if (response.isSuccess()) {
                    isUpdated = true;
                    binding.tvStatus.setText(response.getData().showStatus());
                    binding.tvReturn.setVisibility(View.GONE);
                    useGoodDetailsList.remove(1);
                    UseGoodDetails useGoodDetails = new UseGoodDetails();
                    useGoodDetails.setTime(response.getData().showHandleTime());
                    useGoodDetails.setNickname(response.getData().getUserNickname());
                    useGoodDetails.setAvatar(response.getData().getUserAvatar());
                    useGoodDetails.setAction("已收回物品领用申请");
                    useGoodDetails.setHandleReason(response.getData().getHandleReason());
                    useGoodDetailsList.add(useGoodDetails);
                    adapter.notifyDataSetChanged();
                }
                showToast(response.getMessage());
            }

            @Override
            public void error(Throwable error) {
                hideProgressDialog();
            }

            @Override
            public void complete() {
                hideProgressDialog();
            }
        });
    }

    protected class UseGoodDetails {
        private String time;
        private String nickname;
        private String avatar;
        private String action;
        private String handleReason;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getHandleReason() {
            return handleReason;
        }

        public void setHandleReason(String handleReason) {
            this.handleReason = handleReason;
        }
    }

    @Override
    public void finish() {
        if (isUpdated) {
            setResult(RESULT_OK);
        }
        super.finish();
    }

}
