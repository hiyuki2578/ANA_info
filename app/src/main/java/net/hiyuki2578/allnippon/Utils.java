package net.hiyuki2578.allnippon;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hiyuki on 2017/02/09.
 */

class Utils {
	static String htmlTagRemover(String str) {
		return str.replaceAll("<.+?>", "");
	}
	static String regex(String str, String regex){
		Pattern pattern = Pattern.compile(regex);	//検索文字列のセット
		Matcher matcher = pattern.matcher(str);		//変換前文字列設定
		return matcher.replaceAll("");				//置き換えて戻す
	}

	static String getDate(String str){
		Date date = new Date();			//日時取得
		SimpleDateFormat format = new SimpleDateFormat(str, Locale.JAPAN);	//フォーマットの設定
		return format.format(date);		//Stringでリターン
	}

	private static String[] getDb(Context context, String ans_name, String query){//, int Name){
		DatabaseOpenHelper helper = new DatabaseOpenHelper(context);
		SQLiteDatabase database = helper.getWritableDatabase();
		Cursor c = database.rawQuery("SELECT " + ans_name + " FROM Airport WHERE Airport.IATA Like '" + query + "'", null);
		//if(Name == 1) {
			//c.close();
			//c = database.rawQuery("SELECT " + ans_name + " FROM Station WHERE Airport.IATA = '" + query + "%'", null);
		//}
		c.moveToFirst();
		String[] list = new String[c.getCount()];
		for(int i = 0; i < list.length ; i++){
			list[i] = c.getString(0);
			c.moveToNext();
		}
		c.close();
		helper.close();
		return list;
	}

	static void hideIME(Context context, View view){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);	//IM取得
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);	//IMを隠す
	}
	static void CompANA(Context context, String str, AutoCompleteTextView Ac){
		if(str.length() != 0) {
			String[] sta = getDb(context, "IATA", str);
			ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.list_item, sta);
			Ac.setAdapter(adapter);
			Ac.showDropDown();
		}
	}
	static String getName(Context context, String str){
		if(str.length() != 0) {
			String[] sta = getDb(context, "NAME", str);
			return sta[0];
		}
		return str;
	}
}
