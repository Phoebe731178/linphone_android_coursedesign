package com.hunter.linphoneandroid.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hunter.fastandroid.R;
import com.hunter.linphoneandroid.base.BaseActivity;
import com.hunter.linphoneandroid.presenter.TestPresenter;
import com.hunter.linphoneandroid.ui.adapter.BookAdapter;
import com.hunter.linphoneandroid.ui.interfaces.ITestView;
import com.hunter.linphoneandroid.ui.widget.TitleBar;
import com.hunter.linphoneandroid.vo.Book;

import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements ITestView {
    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.rv_book)
    RecyclerView rvBook;

    private BookAdapter bookAdapter;

    TestPresenter testPresenter;

    @Override
    protected int getContentResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        titleBar.setTitle("测试页面");

        bookAdapter = new BookAdapter(this);
        rvBook.setLayoutManager(new LinearLayoutManager(this));
        rvBook.setAdapter(bookAdapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = etInput.getText().toString();
                if(TextUtils.isEmpty(keyword)){
                    showToast("请先输入书名关键字");
                }else{
                    searchBook(keyword);
                }
            }
        });
    }

    private void searchBook(String keyword) {
        testPresenter.test(keyword,this);
    }

    @Override
    public void initPresenter() {
        testPresenter = new TestPresenter();
    }

    @Override
    public void showData(List<Book> datas) {
        bookAdapter.setData(datas);
        bookAdapter.notifyDataSetChanged();
    }
}
