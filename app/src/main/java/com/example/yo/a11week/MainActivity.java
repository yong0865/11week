package com.example.yo.a11week;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatePicker date;
    EditText memo;
    LinearLayout linear1, linear2;
    ListView listview;
    ArrayList<String> arraylist = new ArrayList<>();
    ArrayAdapter adapter;
    TextView tvCount;
    Button btn1, btnsave, btncancel;
    int num = 0;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView) findViewById(R.id.listview);
        memo = (EditText) findViewById(R.id.memo);
        linear1 = (LinearLayout) findViewById(R.id.linear1);
        linear2 = (LinearLayout) findViewById(R.id.linear2);
        date = (DatePicker) findViewById(R.id.date);
        tvCount = (TextView) findViewById(R.id.tvCount);
        btn1 = (Button) findViewById(R.id.btn1);
        btnsave = (Button) findViewById(R.id.btnsave);
        btncancel = (Button) findViewById(R.id.btncancel);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arraylist);

        setPermission();
        makedirectory();
        Filelist();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    btnsave.setText("수정");
                    String path = getExternalPath();
                    BufferedReader br = new BufferedReader(new FileReader(path + "diary/" + arraylist.get(position)));
                    pos = position;
                    String readStr = "";
                    String str = null;
                    while ((str = br.readLine()) != null)
                        readStr += str + "\n";
                    br.close();

                    memo.setText(readStr.substring(0, readStr.length() - 1));
                    linear1.setVisibility(View.INVISIBLE);
                    linear2.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "File not found", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                pos = position;
                dlg.setTitle("해당 메모를 삭제하겠습니까?")
                        .setMessage(position+1 + "번째 메모를 삭제 하시겠습니까?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String path = getExternalPath();
                                File file = new File(path + "diary/" + arraylist.get(position));
                                file.delete();
                                arraylist.remove(position);
                                adapter.notifyDataSetChanged();
                                num--;
                                tvCount.setText("등록된 메모 개수: " + Integer.toString(num));
                            }
                        })
                        .setNegativeButton("NO", null)
                        .show();
                return true;
            }
        });
    }

    private void makedirectory() {
        String path1 = getExternalPath();
        File file = new File(path1 + "diary");
        if (!file.isDirectory())
            file.mkdir();
    }

    private void Filelist() {
        String path2 = getExternalPath();
        File[] files = new File(path2 + "diary").listFiles();
        String str = "";
        for (File f : files) {
            arraylist.add(f.getName());
            num++;
        }
        listview.setAdapter(adapter);
        tvCount.setText("등록된 메모 개수: " + Integer.toString(num));
    }

    private void getdate() {
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDayOfMonth();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");

        String str = simpleDateFormat.format(new Date(year, month, day));
        arraylist.add(str + ".memo");
    }

    private void setPermission() {
        int permissionInfo = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionInfo == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "SDCard 쓰기 권한 있음", Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(getApplicationContext(), "권한의 필요성 설명", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String str = null;

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                str = "SD Card 쓰기권한 승인";
            else str = "SD Card 쓰기권한 거부";
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
        }
    }

    public String getExternalPath() {
        String sdPath = "";
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        } else
            sdPath = getFilesDir() + "";
        Toast.makeText(getApplicationContext(), sdPath, Toast.LENGTH_SHORT).show();
        return sdPath;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnsave) {
            try {
                if (btnsave.getText().toString() == "수정") {
                    String path = getExternalPath();
                    File file = new File(path + "diary/" + arraylist.get(pos));
                    file.delete();
                    arraylist.remove(pos);

                    adapter.notifyDataSetChanged();
                    num--;

                    linear2.setVisibility(View.INVISIBLE);
                    linear1.setVisibility(View.VISIBLE);
                }
                getdate();
                String path = getExternalPath();
                BufferedWriter bw = new BufferedWriter(new FileWriter(path + "diary/" + arraylist.get(num), true));
                num++;
                bw.write(memo.getText().toString());
                bw.close();
                Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();

                tvCount.setText("등록된 메모 개수: " + Integer.toString(num));
                btnsave.setText("저장");
                memo.setText("");
                Collections.sort(arraylist, checkdate);
                adapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage() + ":" + getFilesDir(), Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId() == R.id.btncancel) {
            Collections.sort(arraylist, checkdate);
            memo.setText("");
            adapter.notifyDataSetChanged();
            linear1.setVisibility(View.VISIBLE);
            linear2.setVisibility(View.INVISIBLE);
        }
        if (v.getId() == R.id.btn1) {
            btnsave.setText("저장");
            linear1.setVisibility(View.INVISIBLE);
            linear2.setVisibility(View.VISIBLE);
        }
    }


    Comparator<String> checkdate = new Comparator<String>() {

        @Override
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    };
}
