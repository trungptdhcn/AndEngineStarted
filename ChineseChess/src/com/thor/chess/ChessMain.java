package com.thor.chess;

import java.util.Date;
import com.thor.chess.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

interface GameListener {
    public void onCreateEngine(Engine engine, boolean netServer);
}

@SuppressLint("ShowToast")
public class ChessMain extends Activity implements GameListener, 
		IChessEventListener {
	
	private Date backKeyTime = null;
	private Toast toast = null;
	
//	/**
//     *
//     * @param activity
//     * @return
//     */
//    public static int getScreenBrightness(Activity activity) {
//        int nowBrightnessValue = 0;
//        ContentResolver resolver = activity.getContentResolver();
//        try {
//            nowBrightnessValue = android.provider.Settings.System.getInt(
//                    resolver, Settings.System.SCREEN_BRIGHTNESS);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return nowBrightnessValue;
//    }
	

	
	@Override
	public void onBackPressed() {
		if (currentView == gameLayout)
			returnMain(true);
		else {
			Date now = new Date();
			
			if (backKeyTime == null || (now.getTime() - backKeyTime.getTime()) > 2000) {
				backKeyTime = new Date();
				toast.show();
			} else {
				toast.cancel();
				finish();
			}
		}
	}

	public void onReturn(boolean needAsk) {
		returnMain(needAsk);
	}

	// Chess board
	private ChessLayout gameLayout = null;
	private View homeMenuLayout = null;
	private View currentView = null;
	private NetGameDialog netDialog = null;
	private MenuItem soundItem = null;
	private MenuItem brightnessItem = null;
	private boolean nightMode = false;
	private int oldAutoBrightness = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
	private int oldBrightness = 255;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = Toast.makeText(this, "�ٰ�һ���˳�", Toast.LENGTH_SHORT);
        // Set screen should always portrait.
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.play_form);
        gameLayout = (ChessLayout)findViewById(R.id.mainLayout);
        gameLayout.setChessListener(this);

        setContentView(R.layout.main_form);
        homeMenuLayout = findViewById(R.id.main_menu);        
        currentView = homeMenuLayout; 
        
        initMainMenu();
        
        recordBrightness();
        gameLayout.setEnableSound(Boolean.parseBoolean(
        		ChessApplication.getSetting("Sound", "true")));
        setNightMode(Boolean.parseBoolean(
        		ChessApplication.getSetting("NightMode", "false")));
    }

	private void initMainMenu() {
		Button aiButton = (Button) findViewById(R.id.btn_ai_game);
		aiButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AIGameDialog dialog = new AIGameDialog(ChessMain.this);
				dialog.setListener(ChessMain.this);
				dialog.show();
			}
		});
		
		Button netButton = (Button) findViewById(R.id.btn_net_game);
		netButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				netDialog = new NetGameDialog(ChessMain.this);
				netDialog.setListener(ChessMain.this);
				netDialog.show();
			}
		});
		
		Button exitButton = (Button) findViewById(R.id.btn_exit);
		exitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				toast.cancel();
				finish();
			}
		});
	}
	
	public void onCreateEngine(Engine engine, boolean netServer) {
		setContentView(gameLayout);
		currentView = gameLayout;
		gameLayout.startGame(engine);
		if (netServer)
			gameLayout.startServer();
	}
	
	public void stopAutoBrightness() {
		Settings.System.putInt(this.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	}
	
	/**
	 * 
	 * @param brightness 0 to 255 (means dark to full bright), 
	 * less 0 means use default
	 */
    public void setBrightness(int brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness / 255f);
        getWindow().setAttributes(lp);
    }
    
    public void recordBrightness() {
	    try {
	    	ContentResolver cr = getContentResolver();
	    	oldAutoBrightness = Settings.System.getInt(cr,
	    			Settings.System.SCREEN_BRIGHTNESS_MODE);
	    	oldBrightness = Settings.System.getInt(cr, 
	    			Settings.System.SCREEN_BRIGHTNESS);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
    }
    
    public void restoreBrightness() {
    	try {
	    	ContentResolver cr = getContentResolver();
	    	Settings.System.putInt(cr,
	    			Settings.System.SCREEN_BRIGHTNESS_MODE, oldAutoBrightness);
	    	Settings.System.putInt(cr, 
	    			Settings.System.SCREEN_BRIGHTNESS, oldBrightness);
	    	setBrightness(oldBrightness);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
    }
	
    /// ��ʵ���Ҳ��ԣ����ֻ�������Լ���Ӧ�����޸����ȵĻ���ֻ��Ҫ���޸�����֮ǰ
    /// �Ȱ��Զ������޸�Ϊ�رգ����޸������Ⱥ��ڻָ���ԭ����ֵ�����ˡ�
	public void setNightMode(boolean nightMode) {
		this.nightMode = nightMode;
		gameLayout.setNightMode(nightMode);
		if (nightMode) {
			stopAutoBrightness();
			setBrightness(0);
		} else {
			restoreBrightness();
		}
        ChessApplication.setSetting("NightMode", ((Boolean)nightMode).toString());
	}
	
	public boolean getNightMode() {
		return nightMode;
	}
	
	private void adjustSoundMenu() {
		if (gameLayout.getEnableSound()) {
        	soundItem.setIcon(R.drawable.mute);
        	soundItem.setTitle(R.string.menu_no_sound);
        } else {
        	soundItem.setIcon(R.drawable.music);
        	soundItem.setTitle(R.string.menu_sound);
        }
	}
	
	private void adjustBrightnessMenu() {
		if (getNightMode()) {
			brightnessItem.setIcon(R.drawable.sun);
			brightnessItem.setTitle(R.string.menu_day_mode);
        } else {
        	brightnessItem.setIcon(R.drawable.moon);
        	brightnessItem.setTitle(R.string.menu_night_mode);
        }
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		
        getMenuInflater().inflate(R.menu.activity_chess_main, menu);
        soundItem = menu.getItem(0);
        adjustSoundMenu();
        soundItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				gameLayout.setEnableSound(!gameLayout.getEnableSound());
				adjustSoundMenu();
				return true;
			}
		});
        
        brightnessItem = menu.getItem(1);
        adjustBrightnessMenu();
        brightnessItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				setNightMode(!getNightMode());
				adjustBrightnessMenu();
				return true;
			}
		});
        MenuItem quitItem = menu.getItem(2);
        quitItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				toast.cancel();
				finish();
				return true;
			}
		});
        
        return true;
    }
	
	public void finishGame() {
		gameLayout.finishGame();
		setContentView(homeMenuLayout);
		currentView = homeMenuLayout;
	}
	
	public void returnMain(boolean needAsk) {
		if (!needAsk) {
			finishGame();
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Ҫ����ǰ��Ϸ��");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finishGame();
			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		builder.create().show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case 1:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
            	netDialog.startDiscovery();
            }
            break;
        case 2:
        	if (resultCode == 300) {
                // Bluetooth is now discoverable, so set up a chat session
            	netDialog.startNetServer();
            }
        	break;
        }
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		restoreBrightness();
		System.exit(0);
	}
}

