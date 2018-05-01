package cn.chestnut.mvvm.teamworker.module.approval;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityWorkOffDetailBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.WorkOff;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/29 14:55:02
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class WorkOffDetailActivity extends BaseActivity {

    private ActivityWorkOffDetailBinding binding;

    private WorkOff workOff;

    private WorkOffDetailAdapter adapter;

    private List<WorkOffDetail> workOffDetailList;

    private static int PASS_WORK_OFF_STATUS = 1; //通过请假申请

    private static int OFF_PASS_WORK_OFF_STATUS = 2;//不通过请假申请

    private boolean isUpdated = false;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("请假条详情");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_work_off_detail, viewGroup, true);
        initView();
        addListener();
    }

    @Override
    protected void initView() {
        workOff = (WorkOff) getIntent().getSerializableExtra("workOff");

        //如果请假条的发起人是自己且请假条的状态为待处理则显示"回收请假条"按钮
        if (workOff.getStatus().equals("已申请，待审批")) {
            if (workOff.getUserId().equals(PreferenceUtil.getInstances(this).getPreferenceString("userId"))) {
                binding.tvReturn.setVisibility(View.VISIBLE);
            } else {  //如果请假条的发起人不是自己且请假条的状态为待处理则显示"通过"和"不通过"按钮
                binding.llApproval.setVisibility(View.VISIBLE);
            }
        }
        binding.setWorkOff(workOff);

        workOffDetailList = new ArrayList<>();
        adapter = new WorkOffDetailAdapter(this, R.layout.item_work_off_detail, BR.workOff, workOffDetailList);

        //初始化请假时间时间轴的第一条数据
        WorkOffDetail firstWorkOff = new WorkOffDetail();
        firstWorkOff.setTime(workOff.getCommitTime());
        firstWorkOff.setNickname(workOff.getUserNickname());
        firstWorkOff.setAvatar(workOff.getUserAvatar());
        firstWorkOff.setAction("发起申请");
        workOffDetailList.add(firstWorkOff);

        //初始化请假时间时间轴的第二条数据
        String workOffStatus = workOff.getStatus();
        if (workOffStatus.equals("已被收回")) {
            WorkOffDetail workOffDetail = new WorkOffDetail();
            workOffDetail.setTime(workOff.getHandleTime());
            workOffDetail.setNickname(workOff.getUserNickname());
            workOffDetail.setAvatar(workOff.getUserAvatar());
            workOffDetail.setAction("已收回请假条");
            workOffDetail.setHandleReason(workOff.getHandleReason());
            workOffDetailList.add(workOffDetail);
        } else if (workOffStatus.equals("已申请，待审批")) {
            WorkOffDetail workOffDetail = new WorkOffDetail();
            workOffDetail.setTime(workOff.getWaitedTime());//多长时间之间申请的请教条
            workOffDetail.setNickname(workOff.getUserNickname());
            workOffDetail.setAvatar(workOff.getUserAvatar());
            workOffDetail.setAction("等待审批中");
            workOffDetailList.add(workOffDetail);
        } else if (workOffStatus.equals("已审批，通过")) {
            WorkOffDetail workOffDetail = new WorkOffDetail();
            workOffDetail.setTime(workOff.getHandleTime());
            workOffDetail.setNickname(workOff.getAdminNickname());
            workOffDetail.setAvatar(workOff.getAdminAvatar());
            workOffDetail.setAction("已审批，通过");
            workOffDetail.setHandleReason(workOff.getHandleReason());
            workOffDetailList.add(workOffDetail);
        } else if (workOffStatus.equals("已审批，不通过")) {
            WorkOffDetail workOffDetail = new WorkOffDetail();
            workOffDetail.setTime(workOff.getCommitTime());
            workOffDetail.setNickname(workOff.getAdminNickname());
            workOffDetail.setAvatar(workOff.getAdminAvatar());
            workOffDetail.setAction("已审批，不通过");
            workOffDetail.setHandleReason(workOff.getHandleReason());
            workOffDetailList.add(workOffDetail);
        }
        binding.lvWorkOff.setAdapter(adapter);

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
                .setTitle("请输入通过请假申请的理由")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() > 0) {
                            handleWorkOff(editText.getText().toString(), PASS_WORK_OFF_STATUS);
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
                .setTitle("请输入不通过请假申请的理由")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() > 0) {
                            handleWorkOff(editText.getText().toString(), OFF_PASS_WORK_OFF_STATUS);
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
                .setTitle("请输入回收请假条的理由")
                .setView(editText)
                .setPositiveButton("回收", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() > 0) {
                            returnWorkOff(editText.getText().toString());
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

    private void handleWorkOff(String handleReason, int handleStatus) {
        ArrayMap<String, Object> params = new ArrayMap<>();
        params.put("workOffId", workOff.getWorkOffId());
        params.put("handleReason", handleReason);
        params.put("handleStatus", handleStatus);
        params.put("nickname", PreferenceUtil.getInstances(this).getPreferenceString("nickname"));
        params.put("avatar", PreferenceUtil.getInstances(this).getPreferenceString("avatar"));
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.HANDLE_WORK_OFF, params, new AppCallBack<ApiResponse<WorkOff>>() {
            @Override
            public void next(ApiResponse<WorkOff> response) {
                if (response.isSuccess()) {
                    isUpdated = true;
                    binding.tvStatus.setText(response.getData().getStatus());
                    binding.llApproval.setVisibility(View.GONE);
                    workOffDetailList.remove(1);
                    WorkOffDetail workOffDetail = new WorkOffDetail();
                    workOffDetail.setTime(response.getData().getHandleTime());
                    workOffDetail.setNickname(response.getData().getAdminNickname());
                    workOffDetail.setAvatar(response.getData().getAdminAvatar());
                    workOffDetail.setAction(response.getData().getStatus());
                    workOffDetail.setHandleReason(response.getData().getHandleReason());
                    workOffDetailList.add(workOffDetail);
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

    private void returnWorkOff(String handleReason) {
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("workOffId", workOff.getWorkOffId());
        params.put("handleReason", handleReason);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.RETURN_WORK_OFF, params, new AppCallBack<ApiResponse<WorkOff>>() {
            @Override
            public void next(ApiResponse<WorkOff> response) {
                if (response.isSuccess()) {
                    isUpdated = true;
                    binding.tvStatus.setText(response.getData().getStatus());
                    binding.tvReturn.setVisibility(View.GONE);
                    workOffDetailList.remove(1);
                    WorkOffDetail workOffDetail = new WorkOffDetail();
                    workOffDetail.setTime(response.getData().getHandleTime());
                    workOffDetail.setNickname(response.getData().getUserNickname());
                    workOffDetail.setAvatar(response.getData().getUserAvatar());
                    workOffDetail.setAction("已收回请假条");
                    workOffDetail.setHandleReason(response.getData().getHandleReason());
                    workOffDetailList.add(workOffDetail);
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

    protected class WorkOffDetail {
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
