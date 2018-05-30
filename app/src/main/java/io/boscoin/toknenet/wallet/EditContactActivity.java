package io.boscoin.toknenet.wallet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.boscoin.toknenet.wallet.conf.Constants;
import io.boscoin.toknenet.wallet.db.DbOpenHelper;
import io.boscoin.toknenet.wallet.model.AddressBook;
import io.boscoin.toknenet.wallet.utils.Utils;

public class EditContactActivity extends AppCompatActivity {

    private static final String TAG = "EditContactActivity";
    private EditText editName, editAdress;
    //private long mIdx;
    private AddressBook mBook;
    private TextView mErrName;
    private boolean mInValidName, mExistName, mIsNext;
    private Cursor mCursor;
    private DbOpenHelper mDbOpenHelper;
    private Context mContext;
    private Button mEditOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        mContext = this;

        Intent it = getIntent();
        mBook = (AddressBook) it.getSerializableExtra(Constants.Invoke.ADDRESS_BOOK);

        initUI();
    }

    private void initUI() {
        findViewById(R.id.add_contact).setVisibility(View.GONE);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editName = findViewById(R.id.edit_cname);
        editAdress = findViewById(R.id.address);
        mErrName = findViewById(R.id.err_name);
        mEditOk = findViewById(R.id.btn_edit_ok);

        editName.setText(mBook.getAddressName());
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString();
                Log.e(TAG, "afterTextChanged");
                if(TextUtils.isEmpty(name) || !Utils.isNameValid(name)){
                    mErrName.setText(R.string.error_book_length);
                    mErrName.setVisibility(View.VISIBLE);
                    mInValidName = true;
                }else{
                    mErrName.setVisibility(View.GONE);
                    mDbOpenHelper = new DbOpenHelper(mContext);
                    mDbOpenHelper.open(Constants.DB.ADDRESS_BOOK);
                    mCursor = null;
                    mCursor = mDbOpenHelper.getColumnAddressName();

                    do{

                        if(name.equals(mCursor.getString(mCursor.getColumnIndex(Constants.DB.BOOK_NAME)))){
                            Log.e(TAG,"값이 존재");
                            mErrName.setText(R.string.error_already);
                            mErrName.setVisibility(View.VISIBLE);
                            mDbOpenHelper.close();
                            mExistName = true;
                            changeButton();
                            return;
                        }
                    }while (mCursor.moveToNext());

                    mInValidName = false;
                    mExistName = false;
                    mErrName.setVisibility(View.GONE);

                    mCursor.close();
                    mDbOpenHelper.close();

                    changeButton();
                }
            }


        });

        editAdress.setText(mBook.getAddress());

        mEditOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsNext){
                    addAddressBook();
                }

            }


        });
    }



    private void changeButton() {
        if(!mInValidName && !mExistName ){
            mEditOk.setBackgroundColor(getResources().getColor(R.color.cerulean));
            mIsNext = true;
        }else{
            mEditOk.setBackgroundColor(getResources().getColor(R.color.pinkish_grey));
            mIsNext = false;
        }
    }



    private void addAddressBook() {
        if(!mInValidName && !mExistName){
            mBook.setAddressName(editName.getText().toString());
            mDbOpenHelper = new DbOpenHelper(mContext);
            mDbOpenHelper.open(Constants.DB.ADDRESS_BOOK);
            mDbOpenHelper.updateColumnAddress(mBook.getAddressId(),mBook.getAddressName(),mBook.getAddress());
            mDbOpenHelper.close();
            finish();
        }
    }
}