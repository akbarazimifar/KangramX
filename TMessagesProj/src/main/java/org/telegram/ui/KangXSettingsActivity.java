/*
 * Copyright 23rd, 2019.
 */

package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.SeekBarView;

import java.lang.reflect.*;
import java.util.ArrayList;

public class KangXSettingsActivity extends BaseFragment {

    private class StickerSizeCell extends FrameLayout {

        private SeekBarView sizeBar;
        private TextPaint textPaint;

        private final int startStickerSize = 2;
        private final int endStickerSize = (int)ChatMessageCell.MAX_STICKER_SIZE;
        private final String option = "stickerSize";

        private float diff() {
            return (float)(endStickerSize -  startStickerSize);
        }

        private float stickerSize() {
            return MessagesController.getGlobalMainSettings().getFloat(option, endStickerSize);
        }

        private void setStickerSize(float size) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat(option, size);
            editor.commit();
        }

        public StickerSizeCell(Context context) {
            super(context);

            setWillNotDraw(false);

            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(AndroidUtilities.dp(16));

            sizeBar = new SeekBarView(context);
            sizeBar.setReportChanges(true);
            sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                @Override
                public void onSeekBarDrag(boolean stop, float progress) {
                    setStickerSize(startStickerSize + diff() * progress);
                    listAdapter.notifyItemChanged(stickerSizeRow);
                }

                @Override
                public void onSeekBarPressed(boolean pressed) {

                }
            });
            addView(
                sizeBar,
                LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT,
                    38,
                    Gravity.LEFT | Gravity.TOP,
                    9,
                    5,
                    43,
                    11));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
            canvas.drawText(
                "" + Math.round(stickerSize()),
                getMeasuredWidth() - AndroidUtilities.dp(39),
                AndroidUtilities.dp(28),
                textPaint);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            float a = (stickerSize() - startStickerSize);
            float b = diff();
            sizeBar.setProgress(a / b);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            sizeBar.invalidate();
        }
    }


    private RecyclerListView listView;
    private ListAdapter listAdapter;

    private ArrayList<Integer> sectionRows = new ArrayList<Integer>();
    private String[] sectionStrings = {"General", "ChatList", "FilterChats", "ChatCamera", "StickerSize", "ExperimentalSettings"};
    private int[] sectionInts = {0, R.string.ChatList, R.string.FilterChats, 0, R.string.StickerSize, R.string.ExperimentalSettings};

    private int rowCount;

    private int inappCameraRow;
    private int systemCameraRow;
    private int photoHasStickerRow;
    private int pauseMusicRecording;
    private int smoothKeyboard;
    private int chatBubbles;
    private int unmutedOnTopRow;
    private int rearVideoMessages;
    private int replaceForward;
    private int openArchiveOnPull;
    private int disableFlipPhotos;
    private int formatWithSeconds;
    private int disableThumbsInDialogList;

    private int stickerSizeRow;

    private ArrayList<Integer> emptyRows = new ArrayList<Integer>();
    private int syncPinsRow;

    private static int getIntLocale(String str) {
        try {
            try {
                return Class.forName("R")
                    .getDeclaredField("string")
                    .getDeclaringClass()
                    .getDeclaredField(str).getInt(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static String getLocale(String s) {
        return LocaleController.getString(s, 0);
    }
    private static String getLocale(String s, int i) {
        return LocaleController.getString(s, i);
    }

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();

        rowCount = 0;
        
        sectionRows.add(rowCount++);
        photoHasStickerRow = rowCount++;
        pauseMusicRecording = rowCount++;
    
        emptyRows.add(rowCount++);
        sectionRows.add(rowCount++);
        syncPinsRow = rowCount++;
        unmutedOnTopRow = rowCount++;
        openArchiveOnPull = rowCount++;
        disableThumbsInDialogList = rowCount++;
    
        emptyRows.add(rowCount++);
        sectionRows.add(rowCount++);
        disableFlipPhotos = rowCount++;
        formatWithSeconds = rowCount++;
        replaceForward = rowCount++;
        rearVideoMessages = rowCount++;
    
        emptyRows.add(rowCount++);
        sectionRows.add(rowCount++);
        inappCameraRow = rowCount++;
        systemCameraRow = rowCount++;

        emptyRows.add(rowCount++);
        sectionRows.add(rowCount++);
        stickerSizeRow = rowCount++;

        emptyRows.add(rowCount++);
        sectionRows.add(rowCount++);
        smoothKeyboard = rowCount++;
        if (Build.VERSION.SDK_INT >= 29) {
            chatBubbles = rowCount++;
        }

        return true;
    }

    public boolean toggleGlobalMainSetting(String option, View view, boolean byDefault) {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        boolean optionBool = preferences.getBoolean(option, byDefault);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(option, !optionBool);
        editor.commit();
        if (view instanceof TextCheckCell) {
            ((TextCheckCell) view).setChecked(!optionBool);
        }
        return !optionBool;
    }

    private void checkEnabledSystemCamera(TextCheckCell t) {
        t.setEnabled(SharedConfig.inappCamera, null);
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("KangXSettingsTitle", R.string.KangXSettingsTitle));

        if (AndroidUtilities.isTablet()) {
            actionBar.setOccupyStatusBar(false);
        }
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        listAdapter = new ListAdapter(context);

        fragmentView = new FrameLayout(context);
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listView = new RecyclerListView(context);
        listView.setVerticalScrollBarEnabled(false);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        listView.setGlowColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
        listView.setAdapter(listAdapter);
        listView.setItemAnimator(null);
        listView.setLayoutAnimation(null);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.TOP | Gravity.LEFT));
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (position == inappCameraRow) {
                SharedConfig.toggleInappCamera();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.inappCamera);
                }

                RecyclerListView.Holder holder = (RecyclerListView.Holder) listView.findViewHolderForAdapterPosition(systemCameraRow);
                if (holder != null) {
                    checkEnabledSystemCamera((TextCheckCell) holder.itemView);
                }
                
            } else if (position == systemCameraRow) {
                if (view instanceof TextCheckCell) {
                    if (((TextCheckCell) view).isFakeEnabled()) {
                        toggleGlobalMainSetting("systemCamera", view, false);
                    }
                }
            } else if (position == photoHasStickerRow) {
                SharedConfig.toggleHasSticker();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.hasSticker);
                }
            } else if (position == pauseMusicRecording) {
                SharedConfig.togglePauseMusicOnRecord();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.pauseMusicOnRecord);
                }
            } else if (position == smoothKeyboard) {
                SharedConfig.toggleSmoothKeyboard();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.smoothKeyboard);
                }
            } else if (position == chatBubbles) {
                SharedConfig.toggleChatBubbles();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.chatBubbles);
                }
            } else if (position == unmutedOnTopRow) {
                toggleGlobalMainSetting("unmutedOnTop", view, false);
                MessagesController.getInstance(currentAccount).sortDialogs(null);
            } else if (position == rearVideoMessages) {
                toggleGlobalMainSetting("rearVideoMessages", view, false);
            } else if (position == replaceForward) {
                toggleGlobalMainSetting("replaceForward", view, true);
            } else if (position == openArchiveOnPull) {
                toggleGlobalMainSetting("openArchiveOnPull", view, false);
            } else if (position == disableFlipPhotos) {
                toggleGlobalMainSetting("disableFlipPhotos", view, false);
            } else if (position == formatWithSeconds) {
                toggleGlobalMainSetting("formatWithSeconds", view, false);
            } else if (position == disableThumbsInDialogList) {
                toggleGlobalMainSetting("disableThumbsInDialogList", view, false);
            } else if (position == syncPinsRow) {
                toggleGlobalMainSetting("syncPins", view, true);
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context mContext;

        public ListAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 2: {
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    break;
                }
                case 3: {
                    TextCheckCell textCell = (TextCheckCell) holder.itemView;
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    if (position == inappCameraRow) {
                        String t = LocaleController.getString("InAppCamera", R.string.InAppCamera);
                        String info = LocaleController.getString("InAppCameraInfo", R.string.InAppCameraInfo);
                        textCell.setTextAndValueAndCheck(t, info, preferences.getBoolean("inappCamera", true), false, false);
                    } else if (position == systemCameraRow) {
                        String t = LocaleController.getString("SystemCamera", R.string.SystemCamera);
                        String info = LocaleController.getString("SystemCameraInfo", R.string.SystemCameraInfo);
                        textCell.setTextAndValueAndCheck(t, info, preferences.getBoolean("systemCamera", false), false, false);
                        checkEnabledSystemCamera(textCell);
                    } else if (position == photoHasStickerRow) {
                        String t = LocaleController.getString("PhotoHasSticker", R.string.PhotoHasSticker);
                        String info = LocaleController.getString("PhotoHasStickerInfo", R.string.PhotoHasStickerInfo);
                        textCell.setTextAndValueAndCheck(t, info, preferences.getBoolean("photoHasSticker", false), true, false);
                    } else if (position == pauseMusicRecording) {
                        String t = LocaleController.getString("DebugMenuEnablePauseMusic", R.string.DebugMenuEnablePauseMusic);
                        textCell.setTextAndCheck(t, preferences.getBoolean("pauseMusicOnRecord", true), false);
                    } else if (position == smoothKeyboard) {
                        String t = LocaleController.getString("DebugMenuEnableSmoothKeyboard", R.string.DebugMenuEnableSmoothKeyboard);
                        textCell.setTextAndCheck(t, preferences.getBoolean("smoothKeyboard", false), false);
                    } else if (position == chatBubbles) {
                        String t = "Enable chat bubbles";
                        textCell.setTextAndCheck(t, preferences.getBoolean("chatBubbles", Build.VERSION.SDK_INT >= 30), false);
                    } else if (position == unmutedOnTopRow) {
                        String t = LocaleController.getString("UnmutedOnTop", R.string.UnmutedOnTop);
                        String info = LocaleController.getString("UnmutedOnTopInfo", R.string.UnmutedOnTopInfo);
                        textCell.setTextAndValueAndCheck(t, info, preferences.getBoolean("unmutedOnTop", false), true, false);
                    } else if (position == rearVideoMessages) {
                        String t = LocaleController.getString("RearVideoMessages", R.string.RearVideoMessages);
                        textCell.setTextAndCheck(t, preferences.getBoolean("rearVideoMessages", false), false);
                    } else if (position == replaceForward) {
                        String t = LocaleController.getString("ReplaceForward", R.string.ReplaceForward);
                        textCell.setTextAndCheck(t, preferences.getBoolean("replaceForward", true), false);
                    } else if (position == openArchiveOnPull) {
                        String t = LocaleController.getString("OpenArchiveOnPull", R.string.OpenArchiveOnPull);
                        textCell.setTextAndCheck(t, preferences.getBoolean("openArchiveOnPull", true), false);
                    } else if (position == disableFlipPhotos) {
                        String t = LocaleController.getString("DisableFlipPhotos", R.string.DisableFlipPhotos);
                        textCell.setTextAndCheck(t, preferences.getBoolean("disableFlipPhotos", false), false);
                    } else if (position == formatWithSeconds) {
                        String t = LocaleController.getString("FormatWithSeconds", R.string.FormatWithSeconds);
                        textCell.setTextAndCheck(t, preferences.getBoolean("formatWithSeconds", false), false);
                    } else if (position == disableThumbsInDialogList) {
                        String t = LocaleController.getString("DisableThumbsInDialogList", R.string.DisableThumbsInDialogList);
                        textCell.setTextAndCheck(t, preferences.getBoolean("disableThumbsInDialogList", false), false);
                    } else if (position == syncPinsRow) {
                        String t = LocaleController.getString("SyncPins", R.string.SyncPins);
                        String info = LocaleController.getString("SyncPinsInfo", R.string.SyncPinsInfo);
                        textCell.setTextAndValueAndCheck(t, info, preferences.getBoolean("syncPins", true), true, false);
                    }
                    break;
                }
                case 4: {
                    int i = sectionRows.indexOf(position);
                    if (i == -1) {
                        break;
                    }
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    headerCell.setText(getLocale(sectionStrings[i], sectionInts[i]));
                    break;
                }
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            boolean kangx = position == inappCameraRow
                        || position == systemCameraRow
                        || position == unmutedOnTopRow
                        || position == rearVideoMessages
                        || position == replaceForward
                        || position == openArchiveOnPull
                        || position == disableFlipPhotos
                        || position == formatWithSeconds
                        || position == disableThumbsInDialogList
                        || position == syncPinsRow
                        || position == photoHasStickerRow
                        || position == pauseMusicRecording
                        || position == smoothKeyboard
                        || position == chatBubbles;
            return kangx;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 1:
                    view = new ShadowSectionCell(mContext);
                    break;
                case 2:
                    view = new TextSettingsCell(mContext);
                    break;
                case 3:
                    view = new TextCheckCell(mContext);
                    break;
                case 4:
                    view = new HeaderCell(mContext);
                    break;
                case 5:
                    view = new StickerSizeCell(mContext);
                    break;
                case 6:
                    view = new TextDetailSettingsCell(mContext);
                    break;
            }
            if (viewType != 1) {
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public int getItemViewType(int position) {
            if (emptyRows.contains(position)) {
                return 1;
            } else if (0 == 1) {
                return 2;
            } else if (position == inappCameraRow
                || position == systemCameraRow
                || position == unmutedOnTopRow
                || position == syncPinsRow
                || position == rearVideoMessages
                || position == replaceForward
                || position == openArchiveOnPull
                || position == disableFlipPhotos
                || position == formatWithSeconds
                || position == disableThumbsInDialogList
                || position == photoHasStickerRow
                || position == pauseMusicRecording
                || position == smoothKeyboard
                || position == chatBubbles) {
                return 3;
            } else if (sectionRows.contains(position)) {
                return 4;
            } else if (position == stickerSizeRow) {
                return 5;
            }
            return 6;
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{EmptyCell.class, TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextDetailSettingsCell.class, NotificationsCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconBlue));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorBlue));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));

        return themeDescriptions;
    }
}
