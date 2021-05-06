package com.damirkin.cambridgewhale;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.damirkin.cambridgewhale.adapters.VariantsAdapter;
import com.damirkin.cambridgewhale.word.Word;
import com.damirkin.cambridgewhale.word.WordVar;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class MainFragment extends Fragment {

    Context context;
    Word serWord = new Word();
    WordSearcher wordSearcher = new WordSearcher(serWord);
    ConstraintLayout variantParent;
    MediaPlayer mPlayerUk, mPlayerUs;
    TextView searchView, title_translated;
    TextView translate_word;
    TextView transUk, transUs;
    LinearLayout translate_layout, translated_layout;
    ListView variantsList, translated_variants;
    ImageButton playUk, playUs;
    LayoutInflater inflater;
    ProgressBar progressBar;
    ArrayAdapter<String> adapter;
    View main_v;
    InputMethodManager keyboard;

//    private static final String FILE_NAME = "data.json";


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;

//        List<String> list = new ArrayList<>();
//        list.add("Кантюков");
//        list.add("Дамир");
//        list.add("Галеевич");
//        exportToJSON(context, list);
//        System.out.println(list);
//        System.out.println(importFromJSON(context));

//        list.removeIf(e -> e == "Кант");
//        System.out.println(list);

//        LinkedList<String> link = new LinkedList<>();
//        link.add("first");
//        link.add("second");
//        ListIterator<String> iterL = link.listIterator();
//        System.out.println(iterL.next());
//        System.out.println(iterL.next());
//        System.out.println(iterL.previous());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        variantParent = (ConstraintLayout) view.findViewById(R.id.body);
        this.main_v = view;
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = view.findViewById(R.id.search);

        ImageButton deleteBtn = view.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener((v -> searchView.setText("")));

        ImageButton searchBtn = view.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener((v -> {
            serWord.deteteAll();
//            adapter.notifyDataSetChanged();
            variantParent.removeView(variantsList);
            System.out.println("вариант лист очищен");
            searchView.clearFocus();
            String search_word = searchView.getText().toString();
            searchWordGo(search_word);
            keyboard.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }));

        keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        searchView.setOnFocusChangeListener((v, isFoc) -> {
            if (isFoc) {
                System.out.println("фокус заработал");
                searchView.addTextChangedListener(watcher);
                keyboard.showSoftInput(searchView, 0);
            } else  {
                System.out.println("фокус выключился");
                searchView.removeTextChangedListener(watcher);
                keyboard.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setList(ArrayList<String> variants) {
        System.out.println("установка вариан листа");
        variantsList = (ListView) variantParent.findViewById(R.id.listView);
        if (variantsList == null) {
            System.out.println("вариант не существует");
            inflater = (LayoutInflater) getLayoutInflater();
            variantsList = (ListView) inflater.inflate(R.layout.listview_list, variantParent, false);
            System.out.println( "Варинт лист первоначально создан");
            if (adapter == null) {
                adapter = new ArrayAdapter<>(context, R.layout.variants_item, variants);
                variantsList.setAdapter(adapter);
            }

            variantsList.setAdapter(adapter);
            System.out.println("вариант лист заполнен");

            variantsList.setDividerHeight((int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));

            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, context.getResources().getDisplayMetrics());
            layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.height = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, (adapter.getCount() * 47), getResources().getDisplayMetrics());
            variantsList.setLayoutParams(layoutParams);
            variantParent.addView(variantsList);
            System.out.println( "Варинт лист первоначально утсановлен");

            variantsList.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {

//                searchView.removeTextChangedListener(watcher);
                searchView.clearFocus();

                System.out.println("прослушиватель поисковика удален");
                String search_word = adapter.getItem(position);

                searchView.setText(search_word);
                serWord.deteteAll();
                adapter.notifyDataSetChanged();
                System.out.println("вариант лист очищен");

                searchWordGo(search_word);

//                searchView.setFocusableInTouchMode(true);
                keyboard.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                variantParent.removeView(variantsList);
                System.out.println("вариант лист удален");
            });

        } else {
            System.out.println("вариант существует");
            if (variants.size() > 0)
//                adapter.addAll(variants);
            adapter.notifyDataSetChanged();
            System.out.println("вариант лист обновлен");
            ConstraintLayout.LayoutParams variantsListParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
            variantsList.setDividerHeight((int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
            variantsListParams.height = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, (adapter.getCount() * 47), getResources().getDisplayMetrics());
            variantsList.setLayoutParams(variantsListParams);
        }
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            System.out.println("изменения в блоке поиска");
            String search_word = s.toString();
            search_word = search_word.trim();
            if (search_word.length() > 1) {
                String finalSearch_word = search_word;
                Runnable serachRun = () -> {
                    try {
                        ArrayList<String> variants = wordSearcher.getVariants(finalSearch_word);
                        if (variants.size() > 0) {
                            /////////////////////////////////////////////////////////////////////////////////////////////////////////
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            Runnable myRunnable = () -> setList(variants);
                            mainHandler.post(myRunnable);
                            /////////////////////////////////////////////////////////////////////////////////////////////////
                        } else {
                            variantParent.post(() -> {
                                        variantParent.removeView(variantsList);
                                        serWord.deteteAll();
//                                        adapter.notifyDataSetChanged();
                                    });
                        }
                    } catch (IOException ex) {
                        System.out.println("Ошибка не в главном потоке при описке слова");
                    }
                };
                Thread thread = new Thread(serachRun);
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                variantParent.removeView(variantsList);
//                if (adapter != null) {
//                    serWord.deteteAll();
//                    adapter.notifyDataSetChanged();
//                }
            }
        }
    };


    private void searchWordGo(String search_word) {
        if (search_word.length() > 1) {
//            ProgressBar progressBar = (ProgressBar) main_v.findViewById(R.id.progressBar);
//            progressBar.setVisibility(ProgressBar.VISIBLE);
            String finalSearch_word = search_word.toLowerCase();
            Runnable serachRun = () -> {
                try {
                    int cause = wordSearcher.getWord(finalSearch_word);
                    if (cause == 1) {
                                Handler mainHandler = new Handler(Looper.getMainLooper());

                                Runnable myRunnable = () -> {
                                    translate_word = (TextView) main_v.findViewById(R.id.item_word);
                                    searchView.setText(serWord.getName());
                                    if (translate_word == null) {

                                        inflater = (LayoutInflater) getLayoutInflater();
                                        translate_layout = (LinearLayout) inflater.inflate(R.layout.translite_item, variantParent, false);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                            translate_layout.setId(View.generateViewId());
                                        }
                                        translated_layout = (LinearLayout) inflater.inflate(R.layout.translited_item, variantParent, false);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                            translated_layout.setId(View.generateViewId());
                                        }

                                        ConstraintLayout.LayoutParams layoutParams1 = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT , ConstraintLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParams1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                                        layoutParams1.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                                        layoutParams1.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                                        translate_layout.setLayoutParams(layoutParams1);
                                        variantParent.addView(translate_layout);

                                        ConstraintLayout.LayoutParams layoutParams2 = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT , ConstraintLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParams2.topToBottom = translate_layout.getId();
                                        layoutParams2.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                                        layoutParams2.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                                        translated_layout.setLayoutParams(layoutParams2);
                                        variantParent.addView(translated_layout);

                                        translate_word = (TextView) translate_layout.findViewById(R.id.item_word);
                                        translate_word.setText(serWord.getName());

                                        transUk = (TextView) translate_layout.findViewById(R.id.uk_transcription);
                                        String UkTransc = "[" + serWord.getUk_trans() + "]";
                                        transUk.setText(UkTransc);
                                        transUs = (TextView) translate_layout.findViewById(R.id.us_transcription);
                                        String UsTransc = "[" + serWord.getUs_trans()  + "]";
                                        transUs.setText(UsTransc);

//                                            mPlayerUk = MediaPlayer.create(getContext(), serWord.getUk_audio());
                                        mPlayerUk = new MediaPlayer();
                                        try {
                                            mPlayerUk.setDataSource(serWord.getUk_audio().toString());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        mPlayerUk.prepareAsync();
                                        mPlayerUk.setOnCompletionListener(MediaPlayer::start);

//                                            mPlayerUs = MediaPlayer.create(getContext(), serWord.getUs_audio());
                                        mPlayerUs = new MediaPlayer();
                                        try {
                                            mPlayerUs.setDataSource(serWord.getUs_audio().toString());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        mPlayerUs.prepareAsync();
                                        mPlayerUs.setOnCompletionListener(MediaPlayer::start);

                                        playUk = (ImageButton) translate_layout.findViewById(R.id.uk_soundn);
                                        playUs = (ImageButton) translate_layout.findViewById(R.id.us_soundn);

                                        playUk.setOnClickListener((v -> {
                                            playUk.setFocusable(true);
                                            System.out.println("Воспроизведение началось");
                                            playUk.setEnabled(false);
                                            mPlayerUk.start();
                                            mPlayerUk.setOnCompletionListener((mp) -> {
                                                    playUk.setFocusable(false);
                                                    playUk.setEnabled(true);
                                                    System.out.println("Воспроизведение закончилось");
                                                }
                                            );
                                        }));
                                        playUs.setOnClickListener((v -> {
                                            playUs.setImageResource(R.drawable.ic_play);
                                            playUs.setEnabled(false);
                                            mPlayerUs.start();
                                            mPlayerUs.setOnCompletionListener((mp) -> {
                                                    playUs.setImageResource(R.drawable.ic_sound);
                                                    playUs.setEnabled(true);
                                                }
                                            );
                                        }));
//////////////////////////////////////////////////////////////
                                        translated_variants = (ListView) inflater.inflate(R.layout.listview_variants_list, variantParent, false);

                                        title_translated = (TextView) translated_layout.findViewById(R.id.translited_item_word);
                                        if (serWord.getExamples().size() == 0)
                                            title_translated.setText("Варинатов переводов для данного слова нет");
                                        else
                                            title_translated.setText(serWord.getExamples().get(0).getTranslation());

                                        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                                                ConstraintLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                                        layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                                        layoutParams.topToBottom = translated_layout.getId();
                                        translated_variants.setLayoutParams(layoutParams);

                                        VariantsAdapter adapter_example = new VariantsAdapter(context, R.layout.words_item, serWord.getExamples());
                                        translated_variants.setAdapter(adapter_example);
                                        translated_variants.setDividerHeight((int) TypedValue.applyDimension(
                                                TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                                        setListViewHeightBasedOnRow(translated_variants);
                                        variantParent.addView(translated_variants);
                                    } else {
                                        translate_word.setText(serWord.getName());

                                        String UkTransc = "[" + serWord.getUk_trans() + "]";
                                        transUk.setText(UkTransc);
                                        String UsTransc = "[" + serWord.getUs_trans()  + "]";
                                        transUs.setText(UsTransc);

//                                            mPlayerUk = MediaPlayer.create(getContext(), serWord.getUk_audio());
                                        mPlayerUk.reset();
                                        try {
                                            mPlayerUk.setDataSource(serWord.getUk_audio().toString());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        mPlayerUk.prepareAsync();
                                        System.out.println(serWord.getUk_audio().toString());

//                                            mPlayerUs = MediaPlayer.create(getContext(), serWord.getUs_audio());
                                        mPlayerUs.reset();
                                        try {
                                            mPlayerUs.setDataSource(serWord.getUs_audio().toString());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        mPlayerUs.prepareAsync();
                                        System.out.println(serWord.getUs_audio().toString());

                                        variantParent.removeView(translated_variants);

                                        translated_variants = (ListView) inflater.inflate(R.layout.listview_variants_list, variantParent, false);

                                        title_translated = (TextView) translated_layout.findViewById(R.id.translited_item_word);
                                        if (serWord.getExamples().size() == 0)
                                            title_translated.setText("Варинатов переводов для данного слова нет");
                                        else
                                            title_translated.setText(serWord.getExamples().get(0).getTranslation());

                                        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                                                ConstraintLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                                        layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                                        layoutParams.topToBottom = translated_layout.getId();
                                        translated_variants.setLayoutParams(layoutParams);

                                        VariantsAdapter adapter_example = new VariantsAdapter(context, R.layout.words_item, serWord.getExamples());
                                        translated_variants.setAdapter(adapter_example);
                                        translated_variants.setDividerHeight((int) TypedValue.applyDimension(
                                                TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                                        setListViewHeightBasedOnRow(translated_variants);
                                        variantParent.addView(translated_variants);
                                    }
                                };
                                mainHandler.post(myRunnable);
//                                progressBar.setVisibility(ProgressBar.INVISIBLE);
                    } else if (cause == 0) {
                        System.out.println("действия какие то если слова вообще нет");
                        variantParent.post(() -> {
                            variantParent.removeView(translate_layout);
                            variantParent.removeView(translated_layout);
                            variantParent.removeView(translated_variants);
                            Toast.makeText(context, "К сожалению такого слова нет", Toast.LENGTH_SHORT).show();
                        });

                        // здесь надо показать какой нибудь тост что либо нет инета либо другая причина
                    }
                } catch (IOException ex) {
                    System.out.println("Поиск слова завершился ошибкой");
                    System.out.println(ex.toString());
                }
            };
            Thread thread = new Thread(serachRun);
            thread.start();
        }
    }

    public void setListViewHeightBasedOnRow(ListView listView) {
        if (listView == null) {
            return;
        }
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        System.out.println(listAdapter.getCount());
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
//            listItem.measure(0, 0);
//            System.out.println(listItem.getMeasuredHeight());
//            totalHeight += listItem.getMeasuredHeight();
            totalHeight += (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 125, getResources().getDisplayMetrics());
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
    }

//    static boolean exportToJSON(Context context, List<String> dataList) {
//        Gson gson = new Gson();
//        String jsonString = gson.toJson(dataList);
//
//        FileOutputStream fileOutputStream = null;
//
//        try {
//            fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
//            fileOutputStream.write(jsonString.getBytes());
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (fileOutputStream != null) {
//                try {
//                    fileOutputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return false;
//    }

//    static List<String> importFromJSON(Context context) {
//        InputStreamReader inputStreamReader = null;
//        FileInputStream fileInputStream = null;
//        try {
//            fileInputStream = context.openFileInput(FILE_NAME);
//            inputStreamReader = new InputStreamReader(fileInputStream);
//            Gson gson = new Gson();
//            List<String> data = gson.fromJson(inputStreamReader, ArrayList.class);
//            return data;
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            if (inputStreamReader != null) {
//                try {
//                    inputStreamReader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (fileInputStream != null) {
//                try {
//                    fileInputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return  null;
//    }

}