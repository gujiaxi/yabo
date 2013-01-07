package com.github.yabo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.yabo.util.CommonUtils;
import com.github.yabo.util.DialogUtils;
import com.github.yabo.util.FileUtils;
import com.github.yabo.util.MediaUtils;
import com.github.yabo.util.NetworkUtils;
import com.github.yabo.util.StringUtils;
import com.github.yabo.util.WeiboUtils;
import com.github.yabo.util.DialogUtils.DialogCallBack;
import com.weibo.net.AccessToken;
import com.weibo.net.DialogError;
import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;

public class ShareMainActivity extends Activity {

	private static final int REQUEST_CODE_ADDIMAGE = 0;
	
	private static final int TOOLBAR0 = 0;
	private static final int TOOLBAR1 = 1;
	private static final int TOOLBAR2 = 2;
	
	private Activity mInstance = null;
	private Context mContext = null;
	
	private SharedPreferences data = null;
	
	private String weiboImgPath = null;

	private ProgressDialog dialog = null;
	private Button shareBtn = null;
	private ImageButton imgChooseBtn = null;
	private ImageView imageView = null;
	private ImageView imageDeleteView = null;
	private EditText weiboContentText = null;
	private TextView wordCounterView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mInstance = this;
		mContext = this.getApplicationContext();
		data = getSharedPreferences(CommonUtils.WEIBO, 0);
		
		initView();
	}

	private void initView(){
		dialog = new ProgressDialog(this);
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);

		wordCounterView = (TextView) findViewById(R.id.share_word_counter);
		weiboContentText = (EditText) findViewById(R.id.share_content);
		imageView = (ImageView) findViewById(R.id.share_image);
		imageDeleteView = (ImageView) findViewById(R.id.share_image_delete);
		shareBtn = (Button) findViewById(R.id.share_submit);
		imgChooseBtn = (ImageButton) findViewById(R.id.share_imagechoose);
		shareBtn.setOnClickListener(listener);
		imgChooseBtn.setOnClickListener(listener);
		imageDeleteView.setOnClickListener(listener);
		
		// Listen on the change of the character #
		TextWatcher watcher = new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				textCountSet();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				textCountSet();
			}

			@Override
			public void afterTextChanged(Editable s) {
				textCountSet();
			}
		};

		weiboContentText.addTextChangedListener(watcher);
	}

	private void textCountSet() {
		String textContent = weiboContentText.getText().toString();
		int currentLength = textContent.length();
		if (currentLength <= 140) {
			wordCounterView.setTextColor(Color.BLACK);
			wordCounterView.setText(String.valueOf(textContent.length()));
		} else {
			wordCounterView.setTextColor(Color.RED);
			wordCounterView.setText(String.valueOf(140 - currentLength));
		}
	}

	OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.share_submit:
				if (NetworkUtils.getNetworkState(mContext) != NetworkUtils.NONE) {
					if (isChecked()) {
						String localToken = data.getString("localToken", "");
						long localExpiresIn = data.getLong("localExpiresIn", 0);

						boolean isSessionValid = WeiboUtils.isSessionValid(localToken, localExpiresIn);
						
						//whether local Token has expired
						if (isSessionValid) {
							Utility.setAuthorization(new Oauth2AccessTokenHeader());
							AccessToken accessToken = new AccessToken(localToken, Weibo.getAppSecret());
							accessToken.setExpiresIn(localExpiresIn);
							
							Weibo.getInstance().setAccessToken(accessToken);

							dialog.setMessage("Sharing...");
							dialog.show();
							new Thread(new UpdateStatusThread()).start();
						} else {
							Weibo weibo = Weibo.getInstance();
							weibo.setupConsumerConfig(Weibo.getAppKey(),Weibo.getAppSecret());
							weibo.setRedirectUrl(CommonUtils.REDIRECT_URL);
							weibo.authorize(mInstance, new AuthDialogListener());
						}
					}
				} else {
					Toast.makeText(mContext, getString(R.string.network_error),
							Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.share_imagechoose:
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, REQUEST_CODE_ADDIMAGE);
				break;
			case R.id.share_image_delete:
	    		DialogUtils.dialogBuilder(mInstance, "Hint","Are you sure you wanna delete this?", new DialogCallBack(){
					@Override
					public void callBack() {
						imageView.setVisibility(View.GONE);
						imageDeleteView.setVisibility(View.GONE);
						weiboImgPath = null;
					}
	    		});
				break;
			}
		}
	};

	
	
	class AuthDialogListener implements WeiboDialogListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			String uid = values.getString("uid");

			long localExpiresIn = System.currentTimeMillis()
					+ Integer.parseInt(expires_in) * 1000;
			
			WeiboUtils.updateLocalToken(data, uid, token, localExpiresIn);
			
			AccessToken accessToken = new AccessToken(token, Weibo.getAppSecret());
			accessToken.setExpiresIn(expires_in);
			Weibo.getInstance().setAccessToken(accessToken);

			dialog.setMessage("Sharing...");
			dialog.show();
			new Thread(new UpdateStatusThread()).start();
		}
		
		@Override
		public void onError(DialogError e) {
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	Handler shareHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			dialog.hide();

			Bundle data = msg.getData();
			int retCode = data.getInt("retCode");
			String retMsg = data.getString("retMsg");

			if (retCode == 1) {
				Toast.makeText(mContext, "Success!", Toast.LENGTH_SHORT).show();
				
				weiboContentText.setText("");
				wordCounterView.setText("");
				imageView.setVisibility(View.GONE);
				imageDeleteView.setVisibility(View.GONE);
				weiboImgPath = null;
			} else {
				if (StringUtils.isBlank(retMsg)) {
					Toast.makeText(mContext, "Sorry, it failed.", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, "Sorry, it failed, " + retMsg,Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_ADDIMAGE) {
			if (resultCode != RESULT_OK) {
				return;
			}

			if (data == null)
				return;

			Uri thisUri = data.getData();
			String thePath = CommonUtils.getAbsolutePathFromNoStandardUri(thisUri);

			// Standard Uri
			if (StringUtils.isBlank(thePath)) {
				weiboImgPath = CommonUtils.getAbsoluteImagePath(mContext, thisUri);
			} else {
				weiboImgPath = thePath;
			}

			String attFormat = FileUtils.getFileFormat(weiboImgPath);
			if (!"photo".equals(MediaUtils.getContentType(attFormat))) {
				weiboImgPath = null;
				Toast.makeText(mContext, "Please choose a picture.", Toast.LENGTH_SHORT).show();
				return;
			}
			imageView.setVisibility(View.VISIBLE);
			imageDeleteView.setVisibility(View.VISIBLE);
		}
	}
	
	//Weibo sharing thread
	class UpdateStatusThread implements Runnable {
		public void run() {

			int retCode = 0;
			String retMsg = "";

			Weibo weibo = Weibo.getInstance();

			if (!StringUtils.isBlank(weiboImgPath)) {
				try {
					retMsg = WeiboUtils.upload(mContext, weibo, Weibo.getAppKey(), weiboImgPath,
							weiboContentText.getText().toString(), "", "");
					retCode = 1;
				} catch (WeiboException e) {
					retCode = -1;
					retMsg = e.getMessage();
				} catch (Exception e) {
					retCode = -1;
					retMsg = e.getMessage();
				}
			} else {
				try {
					retMsg = WeiboUtils.update(mContext, weibo, Weibo.getAppKey(), weiboContentText
							.getText().toString(), "", "");
					retCode = 1;
				} catch (WeiboException e) {
					retCode = -1;
					retMsg = e.getMessage();
				} catch (Exception e) {
					retCode = -1;
					retMsg = e.getMessage();
				}
			}
			
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putInt("retCode", retCode);
			data.putString("retMsg", retMsg);
			msg.setData(data);
			
			shareHandler.sendMessage(msg);
		}
	}

	private boolean isChecked() {
		boolean ret = true;
		if (StringUtils.isBlank(weiboContentText.getText().toString())) {
			Toast.makeText(mContext, "Say something?", Toast.LENGTH_SHORT).show();
			ret = false;
		} else if (weiboContentText.getText().toString().length() > 140) {
			int currentLength = weiboContentText.getText().toString().length();

			Toast.makeText(mContext, "Beyond" + (currentLength - 140) + "Characters.",
					Toast.LENGTH_SHORT).show();
			ret = false;
		}
		return ret;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		menu.add(0, TOOLBAR0, 1, "Logout" ).setIcon( android.R.drawable.ic_menu_delete );
		menu.add(0, TOOLBAR1, 2, "About" ).setIcon( android.R.drawable.ic_menu_help);
		menu.add(0, TOOLBAR2, 3, "Exit" ).setIcon( android.R.drawable.ic_menu_revert );
		return super.onCreateOptionsMenu(menu);
	}
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
    	if( item.getItemId() == 0 )
    	{
    		DialogUtils.dialogBuilder(mInstance, "Hint","Are you sure you want to logout?", new DialogCallBack(){
				@Override
				public void callBack() {
					WeiboUtils.updateLocalToken(data, "", "", 0);
					Toast.makeText(mContext, "User has logged out.", Toast.LENGTH_SHORT).show();
				}
    		});
    	}
    	else if( item.getItemId() == 1 )
    	{
    		about();
    	}
    	else
    	{	
    		finish();
    	}
    	return super.onOptionsItemSelected(item);
	}
    
	private void about()
	{
		Context mContext = getApplicationContext(); 
	    LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE); 
	    View layout = inflater.inflate( R.layout.about_dialog, (ViewGroup)findViewById(R.id.layout_root)); 
	    
	    AlertDialog.Builder builder; 
	    builder = new AlertDialog.Builder( mContext ); 
	    builder.setView ( layout ); 
	    
	    new AlertDialog.Builder(this).setView(layout).setTitle("About")
			.setPositiveButton("OK", 
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog,
							int which)
					{
						dialog.dismiss();
					}
				}).show();
	}
}