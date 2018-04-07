package cn.chestnut.mvvm.teamworker.module.team;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityBuildTeamBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.Department;
import cn.chestnut.mvvm.teamworker.model.DepartmentVo;
import cn.chestnut.mvvm.teamworker.model.MyFriend;
import cn.chestnut.mvvm.teamworker.model.PhoneDirectoryPerson;
import cn.chestnut.mvvm.teamworker.utils.GlideLoader;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import cn.chestnut.mvvm.teamworker.utils.photo.ProcessPhotoUtils;

import static cn.chestnut.mvvm.teamworker.module.team.SelectFromMyFriendActivity.FROM_MY_FRIEND;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/3 22:20:12
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class BuildTeamActivity extends BaseActivity {

    private ActivityBuildTeamBinding binding;

    private List<MyFriend> personList;

    private ArrayList<String> userIdList;

    private BuildTeamAdapter adapter;

    private ProcessPhotoUtils processPhotoUtils;

    private String filePath;

    private String qiniuToken;

    private String pictureKey;

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
        if (requestCode == ProcessPhotoUtils.UPLOAD_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
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
                GlideLoader.displayImage(BuildTeamActivity.this, filePath, binding.ivTeamBadge);
            }
        } else if (requestCode == ProcessPhotoUtils.SHOOT_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            filePath = processPhotoUtils.getMyPhotoFile().getPath();
            if (StringUtil.isStringNotNull(filePath)) {
                GlideLoader.displayImage(BuildTeamActivity.this, filePath, binding.ivTeamBadge);
            }
            Log.d("filePath " + filePath);
        } else if (requestCode == FROM_MY_FRIEND && resultCode == Activity.RESULT_OK) {
            MyFriend myFriend = (MyFriend) data.getSerializableExtra("person");
            Log.d("person" + myFriend.getNickname());
            personList.add(myFriend);
            userIdList.add(myFriend.getUserId());
            adapter.notifyItemInserted(personList.size());
            adapter.notifyDataSetChanged();
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
        binding.llPersonnelScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new AlertDialog.Builder(BuildTeamActivity.this).setTitle("请选择团队人员规模")
                        .setItems(new String[]{"1~10人", "11~20人", "21~50人", "51~100人", "101~200人", "201~500人", "501~1000人", "1001~2000人"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        binding.tvPersonnelScale.setText("1~10人");
                                        break;
                                    case 1:
                                        binding.tvPersonnelScale.setText("11~20人");
                                        break;
                                    case 2:
                                        binding.tvPersonnelScale.setText("21~50人");
                                        break;
                                    case 3:
                                        binding.tvPersonnelScale.setText("51~100人");
                                        break;
                                    case 4:
                                        binding.tvPersonnelScale.setText("101~200人");
                                        break;
                                    case 5:
                                        binding.tvPersonnelScale.setText("201~500人");
                                        break;
                                    case 6:
                                        binding.tvPersonnelScale.setText("501~1000人");
                                        break;
                                    case 7:
                                        binding.tvPersonnelScale.setText("1001~2000人");
                                        break;
                                }
                                dialog.dismiss();
                            }
                        }).create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });

        binding.llRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.llAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuildTeamActivity.this, SelectFromMyFriendActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("userIdList", userIdList);
                intent.putExtras(bundle);
                startActivityForResult(intent, SelectFromMyFriendActivity.FROM_MY_FRIEND);
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
                        && StringUtil.isStringNotNull(binding.tvPersonnelScale.getText().toString())
                        && StringUtil.isStringNotNull(binding.tvRegion.getText().toString())
                        ) {
                    if (StringUtil.isStringNotNull(filePath)) {
                        uploadPicture(filePath);
                    }
                } else {
                    showToast("请填写带红色*号的团队信息");
                }
            }
        });

    }

    private void uploadPicture(String filePath, String token) {
        MyApplication.getUploadManager().put(filePath, null, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if (info.isOK()) {
                            Log.i("qiniu Upload Success");
                            try {
                                pictureKey = res.getString("key");
                                buildTeam();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.i("qiniu Upload Fail");
                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                        }
                        Log.i("qiniu " + key + ",\r\n " + info + ",\r\n " + res);
                    }
                }, null);

    }

    private void uploadPicture(final String filePath) {

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
                    Log.e(error.toString());
                }

                @Override
                public void complete() {

                }

            });

        else {
            uploadPicture(filePath, qiniuToken);
        }
    }

    private void buildTeam() {
        Department department = new Department();
        department.setDepartmentName(binding.etTeamName.getText().toString());
        if (StringUtil.isStringNotNull(pictureKey)) {
            department.setDepartmentBadge(pictureKey);
        }
        department.setDepartmentIndustry(binding.etTeamIndustry.getText().toString());
        department.setDepartmentRegion(binding.tvRegion.getText().toString());
        department.setPersonnelScale(binding.tvPersonnelScale.getText().toString());

        buildTeam(department, userIdList);
    }

    private void buildTeam(Department department, List<String> userIdList) {
        DepartmentVo departmentVo = new DepartmentVo(department, userIdList);
        RequestManager.getInstance(this).executeRequest(HttpUrls.BUILD_TEAM, departmentVo, new AppCallBack<ApiResponse<String>>() {
            @Override
            public void next(ApiResponse<String> response) {
                if (response.isSuccess()) {

                } else {
                    showToast(response.getMessage());
                }
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
