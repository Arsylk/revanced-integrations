package app.revanced.integrations.patches;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.bluesky.browser.videodownloader.VideoDownloaderBean;

import java.lang.reflect.Field;

public class SuperBrowserOnBindPatch {

    public static void attach(Object rawViewHolder, VideoDownloaderBean bean) {
        if (bean == null) return;
        try {
            Field itemViewField = rawViewHolder.getClass().getField("itemView");
            itemViewField.setAccessible(true);
            View itemView = (View) itemViewField.get(rawViewHolder);
            assert itemView != null;
            itemView.setOnLongClickListener(view -> {
                openDialog(view.getContext(), bean);
                return true;
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void openDialog(Context context, VideoDownloaderBean bean) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(new String[]{"Intent Chooser", "Clipboard"}, (dialogInterface, i) -> {
            Intent intent = null;
            switch (i) {
                case 0:
                    intent = prepareIntent(bean);
                    break;
                case 1:
                    copyToClipboard(context, bean);
                    return;
            }
            if (intent != null) context.startActivity(intent);
        });
        builder.setTitle(bean.getVideotitle());
        builder.setNegativeButton("Close", null);
        builder.show();
    }

    private static Intent prepareIntent(VideoDownloaderBean bean) {
        String title = bean.getVideotitle();
        Uri uri = Uri.parse(bean.getSdurl());

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "video/*");
        intent.putExtra(Intent.EXTRA_TEXT, uri.toString());
        intent.putExtra("title", title);
        intent = Intent.createChooser(intent, "Open");

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);

        return intent;
    }

    public static void copyToClipboard(Context context, VideoDownloaderBean bean) {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("VideoDownloaderBean#sdurl", bean.getSdurl());
            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(context, "Copied: "+bean.getSdurl(), Toast.LENGTH_SHORT).show();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
