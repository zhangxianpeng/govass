package com.xianpeng.govass.activity.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xianpeng.govass.R;
import com.xianpeng.govass.activity.detailinfo.DetailInfoActivity;
import com.xianpeng.govass.fragment.policy.PolicyItem;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.xianpeng.govass.Constants.UPLOAD_MULTY_FILE;

public class JavaActivity extends AppCompatActivity {

    private List<PolicyItem> data = new ArrayList<>();
    private BaseQuickAdapter<PolicyItem, BaseViewHolder> baseQuickAdapter;

    private RecyclerView recyclerView;
    private TitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java);
        recyclerView = findViewById(R.id.rv_java);
        titleBar = findViewById(R.id.titlebar);
        titleBar.addAction(new TitleBar.Action() {
            @Override
            public String getText() {
                return null;
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_baseline_send_24;
            }

            @Override
            public void performAction(View view) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures", "数字1.png");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            post(UPLOAD_MULTY_FILE,file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public int leftPadding() {
                return 0;
            }

            @Override
            public int rightPadding() {
                return 0;
            }
        });
        initAdapter();
        initData();
    }

    private void initAdapter() {
        baseQuickAdapter = new BaseQuickAdapter<PolicyItem, BaseViewHolder>(R.layout.adapter_policy_item, data) {
            @Override
            protected void convert(@NotNull BaseViewHolder baseViewHolder, PolicyItem policyItem) {
                baseViewHolder.setText(R.id.tv_policy_title, policyItem.getTitle());
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(baseQuickAdapter);
        baseQuickAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                startActivity(new Intent(JavaActivity.this, DetailInfoActivity.class).putExtra("policyId", data.get(position).getId()));
            }
        });
    }

    private void initData() {
        for (int i = 0; i < 10; i++) {
            PolicyItem policyItem = new PolicyItem();
            policyItem.setTitle("aaa" + i);
            data.add(policyItem);
        }
        if (baseQuickAdapter != null) baseQuickAdapter.notifyDataSetChanged();
    }

    public static String post(String actionUrl, File file) throws IOException {
        String result = null;
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";

        URL uri = new URL(actionUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(5 * 1000); // 缓存的最长时间
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false); // 不允许使用缓存
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        // 发送文件数据
        if (file != null) {
            StringBuilder sb1 = new StringBuilder();
            sb1.append(PREFIX);
            sb1.append(BOUNDARY);
            sb1.append(LINEND);

            sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
            sb1.append(LINEND);
            outStream.write(sb1.toString().getBytes());
            InputStream is = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }

            is.close();
            outStream.write(LINEND.getBytes());
        }

        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();
        // 得到响应码
        int res = conn.getResponseCode();
        System.out.println("response----->>" + conn.getResponseMessage());
        InputStream in = conn.getInputStream();

        outStream.close();
        conn.disconnect();
        return result;
    }
}