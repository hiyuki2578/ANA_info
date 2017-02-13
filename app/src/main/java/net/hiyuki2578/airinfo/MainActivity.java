package net.hiyuki2578.airinfo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static net.hiyuki2578.airinfo.Utils.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, android.app.DatePickerDialog.OnDateSetListener {

	private RequestQueue mRequestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		AutoCompleteTextView Dep = (AutoCompleteTextView)findViewById(R.id.Dep);
		AutoCompleteTextView Arr = (AutoCompleteTextView)findViewById(R.id.Arr);
		Button Search = (Button)findViewById(R.id.button);
		Button Day = (Button)findViewById(R.id.Day);
		Day.setText(getDate("yyyy/MM/dd"));
		Day.setOnClickListener(this);
		Search.setOnClickListener(this);
		Dep.setHint("HND");
		Arr.setHint("ITM");
		Dep.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				CompANA(MainActivity.this, s.toString() ,(AutoCompleteTextView)findViewById(R.id.Dep));
			}

			@Override
			public void afterTextChanged(Editable s) {
				CompANA(MainActivity.this, s.toString() ,(AutoCompleteTextView)findViewById(R.id.Dep));
			}
		});
		Arr.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				CompANA(MainActivity.this, s.toString() ,(AutoCompleteTextView)findViewById(R.id.Arr));
			}

			@Override
			public void afterTextChanged(Editable s) {
				CompANA(MainActivity.this, s.toString() ,(AutoCompleteTextView)findViewById(R.id.Arr));
			}
		});
	}
	public void onClick(View view) {
		hideIME(this, view);
		switch (view.getId()) {
			case R.id.button:
				search();
				break;
			case R.id.Day:
				showDatePickerDialog(view);
				break;
		}
	}
	public void search(){
		final AutoCompleteTextView Dep = (AutoCompleteTextView)findViewById(R.id.Dep);
		final AutoCompleteTextView Arr = (AutoCompleteTextView)findViewById(R.id.Arr);
		final TextView result = (TextView)findViewById(R.id.result);
		RadioButton SFJ = (RadioButton)findViewById(R.id.SFJ);
		Button Day = (Button)findViewById(R.id.Day);
		String uri = getUri.getANA(regex(Day.getText().toString(), "/"), Dep.getText().toString().toUpperCase(), Arr.getText().toString().toUpperCase());
		if(SFJ.isChecked()){
			uri = getUri.getSFJ(regex(Day.getText().toString(), "/"), Dep.getText().toString().toUpperCase(), Arr.getText().toString().toUpperCase());
		}
		mRequestQueue.add(new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response){
				try{
					String text = "出発地 " + getName(MainActivity.this, Dep.getText().toString()) + " 目的地 " +getName(MainActivity.this, Arr.getText().toString()) + "\n";
					JSONObject Data = response.getJSONObject("data");
					JSONArray fsList = Data.getJSONArray("fsList");
					for(int i = 0;i < fsList.length();i++){
						JSONObject fs = fsList.getJSONObject(i);
						text += fs.getString("fltNo") +"便 搭乗口" + fs.getString("bordingGate") + " " + fs.getString("actDepTime") + fs.getString("depCondition") + " " + fs.getString("actArrTime") + fs.getString("arrCondition") + " 機種" + fs.getString("airplaneLink") +"\n";
					}
					result.setText(htmlTagRemover(text));
				}catch (JSONException e){
					e.printStackTrace();
					result.setText("就航してません。");
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("JsonSample",error.toString());
			}
		}));
	}
	@Override
	public void onDateSet(DatePicker view, int year , int month, int day){	//日時指定でOKを押したときに呼ばれる
		Button Day = (Button)findViewById(R.id.Day);
		Day.setText(String.valueOf(year) + "/" + String.format(Locale.JAPAN,"%1$02d",month + 1) + "/" + String.format(Locale.JAPAN,"%1$02d",day));	// 2016/10/12 形式で返す "%1$02d"で2桁で返す
	}

	public void showDatePickerDialog(View view){
		DatePickerDialog datePickerDialog = new DatePickerDialog();
		datePickerDialog.show(getFragmentManager(), "DatePicker");	//理解して
	}
}