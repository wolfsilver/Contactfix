package modify.contact.com.contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        Log.i("phoneNumber", "ContactListActivity created...");

    }


    @Override
    protected void onStart() {
        super.onStart();

        ListView listView = (ListView) findViewById(R.id.listView);

        ContentResolver cr = getContentResolver();
        HashMap<String, Info> map = new HashMap<>();
        Cursor phone = cr.query(Phone.CONTENT_URI, new String[]{
                Phone._ID, Phone.CONTACT_ID, Phone.NUMBER, Phone.DISPLAY_NAME}, null, null, null);
        if (null == phone) {
            Log.d("phoneNumber", "there is no phoneNumber.");
            return;
        }
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

//        Iterator<Map.Entry<String, Info>> it = map.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<String, Info> entry = it.next();
//            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
//        }

        for (String key : map.keySet()) {
            Info info = map.get(key);

            ArrayList<String> originNumber = info.getOriginNumbers();
            ArrayList<String> newNumer = info.getNewNumbers();

            for (String number : originNumber){
                newNumer.add(number.replaceAll("[\\s-]", ""));
            }
            info.setNewNumbers(newNumer);

            Log.i("List", info.toString());



        }


    }


    /**
     * 获取库Phone表字段
     **/
    private static final String[] PHONES_PROJECTION = new String[]{
            Phone.DISPLAY_NAME, Phone.NUMBER, Phone.CONTACT_ID};

    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 联系人的ID
     **/
    private static final int PHONES_CONTACT_ID_INDEX = 2;


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

        ArrayList<String> id = new ArrayList<>();
        String name;
        ArrayList<String> originNumbers = new ArrayList<>();
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
                try {
                    s += "[" + k + "]" + "[" + originNumbers.get(i) +  "]" + newNumbers.get(i) +  "], ";
                }catch (Exception e){
                    continue;
                }
            }
            //[id][originNumber][newNumber]
            return String.format("Name: [%s], phoneNumber: [%s]", name, s);
        }
    }




}
