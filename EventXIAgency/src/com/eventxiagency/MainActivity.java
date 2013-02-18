package com.eventxiagency;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;

import com.eventxiagency.utils.HttpRequest;
import com.eventxiagency.utils.MyTools;
import com.eventxiagency.utils.Singleton;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnMenuItemClickListener{
	
	private String mEquipName;
	private String mCode;
	
	private EditText mEquipNameView;
	private EditText mCodeView;
	private View mJoinFormView;
	private View mJoinStatusView;
	private TextView mJoinStatusMessageView;
	
	private String JSONString;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mEquipName = "Equipe Dadidou";
		mEquipNameView = (EditText) findViewById(R.id.main_equipName);
		mEquipNameView.setText(mEquipName);
		
		mCodeView = (EditText) findViewById(R.id.main_code);
		mCodeView
			.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
					if(id == R.id.main_equipName || id == EditorInfo.IME_NULL) {
						attemptJoin();
						return true;
					}
					return false;
				}
			});
		
		mJoinFormView = findViewById(R.id.main_form);
		mJoinStatusView = findViewById(R.id.main_status);
		mJoinStatusMessageView = (TextView) findViewById(R.id.main_status_message);
		
		findViewById(R.id.main_joinButton).setOnClickListener(
				new View.OnClickListener() {					
					@Override
					public void onClick(View v) {
						attemptJoin();						
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void launchEnquete()
	{
		Intent intent = new Intent(this, EnqueteActivity.class);
		startActivity(intent);
	}
	
	public void attemptJoin() {
		mEquipNameView.setError(null);
		mCodeView.setError(null);
		
		mEquipName = mEquipNameView.getText().toString();
		mCode = mCodeView.getText().toString();
		
		boolean cancel = false;
		View focusView = null;
		
		if(TextUtils.isEmpty(mCode)) {
			mCodeView.setError(getString(R.string.main_error_field_required));
			focusView = mCodeView;
			cancel = true;
		}
		else if (mCode.length() > 8) {
			mCodeView.setError(getString(R.string.main_error_invalid_code));
			focusView = mCodeView;
			cancel = true;
		}
		
		if(TextUtils.isEmpty(mEquipName)) {
			mEquipNameView.setError(getString(R.string.main_error_field_required));
			focusView = mEquipNameView;
			cancel = true;
		}
		
		if(cancel)
		{
			focusView.requestFocus();
		}
		else
		{
			Singleton sglt = Singleton.getInstance();
			mJoinStatusMessageView.setText(R.string.main_progress_joinning);
			new UserLoginTask().execute("http://"+sglt._url+":"+sglt._port+"/join/");
			showProgress(true);
		}
	}
	
	private void showProgress(final boolean show)
	{
		mJoinStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		mJoinFormView.setVisibility(show ? View.GONE : View.VISIBLE);
	}
	
	public class UserLoginTask extends AsyncTask<String, Void, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			List<String[]> datas = new ArrayList<String[]>();
			datas.add(new String[] {"nom_groupe", mEquipName});
			datas.add(new String[] {"partie_code", mCode});
			HttpRequest req = new HttpRequest(params[0], datas);
			
			if(req.responseString.startsWith("["))
			{
				JSONString = req.responseString;
				JSONArray obj = (JSONArray) MyTools.JSONDecode(JSONString);
				int return_code = Integer.parseInt(obj.get(0).toString());
				return return_code;
			}
			else
				return 0;
		}

		@Override
		protected void onPostExecute(final Integer code) {
			showProgress(false);
			
			switch(code)
			{
				case 0:
					mEquipNameView.setError("Une erreur inconnue est survenue, contactez le développeur de l'application");
					mEquipNameView.requestFocus();
					break;
				case 1:
					mEquipNameView.setError(getString(R.string.main_error_exist_equipName));
					mEquipNameView.requestFocus();
					break;
				case 2:
					mCodeView.setError(getString(R.string.main_error_incorrect_code));
					mCodeView.requestFocus();
					break;
				case 12:
					JSONArray obj = (JSONArray) MyTools.JSONDecode(JSONString);					
					Singleton sglt = Singleton.getInstance();
					
					sglt.partie_code = obj.get(1).toString();
					sglt.nom_groupe = obj.get(2).toString();
					
					JSONArray enqueteJson = (JSONArray) obj.get(3);
					sglt.enquete = new String[] {enqueteJson.get(0).toString(), enqueteJson.get(1).toString()};
					
					JSONArray equipeJson = (JSONArray) obj.get(4);
					sglt.equipes = new String[equipeJson.size()];
					for(int i = 0; i < equipeJson.size(); i++)
					{
						sglt.equipes[i] = equipeJson.get(i).toString();
					}
					
					JSONArray coorJson = (JSONArray) obj.get(5);
					sglt.coordinateur = new String[] {coorJson.get(0).toString(), coorJson.get(1).toString()};
					
					JSONArray aniJson = (JSONArray)((JSONArray) obj.get(6));
					sglt.animateurs = new String[aniJson.size()][2];
					for(int i = 0; i < aniJson.size(); i++)
					{
						JSONArray a = (JSONArray) aniJson.get(i);
						sglt.animateurs[i] = new String[] {a.get(0).toString(), a.get(1).toString()};
					}
					
					launchEnquete();
					break;
				default:
					mEquipNameView.setError("Une erreur inconnue est survenue, contactez le développeur de l'application");
					mEquipNameView.requestFocus();
					break;	
			}
		}

		@Override
		protected void onCancelled() {
			showProgress(false);
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		case R.id.menu_quitter:
			this.finish();
			break;
		case R.id.a_propos:
			break;
		}
		return false;
	}
}
