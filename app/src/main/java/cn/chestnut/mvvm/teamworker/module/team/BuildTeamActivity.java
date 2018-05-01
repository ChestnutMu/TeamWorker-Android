package cn.chestnut.mvvm.teamworker.module.team;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityBuildTeamBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.Team;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.module.mine.SelectRegionActivity;
import cn.chestnut.mvvm.teamworker.utils.GlideLoader;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import cn.chestnut.mvvm.teamworker.utils.photo.ProcessPhotoUtils;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/3 22:20:12
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class BuildTeamActivity extends BaseActivity {

    private static final String TAG = "BuildTeamActivity";

    private ActivityBuildTeamBinding binding;

    private List<User> personList;

    private ArrayList<String> userIdList;

    private BuildTeamAdapter adapter;

    private ProcessPhotoUtils processPhotoUtils;

    private String filePath;

    private String qiniuToken;

    private String pictureKey;

    public int FROM_MY_FRIEND = 3;//从我的好友列表中选择用户加入团队

    public int FROM_SEARCH_ACCOUNT = 4;//搜索用户加入团队

    private int SELECT_REGION_REQUESTCODE = 5;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("创建团队");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_build_team, viewGroup, true);
        initData();
        addListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ProcessPhotoUtils.UPLOAD_PHOTO_REQUEST_CODE) {
                Uri originalUri = data.getData(); // 获得图片的uri
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                //获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                cursor.moveToFirst();
                //获得图片的uri
                filePath = cursor.getString(column_index);
                Log.d("filePath " + filePath);
                if (StringUtil.isStringNotNull(filePath)) {
                    uploadPicture(filePath);
                }
            } else if (requestCode == ProcessPhotoUtils.SHOOT_PHOTO_REQUEST_CODE) {
                filePath = processPhotoUtils.getMyPhotoFile().getPath();
                if (StringUtil.isStringNotNull(filePath)) {
                    uploadPicture(filePath);
                }
                Log.d("filePath " + filePath);
            } else if (requestCode == FROM_MY_FRIEND) {
                User user = (User) data.getSerializableExtra("person");
                Log.d(TAG + ":person = " + user.getNickname());
                personList.add(user);
                userIdList.add(user.getUserId());
                adapter.notifyItemInserted(personList.size());
                adapter.notifyDataSetChanged();
            } else if (requestCode == FROM_SEARCH_ACCOUNT) {
                User user = (User) data.getSerializableExtra("person");
                Log.d(TAG + ":person = " + user.getNickname());
                personList.add(user);
                userIdList.add(user.getUserId());
                adapter.notifyItemInserted(personList.size());
                adapter.notifyDataSetChanged();
            } else if (requestCode == SELECT_REGION_REQUESTCODE) {
                Log.d(TAG + ":region = " + data.getStringExtra("region"));
                binding.tvRegion.setText(data.getStringExtra("region"));
            }
        }
    }

    protected void initData() {
        processPhotoUtils = new ProcessPhotoUtils(this);
        personList = new ArrayList<>();
        userIdList = new ArrayList<>();
        adapter = new BuildTeamAdapter(personList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setNestedScrollingEnabled(false);
        binding.recyclerView.setAdapter(adapter);
    }

    protected void addListener() {

        binding.llRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(BuildTeamActivity.this, SelectRegionActivity.class), SELECT_REGION_REQUESTCODE);
            }
        });

        binding.llAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMemberWayDialog();
            }
        });

        binding.ivTeamBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPhotoUtils = new ProcessPhotoUtils(BuildTeamActivity.this);
                processPhotoUtils.startPhoto();
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isStringNotNull(binding.etTeamName.getText().toString())
                        && StringUtil.isStringNotNull(binding.etTeamIndustry.getText().toString())
                        && StringUtil.isStringNotNull(binding.etTeamDesc.getText().toString())
                        && StringUtil.isStringNotNull(binding.tvRegion.getText().toString())
                        ) {
                    buildTeam();
                } else {
                    showToast("请填写带红色*号的团队信息");
                }
            }
        });

        adapter.setRemoveMemberListener(new BuildTeamAdapter.RemoveMemberListener() {
            @Override
            public void removeMember(int position) {
                personList.remove(position);
                userIdList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void showAddMemberWayDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("请选择你要添加成员的方式")
                .setItems(new String[]{"搜索账号", "好友列表"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent0 = new Intent(BuildTeamActivity.this, SelectFromSearchUserActivity.class);
                                Bundle bundle0 = new Bundle();
                                bundle0.putStringArrayList("userIdList", userIdList);
                                intent0.putExtras(bundle0);
                                startActivityForResult(intent0, FROM_SEARCH_ACCOUNT);
                                break;
                            case 1:
                                Intent intent1 = new Intent(BuildTeamActivity.this, SelectFromMyFriendActivity.class);
                                Bundle bundle1 = new Bundle();
                                bundle1.putStringArrayList("userIdList", userIdList);
                                intent1.putExtras(bundle1);
                                startActivityForResult(intent1, FROM_MY_FRIEND);
                                break;
                        }
                    }
                }).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void uploadPicture(final String filePath, String token) {
        showProgressDialog(this);
        MyApplication.getUploadManager().put(filePath, null, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        hideProgressDialog();
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if (info.isOK()) {
                            showToast("照片上传成功");
                            Log.i("qiniu Upload Success");
                            try {
                                pictureKey = res.getString("key");
                                GlideLoader.displayImage(BuildTeamActivity.this, filePath, binding.ivTeamBadge);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast("照片上传失败");
                            Log.i("qiniu Upload Fail");
                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                        }
                        Log.i("qiniu " + key + ",\r\n " + info + ",\r\n " + res);
                    }
                }, null);

    }

    private void uploadPicture(final String filePath) {
        showProgressDialog(this);
        if (StringUtil.isEmpty(qiniuToken))

            RequestManager.getInstance(this).executeRequest(HttpUrls.GET_QINIUTOKEN, null, new AppCallBack<ApiResponse<String>>() {

                @Override
                public void next(ApiResponse<String> response) {
                    if (response.isSuccess()) {
                        qiniuToken = response.getData();
                        uploadPicture(filePath, qiniuToken);
                    } else {
                        showToast(response.getMessage());
                    }
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

        else {
            uploadPicture(filePath, qiniuToken);
        }
    }

    private void buildTeam() {
        Team team = new Team();
        if (StringUtil.isStringNotNull(binding.etTeamName.getText().toString()) &&
                StringUtil.isStringNotNull(binding.etTeamIndustry.getText().toString()) &&
                StringUtil.isStringNotNull(binding.tvRegion.getText().toString()) &&
                StringUtil.isStringNotNull(binding.etTeamDesc.getText().toString())) {

            team.setTeamName(binding.etTeamName.getText().toString());
            if (StringUtil.isStringNotNull(pictureKey)) {
                team.setTeamBadge(pictureKey);
            }
            team.setTeamIndustry(binding.etTeamIndustry.getText().toString());
            team.setTeamRegion(binding.tvRegion.getText().toString());
            team.setTeamDesc(binding.etTeamDesc.getText().toString());

            buildTeam(team, userIdList);
        } else {
            showToast("请填写带红色*的团队信息");
        }

    }

    private void buildTeam(Team team, List<String> userIdList) {
        Gson gson = new Gson();
        Map<String, Object> params = new HashMap<>(2);
        params.put("team", team);
        params.put("userList", gson.toJson(userIdList));
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.BUILD_TEAM, params, new AppCallBack<ApiResponse<Team>>() {
            @Override
            public void next(ApiResponse<Team> response) {
                showToast(response.getMessage());
                Intent intent = new Intent();
                intent.putExtra("newTeam", response.getData());
                setResult(RESULT_OK, intent);
                finish();
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
}
