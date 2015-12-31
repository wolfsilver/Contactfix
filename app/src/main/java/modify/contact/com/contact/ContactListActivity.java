package modify.contact.com.contact;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactListActivity extends FragmentActivity {

    /**
     * 是否带国家码
     */
    protected boolean isWithCountryCode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        Log.i("phoneNumber", "ContactListActivity created...");
    }


    protected ContentResolver cr = null;
    protected HashMap<String, Info> map = new HashMap<>();


    @Override
    protected void onStart() {
        super.onStart();


        Intent intent = getIntent();
        isWithCountryCode = intent.getExtras().getBoolean(MainActivity.isWithCountryCode);
        Log.i("isWithCountryCode", String.valueOf(isWithCountryCode));

        ListView listView = (ListView) findViewById(R.id.listView);

        cr = getContentResolver();
        final Cursor phone = cr.query(Phone.CONTENT_URI, new String[]{
                Phone._ID, Phone.CONTACT_ID, Phone.NUMBER, Phone.DISPLAY_NAME}, null, null, null);
        if (null == phone) {
            Log.d("phoneNumber", "there is no phoneNumber.");
            return;
        }


        Thread getList = new Thread() {
            @Override
            public void run() {
                while (phone.moveToNext()) {
                    String phoneNumber = phone.getString(phone.getColumnIndex(Phone.NUMBER));
                    String name = phone.getString(phone.getColumnIndex(Phone.DISPLAY_NAME));
                    String id = phone.getString(phone.getColumnIndex(Phone.CONTACT_ID));
                    String phoneID = phone.getString(phone.getColumnIndex(Phone._ID));
                    Info info = map.get(id);
                    if (null == info) {
                        info = new Info();
                        ArrayList<String> ids = new ArrayList<>();
                        ArrayList<String> number = new ArrayList<>();

                        ids.add(phoneID);
                        info.setId(ids);

                        info.setName(name);

                        number.add(phoneNumber);
                        info.setOriginNumbers(number);
                        map.put(id, info);
                    } else {
                        info.getOriginNumbers().add(phoneNumber);
                        info.getId().add(phoneID);
                    }
                }

                phone.close();
                handleNumber.start();
            }
        };


        getList.start();


    }

    private void callBack() {
        handleNumber.start();
    }


    /**
     * 处理手机号
     */
    Thread handleNumber = new Thread() {
        @Override
        public void run() {
            Log.i("handle", "handle number thread starts...");
            for (String key : map.keySet()) {
                Info info = map.get(key);

                ArrayList<String> originNumber = info.getOriginNumbers();
                ArrayList<String> newNumer = info.getNewNumbers();

                for (String number : originNumber) {
                    // 空白处理
                    number = number.replaceAll("[\\s-]", "");

                    if (isWithCountryCode) {
                        if (number.matches("^\\+86\\d+") || number.matches("^0\\d+")) {
                            // 区号暂时不添加国家码

                        } else {
                            number = "+86" + number;
                        }
                    } else {
                        // 删除国家码
                        number.replaceAll("^\\+86", "");
                    }

                    newNumer.add(number);
                }
                info.setNewNumbers(newNumer);

                Log.i("List", info.toString());

                int i = 0;

                for (String number : newNumer) {
                    ContentValues values = new ContentValues();
                    values.put(Phone.NUMBER, number);

                    update(cr, info.getId().get(i), values);
                    i++;
                }

            }
            Log.i("handle", "handle number thread end...");
        }
    };


    /**
     * 获取库Phone表字段
     **/
    private static final String[] PHONES_PROJECTION = new String[]{
            Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID};


    private class MyAdapter extends SimpleAdapter {
        int count = 0;
        private List<Map<String, Integer>> mItemList;

        public MyAdapter(Context context, List<? extends Map<String, Integer>> data,
                         int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            mItemList = (List<Map<String, Integer>>) data;
            if (data == null) {
                count = 0;
            } else {
                count = data.size();
            }
        }

        public int getCount() {
            return mItemList.size();
        }

        public Object getItem(int pos) {
            return pos;
        }

        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Map<String, Integer> map = mItemList.get(position);
            View view = super.getView(position, convertView, parent);
            TextView tv = (TextView) view.findViewById(R.id.phoneNumber);
            tv.setText("" + position);
            return view;
        }
    }

    /**
     * 名片
     */
    class Info {

        /**
         * _ID
         */
        ArrayList<String> id = new ArrayList<>();

        /**
         * 姓名
         */
        String name;

        /**
         * 原始号码
         */
        ArrayList<String> originNumbers = new ArrayList<>();

        /**
         * 格式化后号码
         */
        ArrayList<String> newNumbers = new ArrayList<>();

        public ArrayList<String> getId() {
            return id;
        }

        public void setId(ArrayList<String> id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<String> getOriginNumbers() {
            return originNumbers;
        }

        public void setOriginNumbers(ArrayList<String> originNumbers) {
            this.originNumbers = originNumbers;
        }

        public ArrayList<String> getNewNumbers() {
            return newNumbers;
        }

        public void setNewNumbers(ArrayList<String> newNumbers) {
            this.newNumbers = newNumbers;
        }


        public String toString() {
            String s = "";
            int i = 0;
            for (String k : id) {
                s += "[" + k + "]" + "[" + originNumbers.get(i) + "]" + newNumbers.get(i) + "], ";
                i++;
            }
            //[id][originNumber][newNumber]
            return String.format("Name: [%s], phoneNumber: [%s]", name, s);
        }
    }


    /**
     * 更新联系人信息
     *
     * @param cr     ContentResolver
     * @param id     _ID
     * @param values 新号码
     */
    public void update(ContentResolver cr, String id, ContentValues values) {
        String where = Phone._ID + " = " + id;
        int r = cr.update(ContactsContract.Data.CONTENT_URI, values, where, null);
        if (r != 0) {
            Log.i("result", "number: [" + values.get(Phone.NUMBER) + "] updated. ");
        }
    }


}
