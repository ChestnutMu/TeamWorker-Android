package cn.chestnut.mvvm.teamworker.module.team;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityTeamInformationBinding;
import cn.chestnut.mvvm.teamworker.http.ApiResponse;
import cn.chestnut.mvvm.teamworker.http.AppCallBack;
import cn.chestnut.mvvm.teamworker.http.HttpUrls;
import cn.chestnut.mvvm.teamworker.http.RequestManager;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.main.common.MyApplication;
import cn.chestnut.mvvm.teamworker.model.Team;
import cn.chestnut.mvvm.teamworker.model.User;
import cn.chestnut.mvvm.teamworker.model.UserInfo;
import cn.chestnut.mvvm.teamworker.module.massage.MessageDaoUtils;
import cn.chestnut.mvvm.teamworker.module.mine.MyInformationActivity;
import cn.chestnut.mvvm.teamworker.module.mine.SelectRegionActivity;
import cn.chestnut.mvvm.teamworker.utils.EmojiUtil;
import cn.chestnut.mvvm.teamworker.utils.GlideLoader;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.utils.PreferenceUtil;
import cn.chestnut.mvvm.teamworker.utils.StringUtil;
import cn.chestnut.mvvm.teamworker.utils.photo.ProcessPhotoUtils;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/29 21:49:29
 * Description：
 * Email: xiaoting233zhang@126.com
 */

public class TeamInformationActivity extends BaseActivity {

    private ActivityTeamInformationBinding binding;

    private Team team;

    private ProcessPhotoUtils processPhotoUtils;

    private String qiniuToken;

    private final int UPDATE_TEAM_BADGE = 1;

    private final int UPDATE_TEAM_NAME = 2;

    private final int UPDATE_TEAM_INDUSTRY = 3;

    private final int UPDATE_TEAM_REGION = 4;

    private final int UPDATE_TEAM_DESC = 5;

    private int SELECT_REGION_REQUESTCODE = 6;

    private boolean isUpdate = false;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("团队资料");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_team_information, viewGroup, true);
        initView();
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
            String filePath = cursor.getString(column_index);
            Log.d("filePath " + filePath);
            uploadPicture(filePath);
        } else if (requestCode == ProcessPhotoUtils.SHOOT_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String filePath = processPhotoUtils.getMyPhotoFile().getPath();
            Log.d("filePath " + filePath);
            uploadPicture(filePath);
        } else if (requestCode == SELECT_REGION_REQUESTCODE) {
            Team team = new Team();
            team.setTeamRegion(data.getStringExtra("region"));
            updateTeamInformation(team, UPDATE_TEAM_REGION);
        }
    }

    @Override
    protected void initView() {
        processPhotoUtils = new ProcessPhotoUtils(this);
        team = (Team) getIntent().getSerializableExtra("team");
        binding.setTeam(team);
    }

    @Override
    protected void addListener() {
        binding.llTeamBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPhotoUtils = new ProcessPhotoUtils(TeamInformationActivity.this);
                processPhotoUtils.startPhoto();
            }
        });

        binding.llTeamName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText editText = new EditText(TeamInformationActivity.this);
                editText.setMaxLines(1);
                new AlertDialog.Builder(TeamInformationActivity.this)
                        .setTitle("修改团队名称")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Team team = new Team();
                                team.setTeamName(editText.getText().toString());
                                updateTeamInformation(team, UPDATE_TEAM_NAME);
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
        });

        binding.llTeamRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(TeamInformationActivity.this, SelectRegionActivity.class), SELECT_REGION_REQUESTCODE);
            }
        });

        binding.llTeamIndustry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(TeamInformationActivity.this);
                editText.setMaxLines(1);
                new AlertDialog.Builder(TeamInformationActivity.this)
                        .setTitle("修改团队所处行业")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Team team = new Team();
                                team.setTeamIndustry(editText.getText().toString());
                                updateTeamInformation(team, UPDATE_TEAM_INDUSTRY);
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
        });

        binding.llTeamDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(TeamInformationActivity.this);
                new AlertDialog.Builder(TeamInformationActivity.this)
                        .setTitle("修改团队介绍")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Team team = new Team();
                                team.setTeamDesc(editText.getText().toString());
                                updateTeamInformation(team, UPDATE_TEAM_DESC);
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
        });
    }

    private void uploadPicture(String data, String token) {
        String key = null;
        showProgressDialog(this);
        MyApplication.getUploadManager().put(data, key, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        hideProgressDialog();
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if (info.isOK()) {
                            Log.i("qiniu Upload Success");
                            Team team = new Team();
                            try {
                                team.setTeamBadge(res.getString("key"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            updateTeamInformation(team, UPDATE_TEAM_BADGE);
                        } else {
                            Log.i("qiniu Upload Fail");
                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                        }
                        Log.i("qiniu " + key + ",\r\n " + info + ",\r\n " + res);
                        hideProgressDialog();
                    }
                }, null);

    }

    private void updateTeamInformation(Team newTeam, int flag) {
        Map<String, String> params = new HashMap<>();
        switch (flag) {
            case UPDATE_TEAM_BADGE:
                params.put("teamBadge", newTeam.getTeamBadge());
                break;
            case UPDATE_TEAM_NAME:
                params.put("teamName", newTeam.getTeamName());
                break;
            case UPDATE_TEAM_INDUSTRY:
                params.put("teamIndustry", newTeam.getTeamIndustry());
                break;
            case UPDATE_TEAM_REGION:
                params.put("teamRegion", newTeam.getTeamRegion());
                break;
            case UPDATE_TEAM_DESC:
                params.put("teamDesc", newTeam.getTeamDesc());
                break;
        }
        params.put("teamId", team.getTeamId());
        showProgressDialog(this);
        RequestManager.getInstance(this).executeRequest(HttpUrls.UPDATE_TEAM_INFORMATION, params, new AppCallBack<ApiResponse<Team>>() {

            @Override
            public void next(ApiResponse<Team> response) {
                if (response.isSuccess()) {
                    team = response.getData();
                    binding.setTeam(team);
                    isUpdate = true;
                } else {
                    showToast(response.getMessage());
                }
                Log.d("update_my_information" + response.getMessage());
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

    private void uploadPicture(final String data) {
        showProgressDialog(this);
        if (StringUtil.isEmpty(qiniuToken))
            RequestManager.getInstance(this).executeRequest(HttpUrls.GET_QINIUTOKEN, null, new AppCallBack<ApiResponse<String>>() {

                @Override
                public void next(ApiResponse<String> response) {
                    if (response.isSuccess()) {
                        qiniuToken = response.getData();
                        uploadPicture(data, qiniuToken);
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
            uploadPicture(data, qiniuToken);
        }
    }

    @Override
    public void finish() {
        if (isUpdate) {
            Intent intent = new Intent();
            intent.putExtra("team", team);
            setResult(RESULT_OK, intent);
        }
        super.finish();
    }
}
