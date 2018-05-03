package cn.chestnut.mvvm.teamworker.module.mine;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityAboutTeamworkerBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;

public class AboutTeamWorkerActivity extends BaseActivity {

    private ActivityAboutTeamworkerBinding binding;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("关于TeamWorker");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_about_teamworker, viewGroup, true);
    }
}
