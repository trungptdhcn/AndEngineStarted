package com.thor.chess;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

public class AIGameDialog extends Dialog {
	private GameListener listener = null;	
	
	public AIGameDialog(Context context) {
		super(context);
	}
	
	public void setListener(GameListener listener) {
		this.listener = listener;
	}	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ai_menu);
		setTitle(R.string.ai_game);
		
		Button btnStart = (Button)findViewById(R.id.btn_start_game);
		btnStart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int player = 0;
				float searchSeconds = 0.5f;
				RadioGroup radioColor = 
						(RadioGroup)findViewById(R.id.rad_player_color);
				if (radioColor.getCheckedRadioButtonId() == R.id.radio_red) {
					player = 0;
				} else {
					player = 1;
				}
				RadioGroup radioLevel = 
						(RadioGroup)findViewById(R.id.radio_level);
				if (radioLevel.getCheckedRadioButtonId() == R.id.radio_easy) {
					searchSeconds = 0.5f;
				} else if (radioLevel.getCheckedRadioButtonId() == R.id.radio_normal) {
					searchSeconds = 1.5f;
				} else {
					searchSeconds = 2.5f;
				}
				AIEngine engine = new AIEngine(player, searchSeconds);
				
				dismiss();
				if (listener != null)
					listener.onCreateEngine(engine, false);
			}
		});
	}
}


//class AIGameDialog extends DialogFragment {
//	private AIEngine engine = null;
//	private GameListener listener = null;
//	public AIEngine getEngine() {
//		return engine;
//	}
//	
//	public void setListener(GameListener listener) {
//		this.listener = listener;
//	}
//
//	@Override
//	public Dialog onCreateDialog(Bundle savedInstanceState) {
//		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//		LayoutInflater inflater = getActivity().getLayoutInflater();
//		final View view = inflater.inflate(R.layout.ai_menu, null);
//		builder.setTitle(R.string.ai_game);
//		builder.setView(view).setPositiveButton(R.string.start_game,
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						int player = 0;
//						float searchSeconds = 0.5f;
//						RadioGroup radioColor = (RadioGroup) view
//								.findViewById(R.id.rad_player_color);
//						if (radioColor.getCheckedRadioButtonId() == R.id.radio_red) {
//							player = 0;
//						} else {
//							player = 1;
//						}
//						RadioGroup radioLevel = (RadioGroup) view
//								.findViewById(R.id.radio_level);
//						if (radioLevel.getCheckedRadioButtonId() == R.id.radio_easy) {
//							searchSeconds = 0.5f;
//						} else if (radioLevel.getCheckedRadioButtonId() == R.id.radio_easy) {
//							searchSeconds = 2f;
//						} else {
//							searchSeconds = 3f;
//						}
//						engine = new AIEngine(player, searchSeconds);
//						listener.onCreateEngine(engine);
//					}
//				});
//		return builder.create();
//	}
//}