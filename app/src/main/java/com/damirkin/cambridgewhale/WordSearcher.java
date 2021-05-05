package com.damirkin.cambridgewhale;

import android.net.Uri;

import com.damirkin.cambridgewhale.word.Word;
import com.damirkin.cambridgewhale.word.WordVar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class WordSearcher { // коментарий на проверку коммита

    Word word;

    public WordSearcher(Word word) {
        this.word = word;
    }

    private static final String KEMBVARPATH1 = "https://dictionary.cambridge.org/autocomplete/amp?dataset=english&q=";
    private static final String KEMBVARPATH2 = "&__amp_source_origin=https%3A%2F%2Fdictionary.cambridge.org";
    private static final String KEMBWORDPTCH = "https://dictionary.cambridge.org/dictionary/english/";
    private static final String KEMBSEARCH = "https://dictionary.cambridge.org/dictionary/english-russian/";

    public ArrayList<String> getVariants(String word) throws IOException {
        System.out.println("начало поиска вариантов");
        String path = KEMBVARPATH1 + word + KEMBVARPATH2;
        BufferedReader reader = null;
        InputStream stream = null;
        HttpsURLConnection connection = null;
        try {
            URL url = new URL (path);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(1000);
            connection.connect();
            stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder buf = new StringBuilder();
            String line;
            while ((line=reader.readLine()) != null) {
                buf.append(line);
            }
            this.word.deteteAll();
            JSONArray jsonArray = new JSONArray(buf.toString());
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String variant = ((String) jsonObject.get("word")).trim();
                    if (variant.length() > 1) {
                        this.word.setVariant(variant);
                    }
                }
                System.out.println("Поиск закончился и резултаты записаны в ворд: " + buf.toString());
            } else {
                System.out.println("JSON пустой");
            }
                return this.word.getVariants();

        } catch (JSONException e) {
            System.out.println("произошла ошибка прм JSON запросе");
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return this.word.getVariants();
    }

    public int getWord(String word) throws IOException {
        System.out.println("Начало поиска слова");
        Document doc = Jsoup.connect(KEMBWORDPTCH + word).get();
        Element entry_body = doc.select("div.dictionary div.entry-body").first();
        if (entry_body != null) {
            Document variants_body_doc = Jsoup.connect(KEMBSEARCH + word).get();
            Elements variants_body = variants_body_doc.select("div.entry > div.entry-body");
            if (variants_body.size() > 0) {
                System.out.println("что то нашлось");
                for (Element item_box : variants_body) {
                    Elements variants_item = item_box.select("div.pr.dsense");
                    if (variants_item != null) {
                        System.out.println("Здесь однозначно что то есть");
                        int count = 0;
                        for (Element var_item : variants_item) {
                            Element non = var_item.select("div.pr.dsense.dsense-noh").first();
                            if (non != null)
                                continue;
                            Element elem_head = var_item.select("h3.dsense_h").first();
                            if (elem_head == null)
                                continue;
                            String elem_head_t = elem_head.select("span.hw.dsense_hw").first().text()
                                    + " " + elem_head.select("span.pos.dsense_pos").first().text()
                                    + " " + elem_head.select("span.guideword.dsense_gw").first().text();

                            Element elem_example = var_item.select("div.sense-body").first();
                            if (elem_example == null)
                                continue;
                            String elem_example_hed = elem_example.select("div.def.ddef_d.db").first().text();
                            String elem_example_word = elem_example.select("span.trans.dtrans.dtrans-se").first().text();
                            System.out.println(elem_example_word);

                            this.word.setExample(new WordVar(elem_head_t, elem_example_hed, elem_example_word));
                            count++;
                        }
                        if (count == 0) {
                            for (Element var_item : variants_item) {
                                Element elem = var_item.select("div.pr.dsense.dsense-noh").first();
                                if (elem == null)
                                    continue;

                                String elem_head_t = elem.select("div.def.ddef_d.db").first().text();

                                Element elem_body = var_item.select("div.def-body.ddef_b").first();
                                if (elem_body == null)
                                    continue;
                                Element elem_example_word_item = elem_body.select("span.trans.dtrans.dtrans-se").first();
                                String elem_example_word;
                                if (elem_example_word_item != null) {
                                    elem_example_word = elem_example_word_item.text();
                                } else {
                                    elem_example_word = "Перевод отсуствует";
                                }

                                Element elem_example_head_item = elem_body.select("div.examp.dexamp").first();
                                String  elem_example_hed;
                                if (elem_example_head_item != null) {
                                    elem_example_hed = elem_example_head_item.select("span.eg.deg").first().text();
                                } else {
                                    elem_example_hed = "no examples";
                                }

                                System.out.println(elem_example_word);

                                this.word.setExample(new WordVar(elem_head_t, elem_example_hed, elem_example_word));
                            }
                        }
                    } else {
                        System.out.println("Нихрена я не нашел");
                    }
                }
            } else {
                System.out.println("Entry body не нашелся");
            }
            Element name_e = entry_body.select("span.headword > span.hw").first();
            if (name_e != null) {
                String name = name_e.text();
                System.out.println(name);
                this.word.setName(name);
            } else {
                name_e = entry_body.select("h2.headword > b").first();
                String name = name_e.text();
                System.out.println(name);
                this.word.setName(name);
            }
            Element uk_transe = entry_body.select("span.uk span.pron > span.ipa").first();
            if (uk_transe != null) {
                String uk_trans = uk_transe.text();
                this.word.setUk_trans(uk_trans);
            }
            Element us_transe = entry_body.select("span.us span.pron > span.ipa").first();
            if (us_transe != null) {
                String us_trans = uk_transe.text();
                this.word.setUs_trans(us_trans);
            }
            Element uk_audioi = entry_body.select("span.uk  span.daud amp-audio > source").first();
            if (uk_audioi != null) {
                String uk_audio = uk_audioi.attr("src");
                Uri uk_aud = Uri.parse("https://dictionary.cambridge.org" + uk_audio);
                if (uk_aud != null) {
                    this.word.setUk_audio(uk_aud);
                }
            }
            Element us_audioi = entry_body.select("span.us  span.daud amp-audio > source").first();
            if (uk_audioi != null) {
                String us_audio = us_audioi.attr("src");
                Uri us_aud = Uri.parse("https://dictionary.cambridge.org" + us_audio);
                if (us_aud != null) {
                    this.word.setUs_audio(us_aud);
                }
            }
            System.out.println("Поиск слова " + word + " завершился удачно");
            return  1;
        } else {
            System.out.println("Слова " + word + " нету");
            return 0;
        }
    }
}
