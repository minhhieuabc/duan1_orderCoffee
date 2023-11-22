package com.example.da1_odercoffee.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.da1_odercoffee.AddBan_Activity;
import com.example.da1_odercoffee.Dao.BanDao;
import com.example.da1_odercoffee.Home_Activity;
import com.example.da1_odercoffee.R;
import com.example.da1_odercoffee.adapter.BanAdapter;
import com.example.da1_odercoffee.model.Ban;

import java.util.List;

public class BanFragment extends Fragment {
    GridView gridView;
    List<Ban> banlist;
    BanDao banDao;
    BanAdapter banAdapter;
    //Dùng activity result (activityforresult ko hổ trợ nữa) để nhận data gửi từ activity addtable
    ActivityResultLauncher<Intent> resultLauncherAdd = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        boolean ktra = intent.getBooleanExtra("ketquathem", false);
                        if (ktra) {
                            HienThiDSBan();
                            Toast.makeText(getActivity(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> resultLauncherEdit = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        boolean ktra = intent.getBooleanExtra("ketquasua", false);
                        if (ktra) {
                            HienThiDSBan();
                            Toast.makeText(getActivity(), getResources().getString(R.string.edit_sucessful), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.edit_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmentban, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        ((Home_Activity)getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Quản lý bàn</font>"));
        gridView = view.findViewById(R.id.gv_fragmnet_table);

        banDao = new BanDao(getContext());
        HienThiDSBan();

        registerForContextMenu(gridView);

    }
    //tạo ra context menu khi longclick
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.edit_context_menu,menu);
    }

    //Xử lí cho từng trường hợp trong contextmenu
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int vitri = menuInfo.position;
        int maban = banlist.get(vitri).getMaBan();
        if(id==R.id.itEdit) {

            Intent intent = new Intent(getActivity(), AddBan_Activity.class);
            intent.putExtra("maban", maban);
            resultLauncherEdit.launch(intent);
        } else if (id==R.id.itDelete) {

            boolean ktraxoa = banDao.XoaBanTheoMa(maban);
            if(ktraxoa){
                HienThiDSBan();
                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.delete_sucessful),Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.delete_failed),Toast.LENGTH_SHORT).show();
            }
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem itAddTable = menu.add(1,R.id.itAddTable,1,R.string.addTable);
        itAddTable.setIcon(R.drawable.baseline_add_24);
        itAddTable.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id==R.id.itAddTable){

            Intent iAddTable = new Intent(getActivity(),AddBan_Activity.class);
            resultLauncherAdd.launch(iAddTable);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        banAdapter.notifyDataSetChanged();
    }
    private void HienThiDSBan() {

        banlist = banDao.LayTatCaBan();
        banAdapter = new BanAdapter(getActivity(), banlist);
        gridView.setAdapter(banAdapter);
        banAdapter.notifyDataSetChanged();
    }

}
