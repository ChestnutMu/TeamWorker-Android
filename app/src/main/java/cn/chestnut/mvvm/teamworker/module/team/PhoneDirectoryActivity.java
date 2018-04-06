package cn.chestnut.mvvm.teamworker.module.team;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.chestnut.mvvm.teamworker.BR;
import cn.chestnut.mvvm.teamworker.R;
import cn.chestnut.mvvm.teamworker.databinding.ActivityPhoneDirectoryBinding;
import cn.chestnut.mvvm.teamworker.main.common.BaseActivity;
import cn.chestnut.mvvm.teamworker.model.PhoneDirectoryPerson;
import cn.chestnut.mvvm.teamworker.utils.Log;
import cn.chestnut.mvvm.teamworker.widget.WordsIndexBar;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/4/4 21:59:49
 * Description：从手机通信录添加团队成员
 * Email: xiaoting233zhang@126.com
 */

public class PhoneDirectoryActivity extends BaseActivity {

    private ActivityPhoneDirectoryBinding binding;

    private List<PhoneDirectoryPerson> persons;

    @Override
    protected void setBaseTitle(TextView titleView) {
        titleView.setText("选择团队成员");
    }

    @Override
    protected void addContainerView(ViewGroup viewGroup, LayoutInflater inflater) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_phone_directory, viewGroup, true);
        initData();
        initView();
        addListener();
    }

    protected void initData() {
        getContacts();
        Collections.sort(persons, new Comparator<PhoneDirectoryPerson>() {
            @Override
            public int compare(PhoneDirectoryPerson lhs, PhoneDirectoryPerson rhs) {
                //根据拼音进行排序
                return lhs.getPinyin().compareTo(rhs.getPinyin());
            }
        });
    }

    protected void initView() {
        PhoneDirectoryAdapter adapter = new PhoneDirectoryAdapter(R.layout.item_phone_directory, BR.person, persons);
        binding.lvPhoneDirectory.setAdapter(adapter);
    }

    protected void addListener() {
        binding.lvPhoneDirectory.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                binding.wordIndexBar.setTouchIndex(persons.get(firstVisibleItem).getWordHeader());
            }
        });

        binding.wordIndexBar.setOnWordsChangeListener(new WordsIndexBar.OnWordChangeListener() {
            @Override
            public void onWordChange(String words) {
                updateListView(words);
            }
        });

        binding.lvPhoneDirectory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("person", persons.get(position));
                PhoneDirectoryActivity.this.setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * 获取联系人姓名及号码
     */
    private void getContacts() {
        //联系人的Uri，也就是content://com.android.contacts/contacts
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //指定获取_id和display_name两列数据，display_name即为姓名
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };
        //根据Uri查询相应的ContentProvider，cursor为获取到的数据集
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        persons = new ArrayList<>(cursor.getCount());
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Long id = cursor.getLong(0);
                //获取姓名
                String name = cursor.getString(1);
                //指定获取NUMBER这一列数据
                String[] phoneProjection = new String[]{
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };

                //根据联系人的ID获取此人的电话号码
                Cursor phonesCusor = this.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        phoneProjection,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                        null,
                        null);

                //因为每个联系人可能有多个电话号码，所以需要遍历
                if (phonesCusor != null && phonesCusor.moveToFirst()) {
                    do {
                        String phone = phonesCusor.getString(0);
                        Log.d("name:" + name + "   phone:" + phone);
                        PhoneDirectoryPerson phoneDirectoryPerson = new PhoneDirectoryPerson(name, phone);
                        persons.add(phoneDirectoryPerson);
                    } while (phonesCusor.moveToNext());
                }
            } while (cursor.moveToNext());
        }
    }

    /**
     * @param words 首字母
     */
    private void updateListView(String words) {
        for (int i = 0; i < persons.size(); i++) {
            String headerWord = persons.get(i).getWordHeader();
            //将手指按下的字母与列表中相同字母开头的项找出来
            if (words.equals(headerWord)) {
                //将列表选中哪一个
                binding.lvPhoneDirectory.setSelection(i);
                //找到开头的一个即可
                return;
            }
        }
    }

}
