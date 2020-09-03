package com.hunter.linphoneandroid.ui.interfaces;

import com.hunter.linphoneandroid.base.IBaseView;
import com.hunter.linphoneandroid.vo.Book;

import java.util.List;

public interface ITestView extends IBaseView {
    void showData(List<Book> datas);
}
