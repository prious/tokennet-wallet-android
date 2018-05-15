package io.boscoin.toknenet.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import io.boscoin.toknenet.wallet.adapter.WalletListAdapter;
import io.boscoin.toknenet.wallet.conf.Constants;
import io.boscoin.toknenet.wallet.db.DbOpenHelper;
import io.boscoin.toknenet.wallet.model.Wallet;
import io.boscoin.toknenet.wallet.utils.WalletPreference;


public class WalletListActivity extends AppCompatActivity {

    private Wallet mWallet;
    private RecyclerView rv;
    private DbOpenHelper mDbOpenHelper;
    private List<Wallet> walletList;
    private Cursor mCursor;
    private Context mContext;
    private ImageButton mBtnSetting;
    private WalletListAdapter mAdapter;
    private static final int ORDER_REQUEST_CODE = 1;
    private static final int WALLET_DETAIL_VIEW = 2;

    public interface ClickListener {
        void onSendClicked(int postion);
        void onReceivedClicked(int postion);
        void onItemClicked(int postion);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_wallet_list);

        rv=(RecyclerView)findViewById(R.id.rv_walletlist);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        mBtnSetting = findViewById(R.id.btn_setting);
        mBtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(WalletListActivity.this, SettingActivity.class);

                startActivityForResult(it, ORDER_REQUEST_CODE);
            }
        });

        findViewById(R.id.btn_import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(WalletListActivity.this, ImportActivity.class);
                startActivity(it);
            }
        });

        findViewById(R.id.btn_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(WalletListActivity.this, CreateNoticeActivity.class);
                startActivity(it);
            }
        });
        
        initializeData();
        initializeAdapter();
    }

    private void initializeData() {

       getWalletList();

    }

    private void initializeAdapter(){
        mAdapter = new WalletListAdapter(walletList, new ClickListener() {
            @Override
            public void onSendClicked(int postion) {
                Intent it = new Intent(WalletListActivity.this, SendActivity.class);
                it.putExtra(Constants.Invoke.SEND, walletList.get(postion).getWalletId());
                startActivity(it);
               // startActivityForResult(it, WALLET_DETAIL_VIEW);
            }

            @Override
            public void onReceivedClicked(int postion) {
                // TODO: 2018. 4. 12. will be needs received activity 
                Intent it = new Intent(WalletListActivity.this, ReceiveActivity.class);
                it.putExtra(Constants.Invoke.WALLET, walletList.get(postion).getWalletId());
                startActivity(it);
            }

            @Override
            public void onItemClicked(int postion) {
                Intent it = new Intent(WalletListActivity.this, WalletHistoryActivity.class);
                it.putExtra(Constants.Invoke.HISTORY, walletList.get(postion).getWalletId());
               // startActivity(it);
                startActivityForResult(it, WALLET_DETAIL_VIEW);
            }
        });
        rv.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == ORDER_REQUEST_CODE || requestCode == WALLET_DETAIL_VIEW){

            walletList.clear();
            getWalletList();
            mAdapter.setWalletList(walletList);

        }
    }

    private void getWalletList() {
        walletList = new ArrayList<>();
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open(Constants.DB.MY_WALLETS);
        mCursor = null;


        mCursor = mDbOpenHelper.getColumnWalletByOrder("DESC");

        while (mCursor.moveToNext()){

            mWallet = new Wallet(
                    mCursor.getLong(mCursor.getColumnIndex("_id")),
                    mCursor.getString(mCursor.getColumnIndex(Constants.DB.WALLET_NAME)),
                    mCursor.getString(mCursor.getColumnIndex(Constants.DB.WALLET_ADDRESS)),
                    mCursor.getString(mCursor.getColumnIndex(Constants.DB.WALLET_KET)),
                    mCursor.getInt(mCursor.getColumnIndex(Constants.DB.WALLET_ORDER)),
                    mCursor.getString(mCursor.getColumnIndex(Constants.DB.WALLET_LASTEST)),
                    mCursor.getString(mCursor.getColumnIndex(Constants.DB.WALLET_LAST_TIME))


            );

            walletList.add(mWallet);
        }
        mCursor.close();
        mDbOpenHelper.close();

    }
}
