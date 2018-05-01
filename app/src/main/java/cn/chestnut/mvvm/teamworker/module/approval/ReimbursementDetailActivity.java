package cn.chestnut.mvvm.teamworker.module.approval;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import cn.chestnut.mvvm.teamworker.databinding.ActivityReimbursementDetailBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.Reimbursement;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/29 14:55:02
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class ReimbursementDetailActivity extends BaseActivity {

    private ActivityReimbursementDetailBinding binding;

    private Reimbursement reimbursement;

    private ReimbursementDetailAdapter adapter;

    private List<ReimbursementDetail> reimbursementDetailList;

    private static int PASS_REIMBURSEMENT_STATUS = 1; //通过报销申请

    private static int OFF_PASS_REIMBURSEMENT_STATUS = 2;//不通过报销申请

    private boolean isUpdated = false;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("请假条详情");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_reimbursement_detail, viewGroup, true);
        initView();
        addListener();
    }

    @Override
    protected void initView() {
        reimbursement = (Reimbursement) getIntent().getSerializableExtra("reimbursement");

        //如果请假条的发起人是自己且请假条的状态为待处理则显示"回收请假条"按钮
        if (reimbursement.getStatus().equals("已申请，待审批")) {
            if (reimbursement.getUserId().equals(PreferenceUtil.getInstances(this).getPreferenceString("userId"))) {
                binding.tvReturn.setVisibility(View.VISIBLE);
            } else {  //如果请假条的发起人不是自己且请假条的状态为待处理则显示"通过"和"不通过"按钮
                binding.llApproval.setVisibility(View.VISIBLE);
            }
        }
        binding.setReimbursement(reimbursement);

        Log.d("sdflsdfs");
        reimbursementDetailList = new ArrayList<>();
        adapter = new ReimbursementDetailAdapter(this, R.layout.item_reimbursement_detail, BR.reimbursement, reimbursementDetailList);

        //初始化请假时间时间轴的第一条数据
        ReimbursementDetail firstReimbursement = new ReimbursementDetail();
        firstReimbursement.setTime(reimbursement.getCommitTime());
        firstReimbursement.setNickname(reimbursement.getUserNickname());
        firstReimbursement.setAvatar(reimbursement.getUserAvatar());
        firstReimbursement.setAction("发起申请");
        reimbursementDetailList.add(firstReimbursement);

        //初始化请假时间时间轴的第二条数据
        String reimbursementStatus = reimbursement.getStatus();
        if (reimbursementStatus.equals("已被收回")) {
            ReimbursementDetail reimbursementDetail = new ReimbursementDetail();
            reimbursementDetail.setTime(reimbursement.getHandleTime());
            reimbursementDetail.setNickname(reimbursement.getUserNickname());
            reimbursementDetail.setAvatar(reimbursement.getUserAvatar());
            reimbursementDetail.setAction("已收回报销申请");
            reimbursementDetail.setHandleReason(reimbursement.getHandleReason());
            reimbursementDetailList.add(reimbursementDetail);
        } else if (reimbursementStatus.equals("已申请，待审批")) {
            ReimbursementDetail reimbursementDetail = new ReimbursementDetail();
            reimbursementDetail.setTime(reimbursement.getWaitedTime());//多长时间之间申请的报销
            reimbursementDetail.setNickname(reimbursement.getUserNickname());
            reimbursementDetail.setAvatar(reimbursement.getUserAvatar());
            reimbursementDetail.setAction("等待审批中");
            reimbursementDetailList.add(reimbursementDetail);
        } else if (reimbursementStatus.equals("已审批，通过")) {
            ReimbursementDetail reimbursementDetail = new ReimbursementDetail();
            reimbursementDetail.setTime(reimbursement.getHandleTime());
            reimbursementDetail.setNickname(reimbursement.getAdminNickname());
            reimbursementDetail.setAvatar(reimbursement.getAdminAvatar());
            reimbursementDetail.setAction("已审批，通过");
            reimbursementDetail.setHandleReason(reimbursement.getHandleReason());
            reimbursementDetailList.add(reimbursementDetail);
        } else if (reimbursementStatus.equals("已审批，不通过")) {
            ReimbursementDetail reimbursementDetail = new ReimbursementDetail();
            reimbursementDetail.setTime(reimbursement.getCommitTime());
            reimbursementDetail.setNickname(reimbursement.getAdminNickname());
            reimbursementDetail.setAvatar(reimbursement.getAdminAvatar());
            reimbursementDetail.setAction("已审批，不通过");
            reimbursementDetail.setHandleReason(reimbursement.getHandleReason());
            reimbursementDetailList.add(reimbursementDetail);
        }
        binding.lvReimbursement.setAdapter(adapter);

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
                .setTitle("请输入通过报销申请的理由")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() > 0) {
                            handleReimbursement(editText.getText().toString(), PASS_REIMBURSEMENT_STATUS);
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
                .setTitle("请输入不通过报销申请的理由")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() > 0) {
                            handleReimbursement(editText.getText().toString(), OFF_PASS_REIMBURSEMENT_STATUS);
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
                .setTitle("请输入回收报销申请的理由")
                .setView(editText)
                .setPositiveButton("回收", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() > 0) {
                            returnReimbursement(editText.getText().toString());
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

    private void handleReimbursement(String handleReason, int handleStatus) {
        ArrayMap<String, Object> params = new ArrayMap<>();
        params.put("reimbursementId", reimbursement.getReimbursementId());
        params.put("handleReason", handleReason);
        params.put("handleStatus", handleStatus);
        params.put("nickname", PreferenceUtil.getInstances(this).getPreferenceString("nickname"));
        params.put("avatar", PreferenceUtil.getInstances(this).getPreferenceString("avatar"));
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.HANDLE_REIMBURSEMENT, params, new AppCallBack<ApiResponse<Reimbursement>>() {
            @Override
            public void next(ApiResponse<Reimbursement> response) {
                if (response.isSuccess()) {
                    isUpdated = true;
                    binding.tvStatus.setText(response.getData().getStatus());
                    binding.llApproval.setVisibility(View.GONE);
                    reimbursementDetailList.remove(1);
                    ReimbursementDetail reimbursementDetail = new ReimbursementDetail();
                    reimbursementDetail.setTime(response.getData().getHandleTime());
                    reimbursementDetail.setNickname(response.getData().getAdminNickname());
                    reimbursementDetail.setAvatar(response.getData().getAdminAvatar());
                    reimbursementDetail.setAction(response.getData().getStatus());
                    reimbursementDetail.setHandleReason(response.getData().getHandleReason());
                    reimbursementDetailList.add(reimbursementDetail);
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

    private void returnReimbursement(String handleReason) {
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("reimbursementId", reimbursement.getReimbursementId());
        params.put("handleReason", handleReason);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.RETURN_REIMBURSEMENT, params, new AppCallBack<ApiResponse<Reimbursement>>() {
            @Override
            public void next(ApiResponse<Reimbursement> response) {
                if (response.isSuccess()) {
                    isUpdated = true;
                    binding.tvStatus.setText(response.getData().getStatus());
                    binding.tvReturn.setVisibility(View.GONE);
                    reimbursementDetailList.remove(1);
                    ReimbursementDetail reimbursementDetail = new ReimbursementDetail();
                    reimbursementDetail.setTime(response.getData().getHandleTime());
                    reimbursementDetail.setNickname(response.getData().getUserNickname());
                    reimbursementDetail.setAvatar(response.getData().getUserAvatar());
                    reimbursementDetail.setAction("已收回报销申请");
                    reimbursementDetail.setHandleReason(response.getData().getHandleReason());
                    reimbursementDetailList.add(reimbursementDetail);
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

    protected class ReimbursementDetail {
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
