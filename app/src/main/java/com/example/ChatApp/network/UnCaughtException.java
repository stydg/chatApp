package com.example.ChatApp.network;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import com.example.ChatApp.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * {@link UncaughtExceptionHandler} send an e-mail with some debug information
 * to the developer.
 *
 * @author VIJAYAKUMAR
 */
public class UnCaughtException implements UncaughtExceptionHandler {
	private static final String RECIPIENT = "smlee2@kb-sys.co.kr";
	private UncaughtExceptionHandler previousHandler;
	private Context context;
	private static Context context1;
	SharedPreferences preferences;

	public UnCaughtException(Context ctx) {
		context = ctx;
		context1 = ctx;

		preferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
		String errorFile = preferences.getString("error", "");
		if(!TextUtils.isEmpty(errorFile)){
			reportErrorDialog(errorFile);

			Editor editor = preferences.edit();
			editor.remove("error");
			editor.commit();
		}
	}

	private StatFs getStatFs() {
		File path = Environment.getDataDirectory();
		return new StatFs(path.getPath());
	}

	private long getAvailableInternalMemorySize(StatFs stat) {
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	private long getTotalInternalMemorySize(StatFs stat) {
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	private void addInformation(StringBuilder message) {
		message.append("Locale: ").append(Locale.getDefault()).append('\n');
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi;
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			message.append("Version: ").append(pi.versionName).append('\n');
			message.append("Package: ").append(pi.packageName).append('\n');
		} catch (Exception e) {
			Log.e("CustomExceptionHandler", "Error", e);
			message.append("Could not get Version information for ").append(context.getPackageName());
		}
		message.append("Phone Model: ").append(android.os.Build.MODEL).append('\n');
		message.append("Android Version: ").append(android.os.Build.VERSION.RELEASE).append('\n');
		message.append("Board: ").append(android.os.Build.BOARD).append('\n');
		message.append("Brand: ").append(android.os.Build.BRAND).append('\n');
		message.append("Device: ").append(android.os.Build.DEVICE).append('\n');
		message.append("Host: ").append(android.os.Build.HOST).append('\n');
		message.append("ID: ").append(android.os.Build.ID).append('\n');
		message.append("Model: ").append(android.os.Build.MODEL).append('\n');
		message.append("Product: ").append(android.os.Build.PRODUCT).append('\n');
		message.append("Type: ").append(android.os.Build.TYPE).append('\n');
		StatFs stat = getStatFs();
		message.append("Total Internal memory: ").append(getTotalInternalMemorySize(stat)).append('\n');
		message.append("Available Internal memory: ").append(getAvailableInternalMemorySize(stat)).append('\n');
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		try {
			StringBuilder report = new StringBuilder();
			Date curDate = new Date();
			report.append("Error Report collected on : ").append(curDate.toString()).append('\n').append('\n');
			report.append("Informations :").append('\n');
			addInformation(report);
			report.append('\n').append('\n');
			report.append("Stack:\n");
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			report.append(result.toString());
			printWriter.close();
			report.append('\n');
			report.append("**** 에러 보고서 끝 ***");
			Log.e(UnCaughtException.class.getName(), "Error to reportErrorDialog\n" + report);
			//안드로이드 API 30 적용 - 2020.11.26
//			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
				String fullPath = saveContent(report);
				Log.e(UnCaughtException.class.getName(), "Log file path : " + fullPath);
//			}
			/*Editor editor = preferences.edit();
			editor.putString("error", fullPath);
			editor.commit();*/
		} catch (Throwable ignore) {
			Log.e(UnCaughtException.class.getName(), "Error while sending error e-mail", ignore);
		}

//		if (!BuildConfig.DEBUG) {
			Intent intent = new Intent(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent myActivity = PendingIntent.getActivity(context,
					192837,
					intent,
					PendingIntent.FLAG_ONE_SHOT);

			AlarmManager alarmManager;
			alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 15000, myActivity);
//		}

		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}

	/**
	 * This method for call alert dialog when application crashed!
	 *
	 * @author vijayakumar
	 */
	public void reportErrorDialog(final String fullName) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				builder.setTitle("Exception");
				builder.create();
				builder.setNegativeButton("Close", null);
				builder.setPositiveButton("Send Email",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								sendMail(fullName);
							}
						});
				builder.setMessage("Unfortunately, This application has stopped");
				builder.show();
				Looper.loop();
			}
		}.start();
	}

	public String saveContent(final StringBuilder errorContent) {
		long time = new GregorianCalendar().getTimeInMillis();
		String path = Environment.getExternalStorageDirectory() + "/cube/";
		File fPath = new File(path);
		if( !fPath.exists() )  // 원하는 경로에 폴더가 있는지 확인
			fPath.mkdirs();

		String fullPath = path + "cube_error_" + time + ".log";

		File file = new File(fullPath);
		FileOutputStream fos = null;
		try {
			file.createNewFile();
			fos = new FileOutputStream(file, false);
			fos.write(errorContent.toString().getBytes());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//크래시 발생시 메시지 DM 으로 관리자에게 전송 - 2019.12.17
//		ServerInterfaceManager.getInstance().sendMessage(String.valueOf(501187105), String.valueOf(240601), errorContent.toString(), "M", "I");

		return fullPath;
	}


	/*private String extractLogToFile(){
		PackageManager pmanager = context.getPackageManager();
		PackageInfo pInfo = null;

		try {
			pInfo = pmanager.getPackageInfo(context.getPackageName(),  0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		String model = Build.MODEL;
		if(!model.startsWith(Build.MANUFACTURER)){
			model = Build.MANUFACTURER + " " + model;
		}

		String path = Environment.getExternalStorageDirectory() + "/";
		String fullPath = path + "cheongju.log";

		File file = new File(fullPath);
		InputStreamReader reader = null;
		FileWriter writer = null;
		try
		{
			// For Android 4.0 and earlier, you will get all app's log output, so filter it to
			// mostly limit it to your app's output.  In later versions, the filtering isn't needed.
			String cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ?
					"logcat -d -v time MyApp:v dalvikvm:v System.err:v *:s" :
					"logcat -d -v time";

			// get input stream
			Process process = Runtime.getRuntime().exec(cmd);
			reader = new InputStreamReader (process.getInputStream());

			// write output stream
			writer = new FileWriter (file);
			writer.write ("Android version: " +  Build.VERSION.SDK_INT + "\n");
			writer.write ("Device: " + model + "\n");
			writer.write ("App version: " + (pInfo == null ? "(null)" :pInfo.versionCode) + "\n");

			char[] buffer = new char[10000];
			do
			{
				int n = reader.read (buffer, 0, buffer.length);
				if (n == -1)
					break;
				writer.write (buffer, 0, n);
			} while (true);

			reader.close();
			writer.close();
		}
		catch (IOException e)
		{
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e1) {
				}
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e1) {
				}

			// You might want to write a failure message to the log here.
			return null;
		}

		return fullPath;

	}*/

	private void sendMail(String fullName) {
		if (fullName == null)
			return;

		Intent intent = new Intent (Intent.ACTION_SEND);
		intent.setType ("plain/text");
		intent.putExtra (Intent.EXTRA_EMAIL, new String[] { RECIPIENT });
		intent.putExtra (Intent.EXTRA_SUBJECT, "Error Report");
		intent.putExtra (Intent.EXTRA_STREAM, Uri.parse ("file://" + fullName));
		intent.putExtra (Intent.EXTRA_TEXT, "Log file attached."); // do this so some email clients don't complain about empty body.
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context1.startActivity (intent);
	}
}
