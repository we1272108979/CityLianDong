package widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.testcityliandong.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 单城市选择类
 * 
 * @author Dong
 * 
 */
public class CitySelect1Activity extends Activity implements OnClickListener {

	private Button btn_back, btn_right, btn_submit;
	private ListView lv_city;
	private ArrayList<MyRegion> regions;

	private CityAdapter adapter;
	private static int PROVINCE = 0x00;
	private static int CITY = 0x01;
	private static int DISTRICT = 0x02;
	private CityUtils util;

	private Button[] tvs = new Button[3];
	private int[] ids = { R.id.rb_province, R.id.rb_city, R.id.rb_district };

	private City city;
	int last, current;
	private TextView Text;
	private Map<String, String> map = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_city2);
		viewInit();

	}

	private void viewInit() {

		city = new City();
		Intent in = getIntent();
		Text = (TextView) findViewById(R.id.city_edittext);
		/**
		 * 动态找空间 设置点击时间
		 */
		for (int i = 0; i < tvs.length; i++) {
			tvs[i] = (Button) findViewById(ids[i]);
			tvs[i].setOnClickListener(this);
		}

		if (city == null) {
			city = new City();
			city.setProvince("");
			city.setCity("");
			city.setDistrict("");
		} else {
			if (city.getProvince() != null && !city.getProvince().equals("")) {
				map.put("province", city.getProvince());
				changeButtonStatus(0);
			}
			if (city.getCity() != null && !city.getCity().equals("")) {
				map.put("city", city.getCity());
				changeButtonStatus(1);
			}
			if (city.getDistrict() != null && !city.getDistrict().equals("")) {
				map.put("district", city.getDistrict());
				changeButtonStatus(2);
			}
		}

		btn_back = (Button) findViewById(R.id.btn_back);
		btn_right = (Button) findViewById(R.id.btn_right);
		/**
		 * 初始化城市列表类
		 * 首先初始化省份类util.initProvince();
		 */
		util = new CityUtils(this, hand);
		util.initProvince();
		lv_city = (ListView) findViewById(R.id.lv_city);

		regions = new ArrayList<MyRegion>();
		adapter = new CityAdapter(this);
		lv_city.setAdapter(adapter);
		changeButtonStatus(0);

	}

	protected void onStart() {
		super.onStart();
		lv_city.setOnItemClickListener(onItemClickListener);
		btn_back.setOnClickListener(this);
		btn_right.setOnClickListener(this);
	};

	public void stringText() {
		String str = map.get("province") + map.get("city")
				+ map.get("district");
		Text.setText(str);
	}

	@SuppressLint("HandlerLeak")
	Handler hand = new Handler() {
		@SuppressLint("NewApi")
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {

			case 1:
				System.out.println("省份列表what======" + msg.what);

				regions = (ArrayList<MyRegion>) msg.obj;
				adapter.clear();
				adapter.addAll(regions);
				adapter.update();
				changeButtonStatus(0);
				break;

			case 2:
				System.out.println("城市列表what======" + msg.what);
				regions = (ArrayList<MyRegion>) msg.obj;
				adapter.clear();
				adapter.addAll(regions);
				adapter.update();
				changeButtonStatus(1);
				break;

			case 3:
				System.out.println("区/县列表what======" + msg.what);
				regions = (ArrayList<MyRegion>) msg.obj;
				adapter.clear();
				adapter.addAll(regions);
				adapter.update();
				changeButtonStatus(2);
				break;
			default:
break;		}
		};
	};

	OnItemClickListener onItemClickListener = new OnItemClickListener() {
//View view, int position, long id)
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			if (current == PROVINCE) {
				String newProvince = regions.get(arg2).getName();
				if (!newProvince.equals(city.getProvince())) {
					city.setProvince(newProvince);
					map.put("province", regions.get(arg2).getName());
					city.setRegionId(regions.get(arg2).getId());
					city.setProvinceCode(regions.get(arg2).getId());
					city.setCityCode("");
					city.setDistrictCode("");
					map.put("city", "");
					map.put("district", "");
					stringText();
				}
				current = 1;
				// 点击省份列表中的省份就初始化城市列表
				util.initCities(city.getProvinceCode());
			} else if (current == CITY) {
				String newCity = regions.get(arg2).getName();
				if (!newCity.equals(city.getCity())) {
					city.setCity(newCity);
					map.put("city", city.getCity());
					city.setRegionId(regions.get(arg2).getId());
					city.setCityCode(regions.get(arg2).getId());
					city.setDistrictCode("");
					map.put("district", "");
					stringText();
				}
				// 点击城市列表中的城市就初始化区县列表
				util.initDistricts(city.getCityCode());
				current = 2;

			} else if (current == DISTRICT) {

				current = 2;
				city.setDistrictCode(regions.get(arg2).getId());
				city.setRegionId(regions.get(arg2).getId());
				city.setDistrict(regions.get(arg2).getName());
				map.put("district", city.getDistrict());
				stringText();
			}
			last = current;
		}
	};

	//

	class CityAdapter extends ArrayAdapter<MyRegion> {

		LayoutInflater inflater;

		public CityAdapter(Context con) {
			super(con, 0);
			inflater = LayoutInflater.from(CitySelect1Activity.this);
		}

		@Override
		public View getView(int arg0, View v, ViewGroup arg2) {
			v = inflater.inflate(R.layout.city_item, null);
			TextView tv_city = (TextView) v.findViewById(R.id.tv_city);
			tv_city.setText(getItem(arg0).getName());
			return v;
		}

		public void update() {
			this.notifyDataSetChanged();
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:// 返回按钮监听
			finish();
			break;
		case R.id.btn_right:// 确定按钮监听

			Intent in = new Intent();
			if (city.getProvince() == null || city.getProvince().isEmpty()) {
				Toast.makeText(CitySelect1Activity.this, "您还没有选择省份",
						Toast.LENGTH_SHORT).show();
				return;
			} else if (city.getCity() == null || city.getProvince().isEmpty()) {
				Toast.makeText(CitySelect1Activity.this, "您还没有选择城市",
						Toast.LENGTH_SHORT).show();
				return;
			}
			in.putExtra("city", city);
			setResult(8, in);
			finish();
			break;
		default:

		}
		if (ids[0] == v.getId()) {
			current = 0;
			util.initProvince();
			last = current;
			changeButtonStatus(0);
		} else if (ids[1] == v.getId()) {
			if (city.getProvinceCode() == null
					|| city.getProvinceCode().equals("")) {
				current = 0;
				Toast.makeText(CitySelect1Activity.this, "您还没有选择省份",
						Toast.LENGTH_SHORT).show();
				return;
			}
			util.initCities(city.getProvinceCode());
			current = 1;
			last = current;
			changeButtonStatus(1);
			stringText();
		} else if (ids[2] == v.getId()) {
			if (city.getProvinceCode() == null
					|| city.getProvinceCode().equals("")) {
				Toast.makeText(CitySelect1Activity.this, "您还没有选择省份",
						Toast.LENGTH_SHORT).show();
				current = 0;
				util.initProvince();
				return;
			} else if (city.getCityCode() == null
					|| city.getCityCode().equals("")) {
				Toast.makeText(CitySelect1Activity.this, "您还没有选择城市",
						Toast.LENGTH_SHORT).show();
				current = 1;
				util.initCities(city.getProvince());
				return;
			}
			current = 2;
			util.initDistricts(city.getCityCode());
			last = current;
			changeButtonStatus(2);
			stringText();
		}

	}

	/**
	 * 根据当前位置改变按钮选中状态
	 * 
	 * @version 1.0
	 * @author zyh
	 * @param position
	 */
	private void changeButtonStatus(int position) {
		tvs[0].setSelected(position == 0);
		tvs[1].setSelected(position == 1);
		tvs[2].setSelected(position == 2);
	}

}
