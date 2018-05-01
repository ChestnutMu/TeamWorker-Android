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
import cn.chestnut.mvvm.teamworker.databinding.ActivityPurchaseDetailBinding;
import cn.chestnut.mvvm.teamworker.databinding.ActivityUseGoodDetailBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.Purchase;
import cn.chestnut.mvvm.teamworker.model.UseGood;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/29 14:55:02
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class PurchaseDetailActivity extends BaseActivity {

    private ActivityPurchaseDetailBinding binding;

    private Purchase purchase;

    private PurchaseDetailAdapter adapter;

    private List<PurchaseDetails> purchaseDetailsList;

    private static int PASS_PURCHASE_STATUS = 1; //通过采购申请

    private static int OFF_PASS_PURCHASE_STATUS = 2;//不通过采购申请

    /**
     * 请假状态 -1收回请求 0 已申请，待审批；1 已审批，通过；2 已审批，不通过
     */
    public static final int STATUS_PURCHASE_RETURN = -1;

    public static final int STATUS_PURCHASE_WAITING = 0;

    public static final int STATUS_PURCHASE_PASS = 1;

    public static final int STATUS_PURCHASE_UNPASS = 2;

    private boolean isUpdated = false;


    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("物品领用申请详情");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_purchase_detail, viewGroup, true);
        initView();
        addListener();
    }

    @Override
    protected void initView() {
        purchase = (Purchase) getIntent().getSerializableExtra("purchase");

        //如果物品领用申请的发起人是自己且物品领用申请的状态为待处理则显示"回收物品领用申请"按钮
        if (purchase.getStatus() == STATUS_PURCHASE_WAITING) {
            if (purchase.getUserId().equals(PreferenceUtil.getInstances(this).getPreferenceString("userId"))) {
                binding.tvReturn.setVisibility(View.VISIBLE);
            } else {  //如果物品领用申请的发起人不是自己且物品领用申请的状态为待处理则显示"通过"和"不通过"按钮
                binding.llApproval.setVisibility(View.VISIBLE);
            }
        }
        binding.setPurchase(purchase);

        purchaseDetailsList = new ArrayList<>();
        adapter = new PurchaseDetailAdapter(this, R.layout.item_purchase_detail, BR.purchase, purchaseDetailsList);

        //初始化采购申请时间轴的第一条数据
        PurchaseDetails firstUseGood = new PurchaseDetails();
        firstUseGood.setTime(purchase.showCommitTime());
        firstUseGood.setNickname(purchase.getUserNickname());
        firstUseGood.setAvatar(purchase.getUserAvatar());
        firstUseGood.setAction("发起申请");
        purchaseDetailsList.add(firstUseGood);

        //初始化采购申请时间轴的第二条数据
        int useGoodStatus = purchase.getStatus();
        if (useGoodStatus == STATUS_PURCHASE_RETURN) {
            PurchaseDetails purchaseDetails = new PurchaseDetails();
            purchaseDetails.setTime(purchase.showHandleTime());
            purchaseDetails.setNickname(purchase.getUserNickname());
            purchaseDetails.setAvatar(purchase.getUserAvatar());
            purchaseDetails.setAction("已收回采购申请");
            purchaseDetails.setHandleReason(purchase.getHandleReason());
            purchaseDetailsList.add(purchaseDetails);
        } else if (useGoodStatus == STATUS_PURCHASE_WAITING) {
            PurchaseDetails purchaseDetails = new PurchaseDetails();
            purchaseDetails.setTime(purchase.getWaitedTime());//多长时间之间申请的
            purchaseDetails.setNickname(purchase.getUserNickname());
            purchaseDetails.setAvatar(purchase.getUserAvatar());
            purchaseDetails.setAction("等待审批中");
            purchaseDetailsList.add(purchaseDetails);
        } else if (useGoodStatus == STATUS_PURCHASE_PASS) {
            PurchaseDetails purchaseDetails = new PurchaseDetails();
            purchaseDetails.setTime(purchase.showHandleTime());
            purchaseDetails.setNickname(purchase.getAdminNickname());
            purchaseDetails.setAvatar(purchase.getAdminAvatar());
            purchaseDetails.setAction("已审批，通过");
            purchaseDetails.setHandleReason(purchase.getHandleReason());
            purchaseDetailsList.add(purchaseDetails);
        } else if (useGoodStatus == STATUS_PURCHASE_UNPASS) {
            PurchaseDetails purchaseDetails = new PurchaseDetails();
            purchaseDetails.setTime(purchase.showCommitTime());
            purchaseDetails.setNickname(purchase.getAdminNickname());
            purchaseDetails.setAvatar(purchase.getAdminAvatar());
            purchaseDetails.setAction("已审批，不通过");
            purchaseDetails.setHandleReason(purchase.getHandleReason());
            purchaseDetailsList.add(purchaseDetails);
        }
        binding.lvPurchase.setAdapter(adapter);

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
                .setTitle("请输入通过采购申请的理由")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() > 0) {
                            handleUseGood(editText.getText().toString(), PASS_PURCHASE_STATUS);
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
                .setTitle("请输入不通过采购申请的理由")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().length() > 0) {
                            handleUseGood(editText.getText().toString(), OFF_PASS_PURCHASE_STATUS);
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
                .setTitle("请输入回收采购申请的理由")
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
        params.put("purchaseId", purchase.getPurchaseId());
        params.put("handleReason", handleReason);
        params.put("handleStatus", handleStatus);
        params.put("nickname", PreferenceUtil.getInstances(this).getPreferenceString("nickname"));
        params.put("avatar", PreferenceUtil.getInstances(this).getPreferenceString("avatar"));
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.HANDLE_PURCHASE, params, new AppCallBack<ApiResponse<UseGood>>() {
            @Override
            public void next(ApiResponse<UseGood> response) {
                if (response.isSuccess()) {
                    isUpdated = true;
                    binding.tvStatus.setText(response.getData().showStatus());
                    binding.llApproval.setVisibility(View.GONE);
                    purchaseDetailsList.remove(1);
                    PurchaseDetails purchaseDetails = new PurchaseDetails();
                    purchaseDetails.setTime(response.getData().showHandleTime());
                    purchaseDetails.setNickname(response.getData().getAdminNickname());
                    purchaseDetails.setAvatar(response.getData().getAdminAvatar());
                    purchaseDetails.setAction(response.getData().showStatus());
                    purchaseDetails.setHandleReason(response.getData().getHandleReason());
                    purchaseDetailsList.add(purchaseDetails);
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
        params.put("purchaseId", purchase.getPurchaseId());
        params.put("handleReason", handleReason);
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.RETURN_PURCHASE, params, new AppCallBack<ApiResponse<UseGood>>() {
            @Override
            public void next(ApiResponse<UseGood> response) {
                if (response.isSuccess()) {
                    isUpdated = true;
                    binding.tvStatus.setText(response.getData().showStatus());
                    binding.tvReturn.setVisibility(View.GONE);
                    purchaseDetailsList.remove(1);
                    PurchaseDetails purchaseDetails = new PurchaseDetails();
                    purchaseDetails.setTime(response.getData().showHandleTime());
                    purchaseDetails.setNickname(response.getData().getUserNickname());
                    purchaseDetails.setAvatar(response.getData().getUserAvatar());
                    purchaseDetails.setAction("已收回采购申请");
                    purchaseDetails.setHandleReason(response.getData().getHandleReason());
                    purchaseDetailsList.add(purchaseDetails);
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

    protected class PurchaseDetails {
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
