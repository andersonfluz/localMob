package com.mobmundo.localmob.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class Alertas {

	public AlertDialog gerarAlerta(Activity activity, Context ctx, String titulo,
			String mensagem) {
		AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
		alertDialog.setTitle("Acesso Internet");
		alertDialog
				.setMessage("Instabilidade na Internet, verifique sua conexao.");
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog.setIcon(ctx.getResources().getDrawable(
				android.R.drawable.ic_dialog_alert));
		return alertDialog;
	}
}
